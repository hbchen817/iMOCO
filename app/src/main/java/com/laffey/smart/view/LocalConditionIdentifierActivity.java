package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityIdentifierListBinding;
import com.laffey.smart.model.ECondition;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.model.ERetrofit;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalConditionIdentifierActivity extends BaseActivity {
    private ActivityIdentifierListBinding mViewBinding;

    private static final String NICK_NAME = "nick_name";
    private static final String DEV_IOT = "dev_iot";
    private static final String DEV_NAME = "dev_name";
    private static final String PRODUCT_KEY = "product_key";

    private String mNickName = "";
    private String mDevIot = "";
    private String mDevName = "";
    private String mProductKey = "";

    private SceneManager mSceneManager;
    //private CallbackHandler mHandler;

    private List<ECondition> mList;
    private BaseQuickAdapter<ECondition, BaseViewHolder> mAdapter;
    private Typeface mIconfont;

    public static void start(Context context, String nickName, String devIot, String devName, String productKey) {
        Intent intent = new Intent(context, LocalConditionIdentifierActivity.class);
        intent.putExtra(NICK_NAME, nickName);
        intent.putExtra(DEV_IOT, devIot);
        intent.putExtra(DEV_NAME, devName);
        intent.putExtra(PRODUCT_KEY, productKey);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityIdentifierListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        initStatusBar();
        initRecyclerView();
        initData();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        mNickName = getIntent().getStringExtra("nick_name");
        mDevIot = getIntent().getStringExtra("dev_iot");
        mDevName = getIntent().getStringExtra("dev_name");
        mProductKey = getIntent().getStringExtra("product_key");

        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(mNickName);
    }

    private void initRecyclerView() {
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<ECondition, BaseViewHolder>(R.layout.item_identifier, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ECondition item) {
                TextView goIcon = holder.getView(R.id.go_iv);
                goIcon.setTypeface(mIconfont);

                int i = mList.indexOf(item);
                holder.setText(R.id.name_tv, item.getKeyNickName())
                        .setVisible(R.id.divider, i != 0);
                holder.getView(R.id.root_layout).setBackground(null);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mList.get(position).setTarget("LocalConditionValueActivity");
                EventBus.getDefault().postSticky(mList.get(position));
                Intent intent = new Intent(LocalConditionIdentifierActivity.this, LocalConditionValueActivity.class);
                startActivity(intent);
            }
        });
        mViewBinding.recyclerRl.setEnableLoadMore(false);
        mViewBinding.recyclerRl.setEnableRefresh(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.identifierRecycler.setLayoutManager(layoutManager);
        mViewBinding.identifierRecycler.setAdapter(mAdapter);
    }

    private void initData() {
        mSceneManager = new SceneManager(this);
        //mHandler = new CallbackHandler(this);
        //QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        //mSceneManager.queryIdentifierListForCA(mDevIot, 0, mCommitFailureHandler, mResponseErrorHandler, mHandler);

        //getDataConversionRules("chengxunfei", mProductKey);
        if (CTSL.PK_ONE_SCENE_SWITCH.equals(mProductKey) ||
                CTSL.PK_TWO_SCENE_SWITCH.equals(mProductKey) ||
                CTSL.PK_THREE_SCENE_SWITCH.equals(mProductKey) ||
                CTSL.PK_FOUR_SCENE_SWITCH.equals(mProductKey) ||
                CTSL.PK_SIX_SCENE_SWITCH.equals(mProductKey)) {
            // 场景开关
            ECondition eCondition = initEventECondition(mDevIot, getString(R.string.trigger_buttons_2), DeviceBuffer.getDeviceInformation(mDevIot).deviceName, "17",
                    "03A1", "KeyValue", "==");

            mList.add(eCondition);
        } else if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(mProductKey)) {
            // 一路窗帘
            String keyName = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.WC_CurtainConrtol);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.curtains_control);
            }
            ECondition eCondition = initStateECondition(mDevIot, keyName, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "CurtainState", "==");
            mList.add(eCondition);
        } else if (CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(mProductKey)) {
            // 二路窗帘
            String keyName1 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWC_CurtainConrtol);
            if (keyName1 == null || keyName1.length() == 0) {
                keyName1 = getString(R.string.one_curtains);
            }
            String keyName2 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWC_InnerCurtainOperation);
            if (keyName2 == null || keyName2.length() == 0) {
                keyName2 = getString(R.string.two_curtains);
            }
            mList.add(initStateECondition(mDevIot, keyName1, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "CurtainState", "=="));
            mList.add(initStateECondition(mDevIot, keyName2, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "2", "CurtainState", "=="));
        } else if (CTSL.PK_ONEWAYSWITCH.equals(mProductKey)) {
            // 一键面板
            String keyName = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.OWS_P_PowerSwitch_1);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.power_switch);
            }
            mList.add(initStateECondition(mDevIot, keyName, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));
        } else if (CTSL.PK_TWOWAYSWITCH.equals(mProductKey)) {
            // 二键面板
            String keyName1 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWS_P_PowerSwitch_1);
            if (keyName1 == null || keyName1.length() == 0) {
                keyName1 = getString(R.string.one_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName1, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));

            String keyName2 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWS_P_PowerSwitch_2);
            if (keyName2 == null || keyName2.length() == 0) {
                keyName2 = getString(R.string.two_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName2, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "2", "State", "=="));
        } else if (CTSL.PK_THREE_KEY_SWITCH.equals(mProductKey)) {
            // 三键面板
            String keyName1 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWS_P3_PowerSwitch_1);
            if (keyName1 == null || keyName1.length() == 0) {
                keyName1 = getString(R.string.one_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName1, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));

            String keyName2 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWS_P3_PowerSwitch_2);
            if (keyName2 == null || keyName2.length() == 0) {
                keyName2 = getString(R.string.two_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName2, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "2", "State", "=="));

            String keyName3 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.TWS_P3_PowerSwitch_3);
            if (keyName3 == null || keyName3.length() == 0) {
                keyName3 = getString(R.string.three_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName3, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "3", "State", "=="));
        } else if (CTSL.PK_FOURWAYSWITCH_2.equals(mProductKey)) {
            // 四键面板
            String keyName1 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.FWS_P_PowerSwitch_1);
            if (keyName1 == null || keyName1.length() == 0) {
                keyName1 = getString(R.string.one_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName1, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));

            String keyName2 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.FWS_P_PowerSwitch_2);
            if (keyName2 == null || keyName2.length() == 0) {
                keyName2 = getString(R.string.two_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName2, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "2", "State", "=="));

            String keyName3 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.FWS_P_PowerSwitch_3);
            if (keyName3 == null || keyName3.length() == 0) {
                keyName3 = getString(R.string.three_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName3, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "3", "State", "=="));

            String keyName4 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.FWS_P_PowerSwitch_4);
            if (keyName4 == null || keyName4.length() == 0) {
                keyName4 = getString(R.string.four_way_powerswitch);
            }
            mList.add(initStateECondition(mDevIot, keyName4, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "4", "State", "=="));
        } else if (CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(mProductKey)) {
            // 六键四开二场景开关
            String keyName1 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1);
            if (keyName1 == null || keyName1.length() == 0) {
                keyName1 = getString(R.string.powerswitch_1);
            }
            mList.add(initStateECondition(mDevIot, keyName1, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));

            String keyName2 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2);
            if (keyName2 == null || keyName2.length() == 0) {
                keyName2 = getString(R.string.powerswitch_2);
            }
            mList.add(initStateECondition(mDevIot, keyName2, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "2", "State", "=="));

            String keyName3 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3);
            if (keyName3 == null || keyName3.length() == 0) {
                keyName3 = getString(R.string.powerswitch_3);
            }
            mList.add(initStateECondition(mDevIot, keyName3, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "3", "State", "=="));

            String keyName4 = DeviceBuffer.getExtendedInfo(mDevIot).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4);
            if (keyName4 == null || keyName4.length() == 0) {
                keyName4 = getString(R.string.powerswitch_4);
            }
            mList.add(initStateECondition(mDevIot, keyName4, DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "4", "State", "=="));

            mList.add(initEventECondition(mDevIot, getString(R.string.trigger_buttons_2), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "17", "03A1", "KeyValue", "=="));
        } else if (CTSL.PK_OUTLET.equals(mProductKey)) {
            // 插座
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(mProductKey) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(mProductKey) ||
                CTSL.PK_VRV_AC.equals(mProductKey)) {
            // 空调二管制、四管制、VRV温控器
            // 电源开关
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "WorkMode", "=="));
            // 当前温度
            mList.add(initStateECondition(mDevIot, getString(R.string.current_temperature), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Temperature", "=="));
        } else if (CTSL.PK_FLOORHEATING001.equals(mProductKey)) {
            // 电地暖
            // 电源开关
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "WorkMode", "=="));
            // 当前温度
            mList.add(initStateECondition(mDevIot, getString(R.string.current_temperature), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Temperature", "=="));
        } else if (CTSL.PK_FAU.equals(mProductKey)) {
            // 新风
            // 电源开关
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "FanMode", "=="));
            // 当前温度
            mList.add(initStateECondition(mDevIot, getString(R.string.current_temperature), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Temperature", "=="));
        } else if (CTSL.PK_LIGHT.equals(mProductKey)) {
            // 调光调色
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));
        } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(mProductKey)) {
            // 单调光
            mList.add(initStateECondition(mDevIot, getString(R.string.power_switch), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "State", "=="));
        } else if (CTSL.PK_PIRSENSOR.equals(mProductKey)) {
            // 人体红外感应器
            mList.add(initStateECondition(mDevIot, getString(R.string.infrared_detection_status), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Alarm", "=="));
        } else if (CTSL.PK_GASSENSOR.equals(mProductKey)) {
            // 燃气感应器
            mList.add(initStateECondition(mDevIot, getString(R.string.gas_detection_status), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Alarm", "=="));
        } else if (CTSL.PK_TEMHUMSENSOR.equals(mProductKey)) {
            // 温湿度传感器
            // 温度
            mList.add(initStateECondition(mDevIot, getString(R.string.detailsensor_temperature), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Temperature", "=="));
            // 湿度
            mList.add(initStateECondition(mDevIot, getString(R.string.detailsensor_humidity), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Humidity", "=="));
        } else if (CTSL.PK_SMOKESENSOR.equals(mProductKey)) {
            // 烟雾传感器
            mList.add(initStateECondition(mDevIot, getString(R.string.smoke_detection_status), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Alarm", "=="));
        } else if (CTSL.PK_WATERSENSOR.equals(mProductKey)) {
            // 水浸传感器
            mList.add(initStateECondition(mDevIot, getString(R.string.water_detection_status), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Alarm", "=="));
        } else if (CTSL.PK_DOORSENSOR.equals(mProductKey)) {
            // 门磁传感器
            mList.add(initStateECondition(mDevIot, getString(R.string.door_detection_status), DeviceBuffer.getDeviceInformation(mDevIot).deviceName,
                    "1", "Alarm", "=="));
        }
        mAdapter.notifyDataSetChanged();
    }

    private ECondition initEventECondition(String iotId, String keyNickName, String devName, String endId, String eventType, String paramName, String compareType) {
        ECondition eCondition = new ECondition();
        eCondition.setIotId(iotId);
        eCondition.setKeyNickName(keyNickName);

        ItemScene.ConditionParameter parameter = new ItemScene.ConditionParameter();
        parameter.setDeviceId(devName);
        parameter.setEndpointId(endId);
        parameter.setEventType(eventType);
        parameter.setParameterName(paramName);
        parameter.setCompareType(compareType);

        ItemScene.Condition condition = new ItemScene.Condition("Event", parameter);
        eCondition.setCondition(condition);
        return eCondition;
    }

    private ECondition initStateECondition(String iotId, String keyNickName, String devName, String endId, String stateName, String compareType) {
        ECondition eCondition = new ECondition();
        eCondition.setIotId(iotId);
        eCondition.setKeyNickName(keyNickName);

        ItemScene.ConditionParameter parameter = new ItemScene.ConditionParameter();
        parameter.setDeviceId(devName);
        parameter.setEndpointId(endId);
        parameter.setName(stateName);
        parameter.setCompareType(compareType);

        ItemScene.Condition condition = new ItemScene.Condition("State", parameter);
        eCondition.setCondition(condition);
        return eCondition;
    }

    private void getDataConversionRules(String token, String pk) {
        JSONObject obj = new JSONObject();
        obj.put("apiVer", "1.0");
        JSONObject params = new JSONObject();
        params.put("productKey", pk);
        obj.put("params", params);

        ERetrofit.getInstance().getService()
                .getDataConversionRules(token, ERetrofit.convertToBody(obj.toJSONString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code == 200) {

                        } else {
                            String msg = response.getString("message");
                            QMUITipDialogUtil.showFailDialog(LocalConditionIdentifierActivity.this, msg);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.e(e);
                        QMUITipDialogUtil.showFailDialog(LocalConditionIdentifierActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static class Identifier implements Parcelable {
        private String iotId;
        private String productKey;
        private String nickName;
        private String type;//State:状态，Event:事件
        private String identifierName;
        private String key;
        private String endId;
        private String eventType;

        public Identifier(String productKey, String nickName, String type, String iotId, String identifierName, String key, String endId) {
            this.iotId = iotId;
            this.productKey = productKey;
            this.nickName = nickName;
            this.type = type;
            this.identifierName = identifierName;
            this.key = key;
            this.endId = endId;
        }

        public Identifier(String productKey, String nickName, String type, String iotId, String identifierName, String key, String endId, String eventType) {
            this.iotId = iotId;
            this.productKey = productKey;
            this.nickName = nickName;
            this.type = type;
            this.identifierName = identifierName;
            this.key = key;
            this.endId = endId;
            this.eventType = eventType;
        }

        protected Identifier(Parcel in) {
            iotId = in.readString();
            productKey = in.readString();
            nickName = in.readString();
            type = in.readString();
            identifierName = in.readString();
            key = in.readString();
            endId = in.readString();
            eventType = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(iotId);
            dest.writeString(productKey);
            dest.writeString(nickName);
            dest.writeString(type);
            dest.writeString(identifierName);
            dest.writeString(key);
            dest.writeString(endId);
            dest.writeString(eventType);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Identifier> CREATOR = new Creator<Identifier>() {
            @Override
            public Identifier createFromParcel(Parcel in) {
                return new Identifier(in);
            }

            @Override
            public Identifier[] newArray(int size) {
                return new Identifier[size];
            }
        };

        public String getIotId() {
            return iotId;
        }

        public void setIotId(String iotId) {
            this.iotId = iotId;
        }

        public String getProductKey() {
            return productKey;
        }

        public void setProductKey(String productKey) {
            this.productKey = productKey;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIdentifierName() {
            return identifierName;
        }

        public void setIdentifierName(String identifierName) {
            this.identifierName = identifierName;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getEndId() {
            return endId;
        }

        public void setEndId(String endId) {
            this.endId = endId;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Object obj) {

    }

    /*private static class CallbackHandler extends Handler {
        private final WeakReference<LocalIdentifierListActivity> weakRf;

        public CallbackHandler(LocalIdentifierListActivity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            LocalIdentifierListActivity activity = weakRf.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_IDENTIFIER_LIST) {
                activity.mList.clear();
                String result = (String) msg.obj;
                if (result.substring(0, 1).equals("[")) {
                    result = "{\"data\":" + result + "}";
                }
                ViseLog.d(new Gson().toJson(JSON.parseObject(result)));
                JSONObject o = JSON.parseObject(result);
                JSONArray a = o.getJSONArray("data");
                for (int i = 0; i < a.size(); i++) {
                    JSONObject o1 = a.getJSONObject(i);
                    IdentifierItemForCA item = new Gson().fromJson(o1.toJSONString(), IdentifierItemForCA.class);

                    if (item.getType() == 1) {
                        // 属性
                        CaConditionEntry.Property property = new CaConditionEntry.Property();
                        property.setProductKey(activity.mProductKey);
                        property.setDeviceName(activity.mDevName);
                        String identifier = o1.getString("identifier");
                        property.setPropertyName(identifier);

                        JSONObject object = DeviceBuffer.getExtendedInfo(activity.mDevIot);
                        if (object != null) {
                            String name = object.getString(identifier);
                            if (name != null) {
                                item.setName(name);
                            }
                        }

                        item.setObject(property);
                    } else if (item.getType() == 3) {
                        // 事件
                        CaConditionEntry.Event event = new CaConditionEntry.Event();
                        event.setProductKey(activity.mProductKey);
                        event.setDeviceName(activity.mDevName);
                        event.setEventCode(o1.getString("identifier"));
                        item.setObject(event);

                        if (Constant.KEY_NICK_NAME_PK.contains(activity.mProductKey)) {
                            item.setName(activity.getString(R.string.trigger_buttons_2));
                        }
                    }
                    item.setIotId(activity.mDevIot);
                    item.setNickName(activity.mNickName);
                    activity.mList.add(item);
                }
                activity.mAdapter.notifyDataSetChanged();
                QMUITipDialogUtil.dismiss();
                activity.mViewBinding.recyclerRl.finishLoadMore(true);
                activity.mViewBinding.recyclerRl.finishRefresh(true);
                if (a == null || a.size() == 0) {
                    ToastUtils.showShortToast(activity, R.string.this_device_has_no_scene_condition);
                }
            }
        }
    }*/
}