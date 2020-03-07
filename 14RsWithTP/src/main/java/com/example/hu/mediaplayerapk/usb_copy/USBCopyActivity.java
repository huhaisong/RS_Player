package com.example.hu.mediaplayerapk.usb_copy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.dialog.ChooseDialog;
import com.example.hu.mediaplayerapk.ui.activity.BaseActivity;
import com.example.hu.mediaplayerapk.util.APKUtils;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.ShellUtil;

import java.io.File;

import static com.example.hu.mediaplayerapk.application.MyApplication.internal_system_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_washing_path;
import static com.example.hu.mediaplayerapk.util.APKUtils.executeCommand;
import static com.example.hu.mediaplayerapk.util.APKUtils.getSerialNumber;
import static com.example.hu.mediaplayerapk.util.FactoryReset.factoryResetSetting;

public class USBCopyActivity extends BaseActivity {

    ChooseDialog chooseDialog;

    private static final String ACTION = "android.intent.action.MEDIA_UNMOUNTED";
    private static final String TAG = "USBCopyActivity";

    public class FinishBroadCastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getData().getPath().contains("usb")) {
                Log.e(TAG, "FinishBroadCastReceive onReceive: ");
                USBCopyActivity.this.finish();
            }
        }
    }

    FinishBroadCastReceive broadCastReceive;
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_usbcopy);
        this.mContext = this;
        broadCastReceive = new FinishBroadCastReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        registerReceiver(broadCastReceive, filter);
        ShellUtil.executeShellCommand(this);

        if (FileUtils.checkDirExist(Config.USB_STORAGE_ROOT_PATH + File.separator + "ITVLog")) {
            if (FileUtils.copyFolderMerge(FileUtils.getLogROOTPATH(), Config.USB_STORAGE_ROOT_PATH + File.separator + "ITVLog" + File.separator + getSerialNumber()))
                Toast.makeText(this, "copy PlayLog Success!", Toast.LENGTH_SHORT).show();
        }

        if (FileUtils.checkHaveFile(usb_warning_path)
                || FileUtils.checkHaveFile(usb_impacttv_path)
                || FileUtils.checkHaveFile(usb_impactv_path)
                || FileUtils.checkHaveFile(usb_washing_path)
        ) {//是否有相关文件
            chooseDialog = new ChooseDialog(this, getString(R.string.copy_dialog_content));
            ChooseDialog.ClickListenerInterface listen = new ChooseDialog.ClickListenerInterface() {
                @Override
                public void select(int i) {
                    switch (i) {
                        case R.id.iv_choose_yes:
                            USBCopyTask usbCopyTask = new USBCopyTask(USBCopyActivity.this);
                            usbCopyTask.execute();
                            chooseDialog.dismiss();
                            break;
                        case R.id.iv_choose_no:
                            chooseDialog.dismiss();
                            finish();
                            break;
                    }
                }
            };
            chooseDialog.setClickListen(listen);
            chooseDialog.show();
            findViewById(R.id.layout_usb_copy).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_usb_copy)).setText(R.string.usb_copy_is_not_have_content);
        } else if (FileUtils.checkDirExist(Config.USB_STORAGE_ROOT_PATH + File.separator + "SAVED_PIC")) {
            chooseDialog = new ChooseDialog(this, getString(R.string.copy_to_usb_dialog_content));
            ChooseDialog.ClickListenerInterface listen = new ChooseDialog.ClickListenerInterface() {
                @Override
                public void select(int i) {
                    switch (i) {
                        case R.id.iv_choose_yes:
                            USBCopyTask usbCopyTask = new USBCopyTask(USBCopyActivity.this);
                            usbCopyTask.execute();
                            chooseDialog.dismiss();
                            break;
                        case R.id.iv_choose_no:
                            chooseDialog.dismiss();
                            finish();
                            break;
                    }
                }
            };
            chooseDialog.setClickListen(listen);
            chooseDialog.show();
            findViewById(R.id.layout_usb_copy).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_usb_copy)).setText(R.string.usb_copy_is_not_have_content);
        } else {//是否有新版本的apk
            Log.e(TAG, "onCreate: no file");
            final String newAPKPath = APKUtils.getNewAPK(this);
            final String installAPKPath = APKUtils.getNewInstallAPK(this);
            if (newAPKPath != null || installAPKPath != null) {
                chooseDialog = new ChooseDialog(this, this.getString(R.string.update_apk));
                ChooseDialog.ClickListenerInterface listen = new ChooseDialog.ClickListenerInterface() {
                    @Override
                    public void select(int i) {
                        switch (i) {
                            case R.id.iv_choose_yes:
                                factoryResetSetting(mContext);
                                chooseDialog.dismiss();
                                if (installAPKPath != null) {
                                    File fromFile = new File(installAPKPath);
                                    String paramString = "pm install -r " + fromFile.getAbsolutePath();
                                    executeCommand(paramString);
                                }
                                if (newAPKPath != null) {
                                    File fromFile = new File(newAPKPath);
                                    File toFile = new File(internal_system_path + File.separator + fromFile.getName());
                                    FileUtils.copyFile(fromFile, toFile, true);
                                    APKUtils.installApk(USBCopyActivity.this, toFile.getAbsolutePath());
                                }
                                ((Activity) mContext).findViewById(R.id.layout_usb_copy).setVisibility(View.VISIBLE);
                                TextView textView = (TextView) ((Activity) mContext).findViewById(R.id.tv_usb_copy);
                                textView.setText(mContext.getString(R.string.usb_copy_complete));
                                break;
                            case R.id.iv_choose_no:
                                chooseDialog.dismiss();
                                break;
                        }
                    }
                };
                chooseDialog.setClickListen(listen);
                chooseDialog.show();
            } else {
                findViewById(R.id.layout_usb_copy).setVisibility(View.VISIBLE);
                if ((!FileUtils.checkDirExist(usb_event_path)) && (!FileUtils.checkDirExist(usb_impactv_path)) &&
                        !FileUtils.checkDirExist(usb_impacttv_path)) {
                    ((TextView) findViewById(R.id.tv_usb_copy)).setText(R.string.usb_copy_is_not_have_folder);
                } else {
                    ((TextView) findViewById(R.id.tv_usb_copy)).setText(R.string.usb_copy_is_not_have_content);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadCastReceive);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG, "finish: ");
        MyApplication.getInstance().restartApp();
 /*  Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }
}
