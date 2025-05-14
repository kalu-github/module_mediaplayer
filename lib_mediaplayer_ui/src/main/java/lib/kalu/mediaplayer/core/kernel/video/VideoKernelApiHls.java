package lib.kalu.mediaplayer.core.kernel.video;


import java.util.List;

import lib.kalu.mediaplayer.args.HlsSpanInfo;

public interface VideoKernelApiHls {
    default List<HlsSpanInfo> getBufferedHlsSpanInfo() {
        return null;
    }
}