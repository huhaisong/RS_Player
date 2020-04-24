package com.example.hu.mediaplayerapk.model;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.media.MediaPlayerImp;
import com.example.hu.mediaplayerapk.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_washing_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_washing_path;
import static com.example.hu.mediaplayerapk.ui.activity.VideoActivity.isPreView;


public class VideoPreViewModel implements View.OnKeyListener, MediaPlayer.OnErrorListener, MediaPlayerImp.FileError {

    static int currentNum = 0;
    static final int PAGE_SIZE = 10;
    static int currentPage = 0;
    static List<String> selectedVideos = new ArrayList<>();
    static int[] itemTextViewId = {R.id.tv_item1, R.id.tv_item2, R.id.tv_item3, R.id.tv_item4,
            R.id.tv_item5, R.id.tv_item6, R.id.tv_item7, R.id.tv_item8, R.id.tv_item9, R.id.tv_item10};
    private Context mContext;
    private MediaPlayerImp mMediaPlayerImp;
    private int totalPage = 0;
    private List<String> impactTvVideos = new ArrayList<>();
//    private List<String> eventVideos = new ArrayList<>();
//    private List<String> beaconVideos = new ArrayList<>();
    private List<String> warningVideos = new ArrayList<>();
    private List<String> washingVideos = new ArrayList<>();
    public static int isImpactTv = 0;  //0表示impactv，1表示event，2表示beacon
    private boolean isSurfaceViewCreated = false;
    private View mContentView;
    LinearLayout linearLayout;
    int windowHeight, windowWidth;
    Handler startVideoHandler;

