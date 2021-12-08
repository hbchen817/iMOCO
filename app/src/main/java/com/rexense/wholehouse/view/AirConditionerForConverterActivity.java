package com.rexense.wholehouse.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityAirConditionerForConverterBinding;
import com.rexense.wholehouse.model.AirConditionerConverter;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.RealtimeDataParser;
import com.rexense.wholehouse.presenter.RealtimeDataReceiver;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.sdk.APIChannel;
import com.rexense.wholehouse.utility.GsonUtil;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.vise.log.ViseLog;

public class AirConditionerForConverterActivity extends BaseActivity {
    private ActivityAirConditionerForConverterBinding mViewBinding;

    private static final String DEVICES_NICK_NAMES = "virtual_device_nick_names";
    private static final String END_POINT = "end_point";
    private static final String NICKNAMES = "nicknames";
    private static final String IOT_ID = "iot_id";
    private static final String PRODUCT_KEY = "product_key";

    private int mTargetTem = 16;
    private String[] mWorkModes;
    private String[] mWorkModeICs;
    private int mWorkMode = 3;
    private String[] mFanModes;
    private String[] mFanModeICs;
    private int mFanMode = 1;

    private String mEndPoint;
    private String mNicknames;
    private String mIOTId;
    private String mProductKey;

    private TSLHelper mTSLHelper;
    private MyReceiver mMyReceiver;

