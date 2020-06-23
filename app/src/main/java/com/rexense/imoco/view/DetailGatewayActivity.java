package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.presenter.ActivityRouter;
import com.rexense.imoco.presenter.AptDeviceList;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.CodeMapper;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.model.EUser;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 网关详细界面
 */
public class DetailGatewayActivity extends DetailActivity {
    private int mAarmMode = 0;
    private TextView mLblCount, mLblAarmMode, mLblAarmModeClick;
    private ImageView mImgSecurity, mImgSecurityRound;
    private AptDeviceList mAptDeviceList = null;
    private List<EDevice.deviceEntry> mDeviceList;
    private int mStatus;
    private TSLHelper mTSLHelper;
    private final int mPageSize = 50;

    // 开始获取网关子设备列表
    private void startGetGatewaySubdeive() {
        this.mAptDeviceList.clearData();
        this.mDeviceList.clear();
        new UserCenter(this).getGatewaySubdeviceList(this.mIOTId, 1, this.mPageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.processAPIDataHandler);
    }

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if(!super.updateState(propertyEntry))
        {
            return false;
        }

        if(propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode) != null && propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode).length() > 0) {
            this.mAarmMode = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode));
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(DetailGatewayActivity.this, mProductKey, CTSL.GW_P_ArmMode, propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode));
            if(mapperEntry != null && mapperEntry.name != null) {
                TextView lblAarmMode = (TextView)findViewById(R.id.detailGatewayLblArmMode);
                lblAarmMode.setText(mapperEntry.value);
            }
            this.mImgSecurity.setImageResource(ImageProvider.genDeviceStateIcon(CTSL.PK_GATEWAY, CTSL.GW_P_ArmMode, propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode)));

            LinearLayout rl = (LinearLayout)findViewById(R.id.detailGatewayLlMain);
            if(propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode).equals(CTSL.GW_P_ArmMode_deploy)) {
                rl.setBackgroundColor(Color.rgb(0xFC, 0x7C, 0x23));
                mLblAarmModeClick.setText(getString(R.string.detailgateway_armmode_cancel_click));
            } else {
                rl.setBackgroundColor(Color.rgb(0xF1, 0x97, 0x25));
                mLblAarmModeClick.setText(getString(R.string.detailgateway_armmode_deploy_click));
            }
        }
        return true;
    }

    // 处理API数据处理器
    private Handler processAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            if(Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST == msg.what) {
                EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String)msg.obj);
                if(list != null && list.data != null) {
                    for(EUser.deviceEntry e: list.data) {
                        EDevice.deviceEntry entry = new EDevice.deviceEntry();
                        entry.iotId = e.iotId;
                        entry.nickName = e.nickName;
                        entry.productKey = e.productKey;
                        entry.status = e.status;
                        mDeviceList.add(entry);
                    }
                    if(list.data.size() >= list.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        new UserCenter(DetailGatewayActivity.this).getGatewaySubdeviceList(mIOTId, list.pageNo + 1, mPageSize, mCommitFailureHandler, mResponseErrorHandler, processAPIDataHandler);
                    } else {
                        // 数据获取完则加载显示
                        ListView subdeviceList = (ListView)findViewById(R.id.detailGatewayLstSubdevice);
                        mAptDeviceList.setData(mDeviceList);
                        subdeviceList.setAdapter(mAptDeviceList);
                        subdeviceList.setOnItemClickListener(deviceListOnItemClickListener);
                        onlineCount();
                    }
                }
            }
            return false;
        }
    });

    // 实时数据处理器
    private Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNSTATUSNOTIFY:
                    // 处理连接状态通知
                    ERealtimeData.deviceConnectionStatusEntry entry = RealtimeDataParser.processConnectStatus((String)msg.obj);
                    if(entry == null) {
                        return false;
                    }

                    if(entry.iotId.equals(mIOTId)) {
                        // 网关状态处理
                        mStatus = entry.status;
                    } else {
                        // 子设备状态处理
                        boolean isFound = false;
                        if(mAptDeviceList != null && mDeviceList != null && mDeviceList != null) {
                            for(int i = 0; i < mDeviceList.size(); i++) {
                                if(mDeviceList.get(i).iotId.equals(entry.iotId)) {
                                    mDeviceList.get(i).status = entry.status;
                                    isFound = true;
                                    break;
                                }
                            }
                            // 刷新数据
                            mAptDeviceList.notifyDataSetChanged();
                            onlineCount();
                        }
                        if(!isFound) {
                            // 开始获取网关子设备列表以刷新数据
                            startGetGatewaySubdeive();
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
                    // 开始获取网关子设备列表以刷新数据
                    startGetGatewaySubdeive();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 在线统计
    private void onlineCount() {
        TextView lblCount = (TextView)findViewById(R.id.detailGatewayLblCount);
        if(this.mDeviceList != null && this.mDeviceList.size() > 0) {
            int online = 0;
            for(EDevice.deviceEntry e: this.mDeviceList) {
                if(e.status == Constant.CONNECTION_STATUS_ONLINE) {
                    online++;
                }
            }
            lblCount.setText(String.format(getString(R.string.detailgateway_count), this.mDeviceList.size(), online));
        } else {
            lblCount.setText(String.format(getString(R.string.detailgateway_count), 0, 0));
        }
        this.mLblCount.setVisibility(View.VISIBLE);
    }

    // 安防模式点击监听器
    private OnClickListener armModeClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mAarmMode == 0) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_ArmMode}, new String[]{"1"});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_ArmMode}, new String[]{"0"});
            }
        }
    };

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mDeviceList != null && position < mDeviceList.size()) {
                ActivityRouter.toDetail(DetailGatewayActivity.this, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey, mDeviceList.get(position).status, mDeviceList.get(position).nickName);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理

        this.mLblCount = (TextView)findViewById(R.id.detailGatewayLblCount);
        this.mLblCount.setVisibility(View.INVISIBLE);
        this.mTSLHelper = new TSLHelper(this);
        this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
        this.mAptDeviceList = new AptDeviceList(this);

        // 获取网关状态
        this.mStatus = getIntent().getIntExtra("status", Constant.CONNECTION_STATUS_OFFLINE);

        // 安防模式设置处理
        this.mImgSecurity = (ImageView)findViewById(R.id.detailGatewayImgSecurity);
        this.mImgSecurityRound = (ImageView)findViewById(R.id.detailGatewayImgSecurityRound);
        this.mLblAarmMode = (TextView) findViewById(R.id.detailGatewayLblArmMode);
        this.mLblAarmModeClick = (TextView) findViewById(R.id.detailGatewayLblArmModeClick);
        this.mImgSecurity.setOnClickListener(this.armModeClick);
        this.mImgSecurityRound.setOnClickListener(this.armModeClick);
        this.mLblAarmMode.setOnClickListener(this.armModeClick);
        this.mLblAarmModeClick.setOnClickListener(this.armModeClick);

        // 添加子设备处理
        OnClickListener onAddClickListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailGatewayActivity.this, ChoiceProductActivity.class);
                intent.putExtra("gatewayIOTId", mIOTId);
                intent.putExtra("gatewayStatus", mStatus);
                startActivity(intent);
            }
        };
        ImageView imgAdd = (ImageView)findViewById(R.id.detailGatewayImgAdd);
        TextView lblAdd = (TextView)findViewById(R.id.detailGatewayLblAdd);
        imgAdd.setOnClickListener(onAddClickListener);
        lblAdd.setOnClickListener(onAddClickListener);

        // 添加实时数据连接状态回调处理器
        RealtimeDataReceiver.addStatusCallbackHandler("DetailGatewayStatusCallback", this.mRealtimeDataHandler);
        // 添加实时数据设备加网回调处理器
        RealtimeDataReceiver.addJoinCallbackHandler("DetailGatewayJoinCallback", this.mRealtimeDataHandler);

        // 开始获取网关子设备列表
        startGetGatewaySubdeive();
    }

    @Override
    protected void onResume() {
        // 刷新数据
        super.onResume();
        if(this.mDeviceList != null && this.mDeviceList.size() > 0) {
            EDevice.deviceEntry bufferEntry, displayEntry;
            for(int i = this.mDeviceList.size() - 1; i >= 0; i--)
            {
                displayEntry = this.mDeviceList.get(i);
                bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                if(bufferEntry != null) {
                    // 更新备注名称
                    displayEntry.nickName = bufferEntry.nickName;
                } else {
                    // 删除不存在的数据
                    this.mDeviceList.remove(i);
                }
            }
            this.mAptDeviceList.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        // 删除实时数据属性回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayJoinCallback");
        super.onDestroy();
    }
}