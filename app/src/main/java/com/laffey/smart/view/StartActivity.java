package com.laffey.smart.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.MocoApplication;
import com.laffey.smart.utility.LogcatFileManager;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;

import java.lang.ref.WeakReference;

public class StartActivity extends BaseActivity {
    public static final int CODE = 500;
    public static final int TOTAL_TIME = 2000;
    public static final int INTERVAL_TIME = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setTheme(R.style.AppTheme_Launcher);
        setContentView(R.layout.activity_start);
        initView();

        boolean isFirst = SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, "show_policy", false);
        if (!isFirst) {
            showPrivacyPolicyDialog();
        } else {
            initEvent();
        }
    }

    private void initView() {
        //mDownCountTextView = findViewById(R.id.tv_count_down_splash);
        if (SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, "log_state", false)) {
            String path = getApplicationContext().getExternalCacheDir() + "/Log/";
            LogcatFileManager.getInstance().start(path);
        } else {
            LogcatFileManager.getInstance().stop();
        }
    }

    private void showPrivacyPolicyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);

        TextView linkTV = view.findViewById(R.id.policy_link);
        TextView disagreeTV = view.findViewById(R.id.disagree_btn);
        TextView agreeTV = view.findViewById(R.id.agree_btn);

        linkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                H5Activity.actionStart(StartActivity.this, Constant.PRIVACY_POLICY_URL, getString(R.string.aboutus_privacy_policy));
            }
        });
        disagreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPrivacyPolicyCheckDialog();
            }
        });
        agreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.putBooleanValue(StartActivity.this, SpUtils.SP_APP_INFO, "show_policy", true);
                dialog.dismiss();
                initEvent();
            }
        });

        dialog.show();
    }

    private void showPrivacyPolicyCheckDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy_check, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);

        TextView lgoutTV = view.findViewById(R.id.lgout_btn);
        TextView checkAgainTV = view.findViewById(R.id.check_again_btn);

        lgoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        checkAgainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPrivacyPolicyDialog();
            }
        });

        dialog.show();
    }

    private void initEvent() {
        MocoApplication.initAliSDK();

        MyHandler myHandler = new MyHandler(this);
        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = TOTAL_TIME;
        myHandler.sendMessage(message);

//        mDownCountTextView.setOnClickListener(v -> {
//            goToNextActivity();
//            finish();
//            myHandler.removeMessages(CODE);
//        });
    }

    private void goToNextActivity() {
        if (LoginBusiness.isLogin()) {
            Intent intent = new Intent(StartActivity.this, IndexActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            startLogin();
        }
    }

    public static class MyHandler extends Handler {
        private final WeakReference<StartActivity> mWeakReference;

        private MyHandler(StartActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StartActivity activity = mWeakReference.get();
            if (msg.what == CODE) {
                if (activity != null) {
                    int time = msg.arg1;
//                    String content = (time / INTERVAL_TIME) + activity.getResources().getString(R.string.down_count_text);
//                    activity.mDownCountTextView.setText(content);

                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - INTERVAL_TIME;
                    if (time > 0) {
                        sendMessageDelayed(message, INTERVAL_TIME);
                    } else {
                        activity.goToNextActivity();
                    }
                }
            }
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
                ToastUtils.showToastCentrally(MocoApplication.getInstance(), s);
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
