package com.xiezhu.jzj.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiezhu.jzj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FreshAirDetailActivity extends AppCompatActivity {
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.icon_timer)
    TextView iconTimer;
    @BindView(R.id.icon_switch)
    TextView iconSwitch;
    @BindView(R.id.icon_lock)
    TextView iconLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_air_fan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        iconTimer.setTypeface(iconfont);
        iconSwitch.setTypeface(iconfont);
        iconLock.setTypeface(iconfont);
    }

    @OnClick({R.id.wind_low, R.id.wind_mid, R.id.wind_high, R.id.timer, R.id.mSwitch, R.id.lock})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.wind_low) {

        } else if (view.getId() == R.id.wind_mid) {

        } else if (view.getId() == R.id.wind_high) {

        } else if (view.getId() == R.id.timer) {

        } else if (view.getId() == R.id.mSwitch) {

        } else if (view.getId() == R.id.lock) {

        }
    }
}
