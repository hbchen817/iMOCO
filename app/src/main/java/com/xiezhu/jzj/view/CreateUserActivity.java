package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.Intent;
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

public class CreateUserActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.mNameEditText)
    EditText mNameEditText;

    private ProcessDataHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvToolbarTitle.setText(R.string.create_user);
        tvToolbarRight.setText(R.string.nick_name_save);
        tvToolbarRight.setTextColor(getResources().getColor(R.color.topic_color2));
        mHandler = new ProcessDataHandler(this);
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
                UserCenter.createVirtualUser(name, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                break;
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
            EventBus.getDefault().post(new UserManagerActivity.RefreshUserEvent());
            activity.finish();
        }
    }
}