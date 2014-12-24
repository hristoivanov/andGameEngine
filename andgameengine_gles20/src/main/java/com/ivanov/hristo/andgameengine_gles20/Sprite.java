package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Sprite {
    protected float posX;
    protected float posY;
    protected float Width;
    protected float Height;
    protected boolean visible=true;

    protected float velX=0f;
    protected float velY=0f;
    protected boolean isRotated=false;
    protected float rotation=0.0f;
    protected float rotationSpeed=0.0f;
    protected boolean isSized=false;
    protected float ResizeX=1.0f;
    protected float ResizeY=1.0f;

    protected FloatBuffer vertexBuffer;
    protected ShortBuffer drawListBuffer;
    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

    static final int BYTES_PER_FLOAT = 4; //COORDS come in float.
    static final int COORDS_PER_VERTEX = 2; //TODO add more when textures implemented...
    static final int BYTES_PER_COOR= COORDS_PER_VERTEX * BYTES_PER_FLOAT;
    static final int VERTEXS_PER_SQUARE = 4;
    static final int BYTES_PER_SQUARE = VERTEXS_PER_SQUARE * BYTES_PER_COOR;
    static final int BYTES_PER_SHORT = 2;
    static final int BYTES_PER_ORDER = 6 * BYTES_PER_SHORT;

    public Sprite(float posX, float posY, float width, float height){
        this.posX = posX;
        this.posY = posY;
        this.Width = width;
        this.Height = height;

        ByteBuffer bb = ByteBuffer.allocateDirect(BYTES_PER_SQUARE);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(   new float[]{    0,          0-height,
                0+width,    0-height,
                0+width,    0,
                0,          0               });
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(BYTES_PER_ORDER);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mModelMatrix) {
        if (!this.visible)
            return;

        Matrix.setIdentityM(mModelMatrix, 0);
        if(isRotated || isSized){
            Matrix.translateM(mModelMatrix, 0, posX+Width/2, -posY-Height/2, 0.0f);
            if(isRotated) Matrix.rotateM(mModelMatrix, 0, rotation, 0.0f, 0.0f, 1.0f);
            if(isSized) Matrix.scaleM(mModelMatrix, 0, ResizeX, ResizeY, 0.0f);
            Matrix.translateM(mModelMatrix, 0, -Width/2, +Height/2, 0.0f);}
        else{
            Matrix.translateM(mModelMatrix, 0, posX, -posY, 0.0f);}

        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvpMatrix, 0);


        GLES20.glUseProgram(mProgram);

        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                BYTES_PER_COOR, vertexBuffer);

        //TODO change this color shit with textures.
        float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //TODO change this color shit with textures.

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    protected void update(float deltaTime){
        this.posX+=this.velX*deltaTime;
        this.posY+=this.velY*deltaTime;
        if (this.isRotated)this.rotation=(this.rotation+this.rotationSpeed*deltaTime)%360.0f;
    }

    public void setVel(float velX, float velY) {
        this.velX = velX;
        this.velY = velY;
    }

    private final int mProgram;
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
}
