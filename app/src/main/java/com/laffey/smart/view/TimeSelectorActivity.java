package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cncoderx.wheelview.Wheel3DView;
import com.laffey.smart.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeSelectorActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.time_hour_wv)
    Wheel3DView mTimeHourWV;
    @BindView(R.id.time_min_wv)
    Wheel3DView mTimeMinWV;
    @BindView(R.id.once_cb)
    CheckBox mOnceCB;

    private List<String> mHourList = new ArrayList<>();
    private List<String> mMinList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selector);
        ButterKnife.bind(this);

        initStatusBar();

        initView();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void initView() {
        mTitle.setText(getString(R.string.timer_point));
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        for (int i = 0; i < 24; i++) {
            if (i < 10) mHourList.add("0" + i);
            else mHourList.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) mMinList.add("0" + i);
            else mMinList.add(String.valueOf(i));
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        int hour = Integer.valueOf(time.split(":")[0]);
        int min = Integer.valueOf(time.split(":")[1]);

        mTimeHourWV.setEntries(mHourList);
        mTimeHourWV.setCurrentIndex(hour);

        mTimeMinWV.setEntries(mMinList);
        mTimeMinWV.setCurrentIndex(min);

        mOnceCB.setClickable(false);
    }

    @OnClick({R.id.tv_toolbar_right})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right: {
                Intent intent = new Intent(this, NewSceneActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}