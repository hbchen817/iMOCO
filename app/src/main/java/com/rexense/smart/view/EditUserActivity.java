package com.rexense.smart.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityEditUserBinding;
import com.rexense.smart.presenter.UserCenter;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * @author Gary
 * @time 2020/10/13 15:42
 */

public class EditUserActivity extends BaseActivity {
    private ActivityEditUserBinding mViewBinding;

    private static final String ID = "ID";
    private static final String NAME = "NAME";

    private ProcessDataHandler mHandler;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityEditUserBinding.inflate(getLayoutInflater());
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
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(ID);
        String name = intent.getStringExtra(NAME);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.edit_user);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.nick_name_save);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(getResources().getColor(R.color.topic_color2));
        mHandler = new ProcessDataHandler(this);
        mViewBinding.mNameEditText.setText(name);
        if (name != null) mViewBinding.mNameEditText.setSelection(name.length());

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
        mViewBinding.deleteUser.setOnClickListener(this::onViewClicked);
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
            UserCenter.updateVirtualUser(mUserId, name, mCommitFailureHandler, mResponseErrorHandler, mHandler);
        } else if (id == R.id.delete_user) {
            DialogUtils.showConfirmDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    UserCenter.deleteVirtualUser(mUserId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                }
            }, "您确定要删除此用户吗？", "删除用户确认");
        }
    }

    private static class ProcessDataHandler extends Handler {
        final WeakReference<EditUserActivity> mWeakReference;

        public ProcessDataHandler(EditUserActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_CALLBACK_UPDATE_USER:
                case Constant.MSG_CALLBACK_DELETE_USER:
                    EditUserActivity activity = mWeakReference.get();
                    EventBus.getDefault().post(new UserManagerActivity.RefreshUserEvent());
                    activity.finish();
                default:
                    break;
            }

        }
    }

    public static void start(Context context, String id, String name) {
        Intent intent = new Intent(context, EditUserActivity.class);
        intent.putExtra(ID, id);
        intent.putExtra(NAME, name);
        context.startActivity(intent);
    }
}