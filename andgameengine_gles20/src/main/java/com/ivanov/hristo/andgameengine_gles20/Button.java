package com.ivanov.hristo.andgameengine_gles20;

import android.util.Log;

/**
 * Created by hristo on 12/25/14.
 */
public class Button extends Sprite{
    private Texture texture1;
    private Texture texture2;
    protected boolean presed=false;
    private float anim_time=0.25f;
    private float elapsed_time;

    public Button(float posX, float posY, float width, float height,Texture texture1,Texture texture2){
        super(posX, posY, width, height, texture1);
        this.texture1=texture1;
        if (texture2!=null) this.texture2=texture2;  //texture2 can be null....
        else this.texture2=texture1;
    }

    public boolean press(float posX, float posY){
        //Log.d("OnTouch", ""+!this.presed+"   "+isOnMe(posX,posY));
        if (!this.presed && isOnMe(posX,posY)) {
            this.presed = true;
            this.setTexture(this.texture2);
            this.setResize(0.5f, 0.5f);
            this.elapsed_time=0.0f;
            this.OnTouch_start();
            return true;
        }
        return false;
    }

    public void setAnim_time(float anim_time){
        this.anim_time=anim_time;
    }

    @Override
    protected void update(float deltaTime){
        super.update(deltaTime);
        if(this.presed) {
            this.elapsed_time += deltaTime;
            if (this.elapsed_time >= this.anim_time) {
                this.presed = false;
                this.setTexture(this.texture1);
                this.setResize(1.0f, 1.0f);
                this.OnTouch_end();
            }
        }
    }

    //methods to Override
    public void OnTouch_start(){}
    public void OnTouch_end(){}
}
