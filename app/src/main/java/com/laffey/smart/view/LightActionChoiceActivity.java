package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.laffey.smart.R;
import com.laffey.smart.event.ColorLightSceneEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LightActionChoiceActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_action_choice);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mTitle.setText("选择动作");

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

    @OnClick({R.id.lightnessView, R.id.temperatureView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lightnessView:
                ColorTemperatureChoiceActivity.start2(this, 2, 0);
                break;
            case R.id.temperatureView:
                ColorTemperatureChoiceActivity.start2(this, 1, 0);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LightActionChoiceActivity.class);
        context.startActivity(intent);
    }
}
