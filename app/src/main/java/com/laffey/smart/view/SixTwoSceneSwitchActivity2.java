package com.laffey.smart.view;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SixTwoSceneSwitchActivity2 extends DetailActivity implements View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.switch1)
    ImageView mSwitch1;
    @BindView(R.id.switch2)
    ImageView mSwitch2;
    @BindView(R.id.switch3)
    ImageView mSwitch3;
    @BindView(R.id.switch4)
    ImageView mSwitch4;
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//5
    @BindView(R.id.mSceneContentText5)
    TextView mSceneContentText5;//6
    @BindView(R.id.timer_layout)
    RelativeLayout mTimerLayout;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.timer_ic_tv)
    TextView mTimerIcTV;
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
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
    private String mFirstManualSceneId;
    private String mSecondManualSceneId;
    private String mFirstManualSceneName;
    private String mSecondManualSceneName;
    private String mCurrentKey;
    private int mState1;
    private int mState2;
    private int mState3;
    private int mState4;
    private TSLHelper mTSLHelper;
    private int mBackLightState = 1;
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;
    private String mKeyName4;
    private String mKeyName5;
    private String mKeyName6;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1));
            //mSwitch1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_1, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1)));
            mSwitch1.setBackgroundResource(mState1 == 0 ? R.drawable.state_switch_top_off : R.drawable.state_switch_top_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2));
            //mSwitch2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_2, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2)));
            mSwitch2.setBackgroundResource(mState2 == 0 ? R.drawable.state_switch_bottom_off : R.drawable.state_switch_bottom_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3));
            //mSwitch3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_3, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3)));
            mSwitch3.setBackgroundResource(mState3 == 0 ? R.drawable.state_switch_top_off : R.drawable.state_switch_top_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4).length() > 0) {
            mState4 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4));
            //mSwitch4.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_4, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3)));
            mSwitch4.setBackgroundResource(mState4 == 0 ? R.drawable.state_switch_bottom_off : R.drawable.state_switch_bottom_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_BackLight) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_BackLight));
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

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mTimerIcTV.setTypeface(iconfont);
        mBackLightIc.setTypeface(iconfont);

        mMyHandler = new MyHandler(this);
        mTSLHelper = new TSLHelper(this);
        initView();
        initKeyNickName();
        getScenes();
        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.appbgcolor2));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private static final int TAG_GET_EXTENDED_PRO = 10000;

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
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_1, mKey1TV.getText().toString());
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_2, mKey2TV.getText().toString());
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_3, mKey3TV.getText().toString());
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_4, mKey4TV.getText().toString());
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mKey5TV.getText().toString());
                    jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mKey6TV.getText().toString());
                    mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), null, null, null);
                }
            }
        }
    }

    private void initView() {
        mSwitch1.setOnClickListener(this);
        mSwitch2.setOnClickListener(this);
        mSwitch3.setOnClickListener(this);
        mSwitch4.setOnClickListener(this);
        mSceneContentText2.setOnClickListener(this);
        mSceneContentText5.setOnClickListener(this);
        mSceneContentText2.setOnLongClickListener(this);
        mSceneContentText5.setOnLongClickListener(this);

        mTimerLayout.setOnClickListener(this);
        mBackLightLayout.setOnClickListener(this);
        mKey1TV.setOnClickListener(this);
        mKey2TV.setOnClickListener(this);
        mKey3TV.setOnClickListener(this);
        mKey4TV.setOnClickListener(this);
        mKey5TV.setOnClickListener(this);
        mKey6TV.setOnClickListener(this);

        mSceneManager = new SceneManager(this);
    }

    private void getScenes() {
        mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_1;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch1:
                if (mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            case R.id.switch2:
                if (mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            case R.id.switch3:
                if (mState3 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            case R.id.switch4:
                if (mState4 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            case R.id.mSceneContentText2:
                if (mFirstManualSceneId != null) {
                    mSceneManager.executeScene(mFirstManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText5:
                if (mSecondManualSceneId != null) {
                    mSceneManager.executeScene(mSecondManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2);
                }
                break;
            case R.id.timer_layout: {
                // 定时
                PluginHelper.cloudTimer(SixTwoSceneSwitchActivity2.this, mIOTId, mProductKey);
                break;
            }
            case R.id.back_light_layout: {
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
                }
                break;
            }
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
            case R.id.key_4_tv: {
                // 按键4
                showKeyNameDialogEdit(R.id.key_4_tv);
                break;
            }
            case R.id.key_5_tv: {
                // 按键5
                showKeyNameDialogEdit(R.id.key_5_tv);
                break;
            }
            case R.id.key_6_tv: {
                // 按键6
                showKeyNameDialogEdit(R.id.key_6_tv);
                break;
            }
            default:
                break;
        }
    }

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
                nameEt.setText(mKey1TV.getText().toString());
                break;
            }
            case R.id.key_2_tv: {
                // 按键2
                nameEt.setText(mKey2TV.getText().toString());
                break;
            }
            case R.id.key_3_tv: {
                // 按键3
                nameEt.setText(mKey3TV.getText().toString());
                break;
            }
            case R.id.key_4_tv: {
                // 按键4
                nameEt.setText(mKey4TV.getText().toString());
                break;
            }
            case R.id.key_5_tv: {
                // 按键5
                nameEt.setText(mKey5TV.getText().toString());
                break;
            }
            case R.id.key_6_tv: {
                // 按键6
                nameEt.setText(mKey6TV.getText().toString());
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
                        || mKey1TV.getText().toString().length() > 10
                        || mKey2TV.getText().toString().length() > 10
                        || mKey3TV.getText().toString().length() > 10
                        || mKey4TV.getText().toString().length() > 10
                        || mKey5TV.getText().toString().length() > 10
                        || mKey6TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(SixTwoSceneSwitchActivity2.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        || mKey1TV.getText().toString().length() == 0
                        || mKey2TV.getText().toString().length() == 0
                        || mKey3TV.getText().toString().length() == 0
                        || mKey4TV.getText().toString().length() == 0
                        || mKey5TV.getText().toString().length() == 0
                        || mKey6TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(SixTwoSceneSwitchActivity2.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(SixTwoSceneSwitchActivity2.this, R.string.is_setting);
                switch (resId) {
                    case R.id.key_1_tv: {
                        // 按键1
                        mKeyName1 = nameEt.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        mKeyName5 = mKey5TV.getText().toString();
                        mKeyName6 = mKey6TV.getText().toString();
                        break;
                    }
                    case R.id.key_2_tv: {
                        // 按键2
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = nameEt.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        mKeyName5 = mKey5TV.getText().toString();
                        mKeyName6 = mKey6TV.getText().toString();
                        break;
                    }
                    case R.id.key_3_tv: {
                        // 按键3
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = nameEt.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        mKeyName5 = mKey5TV.getText().toString();
                        mKeyName6 = mKey6TV.getText().toString();
                        break;
                    }
                    case R.id.key_4_tv: {
                        // 按键4
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = nameEt.getText().toString();
                        mKeyName5 = mKey5TV.getText().toString();
                        mKeyName6 = mKey6TV.getText().toString();
                        break;
                    }
                    case R.id.key_5_tv: {
                        // 按键5
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        mKeyName5 = nameEt.getText().toString();
                        mKeyName6 = mKey6TV.getText().toString();
                        break;
                    }
                    case R.id.key_6_tv: {
                        // 按键6
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        mKeyName5 = mKey5TV.getText().toString();
                        mKeyName6 = nameEt.getText().toString();
                        break;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_1, mKeyName1);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_2, mKeyName2);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_3, mKeyName3);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_4, mKeyName4);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mKeyName5);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mKeyName6);
                resultObj = jsonObject;
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

    private JSONObject resultObj;

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText2:
                if (mFirstManualSceneId != null) {
                    EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mSceneContentText2.getText().toString());
                }
                break;
            case R.id.mSceneContentText5:
                if (mSecondManualSceneId != null) {
                    EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mSceneContentText5.getText().toString());
                }
                break;
            default:
                break;
        }
        return true;
    }


    private class MyHandler extends Handler {
        final WeakReference<SixTwoSceneSwitchActivity2> mWeakReference;

        public MyHandler(SixTwoSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixTwoSceneSwitchActivity2 activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        if (activity.mCurrentKey.equals(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1)) {
                            if (!jsonObject.isEmpty()) {
                                activity.mSceneContentText2.setText(jsonObject.getString("name"));
                                activity.mFirstManualSceneName = jsonObject.getString("name");
                                activity.mFirstManualSceneId = jsonObject.getString("msId");
                            } else {
                                activity.mSceneContentText2.setText(R.string.no_bind_scene);
                                activity.mFirstManualSceneId = null;
                                activity.mFirstManualSceneName = null;
                            }
                            activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_2;
                            activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                    activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                        } else if (activity.mCurrentKey.equals(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2)) {
                            if (!jsonObject.isEmpty()) {
                                activity.mSceneContentText5.setText(jsonObject.getString("name"));
                                activity.mSecondManualSceneName = jsonObject.getString("name");
                                activity.mSecondManualSceneId = jsonObject.getString("msId");
                            } else {
                                activity.mSceneContentText5.setText(R.string.no_bind_scene);
                                activity.mSecondManualSceneId = null;
                                activity.mSecondManualSceneName = null;
                            }
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    ToastUtils.showShortToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint_2),
                            sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName));
                    break;
                case TAG_GET_EXTENDED_PRO: {
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    mKey1TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1));
                    mKey2TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2));
                    mKey3TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3));
                    mKey4TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4));
                    mKey5TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1));
                    mKey6TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2));
                    DeviceBuffer.addExtendedInfo(mIOTId, object);
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKey1TV.setText(mKeyName1);
                    mKey2TV.setText(mKeyName2);
                    mKey3TV.setText(mKeyName3);
                    mKey4TV.setText(mKeyName4);
                    mKey5TV.setText(mKeyName5);
                    mKey6TV.setText(mKeyName6);
                    DeviceBuffer.addExtendedInfo(mIOTId, resultObj);
                    ToastUtils.showShortToast(SixTwoSceneSwitchActivity2.this, R.string.set_success);
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

