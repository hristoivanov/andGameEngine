package com.ivanov.hristo.andgameengine_gles20;

import android.content.Context;

/**
 * Created by hristo on 12/24/14.
 */

public class GLES20Renderer extends GLRenderer {

    public GLES20Renderer(Context contxt, int device_width, int device_height, int width, int height){
        super(contxt, device_width, device_height, width, height);
    }

    @Override
    public void loadTextures(){}
    @Override
    public void onCreate(){}
    @Override
    public void onUpdate(float deltaTime){}
}
