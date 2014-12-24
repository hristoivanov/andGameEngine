package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */
import android.util.Log;

public class FPSLogger {
    long startTime=System.nanoTime();
    int frames=0;

    public void logFrame(){
        frames++;
        if(System.nanoTime()-startTime>=1000000000) {
            Log.d("FPSCounter","fps: "+ frames);
            frames=0;
            startTime=System.nanoTime();
        }
    }
}
