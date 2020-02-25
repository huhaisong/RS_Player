package com.example.hu.mediaplayerapk.ui.popupWindow;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.bean.WorkTimer;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity;
import com.example.hu.mediaplayerapk.ui.widget.WheelView;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.screenHeightRatio;
import static com.example.hu.mediaplayerapk.application.MyApplication.screenWidthRatio;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITMESSAGE;
import static com.example.hu.mediaplayerapk.ui.activity.OSDSettingActivity.QUITTIME;
import static com.example.hu.mediaplayerapk.util.PickerUtil.resizePikcer;

//import android.widget.LinearLayout;

/**
 * 工作时间设置的popupWindow
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class TimeSettingPop implements View.OnClickListener, View.OnKeyListener, View.OnTouchListener {

    private View parent;
    private Activity mContext;

    private LayoutInflater layoutInflater;
    private PopupWindow timeSettingPop;
    private PopupWindow timeSettingWorkTimePop;
    private PopupWindow timeSettingHolidayPop;

    private int[] dayString = {R.string.everyday, R.string.Monday, R.string.Tuesday, R.string.Wednesday,
            R.string.Thursday, R.string.Friday, R.string.Saturday, R.string.Sunday};
    private int[] confirm = {R.string.ensure, R.string.delete, R.string.cancel};
    private int confirmId = 0;
    private View timeSettingContentView;
    private LinearLayout timeSettingItemLayout;
    private Handler quitHandler;
    private static final String TAG = "TimeSettingPop";

    public TimeSettingPop(Activity mContext, Handler handler) {
        this.mContext = mContext;
        this.quitHandler = handler;
        layoutInflater = LayoutInflater.from(mContext);
        workTimers = getWorkTimers();
        if (workTimers == null) {
            workTimers = new ArrayList<>();
        }
        holidays = getHolidays();
        if (holidays == null) {
            holidays = new ArrayList<>();
        }
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
    }

    private boolean is_showTimeSettingPop = true;

    public void showTimeSettingPop(final View view) {

        is_showTimeSettingPop = true;
        parent = view;
        timeSettingContentView = layoutInflater.inflate(R.layout.time_setting_pop, null);
        timeSettingContentView.findViewById(R.id.tv_time_setting_workTime).setOnClickListener(this);
        timeSettingContentView.findViewById(R.id.tv_time_setting_holiday).setOnClickListener(this);
        timeSettingContentView.findViewById(R.id.tv_time_setting_workTime).setOnKeyListener(this);
        timeSettingContentView.findViewById(R.id.tv_time_setting_holiday).setOnKeyListener(this);
        timeSettingContentView.findViewById(R.id.tv_time_setting_workTime).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_time_setting_work_time));
                }
            }
        });

        timeSettingContentView.findViewById(R.id.tv_time_setting_holiday).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_time_setting_holiday_time));
                }
            }
        });

        timeSettingItemLayout = (LinearLayout) timeSettingContentView.findViewById(R.id.time_setting_item_layout);

        if (workTimers != null && workTimers.size() > 0) {
            for (final WorkTimer item : workTimers) {
                String time = timeFormat(item);
                addWorkTimeItemTextView(timeSettingItemLayout, item.getDay(), time, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeSettingPop.dismiss();
                        oldWorkTimer = item;
                        showWorkTimePop(item);
                        editWorkTime = true;
                        OSDSettingActivity.initIntroduce(null);
                    }
                });
            }
        }

        if (holidays != null && holidays.size() > 0) {
            for (final int day : holidays) {
                addHolidayItemTextView(timeSettingItemLayout, day, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeSettingItemLayout.setVisibility(View.GONE);
                        oldHoliday = day;
                        showHolidayPop(oldHoliday);
                        editHoliday = true;
                        OSDSettingActivity.initIntroduce(null);
                    }
                });
            }
        }

        timeSettingPop = new PopupWindow(timeSettingContentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xPos = (int) (location[0] + view.getWidth() + 40 * screenWidthRatio);

        timeSettingPop.setTouchable(true);
        timeSettingPop.setFocusable(true);
        timeSettingPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        timeSettingPop.showAsDropDown(view, xPos, (int) (-130 * screenHeightRatio));
        timeSettingPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (is_showTimeSettingPop) {
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_timer_setting));
                }
            }
        });
    }

    //增加已经设置好的TimeWork;
    private void addWorkTimeItemTextView(LinearLayout layout, int day, String time, View.OnClickListener onClickListener) {

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setBackgroundResource(R.drawable.time_holiday_selected_item_focused);
        LayoutParams layoutParams = new LayoutParams((int) (677 * screenWidthRatio), (int) (76 * screenHeightRatio));
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setOnClickListener(onClickListener);

        TextView textView = new TextView(mContext);
        textView.setText(dayString[day]);
        if (day == 6) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        } else if (day == 7) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.0f));
        textView.setFocusable(false);
        textView.setFocusableInTouchMode(false);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.ts24));
        linearLayout.addView(textView);

        TextView textView1 = new TextView(mContext);
        textView1.setText(time);
        textView1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.5f));
        textView1.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        textView1.setFocusable(false);
        textView1.setFocusableInTouchMode(false);
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.ts24));
        linearLayout.addView(textView1);
        linearLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_time_setting_work_time_));
                }
            }
        });
        layout.addView(linearLayout);
    }

    //增加已经设置好的Holiday;
    private void addHolidayItemTextView(LinearLayout layout, int day, View.OnClickListener onClickListener) {

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setBackgroundResource(R.drawable.time_holiday_selected_item_focused);
        LayoutParams layoutParams = new LayoutParams((int) (677 * screenWidthRatio), (int) (76 * screenHeightRatio));
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.setOnClickListener(onClickListener);

        TextView textView = new TextView(mContext);
        textView.setText(mContext.getString(dayString[day]));
        if (day == 6) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        } else if (day == 7) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.0f));
        textView.setFocusable(false);
        textView.setFocusableInTouchMode(false);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.ts24));
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);

        TextView textView1 = new TextView(mContext);
        String s = mContext.getResources().getString(R.string.Holiday);
        textView1.setText(s);
        textView1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.5f));
        textView1.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        textView1.setFocusable(false);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.ts24));
        textView1.setFocusableInTouchMode(false);
        textView1.setGravity(Gravity.CENTER);
        linearLayout.addView(textView1);
        linearLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OSDSettingActivity.initIntroduce(mContext.getString(R.string.introduce_time_setting_holiday_time_));
                }
            }
        });
        layout.addView(linearLayout);
    }

    /* about WorkTime -----------------------------------------------------------------------------*/
    private List<WorkTimer> workTimers;
    private boolean editWorkTime = false;
    private WorkTimer oldWorkTimer;
    private int startHour = 0;
    private int startMinute = 0;
    private int endHour = 0;
    private int endMinute = 0;
    private int dayId = 0;
    private TextView dayTextView, startHourTextView, startMinuteTextView, endHourTextView,
            endMinuteTextView, worktimeConfirmTextView;
    private GestureDetector mGestureDetector;
    private PopupWindow startTimePop;
    private PopupWindow endTimePop;
    private PopupWindow dayPop;
    private PopupWindow workTimeConfirmPop;

    //显示WorkTime
    private void showWorkTimePop(WorkTimer item) {
        confirmId = dayId = startHour = startMinute = endMinute = endHour = 0;
        View contentView = layoutInflater.inflate(R.layout.time_setting_work_time_pop, null);
        timeSettingWorkTimePop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];

        timeSettingContentView.getLocationOnScreen(location);
        int xPos = (int) (location[0] + 900 * screenWidthRatio);
        int yPos = (int) (location[1] - 60 * screenHeightRatio);
        contentView.findViewById(R.id.tv_time_setting_workTime_day).requestFocus();
        initWorkTimeView(contentView, item);
        timeSettingWorkTimePop.setTouchable(true);
        timeSettingWorkTimePop.setFocusable(true);
        timeSettingWorkTimePop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        timeSettingWorkTimePop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, location[1]);

        timeSettingWorkTimePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                showTimeSettingPop(parent);
            }
        });
    }

    //初始化WorkTime相关View
    private void initWorkTimeView(View contentView, WorkTimer workTimer) {
        dayTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_day);
        worktimeConfirmTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_confirm);
        endHourTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_end_hour);
        endMinuteTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_end_minute);
        startHourTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_start_hour);
        startMinuteTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_workTime_start_minute);

        dayTextView.setFocusable(true);
        worktimeConfirmTextView.setFocusable(true);
        endHourTextView.setFocusable(true);
        endMinuteTextView.setFocusable(true);
        startMinuteTextView.setFocusable(true);
        startHourTextView.setFocusable(true);
        worktimeConfirmTextView.setFocusable(true);

        dayTextView.setFocusableInTouchMode(true);
        worktimeConfirmTextView.setFocusableInTouchMode(true);
        endHourTextView.setFocusableInTouchMode(true);
        endMinuteTextView.setFocusableInTouchMode(true);
        startMinuteTextView.setFocusableInTouchMode(true);
        startHourTextView.setFocusableInTouchMode(true);
        worktimeConfirmTextView.setFocusableInTouchMode(true);

        dayTextView.setOnKeyListener(this);
        worktimeConfirmTextView.setOnKeyListener(this);
        endHourTextView.setOnKeyListener(this);
        endMinuteTextView.setOnKeyListener(this);
        startMinuteTextView.setOnKeyListener(this);
        startHourTextView.setOnKeyListener(this);

        worktimeConfirmTextView.setOnClickListener(this);
        dayTextView.setOnClickListener(this);
        worktimeConfirmTextView.setOnClickListener(this);
        endHourTextView.setOnClickListener(this);
        endMinuteTextView.setOnClickListener(this);
        startMinuteTextView.setOnClickListener(this);
        startHourTextView.setOnClickListener(this);

        dayTextView.setOnTouchListener(this);
        worktimeConfirmTextView.setOnTouchListener(this);
        endHourTextView.setOnTouchListener(this);
        endMinuteTextView.setOnTouchListener(this);
        startMinuteTextView.setOnTouchListener(this);
        startHourTextView.setOnTouchListener(this);

        if (workTimer != null) {
            confirmId = 1;
            startHour = workTimer.getStartHour();
            startMinute = workTimer.getStartMinute();
            endHour = workTimer.getEndHour();
            endMinute = workTimer.getEndMinute();
            dayId = workTimer.getDay();
            initDay();
            initEndMinute();
            initStartMinute();
            initEndHour();
            initStartHour();
            initConfirm();
            worktimeConfirmTextView.requestFocus();
        } else {
            dayTextView.requestFocus();
        }
    }

    //将时间改成00:00格式
    private String timeFormat(WorkTimer item) {
        if (item == null) {
            return "";
        }
        String time;
        if (item.getStartHour() < 10) time = "0" + item.getStartHour() + ":";
        else time = item.getStartHour() + ":";

        if (item.getStartMinute() < 10) {
            time += "0" + item.getStartMinute() + "-";
        } else {
            time += item.getStartMinute() + "-";
        }
        if (item.getEndHour() < 10) {
            time += "0" + item.getEndHour() + ":";
        } else {
            time += item.getEndHour() + ":";
        }
        if (item.getEndMinute() < 10) {
            time += "0" + item.getEndMinute();
        } else {
            time += item.getEndMinute();
        }
        return time;
    }

    //获得已经保存的WorkTimeList
    private List<WorkTimer> getWorkTimers() {
        String json = FileUtils.readTextLine(Environment.getExternalStorageDirectory() + File.separator + Config.WORK_TIMER_FILE_PATH);
        // return JsonUtils.jsonToList(json, WorkTimer.class);
        return JsonUtils.jsonToList(json, WorkTimer.class);
    }

    //新增workTime
    private void saveWorkTimer() {

        WorkTimer workTimer = new WorkTimer();
        workTimer.setDay(dayId);
        workTimer.setStartHour(startHour);
        workTimer.setStartMinute(startMinute);
        workTimer.setEndHour(endHour);
        workTimer.setEndMinute(endMinute);
        confirmId = dayId = startHour = startMinute = endMinute = endHour = 0;
        workTimers.add(workTimer);
        if (editWorkTime) {
            workTimers.remove(oldWorkTimer);
        }
        String json = JsonUtils.listToJson(workTimers);
        FileUtils.saveTxtFile(Environment.getExternalStorageDirectory() + File.separator + Config.WORK_TIMER_FILE_PATH, json);
    }

    //保存workTimeList
    private void saveWorkTimerList() {
        String json = JsonUtils.listToJson(workTimers);
        FileUtils.saveTxtFile(Environment.getExternalStorageDirectory() + File.separator + Config.WORK_TIMER_FILE_PATH, json);
    }

    //更新DayTextView
    private void initDay() {
        dayId = dayId % 8;
        while (dayId < 0) {
            dayId += 8;
        }
        if (dayId == 6) {
            dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        } else if (dayId == 7) {
            dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else {
            dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        dayTextView.setText(mContext.getString(dayString[dayId % 8]));
    }

    //更新StartHourTextView
    private void initStartHour() {
        if (startHour < 10) {
            startHourTextView.setText("0" + startHour);
        } else {
            startHourTextView.setText(startHour + "");
        }
    }

    //更新StartMinuteTextView
    private void initStartMinute() {
        startMinute = startMinute % 60;
        while (startMinute < 0) {
            startMinute += 60;
        }
        if (startHour == 24) {
            startMinute = 0;
        }
        if (startMinute < 10) {
            startMinuteTextView.setText("0" + startMinute);
        } else {
            startMinuteTextView.setText(startMinute + "");
        }
    }

    //更新EndHourTextView
    private void initEndHour() {
        if (endHour < 10) {
            endHourTextView.setText("0" + endHour);
        } else {
            endHourTextView.setText(endHour + "");
        }
    }

    //更新EndMinuteTextView
    private void initEndMinute() {
        endMinute = endMinute % 60;
        while (endMinute < 0) {
            endMinute += 60;
        }
        if (endHour == 24) {
            endMinute = 0;
        }
        if (endMinute < 10) {
            endMinuteTextView.setText("0" + endMinute);
        } else {
            endMinuteTextView.setText(endMinute + "");
        }
    }

    //更新ConfirmTextView
    private void initConfirm() {
        confirmId = confirmId % 3;
        while (confirmId < 0) {
            confirmId += 3;
        }
        worktimeConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
    }

    private void showStartTimePop() {
        View contentView = layoutInflater.inflate(R.layout.time_pick_pop, null);
        startTimePop = new PopupWindow(contentView, (int) (700*screenWidthRatio), (int) (490 * screenHeightRatio), true);

        int[] location = new int[2];
        startMinuteTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] - 500 * screenWidthRatio);
        int yPos = (int) (location[1] - 335 * screenHeightRatio);
        TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.time_picker);
        resizePikcer(timePicker,mContext);
        timePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(startHour);
        timePicker.setCurrentMinute(startMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                startHour = hourOfDay;
                startMinute = minute;
                initStartHour();
                initStartMinute();
            }
        });
        Button button = (Button) contentView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePop.dismiss();
            }
        });
        ((TextView) contentView.findViewById(R.id.textView)).setText(R.string.set_start_time);
        startTimePop.setTouchable(true);
        startTimePop.setFocusable(true);
        startTimePop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black_gray));
        startTimePop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void showEndTimePop() {
        View contentView = layoutInflater.inflate(R.layout.time_pick_pop, null);
        endTimePop = new PopupWindow(contentView, (int) (700*screenWidthRatio), (int) (490 * screenHeightRatio), true);
        int[] location = new int[2];
        startMinuteTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] - 500 * screenWidthRatio);
        int yPos = (int) (location[1] - 335 * screenHeightRatio);
        TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.time_picker);
        resizePikcer(timePicker,mContext);
        timePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(endHour);
        timePicker.setCurrentMinute(endMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                endHour = hourOfDay;
                endMinute = minute;
                initEndHour();
                initEndMinute();
            }
        });
        Button button = (Button) contentView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimePop.dismiss();
            }
        });
        ((TextView) contentView.findViewById(R.id.textView)).setText(R.string.set_end_time);
        endTimePop.setTouchable(true);
        endTimePop.setFocusable(true);
        endTimePop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black_gray));
        endTimePop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void showDayPop() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view_pop, null);
        dayPop = new PopupWindow(contentView, (int) (700*screenWidthRatio), (int) (490 * screenHeightRatio), true);
        int[] location = new int[2];
        startMinuteTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] - 500 * screenWidthRatio);
        int yPos = (int) (location[1] - 335 * screenHeightRatio);
        ArrayList<String> list = new ArrayList<>();
        for (int aDayString : dayString) {
            list.add(mContext.getString(aDayString));
        }
        WheelView wv = (WheelView) contentView.findViewById(R.id.wheel_view_wv);
        wv.setItems(list);
        wv.setSeletion(dayId);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                dayId = selectedIndex - 1;
                initDay();
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                Log.d(TAG, "[Dialog]selectedIndex: " + (selectedIndex - 1) + ", item: " + item);
            }
        });
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayPop.dismiss();
            }
        });
        dayPop.setTouchable(true);
        dayPop.setFocusable(true);
        dayPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black_gray));
        dayPop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }

    private void showWorkTimeConfirmPop() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view_pop, null);
        workTimeConfirmPop = new PopupWindow(contentView, (int) (700*screenWidthRatio), (int) (490 * screenHeightRatio), true);
        int[] location = new int[2];
        startMinuteTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0] - 500 * screenWidthRatio);
        int yPos = (int) (location[1] - 335 * screenHeightRatio);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < confirm.length; i++) {
            list.add(mContext.getString(confirm[i]));
        }
        WheelView wv = (WheelView) contentView.findViewById(R.id.wheel_view_wv);
        wv.setItems(list);
        wv.setSeletion(confirmId);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                confirmId = selectedIndex - 1;
                initConfirm();
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
                Log.d(TAG, "[Dialog]selectedIndex: " + (selectedIndex - 1) + ", item: " + item);
            }
        });
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workTimeConfirmPop.dismiss();
                if (confirmId % 3 == 0) {
                    if (endHour < startHour) {
                        startHourTextView.requestFocus();
                    } else if (endHour == startHour) {
                        if (endMinute <= startMinute) {
                            startHourTextView.requestFocus();
                        } else {
                            saveWorkTimer();
                            timeSettingWorkTimePop.dismiss();
                        }
                    } else {
                        saveWorkTimer();
                        timeSettingWorkTimePop.dismiss();
                    }
                } else if (confirmId % 3 == 1) {
                    WorkTimer workTimer = new WorkTimer();
                    workTimer.setEndMinute(endMinute);
                    workTimer.setEndHour(endHour);
                    workTimer.setDay(dayId);
                    workTimer.setStartMinute(startMinute);
                    workTimer.setStartHour(startHour);
                    workTimers.remove(workTimer);
                    timeSettingWorkTimePop.dismiss();
                    saveWorkTimerList();
                } else if (confirmId % 3 == 2) {
                    timeSettingWorkTimePop.dismiss();
                }
            }
        });
        workTimeConfirmPop.setTouchable(true);
        workTimeConfirmPop.setFocusable(true);
        workTimeConfirmPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black_gray));
        workTimeConfirmPop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, yPos);

    }
    /* about WorkTime -----------------------------------------------------------------------------*/

    /* about Holiday---------------------------------------------------------------------------------*/
    private View holidayContentView;
    private final int[] holidayRID = {R.id.tv_time_setting_holiday_item1, R.id.tv_time_setting_holiday_item2,
            R.id.tv_time_setting_holiday_item3, R.id.tv_time_setting_holiday_item4, R.id.tv_time_setting_holiday_item5,
            R.id.tv_time_setting_holiday_item6, R.id.tv_time_setting_holiday_item7,};
    private TextView holidayConfirmTextView;
    private int tempHolidayId = -1;
    private Integer oldHoliday = -1;
    private ArrayList<Integer> holidays = new ArrayList<>();
    private boolean editHoliday = false;
    private PopupWindow holidayConfirmPop;

    //显示Holiday
    private void showHolidayPop(int day) {
        confirmId = 0;
        holidayContentView = layoutInflater.inflate(R.layout.time_setting_holiday_pop, null);
        timeSettingHolidayPop = new PopupWindow(holidayContentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int[] location = new int[2];
        timeSettingContentView.getLocationOnScreen(location);
        int xPos = (int) (location[0] + 410 * screenWidthRatio);
        int yPos = location[1];

        timeSettingHolidayPop.setTouchable(true);
        timeSettingHolidayPop.setFocusable(true);
        initHolidayView(holidayContentView, day);
        timeSettingHolidayPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black));
        timeSettingHolidayPop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout),
                xPos, yPos);

        timeSettingHolidayPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                timeSettingPop.dismiss();
                showTimeSettingPop(parent);
            }
        });
    }

    private class OnHolidayItemFocusChangeListener implements View.OnFocusChangeListener {

        private View contentView;

        OnHolidayItemFocusChangeListener(View contentView) {
            this.contentView = contentView;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                for (int i = 0; i < holidayRID.length; i++) {
                    if (holidayRID[i] == v.getId()) {
                        tempHolidayId = i + 1;
                    }
                    contentView.findViewById(holidayRID[i]).setBackground(
                            ContextCompat.getDrawable(mContext, R.drawable.holiday_item_focused));
                }
            }
        }
    }

    //初始化holiday相关View
    private void initHolidayView(View contentView, int tempDay) {
        holidayConfirmTextView = (TextView) contentView.findViewById(R.id.tv_time_setting_holiday_confirm);
        holidayConfirmTextView.setOnKeyListener(this);
        holidayConfirmTextView.setOnClickListener(this);
        holidayConfirmTextView.setOnTouchListener(this);
        holidayConfirmTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holidayContentView.findViewById(holidayRID[tempHolidayId - 1]).setBackground(
                            ContextCompat.getDrawable(mContext, R.drawable.file_selected_item_sel));
                }
            }
        });

        for (int id : holidayRID) {
            holidayContentView.findViewById(id).setOnKeyListener(this);
            holidayContentView.findViewById(id).setOnClickListener(this);
            holidayContentView.findViewById(id).setOnFocusChangeListener(new OnHolidayItemFocusChangeListener(contentView));
        }
        if (tempDay <= 0) {
            tempHolidayId = 1;
            editHoliday = false;
            contentView.findViewById(R.id.tv_time_setting_holiday_item1).requestFocus();
        } else {
            confirmId = 1;
            editHoliday = true;
            tempHolidayId = tempDay;
            holidayConfirmTextView.requestFocus();
            holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
            holidayContentView.findViewById(holidayRID[tempHolidayId - 1])
                    .setBackground(ContextCompat.getDrawable(mContext, R.drawable.holiday_item_sel));
        }
    }

    //获得已经保存的HolidayList
    private ArrayList<Integer> getHolidays() {
        ArrayList<Integer> list = new ArrayList<>();
        String content = FileUtils.readTextLine(Environment.getExternalStorageDirectory() + File.separator + Config.HOLIDAY_FILE_PATH);
        if (content.equals("")) {
            return null;
        }
        String[] holidays = content.split(",");
        for (String item : holidays) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }

    private void saveHolidays() {
        String content = "";
        for (Integer holiday : holidays) {
            content += holiday + ",";
        }
        FileUtils.saveTxtFile(Environment.getExternalStorageDirectory() + File.separator + Config.HOLIDAY_FILE_PATH, content);
    }

    private void showHolidayConfirmPop() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view_pop, null);
        holidayConfirmPop = new PopupWindow(contentView, (int) (300*screenWidthRatio), (int) (360 * screenHeightRatio), true);
        int[] location = new int[2];
        holidayConfirmTextView.getLocationOnScreen(location);
        int xPos = (int) (location[0]+230*screenWidthRatio);
        int yPos = (int) (location[1]-380*screenHeightRatio);
        ArrayList<String> list = new ArrayList<>();
        for (int aConfirm : confirm) {
            list.add(mContext.getString(aConfirm));
        }
        WheelView wv = (WheelView) contentView.findViewById(R.id.wheel_view_wv);
        wv.setItems(list);
        wv.setSeletion(confirmId);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                confirmId = selectedIndex - 1;
                holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                quitHandler.removeMessages(QUITMESSAGE);
                quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
            }
        });
        contentView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holidayConfirmPop.dismiss();
                if (editHoliday) {
                    if (confirmId % 3 == 0) {
                        holidays.remove(oldHoliday);
                        holidays.add(tempHolidayId);
                        saveHolidays();
                    } else if (confirmId % 3 == 1) {
                        holidays.remove(oldHoliday);
                        saveHolidays();
                    }
                } else {
                    if (confirmId % 3 == 0) {
                        holidays.add(tempHolidayId);
                        saveHolidays();
                    }
                }
                timeSettingHolidayPop.dismiss();
            }
        });
        holidayConfirmPop.setTouchable(true);
        holidayConfirmPop.setFocusable(true);
        holidayConfirmPop.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.black_gray));
        holidayConfirmPop.showAsDropDown(mContext.findViewById(R.id.timer_setting_layout), xPos, yPos);
    }
    /* about Holiday---------------------------------------------------------------------------------*/

    @Override
    public void onClick(View v) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        switch (v.getId()) {
            case R.id.tv_time_setting_holiday:
                editHoliday = false;
                timeSettingItemLayout.setVisibility(View.GONE);
                OSDSettingActivity.initIntroduce(null);
                // timeSettingPop.dismiss();
                showHolidayPop(-1);
                break;
            case R.id.tv_time_setting_workTime:
                OSDSettingActivity.initIntroduce(null);
                is_showTimeSettingPop = false;
                timeSettingPop.dismiss();
                showWorkTimePop(null);
                editWorkTime = false;
                break;
            case R.id.tv_time_setting_workTime_day:
                showDayPop();
                break;
            case R.id.tv_time_setting_workTime_start_hour:
                showStartTimePop();
                break;
            case R.id.tv_time_setting_workTime_start_minute:
                showStartTimePop();
                break;
            case R.id.tv_time_setting_workTime_end_hour:
                showEndTimePop();
                break;
            case R.id.tv_time_setting_workTime_end_minute:
                showEndTimePop();
                break;
            case R.id.tv_time_setting_workTime_confirm:
                showWorkTimeConfirmPop();
              /*  if (confirmId % 3 == 0) {
                    if (endHour < startHour) {
                        startHourTextView.requestFocus();
                    } else if (endHour == startHour) {
                        if (endMinute <= startMinute) {
                            startHourTextView.requestFocus();
                        } else {
                            saveWorkTimer();
                            timeSettingWorkTimePop.dismiss();
                        }
                    } else {
                        saveWorkTimer();
                        timeSettingWorkTimePop.dismiss();
                    }
                } else if (confirmId % 3 == 1) {
                    WorkTimer workTimer = new WorkTimer();
                    workTimer.setEndMinute(endMinute);
                    workTimer.setEndHour(endHour);
                    workTimer.setDay(dayId);
                    workTimer.setStartMinute(startMinute);
                    workTimer.setStartHour(startHour);
                    workTimers.remove(workTimer);
                    timeSettingWorkTimePop.dismiss();
                    saveWorkTimerList();
                } else if (confirmId % 3 == 2) {
                    timeSettingWorkTimePop.dismiss();
                }*/
                break;
            case R.id.tv_time_setting_holiday_item1:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 1;
                break;
            case R.id.tv_time_setting_holiday_item2:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 2;
                break;
            case R.id.tv_time_setting_holiday_item3:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 3;
                break;
            case R.id.tv_time_setting_holiday_item4:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 4;
                break;
            case R.id.tv_time_setting_holiday_item5:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 5;
                break;
            case R.id.tv_time_setting_holiday_item6:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 6;
                break;
            case R.id.tv_time_setting_holiday_item7:
                holidayConfirmTextView.requestFocus();
                tempHolidayId = 7;
                break;
            case R.id.tv_time_setting_holiday_confirm:
                showHolidayConfirmPop();
              /*  if (editHoliday) {
                    if (confirmId % 3 == 0) {
                        holidays.remove(oldHoliday);
                        holidays.add(tempHolidayId);
                        saveHolidays();
                    } else if (confirmId % 3 == 1) {
                        holidays.remove(oldHoliday);
                        saveHolidays();
                    }
                } else {
                    if (confirmId % 3 == 0) {
                        holidays.add(tempHolidayId);
                        saveHolidays();
                    }
                }
                timeSettingHolidayPop.dismiss();*/
                break;
            default:
                OSDSettingActivity.initIntroduce(null);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        tempTouchViewId = v.getId();
        Log.e(TAG, "onTouch: v = " + v.getId());
        //  mGestureDetector.onTouchEvent(event);
        return false;
    }

    private boolean isTouchChanged = false;
    private float oldX;

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldY = e1.getRawY();
            float mOldX = e1.getRawX();
            if (oldX != mOldX) {
                isTouchChanged = true;
                oldX = mOldX;
            }
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = mContext.getWindowManager().getDefaultDisplay();
            int windowHeight = disp.getHeight();
            int windowWidth = disp.getWidth();
            float percent = (mOldY - y) / (windowHeight);
            scrollChangeValue(percent);
            Log.e(TAG, "onScroll: percent = " + percent + ",mOldY = " + mOldY + ",y = " + y);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private int tempTouchViewId = 0;
    private int temp = 0;
    private static final int PERCENT_RATIO = 20;

    private void scrollChangeValue(float percent) {

        switch (tempTouchViewId) {
            case R.id.tv_time_setting_workTime_day:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = dayId;
                }
                dayId = (int) (temp + percent * PERCENT_RATIO);
                initDay();
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                break;
            case R.id.tv_time_setting_workTime_confirm:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = confirmId;
                }
                confirmId = (int) (temp + percent * PERCENT_RATIO);
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                initConfirm();
                break;
            case R.id.tv_time_setting_workTime_end_hour:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = endHour;
                }
                endHour = (int) (temp + percent * PERCENT_RATIO);
                endHour = endHour % 25;
                if (endHour < 0) {
                    if (endMinute > 0) {
                        endMinute = 0;
                        endHour++;
                    }
                }
                while (endHour < 0) {
                    endHour += 24;
                }
                if (endHour == 24) {
                    if (endMinute > 0) {
                        endMinute = 0;
                    }
                }
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                initEndHour();
                initEndMinute();
                break;
            case R.id.tv_time_setting_workTime_end_minute:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = endMinute;
                }
                endMinute = (int) (temp + percent * PERCENT_RATIO);
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                initEndMinute();
                break;
            case R.id.tv_time_setting_workTime_start_hour:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = startHour;
                }
                startHour = (int) (temp + percent * PERCENT_RATIO);
                startHour = startHour % 25;
                Log.e(TAG, "scrollChangeValue: startHour1" + startHour);
                if (startHour < 0) {
                    if (startMinute > 0) {
                        startMinute = 0;
                        startHour++;
                    }
                }
                Log.e(TAG, "scrollChangeValue: startHour2" + startHour);
                while (startHour < 0) {
                    startHour += 24;
                }
                if (startHour == 24) {
                    if (startMinute > 0) {
                        startMinute = 0;
                    }
                }
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                initStartHour();
                initStartMinute();
                break;
            case R.id.tv_time_setting_workTime_start_minute:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = startMinute;
                }
                startMinute = (int) (temp + percent * PERCENT_RATIO);
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                initStartMinute();
                break;
            case R.id.tv_time_setting_holiday_confirm:
                if (isTouchChanged) {
                    isTouchChanged = false;
                    temp = confirmId;
                }
                confirmId = (int) (temp + percent * PERCENT_RATIO);
                confirmId = confirmId % 3;
                while (confirmId < 0) {
                    confirmId += 3;
                }
                holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                Log.e(TAG, "scrollChangeValue: temp = " + temp);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        quitHandler.removeMessages(QUITMESSAGE);
        quitHandler.sendEmptyMessageDelayed(QUITMESSAGE, QUITTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_time_setting_workTime:
                case R.id.tv_time_setting_holiday:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        timeSettingPop.dismiss();
                        return true;
                    }
                    break;
                case R.id.tv_time_setting_workTime_day: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        dayId++;
                        dayId = dayId % 8;
                        initDay();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (dayId == 0) {
                            dayId = 8;
                        }
                        dayId--;
                        initDay();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        startHourTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        worktimeConfirmTextView.requestFocus();
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_workTime_start_hour: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (startHour < 23) {
                            startHour++;
                        } else if (startHour == 23) {
                            startHour = 0;
                        }
                        initStartHour();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (startHour > 0) {
                            startHour--;
                        } else if (startHour == 0) {
                            startHour = 23;
                        }
                        initStartHour();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        dayTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        startMinuteTextView.requestFocus();
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_workTime_start_minute: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (startMinute < 59) {
                            startMinute++;
                        } else if (startMinute == 59) {
                            startMinute = 0;
                        }
                        initStartMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (startMinute > 0) {
                            startMinute--;
                        } else if (startMinute == 0) {
                            startMinute = 59;
                        }
                        initStartMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        endHourTextView.requestFocus();
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_workTime_end_hour: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (endHour < 23) {
                            endHour++;
                        } else if (endHour == 23) {
                            if (endMinute > 0) {
                                endMinute = 0;
                                endHour = 24;
                            } else if (endMinute == 0) {
                                endHour = 24;
                            }
                        } else if (endHour == 24) {
                            endHour = 0;
                        }
                        initEndHour();
                        initEndMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (endHour > 0) {
                            endHour--;
                        } else if (endHour == 0) {
                            if (endMinute > 0) {
                                endMinute = 0;
                                endHour = 24;
                            } else if (endMinute == 0) {
                                endHour = 24;
                            }
                        }
                        initEndHour();
                        initEndMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        startMinuteTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        endMinuteTextView.requestFocus();
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_workTime_end_minute: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (endHour == 24) {
                            endMinute = 0;
                        } else if (endHour < 24) {
                            if (endMinute < 59) {
                                endMinute++;
                            } else if (endMinute == 59) {
                                endMinute = 0;
                            }
                        }
                        initEndMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (endHour == 24) {
                            endMinute = 0;
                        } else {
                            if (endMinute > 0) {
                                endMinute--;
                            } else if (endMinute == 0) {
                                endMinute = 59;
                            }
                        }
                        initEndMinute();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_ENTER) {
                        worktimeConfirmTextView.requestFocus();
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_workTime_confirm: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        confirmId++;
                        initConfirm();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        confirmId--;
                        initConfirm();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        dayTextView.requestFocus();
                        if (editWorkTime) {
                            confirmId = 0;
                            worktimeConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        endMinuteTextView.requestFocus();
                        if (editWorkTime) {
                            confirmId = 0;
                            worktimeConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (confirmId % 3 == 0) {
                            if (endHour < startHour) {
                                startHourTextView.requestFocus();
                            } else if (endHour == startHour) {
                                if (endMinute <= startMinute) {
                                    startHourTextView.requestFocus();
                                } else {
                                    saveWorkTimer();
                                    timeSettingWorkTimePop.dismiss();
                                }
                            } else {
                                saveWorkTimer();
                                timeSettingWorkTimePop.dismiss();
                            }
                        } else if (confirmId % 3 == 1) {
                            WorkTimer workTimer = new WorkTimer();
                            workTimer.setEndMinute(endMinute);
                            workTimer.setEndHour(endHour);
                            workTimer.setDay(dayId);
                            workTimer.setStartMinute(startMinute);
                            workTimer.setStartHour(startHour);
                            workTimers.remove(workTimer);
                            timeSettingWorkTimePop.dismiss();
                            saveWorkTimerList();
                        } else if (confirmId % 3 == 2) {
                            timeSettingWorkTimePop.dismiss();
                        }
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item1: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 1;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item2: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 2;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item3: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 3;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item4: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 4;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item5: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 5;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item6: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 6;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_item7: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        holidayConfirmTextView.requestFocus();
                        tempHolidayId = 7;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                break;
                case R.id.tv_time_setting_holiday_confirm: {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                        holidayContentView.findViewById(holidayRID[tempHolidayId - 1]).requestFocus();
                        holidayContentView.findViewById(holidayRID[tempHolidayId - 1])
                                .setBackground(ContextCompat.getDrawable(mContext, R.drawable.holiday_item_focused));
                        if (editHoliday) {
                            confirmId = 0;
                            holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        confirmId++;
                        confirmId = confirmId % 3;
                        holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (confirmId == 0) {
                            confirmId = 3;
                        }
                        confirmId--;
                        holidayConfirmTextView.setText(mContext.getString(confirm[confirmId % 3]));
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (editHoliday) {
                            if (confirmId % 3 == 0) {
                                holidays.remove(oldHoliday);
                                holidays.add(tempHolidayId);
                                saveHolidays();
                            } else if (confirmId % 3 == 1) {
                                holidays.remove(oldHoliday);
                                saveHolidays();
                            }
                        } else {
                            if (confirmId % 3 == 0) {
                                holidays.add(tempHolidayId);
                                saveHolidays();
                            }
                        }
                        timeSettingHolidayPop.dismiss();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
