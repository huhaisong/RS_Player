package com.example.hu.mediaplayerapk.ui.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.util.SPUtils;
import com.example.hu.mediaplayerapk.util.TimeUtil;

import java.io.IOException;
import java.util.Locale;

import static com.example.hu.mediaplayerapk.R.id.tv_advanced_display_ratio_full;
import static com.example.hu.mediaplayerapk.R.id.tv_advanced_firmware_version_serial;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITMESSAGE;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITTIME;
import static com.example.hu.mediaplayerapk.util.APKUtils.getSerialNumber;
import static com.example.hu.mediaplayerapk.util.FactoryReset.factoryResetSetting;
import static com.example.hu.mediaplayerapk.util.PickerUtil.resizeNumberPicker;
import static com.example.hu.mediaplayerapk.util.PickerUtil.resizePikcer;
import static com.example.hu.mediaplayerapk.util.SPUtils.putInt;

public class AdvancedSettingPop implements View.OnKeyListener, View.OnFocusChangeListener, View.OnClickListener {

    private Activity mActivity;
    private LayoutInflater layoutInflater;
    private TextView dataTextView;
    private TextView factoryTextView;
    //    private TextView ecoModeTextView;
    private TextView beaconModeTextView;
    private TextView firmWareVersionTextView;
    private TextView imageSaveTextView;
    private Handler quitHandler;

    public AdvancedSettingPop(Activity mActivity, Handler handler) {
        this.mActivity = mActivity;
        this.layoutInflater = LayoutInflater.from(mActivity);
        this.quitHandler = handler;
    }

    private PopupWindow advancedSettingPop;

    public void showAdvancedSettingPop(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_setting_pop, null);
        advancedSettingPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 40 * screenWidthRatio);

        initAdvancedSettingView(contentView);
        advancedSettingPop.setTouchable(true);
        advancedSettingPop.setFocusable(true);
        advancedSettingPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        advancedSettingPop.showAsDropDown(view, xPos, (int) (-250 * screenHeightRatio));
        advancedSettingPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_detail_setting));
            }
        });
    }

    private void initAdvancedSettingView(View contentView) {
        dataTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Data);
        TextView timeTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Time);
        TextView displayRatioTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Display_Ratio);
        factoryTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Factory);
        firmWareVersionTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Firmware_Version);
        TextView languageTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Language);
        TextView resetTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_Reset);
//        ecoModeTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_eco_mode);
        beaconModeTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_beacon);
//        TextView faceCheckTextView = (TextView) contentView.findViewById(R.id.tv_advanced_setting_face_check);
        imageSaveTextView = contentView.findViewById(R.id.tv_advanced_setting_image_save);
        imageSaveTextView.setOnKeyListener(this);
        dataTextView.setOnKeyListener(this);
        displayRatioTextView.setOnKeyListener(this);
        factoryTextView.setOnKeyListener(this);
        firmWareVersionTextView.setOnKeyListener(this);
        languageTextView.setOnKeyListener(this);
        resetTextView.setOnKeyListener(this);
        timeTextView.setOnKeyListener(this);
//        faceCheckTextView.setOnKeyListener(this);
//        ecoModeTextView.setOnKeyListener(this);
        beaconModeTextView.setOnKeyListener(this);

        dataTextView.setOnClickListener(this);
        displayRatioTextView.setOnClickListener(this);
        factoryTextView.setOnClickListener(this);
        firmWareVersionTextView.setOnClickListener(this);
        languageTextView.setOnClickListener(this);
        resetTextView.setOnClickListener(this);
        timeTextView.setOnClickListener(this);
//        faceCheckTextView.setOnClickListener(this);
//        ecoModeTextView.setOnClickListener(this);
        beaconModeTextView.setOnClickListener(this);
        imageSaveTextView.setOnClickListener(this);

        dataTextView.setOnFocusChangeListener(this);
        displayRatioTextView.setOnFocusChangeListener(this);
        factoryTextView.setOnFocusChangeListener(this);
        firmWareVersionTextView.setOnFocusChangeListener(this);
        languageTextView.setOnFocusChangeListener(this);
        resetTextView.setOnFocusChangeListener(this);
        timeTextView.setOnFocusChangeListener(this);
        imageSaveTextView.setOnFocusChangeListener(this);
