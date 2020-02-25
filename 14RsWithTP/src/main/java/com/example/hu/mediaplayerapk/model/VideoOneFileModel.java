package com.example.hu.mediaplayerapk.model;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.SPUtils;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.PAGE_SIZE;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.currentPage;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.isImpactTv;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.itemTextViewId;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.selectedVideos;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class VideoOneFileModel implements View.OnClickListener {

    private Context mContext;
    private TextView ensureTextView, cancelTextView, contentTextView;
    private View mContentView;
    private int selectedNum;

    public VideoOneFileModel(Context mContext, View contentView) {
        this.mContext = mContext;
        this.mContentView = contentView;
    }

    public void init() {
        mContentView.findViewById(R.id.video_one_layout).setVisibility(View.VISIBLE);
        ensureTextView = (TextView) mContentView.findViewById(R.id.tv_selected_one_file_ensure);
        cancelTextView = (TextView) mContentView.findViewById(R.id.tv_selected_one_cancel);
        contentTextView = (TextView) mContentView.findViewById(R.id.tv_selected_one_content);
        String heldVideo;

        if (isImpactTv == 0) {
            if (existExternalSDCard) {
                heldVideo = SPUtils.getString(mContext, Config.EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE);
            } else {
                heldVideo = SPUtils.getString(mContext, Config.INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE);
            }
        } else {
            if (existExternalSDCard) {
                heldVideo = SPUtils.getString(mContext, Config.EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE);
            } else {
                heldVideo = SPUtils.getString(mContext, Config.INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE);
            }
        }

        if (!heldVideo.equals("null")) {
            for (int i = 0; i < selectedVideos.size(); i++) {
                if (selectedVideos.get(i).equals(heldVideo)) {
                    currentPage = i / PAGE_SIZE;
                    selectedNum = i % PAGE_SIZE;
                    String[] strings = heldVideo.split("/");
                    String content = strings[strings.length - 1];
                    contentTextView.setText(content);
                    break;
                }
            }
        }
        initClickListen();
    }

    private void initClickListen() {
        for (int anItemTextViewId : itemTextViewId) {
            mContentView.findViewById(anItemTextViewId).setOnClickListener(this);
        }
        ensureTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        for (int i = 0; i < itemTextViewId.length; i++) {
            if (v.getId() == itemTextViewId[i])
                selectedNum = i;
        }

        switch (v.getId()) {
            case R.id.tv_selected_one_cancel:
                ((Activity) mContext).finish();
                break;
            case R.id.tv_selected_one_file_ensure:
                if (selectedVideos.size() > 0) {
                    if (existExternalSDCard) {
                        if (isImpactTv == 0) {
                            SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ONE_FILE);
                            SPUtils.putString(mContext, Config.EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE, selectedVideos.get(currentPage * PAGE_SIZE + selectedNum));
                        } else {
                            SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ONE_FILE);
                            SPUtils.putString(mContext, Config.EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE, selectedVideos.get(currentPage * PAGE_SIZE + selectedNum));
                        }
                    } else {
                        if (isImpactTv == 0) {
                            SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ONE_FILE);
                            SPUtils.putString(mContext, Config.INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE,
                                    selectedVideos.get(currentPage * PAGE_SIZE + selectedNum));
                        } else {
                            SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ONE_FILE);
                            SPUtils.putString(mContext, Config.INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE,
                                    selectedVideos.get(currentPage * PAGE_SIZE + selectedNum));
                        }
                    }
                    ((Activity) mContext).finish();
                }
                break;
            default:
                if (selectedVideos.size() > 0) {
                    String[] strings = selectedVideos.get(currentPage * PAGE_SIZE + selectedNum).split("/");
                    String content = strings[strings.length - 1];
                    contentTextView.setText(content);
                    contentTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.file_one_sel_bg));
                    ensureTextView.requestFocus();
                    contentTextView.setSelected(true);
                }
                break;
        }
    }
}
