package com.example.hu.mediaplayerapk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.ui.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/15.
 */

public class WheelDialog extends Dialog {

    private Context mContext;
    private ArrayList<String> stringList;
    private int oldIndex;
    private WheelListenerInterface wheelListener;
    private ButtonListenerInterface buttonListener;
    private static final String TAG = "WheelDialog";

    public WheelDialog(@NonNull Context context, ArrayList<String> stringList, int oldIndex, WheelListenerInterface wheelListener, ButtonListenerInterface buttonListener) {
        super(context);
        this.mContext = context;
        this.stringList = stringList;
        this.oldIndex = oldIndex;
        this.wheelListener = wheelListener;
    }

    public interface WheelListenerInterface {
        public void select(int selectedIndex, String item);
    }

    public interface ButtonListenerInterface {
        public void onClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title

        View outerView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view_pop, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(1);
        wv.setItems(stringList);
        wv.setSeletion(oldIndex);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (wheelListener != null)
                    wheelListener.select(selectedIndex, item);
                Log.d(TAG, "[Dialog]selectedIndex: " + (selectedIndex - 1) + ", item: " + item);
            }
        });
        Button button = (Button) outerView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonListener != null)
                    buttonListener.onClick();
                dismiss();
            }
        });
        setCanceledOnTouchOutside(true);
        setContentView(outerView);
    }
}
