package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.view.AirConditionerForFSSActivity;
import com.laffey.smart.view.DetailActivity;
import com.laffey.smart.view.FloorHeatingForFSSActivity;
import com.laffey.smart.view.NewAirForFSSActivity;
import com.laffey.smart.view.SceneListForFSSActivity;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullScreenSwitchActivity extends DetailActivity {
    @BindView(R.id.includeDetailRl)
    RelativeLayout mTopbar;
    @BindView(R.id.includeDetailImgBack)
    ImageView mBackView;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTitleText;
    @BindView(R.id.air_conditioner_switch)
    TextView mACSwitchTV;
    @BindView(R.id.new_air_switch)
    TextView mNASwitchTV;
    @BindView(R.id.floor_heating_switch)
    TextView mFHSwitchTV;
    @BindView(R.id.scene_layout)
    RelativeLayout mSceneLayout;
    @BindView(R.id.air_conditioner_layout)
    RelativeLayout mAirConditionerLayout;
    @BindView(R.id.new_air_layout)
    RelativeLayout mNewAirLayout;
    @BindView(R.id.floor_heating_layout)
    RelativeLayout mFloorHeatingLayout;

    private TSLHelper mTSLHelper;
    private int mACPowerSwitch = 0;
    private int mNAPowerSwitch = 0;
    private int mFHPowerSwitch = 0;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        // 空调开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1);
            mACPowerSwitch = Integer.parseInt(powerSwitch);
            if (mACPowerSwitch == 0) {
                mACSwitchTV.setTextColor(getResources().getColor(R.color.white3));
            } else {
                mACSwitchTV.setTextColor(getResources().getColor(R.color.appcolor));
            }
        }

        // 新风开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2);
            mNAPowerSwitch = Integer.parseInt(powerSwitch);
            if (mNAPowerSwitch == 0) {
                mNASwitchTV.setTextColor(getResources().getColor(R.color.white3));
            } else {
                mNASwitchTV.setTextColor(getResources().getColor(R.color.appcolor));
            }
        }

        // 地暖开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3);
            mFHPowerSwitch = Integer.parseInt(powerSwitch);
            if (mFHPowerSwitch == 0) {
                mFHSwitchTV.setTextColor(getResources().getColor(R.color.white3));
            } else {
                mFHSwitchTV.setTextColor(getResources().getColor(R.color.appcolor));
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        initStatusBar();

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mACSwitchTV.setTypeface(iconfont);
        mNASwitchTV.setTypeface(iconfont);
        mFHSwitchTV.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        mTopbar.setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @OnClick({R.id.scene_layout, R.id.air_conditioner_layout, R.id.new_air_layout, R.id.floor_heating_layout,
            R.id.air_conditioner_switch, R.id.new_air_switch, R.id.floor_heating_switch})
    protected void onViewClicked(View view) {
        int resId = view.getId();
        if (resId == R.id.scene_layout) {
            // 场景
            Intent intent = new Intent(this, SceneListForFSSActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", CTSL.TEST_PK_FULL_SCREEN_SWITCH);
            intent.putExtra("name", "空调");
            intent.putExtra("owned", mOwned);
            startActivity(intent);
        } else if (resId == R.id.air_conditioner_layout) {
            // 空调
            Intent intent = new Intent(mActivity, AirConditionerForFSSActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", CTSL.TEST_PK_FULL_SCREEN_SWITCH);
            intent.putExtra("name", "空调");
            intent.putExtra("owned", mOwned);
            startActivity(intent);
        } else if (resId == R.id.new_air_layout) {
            // 新风
            Intent intent = new Intent(mActivity, NewAirForFSSActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", CTSL.TEST_PK_FULL_SCREEN_SWITCH);
            intent.putExtra("name", "新风");
            intent.putExtra("owned", mOwned);
            startActivity(intent);
        } else if (resId == R.id.floor_heating_layout) {
            // 地暖
            Intent intent = new Intent(mActivity, FloorHeatingForFSSActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", CTSL.TEST_PK_FULL_SCREEN_SWITCH);
            intent.putExtra("name", "地暖");
            intent.putExtra("owned", mOwned);
            startActivity(intent);
        } else if (resId == R.id.air_conditioner_switch) {
            // 空调开关
            if (mACPowerSwitch == 0) {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
            } else {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
            }
        } else if (resId == R.id.new_air_switch) {
            // 新风开关
            if (mNAPowerSwitch == 0) {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
            } else {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
            }
        } else if (resId == R.id.floor_heating_switch) {
            // 新风开关
            if (mFHPowerSwitch == 0) {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_ON});
            } else {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_OFF});
            }
        }
    }
}