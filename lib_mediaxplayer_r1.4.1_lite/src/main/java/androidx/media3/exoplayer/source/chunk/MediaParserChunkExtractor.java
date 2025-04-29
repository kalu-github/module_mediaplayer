/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.exoplayer.source.chunk;

import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_EAGERLY_EXPOSE_TRACK_TYPE;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_EXPOSE_CAPTION_FORMATS;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_EXPOSE_CHUNK_INDEX_AS_MEDIA_FORMAT;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_EXPOSE_DUMMY_SEEK_MAP;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_INCLUDE_SUPPLEMENTAL_DATA;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_IN_BAND_CRYPTO_INFO;
import static androidx.media3.exoplayer.source.mediaparser.MediaParserUtil.PARAMETER_OVERRIDE_IN_BAND_CAPTION_DECLARATIONS;

import android.annotation.SuppressLint;
import android.media.MediaFormat;
import android.media.MediaParser;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.analytics.PlayerId;
import androidx.media3.exoplayer.source.mediaparser.InputReaderAdapterV30;
import androidx.media3.exoplayer.source.mediaparser.MediaParserUtil;
import androidx.media3.exoplayer.source.mediaparser.OutputConsumerAdapterV30;
import androidx.media3.extractor.ChunkIndex;
import androidx.media3.extractor.DiscardingTrackOutput;
import androidx.media3.extractor.ExtractorInput;
import androidx.media3.extractor.ExtractorOutput;
import androidx.media3.extractor.SeekMap;
import androidx.media3.extractor.TrackOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** {@link ChunkExtractor} implemented on top of the platform's {@link MediaParser}. */
@RequiresApi(30)
@UnstableApi
public final class MediaParserChunkExtractor implements ChunkExtractor {

  // Maximum TAG length is 23 characters.
  private static final String TAG = "MediaPrsrChunkExtractor";

  public static final ChunkExtractor.Factory FACTORY =
      (primaryTrackType,
          format,
          enableEventMessageTrack,
          closedCaptionFormats,
          playerEmsgTrackOutput,
          playerId) -> {
        if (!MimeTypes.isText(format.containerMimeType)) {
          // Container is either Matroska or Fragmented MP4.
          return new MediaParserChunkExtractor(
              primaryTrackType, format, closedCaptionFormats, playerId);
        } else {
          // This is a text track that does not require an extractor.
          return null;
        }
      };

  private final OutputConsumerAdapterV30 outputConsumerAdapter;
  private final InputReaderAdapterV30 inputReaderAdapter;
  private final MediaParser mediaParser;
  private final TrackOutputProviderAdapter trackOutputProviderAdapter;
  private final DiscardingTrackOutput discardingTrackOutput;
  private long pendingSeekUs;
  @Nullable private TrackOutputProvider trackOutputProvider;
  @Nullable private Format[] sampleFormats;

