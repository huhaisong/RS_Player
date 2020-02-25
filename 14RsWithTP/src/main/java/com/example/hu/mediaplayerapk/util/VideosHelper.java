package com.example.hu.mediaplayerapk.util;

import android.util.Log;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.bean.BeaconBean;
import com.example.hu.mediaplayerapk.bean.ScheduleBean;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.model.MainActivityPlayModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.external_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_washing_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_washing_path;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.beaconTagNo;
import static com.example.hu.mediaplayerapk.util.FileUtils.allIsPhoto;
import static com.example.hu.mediaplayerapk.util.FileUtils.getPathFromName;

public class VideosHelper {

    private static final String TAG = "VideosHelper";

    public static List<String> getAllFileList(String path) {
        List<String> list = new ArrayList<>();
        File[] files = FileUtils.getVideoAndPhoto(new File(path));
        if (files != null && files.length > 0) {
            for (File item : files) {
                list.add(item.getAbsolutePath());
            }
        }
        return list;
    }

    public static List<String> getInternalImpactvFileList() {
        ArrayList<String> allVideoFileList = (ArrayList<String>) getAllFileList(internal_impactv_path);
        ArrayList<String> fileList = new ArrayList<>();
        int playModel = SPUtils.getInt(MyApplication.getInstance(), Config.INTERNAL_PLAY_BACK_MODE_IMPACTV);
        if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
            if (FileUtils.isSDCardEnable()) {
                if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_ONE_FILE) {
            String oneFile = SPUtils.getString(MyApplication.getInstance(), Config.INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE);
            for (String item : allVideoFileList) {
                if (item.equals(oneFile)) {
                    fileList.add(item);
                    break;
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
            long nowTime = System.currentTimeMillis();
            for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(internal_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME)) {
                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                    ArrayList<String> arrayList = scheduleBean.getFileTitles();
                    getPathFromName(arrayList, allVideoFileList, fileList);
                    break;
                }
            }
        } else {
            fileList = allVideoFileList;
            fileList = (ArrayList<String>) FileUtils.orderList(fileList);
        }
        if (fileList.size() > 0) {
            if (SPUtils.getInt(MyApplication.getInstance(), Config.IMAGE_BGM_IMPACTV) == Config.IMAGE_BGM_ON) {
                if (allIsPhoto(fileList)) {
                    MainActivityPlayModel.isBGMOn = true;
                }
            }
        }
        return fileList;
    }

    public static List<String> getExternalImpactvFileList() {
        ArrayList<String> allVideoFileList;
        boolean isImpacttv;
        if (FileUtils.checkHaveFile(external_impactv_path)) {
            allVideoFileList = (ArrayList<String>) getAllFileList(external_impactv_path);
            isImpacttv = false;
        } else {
            allVideoFileList = (ArrayList<String>) getAllFileList(external_impacttv_path);
            isImpacttv = true;
        }
        ArrayList<String> fileList = new ArrayList<>();
        int playModel = SPUtils.getInt(MyApplication.getInstance(), Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV);
        if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
            if (FileUtils.isSDCardEnable()) {
                if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_ONE_FILE) {
            String oneFile = SPUtils.getString(MyApplication.getInstance(), Config.EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE);
            for (String item : allVideoFileList) {
                if (item.equals(oneFile)) {
                    fileList.add(item);
                    break;
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
            long nowTime = System.currentTimeMillis();
            String path;
            if (isImpacttv) {
                path = external_impacttv_path + File.separator + Config.SCHEDULE_FILE_NAME;
            } else {
                path = external_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME;
            }
            for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(path)) {
                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                    ArrayList<String> arrayList = scheduleBean.getFileTitles();
                    getPathFromName(arrayList, allVideoFileList, fileList);
                    break;
                }
            }
        } else {
            fileList = allVideoFileList;
            fileList = (ArrayList<String>) FileUtils.orderList(fileList);
        }
        if (fileList.size() > 0) {
            if (SPUtils.getInt(MyApplication.getInstance(), Config.IMAGE_BGM_IMPACTV) == Config.IMAGE_BGM_ON) {
                if (allIsPhoto(fileList)) {
                    MainActivityPlayModel.isBGMOn = true;
                }
            }
        }
        return fileList;
    }

