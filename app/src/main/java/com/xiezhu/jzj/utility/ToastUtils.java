package com.xiezhu.jzj.utility;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class ToastUtils {
    private static Toast mToast;

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, String message) {
        if (mToast == null)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else
            mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(message);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, String message, int delay) {
        if (mToast == null)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(message);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToast.cancel();
            }
        }, delay);
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, int message) {
        if (mToast == null)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(context.getString(message));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showLongToastCentrally(Context context, String message) {
        if (mToast == null)
            mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        else mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(message);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showLongToastCentrally(Context context, int message) {
        if (mToast == null)
            mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        else mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(context.getResources().getString(message));
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showLongToast(Context context, int message) {
        if (mToast == null || mToast.getGravity() == Gravity.CENTER)
            mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        else mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(context.getResources().getString(message));
        mToast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showLongToast(Context context, String message) {
        if (mToast == null || mToast.getGravity() == Gravity.CENTER)
            mToast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        else mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(message);
        mToast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showShortToast(Context context, int message) {
        if (mToast == null || mToast.getGravity() == Gravity.CENTER)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(context.getResources().getString(message));
        mToast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showShortToast(Context context, String message) {
        if (mToast == null || mToast.getGravity() == Gravity.CENTER)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(message);
        mToast.show();
    }

    /**
     * 显示服务端返回的信息
     */
    public static void showRespMsg(Context context, String message) {
        if (mToast == null || mToast.getGravity() == Gravity.CENTER)
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        else mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(message);
        mToast.show();
    }
}
