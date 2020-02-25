package com.example.hu.mediaplayerapk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.media.MediaPlayerImp;
import com.example.hu.mediaplayerapk.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class BGMActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener {

    private MediaPlayerImp mediaPlayerImp;
    private int currentSelectedPage = 0;
    private int selectedTotalPage = 0;
    private static final int PAGE_SIZE = 10;
    private int totalPage = 0;
    private int currentNum = 0;
    private int currentPage = 0;
    private List<String> selectedFileList = new ArrayList<>();
    private List<String> songs = new ArrayList<>();
    private int[] itemPreviewId = {R.id.tv_bgm_item1, R.id.tv_bgm_item2,
            R.id.tv_bgm_item3, R.id.tv_bgm_item4, R.id.tv_bgm_item5, R.id.tv_bgm_item6,
            R.id.tv_bgm_item7, R.id.tv_bgm_item8, R.id.tv_bgm_item9, R.id.tv_bgm_item10};

    private int[] itemSelectedViewId = {R.id.tv_selected_bgm_item1, R.id.tv_selected_bgm_item2,
            R.id.tv_selected_bgm_item3, R.id.tv_selected_bgm_item4};

    private TextView selectedItem1TextView, selectedItem2TextView, selectedItem3TextView, selectedItem4TextView;
    private TextView ensureTextView, cancelTextView, deleteTextView;

    private int currentSelectedNum = 0;
    private boolean isImpactv = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String content = getIntent().getStringExtra("content");
        if (content.equals("impactv")) {
            isImpactv = true;
        } else if (content.equals("event")) {
            isImpactv = false;
        }

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        existExternalSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;
        mediaPlayerImp = new MediaPlayerImp(null, this);
        if (existExternalSDCard) {
            if (isImpactv) {
                if (FileUtils.checkHaveFile(external_impactv_path)) {
                    songs = getList(external_impactv_path);
                } else {
                    songs = getList(external_impacttv_path);
                }
            } else {
                songs = getList(external_event_path);
            }
        } else {
            if (isImpactv) {
                songs = getList(internal_impactv_path);
            } else {
                songs = getList(internal_event_path);
            }
        }
        currentPage = 0;
        getSelectedFile();
        initPreviewView();
        initSelectedListView();
        initPreviewListen();
        initSelectedListener();
    }

    private static final String TAG = "BGMActivity";

    private void initPreviewView() {

        String fileName;
        if (isImpactv) {
            fileName = "Impactv ";
        } else {
            fileName = "Event ";
        }
        if (songs.size() > 0) {
            mediaPlayerImp.stop();
            mediaPlayerImp.play(songs.get(0), false, true);
            ((TextView) findViewById(R.id.tv_bgm_page)).setText(fileName + (currentPage + 1) + "/" + totalPage);
        }

        for (int i = 0; i < itemPreviewId.length; i++) {
            if (songs != null) {
                if ((currentPage * PAGE_SIZE + i) < songs.size()) {
                    String[] strings = songs.get(i + currentPage * PAGE_SIZE).split("/");
                    String content = strings[strings.length - 1];
                    ((TextView) findViewById(itemPreviewId[i])).setText(content);
                } else {
                    ((TextView) findViewById(itemPreviewId[i])).setText("");
                }
            }
        }
    }

    private void initSelectedListView() {
        if (selectedFileList != null) {
            totalPage = selectedFileList.size() / 4;
            if (selectedFileList.size() % 4 != 0) {
                totalPage++;
            }
        }
        if (selectedFileList != null && selectedFileList.size() > 0) {
            for (int i = 0; i < 4; i++) {
                TextView textView = (TextView) findViewById(itemSelectedViewId[i]);
                if ((currentSelectedPage * 4 + i) < selectedFileList.size()) {
                    textView.setFocusable(true);
                    textView.setFocusableInTouchMode(true);
                    String path = selectedFileList.get(currentSelectedPage * 4 + i);
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
                TextView textView = (TextView) findViewById(itemSelectedViewId[i]);
                textView.setText("");
                textView.setFocusable(false);
                textView.setFocusableInTouchMode(false);
            }
        }
    }

    private void initPreviewListen() {
        for (int i = 0; i < itemPreviewId.length; i++) {
            final int finalI = i;
            findViewById(itemPreviewId[i]).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if ((currentPage * PAGE_SIZE + finalI) < songs.size()) {
                            mediaPlayerImp.play(songs.get(finalI + currentPage * PAGE_SIZE), false, true);
                        } else {
                            findViewById(R.id.tv_bgm_item1).requestFocus();
                            currentPage = 0;
                            if (songs.size() > 0) {
                                mediaPlayerImp.play(songs.get(0), false, true);
                                initPreviewView();
                            }
                        }
                    }
                }
            });
        }
        findViewById(R.id.tv_bgm_item1).setOnKeyListener(this);
        findViewById(R.id.tv_bgm_item10).setOnKeyListener(this);

        for (int id : itemPreviewId) {
            findViewById(id).setOnClickListener(itemListen);
        }
    }

    private void initSelectedListener() {

        selectedItem1TextView = (TextView) findViewById(R.id.tv_selected_bgm_item1);
        selectedItem2TextView = (TextView) findViewById(R.id.tv_selected_bgm_item2);
        selectedItem3TextView = (TextView) findViewById(R.id.tv_selected_bgm_item3);
        selectedItem4TextView = (TextView) findViewById(R.id.tv_selected_bgm_item4);
        ensureTextView = (TextView) findViewById(R.id.tv_selected_bgm_ensure);
        cancelTextView = (TextView) findViewById(R.id.tv_selected_bgm_cancel);
        deleteTextView = (TextView) findViewById(R.id.tv_selected_bgm_delete);
        selectedItem1TextView.setOnKeyListener(this);
        selectedItem2TextView.setOnKeyListener(this);
        selectedItem3TextView.setOnKeyListener(this);
        selectedItem4TextView.setOnKeyListener(this);
        ensureTextView.setOnKeyListener(this);
        deleteTextView.setOnKeyListener(this);
        cancelTextView.setOnKeyListener(this);

        selectedItem1TextView.setOnClickListener(this);
        selectedItem2TextView.setOnClickListener(this);
        selectedItem3TextView.setOnClickListener(this);
        selectedItem4TextView.setOnClickListener(this);
        ensureTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        selectedItem1TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem2TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem3TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem4TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        ensureTextView.setOnFocusChangeListener(menuFocusChangeListener);
        deleteTextView.setOnFocusChangeListener(menuFocusChangeListener);
        cancelTextView.setOnFocusChangeListener(menuFocusChangeListener);
    }

    private List<String> getList(String path) {
        List<String> list = new ArrayList<>();
        File[] files = FileUtils.getAudio(new File(path));
        if (files != null && files.length > 0) {
            for (File item : files) {
                list.add(item.getAbsolutePath());
            }
        }
        list = FileUtils.orderList(list);
        totalPage = list.size() / PAGE_SIZE;
        if (list.size() % PAGE_SIZE != 0) {
            totalPage++;
        }
        return list;
    }

    void getSelectedFile() {
        selectedFileList.clear();
        if (FileUtils.isSDCardEnable()) {
            if (existExternalSDCard) {
                if (isImpactv) {
                    if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).equals("")) {
                        String[] strings = FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).split(";");
                        if (strings.length > 0) {
                            Collections.addAll(selectedFileList, strings);
                        }
                    }
                } else {
                    if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).equals("")) {
                        String[] strings = FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).split(";");
                        if (strings.length > 0) {
                            Collections.addAll(selectedFileList, strings);
                        }
                    }
                }
            } else {
                if (isImpactv) {
                    if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).equals("")) {
                        String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME).split(";");
                        if (strings.length > 0) {
                            Collections.addAll(selectedFileList, strings);
                        }
                    }
                } else {
                    if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).equals("")) {
                        String[] strings = FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME).split(";");
                        if (strings.length > 0) {
                            Collections.addAll(selectedFileList, strings);
                        }
                    }
                }
            }
        }

        selectedTotalPage = 0;
        if (selectedFileList != null) {
            selectedTotalPage = selectedFileList.size() / 4;
            if (selectedFileList.size() % 4 != 0) {
                selectedTotalPage++;
            }
        }
    }

    private void saveToSDCard() {
        String string = listToString();
        if (FileUtils.isSDCardEnable()) {
            if (existExternalSDCard) {
                if (isImpactv) {
                    FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, string);
                } else {
                    FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, string);
                }
            } else {
                if (isImpactv) {
                    FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_BGM_FILE_LIST_FILE_NAME, string);
                } else {
                    FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_BGM_FILE_LIST_FILE_NAME, string);
                }
            }
        }
    }

    private String listToString() {
        String string = "";
        if (selectedFileList.size() > 0) {
            for (String item : selectedFileList) {
                string = string + item + ";";
            }
        }
        return string;
    }

    private View.OnClickListener itemListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for (int i = 0; i < itemPreviewId.length; i++) {
                if (v.getId() == itemPreviewId[i])
                    currentNum = i;
            }

            if (songs.size() > 0) {
                selectedFileList.add(songs.get(currentPage * PAGE_SIZE + currentNum));
            }
            if (selectedFileList != null) {
                selectedTotalPage = selectedFileList.size() / 4;
                if (selectedFileList.size() % 4 != 0) {
                    selectedTotalPage++;
                }
            }
            currentSelectedPage = selectedTotalPage - 1;
            if (currentSelectedPage < 0) {
                currentSelectedPage = 0;
            }
            initSelectedListView();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerImp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerImp.unPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerImp.close();
    }

    private View.OnFocusChangeListener menuFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                findViewById(itemSelectedViewId[currentSelectedNum]).setBackground(
                        ContextCompat.getDrawable(BGMActivity.this, R.drawable.file_selected_item_sel));
            }
        }
    };

    private View.OnFocusChangeListener selectedItemFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                for (int i = 0; i < 4; i++) {
                    if (itemSelectedViewId[i] == v.getId()) {
                        currentSelectedNum = i;
                    }
                    findViewById(itemSelectedViewId[i]).setBackground(
                            ContextCompat.getDrawable(BGMActivity.this, R.drawable.file_selected_item_focused));
                }
            }
        }
    };

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_bgm_item10:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentPage < totalPage - 1) {
                            currentPage++;
                            findViewById(R.id.tv_bgm_item1).requestFocus();
                            initPreviewView();
                        } else if (currentPage == totalPage - 1) {
                            currentPage = 0;
                            findViewById(R.id.tv_bgm_item1).requestFocus();
                            initPreviewView();
                        }
                        return true;
                    }
                case R.id.tv_bgm_item1:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (currentPage > 0) {
                            currentPage--;
                            findViewById(R.id.tv_bgm_item10).requestFocus();
                            initPreviewView();
                        } else if (currentPage == 0) {
                            if (totalPage > 0) {
                                currentPage = totalPage - 1;
                                currentNum = (songs.size() - 1) % 10;
                                if (currentNum >= 0) {
                                    findViewById(itemPreviewId[currentNum]).requestFocus();
                                }
                                initPreviewView();
                            }
                        }
                        return true;
                    }
                    break;

                case R.id.tv_selected_bgm_item1:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (currentSelectedPage > 0) {
                            currentSelectedPage--;
                            initSelectedListView();
                            selectedItem4TextView.requestFocus();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        ensureTextView.requestFocus();
                        currentSelectedNum = 0;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 == selectedFileList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_bgm_item2:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        ensureTextView.requestFocus();
                        currentSelectedNum = 1;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 + 1 == selectedFileList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_bgm_item3:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        ensureTextView.requestFocus();
                        currentSelectedNum = 2;
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage * 4 + 2 == selectedFileList.size() - 1) {
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_bgm_item4:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentSelectedPage < selectedTotalPage - 1) {
                            currentSelectedPage++;
                            initSelectedListView();
                            selectedItem1TextView.requestFocus();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        ensureTextView.requestFocus();
                        currentSelectedNum = 3;
                        return true;
                    }
                    break;


                case R.id.tv_selected_bgm_cancel:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        deleteTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (selectedFileList.size() > 0) {
                            findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                    break;
                case R.id.tv_selected_bgm_ensure:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (selectedFileList.size() > 0) {
                            findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        cancelTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                    break;
                case R.id.tv_selected_bgm_delete:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        cancelTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (selectedFileList.size() > 0) {
                            findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                            return true;
                        }
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        {
            switch (v.getId()) {
                case R.id.tv_selected_bgm_item1:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 0;
                    break;
                case R.id.tv_selected_bgm_item2:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 1;
                    break;
                case R.id.tv_selected_bgm_item3:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 2;
                    break;
                case R.id.tv_selected_bgm_item4:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 3;
                    break;
                case R.id.tv_selected_bgm_cancel:
                    finish();
                    break;
                case R.id.tv_selected_bgm_ensure:
                    saveToSDCard();
                    finish();
                    break;
                case R.id.tv_selected_bgm_delete:
                    if (currentSelectedPage * 4 + currentSelectedNum == selectedFileList.size() - 1) {
                        selectedFileList.remove(currentSelectedPage * 4 + currentSelectedNum);
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
                        findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                    } else {
                        if (selectedFileList.size() > 0) {
                            selectedFileList.remove(currentSelectedPage * 4 + currentSelectedNum);
                        }
                    }
                    initSelectedListView();
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
//        Intent intent = new Intent(this, OSDSettingActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}
