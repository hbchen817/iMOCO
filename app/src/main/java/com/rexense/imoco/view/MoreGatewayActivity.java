package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.event.CEvent;
import com.rexense.imoco.event.EEvent;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.event.RefreshFirmwareVersion;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.model.EUser;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.CodeMapper;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.OTAHelper;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 网关更多界面
 */
public class MoreGatewayActivity extends BaseActivity {
    protected String mIOTId = "";
    protected String mProductKey = "";
    protected String mName = "";
    protected String mRoomName = "";
    protected String mBindTime = "";
    private int mSetType = 0;
    private RelativeLayout mWheelPickerLayout;
    private WheelPicker mWheelPicker;
    private TextView mAlarmBellId, mBellVolume, mBellMusicId, mAlarmVolume;
    private TextView mAlarmBellIdValue, mBellVolumeValue, mBellMusicIdValue, mAlarmVolumeValue;
    private TextView mLblTitle, mWheelPickerValue, mLblNewNickName, mLblRoomName;
    private LinearLayout mFunctionSettingsLayout;
    private View upgradeView;
    private boolean hasNewerVersion;
    private String currentVersion;
    private String theNewVersion;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private TSLHelper mTSLHelper;
    private TextView currentVersionTv;
    private int mOwned;
    private HashMap<String, String> mDeviceMap = new HashMap<>();
    private SceneManager mSceneManager;
    private String mSceneType;

    // 更新状态
    protected void updateStatus(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return;
        }

        // 如果是主动获取状态则补全iotId与productKey
        if (propertyEntry.iotId == null || propertyEntry.iotId.length() == 0) {
            propertyEntry.iotId = mIOTId;
            propertyEntry.productKey = mProductKey;
        }

        if (!propertyEntry.iotId.equals(mIOTId) || !propertyEntry.productKey.equals(mProductKey)) {
            return;
        }

