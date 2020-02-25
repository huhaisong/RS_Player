package com.example.hu.mediaplayerapk.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.bean.BeaconTag;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.ScheduleParse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.BLUETOOTH_BROADCAST_NAME;
import static com.example.hu.mediaplayerapk.ui.activity.MainActivity.BLUETOOTH_INT_EXTRA_NAME;

/**
 * Created by huhaisong on 2017/8/31 9:59.
 * 蓝牙温度计设备service
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private ArrayList<BeaconTag> BeaconList = null;  //记录本beacon文件夹内所有有关的beacon设备及其状态

    public void BeaconListClear() {
        BeaconList = null;
    }

    public boolean initialize() {
        initializeBeaconDevice();
        if (getAdapter() == null)
            return false;
        return false;
    }

    private void initializeBeaconDevice() {
        BeaconListClear();

        if (FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0)
            BeaconList = ScheduleParse.parse_BEACON_NO_TXT(MyApplication.external_beacon_path + File.separator + Config.BEACON_DEVICE_FILE_NAME);
        else
            BeaconList = ScheduleParse.parse_BEACON_NO_TXT(MyApplication.internal_beacon_path + File.separator + Config.BEACON_DEVICE_FILE_NAME);

        if (BeaconList.size() > 0) {
            Log.e(TAG, "initializeBeaconDevice: 解析的数据大小：" + BeaconList.size());
        } else {
            Log.e(TAG, "initializeBeaconDevice: 没解析成功");
        }

        for (int i = 0; i < BeaconList.size(); i++) {
            Log.e(TAG, "initializeBeaconDevice: " + BeaconList.get(i).toString());
            if (i >= 1) {
                BeaconList.remove(i);
            }
        }

    }

    //如果设备支持BLE，那么就可以获取蓝牙适配器。
    private BluetoothAdapter getAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
        initialize();
        enableBluetooth();

        for (BeaconTag curDev : BeaconList) {
            if (curDev != null) {
                if (curDev.getBeaconType() == BeaconTag.BEACON_GSENSOR) {
                    curDev.setBeaconData(0);
                } else //另外两种都是初始化为-1
                {
                    curDev.setBeaconData(-1);
                }
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(SCAN_BLE);
        registerReceiver(myBroadcastReceiver, filter);
    }

    public static final String SCAN_BLE = "com.hu.scan.ble";

    //打开蓝牙
    public void enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.enable();
                }
            }).start();
        }
    }

    //扫描蓝牙
    public void startScan() {
        Log.e(TAG, "startScan: ");
        stopScan();
        closeBluetooth();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startLeScan(leScanHook);
    }

    //停止扫描........
    public void stopScan() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.stopLeScan(leScanHook);
    }

    //关闭设备
    public void closeBluetooth() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public BluetoothAdapter.LeScanCallback leScanHook = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG, "onLeScan: device = " + device.getAddress());
            if (BeaconList == null || BeaconList.size() == 0)
                return;
            for (BeaconTag curDev : BeaconList) {
                if (curDev != null) {
                    if (device.getAddress().equalsIgnoreCase(curDev.getBeaconAddr())) {
                        //找到该设备了;
                        Log.e(TAG, "onLeScan: curDev = " + curDev.getBeaconAddr());
                        Log.e(TAG, "Found:address:" + device.getAddress() + "     scanRecord:" + Arrays.toString(scanRecord));
                        Log.e(TAG, "     curDev:" + curDev.toString());
                        switch (curDev.getBeaconType()) {
                            case BeaconTag.BEACON_MAGNET://magnet，捕捉4到0的动作
                                if ((scanRecord[11] == 0) && (curDev.getBeaconData() == 4)) {
                                    Intent intent = new Intent(BLUETOOTH_BROADCAST_NAME);
                                    intent.putExtra(BLUETOOTH_INT_EXTRA_NAME, curDev.getBeaconNo());
                                    sendBroadcast(intent);
                                    Log.i(TAG, "\n\n\nMagnet detected\n\n\n");
                                    //stopScan();//继续scan
                                }
                                curDev.setBeaconData(scanRecord[11]);
                                break;
                            case BeaconTag.BEACON_GSENSOR: //gsensor:捕捉0->2的动作
                                if ((scanRecord[11] == 2) && (curDev.getBeaconData() == 0)) {
                                    Intent intent1 = new Intent(BLUETOOTH_BROADCAST_NAME);
                                    intent1.putExtra(BLUETOOTH_INT_EXTRA_NAME, curDev.getBeaconNo());
                                    sendBroadcast(intent1);
                                    Log.i(TAG, "\n\n\nGsensor detected\n\n\n");
                                    //stopScan();//继续scan
                                }
                                curDev.setBeaconData(scanRecord[11]);
                                break;
                            case BeaconTag.BEACON_IRSENSOR:
                                Log.i(TAG, "\n\n\nsensor detected\n\n\n");
                                if ((scanRecord[11] == 0) && (curDev.getBeaconData() == 4)) {//4---->0  有人
                                    Intent intent2 = new Intent(BLUETOOTH_BROADCAST_NAME);
                                    intent2.putExtra(BLUETOOTH_INT_EXTRA_NAME, Config.BEACON_TAG_PERSION);
                                    sendBroadcast(intent2);
                                    Log.i(TAG, " 4---->0 ");
                                    //stopScan();//继续scan
                                } else if ((scanRecord[11] == 4) && (curDev.getBeaconData() == 0)) {//0---->4
                                    Intent intent2 = new Intent(BLUETOOTH_BROADCAST_NAME);
                                    intent2.putExtra(BLUETOOTH_INT_EXTRA_NAME, Config.BEACON_TAG_NO_PERSION);
                                    Log.i(TAG, " 0---->4 ");
                                    sendBroadcast(intent2);
                                }
                                curDev.setBeaconData(scanRecord[11]);
                                break;
                            default:
                                break;
                        }
//                            break;//找到地址一样的就可以break了
                    }
                }
            }
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        closeBluetooth();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCAN_BLE)) {
                startScan();
                return;
            }
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                startScan();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        startScan();
        return super.onStartCommand(intent, flags, startId);
    }
}
