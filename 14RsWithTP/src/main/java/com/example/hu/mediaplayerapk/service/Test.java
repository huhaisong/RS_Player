package com.example.hu.mediaplayerapk.service;

import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.TimeUtil;

public class Test {
    public static void main(String[] args) {
        System.out.println(Config.INTERNAL_FILE_ROOT_PATH + TimeUtil.getCurrentFormatTime() + ".jpg");
    }
}
