package com.example.hu.mediaplayerapk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hu.mediaplayerapk.ui.activity.MainActivity;


//import com.example.hu.s6_test2.ramandrom.MixInformationPresenter;

/**
 * Created by Administrator on 2017/1/11.
 */

public class HumanReceive extends BroadcastReceiver {
    //定义两个自定义的消息
    public static final String MSG = "com.android.humankey";
    private HRInterface cbk;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.android.humankey")) {
            System.out.println(MSG);
            Log.e("HUMANKEY", "com.android.humankey");
            //mixInformationPresenter.setFaceCheckCorrect(4000);
            cbk.humanSensorCbk();
        }
    }

    public interface HRInterface {
        public void humanSensorCbk();
    }

    public void setHumanSensorListener(HRInterface brInteraction) {
        this.cbk = brInteraction;
    }
}
