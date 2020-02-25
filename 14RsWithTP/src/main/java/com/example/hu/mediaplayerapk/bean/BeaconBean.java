package com.example.hu.mediaplayerapk.bean;

import java.util.ArrayList;

public class BeaconBean {

    private int beaconNo;
    private ArrayList<ScheduleBean> mScheduleBeans;

    public int getBeaconNo() {
        return beaconNo;
    }

    public void setBeaconNo(int beaconNo) {
        this.beaconNo = beaconNo;
    }

    public ArrayList<ScheduleBean> getScheduleBeans() {
        return mScheduleBeans;
    }

    public void setScheduleBeans(ArrayList<ScheduleBean> mScheduleBeans) {
        this.mScheduleBeans = mScheduleBeans;
    }

    @Override
    public String toString() {
        String mScheduleBeansStrings = "";
        for (ScheduleBean item : mScheduleBeans) {
            mScheduleBeansStrings += item.toString();
        }
        return "BeaconBean{" +
                "beaconNo=" + beaconNo +
                ", mScheduleBeans='{" +
                mScheduleBeansStrings +
                "}}";
    }
}
