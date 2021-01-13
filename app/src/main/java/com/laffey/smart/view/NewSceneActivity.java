package com.laffey.smart.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laffey.smart.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSceneActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene);
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

    private void initView(){
        mTitle.setText(getString(R.string.create_new_scene));
        tvToolbarRight.setText(getString(R.string.nick_name_save));
    }
}