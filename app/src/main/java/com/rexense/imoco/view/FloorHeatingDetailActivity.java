package com.rexense.imoco.view;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.imoco.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_heating);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
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