//        faceCheckTextView.setOnFocusChangeListener(this);
//        ecoModeTextView.setOnFocusChangeListener(this);
        beaconModeTextView.setOnFocusChangeListener(this);
        updateECOMode();
    }

    private void updateECOMode() {
       /* if (SPUtils.getInt(mActivity, Config.CHECK_FACE_STATE) == 1 || SPUtils.getInt(mActivity, Config.CHECK_FACE_STATE) == 2) {
            ecoModeTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            ecoModeTextView.setFocusableInTouchMode(true);
            ecoModeTextView.setFocusable(true);
        } else {
            ecoModeTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_white));
            ecoModeTextView.setFocusableInTouchMode(false);
            ecoModeTextView.setFocusable(false);
        }*/
    }

    /*date----------------------------------------------------------------*/
    private TextView yearTextView, monthTextView, dayTextView;
    private int tempYear, tempMonth, tempDay;
    private PopupWindow datePop;
    private PopupWindow dateTouchPop;

    private void showDate(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_date_pop, null);
        datePop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = location[0] + view.getWidth();
        int yPos = (int) (location[1] - 3 * view.getHeight() + 30 * screenHeightRatio);

        int date[] = TimeUtil.getDate();
        yearTextView = (TextView) contentView.findViewById(R.id.tv_advanced_data_year);
        monthTextView = (TextView) contentView.findViewById(R.id.tv_advanced_data_month);
        dayTextView = (TextView) contentView.findViewById(R.id.tv_advanced_data_day);
        tempYear = date[0];
        tempMonth = date[1] + 1;
        tempDay = date[2];

        yearTextView.setOnKeyListener(this);
        monthTextView.setOnKeyListener(this);
        dayTextView.setOnKeyListener(this);

        yearTextView.setOnClickListener(this);
        monthTextView.setOnClickListener(this);
        dayTextView.setOnClickListener(this);

        initDateView();
        datePop.setTouchable(true);
        datePop.setFocusable(true);
        datePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        datePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void initDateView() {
        int maxDay;
        if (tempMonth == 1 || tempMonth == 3 || tempMonth == 5 || tempMonth == 7 ||
                tempMonth == 8 || tempMonth == 10 || tempMonth == 12) {
            maxDay = 31;
        } else if (tempMonth == 2) {
            if (tempYear % 4 == 0) {
                maxDay = 29;
            } else {
                maxDay = 28;
            }
        } else {
            maxDay = 30;
        }
        if (tempMonth < 10) {
            monthTextView.setText("0" + tempMonth + "");
        } else {
            monthTextView.setText(tempMonth + "");
        }
        if (tempDay < 10) {
            dayTextView.setText("0" + tempDay + "");
        } else {
            if (tempDay > maxDay) {
                tempDay = maxDay;
            }
            dayTextView.setText(tempDay + "");
        }
        yearTextView.setText(tempYear + "");
    }

    private void showTouchDatePop() {
        View contentView = layoutInflater.inflate(R.layout.date_pick_pop, null);
        dateTouchPop = new PopupWindow(contentView, (int) (600 * screenWidthRatio), (int) (400 * screenHeightRatio), true);
        int[] location = new int[2];
        dataTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] + 400 * screenWidthRatio);
        int yPos = (int) (location[1] - 163 * screenHeightRatio);

        DatePicker datePicker = (DatePicker) contentView.findViewById(R.id.date_picker);
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker.setCalendarViewShown(false);
        resizePikcer(datePicker, mActivity);
        int date[] = TimeUtil.getDate();
        tempYear = date[0];
        tempMonth = date[1] + 1;
        tempDay = date[2];
        datePicker.init(tempYear, tempMonth - 1, tempDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                tempDay = dayOfMonth;
                tempMonth = monthOfYear + 1;
                tempYear = year;
            }
        });
        Button button = (Button) contentView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTouchPop.dismiss();
                try {
                    TimeUtil.setDate(tempYear, tempMonth, tempDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((TextView) contentView.findViewById(R.id.textView)).setText(R.string.set_date);
        (contentView.findViewById(R.id.textView)).setVisibility(View.GONE);
        dateTouchPop.setTouchable(true);
        dateTouchPop.setFocusable(true);
        dateTouchPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black_gray));
        dateTouchPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*date----------------------------------------------------------------*/

    /*time----------------------------------------------------------------*/
    private TextView hourTextView, minuteTextView, secondTextView;
    private int tempHour, tempMinute, tempSecond;
    private PopupWindow timePop;
    private PopupWindow timeTouchPop;

    private void showTime(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_time_pop, null);
        timePop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = location[0] + view.getWidth();
        int yPos = (int) (location[1] - 3 * view.getHeight() + 30 * screenHeightRatio);

        int date[] = TimeUtil.getTime();
        hourTextView = (TextView) contentView.findViewById(R.id.tv_advanced_time_hour);
        minuteTextView = (TextView) contentView.findViewById(R.id.tv_advanced_time_minute);
        secondTextView = (TextView) contentView.findViewById(R.id.tv_advanced_time_second);
        tempHour = date[0];
        tempMinute = date[1];
        tempSecond = date[2];

        hourTextView.setOnKeyListener(this);
        minuteTextView.setOnKeyListener(this);
        secondTextView.setOnKeyListener(this);

        hourTextView.setOnClickListener(this);
        minuteTextView.setOnClickListener(this);
        secondTextView.setOnClickListener(this);

        initTimeView();
        timePop.setTouchable(true);
        timePop.setFocusable(true);
        timePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        timePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void initTimeView() {
        if (tempHour < 10) {
            hourTextView.setText("0" + tempHour + "");
        } else {
            hourTextView.setText(tempHour + "");
        }
        if (tempMinute < 10) {
            minuteTextView.setText("0" + tempMinute + "");
        } else {
            minuteTextView.setText(tempMinute + "");
        }
        if (tempSecond < 10) {
            secondTextView.setText("0" + tempSecond);
        } else {
            secondTextView.setText("" + tempSecond);
        }
    }

    private void showTouchTimePop() {
        View contentView = layoutInflater.inflate(R.layout.time_pick_with_second_pop, null);
        timeTouchPop = new PopupWindow(contentView, (int) (600 * screenWidthRatio), (int) (400 * screenHeightRatio), true);
        int[] location = new int[2];
        dataTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] + 400 * screenWidthRatio);
        int yPos = (int) (location[1] - 163 * screenHeightRatio);

        int date[] = TimeUtil.getTime();
        tempHour = date[0];
        tempMinute = date[1];
        tempSecond = date[2];

        TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.time_picker);
        resizePikcer(timePicker, mActivity);
        timePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(tempHour);
        timePicker.setCurrentMinute(tempMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                tempHour = hourOfDay;
                tempMinute = minute;
            }
        });

        NumberPicker numberPicker = (NumberPicker) contentView.findViewById(R.id.number_picker);
        resizeNumberPicker(numberPicker, mActivity);
        numberPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(59);
        numberPicker.setValue(tempSecond);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                tempSecond = newVal;
            }
        });

        Button button = (Button) contentView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTouchPop.dismiss();
                try {
                    TimeUtil.setTime(tempHour, tempMinute, tempSecond);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ((TextView) contentView.findViewById(R.id.textView)).setText(R.string.set_time);
        contentView.findViewById(R.id.textView).setVisibility(View.GONE);
        timeTouchPop.setTouchable(true);
        timeTouchPop.setFocusable(true);
        timeTouchPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black_gray));
        timeTouchPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*time-----------------------------------------------------------------*/

    /*DisplayRatio---------------------------------------------------------*/
    private PopupWindow displayRatioPop;

    private void showDisplayRatio(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_display_ratio_pop, null);
        displayRatioPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        contentView.findViewById(R.id.tv_advanced_display_ratio_uniform).setOnKeyListener(this);
        contentView.findViewById(tv_advanced_display_ratio_full).setOnKeyListener(this);

        contentView.findViewById(R.id.tv_advanced_display_ratio_uniform).setOnClickListener(this);
        contentView.findViewById(tv_advanced_display_ratio_full).setOnClickListener(this);

        contentView.findViewById(R.id.tv_advanced_display_ratio_uniform).setOnFocusChangeListener(this);
        contentView.findViewById(tv_advanced_display_ratio_full).setOnFocusChangeListener(this);

        if (SPUtils.getString(mActivity, Config.DISPLAY_RATIO).equals("full")) {
            ((TextView) contentView.findViewById(R.id.tv_advanced_display_ratio_uniform))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            ((TextView) contentView.findViewById(tv_advanced_display_ratio_full))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        } else {
            ((TextView) contentView.findViewById(R.id.tv_advanced_display_ratio_uniform))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            ((TextView) contentView.findViewById(tv_advanced_display_ratio_full))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        }
        displayRatioPop.setFocusable(true);
        displayRatioPop.setTouchable(true);
        displayRatioPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        displayRatioPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
        displayRatioPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Display_Ratio_Setting));
            }
        });
    }
    /*DisplayRatio---------------------------------------------------------*/

    /*Language-------------------------------------------------------------*/
    private PopupWindow languagePop;

    private void showLanguagePop(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_language_pop, null);
        languagePop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        contentView.findViewById(R.id.tv_advanced_language_english).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_advanced_language_chinese).setOnKeyListener(this);
        contentView.findViewById(R.id.tv_advanced_language_english).setOnClickListener(this);
        contentView.findViewById(R.id.tv_advanced_language_chinese).setOnClickListener(this);

        if (SPUtils.getString(mActivity, Config.LANGUAGE).equals("english")) {
            ((TextView) contentView.findViewById(R.id.tv_advanced_language_english))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            ((TextView) contentView.findViewById(R.id.tv_advanced_language_chinese))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        } else {
            ((TextView) contentView.findViewById(R.id.tv_advanced_language_english))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            ((TextView) contentView.findViewById(R.id.tv_advanced_language_chinese))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }

        languagePop.setFocusable(true);
        languagePop.setTouchable(true);
        languagePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        languagePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void switchLanguage(String string) {
        Resources resources = mActivity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if (!string.equals("chinese")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.CHINESE;
        }
        resources.updateConfiguration(config, dm);
        SPUtils.putString(mActivity, "language", string);
    }
    /*Language--------------------------------------------------------------*/

    /*reset-----------------------------------------------------------------*/
    private boolean confirmReset = false;
    private TextView resetHourTextView;
    private PopupWindow resetPop;
    private int oldResetHour;
    private PopupWindow resetTouchPop;

    private void showReset(View view) {
        confirmReset = false;
        View contentView = layoutInflater.inflate(R.layout.advanced_reset_pop, null);
        resetPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 10 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        contentView.findViewById(R.id.layout_advance_reset_hour).setOnKeyListener(this);
        contentView.findViewById(R.id.layout_advance_reset_hour).setOnClickListener(this);
        resetHourTextView = (TextView) contentView.findViewById(R.id.tv_advance_reset_hour);
        TextView switchTextView = (TextView) contentView.findViewById(R.id.tv_advanced_reset_switch);
        switchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putInt(mActivity, Config.RESET_ON, 0);
                //RestartDevice.removeRestartAlarm(mActivity);
                resetPop.dismiss();
            }
        });
        switchTextView.setOnKeyListener(this);
        oldResetHour = SPUtils.getInt(mActivity, Config.RESET_HOUR);
        int restart = SPUtils.getInt(mActivity, Config.RESET_ON);
        if (restart == 0) { //1为开
            switchTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            resetHourTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        } else {
            switchTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            resetHourTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }
        if (oldResetHour < 0) {
            oldResetHour = 3;
            putInt(mActivity, Config.RESET_HOUR, 3);
        }
        if (oldResetHour < 10) {
            resetHourTextView.setText("0" + oldResetHour + "");
        } else {
            resetHourTextView.setText("" + oldResetHour + "");
        }
        resetPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        resetPop.setFocusable(true);
        resetPop.setTouchable(true);
        resetPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void showTouchReset() {
        oldResetHour = SPUtils.getInt(mActivity, Config.RESET_HOUR);
        View contentView = layoutInflater.inflate(R.layout.reboot_hour_pop, null);
        resetTouchPop = new PopupWindow(contentView, (int) (300 * screenWidthRatio), (int) (350 * screenHeightRatio), true);
        int[] location = new int[2];
        dataTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] + 580 * screenWidthRatio);
        int yPos = (location[1]);

        NumberPicker numberPicker = (NumberPicker) contentView.findViewById(R.id.number_picker);
        resizeNumberPicker(numberPicker, mActivity);
        numberPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(23);
        numberPicker.setValue(oldResetHour);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                oldResetHour = newVal;
                if (oldResetHour < 10) {
                    resetHourTextView.setText("0" + oldResetHour + "");
                } else {
                    resetHourTextView.setText("" + oldResetHour + "");
                }
            }
        });

        Button button = (Button) contentView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTouchPop.dismiss();
                putInt(mActivity, Config.RESET_HOUR, oldResetHour);
                putInt(mActivity, Config.RESET_ON, 1);
            }
        });
        ((TextView) contentView.findViewById(R.id.textView)).setText(R.string.set_reboot_hour);
        (contentView.findViewById(R.id.textView)).setVisibility(View.GONE);
        resetTouchPop.setTouchable(true);
        resetTouchPop.setFocusable(true);
        resetTouchPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black_gray));
        resetTouchPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*reset-----------------------------------------------------------------*/

    /*firmwareVersion------------------------------------------------------*/
    private PopupWindow firmwareVersionPop;

    private String serialNumber = null;

    private void showFirmwareVersion(View view) {

        View contentView = layoutInflater.inflate(R.layout.advanced_firmware_version_pop, null);
        firmwareVersionPop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 30 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        TextView appVersionTextView = (TextView) contentView.findViewById(R.id.tv_advanced_firmware_version_app);
        appVersionTextView.setText("App:" + getPackageName(mActivity));

        TextView androidVersionTextView = (TextView) contentView.findViewById(R.id.tv_advanced_firmware_version_android);
        androidVersionTextView.setText("Android:" + android.os.Build.VERSION.RELEASE);

        TextView serialNumTextView = (TextView) contentView.findViewById(tv_advanced_firmware_version_serial);
        serialNumTextView.setText("SN:" + serialNumber);

        serialNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmwareVersionPop.dismiss();
            }
        });
        appVersionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmwareVersionPop.dismiss();
            }
        });
        androidVersionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmwareVersionPop.dismiss();
            }
        });

        serialNumTextView.setOnKeyListener(this);
        appVersionTextView.setOnKeyListener(this);
        androidVersionTextView.setOnKeyListener(this);

        firmwareVersionPop.setTouchable(true);
        firmwareVersionPop.setFocusable(true);
        firmwareVersionPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        firmwareVersionPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private String getPackageName(Context context) {
        serialNumber = getSerialNumber();
        String serialNumberSize = "14";
        String serialNumberType = "RS";
        String serialNumberTemp = "AAAA";
        if (serialNumber != null && serialNumber.length() > 4) {
            serialNumberTemp = serialNumber.substring(0, 4);
            serialNumberSize = serialNumber.substring(3, 5);
            serialNumberType = serialNumber.substring(1, 3);
        }
        String name;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            name = pi.versionName;
            switch (serialNumberTemp) {
                case "PW14":
                    name = name.replaceFirst("14RS", "14S");
                    break;
                case "PB14":
                    name = name.replaceFirst("14RS", "14S");
                    break;
                case "RS14":
                    name = name.replaceFirst("14RS", "14RS");
                    break;
                case "PZ07":
                    name = name.replaceFirst("14RS", "Z07");
                    break;
                case "PZ10":
                    name = name.replaceFirst("14RS", "Z10");
                    break;
                case "PW07":
                    name = name.replaceFirst("14RS", "W07");
                    break;
                case "PW10":
                    name = name.replaceFirst("14RS", "W10");
                    break;
                case "PW19":
                    name = name.replaceFirst("14RS", "W19");
                    break;
                case "PA07":
                    name = name.replaceFirst("14RS", "A07");
                    break;
                case "PA10":
                    name = name.replaceFirst("14RS", "A10");
                    break;
                case "PB07":
                    name = name.replaceFirst("14RS", "B07");
                    break;
                case "PB10":
                    name = name.replaceFirst("14RS", "B10");
                    break;
                case "PB19":
                    name = name.replaceFirst("14RS", "B19");
                    break;
                case "Z19B":
                    name = name.replaceFirst("14RS", "19UT");
                    break;
                case "IS70":
                    name = name.replaceFirst("14RS", "S7");
                    break;
                case "ISS0":
                    name = name.replaceFirst("14RS", "S7S");
                    break;
                case "IST0":
                    name = name.replaceFirst("14RS", "S7T");
                    break;
                default:
                    name = name.replaceFirst("14RS", serialNumberSize + serialNumberType);
                    break;
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*firmwareVersion------------------------------------------------------*/

    /*face_check-------------------------------------------------------------*/
    private void showFaceCheck(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_face_check_pop, null);
        final PopupWindow faceCheckPop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        TextView offTextView = (TextView) contentView.findViewById(R.id.tv_advanced_check_face_off);
        TextView nearTextView = (TextView) contentView.findViewById(R.id.tv_advanced_check_face_near);
//        TextView midTextView = (TextView) contentView.findViewById(R.id.tv_advanced_check_face_mid);

        offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        nearTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
//        midTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));

        if (SPUtils.getInt(mActivity, Config.CHECK_FACE_STATE) == 1) {
            nearTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }/* else if (SPUtils.getInt(mActivity, Config.CHECK_FACE_STATE) == 2) {
            midTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }*/ else {
            offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }

        offTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCheckPop.dismiss();
                putInt(mActivity, Config.CHECK_FACE_STATE, 0);
                putInt(mActivity, Config.ECO_MODE_STATE, -1);
                updateECOMode();
            }
        });
        nearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCheckPop.dismiss();
                putInt(mActivity, Config.CHECK_FACE_STATE, 1);
                updateECOMode();
            }
        });
        /*midTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCheckPop.dismiss();
                putInt(mActivity, Config.CHECK_FACE_STATE, 2);
                updateECOMode();
            }
        });*/
        faceCheckPop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        faceCheckPop.setFocusable(true);
        faceCheckPop.setTouchable(true);
        faceCheckPop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*face_check-------------------------------------------------------------*/

    /*eco_mode---------------------------------------------------------------*/
    private PopupWindow ecoModePop;
    private TextView onTextView;
    private TextView offTextView;

    private void showECOMode(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_eco_mode_pop, null);
        ecoModePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        onTextView = (TextView) contentView.findViewById(R.id.tv_advanced_eco_mode_on);
        offTextView = (TextView) contentView.findViewById(R.id.tv_advanced_eco_mode_off);
        if (SPUtils.getInt(mActivity, Config.ECO_MODE_STATE) >= 0) {
            onTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        } else {
            offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            onTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        }
        onTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               ecoTimePop.dismiss();
                SPUtils.putInt(mActivity, Config.ECO_MODE_STATE, 1);
                ecoModePop.dismiss();
                // showECOTime(v);
            }
        });
        offTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecoModePop.dismiss();
                putInt(mActivity, Config.ECO_MODE_STATE, -1);
            }
        });
        ecoModePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        ecoModePop.setFocusable(true);
        ecoModePop.setTouchable(true);
        ecoModePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*eco_mode---------------------------------------------------------------*/

    private void showECOTime(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_eco_time_pop, null);
        final PopupWindow ecoTimePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        TextView time0TextView = (TextView) contentView.findViewById(R.id.tv_advanced_eco_time_0);
        TextView time30TextView = (TextView) contentView.findViewById(R.id.tv_advanced_eco_time_30);
        TextView time60TextView = (TextView) contentView.findViewById(R.id.tv_advanced_eco_time_60);
        time60TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        time0TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        time30TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        if (SPUtils.getInt(mActivity, Config.ECO_MODE_STATE) == 60) {
            time60TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        } else if (SPUtils.getInt(mActivity, Config.ECO_MODE_STATE) == 30) {
            time30TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        } else if (SPUtils.getInt(mActivity, Config.ECO_MODE_STATE) == 0) {
            time0TextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }
        time0TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecoTimePop.dismiss();
                SPUtils.putInt(mActivity, Config.ECO_MODE_STATE, 0);
                if (offTextView != null && onTextView != null) {
                    onTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
                    offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                }
            }
        });
        time30TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecoTimePop.dismiss();
                putInt(mActivity, Config.ECO_MODE_STATE, 30);
                if (offTextView != null && onTextView != null) {
                    onTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
                    offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                }
            }
        });
        time60TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecoTimePop.dismiss();
                putInt(mActivity, Config.ECO_MODE_STATE, 60);
                if (offTextView != null && onTextView != null) {
                    onTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
                    offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                }
            }
        });
        ecoTimePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        ecoTimePop.setFocusable(true);
        ecoTimePop.setTouchable(true);
        ecoTimePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    /*beacon_mode---------------------------------------------------------------*/
    private PopupWindow beaconModePop;
    private TextView beaconOnTextView;
    private TextView beaconOffTextView;

    private void showBeaconMode(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_beacon_mode_pop, null);
        beaconModePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        beaconOnTextView = (TextView) contentView.findViewById(R.id.tv_advanced_beacon_mode_on);
        beaconOffTextView = (TextView) contentView.findViewById(R.id.tv_advanced_beacon_mode_off);
        if (SPUtils.getInt(mActivity, Config.BEACON_MODE_STATE) < 0) {
            beaconOnTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            beaconOffTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        } else {
            beaconOffTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
            beaconOnTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        }
        beaconOnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putInt(mActivity, Config.BEACON_MODE_STATE, -1);
                beaconModePop.dismiss();
            }
        });
        beaconOffTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconModePop.dismiss();
                putInt(mActivity, Config.BEACON_MODE_STATE, 1);
            }
        });
        beaconModePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        beaconModePop.setFocusable(true);
        beaconModePop.setTouchable(true);
        beaconModePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*beacon_mode---------------------------------------------------------------*/

    /*image_save---------------------------------------------------------------*/
    private void showImageSave(View view) {
        View contentView = layoutInflater.inflate(R.layout.advanced_face_check_pop, null);
        final PopupWindow saveImagePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 20 * screenWidthRatio);
        int yPos = (int) (location[1] - 3 * view.getHeight() + 25 * screenHeightRatio);

        TextView offTextView = (TextView) contentView.findViewById(R.id.tv_advanced_check_face_off);
        TextView nearTextView = (TextView) contentView.findViewById(R.id.tv_advanced_check_face_near);
        offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        nearTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        if (SPUtils.getInt(mActivity, Config.SAVE_IMAGE_STATE) == 0) {
            offTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        } else {
            nearTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.yellow));
        }
        offTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImagePop.dismiss();
                putInt(mActivity, Config.SAVE_IMAGE_STATE, 0);
            }
        });
        nearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImagePop.dismiss();
                putInt(mActivity, Config.SAVE_IMAGE_STATE, 1);
            }
        });
        saveImagePop.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.color.black));
        saveImagePop.setFocusable(true);
        saveImagePop.setTouchable(true);
        saveImagePop.showAsDropDown(mActivity.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /*image_save---------------------------------------------------------------*/

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                //main
                case R.id.tv_advanced_setting_Data: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        factoryTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        advancedSettingPop.dismiss();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        showDate(v);
                        return true;
                    }
                }
                break;
                case R.id.tv_advanced_setting_Time: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        advancedSettingPop.dismiss();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        showTime(v);
                        return true;
                    }
                }
                break;
                case R.id.tv_advanced_setting_Display_Ratio:
                case R.id.tv_advanced_setting_Firmware_Version:
                case R.id.tv_advanced_setting_Language:
                case R.id.tv_advanced_setting_Reset:
