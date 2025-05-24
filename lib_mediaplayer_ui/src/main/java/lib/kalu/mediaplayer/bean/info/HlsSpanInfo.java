package lib.kalu.mediaplayer.bean.info;

import java.io.Serializable;

public final class HlsSpanInfo implements Serializable {

    private long relativeStartTimeUs =0L;
   private long durationUs = 0L;

   private String url;
   private String path;

    public long getRelativeStartTimeUs() {
        return relativeStartTimeUs;
    }

    public void setRelativeStartTimeUs(long relativeStartTimeUs) {
        this.relativeStartTimeUs = relativeStartTimeUs;
    }

    public long getDurationUs() {
        return durationUs;
    }

    public void setDurationUs(long durationUs) {
        this.durationUs = durationUs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
