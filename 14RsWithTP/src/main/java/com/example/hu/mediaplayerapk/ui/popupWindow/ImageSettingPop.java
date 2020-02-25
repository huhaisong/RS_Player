package com.example.hu.mediaplayerapk.ui.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.ui.activity.BGMActivity;
import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITMESSAGE;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITTIME;

public class ImageSettingPop implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {

    private Context mContext;
    private PopupWindow imageSettingTime;
    private PopupWindow imageSettingDirection;
    private PopupWindow impactvImageSettingBGM;
    private PopupWindow eventImageSettingBGM;
    private Handler quitHandler;
    private PopupWindow imageSetting;

    public ImageSettingPop(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.quitHandler = handler;
        existExternalSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
    }

    public void imageSettingPop(View view) {

        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.image_setting_pop, null);
        imageSetting = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 40 * screenWidthRatio);

        contentView.findViewById(R.id.tv_image_setting_SlideTimeSelect).setOnFocusChangeListener(this);
        contentView.findViewById(R.id.tv_image_setting_SlideTimeSelect).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_image_setting_SlideTimeSelect).setOnClickListener(this);

        contentView.findViewById(R.id.tv_image_setting_SlidePatternSelect).setOnFocusChangeListener(this);
        contentView.findViewById(R.id.tv_image_setting_SlidePatternSelect).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_image_setting_SlidePatternSelect).setOnClickListener(this);

        contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setOnFocusChangeListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setOnKeyListener(this);

        contentView.findViewById(R.id.tv_image_setting_bgm_event).setOnFocusChangeListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_event).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_event).setOnKeyListener(this);

        if (!isShowImpactvBGM()) {
            contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setFocusable(false);
            contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setFocusableInTouchMode(false);
            contentView.findViewById(R.id.tv_image_setting_bgm_impactv).setClickable(false);
            ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_impactv)).setTextColor(ContextCompat.getColor(mContext, R.color.gray_white));
        }

        if (!isShowEventBGM()) {
            contentView.findViewById(R.id.tv_image_setting_bgm_event).setFocusable(false);
            contentView.findViewById(R.id.tv_image_setting_bgm_event).setFocusableInTouchMode(false);
            contentView.findViewById(R.id.tv_image_setting_bgm_event).setClickable(false);
            ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_event)).setTextColor(ContextCompat.getColor(mContext, R.color.gray_white));
        }

        imageSetting.setTouchable(true);
        imageSetting.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        imageSetting.showAsDropDown(view, xPos, (int) (-420 * screenHeightRatio));
        imageSetting.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_image_setting));
            }
        });
    }

    private boolean isShowImpactvBGM() {
        List<String> fileList = new ArrayList<>();
        if (existExternalSDCard) {
            int playModel = SPUtils.getInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV);
            if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
                if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        } else {
            int playModel = SPUtils.getInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV);
            if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
                if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        }
        if (fileList.size() > 0) {
            if (allIsPhoto(fileList)) {
                if (existExternalSDCard) {
                    if (FileUtils.getAudio(new File(external_impactv_path)).length > 0) {
                        return true;
                    }
                } else {
                    if (FileUtils.getAudio(new File(internal_impactv_path)).length > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isShowEventBGM() {
        List<String> fileList = new ArrayList<>();
        if (existExternalSDCard) {
            int playModel = SPUtils.getInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT);
            if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
                if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        } else {
            int playModel = SPUtils.getInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT);
            if (playModel == Config.PLAY_BACK_MODE_MIX_PROGRAM) {
                if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(fileList, strings);
                        fileList.remove("");
                    }
                }
            }
        }
        if (fileList.size() > 0) {
            if (allIsPhoto(fileList)) {
                if (existExternalSDCard) {
                    if (FileUtils.getAudio(new File(external_event_path)).length > 0) {
                        return true;
                    }
                } else {
                    if (FileUtils.getAudio(new File(internal_event_path)).length > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean allIsPhoto(List<String> fileList) {
        for (String path : fileList) {
            if (isVideo(path)) {
                return false;
            }
        }
        return true;
    }

    private boolean isVideo(String path) {
        int i = path.lastIndexOf('.');
        if (i != -1) {
            String name = path.substring(i);
            if (name.equalsIgnoreCase(".mp4")
                    || name.equalsIgnoreCase(".3gp")
                    || name.equalsIgnoreCase(".wmv")
                    || name.equalsIgnoreCase(".ts")
                    || name.equalsIgnoreCase(".rmvb")
                    || name.equalsIgnoreCase(".mov")
                    || name.equalsIgnoreCase(".m4v")
                    || name.equalsIgnoreCase(".avi")
                    || name.equalsIgnoreCase(".m3u8")
                    || name.equalsIgnoreCase(".3gpp")
                    || name.equalsIgnoreCase(".3gpp2")
                    || name.equalsIgnoreCase(".mkv")
                    || name.equalsIgnoreCase(".flv")
                    || name.equalsIgnoreCase(".divx")
                    || name.equalsIgnoreCase(".f4v")
                    || name.equalsIgnoreCase(".rm")
                    || name.equalsIgnoreCase(".asf")
                    || name.equalsIgnoreCase(".ram")
                    || name.equalsIgnoreCase(".mpg")
                    || name.equalsIgnoreCase(".v8")
                    || name.equalsIgnoreCase(".swf")
                    || name.equalsIgnoreCase(".m2v")
                    || name.equalsIgnoreCase(".asx")
                    || name.equalsIgnoreCase(".ra")
                    || name.equalsIgnoreCase(".ndivx")
                    || name.equalsIgnoreCase(".xvid")) {
                return true;
            }
        }
        return false;
    }

    private void showImageSettingTimePop(View view) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.image_setting_time_pop, null);
        imageSettingTime = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 60 * screenHeightRatio);
        contentView.findViewById(R.id.tv_image_setting_time_5).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_time_5).setOnKeyListener(this);

        contentView.findViewById(R.id.tv_image_setting_time_10).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_image_setting_time_10).setOnClickListener(this);

        contentView.findViewById(R.id.tv_image_setting_time_20).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_image_setting_time_20).setOnClickListener(this);

        contentView.findViewById(R.id.tv_image_setting_time_30).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_image_setting_time_30).setOnClickListener(this);

        int selected = SPUtils.getInt(mContext, Config.IMAGE_TIME);
        switch (selected) {
            case 5:
                contentView.findViewById(R.id.tv_image_setting_time_5).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_time_5))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case 10:
                contentView.findViewById(R.id.tv_image_setting_time_10).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_time_10))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case 20:
                contentView.findViewById(R.id.tv_image_setting_time_20).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_time_20))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case 30:
                contentView.findViewById(R.id.tv_image_setting_time_30).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_time_30))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            default:
                contentView.findViewById(R.id.tv_image_setting_time_5).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_time_5))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
        }
        imageSettingTime.setTouchable(true);
        imageSettingTime.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        imageSettingTime.showAsDropDown(LayoutInflater.from(mContext).inflate(R.layout.image_setting_pop, null).findViewById(R.id.tv_image_setting_SlideTimeSelect), xPos, yPos);
    }

    private void showImageSettingDirectionPop(View v) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.image_setting_direction_pop, null);
        imageSettingDirection = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int xPos = (int) (location[0] + v.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 187 * screenHeightRatio);
        contentView.findViewById(R.id.image_setting_direction_horizontal_cross).setOnClickListener(this);
        contentView.findViewById(R.id.image_setting_direction_left_to_right).setOnClickListener(this);
        contentView.findViewById(R.id.image_setting_direction_random).setOnClickListener(this);
        contentView.findViewById(R.id.image_setting_direction_normal).setOnClickListener(this);
        contentView.findViewById(R.id.image_setting_direction_up_to_down).setOnClickListener(this);

        contentView.findViewById(R.id.image_setting_direction_horizontal_cross).setOnKeyListener(this);
        contentView.findViewById(R.id.image_setting_direction_left_to_right).setOnKeyListener(this);
        contentView.findViewById(R.id.image_setting_direction_random).setOnKeyListener(this);
        contentView.findViewById(R.id.image_setting_direction_normal).setOnKeyListener(this);
        contentView.findViewById(R.id.image_setting_direction_up_to_down).setOnKeyListener(this);
        int selected = SPUtils.getInt(mContext, Config.IMAGE_DIRECTION);
        switch (selected) {
            case Config.IMAGE_DIRECTION_NORMAL:
                contentView.findViewById(R.id.image_setting_direction_normal).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_normal))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_DIRECTION_RANDOM:
                contentView.findViewById(R.id.image_setting_direction_random).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_random))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_DIRECTION_HORIZONTALCROSS:
                contentView.findViewById(R.id.image_setting_direction_horizontal_cross).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_horizontal_cross))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_DIRECTION_UPTODOWN:
                contentView.findViewById(R.id.image_setting_direction_up_to_down).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_up_to_down))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_DIRECTION_LEFTTORIGHT:
                contentView.findViewById(R.id.image_setting_direction_left_to_right).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_left_to_right))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            default:
                contentView.findViewById(R.id.image_setting_direction_normal).requestFocus();
                ((TextView) contentView.findViewById(R.id.image_setting_direction_normal))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
        }
        imageSettingDirection.setTouchable(true);
        imageSettingDirection.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        imageSettingDirection.showAsDropDown(LayoutInflater.from(mContext).inflate(R.layout.image_setting_pop, null).findViewById(R.id.tv_image_setting_SlideTimeSelect), xPos, yPos);
    }

    private void showImpactvImageSettingBGM(View v) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.image_setting_bgm_pop_impactv, null);
        impactvImageSettingBGM = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int xPos = (int) (location[0] + v.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 58 * screenHeightRatio);
        contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv).setOnKeyListener(this);

        contentView.findViewById(R.id.tv_image_setting_bgm_on_impactv).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_on_impactv).setOnKeyListener(this);

        int selected = SPUtils.getInt(mContext, Config.IMAGE_BGM_IMPACTV);
        switch (selected) {
            case Config.IMAGE_BGM_OFF:
                contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_BGM_ON:
                contentView.findViewById(R.id.tv_image_setting_bgm_on_impactv).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_on_impactv))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            default:
                contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_off_impactv))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
        }
        impactvImageSettingBGM.setTouchable(true);
        impactvImageSettingBGM.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        impactvImageSettingBGM.showAsDropDown(LayoutInflater.from(mContext).inflate(R.layout.image_setting_pop, null).findViewById(R.id.tv_image_setting_SlideTimeSelect), xPos, yPos);
    }

    private void showEventImageSettingBGM(View v) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.image_setting_bgm_pop_event, null);
        eventImageSettingBGM = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int xPos = (int) (location[0] + v.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 58 * screenHeightRatio);
        contentView.findViewById(R.id.tv_image_setting_bgm_off_event).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_off_event).setOnKeyListener(this);

        contentView.findViewById(R.id.tv_image_setting_bgm_on_event).setOnClickListener(this);
        contentView.findViewById(R.id.tv_image_setting_bgm_on_event).setOnKeyListener(this);

        int selected = SPUtils.getInt(mContext, Config.IMAGE_BGM_EVENT);
        switch (selected) {
            case Config.IMAGE_BGM_OFF:
                contentView.findViewById(R.id.tv_image_setting_bgm_off_event).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_off_event))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.IMAGE_BGM_ON:
                contentView.findViewById(R.id.tv_image_setting_bgm_on_event).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_on_event))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            default:
                contentView.findViewById(R.id.tv_image_setting_bgm_off_event).requestFocus();
                ((TextView) contentView.findViewById(R.id.tv_image_setting_bgm_off_event))
                        .setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
        }
        eventImageSettingBGM.setTouchable(true);
        eventImageSettingBGM.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        eventImageSettingBGM.showAsDropDown(LayoutInflater.from(mContext).inflate(R.layout.image_setting_pop, null).findViewById(R.id.tv_image_setting_SlideTimeSelect), xPos, yPos);
    }

    @Override
    public void onClick(View v) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        switch (v.getId()) {
            //bgm
            case R.id.tv_image_setting_bgm_off_impactv:
                SPUtils.putInt(mContext, Config.IMAGE_BGM_IMPACTV, Config.IMAGE_BGM_OFF);
                impactvImageSettingBGM.dismiss();
                break;
            case R.id.tv_image_setting_bgm_on_impactv:
                SPUtils.putInt(mContext, Config.IMAGE_BGM_IMPACTV, Config.IMAGE_BGM_ON);
//                ((Activity) mContext).findViewById(R.id.osd_layout).setVisibility(View.GONE);
                Intent intent = new Intent(mContext, BGMActivity.class);
                intent.putExtra("content", "impactv");
                mContext.startActivity(intent);
                quitHandler.removeMessages(QUITMESSAGE);
                impactvImageSettingBGM.dismiss();
                break;
            case R.id.tv_image_setting_bgm_off_event:
                SPUtils.putInt(mContext, Config.IMAGE_BGM_EVENT, Config.IMAGE_BGM_OFF);
                eventImageSettingBGM.dismiss();
                break;
            case R.id.tv_image_setting_bgm_on_event:
                SPUtils.putInt(mContext, Config.IMAGE_BGM_EVENT, Config.IMAGE_BGM_ON);
//                ((Activity) mContext).findViewById(R.id.osd_layout).setVisibility(View.GONE);
                Intent intentEvent = new Intent(mContext, BGMActivity.class);
                intentEvent.putExtra("content", "event");
                mContext.startActivity(intentEvent);
                quitHandler.removeMessages(QUITMESSAGE);
                eventImageSettingBGM.dismiss();
                break;
            //direction
            case R.id.image_setting_direction_normal:
                SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_NORMAL);
                imageSettingDirection.dismiss();
                break;
            case R.id.image_setting_direction_random:
                SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_RANDOM);
                imageSettingDirection.dismiss();
                break;
            case R.id.image_setting_direction_horizontal_cross:
                SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_HORIZONTALCROSS);
                imageSettingDirection.dismiss();
                break;
            case R.id.image_setting_direction_up_to_down:
                SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_UPTODOWN);
                imageSettingDirection.dismiss();
                break;
            case R.id.image_setting_direction_left_to_right:
                SPUtils.putInt(mContext, Config.IMAGE_DIRECTION, Config.IMAGE_DIRECTION_LEFTTORIGHT);
                imageSettingDirection.dismiss();
                break;
            //time
            case R.id.tv_image_setting_time_5:
                SPUtils.putInt(mContext, Config.IMAGE_TIME, 5);
                imageSettingTime.dismiss();
                break;
            case R.id.tv_image_setting_time_10:
                SPUtils.putInt(mContext, Config.IMAGE_TIME, 10);
                imageSettingTime.dismiss();
                break;
            case R.id.tv_image_setting_time_20:
                SPUtils.putInt(mContext, Config.IMAGE_TIME, 20);
                imageSettingTime.dismiss();
                break;
            case R.id.tv_image_setting_time_30:
                SPUtils.putInt(mContext, Config.IMAGE_TIME, 30);
                imageSettingTime.dismiss();
                break;
            //menu
            case R.id.tv_image_setting_SlideTimeSelect:
                showImageSettingTimePop(v);
                break;
            case R.id.tv_image_setting_SlidePatternSelect:
                showImageSettingDirectionPop(v);
                break;
            case R.id.tv_image_setting_bgm_impactv:
                showImpactvImageSettingBGM(v);
                break;
            case R.id.tv_image_setting_bgm_event:
                showEventImageSettingBGM(v);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_image_setting_bgm_event:
                case R.id.tv_image_setting_bgm_impactv:
                case R.id.tv_image_setting_SlidePatternSelect:
                case R.id.tv_image_setting_SlideTimeSelect:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        imageSetting.dismiss();
                        return true;
                    }
                    break;
                case R.id.tv_image_setting_time_5:
                case R.id.tv_image_setting_time_10:
                case R.id.tv_image_setting_time_20:
                case R.id.tv_image_setting_time_30:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        imageSettingTime.dismiss();
                        return true;
                    }
                    break;

                case R.id.image_setting_direction_horizontal_cross:
                case R.id.image_setting_direction_left_to_right:
                case R.id.image_setting_direction_normal:
                case R.id.image_setting_direction_up_to_down:
                case R.id.image_setting_direction_random:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        imageSettingDirection.dismiss();
                        return true;
                    }
                    break;

                case R.id.tv_image_setting_bgm_off_impactv:
                case R.id.tv_image_setting_bgm_on_impactv:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        impactvImageSettingBGM.dismiss();
                        return true;
                    }
                    break;

                case R.id.tv_image_setting_bgm_off_event:
                case R.id.tv_image_setting_bgm_on_event:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        eventImageSettingBGM.dismiss();
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.tv_image_setting_SlidePatternSelect:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_image_setting_slide_pattern_select));
                    break;
                case R.id.tv_image_setting_SlideTimeSelect:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_image_setting_slide_time_select));
                    break;
                case R.id.tv_image_setting_bgm_event:
                case R.id.tv_image_setting_bgm_impactv:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_image_setting_bgm));
                    break;
            }
        }
    }
}
