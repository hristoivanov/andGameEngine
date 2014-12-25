package com.ivanov.hristo.andgameengine;

/**
 * Created by hristo on 12/24/14.
 */
import com.ivanov.hristo.andgameengine_gles20.Button;
import com.ivanov.hristo.andgameengine_gles20.GLES20Activity;
import com.ivanov.hristo.andgameengine_gles20.GLES20Renderer;
import com.ivanov.hristo.andgameengine_gles20.Texture;
import com.ivanov.hristo.andgameengine_gles20.Sprite;

public class MainActivity extends GLES20Activity{
    @Override
    public GLES20Renderer gameRenderer() {
        return new GLES20Renderer(this, this.DeviceWidth, this.DeviceHeight, 800, 480) {

            public Texture text1;
            public Texture text2;
            public Sprite sp1;
            public Sprite sp2;
            public Button bt1;
            @Override
            public void loadTextures(){
                this.text1=this.loadTexture("drawable/ic_launcher");
                this.text2=this.loadTexture("drawable/nave_128");
            }
            @Override
            public void onCreate(){
                this.sp1=new Sprite(100.0f, 100.0f, 100.0f, 100.0f, text1);
                this.sp2=new Sprite(400.0f, 100.0f, 100.0f, 100.0f, text2);
                this.bt1=new Button(400.0f, 100.0f, 200.0f, 100.0f, text2, null);
                sp1.setVel(20.0f, 0.0f);
                sp2.setVel(-20.0f, 0.0f);
                sp2.setRotation(20.0f);

                //this.attach(sp1);
                //this.attach(sp2);
                this.attach(bt1);
            }
            @Override
            public void onUpdate(float deltaTime){}
        };
    }
}