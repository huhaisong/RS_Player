package com.example.hu.mediaplayerapk.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.hu.mediaplayerapk.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;

/**
 * Created by Administrator on 2017/4/18.
 */

public class PickerUtil {

    public static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    public static void resizePikcer(FrameLayout tp, Context context) {

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        if (screenWidth < 1500 && screenWidth > 900) {
            return;
        }

        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np, context);
        }
    }

    public static void resizeNumberPicker(NumberPicker np, Context context) {



        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        if (screenWidth < 1500 && screenWidth > 900) {
            return;
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90 * (int) screenWidthRatio, 300 * (int) screenHeightRatio);
        params.setMargins(20 * (int) screenWidthRatio, 0, 20 * (int) screenHeightRatio, 0);
        np.setLayoutParams(params);

        View child = null;
        if (null != np) {
            for (int i = 0; i < np.getChildCount(); i++) {
                child = np.getChildAt(i);
                if (child instanceof EditText) {
                    ((EditText) child).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.ts30));
                } else if (child instanceof TextView) {
                    ((TextView) child).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.ts30));
                } else if (child instanceof Button) {
                    ((Button) child).setTextSize(context.getResources().getDimensionPixelSize(R.dimen.ts30));
                }
            }
        }
    }
}
