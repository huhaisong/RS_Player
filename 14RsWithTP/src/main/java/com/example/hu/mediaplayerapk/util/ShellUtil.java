package com.example.hu.mediaplayerapk.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.config.Config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/31.
 */

public class ShellUtil {

    private static final String SET_CONFIG_STR = "set_config_str";
    private static final String SET_CONFIG_INT = "set_config_int";
    private static final String IMPACTV_NAME = "CFG_WORK_DIR";
    private static final String IMPACTTV_NAME = "CFG_WORK_DIR2";
    private static final String EVENT_NAME = "CFG_EVENT_DIR";
    private static final String USB_EVENT_NAME = "CFG_EVENT_COPY_DIR";
    private static final String USB_IMPACTV_NAME = "CFG_COPY_DIR";
    private static final String SYSTEM_NAME = "CFG_UPG_DIR";
    private static final String SHOW_MEG_BOX = "show_msg_box";
    private static final String TAG = "ShellUtil";

    public static void executeShellCommand(Context context) {
        File file = new File(Config.USB_STORAGE_ROOT_PATH);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                if (name.equals("shell.txt")) {
                    return true;
                }
                return false;
            }
        });
        if (files == null || files.length == 0) {
            return;
        }

        ArrayList<String> commands = new ArrayList<>();

        try {
            FileInputStream input = new FileInputStream(files[0].getAbsolutePath());
            InputStreamReader streamReader = new InputStreamReader(input, "gb2312");
            LineNumberReader reader = new LineNumberReader(streamReader);
            String line = null;
            StringBuilder allLine = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                commands.add(line);
            }
            streamReader.close();
            reader.close();
            input.close();
            allLine.toString();
        } catch (Exception e) {
            commands = null;
            e.printStackTrace();
        }
        commands.remove("");
        if (commands == null || commands.size() == 0) {
            return;
        }
        Log.e(TAG, "executeShellCommand: commands = " + commands.toString());
        for (String command : commands) {
            String[] sets = command.split(" ");
            String firstItem = sets[0].replaceAll(" ", "");
            switch (firstItem) {
                case SET_CONFIG_STR: {
                    if (sets.length < 3) {
                        return;
                    }
                    String secondItem = sets[1].replace(" ", "");
                    String thirdItem = sets[2].replace(" ", "");
                    thirdItem = thirdItem.replaceAll(";", "");
                    switch (secondItem) {
                        case IMPACTV_NAME: {
                            SPUtils.putString(context, Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.IMPACTV_FILE_NAME, thirdItem);
                            SPUtils.putString(context, Config.EXTERNAL_FILE_ROOT_PATH + File.separator + Config.IMPACTV_FILE_NAME, thirdItem);
                        }
                        break;
                        case EVENT_NAME: {
                            SPUtils.putString(context, Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.EVENT_FILE_NAME, thirdItem);
                            SPUtils.putString(context, Config.EXTERNAL_FILE_ROOT_PATH + File.separator + Config.EVENT_FILE_NAME, thirdItem);
                        }
                        break;
                        case IMPACTTV_NAME: {
                            SPUtils.putString(context, Config.EXTERNAL_FILE_ROOT_PATH + File.separator + Config.IMPACTTV_FILE_NAME, thirdItem);
                            SPUtils.putString(context, Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.IMPACTTV_FILE_NAME, thirdItem);
                        }
                        break;
                        case USB_IMPACTV_NAME: {
                            SPUtils.putString(context, Config.USB_STORAGE_ROOT_PATH + File.separator + Config.USB_STORAGE_IMPACTV_FILE_NAME, thirdItem);
                        }
                        break;
                        case USB_EVENT_NAME: {
                            SPUtils.putString(context, Config.USB_STORAGE_ROOT_PATH + File.separator + Config.USB_STORAGE_EVENT_FILE_NAME, thirdItem);
                        }
                        break;
                        case SYSTEM_NAME: {
                            SPUtils.putString(context, Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.SYSTEM_FILE_NAME, thirdItem);
                        }
                        default:
                            break;
                    }
                    ((MyApplication) context.getApplicationContext()).initFilePath();
                }
                break;
                case SHOW_MEG_BOX: {
                    Log.e(TAG, "executeShellCommand: SHOW_MEG_BOX ");
                    if (sets.length < 3) {
                        return;
                    }
                    String thirdItem = "";
                    for (int i = 2; i < sets.length; i++) {
                        thirdItem += sets[i].replaceAll(" ", "").replaceAll(";", "") + " ";
                    }
                    // thirdItem = thirdItem.replaceAll(";", "");
                    Toast.makeText(context, thirdItem, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
