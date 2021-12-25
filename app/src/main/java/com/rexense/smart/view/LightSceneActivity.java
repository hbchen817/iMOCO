package com.rexense.smart.view;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.R;
import com.rexense.smart.contract.CScene;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityLightSceneBinding;
import com.rexense.smart.event.ColorLightSceneEvent;
import com.rexense.smart.event.RefreshData;
import com.rexense.smart.model.EScene;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.presenter.CloudDataParser;
import com.rexense.smart.presenter.SceneManager;
import com.rexense.smart.presenter.SystemParameter;
import com.rexense.smart.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class LightSceneActivity extends BaseActivity implements View.OnLongClickListener {
    private ActivityLightSceneBinding mViewBinding;

    private EScene.sceneListItemEntry mScene;
    private SceneManager mSceneManager;
    private String mIotID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText("保存");
        EventBus.getDefault().register(this);
        mScene = (EScene.sceneListItemEntry) getIntent().getSerializableExtra("extra");
        mIotID = getIntent().getStringExtra("iotID");
        mActivityTag = getIntent().getStringExtra("activity_tag");
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
            mViewBinding.lightnessText.setText(String.valueOf(value));
        } else {
            mViewBinding.temperatureView.setVisibility(View.VISIBLE);
            mViewBinding.colorTemperatureText.setText(String.valueOf(value));
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
        if (mScene == null) {
            //创建场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("创建场景");
            mViewBinding.lightnessView.setVisibility(View.GONE);
            mViewBinding.temperatureView.setVisibility(View.GONE);
            mViewBinding.deleteButton.setVisibility(View.GONE);
        } else {
            //编辑场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("编辑场景");
            mViewBinding.sceneName.setText(mScene.name);
            mSceneManager.querySceneDetail(mScene.id, "0", mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
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

    private String mActivityTag;

    public static void start(Context context, EScene.sceneListItemEntry item, String iotID, String tag) {
        Intent intent = new Intent(context, LightSceneActivity.class);
        intent.putExtra("extra", item);
        intent.putExtra("iotID", iotID);
        intent.putExtra("activity_tag", tag);
        context.startActivity(intent);
    }

    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_create_success), mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mViewBinding.sceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_failed), mViewBinding.sceneName.getText().toString()));
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
            mSceneManager.deleteScene(mScene.id, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else if (view.getId() == R.id.tv_toolbar_right) {
            if (mViewBinding.lightnessView.getVisibility() == View.GONE && mViewBinding.temperatureView.getVisibility() == View.GONE) {
                ToastUtils.showShortToast(this, R.string.pls_add_actions);
            } else if (mScene == null) {
                //创建场景
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, mViewBinding.sceneName.getText().toString(), mIotID);
                baseInfoEntry.enable = true;
                List<EScene.responseEntry> parameters = new ArrayList<>();
                if (mViewBinding.lightnessView.getVisibility() == View.VISIBLE) {
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = mIotID;
                    entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_BRIGHTNESS, "", mViewBinding.lightnessText.getText().toString());
                    parameters.add(entry);
                }
                if (mViewBinding.temperatureView.getVisibility() == View.VISIBLE) {
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = mIotID;
                    entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_COLOR_TEMPERATURE, "", mViewBinding.colorTemperatureText.getText().toString());
                    parameters.add(entry);
                }
                mSceneManager.createCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
            } else {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, mViewBinding.sceneName.getText().toString(), mIotID);
                baseInfoEntry.enable = true;
                baseInfoEntry.sceneId = mScene.id;
                List<EScene.responseEntry> parameters = new ArrayList<>();
                if (mViewBinding.lightnessView.getVisibility() == View.VISIBLE) {
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = mIotID;
                    entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_BRIGHTNESS, "", mViewBinding.lightnessText.getText().toString());
                    parameters.add(entry);
                }
                if (mViewBinding.temperatureView.getVisibility() == View.VISIBLE) {
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = mIotID;
                    entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_COLOR_TEMPERATURE, "", mViewBinding.colorTemperatureText.getText().toString());
                    parameters.add(entry);
                }
                mSceneManager.updateCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
            }
        }
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
