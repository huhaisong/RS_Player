package com.example.hu.mediaplayerapk.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobblerSupport2 {
    InputStream is = null;
    String msg = "";
    String tmpFile = null;

    public StreamGobblerSupport2(InputStream paramInputStream) {
        this.is = paramInputStream;
    }

    public String message() {
        return this.msg;
    }

    public void run() {
        try {
            Log.d("Process", "StreamGobbler2");
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(this.is));
            while (true) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    if (this.tmpFile == null)
                        break;
                    throw new NullPointerException();
                }
                this.msg += str;
            }
        } catch (IOException localIOException) {
            Log.e("ProcessService", "Error: " + localIOException.toString());
            localIOException.printStackTrace();
        }
    }
}
