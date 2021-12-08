package com.rexense.wholehouse.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
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
        if (!TextUtils.isEmpty(title)) {
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
        if (!TextUtils.isEmpty(title)) {
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
        if (!isShow) {
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

    // 自定义确认dialog
    public static void showConfirmDialog(Activity activity, String title, String msg, String positiveText, String negativeText,
                                         Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_2, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleTv.setText(title);
        TextView msgTV = (TextView) view.findViewById(R.id.msg_tv);
        msgTV.setText(msg);

        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        TextView positiveTV = view.findViewById(R.id.positive_tv);
        TextView negativeTV = view.findViewById(R.id.negative_tv);
        positiveTV.setText(positiveText);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive();
            }
        });
        negativeTV.setText(negativeText);
        negativeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.negative();
            }
        });
    }

    // 自定义确认dialog
    public static void showConfirmDialog(Activity activity, String title, String msg, String positiveText,
                                         Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_3, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleTv.setText(title);
        TextView msgTV = (TextView) view.findViewById(R.id.msg_tv);
        msgTV.setText(msg);

        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        TextView positiveTV = view.findViewById(R.id.positive_tv);
        positiveTV.setText(positiveText);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive();
            }
        });
    }

    // 自定义确认dialog
    public static void showConfirmDialog(Activity activity, int title, int msg, int positiveText, int negativeText,
                                         Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_2, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleTv.setText(activity.getString(title));
        TextView msgTV = (TextView) view.findViewById(R.id.msg_tv);
        msgTV.setText(activity.getString(msg));

        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        TextView positiveTV = view.findViewById(R.id.positive_tv);
        TextView negativeTV = view.findViewById(R.id.negative_tv);
        positiveTV.setText(activity.getString(positiveText));
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive();
            }
        });
        negativeTV.setText(activity.getString(negativeText));
        negativeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.negative();
            }
        });
    }

    // 自定义确认dialog
    public static void showConfirmDialog(Activity activity, int title, int msg, int positiveText,
                                         Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_3, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleTv.setText(activity.getString(title));
        TextView msgTV = (TextView) view.findViewById(R.id.msg_tv);
        msgTV.setText(activity.getString(msg));

        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        TextView positiveTV = view.findViewById(R.id.positive_tv);
        positiveTV.setText(activity.getString(positiveText));
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive();
            }
        });
    }

    // 文字输入dialog
    public static void showInputDialog(Activity activity, String title, String hint, String content, InputCallback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(title);
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);

        nameEt.setText(content);
        nameEt.setSelection(content.length());

        nameEt.setHint(hint);
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive(nameEt.getText().toString());
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.negative();
            }
        });
    }

    // 文字输入dialog
    public static void showInputDialog(Activity activity, String title, String hint, String content, InputFilter[] filters, InputCallback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(title);
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setFilters(filters);

        nameEt.setText(content);
        nameEt.setSelection(content.length());

        nameEt.setHint(hint);
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = QMUIDisplayHelper.getScreenWidth(activity) * 4 / 5;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.positive(nameEt.getText().toString());
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.negative();
            }
        });
    }

    public interface Callback {
        void positive();

        void negative();
    }

    public interface InputCallback {
        void positive(String result);

        void negative();
    }
}
