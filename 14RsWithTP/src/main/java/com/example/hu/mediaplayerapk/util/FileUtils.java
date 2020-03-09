package com.example.hu.mediaplayerapk.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.hu.mediaplayerapk.config.Config;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static String lock;

    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    public static String getLogPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ITVlog"
                + File.separator + "Playlog_" + TimeUtil.getCurrentFormatDate() + ".csv";
    }

    public static String getLogROOTPATH() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ITVlog";

    }

    public static boolean isMoving = false;

    public static void movePhotoToTargetFolder(final int beaconTagNo) {
        Log.e(TAG, "movePhotoToTargetFolder: isMoving = " + isMoving + ",beaconTagNo = " + beaconTagNo);
        if (isMoving)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String parentPath = Config.INTERNAL_FILE_ROOT_PATH + File.separator
                        + Config.PICKTURE_TEMP_FOLDER;
                if (!checkHaveFile(parentPath)) {
                    isMoving = false;
                    return;
                }
                isMoving = true;
                File parentFile = new File(parentPath);
                File[] filels = parentFile.listFiles();
                String path = "";
                if (beaconTagNo == Config.BEACON_TAG_PERSION) {
                    path = Config.INTERNAL_FILE_ROOT_PATH + File.separator
                            + Config.PICKTURE_OK_FOLDER + File.separator + filels[0].getName();
                    copyFile(filels[0], new File(path), true);
                } else if (beaconTagNo == Config.BEACON_TAG_NO_PERSION) {
                    path = Config.INTERNAL_FILE_ROOT_PATH + File.separator
                            + Config.PICKTURE_NG_FOLDER + File.separator + "NG" + filels[0].getName();
                    copyFile(filels[0], new File(path), true);
                }

                Log.e(TAG, "run: " + beaconTagNo + ",path = " + path);
                deleteDirectory(Config.INTERNAL_FILE_ROOT_PATH + File.separator
                        + Config.PICKTURE_TEMP_FOLDER);
                isMoving = false;
            }
        }).start();
    }

    /**
     * @param path 文件地址
     * @return 文件是否有内容
     */
    public boolean fileState(String path) {
        File f = new File(path);
        if (!f.exists())
            return false;
        if (f.length() == 0)
            return false;
        return true;
    }

    public static File creatFileIfNotExist(String path) {
        System.out.println("cr");
        File file = new File(path);
        if (!file.exists()) {  //判断文件是否存在
            try {
                new File(path.substring(0, path.lastIndexOf(File.separator))).mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 保存字符串
     */
    public static void saveTxtFile(String filePath, String text) {
        try {
            creatFileIfNotExist(filePath);
            FileOutputStream out = new FileOutputStream(filePath);
            // 构建一个写入器,用于向流中写入字符数据
            OutputStreamWriter writer = new OutputStreamWriter(out, "gb2312");
            writer.write(text);
            writer.close();
            out.close();
        } catch (Exception e) {
            String ext = e.getLocalizedMessage();
        }
    }

    /**
     * 保存字符串
     */
    public static void AppendTxtFile(String filePath, String text) {
        try {
            creatFileIfNotExist(filePath);
            FileOutputStream out = new FileOutputStream(filePath, true);
            // 构建一个写入器,用于向流中写入字符数据
            OutputStreamWriter writer = new OutputStreamWriter(out, "gb2312");
            writer.write(text);
            writer.close();
            out.close();
        } catch (Exception e) {
            String ext = e.getLocalizedMessage();
            Log.e(TAG, "saveTxtFile: ext = " + ext);
            e.printStackTrace();
        }
    }

    /**
     * 保存字符串
     */
    public static void CheckAndCreatePlayLogfile(String filePath) {
        File LogFile = new File(filePath);
        String fileHeader = "Date,Time,Type,File\n";
        //TBD:检查并删除旧文件
        if (LogFile.exists()) {
            return;  //如果已经存在文件就不用管
        }

        creatFileIfNotExist(filePath);
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            // 构建一个写入器,用于向流中写入字符数据
            OutputStreamWriter writer = new OutputStreamWriter(out, "gb2312");
            writer.write(fileHeader);
            writer.close();
            out.close();
        } catch (Exception e) {
            String ext = e.getLocalizedMessage();
        }
    }

    // 读取一个给定的文本文件内容,并把内容以一个字符串的形式返回
    public static String readTextLine(String textFile) {
        try {
            // 首先构建一个文件输入流,该流用于从文本文件中读取数据
            FileInputStream input = new FileInputStream(textFile);
            // 为了能够从流中读取文本数据,我们首先要构建一个特定的Reader的实例,
            // 因为我们是从一个输入流中读取数据,所以这里适合使用InputStreamReader.
            InputStreamReader streamReader = new InputStreamReader(input, "gb2312");
            // 为了能够实现一次读取一行文本的功能,我们使用了 LineNumberReader类,
            // 要构建LineNumberReader的实例,必须要传一个Reader实例做参数,
            // 我们传入前面已经构建好的Reder.
            LineNumberReader reader = new LineNumberReader(streamReader);
            // 字符串line用来保存每次读取到的一行文本.
            String line = null;
            // 这里我们使用一个StringBuilder来存储读取到的每一行文本,
            // 之所以不用String,是因为它每次修改都会产生一个新的实例,
            // 所以浪费空间,效率低.
            StringBuilder allLine = new StringBuilder();
            // 每次读取到一行,直到读取完成
            while ((line = reader.readLine()) != null) {
                allLine.append(line);
                // 这里每一行后面,加上一个换行符,LINUX中换行是”\n”,
                // windows中换行是”\r\n”.
                //allLine.append("\n");
            }
            // 把Reader和Stream关闭
            streamReader.close();
            reader.close();
            input.close();
            // 把读取的字符串返回
            return allLine.toString();
        } catch (Exception e) {
            // Toast.makeText(this, e.getLocalizedMessage(),
            // Toast.LENGTH_LONG).show();
            return "";
        }
    }


    /**
     * 复制单个文件
     *
     * @param fromFile String 原文件路径 如：c:/fqf.txt
     * @param toFile   String 复制后路径 如：f:/fqf.txt
     * @param rewrite  boolean 是否覆盖原有的文件
     * @return boolean
     */
    public static void copyFile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
        }
    }


    /**
     * 复制整个文件夹内容，如果有相同文件名的，则是追加上去，而不是覆盖
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static boolean copyFolderMerge(String oldPath, String newPath) {
        Log.e(TAG, "copyFolderMerge: " + newPath);
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return false;
            }
            if (!oldfile.canRead()) {
                return false;
            }
            String[] file = oldfile.list();
            File temp = null;
            if (file == null || file.length == 0)
                return false;
            for (int i = 0; i < file.length; i++) {
                Log.e(TAG, "copyFolderMerge: " + file[i]);
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    File targeFile = new File(newPath + File.separator + temp.getName());
                    if (targeFile.exists()) {
                        BufferedReader fin = new BufferedReader(new FileReader(temp));
                        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(new File(newPath + File.separator + temp.getName()), true)));
                        //fout.println();
                        //fout.println();
                        String str = null;
                        fin.readLine();//把文件头跳过
                        while ((str = fin.readLine()) != null) {
                            fout.println(str);
                        }
                        fout.flush();
                        fout.close();
                        fin.close();
                        temp.delete();
                    } else {
                        FileInputStream input = new FileInputStream(temp);
                        FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                        byte[] b = new byte[1024 * 5];
                        int len;
                        while ((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }
                        output.flush();
                        output.close();
                        input.close();
                        temp.delete();
                    }
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    if (!copyFolderMerge(oldPath + "/" + file[i], newPath + "/" + file[i]))
                        return false;
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return;
            }
            if (!oldfile.isFile()) {
                return;
            }
            if (!oldfile.canRead()) {
                return;
            }
            String[] file = oldfile.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名路径
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }


    /**
     * @param filePath
     * @return 如果有文件就返回true 否则返回false
     */
    public static boolean checkHaveFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.canRead() && file.isDirectory()) {
            File[] files = getAllFile(new File(filePath));
            if (files.length > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkDirExist(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean checkHaveGivenFile(String filepath, String name) {
        File file = new File(filepath);
        if (file.exists() && file.canRead() && file.isDirectory()) {
            File[] files = getAllFile(new File(filepath));
            for (File item : files) {
                if (item.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static File[] getAllFile(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".txt")
                            || name.equalsIgnoreCase(".jar")
                            //图片
                            || name.equalsIgnoreCase(".jpg")
                            || name.equalsIgnoreCase(".jpeg")
                            || name.equalsIgnoreCase(".jpe")
                            || name.equalsIgnoreCase(".bmp")
                            || name.equalsIgnoreCase(".gif")
                            || name.equalsIgnoreCase(".png")
                            || name.equalsIgnoreCase(".tif")
                            //音频
                            || name.equalsIgnoreCase(".mp3")
                            || name.equalsIgnoreCase(".wma")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ogg")
                            || name.equalsIgnoreCase(".ape")
                            || name.equalsIgnoreCase(".flac")
                            || name.equalsIgnoreCase(".wav")
                            || name.equalsIgnoreCase(".mpc")
                            || name.equalsIgnoreCase(".aif")
                            || name.equalsIgnoreCase(".aiff")
                            || name.equalsIgnoreCase(".amr")
                            || name.equalsIgnoreCase(".aac")
                            || name.equalsIgnoreCase(".ac3")
                            || name.equalsIgnoreCase(".m4a")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mid")
                            //视频
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".mpeg")
                            || name.equalsIgnoreCase(".ogg")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".csv")
                            || name.equalsIgnoreCase(".f4v")) {
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }

    public static File[] getVideoAndPhoto(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".jpg")//图片
                            || name.equalsIgnoreCase(".jpeg")
                            || name.equalsIgnoreCase(".jpe")
                            || name.equalsIgnoreCase(".bmp")
                            || name.equalsIgnoreCase(".gif")
                            || name.equalsIgnoreCase(".png")
                            || name.equalsIgnoreCase(".tif")
                            //视频
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".mpeg")
                            || name.equalsIgnoreCase(".ogg")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".f4v")) {
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }

    public static File[] getVideo(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".mpeg")
                            || name.equalsIgnoreCase(".ogg")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".f4v")) {
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }

    public static File[] getPhoto(File file) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".jpg")
                            || name.equalsIgnoreCase(".jpeg")
                            || name.equalsIgnoreCase(".jpe")
                            || name.equalsIgnoreCase(".bmp")
                            || name.equalsIgnoreCase(".gif")
                            || name.equalsIgnoreCase(".png")
                            || name.equalsIgnoreCase(".tif")) {
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }

    public static File[] getAudio(File file) {

        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp3")
                            || name.equalsIgnoreCase(".wma")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ogg")
                            || name.equalsIgnoreCase(".ape")
                            || name.equalsIgnoreCase(".flac")
                            || name.equalsIgnoreCase(".wav")
                            || name.equalsIgnoreCase(".mpc")
                            || name.equalsIgnoreCase(".aif")
                            || name.equalsIgnoreCase(".aiff")
                            || name.equalsIgnoreCase(".amr")
                            || name.equalsIgnoreCase(".aac")
                            || name.equalsIgnoreCase(".ac3")
                            || name.equalsIgnoreCase(".m4a")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mid")) {
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }

    private static final String TAG = "FileUtils";

    public static long getSize(String path) {
        List<String> list = getMountPathList();
        if (!list.contains(path)) {
            return 0;
        } else {
            return 1;
        }
      /*  File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        long total = 0;
        try {
            StatFs statfs = new StatFs(path);
            long blocSize = statfs.getBlockSize();
            long totalBlocks = statfs.getBlockCount();
            total = totalBlocks * blocSize;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getSize: " + "string path err!");
        }
        if (total > 500000000 && total < 520000000) {
            return 0;
        }
        return total;*/
    }

    public static List<String> orderList(List<String> oldList) {
        List<String> newList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        Collections.sort(oldList);
        for (String item : oldList) {
            tempList.add(item);
        }
        for (String item : oldList) {
            if (item.endsWith(".mpg")) {
                newList.add(item);
                tempList.remove(item);
            }
        }
        for (String item : oldList) {
            if (item.endsWith(".avi")) {
                newList.add(item);
                tempList.remove(item);
            }
        }
        for (String item : oldList) {
            if (item.endsWith(".mp4")
                    || item.endsWith(".3gp")
                    || item.endsWith(".wmv")
                    || item.endsWith(".ts")
                    || item.endsWith(".rmvb")
                    || item.endsWith(".mov")
                    || item.endsWith(".m4v")
                    || item.endsWith(".m3u8")
                    || item.endsWith(".3gpp")
                    || item.endsWith(".3gpp2")
                    || item.endsWith(".mkv")
                    || item.endsWith(".flv")
                    || item.endsWith(".divx")
                    || item.endsWith(".f4v")
                    || item.endsWith(".rm")
                    || item.endsWith(".asf")
                    || item.endsWith(".ram")
                    || item.endsWith(".v8")
                    || item.endsWith(".swf")
                    || item.endsWith(".m2v")
                    || item.endsWith(".asx")
                    || item.endsWith(".ra")
                    || item.endsWith(".ndivx")
                    || item.endsWith(".xvid")) {
                newList.add(item);
                tempList.remove(item);
            }
        }
        for (String item : tempList) {
            newList.add(item);
            oldList.remove(item);
        }
        return newList;
    }

    public static boolean isVideo(String path) {
        int i = path.lastIndexOf('.');
        if (i != -1) {
            String name = path.substring(i);
            if (name.equalsIgnoreCase(".mp4")
                    || name.equalsIgnoreCase(".3gp")
                    || name.equalsIgnoreCase(".wmv")
                    || name.equalsIgnoreCase(".ts")
                    || name.equalsIgnoreCase(".rmvb")
                    || name.equalsIgnoreCase(".mov")
                    || name.equalsIgnoreCase(".m4v")
                    || name.equalsIgnoreCase(".avi")
                    || name.equalsIgnoreCase(".m3u8")
                    || name.equalsIgnoreCase(".3gpp")
                    || name.equalsIgnoreCase(".3gpp2")
                    || name.equalsIgnoreCase(".mkv")
                    || name.equalsIgnoreCase(".flv")
                    || name.equalsIgnoreCase(".divx")
                    || name.equalsIgnoreCase(".f4v")
                    || name.equalsIgnoreCase(".rm")
                    || name.equalsIgnoreCase(".asf")
                    || name.equalsIgnoreCase(".ram")
                    || name.equalsIgnoreCase(".mpg")
                    || name.equalsIgnoreCase(".v8")
                    || name.equalsIgnoreCase(".swf")
                    || name.equalsIgnoreCase(".m2v")
                    || name.equalsIgnoreCase(".asx")
                    || name.equalsIgnoreCase(".ra")
                    || name.equalsIgnoreCase(".ndivx")
                    || name.equalsIgnoreCase(".xvid")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPhoto(String path) {
        int i = path.lastIndexOf('.');
        if (i != -1) {
            String name = path.substring(i);
            if (name.equalsIgnoreCase(".jpg")
                    || name.equalsIgnoreCase(".jpeg")
                    || name.equalsIgnoreCase(".jpe")
                    || name.equalsIgnoreCase(".bmp")
                    || name.equalsIgnoreCase(".gif")
                    || name.equalsIgnoreCase(".png")
                    || name.equalsIgnoreCase(".tif")) {
                return true;
            }
        }
        return false;
    }

    public static boolean allIsPhoto(List<String> fileList) {
        for (String path : fileList) {
            if (isVideo(path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param arrayList        已知所有的文件名
     * @param allVideoFileList 已知所有的文件路径
     * @param fileList         通过文件名获得的文件路径
     */
    public static void getPathFromName(ArrayList<String> arrayList, ArrayList<String> allVideoFileList, ArrayList<String> fileList) {
        for (String item1 : arrayList) {
            for (String item : allVideoFileList) {
                File file = new File(item);
                if (file.getName().equals(item1)) {
                    fileList.add(item);
                    break;
                }
            }
        }
    }

    //加载已经挂载的文件
    public static String GetAllSDPath() {
        String strMountInfo = "";
        // 1.首先获得系统已加载的文件系统信息
        try {
            // 创建系统进程生成器对象
            ProcessBuilder objProcessBuilder = new ProcessBuilder();
            // 执行 mount -h 可以看到 mount : list mounted filesystems
            // 这条命令可以列出已加载的文件系统
            objProcessBuilder.command("mount"); // 新的操作系统程序和它的参数
            // 设置错误输出都将与标准输出合并
            objProcessBuilder.redirectErrorStream(true);
            // 基于当前系统进程生成器的状态开始一个新进程，并返回进程实例
            Process objProcess = objProcessBuilder.start();
            // 阻塞线程至到本地操作系统程序执行结束，返回本地操作系统程序的返回值
            objProcess.waitFor();
            // 得到进程对象的输入流，它对于进程对象来说是已与本地操作系统程序的标准输出流(stdout)相连接的
            InputStream objInputStream = objProcess.getInputStream();
            byte[] buffer = new byte[1024];
            // 读取 mount 命令程序返回的信息文本
            while (-1 != objInputStream.read(buffer)) {
                strMountInfo = strMountInfo + new String(buffer);
            }
            // 关闭进程对象的输入流
            objInputStream.close();
            // 终止进程并释放与其相关的任何流
            objProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2.然后再在系统已加载的文件系统信息里查找 SD 卡路径
        // mount 返回的已加载的文件系统信息是以一行一个信息的形式体现的，
        // 所以先用换行符拆分字符串
        String[] lines = strMountInfo.split("\n");
        // 清空该字符串对象，下面将用它来装载真正有用的 SD 卡路径列表
        strMountInfo = "";
        for (int i = 0; i < lines.length; i++) {
            // 如果该行内有 /mnt/和 vfat 字符串，说明可能是内/外置 SD 卡的挂载路径
            if (-1 != lines[i].indexOf(" /mnt/") && // 前面要有空格，以防断章取义
                    -1 != lines[i].indexOf(" vfat "))  // 前后均有空格
            {
                // 再以空格分隔符拆分字符串
                String[] blocks = lines[i].split("\\s"); // \\s 为空格字符
                for (int j = 0; j < blocks.length; j++) {
                    // 如果字符串中含有/mnt/字符串，说明可能是我们要找的 SD 卡挂载路径
                    if (-1 != blocks[j].indexOf("/mnt/")) {
                        // 排除重复的路径
                        if (-1 == strMountInfo.indexOf(blocks[j])) {
                            // 用分号符(;)分隔 SD 卡路径列表，
                            strMountInfo += blocks[j] + ";";
                        }
                    }
                }
            }
        }
        return strMountInfo;
    }

    //获取已挂载路径
    public static List<String> getMountPathList() {
        List<String> pathList = new ArrayList<String>();
        final String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();//取得当前JVM的运行时环境
        try {
            Process p = run.exec(cmd);//执行命令
            BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (-1 != line.indexOf(" /mnt/") && -1 != line.indexOf(" vfat ")) {// 前后均有空格// 前面要有空格，以防断章取义
                    //输出信息内容： /data/media /storage/emulated/0 sdcardfs rw,nosuid,nodev,relatime,uid=1023,gid=1023 0 0
                    String[] temp = TextUtils.split(line, " ");
                    //分析内容可看出第二个空格后面是路径
                    String result = temp[1];
                    File file = new File(result);
                    //类型为目录、可读、可写，就算是一条挂载路径
                    if (file.isDirectory() && file.canRead() && file.canWrite()) {
                        pathList.add(result);
                    }
                    // 检查命令是否执行失败
                    if (p.waitFor() != 0 && p.exitValue() == 1) {
                        // p.exitValue()==0表示正常结束，1：非正常结束
                    }
                }
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            //命令执行异常，就添加默认的路径
            pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            e.printStackTrace();
        }
        return pathList;
    }


    //获得可用的挂载路径
    public static List<String> getStoragePath(Context mContext) {
        List<String> pathList = new ArrayList<String>();
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            //直接获得路径
            Method mMethodGetPaths = null;
            String[] paths = null;
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            ArrayList<String> pathsArrayList = new ArrayList<>();
            Collections.addAll(pathsArrayList, paths);

            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                pathList.add(path);
                /*if (is_removale == removable) {
                    return path;
                }*/
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return pathList;
    }


    public static void deleteMoreOneMonthImage(String filePath) {
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        File[] files = dirFile.listFiles();
        if (files == null)
            return;
        if (files.length <= 0)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String itemFilePath = files[i].getName();
                itemFilePath.replaceAll("NG", "");
                if (itemFilePath.contains(".jpg")) {
                    long time = TimeUtil.getStringToDate(itemFilePath.replaceAll(".jpg", ""));
                    if (Math.abs(time - System.currentTimeMillis()) / 1000 > (30 * 24 * 60 * 60)) {
                        deleteFile(files[i].getAbsolutePath());
                    }
                } else {
                    deleteFile(files[i].getAbsolutePath());
                }
            } else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
    }
}
