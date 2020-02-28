package com.example.hu.mediaplayerapk.model;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.bean.BeaconBean;
import com.example.hu.mediaplayerapk.bean.ScheduleBean;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.media.BGMPlayer;
import com.example.hu.mediaplayerapk.media.MediaPlayerImp;
import com.example.hu.mediaplayerapk.ui.activity.MainActivity;
import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.util.BitmapUtil;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;
import com.example.hu.mediaplayerapk.util.ScheduleParse;
import com.example.hu.mediaplayerapk.util.TimeUtil;
import com.example.hu.mediaplayerapk.util.VideosHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.hu.mediaplayerapk.application.MyApplication.ScreenHeight;
import static com.example.hu.mediaplayerapk.application.MyApplication.ScreenWidth;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.beaconTagNo;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.isCanTimeStart;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.isPlayingBeaconEvent;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.isStartMotionCheck;
import static com.example.hu.mediaplayerapk.util.FileUtils.isPhoto;
import static com.example.hu.mediaplayerapk.util.FileUtils.isVideo;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getExternalEventFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getExternalImpactvFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getExternalStandFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getExternalWarningFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getExternalWashingFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getInternalEventFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getInternalImpactvFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getInternalStandFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getInternalWarningFileList;
import static com.example.hu.mediaplayerapk.util.VideosHelper.getInternalWashingFileList;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivityPlayModel implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayerImp.FileError {

    private static final int PLAY_NEXT_MESSAGE = 7777;
    private static final int PLAY_VIDEO_MESSAGE = 8888;
    private static final int CHECK_EXTERNAL_SDCARD = 1111;

    private int[] horizontalItemId = {
            R.id.image_horizontal_cross_item1, R.id.image_horizontal_cross_item2, R.id.image_horizontal_cross_item3, R.id.image_horizontal_cross_item4, R.id.image_horizontal_cross_item5, R.id.image_horizontal_cross_item6, R.id.image_horizontal_cross_item7, R.id.image_horizontal_cross_item8, R.id.image_horizontal_cross_item9, R.id.image_horizontal_cross_item10,
            R.id.image_horizontal_cross_item11, R.id.image_horizontal_cross_item12, R.id.image_horizontal_cross_item13, R.id.image_horizontal_cross_item14, R.id.image_horizontal_cross_item15, R.id.image_horizontal_cross_item16, R.id.image_horizontal_cross_item17, R.id.image_horizontal_cross_item18, R.id.image_horizontal_cross_item19, R.id.image_horizontal_cross_item20,
            R.id.image_horizontal_cross_item21, R.id.image_horizontal_cross_item22, R.id.image_horizontal_cross_item23, R.id.image_horizontal_cross_item24, R.id.image_horizontal_cross_item25, R.id.image_horizontal_cross_item26, R.id.image_horizontal_cross_item27, R.id.image_horizontal_cross_item28, R.id.image_horizontal_cross_item29, R.id.image_horizontal_cross_item30,
            R.id.image_horizontal_cross_item31, R.id.image_horizontal_cross_item32, R.id.image_horizontal_cross_item33, R.id.image_horizontal_cross_item34, R.id.image_horizontal_cross_item35, R.id.image_horizontal_cross_item36, R.id.image_horizontal_cross_item37, R.id.image_horizontal_cross_item38, R.id.image_horizontal_cross_item39, R.id.image_horizontal_cross_item40,
            R.id.image_horizontal_cross_item41, R.id.image_horizontal_cross_item42, R.id.image_horizontal_cross_item43, R.id.image_horizontal_cross_item44, R.id.image_horizontal_cross_item45, R.id.image_horizontal_cross_item46, R.id.image_horizontal_cross_item47, R.id.image_horizontal_cross_item48, R.id.image_horizontal_cross_item49, R.id.image_horizontal_cross_item50,
            R.id.image_horizontal_cross_item51, R.id.image_horizontal_cross_item52, R.id.image_horizontal_cross_item53, R.id.image_horizontal_cross_item54, R.id.image_horizontal_cross_item55, R.id.image_horizontal_cross_item56, R.id.image_horizontal_cross_item57, R.id.image_horizontal_cross_item58, R.id.image_horizontal_cross_item59, R.id.image_horizontal_cross_item60,
            R.id.image_horizontal_cross_item61, R.id.image_horizontal_cross_item62, R.id.image_horizontal_cross_item63, R.id.image_horizontal_cross_item64};

    private static final String TAG = "MainActivityPlayModel";
//    private int errTime = 0;

    private Handler mAnimatorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (imageLinearLayout.getChildAt(1) != null) {
                        imageLinearLayout.removeViewAt(1);
                    }
                    //Log.e(TAG, "100 :handleMessage: " + startDetector());
                    break;
            }
            return false;
        }
    });

    private Handler mHandler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public boolean handleMessage(Message msg) {
            Log.e(TAG, "handleMessage" + msg.what);
            mHandler.removeMessages(PLAY_VIDEO_MESSAGE);
            mHandler.removeMessages(PLAY_NEXT_MESSAGE);
            switch (msg.what) {
                case PLAY_NEXT_MESSAGE:
                    playNext(1);
                    break;
                case PLAY_VIDEO_MESSAGE:
                    if (isSurfaceViewCreated) {
                        mHandler.removeMessages(PLAY_VIDEO_MESSAGE);
                        if (((MainActivity) mContext).isOnResume())
                            mediaPlayerImp.play(selectedFileList.get(currentNum), false, false);
//                        errTime = 0;
                    } else {
                        Message message = new Message();
                        message.what = PLAY_VIDEO_MESSAGE;
                        mHandler.removeMessages(PLAY_VIDEO_MESSAGE);
                        mHandler.sendMessageDelayed(message, 1000);
                      /*  if (errTime < 15) {
                            errTime++;
                        } else {
                            Log.e(TAG, "handleMessage: errTime >15 -------------startPlay()" + startPlay());
                        }*/
                    }
                    break;
                case CHECK_EXTERNAL_SDCARD: {
                    mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
                    Log.e(TAG, "handleMessage: CHECK_EXTERNAL_SDCARD-------------startPlay1()" + startPlay());
                }
                break;
            }
            return false;
        }
    });

    public void setEVENT(boolean EVENT) {
        isEVENT = EVENT;
    }

    public void setPlayingBeacon(boolean playingBeacon) {
        isPlayingBeaconEvent = playingBeacon;
    }

    private class SDCardBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            Log.e(TAG, "onReceive: " + intent.getAction() + ",intent.getData() =" + intent.getData());
            isEVENT = false;
            isPlayingBeaconEvent = false;
            ((MyApplication) context.getApplicationContext()).initFilePath();
            boolean isNotExistUSBStorageSDCard = FileUtils.getSize(Config.USB_STORAGE_ROOT_PATH) == 0;
            Log.e(TAG, "onReceive: " + ",isNotExistUSBStorageSDCard = " + isNotExistUSBStorageSDCard);
            if (intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")) {
                if (intent.getData().toString().contains("usb")) {
                    isNotExistUSBStorageSDCard = true;
                }
            } else if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
                if (intent.getData().toString().contains("usb")) {
                    isNotExistUSBStorageSDCard = false;
                    ((MyApplication) context.getApplicationContext()).initUSBPath(intent.getData().getPath());
                } else {
                    isNotExistUSBStorageSDCard = true;
                    mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
                    mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 2000);
                }
            }
            Log.e(TAG, "onReceive: " + ",isNotExistUSBStorageSDCard = " + isNotExistUSBStorageSDCard);
            /*if (isNotExistUSBStorageSDCard) {
                isMotionDetector = SPUtils.getInt(mContext, Config.CHECK_FACE_STATE) == 1 || SPUtils.getInt(mContext, Config.CHECK_FACE_STATE) == 2;
                if (isMotionDetector) {
                    Log.e(TAG, "158 : " + startDetector(SPUtils.getInt(mContext, Config.ECO_MODE_STATE)));
                }
            }*/
            if (intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")) {
                if (isNotExistUSBStorageSDCard) {
                    close();
                    mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
                    mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 2000);
                }
            } else if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
                if (isNotExistUSBStorageSDCard) {
                    close();
                    mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
                    mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 3000);
                }
            }
            if (SystemClock.elapsedRealtime() < 20000)
                return;

         /*   mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
                        mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD,2000);*/
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
                        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0) {
                            if (FileUtils.checkHaveGivenFile(external_impacttv_path, Config.SCHEDULE_FILE_NAME)) {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
                            } else {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                            }
                            if (FileUtils.checkHaveGivenFile(external_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
                            } else {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
                            }
                            if (FileUtils.checkHaveGivenFile(external_event_path, Config.SCHEDULE_FILE_NAME)) {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_SCHEDULE);
                            } else {
                                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ALL_FILE);
                            }
                        }
