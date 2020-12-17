package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rexense.imoco.R;
import com.rexense.imoco.event.ColorLightSceneEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorTemperatureChoiceActivity extends BaseActivity {

    @BindView(R.id.lightnessProgressBar)
    SeekBar mSeekBar;
    @BindView(R.id.tv_toolbar_right)
    TextView mRightText;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;

    private TextView mTypeName;
    private TextView mMaxValue;
    private TextView mMinValue;
    private boolean mSceneAction;
    private int mSceneType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_temperature_choice);
        ButterKnife.bind(this);
        mTypeName = findViewById(R.id.mTypeName);
        mMaxValue = findViewById(R.id.mMaxValue);
        mMinValue = findViewById(R.id.minValue);
        mRightText.setText("保存");
        mTitle.setText("选择色温");
        mSceneType = getIntent().getIntExtra("sceneType", 0);
        if (mSceneType > 0) {
            mSceneAction = true;
            int value = getIntent().getIntExtra("value", 0);
            mTypeName.setText(mSceneType == 1 ? R.string.color_temperature : R.string.lightness);
            mMaxValue.setText(mSceneType == 1 ? "6500" : "100");
            mMinValue.setText(mSceneType == 1 ? "2700" : "0");
            mTitle.setText("场景动作");
            mSeekBar.setMax(mSceneType == 1 ? 65 - 27 : 100);
            mSeekBar.setProgress(mSceneType == 1 ? (value / 100) - 27 : value);
        } else {
            int temperature = getIntent().getIntExtra("temperature", 0);
            mMaxValue.setText("6500");
            mMinValue.setText("2700");
            mSeekBar.setMax(65 - 27);
            mSeekBar.setProgress((temperature / 100) - 27);
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

    @OnClick({R.id.tv_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                int progress = mSeekBar.getProgress();
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
                break;
        }
    }
}
