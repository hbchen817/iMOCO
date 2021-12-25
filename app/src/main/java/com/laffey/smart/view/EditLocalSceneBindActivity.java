package com.laffey.smart.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityEditSceneBindBinding;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

public class EditLocalSceneBindActivity extends BaseActivity {
    private ActivityEditSceneBindBinding mViewBinding;

    private String mIotId;
    private String mKeyCode;
    private SceneManager mSceneManager;
    private MyHandler mMyHandler;

    private String mGwId;
    private String mGwMac;
    private String mSceneId;
    private ItemSceneInGateway mScene;
    private ItemSceneInGateway mUpdateScene;

    private final int BIND_SCENE_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityEditSceneBindBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mSceneManager = new SceneManager(this);
        mMyHandler = new MyHandler(this);
        mIotId = getIntent().getStringExtra("iotId");
        mKeyCode = getIntent().getStringExtra("keyCode");
        mGwId = getIntent().getStringExtra("gwId");
        mGwMac = getIntent().getStringExtra("gwMac");
        mSceneId = getIntent().getStringExtra("sceneId");

        initStatusBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*RealtimeDataReceiver.addEventCallbackHandler("EditLocalSceneBindCallback", mMyHandler);*/

        mScene = null;
        for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
            if ("1".equals(scene.getSceneDetail().getType())) {
                JSONObject appParams = scene.getAppParams();
                if (appParams != null &&
                        appParams.getString("switchIotId") != null &&
                        appParams.getString("switchIotId").contains(mIotId)) {
                    ViseLog.d("mIotId = " + mIotId +
                            "\nscene = \n" + GsonUtil.toJson(scene));
                    String key = appParams.getString("key");
                    ViseLog.d("key = " + key + "\nmKeyCode = " + mKeyCode);
                    if (key != null && key.contains(mKeyCode)) {
                        mScene = JSONObject.parseObject(GsonUtil.toJson(scene), ItemSceneInGateway.class);
                        mSceneId = mScene.getSceneDetail().getSceneId();
                    }
                }
            }
        }
        initView();
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
    public void refreshSceneName(SceneBindEvent event) {
        mViewBinding.mSceneContentText.setText(event.sceneName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        String title = getIntent().getStringExtra("title");
        mViewBinding.includeToolbar.tvToolbarTitle.setText(title + "绑定场景");
        ViseLog.d("mScene = \n" + GsonUtil.toJson(mScene));
        if (mScene != null && mScene.getSceneDetail() != null && mScene.getSceneDetail().getName() != null &&
                mScene.getSceneDetail().getName().length() > 0) {
            mViewBinding.mSceneContentText.setText(mScene.getSceneDetail().getName());
        } else {
            finish();
        }

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.mSceneContentText.setOnClickListener(this::onViewClicked);
        mViewBinding.unbind.setOnClickListener(this::onViewClicked);
        mViewBinding.goTv.setOnClickListener(this::onViewClicked);
    }

    public static void start(Activity activity, String title, String iotId, String keyCode, String sceneName, String gwId, String gwMac, String sceneId, int requestCode) {
        Intent intent = new Intent(activity, EditLocalSceneBindActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("iotId", iotId);
        intent.putExtra("keyCode", keyCode);
        intent.putExtra("sceneName", sceneName);

        intent.putExtra("gwId", gwId);
        intent.putExtra("gwMac", gwMac);
        intent.putExtra("sceneId", sceneId);
        activity.startActivityForResult(intent, requestCode);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.mSceneContentText ||
                id == R.id.go_tv) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                SwitchLocalSceneListActivity.start(this, mIotId, mGwId, mGwMac, mKeyCode, mSceneId, BIND_SCENE_REQUEST_CODE);
            } else {
                SwitchSceneListActivity.start(this, mIotId, mKeyCode);
            }
        } else if (id == R.id.unbind) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                EDevice.deviceEntry gwDev = DeviceBuffer.getDeviceInformation(mGwId);
                if (gwDev.status == Constant.CONNECTION_STATUS_OFFLINE) {
                    // 网关离线
                    ToastUtils.showLongToast(this, R.string.gw_is_offline_cannot_unbind_scene);
                    return;
                }
                showUnbindDialog();
            } else {
                mSceneManager.getExtendedProperty(mIotId, mKeyCode,
                        mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
            }
        }
    }

    private void showUnbindDialog() {
        DialogUtils.showConfirmDialog(this, R.string.dialog_title, R.string.do_you_want_unbind_scene,
                R.string.dialog_confirm, R.string.dialog_cancel, new DialogUtils.Callback() {
                    @Override
                    public void positive() {
                        QMUITipDialogUtil.showLoadingDialg(EditLocalSceneBindActivity.this, R.string.is_submitted);
                        updateManualScene(mSceneId, mKeyCode);
                    }

                    @Override
                    public void negative() {

                    }
                });
    }

    // 更新手动场景
    private void updateManualScene(String sceneId, String keyCode) {
        ItemSceneInGateway scene = DeviceBuffer.getScene(sceneId);
        ViseLog.d("需要更新的场景前 = " + GsonUtil.toJson(scene));

        // 去掉手动场景key中包含的mKeyCode
        String key = scene.getAppParams().getString("key");
        StringBuilder keySB = new StringBuilder();
        String[] keys = key.split(",");
        for (String key1 : keys) {
            if (!key1.equals(mKeyCode)) {
                if (keySB.toString().length() != 0) {
                    keySB.append(",");
                }
                keySB.append(key1);
            }
        }
        if (keySB.toString().length() == 0) {
            scene.getAppParams().remove("key");
        } else {
            scene.getAppParams().put("key", keySB.toString());
        }

        // 去掉手动场景cId中包含的自动场景id
        ItemSceneInGateway autoScene = DeviceBuffer.getSceneByCid(sceneId, keyCode);
        String autoCId = "";
        if (autoScene != null)
            autoCId = autoScene.getSceneDetail().getSceneId();
        String cId = scene.getAppParams().getString("cId");
        StringBuilder cIdSB = new StringBuilder();
        String[] cIds = cId.split(",");
        for (String cId1 : cIds) {
            if (!cId1.equals(autoCId)) {
                if (cIdSB.toString().length() != 0) {
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

        mUpdateScene = JSONObject.parseObject(GsonUtil.toJson(scene), ItemSceneInGateway.class);
        scene.getSceneDetail().setConditions(new ArrayList<>());
        updateScene(scene);
        ViseLog.d("需要更新的场景后 = " + GsonUtil.toJson(scene));
    }

    private void updateScene(ItemSceneInGateway scene) {
        SceneManager.updateScene(this, scene, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 更新本地场景
                ViseLog.d("更新本地场景 = " + response.toJSONString());
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        String sceneId = response.getString("sceneId");
                        SceneManager.manageSceneService(mGwId, sceneId, 2,
                                mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                        DeviceBuffer.addScene(sceneId, scene);

                        // 获取自动场景
                        ItemSceneInGateway scene = DeviceBuffer.getSceneByCid(sceneId, mKeyCode);
                        if (scene != null) {
                            ViseLog.d("网关上报后删除云端场景 sceneId = " + scene.getSceneDetail().getSceneId());
                            // 删除自动场景
                            deleteScene(scene.getGwMac(), scene.getSceneDetail().getSceneId());
                        } else {
                            ToastUtils.showLongToast(EditLocalSceneBindActivity.this, R.string.unbind_scene_success);
                            setResult(2);
                            finish();
                        }
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(EditLocalSceneBindActivity.this, response);
                    }
                } else {
                    RetrofitUtil.showErrorMsg(EditLocalSceneBindActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(EditLocalSceneBindActivity.this, e.getMessage());
            }
        });
    }

    private void deleteScene(String gwMac, String sceneId) {
        SceneManager.deleteScene(this, gwMac, sceneId, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 删除本地场景
                QMUITipDialogUtil.dismiss();
                ViseLog.d("删除自动场景 = " + response.toJSONString());
                int code = response.getInteger("code");
                if (code == 200) {
                    boolean result = response.getBoolean("result");
                    if (result) {
                        DeviceBuffer.addScene(mUpdateScene.getSceneDetail().getSceneId(), mUpdateScene);

                        ViseLog.d("需要删除的自动场景id = " + sceneId);
                        SceneManager.manageSceneService(mGwId, sceneId, 3,
                                mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                        DeviceBuffer.removeScene(sceneId);

                        ToastUtils.showLongToast(EditLocalSceneBindActivity.this, R.string.unbind_scene_success);
                        setResult(2);
                        finish();
                    } else {
                        RetrofitUtil.showErrorMsg(EditLocalSceneBindActivity.this, response);
                    }
                } else {
                    RetrofitUtil.showErrorMsg(EditLocalSceneBindActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(EditLocalSceneBindActivity.this, e.getMessage());
            }
        });
    }

    private static class MyHandler extends Handler {
        final WeakReference<EditLocalSceneBindActivity> mWeakReference;

        public MyHandler(EditLocalSceneBindActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EditLocalSceneBindActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                /*case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                    // 删除网关下的场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    ViseLog.d("网关返回删除结果 LocalSceneActivity = " + jsonObject.toJSONString());
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("ManageSceneNotification".equals(identifier)) {
                        String type = value.getString("Type");
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        if ("0".equals(status)) {
                            // type  1: 增加场景  2: 编辑场景  3: 删除场景
                            if ("3".equals(type)) {
                                ItemSceneInGateway scene = DeviceBuffer.getSceneByCid(activity.mSceneId, activity.mKeyCode);
                                ViseLog.d("网关上报后删除云端场景 sceneId = " + scene.getSceneDetail().getSceneId());
                                activity.mSceneManager.deleteScene(activity, scene.getGwMac(), scene.getSceneDetail().getSceneId(),
                                        Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mMyHandler);
                            }
                        } else {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        }
                    }
                    break;
                }*/
                case Constant.MSG_QUEST_DELETE_SCENE: {
                    // 删除本地场景
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("删除自动场景 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            DeviceBuffer.addScene(activity.mUpdateScene.getSceneDetail().getSceneId(), activity.mUpdateScene);

                            ViseLog.d("需要删除的自动场景id = " + sceneId);
                            SceneManager.manageSceneService(activity.mGwId, sceneId, 3,
                                    activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);
                            DeviceBuffer.removeScene(sceneId);

                            ToastUtils.showLongToast(activity, R.string.unbind_scene_success);
                            activity.setResult(2);
                            activity.finish();
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response);
                        }
                    } else {
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_UPDATE_SCENE: {
                    // 更新本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("更新本地场景 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            SceneManager.manageSceneService(activity.mGwId, sceneId, 2,
                                    activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);

                            ItemSceneInGateway scene = DeviceBuffer.getSceneByCid(sceneId, activity.mKeyCode);
                            if (scene != null) {
                                ViseLog.d("网关上报后删除云端场景 sceneId = " + scene.getSceneDetail().getSceneId());
                                activity.mSceneManager.deleteScene(activity, scene.getGwMac(), scene.getSceneDetail().getSceneId(),
                                        Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mMyHandler);
                            } else {
                                ToastUtils.showLongToast(activity, R.string.unbind_scene_success);
                                activity.setResult(2);
                                activity.finish();
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            RetrofitUtil.showErrorMsg(activity, response);
                        }
                    } else {
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_DELETE_SCENE_ERROR: // 删除本地场景失败
                case Constant.MSG_QUEST_UPDATE_SCENE_ERROR:// 更新本地场景错误
                case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR: {
                    // 获取本地场景列表错误
                    Throwable e = (Throwable) msg.obj;
                    if (e != null) {
                        ToastUtils.showLongToast(activity, e.getMessage());
                    } else
                        ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                    break;
                }
                case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                    // 获取本地场景列表
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    if (code == 200) {
                        JSONArray list = response.getJSONArray("sceneList");
                        for (int i = 0; i < list.size(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            ItemSceneInGateway scene = JSONObject.toJavaObject(item, ItemSceneInGateway.class);
                            DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                        }
                        activity.mScene = DeviceBuffer.getScene(activity.mSceneId);
                    } else {
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        String keyNo = jsonObject.getString("keyNo");
                        if (keyNo != null && keyNo.equals(activity.mKeyCode)) {
                            String autoSceneId = jsonObject.getString("asId");
                            activity.mSceneManager.deleteScene(autoSceneId, activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);
                            activity.mSceneManager.setExtendedProperty(activity.mIotId, activity.mKeyCode, "{}", activity.mCommitFailureHandler
                                    , activity.mResponseErrorHandler, activity.mMyHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    //处理删除场景

                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET:
                    //处理设置数据
                    ToastUtils.showToastCentrally(activity, "解绑成功");
                    EventBus.getDefault().post(new SceneBindEvent(""));
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
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
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BIND_SCENE_REQUEST_CODE) {
            if (resultCode == 2) {
                mIotId = getIntent().getStringExtra("iotId");
                mKeyCode = getIntent().getStringExtra("keyCode");
                mGwId = getIntent().getStringExtra("gwId");
                mGwMac = getIntent().getStringExtra("gwMac");
                mSceneId = getIntent().getStringExtra("sceneId");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*RealtimeDataReceiver.deleteCallbackHandler("EditLocalSceneBindCallback");*/
    }
}
