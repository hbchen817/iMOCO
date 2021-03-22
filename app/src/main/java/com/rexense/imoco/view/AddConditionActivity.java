package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.imoco.R;

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
    @BindView(R.id.dev_divider)
    TextView mDevDividerTV;
    @BindView(R.id.timer_iv)
    TextView mTimerIV;
    @BindView(R.id.timer_range_iv)
    TextView mTimerRangeIV;
    @BindView(R.id.dev_iv)
    TextView mDevIV;
    @BindView(R.id.timer_go_iv)
    TextView mTimerGoIV;
    @BindView(R.id.timer_range_go_iv)
    TextView mTimerRangeGoIV;
    @BindView(R.id.dev_go_iv)
    TextView mDevGoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_condition);
        ButterKnife.bind(this);

        initStatusBar();
        boolean hasTimeCondition = getIntent().getBooleanExtra("has_time_condition", false);
        mTimeLayout.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);
        mTimeRangeLayout.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);
        mDevDividerTV.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mTimerIV.setTypeface(iconfont);
        mTimerRangeIV.setTypeface(iconfont);
        mDevIV.setTypeface(iconfont);
        mTimerGoIV.setTypeface(iconfont);
        mTimerRangeGoIV.setTypeface(iconfont);
        mDevGoIV.setTypeface(iconfont);

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
        mTitle.setText(getString(R.string.add_condition));
    }

    @OnClick({R.id.time_layout, R.id.time_range_layout, R.id.dev_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.time_layout: {
                Intent intent = new Intent(this, TimeSelectorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.time_range_layout: {
                Intent intent = new Intent(this, TimeRangeSelectorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.dev_layout: {
                Intent intent = new Intent(this, DevListForCAActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}