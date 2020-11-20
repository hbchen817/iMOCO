package com.xiezhu.jzj.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiezhu.jzj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloorHeatingDetailActivity extends AppCompatActivity {


    @BindView(R.id.temperature_value)
    AlignTextView mTemperatureValue;
    @BindView(R.id.temperature)
    TextView mTemperature;
    @BindView(R.id.humidity)
    TextView mHumidity;
    @BindView(R.id.temperature2)
    TextView mTemperatureSmall;
    @BindView(R.id.temperatureSeekBar)
    SeekBar mTemperatureProgressBar;
    @BindView(R.id.icon_lock)
    TextView iconLock;
    @BindView(R.id.icon_switch)
    TextView iconSwitch;
    @BindView(R.id.icon_timer)
    TextView iconTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_heating);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        iconLock.setTypeface(typeface);
        iconSwitch.setTypeface(typeface);
        iconTimer.setTypeface(typeface);
        mTemperatureProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTemperatureValue.setText(String.valueOf(i + 16));
                mTemperatureSmall.setText(String.valueOf(i + 16));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @OnClick({R.id.lock, R.id.mSwitch, R.id.timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lock:
                break;
            case R.id.mSwitch:
                break;
            case R.id.timer:
                break;
        }
    }
}
