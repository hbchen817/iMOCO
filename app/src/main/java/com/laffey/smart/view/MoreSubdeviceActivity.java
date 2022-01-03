package com.laffey.smart.view;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.databinding.ActivityMoreSubdeviceBinding;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.DeviceManager;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.Dialog;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 子设备更多界面
 */
public class MoreSubdeviceActivity extends BaseActivity implements OnClickListener {
    private ActivityMoreSubdeviceBinding mViewBinding;

    private String mIOTId, mProductKey, mMac;
    private int mSetType = 0;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private SceneManager mSceneManager;
    private String mSceneType;

    private final List<ItemSceneInGateway> mSceneList = new ArrayList<>();
    private String mGwId;

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID: {
                    // 根据子设备iotId查询网关iotId
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    mGwId = response.getString("gwIotId");
                    if (code == 200) {
                        EDevice.deviceEntry gwDev = DeviceBuffer.getDeviceInformation(mGwId);
                        if (gwDev.status == Constant.CONNECTION_STATUS_OFFLINE) {
                            EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
                            if (deviceEntry != null) {
                                mUserCenter.unbindSubDevice(deviceEntry.productKey, deviceEntry.deviceName, Constant.MSG_CALLBACK_UNBINDEVICE,
                                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                ToastUtils.showShortToast(MoreSubdeviceActivity.this, R.string.pls_try_again_later);
                            }
                        } else {
                            SceneManager.manageSceneService(mGwId, mSceneList.get(0).getSceneDetail().getSceneId(), 3,
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(MoreSubdeviceActivity.this, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR: {
                    // 根据子设备iotId查询网关iotId失败
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e.getMessage());
                    ToastUtils.showLongToast(MoreSubdeviceActivity.this, e.getMessage());
                    break;
                }
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                    // 删除设备相关场景
                    break;
                }
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
                    ViseLog.d("处理设备解除绑定回调");
                    QMUITipDialogUtil.dismiss();
                    // 处理设备解除绑定回调(用于Detail界面直接退出)
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    RefreshData.refreshDeviceStateDataFromBuffer();
                    SystemParameter.getInstance().setIsRefreshDeviceData(true);
                    DialogUtils.showConfirmDialog(MoreSubdeviceActivity.this, R.string.dialog_title, R.string.dialog_unbind_ok,
                            R.string.dialog_confirm, new DialogUtils.Callback() {
                                @Override
                                public void positive() {
                                    finish();
                                }

                                @Override
                                public void negative() {

                                }
                            });
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
                                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
        mMac = DeviceBuffer.getDeviceMac(mIOTId);

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(name);

        initStatusBar();

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(this);
        mViewBinding.moreSubdeviceLblName.setText(name);

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        if (deviceEntry == null) {
            getHomeSubDeviceList(1);
        } else {
            initView(deviceEntry);
        }
    }

    private void getHomeSubDeviceList(int pageNo) {
        HomeSpaceManager.getHomeSubDeviceList(this, SystemParameter.getInstance().getHomeId(), pageNo, 50,
                new APIChannel.Callback() {
                    @Override
                    public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                        commitFailure(MoreSubdeviceActivity.this, failEntry);
                    }

                    @Override
                    public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                        responseError(MoreSubdeviceActivity.this, errorEntry);
                    }

                    @Override
                    public void onProcessData(String result) {
                        EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList(result);
                        if (homeDeviceList != null && homeDeviceList.data != null) {
                            // 向缓存追加家列表数据
                            EHomeSpace.deviceEntry deviceEntry = null;
                            for (EHomeSpace.deviceEntry entry : homeDeviceList.data) {
                                if (mIOTId.equals(entry.iotId)) {
                                    deviceEntry = entry;
                                    break;
                                }
                            }

                            if (deviceEntry != null) {
                                DeviceBuffer.addHomeDevice(deviceEntry);
                                getDeviceList(1);
                            } else {
                                if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                                    // 数据没有获取完则获取下一页数据
                                    getHomeSubDeviceList(homeDeviceList.pageNo + 1);
                                } else {
                                    ToastUtils.showLongToast(MoreSubdeviceActivity.this, getString(R.string.pls_try_again_later)
                                            + ":\n" + Constant.API_PATH_GETHOMEDEVICELIST);
                                }
                            }
                        }
                    }
                });
    }

