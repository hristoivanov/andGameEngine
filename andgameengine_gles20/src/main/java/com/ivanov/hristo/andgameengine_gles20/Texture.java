package com.ivanov.hristo.andgameengine_gles20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by hristo on 12/24/14.
 */
public class Texture {
    private Context mContext;
    private int textureId;
    private String file_name;

    public Texture(Context mContext, String file_name){
        this.mContext=mContext;
        this.file_name=file_name;

        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        textureId = texturenames[0];
        int id = this.mContext.getResources().getIdentifier(this.file_name, null, mContext.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }

    public void bind() {//Change to protected If its public change it
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    }
}
