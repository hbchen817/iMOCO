package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class TwoSceneSwitchActivity2 extends DetailActivity {

    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//2
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.key_1_tv)
    TextView mKeyName1TV;
    @BindView(R.id.key_2_tv)
    TextView mKeyName2TV;
    @BindView(R.id.one_go_ic)
    TextView mOneGoIC;
    @BindView(R.id.two_go_ic)
    TextView mTwoGoIC;
    @BindView(R.id.battery_layout)
    LinearLayout mBatteryLayout;
    @BindView(R.id.battery_value_tv)
    TextView mBatteryValueTV;

    private final String LOCAL_SCENE_CALL_BACK = "TwoSceneSwitchActivity2LocalSceneCallback";
    private final int EDIT_LOCAL_SCENE = 10001;
    private final int BIND_SCENE_REQUEST_CODE = 10000;

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[2];
    private String[] mManualNames = new String[2];
    private String mCurrentKey;
    private String mExecuteScene = "";
    private int mBackLightState = 1;
    private TSLHelper mTSLHelper;
    private final int TAG_GET_EXTENDED_PRO = 10000;
    private String mKeyName1;
    private String mKeyName2;
    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;

    private String mGatewayId;
    private String mGatewayMac;
    private ItemSceneInGateway m1Scene;
    private ItemSceneInGateway m2Scene;

    private long mDoubleClickedTime = 0;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }
        QMUITipDialogUtil.dismiss();
        if (propertyEntry.getPropertyValue(CTSL.PTS_BackLight) != null && propertyEntry.getPropertyValue(CTSL.PTS_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.PTS_BackLight));
            switch (mBackLightState) {
                case CTSL.STATUS_OFF: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.gray3));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.gray3));
                    break;
                }
                case CTSL.STATUS_ON: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.blue2));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.blue2));
                    break;
                }
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.PTS_BackLight) != null && propertyEntry.getPropertyValue(CTSL.PTS_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.PTS_BackLight));
            switch (mBackLightState) {
                case CTSL.STATUS_OFF: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.gray3));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.gray3));
                    break;
                }
                case CTSL.STATUS_ON: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.blue2));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.blue2));
                    break;
                }
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.BATTERY) != null && propertyEntry.getPropertyValue(CTSL.BATTERY).length() > 0) {
            int battery = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.BATTERY));
            mBatteryValueTV.setText(String.valueOf(battery));
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mBackLightIc.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);
        mDelSceneHandler = new DelSceneHandler(this);

        EventBus.getDefault().register(this);
        mMyHandler = new MyHandler(this);
        initView();
        // getScenes();

        initStatusBar();
        initKeyNickName();

        if (CTSL.PK_TWO_SCENE_SWITCH.equals(mProductKey)) {
            mBackLightLayout.setVisibility(View.VISIBLE);
            mBatteryLayout.setVisibility(View.GONE);
        } else if (CTSL.PK_ANY_TWO_SCENE_SWITCH.equals(mProductKey) ||
                CTSL.PK_SYT_TWO_SCENE_SWITCH.equals(mProductKey)) {
            mBackLightLayout.setVisibility(View.GONE);
            mBatteryLayout.setVisibility(View.VISIBLE);
        }
    }

    // 获取面板所属网关iotId
    private void getGatewayId(String iotId) {
        SceneManager.getGWIotIdBySubIotId(this, iotId, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    mGatewayId = response.getString("gwIotId");
                    mGatewayMac = DeviceBuffer.getDeviceMac(mGatewayId);
                    querySceneName();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(TwoSceneSwitchActivity2.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                // 根据子设备iotId查询网关iotId
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(TwoSceneSwitchActivity2.this, e.getMessage());
            }
        });
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME,
                TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);
    }

    private static class MyResponseErrHandler extends Handler {
        private final WeakReference<TwoSceneSwitchActivity2> ref;

        public MyResponseErrHandler(TwoSceneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TwoSceneSwitchActivity2 activity = ref.get();
            if (activity == null) return;
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry =
                        (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry :
                            responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(),
                                entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s",
                        responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s",
                        responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {
                    //检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
                    activity.logOut();
                } else if (responseErrorEntry.code == 6741) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1,
                            activity.mKeyName1TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2,
                            activity.mKeyName2TV.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId,
                            Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                            null, null, null);
                }
            }
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appbgcolor));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mSceneManager = new SceneManager(this);
    }

    private void getScenes() {
        mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_1;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler,
                mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @OnClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSwitch1, R.id.mSwitch2,
            R.id.back_light_layout, R.id.one_go_ic, R.id.two_go_ic})
    public void onClickView(View view) {
        int id = view.getId();
        if (id == R.id.one_go_ic ||
                id == R.id.mSceneContentText1 ||
                id == R.id.mSwitch1) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                    if (m1Scene != null) {
                        if ("1".equals(m1Scene.getSceneDetail().getEnable())) {
                            QMUITipDialogUtil.showLoadingDialg(this, R.string.click_scene);
                            SceneManager.invokeLocalSceneService(this, mGatewayId,
                                    m1Scene.getSceneDetail().getSceneId(), null);
                        } else {
                            // 禁用
                            ToastUtils.showLongToast(this, R.string.scene_is_invaild);
                        }
                    } else {
                        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1)
                            SwitchLocalSceneListActivity.start(this, mIOTId, mGatewayId, mGatewayMac,
                                    CTSL.SCENE_SWITCH_KEY_CODE_1, BIND_SCENE_REQUEST_CODE);
                        else {
                            ToastUtils.showLongToast(this, R.string.sharing_dev_does_not_support_edit_scene);
                        }
                    }
                }
                mDoubleClickedTime = System.currentTimeMillis();
            } else {
                if (mManualIDs[0] != null) {
                    mPressedKey = "1";
                    mExecuteScene = mSceneContentText1.getText().toString();
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler,
                            mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1);
                }
            }
        } else if (id == R.id.two_go_ic || id == R.id.mSceneContentText2 ||
                id == R.id.mSwitch2) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                    if (m2Scene != null) {
                        if (m2Scene.getSceneDetail() != null) {
                            if ("1".equals(m2Scene.getSceneDetail().getEnable())) {
                                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_scene);
                                SceneManager.invokeLocalSceneService(this, mGatewayId,
                                        m2Scene.getSceneDetail().getSceneId(), null);
                            } else {
                                // 禁用
                                ToastUtils.showLongToast(this, R.string.scene_is_invaild);
                            }
                        } else {
                            ToastUtils.showLongToast(this, R.string.pls_try_again_later);
                        }
                    } else {
                        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1)
                            SwitchLocalSceneListActivity.start(this, mIOTId, mGatewayId, mGatewayMac,
                                    CTSL.SCENE_SWITCH_KEY_CODE_2, BIND_SCENE_REQUEST_CODE);
                        else {
                            ToastUtils.showLongToast(this, R.string.sharing_dev_does_not_support_edit_scene);
                        }
                    }
                }
                mDoubleClickedTime = System.currentTimeMillis();
            } else {
                if (mManualIDs[1] != null) {
                    mPressedKey = "2";
                    mExecuteScene = mSceneContentText2.getText().toString();
                    mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler,
                            mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2);
                }
            }
        } else if (id == R.id.back_light_layout) {
            // 背光
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_backlight);
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.P2S_BackLight},
                            new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.P2S_BackLight},
                            new String[]{"" + CTSL.STATUS_OFF});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void notifyResponseError(int type) {
        super.notifyResponseError(type);
        if (type == 10360) {
            // scene rule not exist
            mSceneManager.getExtendedProperty(mIOTId, mPressedKey, null, null, mDelSceneHandler);
        }
    }

    private static class DelSceneHandler extends Handler {
        private final WeakReference<TwoSceneSwitchActivity2> ref;

        public DelSceneHandler(TwoSceneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TwoSceneSwitchActivity2 activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    String keyNo = jsonObject.getString("keyNo");
                    if (keyNo != null && keyNo.equals(activity.mPressedKey)) {
                        String autoSceneId = jsonObject.getString("asId");
                        activity.mSceneManager.deleteScene(autoSceneId, null, null, null);
                        activity.mSceneManager.setExtendedProperty(activity.mIOTId,
                                activity.mPressedKey, "{}", null,
                                null, activity.mDelSceneHandler);
                    }
                }
            } else if (msg.what == Constant.MSG_CALLBACK_DELETESCENE) {
                activity.mSceneManager.setExtendedProperty(activity.mIOTId, activity.mPressedKey,
                        "{}", null,
                        null, activity.mDelSceneHandler);
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                activity.getScenes();
            }
        }
    }

    private JSONObject mResultObj;

    // 显示按键名称修改对话框
    private void showKeyNameDialogEdit(int resId) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.key_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setHint(getString(R.string.pls_input_key_name));
        if (resId == R.id.key_1_tv) {
            // 按键1
            nameEt.setText(mKeyName1TV.getText().toString());
        } else if (resId == R.id.key_2_tv) {
            // 按键2
            nameEt.setText(mKeyName2TV.getText().toString());
        }

        nameEt.setSelection(nameEt.getText().toString().length());
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
                if (nameEt.getText().toString().length() > 10
                        && mKeyName1TV.getText().toString().length() > 10
                        && mKeyName2TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(TwoSceneSwitchActivity2.this,
                            R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKeyName1TV.getText().toString().length() == 0
                        && mKeyName2TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(TwoSceneSwitchActivity2.this,
                            R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(TwoSceneSwitchActivity2.this,
                        R.string.is_setting);
                if (resId == R.id.key_1_tv) {
                    // 按键1
                    mKeyName1 = nameEt.getText().toString();
                    mKeyName2 = mKeyName2TV.getText().toString();
                } else if (resId == R.id.key_2_tv) {
                    // 按键2
                    mKeyName1 = mKeyName1TV.getText().toString();
                    mKeyName2 = nameEt.getText().toString();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName1);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKeyName2);
                mResultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME,
                        jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler,
                        mMyHandler);
                dialog.dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public String getExecuteScene() {
        return mExecuteScene;
    }

    @OnLongClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.one_go_ic, R.id.two_go_ic,
            R.id.key_1_tv, R.id.key_2_tv})
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.one_go_ic || view.getId() == R.id.mSceneContentText1) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (m1Scene != null) {
                    if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1)
                        EditLocalSceneBindActivity.start(this, mKeyName1TV.getText().toString(), mIOTId,
                                CTSL.SCENE_SWITCH_KEY_CODE_1,
                                mSceneContentText1.getText().toString(), mGatewayId, mGatewayMac,
                                m1Scene.getSceneDetail().getSceneId(), EDIT_LOCAL_SCENE);
                }
            } else if (mManualIDs[0] != null) {
                EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1,
                        mSceneContentText1.getText().toString());
            }
        } else if (view.getId() == R.id.two_go_ic || view.getId() == R.id.mSceneContentText2) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (m2Scene != null) {
                    EditLocalSceneBindActivity.start(this, mKeyName2TV.getText().toString(), mIOTId,
                            CTSL.SCENE_SWITCH_KEY_CODE_2,
                            mSceneContentText2.getText().toString(), mGatewayId, mGatewayMac,
                            m2Scene.getSceneDetail().getSceneId(), EDIT_LOCAL_SCENE);
                }
            } else if (mManualIDs[1] != null) {
                EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2,
                        mSceneContentText2.getText().toString());
            }
        } else if (view.getId() == R.id.key_1_tv) {
            // 按键1昵称
            showKeyNameDialogEdit(R.id.key_1_tv);
        } else if (view.getId() == R.id.key_2_tv) {
            // 按键2昵称
            showKeyNameDialogEdit(R.id.key_2_tv);
        }
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<TwoSceneSwitchActivity2> mWeakReference;

        public MyHandler(TwoSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TwoSceneSwitchActivity2 activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR: {
                    // 根据子设备iotId查询网关iotId
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e);
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID: {
                    // 根据子设备iotId查询网关iotId
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String gwId = response.getString("gwIotId");
                    if (code == 200) {
                        activity.mGatewayId = gwId;
                        activity.mGatewayMac = DeviceBuffer.getDeviceMac(activity.mGatewayId);
                        activity.querySceneName();
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        switch (activity.mCurrentKey) {
                            case CTSL.SCENE_SWITCH_KEY_CODE_1:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText1.setText(jsonObject.getString(
                                            "name"));
                                    activity.mManualNames[0] = jsonObject.getString("name");
                                    activity.mManualIDs[0] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText1.setText(R.string.no_bind_scene);
                                    activity.mManualNames[0] = null;
                                    activity.mManualIDs[0] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_2;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId,
                                        activity.mCurrentKey,
                                        activity.mCommitFailureHandler,
                                        activity.mExtendedPropertyResponseErrorHandler,
                                        activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_2:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText2.setText(jsonObject.getString(
                                            "name"));
                                    activity.mManualNames[1] = jsonObject.getString("name");
                                    activity.mManualIDs[1] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText2.setText(R.string.no_bind_scene);
                                    activity.mManualNames[1] = null;
                                    activity.mManualIDs[1] = null;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    ToastUtils.showShortToast(activity,
                            String.format(activity.getString(R.string.main_scene_execute_hint_2),
                                    activity.getExecuteScene()));
                    //Toast.makeText(activity, String.format(activity.getString(R.string
                    // .main_scene_execute_hint)
//                            , sceneId.equals(activity.mFirstManualSceneId) ? activity
//                            .mFirstManualSceneName : activity.mSecondManualSceneName), Toast
//                            .LENGTH_LONG).show();
                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    activity.mKeyName1TV.setText(activity.mKeyName1);
                    activity.mKeyName2TV.setText(activity.mKeyName2);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.mResultObj);
                    RefreshData.refreshDeviceKeyName();
                    ToastUtils.showShortToast(activity, R.string.set_success);
                    break;
                }
                default:
                    break;
            }
            if (msg.what == activity.TAG_GET_EXTENDED_PRO) {
                // 获取按键昵称
                JSONObject object = JSONObject.parseObject((String) msg.obj);
                DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
                activity.mKeyName1TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_1));
                activity.mKeyName2TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_2));
            }
        }
    }

    // 响应错误处理器
    protected Handler mExtendedPropertyResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry =
                        (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry :
                            responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(),
                                entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s",
                        responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s",
                        responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {
                    //检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
                    logOut();
                    return false;
                }
            }
            return false;
        }
    });

    private void initSceneView() {
        mSceneContentText1.setText(R.string.no_bind_scene);
        mSceneContentText2.setText(R.string.no_bind_scene);

        m1Scene = null;
        m2Scene = null;
    }

    // 获取按键绑定场景的名称
    private void querySceneName() {
        initSceneView();
        ViseLog.d("场景缓存 = \n" + GsonUtil.toJson(DeviceBuffer.getAllScene()));
        for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
            if (scene.getAppParams() == null) continue;
            String switchIotId = scene.getAppParams().getString("switchIotId");
            if (switchIotId == null || switchIotId.length() == 0) {
                continue;
            } else if (!switchIotId.contains(mIOTId)) continue;
            String key = scene.getAppParams().getString("key");
            if (key == null) continue;
            if (key.contains(CTSL.SCENE_SWITCH_KEY_CODE_1)) {
                mSceneContentText1.setText(scene.getSceneDetail().getName());
                m1Scene = scene;
            }
            if (key.contains(CTSL.SCENE_SWITCH_KEY_CODE_2)) {
                mSceneContentText2.setText(scene.getSceneDetail().getName());
                m2Scene = scene;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addEventCallbackHandler();
        if (mGatewayMac == null || mGatewayMac.length() == 0)
            getGatewayId(mIOTId);
        else querySceneName();
    }

    private void addEventCallbackHandler() {
        RealtimeDataReceiver.addEventCallbackHandler(LOCAL_SCENE_CALL_BACK, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                    // 处理触发手动场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("InvokeLocalSceneNotification".equals(identifier)) {
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        QMUITipDialogUtil.dismiss();
                        if ("0".equals(status)) {
                            String name = DeviceBuffer.getScene(value.getString("SceneId")).getSceneDetail().getName();
                            if (name != null && name.length() > 0) {
                                String tip = String.format(getString(R.string.main_scene_execute_hint_2), name);
                                ToastUtils.showLongToast(TwoSceneSwitchActivity2.this, tip);
                            } else {
                                ToastUtils.showLongToast(TwoSceneSwitchActivity2.this, R.string.perform_scene);
                            }
                        } else {
                            ToastUtils.showLongToast(TwoSceneSwitchActivity2.this, R.string.scene_do_fail);
                        }
                    }
                }
                return false;
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCAL_SCENE) {
            if (resultCode == 2) {
                ToastUtils.showLongToast(this, R.string.unbind_scene_success);
                initSceneView();
            }
        } else if (requestCode == BIND_SCENE_REQUEST_CODE) {
            if (resultCode == 2)
                ToastUtils.showLongToast(this, R.string.bind_scene_success);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        QMUITipDialogUtil.dismiss();
        RealtimeDataReceiver.deleteCallbackHandler(LOCAL_SCENE_CALL_BACK);
    }
}

