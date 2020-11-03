package com.rexense.imoco.view;

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
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.imoco.R;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.utility.ToastUtils;

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
    }

    private void initView() {
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(ID);
        String name = intent.getStringExtra(NAME);
        tvToolbarTitle.setText(R.string.edit_user);
        tvToolbarRight.setText(R.string.nick_name_save);
        tvToolbarRight.setTextColor(0xFFD5A035);
        mHandler = new ProcessDataHandler(this);
        mNameEditText.setText(name);
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