package com.example.hu.mediaplayerapk.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.FileUtils;
import com.example.hu.mediaplayerapk.util.SPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.existExternalSDCard;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impacttv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.external_impactv_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_event_path;
import static com.example.hu.mediaplayerapk.application.MyApplication.internal_impactv_path;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PhotoActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener {

    private int currentSelectedPage = 0;
    private int currentSelectedNum = 0;
    private int selectedTotalPage = 0;
    private static final int PAGE_SIZE = 10;
    private int totalPage = 0;
    private int currentNum = 0;
    private int currentPage = 0;
    private List<String> selectedFileList = new ArrayList<>();
    private List<String> photos = new ArrayList<>();
    private int[] itemTextPhotoViewId = {R.id.tv_photo_item1, R.id.tv_photo_item2,
            R.id.tv_photo_item3, R.id.tv_photo_item4, R.id.tv_photo_item5, R.id.tv_photo_item6,
            R.id.tv_photo_item7, R.id.tv_photo_item8, R.id.tv_photo_item9, R.id.tv_photo_item10};

    private int[] itemSelectedViewId = {R.id.tv_selected_photo_item1, R.id.tv_selected_photo_item2,
            R.id.tv_selected_photo_item3, R.id.tv_selected_photo_item4};

    private TextView selectedItem1TextView, selectedItem2TextView, selectedItem3TextView, selectedItem4TextView;
    private TextView ensureTextView, cancelTextView, deleteTextView;

    private ImageView imageView;

    private boolean isImpactv = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo_select);


        String content = getIntent().getStringExtra("content");
        if (content.equals("impactv")) {
            isImpactv = true;
        } else if (content.equals("event")) {
            isImpactv = false;
        }

        currentPage = 0;
        imageView = (ImageView) findViewById(R.id.imageView_select_photo);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (SPUtils.getString(this, Config.DISPLAY_RATIO).equals("full")) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        existExternalSDCard = FileUtils.getSize(Config.EXTERNAL_FILE_ROOT_PATH) > 0;

        if (existExternalSDCard) {
            if (isImpactv) {
                if (FileUtils.checkHaveFile(external_impactv_path)) {
                    photos = getList(external_impactv_path);
                } else {
                    photos = getList(external_impacttv_path);
                }
            } else {
                photos = getList(external_event_path);
            }
        } else {
            if (isImpactv) {
                photos = getList(internal_impactv_path);
            } else {
                photos = getList(internal_event_path);
            }
        }
        totalPage = photos.size() / PAGE_SIZE;
        if (photos.size() % PAGE_SIZE != 0) {
            totalPage++;
        }
        getSelectedFile();
        initPreviewView();
        initPreviewListen();
        initSelectedListView();
        initSelectedListener();
    }

    private static final String TAG = "PhotoActivity";

    private void initPreviewView() {
        String fileName;
        if (isImpactv) {
            fileName = "Impactv ";
        } else {
            fileName = "Event ";
        }
        ((TextView) findViewById(R.id.tv_photo_page)).setText(fileName + (currentPage + 1) + "/" + totalPage);
        for (int i = 0; i < itemTextPhotoViewId.length; i++) {
            TextView textView = (TextView) findViewById(itemTextPhotoViewId[i]);
            if (textView != null) {
                if ((currentPage * PAGE_SIZE + i) < photos.size()) {
                    String[] strings = photos.get(i + currentPage * PAGE_SIZE).split("/");
                    String content = strings[strings.length - 1];
                    textView.setText(content);
                } else {
                    textView.setText("");
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
        for (int i = 0; i < itemTextPhotoViewId.length; i++) {
            final int finalI = i;
            findViewById(itemTextPhotoViewId[i]).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if ((currentPage * PAGE_SIZE + finalI) < photos.size()) {
                            Bitmap bitmap = null;
                            InputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(new File(photos.get(currentPage * PAGE_SIZE + finalI)));
                                bitmap = BitmapFactory.decodeStream(inputStream);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (OutOfMemoryError e) {
                                Toast.makeText(PhotoActivity.this, "the image is too large!", Toast.LENGTH_SHORT).show();
                            } finally {
                                try {
                                    inputStream.close();
                                    inputStream = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            imageView.setImageBitmap(bitmap);
                            if (bitmap != null && !bitmap.isRecycled()) {
                                // bitmap.recycle();
                                bitmap = null; // recycle()是个比较漫长的过程，设为null，然后在最后调用System.gc()，效果能好很多
                            }
                            System.gc();
                        } else {
                            findViewById(R.id.tv_photo_item1).requestFocus();
                            currentPage = 0;
                            if (photos.size() > 0) {
                                InputStream inputStream = null;
                                Bitmap bitmap = null;
                                try {
                                    inputStream = new FileInputStream(new File(photos.get(0)));
                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                    imageView.setImageBitmap(bitmap);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (OutOfMemoryError e) {
                                    Toast.makeText(PhotoActivity.this, "the image is too large!", Toast.LENGTH_SHORT).show();
                                } finally {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (bitmap != null && !bitmap.isRecycled()) {
                                    // bitmap.recycle();
                                    bitmap = null; // recycle()是个比较漫长的过程，设为null，然后在最后调用System.gc()，效果能好很多
                                }
                                System.gc();
                                initPreviewView();
                            }
                        }
                    }
                }
            });
        }
        findViewById(R.id.tv_photo_item1).setOnKeyListener(this);
        findViewById(R.id.tv_photo_item10).setOnKeyListener(this);

        for (int id : itemTextPhotoViewId) {
            findViewById(id).setOnClickListener(itemListen);
        }
    }

    private void initSelectedListener() {
        selectedItem1TextView = (TextView) findViewById(R.id.tv_selected_photo_item1);
        selectedItem2TextView = (TextView) findViewById(R.id.tv_selected_photo_item2);
        selectedItem3TextView = (TextView) findViewById(R.id.tv_selected_photo_item3);
        selectedItem4TextView = (TextView) findViewById(R.id.tv_selected_photo_item4);
        ensureTextView = (TextView) findViewById(R.id.tv_selected_photo_ensure);
        cancelTextView = (TextView) findViewById(R.id.tv_selected_photo_cancel);
        deleteTextView = (TextView) findViewById(R.id.tv_selected_photo_delete);
        selectedItem1TextView.setOnKeyListener(this);
        selectedItem2TextView.setOnKeyListener(this);
        selectedItem3TextView.setOnKeyListener(this);
        selectedItem4TextView.setOnKeyListener(this);
        ensureTextView.setOnKeyListener(this);
        cancelTextView.setOnKeyListener(this);
        deleteTextView.setOnKeyListener(this);

        selectedItem1TextView.setOnClickListener(this);
        selectedItem2TextView.setOnClickListener(this);
        selectedItem3TextView.setOnClickListener(this);
        selectedItem4TextView.setOnClickListener(this);
        ensureTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);

        selectedItem1TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem2TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem3TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        selectedItem4TextView.setOnFocusChangeListener(selectedItemFocusChangeListener);
        ensureTextView.setOnFocusChangeListener(menuFocusChangeListener);
        cancelTextView.setOnFocusChangeListener(menuFocusChangeListener);
        deleteTextView.setOnFocusChangeListener(menuFocusChangeListener);
    }

    private View.OnFocusChangeListener menuFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                findViewById(itemSelectedViewId[currentSelectedNum]).setBackground(
                        ContextCompat.getDrawable(PhotoActivity.this, R.drawable.file_selected_item_sel));
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
                            ContextCompat.getDrawable(PhotoActivity.this, R.drawable.file_selected_item_focused));
                }
            }
        }
    };

    private View.OnClickListener itemListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for (int i = 0; i < itemTextPhotoViewId.length; i++) {
                if (v.getId() == itemTextPhotoViewId[i])
                    currentNum = i;
            }
            if (photos.size() > 0) {
                selectedFileList.add(photos.get(currentPage * PAGE_SIZE + currentNum));
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

    private List<String> getList(String path) {
        List<String> list = new ArrayList<>();
        File[] files = FileUtils.getPhoto(new File(path));
        if (files != null && files.length > 0) {
            for (File item : files) {
                list.add(item.getAbsolutePath());
            }
        }
        list = FileUtils.orderList(list);
        return list;
    }

    void getSelectedFile() {
        selectedFileList.clear();
        if (existExternalSDCard) {
            if (isImpactv) {
                if (!FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(selectedFileList, strings);
                    }
                    selectedFileList.remove("");
                }
            } else {
                if (!FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(selectedFileList, strings);
                    }
                    selectedFileList.remove("");
                }
            }
        } else {
            if (isImpactv) {
                if (!FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(selectedFileList, strings);
                    }
                    selectedFileList.remove("");
                }
            } else {
                if (!FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).equals("")) {
                    String[] strings = FileUtils.readTextLine(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME).split(";");
                    if (strings.length > 0) {
                        Collections.addAll(selectedFileList, strings);
                    }
                    selectedFileList.remove("");
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
                    FileUtils.saveTxtFile(external_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
                } else {
                    FileUtils.saveTxtFile(external_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
                }
            } else {
                if (isImpactv) {
                    FileUtils.saveTxtFile(internal_impactv_path + File.separator + Config.IMPACTV_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
                } else {
                    FileUtils.saveTxtFile(internal_event_path + File.separator + Config.EVENT_MIX_PROGRAM_FILE_LIST_FILE_NAME, string);
                }
            }
        }
    }

    private String listToString() {
        String string = "";
        if (selectedFileList.size() > 0) {
            if (existExternalSDCard) {
                if (isImpactv) {
                    SPUtils.putInt(this, Config.EXTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                } else {
                    SPUtils.putInt(this, Config.EXTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                }
            } else {
                if (isImpactv) {
                    SPUtils.putInt(this, Config.INTERNAL_PLAY_BACK_MODE_IMPACTV, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                } else {
                    SPUtils.putInt(this, Config.INTERNAL_PLAY_BACK_MODE_EVENT, Config.PLAY_BACK_MODE_MIX_PROGRAM);
                }
            }
            for (String item : selectedFileList) {
                string = string + item + ";";
            }
        }
        return string;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.tv_photo_item10:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (currentPage < totalPage - 1) {
                            currentPage++;
                            findViewById(R.id.tv_photo_item1).requestFocus();
                            initPreviewView();
                        } else if (currentPage == totalPage - 1) {
                            currentPage = 0;
                            findViewById(R.id.tv_photo_item1).requestFocus();
                            initPreviewView();
                        }
                        return true;
                    }
                    break;
                case R.id.tv_photo_item1:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (currentPage > 0) {
                            currentPage--;
                            findViewById(R.id.tv_photo_item10).requestFocus();
                            initPreviewView();
                        } else if (currentPage == 0) {
                            if (totalPage > 0) {
                                currentPage = totalPage - 1;
                                currentNum = (photos.size() - 1) % 10;
                                if (currentNum >= 0) {
                                    findViewById(itemTextPhotoViewId[currentNum]).requestFocus();
                                }
                                initPreviewView();
                            }
                        }
                        return true;
                    }
                    break;
                case R.id.tv_selected_photo_item1:
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
                case R.id.tv_selected_photo_item2:
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
                case R.id.tv_selected_photo_item3:
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
                case R.id.tv_selected_photo_item4:
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


                case R.id.tv_selected_photo_cancel:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        deleteTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        ensureTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (selectedFileList.size() > 0) {
                            findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                            return true;
                        } else {
                            return false;
                        }
                    }
                    break;
                case R.id.tv_selected_photo_ensure:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        cancelTextView.requestFocus();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (selectedFileList.size() > 0) {
                            findViewById(itemSelectedViewId[currentSelectedNum]).requestFocus();
                            return true;
                        }
                    }
                    break;
                case R.id.tv_selected_photo_delete:
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
                case R.id.tv_selected_photo_item1:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 0;
                    break;
                case R.id.tv_selected_photo_item2:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 1;
                    break;
                case R.id.tv_selected_photo_item3:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 2;
                    break;
                case R.id.tv_selected_photo_item4:
                    ensureTextView.requestFocus();
                    currentSelectedNum = 3;
                    break;
                case R.id.tv_selected_photo_cancel:
                    finish();
                    break;
                case R.id.tv_selected_photo_ensure:
                    saveToSDCard();
                    finish();
                    break;
                case R.id.tv_selected_photo_delete:
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
