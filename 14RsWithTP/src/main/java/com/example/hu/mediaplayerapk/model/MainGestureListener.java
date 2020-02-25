package com.example.hu.mediaplayerapk.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.ui.popupWindow.VolumeAndLightPop;

/**
 * Created by Administrator on 2017/4/10.
 * 播放广告界面手势监听类
 * 功能：控制背光以及进入osd
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Context mContext;
    private VolumeAndLightPop volumeAndLightPop;
    int windowHeight;
    int windowWidth;

    public MainGestureListener(Context mContext, VolumeAndLightPop volumeAndLightPop) {
        this.mContext = mContext;
        this.volumeAndLightPop = volumeAndLightPop;
        Display disp = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        windowHeight = disp.getHeight();
        windowWidth = disp.getWidth();
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    private static final String TAG = "MainGestureListener";

    @Override
    public void onLongPress(MotionEvent e) {

        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        if (x < (windowWidth * 4) / 5 && x > windowWidth / 5 && y < windowHeight * 4 / 5 && y > windowHeight / 5) {
//            ((Activity) mContext).finish();
            Intent intent = new Intent(mContext, OSDSettingActivity.class);
            mContext.startActivity(intent);
        }
        super.onLongPress(e);
    }

    /**
     * 滑动
     */
    private boolean isSingleTouch = true;
    private float oldX;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        float mOldY = e1.getY();
        float mOldX = e1.getX();
        if (volumeAndLightPop.isShow) {
            if (oldX != mOldX) {
                isSingleTouch = false;
                oldX = mOldX;
            }
        } else {
            oldX = mOldX;
        }
        int y = (int) e2.getRawY();
        int x = (int) e2.getRawX();
        float percent = (mOldY - y) / (windowHeight);
        if (mOldX < windowWidth / 3) {
            onBrightnessSlide(percent);
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    float mBrightness = -1;

    private void onBrightnessSlide(float percent) {
        /*if (!volumeAndLightPop.isShow || !isSingleTouch) {
            isSingleTouch = true;
            mBrightness = volumeAndLightPop.getSystemBrightness();
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;
        }
        volumeAndLightPop.showVolumeAndBrightnessPop();
        // 显示
        int current = (int) ((percent + mBrightness / 255) * 7);
        Log.e(TAG, "onBrightnessSlide: current = " + current + ",mBrightness/255 = " + mBrightness / 255);
        volumeAndLightPop.setBrightness(current);*///禁止滑动调节背光
    }
}
