package com.rexense.wholehouse.view;

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
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.event.SceneBindEvent;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class SixSceneSwitchActivity2 extends DetailActivity {

    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//2
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText3;//3
    @BindView(R.id.mSceneContentText4)
    TextView mSceneContentText4;//4
    @BindView(R.id.mSceneContentText5)
    TextView mSceneContentText5;//5
    @BindView(R.id.mSceneContentText6)
    TextView mSceneContentText6;//6
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.back_light_tv)
    TextView mBackLightIc;
    @BindView(R.id.back_light_txt)
    TextView mBackLightTV;
    @BindView(R.id.key_1_tv)
    TextView mKey1TV;
    @BindView(R.id.key_2_tv)
    TextView mKey2TV;
    @BindView(R.id.key_3_tv)
    TextView mKey3TV;
    @BindView(R.id.key_4_tv)
    TextView mKey4TV;
    @BindView(R.id.key_5_tv)
    TextView mKey5TV;
    @BindView(R.id.key_6_tv)
    TextView mKey6TV;

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[6];
    private String[] mManualNames = new String[6];
    private String mCurrentKey;
    private String mExecuteScene = "";
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;
    private String mKeyName4;
    private String mKeyName5;
    private String mKeyName6;

    private int mBackLightState = 1;

    private TSLHelper mTSLHelper;

    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        ViseLog.d(new Gson().toJson(propertyEntry));
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.PSS_BackLightMode) != null && propertyEntry.getPropertyValue(CTSL.PSS_BackLightMode).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.PSS_BackLightMode));
            switch (mBackLightState) {
                case 0: {
                    // 关闭背光
                    mBackLightIc.setTextColor(getResources().getColor(R.color.gray3));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.gray3));
                    break;
                }
                case 1: {
                    // 打开背光
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
        initKeyNickName();
        getScenes();

        initStatusBar();
    }

    private final int TAG_GET_EXTENDED_PRO = 10000;

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);
    }

    private static class MyResponseErrHandler extends Handler {
        private WeakReference<SixSceneSwitchActivity2> ref;

        public MyResponseErrHandler(SixSceneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixSceneSwitchActivity2 activity = ref.get();
            if (activity == null) return;
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
                    activity.logOut();
                } else if (responseErrorEntry.code == 6741) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, activity.mKey1TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, activity.mKey2TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, activity.mKey3TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, activity.mKey4TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_5, activity.mKey5TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_6, activity.mKey6TV.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), null, null, null);
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

    @OnClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4, R.id.mSceneContentText5, R.id.mSceneContentText6,
            R.id.back_light_layout, R.id.key_1_tv, R.id.key_2_tv, R.id.key_3_tv, R.id.key_4_tv, R.id.key_5_tv, R.id.key_6_tv})
    public void onClickView(View view) {
        if (view.getId() == R.id.mSceneContentText1) {
            if (mManualIDs[0] != null) {
                mPressedKey = "1";
                mExecuteScene = mSceneContentText1.getText().toString();
                mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1);
            }
        } else if (view.getId() == R.id.mSceneContentText2) {
            if (mManualIDs[1] != null) {
                mPressedKey = "2";
                mExecuteScene = mSceneContentText2.getText().toString();
                mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2);
            }
        } else if (view.getId() == R.id.mSceneContentText3) {
            if (mManualIDs[2] != null) {
                mPressedKey = "3";
                mExecuteScene = mSceneContentText3.getText().toString();
                mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3);
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            if (mManualIDs[3] != null) {
                mPressedKey = "4";
                mExecuteScene = mSceneContentText4.getText().toString();
                mSceneManager.executeScene(mManualIDs[3], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4);
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mManualIDs[4] != null) {
                mPressedKey = "5";
                mExecuteScene = mSceneContentText5.getText().toString();
                mSceneManager.executeScene(mManualIDs[4], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
            }
        } else if (view.getId() == R.id.mSceneContentText6) {
            if (mManualIDs[5] != null) {
                mPressedKey = "6";
                mExecuteScene = mSceneContentText6.getText().toString();
                mSceneManager.executeScene(mManualIDs[5], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2);
            }
        } else if (view.getId() == R.id.back_light_layout) {
            // 背光
            if (mBackLightState == 0) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PSS_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PSS_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
            }
        } else if (view.getId() == R.id.key_1_tv) {
            // 按键1
            showKeyNameDialogEdit(R.id.key_1_tv);
        } else if (view.getId() == R.id.key_2_tv) {
            // 按键2
            showKeyNameDialogEdit(R.id.key_2_tv);
        } else if (view.getId() == R.id.key_3_tv) {
            // 按键3
            showKeyNameDialogEdit(R.id.key_3_tv);
        } else if (view.getId() == R.id.key_4_tv) {
            // 按键4
            showKeyNameDialogEdit(R.id.key_4_tv);
        } else if (view.getId() == R.id.key_5_tv) {
            // 按键5
            showKeyNameDialogEdit(R.id.key_5_tv);
        } else if (view.getId() == R.id.key_6_tv) {
            // 按键6
            showKeyNameDialogEdit(R.id.key_6_tv);
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
        private WeakReference<SixSceneSwitchActivity2> ref;

        public DelSceneHandler(SixSceneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SixSceneSwitchActivity2 activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    String keyNo = jsonObject.getString("keyNo");
                    if (keyNo != null && keyNo.equals(activity.mPressedKey)) {
                        String autoSceneId = jsonObject.getString("asId");
                        activity.mSceneManager.deleteScene(autoSceneId, null, null, null);
                        activity.mSceneManager.setExtendedProperty(activity.mIOTId, activity.mPressedKey, "{}", null,
                                null, activity.mDelSceneHandler);
                    }
                }
            } else if (msg.what == Constant.MSG_CALLBACK_DELETESCENE) {
                activity.mSceneManager.setExtendedProperty(activity.mIOTId, activity.mPressedKey, "{}", null,
                        null, activity.mDelSceneHandler);
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                activity.getScenes();
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
        if (resId == R.id.key_1_tv) {
            // 按键1
            nameEt.setText(mKey1TV.getText().toString());
        } else if (resId == R.id.key_2_tv) {
            // 按键2
            nameEt.setText(mKey2TV.getText().toString());
        } else if (resId == R.id.key_3_tv) {
            // 按键3
            nameEt.setText(mKey3TV.getText().toString());
        } else if (resId == R.id.key_4_tv) {
            // 按键4
            nameEt.setText(mKey4TV.getText().toString());
        } else if (resId == R.id.key_5_tv) {
            // 按键5
            nameEt.setText(mKey5TV.getText().toString());
        } else if (resId == R.id.key_6_tv) {
            // 按键6
            nameEt.setText(mKey6TV.getText().toString());
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
                        && mKey1TV.getText().toString().length() > 10
                        && mKey2TV.getText().toString().length() > 10
                        && mKey3TV.getText().toString().length() > 10
                        && mKey4TV.getText().toString().length() > 10
                        && mKey5TV.getText().toString().length() > 10
                        && mKey6TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(SixSceneSwitchActivity2.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKey1TV.getText().toString().length() == 0
                        && mKey2TV.getText().toString().length() == 0
                        && mKey3TV.getText().toString().length() == 0
                        && mKey4TV.getText().toString().length() == 0
                        && mKey5TV.getText().toString().length() == 0
                        && mKey6TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(SixSceneSwitchActivity2.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(SixSceneSwitchActivity2.this, R.string.is_setting);
                if (resId == R.id.key_1_tv) {
                    // 按键1
                    mKeyName1 = nameEt.getText().toString();
                    mKeyName2 = mKey2TV.getText().toString();
                    mKeyName3 = mKey3TV.getText().toString();
                    mKeyName4 = mKey4TV.getText().toString();
                    mKeyName5 = mKey5TV.getText().toString();
                    mKeyName6 = mKey6TV.getText().toString();
                } else if (resId == R.id.key_2_tv) {
                    // 按键2
                    mKeyName1 = mKey1TV.getText().toString();
                    mKeyName2 = nameEt.getText().toString();
                    mKeyName3 = mKey3TV.getText().toString();
                    mKeyName4 = mKey4TV.getText().toString();
                    mKeyName5 = mKey5TV.getText().toString();
                    mKeyName6 = mKey6TV.getText().toString();
                } else if (resId == R.id.key_3_tv) {
                    // 按键3
                    mKeyName1 = mKey1TV.getText().toString();
                    mKeyName2 = mKey2TV.getText().toString();
                    mKeyName3 = nameEt.getText().toString();
                    mKeyName4 = mKey4TV.getText().toString();
                    mKeyName5 = mKey5TV.getText().toString();
                    mKeyName6 = mKey6TV.getText().toString();
                } else if (resId == R.id.key_4_tv) {
                    // 按键4
                    mKeyName1 = mKey1TV.getText().toString();
                    mKeyName2 = mKey2TV.getText().toString();
                    mKeyName3 = mKey3TV.getText().toString();
                    mKeyName4 = nameEt.getText().toString();
                    mKeyName5 = mKey5TV.getText().toString();
                    mKeyName6 = mKey6TV.getText().toString();
                } else if (resId == R.id.key_5_tv) {
                    // 按键5
                    mKeyName1 = mKey1TV.getText().toString();
                    mKeyName2 = mKey2TV.getText().toString();
                    mKeyName3 = mKey3TV.getText().toString();
                    mKeyName4 = mKey4TV.getText().toString();
                    mKeyName5 = nameEt.getText().toString();
                    mKeyName6 = mKey6TV.getText().toString();
                } else if (resId == R.id.key_6_tv) {
                    // 按键6
                    mKeyName1 = mKey1TV.getText().toString();
                    mKeyName2 = mKey2TV.getText().toString();
                    mKeyName3 = mKey3TV.getText().toString();
                    mKeyName4 = mKey4TV.getText().toString();
                    mKeyName5 = mKey5TV.getText().toString();
                    mKeyName6 = nameEt.getText().toString();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName1);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKeyName2);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKeyName3);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, mKeyName4);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_5, mKeyName5);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_6, mKeyName6);
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

    @OnLongClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4, R.id.mSceneContentText5, R.id.mSceneContentText6})
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.mSceneContentText1) {
            if (mManualIDs[0] != null) {
                EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1, mSceneContentText1.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText2) {
            if (mManualIDs[1] != null) {
                EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2, mSceneContentText2.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText3) {
            if (mManualIDs[2] != null) {
                EditSceneBindActivity.start(this, "按键三", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3, mSceneContentText3.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            if (mManualIDs[3] != null) {
                EditSceneBindActivity.start(this, "按键四", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4, mSceneContentText4.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mManualIDs[4] != null) {
                EditSceneBindActivity.start(this, "按键五", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mSceneContentText5.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText6) {
            if (mManualIDs[5] != null) {
                EditSceneBindActivity.start(this, "按键六", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mSceneContentText6.getText().toString());
            }
        }
        return true;
    }

    public String getExecuteScene() {
        return mExecuteScene;
    }

    private static class MyHandler extends Handler {
        final WeakReference<SixSceneSwitchActivity2> mWeakReference;

        public MyHandler(SixSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixSceneSwitchActivity2 activity = mWeakReference.get();
            if (activity == null) return;
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
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_4;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_4:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText4.setText(jsonObject.getString("name"));
                                    activity.mManualNames[3] = jsonObject.getString("name");
                                    activity.mManualIDs[3] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText4.setText(R.string.no_bind_scene);
                                    activity.mManualNames[3] = null;
                                    activity.mManualIDs[3] = null;
                                }
                                activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_1;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SIX_SCENE_SWITCH_KEY_CODE_1:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText5.setText(jsonObject.getString("name"));
                                    activity.mManualNames[4] = jsonObject.getString("name");
                                    activity.mManualIDs[4] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText5.setText(R.string.no_bind_scene);
                                    activity.mManualNames[4] = null;
                                    activity.mManualIDs[4] = null;
                                }
                                activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_2;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SIX_SCENE_SWITCH_KEY_CODE_2:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText6.setText(jsonObject.getString("name"));
                                    activity.mManualNames[5] = jsonObject.getString("name");
                                    activity.mManualIDs[5] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText6.setText(R.string.no_bind_scene);
                                    activity.mManualNames[5] = null;
                                    activity.mManualIDs[5] = null;
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
                case 10000: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
                    activity.mKey1TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_1));
                    activity.mKey2TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_2));
                    activity.mKey3TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_3));
                    activity.mKey4TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_4));
                    activity.mKey5TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_5));
                    activity.mKey6TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_6));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    activity.mKey1TV.setText(activity.mKeyName1);
                    activity.mKey2TV.setText(activity.mKeyName2);
                    activity.mKey3TV.setText(activity.mKeyName3);
                    activity.mKey4TV.setText(activity.mKeyName4);
                    activity.mKey5TV.setText(activity.mKeyName5);
                    activity.mKey6TV.setText(activity.mKeyName6);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.mResultObj);
                    ToastUtils.showShortToast(activity, R.string.set_success);
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

