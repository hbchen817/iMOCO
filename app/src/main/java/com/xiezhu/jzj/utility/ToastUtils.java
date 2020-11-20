package com.xiezhu.jzj.utility;

import android.content.Context;
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
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 在屏幕正中间弹短吐司
     */
    public static void showToastCentrally(Context context, int message) {
        Toast toast = Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 显示服务端返回的信息
     */
    public static void showRespMsg(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