    public VideoPreViewModel(Context mContext, View contentView, int impactTv, Handler handler) {
        this.mContext = mContext;
        this.mContentView = contentView;
        isImpactTv = impactTv;
        existExternalSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
        Display disp = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        windowHeight = disp.getHeight();
        windowWidth = disp.getWidth();
        this.startVideoHandler = handler;
        if (existExternalSDCard) {
            if (FileUtils.checkHaveFile(external_impactv_path)) {
                impactTvVideos = getVideoList(external_impactv_path);
            } else {
                impactTvVideos = getVideoList(external_impacttv_path);
            }
//            eventVideos = getVideoList(external_event_path);
//            beaconVideos = getVideoList(external_beacon_path);
            warningVideos = getVideoList(external_warning_path);
            washingVideos = getVideoList(external_washing_path);
        } else {
            impactTvVideos = getVideoList(internal_impactv_path);
//            eventVideos = getVideoList(internal_event_path);
//            beaconVideos = getVideoList(internal_beacon_path);
            warningVideos = getVideoList(internal_warning_path);
            washingVideos = getVideoList(internal_washing_path);
        }
        mSurfaceView = (SurfaceView) ((Activity) mContext).findViewById(R.id.surfaceView_video);
        linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.onError_layout);
        mContentView.findViewById(R.id.tv_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < totalPage - 1) {
                    currentPage++;
                    mContentView.findViewById(R.id.tv_item1).requestFocus();
                    initVideoListView();
                } else if (currentPage == totalPage - 1) {
                    currentPage = 0;
                    mContentView.findViewById(R.id.tv_item1).requestFocus();
                    initVideoListView();
                }
                setErrorLayoutGone();
                mMediaPlayerImp.stop();
                if (isSurfaceViewCreated)
                    mMediaPlayerImp.play(selectedVideos.get(currentPage * PAGE_SIZE), false, false);
            }
        });

    }

    SurfaceView mSurfaceView;

    public void init() {
        Log.e(TAG, "init: ");
        isSurfaceViewCreated = false;
        if (mSurfaceView != null) {
            mSurfaceView.setKeepScreenOn(true);
        }
        mMediaPlayerImp = new MediaPlayerImp(mSurfaceView, mContext);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewHeight = dm.heightPixels;
        int mSurfaceViewWidth = dm.widthPixels;
        mMediaPlayerImp.setScreenHeightAndWight(mSurfaceViewHeight, mSurfaceViewWidth);
        mMediaPlayerImp.setOnCompletionListener(new MyOnCompletionListener());
        mMediaPlayerImp.setOnFileErrorListener(this);
        mMediaPlayerImp.setOnErrorListener(this);
        assert mSurfaceView != null;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startVideoHandler.sendEmptyMessage(0);
                isSurfaceViewCreated = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceViewCreated = false;
            }
        });
        initListen();
        switchDirectory();
    }

    private static final String TAG = "VideoPreViewModel";

    public void switchDirectory() {
        if (selectedVideos.size() == 0)
            setErrorLayoutGone();
        switch (isImpactTv) {
            case 0:
                selectedVideos = impactTvVideos;
                break;
        /*    case 1:
                selectedVideos = eventVideos;
                break;
            case 2:
                selectedVideos = beaconVideos;
                break;*/
            case 1:
                selectedVideos = washingVideos;
                break;
            case 2:
                selectedVideos = warningVideos;
                break;
        }
        if (selectedVideos.size() == 0) {
            mMediaPlayerImp.stop();
            mSurfaceView.setVisibility(View.GONE);
        } else {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        currentPage = 0;
        totalPage = selectedVideos.size() / PAGE_SIZE;
        if (selectedVideos.size() % PAGE_SIZE != 0) {
            totalPage++;
        }
        if (selectedVideos.size() > 0 && isSurfaceViewCreated) {
            setErrorLayoutGone();
            mMediaPlayerImp.stop();
            mMediaPlayerImp.play(selectedVideos.get(0), false, false);
        }
        initVideoListView();
    }

    private void initVideoListView() {
        String content = "";
        switch (isImpactTv) {
            case 0:
                content = "Impactv ";
                break;
          /*  case 1:
                content = "Event ";
                break;
            case 2:
                content = "Beacon ";
                break;*/
            case 1:
                content = "Washing ";
                break;
            case 2:
                content = "Warning ";
                break;
        }
        ((TextView) mContentView.findViewById(R.id.tv_page)).setText(content + (currentPage + 1) + "/" + totalPage);
        for (int i = 0; i < itemTextViewId.length; i++) {
            if ((currentPage * PAGE_SIZE + i) < selectedVideos.size()) {
                String[] titles = selectedVideos.get(i + currentPage * PAGE_SIZE).split("/");
                String title = titles[titles.length - 1];
                ((TextView) mContentView.findViewById(itemTextViewId[i])).setText(title);
            } else {
                ((TextView) mContentView.findViewById(itemTextViewId[i])).setText("");
            }
        }
    }

    private void initListen() {

        for (int anItemTextViewId : itemTextViewId) {
            mContentView.findViewById(anItemTextViewId).setOnKeyListener(this);
        }

        for (int i = 0; i < itemTextViewId.length; i++) {
            final int finalI = i;
            Log.e(TAG, "initListen: ");
            mContentView.findViewById(itemTextViewId[i]).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!isSurfaceViewCreated)
                        return;
                    if (hasFocus) {
                        if ((currentPage * PAGE_SIZE + finalI) < selectedVideos.size() && isSurfaceViewCreated) {
                            setErrorLayoutGone();
                            mMediaPlayerImp.stop();
                            if (selectedVideos.size() > 0)
                                mMediaPlayerImp.play(selectedVideos.get(finalI + currentPage * PAGE_SIZE), false, false);
                            Log.e(TAG, "onFocusChange1: play v.getId():" + v.getId());
                            currentNum = finalI;
                        } else {
                            currentPage = 0;
                            currentNum = 0;
                            mContentView.findViewById(R.id.tv_item1).requestFocus();
                            if (selectedVideos.size() > 0 && isSurfaceViewCreated) {
                                setErrorLayoutGone();
                                mMediaPlayerImp.stop();
                                mMediaPlayerImp.play(selectedVideos.get(0), false, false);
                                Log.e(TAG, "onFocusChange2: play v.getId():" + v.getId());
                            }
                            initVideoListView();
                        }
                    }
                }
            });
        }
    }

    private List<String> getVideoList(String path) {
        List<String> list = new ArrayList<>();
        List<String> newList;
        File[] files = FileUtils.getVideo(new File(path));
        if (files != null && files.length > 0) {
            for (File item : files) {
                list.add(item.getAbsolutePath());
            }
        }
        newList = FileUtils.orderList(list);
        return newList;
    }

    public void onPause() {
        mMediaPlayerImp.pause();
    }

    public void onDestroy() {
        mMediaPlayerImp.close();
    }

    public void onResume() {
        mMediaPlayerImp.unPause();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (isPreView) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        isImpactTv -= 1;
                        if (isImpactTv < 0)
                            isImpactTv += 3;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        isImpactTv += 1;
                    }
                    isImpactTv = isImpactTv % 3;
                    switchDirectory();
                    mContentView.findViewById(R.id.tv_item1).requestFocus();
                }
            }
            switch (v.getId()) {
                case R.id.tv_item1:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (currentPage > 0) {
                            currentPage--;
                            mContentView.findViewById(R.id.tv_item10).requestFocus();
                            initVideoListView();
                        } else if (currentPage == 0) {
                            if (totalPage > 0) {
                                currentPage = totalPage - 1;
                                Log.e(TAG, "onKey: totalPage > 0");
                                currentNum = (selectedVideos.size() - 1) % 10;
                                if (currentNum >= 0) {
                                    mContentView.findViewById(itemTextViewId[currentNum]).requestFocus();
                                }
                                initVideoListView();
                            }
                        }
                        return true;
                    }
                    return false;
                case R.id.tv_item10:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentPage < totalPage - 1) {
                            currentPage++;
                            mContentView.findViewById(R.id.tv_item1).requestFocus();
                            initVideoListView();
                        } else if (currentPage == totalPage - 1) {
                            currentPage = 0;
                            mContentView.findViewById(R.id.tv_item1).requestFocus();
                            initVideoListView();
                        }
                        return true;
                    }
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setErrorLayoutVisible();
        return false;
    }

    @Override
    public void onFileError() {
        setErrorLayoutVisible();
    }

    private void setErrorLayoutVisible() {
        mMediaPlayerImp.close();
        if (linearLayout.getVisibility() == View.GONE) {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setErrorLayoutGone() {
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            setErrorLayoutGone();
            if (isSurfaceViewCreated)
                if (selectedVideos.size() > 0) {
                    mMediaPlayerImp.play(selectedVideos.get(currentPage * PAGE_SIZE + currentNum), false, false);
                }
        }
    }

    int xTouch, yTouch;

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        switch (paramMotionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.xTouch = (int) paramMotionEvent.getX();
                this.yTouch = (int) paramMotionEvent.getY();
                return true;
            case 1:
                float f2 = paramMotionEvent.getY();
                Log.e(TAG, "onTouchEvent:  f2 = " + f2 + ",xTouch = " + xTouch + ",yTouch = " + yTouch + ",windowWidth = " + windowWidth);
                if (this.xTouch < (windowWidth * 7) / 24) {
                    if (yTouch - f2 > 200) {
                        if (currentPage > 0) {
                            currentPage--;
                            mContentView.findViewById(R.id.tv_item10).requestFocus();
                            initVideoListView();
                        } else if (currentPage == 0) {
                            if (totalPage > 0) {
                                currentPage = totalPage - 1;
                                Log.e(TAG, "onKey: totalPage > 0");
                                currentNum = (selectedVideos.size() - 1) % 10;
                                if (currentNum >= 0) {
                                    mContentView.findViewById(itemTextViewId[currentNum]).requestFocus();
                                }
                                initVideoListView();
                            }
                        }
                        setErrorLayoutGone();
                        mMediaPlayerImp.stop();
                        if (isSurfaceViewCreated) {
                            mMediaPlayerImp.play(selectedVideos.get(currentPage * PAGE_SIZE), false, false);
                        }
                    } else if (yTouch - f2 < -200) {
                        if (currentPage < totalPage - 1) {
                            currentPage++;
                            mContentView.findViewById(R.id.tv_item1).requestFocus();
                            initVideoListView();
                        } else if (currentPage == totalPage - 1) {
                            currentPage = 0;
                            mContentView.findViewById(R.id.tv_item1).requestFocus();
                            initVideoListView();
                        }
                        setErrorLayoutGone();
                        mMediaPlayerImp.stop();
                        if (isSurfaceViewCreated) {
                            mMediaPlayerImp.play(selectedVideos.get(currentPage * PAGE_SIZE), false, false);
                        }
                    }
                    return true;
                }
                return true;
            default:
                return false;
        }
    }
}
