package com.example.hu.mediaplayerapk.ui.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.broadcast.HumanReceive;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.model.MainActivityPlayModel;
import com.example.hu.mediaplayerapk.model.MainGestureListener;
import com.example.hu.mediaplayerapk.model.TouchModel;
import com.example.hu.mediaplayerapk.service.BluetoothService;
import com.example.hu.mediaplayerapk.service.MotionDetectorService;
import com.example.hu.mediaplayerapk.service.WorkTimerService;
import com.example.hu.mediaplayerapk.ui.popupWindow.VolumeAndLightPop;
import com.example.hu.mediaplayerapk.usb_copy.USBReceive;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;
import com.rockchip.Gpio;

import static com.example.hu.mediaplayerapk.model.MainActivityPlayModel.eventNo;
import static com.example.hu.mediaplayerapk.util.GoToHome.goToHome;
import static com.example.hu.mediaplayerapk.util.WorkTimeUtil.checkIsWorkTime;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends com.example.hu.mediaplayerapk.ui.activity.BaseActivity {


    private HumanReceive receiver = null;
    public static boolean isStartMotionCheck = false;
    public static boolean isPlayingBeaconEvent = false;  //是否在播放beacon的视频
    public static boolean isECO;
    public static int beaconTagNo;

    private void setScreenBrightnessOff() {
        Log.e(TAG, "setScreenBrightnessOff: ");
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0.0f;//Float.valueOf(0.0f);
        getWindow().setAttributes(lp);
    }

    private void setScreenBrightnessOn() {
        Log.e(TAG, "setScreenBrightnessOn: ");
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = getSystemBrightness() / 255.0f;
        getWindow().setAttributes(lp);
    }

    private int getSystemBrightness() {
        //获取当前亮度,获取失败则返回255
        return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
    }

    private static final String TAG = "MainActivity";
    private MainActivityPlayModel mainActivityPlayModel;
    public static boolean isCanTimeStart = true;   //如果eco==true 那么久不能够唤醒屏幕
    //public static int OPEN_OPENCV = 3333;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1111) {//1111表示熄灭
                setScreenBrightnessOff();
                try {
                    Gpio.setLedRed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mainActivityPlayModel != null) {
                    //  Log.e(TAG, "handleMessage: screenOff");
                    mainActivityPlayModel.close();
                }
            } else if (msg.what == 2222 && isCanTimeStart) {//2222表示亮
                setScreenBrightnessOn();
                try {
                    Gpio.setLedGreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mainActivityPlayModel != null && !mainActivityPlayModel.isPlaying()) {
                    //  Log.e(TAG, "handleMessage: screenOn");
                    Log.e(TAG, "handleMessage: ---------mainActivityPlayModel.startPlay()" + mainActivityPlayModel.startPlay());
                }
           /* } else if (msg.what == OPEN_OPENCV) {
                if (!OpenCVLoader.initDebug()) {
                    Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, MainActivity.this, mLoaderCallback);
                } else {
                    Log.d(TAG, "OpenCV library found inside package. Using it!");
                    mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                }*/
            } else if (msg.what == 3333 && isCanTimeStart) {//3333表示更新
                Log.e(TAG, "handleMessage: what == 3333");
                setScreenBrightnessOn();
                try {
                    Gpio.setLedGreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mainActivityPlayModel != null) {
                    //  Log.e(TAG, "handleMessage: screenOn");
                    Log.e(TAG, "handleMessage: ---------mainActivityPlayModel.startPlay()" + mainActivityPlayModel.startPlay());
                }
            }
            return false;
        }
    });

    private ServiceConnection workTimeServiceConnection = new WorkTimeServiceConnection();

    private MotionDetectorConnection motionDetectorConnection = new MotionDetectorConnection();

    private class WorkTimeServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.e(TAG, "onServiceConnected: ");
            WorkTimerService.WorkTimerBinder workTimerBinder = (WorkTimerService.WorkTimerBinder) binder;
            WorkTimerService mWorkTimerService = workTimerBinder.getWorkTimerService();
            mWorkTimerService.setHandler(mHandler);
            mWorkTimerService.startCheckTime();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: ");
        }
    }

    private MotionDetectorService motionDetectorService;  //人脸检测service用来检测人脸并保存图片

    private class MotionDetectorConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: ");
            motionDetectorService = ((MotionDetectorService.MotionDetectorServiceBinder) service).getMotionDetectorService();
          /*  if (motionDetectorService != null) {
                motionDetectorService.startDetect();
            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: ");
            motionDetectorService = null;
        }
    }

    private VolumeAndLightPop volumeAndLightPop;
    MainGestureListener mainGestureListener;
    GestureDetector mGestureDetector;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mainActivityPlayModel = new MainActivityPlayModel(this, mHandler);
        volumeAndLightPop = new VolumeAndLightPop(this, mainActivityPlayModel);
        mainGestureListener = new MainGestureListener(this, volumeAndLightPop);
        mGestureDetector = new GestureDetector(this, mainGestureListener);
        touchModel = new TouchModel(this);


        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BLUETOOTH_BROADCAST_NAME);
        registerReceiver(bluetoothActBroadcastReceiver, bluetoothFilter);
        ((MyApplication) this.getApplicationContext()).setOpen(true);  //

        // 生成一个BroiadcastReceiver对象
        receiver = new HumanReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(HumanReceive.MSG);
        registerReceiver(receiver, filter);
        receiver.setHumanSensorListener(new HumanReceive.HRInterface() {
            @Override
            public void humanSensorCbk() {
                //移动检测
                if (isStartMotionCheck) {
                    human_sensing_hook(-1, false);
                }
            }
        });
    }

    private TouchModel touchModel;
    private USBReceive usbBroadCastReceive;

    private boolean isOnResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity OnResume\n\n");
        openService();
        isOnResume = true;
        mainActivityPlayModel.onResume();

        receiver = new HumanReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(HumanReceive.MSG);
        registerReceiver(receiver, filter);
        receiver.setHumanSensorListener(new HumanReceive.HRInterface() {
            @Override
            public void humanSensorCbk() {
                //移动检测
                if (isStartMotionCheck) {
                    human_sensing_hook(-1, false);
                }
            }
        });
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BLUETOOTH_BROADCAST_NAME);
        registerReceiver(bluetoothActBroadcastReceiver, bluetoothFilter);
    }

    public boolean isOnResume() {
        return isOnResume;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "MainActivity onPause\n\n");
        unregisterReceiver(receiver);
        unregisterReceiver(bluetoothActBroadcastReceiver);
        //bluetoothActBroadcastReceiver = null;
        receiver = null;
        super.onPause();
        closeService();
        isOnResume = false;
    }

    Intent beaconIntent;

    private void openService() {
        isCanTimeStart = true;
        //注册U盘更新广播
        usbBroadCastReceive = new USBReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addDataScheme("file");
        registerReceiver(usbBroadCastReceive, filter);

        //检查是否为工作时间 检测是否重启
        Intent intent = new Intent(MainActivity.this, WorkTimerService.class);
        this.bindService(intent, workTimeServiceConnection, Context.BIND_AUTO_CREATE);

        beaconIntent = new Intent(MainActivity.this, BluetoothService.class);
        //打开beaconservice；
        if (SPUtils.getInt(MainActivity.this, Config.BEACON_MODE_STATE) < 0) {
            startService(beaconIntent);
        }

        //人脸检测
        Intent motionDetectionIntent = new Intent(MainActivity.this, MotionDetectorService.class);
        bindService(motionDetectionIntent, motionDetectorConnection, Context.BIND_AUTO_CREATE);

        if (SPUtils.getInt(this, Config.CHECK_FACE_STATE) == 1 || SPUtils.getInt(this, Config.CHECK_FACE_STATE) == 2) {
            /*if (!isBindMotion) {
                mHandler.sendEmptyMessage(OPEN_OPENCV);
            }*/
            //开启移动检测
            isStartMotionCheck = true;
            if (SPUtils.getInt(MainActivity.this, Config.ECO_MODE_STATE) >= 1) {
                isECO = true;
                isCanTimeStart = false;
                setScreenBrightnessOff();
                try {
                    Gpio.setLedRed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mainActivityPlayModel.setEVENT(false);
                mHandler.sendEmptyMessage(2222);
            }
        } else {
            //播放广告
            if (mainActivityPlayModel == null) {
                mainActivityPlayModel = new MainActivityPlayModel(this, mHandler);
            }
            mainActivityPlayModel.setEVENT(false);
            setScreenBrightnessOn();
            try {
                Gpio.setLedGreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "openService: ---------mainActivityPlayModel.startPlay()" + mainActivityPlayModel.startPlay());
        }
    }

    private void closeService() {
        Log.e(TAG, "closeService: ");
        //检测是否是工作时间，检测是否重启
        mainActivityPlayModel.onPause();
        unbindService(workTimeServiceConnection);

        //beaconservice；
        if (beaconIntent != null)
            stopService(beaconIntent);

        //u盘拷贝注销
        unregisterReceiver(usbBroadCastReceive);
        //关闭播放广告
        mainActivityPlayModel.close();
        mainActivityPlayModel.setEVENT(false);
        mainActivityPlayModel.setPlayingBeacon(false);
        if (motionDetectorConnection != null) {
            unbindService(motionDetectorConnection);
        }
        setScreenBrightnessOn();
        Gpio.setLedGreen();
        if (volumeAndLightPop != null) {
            volumeAndLightPop.onPause();
        }
    }

    private void human_sensing_hook(int number, boolean isButton) {
        Log.e(TAG, "human_sensing_hook\n\n\n ");
        eventNo = number;
        if (checkIsWorkTime()) {
            isStartMotionCheck = false;
            if (mainActivityPlayModel != null) {
                if (SPUtils.getInt(MainActivity.this, Config.ECO_MODE_STATE) >= 0) {
                    isECO = true;
                    isCanTimeStart = false;
                } else {
                    isCanTimeStart = true;
                }

                long timeSpan = System.currentTimeMillis() - MainActivityPlayModel.reStartTime;
                if (timeSpan > SPUtils.getInt(MainActivity.this, Config.ECO_MODE_STATE) * 1000) {
                    setScreenBrightnessOn();
                    try {
                        Gpio.setLedGreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isPlayingBeaconEvent) {
                        mainActivityPlayModel.startPlay(true, isButton);
                    }
                    Log.e(TAG, "onKeyDown: start play ");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //移动检测
            //if (isStartMotionCheck) {
            if (keyCode == KeyEvent.KEYCODE_F1) {
                human_sensing_hook(-1, true);
            }
            if (keyCode == KeyEvent.KEYCODE_F2) {
                human_sensing_hook(0, true);
            }
            if (keyCode == KeyEvent.KEYCODE_F3) {
                human_sensing_hook(1, true);
            }
            //}
            goToHome(keyCode, event, this);

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.getRepeatCount() > 30) {
                    mainActivityPlayModel.startToOSD();
                }
            }
            //Log.d(TAG, "key:"+ keyCode);
            volumeAndLightPop.onkeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchModel.onTouchEvent(event);
        return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
        volumeAndLightPop.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (bluetoothActBroadcastReceiver != null) {
            unregisterReceiver(bluetoothActBroadcastReceiver);
        }
        bluetoothActBroadcastReceiver = null;
        receiver = null;
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        mainActivityPlayModel.onDestroy();
    }


    public static final String BLUETOOTH_BROADCAST_NAME = "com.hu.bluetooth.BLUETOOTH_ACTION";
    public static final String BLUETOOTH_INT_EXTRA_NAME = "BLUETOOTH_ACTION_STRING_NAME";

    private BluetoothActBroadcastReceiver bluetoothActBroadcastReceiver = new BluetoothActBroadcastReceiver();

    private long oldTime = 0;

    private class BluetoothActBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int intentNo = intent.getIntExtra(BLUETOOTH_INT_EXTRA_NAME, -1);
            Log.e(TAG, "onReceive --------------1 onReceive: intentNo = " + intentNo + ",isPlayingBeaconEvent = " + isPlayingBeaconEvent);
           /* if (System.currentTimeMillis() - oldTime <= 2 * 1000) {
                Log.e(TAG, "onReceive: time is too close");
                return;
            }*/
            if (isPlayingBeaconEvent) {//如果正在播放beacon，检测到另外的beacon设备在5之内不播放新的beacon。
                if (intentNo == Config.BEACON_TAG_NO_PERSION) {//没人
                    if (beaconTagNo == Config.BEACON_TAG_NO_PERSION && intentNo == Config.BEACON_TAG_NO_PERSION) {  //原来没人，现在再次没人
                        return;
                    }
                    oldTime = System.currentTimeMillis();
                    beaconTagNo = intentNo;
                    mainActivityPlayModel.startPlayBeacon();
                } else if (intentNo == Config.BEACON_TAG_PERSION && beaconTagNo == Config.BEACON_TAG_NO_PERSION) {//原来播放没人，现在又有人了
                    FileUtils.movePhotoToTargetFolder(beaconTagNo);
                    if (motionDetectorService != null) {
                        motionDetectorService.startDetect();
                    }
                    oldTime = System.currentTimeMillis();
                    beaconTagNo = intentNo;
                    mainActivityPlayModel.startPlayBeacon();
                }
            } else {
                if (intentNo == Config.BEACON_TAG_PERSION) {//有人
                    oldTime = System.currentTimeMillis();
                    beaconTagNo = intentNo;
                    mainActivityPlayModel.startPlayBeacon();
                    if (motionDetectorService != null) {
                        motionDetectorService.startDetect();
                    }
                }
            }
        }
    }
}
