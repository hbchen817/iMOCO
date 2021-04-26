package com.rexense.wholehouse.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.wholehouse.R;

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
        switch (view.getId()) {
            case R.id.wind_low:
                break;
            case R.id.wind_mid:
                break;
            case R.id.wind_high:
                break;
            case R.id.timer:
                break;
            case R.id.mSwitch:
                break;
            case R.id.lock:
                break;
        }
    }
}
