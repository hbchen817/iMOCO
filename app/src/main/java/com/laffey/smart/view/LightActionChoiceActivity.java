package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLightActionChoiceBinding;
import com.laffey.smart.event.ColorLightSceneEvent;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LightActionChoiceActivity extends BaseActivity {
    private ActivityLightActionChoiceBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightActionChoiceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        //ButterKnife.bind(this);

        initStatusBar();

        Typeface iconface = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.lightnessIc.setTypeface(iconface);
        mViewBinding.tempIc.setTypeface(iconface);

        EventBus.getDefault().register(this);
        mViewBinding.includeToolbar.tvToolbarTitle.setText("选择动作");
        mActivityTag = getIntent().getStringExtra("activity_tag");

        mViewBinding.lightnessView.setOnClickListener(this::onViewClicked);
        mViewBinding.temperatureView.setOnClickListener(this::onViewClicked);

        if ("LightDetailActivity".equals(mActivityTag)) {
            mViewBinding.temperatureView.setVisibility(View.GONE);
        } else if ("ColorLightDetailActivity".equals(mActivityTag)) {
            mViewBinding.temperatureView.setVisibility(View.VISIBLE);
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Subscribe
    public void refreshData(ColorLightSceneEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.lightnessView) {
            ColorTemperatureChoiceActivity.start2(this, 2, 0);
        } else if (view.getId() == R.id.temperatureView) {
            ColorTemperatureChoiceActivity.start2(this, 1, 0);
        }
    }

    private String mActivityTag;

    public static void start(Context context, String tag) {
        Intent intent = new Intent(context, LightActionChoiceActivity.class);
        intent.putExtra("activity_tag", tag);
        context.startActivity(intent);
    }
}
