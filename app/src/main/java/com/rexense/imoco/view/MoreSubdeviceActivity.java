package com.rexense.imoco.view;

import java.util.ArrayList;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Dialog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 子设备更多界面
 */
public class MoreSubdeviceActivity extends BaseActivity {
    private String mIOTId, mName, mProductKey, mRoomName, mBindTime;
    private int mSetType = 0;
    private RelativeLayout mWheelPickerLayout;
    private WheelPicker mWheelPicker;
    private TextView mLblTitle, mWheelPickerValue, mLblNewNickName, mLblRoomName, mLblMACAddress;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private SceneManager mSceneManager;
    private String mSceneType;

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
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
                    TextView version = (TextView) findViewById(R.id.moreSubdeviceLblVersion);
                    version.setText(thingBaseInforEntry.firmwareVersion);
                    break;
                case Constant.MSG_CALLBACK_UNBINDEVICE:
                    // 处理设备解除绑定回调(用于Detail界面直接退出)
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    Dialog.confirm(MoreSubdeviceActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            if (item.description.contains(mIOTId)) {
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
                                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, sceneList.pageNo + 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            }
                            if (mSceneType.equals(CScene.TYPE_MANUAL)) {
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

    // 设置滑轮选择器(type取值,1房间列表)
    private void setWheelPicker(int type, String initValue) {
        mSetType = type;
        mWheelPickerValue.setText(initValue + "");
        // 确认处理
        TextView ok = (TextView) findViewById(R.id.oneItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                if (mSetType == 1) {
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
        if (type == 1) {
            count = mRoomListEntry == null || mRoomListEntry.data == null ? 0 : mRoomListEntry.data.size();
        }

        // 生成选择项内容
        if (count > 0) {
            List<String> data = new ArrayList<String>();
            int initIndex = 0;
            if (type == 1) {
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
                    if (mSetType == 1) {
                        mNewRoomId = mRoomListEntry.data.get(position).roomId;
                        mNewRoomName = data.toString();
                    }
                }
            });

            // 如果房间没有初始值则默认选择第一项
            if (mSetType == 1 && initValue.equals("")) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_subdevice);

        // 获取参数
        Intent intent = getIntent();
        this.mIOTId = intent.getStringExtra("iotId");
        this.mName = intent.getStringExtra("name");
        this.mProductKey = intent.getStringExtra("productKey");

        this.mLblTitle = (TextView) findViewById(R.id.includeTitleLblTitle);
        this.mLblTitle.setText(this.mName);

        // 分享设备不允许修改房间，故不显示
        if (intent.getIntExtra("owned", 0) == 0) {
            RelativeLayout rlRoom = (RelativeLayout) findViewById(R.id.moreSubdeviceRLRoom);
            rlRoom.setVisibility(View.GONE);
        }

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if (deviceEntry != null) {
            this.mRoomName = deviceEntry.roomName;
            this.mBindTime = deviceEntry.bindTime;
            this.mLblMACAddress = (TextView) findViewById(R.id.moreSubdeviceLblMACAddress);
            this.mLblMACAddress.setText(deviceEntry.deviceName);
        }

        // 回退处理
        ImageView imgBack = (ImageView) findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.mWheelPickerLayout = (RelativeLayout) findViewById(R.id.oneItemWheelPickerRLPicker);
        this.mWheelPickerLayout.setVisibility(View.GONE);
        this.mWheelPickerValue = (TextView) findViewById(R.id.oneItemWheelPickerLblValue);
        this.mWheelPicker = (WheelPicker) findViewById(R.id.oneItemWheelPickerWPPicker);
        this.mLblRoomName = (TextView) findViewById(R.id.moreSubdeviceLblRoom);
        this.mLblRoomName.setText(this.mRoomName);
        TextView bindTime = (TextView) findViewById(R.id.moreSubdeviceLblBindTime);
        bindTime.setText(this.mBindTime);

        // 显示设备名称修改对话框事件处理
        this.mLblNewNickName = (TextView) findViewById(R.id.moreSubdeviceLblName);
        this.mLblNewNickName.setText(mName);
        ImageView inputNickName = (ImageView) findViewById(R.id.moreSubdeviceImgName);
        inputNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceNameDialogEdit();
            }
        });

        // 选择所属房间处理
        OnClickListener selectRoomListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, mLblRoomName.getText().toString());
            }
        };
        ImageView selectRoom = (ImageView) findViewById(R.id.moreSubdeviceImgRoom);
        selectRoom.setOnClickListener(selectRoomListener);

        // 解除绑定处理
        OnClickListener unBindListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoreSubdeviceActivity.this);
                builder.setIcon(R.drawable.dialog_quest);
                builder.setTitle(R.string.dialog_title);
                builder.setMessage(R.string.dialog_unbind);
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 设备解除绑定
                        unbindDevice();
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
        TextView lblUnbind = (TextView) findViewById(R.id.moreSubdeviceLblUnbind);
        ImageView imgUnbind = (ImageView) findViewById(R.id.moreSubdeviceImgUnbind);
        lblUnbind.setOnClickListener(unBindListener);
        imgUnbind.setOnClickListener(unBindListener);
        List<ETSL.messageRecordContentEntry> list = new TSLHelper(this).getMessageRecordContent(mProductKey);
        if (list == null || list.size()== 0){
            RelativeLayout record = (RelativeLayout) findViewById(R.id.recordLayout);
            record.setVisibility(View.GONE);
        }
        // 消息记录处理
        OnClickListener messageRecordListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreSubdeviceActivity.this, MessageRecordActivity.class);
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                startActivity(intent);
            }
        };
        ImageView imgMessageRecord = (ImageView) findViewById(R.id.moreSubdeviceImgMsg);
        imgMessageRecord.setOnClickListener(messageRecordListener);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        this.mHomeSpaceManager = new HomeSpaceManager(this);
        this.mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        mSceneManager = new SceneManager(this);
        this.mUserCenter = new UserCenter(this);

        initStatusBar();
    }


    private void unbindDevice() {
        switch (mProductKey) {
            case CTSL.PK_LIGHT:
            case CTSL.PK_ONE_SCENE_SWITCH:
            case CTSL.PK_TWO_SCENE_SWITCH:
            case CTSL.PK_THREE_SCENE_SWITCH:
            case CTSL.PK_FOUR_SCENE_SWITCH:
            case CTSL.PK_SIX_SCENE_SWITCH:
            case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                this.mSceneType = CScene.TYPE_AUTOMATIC;
                this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_AUTOMATIC, 1, 50, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
                break;
            default:
                mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                break;
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
}