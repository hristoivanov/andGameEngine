package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.graphics.Point;

import com.ivanov.hristo.andgameengine_gles20.util.Util;

public abstract class GLES20Activity extends Activity {

    private GLSurfaceView mGLView;
    private GLES20Renderer mRenderer;
    public int DeviceWidth;
    public int DeviceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Util.LOG_TAG, "Game stated");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (hasGLES20()) {
            mGLView = new GLSurfaceView(this){
                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    try{
                        mRenderer.ONTouch(event);
                    }catch(NullPointerException exc ){
                        Log.d("OnTouch"," NullPointerException");
                    }
                    return true;
                }
            };
            this.loadDeviceDimensions();
            this.mRenderer=gameRenderer();
            mGLView.setEGLContextClientVersion(2);
            mGLView.setPreserveEGLContextOnPause(true);
            mGLView.setRenderer(this.mRenderer);
        } else {
            Log.d(Util.LOG_TAG, "Yolo bitch no GLES20");
            return;
        }

        setContentView(mGLView);
    }

    public abstract GLES20Renderer gameRenderer();

    private boolean hasGLES20() {
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    protected void loadDeviceDimensions(){
        Point size = new Point();//Calcular Width y Height.
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        DeviceWidth = size.x;
        DeviceHeight = size.y;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGLView != null) {
            mGLView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLView != null) {
            mGLView.onPause();
        }
    }
}
