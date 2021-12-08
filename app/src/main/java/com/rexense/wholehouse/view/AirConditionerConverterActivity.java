package com.rexense.wholehouse.view;

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
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kyleduo.switchbutton.SwitchButton;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityAirConditionerConverterBinding;
import com.rexense.wholehouse.model.AirConditionerConverter;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.RealtimeDataParser;
import com.rexense.wholehouse.presenter.RealtimeDataReceiver;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.sdk.APIChannel;
import com.rexense.wholehouse.utility.GsonUtil;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.widget.DialogUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AirConditionerConverterActivity extends BaseActivity {
    private ActivityAirConditionerConverterBinding mViewBinding;

    private static final String DEVICES_NICK_NAMES = "virtual_device_nick_names";

    protected String mIOTId = "";
    protected String mProductKey = "";
    protected String mName = "";
    protected int mOwned = 0;
    private String mVirtualNames = "";

    private Typeface mIconFont;
    private TSLHelper mTSLHelper;

    private final List<AirConditionerConverter.AirConditioner> mList = new ArrayList<>();
    private BaseQuickAdapter<AirConditionerConverter.AirConditioner, BaseViewHolder> mAdapter;

    private MyReceiver mMyReceiver;

    protected boolean updateState(ETSL.propertyEntry propertyEntry) {

        ViseLog.d(GsonUtil.toJson(propertyEntry));
        for (int i = 0; i < 16; i++) {
            String endPoint = String.valueOf(i + 1);

            // 开关
            if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + endPoint) != null &&
                    propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + endPoint).length() > 0) {
                if (DeviceBuffer.containsAirCKey(mIOTId + "_" + endPoint)) {
                    DeviceBuffer.getAirConditioner(mIOTId + "_" + endPoint).setPowerSwitch(propertyEntry.getPropertyValue(CTSL.AIRC_Converter_PowerSwitch_ + endPoint));
                }
            }

            // 当前温度
            if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + endPoint) != null &&
                    propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + endPoint).length() > 0) {
                if (DeviceBuffer.containsAirCKey(mIOTId + "_" + endPoint)) {
                    DeviceBuffer.getAirConditioner(mIOTId + "_" + endPoint).setCurrentTemperature(propertyEntry.getPropertyValue(CTSL.AIRC_Converter_CurrentTemperature_ + endPoint));
                }
            }

            // 目标温度
            if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + endPoint) != null &&
                    propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + endPoint).length() > 0) {
                if (DeviceBuffer.containsAirCKey(mIOTId + "_" + endPoint)) {
                    DeviceBuffer.getAirConditioner(mIOTId + "_" + endPoint).setTargetTemperature(propertyEntry.getPropertyValue(CTSL.AIRC_Converter_TargetTemperature_ + endPoint));
                }
            }

            // 风速
            if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + endPoint) != null &&
                    propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + endPoint).length() > 0) {
                if (DeviceBuffer.containsAirCKey(mIOTId + "_" + endPoint)) {
                    DeviceBuffer.getAirConditioner(mIOTId + "_" + endPoint).setWindSpeed(propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WindSpeed_ + endPoint));
                }
            }

            // 工作模式
            if (propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + endPoint) != null &&
                    propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + endPoint).length() > 0) {
                if (DeviceBuffer.containsAirCKey(mIOTId + "_" + endPoint)) {
                    DeviceBuffer.getAirConditioner(mIOTId + "_" + endPoint).setWorkMode(propertyEntry.getPropertyValue(CTSL.AIRC_Converter_WorkMode_ + endPoint));
                }
            }
        }

        mList.clear();
        mList.addAll(DeviceBuffer.getAllAirConditioner());
        if (mList.size() < 16) {
            AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
            conditioner.setEndPoint("-1");
            mList.add(conditioner);
        }
        mAdapter.notifyDataSetChanged();
        QMUITipDialogUtil.dismiss();
        mViewBinding.devSrl.finishRefresh(true);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAirConditionerConverterBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        // 获取参数
        Intent intent = getIntent();
        mIOTId = intent.getStringExtra("iotId");
        mProductKey = intent.getStringExtra("productKey");
        mName = intent.getStringExtra("name");
        mOwned = intent.getIntExtra("owned", 0);

        mTSLHelper = new TSLHelper(this);

        initStatusBar();
        mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initAdapter();

        initView();
        initData();
    }

    private void initView() {
        mViewBinding.devSrl.setEnableLoadMore(false);
        mViewBinding.devSrl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getExtendedProperty(DEVICES_NICK_NAMES);
            }
        });
    }

    private void initData() {
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
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
        DeviceBuffer.initAirConditioner();
        getExtendedProperty(DEVICES_NICK_NAMES);

        mMyReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UPDATE_VIRTUAL_AIRC_NICKNAME);
        registerReceiver(mMyReceiver, filter);
    }

    // 获取所有虚拟空调的EndPoint
    private void getExtendedProperty(String key) {
        ViseLog.d("mIOTId = " + mIOTId);
        SceneManager.getExtendedProperty(this, mIOTId, key, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(AirConditionerConverterActivity.this, failEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                mViewBinding.devSrl.finishRefresh(false);
                if (errorEntry.code == 6741) {
                    // 6741: 无扩展信息
                    mList.clear();
                    AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
                    conditioner.setEndPoint("-1");
                    mList.add(conditioner);
                    mAdapter.notifyDataSetChanged();
                    QMUITipDialogUtil.dismiss();
                } else {
                    responseError(AirConditionerConverterActivity.this, errorEntry);
                }
            }

            @Override
            public void onProcessData(String result) {
                ViseLog.d("onProcessData = " + result);
                mVirtualNames = result;
                if (mVirtualNames == null || mVirtualNames.length() == 0) {
                    mViewBinding.devSrl.finishRefresh(true);
                    QMUITipDialogUtil.dismiss();
                    mList.clear();
                    AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
                    conditioner.setEndPoint("-1");
                    mList.add(conditioner);
                    mAdapter.notifyDataSetChanged();
                } else {
                    // getVirtualExtended(0);
                    String[] names = mVirtualNames.split(",");
                    for (int i = 0; i < names.length; i++) {
                        if (names[i] != null && names[i].trim().length() > 0) {
                            AirConditionerConverter.AirConditioner airConditioner = new AirConditionerConverter.AirConditioner();
                            airConditioner.setNickname(names[i]);
                            airConditioner.setEndPoint(String.valueOf(i + 1));
                            DeviceBuffer.addAirConditioner(mIOTId + "_" + airConditioner.getEndPoint(), airConditioner);
                        }
                    }
                    getProperty();
                }
            }
        });
    }

    // 主动获取设备属性
    private void getProperty() {
        TSLHelper.getProperty(this, mIOTId, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(AirConditionerConverterActivity.this, failEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(AirConditionerConverterActivity.this, errorEntry);
                mViewBinding.devSrl.finishRefresh(false);
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
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
        mViewBinding.topBar.includeDetailLblTitle.setText(deviceEntry.nickName);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<AirConditionerConverter.AirConditioner, BaseViewHolder>(R.layout.item_grid_airconditioner, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, AirConditionerConverter.AirConditioner airConditioner) {
                String endPoint = airConditioner.getEndPoint();
                if ("-1".equals(endPoint)) {
                    holder.setVisible(R.id.dev_layout, false);
                    holder.setVisible(R.id.add_layout, true);
                    TextView addIc = holder.getView(R.id.add_tv);
                    addIc.setTypeface(mIconFont);
                    RelativeLayout addLayout = holder.getView(R.id.add_layout);
                    addLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddAirConditionerActivity.start(AirConditionerConverterActivity.this,
                                    mIOTId, mProductKey, mVirtualNames, Constant.REQUESTCODE_CALLADDAIRCONDITIONER);
                        }
                    });
                } else {
                    holder.setVisible(R.id.dev_layout, true);
                    holder.setVisible(R.id.add_layout, false);
                    ViseLog.d("test = \n" + GsonUtil.toJson(airConditioner));
                    holder.setText(R.id.dev_name_tv, airConditioner.getNickname());
                    holder.setText(R.id.temperature_tv, airConditioner.getCurrentTemperature() + getString(R.string.centigrade));
                    TextView switchIC = holder.getView(R.id.switch_ic);
                    switchIC.setTypeface(mIconFont);
                    if (String.valueOf(CTSL.STATUS_ON).equals(airConditioner.getPowerSwitch())) {
                        switchIC.setTextColor(ContextCompat.getColor(AirConditionerConverterActivity.this, R.color.appcolor));
                    } else {
                        switchIC.setTextColor(ContextCompat.getColor(AirConditionerConverterActivity.this, R.color.all_8_2));
                    }
                    switchIC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (String.valueOf(CTSL.STATUS_ON).equals(airConditioner.getPowerSwitch())) {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_PowerSwitch_ + airConditioner.getEndPoint()},
                                        new String[]{"" + CTSL.STATUS_OFF});
                            } else {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_PowerSwitch_ + airConditioner.getEndPoint()},
                                        new String[]{"" + CTSL.STATUS_ON});
                            }
                        }
                    });
                    RelativeLayout rootLayout = holder.getView(R.id.root_layout);
                    rootLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AirConditionerForConverterActivity.start(AirConditionerConverterActivity.this, mIOTId, mProductKey,
                                    airConditioner.getEndPoint(), mVirtualNames, Constant.REQUESTCODE_CALLDELAIRCONDITIONER);
                        }
                    });
                }
            }
        };
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (position < mList.size() - 1 && DeviceBuffer.getDeviceInformation(mIOTId).owned == 1) {
                    ViseLog.d("position = " + position);
                    DialogUtils.showConfirmDialog(AirConditionerConverterActivity.this, getString(R.string.dialog_title),
                            String.format(getString(R.string.do_you_want_to_delete_virtual_air), mList.get(position).getNickname()),
                            getString(R.string.dialog_confirm), getString(R.string.dialog_cancel),
                            new DialogUtils.Callback() {
                                @Override
                                public void positive() {
                                    QMUITipDialogUtil.showLoadingDialg(AirConditionerConverterActivity.this, R.string.is_loading);
                                    updateDeviceExtendedProperty(mList.get(position).getEndPoint());
                                }

                                @Override
                                public void negative() {

                                }
                            });
                }
                return false;
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mViewBinding.aircRv.setLayoutManager(layoutManager);
        mViewBinding.aircRv.setAdapter(mAdapter);
    }

    private void updateDeviceExtendedProperty(String endPoint) {
        StringBuilder sb = new StringBuilder();
        String[] indexs = mVirtualNames.split(",");
        for (int i = 0; i < 16; i++) {
            if (String.valueOf(i + 1).equals(endPoint)) {
                indexs[i] = " ";
            }
            if (sb.length() == 0) {
                sb.append(indexs[i]);
            } else {
                sb.append("," + indexs[i]);
            }
        }
        ViseLog.d("sb = " + sb);
        String result = sb.toString().trim().replace(",", "").replace(" ", "");
        ViseLog.d("result = " + result.length());
        if (result.length() > 0) {
            SceneManager.setExtendedProperty(AirConditionerConverterActivity.this, mIOTId, DEVICES_NICK_NAMES, sb.toString(), new APIChannel.Callback() {
                @Override
                public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                    commitFailure(AirConditionerConverterActivity.this, failEntry);
                }

                @Override
                public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                    responseError(AirConditionerConverterActivity.this, errorEntry);
                }

                @Override
                public void onProcessData(String result) {
                    mVirtualNames = sb.toString();
                    DeviceBuffer.removeAirConditioner(mIOTId + "_" + endPoint);
                    getProperty();
                }
            });
        } else {
            SceneManager.delExtendedProperty(this, mIOTId, DEVICES_NICK_NAMES, new APIChannel.Callback() {
                @Override
                public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                    commitFailure(AirConditionerConverterActivity.this, failEntry);
                }

                @Override
                public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                    responseError(AirConditionerConverterActivity.this, errorEntry);
                }

                @Override
                public void onProcessData(String result) {
                    mVirtualNames = "";
                    DeviceBuffer.removeAirConditioner(mIOTId + "_" + endPoint);
                    getProperty();
                }
            });
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        mViewBinding.topBar.includeDetailRl.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mViewBinding.topBar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewBinding.topBar.includeDetailImgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (mProductKey.equals(CTSL.PK_GATEWAY) || mProductKey.equals(CTSL.PK_GATEWAY_RG4100_RY)) {
                    intent = new Intent(AirConditionerConverterActivity.this, MoreGatewayActivity.class);
                } else {
                    intent = new Intent(AirConditionerConverterActivity.this, MoreSubdeviceActivity.class);
                }
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                intent.putExtra("name", mName);
                intent.putExtra("owned", mOwned);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLMOREACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUESTCODE_CALLADDAIRCONDITIONER ||
                requestCode == Constant.REQUESTCODE_CALLDELAIRCONDITIONER) {
            if (resultCode == Constant.RESULTCODE_CALLADDAIRCONDITIONER ||
                    resultCode == Constant.RESULTCODE_CALLDELAIRCONDITIONER) {
                mVirtualNames = data.getStringExtra(DEVICES_NICK_NAMES);
                String[] names = mVirtualNames.split(",");
                DeviceBuffer.initAirConditioner();
                for (int i = 0; i < 16; i++) {
                    if (names[i] != null && names[i].trim().length() > 0) {
                        AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
                        conditioner.setEndPoint(String.valueOf(i + 1));
                        conditioner.setNickname(names[i]);
                        DeviceBuffer.addAirConditioner(mIOTId + "_" + String.valueOf(i + 1), conditioner);
                    }
                }

                // 主动获取设备属性
                mTSLHelper.getProperty(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == Constant.MSG_CALLBACK_GETTSLPROPERTY) {// 处理获取属性回调
                            ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                            ViseLog.d("处理获取属性回调 = " + GsonUtil.toJson(JSONObject.parseObject((String) msg.obj)));
                            JSONObject items = JSON.parseObject((String) msg.obj);
                            if (items != null) {
                                TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                                updateState(propertyEntry);
                            }
                        }
                        return false;
                    }
                }));
            }
        } else if (requestCode == Constant.REQUESTCODE_CALLMOREACTIVITY) {
            if (resultCode == Constant.RESULTCODE_CALLMOREACTIVITYUNBIND) {
                // 执行了解绑则直接退出
                finish();
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
                mVirtualNames = intent.getStringExtra(DEVICES_NICK_NAMES);
                String[] names = mVirtualNames.split(",");
                DeviceBuffer.initAirConditioner();
                for (int i = 0; i < 16; i++) {
                    if (names[i] != null && names[i].trim().length() > 0) {
                        AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
                        conditioner.setEndPoint(String.valueOf(i + 1));
                        conditioner.setNickname(names[i]);
                        DeviceBuffer.addAirConditioner(mIOTId + "_" + String.valueOf(i + 1), conditioner);
                    }
                }

                // 主动获取设备属性
                mTSLHelper.getProperty(mIOTId, mCommitFailureHandler, mResponseErrorHandler, new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == Constant.MSG_CALLBACK_GETTSLPROPERTY) {// 处理获取属性回调
                            ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                            ViseLog.d("处理获取属性回调 = " + GsonUtil.toJson(JSONObject.parseObject((String) msg.obj)));
                            JSONObject items = JSON.parseObject((String) msg.obj);
                            if (items != null) {
                                TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                                updateState(propertyEntry);
                            }
                        }
                        return false;
                    }
                }));
            }
        }
    }
}