//                        mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
//                        mHandler.sendEmptyMessage(CHECK_EXTERNAL_SDCARD);
                    }
                }
            }, 1000);
        }
    }

    private static SDCardBroadCast sdCardBroadCast;
    public static boolean isBGMOn = false;
    private boolean isSurfaceViewCreated = false;
    private SurfaceView mSurfaceView;
    private RelativeLayout imageLinearLayout;
    private int currentNum = -1;
    private Context mContext;
    private MediaPlayerImp mediaPlayerImp;
    private BGMPlayer mBGMPlayer;
    private List<String> selectedFileList = new ArrayList<>();
    private int animationTime;
    private static boolean isMotionDetector;
    private boolean isECO;
    public static boolean isEVENT = false;
    public static boolean isButtonKey = false; //表示当前这次event是否按外接按键触发的，如果是，则播一个文件就退回impacttv
    public static int eventNo = 0;  //记录event内播放第几个文件

    private Handler mMainActivityHandler;

    public MainActivityPlayModel(Context mContext, Handler handler) {
        Log.e(TAG, "MainActivityPlayModel: ");
        this.mContext = mContext;
        this.mMainActivityHandler = handler;
        init();
    }

    private LayoutInflater layoutInflater;

    private void init() {
        if (sdCardBroadCast == null) {
            sdCardBroadCast = new SDCardBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            mContext.registerReceiver(sdCardBroadCast, filter);
        }
        layoutInflater = LayoutInflater.from(mContext);
        mSurfaceView = (SurfaceView) ((Activity) mContext).findViewById(R.id.surfaceView_main_activity);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isSurfaceViewCreated = true;
              /*  Paint paint = new Paint();
                paint.setColor(ContextCompat.getColor(mContext, R.color.black));
                Canvas canvas = holder.lockCanvas();
                canvas.drawRect(0, 0, ScreenWidth, ScreenHeight, paint);//the first method 绘制一个和手机屏幕一样大小的矩形
                canvas.drawColor(Color.BLACK);//the second methos绘制颜色填充整个屏幕
                canvas.drawRGB(0, 0, 0);//the third method 绘制颜色填充整个屏幕
                holder.unlockCanvasAndPost(canvas);*/
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceViewCreated = false;
            }
        });
        imageLinearLayout = (RelativeLayout) ((Activity) mContext).findViewById(R.id.image_contain_layout);
        mediaPlayerImp = new MediaPlayerImp(mSurfaceView, mContext);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewHeight = dm.heightPixels;
        int mSurfaceViewWidth = dm.widthPixels;
        mediaPlayerImp.setScreenHeightAndWight(mSurfaceViewHeight, mSurfaceViewWidth);
        mediaPlayerImp.setOnCompletionListener(this);
        mediaPlayerImp.setOnErrorListener(this);
        mediaPlayerImp.setOnFileErrorListener(this);
        updateSelectedList();
        animationTime = SPUtils.getInt(mContext, Config.IMAGE_TIME);
        if (animationTime == -1) {
            animationTime = 5;
        }
    }

    public String startPlay() {
//        errTime = 0;
        animationTime = SPUtils.getInt(mContext, Config.IMAGE_TIME);
        if (animationTime == -1) {
            animationTime = 5;
        }
        isMotionDetector = SPUtils.getInt(mContext, Config.CHECK_FACE_STATE) == 1 || SPUtils.getInt(mContext, Config.CHECK_FACE_STATE) == 2;
        isECO = SPUtils.getInt(mContext, Config.ECO_MODE_STATE) >= 0;
        Log.e(TAG, "startPlay:  isEco =" + SPUtils.getInt(mContext, Config.ECO_MODE_STATE));
        boolean isExistSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;

     /*   if (isPlayingBeaconEvent) {//判断如果beacon不可播情况下重新检测
            long nowTime = System.currentTimeMillis();
            ScheduleBean tempScheduleBean = null;
            String path;
            if (isExistSDCard) {
                path = external_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME;
            } else {
                path = internal_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME;
            }
            BeaconBean beaconBean = null;
            ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(path);
            for (BeaconBean item : beaconBeans) {
                if (item.getBeaconNo() == beaconTagNo)
                    beaconBean = item;
            }
            if (beaconBean == null) {
                isPlayingBeaconEvent = false;
                if (isMotionDetector) {
                    isStartMotionCheck = true;
                }
                mHandler.sendEmptyMessage(CHECK_EXTERNAL_SDCARD);
                return "----------------startPlay: beaconBean is null";
            }
            for (ScheduleBean scheduleBean : beaconBean.getScheduleBeans()) {
                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                    tempScheduleBean = scheduleBean;
                    break;
                }
            }
            if (tempScheduleBean == null) {//impactv schedule 不可播
                isPlayingBeaconEvent = false;
                if (isMotionDetector) {
                    isStartMotionCheck = true;
                }
                mHandler.sendEmptyMessage(CHECK_EXTERNAL_SDCARD);
                return "----------------startPlay: isPlayingBeaconEvent tempScheduleBean state is close";
            }
        }*/

        if (!isEVENT) {//为了预防播放完event之后继续播放impactv文件（即使impactv不可播）；
            if (isECO && isMotionDetector) {//如果非isEvent则检测是否时eco模式
                mMainActivityHandler.sendEmptyMessage(1111);
                return "----------------startPlay: eco state is open";
            }
            int playModel;
            if (isExistSDCard) {
                playModel = SPUtils.getInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV);
            } else {
                playModel = SPUtils.getInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV);
            }
            if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
                long nowTime = System.currentTimeMillis();
                ScheduleBean tempScheduleBean = null;
                String path;
                if (isExistSDCard) {
                    if (!FileUtils.checkHaveFile(external_impactv_path)) {
                        path = external_impacttv_path + File.separator + Config.SCHEDULE_FILE_NAME;
                    } else {
                        path = external_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME;
                    }
                } else {
                    path = internal_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME;
                }
                for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(path)) {
                    long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                    long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                    if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                        tempScheduleBean = scheduleBean;
                        break;
                    }
                }
                if (tempScheduleBean == null) {//impactv schedule 不可播
                    mMainActivityHandler.sendEmptyMessage(1111);
                    return "----------------startPlay: tempScheduleBean state is close";
                }
            }
        } else {//在event不可播的时候继续原来的播放
            List<String> eventFiles;
            if (isExistSDCard) {
                eventFiles = getExternalEventFileList();
            } else {
                eventFiles = getInternalEventFileList();
            }
            if (eventFiles.size() == 0) {
                isEVENT = false;
                return "----------------startPlay:event is null";
            }
            int playModel;
            if (isExistSDCard) {
                playModel = SPUtils.getInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT);
            } else {
                playModel = SPUtils.getInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT);
            }
            if (playModel == Config.PLAY_BACK_MODE_SCHEDULE) {
                long nowTime = System.currentTimeMillis();
                ScheduleBean tempScheduleBean = null;
                String path;
                if (isExistSDCard) {
                    path = external_event_path + File.separator + Config.SCHEDULE_FILE_NAME;
                } else {
                    path = internal_event_path + File.separator + Config.SCHEDULE_FILE_NAME;
                }
                for (ScheduleBean scheduleBean : ScheduleParse.parseImpactvTXT(path)) {
                    long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                    long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                    if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                        tempScheduleBean = scheduleBean;
                        break;
                    }
                }
                if (tempScheduleBean == null) {
                    isEVENT = false;
                    return "----------------startPlay: tempScheduleBean state is close";
                }
            }
        }
        close();
        updateSelectedList();
        if (isBGMOn) {
            playBGM();
        }
        currentNum = -1;
        if (isEVENT) {
            currentNum = eventNo;
        }
        playNext(0);
        return "----------------startPlay:开始播放";
    }

    private void playBGM() {
        if (mBGMPlayer != null) {
            mBGMPlayer.close();
            mBGMPlayer = null;
        }
        mBGMPlayer = BGMPlayer.getInstance(getBGMFile(), mContext);
        mBGMPlayer.startPlay();
    }

    private List<String> getBGMFile() {
        ArrayList<String> bgmList = new ArrayList<>();
        boolean isExistSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
        if (isEVENT) {
            if (isExistSDCard) {
                if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(bgmList, strings);
                        bgmList.remove("");
                    }
                }
            } else {
                if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(bgmList, strings);
                        bgmList.remove("");
                    }
                }
            }
        } else {
            if (isExistSDCard) {
                if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(bgmList, strings);
                        bgmList.remove("");
                    }
                }
            } else {
                if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(bgmList, strings);
                        bgmList.remove("");
                    }
                }
            }
        }
        return bgmList;
    }

    private boolean canSetRestartTime = false;


    public static long reStartTime;

    private void playNext(int is_auto_play) {
        System.gc();
        currentNum++;
        if (selectedFileList.size() <= 0) {
            showNoSelectedFile();
            return;
        } else {
            ((Activity) mContext).findViewById(R.id.no_selected_file_layout).setVisibility(View.GONE);
        }
        Log.i(TAG, "playNext " + currentNum + " " + selectedFileList.size());
        if (currentNum >= selectedFileList.size()) {
            currentNum = 0;
            if ((is_auto_play == 1) && (isEVENT)) {
                isStartMotionCheck = true;
                canSetRestartTime = true;
                isEVENT = false;
                isButtonKey = false;
                Log.e(TAG, "play 1 event video mode --------------startPlay()" + startPlay());
                return;
            }
            if (isPlayingBeaconEvent) {
                isPlayingBeaconEvent = false;
                FileUtils.removePhotoToTarget(beaconTagNo);
                Log.e(TAG, "currentNum >= selectedFileList.size() --------------startPlay()" + startPlay());
                return;
            }
            if (isMotionDetector) {
                isStartMotionCheck = true;
                if (isECO) {
                    isCanTimeStart = false;
                    mMainActivityHandler.sendEmptyMessage(1111);
                    isEVENT = false;
                    if (canSetRestartTime) {
                        reStartTime = System.currentTimeMillis();
                        canSetRestartTime = false;
                    }
                    Log.i(TAG, "playNext ececo ");
                    return;
                }
                if (isEVENT) {
                    canSetRestartTime = true;
                    isEVENT = false;
                    Log.e(TAG, "currentNum >= selectedFileList.size() --------------startPlay()" + startPlay());
                    return;
                }
            }
        } else {
            if ((is_auto_play == 1) && (isEVENT) && (isButtonKey))  //如果是外部按键触发的event播放，播完一个文件就退出
            {
                isStartMotionCheck = true;
                canSetRestartTime = true;
                isEVENT = false;
                isButtonKey = false;
                Log.e(TAG, "play 1 event video mode --------------startPlay()" + startPlay());
                return;
            }
        }
        Log.i(TAG, "playNext exexex ");

        String path = selectedFileList.get(currentNum);

        if (isPlayingBeaconEvent) {
            //保存log
            String tower = TimeUtil.getCurrentFormatDate() + "," + TimeUtil.getCurrentFormatTimeOnly() + ",Beacon," + path + "\n";
            Log.e(TAG, "handleMessage: " + tower);
            FileUtils.AppendTxtFile(FileUtils.getLogPath(), tower);
        }
        if (isVideo(path)) {
            playVideo();
        } else if (isPhoto(path)) {
            playPhoto();
        }
    }

    private void showNoSelectedFile() {
        ((Activity) mContext).findViewById(R.id.no_selected_file_layout).setVisibility(View.VISIBLE);
        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0) {
            ((TextView) ((Activity) mContext).findViewById(R.id.tv_no_selected_file)).setText(mContext.getString(R.string.information_of_external_no_file));
        } else {
            ((TextView) ((Activity) mContext).findViewById(R.id.tv_no_selected_file)).setText(mContext.getString(R.string.information_of_internal_no_file));
        }
        mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
        mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 3000);
        Log.e(TAG, "-----------showNoSelectedFile: ");
        isEVENT = false;
        isPlayingBeaconEvent = false;
    }

    public void startPlay(boolean isEvent, boolean isButton) {
        isEVENT = isEvent;
        isButtonKey = isButton;
        Log.e(TAG, "移动检测----------- " + startPlay());
    }

    public void startPlayBeacon() {
//        if (!checkBeaconAct())
//            return;
        isPlayingBeaconEvent = true;
        isEVENT = false;
        Log.e(TAG, "Beacon Play---------- " + startPlay());
    }

    private boolean checkBeaconAct() {
        long nowTime = System.currentTimeMillis();
        ScheduleBean tempScheduleBean = null;
        String path;
        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0) {
            path = external_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME;
        } else {
            path = internal_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME;
        }
        BeaconBean beaconBean = null;
        ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(path);
        for (BeaconBean item : beaconBeans) {
            if (item.getBeaconNo() == beaconTagNo)
                beaconBean = item;
        }
        if (beaconBean == null) {
            return false;
        }
        for (ScheduleBean scheduleBean : beaconBean.getScheduleBeans()) {
            long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
            long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
            if (nowTime >= days[0] && nowTime <= days[1] && nowTime >= times[0] && nowTime <= times[1]) {
                tempScheduleBean = scheduleBean;
                break;
            }
        }
        if (tempScheduleBean == null) {//impactv schedule 不可播
            return false;
        }
        return true;
    }


    private void playPhoto() {
        if (imageLinearLayout.getVisibility() != View.VISIBLE) {
            imageLinearLayout.setVisibility(View.VISIBLE);
            imageLinearLayout.removeAllViews();
        }
        if (mSurfaceView.getVisibility() != View.GONE) {
            mSurfaceView.setVisibility(View.GONE);
        }

        //Log.e(TAG, "playPhoto: imageLinearLayout.getChildCount() = " + imageLinearLayout.getChildCount());

        Bitmap bitmap = null;//BitmapFactory.decodeFile(selectedFileList.get(currentNum));
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(selectedFileList.get(currentNum)));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException | OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (SPUtils.getInt(mContext, Config.IMAGE_DIRECTION)) {
            case Config.IMAGE_DIRECTION_LEFTTORIGHT:
                leftToRightAnimator(bitmap);
                break;
            case Config.IMAGE_DIRECTION_UPTODOWN:
                upToDownAnimator(bitmap);
                break;
            case Config.IMAGE_DIRECTION_RANDOM:
                randomAnimator(bitmap);
                break;
            case Config.IMAGE_DIRECTION_HORIZONTALCROSS:
                horizontalCrossAnimator(bitmap);
                break;
            default:
                normalAnimator(bitmap);
                break;
        }
        mHandler.removeMessages(PLAY_NEXT_MESSAGE);
        mHandler.sendEmptyMessageDelayed(PLAY_NEXT_MESSAGE, animationTime * 1000);
    }

    /*动画效果*/
    private boolean isHorizontalCrossAnimator = false;

    private void randomAnimator(Bitmap bitmap) {
        Random random = new Random();
        int randomInt = random.nextInt(5);
        if (isHorizontalCrossAnimator) {
            randomInt = 1;
        }
        switch (randomInt) {
            case 0:
                normalAnimator(bitmap);
                break;
            case 1:
                horizontalCrossAnimator(bitmap);
                break;
            case 2:
                upToDownAnimator(bitmap);
                break;
            case 3:
                leftToRightAnimator(bitmap);
                break;
            case 4:
                scaleAnimator(bitmap);
                break;
        }
    }

    private void scaleAnimator(Bitmap bitmap) {
        final View view = imageLinearLayout.getChildAt(0);
        if (SPUtils.getString(mContext, Config.DISPLAY_RATIO).equals("full")) {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, true);
        } else {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, false);
        }
        if (view == null) {
            Log.e(TAG, "leftToRight: addView");
            ImageView imageView1 = new ImageView(mContext);
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView1.setImageBitmap(bitmap);
            imageLinearLayout.addView(imageView1);
        } else {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0f);
            ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
            AnimatorSet animSet = new AnimatorSet().setDuration(1000);
            //   animSet.setInterpolator(new LinearInterpolator());
            animSet.playTogether(anim1, anim2, anim3);
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageLinearLayout.removeView(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animSet.start();
            ImageView imageView2 = new ImageView(mContext);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView2.setImageBitmap(bitmap);
            imageLinearLayout.addView(imageView2);
            ObjectAnimator anim11 = ObjectAnimator.ofFloat(imageView2, "scaleX", 0.0f, 1.0f);
            ObjectAnimator anim12 = ObjectAnimator.ofFloat(imageView2, "scaleY", 0.0f, 1.0f);
            ObjectAnimator anim13 = ObjectAnimator.ofFloat(imageView2, "alpha", 0.0f, 1.0f);
            AnimatorSet animatorSet1 = new AnimatorSet().setDuration(1000);
            animatorSet1.playTogether(anim12, anim11, anim13);
            animatorSet1.start();
        }
    }

    private void horizontalCrossAnimator(Bitmap bitmap) {
        final View view = imageLinearLayout.getChildAt(0);
        if (SPUtils.getString(mContext, Config.DISPLAY_RATIO).equals("full")) {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, true);
        } else {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, false);
        }

        ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>) BitmapUtil.splitBitmap(bitmap);
        if (view == null) {
            View contentView = layoutInflater.inflate(R.layout.horizontal_corss_animator_layout, null);
            for (int i = 0; i < 64; i++) {
                ImageView imageView = ((ImageView) contentView.findViewById(horizontalItemId[i]));
                imageView.setImageBitmap(bitmaps.get(i));
            }
            contentView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageLinearLayout.addView(contentView);
        } else {
            List<ObjectAnimator> objectAnimators = new ArrayList<>();
            isHorizontalCrossAnimator = false;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ImageView imageView = ((ImageView) view.findViewById(horizontalItemId[i * 8 + j]));
                    if (imageView == null) {
                        View contentView = layoutInflater.inflate(R.layout.horizontal_corss_animator_layout, null);
                        for (int k = 0; k < 64; k++) {
                            ImageView imageView1 = ((ImageView) contentView.findViewById(horizontalItemId[k]));
                            imageView1.setImageBitmap(bitmaps.get(k));
                        }
                        contentView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        imageLinearLayout.addView(contentView);
                        imageLinearLayout.removeViewAt(0);
                        isHorizontalCrossAnimator = true;
                        return;
                    }
                    ObjectAnimator anim;
                    if (i % 2 == 0) {
                        imageView.setPivotX(0);
                        anim = ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f, 0.0f).setDuration(1000);
                    } else {
                        imageView.setPivotX(ScreenWidth / 8);
                        anim = ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f, 0.0f).setDuration(1000);
                    }
                    objectAnimators.add(anim);
                }
            }
            View contentView = layoutInflater.inflate(R.layout.horizontal_corss_animator_layout, null);
            for (int i = 0; i < 64; i++) {
                ImageView imageView = ((ImageView) contentView.findViewById(horizontalItemId[i]));
                imageView.setImageBitmap(bitmaps.get(i));
            }
            imageLinearLayout.addView(contentView, 0);
            mAnimatorHandler.sendEmptyMessageDelayed(1, 980);
            for (int i = 0; i < 64; i++) {
                objectAnimators.get(i).start();
            }
        }
    }

    private void upToDownAnimator(Bitmap bitmap) {
        final View view = imageLinearLayout.getChildAt(0);
        if (SPUtils.getString(mContext, Config.DISPLAY_RATIO).equals("full")) {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, true);
        } else {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, false);
        }
        if (view == null) {
            ImageView imageView1 = new ImageView(mContext);
            imageView1.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setImageBitmap(bitmap);
            imageLinearLayout.addView(imageView1);
            ObjectAnimator anim = ObjectAnimator.ofFloat(imageView1, "translationY", -1.0f * ScreenHeight, 0.0f).setDuration(1000);
            anim.addListener(startDetectorAnimatorListener);
            anim.start();
        } else {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY", 0.0f, 1.0f * ScreenHeight).setDuration(1000);
            final Bitmap finalBitmap = bitmap;
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    final ImageView imageView2 = new ImageView(mContext);
                    imageView2.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView2.setImageBitmap(finalBitmap);
                    imageLinearLayout.addView(imageView2);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView2, "translationY", -1.0f * ScreenHeight, 0.0f).setDuration(1000);
                    anim2.start();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageLinearLayout.removeView(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim1.start();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void leftToRightAnimator(Bitmap bitmap) {
        final View view = imageLinearLayout.getChildAt(0);
        if (SPUtils.getString(mContext, Config.DISPLAY_RATIO).equals("full")) {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, true);
        } else {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, false);
        }
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (view == null) {
            ImageView imageView1 = new ImageView(mContext);
            imageView1.setImageBitmap(bitmap);
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageLinearLayout.addView(imageView1);
            imageView1.setLayoutParams(lp);
            ObjectAnimator anim = ObjectAnimator.ofFloat(imageView1, "translationX", -1.0f * ScreenWidth, 0.0f).setDuration(1000);
            anim.addListener(startDetectorAnimatorListener);
            anim.start();
        } else {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationX", 0.0f, 1.0f * ScreenWidth).setDuration(1000);
            final Bitmap finalBitmap = bitmap;
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    final ImageView imageView2 = new ImageView(mContext);
                    imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView2.setImageBitmap(finalBitmap);
                    imageLinearLayout.addView(imageView2);
                    imageView2.setLayoutParams(lp);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView2, "translationX", -1.0f * ScreenWidth, 0.0f).setDuration(1000);
                    anim2.start();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageLinearLayout.removeView(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim1.start();
        }
    }

    private Animator.AnimatorListener startDetectorAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.e(TAG, "onAnimationEnd: ");
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void normalAnimator(Bitmap bitmap) {
        View view = imageLinearLayout.getChildAt(0);
        if (SPUtils.getString(mContext, Config.DISPLAY_RATIO).equals("full")) {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, true);
        } else {
            bitmap = BitmapUtil.full_screen_bitmap(bitmap, false);
        }
        if (view == null) {
            ImageView imageView1 = new ImageView(mContext);
            imageView1.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setImageBitmap(bitmap);
            imageLinearLayout.addView(imageView1);
        } else {
            if (view.findViewById(R.id.image_horizontal_cross_item1) != null) {
                ImageView imageView2 = new ImageView(mContext);
                imageView2.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView2.setImageBitmap(bitmap);
                imageLinearLayout.addView(imageView2);
                imageLinearLayout.removeViewAt(0);
            } else {
                ((ImageView) view).setImageBitmap(bitmap);
            }
        }
    }
    /*动画效果*/

    private void playVideo() {
        if (imageLinearLayout.getVisibility() != View.GONE) {
            imageLinearLayout.removeAllViews();
            imageLinearLayout.setVisibility(View.GONE);
        }
        if (mSurfaceView.getVisibility() != View.VISIBLE) {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        mHandler.sendEmptyMessage(PLAY_VIDEO_MESSAGE);
    }

    public void close() {
        mHandler.removeMessages(PLAY_NEXT_MESSAGE);
        mHandler.removeMessages(PLAY_VIDEO_MESSAGE);
        mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
//        errTime = 0;
        mediaPlayerImp.close();
        if (mBGMPlayer != null) {
            mBGMPlayer.close();
            mBGMPlayer = null;
            isBGMOn = false;
        }
        ((Activity) mContext).findViewById(R.id.no_selected_file_layout).setVisibility(View.GONE);
        imageLinearLayout.removeAllViews();
        imageLinearLayout.setVisibility(View.GONE);

        mSurfaceView.setVisibility(View.GONE);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mHandler.sendEmptyMessage(PLAY_NEXT_MESSAGE);
    }

    public void onPause() {
        if (sdCardBroadCast != null) {
            try {
                mContext.unregisterReceiver(sdCardBroadCast);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            sdCardBroadCast = null;
        }
    }

    public void onResume() {
        if (sdCardBroadCast == null) {
            sdCardBroadCast = new SDCardBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            mContext.registerReceiver(sdCardBroadCast, filter);
        }
    }

    public void onDestroy() {
        if (sdCardBroadCast != null) {
            try {
                mContext.unregisterReceiver(sdCardBroadCast);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            sdCardBroadCast = null;
        }
    }

    public void startToOSD() {
        mHandler.removeMessages(PLAY_NEXT_MESSAGE);
        mHandler.removeMessages(PLAY_VIDEO_MESSAGE);
//        ((Activity) mContext).finish();
        Intent intent = new Intent(mContext, OSDSettingActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        close();
        Log.e(TAG, "onError: ");
        mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
        mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 3000);
        LinearLayout linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.no_selected_file_layout);
        if (linearLayout.getVisibility() == View.GONE) {
            linearLayout.setVisibility(View.VISIBLE);
        }
        ((TextView) ((Activity) mContext).findViewById(R.id.tv_no_selected_file)).setText(mContext.getString(R.string.mediaplayer_onFileError));
        return false;
    }

    @Override
    public void onFileError() {
        Log.e(TAG, "onFileError: ");
        mHandler.removeMessages(CHECK_EXTERNAL_SDCARD);
        mHandler.sendEmptyMessageDelayed(CHECK_EXTERNAL_SDCARD, 3000);
        LinearLayout linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.no_selected_file_layout);
        if (linearLayout.getVisibility() == View.GONE) {
            linearLayout.setVisibility(View.VISIBLE);
        }
        ((TextView) ((Activity) mContext).findViewById(R.id.tv_no_selected_file)).setText(mContext.getString(R.string.mediaplayer_onFileError));
    }

    public boolean isPlaying() {
        if (mediaPlayerImp != null) {
            if (mediaPlayerImp.isPlaying() || imageLinearLayout.getVisibility() == View.VISIBLE || mSurfaceView.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return false;
    }

    private void updateSelectedList() {
        Log.e(TAG, "updateSelectedList: isPlayingBeaconEvent = " + isPlayingBeaconEvent + ",beaconTagNo = " + beaconTagNo);
        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0) {
            if (isPlayingBeaconEvent) {
                if (beaconTagNo == Config.BEACON_TAG_PERSION) {
                    selectedFileList = getExternalWashingFileList();
                } else if (beaconTagNo == Config.BEACON_TAG_NO_PERSION) {
                    selectedFileList = getExternalWarningFileList();
                }
                return;
            }
            selectedFileList = getExternalImpactvFileList();
        } else {
            if (isPlayingBeaconEvent) {
                if (beaconTagNo == Config.BEACON_TAG_PERSION) {
                    selectedFileList = getInternalWashingFileList();
                } else if (beaconTagNo == Config.BEACON_TAG_NO_PERSION) {
                    selectedFileList = getInternalWarningFileList();
                }
                return;
            }
            selectedFileList = getInternalImpactvFileList();
        }
        Log.e(TAG, "updateSelectedList: selectedFileList = " + selectedFileList.size());
    }
}
