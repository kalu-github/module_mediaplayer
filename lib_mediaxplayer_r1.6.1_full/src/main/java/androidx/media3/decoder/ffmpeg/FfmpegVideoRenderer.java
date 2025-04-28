/*
 * Copyright (C) 2019 The Android Open Source Project
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
package androidx.media3.decoder.ffmpeg;

import static androidx.media3.common.C.FORMAT_HANDLED;
import static androidx.media3.common.C.FORMAT_UNSUPPORTED_DRM;
import static androidx.media3.common.C.FORMAT_UNSUPPORTED_SUBTYPE;
import static androidx.media3.common.C.FORMAT_UNSUPPORTED_TYPE;
import static androidx.media3.exoplayer.DecoderReuseEvaluation.DISCARD_REASON_MIME_TYPE_CHANGED;
import static androidx.media3.exoplayer.DecoderReuseEvaluation.REUSE_RESULT_NO;
import static androidx.media3.exoplayer.DecoderReuseEvaluation.REUSE_RESULT_YES_WITHOUT_RECONFIGURATION;
import static java.lang.Runtime.getRuntime;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.TraceUtil;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.decoder.CryptoConfig;
import androidx.media3.decoder.Decoder;
import androidx.media3.decoder.DecoderInputBuffer;
import androidx.media3.decoder.VideoDecoderOutputBuffer;
import androidx.media3.exoplayer.DecoderReuseEvaluation;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.RendererCapabilities;
import androidx.media3.exoplayer.video.DecoderVideoRenderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

/**
 * Decodes and renders video using libgav1 decoder.
 *
 * <p>This renderer accepts the following messages sent via {@link #()}
 * on the playback thread:
 *
 * <ul>
 *       buffer renderer. The message payload should be the target {@link
 *       }, or null.
 * </ul>
 */
@UnstableApi
public class FfmpegVideoRenderer extends DecoderVideoRenderer {

    private static final String TAG = "FfmpegVideoRenderer";

    private static final int DEFAULT_NUM_OF_INPUT_BUFFERS = 4;
    private static final int DEFAULT_NUM_OF_OUTPUT_BUFFERS = 4;
    /* Default size based on 720p resolution video compressed by a factor of two. */
    private static final int DEFAULT_INPUT_BUFFER_SIZE =
            Util.ceilDivide(1280, 64) * Util.ceilDivide(720, 64) * (64 * 64 * 3 / 2) / 2;

    /**
     * The number of input buffers.
     */
    private final int numInputBuffers;
    /**
     * The number of output buffers. The renderer may limit the minimum possible value due to
     * requiring multiple output buffers to be dequeued at a time for it to make progress.
     */
    private final int numOutputBuffers;

    private final int threads;

    @Nullable
    private FfmpegVideoDecoder decoder;

    /**
     * Creates a Libgav1VideoRenderer.
     *
     * @param allowedJoiningTimeMs     The maximum duration in milliseconds for which this video renderer
     *                                 can attempt to seamlessly join an ongoing playback.
     * @param eventHandler             A handler to use when delivering events to {@code eventListener}. May be
     *                                 null if delivery of events is not required.
     * @param eventListener            A listener of events. May be null if delivery of events is not required.
     * @param maxDroppedFramesToNotify The maximum number of frames that can be dropped between
     *                                 invocations of {@link VideoRendererEventListener#onDroppedFrames(int, long)}.
     */
    public FfmpegVideoRenderer(
            long allowedJoiningTimeMs,
            @Nullable Handler eventHandler,
            @Nullable VideoRendererEventListener eventListener,
            int maxDroppedFramesToNotify) {

        this(allowedJoiningTimeMs,
                eventHandler,
                eventListener,
                maxDroppedFramesToNotify,
                getRuntime().availableProcessors(),
                DEFAULT_NUM_OF_INPUT_BUFFERS,
                DEFAULT_NUM_OF_OUTPUT_BUFFERS);
    }

    /**
     * Creates a new instance.
     *
     * @param allowedJoiningTimeMs     The maximum duration in milliseconds for which this video renderer
     *                                 can attempt to seamlessly join an ongoing playback.
     * @param eventHandler             A handler to use when delivering events to {@code eventListener}. May be
     *                                 null if delivery of events is not required.
     * @param eventListener            A listener of events. May be null if delivery of events is not required.
     * @param maxDroppedFramesToNotify The maximum number of frames that can be dropped between
     *                                 invocations of {@link VideoRendererEventListener#onDroppedFrames(int, long)}.
     * @param threads                  Number of threads libgav1 will use to decode.
     * @param numInputBuffers          Number of input buffers.
     * @param numOutputBuffers         Number of output buffers.
     */
    public FfmpegVideoRenderer(
            long allowedJoiningTimeMs,
            @Nullable Handler eventHandler,
            @Nullable VideoRendererEventListener eventListener,
            int maxDroppedFramesToNotify,
            int threads,
            int numInputBuffers,
            int numOutputBuffers) {
        super(allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFramesToNotify);
        this.threads = threads;
        this.numInputBuffers = numInputBuffers;
        this.numOutputBuffers = numOutputBuffers;
    }

