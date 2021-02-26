package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.demoTest.ActionEntry;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActionActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.notifition_et)
    EditText mNotificationET;
    @BindView(R.id.content_count_tv)
    TextView mContentCountTV;

    private ActionEntry.SendMsg mSendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_action);
        ButterKnife.bind(this);

        initStatusBar();
        initView();

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(ActionEntry.SendMsg msg){
        mSendMsg = msg;
        mNotificationET.setText(mSendMsg.getMessage());
        mNotificationET.setSelection(mSendMsg.getMessage().length());

        EventBus.getDefault().removeStickyEvent(msg);
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
        mTitle.setText(getString(R.string.send_notifications_to_your_phone));
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = mNotificationET.getText().toString();
                if (info == null || info.length() == 0) {
                    ToastUtils.showLongToast(NotificationActionActivity.this, R.string.pls_enter_the_notification);
                    return;
                }

                if (mSendMsg == null)
                    mSendMsg = new ActionEntry.SendMsg();
                mSendMsg.setMessage(mNotificationET.getText().toString());

                EventBus.getDefault().unregister(NotificationActionActivity.this);
                EventBus.getDefault().postSticky(mSendMsg);

                Intent intent = new Intent(NotificationActionActivity.this, NewSceneActivity.class);
                startActivity(intent);
            }
        });

        mNotificationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentCountTV.setText(s.toString().length()+"/60");
            }
        });
        mContentCountTV.setText("0/60");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}