package lib.kalu.mediaplayer.core.render2.glsurface;

/**
 * @ProjectName: TheSimpllestplayer
 * @Package: com.yw.thesimpllestplayer.renderview
 * @ClassName: IDrawer
 * @Description: 渲染器
 * @Author: wei.yang
 * @CreateDate: 2021/11/6 14:18
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/11/6 14:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IDrawer {
    /**
     * 设置视频大小
     * @param videoWidth 视频宽度
     * @param videoHeight 视频高度
     */
    void setVideoSize(int videoWidth,int videoHeight);
    void setWorldSize(int worldWidth,int worldHeight);
    void setAlpha(float alpha);
    void draw();
    void setTextureID(int textureID);
//    void getSurfaceTexture(cb: (st: SurfaceTexture)->Unit) {}

    /**
     * 销毁渲染器
     */
    void release();
}