    @SuppressLint("WrongConstant")
    @Override
    @Capabilities
    public final int supportsFormat(Format format) {

//    String mimeType = Assertions.checkNotNull(format.sampleMimeType);
//    if (!FfmpegLibrary.isAvailable() || !MimeTypes.isVideo(mimeType)) {
//      return FORMAT_UNSUPPORTED_TYPE;
//    } else if (!FfmpegLibrary.supportsFormat(format.sampleMimeType)) {
//      return RendererCapabilities.create(FORMAT_UNSUPPORTED_SUBTYPE);
//    } else if (format.drmInitData != null && format.exoMediaCryptoType == null) {
//      return RendererCapabilities.create(FORMAT_UNSUPPORTED_DRM);
//    } else {
//      return RendererCapabilities.create(
//              FORMAT_HANDLED,
//              ADAPTIVE_SEAMLESS,
//              TUNNELING_NOT_SUPPORTED);
//    }

        String mimeType = Assertions.checkNotNull(format.sampleMimeType);
        if (!FfmpegLibrary.isAvailable() || !MimeTypes.isVideo(mimeType)) {
            return FORMAT_UNSUPPORTED_TYPE;
        } else if (!FfmpegLibrary.supportsFormat(format.sampleMimeType)) {
            return RendererCapabilities.create(FORMAT_UNSUPPORTED_SUBTYPE);
        } else if (format.drmInitData != null) {
            return RendererCapabilities.create(FORMAT_UNSUPPORTED_DRM);
        } else {
            return RendererCapabilities.create(
                    FORMAT_HANDLED,
                    ADAPTIVE_SEAMLESS,
                    TUNNELING_NOT_SUPPORTED);
        }
    }

    @Override
    protected Decoder<DecoderInputBuffer, VideoDecoderOutputBuffer, FfmpegDecoderException> createDecoder(Format format, @Nullable CryptoConfig cryptoConfig) throws FfmpegDecoderException {
        TraceUtil.beginSection("createFfmpegVideoDecoder");
        int initialInputBufferSize =
                format.maxInputSize != Format.NO_VALUE ? format.maxInputSize : DEFAULT_INPUT_BUFFER_SIZE;
        Decoder decoder = new FfmpegVideoDecoder(numInputBuffers, numOutputBuffers, initialInputBufferSize, threads, format);
        this.decoder = (FfmpegVideoDecoder) decoder;
        TraceUtil.endSection();
        Log.d(TAG, "createDecoder: " + decoder);
        return decoder;
    }

    //  @Override
//  protected Decoder<VideoDecoderInputBuffer, VideoDecoderOutputBuffer, FfmpegDecoderException>
//  createDecoder(Format format, @Nullable CryptoInfo mediaCrypto)
//          throws FfmpegDecoderException {
//
//  }

    @Override
    protected void renderOutputBufferToSurface(VideoDecoderOutputBuffer outputBuffer, Surface surface)
            throws FfmpegDecoderException {
        try {
            if (decoder == null) {
                throw new FfmpegDecoderException(
                        "Failed to render output buffer to surface: decoder is not initialized.");
            }
            decoder.renderToSurface(outputBuffer, surface);
            outputBuffer.release();
        } catch (Exception e) {
        }
    }

    @Override
    protected void setDecoderOutputMode(@C.VideoOutputMode int outputMode) {
        if (decoder != null) {
            decoder.setOutputMode(outputMode);
        }
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void setPlaybackSpeed(float currentPlaybackSpeed, float targetPlaybackSpeed) throws ExoPlaybackException {

    }

    @Override
    protected DecoderReuseEvaluation canReuseDecoder(
            String decoderName, Format oldFormat, Format newFormat) {
        boolean sameMimeType = Util.areEqual(oldFormat.sampleMimeType, newFormat.sampleMimeType);
        return new DecoderReuseEvaluation(
                decoderName,
                oldFormat,
                newFormat,
                sameMimeType ? REUSE_RESULT_YES_WITHOUT_RECONFIGURATION : REUSE_RESULT_NO,
                sameMimeType ? 0 : DISCARD_REASON_MIME_TYPE_CHANGED);
    }

    // PlayerMessage.Target implementation.

//  @Override
//  public void handleMessage(int messageType, @Nullable Object message) throws ExoPlaybackException {
//    if (messageType == MSG_SET_SURFACE) {
//      setOutputSurface((Surface) message);
//    } else if (messageType == MSG_SET_VIDEO_DECODER_OUTPUT_BUFFER_RENDERER) {
//      setOutputBufferRenderer((VideoDecoderOutputBufferRenderer) message);
//    } else {
//      super.handleMessage(messageType, message);
//    }
//  }
}