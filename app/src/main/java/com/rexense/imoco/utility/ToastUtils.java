package com.rexense.imoco.utility;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class ToastUtils {

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, String message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, String message, int delay) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, delay);
    }
    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, int message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(context.getString(message));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showLongToastCentrally(Context context, String message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showLongToastCentrally(Context context, int message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(context.getResources().getString(message));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showLongToast(Context context, int message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(context.getResources().getString(message));
        toast.show();
    }

    /**
     * 在屏幕弹短吐司
     */
    public static void showLongToast(Context context, String message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(message);
        toast.show();
    }

    /**
     * 显示服务端返回的信息
     */
    public static void showRespMsg(Context context, String message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