    public static void start(Activity activity, String iotId, String productKey, String endPoint, String nicknames, int requestCode) {
        Intent intent = new Intent(activity, AirConditionerForConverterActivity.class);
        intent.putExtra(END_POINT, endPoint);
        intent.putExtra(NICKNAMES, nicknames);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(PRODUCT_KEY, productKey);
        activity.startActivityForResult(intent, requestCode);
    }

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }
        // 室温
        if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + mEndPoint) != null
                && propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + mEndPoint).length() > 0) {
            String currentTem = propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + mEndPoint);
            mViewBinding.currentTemTv.setText(currentTem);
        }
        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + mEndPoint) != null
                && propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + mEndPoint).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + mEndPoint);
            mTargetTem = Integer.parseInt(targetTem);
            mViewBinding.targetTemShowTv.setText(targetTem);
            mViewBinding.targetTemTv.setText(targetTem);
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint) != null
                && propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint);
            int power = Integer.parseInt(powerSwitch);
            refreshViewState(power);
        }

        // 风速
        if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + mEndPoint) != null
                && propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + mEndPoint).length() > 0) {
            String windSpeed = propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + mEndPoint);
            mFanMode = Integer.parseInt(windSpeed);
            ViseLog.d("mFanMode = " + mFanMode);
            switch (mFanMode) {
                case 1: {
                    // 低风
                    mViewBinding.fanmodeTv.setText(mFanModes[0]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[0]);
                    break;
                }
                case 2: {
                    // 中风
                    mViewBinding.fanmodeTv.setText(mFanModes[1]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[1]);
                    break;
                }
                case 3: {
                    // 高风
                    mViewBinding.fanmodeTv.setText(mFanModes[2]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[2]);
                    break;
                }
            }

        }

        // 工作模式
        if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + mEndPoint) != null
                && propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + mEndPoint).length() > 0) {
            String workMode = propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + mEndPoint);
            mWorkMode = Integer.parseInt(workMode);
            switch (mWorkMode) {
                case 3: {
                    // 制冷
                    mViewBinding.workmodeTv.setText(mWorkModes[0]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[0]);
                    break;
                }
                case 4: {
                    // 制热
                    mViewBinding.workmodeTv.setText(mWorkModes[1]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[1]);
                    break;
                }
                case 7: {
                    // 新风
                    mViewBinding.workmodeTv.setText(mWorkModes[2]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[2]);
                    break;
                }
                case 8: {
                    // 除湿
                    mViewBinding.workmodeTv.setText(mWorkModes[3]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[3]);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAirConditionerForConverterBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mEndPoint = getIntent().getStringExtra(END_POINT);
        mNicknames = getIntent().getStringExtra(NICKNAMES);
        mIOTId = getIntent().getStringExtra(IOT_ID);
        mProductKey = getIntent().getStringExtra(PRODUCT_KEY);

        initStatusBar();

        mTSLHelper = new TSLHelper(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.subTv.setTypeface(iconfont);
        mViewBinding.addTv.setTypeface(iconfont);
        mViewBinding.workmodeIc.setTypeface(iconfont);
        mViewBinding.switchIc.setTypeface(iconfont);
        mViewBinding.fanmodeIc.setTypeface(iconfont);
        mViewBinding.timingIc.setTypeface(iconfont);

        mWorkModes = getResources().getStringArray(R.array.work_modes_2);
        mWorkModeICs = getResources().getStringArray(R.array.work_mode_ics_2);

        mFanModes = getResources().getStringArray(R.array.fan_modes_2);
        mFanModeICs = getResources().getStringArray(R.array.fan_mode_ics);

        mViewBinding.switchLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.subTv.setOnClickListener(this::onViewClicked);
        mViewBinding.addTv.setOnClickListener(this::onViewClicked);
        mViewBinding.workmodeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.fanmodeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.timingLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.timingRootLayout.setVisibility(View.GONE);

        mViewBinding.workmodeTv.setText("--");
        mViewBinding.workmodeIc.setText("--");

        mViewBinding.fanmodeTv.setText("--");
        mViewBinding.fanmodeIc.setText("--");

        initData();
    }

    private void initData() {
        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                        // 处理属性通知回调
                        ViseLog.d("实时 = " + GsonUtil.toJson(JSONObject.parseObject((String) msg.obj)));
                        ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                        updateState(propertyEntry);
                        break;
                    default:
                        break;
                }
                return false;
            }
        }));

        getProperty();

        mMyReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UPDATE_VIRTUAL_AIRC_NICKNAME);
        registerReceiver(mMyReceiver, filter);
    }

    // 主动获取设备属性
    private void getProperty() {
        TSLHelper.getProperty(this, mIOTId, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(AirConditionerForConverterActivity.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(AirConditionerForConverterActivity.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                ViseLog.d("处理获取属性回调 = " + GsonUtil.toJson(JSONObject.parseObject(result)));
                JSONObject items = JSON.parseObject(result);
                if (items != null) {
                    TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                    updateState(propertyEntry);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.includeToolbar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getColor(R.color.appcolor));
        }
        String[] names = mNicknames.split(",");
        mViewBinding.includeToolbar.includeDetailLblTitle.setText(names[Integer.parseInt(mEndPoint) - 1]);
        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1)
            mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.VISIBLE);
        else mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.GONE);
        mViewBinding.includeToolbar.includeDetailImgMore.setOnClickListener(this::onViewClicked);
    }

    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.switch_layout) {
            // 开关
            if (mViewBinding.switchIc.getCurrentTextColor() == getResources().getColor(R.color.index_imgcolor)) {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (id == R.id.sub_tv) {
            // 减
            if (mViewBinding.subTv.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                if (mTargetTem > 16) {
                    mTargetTem--;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_TargetTemperature_ + mEndPoint}, new String[]{"" + mTargetTem});
            }
        } else if (id == R.id.add_tv) {
            // 加
            if (mViewBinding.addTv.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                if (mTargetTem < 32) {
                    mTargetTem++;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_TargetTemperature_ + mEndPoint}, new String[]{"" + mTargetTem});
            }
        } else if (id == R.id.workmode_layout) {
            // 模式
            if (mViewBinding.workmodeIc.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                if (mWorkMode == 3) {
                    mWorkMode = 4;
                } else if (mWorkMode == 4) {
                    mWorkMode = 7;
                } else if (mWorkMode == 7) {
                    mWorkMode = 8;
                } else if (mWorkMode == 8) {
                    mWorkMode = 3;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_WorkMode_ + mEndPoint}, new String[]{"" + mWorkMode});
            }
        } else if (id == R.id.fanmode_layout) {
            // 风速
            if (mViewBinding.fanmodeIc.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                switch (mFanMode) {
                    case 1: {
                        // 低风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_WindSpeed_ + mEndPoint}, new String[]{"2"});
                        break;
                    }
                    case 2: {
                        // 中风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_WindSpeed_ + mEndPoint}, new String[]{"3"});
                        break;
                    }
                    case 3: {
                        // 高风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_WindSpeed_ + mEndPoint}, new String[]{"1"});
                        break;
                    }
                }
            }
        } else if (id == R.id.timing_layout) {
            // 定时
            /*if (mViewBinding.switchIc.getCurrentTextColor() == getResources().getColor(R.color.index_imgcolor)) {
                PluginHelper.cloudTimer(AirConditionerForMultiDevActivity.this, mIOTId, mProductKey);
            }*/
        } else if (id == mViewBinding.includeToolbar.includeDetailImgMore.getId()) {
            // 更多
            MoreVirtualAirCActivity.start(this, mIOTId, mEndPoint, mNicknames, Constant.REQUESTCODE_CALLDELAIRCONDITIONER);
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
                int all82 = getResources().getColor(R.color.all_8_2);
                mViewBinding.switchIc.setTextColor(all82);
                mViewBinding.switchTv.setTextColor(all82);
                mViewBinding.workmodeIc.setTextColor(all82);
                mViewBinding.workmodeTv.setTextColor(all82);
                mViewBinding.fanmodeIc.setTextColor(all82);
                mViewBinding.fanmodeTv.setTextColor(all82);
                mViewBinding.timingIc.setTextColor(all82);
                mViewBinding.timingTv.setTextColor(all82);
                int white2 = getResources().getColor(R.color.white2);
                mViewBinding.subTv.setTextColor(white2);
                mViewBinding.addTv.setTextColor(white2);
                mViewBinding.targetTemShowTv.setTextColor(white2);
                mViewBinding.targetTemTv.setTextColor(white2);
                break;
            }
            case 1: {
                // 打开
                int indexImgColor = getResources().getColor(R.color.index_imgcolor);
                mViewBinding.switchIc.setTextColor(indexImgColor);
                mViewBinding.switchTv.setTextColor(indexImgColor);
                mViewBinding.workmodeIc.setTextColor(indexImgColor);
                mViewBinding.workmodeTv.setTextColor(indexImgColor);
                mViewBinding.fanmodeIc.setTextColor(indexImgColor);
                mViewBinding.fanmodeTv.setTextColor(indexImgColor);
                mViewBinding.timingIc.setTextColor(indexImgColor);
                mViewBinding.timingTv.setTextColor(indexImgColor);
                int white = getResources().getColor(R.color.white);
                mViewBinding.subTv.setTextColor(white);
                mViewBinding.addTv.setTextColor(white);
                mViewBinding.targetTemShowTv.setTextColor(white);
                mViewBinding.targetTemTv.setTextColor(white);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
        unregisterReceiver(mMyReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ACTION_UPDATE_VIRTUAL_AIRC_NICKNAME.equals(action)) {
                mNicknames = intent.getStringExtra(DEVICES_NICK_NAMES);
                String[] names = mNicknames.split(",");
                mViewBinding.includeToolbar.includeDetailLblTitle.setText(names[Integer.parseInt(mEndPoint) - 1]);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUESTCODE_CALLDELAIRCONDITIONER) {
            if (resultCode == Constant.RESULTCODE_CALLDELAIRCONDITIONER) {
                setResult(Constant.RESULTCODE_CALLDELAIRCONDITIONER, data);
                finish();
            }
        }
    }
}