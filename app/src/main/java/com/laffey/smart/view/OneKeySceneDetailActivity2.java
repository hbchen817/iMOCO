package com.laffey.smart.view;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
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

/**
 * @author Gary
 * @time 2020/9/27 13:51
 */

public class OneKeySceneDetailActivity2 extends DetailActivity {
    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.key_name_tv)
    TextView mKeyNameTV;
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.go_ic)
    TextView mGoIC;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;

    private final int EDIT_LOCAL_SCENE = 10001;
    private final int BIND_SCENE_REQUEST_CODE = 10000;

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[1];
    private String[] mManualNames = new String[1];
    private String mCurrentKey;
    private String mExecuteScene = "";
    private int mBackLightState = 1;
    private TSLHelper mTSLHelper;
    private final int TAG_GET_EXTENDED_PRO = 10000;
    private String mKeyName;

    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;

    private final List<ItemScene> mSceneList = new ArrayList<>();
    private String mGatewayId;
    private String mGatewayMac;
    private ItemSceneInGateway m1Scene;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.POS_BackLight) != null && propertyEntry.getPropertyValue(CTSL.POS_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.POS_BackLight));
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

        if (CTSL.PK_ONE_SCENE_SWITCH.equals(mProductKey)) {
            mBackLightLayout.setVisibility(View.VISIBLE);
        } else if (CTSL.PK_SYT_ONE_SCENE_SWITCH.equals(mProductKey)) {
            mBackLightLayout.setVisibility(View.GONE);
        }
    }

    // 获取面板所属网关iotId
    private void getGatewayId(String iotId) {
        if (Constant.IS_TEST_DATA) {
            iotId = "y6pVEun2KgQ6wMlxLdLhdTtYmY";
        }
        mSceneManager.getGWIotIdBySubIotId(this, iotId, Constant.MSG_QUEST_GW_ID_BY_SUB_ID,
                Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR, mMyHandler);
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME,
                TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);
    }

    private class MyResponseErrHandler extends Handler {
        private WeakReference<Activity> ref;

        public MyResponseErrHandler(Activity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
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
                } else if (responseErrorEntry.code == 6741) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyNameTV.getText().toString());
                    mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME,
                            jsonObject.toJSONString(), null, null, null);
                }
            }
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.appbgcolor));
        }
    }

    public String getExecuteScene() {
        return mExecuteScene;
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

    @OnClick({R.id.mSceneContentText1, R.id.device_image_view, R.id.key_name_tv,
            R.id.back_light_layout, R.id.go_ic})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.go_ic:
            case R.id.mSceneContentText1:
                // 场景按键
                if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                    if (m1Scene == null) {
                        SwitchLocalSceneListActivity.start(this, mIOTId, mGatewayId, mGatewayMac,
                                CTSL.SCENE_SWITCH_KEY_CODE_1, BIND_SCENE_REQUEST_CODE);
                    } else {
                        String msg = String.format(getString(R.string.main_scene_execute_hint_2),
                                m1Scene.getSceneDetail().getName());
                        ToastUtils.showLongToast(this, msg);
                        mSceneManager.invokeLocalSceneService(mGatewayId,
                                m1Scene.getSceneDetail().getSceneId(), mCommitFailureHandler,
                                mResponseErrorHandler, null);
                    }
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
                break;
            case R.id.device_image_view:
                if (mManualIDs[0] != null) {
                    mPressedKey = "1";
                    mExecuteScene = mSceneContentText1.getText().toString();
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler,
                            mResponseErrorHandler, mMyHandler);
                }
                break;
            case R.id.key_name_tv: {
                //按键名称
                showKeyNameDialogEdit();
                break;
            }
            case R.id.back_light_layout: {
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.POS_BackLight},
                            new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.POS_BackLight},
                            new String[]{"" + CTSL.STATUS_OFF});
                }
                break;
            }
            default:
                break;
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

    private class DelSceneHandler extends Handler {
        private WeakReference<Activity> ref;

        public DelSceneHandler(Activity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    String keyNo = jsonObject.getString("keyNo");
                    if (keyNo != null && keyNo.equals(mPressedKey)) {
                        String autoSceneId = jsonObject.getString("asId");
                        mSceneManager.deleteScene(autoSceneId, null, null, mDelSceneHandler);
                    }
                }
            } else if (msg.what == Constant.MSG_CALLBACK_DELETESCENE) {
                mSceneManager.setExtendedProperty(mIOTId, mPressedKey, "{}", null,
                        null, mDelSceneHandler);
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                getScenes();
            }
        }
    }

    private JSONObject mResultObj;

    // 显示按键名称修改对话框
    private void showKeyNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.key_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setHint(getString(R.string.pls_input_key_name));
        nameEt.setText(mKeyNameTV.getText().toString());
        nameEt.setSelection(nameEt.getText().toString().length());
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
                if (nameEt.getText().toString().length() > 10
                        && mKeyNameTV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(OneKeySceneDetailActivity2.this,
                            R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKeyNameTV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(OneKeySceneDetailActivity2.this,
                            R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(OneKeySceneDetailActivity2.this,
                        R.string.is_setting);
                mKeyName = nameEt.getText().toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName);
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

    @OnLongClick({R.id.mSceneContentText1, R.id.go_ic})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.go_ic:
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                        if (m1Scene != null)
                            EditLocalSceneBindActivity.start(this, mKeyName, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1,
                                    mSceneContentText1.getText().toString(), mGatewayId, mGatewayMac, m1Scene.getSceneDetail().getSceneId(),
                                    EDIT_LOCAL_SCENE);
                    } else
                        EditSceneBindActivity.start(this, "按键一", mIOTId,
                                CTSL.SCENE_SWITCH_KEY_CODE_1
                                , mSceneContentText1.getText().toString());
                }
                break;
            default:
                break;
        }
        return true;
    }

    private class MyHandler extends Handler {
        final WeakReference<OneKeySceneDetailActivity2> mWeakReference;

        public MyHandler(OneKeySceneDetailActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            OneKeySceneDetailActivity2 activity = mWeakReference.get();
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
//                                    activity.mSceneManager.querySceneDetail(jsonObject
//                                    .getString("asId"), CScene.TYPE_AUTOMATIC,
//                                            activity.mCommitFailureHandler, activity
//                                            .mResponseErrorHandler, activity.mMyHandler);
                                } else {
                                    activity.mSceneContentText1.setText(R.string.no_bind_scene);
                                    activity.mManualNames[0] = null;
                                    activity.mManualIDs[0] = null;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    ToastUtils.showLongToast(activity,
                            String.format(activity.getString(R.string.main_scene_execute_hint),
                                    activity.getExecuteScene()));
                    //Toast.makeText(activity, String.format(activity.getString(R.string
                    // .main_scene_execute_hint)
//                            , sceneId.equals(activity.mFirstManualSceneId) ? activity
//                            .mFirstManualSceneName : activity.mSecondManualSceneName), Toast
//                            .LENGTH_LONG).show();
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    break;
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    if (object.toJSONString().length() == 2) break;
                    DeviceBuffer.addExtendedInfo(mIOTId, object);
                    mKeyNameTV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_1));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKeyNameTV.setText(mKeyName);
                    DeviceBuffer.addExtendedInfo(mIOTId, mResultObj);
                    ToastUtils.showShortToast(OneKeySceneDetailActivity2.this,
                            R.string.set_success);
                    break;
                }
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

    // 获取按键绑定场景的名称
    private void querySceneName() {
        mSceneContentText1.setText(R.string.no_bind_scene);
        m1Scene = null;
        for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
            if (scene.getAppParams() == null) continue;
            String key = scene.getAppParams().getString("key");
            if (key == null) continue;
            if (key.contains(CTSL.SCENE_SWITCH_KEY_CODE_1)) {
                mSceneContentText1.setText(scene.getSceneDetail().getName());
                m1Scene = scene;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGatewayMac == null || mGatewayMac.length() == 0)
            getGatewayId(mIOTId);
        else querySceneName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCAL_SCENE) {
            if (resultCode == 2) {
                ToastUtils.showLongToast(this, R.string.unbind_scene_success);
                mSceneContentText1.setText(R.string.no_bind_scene);
                m1Scene = null;
            }
        } else if (requestCode == BIND_SCENE_REQUEST_CODE) {
            if (resultCode == 2)
                ToastUtils.showLongToast(this, R.string.bind_scene_success);
        }
    }
}