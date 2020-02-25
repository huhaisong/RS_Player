
package com.example.hu.mediaplayerapk.model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.hu.mediaplayerapk.R.id.tv_selected_item4;
import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.PAGE_SIZE;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.currentNum;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.currentPage;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.isImpactTv;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.itemTextViewId;
import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.selectedVideos;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class VideoSelectedFileModel implements View.OnKeyListener, View.OnClickListener {
    private Context mContext;
    private int currentSelectedPage = 0;
    private int currentSelectedNum = 0;
    private int selectTotalPage = 0;
    private int[] selectedItemTextViewId = {R.id.tv_selected_item1, R.id.tv_selected_item2, R.id.tv_selected_item3, tv_selected_item4};
    private List<String> tempVideoList;
    private TextView ensureTextView, deleteTextView, cancelTextView;
    private View mContentView;

    public VideoSelectedFileModel(Context mContext, View mContentView) {
        this.mContext = mContext;
        tempVideoList = new ArrayList<>();
        this.mContentView = mContentView;
    }

    public void init() {
        initChooseVideos();
        initPreviewListener();
    }

    private void initChooseVideos() {
        mContentView.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
        tempVideoList.clear();
        if (existExternalSDCard) {
            if (isImpactTv == 0) {
                if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(
                            external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(tempVideoList, strings);
                    }
                    tempVideoList.remove("");
                }
            } else {
                if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(
                            external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(tempVideoList, strings);
                    }
                    tempVideoList.remove("");
                }
            }
        } else {
            if (isImpactTv == 0) {
                if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(tempVideoList, strings);
                    }
                    tempVideoList.remove("");

                }
            } else {
                if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(
                            internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(tempVideoList, strings);
                    }
                    tempVideoList.remove("");
                }
            }
        }
        Log.e(TAG, "initChooseVideos: " + tempVideoList.toString());
        ensureTextView = (TextView) mContentView.findViewById(R.id.tv_selected_ensure);
        cancelTextView = (TextView) mContentView.findViewById(R.id.tv_selected_cancel);
        deleteTextView = (TextView) mContentView.findViewById(R.id.tv_selected_delete);
        ensureTextView.setOnKeyListener(this);
        cancelTextView.setOnKeyListener(this);
        deleteTextView.setOnKeyListener(this);
        mContentView.findViewById(tv_selected_item4).setOnKeyListener(this);
        mContentView.findViewById(R.id.tv_selected_item1).setOnKeyListener(this);
        mContentView.findViewById(R.id.tv_selected_item2).setOnKeyListener(this);
        mContentView.findViewById(R.id.tv_selected_item3).setOnKeyListener(this);

        ensureTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);

        ensureTextView.setOnFocusChangeListener(menuFocusChangeListener);
        cancelTextView.setOnFocusChangeListener(menuFocusChangeListener);
        deleteTextView.setOnFocusChangeListener(menuFocusChangeListener);
        mContentView.findViewById(R.id.tv_selected_item4).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_selected_item1).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_selected_item2).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_selected_item3).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_selected_item4).setOnFocusChangeListener(selectedItemFocusChangeListener);
        mContentView.findViewById(R.id.tv_selected_item1).setOnFocusChangeListener(selectedItemFocusChangeListener);
        mContentView.findViewById(R.id.tv_selected_item2).setOnFocusChangeListener(selectedItemFocusChangeListener);
        mContentView.findViewById(R.id.tv_selected_item3).setOnFocusChangeListener(selectedItemFocusChangeListener);
        initSelectedListView();
    }

    private View.OnFocusChangeListener menuFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mContentView.findViewById(selectedItemTextViewId[currentSelectedNum]).setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.file_selected_item_sel));
            }
        }
    };

    private View.OnFocusChangeListener selectedItemFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                for (int i = 0; i < 4; i++) {
                    if (selectedItemTextViewId[i] == v.getId()) {
                        currentSelectedNum = i;
                    }
                    mContentView.findViewById(selectedItemTextViewId[i]).setBackground(
                            ContextCompat.getDrawable(mContext, R.drawable.file_selected_item_focused));
                }
            }
        }
    };

    private void initSelectedListView() {
        if (tempVideoList != null) {
            selectTotalPage = tempVideoList.size() / 4;
            if (tempVideoList.size() % 4 != 0) {
                selectTotalPage++;
            }
        }
        if (tempVideoList != null && tempVideoList.size() > 0) {
            for (int i = 0; i < 4; i++) {
                TextView textView = (TextView) mContentView.findViewById(selectedItemTextViewId[i]);
                if ((currentSelectedPage * 4 + i) < tempVideoList.size()) {
                    textView.setFocusable(true);
                    textView.setFocusableInTouchMode(true);
                    String path = tempVideoList.get(currentSelectedPage * 4 + i);
                    String temp[] = path.split("/");
                    String title = temp[temp.length - 1];
                    textView.setText(title);
                } else {
                    textView.setText("");
                    textView.setFocusable(false);
                    textView.setFocusableInTouchMode(false);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                TextView textView = (TextView) mContentView.findViewById(selectedItemTextViewId[i]);
                textView.setText("");
                textView.setFocusable(false);
                textView.setFocusableInTouchMode(false);
            }
        }
    }

    private void initPreviewListener() {
        for (int id : itemTextViewId) {
            mContentView.findViewById(id).setOnClickListener(previewItemListener);
        }
    }

    private View.OnClickListener previewItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < itemTextViewId.length; i++) {
                if (v.getId() == itemTextViewId[i])
                    currentNum = i;
            }
            if (selectedVideos.size() > 0) {
                tempVideoList.add(selectedVideos.get(currentPage * PAGE_SIZE + currentNum));
            }
            if (tempVideoList != null) {
                selectTotalPage = tempVideoList.size() / 4;
                if (tempVideoList.size() % 4 != 0) {
                    selectTotalPage++;
                }
            }
            currentSelectedPage = selectTotalPage - 1;
            initSelectedListView();
        }
    };

    private void saveToSDCard() {
        String string = listToString();
        if (existExternalSDCard) {
            if (isImpactTv == 0) {
                FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
            } else {
                FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
            }
        } else {
            if (isImpactTv == 0) {
                FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
            } else {
                FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
            }
        }
    }

    private String listToString() {
        String string = "";
        if (tempVideoList.size() > 0) {
            if (existExternalSDCard) {
                if (isImpactTv == 0) {
                    SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                } else {
                    SPUtils.putInt(mContext, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                }
            } else {
                if (isImpactTv == 0) {
                    SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                } else {
                    SPUtils.putInt(mContext, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                }
            }
            for (String item : tempVideoList) {
                string = string + item + ";";
            }
        }
        return string;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_selected_item4:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage < selectTotalPage - 1) {
                            currentSelectedPage++;
                            initSelectedListView();
                            mContentView.findViewById(R.id.tv_selected_item1).requestFocus();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        currentSelectedNum = 3;
                        ensureTextView.requestFocus();
                        return true;
                    }
                    break;
                case R.id.tv_selected_item1:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (currentSelectedPage > 0) {
                            currentSelectedPage--;
                            initSelectedListView();
                            mContentView.findViewById(tv_selected_item4).requestFocus();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        currentSelectedNum = 0;
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 == tempVideoList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_item2:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        currentSelectedNum = 1;
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 + 1 == tempVideoList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_item3:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        currentSelectedNum = 2;
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 + 2 == tempVideoList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_cancel:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        deleteTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                    break;
                case R.id.tv_selected_ensure:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        mContentView.findViewById(selectedItemTextViewId[currentSelectedNum]).requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        cancelTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                    break;
                case R.id.tv_selected_delete:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        cancelTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selected_item4:
                currentSelectedNum = 3;
                ensureTextView.requestFocus();
                break;
            case R.id.tv_selected_item1:
                currentSelectedNum = 0;
                ensureTextView.requestFocus();
                break;
            case R.id.tv_selected_item2:
                currentSelectedNum = 1;
                ensureTextView.requestFocus();
                break;
            case R.id.tv_selected_item3:
                currentSelectedNum = 2;
                ensureTextView.requestFocus();
                break;
            case R.id.tv_selected_cancel:
                ((Activity) mContext).finish();
                break;
            case R.id.tv_selected_ensure:
                saveToSDCard();
                ((Activity) mContext).finish();
                break;
            case R.id.tv_selected_delete:
                if (currentSelectedPage * 4 + currentSelectedNum == tempVideoList.size() - 1) {
                    tempVideoList.remove(currentSelectedPage * 4 + currentSelectedNum);
                    if (currentSelectedNum == 0) {
                        currentSelectedNum = 3;
                        currentSelectedPage--;
                        if (currentSelectedPage < 0) {
                            currentSelectedPage = 0;
                            currentSelectedNum = 0;
                        }
                    } else {
                        currentSelectedNum--;
                    }
                    mContentView.findViewById(selectedItemTextViewId[currentSelectedNum]).requestFocus();
                } else {
                    if (tempVideoList.size() > 0) {
                        tempVideoList.remove(currentSelectedPage * 4 + currentSelectedNum);
                    }
                }
                initSelectedListView();
                break;
        }
    }
}