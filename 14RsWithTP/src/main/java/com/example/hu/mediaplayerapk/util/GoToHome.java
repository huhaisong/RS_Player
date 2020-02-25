package com.example.hu.mediaplayerapk.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2017/3/31.
 */

public class GoToHome {

    public static int secretCode = -1;

    public static void goToHome(int keyCode, KeyEvent event, Context context) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                Log.e("aaa1", "getRepeatCount:" + event.getRepeatCount());
                if (event.getRepeatCount() > 20) {
                    Log.e("1aaa", "-----------------");
                    secretCode = 0;
                } else {
                    secretCode = -1;
                }
            }
            break;
            case KeyEvent.KEYCODE_DPAD_UP: {
                if (secretCode == 0) {
                    secretCode = 1;
                } else {
                    secretCode = -1;
                }
            }
            break;
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (secretCode == 1) {
                    secretCode = 2;
                } else {
                    secretCode = -1;
                }
            }
            break;
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (secretCode == 2) {
                    secretCode = 3;
                } else {
                    secretCode = -1;
                }
            }
            break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if (secretCode == 3) {
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.HOME");
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                secretCode = -1;
            }
            break;
        }
    }

}
