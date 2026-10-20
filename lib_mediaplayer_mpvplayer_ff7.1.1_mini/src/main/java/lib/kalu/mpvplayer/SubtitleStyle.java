package lib.kalu.mpvplayer;

/** Scaled subtitle pixels use mpv's 720-pixel reference height; colors are Android ARGB. */
public final class SubtitleStyle {
    public final float fontSize, opacity;
    public final int textColor, backgroundColor, outlineColor, positionPercent, marginX, marginY;
    public final boolean overrideAss;
    public SubtitleStyle(float fontSize,int textColor,int backgroundColor,int outlineColor,
                         int positionPercent,float opacity,int marginX,int marginY,boolean overrideAss) {
        if (Float.isNaN(fontSize) || Float.isInfinite(fontSize) || fontSize<=0
            || Float.isNaN(opacity) || Float.isInfinite(opacity) || opacity<0 || opacity>1
            || positionPercent<0 || positionPercent>100 || marginX<0 || marginY<0)
            throw new IllegalArgumentException("Invalid subtitle style");
        this.fontSize=fontSize; this.textColor=textColor; this.backgroundColor=backgroundColor;
        this.outlineColor=outlineColor; this.positionPercent=positionPercent; this.opacity=opacity;
        this.marginX=marginX; this.marginY=marginY; this.overrideAss=overrideAss;
    }
}
