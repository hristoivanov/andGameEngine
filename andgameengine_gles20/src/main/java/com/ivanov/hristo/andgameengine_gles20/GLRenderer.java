package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/24/14.
 */
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import android.util.Log;
import com.ivanov.hristo.andgameengine_gles20.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class GLRenderer implements Renderer {
    private FPSLogger fpsLogger;
    final float Nanos = 1000000000.0f;
    private long startTime;

    private int Width;
    private int Height;
    private float deviceRelationX;
    private float deviceRelationY;

    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mProgram_Sprite;
    private Context contxt;

    private ArrayList<Sprite> sprites;
    private Object spritesNotInUse = new Object();
    private ArrayList<Button> buttons;
    private Object buttonsNotInUse=new Object();
    private ArrayList<Text> texts;
    private Object textsNotInUse=new Object();

    public GLRenderer(Context contxt, int device_width, int device_height, int width, int height) {
        this.contxt=contxt;
        this.fpsLogger=new FPSLogger();
        this.Width=width;
        this.Height=height;
        this.deviceRelationX=((float)this.Width)/device_width;
        this.deviceRelationY=((float)this.Height)/device_height;

        this.sprites=new ArrayList<Sprite>();
        this.texts=new ArrayList<Text>();
        this.buttons=new ArrayList<Button>();
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface created.");
        }

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram_Sprite = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram_Sprite, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram_Sprite, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram_Sprite);                  // create OpenGL program executables

        this.loadTextures();
        this.onCreate();
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height) {
        if (Util.DEBUG) {
            Log.d(Util.LOG_TAG, "Surface changed.");
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.orthoM(mProjectionMatrix, 0, 0.0f, this.Width, -this.Height, 0.0f, 1.0f, -1.0f); //TODO  100 --> 0
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        startTime = System.nanoTime();
    }

    @Override
    public void onDrawFrame(GL10 notUsed) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        float deltaTime = (System.nanoTime()-startTime) / Nanos;
        startTime = System.nanoTime();

        this.update(deltaTime);
        this.draw(deltaTime);

        this.fpsLogger.logFrame();
    }

    protected void update(float deltaTime){
            synchronized(spritesNotInUse){
                Iterator<Sprite> it=this.sprites.iterator();
                while(it.hasNext()){
                    Sprite sprite=it.next();
                    sprite.update(deltaTime);
                }
            }
            synchronized(buttonsNotInUse){
                Iterator<Button> it=this.buttons.iterator();
                while(it.hasNext()){
                    Button button=it.next();
                    button.update(deltaTime);
                }
            }
        this.onUpdate(deltaTime);
    }

    protected void draw(float deltaTime){
        synchronized(spritesNotInUse){
            Iterator<Sprite> it=this.sprites.iterator();
            while(it.hasNext()){
                Sprite sprite=it.next();
                sprite.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);
            }
        }

        synchronized(textsNotInUse){
            Iterator<Text> it=this.texts.iterator();
            while(it.hasNext()){
                Text text=it.next();
                text.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);
            }
        }

        synchronized(buttonsNotInUse){
            Iterator<Button> it=this.buttons.iterator();
            while(it.hasNext()){
                Button button=it.next();
                button.draw(mProjectionMatrix, mViewMatrix, mModelMatrix);
            }
        }
    }

    protected void ONTouch(MotionEvent event){
        float PosX=event.getRawX()*this.deviceRelationX;
        float PosY=event.getRawY()*this.deviceRelationY;
        int type = event.getAction();
        boolean handled=false;
        switch (type) {
            case MotionEvent.ACTION_DOWN:   // finger makes contact with the screen.
                synchronized(buttonsNotInUse){
                    Iterator<Button> it=this.buttons.iterator();
                    while(it.hasNext()){
                        Button button=it.next();
                        if(button.press(PosX, PosY)){
                            handled=true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:   // finger moves on the screen.
                break;
            case MotionEvent.ACTION_UP:     // finger leaves the screen.
                break;
        }
        if (!handled)onTouch(type,PosX,PosY);

    }

    public void attach(Sprite sprite){
        sprite.setmProgram(this.mProgram_Sprite);
        synchronized(spritesNotInUse){
            this.sprites.add(sprite);
        }
    }
    public void dettach(Sprite sprite){
        synchronized(spritesNotInUse){
            this.sprites.remove(sprite);
        }
    }

    public void attach(Text text){
        text.setmProgram(this.mProgram_Sprite);
        synchronized(textsNotInUse){
            this.texts.add(text);
        }
    }
    public void dettach(Text text){
        synchronized(textsNotInUse){
            this.texts.remove(text);
        }
    }

    public void attach(Button button){
        button.setmProgram(this.mProgram_Sprite);
        synchronized(buttonsNotInUse){
            this.buttons.add(button);
        }
    }
    public void dettach(Button button){
        synchronized(buttonsNotInUse){
            this.buttons.remove(button);
        }
    }

    public void setColorBackGround(float red,float green,float blue){
        GLES20.glClearColor(red, green, blue, 1);
    }

    public Texture loadTexture(String file_name){
        return new Texture(this.contxt, file_name);
    }

    public void finish(){
        GLES20Activity aux = (GLES20Activity) contxt;
        aux.finish();
    }


    //Methods meant to be overwritten
    public abstract void loadTextures();
    public abstract void onCreate();
    public abstract void onUpdate(float deltaTime);
    public void onTouch(int type,float X, float Y){}


    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.d("GLRenderer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";

    private final String fragmentShaderCode =
                    "precision mediump float;" +
                     "varying vec2 v_texCoord;" +
                     "uniform sampler2D s_texture;" +
                     "void main() {" +
                     "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                     "}";
}
