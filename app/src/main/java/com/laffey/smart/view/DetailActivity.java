package com.laffey.smart.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.StatusBarUtils;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-22 18:29
 * Description: 设备详细界面
 */
public class DetailActivity extends BaseActivity {
    protected String mIOTId = "";
    protected String mProductKey = "";
    protected String mName = "";
    protected int mOwned = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取参数
        Intent intent = getIntent();
        this.mIOTId = intent.getStringExtra("iotId");
        this.mProductKey = intent.getStringExtra("productKey");
        this.mName = intent.getStringExtra("name");
        this.mOwned = intent.getIntExtra("owned", 0);

        if (mProductKey == null) mProductKey = "";
        // 处理布局文件
        switch (this.mProductKey) {
            case CTSL.PK_DOORSENSOR:
            case CTSL.PK_WATERSENSOR:
            case CTSL.PK_PIRSENSOR:
            case CTSL.PK_GASSENSOR:
            case CTSL.PK_SMOKESENSOR:
            case CTSL.PK_TEMHUMSENSOR:
            case CTSL.PK_REMOTECONTRILBUTTON:
                setContentView(R.layout.activity_detail_sensor);
                break;
            case CTSL.PK_ONEWAYSWITCH:
                setContentView(R.layout.activity_detail_oneswitch_2);
                // setContentView(R.layout.activity_detail_oneswitch);
                break;
            case CTSL.PK_TWOWAYSWITCH:
                setContentView(R.layout.activity_detail_twoswitch_2);
                //setContentView(R.layout.activity_detail_twoswitch);
                break;
            case CTSL.PK_FOURWAYSWITCH:
            case CTSL.PK_FOURWAYSWITCH_2:
                // 四键开关
                setContentView(R.layout.activity_detail_fourswitch_2);
                break;
            case CTSL.TEST_PK_ONEWAYWINDOWCURTAINS:
                // 单路窗帘
                setContentView(R.layout.activity_one_way_window_curtains);
                break;
            case CTSL.TEST_PK_TWOWAYWINDOWCURTAINS: {
                // 双路窗帘
                setContentView(R.layout.activity_two_way_curtains);
                break;
            }
            case CTSL.PK_THREE_KEY_SWITCH: {
                // 三键开关
                setContentView(R.layout.activity_detail_threeswitch);
                break;
            }
            case CTSL.PK_SMART_LOCK_A7:
            case CTSL.PK_SMART_LOCK:
                StatusBarUtils.getStatusBarHeight(this);
                setContentView(R.layout.activity_detail_lock);
                StatusBarUtils.setStatusBar(this, false, false);
                break;
            case CTSL.PK_LIGHT:
            case CTSL.PK_ONE_WAY_DIMMABLE_LIGHT:
                setContentView(R.layout.activity_color_light);
                break;
            case CTSL.PK_SYT_ONE_SCENE_SWITCH:
            case CTSL.PK_ONE_SCENE_SWITCH:
                // if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
                setContentView(R.layout.activity_one_key_scene_2);
                // else setContentView(R.layout.activity_one_key_scene);
                break;
            case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                //if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
                setContentView(R.layout.activity_six_two_scene_2);
                break;
            case CTSL.PK_U_SIX_SCENE_SWITCH:
                setContentView(R.layout.activity_u_six_scene_2);
                break;
            case CTSL.PK_SIX_SCENE_SWITCH_YQSXB:
                setContentView(R.layout.activity_six_scene_3);
                break;
            case CTSL.PK_SIX_SCENE_SWITCH:
            case CTSL.PK_SYT_SIX_SCENE_SWITCH:
                setContentView(R.layout.activity_six_scene_2);
                break;
            case CTSL.PK_ANY_TWO_SCENE_SWITCH:
            case CTSL.PK_TWO_SCENE_SWITCH:
            case CTSL.PK_SYT_TWO_SCENE_SWITCH:
                //if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
                setContentView(R.layout.activity_two_key_scene_2);
                //else setContentView(R.layout.activity_two_key_scene);
                break;
            case CTSL.PK_THREE_SCENE_SWITCH:
            case CTSL.PK_SYT_THREE_SCENE_SWITCH:
                //if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
                setContentView(R.layout.activity_three_key_scene_2);
                //else setContentView(R.layout.activity_three_key_scene);
                break;
            case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
                setContentView(R.layout.activity_four_key_scene_3);
                break;
            case CTSL.PK_FOUR_SCENE_SWITCH:
            case CTSL.PK_SYT_FOUR_SCENE_SWITCH:
                //if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
                setContentView(R.layout.activity_four_key_scene_2);
                //else setContentView(R.layout.activity_four_key_scene);
                break;
            case CTSL.TEST_PK_FULL_SCREEN_SWITCH: {
                // 全面屏
                setContentView(R.layout.activity_full_screen_switch);
                break;
            }
            case CTSL.PK_MULTI_THREE_IN_ONE: {
                // 三合一
                setContentView(R.layout.activity_multi_dev_for_three);
                break;
            }
            case CTSL.PK_MULTI_AC_AND_FH: {
                // 空调+地暖
                setContentView(R.layout.activity_multi_dev_for_ac_and_fh);
                break;
            }
            case CTSL.PK_MULTI_AC_AND_FA: {
                // 空调+新风
                setContentView(R.layout.activity_multi_dev_for_ac_and_af);
                break;
            }
            case CTSL.PK_MULTI_FH_AND_FA: {
                // 地暖+新风
                setContentView(R.layout.activity_multi_dev_for_fh_and_af);
                break;
            }
            case CTSL.PK_AIRCOMDITION_FOUR:// 空调四管制
            case CTSL.PK_AIRCOMDITION_TWO: {
                // 空调二管制
                setContentView(R.layout.activity_air_conditioner_for_full_screen);
                break;
            }
            case CTSL.PK_FLOORHEATING001: {
                // 地暖
                setContentView(R.layout.activity_floor_heating_for_full_screen);
                return;
            }
            case CTSL.PK_FAU: {
                // 新风
                setContentView(R.layout.activity_new_air_for_full_screen);
                return;
            }
            default:
                //todo 换回gateway
                StatusBarUtils.getStatusBarHeight(this);
                //setContentView(R.layout.activity_detail_lock);
                setContentView(R.layout.activity_detail_gateway);
                StatusBarUtils.setStatusBar(this, false, false);
                break;
        }

