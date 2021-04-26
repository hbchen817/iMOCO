package com.rexense.wholehouse.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.contract.IAccount;
import com.rexense.wholehouse.presenter.AccountHelper;
import com.rexense.wholehouse.presenter.SystemParameter;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 对话框
 */
public class Dialog {
    // 确认
    public static void confirm(Context context, int titleId, String message, int iconId, int buttonNameId, boolean isFinish) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return;
        }
        Context mContext = context;
        boolean mIsFinish = isFinish;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(iconId);
        builder.setTitle(titleId);
        builder.setMessage(message);
        builder.setPositiveButton(buttonNameId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mIsFinish) {
                    ((Activity) mContext).finish();
                }
            }
        });
        try {
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("lzm", "dialog exception " + e.getMessage());
        }
    }

    // 确认登录
    public static void confirmLogin(Context context, int titleId, String message, int iconId, int buttonNameId, Handler handler) {
        if (context == null) {
            return;
        }

        Context mContext = context;
        Handler mHandler = handler;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(iconId);
        builder.setTitle(titleId);
        builder.setMessage(message);
        builder.setPositiveButton(buttonNameId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                AccountHelper.login(new IAccount.loginCallback() {
                    @Override
                    public void returnLoginResult(int loginResult) {
                        if (loginResult == 1) {
                            Logger.i(mContext.getString(R.string.login_success));
                            SystemParameter.getInstance().setIsLogin(true);

                            // 登录后异步处理
                            Message msg = new Message();
                            msg.what = Constant.MSG_POSTLOGINPORCESS;
                            mHandler.sendMessage(msg);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            Logger.e(mContext.getString(R.string.login_failed));
                            SystemParameter.getInstance().setIsLogin(false);
                        }
                    }
                });
            }
        });
        builder.create().show();
    }
}
