package com.laffey.smart.view;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.databinding.ActivityMoreGatewayBinding;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.OTAHelper;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.utility.Dialog;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 网关更多界面
 */
public class MoreGatewayActivity extends BaseActivity implements OnClickListener {
    private ActivityMoreGatewayBinding mViewBinding;
    protected String mIOTId = "";
    protected String mProductKey = "";
    protected String mName = "";
    protected String mRoomName = "";
    protected String mBindTime = "";
    private int mSetType = 0;
    private boolean hasNewerVersion;
    private String currentVersion;
    private String theNewVersion;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;
    private TSLHelper mTSLHelper;
    private int mOwned;
    private HashMap<String, String> mDeviceMap = new HashMap<>();
    private SceneManager mSceneManager;
    private String mSceneType;

    private List<ItemSceneInGateway> mSceneList = new ArrayList<>();
    private final List<EUser.deviceEntry> mSubDevList = new ArrayList<>();
    private UnBindingHandler mUnBindingHandler;

    // 更新状态
    @SuppressLint("SetTextI18n")
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
                mViewBinding.moreGatewayLblAlarmBellId.setText(mapperEntry.name + ":");
                mViewBinding.moreGatewayLblAlarmBellIdValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundVolume));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mViewBinding.moreGatewayLblBellVolume.setText(mapperEntry.name + ":");
                mViewBinding.moreGatewayLblBellVolumeValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID) != null && propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_DoorBellSoundID, propertyEntry.getPropertyValue(CTSL.GW_P_DoorBellSoundID));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mViewBinding.moreGatewayLblBellMusicId.setText(mapperEntry.name + ":");
                mViewBinding.moreGatewayLblBellMusicIdValue.setText(mapperEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume) != null && propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume).length() > 0) {
            ETSL.stateEntry mapperEntry = CodeMapper.processPropertyState(MoreGatewayActivity.this, mProductKey, CTSL.GW_P_AlarmSoundVolume, propertyEntry.getPropertyValue(CTSL.GW_P_AlarmSoundVolume));
            if (mapperEntry != null && mapperEntry.name != null && mapperEntry.value != null) {
                mViewBinding.moreGatewayLblAlarmVolume.setText(mapperEntry.name + ":");
                mViewBinding.moreGatewayLblAlarmVolumeValue.setText(mapperEntry.value);
            }
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUEST_DELETE_SCENE: {
                    // 删除本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            DeviceBuffer.removeScene(sceneId);
                            for (ItemSceneInGateway scene : mSceneList) {
                                if (sceneId.equals(scene.getSceneDetail().getSceneId())) {
                                    mSceneList.remove(scene);
                                    break;
                                }
                            }
                            ViseLog.d("云端删除场景 = " + mSceneList.size());
                            if (mSceneList.size() > 0) {
                                if (Constant.IS_TEST_DATA) {
                                    mSceneManager.deleteScene(MoreGatewayActivity.this, mSceneList.get(0).getGwMac(), mSceneList.get(0).getSceneDetail().getSceneId(),
                                            Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, mAPIDataHandler);
                                } else {
                                    ViseLog.d("去网关删除场景 = " + mSceneList.get(0).getSceneDetail().getSceneId());
                                    SceneManager.manageSceneService(mIOTId, mSceneList.get(0).getSceneDetail().getSceneId(), 3,
                                            mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                }
                            } else {
                                EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(mIOTId);
                                if (deviceEntry != null) {
                                    // 设备解除绑定
                                    mUserCenter.getGatewaySubdeviceList(mIOTId, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                } else {
                                    ToastUtils.showShortToast(MoreGatewayActivity.this, R.string.pls_try_again_later);
                                }
                            }
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(MoreGatewayActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(MoreGatewayActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(MoreGatewayActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(MoreGatewayActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                    // 删除设备相关场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("ManageSceneNotification".equals(identifier)) {
                        String type = value.getString("Type");
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        if ("0".equals(status)) {
                            // type  1: 增加场景  2: 编辑场景  3: 删除场景
                            if ("3".equals(type)) {
                                ViseLog.d("网关删除场景 = " + mSceneList.get(0).getSceneDetail().getSceneId());
                                mSceneManager.deleteScene(mActivity, mSceneList.get(0).getGwMac(), mSceneList.get(0).getSceneDetail().getSceneId(),
                                        Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, mAPIDataHandler);
                            }
                        } else {
                            ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                        }
                    }
                    break;
                }
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
                    mViewBinding.moreGatewayLblName.setText(mNewNickName);
                    mViewBinding.toolbarLayout.includeTitleLblTitle.setText(mNewNickName);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIOTId, mNewNickName);
                    break;
                case Constant.MSG_CALLBACK_UPDATEDEVICEROOM:
                    // 处理更新设备所属房间回调
                    mViewBinding.moreGatewayLblRoom.setText(mNewRoomName);
                    // 更新设备缓存房间数据
                    DeviceBuffer.updateDeviceRoom(mIOTId, mNewRoomId, mNewRoomName);
                    RefreshData.refreshRoomListData();
                    RefreshData.refreshDeviceListRoomData(mIOTId);
                    break;
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    // 处理获取物的基本信息回调
                    ETSL.thingBaseInforEntry thingBaseInforEntry = CloudDataParser.processThingBaseInformation((String) msg.obj);
                    mViewBinding.moreGatewayLblVersion.setText(thingBaseInforEntry.firmwareVersion);
                    break;
                case Constant.MSG_CALLBACK_UNBINDEVICE:
                    QMUITipDialogUtil.dismiss();
                    // 处理设备解除绑定回调
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 设置系统参数解绑网关以触发数据刷新
                    SystemParameter.getInstance().setIsRefreshDeviceData(true);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    //SpUtils.removeKey(MoreGatewayActivity.this, SpUtils.SP_DEVS_INFO, mIOTId);
                    Dialog.confirm(MoreGatewayActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                    break;
                case Constant.MSG_CALLBACK_GETOTAFIRMWAREINFO:
                    // 处理获取OTA固件信息
                    JSONObject dataJson = JSONObject.parseObject((String) msg.obj);
                    currentVersion = dataJson.getString("currentVersion");
                    theNewVersion = dataJson.getString("version");
                    hasNewerVersion = !currentVersion.equals(theNewVersion);
                    mViewBinding.moreGatewayLblVersion.setText(currentVersion);
                    break;
                case Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST: {
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    if (list != null && list.data != null) {
                        mSubDevList.addAll(list.data);
                        for (EUser.deviceEntry e : list.data) {
                            if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                                mSceneManager.setExtendedProperty(e.iotId, Constant.TAG_DEV_KEY_NICKNAME, "{}",
                                        null, null, null);
                            } else {
                                switch (e.productKey) {
                                    case CTSL.PK_LIGHT:
                                    case CTSL.PK_ONE_WAY_DIMMABLE_LIGHT:
                                    case CTSL.PK_SYT_ONE_SCENE_SWITCH:
                                    case CTSL.PK_ONE_SCENE_SWITCH:
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mDeviceMap.put(e.iotId, e.iotId);
                                        break;
                                    case CTSL.PK_ANY_TWO_SCENE_SWITCH:
                                    case CTSL.PK_TWO_SCENE_SWITCH:
                                    case CTSL.PK_SYT_TWO_SCENE_SWITCH:
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                        mDeviceMap.put(e.iotId, e.iotId);
                                        break;
                                    case CTSL.PK_THREE_SCENE_SWITCH:
                                    case CTSL.PK_SYT_THREE_SCENE_SWITCH:
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                        mDeviceMap.put(e.iotId, e.iotId);
                                        break;
                                    case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
                                    case CTSL.PK_FOUR_SCENE_SWITCH:
                                    case CTSL.PK_SYT_FOUR_SCENE_SWITCH:
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", null, null, mAPIDataHandler);
                                        mDeviceMap.put(e.iotId, e.iotId);
                                        break;
                                    case CTSL.PK_SIX_SCENE_SWITCH_YQSXB:
                                    case CTSL.PK_U_SIX_SCENE_SWITCH:
                                    case CTSL.PK_SIX_SCENE_SWITCH:
                                    case CTSL.PK_SYT_SIX_SCENE_SWITCH:
                                    case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                                        ViseLog.d("CTSL.PK_SIX_TWO_SCENE_SWITCH = " + CTSL.PK_SIX_TWO_SCENE_SWITCH);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                        mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                        mDeviceMap.put(e.iotId, e.iotId);
                                        break;
                                /*case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", null, null, mAPIDataHandler);
                                    mSceneManager.setExtendedProperty(e.iotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", null, null, mAPIDataHandler);
                                    mDeviceMap.put(e.iotId, e.iotId);
                                    break;*/
                                    default:
                                        break;
                                }
                            }
                        }
                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getGatewaySubdeviceList(mIOTId, list.pageNo + 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则加载显示
                            /*if (mDeviceMap.size() > 0) {
                                mSceneType = CScene.TYPE_AUTOMATIC;
                                // mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            }*/
                            unBindingDev(0);
                            /*mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);*/
                        }
                    }
                    break;
                }
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

    private static class UnBindingHandler extends Handler {
        private final WeakReference<MoreGatewayActivity> ref;

        public UnBindingHandler(MoreGatewayActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MoreGatewayActivity activity = ref.get();
            if (activity == null) return;
            activity.unBindingDev(msg.what);
        }
    }

    private void unBindingDev(int pos) {
        if (pos <= mSubDevList.size() - 1)
            mUserCenter.unbindSubDevice(mSubDevList.get(pos).productKey, mSubDevList.get(pos).deviceName,
                    mCommitFailureHandler, mResponseErrorHandler, new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            DeviceBuffer.deleteDevice(mSubDevList.get(pos).iotId);
                            mUnBindingHandler.sendEmptyMessageDelayed(pos + 1, 2000);
                            return false;
                        }
                    }));
        else {
            mUserCenter.unbindDevice(mIOTId,
                    mCommitFailureHandler, mResponseErrorHandler, new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            QMUITipDialogUtil.dismiss();
                            // 处理设备解除绑定回调
                            setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                            // 设置系统参数解绑网关以触发数据刷新
                            SystemParameter.getInstance().setIsRefreshDeviceData(true);
                            // 删除缓存中的数据
                            DeviceBuffer.deleteDevice(mIOTId);
                            //SpUtils.removeKey(MoreGatewayActivity.this, SpUtils.SP_DEVS_INFO, mIOTId);
                            Dialog.confirm(MoreGatewayActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                            return false;
                        }
                    }));
        }
    }

    // 实时数据处理器
    private final Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_LNPROPERTYNOTIFY) {// 处理属性通知回调
                ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                updateStatus(propertyEntry);
            }
            return false;
        }
    });

    // 设置滑轮选择器(type取值,1报警铃音,2门铃音量,3门铃音乐,4报警音量, 5房间列表)
    @SuppressLint("SetTextI18n")
    private void setWheelPicker(int type, String initValue) {
        mSetType = type;
        mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.setText(initValue + "");
        // 确认处理
        TextView ok = (TextView) findViewById(R.id.oneItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
                // 设置网关属性
                String value = mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.getText().toString();
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
                mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
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
                    if (String.format(Locale.getDefault(), "%d", i).equals(initValue)) {
                        initIndex = i - 1;
                    }
                    data.add(String.format(Locale.getDefault(), "%d", i));
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
            mViewBinding.includeWheelPicker.oneItemWheelPickerWPPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    mViewBinding.includeWheelPicker.oneItemWheelPickerLblValue.setText(data.toString());
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
        nameEt.setText(mViewBinding.moreGatewayLblName.getText().toString());
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
        mViewBinding = ActivityMoreGatewayBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        mIOTId = intent.getStringExtra("iotId");
        mProductKey = intent.getStringExtra("productKey");
        mName = intent.getStringExtra("name");

        // 分享设备不允许修改房间,故不显示
        mOwned = intent.getIntExtra("owned", 0);
        if (mOwned == 0) {
            mViewBinding.moreGatewayRLRoom.setVisibility(View.GONE);
        }

        mTSLHelper = new TSLHelper(this);

        initStatusBar();

        if (CTSL.PK_GATEWAY_RG4100.equals(mProductKey)) {
            mViewBinding.functionSettings.setVisibility(View.GONE);
        }

        // 回退处理
        mViewBinding.toolbarLayout.includeTitleImgBack.setOnClickListener(this);

        mViewBinding.toolbarLayout.includeTitleLblTitle.setText(mName);

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if (deviceEntry != null) {
            mRoomName = deviceEntry.roomName;
            mBindTime = deviceEntry.bindTime;
        } else {
            ToastUtils.showShortToast(this, R.string.pls_try_again_later);
            mViewBinding.moreLblUnbind.setOnClickListener(this);
            mViewBinding.moreImgUnbind.setOnClickListener(this);
            return;
        }

        mViewBinding.moreGatewayLblId.setText(deviceEntry.deviceName);

        // 分享设备不能进行升级,故不显示
        if (mOwned == 0) {
            mViewBinding.upgradeView.setVisibility(View.GONE);
        }
        mViewBinding.upgradeView.setOnClickListener(this);

        mViewBinding.includeWheelPicker.oneItemWheelPickerRLPicker.setVisibility(View.GONE);
        mViewBinding.moreGatewayLblRoom.setText(mRoomName);
        mViewBinding.moreGatewayLblBindTime.setText(this.mBindTime);

        mViewBinding.moreGatewayImgAlarmBellId.setOnClickListener(mSetAlarmBellIdListener);
        mViewBinding.moreGatewayImgBellVolume.setOnClickListener(mSetBellVolumeListener);
        mViewBinding.moreGatewayImgBellMusicId.setOnClickListener(mSetBellMusicIdListener);
        mViewBinding.moreGatewayImgAlarmVolume.setOnClickListener(mSetAlarmVolumeListener);

        // 修改设备名称事件处理
        mViewBinding.moreGatewayLblName.setText(mName);
        mViewBinding.moreGatewayImgName.setOnClickListener(this);

        mViewBinding.moreGatewayImgRoom.setOnClickListener(mSelectRoomListener);
        List<ETSL.messageRecordContentEntry> list = new TSLHelper(this).getMessageRecordContent(mProductKey);
        if (list == null || list.size() == 0) {
            mViewBinding.recordLayout.setVisibility(View.GONE);
        }

        mViewBinding.moreGatewayImgMsg.setOnClickListener(mMessageRecordListener);

        mViewBinding.moreLblUnbind.setOnClickListener(mUnBindListener);
        mViewBinding.moreImgUnbind.setOnClickListener(mUnBindListener);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        mHomeSpaceManager = new HomeSpaceManager(this);
        mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 主动获取设备属性
        new TSLHelper(this).getProperty(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler("MoreGatewayProperty", mRealtimeDataHandler);

        RealtimeDataReceiver.addEventCallbackHandler("MoreGatewaySceneListCallback", mAPIDataHandler);

        mUserCenter = new UserCenter(this);
        mSceneManager = new SceneManager(this);
        // 非共享设备才能去获取版本号信息
        if (mOwned > 0) {
            OTAHelper.getFirmwareInformation(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }

        mUnBindingHandler = new UnBindingHandler(this);
    }

    // 解除绑定处理
    private final OnClickListener mUnBindListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MoreGatewayActivity.this);
            builder.setIcon(R.drawable.dialog_quest);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(R.string.dialog_unbind);
            builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    //deleteScene();
                    QMUITipDialogUtil.showLoadingDialg(MoreGatewayActivity.this, R.string.is_submitted);
                    mSubDevList.clear();
                    EDevice.deviceEntry gwDev = DeviceBuffer.getDeviceInformation(mIOTId);
                    if (gwDev.status == Constant.CONNECTION_STATUS_OFFLINE) {
                        mUserCenter.getGatewaySubdeviceList(mIOTId, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else
                        querySceneList(MoreGatewayActivity.this, DeviceBuffer.getDeviceInformation(mIOTId).mac, "0");
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

    // 消息记录处理
    private final OnClickListener mMessageRecordListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MoreGatewayActivity.this, MessageRecordActivity.class);
            intent.putExtra("iotId", mIOTId);
            intent.putExtra("productKey", mProductKey);
            startActivity(intent);
        }
    };

    // 门铃音量设置事件处理
    private final OnClickListener mSetBellVolumeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String volume = mViewBinding.moreGatewayLblBellVolumeValue.getText().toString().substring(0, mViewBinding.moreGatewayLblBellVolumeValue.getText().toString().length() - 1);
            setWheelPicker(2, volume);
        }
    };

    // 门铃音乐设置事件处理
    private final OnClickListener mSetBellMusicIdListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setWheelPicker(3, mViewBinding.moreGatewayLblBellMusicIdValue.getText().toString());
        }
    };

    // 报警音量设置事件处理
    private final OnClickListener mSetAlarmVolumeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String volume = mViewBinding.moreGatewayLblAlarmVolumeValue.getText().toString().substring(0, mViewBinding.moreGatewayLblAlarmVolumeValue.getText().toString().length() - 1);
            setWheelPicker(4, volume);
        }
    };

    // 报警铃音设置事件处理
    private final OnClickListener mSetAlarmBellIdListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setWheelPicker(1, mViewBinding.moreGatewayLblAlarmBellIdValue.getText().toString());
        }
    };

    // 选择所属房间处理
    private final OnClickListener mSelectRoomListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setWheelPicker(5, mViewBinding.moreGatewayLblRoom.getText().toString());
        }
    };

    private void deleteScene() {
        mSceneList.clear();
        mSceneList.addAll(DeviceBuffer.getAllSceneInGW(DeviceBuffer.getDeviceMac(mIOTId)));
        ViseLog.d("2 = " + GsonUtil.toJson(mSceneList));
        if (Constant.IS_TEST_DATA) {
            if (mSceneList.size() > 0) {
                mSceneManager.deleteScene(this, DeviceBuffer.getDeviceMac(mIOTId), mSceneList.get(0).getSceneDetail().getSceneId(),
                        Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, mAPIDataHandler);
            } else {
                mUserCenter.getGatewaySubdeviceList(mIOTId, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        } else {
            if (mSceneList.size() > 0) {
                SceneManager.manageSceneService(mIOTId, mSceneList.get(0).getSceneDetail().getSceneId(), 3,
                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            } else {
                mUserCenter.getGatewaySubdeviceList(mIOTId, 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.toolbarLayout.includeTitleImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.moreLblUnbind.getId()) {
            ToastUtils.showShortToast(this, R.string.pls_try_again_later);
        } else if (v.getId() == mViewBinding.moreGatewayImgName.getId()) {
            showDeviceNameDialogEdit();
        } else if (v.getId() == mViewBinding.moreImgUnbind.getId()) {
            ToastUtils.showShortToast(this, R.string.pls_try_again_later);
        } else if (v.getId() == mViewBinding.upgradeView.getId()) {
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
    }

    // 查询本地场景列表
    private void querySceneList(Context context, String mac, String type) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().querySceneList(context, mac, type);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        // ViseLog.d("手动场景 = " + GsonUtil.toJson(response));
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 0 || code == 200) {
                            mSceneList.clear();
                            if (sceneList != null) {
                                for (int i = 0; i < sceneList.size(); i++) {
                                    JSONObject sceneObj = sceneList.getJSONObject(i);
                                    try {
                                        ItemSceneInGateway scene = JSONObject.parseObject(sceneObj.toJSONString(), ItemSceneInGateway.class);

                                        DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if ("1".equals(type)) {
                                // 数据获取完则设置场景列表数据
                                deleteScene();
                            } else if ("0".equals(type)) {
                                querySceneList(mActivity, mac, "1");
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            if (msg != null && msg.length() > 0)
                                ToastUtils.showLongToast(mActivity, msg);
                            else
                                ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.e(e);
                        ToastUtils.showLongToast(mActivity, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler("MoreGatewaySceneListCallback");
    }
}