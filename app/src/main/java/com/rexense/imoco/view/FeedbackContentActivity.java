package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.demoTest.ActionEntry;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.AppUtils;
import com.rexense.imoco.utility.BuildProperties;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackContentActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.notifition_et)
    EditText mContentET;
    @BindView(R.id.content_count_tv)
    TextView mContentCountTV;

    private int mFeedbackType = 0;// 101表示反馈故障、102表示功能建议、103表示其他问题
    private UserCenter mUserCenter;
    private DataHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_action);
        ButterKnife.bind(this);

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

    private void initView() {
        mFeedbackType = getIntent().getIntExtra("feedback_type", 0);
        mContentET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        mUserCenter = new UserCenter(FeedbackContentActivity.this);
        mHandler = new DataHandler(this);
        switch (mFeedbackType) {
            case 101: {// 反馈故障
                mTitle.setText(getString(R.string.feedback_fault));
                mContentET.setHint(R.string.pls_enter_fault_description);
                break;
            }
            case 102: {// 功能建议
                mTitle.setText(getString(R.string.feature_suggestions));
                mContentET.setHint(R.string.pls_enter_feature_suggestions);
                break;
            }
            case 103: {// 其他问题
                mTitle.setText(getString(R.string.other_problems));
                mContentET.setHint(R.string.pls_enter_description_of_the_problem);
                break;
            }
        }
        tvToolbarRight.setText(getString(R.string.share_device_commit));

        tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = mContentET.getText().toString();
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
                String content = mContentET.getText().toString();
                String mobileModel = Build.MODEL;
                String contact = Account.getUserPhone();
                String topic = String.valueOf(mFeedbackType);

                QMUITipDialogUtil.showLoadingDialg(FeedbackContentActivity.this, R.string.is_uploading);
                mUserCenter.submitFeedback(mobileSystem, appVersion, mFeedbackType, content, mobileModel, contact, topic,
                        mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });

        mContentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentCountTV.setText(s.toString().length() + "/200");
            }
        });
        mContentCountTV.setText("0/200");
    }

    private class DataHandler extends Handler {
        private WeakReference<Activity> ref;

        public DataHandler(Activity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
            if (msg.what == Constant.MSG_CALLBACK_SUBMIT_FEEDBACK) {
                QMUITipDialogUtil.showSuccessDialog(FeedbackContentActivity.this, R.string.submit_completed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        QMUITipDialogUtil.dismiss();
                        finish();
                    }
                }, 1000);
            }
        }
    }
}