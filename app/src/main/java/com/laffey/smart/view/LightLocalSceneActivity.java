package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLightSceneBinding;
import com.laffey.smart.event.ColorLightSceneEvent;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class LightLocalSceneActivity extends BaseActivity implements View.OnLongClickListener {
    private ActivityLightSceneBinding mViewBinding;

    private ItemSceneInGateway mScene;
    private SceneManager mSceneManager;
    private String mIotID;
    private String mGwId;
    private String mGwMac;
    private String mSceneId;
    private String mActivityTag;
    private String mProductKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText("保存");
        EventBus.getDefault().register(this);
        mIotID = getIntent().getStringExtra("iotID");
        mActivityTag = getIntent().getStringExtra("activity_tag");
        mGwId = getIntent().getStringExtra("gw_id");
        mGwMac = getIntent().getStringExtra("gw_mac");
        mSceneId = getIntent().getStringExtra("scene_id");
        mProductKey = DeviceBuffer.getDeviceInformation(mIotID).productKey;

        if (mSceneId != null && mSceneId.length() > 0) {
            mScene = DeviceBuffer.getScene(mSceneId);
        }

        mSceneManager = new SceneManager(this);
        initView();

        initStatusBar();

        Typeface iconface = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.lightnessIc.setTypeface(iconface);
        mViewBinding.tempIc.setTypeface(iconface);
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
    public void refreshData(ColorLightSceneEvent event) {
        int value = event.getmValue();
        if (event.getmType() == ColorLightSceneEvent.TYPE.TYPE_LIGHTNESS) {
            mViewBinding.lightnessView.setVisibility(View.VISIBLE);
            mViewBinding.lightnessIc.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mViewBinding.lightnessText.setText(String.valueOf(value));
            mViewBinding.lightnessText.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mViewBinding.lightnessUnit.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        } else {
            mViewBinding.temperatureView.setVisibility(View.VISIBLE);
            mViewBinding.tempIc.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mViewBinding.colorTemperatureText.setText(String.valueOf(value));
            mViewBinding.colorTemperatureText.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mViewBinding.colorTemperatureUnit.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        }
    }

    // 显示设备名称修改对话框
    private void showDeviceNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText("输入场景名称");
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(mViewBinding.sceneName.getText());
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
                    mViewBinding.sceneName.setText(nameStr);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        if (mSceneId == null) {
            //创建场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("创建场景");
            mViewBinding.lightnessView.setVisibility(View.GONE);
            mViewBinding.temperatureView.setVisibility(View.GONE);
            mViewBinding.deleteButton.setVisibility(View.GONE);
        } else {
            //编辑场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("编辑场景");
            mViewBinding.sceneName.setText(mScene.getSceneDetail().getName());

            initActionView();
        }
        mViewBinding.nameLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.addBtn.setOnClickListener(this::onViewClicked);
        mViewBinding.lightnessView.setOnClickListener(this::onViewClicked);
        mViewBinding.temperatureView.setOnClickListener(this::onViewClicked);
        mViewBinding.deleteButton.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);

        mViewBinding.lightnessView.setOnLongClickListener(this);
        mViewBinding.temperatureView.setOnLongClickListener(this);
    }

    private void initActionView() {
        if (CTSL.PK_LIGHT.equals(mProductKey)) {
            List<ItemScene.Action> actionList = mScene.getSceneDetail().getActions();
            for (ItemScene.Action action : actionList) {
                JSONObject command = action.getParameters().getCommand();
                String level = command.getString(CTSL.PK_LIGHT_BRIGHTNESS_PARAM);// 亮度
                String temperature = command.getString(CTSL.PK_LIGHT_COLOR_TEMP_PARAM);// 色温
                if (level != null && level.length() > 0) {
                    mViewBinding.lightnessView.setVisibility(View.VISIBLE);
                    mViewBinding.lightnessIc.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                    mViewBinding.lightnessText.setText(command.getString(CTSL.PK_LIGHT_BRIGHTNESS_PARAM));
                    mViewBinding.lightnessText.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                    mViewBinding.lightnessUnit.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                }
                if (temperature != null && temperature.length() > 0) {
                    mViewBinding.temperatureView.setVisibility(View.VISIBLE);
                    mViewBinding.tempIc.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                    mViewBinding.colorTemperatureText.setText(command.getString(CTSL.PK_LIGHT_COLOR_TEMP_PARAM));
                    mViewBinding.colorTemperatureText.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                    mViewBinding.colorTemperatureUnit.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                }
            }
        }
    }

    public static void start(Activity activity, EScene.sceneListItemEntry item, String iotID, String gwId, String gwMac, String tag, String sceneId, int requestCode) {
        Intent intent = new Intent(activity, LightLocalSceneActivity.class);
        intent.putExtra("extra", item);
        intent.putExtra("iotID", iotID);
        intent.putExtra("activity_tag", tag);
        intent.putExtra("gw_id", gwId);
        intent.putExtra("gw_mac", gwMac);
        intent.putExtra("gw_mac", gwMac);
        intent.putExtra("scene_id", sceneId);
        activity.startActivityForResult(intent, requestCode);
    }

    private final Handler processDataHandler = new Handler(new Handler.Callback() {
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
                            mSceneManager.manageSceneService(mGwId, sceneId, 3, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            RefreshData.refreshHomeSceneListData();
                            setResult(10003);
                            finish();
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_UPDATE_SCENE: {
                    // 更新本地场景
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String sceneId = response.getString("sceneId");
                            mSceneManager.manageSceneService(mGwId, sceneId, 2, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            RefreshData.refreshHomeSceneListData();
                            setResult(10002);
                            finish();
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_ADD_SCENE: {
                    // 添加本地场景
                    JSONObject response = (JSONObject) msg.obj;
                    QMUITipDialogUtil.dismiss();
                    int code = response.getInteger("code");
                    String sceneId = response.getString("sceneId");
                    String message = response.getString("message");
                    if (code == 200) {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            QMUITipDialogUtil.dismiss();
                            mSceneManager.manageSceneService(mGwId, sceneId, 1, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            RefreshData.refreshHomeSceneListData();
                            setResult(10001);
                            finish();
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                        }
                    } else {
                        if (message == null || message.length() == 0) {
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, R.string.pls_try_again_later);
                        } else
                            ToastUtils.showLongToast(LightLocalSceneActivity.this, message);
                    }
                    break;
                }
                case Constant.MSG_QUEST_DELETE_SCENE_ERROR:
                case Constant.MSG_QUEST_UPDATE_SCENE_ERROR:
                case Constant.MSG_QUEST_ADD_SCENE_ERROR: {
                    // 添加本地场景失败
                    Throwable e = (Throwable) msg.obj;
                    QMUITipDialogUtil.dismiss();
                    ToastUtils.showLongToast(LightLocalSceneActivity.this, e.getMessage());
                    break;
                }
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        ToastUtils.showToastCentrally(LightLocalSceneActivity.this, String.format(getString(R.string.scene_maintain_create_success), mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightLocalSceneActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mViewBinding.sceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        ToastUtils.showToastCentrally(LightLocalSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightLocalSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_failed), mViewBinding.sceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    // 处理删除列表数据
                    //String sceneId = CloudDataParser.processDeleteSceneResult((String) msg.obj);
                    ToastUtils.showToastCentrally(mActivity, R.string.scene_delete_sucess);
                    finish();
                    RefreshData.refreshSceneListData();
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详情
                    JSONObject result = JSON.parseObject((String) msg.obj);
                    JSONArray actionsJson = result.getJSONArray("actionsJson");
                    for (int j = 0; j < actionsJson.size(); j++) {
                        JSONObject jsonObject = JSON.parseObject(actionsJson.getString(j));
                        JSONObject params = jsonObject.getJSONObject("params");
                        mIotID = params.getString("iotId");
                        if (params.getString("propertyName").equalsIgnoreCase(CTSL.LIGHT_P_BRIGHTNESS)) {
                            mViewBinding.lightnessView.setVisibility(View.VISIBLE);
                            mViewBinding.lightnessText.setText(params.getString("propertyValue"));
                        } else {
                            mViewBinding.temperatureView.setVisibility(View.VISIBLE);
                            mViewBinding.colorTemperatureText.setText(params.getString("propertyValue"));
                        }
                    }
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    public void onViewClicked(View view) {
        if (view.getId() == R.id.nameLayout) {
            showDeviceNameDialogEdit();
        } else if (view.getId() == R.id.addBtn) {
            LightActionChoiceActivity.start(this, mActivityTag);
        } else if (view.getId() == R.id.lightnessView) {
            ColorTemperatureChoiceActivity.start2(this, 2, Integer.parseInt(mViewBinding.lightnessText.getText().toString()));
        } else if (view.getId() == R.id.temperatureView) {
            ColorTemperatureChoiceActivity.start2(this, 1, Integer.parseInt(mViewBinding.colorTemperatureText.getText().toString()));
        } else if (view.getId() == R.id.deleteButton) {
            // 删除场景
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_deleting_scene);
            mSceneManager.deleteScene(this, mGwMac, mSceneId, Constant.MSG_QUEST_DELETE_SCENE,
                    Constant.MSG_QUEST_DELETE_SCENE_ERROR, processDataHandler);
        } else if (view.getId() == R.id.tv_toolbar_right) {
            if (mViewBinding.lightnessView.getVisibility() == View.GONE && mViewBinding.temperatureView.getVisibility() == View.GONE) {
                ToastUtils.showShortToast(this, R.string.pls_add_actions);
            } else if (mSceneId == null) {
                //创建场景
                ItemSceneInGateway scene = createItemSceneInGateway(mViewBinding.sceneName.getText().toString(),
                        mIotID, mGwMac);
                ViseLog.d("生成的场景信息 = " + GsonUtil.toJson(scene));
                mSceneManager.addScene(this, scene, Constant.MSG_QUEST_ADD_SCENE, Constant.MSG_QUEST_ADD_SCENE_ERROR, processDataHandler);
            } else {
                // 编辑本地场景
                QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
                ItemSceneInGateway scene = editItemSceneInGateway(mScene, mViewBinding.sceneName.getText().toString());
                ViseLog.d("编辑的场景信息 = " + GsonUtil.toJson(scene));
                mSceneManager.updateScene(this, scene,
                        Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, processDataHandler);
            }
        }
    }

    // 编辑本地场景
    private ItemSceneInGateway editItemSceneInGateway(ItemSceneInGateway scene, String name) {
        scene.getSceneDetail().setName(name);

        List<ItemScene.Action> actionList = new ArrayList<>();
        if (mViewBinding.lightnessView.getVisibility() == View.VISIBLE) {
            if (CTSL.PK_LIGHT.equals(DeviceBuffer.getDeviceInformation(mIotID).productKey)) {
                ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotID));
                actionParameter.setEndpointId(CTSL.PK_LIGHT_BRIGHTNESS_ENDPOINTID);
                actionParameter.setCommandType(CTSL.PK_LIGHT_BRIGHTNESS_COMMAND_TYPE);

                JSONObject command = new JSONObject();
                command.put(CTSL.PK_LIGHT_BRIGHTNESS_PARAM, mViewBinding.lightnessText.getText().toString());
                actionParameter.setCommand(command);

                ItemScene.Action action = new ItemScene.Action();
                action.setType("Command");
                action.setParameters(actionParameter);
                actionList.add(action);
            }
        }
        if (mViewBinding.temperatureView.getVisibility() == View.VISIBLE) {
            if (CTSL.PK_LIGHT.equals(DeviceBuffer.getDeviceInformation(mIotID).productKey)) {
                ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotID));
                actionParameter.setEndpointId(CTSL.PK_LIGHT_COLOR_TEMP_ENDPOINTID);
                actionParameter.setCommandType(CTSL.PK_LIGHT_COLOR_TEMP_COMMAND_TYPE);

                JSONObject command = new JSONObject();
                command.put(CTSL.PK_LIGHT_COLOR_TEMP_PARAM, mViewBinding.colorTemperatureText.getText().toString());
                actionParameter.setCommand(command);

                ItemScene.Action action = new ItemScene.Action();
                action.setType("Command");
                action.setParameters(actionParameter);
                ViseLog.d("色温动作 = " + GsonUtil.toJson(action));
                actionList.add(action);
            }
        }

        scene.getSceneDetail().setActions(actionList);
        return scene;
    }

    // 新建本地场景对象
    private ItemSceneInGateway createItemSceneInGateway(String name, String devId, String gwMac) {
        ItemSceneInGateway scene = new ItemSceneInGateway();
        JSONObject appParams = new JSONObject();
        appParams.put("type", "e");
        appParams.put("switchIotId", devId);

        scene.setAppParams(appParams);
        scene.setGwMac(gwMac);

        ItemScene itemScene = new ItemScene();
        itemScene.setConditionMode("Any");
        itemScene.setEnable("1");
        itemScene.setName(name);
        itemScene.setTime(new ItemScene.Timer());
        itemScene.setType("1");

        List<ItemScene.Action> actionList = new ArrayList<>();
        if (mViewBinding.lightnessView.getVisibility() == View.VISIBLE) {
            if (CTSL.PK_LIGHT.equals(DeviceBuffer.getDeviceInformation(mIotID).productKey)) {
                ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotID));
                actionParameter.setEndpointId(CTSL.PK_LIGHT_BRIGHTNESS_ENDPOINTID);
                actionParameter.setCommandType(CTSL.PK_LIGHT_BRIGHTNESS_COMMAND_TYPE);

                JSONObject command = new JSONObject();
                command.put(CTSL.PK_LIGHT_BRIGHTNESS_PARAM, mViewBinding.lightnessText.getText().toString());
                actionParameter.setCommand(command);

                ItemScene.Action action = new ItemScene.Action();
                action.setType("Command");
                action.setParameters(actionParameter);
                actionList.add(action);
            }
        }
        if (mViewBinding.temperatureView.getVisibility() == View.VISIBLE) {
            if (CTSL.PK_LIGHT.equals(DeviceBuffer.getDeviceInformation(mIotID).productKey)) {
                ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotID));
                actionParameter.setEndpointId(CTSL.PK_LIGHT_COLOR_TEMP_ENDPOINTID);
                actionParameter.setCommandType(CTSL.PK_LIGHT_COLOR_TEMP_COMMAND_TYPE);

                JSONObject command = new JSONObject();
                command.put(CTSL.PK_LIGHT_COLOR_TEMP_PARAM, mViewBinding.colorTemperatureText.getText().toString());
                actionParameter.setCommand(command);

                ItemScene.Action action = new ItemScene.Action();
                action.setType("Command");
                action.setParameters(actionParameter);
                actionList.add(action);
            }
        }

        itemScene.setActions(actionList);

        scene.setSceneDetail(itemScene);
        return scene;
    }

    @Override
    public boolean onLongClick(View v) {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(this).create();
        alert.setIcon(R.drawable.dialog_quest);
        alert.setTitle(R.string.dialog_title);
        alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_action));
        //添加否按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //添加是按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (v.getId() == mViewBinding.lightnessView.getId()) {
                    mViewBinding.lightnessView.setVisibility(View.GONE);
                } else if (v.getId() == mViewBinding.temperatureView.getId()) {
                    mViewBinding.temperatureView.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
        alert.show();
        return false;
    }
}
