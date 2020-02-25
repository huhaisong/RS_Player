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
import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.ui.activity.PhotoActivity;
import com.example.hu.mediaplayerapk.ui.activity.VideoActivity;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.util.ArrayList;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITMESSAGE;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITTIME;

public class PlayBackModePop implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {

    private Context mContext;
    private PopupWindow playBackMode;
    private PopupWindow impactvProgramPop;
    private Handler quitHandler;

    public PlayBackModePop(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.quitHandler = handler;
        existExternalSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
    }

    //private View parent;
    private ArrayList<TextView> playBackModeTextViews = new ArrayList<>();
    private TextView impactvModeOneFileTextView, impactvModeAllFileTextView, impactvModeProgramTextView, impactvModeScheduleTextView;

    public void showPlayBackModePop(View view) {
        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.play_back_mode_pop, null);
        impactvModeOneFileTextView = (TextView) contentView.findViewById(R.id.tv_play_back_mode_one_file_impactv);
        impactvModeAllFileTextView = (TextView) contentView.findViewById(R.id.tv_play_back_mode_all_file_impactv);
        impactvModeProgramTextView = (TextView) contentView.findViewById(R.id.tv_play_back_mode_program_impactv);
        impactvModeScheduleTextView = (TextView) contentView.findViewById(R.id.tv_play_back_mode_schedule_impactv);
        playBackModeTextViews.add(impactvModeOneFileTextView);
        playBackModeTextViews.add(impactvModeAllFileTextView);
        playBackModeTextViews.add(impactvModeProgramTextView);
        playBackModeTextViews.add(impactvModeScheduleTextView);
        initPlayBackModePopListen();
        initPlayBackModePopView();

        playBackMode = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        // parent = view;
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 40 * screenWidthRatio);
        playBackMode.setTouchable(true);
        playBackMode.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        playBackMode.showAsDropDown(view, xPos, (int) (-510 * screenHeightRatio));
        playBackMode.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_play_model));
            }
        });
    }

    private void setTextViewFocuse(TextView textView) {
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
    }

    private void setTextViewUnFocuse(TextView textView) {
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.gray_white));
        textView.setFocusable(false);
        textView.setFocusableInTouchMode(false);
    }

    private void initPlayBackModePopView() {

        if (existExternalSDCard) {
            if (!FileUtils.checkHaveGivenFile(external_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                setTextViewUnFocuse(impactvModeScheduleTextView);
            }
        } else {
            if (!FileUtils.checkHaveGivenFile(internal_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                setTextViewUnFocuse(impactvModeScheduleTextView);
            }
        }

        int selected;
        if (existExternalSDCard) {
            selected = SPUtils.getInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV);
        } else {
            selected = SPUtils.getInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV);
        }
        switch (selected) {
            case Config.PLAY_BACK_MODE_ALL_FILE:
                impactvModeAllFileTextView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.PLAY_BACK_MODE_ONE_FILE:
                impactvModeOneFileTextView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.PLAY_BACK_MODE_MIX_PROGRAM:
                impactvModeProgramTextView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
            case Config.PLAY_BACK_MODE_SCHEDULE:
                if (existExternalSDCard) {
                    if (!FileUtils.checkHaveGivenFile(external_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                        SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                    } else {
                        setTextViewFocuse(impactvModeScheduleTextView);
                        break;
                    }
                } else {
                    if (!FileUtils.checkHaveGivenFile(internal_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                        SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                    } else {
                        setTextViewFocuse(impactvModeScheduleTextView);
                        break;
                    }
                }
            default:
                impactvModeAllFileTextView.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                break;
        }
    }

    private void initPlayBackModePopListen() {
        for (TextView textView : playBackModeTextViews) {
            textView.setOnFocusChangeListener(this);
            textView.setOnClickListener(this);
            textView.setOnKeyListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        switch (v.getId()) {
            //impactv
            case R.id.tv_play_back_mode_one_file_impactv:
//                ((Activity) mContext).findViewById(R.id.osd_layout).setVisibility(View.GONE);
                Intent oneFileIntent = new Intent(mContext, VideoActivity.class);
                oneFileIntent.putExtra("content", "video_impactv");
                mContext.startActivity(oneFileIntent);
                quitHandler.removeMessages(QUITMESSAGE);
                playBackMode.dismiss();
                break;
            case R.id.tv_play_back_mode_all_file_impactv:
                if (existExternalSDCard) {
                    SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                } else {
                    SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                }
                playBackMode.dismiss();
                break;
            case R.id.tv_play_back_mode_program_impactv:
                showImpactvProgramPop(v);
                break;
            case R.id.tv_play_back_mode_program_photo_impactv:
//                ((Activity) mContext).findViewById(R.id.osd_layout).setVisibility(View.GONE);
                Intent modeProgramPhotoIntent = new Intent(mContext, PhotoActivity.class);
                modeProgramPhotoIntent.putExtra("content", "impactv");
                mContext.startActivity(modeProgramPhotoIntent);
                quitHandler.removeMessages(QUITMESSAGE);
                impactvProgramPop.dismiss();
                playBackMode.dismiss();
                break;
            case R.id.tv_play_back_mode_program_video_impactv:
//                ((Activity) mContext).findViewById(R.id.osd_layout).setVisibility(View.GONE);
                Intent modeProgramVideoIntent = new Intent(mContext, VideoActivity.class);
                modeProgramVideoIntent.putExtra("content", "videos_impactv");
                mContext.startActivity(modeProgramVideoIntent);
                quitHandler.removeMessages(QUITMESSAGE);
                impactvProgramPop.dismiss();
                playBackMode.dismiss();
                break;
            case R.id.tv_play_back_mode_schedule_impactv:
                if (existExternalSDCard) {
                    SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
                } else {
                    SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
                }
                playBackMode.dismiss();
                break;
        }
    }

    private void showImpactvProgramPop(View v) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.play_back_mode_program_pop_impactv, null);
        impactvProgramPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int xPos = (int) (location[0] + v.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 30 * screenHeightRatio);
        contentView.findViewById(R.id.tv_play_back_mode_program_photo_impactv).setOnClickListener(this);
        contentView.findViewById(R.id.tv_play_back_mode_program_video_impactv).setOnClickListener(this);
        contentView.findViewById(R.id.tv_play_back_mode_program_photo_impactv).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_play_back_mode_program_video_impactv).setOnKeyListener(this);
        impactvProgramPop.setTouchable(true);
        impactvProgramPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        impactvProgramPop.showAsDropDown(LayoutInflater.from(mContext)
                .inflate(R.layout.play_back_mode_pop, null)
                .findViewById(R.id.tv_play_back_mode_one_file_impactv), xPos, yPos);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_play_back_mode_all_file_impactv:
                case R.id.tv_play_back_mode_one_file_impactv:
                case R.id.tv_play_back_mode_program_impactv:
                case R.id.tv_play_back_mode_schedule_impactv:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        playBackMode.dismiss();
                        return true;
                    }
                    break;
                case R.id.tv_play_back_mode_program_photo_impactv:
                case R.id.tv_play_back_mode_program_video_impactv:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        impactvProgramPop.dismiss();
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
                case R.id.tv_play_back_mode_one_file_impactv:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_play_back_mode_one_file));
                    break;
                case R.id.tv_play_back_mode_all_file_impactv:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_play_back_mode_all_file));
                    break;
                case R.id.tv_play_back_mode_program_impactv:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_play_back_mode_program));
                    break;
                case R.id.tv_play_back_mode_schedule_impactv:
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_play_back_mode_Schedule));
                    break;
            }
        }
    }
}
