package com.example.hu.mediaplayerapk.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 * 用于界面的手势退出
 */

public class TouchModel {

    private Context mContext;
    private int xTouch;
    private int yTouch;
    private String tapValue;

    public TouchModel(Context mContext) {
        this.mContext = mContext;
        tapValue = "null";
    }

    private void checkSecretTap() {
        Object localObject = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point localPoint = new Point();
        ((Display) localObject).getSize(localPoint);
        int i = localPoint.x / 2;
        int j = localPoint.y / 2;

        if ((this.xTouch < i) && (this.yTouch < j))
            this.tapValue = this.tapValue.concat("1");
        else if ((this.xTouch >= i) && (this.yTouch < j))
            this.tapValue = this.tapValue.concat("2");
        else if ((this.xTouch < i) && (this.yTouch >= j))
            this.tapValue = this.tapValue.concat("3");
        else if ((this.xTouch >= i) && (this.yTouch >= j))
            this.tapValue = this.tapValue.concat("4");

        if ("4321".equals(this.tapValue)) {
            localObject = new Intent("android.intent.action.MAIN");
            ((Intent) localObject).addCategory("android.intent.category.HOME");
            mContext.startActivity((Intent) localObject);
        }
    }


    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        switch (paramMotionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.xTouch = (int) paramMotionEvent.getX();
                this.yTouch = (int) paramMotionEvent.getY();
                return true;
            case 1:
                float f2 = paramMotionEvent.getX();
                if (this.xTouch > 200.0F + f2) {
                    this.tapValue = "";
                    return true;
                }
                checkSecretTap();
                return true;
            default:
                return false;
        }
    }
}
