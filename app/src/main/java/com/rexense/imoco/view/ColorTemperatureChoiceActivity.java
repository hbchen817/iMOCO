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
    private boolean mSceneAction;
    private int mSceneType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_temperature_choice);
        ButterKnife.bind(this);
        mTypeName = (TextView) findViewById(R.id.mTypeName);
        mMaxValue = (TextView) findViewById(R.id.mMaxValue);
        mRightText.setText("保存");
        mTitle.setText("选择色温");
        mSceneType = getIntent().getIntExtra("sceneType", 0);
        if (mSceneType > 0) {
            mSceneAction = true;
            int value = getIntent().getIntExtra("value", 0);
            mTypeName.setText(mSceneType == 1 ? R.string.color_temperature : R.string.lightness);
            mMaxValue.setText(mSceneType == 1 ? "1000" : "100");
            mTitle.setText("场景动作");
            mSeekBar.setProgress(value);
        } else {
            int temperature = getIntent().getIntExtra("temperature", 0);
            mMaxValue.setText("1000");
            mSeekBar.setProgress(temperature);
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
                if (mSceneAction) {
                    ColorLightSceneEvent event = new ColorLightSceneEvent();
                    event.setmType(mSceneType == 1 ? ColorLightSceneEvent.TYPE.TYPE_COLOR_TEMPERATURE : ColorLightSceneEvent.TYPE.TYPE_LIGHTNESS);
                    event.setmValue(mSeekBar.getProgress());
                    EventBus.getDefault().post(event);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("temperature", mSeekBar.getProgress() * 10);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }
}
