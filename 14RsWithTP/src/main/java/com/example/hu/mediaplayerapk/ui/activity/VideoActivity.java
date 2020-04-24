package com.example.hu.mediaplayerapk.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.model.VideoOneFileModel;
import com.example.hu.mediaplayerapk.model.VideoPreViewModel;
import com.example.hu.mediaplayerapk.model.VideoSelectedFileModel;

import static com.example.hu.mediaplayerapk.model.VideoPreViewModel.isImpactTv;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class VideoActivity extends BaseActivity {

    private VideoPreViewModel videoPreViewModel;

    private PopupWindow popupWindow;

    public static boolean isPreView = false;
    private static final String TAG = "VideoPreViewActivity";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_preview_activity);
        String content = getIntent().getStringExtra("content");
        Log.e(TAG, "onCreate: content:" + content);

        final View contentView = LayoutInflater.from(VideoActivity.this).inflate(R.layout.preview_pop, null);
        contentView.findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                finish();
            }
        });

        view = findViewById(R.id.preview_content);
        handler.sendEmptyMessageDelayed(0, 2000);

        VideoOneFileModel videoOneFileModel;
        VideoSelectedFileModel videoSelectedFileModel;
        if (content == null || content.equals("null")) {
            videoPreViewModel = new VideoPreViewModel(VideoActivity.this, contentView, 0, handler);
            videoPreViewModel.init();
            isPreView = true;
        } else if (content.equals("video_impactv")) {
            videoPreViewModel = new VideoPreViewModel(VideoActivity.this, contentView, 0, handler);
            videoPreViewModel.init();
            videoOneFileModel = new VideoOneFileModel(VideoActivity.this, contentView);
            videoOneFileModel.init();
            isPreView = false;
        } else if (content.equals("video_event")) {
            videoPreViewModel = new VideoPreViewModel(VideoActivity.this, contentView, 1, handler);
            videoPreViewModel.init();
            videoOneFileModel = new VideoOneFileModel(VideoActivity.this, contentView);
            videoOneFileModel.init();
            isPreView = false;
        } else if (content.equals("videos_impactv")) {
            videoPreViewModel = new VideoPreViewModel(VideoActivity.this, contentView, 0, handler);
            videoPreViewModel.init();
            videoSelectedFileModel = new VideoSelectedFileModel(VideoActivity.this, contentView);
            videoSelectedFileModel.init();
            isPreView = false;
        } else if (content.equals("videos_event")) {
            videoPreViewModel = new VideoPreViewModel(VideoActivity.this, contentView, 1, handler);
            videoPreViewModel.init();
            videoSelectedFileModel = new VideoSelectedFileModel(VideoActivity.this, contentView);
            videoSelectedFileModel.init();
            isPreView = false;
        }
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                videoPreViewModel.onTouchEvent(event);
                return false;
            }
        });
        if (!isPreView) {
            contentView.findViewById(R.id.ib_switch).setVisibility(View.GONE);
        }

        contentView.findViewById(R.id.ib_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreView) {
                    isImpactTv += 1;
                    isImpactTv = isImpactTv % 3;
                    videoPreViewModel.switchDirectory();
                    contentView.findViewById(R.id.tv_item1).requestFocus();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPreViewModel.onPause();
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!popupWindow.isShowing())
                popupWindow.showAsDropDown(view, 0, 0);
            return false;
        }
    });

    View view;

    @Override
    protected void onResume() {
        super.onResume();
        videoPreViewModel.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        videoPreViewModel.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        synchronized (this) {
            handler.removeMessages(0);
            if (popupWindow.isShowing())
                popupWindow.dismiss();
        }
        Log.e(TAG, "finish: ");
        super.finish();
//        Intent intent = new Intent(this, OSDSettingActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}
