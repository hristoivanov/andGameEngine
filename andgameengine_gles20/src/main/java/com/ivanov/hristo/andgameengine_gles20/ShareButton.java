package com.ivanov.hristo.andgameengine_gles20;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by hristo on 12/27/14.
 */
public class ShareButton extends Button{
    private Context contx;
    private String msg;
    private String app;
    public enum APPS {TWITTER, FACEBOOK};
    
    public ShareButton(float posX, float posY, float width, float height,Texture texture1,Texture texture2, Context contx, String msg, APPS app){
        super(posX, posY, width, height, texture1, texture2);
        this.contx =contx;
        this.msg=msg;
        if (app==APPS.TWITTER) this.app="twi";
        if (app==APPS.FACEBOOK) this.app="face";
    }
    
    @Override
    public void OnTouch_end(){
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = contx.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(this.app) ||
                        info.activityInfo.name.toLowerCase().contains(this.app) ) {
                    share.putExtra(Intent.EXTRA_SUBJECT,  "It's awesome");
                    share.putExtra(Intent.EXTRA_TEXT,     this.msg);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;
            contx.startActivity(Intent.createChooser(share, "Select"));
        }
    }
};

