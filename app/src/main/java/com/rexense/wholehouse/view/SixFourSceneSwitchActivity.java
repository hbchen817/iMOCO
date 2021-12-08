package com.rexense.wholehouse.view;

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

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.event.SceneBindEvent;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.PluginHelper;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.utility.GsonUtil;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SixFourSceneSwitchActivity extends DetailActivity implements View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.switch1)
    ImageView mSwitch1;
    @BindView(R.id.switch2)
    ImageView mSwitch2;
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText3;//3
    @BindView(R.id.mSceneContentText4)
    TextView mSceneContentText4;//4
    @BindView(R.id.mSceneContentText5)
    TextView mSceneContentText5;//5
    @BindView(R.id.mSceneContentText6)
    TextView mSceneContentText6;//6
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
    private String mKey3ManualSceneId;
    private String mKey4ManualSceneId;
    private String mKey5ManualSceneId;
    private String mKey6ManualSceneId;
    private String mKey3ManualSceneName;
    private String mKey4ManualSceneName;
    private String mKey5ManualSceneName;
    private String mKey6ManualSceneName;
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

    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;
    private final Map<String, String> mSceneMap = new HashMap<>();

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }
        ViseLog.d("propertyEntry = \n" + GsonUtil.toJson(propertyEntry));
        if (propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_1) != null && propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_1));
            //mSwitch1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_1, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1)));
            mSwitch1.setBackgroundResource(mState1 == 0 ? R.drawable.state_switch_top_off : R.drawable.state_switch_top_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_2) != null && propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_FOUR_SCENE_SWITCH_POWER_2));
            //mSwitch2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_2, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2)));
            mSwitch2.setBackgroundResource(mState2 == 0 ? R.drawable.state_switch_bottom_off : R.drawable.state_switch_bottom_on);
        }
        if (propertyEntry.getPropertyValue(CTSL.FOUR_TWO_SCENE_SWITCH_BackLight) != null && propertyEntry.getPropertyValue(CTSL.FOUR_TWO_SCENE_SWITCH_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FOUR_TWO_SCENE_SWITCH_BackLight));
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
        mTimerIcTV.setTypeface(iconfont);
        mBackLightIc.setTypeface(iconfont);

        mMyHandler = new MyHandler(this);
        mTSLHelper = new TSLHelper(this);
        mDelSceneHandler = new DelSceneHandler(this);
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

    private static class MyResponseErrHandler extends Handler {
        private WeakReference<SixFourSceneSwitchActivity> ref;

        public MyResponseErrHandler(SixFourSceneSwitchActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixFourSceneSwitchActivity activity = ref.get();
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
                    jsonObject.put(CTSL.FOUR_TWO_SCENE_SWITCH_POWER_1, activity.mKey1TV.getText().toString());
                    jsonObject.put(CTSL.FOUR_TWO_SCENE_SWITCH_POWER_2, activity.mKey2TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, activity.mKey3TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, activity.mKey4TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_5, activity.mKey5TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_6, activity.mKey6TV.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                            null, null, null);
                }
            }
        }
    }

    private void initView() {
        mSwitch1.setOnClickListener(this);
        mSwitch2.setOnClickListener(this);
        mSceneContentText3.setOnClickListener(this);
        mSceneContentText4.setOnClickListener(this);
        mSceneContentText5.setOnClickListener(this);
        mSceneContentText6.setOnClickListener(this);

        mSceneContentText3.setOnLongClickListener(this);
        mSceneContentText4.setOnLongClickListener(this);
        mSceneContentText5.setOnLongClickListener(this);
        mSceneContentText6.setOnLongClickListener(this);

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
        mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_3;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.switch1) {
            if (mState1 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_POWER_1}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_POWER_1}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.switch2) {
            if (mState2 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_POWER_2}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_POWER_2}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.mSceneContentText3) {
            if (mKey3ManualSceneId != null) {
                mPressedKey = "3";
                mSceneManager.executeScene(mKey3ManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3);
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            if (mKey4ManualSceneId != null) {
                mPressedKey = "4";
                mSceneManager.executeScene(mKey4ManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4);
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mKey5ManualSceneId != null) {
                mPressedKey = "5";
                mSceneManager.executeScene(mKey5ManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_5);
            }
        } else if (view.getId() == R.id.mSceneContentText6) {
            if (mKey6ManualSceneId != null) {
                mPressedKey = "6";
                mSceneManager.executeScene(mKey6ManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_5);
            }
        } else if (view.getId() == R.id.timer_layout) {
            // 定时
            PluginHelper.cloudTimer(SixFourSceneSwitchActivity.this, mIOTId, mProductKey);
        } else if (view.getId() == R.id.back_light_layout) {
            // 背光
            if (mBackLightState == CTSL.STATUS_OFF) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_ON});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_FOUR_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
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
        private WeakReference<SixFourSceneSwitchActivity> ref;

        public DelSceneHandler(SixFourSceneSwitchActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SixFourSceneSwitchActivity activity = ref.get();
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
                        || mKey1TV.getText().toString().length() > 10
                        || mKey2TV.getText().toString().length() > 10
                        || mKey3TV.getText().toString().length() > 10
                        || mKey4TV.getText().toString().length() > 10
                        || mKey5TV.getText().toString().length() > 10
                        || mKey6TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(SixFourSceneSwitchActivity.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        || mKey1TV.getText().toString().length() == 0
                        || mKey2TV.getText().toString().length() == 0
                        || mKey3TV.getText().toString().length() == 0
                        || mKey4TV.getText().toString().length() == 0
                        || mKey5TV.getText().toString().length() == 0
                        || mKey6TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(SixFourSceneSwitchActivity.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(SixFourSceneSwitchActivity.this, R.string.is_setting);
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
                jsonObject.put(CTSL.FOUR_TWO_SCENE_SWITCH_POWER_1, mKeyName1);
                jsonObject.put(CTSL.FOUR_TWO_SCENE_SWITCH_POWER_2, mKeyName2);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKeyName3);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, mKeyName4);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_5, mKeyName5);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_6, mKeyName6);
                resultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                        mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
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
        if (view.getId() == R.id.mSceneContentText3) {
            if (mKey3ManualSceneId != null) {
                EditSceneBindActivity.start(this, getString(R.string.key_3), mIOTId,
                        CTSL.SCENE_SWITCH_KEY_CODE_3, mSceneContentText3.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            if (mKey4ManualSceneId != null) {
                EditSceneBindActivity.start(this, getString(R.string.key_4), mIOTId,
                        CTSL.SCENE_SWITCH_KEY_CODE_4, mSceneContentText4.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mKey5ManualSceneId != null) {
                EditSceneBindActivity.start(this, getString(R.string.key_5), mIOTId,
                        CTSL.SCENE_SWITCH_KEY_CODE_5, mSceneContentText5.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText6) {
            if (mKey6ManualSceneId != null) {
                EditSceneBindActivity.start(this, getString(R.string.key_6), mIOTId,
                        CTSL.SCENE_SWITCH_KEY_CODE_6, mSceneContentText6.getText().toString());
            }
        }
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<SixFourSceneSwitchActivity> mWeakReference;

        public MyHandler(SixFourSceneSwitchActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixFourSceneSwitchActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        ViseLog.d("场景 = \n" + GsonUtil.toJson(jsonObject));
                        activity.initSceneIdAndName(jsonObject);
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    String sceneName = activity.mSceneMap.get(sceneId);
                    ToastUtils.showShortToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint_2),
                            sceneName));
                    break;
                case TAG_GET_EXTENDED_PRO: {
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    ViseLog.d("昵称 = \n" + GsonUtil.toJson(object));
                    activity.mKey1TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1));
                    activity.mKey2TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2));
                    activity.mKey3TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_3));
                    activity.mKey4TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_4));
                    activity.mKey5TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_5));
                    activity.mKey6TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_6));
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
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
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.resultObj);
                    ToastUtils.showShortToast(activity, R.string.set_success);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void initSceneIdAndName(JSONObject object) {
        if (mCurrentKey.equals(CTSL.SCENE_SWITCH_KEY_CODE_3)) {
            if (!object.isEmpty() && !"{}".equals(object.toJSONString())) {
                mSceneContentText3.setText(object.getString("name"));
                mKey3ManualSceneName = object.getString("name");
                mKey3ManualSceneId = object.getString("msId");
                mSceneMap.put(mKey3ManualSceneId, mKey3ManualSceneName);
            } else {
                mSceneContentText3.setText(R.string.no_bind_scene);
                if (mKey3ManualSceneId != null &&
                        mKey3ManualSceneId.length() > 0)
                    mSceneMap.remove(mKey3ManualSceneId);
                mKey3ManualSceneId = null;
                mKey3ManualSceneName = null;
            }
            mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_4;
        } else if (mCurrentKey.equals(CTSL.SCENE_SWITCH_KEY_CODE_4)) {
            if (!object.isEmpty() && !"{}".equals(object.toJSONString())) {
                mSceneContentText4.setText(object.getString("name"));
                mKey4ManualSceneName = object.getString("name");
                mKey4ManualSceneId = object.getString("msId");
                mSceneMap.put(mKey4ManualSceneId, mKey4ManualSceneName);
            } else {
                mSceneContentText4.setText(R.string.no_bind_scene);
                if (mKey4ManualSceneId != null &&
                        mKey4ManualSceneId.length() > 0)
                    mSceneMap.remove(mKey4ManualSceneId);
                mKey4ManualSceneId = null;
                mKey4ManualSceneName = null;
            }
            mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_5;
        } else if (mCurrentKey.equals(CTSL.SCENE_SWITCH_KEY_CODE_5)) {
            if (!object.isEmpty() && !"{}".equals(object.toJSONString())) {
                mSceneContentText5.setText(object.getString("name"));
                mKey5ManualSceneName = object.getString("name");
                mKey5ManualSceneId = object.getString("msId");
                mSceneMap.put(mKey5ManualSceneId, mKey5ManualSceneName);
            } else {
                mSceneContentText5.setText(R.string.no_bind_scene);
                if (mKey5ManualSceneId != null &&
                        mKey5ManualSceneId.length() > 0)
                    mSceneMap.remove(mKey5ManualSceneId);
                mKey5ManualSceneId = null;
                mKey5ManualSceneName = null;
            }
            mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_6;
        } else if (mCurrentKey.equals(CTSL.SCENE_SWITCH_KEY_CODE_6)) {
            if (!object.isEmpty() && !"{}".equals(object.toJSONString())) {
                mSceneContentText6.setText(object.getString("name"));
                mKey6ManualSceneName = object.getString("name");
                mKey6ManualSceneId = object.getString("msId");
                mSceneMap.put(mKey6ManualSceneId, mKey6ManualSceneName);
            } else {
                mSceneContentText6.setText(R.string.no_bind_scene);
                if (mKey6ManualSceneId != null &&
                        mKey6ManualSceneId.length() > 0)
                    mSceneMap.remove(mKey6ManualSceneId);
                mKey6ManualSceneId = null;
                mKey6ManualSceneName = null;
            }
            mCurrentKey = "-1";
        }
        if (!mCurrentKey.equals("-1"))
            mSceneManager.getExtendedProperty(mIOTId, mCurrentKey,
                    mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
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

