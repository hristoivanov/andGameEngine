package com.ivanov.hristo.andgameengine;

/**
 * Created by hristo on 12/24/14.
 */
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.ivanov.hristo.andgameengine_gles20.Button;
import com.ivanov.hristo.andgameengine_gles20.GLES20Activity;
import com.ivanov.hristo.andgameengine_gles20.GLES20Renderer;
import com.ivanov.hristo.andgameengine_gles20.Sound;
import com.ivanov.hristo.andgameengine_gles20.Text;
import com.ivanov.hristo.andgameengine_gles20.Texture;
import com.ivanov.hristo.andgameengine_gles20.Sprite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GLES20Activity{
    @Override
    public GLES20Renderer gameRenderer() {
        return new GLES20Renderer(this, this.DeviceWidth, this.DeviceHeight, 800, 480) {

            public Texture text1;
            public Texture text2;
            public Sprite sp1;
            public Button b1;
            public Text the_text;
            public Sound sn1;
            @Override
            public void loadTextures(){
                this.text1=this.loadTexture("drawable/abc");
                this.text2=this.loadTexture("drawable/nave_128");
            }
            @Override
            public void onCreate(){
                the_text=new Text(20.0f,50.0f,1.6f,this.text1,"YoLo Bitchezz:45");
                this.attach(the_text);
                b1=new Button(100.0f, 100.0f, 100.0f, 100.0f, text2, null){
                    @Override
                    public void OnTouch_end(){
                        String type="twi";
                        boolean found = false;
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("image/jpeg");

                        // gets the list of intents that can be loaded.
                        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
                        if (!resInfo.isEmpty()){
                            for (ResolveInfo info : resInfo) {
                                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                                        info.activityInfo.name.toLowerCase().contains(type) ) {
                                    share.putExtra(Intent.EXTRA_SUBJECT,  "subject");
                                    share.putExtra(Intent.EXTRA_TEXT,     "Give it a try, Its awesome:\n https://play.google.com/store/apps/details?id=com.uah.ubicuos.HAF.spacefuckers");
                                    share.setPackage(info.activityInfo.packageName);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                                return;

                            startActivity(Intent.createChooser(share, "Select"));
                        }
                    }
                };
                this.attach(b1);
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