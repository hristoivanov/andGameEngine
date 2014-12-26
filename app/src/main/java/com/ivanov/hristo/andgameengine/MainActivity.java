package com.ivanov.hristo.andgameengine;

/**
 * Created by hristo on 12/24/14.
 */
import com.ivanov.hristo.andgameengine_gles20.Button;
import com.ivanov.hristo.andgameengine_gles20.GLES20Activity;
import com.ivanov.hristo.andgameengine_gles20.GLES20Renderer;
import com.ivanov.hristo.andgameengine_gles20.Sound;
import com.ivanov.hristo.andgameengine_gles20.Text;
import com.ivanov.hristo.andgameengine_gles20.Texture;
import com.ivanov.hristo.andgameengine_gles20.Sprite;

public class MainActivity extends GLES20Activity{
    @Override
    public GLES20Renderer gameRenderer() {
        return new GLES20Renderer(this, this.DeviceWidth, this.DeviceHeight, 800, 480) {

            public Texture text1;
            public Texture text2;
            public Sprite sp1;
            public Text the_text;
            public Sound sn1;
            @Override
            public void loadTextures(){
                this.text1=this.loadTexture("drawable/abc");
                this.text2=this.loadTexture("drawable/nave_128");
            }
            @Override
            public void onCreate(){
                this.sp1=new Sprite(100.0f, 100.0f, 100.0f, 100.0f, text2);
                this.attach(sp1);
                the_text=new Text(20.0f,50.0f,1.6f,this.text1,"YoLo Bitchezz:45");
                this.attach(the_text);
                sn1 = audioManager.newSound("go.wav");
            }
            @Override
            public void onUpdate(float deltaTime){}

            @Override
            public void onTouch(int type,float X, float Y){
                sn1.play(0.5f);
            }

        };
    }
}