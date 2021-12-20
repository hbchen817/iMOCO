package com.laffey.smart.view;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.presenter.ActivityRouter;
import com.laffey.smart.presenter.AptDeviceList;
import com.laffey.smart.presenter.AptSubGwList;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.OTAHelper;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ERealtimeData;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.EUser;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 网关详细界面
 */
public class DetailGatewayActivity extends DetailActivity implements OnClickListener {
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
    @BindView(R.id.dev_rg)
    protected RadioGroup mDevRG;
    @BindView(R.id.sub_dev_rb)
    protected RadioButton mSubDevRB;
    @BindView(R.id.sub_gw_rb)
    protected RadioButton mSubGwRB;
    @BindView(R.id.detailGatewayLstSubdevice)
    protected ListView mSubDevLV;
    @BindView(R.id.gw_dev_lv)
    protected ListView mSubGwLV;

    private AptDeviceList mAptDeviceList = null;
    private final List<EDevice.deviceEntry> mDeviceList = new ArrayList<>();
    private AptSubGwList mGwDeviceList = null;
    private final List<EDevice.subGwEntry> mGwList = new ArrayList<>();
    private int mStatus;
    private TSLHelper mTSLHelper;
    private final int PAGE_SIZE = 50;
    private int mAarmMode = 0;
    private String mGwMac;
    private boolean mIsShowSubGwList;

    private static final String SHOW_SUB_GW_LIST = "showSubGwList";

    public static void start(Activity activity, String iotId, String productKey, int status, String name, int owned, boolean showSubGwList) {
        Intent intent = new Intent(activity, DetailGatewayActivity.class);
        intent.putExtra("iotId", iotId);
        intent.putExtra("productKey", productKey);
        intent.putExtra("status", status);
        intent.putExtra("name", name);
        intent.putExtra("owned", owned);
        intent.putExtra(SHOW_SUB_GW_LIST, showSubGwList);
        activity.startActivity(intent);
    }

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
                        mAptDeviceList.setData(mDeviceList);
                        mSubDevLV.setAdapter(mAptDeviceList);
                        mSubDevLV.setOnItemClickListener(deviceListOnItemClickListener);
                        onlineCount();
                        // 子网关列表
                        getSubGwList();
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
        int total = 0;
        int onLine = 0;
        if (mSubDevRB.isChecked()) {
            if (mDeviceList != null && mDeviceList.size() > 0) {
                total = mDeviceList.size();
                for (EDevice.deviceEntry e : mDeviceList) {
                    if (e.status == Constant.CONNECTION_STATUS_ONLINE) {
                        onLine++;
                    }
                }
            }
            mLblCount.setText(String.format(getString(R.string.detailgateway_count), total, onLine));
        } else if (mSubGwRB.isChecked()) {
            if (mGwList != null && mGwList.size() > 0) {
                total = mGwList.size();
                for (EDevice.subGwEntry e : mGwList) {
                    // 子网关状态0-未激活，1-已激活，2-所有
                    if ("1".equals(e.getState())) {
                        onLine++;
                    }
                }
            }
            mLblCount.setText(String.format(getString(R.string.detailgateway_count_2), total, onLine));
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

    // 子网关列表点击监听器
    private final AdapterView.OnItemClickListener mSubGwListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SubGwInfoActivity.start(DetailGatewayActivity.this, mIOTId,
                    GsonUtil.toJson(mGwList.get(position)), Constant.REQUESTCODE_CALLSUBGWINFOACTIVITY);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理
        ButterKnife.bind(this);

        mLblCount.setVisibility(View.INVISIBLE);
        mTSLHelper = new TSLHelper(this);
        mAptDeviceList = new AptDeviceList(this);
        mGwDeviceList = new AptSubGwList(this);
        mGwDeviceList.setData(mGwList);
        mSubGwLV.setAdapter(mGwDeviceList);
        mSubGwLV.setOnItemClickListener(mSubGwListOnItemClickListener);

        RelativeLayout armView = findViewById(R.id.mArmViw);
        ImageView gateway4100 = findViewById(R.id.mGateway4100);
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
            RelativeLayout rlAdd = findViewById(R.id.detailGatewayRlAdd);
            rlAdd.setVisibility(View.GONE);
        }

        ImageView imgAdd = findViewById(R.id.detailGatewayImgAdd);
        TextView lblAdd = findViewById(R.id.detailGatewayLblAdd);
        imgAdd.setOnClickListener(mOnAddClickListener);
        lblAdd.setOnClickListener(mOnAddClickListener);

