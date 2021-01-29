package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laffey.smart.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActionActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.dev_action_layout)
    RelativeLayout mDevLayout;
    @BindView(R.id.scene_action_layout)
    LinearLayout mSceneLayout;
    @BindView(R.id.notification_action_layout)
    LinearLayout mNotificationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);
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
        mTitle.setText(getString(R.string.add_action));
    }

    @OnClick({R.id.dev_action_layout, R.id.scene_action_layout, R.id.notification_action_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dev_action_layout: {
                Intent intent = new Intent(this, DevListForActionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.scene_action_layout: {
                Intent intent = new Intent(this, SceneActionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.notification_action_layout: {
                Intent intent = new Intent(this, NotificationActionActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}