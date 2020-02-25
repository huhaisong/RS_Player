package com.example.hu.mediaplayerapk.factory_reset;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ResetFactory {

    private static final String TAG = "ResetFactory";
    //设备安全管理服务
    private DevicePolicyManager devicePolicyManager = null;
    //对应自定义DeviceAdminReceiver的组件
    private ComponentName componentName = null;

    private boolean isRequestPermission = false;
    private Activity mActivity;
    private long time = 0;

    public ResetFactory(Activity mActivity) {
        this.mActivity = mActivity;
        this.time = System.currentTimeMillis();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                if (System.currentTimeMillis() - time < 60 * 1000) {
                    resetFactorySetting();
                }
            }
            return false;
        }
    });

    /*factorySetting----------------------------------------------------------------factorySetting*/
    //激活设备管理器
    private void getPermission() {
        devicePolicyManager = (DevicePolicyManager) mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(mActivity, LockReceiver.class);
        if (isAdminActive()) {
            Toast.makeText(mActivity, "设备管理器已激活", Toast.LENGTH_SHORT).show();
        } else {
            // 打备管理器的激活窗口
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 指定需要激活的组件
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "校园传媒基本服务");
            mActivity.startActivityForResult(intent, 1);
        }
    }

    //恢复出厂设置
    public void resetFactorySetting() {

        Log.e(TAG, "resetFactorySetting: " );
        if (isAdminActive()) {
            // 模拟器上无效，真机上慎用
            devicePolicyManager.wipeData(0);
        } else {
            if (!isRequestPermission) {
                getPermission();
                isRequestPermission = true;
            }
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    // 判断该组件是否有系统管理员的权限（【系统设置-安全-设备管理器】中是否激活）
    private boolean isAdminActive() {
        if (devicePolicyManager == null) {
            devicePolicyManager = (DevicePolicyManager) mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            componentName = new ComponentName(mActivity, LockReceiver.class);
        }
        return devicePolicyManager.isAdminActive(componentName);
    }
    /*factorySetting----------------------------------------------------------------factorySetting*/

    /*lockScreen------------------------------------------------------------------------lockScreen*/
  /*  public void wakeUpAndUnlock(Context context) {
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }*/

       /* public  void lockScreen() {
        if (isAdminActive()) {
            devicePolicyManager.lockNow();
        }
    }*/
    /*lockScreen------------------------------------------------------------------------lockScreen*/
}
