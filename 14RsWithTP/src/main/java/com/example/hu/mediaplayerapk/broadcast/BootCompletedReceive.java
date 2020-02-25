package com.example.hu.mediaplayerapk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.ui.activity.MainActivity;
import com.example.hu.mediaplayerapk.util.FileUtils;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class BootCompletedReceive extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceive";
    public boolean isOpen = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.MY_REBOOT")) {
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            isOpen = ((MyApplication) context.getApplicationContext()).isOpen();
            Log.e(TAG, "onReceive: BOOT_COMPLETED open main activity  isOpen:" + isOpen);
            if (!isOpen) {
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
                ((MyApplication) context.getApplicationContext()).setOpen(true);
            }
        } else if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
            Log.e(TAG, "onReceive: MEDIA_MOUNTED path = " + intent.getData().getPath());
            isOpen = ((MyApplication) context.getApplicationContext()).isOpen();
            if (intent.getData().toString().contains("usb")) {
                ((MyApplication) context.getApplicationContext()).initUSBPath(intent.getData().getPath());
            } else {
                ((MyApplication) context.getApplicationContext()).initFilePath();
            }
            Log.e(TAG, "onReceive: MEDIA_MOUNTED isOpen:" + isOpen);
            if (FileUtils.getSize(Config.USB_STORAGE_ROOT_PATH) == 0 && !isOpen) {
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                ((MyApplication) context.getApplicationContext()).setOpen(true);
                context.startActivity(myIntent);
            }
        }
    }
}
