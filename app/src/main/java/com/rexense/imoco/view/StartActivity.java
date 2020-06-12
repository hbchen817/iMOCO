package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.rexense.imoco.R;
import com.rexense.imoco.presenter.MocoApplication;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.ToastUtils;

public class StartActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (LoginBusiness.isLogin()) {
            Intent intent = new Intent(StartActivity.this, IndexActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            startLogin();
        }
    }

    private void startLogin() {
        //跳转到登录界面
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                IndexActivity.start(StartActivity.this);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onLoginFailed(int i, String s) {
                ToastUtils.showToastCentrally(MocoApplication.getInstance(),s);
            }
        });
        finishLater();

    }

    private void killProcess() {
        ThreadPool.MainThreadHandler.getInstance().post(() -> Process.killProcess(Process.myPid()), 2000);
    }

    private void finishLater() {
        ThreadPool.MainThreadHandler.getInstance().post(() -> finish(), 500);

    }
}
