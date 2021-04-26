package com.rexense.wholehouse.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Window;

import com.rexense.wholehouse.R;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class DialogUtils {

    private static Dialog mLoadingDialog;
    private static AlertDialog.Builder netErrorBuilder;
    private static boolean isShow;

    /**
     * 显示加载Dialog
     */
    public static void showLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mLoadingDialog = dialog;
        try {
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消加载Dialog
     */
    public static void dismissLoadingDialog() {
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 确认Dialog
     */
    public static void showConfirmDialog(final Context context, DialogInterface.OnClickListener onClickListener, String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(context.getString(R.string.dialog_ok), onClickListener);
        builder.show();
    }

    /**
     * 选择Dialog
     */
    public static void showEnsureDialog(final Context context, DialogInterface.OnClickListener onClickListener, String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        builder.setNegativeButton(context.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton(context.getString(R.string.dialog_ok), onClickListener);
        builder.show();
    }

    /**
     * 错误提示Dialog
     */
    public static void showNetErrorDialog(final Context context, String msg) {
        netErrorBuilder = new AlertDialog.Builder(context);
        netErrorBuilder.setMessage(msg);
        netErrorBuilder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        netErrorBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShow = false;
            }
        });
        if (!isShow){
            netErrorBuilder.show();
            isShow = true;
        }
    }

    /**
     * 提示Dialog
     */
    public static void showMsgDialog(final Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    /**
     * 通用Dialog
     */
    public static void showCommonDialog(Context context, String title, String negativeMsg, String positiveMsg,
                                        DialogInterface.OnClickListener negativeOnClickListener,
                                        DialogInterface.OnClickListener positiveOnClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title);
        builder.setNegativeButton(negativeMsg, negativeOnClickListener);
        builder.setPositiveButton(positiveMsg, positiveOnClickListener);
        builder.show();
    }
}
