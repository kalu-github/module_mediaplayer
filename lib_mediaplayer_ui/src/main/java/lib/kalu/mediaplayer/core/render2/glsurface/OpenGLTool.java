package lib.kalu.mediaplayer.core.render2.glsurface;

import android.opengl.GLES20;

/**
 * @ProjectName: TheSimpllestplayer
 * @Package: com.yw.thesimpllestplayer.renderview
 * @ClassName: OpenGLTool
 * @Description: opengl常用方法集合
 * @Author: wei.yang
 * @CreateDate: 2021/11/6 15:40
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/11/6 15:40
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OpenGLTool {
    private OpenGLTool() {
    }

    private static OpenGLTool instance = null;

    public static OpenGLTool getInstance() {
        synchronized (OpenGLTool.class) {
            if (instance == null) {
                instance = new OpenGLTool();
            }
        }
        return instance;
    }

    public int[] createTextureIds(int count) {
        int[] texture = new int[count];
        GLES20.glGenTextures(count, texture, 0);//生成纹理
        return texture;
    }

    public int createFBOTexture(int width, int height) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textures[0];
    }

    public int createFrameBuffer() {
        int[] fbs = new int[1];
        GLES20.glGenFramebuffers(1, fbs, 0);
        return fbs[0];
    }

    public void bindFBO(int fb, int textureId) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
    }

    public void unbindFBO() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, GLES20.GL_NONE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void deleteFBO(int[] frame, int[] texture) {
        //删除Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, GLES20.GL_NONE);
//        GLES20.glDeleteRenderbuffers(1, fRender, 0)
        //删除Frame Buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
        GLES20.glDeleteFramebuffers(1, frame, 0);
        //删除纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, texture, 0);
    }
}