//                case R.id.tv_advanced_setting_eco_mode:
                case R.id.tv_advanced_setting_image_save:
                case R.id.tv_advanced_setting_beacon: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        advancedSettingPop.dismiss();
                        return true;
                    }
                }
                break;
                case R.id.tv_advanced_setting_Factory: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        dataTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        advancedSettingPop.dismiss();
                        return true;
                    }
                }
                break;
               /* case R.id.tv_advanced_setting_face_check: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        advancedSettingPop.dismiss();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (SPUtils.getInt(mActivity, Config.CHECK_FACE_STATE) == 0) {
                            firmWareVersionTextView.requestFocus();
                            return true;
                        }
                    }
                }
                break;*/

                //dateSetting-------------------------------------------------------------------
                case R.id.tv_advanced_data_day:
                    int maxDay;
                    if (tempMonth == 1 || tempMonth == 3 || tempMonth == 5 || tempMonth == 7 ||
                            tempMonth == 8 || tempMonth == 10 || tempMonth == 12) {
                        maxDay = 31;
                    } else if (tempMonth == 2) {
                        if (tempYear % 4 == 0) {
                            maxDay = 29;
                        } else {
                            maxDay = 28;
                        }
                    } else {
                        maxDay = 30;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (tempDay >= maxDay) {
                            tempDay = 1;
                        } else {
                            tempDay++;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (tempDay <= 1) {
                            tempDay = maxDay;
                        } else {
                            tempDay--;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        monthTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        yearTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        try {
                            TimeUtil.setDate(tempYear, tempMonth, tempDay);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        datePop.dismiss();
                    }
                    initDateView();
                    return true;
                case R.id.tv_advanced_data_year:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        tempYear++;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        tempYear--;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        dayTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        monthTextView.requestFocus();
                    }
                    initDateView();
                    return true;
                case R.id.tv_advanced_data_month:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (tempMonth >= 12) {
                            tempMonth = 1;
                        } else {
                            tempMonth++;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (tempMonth <= 1) {
                            tempMonth = 12;
                        } else {
                            tempMonth--;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        yearTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        dayTextView.requestFocus();
                    }
                    initDateView();
                    return true;
                //timeSetting
                case R.id.tv_advanced_time_hour:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (tempHour >= 23) {
                            tempHour = 0;
                        } else {
                            tempHour++;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (tempHour <= 0) {
                            tempHour = 23;
                        } else {
                            tempHour--;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        secondTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        minuteTextView.requestFocus();
                    }
                    initTimeView();
                    return true;
                case R.id.tv_advanced_time_minute:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (tempMinute >= 59) {
                            tempMinute = 0;
                        } else {
                            tempMinute++;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (tempMinute <= 0) {
                            tempMinute = 59;
                        } else {
                            tempMinute--;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        hourTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        secondTextView.requestFocus();
                    }
                    initTimeView();
                    return true;
                case R.id.tv_advanced_time_second:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (tempSecond >= 59) {
                            tempSecond = 0;
                        } else {
                            tempSecond++;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (tempSecond <= 0) {
                            tempSecond = 59;
                        } else {
                            tempSecond--;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        minuteTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        hourTextView.requestFocus();
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        try {
                            TimeUtil.setTime(tempHour, tempMinute, tempSecond);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        timePop.dismiss();
                        return true;
                    }
                    initTimeView();
                    return true;
                //displayRatio
                case tv_advanced_display_ratio_full:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        displayRatioPop.dismiss();
                        return true;
                    }
                    break;
                case R.id.tv_advanced_display_ratio_uniform:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        displayRatioPop.dismiss();
                        return true;
                    }
                    break;
                //language
                case R.id.tv_advanced_language_english:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        languagePop.dismiss();
                        return true;
                    }
                    break;
                case R.id.tv_advanced_language_chinese:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        languagePop.dismiss();
                        return true;
                    }
                    break;
                //reset
                case R.id.layout_advance_reset_hour: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (confirmReset) {
                            if (oldResetHour >= 23) {
                                oldResetHour = 0;
                            } else {
                                oldResetHour++;
                            }
                            if (oldResetHour < 10) {
                                resetHourTextView.setText("0" + oldResetHour + "");
                            } else {
                                resetHourTextView.setText("" + oldResetHour + "");
                            }
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (confirmReset) {
                            if (oldResetHour <= 0) {
                                oldResetHour = 23;
                            } else {
                                oldResetHour--;
                            }
                            if (oldResetHour < 10) {
                                resetHourTextView.setText("0" + oldResetHour + "");
                            } else {
                                resetHourTextView.setText("" + oldResetHour + "");
                            }
                        } else {
                            return false;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (confirmReset) {
                            confirmReset = false;
                        } else {
                            resetPop.dismiss();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (confirmReset) {
                            resetPop.dismiss();
                            putInt(mActivity, Config.RESET_HOUR, oldResetHour);
                            putInt(mActivity, Config.RESET_ON, 1);
                        }
                        confirmReset = !confirmReset;
                    }
                    return true;
                }
                case R.id.tv_advanced_reset_switch:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        resetPop.dismiss();
                        return true;
                    }
                    break;
                //firmware
                case R.id.tv_advanced_firmware_version_android:
                case R.id.tv_advanced_firmware_version_app:
                case R.id.tv_advanced_firmware_version_serial:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        firmwareVersionPop.dismiss();
                        return true;
                    }
                    break;
                case 0:
                    break;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.tv_advanced_setting_Data:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Data_Setting));
                    break;
                case R.id.tv_advanced_setting_Time:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Time_Setting));
                    break;
                case R.id.tv_advanced_setting_Display_Ratio:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Display_Ratio_Setting));
                    break;
                case R.id.tv_advanced_setting_Factory:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Factory_Setting));
                    break;
                case R.id.tv_advanced_setting_Firmware_Version:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Firmware_Version));
                    break;
                case R.id.tv_advanced_setting_Language:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Language));
                    break;
                case R.id.tv_advanced_setting_Reset:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Reset_Setting));
                    break;
                case R.id.tv_advanced_display_ratio_full:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_full));
                    break;
                case R.id.tv_advanced_display_ratio_uniform:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_uniform));
                    break;
               /* case R.id.tv_advanced_setting_face_check:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Face_check));
                    break;
                case R.id.tv_advanced_setting_eco_mode:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_ECO_mode));
                    break;*/
                case R.id.tv_advanced_setting_beacon:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_Beacon_mode));
                    break;
                case R.id.tv_advanced_setting_image_save:
                    OSDSettingActivity.initIntroduce(mActivity.getString(R.string.introduce_image_save));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        switch (v.getId()) {
            //main
            case R.id.tv_advanced_setting_Data:
                showTouchDatePop();
                break;
            case R.id.tv_advanced_setting_Time:
                showTouchTimePop();
                break;
            case R.id.tv_advanced_setting_Display_Ratio:
                showDisplayRatio(v);
                break;
            case R.id.tv_advanced_setting_Factory:
                factoryResetSetting(mActivity);
                mActivity.finish();
                Intent intent = new Intent(mActivity, OSDSettingActivity.class);
                mActivity.startActivity(intent);
                advancedSettingPop.dismiss();
                break;
            case R.id.tv_advanced_setting_Firmware_Version:
                showFirmwareVersion(v);
                break;
            case R.id.tv_advanced_setting_Language:
                showLanguagePop(v);
                break;
            case R.id.tv_advanced_setting_Reset:
                showReset(v);
                break;
        /*    case R.id.tv_advanced_setting_eco_mode:
                showECOMode(v);
                break;*/
            case R.id.tv_advanced_setting_image_save:
                showImageSave(v);
                break;
            case R.id.tv_advanced_setting_beacon:
                showBeaconMode(v);
                break;
          /*  case R.id.tv_advanced_setting_face_check:
                showFaceCheck(v);
                break;*/

            //displayRatio
            case tv_advanced_display_ratio_full:
                SPUtils.putString(mActivity, Config.DISPLAY_RATIO, "full");
                displayRatioPop.dismiss();
                break;
            case R.id.tv_advanced_display_ratio_uniform:
                SPUtils.putString(mActivity, Config.DISPLAY_RATIO, "uniform");
                displayRatioPop.dismiss();
                break;
            //language
            case R.id.tv_advanced_language_english:
                switchLanguage("english");
                mActivity.finish();
                Intent it1 = new Intent(mActivity, OSDSettingActivity.class);
                mActivity.startActivity(it1);
                break;
            case R.id.tv_advanced_language_chinese:
                switchLanguage("chinese");
                mActivity.finish();
                Intent it2 = new Intent(mActivity, OSDSettingActivity.class);
                mActivity.startActivity(it2);
                break;
            //reset
            case R.id.layout_advance_reset_hour:
                showTouchReset();
                break;
            //firmware
            case R.id.tv_advanced_firmware_version_android:
            case R.id.tv_advanced_firmware_version_app:
            case R.id.tv_advanced_firmware_version_serial:
                if (firmwareVersionPop != null)
                    firmwareVersionPop.dismiss();
                break;
            case 0:
                break;
        }
    }
}
