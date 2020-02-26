package com.example.hu.mediaplayerapk.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.bean.BeaconBean;
import com.example.hu.mediaplayerapk.bean.ScheduleBean;
import com.example.hu.mediaplayerapk.bean.WorkTimer;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.ui.activity.MainActivity;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;
import com.example.hu.mediaplayerapk.util.ScheduleParse;
import com.example.hu.mediaplayerapk.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.hu.mediaplayerapk.application.MyApplication.external_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.model.MainActivityPlayModel.isEVENT;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.beaconTagNo;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.isPlayingBeaconEvent;
import static com.example.hu.mediaplayerapk.util.WorkTimeUtil.getHolidays;
import static com.example.hu.mediaplayerapk.util.WorkTimeUtil.getWorkTimers;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class WorkTimerService extends Service {
    private Handler mHandler;
    private ArrayList<Integer> holidays;
    private ArrayList<WorkTimer> workTimers;
    private static final String TAG = "WorkTimerService";
    private boolean isSchedule = false;
    private ArrayList<ScheduleBean> scheduleBeanArrayList = null;
    private ScheduleBean lastImpacttvSchedule = null; //记录上次的schedule序号，如果中间发生变化，则需要更新
    private ScheduleBean lastEventSchedule = null; //记录上次的schedule序号，如果中间发生变化，则需要更新
    private ScheduleBean lastBeaconSchedule = null; //记录上次的schedule序号，如果中间发生变化，则需要更新

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return new WorkTimerBinder();
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
        loop = true;
        holidays = getHolidays();
        workTimers = (ArrayList<WorkTimer>) getWorkTimers();
        if (holidays == null) {
            holidays = new ArrayList<>();
            Log.e(TAG, "holidays = null");
        }
        if (workTimers == null)
            workTimers = new ArrayList<>();
    }

    private boolean loop = true;

    public void startCheckTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "StartCheckTimer\n\n\n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //isSchedule = false;
                //getScheduleState();
                if (lastImpacttvSchedule != null) {
                    lastImpacttvSchedule = null;
                }
                if (lastEventSchedule != null) {
                    lastEventSchedule = null;
                }
                if (lastBeaconSchedule != null) {
                    lastBeaconSchedule = null;
                }
                while (loop) {
                    checkWorkTimer();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (loop) {
                        checkRestart();
                    }
                }
            }
        }).start();
    }

    public class WorkTimerBinder extends Binder {
        public WorkTimerService getWorkTimerService() {
            return WorkTimerService.this;
        }
    }

    private void checkRestart() {
        if (SPUtils.getInt(this, Config.RESET_ON) != 0) {
            int hour = SPUtils.getInt(this, Config.RESET_HOUR);
            if (hour == -1) {
                hour = 3;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long sub = System.currentTimeMillis() - calendar.getTimeInMillis();
            if (Math.abs(sub) <= 5 * 1000) {
                Intent restartIntent = new Intent("android.intent.action.MY_REBOOT");
                sendBroadcast(restartIntent);
            }
        }
    }

    private void getScheduleState() {
        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0) {
            Log.d(TAG, "FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0" + isEVENT);
            if (MainActivity.isPlayingBeaconEvent) {
                isSchedule = true;
                int beaconNum = beaconTagNo;
                ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(
                        external_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME);
                BeaconBean beaconBean = null;
                for (BeaconBean item : beaconBeans) {
                    if (item.getBeaconNo() == beaconNum)
                        beaconBean = item;
                }
                scheduleBeanArrayList = beaconBean.getScheduleBeans();
                return;
            }
            if (isEVENT) {
                if (SPUtils.getInt(this, Config.EXTERNAL_PLAY_BACK_MODE_EVENT) == Config.PLAY_BACK_MODE_SCHEDULE) {
                    Log.d(TAG, "Event SD schedule=true");
                    isSchedule = true;
                    scheduleBeanArrayList = ScheduleParse.parseImpactvTXT(external_event_path + File.separator + Config.SCHEDULE_FILE_NAME);
                }
            } else {
                if (SPUtils.getInt(this, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV) == Config.PLAY_BACK_MODE_SCHEDULE) {
                    isSchedule = true;
                    Log.d(TAG, "Impact SD schedule=true");
                    scheduleBeanArrayList = ScheduleParse.parseImpactvTXT(external_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME);
                }
            }
        } else {
            Log.d(TAG, "FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) = 0 " + isEVENT);
            if (MainActivity.isPlayingBeaconEvent) {
                isSchedule = true;
                ArrayList<BeaconBean> beaconBeans = ScheduleParse.parse_BEACON_Schedule_TXT(
                        internal_beacon_path + File.separator + Config.BEACON_SCHEDULE_FILE_NAME);
                BeaconBean beaconBean = null;
                for (BeaconBean item : beaconBeans) {
                    if (item.getBeaconNo() == beaconTagNo)
                        beaconBean = item;
                }
                if (beaconBean != null) {
                    scheduleBeanArrayList = beaconBean.getScheduleBeans();
                }else {
//                    MainActivity.isPlayingBeaconEvent = false;
                    isSchedule = false;
                }
                return;
            }
            if (isEVENT) {
                if (SPUtils.getInt(this, Config.INTERNAL_PLAY_BACK_MODE_EVENT) == Config.PLAY_BACK_MODE_SCHEDULE) {
                    isSchedule = true;
                    Log.d(TAG, "Event Flash schedule=true");
                    scheduleBeanArrayList = ScheduleParse.parseImpactvTXT(internal_event_path + File.separator + Config.SCHEDULE_FILE_NAME);
                }
            } else {
                if (SPUtils.getInt(this, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV) == Config.PLAY_BACK_MODE_SCHEDULE) {
                    isSchedule = true;
                    Log.d(TAG, "Impacttv Flash schedule=true");
                    scheduleBeanArrayList = ScheduleParse.parseImpactvTXT(internal_impactv_path + File.separator + Config.SCHEDULE_FILE_NAME);
                }
            }
        }
    }

    private void checkWorkTimer() {
        isSchedule = false;
        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(System.currentTimeMillis());
        long nowTime = c.getTimeInMillis();
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        switch (weekday) {
            case 1:
                weekday = 7;
                break;
            case 2:
                weekday = 1;
                break;
            case 3:
                weekday = 2;
                break;
            case 4:
                weekday = 3;
                break;
            case 5:
                weekday = 4;
                break;
            case 6:
                weekday = 5;
                break;
            case 7:
                weekday = 6;
                break;
        }

        for (Integer holiday : holidays) {
            Log.e(TAG, "checkWorkTimer: holiday = " + holiday + ",weekday = " + weekday);
            if (weekday == holiday) {
                mHandler.sendEmptyMessage(1111);
                return;
            }
        }


        //if (isMotionDetector) {
        getScheduleState();
        //}
        if (workTimers.size() == 0) {
            if (isSchedule) {
                for (ScheduleBean scheduleBean : scheduleBeanArrayList) {
                    long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                    if (nowTime >= days[0] && nowTime <= days[1]) {
                        long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                        if (nowTime >= times[0] && nowTime <= times[1]) {
                            Log.d(TAG, "scheduleBean.No = " + scheduleBean.toString());
                            if (isEVENT) {
                                if (lastEventSchedule == null) {
                                    mHandler.sendEmptyMessage(2222);
                                    lastEventSchedule = scheduleBean;
                                } else {
                                    if (lastEventSchedule.getNum() != scheduleBean.getNum()) {
                                        mHandler.sendEmptyMessage(3333);
                                        lastEventSchedule = scheduleBean;
                                    }else {
                                        mHandler.sendEmptyMessage(2222);
                                    }
                                }
                            } else {
                                if (isPlayingBeaconEvent) {
                                    if (lastBeaconSchedule == null) {
                                        mHandler.sendEmptyMessage(2222);
                                        lastBeaconSchedule = scheduleBean;
                                    } else {
                                        if (lastBeaconSchedule.getNum() != scheduleBean.getNum()) {
                                            Log.e(TAG, "lastSchedule != scheduleBean\n\n\n");
                                            Log.d(TAG, "lastImpacttvSchedule = " + lastBeaconSchedule.toString());
                                            mHandler.sendEmptyMessage(3333);
                                            lastBeaconSchedule = scheduleBean;
                                        }else {
                                            mHandler.sendEmptyMessage(2222);
                                        }
                                    }
                                    return;
                                }
                                if (lastImpacttvSchedule == null) {
                                    mHandler.sendEmptyMessage(2222);
                                    lastImpacttvSchedule = scheduleBean;
                                } else {
                                    if (lastImpacttvSchedule.getNum() != scheduleBean.getNum()) {
                                        Log.e(TAG, "lastSchedule != scheduleBean\n\n\n");
                                        Log.d(TAG, "lastImpacttvSchedule = " + lastImpacttvSchedule.toString());
                                        mHandler.sendEmptyMessage(3333);
                                        lastImpacttvSchedule = scheduleBean;
                                    }else {
                                        mHandler.sendEmptyMessage(2222);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
                //无timer，有schedule，但是无可播放的，进入休眠
                mHandler.sendEmptyMessage(1111);
            } else {
                //无timer,无schedule，直接播放
                mHandler.sendEmptyMessage(2222);
            }
            return;
        }

        for (WorkTimer item : workTimers) {
            if (item.getDay() == weekday || item.getDay() == 0) {
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTimeInMillis(System.currentTimeMillis());
                startCalendar.set(Calendar.HOUR_OF_DAY, item.getStartHour());
                startCalendar.set(Calendar.MINUTE, item.getStartMinute());
                startCalendar.set(Calendar.SECOND, 0);
                long startTime = startCalendar.getTimeInMillis();

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTimeInMillis(System.currentTimeMillis());
                endCalendar.set(Calendar.HOUR_OF_DAY, item.getEndHour());
                endCalendar.set(Calendar.MINUTE, item.getEndMinute());
                endCalendar.set(Calendar.SECOND, 0);
                long endTime = endCalendar.getTimeInMillis();

                if (nowTime <= endTime && nowTime >= startTime) {
                    if (isSchedule) {
                        for (ScheduleBean scheduleBean : scheduleBeanArrayList) {
                            long[] days = TimeUtil.getDayFromSchedule(scheduleBean);
                            if (nowTime >= days[0] && nowTime <= days[1]) {
                                long[] times = TimeUtil.getTimeFromSchedule(scheduleBean);
                                if (nowTime >= times[0] && nowTime <= times[1]) {
                                    Log.d(TAG, "scheduleBean.2  = " + scheduleBean.toString());
                                    if (isEVENT) {
                                        if (lastEventSchedule == null) {
                                            mHandler.sendEmptyMessage(2222);
                                            lastEventSchedule = scheduleBean;
                                        } else {
                                            if (lastEventSchedule.getNum() != scheduleBean.getNum()) {
                                                Log.e(TAG, "lastSchedule != scheduleBean\n\n\n");
                                                mHandler.sendEmptyMessage(3333);
                                                lastEventSchedule = scheduleBean;
                                            }else {
                                                mHandler.sendEmptyMessage(2222);
                                            }
                                        }
                                    } else {
                                        if (isPlayingBeaconEvent) {
                                            if (lastBeaconSchedule == null) {
                                                mHandler.sendEmptyMessage(2222);
                                                lastBeaconSchedule = scheduleBean;
                                            } else {
                                                if (lastBeaconSchedule.getNum() != scheduleBean.getNum()) {
                                                    mHandler.sendEmptyMessage(3333);
                                                    lastBeaconSchedule = scheduleBean;
                                                }else {
                                                    mHandler.sendEmptyMessage(2222);
                                                }
                                            }
                                            return;
                                        }
                                        if (lastImpacttvSchedule == null) {
                                            mHandler.sendEmptyMessage(2222);
                                            lastImpacttvSchedule = scheduleBean;
                                        } else {
                                            if (lastImpacttvSchedule.getNum() != scheduleBean.getNum()) {
                                                Log.e(TAG, "lastSchedule != scheduleBean\n\n\n");
                                                mHandler.sendEmptyMessage(3333);
                                                lastImpacttvSchedule = scheduleBean;
                                            }else {
                                                mHandler.sendEmptyMessage(2222);
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "No schedule 2\n");
                        mHandler.sendEmptyMessage(2222);
                        return;
                    }
                }
            }
        }
        mHandler.sendEmptyMessage(1111);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        loop = false;
        isSchedule = false;
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        isSchedule = false;
        loop = false;
        mHandler.removeMessages(1111);
        mHandler.removeMessages(2222);
    }
}