    // 数据获取完则同步刷新设备列表数据
    private void getDeviceList(int pageNo) {
        UserCenter.getDeviceList(MoreSubdeviceActivity.this, pageNo, 50, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(MoreSubdeviceActivity.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(MoreSubdeviceActivity.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取用户设备列表数据
                EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList(result);
                // ViseLog.d("设备列表 = \n" + GsonUtil.toJson(userBindDeviceList));
                if (userBindDeviceList != null && userBindDeviceList.data != null) {
                    EUser.deviceEntry deviceEntry = null;
                    for (EUser.deviceEntry entry : userBindDeviceList.data) {
                        if (mIOTId.equals(entry.iotId)) {
                            deviceEntry = entry;
                            break;
                        }
                    }
                    if (deviceEntry != null) {
                        DeviceBuffer.addUserBindDevice(deviceEntry);
                        queryMac();
                    } else {
                        if (userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            getDeviceList(userBindDeviceList.pageNo + 1);
                        } else {
                            ToastUtils.showLongToast(MoreSubdeviceActivity.this, getString(R.string.pls_try_again_later)
                                    + ":\n" + Constant.API_PATH_GETUSERDEVICELIST);
                        }
                    }
                }
            }
        });
    }

    private void queryMac() {
        List<String> iotList = new ArrayList<>();
        iotList.add(mIOTId);
        DeviceManager.queryMacByIotId(MoreSubdeviceActivity.this, iotList, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                // ViseLog.d("iot - mac = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray iotIdAndMacList = response.getJSONArray("iotIdAndMacList");
                    for (int i = 0; i < iotIdAndMacList.size(); i++) {
                        JSONObject o = iotIdAndMacList.getJSONObject(i);
                        String iotId = o.getString("iotId");
                        String mac = o.getString("mac");
                        DeviceBuffer.updateDeviceMac(iotId, mac);
                    }
                    initView(DeviceBuffer.getDeviceInformation(mIOTId));
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(MoreSubdeviceActivity.this, response, Constant.QUERY_MAC_BY_IOTID);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(mActivity, e.getMessage() + ":\n" + Constant.QUERY_MAC_BY_IOTID);
            }
        });
    }

    private void initView(EDevice.deviceEntry deviceEntry) {
        mViewBinding.moreSubdeviceLblMACAddress.setText(deviceEntry.deviceName);
        mViewBinding.moreSubdeviceLblRoom.setText(deviceEntry.roomName);
        mViewBinding.moreSubdeviceLblBindTime.setText(deviceEntry.bindTime);
        mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);

        // 显示设备名称修改对话框事件处理
        mViewBinding.moreSubdeviceImgName.setOnClickListener(this);

        // 选择所属房间处理
        mViewBinding.moreSubdeviceImgRoom.setOnClickListener(this);

        // 解除绑定处理
        mViewBinding.moreSubdeviceLblUnbind.setOnClickListener(this);
        mViewBinding.moreSubdeviceImgUnbind.setOnClickListener(this);
        List<ETSL.messageRecordContentEntry> list = new TSLHelper(this).getMessageRecordContent(mProductKey);
        if (list == null || list.size() == 0) {
            mViewBinding.recordLayout.setVisibility(View.GONE);
        }
        // 消息记录处理
        mViewBinding.moreSubdeviceImgMsg.setOnClickListener(this);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        mHomeSpaceManager = new HomeSpaceManager(this);
        mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        mSceneManager = new SceneManager(this);
        mUserCenter = new UserCenter(this);

        RealtimeDataReceiver.addEventCallbackHandler("MoreSubDevSceneListCallback", mAPIDataHandler);
        getGWIotIdBySubIotId();

        // 分享设备不允许修改房间，故不显示
        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 0) {
            mViewBinding.moreSubdeviceRLRoom.setVisibility(View.GONE);
        } else {
            mViewBinding.moreSubdeviceRLRoom.setVisibility(View.VISIBLE);
        }
    }

    private void getGWIotIdBySubIotId() {
        SceneManager.getGWIotIdBySubIotId(this, mIOTId, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                if (code == 200) {
                    mGwId = response.getString("gwIotId");
                } else {
                    RetrofitUtil.showErrorMsg(MoreSubdeviceActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.d(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(MoreSubdeviceActivity.this, e.getMessage());
            }
        });
    }

    private void unbindDevice() {
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
        DeviceBuffer.removeExtendedInfo(mIOTId);
        mSceneManager.delExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, null, null, null);
        mSceneList.clear();
        mSceneList.addAll(DeviceBuffer.getScenesBySwitchIotId(mIOTId));
        if (mSceneList.size() > 0) {
            deleteScene(0);
        } else {
            deleteMacControlGroup();
        }
    }

    // 删除子设备相关多控组
    private void deleteMacControlGroup() {
        DeviceManager.deleteSubMacControlGroup(this, DeviceBuffer.getDeviceMac(mIOTId),
                DeviceBuffer.getDeviceMac(mGwId), new DeviceManager.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        int code = response.getInteger("code");
                        if (code == 200) {
                            EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
                            if (deviceEntry != null) {
                                mUserCenter.unbindSubDevice(deviceEntry.productKey, deviceEntry.deviceName, Constant.MSG_CALLBACK_UNBINDEVICE,
                                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                ToastUtils.showShortToast(MoreSubdeviceActivity.this, R.string.pls_try_again_later);
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            RetrofitUtil.showErrorMsg(MoreSubdeviceActivity.this, response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ViseLog.d(e);
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(MoreSubdeviceActivity.this, e.getMessage());
                    }
                });
    }

    // 删除场景
    private void deleteScene(int pos) {
        SceneManager.deleteScene(this, mSceneList.get(pos), new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    String sceneId = mSceneList.get(pos).getSceneDetail().getSceneId();
                    DeviceBuffer.removeScene(sceneId);
                    SceneManager.manageSceneService(mGwId, sceneId, 3,
                            mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    int index = pos + 1;
                    if (index < mSceneList.size()) {
                        deleteScene(index);
                    } else {
                        deleteMacControlGroup();
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(MoreSubdeviceActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.d(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(MoreSubdeviceActivity.this, e.getMessage());
            }
        });
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
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler("MoreSubDevSceneListCallback");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.includeTitleImgBack.getId()) {
            // 返回
            finish();
        } else if (v.getId() == mViewBinding.moreSubdeviceLblUnbind.getId()) {
            if (DeviceBuffer.getDeviceInformation(mIOTId) == null) {
                ToastUtils.showShortToast(this, R.string.pls_try_again_later);
            } else {
                DialogUtils.showConfirmDialog(MoreSubdeviceActivity.this, R.string.dialog_title,
                        R.string.dialog_unbind, R.string.dialog_confirm, R.string.dialog_cancel,
                        new DialogUtils.Callback() {
                            @Override
                            public void positive() {
                                // 设备解除绑定
                                unbindDevice();
                            }

                            @Override
                            public void negative() {

                            }
                        });
            }
        } else if (v.getId() == mViewBinding.moreSubdeviceImgUnbind.getId()) {
            if (DeviceBuffer.getDeviceInformation(mIOTId) == null) {
                ToastUtils.showShortToast(this, R.string.pls_try_again_later);
            } else {
                DialogUtils.showConfirmDialog(MoreSubdeviceActivity.this, R.string.dialog_title,
                        R.string.dialog_unbind, R.string.dialog_confirm, R.string.dialog_cancel,
                        new DialogUtils.Callback() {
                            @Override
                            public void positive() {
                                // 设备解除绑定
                                unbindDevice();
                            }

                            @Override
                            public void negative() {

                            }
                        });
            }
        } else if (v.getId() == mViewBinding.moreSubdeviceImgName.getId()) {
            // 设备名称
            showDeviceNameDialogEdit();
        } else if (v.getId() == mViewBinding.moreSubdeviceImgRoom.getId()) {
            // 所属房间
            setWheelPicker(1, mViewBinding.moreSubdeviceLblRoom.getText().toString());
        } else if (v.getId() == mViewBinding.moreSubdeviceImgMsg.getId()) {
            // 消息记录处理
            Intent intent = new Intent(this, MessageRecordActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", mProductKey);
            startActivity(intent);
        }
    }
}