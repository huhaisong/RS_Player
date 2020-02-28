package com.example.hu.mediaplayerapk.config;

/**
 * Created by Administrator on 2016/11/23.
 */

public class Config {

    //人感
    public static final String CHECK_FACE_STATE = "check_face_state";
    //eco
    public static final String ECO_MODE_STATE = "eco_mode_state";

    public static final String BEACON_MODE_STATE = "eco_mode_state";  //  -1 代表开启，1代表关闭

    public static final String IMAGE_DIRECTION = "image_direction";
    public static final String IMAGE_TIME = "image_time";
    public static final String IMAGE_BGM_IMPACTV = "image_bgm_impactv";
    public static final String IMAGE_BGM_EVENT = "image_bgm_event";

    //图片动画方向
    public static final int IMAGE_DIRECTION_NORMAL = 0;
    public static final int IMAGE_DIRECTION_RANDOM = 1;
    public static final int IMAGE_DIRECTION_UPTODOWN = 2;
    public static final int IMAGE_DIRECTION_LEFTTORIGHT = 3;
    public static final int IMAGE_DIRECTION_HORIZONTALCROSS = 4;

    //背景音乐开启状态
    public static final int IMAGE_BGM_ON = 0;
    public static final int IMAGE_BGM_OFF = 1;

    //播放模式
    public static final int PLAY_BACK_MODE_ONE_FILE = 0;
    public static final int PLAY_BACK_MODE_ALL_FILE = 1;
    public static final int PLAY_BACK_MODE_MIX_PROGRAM = 2;
    public static final int PLAY_BACK_MODE_SCHEDULE = 3;

    public static final String INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE = "internal_impactv_play_back_mode_one_file_title";
    public static final String EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE = "external_impactv_play_back_mode_one_file_title";
    public static final String INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE = "internal_event_play_back_mode_one_file_title";
    public static final String EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE = "external_event_play_back_mode_one_file_title";
    public static final String INTERNAL_PLAY_BACK_MODE_IMPACTV = "internal_play_back_mode_impactv";
    public static final String EXTERNAL_PLAY_BACK_MODE_IMPACTV = "external_play_back_mode_impactv";
    public static final String INTERNAL_PLAY_BACK_MODE_EVENT = "internal_play_back_mode_event";
    public static final String EXTERNAL_PLAY_BACK_MODE_EVENT = "external_play_back_mode_event";

    public static final String WORK_TIMER_FILE_PATH = "worktimer.txt";
    public static final String HOLIDAY_FILE_PATH = "holiday.txt";

    public static final String DISPLAY_RATIO = "display_ratio";
    public static final String LANGUAGE = "language";

    public static final String RESET_HOUR = "reset_hour";
    public static final String RESET_ON = "reset_on";

    //文件名称
    public static final String IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME = "impactv_mix_program_list.txt";
    public static final String EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME = "event_mix_program_list.txt";
    public static final String IMPACTV_BGM_FILE_LIST_FILE_NAME = "impactv_bgm_list.txt";
    public static final String EVENT_BGM_FILE_LIST_FILE_NAME = "event_bgm_list.txt";
    public static final String SCHEDULE_FILE_NAME = "ImpacTV.txt";
    public static final String BEACON_DEVICE_FILE_NAME = "beaconList.txt";
    public static final String BEACON_SCHEDULE_FILE_NAME = "beaconSchedule.txt";
    public static final String USB_STORAGE_IMPACTTV_FILE_NAME_INVARIANT = "impacttv14";
    public static final String USB_STORAGE_IMPACTV_FILE_NAME_INVARIANT = "impactv14";
    public static final String USB_STORAGE_EVENT_FILE_NAME_INVARIANT = "event14";
    public static final String USB_STORAGE_BEACON_EVENT_FILE_NAME_INVARIANT = "beacon14";
    public static final String USB_STORAGE_SYSTEM_FILE_NAME_INVARIANT = "system14";
    public static String USB_STORAGE_IMPACTTV_FILE_NAME = USB_STORAGE_IMPACTTV_FILE_NAME_INVARIANT;
    public static String USB_STORAGE_IMPACTV_FILE_NAME = USB_STORAGE_IMPACTV_FILE_NAME_INVARIANT;
    public static String USB_STORAGE_EVENT_FILE_NAME = USB_STORAGE_EVENT_FILE_NAME_INVARIANT;
    public static String USB_STORAGE_BEACON_EVENT_FILE_NAME = USB_STORAGE_BEACON_EVENT_FILE_NAME_INVARIANT;
    public static String USB_STORAGE_SYSTEM_FILE_NAME = USB_STORAGE_SYSTEM_FILE_NAME_INVARIANT;
    public static final String USB_STORAGE_PATH = "/mnt/usb_storage/USB_DISK0";
    public static String USB_STORAGE_ROOT_PATH = USB_STORAGE_PATH;

    //文件路径
    public static final String EXTERNAL_FILE_PATH = "/mnt/external_sd";
    public static final String INTERNAL_FILE_PATH = "/mnt/internal_sd";
    public static String EXTERNAL_FILE_ROOT_PATH = EXTERNAL_FILE_PATH;
    public static String INTERNAL_FILE_ROOT_PATH = INTERNAL_FILE_PATH;
    public static final String IMPACTV_FILE_NAME = "impactv";
    public static final String IMPACTTV_FILE_NAME = "impacttv";
    public static final String WASHING_FILE_NAME = "WASHING";
    public static final String WARNING_FILE_NAME = "WARNING";
    public static final String EVENT_FILE_NAME = "Event";
    public static final String BEACON_FILE_NAME = "Beacons";
    public static final String SYSTEM_FILE_NAME = "System";

    public static final String APK_PACKAGE_NAME = "com.example.hu.mediaplayerapk";

    //蓝牙传入
    public static final int BEACON_TAG_NO_PERSION = 4;  //4代表没人
    public static final int BEACON_TAG_PERSION = 0;  //0代表有人

    public static final String PICKTURE_OK_FOLDER = "OUTPUT_OK";
    public static final String PICKTURE_NG_FOLDER = "OUTPUT_NG";
    public static final String PICKTURE_TEMP_FOLDER = "OUTPUT_TEMP";
}
