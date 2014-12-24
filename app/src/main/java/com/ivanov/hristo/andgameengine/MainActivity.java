package com.ivanov.hristo.andgameengine;

/**
 * Created by hristo on 12/24/14.
 */
import com.ivanov.hristo.andgameengine_gles20.GLES20Activity;
import com.ivanov.hristo.andgameengine_gles20.GLES20Renderer;

public class MainActivity extends GLES20Activity{
    @Override
    public GLES20Renderer gameRenderer() {
        return new GLES20Renderer(800, 480) {
            @Override
            public void onCreate(int width, int height, boolean contextLost) {
                super.onCreate(width, height, contextLost);
            }

            @Override
            public void onDrawFrame(boolean firstDraw) {
                super.onDrawFrame(firstDraw);
            }
        };
    }
}