package lib.kalu.mediaplayer.core.render2.glsurface;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

public class VideoGLSurfaceView extends GLSurfaceView implements VideoRenderApi {

    @Nullable
    private VideoKernelApi mKernel;
    @Nullable
    private SurfaceHolder.Callback mSurfaceHolderCallback;

    public VideoGLSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startUpdateProgress();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopUpdateProgress();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            startUpdateProgress();
        } else {
            stopUpdateProgress();
        }
    }

    @Override
    public void init() {
        VideoRenderApi.super.init();
        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(true); //禁止onDraw
        setZOrderOnTop(true); //画布透明处理
        setZOrderMediaOverlay(true); //画面置顶
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        setEGLContextClientVersion(2);
//        //初始化绘制器
//        videoDrawer = new VideoDrawer();
//        videoDrawer.setVideoSize(1080, 1920);
//        //初始化渲染器
//        videoRender = new VideoRender();
//        videoRender.addDrawer(videoDrawer);
//        setRenderer(videoRender);

        registListener();
    }

    @Override
    public void registListener() {

        GLRender glRender = new GLRender();
        setRenderer(glRender);
    }

    @Override
    public void unRegistListener() {

    }

    //    @Override
//    public void addListener() {
//        try {
//            if (null != mSurfaceHolderCallback)
//                throw new Exception("mSurfaceHolderCallback warning: " + mSurfaceHolderCallback);
//            mSurfaceHolderCallback = new SurfaceHolder.Callback() {
//
//                /**
//                 * 创建的时候调用该方法
//                 * @param holder                        holder
//                 */
//                @Override
//                public void surfaceCreated(SurfaceHolder holder) {
////                    LogUtil.log("VideoGLSurfaceView => addListener => surfaceCreated => width = " + getWidth() + ", height = " + getHeight() + ", mKernel = " + mKernel + ", mHandler = " + mHandler + ", holder = " + holder + ", suface = " + holder.getSurface());
//                    setSurface(false);
//                }
//
//                /**
//                 * 视图改变的时候调用方法
//                 * @param holder
//                 * @param format
//                 * @param width
//                 * @param height
//                 */
//                @Override
//                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                    LogUtil.log("VideoGLSurfaceView => addListener => surfaceChanged => width = " + width + ", height = " + height + ",surfaceChanged => " + this);
//                }
//
//                /**
//                 * 销毁的时候调用该方法
//                 * @param holder
//                 */
//                @Override
//                public void surfaceDestroyed(SurfaceHolder holder) {
//                    LogUtil.log("VideoGLSurfaceView => addListener => surfaceDestroyed => " + this);
//                    setSurface(true);
//                }
//            };
//            getHolder().addCallback(mSurfaceHolderCallback);
//        } catch (Exception e) {
//            LogUtil.log("VideoGLSurfaceView => addListener => " + e.getMessage());
//        }
//    }

    @Override
    public void setSurface(boolean release) {
        try {
            if (null == mKernel)
                throw new Exception("mKernel warning: null");
            if (release) {
//                mKernel.setDisplay(null);
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            LogUtil.log("VideoGLSurfaceView => setSurface => " + e.getMessage());
        }
    }

    @Override
    public void reset() {
        LogUtil.log("VideoGLSurfaceView => reset =>");
        setSurface(false);
    }

    @Override
    public void release() {
        // step1
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            Surface surface = surfaceHolder.getSurface();
            if (null == surface)
                throw new Exception("surface error: null");
//            clearSurface(surface);
            surface.release();
            LogUtil.log("VideoGLSurfaceView => release => removeSurface => succ");
        } catch (Exception e) {
            LogUtil.log("VideoGLSurfaceView => release => removeSurface => " + e.getMessage());
        }

        // step2
        try {
            if (null == mSurfaceHolderCallback)
                throw new Exception("mSurfaceHolderCallback error: null");
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            surfaceHolder.removeCallback(mSurfaceHolderCallback);
            mSurfaceHolderCallback = null;
            LogUtil.log("VideoGLSurfaceView => release => removeCallback => succ");
        } catch (Exception e) {
            LogUtil.log("VideoGLSurfaceView => release => removeCallback => " + e.getMessage());
        }
    }

    @Override
    public void setVideoKernel(VideoKernelApi kernel) {
        this.mKernel = kernel;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return this.mKernel;
    }


    @Override
    public String screenshot(String url, long position) {
        return null;
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(url);
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(position);
//            mediaMetadataRetriever.release();
//            return saveBitmap(getContext().getApplicationContext(), bitmap);
//        } catch (Exception e) {
//            return null;
//        }

//        Canvas canvasH = getHolder().lockCanvas(null);//获取画布
//
//        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
//
//        getHolder().unlockCanvasAndPost(canvasH);//解锁画布，提交画好的图像

//        return saveBitmap(getContext().getApplicationContext(), bitmap);

//        Context context = getContext();
//        Bitmap bitmap = getDrawingCache();
//        return saveBitmap(context, bitmap);
    }

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

    @Override
    public boolean hasExplicitFocusable() {
        return false;
    }

    @Override
    public boolean hasWindowFocus() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    /************/

    /**
     * 记得一定要重新写这个方法，如果角度发生了变化，就重新绘制布局
     * 设置视频旋转角度
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        try {
            float v = getRotation();
            if (v == rotation)
                throw new Exception("rotation warning: " + rotation);
            super.setRotation(rotation);
            requestLayout();
        } catch (Exception e) {
            LogUtil.log("VideoGLSurfaceView => setRotation => " + e.getMessage());
        }
    }

    /***************/

