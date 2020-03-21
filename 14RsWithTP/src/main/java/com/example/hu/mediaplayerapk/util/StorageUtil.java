package com.example.hu.mediaplayerapk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;


/**
 * @author ajeet
 * 05-Dec-2014  2014
 */
public class StorageUtil {

    public static void isRemovebleSDCardMounted() {
        File file = new File("/sys/class/block/");
        File[] files = file.listFiles(new MmcblkFilter("mmcblk\\d$"));
        if (files != null && files.length > 0) {
            for (File mmcfile : files) {
                File scrfile = new File(mmcfile, "device/scr");
                if (scrfile.exists()) {
                    Log.e(TAG, "isRemovebleSDCardMounted: " + scrfile.getAbsolutePath());
                    break;
                }
            }
        }
    }

    public static String getRemovebleSDCardPath() throws IOException {
        String sdpath = null;
        File file = new File("/sys/class/block/");
        File[] files = file.listFiles(new MmcblkFilter("mmcblk\\d$"));
        String sdcardDevfile = null;
        for (File mmcfile : files) {
            Log.d("SDCARD", mmcfile.getAbsolutePath());
            File scrfile = new File(mmcfile, "device/scr");
            if (scrfile.exists()) {
                sdcardDevfile = mmcfile.getName();
                Log.d("SDCARD", mmcfile.getName());
                break;
            }
        }
        if (sdcardDevfile == null) {
            return null;
        }
        FileInputStream is;
        BufferedReader reader;

        files = file.listFiles(new MmcblkFilter(sdcardDevfile + "p\\d+"));
        String deviceName = null;
        if (files.length > 0) {
            Log.d("SDCARD", files[0].getAbsolutePath());
            File devfile = new File(files[0], "dev");
            if (devfile.exists()) {
                FileInputStream fis = new FileInputStream(devfile);
                reader = new BufferedReader(new InputStreamReader(fis));
                String line = reader.readLine();
                deviceName = line;
            }
            Log.d("SDCARD", "" + deviceName);
            if (deviceName == null) {
                return null;
            }
            Log.d("SDCARD", deviceName);

            final File mountFile = new File("/proc/self/mountinfo");

            if (mountFile.exists()) {
                is = new FileInputStream(mountFile);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    // Log.d("SDCARD", line);
                    // line = reader.readLine();
                    // Log.d("SDCARD", line);
                    String[] mPonts = line.split("\\s+");
                    if (mPonts.length > 6) {
                        if (mPonts[2].trim().equalsIgnoreCase(deviceName)) {
                            if (mPonts[4].contains(".android_secure")
                                    || mPonts[4].contains("asec")) {
                                continue;
                            }
                            sdpath = mPonts[4];
                            Log.d("SDCARD", mPonts[4]);

                        }
                    }

                }
            }

        }

        return sdpath;
    }

    static class MmcblkFilter implements FilenameFilter {
        private String pattern;

        public MmcblkFilter(String pattern) {
            this.pattern = pattern;

        }

        @Override
        public boolean accept(File dir, String filename) {
            if (filename.matches(pattern)) {
                return true;
            }
            return false;
        }

    }

    private static final String TAG = "StorageUtil";

    public static List<String> getAllExternalSdcardPath() {
        List<String> PathList = new ArrayList<String>();

        String firstPath = Environment.getExternalStorageDirectory().getPath();
        Log.d(TAG, "getAllExternalSdcardPath , firstPath = " + firstPath);

        try {
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // 将常见的linux分区过滤掉
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }

                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[1].toLowerCase(Locale.getDefault());
                        Log.e(TAG, "getAllExternalSdcardPath: " + path);
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !PathList.contains(path) && path.contains("sd"))
                            PathList.add(items[1]);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!PathList.contains(firstPath)) {
            PathList.add(firstPath);
        }

        return PathList;
    }

}