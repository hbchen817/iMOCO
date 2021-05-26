package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.rexense.imoco.R;
import com.rexense.imoco.databinding.ActivityLightActionChoiceBinding;
import com.rexense.imoco.event.ColorLightSceneEvent;

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

        EventBus.getDefault().register(this);
        mViewBinding.includeToolbar.tvToolbarTitle.setText("选择动作");

        mViewBinding.lightnessView.setOnClickListener(this::onViewClicked);
        mViewBinding.temperatureView.setOnClickListener(this::onViewClicked);
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

    public static void start(Context context) {
        Intent intent = new Intent(context, LightActionChoiceActivity.class);
        context.startActivity(intent);
    }
}
