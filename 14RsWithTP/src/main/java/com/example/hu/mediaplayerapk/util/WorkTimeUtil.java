package com.example.hu.mediaplayerapk.util;

import android.os.Environment;

import com.example.hu.mediaplayerapk.bean.WorkTimer;
import com.example.hu.mediaplayerapk.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31.
 */

public class WorkTimeUtil {

    //判断是否是工作时间如果是，则返回true
    public static boolean checkIsWorkTime() {

        ArrayList<Integer> holidays;
        ArrayList<WorkTimer> workTimers;
        holidays = getHolidays();
        workTimers = (ArrayList<WorkTimer>) getWorkTimers();
        if (holidays == null) {
            holidays = new ArrayList<>();
        }
        if (workTimers == null)
            workTimers = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        long nowTime = c.getTimeInMillis();
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        switch (weekday) {
            case 1:
                weekday = 7;
                break;
            case 2:
                weekday = 1;
                break;
            case 3:
                weekday = 2;
                break;
            case 4:
                weekday = 3;
                break;
            case 5:
                weekday = 4;
                break;
            case 6:
                weekday = 5;
                break;
            case 7:
                weekday = 6;
                break;
        }

        for (Integer holiday : holidays) {
            if (weekday == holiday) {
                return false;
            }
        }

        if (workTimers.size() == 0) {
            return true;
        }

        for (WorkTimer item : workTimers) {
            if (item.getDay() == weekday || item.getDay() == 0) {
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTimeInMillis(System.currentTimeMillis());
                startCalendar.set(Calendar.HOUR_OF_DAY, item.getStartHour());
                startCalendar.set(Calendar.MINUTE, item.getStartMinute());
                startCalendar.set(Calendar.SECOND, 0);
                long startTime = startCalendar.getTimeInMillis();

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTimeInMillis(System.currentTimeMillis());
                endCalendar.set(Calendar.HOUR_OF_DAY, item.getEndHour());
                endCalendar.set(Calendar.MINUTE, item.getEndMinute());
                endCalendar.set(Calendar.SECOND, 0);
                long endTime = endCalendar.getTimeInMillis();

                if (nowTime <= endTime && nowTime >= startTime) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<WorkTimer> getWorkTimers() {
        String json = FileUtils.readTextLine(Environment.getExternalStorageDirectory() + File.separator + Config.WORK_TIMER_FILE_PATH);
        // return JsonUtils.jsonToList(json, WorkTimer.class);
        return JsonUtils.jsonToList(json, WorkTimer.class);
    }

    public static ArrayList<Integer> getHolidays() {
        ArrayList<Integer> list = new ArrayList<>();
        String content = FileUtils.readTextLine(Environment.getExternalStorageDirectory() + File.separator + Config.HOLIDAY_FILE_PATH);
        if (content.equals("")) {
            return null;
        }
        String[] holidays = content.split(",");
        for (String item : holidays) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }
}
