package com.example.hu.mediaplayerapk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hu.mediaplayerapk.R;
import com.example.hu.mediaplayerapk.config.Config;
import com.example.hu.mediaplayerapk.util.TimeUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MotionDetectorService extends Service implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MotionDetectorService";
    private JavaCameraView mOpenCvCameraView;
    private int mAbsoluteFaceSize = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();
    }

    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;
    private float mRelativeFaceSize = 0.2f;
    int faceSerialCount = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        cascadeDir.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void init() {
        mOpenCvCameraView = new JavaCameraView(this, null);
        mOpenCvCameraView.setCvCameraViewListener(this);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MotionDetectorServiceBinder();
    }


    public void startDetect() {
        Log.e(TAG, "startDetect: " );
        mOpenCvCameraView.enableView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mOpenCvCameraView.takePhoto(Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.PICKTURE_FOLDER + File.separator + TimeUtil.getCurrentFormatTime() + ".jpg");
            }
        }, 5000);
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();


        if (mNativeDetector != null)
            mNativeDetector.detect(mGray, faces);
        Rect[] facesArray = faces.toArray();
        int faceCount = facesArray.length;
        if (faceCount > 0) {
            faceSerialCount++;
        } else {
            faceSerialCount = 0;
        }
        if (faceSerialCount > 5) {
//            Log.e(TAG, "onCameraFrame: " + Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "OUTPUT" + File.separator + TimeUtil.getCurrentFormatTime() + ".jpg");
            Log.e(TAG, "onCameraFrame: " + Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.PICKTURE_FOLDER + File.separator + TimeUtil.getCurrentFormatTime() + ".jpg");
//            mOpenCvCameraView.takePhoto(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OUTPUT" + File.separator + TimeUtil.getCurrentFormatTime() + ".jpg");
            mOpenCvCameraView.takePhoto(Config.INTERNAL_FILE_ROOT_PATH + File.separator + Config.PICKTURE_FOLDER + File.separator + TimeUtil.getCurrentFormatTime() + ".jpg");
            faceSerialCount = -5000;
            Log.i("takephoto", "takephoto1");
        }
        return mRgba;
    }

    public class MotionDetectorServiceBinder extends Binder {
        public MotionDetectorService getMotionDetectorService() {
            return MotionDetectorService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
        mOpenCvCameraView.disableView();
    }
}
