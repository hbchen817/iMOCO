package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLightSceneListBinding;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EEventScene;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.ProductHelper;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwitchLocalSceneListActivity extends BaseActivity {
    private ActivityLightSceneListBinding mViewBinding;

    private final int PAGE_SIZE = 10;
    private final int SCENE_LIST_REQUEST_CODE = 10000;
    private final int SCENE_LIST_RESULT_CODE = 10001;

    private String mIotId;
    private SceneManager mSceneManager;
    private List<EScene.sceneListItemEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder> mAdapter;
    private int mClickPosition = -1;
    private String mKeyCode;
    private int mBindPosition;
    private JSONObject mExtendedJsonObject;
    private String mAutoSceneID;
    private TypedArray mSceneBgs;

    private UserCenter mUserCenter;
    private final List<EDevice.deviceEntry> mGatewayList = new ArrayList<>();
    private EDevice.deviceEntry mGatewayEntry;
    private String mGatewayId;
    private String mGatewayMac;
    private String mSceneId;
    private final List<ItemSceneInGateway> mItemSceneList = new ArrayList<>();
    private String mSType = "0";
    private ItemSceneInGateway mBindScene;
    private int mSceneType = 0;// 0：自动场景 1：手动场景
    private int mBindPos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightSceneListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mUserCenter = new UserCenter(this);
        EventBus.getDefault().register(this);
        mIotId = getIntent().getStringExtra("extra");
        mKeyCode = getIntent().getStringExtra("keyCode");
        mGatewayId = getIntent().getStringExtra("gateway_id");
        mGatewayMac = getIntent().getStringExtra("gateway_mac");
        mSceneId = getIntent().getStringExtra("scene_id");
        mViewBinding.includeToolbar.tvToolbarTitle.setText("场景绑定");
        mSceneManager = new SceneManager(this);
        mSceneBgs = getResources().obtainTypedArray(R.array.scene_bgs);
        initAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mViewBinding.recycleView.setLayoutManager(gridLayoutManager);
        mViewBinding.recycleView.setAdapter(mAdapter);
        mViewBinding.createSceneView.setOnClickListener(this::onViewClicked);
        mViewBinding.recycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mClickPosition = -1;
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        getList();

        initStatusBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // RealtimeDataReceiver.addEventCallbackHandler("SwitchLocalSceneListCallback", mAPIDataHandler);
        ViseLog.d("mSceneId = " + mSceneId + "\n场景缓存 = \n" + GsonUtil.toJson(DeviceBuffer.getAllScene()));
    }

    // 修改按键绑定的场景，将原自动场景删除
    private void deletePreAutoScene() {
        ViseLog.d("删掉原来绑定的自动场景 = " + GsonUtil.toJson(DeviceBuffer.getSceneByCid(mSceneId, mKeyCode)));
        SceneManager.deleteScene(this, DeviceBuffer.getSceneByCid(mSceneId, mKeyCode), new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        String sceneId = response.getString("sceneId");
                        DeviceBuffer.removeScene(sceneId);

                        // bindKeyScene(mBindPos);
                        // 更新之前的手动场景，删除与之前自动场景的关联
                        SceneManager.manageSceneService(mGatewayId, sceneId, 3,
                                mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        //DeviceBuffer.removeScene(mSceneId);

                        updatePreManualScene(mSceneId, sceneId, mKeyCode);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, e.getMessage());
            }
        });
    }

    // 修改按键绑定的场景，修改原手动场景
    private void updatePreManualScene(String sceneId, String preAutoSceneId, String keyCode) {
        ItemSceneInGateway scene = DeviceBuffer.getScene(sceneId);
        ViseLog.d("修改之前绑定的手动场景 修改前 = " + GsonUtil.toJson(scene));

        // 删除掉原手动场景关联的按键键值
        StringBuilder keySb = new StringBuilder();
        String key = scene.getAppParams().getString("key");
        String[] keys = key.split(",");
        for (String key1 : keys) {
            if (!key1.equals(keyCode)) {
                if (keySb.length() != 0) {
                    keySb.append(",");
                }
                keySb.append(key1);
            }
        }
        if (keySb.toString().length() == 0) {
            scene.getAppParams().remove("key");
        } else {
            scene.getAppParams().put("key", keySb.toString());
        }

        // 删除掉原手动场景关联的自动场景id
        StringBuilder cIdSB = new StringBuilder();
        String cId = scene.getAppParams().getString("cId");
        String[] cIds = cId.split(",");
        for (String cId1 : cIds) {
            if (!cId1.equals(preAutoSceneId)) {
                if (cIdSB.length() != 0) {
                    cIdSB.append(",");
                }
                cIdSB.append(cId1);
            }
        }
        if (cIdSB.toString().length() == 0) {
            scene.getAppParams().remove("cId");
        } else {
            scene.getAppParams().put("cId", cIdSB.toString());
        }

        ViseLog.d("修改之前绑定的手动场景 修改后 = " + GsonUtil.toJson(scene));
        // 更新已经修改绑定按键的手动场景
        SceneManager.updateScene(this, scene, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        String sceneId = response.getString("sceneId");
                        SceneManager.manageSceneService(mGatewayId, sceneId, 2, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        DeviceBuffer.addScene(sceneId, scene);
                                /*if (sceneId.equals(mSceneId)) {
                                    bindKeyScene(mBindPos);
                                }*/
                        bindKeyScene(mBindPos);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, e.getMessage());
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

    @Subscribe
    public void refreshSceneList(EEvent eventEntry) {
        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            getList();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // RealtimeDataReceiver.deleteCallbackHandler("SwitchLocalSceneListCallback");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getList() {
        mSType = "0";
        mItemSceneList.clear();
        mList.clear();
        SceneManager.querySceneList(this, mGatewayMac, "1", new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 查询本地场景列表
                // ViseLog.d("查询本地场景列表 = " + GsonUtil.toJson(response));
                int code = response.getInteger("code");
                JSONArray sceneList = response.getJSONArray("sceneList");
                if (code == 200) {
                    if (sceneList != null) {
                        for (int i = 0; i < sceneList.size(); i++) {
                            JSONObject sceneObj = sceneList.getJSONObject(i);
                            ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                            DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                            JSONObject appParams = scene.getAppParams();
                            if (appParams == null || !"e".equals(appParams.getString("type"))
                                    || appParams.getString("switchIotId") == null
                                    || !mIotId.equals(appParams.getString("switchIotId"))) {
                                continue;
                            }

                            mItemSceneList.add(scene);

                            EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                            entry.id = scene.getSceneDetail().getSceneId();
                            entry.name = scene.getSceneDetail().getName();
                            entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                            entry.description = scene.getGwMac();
                            entry.catalogId = scene.getSceneDetail().getType();
                            mList.add(entry);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, e.getMessage());
            }
        });
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                    // 网关上报事件，监听删除网关上场景事件
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    ViseLog.d("删除场景 = " + jsonObject.toJSONString());
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("ManageSceneNotification".equals(identifier)) {
                        String type = value.getString("Type");
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        if ("0".equals(status)) {
                            // type  1: 增加场景  2: 编辑场景  3: 删除场景
                            ViseLog.d("删除掉之前的自动场景 mSceneId = " + mSceneId);
                            if ("3".equals(type) && mSceneId != null) {
                                deletePreAutoScene();
                            }
                        } else {
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                        }
                    }
                    break;
                }
                /*case Constant.MSG_QUEST_DELETE_SCENE: {
                    // 删除本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            DeviceBuffer.removeScene(sceneId);

                            updatePreManualScene(mSceneId, sceneId, mKeyCode);
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                    }
                    break;
                }*/
                case Constant.MSG_QUEST_ADD_SCENE_ERROR:
                case Constant.MSG_QUEST_DELETE_SCENE_ERROR:
                case Constant.MSG_QUEST_UPDATE_SCENE_ERROR: {
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e);
                    ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, e.getMessage());
                    break;
                }
                case Constant.MSG_QUEST_UPDATE_SCENE: {
                    // 更新本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    QMUITipDialogUtil.dismiss();
                    boolean result = response.getBoolean("result");
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            SceneManager.manageSceneService(mGatewayId, sceneId, 2, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            if (mSceneId == null || mSceneId.length() == 0) {
                                QMUITipDialogUtil.dismiss();
                                setResult(2);
                                finish();
                            } else {
                                if (sceneId.equals(mSceneId)) {
                                    bindKeyScene(mBindPos);
                                } else {
                                    QMUITipDialogUtil.dismiss();
                                    Intent intent = new Intent(SwitchLocalSceneListActivity.this, SixTwoSceneSwitchActivity2.class);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                        }
                        RefreshData.refreshHomeSceneListData();
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_ADD_SCENE: {
                    // 新增本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    // ViseLog.d("创建成功 = " + GsonUtil.toJson(response));
                    QMUITipDialogUtil.dismiss();
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            ItemSceneInGateway scene = getNewAutoScene(mBindScene, mKeyCode);
                            scene.getSceneDetail().setSceneId(sceneId);
                            DeviceBuffer.addScene(sceneId, scene);
                            if (mSceneType == 0) {
                                DeviceBuffer.addScene(sceneId, getNewAutoScene(mBindScene, mKeyCode));
                                // 自动场景提交成功后，再提交手动场景
                                ItemSceneInGateway tmp = JSONObject.parseObject(GsonUtil.toJson(mBindScene), ItemSceneInGateway.class);

                                StringBuilder cidSB = new StringBuilder();
                                String cId = tmp.getAppParams().getString("cId");
                                if (cId != null && cId.length() > 0) {
                                    cidSB.append(cId + "," + sceneId);
                                } else
                                    cidSB.append(sceneId);
                                tmp.getAppParams().put("cId", cidSB.toString());

                                StringBuilder KeySB = new StringBuilder();
                                String key = tmp.getAppParams().getString("key");
                                if (key != null && key.length() > 0) {
                                    KeySB.append(key + "," + mKeyCode);
                                } else
                                    KeySB.append(mKeyCode);
                                tmp.getAppParams().put("key", KeySB.toString());

                                tmp.getSceneDetail().setConditions(new ArrayList<>());
                                tmp.getSceneDetail().setType("1");
                                // ViseLog.d("自动场景提交成功后 = " + GsonUtil.toJson(tmp));
                                mSceneType = 1;
                                mSceneManager.manageSceneService(mGatewayId, sceneId, 1, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                mSceneManager.updateScene(SwitchLocalSceneListActivity.this, tmp, Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, mAPIDataHandler);
                            } else {
                                QMUITipDialogUtil.dismiss();
                            }
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                    // 查询本地场景列表
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("查询本地场景列表 = " + GsonUtil.toJson(response));
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    if (code == 0 || code == 200) {
                        if (sceneList != null) {
                            // ViseLog.d("场景列表 = " + GsonUtil.toJson(sceneList));
                            for (int i = 0; i < sceneList.size(); i++) {
                                JSONObject sceneObj = sceneList.getJSONObject(i);
                                ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                                DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                                if ("1".equals(mSType)) {
                                    JSONObject appParams = scene.getAppParams();
                                    if (appParams == null || !"e".equals(appParams.getString("type"))
                                            || appParams.getString("switchIotId") == null
                                            || !mIotId.equals(appParams.getString("switchIotId"))) {
                                        continue;
                                    }

                                    mItemSceneList.add(scene);

                                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                                    entry.id = scene.getSceneDetail().getSceneId();
                                    entry.name = scene.getSceneDetail().getName();
                                    entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                                    entry.description = scene.getGwMac();
                                    entry.catalogId = scene.getSceneDetail().getType();
                                    mList.add(entry);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                    }
                    if ("0".equals(mSType)) {
                        mSType = "1";
                        mSceneManager.querySceneList(SwitchLocalSceneListActivity.this, mGatewayMac, mSType,
                                Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
                    }
                    break;
                }
                case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR: {
                    // 查询本地场景列表错误
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e);
                    ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.pls_try_again_later);
                    break;
                }
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            if (item.description.contains(mIotId)) {
                                mList.add(item);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL,
                                    sceneList.pageNo + 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        mExtendedJsonObject = JSON.parseObject((String) msg.obj);
                        if (!mExtendedJsonObject.isEmpty()) {
                            //更换绑定场景
                            String autoSceneId = mExtendedJsonObject.getString("asId");
                            EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                                    CScene.TYPE_AUTOMATIC, mList.get(mBindPosition).name, mIotId);
                            baseInfoEntry.enable = true;
                            baseInfoEntry.sceneId = autoSceneId;
                            EScene.triggerEntry entry = new EScene.triggerEntry();
                            EDevice.deviceEntry deviceInformation = DeviceBuffer.getDeviceInformation(mIotId);
                            entry.productKey = deviceInformation.productKey;
                            entry.deviceName = deviceInformation.deviceName;
                            entry.state = new ETSL.stateEntry("", "", "", mKeyCode);
                            mSceneManager.updateSwitchAutoScene(baseInfoEntry, entry, mList.get(mBindPosition).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            break;
                        }
                    }
                    createAutoScene();
                    break;
                case Constant.MSG_CALLBACK_CREATE_SWITCH_AUTO_SCENE:
                    mAutoSceneID = (String) msg.obj;
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("asId", mAutoSceneID);
                        jsonObject.put("keyNo", mKeyCode);
                        jsonObject.put("name", mList.get(mBindPosition).name);
                        jsonObject.put("msId", mList.get(mBindPosition).id);
                        mSceneManager.setExtendedProperty(mIotId, mKeyCode, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else {
                        ToastUtils.showToastCentrally(mActivity, "绑定失败");
                    }
                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET:
                    ToastUtils.showToastCentrally(mActivity, "绑定成功");
                    EventBus.getDefault().post(new SceneBindEvent(mList.get(mBindPosition).name));
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE: {
                    mAutoSceneID = (String) msg.obj;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("asId", mAutoSceneID);
                    jsonObject.put("keyNo", mKeyCode);
                    jsonObject.put("name", mList.get(mBindPosition).name);
                    jsonObject.put("msId", mList.get(mBindPosition).id);
                    mSceneManager.setExtendedProperty(mIotId, mKeyCode, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    break;
                }
                case Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST: {
                    // 网关下子设备列表
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    if (list != null && list.data != null) {
                        for (EUser.deviceEntry e : list.data) {
                            DeviceBuffer.setGatewayId(e.iotId, mGatewayEntry.iotId);
                        }

                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getGatewaySubdeviceList(mGatewayEntry.iotId, list.pageNo + 1, PAGE_SIZE, Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST,
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完后
                            int pos = mGatewayList.indexOf(mGatewayEntry);
                            if (pos < mGatewayList.size() - 1) {
                                pos++;
                                mGatewayEntry = mGatewayList.get(pos);
                                mUserCenter.getGatewaySubdeviceList(mGatewayEntry.iotId, 1, PAGE_SIZE, Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST,
                                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            } else {
                                // ViseLog.d(GsonUtil.toJson(DeviceBuffer.getAllDeviceInformation()));
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    private void createAutoScene() {
        EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                CScene.TYPE_AUTOMATIC, mList.get(mBindPosition).name, mIotId);
        baseInfoEntry.enable = true;
        EScene.triggerEntry entry = new EScene.triggerEntry();
        EDevice.deviceEntry deviceInformation = DeviceBuffer.getDeviceInformation(mIotId);
        entry.productKey = deviceInformation.productKey;
        entry.deviceName = deviceInformation.deviceName;
        entry.state = new ETSL.stateEntry("", "", "", mKeyCode);
        mSceneManager.createSwitchAutoScene(baseInfoEntry, entry, mList.get(mBindPosition).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder>(R.layout.item_scene, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, EScene.sceneListItemEntry sceneListItemEntry) {
                int i = mList.indexOf(sceneListItemEntry);
                i = i % mSceneBgs.length();
                baseViewHolder.setText(R.id.sceneName, sceneListItemEntry.name);
                if (mSceneId != null && mSceneId.equals(sceneListItemEntry.id)) {
                    baseViewHolder.setTextColor(R.id.sceneName, ContextCompat.getColor(SwitchLocalSceneListActivity.this, R.color.appcolor));
                } else {
                    baseViewHolder.setTextColor(R.id.sceneName, ContextCompat.getColor(SwitchLocalSceneListActivity.this, R.color.black));
                }
                baseViewHolder.setGone(R.id.editMask, mClickPosition != baseViewHolder.getAdapterPosition());
                baseViewHolder.setImageResource(R.id.image, mSceneBgs.getResourceId(i, 0));
            }
        };
        mAdapter.addChildClickViewIds(R.id.editBtn, R.id.bindBtn);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            mClickPosition = position;
            mAdapter.notifyDataSetChanged();
        });
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.editBtn) {
                    EEventScene scene = new EEventScene();
                    scene.setTarget("SwitchLocalSceneActivity");
                    scene.setGatewayId(mGatewayId);
                    scene.setScene(mItemSceneList.get(position).getSceneDetail());
                    scene.setGatewayMac(mItemSceneList.get(position).getGwMac());
                    scene.setAppParams(mItemSceneList.get(position).getAppParams());
                    EventBus.getDefault().postSticky(scene);

                    Intent intent = new Intent(SwitchLocalSceneListActivity.this, SwitchLocalSceneActivity.class);
                    startActivityForResult(intent, SCENE_LIST_REQUEST_CODE);
                } else if (view.getId() == R.id.bindBtn) {
                    mBindPos = position;
                    if (DeviceBuffer.getDeviceInformation(mGatewayId).status != Constant.CONNECTION_STATUS_OFFLINE) {
                        if (mSceneId == null || mSceneId.length() == 0) {
                            // 此按键之前未绑定场景
                            bindKeyScene(position);
                        } else {
                            // 此按键之前绑定了场景，需要重新绑定另外场景
                            // 将之前绑定的自动场景删除，创建新的自动场景，然后再更新手动场景
                            QMUITipDialogUtil.showLoadingDialg(SwitchLocalSceneListActivity.this, R.string.is_binding_scene);
                            ViseLog.d("正在绑定场景 mSceneId = " + mSceneId);
                            deletePreAutoScene();
                        }
                    } else {
                        ToastUtils.showLongToast(SwitchLocalSceneListActivity.this, R.string.gw_is_offline_cannot_create_scene);
                    }
                }
                mClickPosition = -1;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void bindKeyScene(int pos) {
        mSceneType = 0;
        mBindScene = mItemSceneList.get(pos);
        JSONObject appParams = mBindScene.getAppParams();
        String key = appParams.getString("key");
        String cId = appParams.getString("cId");
        if (key == null || key.length() == 0) {
            // 此手动场景未绑定按键
            ItemSceneInGateway scene = getNewAutoScene(mBindScene, mKeyCode);
            ViseLog.d("新自动场景 = " + GsonUtil.toJson(scene));
            addScene(this, scene);
        } else {
            boolean isContains = false;
            String[] keys = key.split(",");
            for (String key1 : keys) {
                if (mKeyCode.equals(key1)) {
                    isContains = true;
                    break;
                }
            }
            if (isContains) {
                // 此手动场景已绑定此按键
                ToastUtils.showLongToast(this, R.string.bind_scene_success);
            } else {
                // 此手动场景未绑定此按键
                ViseLog.d("新自动场景2 = " + GsonUtil.toJson(getNewAutoScene(mBindScene, mKeyCode)));
                addScene(this, getNewAutoScene(mBindScene, mKeyCode));
            }
        }
    }

    // 按键未绑定过场景，先根据手动场景创建一个自动场景并提交，再更新手动场景并提交
    private void addScene(SwitchLocalSceneListActivity activity, ItemSceneInGateway scene) {
        ViseLog.d("添加新自动场景 = " + GsonUtil.toJson(scene));
        SceneManager.addScene(activity, scene, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // ViseLog.d("创建成功 = " + GsonUtil.toJson(response));
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        String sceneId = response.getString("sceneId");
                        SceneManager.manageSceneService(mGatewayId, sceneId, 1, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        scene.getSceneDetail().setSceneId(sceneId);
                        DeviceBuffer.addScene(sceneId, scene);

                        ItemSceneInGateway tmp = JSONObject.parseObject(GsonUtil.toJson(mBindScene), ItemSceneInGateway.class);

                        StringBuilder cidSB = new StringBuilder();
                        String cId = tmp.getAppParams().getString("cId");
                        if (cId != null && cId.length() > 0) {
                            cidSB.append(cId + "," + sceneId);
                        } else
                            cidSB.append(sceneId);
                        tmp.getAppParams().put("cId", cidSB.toString());

                        StringBuilder KeySB = new StringBuilder();
                        String key = tmp.getAppParams().getString("key");
                        if (key != null && key.length() > 0) {
                            KeySB.append(key + "," + mKeyCode);
                        } else
                            KeySB.append(mKeyCode);
                        tmp.getAppParams().put("key", KeySB.toString());

                        tmp.getSceneDetail().setConditions(new ArrayList<>());
                        tmp.getSceneDetail().setType("1");

                        updateScene(activity, tmp);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                // 添加本地场景失败
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(activity, e.getMessage());
            }
        });
    }

    private void updateScene(SwitchLocalSceneListActivity activity, ItemSceneInGateway scene) {
        ViseLog.d("新手动场景 = " + GsonUtil.toJson(scene));
        SceneManager.updateScene(activity, scene, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        String sceneId = response.getString("sceneId");
                        scene.getSceneDetail().setSceneId(sceneId);
                        DeviceBuffer.addScene(sceneId, scene);
                        SceneManager.manageSceneService(mGatewayId, sceneId, 2, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        ToastUtils.showLongToast(activity, R.string.bind_scene_success);
                        finish();
                    } else {
                        RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                    }
                    RefreshData.refreshHomeSceneListData();
                } else {
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(activity, e.getMessage());
            }
        });
    }

    // 创建新自动创建
    private ItemSceneInGateway getNewAutoScene(ItemSceneInGateway scene, String keyCode) {
        ItemSceneInGateway tmpScene = JSONObject.parseObject(GsonUtil.toJson(scene), ItemSceneInGateway.class);
        JSONObject appParams = tmpScene.getAppParams();
        if (appParams == null) appParams = new JSONObject();
        appParams.put("key", keyCode);
        appParams.put("cId", scene.getSceneDetail().getSceneId());
        tmpScene.getSceneDetail().setSceneId(null);
        tmpScene.getSceneDetail().setType("0");

        List<ItemScene.Condition> list = new ArrayList<>();
        ItemScene.Condition condition = getCondition("03A1", "KeyValue", mKeyCode);
        list.add(condition);
        tmpScene.getSceneDetail().setConditions(list);
        return tmpScene;
    }

    private ItemScene.Condition getCondition(String eventType, String name, String keyCode) {
        ItemScene.Condition condition = new ItemScene.Condition();
        condition.setType("Event");

        ItemScene.ConditionParameter parameter = new ItemScene.ConditionParameter();
        parameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotId));
        parameter.setEndpointId(keyCode);
        parameter.setEventType(eventType);
        parameter.setParameterName(name);
        parameter.setCompareType("==");
        parameter.setCompareValue(keyCode);
        ViseLog.d("parameter = " + GsonUtil.toJson(parameter));

        condition.setParameters(parameter);
        return condition;
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.create_scene_view) {
            SwitchLocalSceneActivity.start(this, mGatewayId, mGatewayMac, mIotId, DeviceBuffer.getDeviceMac(mIotId), mKeyCode, SCENE_LIST_REQUEST_CODE);
        }
    }

    public static void start(Activity activity, String iotId, String gatewayId, String gatewayMac, String keyCode, int requestCode) {
        Intent intent = new Intent(activity, SwitchLocalSceneListActivity.class);
        intent.putExtra("extra", iotId);
        intent.putExtra("keyCode", keyCode);
        intent.putExtra("gateway_id", gatewayId);
        intent.putExtra("gateway_mac", gatewayMac);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Activity activity, String iotId, String gatewayId, String gatewayMac, String keyCode, String sceneId, int requestCode) {
        Intent intent = new Intent(activity, SwitchLocalSceneListActivity.class);
        intent.putExtra("extra", iotId);
        intent.putExtra("keyCode", keyCode);
        intent.putExtra("gateway_id", gatewayId);
        intent.putExtra("gateway_mac", gatewayMac);
        intent.putExtra("scene_id", sceneId);
        activity.startActivityForResult(intent, requestCode);
    }

    // 响应错误处理器
    protected Handler mExtendedPropertyResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
                    logOut();
                    return false;
                }
                createAutoScene();
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCENE_LIST_REQUEST_CODE) {
            if (resultCode == Constant.ADD_LOCAL_SCENE) {
                // 新增场景
                ToastUtils.showLongToast(this, R.string.scenario_created_successfully);
                getList();
            } else if (resultCode == Constant.RESULT_CODE_UPDATE_SCENE) {
                // 编辑场景
                ToastUtils.showLongToast(this, R.string.scene_updated_successfully);
                getList();
            } else if (resultCode == Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY) {
                // 删除场景
                ToastUtils.showLongToast(this, R.string.scene_delete_successfully);
                getList();
            }
        }
    }
}
