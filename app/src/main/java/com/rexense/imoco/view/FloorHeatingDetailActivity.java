package com.rexense.imoco.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityFloorHeatingBinding;

public class FloorHeatingDetailActivity extends AppCompatActivity {
    private ActivityFloorHeatingBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityFloorHeatingBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        initView();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.iconLock.setTypeface(typeface);
        mViewBinding.iconSwitch.setTypeface(typeface);
        mViewBinding.iconTimer.setTypeface(typeface);
        mViewBinding.temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mViewBinding.temperatureValue.setText(String.valueOf(i + 16));
                mViewBinding.temperature2.setText(String.valueOf(i + 16));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mViewBinding.lock.setOnClickListener(this::onViewClicked);
        mViewBinding.mSwitch.setOnClickListener(this::onViewClicked);
        mViewBinding.timer.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.lock) {
        } else if (id == R.id.mSwitch) {
        } else if (id == R.id.timer) {
        }
    }
}
