package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.event.CEvent;
import com.rexense.imoco.event.EEvent;
import com.rexense.imoco.presenter.ActivityRouter;
import com.rexense.imoco.presenter.AptDeviceList;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.CodeMapper;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.OTAHelper;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.model.EUser;
import com.rexense.imoco.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 网关详细界面
 */
public class DetailGatewayActivity extends DetailActivity {
    @BindView(R.id.fake_statusbar_view)
    protected View mFakeStatusbarView;
    @BindView(R.id.detailGatewayLblCount)
    protected TextView mLblCount;
    @BindView(R.id.detailGatewayLblArmMode)
    protected TextView mLblAarmMode;
    @BindView(R.id.detailGatewayLblArmModeClick)
    protected TextView mLblAarmModeClick;
    @BindView(R.id.detailGatewayImgSecurity)
    protected ImageView mImgSecurity;
    @BindView(R.id.detailGatewayImgSecurityRound)
    protected ImageView mImgSecurityRound;

    private AptDeviceList mAptDeviceList = null;
    private List<EDevice.deviceEntry> mDeviceList;
    private int mStatus;
    private TSLHelper mTSLHelper;
    private final int PAGE_SIZE = 50;
    private int mAarmMode = 0;

    // 开始获取网关子设备列表
    private void startGetGatewaySubdeive() {
        mAptDeviceList.clearData();
        mDeviceList.clear();
        new UserCenter(this).getGatewaySubdeviceList(mIOTId, 1, PAGE_SIZE,
                mCommitFailureHandler, mResponseErrorHandler, processAPIDataHandler);
    }

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode) != null && propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode).length() > 0) {
            mAarmMode = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode));
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(DetailGatewayActivity.this, mProductKey, CTSL.GW_P_ArmMode, propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode));
            if (mapperEntry != null && mapperEntry.name != null) {
                TextView lblAarmMode = (TextView) findViewById(R.id.detailGatewayLblArmMode);
                lblAarmMode.setText(mapperEntry.value);
            }
            mImgSecurity.setImageResource(ImageProvider.genDeviceStateIcon(CTSL.PK_GATEWAY, CTSL.GW_P_ArmMode, propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode)));

            LinearLayout rl = (LinearLayout) findViewById(R.id.detailGatewayLlMain);
            if (propertyEntry.getPropertyValue(CTSL.GW_P_ArmMode).equals(CTSL.GW_P_ArmMode_deploy)) {
                int topicColor1 = getResources().getColor(R.color.topic_color1);
                rl.setBackgroundColor(topicColor1);
                mLblAarmModeClick.setText(getString(R.string.detailgateway_armmode_cancel_click));
                mFakeStatusbarView.setBackgroundColor(topicColor1);
                if (Build.VERSION.SDK_INT >= 23)
                    getWindow().setStatusBarColor(topicColor1);
            } else {
                int topicColor2 = getResources().getColor(R.color.topic_color2);
                rl.setBackgroundColor(topicColor2);
                mLblAarmModeClick.setText(getString(R.string.detailgateway_armmode_deploy_click));
                if (Build.VERSION.SDK_INT >= 23)
                    getWindow().setStatusBarColor(topicColor2);
                mFakeStatusbarView.setBackgroundColor(topicColor2);
            }
        }
        return true;
    }

    // 处理API数据处理器
    private final Handler processAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST == msg.what) {
                EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                if (list != null && list.data != null) {
                    for (EUser.deviceEntry e : list.data) {
                        EDevice.deviceEntry entry = new EDevice.deviceEntry();
                        entry.iotId = e.iotId;
                        entry.nickName = e.nickName;
                        entry.productKey = e.productKey;
                        entry.status = e.status;
                        entry.owned = DeviceBuffer.getDeviceOwned(e.iotId);
                        entry.image = e.image;
                        mDeviceList.add(entry);
                    }
                    if (list.data.size() >= list.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        new UserCenter(DetailGatewayActivity.this).getGatewaySubdeviceList(mIOTId, list.pageNo + 1, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, processAPIDataHandler);
                    } else {
                        // 数据获取完则加载显示
                        ListView subdeviceList = (ListView) findViewById(R.id.detailGatewayLstSubdevice);
                        mAptDeviceList.setData(mDeviceList);
                        subdeviceList.setAdapter(mAptDeviceList);
                        subdeviceList.setOnItemClickListener(deviceListOnItemClickListener);
                        onlineCount();
                    }
                }
            }
            if (msg.what == Constant.MSG_CALLBACK_GETOTAFIRMWAREINFO) {
                // 处理获取OTA固件信息
                JSONObject dataJson = JSONObject.parseObject((String) msg.obj);
                String currentVersion = dataJson.getString("currentVersion");
                String theNewVersion = dataJson.getString("version");
                Log.i("lzm", "currentVersion = " + currentVersion + "theNewVersion = " + theNewVersion);
                if (!currentVersion.equals(theNewVersion)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailGatewayActivity.this);
                    builder.setIcon(R.drawable.dialog_quest);
                    builder.setTitle(R.string.upgrade_firmware);
                    builder.setMessage(R.string.dialog_ota);
                    builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(mActivity, UpgradeFirmwareActivity.class);
                            intent.putExtra("iotId", mIOTId);
                            intent.putExtra("productKey", mProductKey);
                            intent.putExtra("currentVersion", currentVersion);
                            intent.putExtra("theNewVersion", theNewVersion);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    builder.create().show();
                }
            }
            return false;
        }
    });


    // 实时数据处理器
    private final Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNSTATUSNOTIFY:
                    // 处理连接状态通知
                    ERealtimeData.deviceConnectionStatusEntry entry = RealtimeDataParser.processConnectStatus((String) msg.obj);
                    if (entry == null || entry.iotId == null) {
                        return false;
                    }

                    if (entry.iotId.equals(mIOTId)) {
                        // 网关状态处理
                        mStatus = entry.status;
                    } else {
                        // 子设备状态处理
                        boolean isFound = false;
                        if (mAptDeviceList != null && mDeviceList != null && mDeviceList != null) {
                            for (int i = 0; i < mDeviceList.size(); i++) {
                                if (mDeviceList.get(i).iotId.equals(entry.iotId)) {
                                    mDeviceList.get(i).status = entry.status;
                                    isFound = true;
                                    break;
                                }
                            }
                            // 刷新数据
                            mAptDeviceList.notifyDataSetChanged();
                            onlineCount();
                        }
                        if (!isFound) {
                            // 开始获取网关子设备列表以刷新数据
                            startGetGatewaySubdeive();
                        }
                    }
                    break;
