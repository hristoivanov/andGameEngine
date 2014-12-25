package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;

import android.util.Log;

import com.ivanov.hristo.andgameengine_gles20.util.Util;

public abstract class GLRenderer implements Renderer {
    private FPSLogger fpsLogger;
    final float Nanos = 1000000000.0f;
    private long startTime;

    private int Width;
    private int Height;


    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private int mProgram_Sprite;
    private Context contxt;

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    private Sprite   mSquare1;
    private Sprite   mSquare2;
    public GLRenderer(Context contxt, int width, int height) {
        this.contxt=contxt;
        this.fpsLogger=new FPSLogger();
        this.Width=width;
        this.Height=height;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface created.");
        }

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram_Sprite = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram_Sprite, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram_Sprite, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram_Sprite);                  // create OpenGL program executables

        Texture text1=new Texture(this.contxt, "drawable/ic_launcher");
        Texture text2=new Texture(this.contxt, "drawable/nave_128");
        mSquare1   = new Sprite(mProgram_Sprite, 100.0f, 100.0f, 100.0f, 100.0f, text1);
        mSquare2   = new Sprite(mProgram_Sprite, 400.0f, 100.0f, 100.0f, 100.0f, text2);
        mSquare1.setVel(20.0f, 0.0f);
        mSquare2.setVel(-10.0f, 0.0f);
        mSquare2.isRotated=true;
        mSquare2.rotation=20.0f;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface changed.");
        }

        GLES20.glClearColor(0.0f, 0.3f, 0.0f, 1);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.orthoM(mProjectionMatrix, 0, -100f, this.Width, -this.Height, 100f, 1.0f, -1.0f); //TODO  100 --> 0
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        startTime = System.nanoTime();
    }


    @Override
    public void onDrawFrame(GL10 notUsed) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        float deltaTime = (System.nanoTime()-startTime) / Nanos;
        startTime = System.nanoTime();

        mSquare1.update(deltaTime);
        mSquare2.update(deltaTime);
        mSquare2.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);
        mSquare1.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);


        this.fpsLogger.logFrame();
    }

    public void setColorBackGround(float red,float green,float blue){
        GLES20.glClearColor(red, green, blue, 1);
    }


    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.d("GLRenderer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public abstract void onCreate(int width, int height, boolean contextLost);
    public abstract void onDrawFrame(boolean firstDraw);


    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";

    private final String fragmentShaderCode =
                    "precision mediump float;" +
                     "varying vec2 v_texCoord;" +
                     "uniform sampler2D s_texture;" +
                     "void main() {" +
                     "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                     "}";
}
