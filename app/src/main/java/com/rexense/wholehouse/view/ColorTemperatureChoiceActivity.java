package com.rexense.wholehouse.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.databinding.ActivityColorTemperatureChoiceBinding;
import com.rexense.wholehouse.event.ColorLightSceneEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorTemperatureChoiceActivity extends BaseActivity {
    private ActivityColorTemperatureChoiceBinding mViewBinding;

    private boolean mSceneAction;
    private int mSceneType;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityColorTemperatureChoiceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();

        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setText("保存");
        mViewBinding.includeToolbar.tvToolbarTitle.setText("选择色温");
        mSceneType = getIntent().getIntExtra("sceneType", 0);
        if (mSceneType > 0) {
            mSceneAction = true;
            int value = getIntent().getIntExtra("value", 0);
            mViewBinding.mTypeName.setText(mSceneType == 1 ? R.string.color_temperature : R.string.lightness);
            mViewBinding.mMaxValue.setText(mSceneType == 1 ? "6500" : "100");
            mViewBinding.minValue.setText(mSceneType == 1 ? "2700" : "0");
            mViewBinding.includeToolbar.tvToolbarTitle.setText("场景动作");
            mViewBinding.lightnessProgressBar.setMax(mSceneType == 1 ? 65 - 27 : 100);
            mViewBinding.lightnessProgressBar.setProgress(mSceneType == 1 ? (value / 100) - 27 : value);
        } else {
            int temperature = getIntent().getIntExtra("temperature", 0);
            mViewBinding.mMaxValue.setText("6500");
            mViewBinding.minValue.setText("2700");
            mViewBinding.lightnessProgressBar.setMax(65 - 27);
            mViewBinding.lightnessProgressBar.setProgress((temperature / 100) - 27);
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

    public static void start(Activity context, int temperature) {
        Intent intent = new Intent(context, ColorTemperatureChoiceActivity.class);
        intent.putExtra("temperature", temperature);
        context.startActivityForResult(intent, 1);
    }

    public static void start2(Context context, int sceneType, int value) {
        Intent intent = new Intent(context, ColorTemperatureChoiceActivity.class);
        intent.putExtra("sceneType", sceneType);
        intent.putExtra("value", value);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            int progress = mViewBinding.lightnessProgressBar.getProgress();
            if (mSceneAction) {
                ColorLightSceneEvent event = new ColorLightSceneEvent();
                event.setmType(mSceneType == 1 ? ColorLightSceneEvent.TYPE.TYPE_COLOR_TEMPERATURE : ColorLightSceneEvent.TYPE.TYPE_LIGHTNESS);
                event.setmValue(mSceneType == 1 ? (progress + 27) * 100 : progress);
                EventBus.getDefault().post(event);
            } else {
                Intent intent = new Intent();
                intent.putExtra("temperature", (progress + 27) * 100);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }
}
