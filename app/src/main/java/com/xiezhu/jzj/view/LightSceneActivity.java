package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.event.ColorLightSceneEvent;
import com.xiezhu.jzj.event.RefreshData;
import com.xiezhu.jzj.model.EProduct;
import com.xiezhu.jzj.model.EScene;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.utility.ToastUtils;
import com.xiezhu.jzj.view.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LightSceneActivity extends BaseActivity {

    @BindView(R.id.sceneName)
    TextView mSceneName;
    @BindView(R.id.addBtn)
    ImageView mAddButton;
    @BindView(R.id.lightnessView)
    LinearLayout mLightnessView;
    @BindView(R.id.temperatureView)
    LinearLayout mTemperatureView;
    @BindView(R.id.lightnessText)
    TextView mLightnessText;
    @BindView(R.id.colorTemperatureText)
    TextView mTemperatureText;
    @BindView(R.id.deleteButton)
    TextView mDeleteButton;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView mRightText;

    private EScene.sceneListItemEntry mScene;
    private SceneManager mSceneManager;
    private String mIotID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_scene);
        ButterKnife.bind(this);
        mRightText.setText("保存");
        EventBus.getDefault().register(this);
        mScene = (EScene.sceneListItemEntry) getIntent().getSerializableExtra("extra");
        mIotID = getIntent().getStringExtra("iotID");
        mSceneManager = new SceneManager(this);
        initView();
    }

    @Subscribe
    public void refreshData(ColorLightSceneEvent event) {
        int value = event.getmValue();
        if (event.getmType() == ColorLightSceneEvent.TYPE.TYPE_LIGHTNESS) {
            mLightnessView.setVisibility(View.VISIBLE);
            mLightnessText.setText(String.valueOf(value));
        } else {
            mTemperatureView.setVisibility(View.VISIBLE);
            mTemperatureText.setText(String.valueOf(value));
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
        nameEt.setText(mSceneName.getText());
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
                    mSceneName.setText(nameStr);
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
            mTitle.setText("创建场景");
            mLightnessView.setVisibility(View.GONE);
            mTemperatureView.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);
        } else {
            //编辑场景
            mTitle.setText("编辑场景");
            mSceneName.setText(mScene.name);
        }
    }

    public static void start(Context context, EScene.sceneListItemEntry item, String iotID) {
        Intent intent = new Intent(context, LightSceneActivity.class);
        intent.putExtra("extra", item);
        intent.putExtra("iotID", iotID);
        context.startActivity(intent);
    }

    private Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_create_success), mSceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mSceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mSceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(LightSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_failed), mSceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    // 处理删除列表数据
                    String sceneId = CloudDataParser.processDeleteSceneResult((String) msg.obj);
                    ToastUtils.showToastCentrally(mActivity, R.string.scene_delete_sucess);
                    finish();
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    @OnClick({R.id.nameLayout, R.id.addBtn, R.id.lightnessView, R.id.temperatureView, R.id.deleteButton, R.id.tv_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nameLayout:
                showDeviceNameDialogEdit();
                break;
            case R.id.addBtn:
                LightActionChoiceActivity.start(this);
                break;
            case R.id.lightnessView:
                ColorTemperatureChoiceActivity.start2(this, 2, Integer.parseInt(mLightnessText.getText().toString()));
                break;
            case R.id.temperatureView:
                ColorTemperatureChoiceActivity.start2(this, 1, Integer.parseInt(mTemperatureText.getText().toString()));
                break;
            case R.id.deleteButton:
                mSceneManager.deleteScene(mScene.id, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                break;
            case R.id.tv_toolbar_right:
                if (mScene == null) {
                    //创建场景
                    EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                            CScene.TYPE_MANUAL, mSceneName.getText().toString(), mIotID);
                    baseInfoEntry.enable = true;
                    List<EScene.responseEntry> parameters = new ArrayList<>();
                    if (mLightnessView.getVisibility() == View.VISIBLE) {
                        EScene.responseEntry entry = new EScene.responseEntry();
                        entry.iotId = mIotID;
                        entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_BRIGHTNESS, "", mLightnessText.getText().toString());
                    }
                    if (mTemperatureView.getVisibility() == View.VISIBLE) {
                        EScene.responseEntry entry = new EScene.responseEntry();
                        entry.iotId = mIotID;
                        entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_COLOR_TEMPERATURE, "", mTemperatureText.getText().toString());
                    }
                    mSceneManager.createCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                } else {
                    EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                            CScene.TYPE_MANUAL, mSceneName.getText().toString(), mIotID);
                    baseInfoEntry.enable = true;
                    baseInfoEntry.sceneId = mScene.id;
                    List<EScene.responseEntry> parameters = new ArrayList<>();
                    if (mLightnessView.getVisibility() == View.VISIBLE) {
                        EScene.responseEntry entry = new EScene.responseEntry();
                        entry.iotId = mIotID;
                        entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_BRIGHTNESS, "", mLightnessText.getText().toString());
                    }
                    if (mTemperatureView.getVisibility() == View.VISIBLE) {
                        EScene.responseEntry entry = new EScene.responseEntry();
                        entry.iotId = mIotID;
                        entry.state = new ETSL.stateEntry("", CTSL.LIGHT_P_COLOR_TEMPERATURE, "", mTemperatureText.getText().toString());
                    }
                    mSceneManager.updateCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                }
                break;
        }
    }


}