    public static List<String> getInternalEventFileList() {
        ArrayList<String> allVideoFileList = (ArrayList<String>) getAllFileList(internal_event_path);
        ArrayList<String> fileList = new ArrayList<>();
        int playModel = SPUtils.getInt(MyApplication.getInstance(), Config.INTERNAL_PLAY_BACK_MODE_EVENT);
        if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
            if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                String[] strings = FileUtils.readTextLine(
                        internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                if (strings.length > 0) {
                    Collections.addAll(fileList, strings);
                    fileList.remove("");
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_ONE_FILE) {
            String oneFile = SPUtils.getString(MyApplication.getInstance(), Config.INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE);
            for (String item : allVideoFileList) {
                if (item.equals(oneFile)) {
                    fileList.add(item);
                    break;
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
            long nowTime = System.currentTimeMillis();
            for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(internal_event_path + File.separator + Config.SCHEDULE_FILE_NAME)) {
                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                    ArrayList<String> arrayList = scheduleBean.getFileTitles();
                    getPathFromName(arrayList, allVideoFileList, fileList);
                    break;
                }
            }
        } else {
            fileList = allVideoFileList;
            fileList = (ArrayList<String>) FileUtils.orderList(fileList);
        }
        if (fileList.size() > 0) {
            if (SPUtils.getInt(MyApplication.getInstance(), Config.IMAGE_BGM_EVENT) == Config.IMAGE_BGM_ON) {
                if (allIsPhoto(fileList)) {
                    MainActivityPlayModel.isBGMOn = true;
                }
            }
        }
        return fileList;
    }

    public static List<String> getExternalEventFileList() {
        ArrayList<String> allVideoFileList = (ArrayList<String>) getAllFileList(external_event_path);
        ArrayList<String> fileList = new ArrayList<>();
        int playModel = SPUtils.getInt(MyApplication.getInstance(), Config.EXTERNAL_PLAY_BACK_MODE_EVENT);
        if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
            if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                String[] strings = FileUtils.readTextLine(
                        external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                if (strings.length > 0) {
                    Collections.addAll(fileList, strings);
                    fileList.remove("");
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_ONE_FILE) {
            String oneFile = SPUtils.getString(MyApplication.getInstance(), Config.EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE);
            for (String item : allVideoFileList) {
                if (item.equals(oneFile)) {
                    fileList.add(item);
                    break;
                }
            }
        } else if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
            long nowTime = System.currentTimeMillis();
            for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(external_event_path + File.separator + Config.SCHEDULE_FILE_NAME)) {
                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                    ArrayList<String> arrayList = scheduleBean.getFileTitles();
                    getPathFromName(arrayList, allVideoFileList, fileList);
                    break;
                }
            }
        } else {
            fileList = allVideoFileList;
            fileList = (ArrayList<String>) FileUtils.orderList(fileList);
        }
        if (fileList.size() > 0) {
            if (SPUtils.getInt(MyApplication.getInstance(), Config.IMAGE_BGM_EVENT) == Config.IMAGE_BGM_ON) {
                if (allIsPhoto(fileList)) {
                    MainActivityPlayModel.isBGMOn = true;
                }
            }
        }
        return fileList;
    }

    public static List<String> getExternalBeaconEventFileList() {
        ArrayList<String> allVideoFileList = (ArrayList<String>) getAllFileList(external_beacon_path);
        ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(
                MyApplication.external_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME);
        int beaconNum = beaconTagNo;
        BeaconBean beaconBean = null;
        for (BeaconBean item : beaconBeans) {
            if (item.getBeaconNo() == beaconNum)
                beaconBean = item;
        }
        ArrayList<String> fileList = new ArrayList<>();
        long nowTime = System.currentTimeMillis();
        for (ScheduleBean scheduleBean : beaconBean.getScheduleBeans()) {
            long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
            long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
            if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                ArrayList<String> arrayList = scheduleBean.getFileTitles();
                getPathFromName(arrayList, allVideoFileList, fileList);
                break;
            }
        }
        for (String item : fileList) {
            Log.e(TAG, "getExternalBeaconEventFileList: " + item);
        }
        return fileList;
    }

    public static List<String> getInternalBeaconEventFileList() {
        ArrayList<String> allVideoFileList = (ArrayList<String>) getAllFileList(internal_beacon_path);

        ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(
                internal_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME);
        int beaconNum = beaconTagNo;
        BeaconBean beaconBean = null;
        for (BeaconBean item : beaconBeans) {
            if (item.getBeaconNo() == beaconNum)
                beaconBean = item;
        }
        ArrayList<String> fileList = new ArrayList<>();
        long nowTime = System.currentTimeMillis();
        for (ScheduleBean scheduleBean : beaconBean.getScheduleBeans()) {
            long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
            long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
            if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                ArrayList<String> arrayList = scheduleBean.getFileTitles();
                getPathFromName(arrayList, allVideoFileList, fileList);
                break;
            }
        }
        for (String item : fileList) {
            Log.e(TAG, "getExternalBeaconEventFileList: " + item);
        }

        for (String item : allVideoFileList) {
            Log.e(TAG, "getExternalBeaconEventFileList: allVideoFileList" + item);
        }
        return fileList;
    }


    public static List<String> getInternalWarningFileList() {
        return getAllFileList(internal_warning_path);
    }

    public static List<String> getExternalWarningFileList() {
        return getAllFileList(external_warning_path);
    }

    public static List<String> getInternalWashingFileList() {
        return getAllFileList(internal_washing_path);
    }

    public static List<String> getExternalWashingFileList() {
        return getAllFileList(external_washing_path);
    }

    public static List<String> getInternalStandFileList() {
        return getAllFileList(internal_impactv_path);
    }

    public static List<String> getExternalStandFileList() {
        if (FileUtils.checkHaveFile(external_impactv_path)) {
           return getAllFileList(external_impactv_path);
        } else {
          return getAllFileList(external_impacttv_path);
        }
    }
}
