package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityFloorHeatingForFullScreenBinding;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.GsonUtil;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloorHeatingActivity extends DetailActivity {
    ActivityFloorHeatingForFullScreenBinding mViewBinding;

    private int mPowerSwitch = 0;// 0: 关闭  1: 打开
    private int mTargetTem = 0;

    private TSLHelper mTSLHelper;

    private int mOrange3;
    private int mOrange2;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 开关
        ViseLog.d("更新状态 = \n" + GsonUtil.toJson(propertyEntry));
        if (propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating) != null && propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating);
            mPowerSwitch = Integer.parseInt(powerSwitch);
            refreshViewState(mPowerSwitch);
        }

        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating);
            mTargetTem = Integer.parseInt(targetTem);
            mViewBinding.temperatureValue.setText(String.valueOf(mTargetTem));
            mViewBinding.temperature2.setText(String.valueOf(mTargetTem));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mViewBinding.temperatureSeekBar.setProgress(mTargetTem - 16, true);
            else mViewBinding.temperatureSeekBar.setProgress(mTargetTem - 16);
        }

        // 当前温度
        if (propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating).length() > 0) {
            String currentTem = propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating);
            mViewBinding.temperature.setText(currentTem);
        }

        // 加热状态
        if (propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating).length() > 0) {
            String autoWorkMode = propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating);
            mViewBinding.autoWorkModeTv.setText("0".equals(autoWorkMode) ? R.string.close : R.string.open);
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityFloorHeatingForFullScreenBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mTSLHelper = new TSLHelper(this);

        initStatusBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.topbar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.iconSwitch.setTypeface(iconfont);
        mViewBinding.timingIc.setTypeface(iconfont);

        mViewBinding.temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String tem = String.valueOf(16 + progress);
                mViewBinding.temperatureValue.setText(tem);
                mViewBinding.temperature2.setText(tem);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_TargetTemperature_FloorHeating},
                        new String[]{"" + (16 + seekBar.getProgress())});
            }
        });
        mViewBinding.temperatureValue.setText("--");

        mOrange3 = ContextCompat.getColor(this, R.color.orange3);
        mOrange2 = ContextCompat.getColor(this, R.color.orange2);

        mViewBinding.switchLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.timingLayout.setOnClickListener(this::onViewClicked);

        // 主动获取设备属性
        new TSLHelper(this).getProperty(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_GETTSLPROPERTY) {
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    JSONObject items = JSON.parseObject((String) msg.obj);
                    if (items != null) {
                        TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                        updateState(propertyEntry);
                    }
                }
                return false;
            }
        }));

        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNPROPERTYNOTIFY) {
                    // 处理属性通知回调
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                    updateState(propertyEntry);
                }
                return false;
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.topbar.includeDetailLblTitle.setText(mName);
        mViewBinding.topbar.includeDetailRl.setBackgroundColor(Color.WHITE);//includeDetailRl
        mViewBinding.topbar.includeDetailImgMore.setVisibility(View.VISIBLE);
        mViewBinding.topbar.includeDetailImgMore.setOnClickListener(this::onViewClicked);
    }

    protected void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.switch_layout) {
            // 开关
            if (mPowerSwitch == 1) {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_PowerSwitch_FloorHeating}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_PowerSwitch_FloorHeating}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (viewId == R.id.timing_layout) {
            // 定时
            if (mPowerSwitch == 1) {
                PluginHelper.cloudTimer(this, mIOTId, mProductKey);
            }
        } else if (viewId == mViewBinding.topbar.includeDetailImgMore.getId()) {
            // 设备详情
            Intent intent;
            intent = new Intent(this, MoreSubdeviceActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", mProductKey);
            intent.putExtra("name", mName);
            intent.putExtra("owned", mOwned);
            startActivityForResult(intent, Constant.REQUESTCODE_CALLMOREACTIVITY);
        }
    }

    /**
     * 根据开关状态刷新ui
     *
     * @param flag 0:关闭 1:打开
     */
    private void refreshViewState(int flag) {
        switch (flag) {
            case 0: {
                // 关闭
                mViewBinding.iconSwitch.setTextColor(mOrange3);
                mViewBinding.temperatureValue.setTextColor(mOrange3);
                mViewBinding.temperature.setTextColor(mOrange3);
                mViewBinding.temperature2.setTextColor(mOrange3);
                mViewBinding.autoWorkModeTv.setTextColor(mOrange3);
                mViewBinding.temUnit1.setTextColor(mOrange3);
                mViewBinding.temUnit2.setTextColor(mOrange3);
                mViewBinding.timingIc.setTextColor(mOrange3);
                mViewBinding.temperatureSeekBar.setEnabled(false);
                break;
            }
            case 1: {
                // 打开
                mViewBinding.iconSwitch.setTextColor(mOrange2);
                mViewBinding.temperatureValue.setTextColor(mOrange2);
                mViewBinding.temperature.setTextColor(mOrange2);
                mViewBinding.temperature2.setTextColor(mOrange2);
                mViewBinding.autoWorkModeTv.setTextColor(mOrange2);
                mViewBinding.temUnit1.setTextColor(mOrange2);
                mViewBinding.temUnit2.setTextColor(mOrange2);
                mViewBinding.timingIc.setTextColor(mOrange2);
                mViewBinding.temperatureSeekBar.setEnabled(true);
                break;
            }
        }
    }
}