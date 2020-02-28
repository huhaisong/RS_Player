package com.example.hu.mediaplayerapk.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.ui.popupWindow.AdvancedSettingPop;
import com.example.hu.mediaplayerapk.ui.popupWindow.ImageSettingPop;
import com.example.hu.mediaplayerapk.ui.popupWindow.PlayBackModePop;
import com.example.hu.mediaplayerapk.ui.popupWindow.TimeSettingPop;
import com.example.hu.mediaplayerapk.usb_copy.USBReceive;
import com.example.hu.mediaplayerapk.util.GoToHome;

import org.opencv.samples.facedetect.FdActivity;

public class OSDSettingActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener {

    public static int QUITTIME = 30 * 1000;
    public static int QUITMESSAGE = 1111;
    public static int QUITMESSAGE2 = 2222;
    LinearLayout mTimerSettingLayout;
    LinearLayout mAdvancedSettingLayout;
    LinearLayout mVideoPreviewLayout;
    LinearLayout mPlayModeLayout;
    LinearLayout mImageSettingLayout;
    LinearLayout mQuiLayout;
    ImageSettingPop mImageSettingPop;
    PlayBackModePop mPlayBackModePop;
    TimeSettingPop mTimeSettingPop;
    AdvancedSettingPop advancedSettingPop;

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == QUITMESSAGE) {
                finish();//如果不加finish会导致app挂掉
//                MyApplication.getInstance().restartApp();
            }
            return false;
        }
    });
    private static final String TAG = "OSDSettingActivity";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);
        introduceLayout = (LinearLayout) findViewById(R.id.osd_introduce_layout);
        introduceTextView = (TextView) findViewById(R.id.tv_osd_introduce);
        initView();
        initListen();
    }

    private USBReceive usbBroadCastReceive;

    @Override
    protected void onResume() {
        mHandler.removeMessages(QUITMESSAGE);
        mHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        //注册U盘广播
        usbBroadCastReceive = new USBReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addDataScheme("file");
        registerReceiver(usbBroadCastReceive, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(usbBroadCastReceive);
        mHandler.removeMessages(QUITMESSAGE);
        super.onPause();
    }

    private void initListen() {
        mTimerSettingLayout.setOnFocusChangeListener(this);
        mAdvancedSettingLayout.setOnFocusChangeListener(this);
        mVideoPreviewLayout.setOnFocusChangeListener(this);
        mPlayModeLayout.setOnFocusChangeListener(this);
        mImageSettingLayout.setOnFocusChangeListener(this);
        mQuiLayout.setOnFocusChangeListener(this);

        mTimerSettingLayout.setOnClickListener(this);
        mAdvancedSettingLayout.setOnClickListener(this);
        mVideoPreviewLayout.setOnClickListener(this);
        mPlayModeLayout.setOnClickListener(this);
        mImageSettingLayout.setOnClickListener(this);
        mQuiLayout.setOnClickListener(this);
        /*mQuiLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                              @Override
                                              public boolean onLongClick(View v) {
                                                  onBackPressed();
                                                  return true;
                                              }
                                          });*/

        mTimerSettingLayout.setOnKeyListener(this);
        mAdvancedSettingLayout.setOnKeyListener(this);
        mVideoPreviewLayout.setOnKeyListener(this);
        mPlayModeLayout.setOnKeyListener(this);
        mImageSettingLayout.setOnKeyListener(this);
        mQuiLayout.setOnKeyListener(this);
    }

    private void initView() {
        mTimerSettingLayout = (LinearLayout) findViewById(R.id.timer_setting_layout);
        mAdvancedSettingLayout = (LinearLayout) findViewById(R.id.advance_setting_layout);
        mVideoPreviewLayout = (LinearLayout) findViewById(R.id.video_preview_layout);
        mPlayModeLayout = (LinearLayout) findViewById(R.id.play_model_layout);
        mImageSettingLayout = (LinearLayout) findViewById(R.id.image_setting_layout);
        mQuiLayout = (LinearLayout) findViewById(R.id.quit_layout);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.timer_setting_layout:
                    initIntroduce(getString(R.string.introduce_timer_setting));
                    break;
                case R.id.advance_setting_layout:
                    initIntroduce(getString(R.string.introduce_detail_setting));
                    break;
                case R.id.video_preview_layout:
                    initIntroduce(getString(R.string.introduce_video_preview));
                    break;
                case R.id.play_model_layout:
                    initIntroduce(getString(R.string.introduce_play_model));
                    break;
                case R.id.image_setting_layout:
                    initIntroduce(getString(R.string.introduce_image_setting));
                    break;
                case R.id.quit_layout:
                    initIntroduce(getString(R.string.introduce_quit));
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        mHandler.removeMessages(QUITMESSAGE);
        mHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        switch (v.getId()) {
            case R.id.advance_setting_layout:
                advancedSettingPop = new AdvancedSettingPop(this, mHandler);
                advancedSettingPop.showAdvancedSettingPop(v);
                break;
            case R.id.image_setting_layout:
//                Intent intent1 = new Intent(this, FdActivity.class);
//                startActivity(intent1);
                mImageSettingPop = new ImageSettingPop(this, mHandler);
                mImageSettingPop.imageSettingPop(v);
                break;
            case R.id.quit_layout:
                onBackPressed();
                // Intent intent1 = new Intent(OSDSettingActivity.this, USBCopyActivity.class);
                // startActivity(intent1);
                // FileUtils.getSize(Config.EXTERNAL_FILE_PATH);
                break;
            case R.id.video_preview_layout:
//                findViewById(R.id.osd_layout).setVisibility(View.GONE);
                mHandler.removeMessages(QUITMESSAGE);
                Intent intent = new Intent(this, VideoActivity.class);
                intent.putExtra("content", "null");
                startActivity(intent);
                break;
            case R.id.play_model_layout:
                mPlayBackModePop = new PlayBackModePop(this, mHandler);
                mPlayBackModePop.showPlayBackModePop(v);
                break;
            case R.id.timer_setting_layout:
                mTimeSettingPop = new TimeSettingPop(this, mHandler);
                mTimeSettingPop.showTimeSettingPop(v);
        }
    }


    private static LinearLayout introduceLayout;
    private static TextView introduceTextView;

    public static void initIntroduce(String string) {
        if (string != null && !string.equals("null")) {
            introduceLayout.setVisibility(View.VISIBLE);
            introduceTextView.setText(string);
        } else {
            introduceLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        mHandler.removeMessages(QUITMESSAGE);
        mHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            GoToHome.goToHome(keyCode, event, OSDSettingActivity.this);
            switch (v.getId()) {
                case R.id.timer_setting_layout:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mQuiLayout.requestFocus();
                        return true;
                    }
                    break;
                case R.id.quit_layout:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        mTimerSettingLayout.requestFocus();
                        return true;
                    }
                    break;
            }
        }
        /*if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;//
        }
        else*/
        {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeMessages(QUITMESSAGE);
        mHandler.sendEmptyMessage(QUITMESSAGE);
    }

    @Override
    public void finish() {
        Log.e(TAG, "finish: ");
        mHandler.removeMessages(QUITMESSAGE);
//        findViewById(R.id.osd_introduce_layout).setVisibility(View.GONE);
        super.finish();
    /*    Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }
}
