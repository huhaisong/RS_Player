package com.rockchip;

import android.util.Log;

/**
 * Created by Administrator on 2016/12/28.
 */


public class Gpio {


    static {
//        System.loadLibrary("rockgpio-jni");
    }

    public static native int getgpionumber(String gpionumber);

    public static native int openGpioDev();

    public static native int closeGpioDev(int fd);

    public static native int requestGpio(int fd, int num);

    public static native int releaseGpio(int fd, int num);

    public static native int setGpioState(int fd, int num, int state);

    public static native int getGpioState(int fd, int num);

    public static native int setGpioInput(int fd, int num);

    public static native int setGreenGpio(int fd, int status);

    public static native int setRedGpio(int fd, int status);


    static int mfd, err;
    private static final String TAG = "Gpio";

    public static void setLedGreen() {
      /*  mfd = Gpio.openGpioDev();
        if (mfd < 0) {
            Log.e("hello", "open err");
            return;
        }
        err = Gpio.setGreenGpio(mfd, 1);
        if (err < 0)
            Log.e("GPIO", "set gpio input err");
        err = Gpio.setRedGpio(mfd, 0);
        if (err < 0)
            Log.e("GPIO", "set gpio input err");

        err = Gpio.closeGpioDev(mfd);
        if (err < 0) {
            Log.e("GPIO", "set gpio input err");
        }*/
    }

    public static void setLedRed() {
     /*   mfd = Gpio.openGpioDev();
        if (mfd < 0) {
            Log.e("hello", "open err");
            return;
        }
        err = Gpio.setGreenGpio(mfd, 0);
        if (err < 0)
            Log.e("GPIO", "set gpio input err");
        err = Gpio.setRedGpio(mfd, 1);
        if (err < 0)
            Log.e("GPIO", "set gpio input err");
        err = Gpio.closeGpioDev(mfd);
        if (err < 0) {
            Log.e("GPIO", "set gpio input err");
        }*/
    }

	/*
    public static native  int setGpioArrayInput(int fd);

	public static native  int getGpioArrayState(int fd);

	public static native  int releaseArrayGpio(int fd);*/
}
