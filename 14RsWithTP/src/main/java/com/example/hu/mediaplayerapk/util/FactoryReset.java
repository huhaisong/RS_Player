package com.example.hu.mediaplayerapk.util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.provider.Settings;

import com.example.hu.mediaplayerapk.config.Config;

import java.io.File;

import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_system_path;

/**
 * Created by Administrator on 2017/3/30.
 */

public class FactoryReset {

    public static void factoryResetSetting(Context mContext) {
        //背光
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 6 * (255 / 7));
        //音量
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7 * maxVolume / 15, 0);
        //workTimer
        FileUtils.saveTxtFile(Environment.getExternalStorageDirectory() + File.separator + Config.WORK_TIMER_FILE_PATH, "");
        //holidays
        FileUtils.saveTxtFile(Environment.getExternalStorageDirectory() + File.separator + Config.HOLIDAY_FILE_PATH, "");
        //displayRatio
        SPUtils.putString(mContext, Config.DISPLAY_RATIO, "uniform");
        //language
        SPUtils.putString(mContext, Config.LANGUAGE, "chinese");
        //reboot setting
        SPUtils.putInt(mContext, Config.RESET_HOUR, 3);
        SPUtils.putInt(mContext, Config.RESET_ON, 1);
        //playback mode
        SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
        //one file repeat
        SPUtils.putString(mContext, Config.INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        SPUtils.putString(mContext, Config.EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        //program
        FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        //slide timer
        SPUtils.putInt(mContext, Config.IMAGE_TIME, 5);
        //slide pattern
        SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_NORMAL);
        //bgm
        SPUtils.putInt(mContext, Config.IMAGE_BGM_IMPACTV, Config.IMAGE_BGM_OFF);
        FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, "");
        //delete apk in system
        FileUtils.deleteDirectory(internal_system_path);
        //motionDetection
        SPUtils.putInt(mContext, Config.CHECK_FACE_STATE, 0);
        SPUtils.putInt(mContext, Config.ECO_MODE_STATE, -1);
        //EVENT
        SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putString(mContext, Config.INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        SPUtils.putString(mContext, Config.EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        SPUtils.putInt(mContext, Config.IMAGE_BGM_EVENT, Config.IMAGE_BGM_OFF);
        FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, "");

        //设置是否拍照
        SPUtils.putInt(mContext, Config.SAVE_IMAGE_STATE, 1);
    }
}