        RelativeLayout addDevRL = findViewById(R.id.add_dev_rl);
        addDevRL.setOnClickListener(this);
        RelativeLayout addSceneRL = findViewById(R.id.scene_add_layout);
        if (mOwned == 0) {
            // 分享者
            addDevRL.setVisibility(View.GONE);
            addSceneRL.setVisibility(View.GONE);
        } else {
            // 拥有者
            addDevRL.setVisibility(View.VISIBLE);
            addSceneRL.setVisibility(View.VISIBLE);
        }

        ImageView sceneAdd = findViewById(R.id.scene_add_img);
        sceneAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalSceneListActivity.start(DetailGatewayActivity.this, mIOTId);
            }
        });

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

        mDevRG.setOnCheckedChangeListener(mDevCheckedChangeListener);
        mGwMac = DeviceBuffer.getDeviceMac(mIOTId);
        mDevRG.setVisibility(DeviceBuffer.getDeviceOwned(mIOTId) == 1 ? View.VISIBLE : View.GONE);

        mIsShowSubGwList = getIntent().getBooleanExtra(SHOW_SUB_GW_LIST, false);
        if (mIsShowSubGwList) mSubGwRB.setChecked(true);

        RealtimeDataReceiver.addEventCallbackHandler("DetailGatewayCallback", new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                    // 处理触发手动场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    ViseLog.d("jsonObject = \n" + GsonUtil.toJson(jsonObject));
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("SubGatewayStatusNotification".equals(identifier)) {
                        String status = value.getString("Status");
                        String mac = value.getString("Mac");
                        // status  1: 在线  3: 离线
                        synchronized (DetailGatewayActivity.class) {
                            for (EDevice.subGwEntry entry : mGwList) {
                                if (mac.equals(entry.getMac())) {
                                    entry.setStatus(status);
                                    mGwDeviceList.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }));
    }

    // 添加子设备处理
    private final OnClickListener mOnAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DetailGatewayActivity.this, ChoiceProductActivity.class);
            intent.putExtra("gatewayIOTId", mIOTId);
            intent.putExtra("gatewayStatus", mStatus);
            startActivity(intent);
        }
    };

    // 子设备、子网关列表切换
    private final RadioGroup.OnCheckedChangeListener mDevCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.sub_dev_rb) {
                // 子设备列表
                mSubDevRB.setTextColor(ContextCompat.getColor(DetailGatewayActivity.this, R.color.topic_color1));
                mSubGwRB.setTextColor(ContextCompat.getColor(DetailGatewayActivity.this, R.color.black));
                mSubDevLV.setVisibility(View.VISIBLE);
                mSubGwLV.setVisibility(View.GONE);
            } else if (checkedId == R.id.sub_gw_rb) {
                // 子网关列表
                mSubDevRB.setTextColor(ContextCompat.getColor(DetailGatewayActivity.this, R.color.black));
                mSubGwRB.setTextColor(ContextCompat.getColor(DetailGatewayActivity.this, R.color.topic_color1));
                mSubDevLV.setVisibility(View.GONE);
                mSubGwLV.setVisibility(View.VISIBLE);
            }
            onlineCount();
        }
    };

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
        if (DeviceBuffer.getDeviceOwned(mIOTId) == 1) {
            // 拥有者
            for (EDevice.subGwEntry entry : mGwList) {
                EDevice.subGwEntry subGwEntry = DeviceBuffer.getSubGw(entry.getMac());
                if (subGwEntry != null)
                    entry.setNickname(subGwEntry.getNickname());
            }
            mGwDeviceList.notifyDataSetChanged();
        }
        onlineCount();
    }

    @Override
    protected void onDestroy() {
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        // 删除实时数据属性回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayJoinCallback");
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayCallback");
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_dev_rl) {
            // 新增设备
            if (DeviceBuffer.getDeviceOwned(mIOTId) == 1) {
                // 拥有者
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
                builder.addItem(getString(R.string.detailgateway_add));
                builder.addItem(getString(R.string.add_sub_gateway));
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0: {
                                // 添加子设备
                                Intent intent = new Intent(DetailGatewayActivity.this, ChoiceProductActivity.class);
                                intent.putExtra("gatewayIOTId", mIOTId);
                                intent.putExtra("gatewayStatus", mStatus);
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                // 添加子网关
                                // 已经获取权限
                                requestPermission();
                                break;
                            }
                        }
                    }
                });
                builder.build().show();
            } else {
                // 分享者
                Intent intent = new Intent(this, ChoiceProductActivity.class);
                intent.putExtra("gatewayIOTId", mIOTId);
                intent.putExtra("gatewayStatus", mStatus);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            String qrKey = data.getStringExtra("result");
            ViseLog.d("qrKey = " + qrKey);
            AddSubGwActivity.start(this, mIOTId, qrKey, Constant.REQUESTCODE_CALLADDSUBGWACTIVITY);
        } else if (requestCode == Constant.REQUESTCODE_CALLSUBGWINFOACTIVITY &&
                resultCode == Constant.RESULTCODE_CALLSUBGWINFOACTIVITY) {
            getSubGwList();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 重新获取子网关列表
        mSubGwRB.setChecked(true);
        // 子网关状态0-未激活，1-已激活，2-所有
        getSubGwList();
        mIsShowSubGwList = getIntent().getBooleanExtra(SHOW_SUB_GW_LIST, false);
    }

    private void getSubGwList() {
        if (DeviceBuffer.getDeviceOwned(mIOTId) == 1)
            UserCenter.getSubGwList(this, mGwMac, "2",
                    new UserCenter.Callback() {
                        @Override
                        public void onNext(JSONObject response) {
                            // 获取网关下的子网关列表
                            int code = response.getInteger("code");
                            ViseLog.d("子网关列表 = \n" + GsonUtil.toJson(response));
                            if (code == 200) {
                                JSONArray subGwList = response.getJSONArray("subGwList");
                                mGwList.clear();
                                DeviceBuffer.initSubGw();
                                if (subGwList != null) {
                                    for (int i = 0; i < subGwList.size(); i++) {
                                        JSONObject object = subGwList.getJSONObject(i);

                                        EDevice.subGwEntry entry = new EDevice.subGwEntry();
                                        entry.setMac(object.getString("mac"));
                                        entry.setNickname(object.getString("nickname"));
                                        entry.setPosition(object.getString("position"));
                                        entry.setCreateTime(object.getString("createTime"));
                                        entry.setActivateTime(object.getString("activateTime"));
                                        entry.setState(object.getString("state"));
                                        entry.setImage(DeviceBuffer.getDeviceInformation(mIOTId).image);
                                        mGwList.add(entry);
                                        DeviceBuffer.addSubGw(entry.getMac(), entry);
                                    }
                                }
                                mGwDeviceList.notifyDataSetChanged();
                                for (EDevice.subGwEntry entry : mGwList) {
                                    SceneManager.querySubGatewayStatusService(DetailGatewayActivity.this, mIOTId,
                                            entry.getMac(), null);
                                }
                                onlineCount();
                            } else {
                                RetrofitUtil.showErrorMsg(DetailGatewayActivity.this, response);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // 获取网关下的子网关列表失败
                            ViseLog.e(e);
                            ToastUtils.showLongToast(DetailGatewayActivity.this, e.getMessage());
                        }
                    });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            boolean hasRequstCamera = SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, SpUtils.PS_REQUEST_CAMERA_PERMISSION, false);
            // 未有权限
            // 第一次请求权限 false
            // 第一次请求权限拒绝，但未选择“不再提醒” true
            // 第一次请求权限拒绝，并选择“不再提醒” false
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || !hasRequstCamera) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

                SpUtils.putBooleanValue(this, SpUtils.SP_APP_INFO, SpUtils.PS_REQUEST_CAMERA_PERMISSION, true);
            } else {
                ToastUtils.showLongToast(mActivity, getString(R.string.camera_denied_and_dont_ask_msg));
            }

            /*ViseLog.d("flag = " + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));*/
        } else {
            // 已经获取权限
            // showConfirmDialog(getString(R.string.dialog_title), getString(R.string.scan_bar_code_on_back_of_gw));
            Intent intent = new Intent(mActivity, ScanActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private void showConfirmDialog(String title, String content) {
        DialogUtils.showConfirmDialog(this, title, content, getString(R.string.dialog_confirm),
                getString(R.string.dialog_cancel), new DialogUtils.Callback() {
                    @Override
                    public void positive() {
                        Intent intent = new Intent(mActivity, ScanActivity.class);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void negative() {

                    }
                });
    }
}