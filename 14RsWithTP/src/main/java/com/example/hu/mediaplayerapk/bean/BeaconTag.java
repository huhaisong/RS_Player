package com.example.hu.mediaplayerapk.bean;

/**
 * Created by Administrator on 2018-12-01.
 */

public class BeaconTag {

    public static final int BEACON_MAGNET = 0;
    public static final int BEACON_GSENSOR = 1;
    public static final int BEACON_IRSENSOR = 2;

    private int beaconNo;
    private String BeaconAddr;  //EA:0B:DD:11:22:33, 以:区分，全部大写
    private String BeaconType;     //0: magnet; 1: gsensor; 2: IR sensor
    private int BeaconData;     //不同类型的beacon,该变量还有不同的含义

    public void setBeaconAddr(String addr) {
        addr.replace("-", ":");
        addr.toUpperCase();//全部大写
        this.BeaconAddr = addr;
    }

    public void setBeaconData(int data) {
        this.BeaconData = data;
    }

    public String getBeaconAddr() {
        return this.BeaconAddr;
    }

    //默认返回0；
    public int getBeaconType() {
        int beaconType = 0;
        switch (BeaconType) {
            case "magnet":
                beaconType = BEACON_MAGNET;
                break;
            case "gsensor":
                beaconType = BEACON_GSENSOR;
                break;
            case "sensor":
            case "IR sensing":
            case "IR":
                beaconType = BEACON_IRSENSOR;
                break;
        }
        return beaconType;
    }

    public void setBeaconType(String beaconType) {
        BeaconType = beaconType;
    }

    public int getBeaconData() {
        return this.BeaconData;
    }

    public int getBeaconNo() {
        return beaconNo;
    }

    public void setBeaconNo(int beaconNo) {
        this.beaconNo = beaconNo;
    }

    @Override
    public String toString() {
        return "BeaconTag{" +
                "beaconNo=" + beaconNo +
                ", BeaconAddr='" + BeaconAddr + '\'' +
                ", BeaconType='" + BeaconType + '\'' +
                ", BeaconData=" + BeaconData +
                '}';
    }
}
