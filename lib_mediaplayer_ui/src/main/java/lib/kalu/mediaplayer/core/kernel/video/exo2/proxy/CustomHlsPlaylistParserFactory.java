package lib.kalu.mediaplayer.core.kernel.video.exo2.proxy;

import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMultivariantPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.upstream.ParsingLoadable;

public final class CustomHlsPlaylistParserFactory implements HlsPlaylistParserFactory {
    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
        return new CustomHlsPlaylistParser();
    }

    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMultivariantPlaylist multivariantPlaylist, HlsMediaPlaylist previousMediaPlaylist) {
        return new CustomHlsPlaylistParser();
    }
}
