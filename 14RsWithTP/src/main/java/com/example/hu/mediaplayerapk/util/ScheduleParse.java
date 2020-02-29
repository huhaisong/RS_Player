package com.example.hu.mediaplayerapk.util;

import android.util.Log;

import com.example.hu.mediaplayerapk.bean.BeaconBean;
import com.example.hu.mediaplayerapk.bean.BeaconTag;
import com.example.hu.mediaplayerapk.bean.ScheduleBean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ScheduleParse {

    private static final String PROGRAMNO = "ProgramNo";
    private static final String BEGINDATE = "BeginDate";
    private static final String ENDDATE = "EndDate";
    private static final String STARTTIME = "StartTime";
    private static final String ENDTIME = "EndTime";
    private static final String MEDIAFILESTART = "MediaFileStart";
    private static final String MEDIAFILEEND = "MediaFileEnd";
    private static final String TAG = "ScheduleParse";
    private static final String BEACONNO = "BeaconNo";
    private static final String BEACONADDR = "Address";
    private static final String BEACONTYPE = "Type";
    private static final String PROGRAMSTART = "ProgramStart";
    private static final String PROGRAMEND = "ProgramEnd";

    public static void readForAll(String path) {
        File file = new File(path);
        try {
            SortedMap all = null;
            all = Charset.availableCharsets();
            Iterator iter = null;
            iter = all.entrySet().iterator();
            while (iter.hasNext()) {
                String text = "";
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream in = new BufferedInputStream(fis);
                Map.Entry me = (Map.Entry) iter.next();
                //System.out.println(me.getKey());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, me.getKey().toString()));
                String str = reader.readLine();
                while (str != null) {
                    text = text + str + "/n";
                    str = reader.readLine();
                }
                Log.e(TAG, "readForAll: " + me.getKey().toString() + ":=" + text);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readTXT(String path) {
        File file = new File(path);
        BufferedReader reader;
        String text = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fis);
            in.mark(4);
            byte[] first3bytes = new byte[3];
            in.read(first3bytes);//找到文档的前三个字节并自动判断文档类型。
            in.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB && first3bytes[2] == (byte) 0xBF) {// utf-8
//                Log.e(TAG, "readImpactvTXT: utf-8");
                reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
//                Log.e(TAG, "readImpactvTXT: unicode");
                reader = new BufferedReader(new InputStreamReader(in, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
//                Log.e(TAG, "readImpactvTXT: utf-16be");
                reader = new BufferedReader(new InputStreamReader(in, "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
//                Log.e(TAG, "readImpactvTXT: utf-16le");
                reader = new BufferedReader(new InputStreamReader(in, "utf-16le"));
            } else if (first3bytes[0] == 91 && first3bytes[1] == 80 && first3bytes[2] == 114) {
//                Log.e(TAG, "readImpactvTXT: Shift_JIS");
                reader = new BufferedReader(new InputStreamReader(in, "Shift_JIS"));
            } else {
//                Log.e(TAG, "readImpactvTXT: GBK");
                reader = new BufferedReader(new InputStreamReader(in, "GBK"));
            }
            String str = reader.readLine();
            while (str != null) {
                text = text + str + "/n";
                str = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static ArrayList<ScheduleBean> parseImpactvTXT(String path) {
        return parseScheduleItems(readTXT(path));
    }

    private static ArrayList<ScheduleBean> parseScheduleItems(String content) {
        ScheduleBean scheduleBean = new ScheduleBean();
        ArrayList<ScheduleBean> scheduleBeanArrayList = new ArrayList<>();
        String[] strings = content.split("\\[");
        for (int i = 0; i < strings.length; i++) {
            String[] stringsItem = strings[i].split("\\]");
            if (stringsItem.length >= 2) {
                String tempString;
                switch (stringsItem[0]) {
                    case PROGRAMNO:
                        tempString = stringsItem[1].replaceAll("/n", "");
                        scheduleBean.setNom(Integer.valueOf(tempString));
                        break;
                    case BEGINDATE:
                        tempString = stringsItem[1].replaceAll("/n", "");
                        scheduleBean.setBeginData(tempString);
                        break;
                    case ENDDATE:
                        tempString = stringsItem[1].replaceAll("/n", "");
                        scheduleBean.setEndData(tempString);
                        break;
                    case STARTTIME:
                        tempString = stringsItem[1].replaceAll("/n", "");
                        scheduleBean.setBeginTime(tempString);
                        break;
                    case ENDTIME:
                        tempString = stringsItem[1].replaceAll("/n", "");
                        scheduleBean.setEndTime(tempString);
                        break;
                    case MEDIAFILESTART:
                        scheduleBean.setFileTitles(stringsItem[1]);
                        break;
                    case MEDIAFILEEND:
                        scheduleBeanArrayList.add(scheduleBean);
                        scheduleBean = new ScheduleBean();
                        break;
                }
            }
        }
        return scheduleBeanArrayList;
    }

    public static BeaconTag parse_BEACON_NO_TXT(String path) {
        String string = readTXT(path);
        String[] strings = string.split("/n");
        String serialNumber = getSerialNumber();
        Log.e(TAG, "parse_BEACON_NO_TXT: serialNumber = " + serialNumber);



        for (int i = 0; i < strings.length; i++) {
            String[] stringsItem = strings[i].split(",");
            if (stringsItem.length >= 2) {
                String tempString;
                Log.e(TAG, "parse_BEACON_NO_TXT: stringsItem[0] = " + stringsItem[0]);
                Log.e(TAG, "parse_BEACON_NO_TXT: serialNumber.equals(stringsItem[0])= " + serialNumber.equals(stringsItem[0]));
                if (serialNumber.equals(stringsItem[0])) {
                    tempString = stringsItem[1].replaceAll("/n", "");
                    tempString = tempString.replaceAll("-", ":");
                    BeaconTag beaconTag = new BeaconTag();
                    beaconTag.setBeaconAddr(tempString);
                    return beaconTag;
                }
            }
        }
        return null;
    }

    public static ArrayList<BeaconBean> parse_BEACON_Schedule_TXT(String path) {
        ArrayList<BeaconBean> beaconBeans = new ArrayList<>();
        String string = readTXT(path);
        String[] strings = string.split("\\[BeaconNo\\]");
        for (int i = 0; i < strings.length; i++) {
            String[] stringsItem = strings[i].split("\\[ProgramStart\\]");
            if (stringsItem.length >= 2) {
                String tempString = stringsItem[0].replaceAll("/n", "");
                BeaconBean beaconBean = new BeaconBean();
                beaconBean.setBeaconNo(Integer.valueOf(tempString));
                tempString = stringsItem[1].replaceAll("\\[ProgramEnd\\]", "");
                beaconBean.setScheduleBeans(parseScheduleItems(tempString));
                beaconBeans.add(beaconBean);
            }
        }
        Log.e(TAG, "parse_BEACON_Schedule_TXT: " + beaconBeans.size());
        for (BeaconBean item : beaconBeans) {
            Log.e(TAG, "parse_BEACON_Schedule_TXT: " + item.toString());
        }
        return beaconBeans;
    }


    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }
}
