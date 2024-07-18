package lib.kalu.mediaplayer.core.render2.glsurface;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * @ProjectName: TheSimpllestplayer
 * @Package: com.yw.thesimpllestplayer.renderview
 * @ClassName: VideoDrawer
 * @Description: 视频渲染器
 * @Author: wei.yang
 * @CreateDate: 2021/11/6 14:23
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/11/6 14:23
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class VideoDrawer implements IDrawer {
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

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    @Override
    public void setWorldSize(int worldWidth, int worldHeight) {
        mWorldWidth = worldWidth;
        mWorldHeight = worldHeight;
    }

    @Override
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    @Override
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

    @Override
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

    @Override
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
