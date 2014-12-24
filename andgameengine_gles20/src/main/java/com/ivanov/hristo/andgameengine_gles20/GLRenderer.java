package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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


    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    private Sprite   mSquare1;
    private Sprite   mSquare2;
    public GLRenderer(int width, int height) {
        this.fpsLogger=new FPSLogger();
        this.Width=width;
        this.Height=height;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface created.");
        }
        mSquare1   = new Sprite(100.0f, 100.0f, 40.0f, 40.0f);
        mSquare2   = new Sprite(400.0f, 400.0f, 40.0f, 40.0f);
        mSquare1.setVel(20.0f, 0.0f);
        mSquare2.setVel(-10.0f, 0.0f);
        mSquare2.isRotated=true;
        mSquare2.isSized=true;
        mSquare2.ResizeX=2.0f;
        mSquare2.rotation=20.0f;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface changed.");
        }

        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.orthoM(mProjectionMatrix, 0, 0f, this.Width, -this.Height, 0f, 1.0f, -1.0f);
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
        mSquare1.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);
        mSquare2.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);

        this.fpsLogger.logFrame();
    }

    public void setColorBackGround(float red,float green,float blue){
        GLES20.glClearColor(red, green, blue, 1);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.d("GLRenderer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public abstract void onCreate(int width, int height,
                                  boolean contextLost);

    public abstract void onDrawFrame(boolean firstDraw);
}
