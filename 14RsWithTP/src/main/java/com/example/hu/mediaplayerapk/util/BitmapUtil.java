package com.example.hu.mediaplayerapk.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static com.example.hu.mediaplayerapk.application.MyApplication.ScreenHeight;
import static com.example.hu.mediaplayerapk.application.MyApplication.ScreenWidth;

/**
 * Created by Administrator on 2017/2/25.
 */

public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    /**
     * @param bm           原资源图片
     * @param isFullScreen 是否全屏
     * @return 返回一个全屏幕的图片
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap full_screen_bitmap(Bitmap bm, boolean isFullScreen) {
        if (bm == null) {
            return null;
        }
        float w = bm.getWidth();
        float h = bm.getHeight();
        float sx, sy;
        sx = ((float) ScreenWidth / w);
        sy = (float) ScreenHeight / h;
        Matrix matrix = new Matrix();
        if (isFullScreen) {
            matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        } else {
            if (ScreenWidth == 1024) {
                float ration = ScreenHeight * 16f / (9f * ScreenWidth);
                if (sy  > sx) {//如果纵轴拉sy伸大，就用横轴sx拉伸（纵轴拉伸sx*ration是绝对会比ScreenHeight小的）
                    matrix.postScale(sx, sx * ration);
                } else {
                    matrix.postScale(sy/ration, sy  );
                }
            } else {
                if (sy > sx) {
                    matrix.postScale(sx, sx);
                } else {
                    matrix.postScale(sy, sy);
                }
            }
        }
        bm = Bitmap.createBitmap(bm, 0, 0, (int) w, (int) h, matrix, true);
        Bitmap newBitmap = Bitmap.createBitmap(ScreenWidth, ScreenHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newBitmap);
        Paint p = new Paint();
        p.setColor(0);
        canvas.drawRect(new Rect(0, 0, ScreenWidth, ScreenHeight), p);
        canvas.drawBitmap(bm, (ScreenWidth - bm.getWidth()) / 2, (ScreenHeight - bm.getHeight()) / 2, null);
        canvas.save();
        canvas.restore();
        return newBitmap;
    }

    public static List<Bitmap> splitBitmap(Bitmap bitmap) {

        List<Bitmap> pieces = new ArrayList<>(8 * 8);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / 8;
        int pieceHeight = height / 8;
        Bitmap imagePiece;
        for (int i = 0; i < 8; i++) {
            System.gc();
            for (int j = 0; j < 8; j++) {
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                imagePiece = Bitmap.createBitmap(bitmap, xValue, yValue, pieceWidth, pieceHeight);
                pieces.add(imagePiece);
            }
        }
        return pieces;
    }
}
