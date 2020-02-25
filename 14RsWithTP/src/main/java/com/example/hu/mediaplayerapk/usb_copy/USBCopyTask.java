package com.example.hu.mediaplayerapk.usb_copy;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.dialog.ChooseDialog;
import com.example.hu.mediaplayerapk.util.APKUtils;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.example.hu.mediaplayerapk.application.MyApplication.external_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_washing_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_system_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_washing_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_beacon_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_warning_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.usb_washing_path;
import static com.example.hu.mediaplayerapk.util.APKUtils.executeCommand;
import static com.example.hu.mediaplayerapk.util.FactoryReset.factoryResetSetting;

public class USBCopyTask extends AsyncTask<Void, String, Void> {

    private Context mContext;
    private TextView progressTextView, nameTextView;
    private static final String TAG = "USBCopyTask";
    private boolean isExistSDCard;

    public USBCopyTask(Context context) {
        this.mContext = context;
        progressTextView = (TextView) ((Activity) mContext).findViewById(R.id.tv_usb_copy_progress);
        nameTextView = (TextView) ((Activity) mContext).findViewById(R.id.tv_usb_copy_file_name);
    }

    @Override
    protected void onPreExecute() {
        ((Activity) mContext).findViewById(R.id.layout_usb_copy).setVisibility(View.GONE);
        ((Activity) mContext).findViewById(R.id.layout_usb_copy_file).setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView) ((Activity) mContext).findViewById(R.id.image_usb_copy_anim);
        imageView.setBackgroundResource(R.drawable.copy_file_anim);
        AnimationDrawable AniDraw = (AnimationDrawable) imageView.getBackground();
        AniDraw.start();
        isExistSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
        if (FileUtils.checkHaveFile(usb_event_path)
                || FileUtils.checkHaveFile(usb_impacttv_path)
                || FileUtils.checkHaveFile(usb_impactv_path)) {
            if (isExistSDCard) {
                FileUtils.deleteDirectory(external_impactv_path);
                FileUtils.deleteDirectory(external_impacttv_path);
                FileUtils.deleteDirectory(external_event_path);
            } else {
                FileUtils.deleteDirectory(internal_impactv_path);
                FileUtils.deleteDirectory(internal_event_path);
            }
        }
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (isExistSDCard) {
            if (FileUtils.checkHaveFile(usb_impacttv_path)) {
                copyFolder(usb_impacttv_path, external_impactv_path);
            } else {
                copyFolder(usb_impactv_path, external_impactv_path);
            }
            if (FileUtils.checkHaveFile(usb_warning_path)) {
                copyFolder(usb_warning_path, external_warning_path);
            }
            if (FileUtils.checkHaveFile(usb_washing_path)) {
                copyFolder(usb_washing_path, external_washing_path);
            }
        } else {
            if (FileUtils.checkHaveFile(usb_impacttv_path)) {
                copyFolder(usb_impacttv_path, internal_impactv_path);
                Log.e(TAG, "doInBackground: copyFolder(Config.USB_STORAGE_IMPACTTV_PATH, Config.INTERNAL_IMPACTTV_FILE_PATH);");
            } else {
                copyFolder(usb_impactv_path, internal_impactv_path);
                Log.e(TAG, "doInBackground:  copyFolder(Config.USB_STORAGE_IMPACTTV_PATH14, Config.INTERNAL_IMPACTTV_FILE_PATH);");
            }
            if (FileUtils.checkHaveFile(usb_washing_path)) {
                copyFolder(usb_washing_path, internal_washing_path);
            }
            if (FileUtils.checkHaveFile(usb_warning_path)) {
                Log.e(TAG, "doInBackground: checkHaveFile  usb_beacon_path true");
                copyFolder(usb_warning_path, internal_warning_path);
            }
        }
        if (FileUtils.checkDirExist(Config.USB_STORAGE_ROOT_PATH + File.separator + "SAVED_PIC")) {
            copyFolder(Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.PICKTURE_FOLDER, Config.USB_STORAGE_ROOT_PATH + File.separator + "SAVED_PIC");
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        nameTextView.setText(values[1]);
        progressTextView.setText(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //motionDetection
//        SPUtils.putInt(mContext, Config.CHECK_FACE_STATE, 0);
//        SPUtils.putInt(mContext, Config.ECO_MODE_STATE, -1);
        //playback mode
        SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_ALL_FILE);
        //one file repeat
        SPUtils.putString(mContext, Config.INTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        SPUtils.putString(mContext, Config.EXTERNAL_IMPACTV_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        //program
        FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        //bgm
        SPUtils.putInt(mContext, Config.IMAGE_BGM_IMPACTV, Config.IMAGE_BGM_OFF);
        FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, "");
        //EVENT
        SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_ALL_FILE);
        SPUtils.putString(mContext, Config.INTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        SPUtils.putString(mContext, Config.EXTERNAL_EVENT_PLAY_BACK_MODE_ONE_FILE_TITLE, "");
        FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, "");
        SPUtils.putInt(mContext, Config.IMAGE_BGM_EVENT, Config.IMAGE_BGM_OFF);
        FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, "");
        FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, "");
        if (FileUtils.checkHaveGivenFile(internal_impactv_path, Config.SCHEDULE_FILE_NAME)) {
            SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
        }
        if (FileUtils.checkHaveGivenFile(internal_event_path, Config.SCHEDULE_FILE_NAME)) {
            SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_SCHEDULE);
        }
        if (isExistSDCard) {
            if (FileUtils.checkHaveGivenFile(external_impacttv_path, Config.SCHEDULE_FILE_NAME)) {
                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
            }
            if (FileUtils.checkHaveGivenFile(external_impactv_path, Config.SCHEDULE_FILE_NAME)) {
                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_SCHEDULE);
            }
            if (FileUtils.checkHaveGivenFile(external_event_path, Config.SCHEDULE_FILE_NAME)) {
                SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_SCHEDULE);
            }
        }

        final String newAPKPath = APKUtils.getNewAPK(mContext);
        final String installAPKPath = APKUtils.getNewInstallAPK(mContext);
        ((Activity) mContext).findViewById(R.id.layout_usb_copy).setVisibility(View.VISIBLE);
        TextView textView = (TextView) ((Activity) mContext).findViewById(R.id.tv_usb_copy);
        textView.setText(mContext.getString(R.string.usb_copy_complete));
        ((Activity) mContext).findViewById(R.id.layout_usb_copy_file).setVisibility(View.GONE);
        if (newAPKPath != null || installAPKPath != null) {
            final ChooseDialog chooseDialog = new ChooseDialog(mContext, mContext.getString(R.string.update_apk));
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
                                APKUtils.installApk(mContext, toFile.getAbsolutePath());
                            }
                            break;
                        case R.id.iv_choose_no:
                            chooseDialog.dismiss();
                            break;
                    }
                }
            };
            chooseDialog.setClickListen(listen);
            chooseDialog.show();
        }
        super.onPostExecute(aVoid);
    }

    private void copyFolder(String fromPath, String toPath) {
        try {
            File toFile = new File(toPath);
            File fromFile = new File(fromPath);
            if (!fromFile.exists()) return;
            if (fromFile.isFile()) return;
            if (!fromFile.canRead()) return;
            if (!FileUtils.checkHaveFile(fromPath)) return;
            if (toFile.exists()) {
                FileUtils.deleteDirectory(toPath);
            }
            toFile.mkdirs();
            File[] files = FileUtils.getAllFile(fromFile);
            File temp;
            for (int i = 0; i < files.length; i++) {
                temp = files[i];
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output;
                    if (temp.getName().indexOf("#") == 0) {
                        output = new FileOutputStream(toPath + File.separator + temp.getName().substring(1));
                    } else {
                        output = new FileOutputStream(toPath + File.separator + temp.getName());
                    }
                    byte[] b = new byte[1024 * 10];
                    byte[] c = new byte[1024 * 10];
                    int len;
                    long total = input.available();
                    long progress = 0;
                    while ((len = input.read(b)) != -1) {
                        progress += len;
                        int percent = (int) ((progress * 100) / total);
                        if (temp.getName().indexOf("#") == 0) {
                            for (int j = 0; j < len; j++) {
                                c[j] = (byte) ~b[j];
                            }
                            output.write(c, 0, len);
                            publishProgress(String.valueOf(percent) + "%", temp.getName().substring(1));
                        } else {
                            output.write(b, 0, len);
                            publishProgress(String.valueOf(percent) + "%", temp.getName());
                        }
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "copyFolder: 复制整个文件夹出错！");
            e.printStackTrace();
        }
    }
}
