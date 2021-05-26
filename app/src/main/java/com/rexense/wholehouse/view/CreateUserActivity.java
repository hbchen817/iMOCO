package com.rexense.wholehouse.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.databinding.ActivityCreateUserBinding;
import com.rexense.wholehouse.presenter.UserCenter;
import com.rexense.wholehouse.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/13 15:42
 */

public class CreateUserActivity extends BaseActivity {
    private ActivityCreateUserBinding mViewBinding;

    private ProcessDataHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityCreateUserBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initView();
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

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.create_user);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.nick_name_save);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(getResources().getColor(R.color.topic_color2));
        mHandler = new ProcessDataHandler(this);

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.tv_toolbar_right) {
            String name = mViewBinding.mNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtils.showToastCentrally(this, R.string.create_user_name_hint);
                return;
            }
            UserCenter.createVirtualUser(name, mCommitFailureHandler, mResponseErrorHandler, mHandler);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CreateUserActivity.class);
        context.startActivity(intent);
    }

    private static class ProcessDataHandler extends Handler {
        final WeakReference<CreateUserActivity> mWeakReference;

        public ProcessDataHandler(CreateUserActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            CreateUserActivity activity = mWeakReference.get();
            if (activity == null) return;
            EventBus.getDefault().post(new UserManagerActivity.RefreshUserEvent());
            activity.finish();
        }
    }
}