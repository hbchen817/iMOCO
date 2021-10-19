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
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
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

        mScene = DeviceBuffer.getScene(mSceneId);
        initView();
        initStatusBar();

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.querySceneList("chengxunfei", mGwMac, "0", Constant.MSG_QUEST_QUERY_SCENE_LIST,
                Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mMyHandler);
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
        mViewBinding.mSceneContentText.setText(getIntent().getStringExtra("sceneName"));

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.mSceneContentText.setOnClickListener(this::onViewClicked);
        mViewBinding.unbind.setOnClickListener(this::onViewClicked);
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
        } else if (id == R.id.mSceneContentText) {
            if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                SwitchLocalSceneListActivity.start(this, mIotId, mGwId, mGwMac, mKeyCode, mSceneId, BIND_SCENE_REQUEST_CODE);
            } else {
                SwitchSceneListActivity.start(this, mIotId, mKeyCode);
            }
        } else if (id == R.id.unbind) {
            if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                updateManualScene(mSceneId, mKeyCode);
            } else {
                mSceneManager.getExtendedProperty(mIotId, mKeyCode,
                        mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
            }
        }
    }

    // 更新手动场景
    private void updateManualScene(String sceneId, String keyCode) {
        ItemSceneInGateway scene = DeviceBuffer.getScene(sceneId);

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
        String autoCId = autoScene.getSceneDetail().getSceneId();
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

        mSceneManager.updateScene("chengxunfei", scene, Constant.MSG_QUEST_UPDATE_SCENE,
                Constant.MSG_QUEST_UPDATE_SCENE_ERROR, mMyHandler);
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
                            activity.mSceneManager.manageSceneService(activity.mGwId, sceneId, 3,
                                    activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);
                            activity.setResult(2);
                            activity.finish();
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(activity, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(activity, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_UPDATE_SCENE: {
                    // 更新本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            activity.mSceneManager.manageSceneService(activity.mGwId, sceneId, 2,
                                    activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);
                            ItemSceneInGateway gwScene = DeviceBuffer.getSceneByCid(sceneId, activity.mKeyCode);
                            activity.mSceneManager.deleteScene("chengxunfei", gwScene, Constant.MSG_QUEST_DELETE_SCENE,
                                    Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mMyHandler);
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(activity, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(activity, message);
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
                    } else {
                        String message = response.getString("message");
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(activity, message);
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
}
