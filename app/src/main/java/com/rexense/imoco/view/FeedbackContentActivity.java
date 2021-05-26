package com.rexense.imoco.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityNotificationActionBinding;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.AppUtils;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;

import java.lang.ref.WeakReference;

public class FeedbackContentActivity extends BaseActivity {
    private ActivityNotificationActionBinding mViewBinding;

    private int mFeedbackType = 0;// 101表示反馈故障、102表示功能建议、103表示其他问题
    private UserCenter mUserCenter;
    private DataHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityNotificationActionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mFeedbackType = getIntent().getIntExtra("feedback_type", 0);
        mViewBinding.notifitionEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        mUserCenter = new UserCenter(this);
        mHandler = new DataHandler(this);
        switch (mFeedbackType) {
            case 101: {// 反馈故障
                mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.feedback_fault));
                mViewBinding.notifitionEt.setHint(R.string.pls_enter_fault_description);
                break;
            }
            case 102: {// 功能建议
                mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.feature_suggestions));
                mViewBinding.notifitionEt.setHint(R.string.pls_enter_feature_suggestions);
                break;
            }
            case 103: {// 其他问题
                mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.other_problems));
                mViewBinding.notifitionEt.setHint(R.string.pls_enter_description_of_the_problem);
                break;
            }
        }
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_commit));

        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = mViewBinding.notifitionEt.getText().toString();
                if (info == null || info.length() == 0) {
                    switch (mFeedbackType) {
                        case 101: {// 反馈故障
                            ToastUtils.showLongToast(FeedbackContentActivity.this, R.string.pls_enter_fault_description);
                            break;
                        }
                        case 102: {// 功能建议
                            ToastUtils.showLongToast(FeedbackContentActivity.this, R.string.pls_enter_feature_suggestions);
                            break;
                        }
                        case 103: {// 其他问题
                            ToastUtils.showLongToast(FeedbackContentActivity.this, R.string.pls_enter_description_of_the_problem);
                            break;
                        }
                    }
                    return;
                }

                String mobileSystem = "android " + Build.VERSION.RELEASE;
                String appVersion = AppUtils.getVersionName(FeedbackContentActivity.this);
                String content = mViewBinding.notifitionEt.getText().toString();
                String mobileModel = Build.MODEL;
                String contact = Account.getUserPhone();
                String topic = String.valueOf(mFeedbackType);

                QMUITipDialogUtil.showLoadingDialg(FeedbackContentActivity.this, R.string.is_uploading);
                mUserCenter.submitFeedback(mobileSystem, appVersion, mFeedbackType, content, mobileModel, contact, topic,
                        mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });

        mViewBinding.notifitionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                mViewBinding.contentCountTv.setText(s.toString().length() + "/200");
            }
        });
        mViewBinding.contentCountTv.setText("0/200");
    }

    private static class DataHandler extends Handler {
        private final WeakReference<FeedbackContentActivity> ref;

        public DataHandler(FeedbackContentActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            FeedbackContentActivity activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_SUBMIT_FEEDBACK) {
                QMUITipDialogUtil.showSuccessDialog(activity, R.string.submit_completed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        QMUITipDialogUtil.dismiss();
                        activity.finish();
                    }
                }, 1000);
            }
        }
    }
}