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

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.MocoApplication;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.AppUtils;
import com.laffey.smart.utility.LogcatFileManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        // SpUtils.putAccessToken(this, "wyy");
        initView();

        // SystemParameter.getInstance().setIsRefreshDeviceData(true);
        boolean isFirst = SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, "show_policy", false);
        if (!isFirst) {
            showPrivacyPolicyDialog();
        } else {
            initEvent();
        }
    }

    private void initView() {
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
    }

    private void goToNextActivity() {
        /*SpUtils.putAccessToken(this, "wyy");
        SpUtils.putRefreshToken(this, "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJleHAiOjE2MzY4NjA4MzUsInR5cGUiOiJSRUZSRVNIIiwiaWF0IjoxNjM2MjU2MDM1LCJhY2NvdW50c0lkIjoiOTdjMDE3NGJmYmY4NDkwYzgwZTI3ZGNmMDcxYjY5OWEifQ.JoXZ6ynzCLDncw7fTIMDF3Iz4nW9Xw9Vr1M3XYv6w_SxjHgLHZMHu9pT-cNN9hJfYnOw0u4q2Yl618N-bSIt7W7VTsuKCbB9NHFsAap4F31j6dDkG_xeUfO8--wIB91aY3YvBQzyUunNTg5difwGuG_kEWzNC---zkRMBo19qa0t4DvXUc6Yj9LmDN4OMkA_7MSUKffTaDO5_Ncnm3p-MUezHu8MOO4KJbEVf0GLy5sa1r3NZnpIQoF1CZGFv7xCRDIoeiMpV10YwaF0yeQopcg9blqkNKVcPrHGf4r2AJIAAP5CPzS-DFefXAgQh-F6TF1hQHlxrS-YwSQGl95MkkSXHRl4eW02tM-k7tMy9fOqGIOLir7i9-lQZLf8hNHjJMBZwSlHA4vsqmaIMHw0xK5Claop2ZirnyLRs91vMic-VVIF6Ujt7Hx2mcXdwCKuQJRBF4bmRSKkf3x51F1TGfhXtod3p94aFCuP5hDbe1zbaGQyKVo5ujLwQZ0Vozs0");
        SpUtils.putRefreshTokenTime(this, System.currentTimeMillis());*/
        if (LoginBusiness.isLogin()) {
            if (System.currentTimeMillis() - SpUtils.getRefreshTokenTime(this) > 6 * 24 * 60 * 60 * 1000) {
                // 大于六天重新登录
                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        DeviceBuffer.initSceneBuffer();
                        SpUtils.putAccessToken(StartActivity.this, "");
                        SpUtils.putRefreshTokenTime(StartActivity.this, -1);
                        LoginActivity.start(mActivity, null);
                        finish();
                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
                    }
                });
            } else {
                Intent intent = new Intent(StartActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        } else {
            // startLogin();
            LoginActivity.start(mActivity, null);
            finish();
            // LoginActivity.start(this, null);
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
