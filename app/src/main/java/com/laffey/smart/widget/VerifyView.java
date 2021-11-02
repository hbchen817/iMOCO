package com.laffey.smart.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.vise.log.ViseLog;

import java.util.Random;

public class VerifyView extends View {
    private Bitmap bitmap;// 原图
    private Bitmap drawBitmap;// 背景图
    private Bitmap verifyBitmap;// 验证图
    private boolean reCalc = true;// 是否需要重新计算
    private int x;// 随机选取的位置
    private int y;
    private int left, top, right, bottom;// 验证的地方
    private int moveX;// 移动x坐标
    private int moveMax;// 最大移动
    private int trueX;// 正确移动的x坐标

    private int screenWidth;
    private int screenHeight;
    private float scaleValue;

    public VerifyView(Context context) {
        super(context);
    }

    public VerifyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VerifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawBitmap == null || verifyBitmap == null) return;

        if (reCalc) {
            // 背景图
            int width = getWidth();
            int height = getHeight();

            //drawBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            // 验证的地方
            int length = Math.min(width, height);
            length /= 4;

            x = 0;//new Random().nextInt(width - length * 2) + length;// 随机选取的位置
            y = 0;//new Random().nextInt(height - length * 2) + length;

            left = x;
            top = y;
            right = left + length;
            bottom = top + length;

            // 验证的图片
            //verifyBitmap = Bitmap.createBitmap(drawBitmap, x, y, length, length);

            // 验证图片的最大移动距离
            moveMax = width - length;
            // 正确的验证位置
            trueX = x;

            reCalc = false;// 下次不用再进入这个if
        }
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        // float scaleValue = (float) (screenWidth - 120) / drawBitmap.getWidth();
        moveMax = screenWidth - 120;
        // ViseLog.d("scaleValue = " + scaleValue);
        matrix.postScale(scaleValue, scaleValue);
        Bitmap resizeDrawBitmap = Bitmap.createBitmap(drawBitmap, 0, 0, drawBitmap.getWidth(), drawBitmap.getHeight(), matrix, true);
        //canvas.drawBitmap(drawBitmap, matrix, paint);// 画上背景图
        canvas.drawBitmap(resizeDrawBitmap, 0, 0, paint);// 画上背景图
        paint.setColor(Color.parseColor("#55000000"));
        // canvas.drawRect(left, top, right, bottom, paint);// 画上阴影
        paint.setColor(Color.parseColor("#ffffffff"));
        // canvas.drawBitmap(verifyBitmap, matrix, paint);// 画验证图片
        Bitmap resizeVerifyBitmap = Bitmap.createBitmap(verifyBitmap, 0, 0, verifyBitmap.getWidth(), verifyBitmap.getHeight(), matrix, true);
        //if (moveX <= verifyBitmap.getWidth()) {
        canvas.drawBitmap(resizeVerifyBitmap, moveX, y, paint);// 画验证图片
        // canvas.drawBitmap(resizeVerifyBitmap, null, new Rect(moveX, 0, drawBitmap.getWidth(), drawBitmap.getHeight()), paint);
        //}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (drawBitmap == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        float scaleValue = (float) (screenWidth - 120) / drawBitmap.getWidth();

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int w = widthSpecSize;
        int h = heightSpecSize;

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = (int) (scaleValue * drawBitmap.getWidth());
            h = (int) (scaleValue * drawBitmap.getHeight());
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = (int) (scaleValue * drawBitmap.getWidth());
            h = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize;
            h = (int) (scaleValue * drawBitmap.getHeight());
        } else {
            w = widthSpecSize;
            h = heightSpecSize;
        }
        setMeasuredDimension(w, h);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public void setWidthAndHeightAndScaleView(int screenWidth, int screenHeight, float scaleValue) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scaleValue = scaleValue;
    }

    public float getScaleValue() {
        return scaleValue;
    }

    public void setScaleValue(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setDrawBitmap(Bitmap bitmap) {
        this.drawBitmap = bitmap;
        requestLayout();
    }

    public void setVerifyBitmap(Bitmap bitmap) {
        this.verifyBitmap = bitmap;
        requestLayout();
    }

    public void setMove(double precent) {
        if (precent < 0 || precent > 1) return;
        moveX = (int) (moveMax * precent);
        requestLayout();
    }

    public void setMove(int progress) {
        moveX = progress;
        requestLayout();
    }

    public boolean isTrue(double range) {
        return moveX > trueX * (1 - range) && moveX < trueX * (1 + range);
    }

    public void setReDraw() {
        reCalc = true;
        invalidate();
    }
}
