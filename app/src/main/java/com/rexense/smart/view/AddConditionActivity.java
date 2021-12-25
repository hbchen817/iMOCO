package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityAddConditionBinding;

public class AddConditionActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddConditionBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAddConditionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        boolean hasTimeCondition = getIntent().getBooleanExtra("has_time_condition", false);
        mViewBinding.timeLayout.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);
        mViewBinding.timeRangeLayout.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);
        mViewBinding.devDivider.setVisibility(hasTimeCondition ? View.GONE : View.VISIBLE);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.timerIv.setTypeface(iconfont);
        mViewBinding.timerRangeIv.setTypeface(iconfont);
        mViewBinding.devIv.setTypeface(iconfont);
        mViewBinding.timerGoIv.setTypeface(iconfont);
        mViewBinding.timerRangeGoIv.setTypeface(iconfont);
        mViewBinding.devGoIv.setTypeface(iconfont);

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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.add_condition));

        mViewBinding.timeLayout.setOnClickListener(this);
        mViewBinding.timeRangeLayout.setOnClickListener(this);
        mViewBinding.devLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.time_layout) {
            Intent intent = new Intent(this, TimeSelectorActivity.class);
            startActivity(intent);
        } else if (id == R.id.time_range_layout) {
            Intent intent = new Intent(this, TimeRangeSelectorActivity.class);
            startActivity(intent);
        } else if (id == R.id.dev_layout) {
            Intent intent = new Intent(this, DevListForCAActivity.class);
            startActivity(intent);
        }
    }
}