        if (propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID) != null && propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_AlarmSoundID, propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mAlarmBellId.setText(mapperEntry.name + ":");
                mAlarmBellIdValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mBellVolume.setText(mapperEntry.name + ":");
                mBellVolumeValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundID, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mBellMusicId.setText(mapperEntry.name + ":");
                mBellMusicIdValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_AlarmSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mAlarmVolume.setText(mapperEntry.name + ":");
                mAlarmVolumeValue.setText(mapperEntry.value);
            }
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTY:
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    JSONObject items = JSON.parseObject((String) msg.obj);
                    if (items != null) {
                        TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                        updateStatus(propertyEntry);
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表回调
                    mRoomListEntry = CloudDataParser.processHomeRoomList((String) msg.obj);
                    break;
                case Constant.MSG_CALLBACK_SETDEVICENICKNAME:
                    // 处理设置设备昵称回调
                    mLblNewNickName.setText(mNewNickName);
                    mLblTitle.setText(mNewNickName);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIOTId, mNewNickName);
                    break;
                case Constant.MSG_CALLBACK_UPDATEDEVICEROOM:
                    // 处理更新设备所属房间回调
                    mLblRoomName.setText(mNewRoomName);
                    // 更新设备缓存房间数据
                    DeviceBuffer.updateDeviceRoom(mIOTId, mNewRoomId, mNewRoomName);
                    RefreshData.refreshRoomListData();
                    RefreshData.refreshDeviceListRoomData(mIOTId);
                    break;
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    // 处理获取物的基本信息回调
                    ETSL.thingBaseInforEntry thingBaseInforEntry = CloudDataParser.processThingBaseInformation((String) msg.obj);
                    currentVersionTv.setText(thingBaseInforEntry.firmwareVersion);
                    break;
                case Constant.MSG_CALLBACK_UNBINDEVICE:
                    // 处理设备解除绑定回调
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 设置系统参数解绑网关以触发数据刷新
                    SystemParameter.getInstance().setIsRefreshDeviceData(true);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    Dialog.confirm(MoreGatewayActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                    break;
                case Constant.MSG_CALLBACK_GETOTAFIRMWAREINFO:
                    // 处理获取OTA固件信息
                    JSONObject dataJson = JSONObject.parseObject((String) msg.obj);
                    currentVersion = dataJson.getString("currentVersion");
                    theNewVersion = dataJson.getString("version");
                    hasNewerVersion = !currentVersion.equals(theNewVersion);
                    currentVersionTv.setText(currentVersion);
                    break;
                case Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST:
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    if (list != null && list.data != null) {
                        for (EUser.deviceEntry e : list.data) {
                            switch (e.productKey) {
                                case CTSL.PK_LIGHT:
                                case CTSL.PK_ONE_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                case CTSL.PK_ANY_TWO_SCENE_SWITCH:
                                case CTSL.PK_TWO_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                case CTSL.PK_THREE_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
                                case CTSL.PK_FOUR_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                case CTSL.PK_SIX_SCENE_SWITCH_YQSXB:
                                case CTSL.PK_U_SIX_SCENE_SWITCH:
                                case CTSL.PK_SIX_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getGatewaySubdeviceList(mIOTId, list.pageNo + 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则加载显示
                            if (mDeviceMap.size() > 0) {
                                mSceneType = CScene.TYPE_AUTOMATIC;
                                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            }
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            String iotID = item.description.replace("mode == CA,", "");
                            if (mDeviceMap.containsKey(iotID)) {
                                mSceneManager.deleteScene(item.id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            }
                        }
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, sceneList.pageNo + 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 如果自动场景获取结束则开始获取手动场景
                            if (mSceneType.equals(CScene.TYPE_AUTOMATIC)) {
                                mSceneType = CScene.TYPE_MANUAL;
                                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                // 数据获取完则设置场景列表数据
                                mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 实时数据处理器
    private Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                    // 处理属性通知回调
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                    updateStatus(propertyEntry);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 设置滑轮选择器(type取值,1报警铃音,2门铃音量,3门铃音乐,4报警音量, 5房间列表)
    private void setWheelPicker(int type, String initValue) {
        mSetType = type;
        mWheelPickerValue.setText(initValue + "");
        // 确认处理
        TextView ok = (TextView) findViewById(R.id.oneItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                // 设置网关属性
                String value = mWheelPickerValue.getText().toString();
                if (mSetType == 1) {
                    if (value.equals("")) {
                        value = "1";
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_AlarmSoundID}, new String[]{value});
                } else if (mSetType == 2) {
                    if (value.equals("")) {
                        value = "60";
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_DoorBellSoundVolume}, new String[]{value});
                } else if (mSetType == 3) {
                    if (value.equals("")) {
                        value = "1";
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_DoorBellSoundID}, new String[]{value});
                } else if (mSetType == 4) {
                    if (value.equals("")) {
                        value = "90";
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.GW_P_AlarmSoundVolume}, new String[]{value});
                } else if (mSetType == 5) {
                    // 设置设备所属房间
                    mHomeSpaceManager.updateRoomDevice(SystemParameter.getInstance().getHomeId(), mNewRoomId, mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        // 取消处理
        TextView cancel = (TextView) findViewById(R.id.oneItemWheelPickerLblCancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
            }
        });

        // 生成选择项数量
        int count = 0;
        if (type == 1 || type == 3) {
            count = 10;
        } else if (type == 2 || type == 4) {
            count = 100;
        } else if (type == 5) {
            count = mRoomListEntry == null || mRoomListEntry.data == null ? 0 : mRoomListEntry.data.size();
        }

        // 生成选择项内容
        if (count > 0) {
            List<String> data = new ArrayList<String>();
            int initIndex = 0;
            if (type <= 4) {
                for (int i = 1; i <= count; i++) {
                    if (String.format("%d", i).equals(initValue)) {
                        initIndex = i - 1;
                    }
                    data.add(String.format("%d", i));
                }
            } else {
                int n = 0;
                for (EHomeSpace.roomEntry room : mRoomListEntry.data) {
                    data.add(room.name);
                    if (room.name.equals(initValue)) {
                        initIndex = n;
                    }
                    n++;
                }
            }
            // 滑轮滚动处理
            mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    mWheelPickerValue.setText(data.toString());
                    if (mSetType == 5) {
                        mNewRoomId = mRoomListEntry.data.get(position).roomId;
                        mNewRoomName = data.toString();
                    }
                }
            });

            // 如果房间没有初始值则默认选择第一项
            if (mSetType == 5 && initValue.equals("")) {
                initIndex = 0;
                mNewRoomId = mRoomListEntry.data.get(0).roomId;
                mNewRoomName = data.get(0);
                mWheelPickerValue.setText(mNewRoomName);
            }

            // 加载两次数据是为了正确初始选中位置
            for (int i = 0; i < 2; i++) {
                mWheelPicker.setData(data);
                mWheelPicker.setSelectedItemPosition(initIndex);
            }
            mWheelPicker.invalidate();
            this.mWheelPickerLayout.setVisibility(View.VISIBLE);
        }
    }

    // 显示设备名称修改对话框
    private void showDeviceNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.moregateway_editname));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(this.mLblNewNickName.getText().toString());
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_320);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEt.getText().toString().trim();
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    mNewNickName = nameEt.getText().toString();
                    // 设置设备昵称
                    mUserCenter.setDeviceNickName(mIOTId, mNewNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // 订阅刷新数据事件
    @Subscribe
    public void onRefreshRoomData(EEvent eventEntry) {
        // 处理刷新网关固件数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_GATEWAY_FIRMWARE_DATA)) {
            // 获取设备基本信息
            new TSLHelper(this).getBaseInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

            // 非共享设备才能去获取版本号信息
            if (mOwned > 0) {
                OTAHelper.getFirmwareInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_gateway);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        this.mIOTId = intent.getStringExtra("iotId");
        this.mProductKey = intent.getStringExtra("productKey");
        this.mName = intent.getStringExtra("name");

        // 分享设备不允许修改房间,故不显示
        this.mOwned = intent.getIntExtra("owned", 0);
        if (this.mOwned == 0) {
            RelativeLayout rlRoom = (RelativeLayout) findViewById(R.id.moreGatewayRLRoom);
            rlRoom.setVisibility(View.GONE);
        }

        this.mTSLHelper = new TSLHelper(this);

        currentVersionTv = (TextView) findViewById(R.id.moreGatewayLblVersion);
        initStatusBar();

        mFunctionSettingsLayout = (LinearLayout) findViewById(R.id.function_settings);
        if (CTSL.PK_GATEWAY_RG4100.equals(mProductKey)) {
            mFunctionSettingsLayout.setVisibility(View.GONE);
        }

        // 回退处理
        ImageView imgBack = (ImageView) findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView lblDeviceId = (TextView) findViewById(R.id.moreGatewayLblId);
        mLblTitle = (TextView) findViewById(R.id.includeTitleLblTitle);
        mLblTitle.setText(mName);

        TextView lblUnbind = (TextView) findViewById(R.id.moreLblUnbind);
        ImageView imgUnbind = (ImageView) findViewById(R.id.moreImgUnbind);

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if (deviceEntry != null) {
            mRoomName = deviceEntry.roomName;
            mBindTime = deviceEntry.bindTime;
        } else {
            ToastUtils.showShortToast(this, R.string.pls_try_again_later);
            lblUnbind.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(MoreGatewayActivity.this, R.string.pls_try_again_later);
                }
            });
            imgUnbind.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(MoreGatewayActivity.this, R.string.pls_try_again_later);
                }
            });
            return;
        }

        lblDeviceId.setText(deviceEntry.deviceName);

        // 分享设备不能进行升级,故不显示
        if (mOwned == 0) {
            RelativeLayout upgrade = (RelativeLayout) findViewById(R.id.upgrade_view);
            upgrade.setVisibility(View.GONE);
        }
        upgradeView = findViewById(R.id.upgrade_view);
        upgradeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasNewerVersion) {
                    Intent intent1 = new Intent(mActivity, UpgradeFirmwareActivity.class);
                    intent1.putExtra("iotId", mIOTId);
                    intent1.putExtra("productKey", mProductKey);
                    intent1.putExtra("currentVersion", currentVersion);
                    intent1.putExtra("theNewVersion", theNewVersion);
                    startActivity(intent1);
                } else {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.current_version_is_new));
                }
            }
        });

        mAlarmBellId = (TextView) findViewById(R.id.moreGatewayLblAlarmBellId);
        mBellVolume = (TextView) findViewById(R.id.moreGatewayLblBellVolume);
        mBellMusicId = (TextView) findViewById(R.id.moreGatewayLblBellMusicId);
        mAlarmVolume = (TextView) findViewById(R.id.moreGatewayLblAlarmVolume);

        mAlarmBellIdValue = (TextView) findViewById(R.id.moreGatewayLblAlarmBellIdValue);
        mBellVolumeValue = (TextView) findViewById(R.id.moreGatewayLblBellVolumeValue);
        mBellMusicIdValue = (TextView) findViewById(R.id.moreGatewayLblBellMusicIdValue);
        mAlarmVolumeValue = (TextView) findViewById(R.id.moreGatewayLblAlarmVolumeValue);

        mWheelPickerLayout = (RelativeLayout) findViewById(R.id.oneItemWheelPickerRLPicker);
        mWheelPickerLayout.setVisibility(View.GONE);
        mWheelPickerValue = (TextView) findViewById(R.id.oneItemWheelPickerLblValue);
        mWheelPickerLayout = (RelativeLayout) findViewById(R.id.oneItemWheelPickerRLPicker);
        mWheelPicker = (WheelPicker) findViewById(R.id.oneItemWheelPickerWPPicker);
        mLblRoomName = (TextView) findViewById(R.id.moreGatewayLblRoom);
        mLblRoomName.setText(mRoomName);
        TextView bindTime = (TextView) findViewById(R.id.moreGatewayLblBindTime);
        ImageView setAlarmBellId = (ImageView) findViewById(R.id.moreGatewayImgAlarmBellId);
        ImageView setBellVolume = (ImageView) findViewById(R.id.moreGatewayImgBellVolume);
        ImageView setBellMusicId = (ImageView) findViewById(R.id.moreGatewayImgBellMusicId);
        ImageView setAlarmVolume = (ImageView) findViewById(R.id.moreGatewayImgAlarmVolume);
        mLblNewNickName = (TextView) findViewById(R.id.moreGatewayLblName);
        ImageView inputNickName = (ImageView) findViewById(R.id.moreGatewayImgName);

        bindTime.setText(this.mBindTime);

        // 报警铃音设置事件处理
        OnClickListener setAlarmBellIdListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, mAlarmBellIdValue.getText().toString());
            }
        };
        setAlarmBellId.setOnClickListener(setAlarmBellIdListener);

        // 门铃音量设置事件处理
        OnClickListener setBellVolumeListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String volume = mBellVolumeValue.getText().toString().substring(0, mBellVolumeValue.getText().toString().length() - 1);
                setWheelPicker(2, volume);
            }
        };
        setBellVolume.setOnClickListener(setBellVolumeListener);

        // 门铃音乐设置事件处理
        OnClickListener setBellMusicIdListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(3, mBellMusicIdValue.getText().toString());
            }
        };
        setBellMusicId.setOnClickListener(setBellMusicIdListener);

        // 报警音量设置事件处理
        OnClickListener setAlarmVolumeListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String volume = mAlarmVolumeValue.getText().toString().substring(0, mAlarmVolumeValue.getText().toString().length() - 1);
                setWheelPicker(4, volume);
            }
        };
        setAlarmVolume.setOnClickListener(setAlarmVolumeListener);

        // 修改设备名称事件处理
        mLblNewNickName.setText(mName);
        inputNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceNameDialogEdit();
            }
        });

        // 选择所属房间处理
        ImageView selectRoom = (ImageView) findViewById(R.id.moreGatewayImgRoom);
        OnClickListener selectRoomListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(5, mLblRoomName.getText().toString());
            }
        };
        selectRoom.setOnClickListener(selectRoomListener);
        List<ETSL.messageRecordContentEntry> list = new TSLHelper(this).getMessageRecordContent(mProductKey);
        if (list == null || list.size() == 0) {
            RelativeLayout record = (RelativeLayout) findViewById(R.id.recordLayout);
            record.setVisibility(View.GONE);
        }
        // 消息记录处理
        OnClickListener messageRecordListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreGatewayActivity.this, MessageRecordActivity.class);
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                startActivity(intent);
            }
        };
        ImageView imgMessageRecord = (ImageView) findViewById(R.id.moreGatewayImgMsg);
        imgMessageRecord.setOnClickListener(messageRecordListener);

        // 解除绑定处理
        OnClickListener unBindListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoreGatewayActivity.this);
                builder.setIcon(R.drawable.dialog_quest);
                builder.setTitle(R.string.dialog_title);
                builder.setMessage(R.string.dialog_unbind);
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 设备解除绑定
                        mUserCenter.getGatewaySubdeviceList(mIOTId, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                });
                builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create().show();
            }
        };
        lblUnbind.setOnClickListener(unBindListener);
        imgUnbind.setOnClickListener(unBindListener);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        this.mHomeSpaceManager = new HomeSpaceManager(this);
        this.mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 主动获取设备属性
        new TSLHelper(this).getProperty(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler("MoreGatewayProperty", this.mRealtimeDataHandler);

        this.mUserCenter = new UserCenter(this);
        mSceneManager = new SceneManager(this);
        // 非共享设备才能去获取版本号信息
        if (mOwned > 0) {
            OTAHelper.getFirmwareInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        // 删除长连接实时数据属性回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("MoreGatewayProperty");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}