package com.example.hu.mediaplayerapk.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.usb_system_path;
import static com.example.hu.mediaplayerapk.config.Config.APK_PACKAGE_NAME;

/**
 * 获取手机上apk文件信息类
 */
public class APKUtils {


    public static int getCurrentAPPVersionCode(Context mContext) {
        int currentVersionCode = 0;
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String appVersionName = info.versionName; //版本名字
            currentVersionCode = info.versionCode; //版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

    public static String getNewAPK(Context mContext) {
        File usbFile = new File(usb_system_path);
        if (!usbFile.exists()||!usbFile.isDirectory())
            return null;
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

        if (usbFiles != null && usbFiles.length > 0) {
            PackageManager pm = mContext.getPackageManager();
            for (File file : usbFiles) {
                PackageInfo packageInfo = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    if (appInfo != null) {
                        if (packageInfo.packageName.equals(APK_PACKAGE_NAME) && packageInfo.versionCode > getCurrentAPPVersionCode(mContext)) {
                            return file.getAbsolutePath();
                        }
                    }
                }
            }
        }
        return null;
    }

    private static final String TAG = "APKUtils";
    public static final String INSTALL_APK_PACKAGE_NAME = "com.example.hu.installapk";

    public static int getAppVersion(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> oldPackageInfos = pm.getInstalledPackages(0);
        int version = 0;
        for (PackageInfo pInfo : oldPackageInfos) {
            if (pInfo.packageName.equals(APK_PACKAGE_NAME)) {
                version = pInfo.versionCode;
                break;
            }
        }
        return version;
    }

    public static String getNewInstallAPK(Context mContext) {
        File usbFile = new File(usb_system_path);
        if (!usbFile.exists()||!usbFile.isDirectory())
            return null;
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

        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> oldPackageInfos = pm.getInstalledPackages(0);
        int version = 0;
        for (PackageInfo pInfo : oldPackageInfos) {
            if (pInfo.packageName.equals(INSTALL_APK_PACKAGE_NAME)) {
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
                        if (packageInfo.packageName.equals(INSTALL_APK_PACKAGE_NAME) && version < packageInfo.versionCode) {
                            return file.getAbsolutePath();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void installApk(Context context, String path) {
        File apkfile = new File(path);
        if (!apkfile.exists()) {
            return;
        }
        Log.e(TAG, "installApk: android.os.Process.myPid():" + android.os.Process.myPid());
        if (!doStartApplicationWithPackageName("com.example.hu.installapk", context)) {
            Log.e(TAG, "installApk: "+ "doStartApplicationWithPackageName unsuccessful" );
            Intent i = new Intent("android.intent.action.VIEW");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            context.startActivity(i);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static boolean doStartApplicationWithPackageName(String packagename, Context context) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            context.startActivity(intent);
            return true;
        }
        return false;
    }


    public static boolean executeCommand(String paramString) {
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
                    Log.e(TAG, "executeCommand: failed!");
                    bool1 = true;
                }
            }
            return bool1;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "executeCommand: exception");
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }
}