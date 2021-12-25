package com.rexense.smart.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityDeleteAccountBinding;
import com.rexense.smart.presenter.AccountHelper;
import com.rexense.smart.presenter.AccountManager;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.SpUtils;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

public class DeleteAccountActivity extends BaseActivity {
    private ActivityDeleteAccountBinding mViewBinding;

    private final DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AccountHelper.unregister(mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.myinfo_delete_account));

        initStatusBar();
        mViewBinding.confirmBtn.setOnClickListener(this::onClick);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    void onClick(View view) {
        if (view.getId() == R.id.confirm_btn) {
            DialogUtils.showEnsureDialog(mActivity, confirmListener, getString(R.string.delete_account_confirm_again_tips),
                    getString(R.string.delete_account_confirm_again));
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_UNREGISTER/* || msg.what == Constant.MSG_QUEST_CANCELLATION*/) {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.delete_account_success));
                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        AccountManager.cancellation(mActivity,
                                Constant.MSG_QUEST_CANCELLATION, Constant.MSG_QUEST_CANCELLATION_ERROR, mAPIDataHandler);
                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
                    }
                });
            } else if (msg.what == Constant.MSG_QUEST_CANCELLATION_IOT) {
                JSONObject response = (JSONObject) msg.obj;
                int code = response.getInteger("code");
                if (code == 200) {
                    SpUtils.cancellation(mActivity);
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    IndexActivity.mainActivity.finish();
                    MyInfoActivity.myInfoActivity.finish();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    RetrofitUtil.showErrorMsg(DeleteAccountActivity.this, response);
                }
            } else if (msg.what == Constant.MSG_QUEST_CANCELLATION) {
                JSONObject response = (JSONObject) msg.obj;
                int code = response.getInteger("code");
                if (code == 200) {
                    AccountManager.cancellationIot(mActivity, Constant.MSG_QUEST_CANCELLATION_IOT,
                            Constant.MSG_QUEST_CANCELLATION_IOT_ERROR, mAPIDataHandler);
                } else {
                    RetrofitUtil.showErrorMsg(DeleteAccountActivity.this, response);
                }
            } else if (msg.what == Constant.MSG_QUEST_CANCELLATION_ERROR ||
                    msg.what == Constant.MSG_QUEST_CANCELLATION_IOT_ERROR) {
                Throwable e = (Throwable) msg.obj;
                ViseLog.e(e);
                ToastUtils.showLongToast(DeleteAccountActivity.this, e.getMessage());
            }
            return false;
        }
    });

}
