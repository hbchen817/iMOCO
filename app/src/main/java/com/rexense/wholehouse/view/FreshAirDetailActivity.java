package com.rexense.wholehouse.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityFreshAirFanBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FreshAirDetailActivity extends AppCompatActivity {
    private ActivityFreshAirFanBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityFreshAirFanBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initView();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.iconTimer.setTypeface(iconfont);
        mViewBinding.iconSwitch.setTypeface(iconfont);
        mViewBinding.iconLock.setTypeface(iconfont);

        mViewBinding.windLow.setOnClickListener(this::onViewClicked);
        mViewBinding.windMid.setOnClickListener(this::onViewClicked);
        mViewBinding.windHigh.setOnClickListener(this::onViewClicked);
        mViewBinding.timer.setOnClickListener(this::onViewClicked);
        mViewBinding.mSwitch.setOnClickListener(this::onViewClicked);
        mViewBinding.lock.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int resId = view.getId();
        if (resId == R.id.wind_low) {

        } else if (resId == R.id.wind_mid) {

        } else if (resId == R.id.wind_high) {

        } else if (resId == R.id.timer) {

        } else if (resId == R.id.mSwitch) {

        } else if (resId == R.id.lock) {

        }
    }
}
