package com.example.hu.mediaplayerapk.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hu.mediaplayerapk.application.MyApplication;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.util.Locale;

/**
 * Created by Administrator on 2016/12/21.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        String language = SPUtils.getString(this, "language");
        switchLanguage(language);
        MyApplication.getInstance().addActivity(this);
    }

    protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (language.equals("english")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.CHINESE;
        }
        resources.updateConfiguration(config, dm);
        SPUtils.putString(this, "language", language);
    }


}
