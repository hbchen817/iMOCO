package com.rexense.wholehouse.utility;

import android.content.Context;
import android.os.Handler;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class QMUITipDialogUtil {
    public static QMUITipDialog mDailog;

    public static void showLoadingDialg(Context context, String tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tip)
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 20000);
    }

    public static void showLoadingDialg(Context context, int tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(context.getString(tip))
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 20000);
    }

    public static void showSuccessDialog(Context context, String tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(tip)
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 2000);
    }

    public static void showSuccessDialog(Context context, int tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(context.getString(tip))
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 2000);
    }

    public static void showFailDialog(Context context, String tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(tip)
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 2000);
    }

    public static void showFailDialog(Context context, int tip) {
        dismiss();
        mDailog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(context.getString(tip))
                .create();
        mDailog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDailog != null) mDailog.dismiss();
            }
        }, 2000);
    }

    public static void dismiss() {
        if (mDailog != null) {
            mDailog.dismiss();
        }
    }
}
