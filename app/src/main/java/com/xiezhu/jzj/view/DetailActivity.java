package com.xiezhu.jzj.view;

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
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.presenter.DeviceBuffer;
import com.xiezhu.jzj.presenter.RealtimeDataParser;
import com.xiezhu.jzj.presenter.RealtimeDataReceiver;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.utility.StatusBarUtils;

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
                setContentView(R.layout.activity_detail_oneswitch);
                break;
            case CTSL.PK_TWOWAYSWITCH:
                setContentView(R.layout.activity_detail_twoswitch);
                break;
            case CTSL.TEST_PK_ONEWAYWINDOWCURTAINS:
                setContentView(R.layout.activity_one_way_window_curtains);
                break;
            case CTSL.PK_SMART_LOCK:
                StatusBarUtils.getStatusBarHeight(this);
                setContentView(R.layout.activity_detail_lock);
                StatusBarUtils.setStatusBar(this, false, false);
                break;
            default:
                //todo 换回gateway
                StatusBarUtils.getStatusBarHeight(this);
                //setContentView(R.layout.activity_detail_lock);
                setContentView(R.layout.activity_detail_gateway);
                StatusBarUtils.setStatusBar(this, false, false);
                break;
        }

        // 标题处理
        TextView title = (TextView)findViewById(R.id.includeDetailLblTitle);
        title.setText(this.mName);
        Log.i("lzm", "this.mName"+ this.mName);

        // 回退处理
        ImageView imgBack = (ImageView)findViewById(R.id.includeDetailImgBack);
        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 更多处理
        ImageView more = (ImageView)findViewById(R.id.includeDetailImgMore);
        more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;
                if(mProductKey.equals(CTSL.PK_GATEWAY)||mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                    intent = new Intent(DetailActivity.this, MoreGatewayActivity.class);
                } else {
                    intent = new Intent(DetailActivity.this, MoreSubdeviceActivity.class);
                }
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                intent.putExtra("name", mName);
                Log.i("lzm", "this.mName2"+ mName);
                intent.putExtra("owned", mOwned);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLMOREACTIVITY);
            }
        });

        // 主动获取设备属性
        new TSLHelper(this).getProperty(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIProperyDataHandler);

        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", this.mRealtimeDataPropertyHandler);
    }

    @Override
    protected void onResume() {
        // 刷新标题数据
        super.onResume();
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if(deviceEntry != null) {
            TextView title = (TextView)findViewById(R.id.includeDetailLblTitle);
            if(title == null) {
                title = (TextView)findViewById(R.id.includeTitleLblTitle);
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
        if(propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 如果是主动获取状态则补全iotId与productKey
        if(propertyEntry.iotId == null || propertyEntry.iotId.length() == 0) {
            propertyEntry.iotId = mIOTId;
            propertyEntry.productKey = mProductKey;
        }

        if(!propertyEntry.iotId.equals(mIOTId) || !propertyEntry.productKey.equals(mProductKey)) {
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
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTY:
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    JSONObject items = JSON.parseObject((String)msg.obj);
                    if(items != null) {
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
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String)msg.obj);
                    updateState(propertyEntry);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}