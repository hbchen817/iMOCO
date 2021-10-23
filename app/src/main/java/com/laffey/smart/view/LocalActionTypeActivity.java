package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalActionTypeBinding;
import com.vise.log.ViseLog;

public class LocalActionTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLocalActionTypeBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private static final String ACTIVITY_TAG = "activity_tag";
    private static final String IOT_ID = "iot_id";

    private String mGatewayId;
    private String mIotId;
    private String mActivityTag;

    public static void start(Context context, String gatewayId, String iotId, String activityTag) {
        Intent intent = new Intent(context, LocalActionTypeActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(ACTIVITY_TAG, activityTag);
        intent.putExtra(IOT_ID, iotId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalActionTypeBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        initData();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.propertyIv.setTypeface(iconfont);
        mViewBinding.propertyGoIv.setTypeface(iconfont);
        mViewBinding.sceneIv.setTypeface(iconfont);
        mViewBinding.sceneGoIv.setTypeface(iconfont);

        mViewBinding.propertyLayout.setOnClickListener(this);
        mViewBinding.sceneLayout.setOnClickListener(this);
    }

    private void initData() {
        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mActivityTag = getIntent().getStringExtra(ACTIVITY_TAG);
        mIotId = getIntent().getStringExtra(IOT_ID);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.select_response_content));
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.propertyLayout.getId()) {
            LocalActionDevsActivity.start(this, mGatewayId);
        } else if (v.getId() == mViewBinding.sceneLayout.getId()) {
            LocalActionScenesActivity.start(this, mGatewayId, mIotId, null, mActivityTag);
        }
    }
}