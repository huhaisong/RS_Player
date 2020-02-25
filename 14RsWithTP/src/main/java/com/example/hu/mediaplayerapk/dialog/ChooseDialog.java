package com.example.hu.mediaplayerapk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.hu.mediaplayerapk.R;

/**
 * Created by Administrator on 2016/11/7.
 */
public class ChooseDialog extends Dialog {

    private ClickListenerInterface clickListenerInterface;
    private Context mContext;
    private String content;

    public interface ClickListenerInterface {
        public void select(int i);
    }

    public ChooseDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ChooseDialog(Context context,  String content) {
        super(context);
        this.mContext = context;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(content);
    }

    private void init(String content) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.choose_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title
        setCancelable(false); //设置点击白色不消失
        setContentView(view);

        ClickListen clickListen = new ClickListen();
        if (content != null) {
            ((TextView) view.findViewById(R.id.tv_choose_dialog)).setText(content);
        }
        view.findViewById(R.id.iv_choose_yes).setOnClickListener(clickListen);
        view.findViewById(R.id.iv_choose_no).setOnClickListener(clickListen);
    }

    public void setClickListen(ClickListenerInterface clickListen) {
        this.clickListenerInterface = clickListen;
    }

    private class ClickListen implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            clickListenerInterface.select(v.getId());
        }
    }
}
