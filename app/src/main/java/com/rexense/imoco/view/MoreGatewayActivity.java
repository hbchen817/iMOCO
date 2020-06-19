package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.CodeMapper;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.OTAHelper;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.ToastUtils;

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
    private View upgradeView;
    private boolean hasNewerVersion;
    private String currentVersion;
    private String theNewVersion;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private TSLHelper mTSLHelper;

    // 更新状态
    protected void updateStatus(ETSL.propertyEntry propertyEntry) {
        if(propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return;
        }

        // 如果是主动获取状态则补全iotId与productKey
        if(propertyEntry.iotId == null || propertyEntry.iotId.length() == 0) {
            propertyEntry.iotId = mIOTId;
            propertyEntry.productKey = mProductKey;
        }

        if(!propertyEntry.iotId.equals(mIOTId) || !propertyEntry.productKey.equals(mProductKey)) {
            return;
        }

        if(propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID) != null && propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_AlarmSoundID, propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundID));
            if(mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mAlarmBellId.setText(mapperEntry.name + ":");
                mAlarmBellIdValue.setText(mapperEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume));
            if(mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mBellVolume.setText(mapperEntry.name + ":");
                mBellVolumeValue.setText(mapperEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundID, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID));
            if(mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mBellMusicId.setText(mapperEntry.name + ":");
                mBellMusicIdValue.setText(mapperEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_AlarmSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume));
            if(mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mAlarmVolume.setText(mapperEntry.name + ":");
                mAlarmVolumeValue.setText(mapperEntry.value);
            }
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTY:
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    JSONObject items = JSON.parseObject((String)msg.obj);
                    if(items != null) {
                        TSLHelper.parseProperty(mProductKey, items, propertyEntry);
                        updateStatus(propertyEntry);
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表回调
                    mRoomListEntry = CloudDataParser.processHomeRoomList((String)msg.obj);
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
                    break;
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    // 处理获取物的基本信息回调
                    ETSL.thingBaseInforEntry thingBaseInforEntry = CloudDataParser.processThingBaseInformation((String)msg.obj);
                    TextView version = (TextView)findViewById(R.id.moreGatewayLblVersion);
                    version.setText(thingBaseInforEntry.firmwareVersion);
                    break;
                case Constant.MSG_CALLBACK_UNBINDEVICE:
                    // 处理设备解除绑定回调
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 设置系统参数解绑网关以触发数据刷新
                    SystemParameter.getInstance().setIsRefreshData(true);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    Dialog.confirm(MoreGatewayActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                    break;
                case Constant.MSG_CALLBACK_GETOTAFIRMWAREINFO:
                    JSONObject dataJson = JSONObject.parseObject((String)msg.obj);
                    currentVersion = dataJson.getString("currentVersion");
                    theNewVersion = dataJson.getString("version");
                    hasNewerVersion = !currentVersion.equals(theNewVersion);
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
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String)msg.obj);
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
        TextView ok = (TextView)findViewById(R.id.oneItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                // 设置网关属性
                if(mSetType == 1) {
                    mTSLHelper.setProperty(mIOTId,mProductKey, new String[]{CTSL.GW_P_AlarmSoundID}, new String[]{mWheelPickerValue.getText().toString()});
                } else if(mSetType == 2) {
                    mTSLHelper.setProperty(mIOTId,mProductKey, new String[]{CTSL.GW_P_DoorBellSoundVolume}, new String[]{mWheelPickerValue.getText().toString()});
                } else if(mSetType == 3) {
                    mTSLHelper.setProperty(mIOTId,mProductKey, new String[]{CTSL.GW_P_DoorBellSoundID}, new String[]{mWheelPickerValue.getText().toString()});
                } else if(mSetType == 4) {
                    mTSLHelper.setProperty(mIOTId,mProductKey, new String[]{CTSL.GW_P_AlarmSoundVolume}, new String[]{mWheelPickerValue.getText().toString()});
                } else if(mSetType == 5) {
                    // 设置设备所属房间
                    mHomeSpaceManager.updateRoomDevice(SystemParameter.getInstance().getHomeId(), mNewRoomId, mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        TextView cancel = (TextView)findViewById(R.id.oneItemWheelPickerLblCancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
            }
        });

        // 生成选择项数量
        int count = 0;
        if(type == 1 || type == 3) {
            count = 10;
        } else if (type == 2 || type == 4) {
            count = 100;
        } else if (type == 5) {
            count = mRoomListEntry == null || mRoomListEntry.data == null ? 0 : mRoomListEntry.data.size();
        }

        // 生成选择项内容
        if(count > 0) {
            List<String> data = new ArrayList<String>();
            int initIndex = 0;
            if(type <= 4) {
                for(int i = 1; i <= count; i++) {
                    if(String.format("%d", i).equals(initValue)) {
                        initIndex = i - 1;
                    }
                    data.add(String.format("%d", i));
                }
            } else {
                int n = 0;
                for(EHomeSpace.roomEntry room : mRoomListEntry.data) {
                    data.add(room.name);
                    if(room.name.equals(initValue)) {
                        initIndex = n;
                    }
                    n++;
                }
            }
            mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    mWheelPickerValue.setText(data.toString());
                    if(mSetType == 5) {
                        mNewRoomId = mRoomListEntry.data.get(position).roomId;
                        mNewRoomName = data.toString();
                    }
                }
            });
            // 加载两次数据是为了正确初始选中位置
            for(int i = 0; i < 2; i++) {
                mWheelPicker.setData(data);
                mWheelPicker.setSelectedItemPosition(initIndex);
            }
            mWheelPicker.invalidate();
            this.mWheelPickerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_gateway);

        Intent intent = getIntent();
        this.mIOTId = intent.getStringExtra("iotId");
        this.mProductKey = intent.getStringExtra("productKey");
        this.mName = intent.getStringExtra("name");

        this.mTSLHelper = new TSLHelper(this);

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if(deviceEntry != null) {
            this.mRoomName = deviceEntry.roomName;
            this.mBindTime = deviceEntry.bindTime;
        }

        this.mLblTitle = (TextView)findViewById(R.id.includeTitleLblTitle);
        this.mLblTitle.setText(this.mName);

        // 回退处理
        ImageView imgBack = (ImageView)findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.upgradeView = findViewById(R.id.upgrade_view);
        upgradeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (hasNewerVersion){
                    Intent intent1 = new Intent(mActivity,UpgradeFirmwareActivity.class);
                    intent1.putExtra("iotId",mIOTId);
                    intent1.putExtra("productKey",mProductKey);
                    intent1.putExtra("currentVersion",currentVersion);
                    intent1.putExtra("theNewVersion",theNewVersion);
                    startActivity(intent1);
//                }else {
//                    ToastUtils.showToastCentrally(mActivity,getString(R.string.current_version_is_new));
//                }
            }
        });
        this.mAlarmBellId = (TextView)findViewById(R.id.moreGatewayLblAlarmBellId);
        this.mBellVolume = (TextView)findViewById(R.id.moreGatewayLblBellVolume);
        this.mBellMusicId = (TextView)findViewById(R.id.moreGatewayLblBellMusicId);
        this.mAlarmVolume = (TextView)findViewById(R.id.moreGatewayLblAlarmVolume);

        this.mAlarmBellIdValue = (TextView)findViewById(R.id.moreGatewayLblAlarmBellIdValue);
        this.mBellVolumeValue = (TextView)findViewById(R.id.moreGatewayLblBellVolumeValue);
        this.mBellMusicIdValue = (TextView)findViewById(R.id.moreGatewayLblBellMusicIdValue);
        this.mAlarmVolumeValue = (TextView)findViewById(R.id.moreGatewayLblAlarmVolumeValue);

        this.mWheelPickerLayout = (RelativeLayout)findViewById(R.id.oneItemWheelPickerRLPicker);
        this.mWheelPickerLayout.setVisibility(View.GONE);
        this.mWheelPickerValue = (TextView)findViewById(R.id.oneItemWheelPickerLblValue);
        this.mWheelPickerLayout = (RelativeLayout)findViewById(R.id.oneItemWheelPickerRLPicker);
        this.mWheelPicker = (WheelPicker) findViewById(R.id.oneItemWheelPickerWPPicker);
        this.mLblRoomName = (TextView)findViewById(R.id.moreGatewayLblRoom);
        this.mLblRoomName.setText(this.mRoomName);
        TextView bindTime = (TextView)findViewById(R.id.moreGatewayLblBindTime);
        bindTime.setText(this.mBindTime);

        // 报警铃音设置事件处理
        OnClickListener setAlarmBellIdListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, mAlarmBellIdValue.getText().toString());
            }
        };
        ImageView setAlarmBellId = (ImageView) findViewById(R.id.moreGatewayImgAlarmBellId);
        setAlarmBellId.setOnClickListener(setAlarmBellIdListener);

        // 门铃音量设置事件处理
        OnClickListener setBellVolumeListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String volume = mBellVolumeValue.getText().toString().substring(0, mBellVolumeValue.getText().toString().length() - 1);
                setWheelPicker(2, volume);
            }
        };
        ImageView setBellVolume = (ImageView) findViewById(R.id.moreGatewayImgBellVolume);
        setBellVolume.setOnClickListener(setBellVolumeListener);

        // 门铃音乐设置事件处理
        OnClickListener setBellMusicIdListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(3, mBellMusicIdValue.getText().toString());
            }
        };
        ImageView setBellMusicId = (ImageView) findViewById(R.id.moreGatewayImgBellMusicId);
        setBellMusicId.setOnClickListener(setBellMusicIdListener);

        // 报警音量设置事件处理
        OnClickListener setAlarmVolumeListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String volume = mAlarmVolumeValue.getText().toString().substring(0, mAlarmVolumeValue.getText().toString().length() - 1);
                setWheelPicker(4, volume);
            }
        };
        ImageView setAlarmVolume = (ImageView) findViewById(R.id.moreGatewayImgAlarmVolume);
        setAlarmVolume.setOnClickListener(setAlarmVolumeListener);

        // 录入备注名称事件处理
        this.mLblNewNickName = (TextView)findViewById(R.id.moreGatewayLblName);
        this.mLblNewNickName.setText(mName);
        ImageView inputNickName = (ImageView) findViewById(R.id.moreGatewayImgName);
        inputNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mNickName = new EditText(MoreGatewayActivity.this);
                mNickName.setText(mLblNewNickName.getText().toString());
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MoreGatewayActivity.this);
                mDialogBuilder.setTitle(R.string.moredevice_namehint);
                mDialogBuilder.setIcon(R.drawable.dialog_prompt);
                mDialogBuilder.setView(mNickName);
                mDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNewNickName = mNickName.getText().toString();
                        // 设置设备昵称
                        mUserCenter.setDeviceNickName(mIOTId, mNewNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                });
                mDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mDialogBuilder.setCancelable(true);
                AlertDialog dialog = mDialogBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
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
        ImageView imgMessageRecord = (ImageView)findViewById(R.id.moreGatewayImgMsg);
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
                        mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
        TextView lblUnbind = (TextView)findViewById(R.id.moreLblUnbind);
        ImageView imgUnbind = (ImageView)findViewById(R.id.moreImgUnbind);
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

        OTAHelper.getFirmwareInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    @Override
    protected void onDestroy() {
        // 删除长连接实时数据属性回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("MoreGatewayProperty");
        super.onDestroy();
    }
}