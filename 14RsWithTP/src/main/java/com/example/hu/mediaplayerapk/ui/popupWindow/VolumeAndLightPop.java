package com.example.hu.mediaplayerapk.ui.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.model.MainActivityPlayModel;

import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;

/**
 * Created by Administrator on 2017/3/31.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class VolumeAndLightPop {

    private ImageView volumeAndLightImageView01;
    private ImageView volumeAndLightImageView02;
    private TextView volumeAndLightTextView;
    private AudioManager mAudioManager;
    private int stepVolume;
    private float stepBrightNess;
    private PopupWindow volumeAndBrightnessPopupWindow;
    private Context mContext;
    private MainActivityPlayModel mainActivityPlayModel;
    private boolean isSetVolume = true;
    private boolean isFirstVolume = true;
    private boolean isSetLight = false;
    private int[] volumeImageSourceID = {R.drawable.speaker_off, R.drawable.speaker_01, R.drawable.speaker_02, R.drawable.speaker_03, R.drawable.speaker_04, R.drawable.speaker_05, R.drawable.speaker_06, R.drawable.speaker_07, R.drawable.speaker_08, R.drawable.speaker_09,
            R.drawable.speaker_10, R.drawable.speaker_11, R.drawable.speaker_12, R.drawable.speaker_13, R.drawable.speaker_14, R.drawable.speaker_15};

    private int[] BrightnessImageSourceId = {R.drawable.brightness_icon, R.drawable.brightness_icon_01, R.drawable.brightness_icon_02, R.drawable.brightness_icon_03, R.drawable.brightness_icon_04, R.drawable.brightness_icon_05, R.drawable.brightness_icon_06, R.drawable.brightness_icon_07,};

    private Handler volumeAndLightHandler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1111:
                    //mainActivityPlayModel.startDetector();
                    if (volumeAndBrightnessPopupWindow.isShowing()) {
                        volumeAndBrightnessPopupWindow.dismiss();
                    }
                    isSetVolume = true;
                    isSetLight = false;
                    isFirstVolume = true;
                    isShow = false;
                    break;
            }
            return false;
        }
    });

    public VolumeAndLightPop(Context context, MainActivityPlayModel mainActivityPlayModel) {
        mContext = context;
        this.mainActivityPlayModel = mainActivityPlayModel;
        initVolumeAndBrightness();
    }

    private void initVolumeAndBrightness() {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.volume_and_brightness_pop, null);
        volumeAndBrightnessPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        volumeAndLightImageView01 = (ImageView) contentView.findViewById(R.id.iv_volume_light_01_pop);
        volumeAndLightImageView02 = (ImageView) contentView.findViewById(R.id.iv_volume_light_02_pop);
        volumeAndLightTextView = (TextView) contentView.findViewById(R.id.tv_volume_light_pop);

        volumeAndBrightnessPopupWindow.setTouchable(false);
        volumeAndBrightnessPopupWindow.setFocusable(false);

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        stepVolume = maxVolume / 15;
        stepBrightNess = 255 / 7;
    }

    private static final String TAG = "VolumeAndLightPop";

    public void setScreenBrightness(int brightness) {
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = (float) brightness * 7.0f / 7.0f / 7.0f;
        Log.e(TAG, "setScreenBrightness: " + lp.screenBrightness);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public void setBrightness(int current) {
        if (current > 7) {
            current = 7;
        }
        if (current < 0) {
            current = 0;
        }
        volumeAndLightImageView01.setBackground(ContextCompat.getDrawable(mContext, R.drawable.brightness_icon));
        if (current != 0) {
            volumeAndLightImageView02.setVisibility(View.VISIBLE);
            volumeAndLightImageView02.setImageResource(BrightnessImageSourceId[current]);
        } else {
            volumeAndLightImageView02.setVisibility(View.GONE);
        }
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int)(current * stepBrightNess));
        setScreenBrightness(current);
        volumeAndLightTextView.setText("0" + current);
    }

    private void setVolume(int progress) {
        if (progress > 15) {
            progress = 15;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress == 0) {
            volumeAndLightImageView01.setBackground(ContextCompat.getDrawable(mContext, R.drawable.speaker_off));
            volumeAndLightImageView02.setVisibility(View.GONE);
        } else {
            volumeAndLightImageView02.setVisibility(View.VISIBLE);
            volumeAndLightImageView01.setBackground(ContextCompat.getDrawable(mContext, R.drawable.speaker_on));
            volumeAndLightImageView02.setImageResource(volumeImageSourceID[progress]);
        }
        if (progress > 9) {
            volumeAndLightTextView.setText(progress + "");
        } else {
            volumeAndLightTextView.setText("0" + progress);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress * stepVolume, 0);
    }

    public int getSystemBrightness() {
        //获取当前亮度,获取失败则返回255
        int brightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        Log.e(TAG, "getSystemBrightness: " + brightness);
        return brightness;
    }

    public boolean isShow = false;
    private static int DISSMISS_TIME = 2 * 1000;

    public void showVolumeAndBrightnessPop() {
        //mainActivityPlayModel.stopDetector();
        volumeAndLightHandler.removeMessages(1111);
        volumeAndLightHandler.sendEmptyMessageDelayed(1111, DISSMISS_TIME);
        View view = ((Activity) mContext).findViewById(R.id.activity_main);
//        int height = (int) (700 * screenHeightRatio);
//        int width = (int) (460 * screenWidthRatio);
        int height = (int) (650 * screenHeightRatio);
        int width = (int) (560 * screenWidthRatio);
        isShow = true;
        volumeAndBrightnessPopupWindow.showAsDropDown(view, width, -height);
    }

    public void onkeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (KeyEvent.KEYCODE_ENTER == keyCode || KeyEvent.KEYCODE_DPAD_CENTER == keyCode) {
                if (volumeAndBrightnessPopupWindow.isShowing()) {
                    volumeAndLightHandler.sendEmptyMessage(1111);
                } else {
                    //mainActivityPlayModel.stopDetector();
                    volumeAndLightHandler.sendEmptyMessageDelayed(1111, 4000);
                }
            } else if (KeyEvent.KEYCODE_DPAD_UP == keyCode || KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
                showVolumeAndBrightnessPop();
                isSetLight = true;
                isSetVolume = false;
                int current = (int)(getSystemBrightness() / stepBrightNess);
                volumeAndLightImageView01.setBackground(ContextCompat.getDrawable(mContext, R.drawable.brightness_icon));
                if (current != 0) {
                    volumeAndLightImageView02.setImageResource(BrightnessImageSourceId[current]);
                    volumeAndLightImageView02.setVisibility(View.VISIBLE);
                } else {
                    volumeAndLightImageView02.setVisibility(View.GONE);
                }
                if (current > 9) {
                    volumeAndLightTextView.setText(current + "");
                } else {
                    volumeAndLightTextView.setText("0" + current);
                }
            } else if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
                showVolumeAndBrightnessPop();
                if (isSetVolume) {
                    int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / stepVolume;
                    if (!isFirstVolume) {
                        current--;
                    }
                    if (current < 0) {
                        current = 0;
                    }
                    setVolume(current);
                } else if (isSetLight) {
                    int current = (int)(getSystemBrightness() / stepBrightNess);
                    current--;
                    setBrightness(current);
                }
                isFirstVolume = false;
            } else if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
                showVolumeAndBrightnessPop();
                if (isSetVolume) {
                    int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / stepVolume;
                    if (!isFirstVolume) {
                        current++;
                    }
                    if (current > 15) {
                        current = 15;
                    }
                    setVolume(current);
                } else if (isSetLight) {
                    int current = (int)(getSystemBrightness() / stepBrightNess);
                    current++;
                    setBrightness(current);
                }
                isFirstVolume = false;
            }
        }
    }

    public void onBackPressed() {
        volumeAndLightHandler.sendEmptyMessage(1111);
    }

    public void onPause() {
        volumeAndLightHandler.removeMessages(1111);
        if (volumeAndBrightnessPopupWindow.isShowing()) {
            volumeAndBrightnessPopupWindow.dismiss();
        }
        isSetVolume = true;
        isSetLight = false;
        isFirstVolume = true;
        isShow = false;
    }
}
