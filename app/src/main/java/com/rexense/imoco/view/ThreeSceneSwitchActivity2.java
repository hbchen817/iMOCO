package com.rexense.imoco.view;

import android.app.Activity;
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
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.SceneBindEvent;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ThreeSceneSwitchActivity2 extends DetailActivity {

    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//2
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText3;//3
    @BindView(R.id.key_1_tv)
    TextView mKeyName1TV;
    @BindView(R.id.key_2_tv)
    TextView mKeyName2TV;
    @BindView(R.id.key_3_tv)
    TextView mKeyName3TV;
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[3];
    private String[] mManualNames = new String[3];
    private String mCurrentKey;
    private String mExecuteScene = "";
    private int mBackLightState = 1;
    private TSLHelper mTSLHelper;
    private final int TAG_GET_EXTENDED_PRO = 10000;
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;

    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
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
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mBackLightIc.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);

        mDelSceneHandler = new DelSceneHandler(this);
        mMyHandler = new MyHandler(this);
        initView();
        getScenes();

        initStatusBar();
        initKeyNickName();
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);
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
                } else if (responseErrorEntry.code == 6741) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName1TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKeyName2TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKeyName3TV.getText().toString());
                    mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), null, null, null);
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
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @OnClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSwitch1, R.id.mSwitch2, R.id.mSwitch3,
            R.id.key_1_tv, R.id.key_2_tv, R.id.key_3_tv, R.id.back_light_layout})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    mPressedKey = "1";
                    mExecuteScene = mSceneContentText1.getText().toString();
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText2:
                if (mManualIDs[1] != null) {
                    mPressedKey = "2";
                    mExecuteScene = mSceneContentText2.getText().toString();
                    mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2);
                }
                break;
            case R.id.mSceneContentText3:
                if (mManualIDs[2] != null) {
                    mPressedKey = "3";
                    mExecuteScene = mSceneContentText3.getText().toString();
                    mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3);
                }
                break;

            case R.id.mSwitch1:
                if (mManualIDs[0] != null) {
                    mPressedKey = "1";
                    mExecuteScene = mSceneContentText1.getText().toString();
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                }
                break;
            case R.id.mSwitch2:
                if (mManualIDs[1] != null) {
                    mPressedKey = "2";
                    mExecuteScene = mSceneContentText2.getText().toString();
                    mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                }
                break;
            case R.id.mSwitch3:
                if (mManualIDs[2] != null) {
                    mPressedKey = "3";
                    mExecuteScene = mSceneContentText3.getText().toString();
                    mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                }
                break;
            case R.id.key_1_tv: {
                // 按键1
                showKeyNameDialogEdit(R.id.key_1_tv);
                break;
            }
            case R.id.key_2_tv: {
                // 按键2
                showKeyNameDialogEdit(R.id.key_2_tv);
                break;
            }
            case R.id.key_3_tv: {
                // 按键3
                showKeyNameDialogEdit(R.id.key_3_tv);
                break;
            }
            case R.id.back_light_layout: {
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PTS_BackLight}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PTS_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
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
                ViseLog.d("mPressedKey = " + mPressedKey + " , " + (String) msg.obj);
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
    private void showKeyNameDialogEdit(int resId) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.key_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setHint(getString(R.string.pls_input_key_name));
        switch (resId) {
            case R.id.key_1_tv: {
                // 按键1
                nameEt.setText(mKeyName1TV.getText().toString());
                break;
            }
            case R.id.key_2_tv: {
                // 按键2
                nameEt.setText(mKeyName2TV.getText().toString());
                break;
            }
            case R.id.key_3_tv: {
                // 按键3
                nameEt.setText(mKeyName3TV.getText().toString());
                break;
            }
        }

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
                        && mKeyName1TV.getText().toString().length() > 10
                        && mKeyName2TV.getText().toString().length() > 10
                        && mKeyName3TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(ThreeSceneSwitchActivity2.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKeyName1TV.getText().toString().length() == 0
                        && mKeyName2TV.getText().toString().length() == 0
                        && mKeyName3TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(ThreeSceneSwitchActivity2.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(ThreeSceneSwitchActivity2.this, R.string.is_setting);
                switch (resId) {
                    case R.id.key_1_tv: {
                        // 按键1
                        mKeyName1 = nameEt.getText().toString();
                        mKeyName2 = mKeyName2TV.getText().toString();
                        mKeyName3 = mKeyName3TV.getText().toString();
                        break;
                    }
                    case R.id.key_2_tv: {
                        // 按键2
                        mKeyName1 = mKeyName1TV.getText().toString();
                        mKeyName2 = nameEt.getText().toString();
                        mKeyName3 = mKeyName3TV.getText().toString();
                        break;
                    }
                    case R.id.key_3_tv: {
                        // 按键3
                        mKeyName1 = mKeyName1TV.getText().toString();
                        mKeyName2 = mKeyName2TV.getText().toString();
                        mKeyName3 = nameEt.getText().toString();
                        break;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName1);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKeyName2);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKeyName3);
                mResultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
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

    @OnLongClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1, mSceneContentText1.getText().toString());
                }
                break;
            case R.id.mSceneContentText2:
                if (mManualIDs[1] != null) {
                    EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2, mSceneContentText2.getText().toString());
                }
                break;
            case R.id.mSceneContentText3:
                if (mManualIDs[2] != null) {
                    EditSceneBindActivity.start(this, "按键三", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3, mSceneContentText3.getText().toString());
                }
                break;
            default:
                break;
        }
        return true;
    }

    private class MyHandler extends Handler {
        final WeakReference<ThreeSceneSwitchActivity2> mWeakReference;

        public MyHandler(ThreeSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ThreeSceneSwitchActivity2 activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        switch (activity.mCurrentKey) {
                            case CTSL.SCENE_SWITCH_KEY_CODE_1:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText1.setText(jsonObject.getString("name"));
                                    activity.mManualNames[0] = jsonObject.getString("name");
                                    activity.mManualIDs[0] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText1.setText(R.string.no_bind_scene);
                                    activity.mManualNames[0] = null;
                                    activity.mManualIDs[0] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_2;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_2:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText2.setText(jsonObject.getString("name"));
                                    activity.mManualNames[1] = jsonObject.getString("name");
                                    activity.mManualIDs[1] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText2.setText(R.string.no_bind_scene);
                                    activity.mManualNames[1] = null;
                                    activity.mManualIDs[1] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_3;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_3:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText3.setText(jsonObject.getString("name"));
                                    activity.mManualNames[2] = jsonObject.getString("name");
                                    activity.mManualIDs[2] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText3.setText(R.string.no_bind_scene);
                                    activity.mManualNames[2] = null;
                                    activity.mManualIDs[2] = null;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    ToastUtils.showLongToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint),
                            activity.getExecuteScene()));
                    //Toast.makeText(activity, String.format(activity.getString(R.string.main_scene_execute_hint)
//                            , sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName), Toast.LENGTH_LONG).show();
                    break;
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    DeviceBuffer.addExtendedInfo(mIOTId, object);
                    mKeyName1TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_1));
                    mKeyName2TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_2));
                    mKeyName3TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_3));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKeyName1TV.setText(mKeyName1);
                    mKeyName2TV.setText(mKeyName2);
                    mKeyName3TV.setText(mKeyName3);
                    DeviceBuffer.addExtendedInfo(mIOTId, mResultObj);
                    ToastUtils.showShortToast(ThreeSceneSwitchActivity2.this, R.string.set_success);
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
}

