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
    private final short drawOrder[] = { 0, 1, 2, 2, 3, 0 };
    protected FloatBuffer textureBuffer;

    static final int BYTES_PER_FLOAT = 4; //COORDS come in float.
    static final int COORDS_PER_VERTEX = 2;
    static final int BYTES_PER_VERTEX = COORDS_PER_VERTEX * BYTES_PER_FLOAT;
    static final int VERTEXS_PER_SQUARE = 4;
    static final int BYTES_PER_SQUARE = VERTEXS_PER_SQUARE * BYTES_PER_VERTEX;
    static final int BYTES_PER_SHORT = 2;
    static final int BYTES_PER_ORDER = 6 * BYTES_PER_SHORT;

    static final int TEX_COORDS_PER_VERTEX = 2;
    static final int TEX_BYTES_PER_VERTEX = TEX_COORDS_PER_VERTEX * BYTES_PER_FLOAT;
    static final int TEX_VERTEXS_PER_TEXTURE = 4;
    static final int BYTES_PER_TEXTURE = TEX_VERTEXS_PER_TEXTURE * TEX_BYTES_PER_VERTEX;

    private int mProgram;
    private Texture texture;

    public Sprite(int mProgram, float posX, float posY, float width, float height, Texture texture){
        this.mProgram=mProgram;
        this.posX = posX;
        this.posY = posY;
        this.Width = width;
        this.Height = height;
        this.texture=texture;

        this.load_vertices();

        ByteBuffer dlb = ByteBuffer.allocateDirect(BYTES_PER_ORDER);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer tex_bb = ByteBuffer.allocateDirect(BYTES_PER_TEXTURE);
        tex_bb.order(ByteOrder.nativeOrder());
        textureBuffer = tex_bb.asFloatBuffer();
        textureBuffer.put(  new float[]{    0.0f, 1.0f,
                                            1.0f, 1.0f,
                                            1.0f, 0.0f,
                                            0.0f, 0.0f});
        textureBuffer.position(0);
    }

    protected void load_vertices(){
        ByteBuffer bb = ByteBuffer.allocateDirect(BYTES_PER_SQUARE);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(   new float[]{    0,              0-this.Height,
                                            0+this.Width,   0-this.Height,
                                            0+this.Width,   0,
                                            0,              0               });
        vertexBuffer.position(0);
    }

    protected void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mModelMatrix) {
        if (!this.visible)
            return;

        texture.bind();

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
                BYTES_PER_VERTEX, vertexBuffer);

        //TODO change this color shit with textures.
        /*float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);*/
        int mTexCoordLoc = GLES20.glGetAttribLocation(mProgram, "a_texCoord" );
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
        GLES20.glVertexAttribPointer ( mTexCoordLoc, TEX_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEX_BYTES_PER_VERTEX, textureBuffer);

        //TODO change this color shit with textures.

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        int mSamplerLoc = GLES20.glGetUniformLocation (mProgram,"s_texture" );
        GLES20.glUniform1i ( mSamplerLoc, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }


    protected void update(float deltaTime){
        this.posX+=this.velX*deltaTime;
        this.posY+=this.velY*deltaTime;
        if (this.isRotated)this.rotation=(this.rotation+this.rotationSpeed*deltaTime)%360.0f;
        this.OnUpdate(deltaTime);
    }
    //This Method is meant to be overwritten
    public void OnUpdate(float deltaTime){}

    public void setVel(float velX, float velY) {
        this.velX = velX;
        this.velY = velY;
    }

    public float[] getVel(){
        return new float[]{this.velX, this.velY};
    }

    public void setPos(float posX, float posY){
        this.posX=posX;
        this.posY=posY;
    }

    public float[] getPos(){
        return new float[]{this.posX, this.posY};
    }

    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setSize(float Width, float Height){
        this.Width=Width;
        this.Height=Height;
        this.load_vertices();
    }

    public float[] getSize(){
        return new float[]{this.Width, this.Height};
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        if (rotationSpeed==0.0f&&this.rotation==0.0f) this.isRotated=false;
        else this.isRotated=true;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
        if (rotationSpeed==0.0f&&this.rotation==0.0f) this.isRotated=false;
        else this.isRotated=true;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setResize(float ResizeX, float ResizeY){
        this.ResizeX = ResizeX;
        this.ResizeY = ResizeY;
        if(this.ResizeX==0.0f && this.ResizeY==0.0f)this.isSized=false;
        else this.isSized=true;
    }

    public float[] getResize(){
        return new float[]{this.ResizeX, this.ResizeY};
    }

    public boolean isRotated() {
        return isRotated;
    }

    public boolean isSized() {
        return isSized;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
