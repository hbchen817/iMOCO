package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.databinding.ActivityMoreSubdeviceBinding;
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
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 子设备更多界面
 */
public class MoreSubdeviceActivity extends BaseActivity {
    private ActivityMoreSubdeviceBinding mViewBinding;

    private String mIOTId, mProductKey;
    private int mSetType = 0;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private SceneManager mSceneManager;
    private String mSceneType;

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_DEL: {
                    ViseLog.d((String) msg.obj);
                    break;
                }
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表回调
                    mRoomListEntry = CloudDataParser.processHomeRoomList((String) msg.obj);
                    break;
                case Constant.MSG_CALLBACK_SETDEVICENICKNAME:
                    // 处理设置设备昵称回调
                    mViewBinding.moreSubdeviceLblName.setText(mNewNickName);
                    mViewBinding.includeToolbar.includeTitleLblTitle.setText(mNewNickName);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIOTId, mNewNickName);
                    RefreshData.refreshDeviceStateDataFromBuffer();
                    break;
                case Constant.MSG_CALLBACK_UPDATEDEVICEROOM:
                    // 处理更新设备所属房间回调
                    mViewBinding.moreSubdeviceLblRoom.setText(mNewRoomName);
                    // 更新设备缓存房间数据
                    DeviceBuffer.updateDeviceRoom(mIOTId, mNewRoomId, mNewRoomName);
                    RefreshData.refreshRoomListData();
                    RefreshData.refreshDeviceListRoomData(mIOTId);
                    break;
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    // 处理获取物的基本信息回调
                    ETSL.thingBaseInforEntry thingBaseInforEntry = CloudDataParser.processThingBaseInformation((String) msg.obj);
                    mViewBinding.moreSubdeviceLblVersion.setText(thingBaseInforEntry.firmwareVersion);
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
                                //mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

                                EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
                                if (deviceEntry != null)
                                    mUserCenter.unbindSubDevice(deviceEntry.productKey, deviceEntry.deviceName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                else
                                    ToastUtils.showShortToast(MoreSubdeviceActivity.this, R.string.pls_try_again_later);
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
    @SuppressLint("SetTextI18n")
    private void setWheelPicker(int type, String initValue) {
        mSetType = type;
        mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.setText(initValue + "");
        // 确认处理
        mViewBinding.includeWheelPicker.oneItemWheelPickerLblOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
                if (mSetType == 1) {
                    // 设置设备所属房间
                    mHomeSpaceManager.updateRoomDevice(SystemParameter.getInstance().getHomeId(), mNewRoomId, mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        // 取消处理
        mViewBinding.includeWheelPicker.oneItemWheelPickerLblCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
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
            mViewBinding.includeWheelPicker.oneItemWheelPickerWPPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.setText(data.toString());
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
                mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.setText(mNewRoomName);
            }

            // 加载两次数据是为了正确初始选中位置
            for (int i = 0; i < 2; i++) {
                mViewBinding.includeWheelPicker.oneItemWheelPickerWPPicker.setData(data);
                mViewBinding.includeWheelPicker.oneItemWheelPickerWPPicker.setSelectedItemPosition(initIndex);
            }
            mViewBinding.includeWheelPicker.oneItemWheelPickerWPPicker.invalidate();
            mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.VISIBLE);
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
        nameEt.setText(mViewBinding.moreSubdeviceLblName.getText().toString());
        final android.app.Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
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
        mViewBinding = ActivityMoreSubdeviceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        // 获取参数
        Intent intent = getIntent();
        mIOTId = intent.getStringExtra("iotId");
        String name = intent.getStringExtra("name");
        mProductKey = intent.getStringExtra("productKey");

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(name);

        // 分享设备不允许修改房间，故不显示
        if (intent.getIntExtra("owned", 0) == 0) {
            mViewBinding.moreSubdeviceRLRoom.setVisibility(View.GONE);
        }

        initStatusBar();

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
        String roomName = "";
        String bindTimeStr = "";
        if (deviceEntry != null) {
            roomName = deviceEntry.roomName;
            bindTimeStr = deviceEntry.bindTime;
            mViewBinding.moreSubdeviceLblMACAddress.setText(deviceEntry.deviceName);
        } else {
            ToastUtils.showShortToast(this, R.string.pls_try_again_later);
            mViewBinding.moreSubdeviceLblUnbind.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(MoreSubdeviceActivity.this, R.string.pls_try_again_later);
                }
            });
            mViewBinding.moreSubdeviceImgUnbind.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(MoreSubdeviceActivity.this, R.string.pls_try_again_later);
                }
            });
            return;
        }

        mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
        mViewBinding.moreSubdeviceLblRoom.setText(roomName);
        mViewBinding.moreSubdeviceLblBindTime.setText(bindTimeStr);

        // 显示设备名称修改对话框事件处理
        mViewBinding.moreSubdeviceLblName.setText(name);
        mViewBinding.moreSubdeviceImgName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceNameDialogEdit();
            }
        });

        // 选择所属房间处理
        OnClickListener selectRoomListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, mViewBinding.moreSubdeviceLblRoom.getText().toString());
            }
        };
        mViewBinding.moreSubdeviceImgRoom.setOnClickListener(selectRoomListener);

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
        mViewBinding.moreSubdeviceLblUnbind.setOnClickListener(unBindListener);
        mViewBinding.moreSubdeviceImgUnbind.setOnClickListener(unBindListener);
        List<ETSL.messageRecordContentEntry> list = new TSLHelper(this).getMessageRecordContent(mProductKey);
        if (list == null || list.size() == 0) {
            mViewBinding.recordLayout.setVisibility(View.GONE);
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
        mViewBinding.moreSubdeviceImgMsg.setOnClickListener(messageRecordListener);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        mHomeSpaceManager = new HomeSpaceManager(this);
        mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        mSceneManager = new SceneManager(this);
        mUserCenter = new UserCenter(this);
    }


    private void unbindDevice() {
        DeviceBuffer.removeExtendedInfo(mIOTId);
        mSceneManager.delExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, null, null, null);
        switch (mProductKey) {
            case CTSL.PK_LIGHT:
            case CTSL.PK_ONE_SCENE_SWITCH:
            case CTSL.PK_TWO_SCENE_SWITCH:
            case CTSL.PK_THREE_SCENE_SWITCH:
            case CTSL.PK_FOUR_SCENE_SWITCH:
            case CTSL.PK_SIX_SCENE_SWITCH:
            case CTSL.PK_ANY_TWO_SCENE_SWITCH:
            case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
            case CTSL.PK_U_SIX_SCENE_SWITCH:
            case CTSL.PK_SIX_SCENE_SWITCH_YQSXB:
            case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                mSceneType = CScene.TYPE_AUTOMATIC;
                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_AUTOMATIC, 1, 50, mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
                break;
            default:
                //mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

                EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
                if (deviceEntry != null) {
                    mUserCenter.unbindSubDevice(deviceEntry.productKey, deviceEntry.deviceName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                } else {
                    ToastUtils.showShortToast(this, R.string.pls_try_again_later);
                }
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