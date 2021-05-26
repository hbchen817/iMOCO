package com.rexense.wholehouse.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityAddActionBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActionActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddActionBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAddActionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.devIv.setTypeface(iconfont);
        mViewBinding.sceneIv.setTypeface(iconfont);
        mViewBinding.notificationIv.setTypeface(iconfont);
        mViewBinding.devGoIv.setTypeface(iconfont);
        mViewBinding.sceneGoIv.setTypeface(iconfont);
        mViewBinding.notificationGoIv.setTypeface(iconfont);

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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.add_action));

        mViewBinding.devActionLayout.setOnClickListener(this);
        mViewBinding.sceneActionLayout.setOnClickListener(this);
        mViewBinding.notificationActionLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.dev_action_layout) {
            Intent intent = new Intent(this, DevListForActionActivity.class);
            startActivity(intent);
        } else if (id == R.id.scene_action_layout) {
            Intent intent = new Intent(this, SceneActionActivity.class);
            startActivity(intent);
        } else if (id == R.id.notification_action_layout) {
            Intent intent = new Intent(this, NotificationActionActivity.class);
            startActivity(intent);
        }
    }
}