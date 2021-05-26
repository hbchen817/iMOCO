package com.rexense.wholehouse.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityDeleteAccountBinding;
import com.rexense.wholehouse.presenter.AccountHelper;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.widget.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
            if (msg.what == Constant.MSG_CALLBACK_UNREGISTER) {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.delete_account_success));
                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        IndexActivity.mainActivity.finish();
                        MyInfoActivity.myInfoActivity.finish();
                        finish();
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
                    }
                });
            }
            return false;
        }
    });

}
