package com.example.hu.mediaplayerapk.bean;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ScheduleBean {

    private int Nom;
    private String beginDate;
    private String endDate;
    private String beginTime;
    private String endTime;
    private String fileTitles;

    public void setNom(int nom) {
        Nom = nom;
    }

    public void setBeginData(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndData(String endDate) {
        this.endDate = endDate;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setFileTitles(String fileTitles) {
        this.fileTitles = fileTitles;
    }

    public int getNum() {
        return Nom;
    }

    public int getBeginDateYear() {
        return Integer.valueOf(beginDate) / 10000;
    }

    public int getBeginDateMonth() {
        return (Integer.valueOf(beginDate) / 100) % 100;
    }

    public int getBeginDateDay() {
        return Integer.valueOf(beginDate) % 100;
    }

    public int getEndDateYear() {
        return Integer.valueOf(endDate) / 10000;
    }

    public int getEndDateMonth() {
        return (Integer.valueOf(endDate) / 100) % 100;
    }

    public int getEndDateDay() {
        return Integer.valueOf(endDate) % 100;
    }

    public int getBeginTimeHour() {
        return Integer.valueOf(beginTime) / 10000;
    }

    public int getBeginTimeMinute() {
        return (Integer.valueOf(beginTime) / 100) % 100;
    }

    public int getBeginTimeSecond() {
        return Integer.valueOf(beginTime) % 100;
    }

    public int getEndTimeHour() {
        return Integer.valueOf(endTime) / 10000;
    }

    public int getEndTimeMinute() {
        return (Integer.valueOf(endTime) / 100) % 100;
    }

    public int getEndTimeSecond() {
        return Integer.valueOf(endTime) % 100;
    }

    private static final String TAG = "ScheduleBean";

    public ArrayList<String> getFileTitles() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (fileTitles != null) {
            String[] strings = fileTitles.split("/n");
            Collections.addAll(arrayList, strings);
            arrayList.remove("");
            arrayList.remove("/n");
        }
        return arrayList;
    }

    @Override
    public String toString() {
        return "ScheduleBean{" +
                "Nom=" + Nom +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", fileTitles='" + getFileTitles().toString() + '\'' +
                '}' + "\n";
    }
}