        // 标题处理
        TextView title = (TextView) findViewById(R.id.includeDetailLblTitle);
        title.setText(this.mName);
        Log.i("lzm", "this.mName" + this.mName);

        // 回退处理
        ImageView imgBack = (ImageView) findViewById(R.id.includeDetailImgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 更多处理
        ImageView more = (ImageView) findViewById(R.id.includeDetailImgMore);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (mProductKey.equals(CTSL.PK_GATEWAY) || mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                    intent = new Intent(DetailActivity.this, MoreGatewayActivity.class);
                } else {
                    intent = new Intent(DetailActivity.this, MoreSubdeviceActivity.class);
                }
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                intent.putExtra("name", mName);
                intent.putExtra("owned", mOwned);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLMOREACTIVITY);
            }
        });

        // 主动获取设备属性
        new TSLHelper(this).getProperty(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIProperyDataHandler);

        // 添加实时数据属性回调处理器
        ViseLog.d("this.toString() = " + this.toString());
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", this.mRealtimeDataPropertyHandler);
    }

    @Override
    protected void onResume() {
        // 刷新标题数据
        super.onResume();
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if (deviceEntry != null) {
            TextView title = (TextView) findViewById(R.id.includeDetailLblTitle);
            if (title == null) {
                title = (TextView) findViewById(R.id.includeTitleLblTitle);
            }
            title.setText(deviceEntry.nickName);
            mName = deviceEntry.nickName;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUESTCODE_CALLMOREACTIVITY) {
            if (resultCode == Constant.RESULTCODE_CALLMOREACTIVITYUNBIND) {
                // 执行了解绑则直接退出
                finish();
            }
        }
    }

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 如果是主动获取状态则补全iotId与productKey
        if (propertyEntry.iotId == null || propertyEntry.iotId.length() == 0) {
            propertyEntry.iotId = mIOTId;
            propertyEntry.productKey = mProductKey;
        }

        if (!propertyEntry.iotId.equals(mIOTId) || !propertyEntry.productKey.equals(mProductKey)) {
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        // 删除长连接实时数据属性处理器
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
        super.onDestroy();
    }

    // API属性数据处理器
    private Handler mAPIProperyDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTY:
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    // ViseLog.d("处理获取属性回调 = \n" + GsonUtil.toJson(propertyEntry));
                    JSONObject items = JSON.parseObject((String) msg.obj);
                    if (items != null) {
                        TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                        updateState(propertyEntry);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 实时数据属性处理器
    private Handler mRealtimeDataPropertyHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                    // 处理属性通知回调
                    // ViseLog.d("实时 = " + (String) msg.obj);
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                    // ViseLog.d("实时处理 = \n" + GsonUtil.toJson(propertyEntry));
                    updateState(propertyEntry);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}