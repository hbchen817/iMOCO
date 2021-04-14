package com.laffey.smart.view;

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
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.TSLHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewAirForFSSActivity extends DetailActivity {
    @BindView(R.id.includeDetailRl)
    RelativeLayout mTopbarRoot;
    @BindView(R.id.includeDetailImgMore)
    ImageView mTopbarMore;
    @BindView(R.id.includeDetailImgBack)
    ImageView mTopbarBack;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTopbarTitle;
    @BindView(R.id.low_wind_ic)
    TextView mLowWindIC;
    @BindView(R.id.mid_wind_ic)
    TextView mMidWindIC;
    @BindView(R.id.high_wind_ic)
    TextView mHighWindIC;
    @BindView(R.id.switch_ic)
    TextView mSwitchIC;
    @BindView(R.id.timing_ic)
    TextView mTimingIC;
    @BindView(R.id.low_wind_tv)
    TextView mLowWindTV;
    @BindView(R.id.mid_wind_tv)
    TextView mMidWindTV;
    @BindView(R.id.high_wind_tv)
    TextView mHighWindTV;
    @BindView(R.id.switch_tv)
    TextView mSwitchTV;
    @BindView(R.id.fanmode_show_tv)
    TextView mFanModeShowTV;
    @BindView(R.id.timing_tv)
    TextView mTimingTV;
    @BindView(R.id.low_wind_layout)
    RelativeLayout mLowWindLayout;
    @BindView(R.id.mid_wind_layout)
    RelativeLayout mMidWindLayout;
    @BindView(R.id.high_wind_layout)
    RelativeLayout mHighWindLayout;
    @BindView(R.id.switch_layout)
    RelativeLayout mSwitchLayout;
    @BindView(R.id.timing_layout)
    RelativeLayout mTimingLayout;

    private int mFanMode = 0;//0: 低风 1: 中风 2: 高风
    private int mPowerSwitch = 3;// 3: 关闭 4: 打开

    private TSLHelper mTSLHelper;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 风速
        if (propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2) != null && propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2).length() > 0) {
            String windSpeed = propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2);
            mFanMode = Integer.parseInt(windSpeed) - 2;
            refreshFanLayout(mFanMode);
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2);
            mPowerSwitch = Integer.parseInt(powerSwitch) + 3;
            refreshFanLayout(mPowerSwitch);
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_air_for_full_screen);
        ButterKnife.bind(this);

        initStatusBar();
        initView();
        mTSLHelper = new TSLHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTopbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopbarTitle.setText(R.string.new_air);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mTopbarRoot.setBackgroundColor(Color.WHITE);
        mTopbarMore.setVisibility(View.GONE);
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mLowWindIC.setTypeface(iconfont);
        mMidWindIC.setTypeface(iconfont);
        mHighWindIC.setTypeface(iconfont);
        mSwitchIC.setTypeface(iconfont);
        mTimingIC.setTypeface(iconfont);
    }

    @OnClick({R.id.low_wind_layout, R.id.mid_wind_layout, R.id.high_wind_layout, R.id.switch_layout, R.id.timing_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.low_wind_layout: {
                // 低风
                if (mPowerSwitch == 4)
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 2});
                break;
            }
            case R.id.mid_wind_layout: {
                // 中风
                if (mPowerSwitch == 4)
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 3});
                break;
            }
            case R.id.high_wind_layout: {
                // 高风
                if (mPowerSwitch == 4)
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 4});
                break;
            }
            case R.id.switch_layout: {
                // 开关
                if (mPowerSwitch == 4) {
                    // 关闭
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    // 打开
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            }
            case R.id.timing_layout: {
                // 定时
                if (mPowerSwitch == 4)
                    PluginHelper.cloudTimer(NewAirForFSSActivity.this, mIOTId, mProductKey);
                break;
            }
        }
    }

    private void refreshFanLayout(int flag) {
        switch (flag) {
            case 0: {
                // 低风
                if (mPowerSwitch == 4) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.appgreen));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                } else if (mPowerSwitch == 3) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.all_8_2));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }

                mFanModeShowTV.setText(getString(R.string.fan_speed_low));
                if (mPowerSwitch == 3) break;
                mFanModeShowTV.setTextColor(getResources().getColor(R.color.appgreen));
                break;
            }
            case 1: {
                // 中风
                if (mPowerSwitch == 4) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.appgreen));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                } else if (mPowerSwitch == 3) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.all_8_2));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }

                mFanModeShowTV.setText(getString(R.string.fan_speed_mid));
                if (mPowerSwitch == 3) break;
                mFanModeShowTV.setTextColor(getResources().getColor(R.color.appgreen));
                break;
            }
            case 2: {
                // 高风
                if (mPowerSwitch == 4) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.appgreen));
                } else if (mPowerSwitch == 3) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.all_8_2));

                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.all_8_2));

                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));
                }

                mFanModeShowTV.setText(getString(R.string.fan_speed_high));
                if (mPowerSwitch == 3) break;
                mFanModeShowTV.setTextColor(getResources().getColor(R.color.appgreen));
                break;
            }
            case 3: {
                // 关闭
                mSwitchIC.setTextColor(getResources().getColor(R.color.all_8_2));
                mSwitchTV.setTextColor(getResources().getColor(R.color.all_8_2));
                mTimingIC.setTextColor(getResources().getColor(R.color.all_8_2));
                mTimingTV.setTextColor(getResources().getColor(R.color.all_8_2));

                if (mFanMode == 0) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));
                } else {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }

                if (mFanMode == 1) {
                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));
                } else {
                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }

                if (mFanMode == 2) {
                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen_2);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.appgreen_2));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.appgreen_2));
                } else {
                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }

                mFanModeShowTV.setTextColor(getResources().getColor(R.color.appgreen_2));
                break;
            }

            case 4: {
                // 打开
                mSwitchIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mSwitchTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mTimingIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mTimingTV.setTextColor(getResources().getColor(R.color.index_imgcolor));

                if (mFanMode == 0) {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.appgreen));
                } else {
                    mLowWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mLowWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mLowWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                }

                if (mFanMode == 1) {
                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.appgreen));
                } else {
                    mMidWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mMidWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mMidWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                }

                if (mFanMode == 2) {
                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner_appgreen);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.appgreen));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.appgreen));
                } else {
                    mHighWindLayout.setBackgroundResource(R.drawable.shape_corner);
                    mHighWindIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                    mHighWindTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                }
                mFanModeShowTV.setTextColor(getResources().getColor(R.color.appgreen));
                break;
            }
        }
    }
}