//                case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
//                    // 开始获取网关子设备列表以刷新数据
//                    startGetGatewaySubdeive();
//                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 在线统计
    private void onlineCount() {
        TextView lblCount = (TextView) findViewById(R.id.detailGatewayLblCount);
        if (mDeviceList != null && mDeviceList.size() > 0) {
            int online = 0;
            for (EDevice.deviceEntry e : mDeviceList) {
                if (e.status == Constant.CONNECTION_STATUS_ONLINE) {
                    online++;
                }
            }
            lblCount.setText(String.format(getString(R.string.detailgateway_count), mDeviceList.size(), online));
        } else {
            lblCount.setText(String.format(getString(R.string.detailgateway_count), 0, 0));
        }
        mLblCount.setVisibility(View.VISIBLE);
    }

    // 安防模式点击监听器
    private final OnClickListener armModeClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAarmMode == 0) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_ArmMode}, new String[]{"1"});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_ArmMode}, new String[]{"0"});
            }
        }
    };

    // 设备列表点击监听器
    private final AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                if (mDeviceList.get(position) != null && mDeviceList.get(position).productKey != null) {
                    ActivityRouter.toDetail(DetailGatewayActivity.this, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                            mDeviceList.get(position).status, mDeviceList.get(position).nickName, mDeviceList.get(position).owned);
                } else {
                    ToastUtils.showLongToast(DetailGatewayActivity.this, R.string.pls_try_again_later);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理
        ButterKnife.bind(this);

        mLblCount.setVisibility(View.INVISIBLE);
        mTSLHelper = new TSLHelper(this);
        mDeviceList = new ArrayList<EDevice.deviceEntry>();
        mAptDeviceList = new AptDeviceList(this);

        RelativeLayout armView = (RelativeLayout) findViewById(R.id.mArmViw);
        ImageView gateway4100 = (ImageView) findViewById(R.id.mGateway4100);
        if (mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
            gateway4100.setVisibility(View.VISIBLE);
            armView.setVisibility(View.GONE);
        }

        // 获取网关状态
        mStatus = getIntent().getIntExtra("status", Constant.CONNECTION_STATUS_OFFLINE);

        // 安防模式设置处理
        mImgSecurity.setOnClickListener(armModeClick);
        mImgSecurityRound.setOnClickListener(armModeClick);
        mLblAarmMode.setOnClickListener(armModeClick);
        mLblAarmModeClick.setOnClickListener(armModeClick);

        // 共享网关不能添加子设备
        if (mOwned == 0) {
            RelativeLayout rlAdd = (RelativeLayout) findViewById(R.id.detailGatewayRlAdd);
            rlAdd.setVisibility(View.GONE);
        }

        // 添加子设备处理
        OnClickListener onAddClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailGatewayActivity.this, ChoiceProductActivity.class);
                intent.putExtra("gatewayIOTId", mIOTId);
                intent.putExtra("gatewayStatus", mStatus);
                startActivity(intent);
            }
        };
        ImageView imgAdd = (ImageView) findViewById(R.id.detailGatewayImgAdd);
        TextView lblAdd = (TextView) findViewById(R.id.detailGatewayLblAdd);
        imgAdd.setOnClickListener(onAddClickListener);
        lblAdd.setOnClickListener(onAddClickListener);

        // 添加实时数据连接状态回调处理器
        RealtimeDataReceiver.addStatusCallbackHandler("DetailGatewayStatusCallback", mRealtimeDataHandler);
        // 添加实时数据设备加网回调处理器
        RealtimeDataReceiver.addJoinCallbackHandler("DetailGatewayJoinCallback", mRealtimeDataHandler);
        // 注册事件总线
        EventBus.getDefault().register(this);

        // 开始获取网关子设备列表
        startGetGatewaySubdeive();
        // 非共享设备才能去获取版本号信息
        if (mOwned > 0) {
            OTAHelper.getFirmwareInformation(mIOTId, mCommitFailureHandler, mResponseErrorHandler, processAPIDataHandler);
        }
        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            //getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    protected void onResume() {
        // 刷新数据
        super.onResume();
        if (mDeviceList != null && mDeviceList.size() > 0) {
            EDevice.deviceEntry bufferEntry, displayEntry;
            for (int i = mDeviceList.size() - 1; i >= 0; i--) {
                displayEntry = mDeviceList.get(i);
                bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                if (bufferEntry != null) {
                    // 更新备注名称
                    displayEntry.nickName = bufferEntry.nickName;
                } else {
                    // 删除不存在的数据
                    mDeviceList.remove(i);
                }
            }
            mAptDeviceList.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        // 删除实时数据属性回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayJoinCallback");
        super.onDestroy();
    }

    // 订阅刷新数据事件
    @Subscribe
    public void onRefreshRoomData(EEvent eventEntry) {
        // 处理刷新设备数量数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_NUMBER_DATA)) {
            onlineCount();
        }

        // 处理刷新设备列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_LIST_DATA)) {
            startGetGatewaySubdeive();
        }
    }
}