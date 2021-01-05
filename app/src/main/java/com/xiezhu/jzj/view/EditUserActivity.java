package com.xiezhu.jzj.view;

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

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.presenter.UserCenter;
import com.xiezhu.jzj.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/13 15:42
 */

public class EditUserActivity extends BaseActivity {

    private static final String ID = "ID";
    private static final String NAME = "NAME";

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.mNameEditText)
    EditText mNameEditText;

    private ProcessDataHandler mHandler;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
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
        tvToolbarTitle.setText(R.string.edit_user);
        tvToolbarRight.setText(R.string.nick_name_save);
        tvToolbarRight.setTextColor(getResources().getColor(R.color.topic_color2));
        mHandler = new ProcessDataHandler(this);
        mNameEditText.setText(name);
        if (name != null) mNameEditText.setSelection(name.length());
    }

    @OnClick({R.id.iv_toolbar_left, R.id.tv_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.tv_toolbar_right:
                String name = mNameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToastCentrally(this, R.string.create_user_name_hint);
                    return;
                }
                UserCenter.updateVirtualUser(mUserId, name, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                break;
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
            EditUserActivity activity = mWeakReference.get();
            EventBus.getDefault().post(new UserManagerActivity.RefreshUserEvent());
            activity.finish();
        }
    }

    public static void start(Context context, String id, String name) {
        Intent intent = new Intent(context, EditUserActivity.class);
        intent.putExtra(ID, id);
        intent.putExtra(NAME, name);
        context.startActivity(intent);
    }
}