package com.example.hu.mediaplayerapk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.hu.mediaplayerapk.R;

/**
 * Created by Administrator on 2017/4/15.
 */

public class TimePickDialog extends Dialog {


    private Context mContext;
    private int hour;
    private int minute;
    private TimeChangeListerInterface timeChangeLister;
    private ButtonListenerInterface buttonListener;

    public TimePickDialog(@NonNull Context context, int hour, int minute, TimeChangeListerInterface
            timeChangeLister, ButtonListenerInterface buttonListener) {
        super(context);
        this.hour = hour;
        this.minute = minute;
        this.timeChangeLister = timeChangeLister;
        this.buttonListener = buttonListener;
        this.mContext = context;
    }

    public interface ButtonListenerInterface {
        public void onClick();
    }

    public interface TimeChangeListerInterface {
        public void onTImeChanged(int hourofDay, int minute);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  View outerView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view_dialog, null);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.time_pick_pop, null);
         requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title
          setCancelable(true);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (timeChangeLister != null)
                    timeChangeLister.onTImeChanged(hourOfDay, minute);
            }
        });

        Button button = (Button) view.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener.onClick();
                dismiss();
            }
        });

        setContentView(view);
    }
}
