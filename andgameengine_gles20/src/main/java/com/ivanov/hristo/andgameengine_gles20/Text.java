package com.ivanov.hristo.andgameengine_gles20;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by hristo on 12/26/14.
 */
public class Text {
    private float posX;
    private float posY;
    private float Size;
    protected boolean visible=true;
    private float DefSizeX=10;
    private float DefSizeY=16;
    private float miSizeX;
    private float miSizeY;
    private Texture texture;
    private String Text;
    private int TextLenght;

    private int mProgram;

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
    static final int COORDS_PER_ORDER = 6;
    static final int BYTES_PER_ORDER = COORDS_PER_ORDER * BYTES_PER_SHORT;

    static final int TEX_COORDS_PER_VERTEX = 2;
    static final int TEX_BYTES_PER_VERTEX = TEX_COORDS_PER_VERTEX * BYTES_PER_FLOAT;
    static final int TEX_VERTEXS_PER_TEXTURE = 4;
    static final int BYTES_PER_TEXTURE = TEX_VERTEXS_PER_TEXTURE * TEX_BYTES_PER_VERTEX;

    public Text(float posX, float posY, float Size,Texture texture,String text){
        this.posX = posX;
        this.posY = posY;
        this.Size = Size;
        this.miSizeX=Size*this.DefSizeX;
        this.miSizeY=Size*this.DefSizeY;
        this.texture = texture;
        this.Text=text;
        SetText(this.Text);
    }

    public void SetText(String text) {
        this.Text = text;
        this.TextLenght = this.Text.length();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTES_PER_SQUARE * this.TextLenght);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();

        ByteBuffer tex_bb = ByteBuffer.allocateDirect(BYTES_PER_TEXTURE * this.TextLenght);
        tex_bb.order(ByteOrder.nativeOrder());
        textureBuffer = tex_bb.asFloatBuffer();

        float slim = 20.0f / 512.0f;
        float slim2 = 32.0f / 128.0f;
        float shady = 0.0f;//Columna..
        float shady2 = 0.0f; //Fila..

        for (int i = 0; i < this.TextLenght; i++) {
            int aux = (int) this.Text.charAt(i);//Coger Codigo ASCII  Para todo lo de abajo consultar tabla ASCII..
            shady = 0.0f;
            shady2 = 0.0f;
            if (aux > 31 && aux < 57) {
                shady = slim * (aux - 32);
                shady2 = 0.0f;
            }
            if (aux > 56 && aux < 82) {
                shady = slim * (aux - 57);//
                shady2 = slim2;
            }
            if (aux > 81 && aux < 107) {
                shady = slim * (aux - 82);
                shady2 = slim2 * 2;
            }
            if (aux > 106 && aux < 127) {
                shady = slim * (aux - 107);
                shady2 = slim2 * 3;
            }
            if (aux == 209) {//Ñ
                shady = 400.0f / 512.0f;
                shady2 = slim2 * 3;
            }
            if (aux == 241) {//ñ
                shady = 420.0f / 512.0f;
                shady2 = slim2 * 3;
            }
            vertexBuffer.put(new float[]{this.miSizeX * i, 0 - this.miSizeY,
                    this.miSizeX * i + this.miSizeX, 0 - this.miSizeY,
                    this.miSizeX * i + this.miSizeX, 0,
                    this.miSizeX * i, 0,});

            textureBuffer.put(new float[]{shady, slim2 + shady2,
                    slim + shady, slim2 + shady2,
                    slim + shady, shady2,
                    shady, shady2});
        }

        vertexBuffer.flip();
        textureBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(BYTES_PER_ORDER * this.TextLenght);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        for (int i = 0; i < this.TextLenght; i++) {
            short aux = (short) (i * 4);
            drawListBuffer.put(new short[]{aux, (short) (aux + 1), (short) (aux + 2),
                    (short) (aux + 2), (short) (aux + 3), aux});
        }
        drawListBuffer.position(0);
    }

    protected void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mModelMatrix){
        if (!this.visible)
            return;

        texture.bind();

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, posX, -posY, 0.0f);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUseProgram(mProgram);

        // Prepare the triangle coordinate data
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                BYTES_PER_VERTEX, vertexBuffer);

        // Texture
        int mTexCoordLoc = GLES20.glGetAttribLocation(mProgram, "a_texCoord" );
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
        GLES20.glVertexAttribPointer ( mTexCoordLoc, TEX_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEX_BYTES_PER_VERTEX, textureBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        int mSamplerLoc = GLES20.glGetUniformLocation (mProgram,"s_texture" );
        GLES20.glUniform1i ( mSamplerLoc, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, COORDS_PER_ORDER * this.TextLenght,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
    protected void setmProgram(int mProgram){
        this.mProgram=mProgram;
    }


    public float getPosX() {
        return posX;
    }
    public float[] getPos(){
        return new float[]{this.posX, this.posY};
    }
    public float getDefSizeX() {
        return DefSizeX;
    }
    public float getDefSizeY() {
        return DefSizeY;
    }
    public String getText() {
        return Text;
    }
    public int getTextLenght() {
        return TextLenght;
    }
    public void setPos(float posX, float posY){
        this.posX=posX;
        this.posY=posY;
    }
    public void setSize(float size) {
        Size = size;
        this.miSizeX=Size*this.DefSizeX;
        this.miSizeY=Size*this.DefSizeY;
        this.SetText(this.Text);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
