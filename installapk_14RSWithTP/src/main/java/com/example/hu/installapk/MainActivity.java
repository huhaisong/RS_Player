package com.example.hu.installapk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String APK_PACKAGE_NAME = "com.example.hu.mediaplayerapk";
    public static final String INTERNAL_STORAGE_SYSTEM = "/mnt/sdcard/System";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate:  android.os.Process.myPid():" + android.os.Process.myPid());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    private boolean success = false;
    private boolean isExit = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.e(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case 0:
                    Log.e(TAG, "handleMessage: finish!");
                    handler.removeMessages(0);
                    handler.removeMessages(2);
                    handler.removeMessages(1);
                    finish();
                    break;
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String newAPKPath = getNewAPK();
                            if (newAPKPath != null) {
                                File fromFile = new File(newAPKPath);
                                Log.e(TAG, "run: path" + fromFile.getAbsolutePath());
                                String paramString = "pm install -r " + fromFile.getAbsolutePath();
                                success = executeCommand(paramString);
                                if (success) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) findViewById(R.id.textView)).setText("アプリケーション更新完了");
                                            handler.sendEmptyMessageDelayed(0, 5000);
                                        }
                                    });
                                    return;
                                }
                                if (installSlient(newAPKPath)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) findViewById(R.id.textView)).setText("アプリケーション更新完了\nデバイスに再起動する準備ができています");
                                            handler.sendEmptyMessageDelayed(0, 5000);
                                        }
                                    });
                                    try {
                                        Thread.currentThread().sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    // handler.sendEmptyMessage(2);
                                    Intent i = new Intent("android.intent.action.VIEW");
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.setDataAndType(Uri.parse("file://" + newAPKPath.toString()), "application/vnd.android.package-archive");
                                    MainActivity.this.startActivity(i);
                                }
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    }).start();
                    break;
                case 2:
                    ((TextView) findViewById(R.id.textView)).setText("アプリケーション更新失敗");
                    isExit = true;
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(1, 1000);
        handler.sendEmptyMessageDelayed(2, 30000);
        isExit = false;
    }

    private boolean executeCommand(String paramString) {
        boolean bool2 = false;
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", paramString});
            StreamGobblerSupport2 localStreamGobblerSupport2 = new StreamGobblerSupport2(process.getInputStream());
            localStreamGobblerSupport2.run();
            process.waitFor();
            String request = localStreamGobblerSupport2.message();
            Log.e(TAG, "executeCommand: " + request);
            boolean bool1 = bool2;
            if (request != null) {
                boolean bool3 = request.equalsIgnoreCase("Success");
                bool1 = bool2;
                if (bool3) {
                    bool1 = true;
                }
            }
            return bool1;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "executeCommand: install exception");
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean installSlient(String path) {
        boolean isSuccess = true;
        String cmd = "pm uninstall " + APK_PACKAGE_NAME + "\npm install -r " + path;
//        String cmd = "mount -o remount,rw /system\ncp " + path + " /system/app/S6Video/S6Video.apk";
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //静默安装需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
                isSuccess = false;
            }
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
                Log.e(TAG, "installSlient: " + successMsg);
                if (s.equals("Failure"))
                    isSuccess = false;
                else
                    isSuccess = true;
            }
            if (errorMsg.equals(" ") || errorMsg.equals(""))
                isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //显示结果
        Log.e(TAG, "installSlient: 成功消息：" + successMsg.toString() + "\n" + "错误消息: " + errorMsg.toString());
        return isSuccess;
    }

    private String getNewAPK() {
        File usbFile = new File(INTERNAL_STORAGE_SYSTEM);
        File[] usbFiles = usbFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                if (name.endsWith(".apk")) {
                    return true;
                }
                return false;
            }
        });

        PackageManager pm = getPackageManager();
        List<PackageInfo> oldPackageInfos = pm.getInstalledPackages(0);
        int version = 0;
        for (PackageInfo pInfo : oldPackageInfos) {
            if (pInfo.packageName.equals(APK_PACKAGE_NAME)) {
                version = pInfo.versionCode;
                break;
            }
        }

        if (usbFiles != null && usbFiles.length > 0) {
            for (File file : usbFiles) {
                PackageInfo packageInfo = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    if (appInfo != null) {
                        Log.e(TAG, "getNewAPK: packageInfo.packageName =  " + packageInfo.packageName + ",packageInfo.versionCode = " + packageInfo.versionCode);
                        if (packageInfo.packageName.equals(APK_PACKAGE_NAME) && version < packageInfo.versionCode) {
                            return file.getAbsolutePath();
                        }
                    }
                }
            }
        }
        return null;
    }

    private static final String TAG = "MainActivity";

    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isExit) {
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        doStartApplicationWithPackageName(APK_PACKAGE_NAME);
        super.finish();
    }
}
