package com.rexense.imoco.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.presenter.AccountHelper;
import com.rexense.imoco.presenter.MocoApplication;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.widget.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeleteAccountActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    private DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AccountHelper.unregister(mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.myinfo_delete_account));

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @OnClick({R.id.confirm_btn})
    void onClick(View view){
        switch (view.getId()){
            case R.id.confirm_btn:
                DialogUtils.showEnsureDialog(mActivity,confirmListener,getString(R.string.delete_account_confirm_again_tips),
                        getString(R.string.delete_account_confirm_again));
                break;
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_UNREGISTER:
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
                            ToastUtils.showToastCentrally(mActivity,getString(R.string.account_logout_failed) + error);
                        }
                    });
                    break;
            }
            return false;
        }
    });

}
