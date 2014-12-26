package com.ivanov.hristo.andgameengine_gles20;

/**
 * Created by hristo on 12/26/14.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;


public interface FileIO {
    public InputStream readAsset(String fileName) throws IOException;

    public InputStream readFile(String fileName) throws IOException;

    public OutputStream writeFile(String fileName) throws IOException;

    public SharedPreferences getPreferences();
}