  /**
   * Creates a new instance.
   *
   * @param primaryTrackType The {@link C.TrackType type} of the primary track. {@link
   *     C#TRACK_TYPE_NONE} if there is no primary track.
   * @param manifestFormat The chunks {@link Format} as obtained from the manifest.
   * @param closedCaptionFormats A list containing the {@link Format Formats} of the closed-caption
   *     tracks in the chunks.
   * @param playerId The {@link PlayerId} of the player this chunk extractor is used for.
   */
  @SuppressLint("WrongConstant")
  public MediaParserChunkExtractor(
      @C.TrackType int primaryTrackType,
      Format manifestFormat,
      List<Format> closedCaptionFormats,
      PlayerId playerId) {
    outputConsumerAdapter =
        new OutputConsumerAdapterV30(
            manifestFormat, primaryTrackType, /* expectDummySeekMap= */ true);
    inputReaderAdapter = new InputReaderAdapterV30();
    String mimeType = Assertions.checkNotNull(manifestFormat.containerMimeType);
    String parserName =
        MimeTypes.isMatroska(mimeType)
            ? MediaParser.PARSER_NAME_MATROSKA
            : MediaParser.PARSER_NAME_FMP4;
    outputConsumerAdapter.setSelectedParserName(parserName);
    mediaParser = MediaParser.createByName(parserName, outputConsumerAdapter);
    mediaParser.setParameter(MediaParser.PARAMETER_MATROSKA_DISABLE_CUES_SEEKING, true);
    mediaParser.setParameter(PARAMETER_IN_BAND_CRYPTO_INFO, true);
    mediaParser.setParameter(PARAMETER_INCLUDE_SUPPLEMENTAL_DATA, true);
    mediaParser.setParameter(PARAMETER_EAGERLY_EXPOSE_TRACK_TYPE, true);
    mediaParser.setParameter(PARAMETER_EXPOSE_DUMMY_SEEK_MAP, true);
    mediaParser.setParameter(PARAMETER_EXPOSE_CHUNK_INDEX_AS_MEDIA_FORMAT, true);
    mediaParser.setParameter(PARAMETER_OVERRIDE_IN_BAND_CAPTION_DECLARATIONS, true);
    ArrayList<MediaFormat> closedCaptionMediaFormats = new ArrayList<>();
    for (int i = 0; i < closedCaptionFormats.size(); i++) {
      closedCaptionMediaFormats.add(
          MediaParserUtil.toCaptionsMediaFormat(closedCaptionFormats.get(i)));
    }
    mediaParser.setParameter(PARAMETER_EXPOSE_CAPTION_FORMATS, closedCaptionMediaFormats);
    if (Util.SDK_INT >= 31) {
      MediaParserUtil.setLogSessionIdOnMediaParser(mediaParser, playerId);
    }
    outputConsumerAdapter.setMuxedCaptionFormats(closedCaptionFormats);
    trackOutputProviderAdapter = new TrackOutputProviderAdapter();
    discardingTrackOutput = new DiscardingTrackOutput();
    pendingSeekUs = C.TIME_UNSET;
  }

  // ChunkExtractor implementation.

  @Override
  public void init(
      @Nullable TrackOutputProvider trackOutputProvider, long startTimeUs, long endTimeUs) {
    this.trackOutputProvider = trackOutputProvider;
    outputConsumerAdapter.setSampleTimestampUpperLimitFilterUs(endTimeUs);
    outputConsumerAdapter.setExtractorOutput(trackOutputProviderAdapter);
    pendingSeekUs = startTimeUs;
  }

  @Override
  public void release() {
    mediaParser.release();
  }

  @Override
  public boolean read(ExtractorInput input) throws IOException {
    maybeExecutePendingSeek();
    inputReaderAdapter.setDataReader(input, input.getLength());
    return mediaParser.advance(inputReaderAdapter);
  }

  @Nullable
  @Override
  public ChunkIndex getChunkIndex() {
    return outputConsumerAdapter.getChunkIndex();
  }

  @Nullable
  @Override
  public Format[] getSampleFormats() {
    return sampleFormats;
  }

  // Internal methods.

  private void maybeExecutePendingSeek() {
    @Nullable MediaParser.SeekMap dummySeekMap = outputConsumerAdapter.getDummySeekMap();
    if (pendingSeekUs != C.TIME_UNSET && dummySeekMap != null) {
      mediaParser.seek(dummySeekMap.getSeekPoints(pendingSeekUs).first);
      pendingSeekUs = C.TIME_UNSET;
    }
  }

  // Internal classes.

  private class TrackOutputProviderAdapter implements ExtractorOutput {

    @Override
    public TrackOutput track(int id, int type) {
      return trackOutputProvider != null
          ? trackOutputProvider.track(id, type)
          : discardingTrackOutput;
    }

    @Override
    public void endTracks() {
      // Imitate BundledChunkExtractor behavior, which captures a sample format snapshot when
      // endTracks is called.
      sampleFormats = outputConsumerAdapter.getSampleFormats();
    }

    @Override
    public void seekMap(SeekMap seekMap) {
      // Do nothing.
    }
  }
}
