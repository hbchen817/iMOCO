package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laffey.smart.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddConditionActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.time_layout)
    RelativeLayout mTimeLayout;
    @BindView(R.id.time_range_layout)
    RelativeLayout mTimeRangeLayout;
    @BindView(R.id.dev_layout)
    RelativeLayout mDevLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_condition);
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
        mTitle.setText(getString(R.string.add_condition));
    }

    @OnClick({R.id.time_layout,R.id.time_range_layout,R.id.dev_layout})
    protected void onViewClicked(View view){
        switch (view.getId()){
            case R.id.time_layout:{
                Intent intent = new Intent(this, TimeSelectorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.time_range_layout:{
                break;
            }
            case R.id.dev_layout:{
                break;
            }
        }
    }
}