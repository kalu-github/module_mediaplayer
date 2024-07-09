package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public interface VideoRenderApiBase {

    void setKernel(VideoKernelApi player);

    VideoKernelApi getKernel();
}