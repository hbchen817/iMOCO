package com.rexense.wholehouse.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AirRemoteControlActivity extends AppCompatActivity {
    @BindView(R.id.iv_toolbar_left)
    ImageView mToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.switch_ic)
    TextView mSwitchIC;
    @BindView(R.id.mode_ic)
    TextView mModeIC;
    @BindView(R.id.wind_speed_ic)
    TextView mWindSpeedIC;
    @BindView(R.id.swing_h_ic)
    TextView mSwingHIC;
    @BindView(R.id.swing_v_ic)
    TextView mSwingVIC;
    @BindView(R.id.minus_ic)
    TextView mMinusIC;
    @BindView(R.id.plus_ic)
    TextView mPlusIC;
    @BindView(R.id.previous_ic)
    TextView mPreviousIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_remote_control);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mSwitchIC.setTypeface(iconfont);
        mModeIC.setTypeface(iconfont);
        mWindSpeedIC.setTypeface(iconfont);
        mSwingHIC.setTypeface(iconfont);
        mSwingVIC.setTypeface(iconfont);
        mMinusIC.setTypeface(iconfont);
        mPlusIC.setTypeface(iconfont);
        mPreviousIC.setTypeface(iconfont);

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mToolbarTitle.setText(R.string.air_conditioner);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.switch_ic, R.id.mode_ic, R.id.wind_speed_ic, R.id.swing_h_ic,
            R.id.swing_v_ic, R.id.minus_ic, R.id.plus_ic})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left: {
                finish();
                break;
            }
            case R.id.switch_ic: {
                // 开关
                ViseLog.d("开关");
                break;
            }
            case R.id.mode_ic: {
                // 模式
                ViseLog.d("模式");
                break;
            }
            case R.id.wind_speed_ic: {
                // 风速
                ViseLog.d("风速");
                break;
            }
            case R.id.swing_h_ic: {
                // 左右扫风
                ViseLog.d("左右扫风");
                break;
            }
            case R.id.swing_v_ic: {
                // 上下扫风
                ViseLog.d("上下扫风");
                break;
            }
            case R.id.minus_ic: {
                // 减温度
                ViseLog.d("减温度");
                break;
            }
            case R.id.plus_ic: {
                // 加温度
                ViseLog.d("加温度");
                break;
            }
        }
    }
}