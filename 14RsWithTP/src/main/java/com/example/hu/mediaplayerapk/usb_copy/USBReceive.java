package com.example.hu.mediaplayerapk.usb_copy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.FileUtils;

/**
 * Created by Administrator on 2017/1/11.
 */

public class USBReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
            if (intent.getData().toString().contains("usb")) {
                ((MyApplication) context.getApplicationContext()).initUSBPath(intent.getData().getPath());
            } else {
                ((MyApplication) context.getApplicationContext()).initFilePath();
            }
            if (FileUtils.getSize(Config.USB_STORAGE_ROOT_PATH) > 0 && intent.getData().toString().contains("usb")) {
                Intent myIntent = new Intent(context, USBCopyActivity.class);
                context.startActivity(myIntent);
            }
        }
    }
}
