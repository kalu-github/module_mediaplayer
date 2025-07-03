package lib.kalu.mediaplayer.core.kernel.video;


import java.util.List;

import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;

public interface VideoKernelApiHls {
    default List<HlsSpanInfo> getSegments() {
        return null;
    }
}