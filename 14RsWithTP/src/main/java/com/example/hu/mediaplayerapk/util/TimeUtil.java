package com.example.hu.mediaplayerapk.util;

import android.os.SystemClock;
import android.util.Log;

import com.example.hu.mediaplayerapk.bean.ScheduleBean;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/4.
 */

public class TimeUtil {

    private static final String TAG = "TimeUtil";

    /*public static void setDate(int year, int month, int day) throws IOException, InterruptedException {

        requestPermission();

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);

        if (now - when > 1000)
            throw new IOException("failed to set Date.");
    }

    public static void setTime(int hour, int minute, int second) throws IOException, InterruptedException {

        requestPermission();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);
        if (now - when > 1000)
            throw new IOException("failed to set Time.");
    }*/

    public static void setDate(int year, int month, int day) throws IOException, InterruptedException {
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        String datetime = "";
        datetime += year;
        if (month < 10) {
            datetime = datetime + "0" + month;
        } else {
            datetime += month;
        }
        if (day < 10) {
            datetime = datetime + "0" + day;
        } else {
            datetime += day;
        }
        datetime += ".";
        if (hour < 10) {
            datetime = datetime + "0" + hour;
        } else {
            datetime += hour;
        }
        if (minute < 10) {
            datetime = datetime + "0" + minute;
        } else {
            datetime += minute;
        }
        if (second < 10) {
            datetime = datetime + "0" + second;
        } else {
            datetime += second;
        }
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e(TAG, "setTime: 设置成功");
        } catch (IOException e) {
            Log.e(TAG, "setTime: 设置失败");
            e.printStackTrace();
        }
    }

    public static void setTime(int hour, int minute, int second) throws IOException, InterruptedException {

        String datetime = "";
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datetime += year;
        month += 1;
        if (month < 10) {
            datetime = datetime + "0" + month;
        } else {
            datetime += month;
        }
        if (day < 10) {
            datetime = datetime + "0" + day;
        } else {
            datetime += day;
        }
        datetime += ".";
        if (hour < 10) {
            datetime = datetime + "0" + hour;
        } else {
            datetime += hour;
        }
        if (minute < 10) {
            datetime = datetime + "0" + minute;
        } else {
            datetime += minute;
        }
        if (second < 10) {
            datetime = datetime + "0" + second;
        } else {
            datetime += second;
        }
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e(TAG, "setTime: 设置成功");
        } catch (IOException e) {
            Log.e(TAG, "setTime: 设置失败");
            e.printStackTrace();
        }
    }


    static void requestPermission() throws InterruptedException, IOException {
        createSuProcess("chmod 666 /dev/alarm").waitFor();
    }

    static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if (rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    static Process createSuProcess(String cmd) throws IOException {
        DataOutputStream os = null;
        Process process = createSuProcess();
        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
        return process;
    }

    public static String getCurrentFormatTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getCurrentFormatDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getCurrentFormatTimeOnly(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static int[] getDate() {
        int[] date = {0, 0, 0};
        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        date[0] = mCalendar.get(Calendar.YEAR);
        date[1] = mCalendar.get(Calendar.MONTH);
        date[2] = mCalendar.get(Calendar.DAY_OF_MONTH);
        return date;
    }

    public static int[] getTime() {
        int[] date = {0, 0, 0};
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        date[0] = mCalendar.get(Calendar.HOUR_OF_DAY);
        date[1] = mCalendar.get(Calendar.MINUTE);
        date[2] = mCalendar.get(Calendar.SECOND);
        return date;
    }

    public static void showTime(long time, String str) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int year, month, day, hour, minute, second;
        year = mCalendar.get(Calendar.YEAR);
        month = mCalendar.get(Calendar.MONTH);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        minute = mCalendar.get(Calendar.MINUTE);
        second = mCalendar.get(Calendar.SECOND);
        Log.e(TAG, str + ":" + "year:" + year + ",month:" + month + ",day:" + day
                + ",hour:" + hour + ",minute:" + minute + ",second:" + second);
    }

    /**
     * @param scheduleBean 传入的schedule；
     * @return long[0] 为开始的时间  long[1] 位结束的时间；
     */
    public static long[] getDayFromSchedule(ScheduleBean scheduleBean) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.DAY_OF_MONTH, scheduleBean.getBeginDateDay());
        beginCalendar.set(Calendar.MONTH, scheduleBean.getBeginDateMonth() - 1);
        beginCalendar.set(Calendar.YEAR, scheduleBean.getBeginDateYear());
        Calendar endCalendar = Calendar.getInstance();

        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.DAY_OF_MONTH, scheduleBean.getEndDateDay());
        endCalendar.set(Calendar.MONTH, scheduleBean.getEndDateMonth() - 1);
        endCalendar.set(Calendar.YEAR, scheduleBean.getEndDateYear());
        endCalendar.add(Calendar.DATE, 1);
        return new long[]{beginCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()};
    }


    /**
     * @param scheduleBean 传入的schedule；
     * @return long[0] 为开始的时间  long[1] 位结束的时间；
     */
    public static long[] getTimeFromSchedule(ScheduleBean scheduleBean) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTimeInMillis(System.currentTimeMillis());
        beginCalendar.set(Calendar.HOUR_OF_DAY, scheduleBean.getBeginTimeHour());//有点奇怪
        beginCalendar.set(Calendar.MINUTE, scheduleBean.getBeginTimeMinute());
        beginCalendar.set(Calendar.SECOND, scheduleBean.getBeginTimeSecond());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, scheduleBean.getEndTimeHour());//有点奇怪
        endCalendar.set(Calendar.MINUTE, scheduleBean.getEndTimeMinute());
        endCalendar.set(Calendar.SECOND, scheduleBean.getEndTimeSecond());
        return new long[]{beginCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()};
    }


    public static long getStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