//    private void drawBitmap() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Canvas canvas = null;
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    canvas = holder.lockCanvas();
//                    Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//                    bitmap.eraseColor(Color.RED);//填充颜色
//                    canvas.drawBitmap(bitmap, 0, 0, null);
//                } catch (Exception e) {
//                    MPLogUtil.log("VideoGLSurfaceView => drawBitmap => " + e.getMessage());
//                }
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    if (null == canvas)
//                        throw new Exception("canvas warning: null");
//                    //手动try catch一下这个方法，让程序在4.3的手机上不至于崩溃，部分Android13也会崩溃
//                    getHolder().unlockCanvasAndPost(canvas);
//                } catch (Exception e) {
//                    MPLogUtil.log("VideoGLSurfaceView => drawBitmap => " + e.getMessage());
//                }
//            }
//        }).start();
//    }

    /***************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            int[] measureSpec = doMeasureSpec(screenWidth, screenHeight);
            if (null == measureSpec)
                throw new Exception("warning: measureSpec null");
            int width = measureSpec[0];
            int height = measureSpec[1];
            int specW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
//            getHolder().setFixedSize(measureSpec[0], measureSpec[1]);
        } catch (Exception e) {
            LogUtil.log("VideoGLSurfaceView => onMeasure => Exception " + e.getMessage());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public final static class VideoDrawer {
        public VideoDrawer() {
            //1.初始化顶点坐标
            initPos();
        }

        /**
         * 顶点坐标，此处的坐标系是物体坐标系：中心店坐标是（0,0）
         */
        private float[] mVertexCoors = new float[]{
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f
        };
        /**
         * 纹理坐标系，中心坐标点为（0.5,0.5），上方向为t从0~1，右边方向为s，从0~1.刚好和计算器物理坐标系是反过来的。
         */
        private float[] mTextureCoors = new float[]{
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
        };
        private String vertextShaderSource = "attribute vec4 aPosition;" +
                "precision mediump float;" +
                "uniform mat4 uMatrix;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "attribute float alpha;" +
                "varying float inAlpha;" +
                "void main(){" +
                "gl_Position = uMatrix*aPosition;" +
                "vCoordinate = aCoordinate;" +
                "inAlpha = alpha;" +
                "}";
        private String fragmentShaderSource = "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "varying vec2 vCoordinate;" +
                "varying float inAlpha;" +
                "uniform samplerExternalOES uTexture;" +
                "void main() {" +
                "vec4 color = texture2D(uTexture, vCoordinate);" +
                "gl_FragColor = vec4(color.r, color.g, color.b, inAlpha);" +
                "}";
        /**
         * 视频宽高
         */
        private int mVideoWidth = -1;
        private int mVideoHeight = -1;
        /**
         * 物理屏幕的宽高
         */
        private int mWorldWidth = -1;
        private int mWorldHeight = -1;

        /**
         * 纹理ID
         */
        private int mTextureId = -1;

        /**
         * 定义SurfaceTexture 为显示视频做准备
         */
        private SurfaceTexture mSurfaceTexture = null;

        /**
         * 定义OpenGL 程序ID
         */
        private int mProgram = -1;
        /**
         * 矩阵变换接受者（shader中）
         */
        private int mVertexMatrixHandler = -1;
        /**
         * 顶点坐标接收者
         */
        private int mVertexPosHandler = -1;
        /**
         * 纹理坐标接受者
         */
        private int mTexturePosHandler = -1;
        /**
         * 纹理接受者
         */
        private int mTextureHandler = -1;
        /**
         * 半透明值接受者
         */
        private int mAlphaHandler = -1;
        /**
         * 顶点缓冲
         */
        private FloatBuffer mVertexBuffer = null;
        /**
         * 纹理缓冲
         */
        private FloatBuffer mTextureBuffer = null;
        /**
         * 矩阵
         */
        private float[] mMatrix = null;
        /**
         * 透明度
         */
        private float mAlpha = 1f;
        /**
         * 旋转角度
         */
        private float mWidthRatio = 1f;
        private float mHeightRatio = 1f;
        private int floatLength = 16;

        /**
         * 初始化顶点坐标
         */
        private void initPos() {
            ByteBuffer vByteBuffer = ByteBuffer.allocateDirect(mVertexCoors.length * 4);
            vByteBuffer.order(ByteOrder.nativeOrder());
            //将坐标转换为floatbuffer，用以传给opengl程序
            mVertexBuffer = vByteBuffer.asFloatBuffer();
            mVertexBuffer.put(mVertexCoors);
            mVertexBuffer.position(0);

            ByteBuffer tByteBuffer = ByteBuffer.allocateDirect(mTextureCoors.length * 4);
            tByteBuffer.order(ByteOrder.nativeOrder());
            mTextureBuffer = tByteBuffer.asFloatBuffer();
            mTextureBuffer.put(mTextureCoors);
            mTextureBuffer.position(0);
        }

        /**
         * 初始化矩阵变换，主要是防止视频拉伸变形
         */
        private void initDefMatrix() {
            if (mMatrix != null) return;
            if (mVideoWidth != -1 && mVideoHeight != -1 &&
                    mWorldWidth != -1 && mWorldHeight != -1) {
                mMatrix = new float[floatLength];
                float[] prjMatrix = new float[floatLength];
                float originRatio = mVideoWidth / (float) mVideoHeight;
                float worldRatio = mWorldWidth / (float) mWorldHeight;
                if (mWorldWidth > mWorldHeight) {
                    if (originRatio > worldRatio) {
                        mHeightRatio = originRatio / worldRatio;
                        Matrix.orthoM(
                                prjMatrix, 0,
                                -mWidthRatio, mWidthRatio,
                                -mHeightRatio, mHeightRatio,
                                3f, 5f
                        );
                    } else {// 原始比例小于窗口比例，缩放高度度会导致高度超出，因此，高度以窗口为准，缩放宽度
                        mWidthRatio = worldRatio / originRatio;
                        Matrix.orthoM(
                                prjMatrix, 0,
                                -mWidthRatio, mWidthRatio,
                                -mHeightRatio, mHeightRatio,
                                3f, 5f
                        );
                    }
                } else {
                    if (originRatio > worldRatio) {
                        mHeightRatio = originRatio / worldRatio;
                        Matrix.orthoM(
                                prjMatrix, 0,
                                -mWidthRatio, mWidthRatio,
                                -mHeightRatio, mHeightRatio,
                                3f, 5f
                        );
                    } else {// 原始比例小于窗口比例，缩放高度会导致高度超出，因此，高度以窗口为准，缩放宽度
                        mWidthRatio = worldRatio / originRatio;
                        Matrix.orthoM(
                                prjMatrix, 0,
                                -mWidthRatio, mWidthRatio,
                                -mHeightRatio, mHeightRatio,
                                3f, 5f
                        );
                    }
                }
                //设置相机位置
                float[] viewMatrix = new float[floatLength];
                Matrix.setLookAtM(
                        viewMatrix, 0,
                        0f, 0f, 5.0f,
                        0f, 0f, 0f,
                        0f, 1.0f, 0f
                );
                //计算变换矩阵
                Matrix.multiplyMM(mMatrix, 0, prjMatrix, 0, viewMatrix, 0);
            }
        }

        public void setVideoSize(int videoWidth, int videoHeight) {
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;
        }

        public void setWorldSize(int worldWidth, int worldHeight) {
            mWorldWidth = worldWidth;
            mWorldHeight = worldHeight;
        }

        public void setAlpha(float alpha) {
            mAlpha = alpha;
        }

        public void draw() {
            if (mTextureId != -1) {
                initDefMatrix();
                //2/创建、编译、启动opengles着色器
                createGLPrg();
                //3.激活并绑定纹理单元
                activateTexture();
                //4.绑定图元到纹理单元
                updateTexture();
                //5.开始绘制渲染
                doDraw();
            }
        }

        public void setTextureID(int textureID) {
            mTextureId = textureID;
            //根据textureId初始化一个SurfaceTexture
            mSurfaceTexture = new SurfaceTexture(textureID);
        }

        public SurfaceTexture getSurfaceTexture() {
            return mSurfaceTexture;
        }

        /**
         * 创建并使用opengles程序
         */
        private void createGLPrg() {
            if (mProgram == -1) {
                int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertextShaderSource);
                int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
                //创建programe陈谷
                mProgram = GLES20.glCreateProgram();
                //将顶点着色器加入程序
                GLES20.glAttachShader(mProgram, vertexShader);
                //将片元着色器加入程序
                GLES20.glAttachShader(mProgram, fragmentShader);
                GLES20.glLinkProgram(mProgram);
                //从程序中获取句柄
                mVertexMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMatrix");
                mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
                mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
                mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
                mAlphaHandler = GLES20.glGetAttribLocation(mProgram, "alpha");

            }
            //使用opengl程序
            GLES20.glUseProgram(mProgram);
        }

        /**
         * 激活并绑定纹理单元
         */
        private void activateTexture() {
            //激活指定纹理单元
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //绑定纹理ID到纹理单元
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
            //将激活并绑定的纹理id传递到着色器里面
            GLES20.glUniform1i(mTextureHandler, 0);
            //配置边缘过滤参数
            GLES20.glTexParameterf(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
            );
            GLES20.glTexParameterf(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
            );
            //配置s轴和t轴的方式
            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE
            );
            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
            );
        }

        private void updateTexture() {
            mSurfaceTexture.updateTexImage();
        }

        /**
         * 加载着色器
         *
         * @param shaderType 着色器类型
         * @param shaderCode 着色器代码
         * @return
         */
        private int loadShader(int shaderType, String shaderCode) {
            //根据着色器类型创建着色器
            int shader = GLES20.glCreateShader(shaderType);
            //将着色其代码加入到着色器
            GLES20.glShaderSource(shader, shaderCode);
            //编译zhuoseq
            GLES20.glCompileShader(shader);
            return shader;
        }

        /**
         * 开始绘制渲染
         */
        public void doDraw() {
            //启用顶点坐标句柄
            GLES20.glEnableVertexAttribArray(mVertexPosHandler);
            GLES20.glEnableVertexAttribArray(mTexturePosHandler);
            GLES20.glUniformMatrix4fv(mVertexMatrixHandler, 1, false, mMatrix, 0);
            //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
            GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
            GLES20.glVertexAttribPointer(
                    mTexturePosHandler,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    mTextureBuffer
            );
            GLES20.glVertexAttrib1f(mAlphaHandler, mAlpha);
            //开始绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }

        public void release() {
            GLES20.glDisableVertexAttribArray(mVertexPosHandler);
            GLES20.glDisableVertexAttribArray(mTexturePosHandler);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
            GLES20.glDeleteProgram(mProgram);
        }

        public void translate(float dx, float dy) {
            Matrix.translateM(mMatrix, 0, dx * mWidthRatio * 2, -dy * mHeightRatio * 2, 0f);
        }

        public void scale(float sx, float sy) {
            Matrix.scaleM(mMatrix, 0, sx, sy, 1f);
            mWidthRatio /= sx;
            mHeightRatio /= sy;
        }
    }

    public class GLRender implements Renderer {
        private final List<IDrawer> drawers = new ArrayList<IDrawer>();

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 0f);
            //开启混合，即半透明
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            int[] textureIds = OpenGLTool.getInstance().createTextureIds(drawers.size());
            for (int i = 0; i < textureIds.length; i++) {
                drawers.get(i).setTextureID(textureIds[i]);
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            for (IDrawer drawer : drawers) {
                drawer.setWorldSize(width, height);
            }

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除颜色缓冲和深度缓冲
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            for (int i = 0; i < drawers.size(); i++) {
                drawers.get(i).draw();
            }
        }

        /**
         * 添加渲染器
         *
         * @param drawer
         */
        public void addDrawer(IDrawer drawer) {
            drawers.add(drawer);
        }
    }
}
