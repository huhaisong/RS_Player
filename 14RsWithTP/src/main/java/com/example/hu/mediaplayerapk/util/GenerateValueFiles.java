package com.example.hu.mediaplayerapk.util;

/**
 * Created by huhaisong on 2017/8/4 16:03.
 * 这是用来创建不同分辨率资源文件的
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateValueFiles {

    private int baseW;
    private int baseH;
    String dirStr;
    private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
    private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

    /**
     * {0}-HEIGHT
     */
    private final static String VALUE_TEMPLATE = "values-{0}x{1}";
    private static final String SUPPORT_DIMESION = "";
    private String supportStr = SUPPORT_DIMESION;

    public GenerateValueFiles(int baseX, int baseY, String supportStr) {
        this.baseW = baseX;
        this.baseH = baseY;
        this.supportStr = supportStr;
        dirStr = "res/values";
        File dir = new File(dirStr);
        System.out.println(dir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void generate() {
        String[] wh = supportStr.split(",");
        generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
    }

    private void generateXmlFile(int w, int h) {
        StringBuffer sbForWidth = new StringBuffer();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForWidth.append("<resources>");
        float cellw = w * 1.0f / baseW;
        float cellh = h * 1.0f / baseH;
        for (int i = 1; i < baseW; i++) {
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", baseW + "").replace("{1}", w + ""));
        sbForWidth.append("</resources>");
        StringBuffer sbForHeight = new StringBuffer();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForHeight.append("<resources>");
        for (int i = 1; i < baseH; i++) {
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}", change(cellh * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", baseH + "").replace("{1}", h + ""));
        sbForHeight.append("</resources>");

        File fileDir = new File(dirStr + File.separator + VALUE_TEMPLATE.replace("{0}", w + "").replace("{1}", h + ""));
        fileDir.mkdir();
        File layxFile = new File(fileDir.getAbsolutePath(), "lay_x.xml");
        File layyFile = new File(fileDir.getAbsolutePath(), "lay_y.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(sbForWidth.toString());
            pw.close();
            pw = new PrintWriter(new FileOutputStream(layyFile));
            pw.print(sbForHeight.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }

    public static void main(String[] args) {
        int baseW = 1920;
        int baseH = 1080;
        String addition = "1280,750";  //格式为  "1280,720"  1280为宽，720为高
        new GenerateValueFiles(baseW, baseH, addition).generate();
    }
}
