package com.rexense.imoco.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.presenter.CodeMapper;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.PluginHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Creator: xieshaobing
 * creat time: 2020-12-07 17:14
 * Description: 四键开关详细界面
 */
public class DetailFourSwitchActivity2 extends DetailActivity {
    private int mState1 = 0;
    private int mState2 = 0;
    private int mState3 = 0;
    private int mState4 = 0;
    private ImageView mImgOperate1;
    private ImageView mImgOperate2;
    private ImageView mImgOperate3;
    private ImageView mImgOperate4;
    private TextView mStateName1;
    private TextView mStateValue1;
    private TextView mStateName2;
    private TextView mStateValue2;
    private TextView mStateName3;
    private TextView mStateValue3;
    private TextView mStateName4;
    private TextView mStateValue4;
    private TSLHelper mTSLHelper;
    private TextView mTimerIcTV;
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.back_light_root)
    RelativeLayout mBackLightRoot;

    private int mBackLightState = 1;
    private static final int TAG_GET_EXTENDED_PRO = 10000;
    private SceneManager mSceneManager;
    private MyHandler mHandler;
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;
    private String mKeyName4;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1));
            mImgOperate1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1));
            if (stateEntry != null) {
                //mStateName1.setText(stateEntry.name + ":");
                mStateValue1.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2));
            mImgOperate2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2));
            if (stateEntry != null) {
                //mStateName2.setText(stateEntry.name + ":");
                mStateValue2.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3));
            mImgOperate3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3));
            if (stateEntry != null) {
                //mStateName3.setText(stateEntry.name + ":");
                mStateValue3.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4).length() > 0) {
            mState4 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4));
            mImgOperate4.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_4, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_4, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4));
            if (stateEntry != null) {
                //mStateName4.setText(stateEntry.name + ":");
                mStateValue4.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.FWS_P_BackLightMode) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_BackLightMode).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_BackLightMode));
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
        // 注意：公共操作已经在父类中处理
        ButterKnife.bind(this);

        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);

        // 键1操作事件处理
        OnClickListener operateOnClickListener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate1 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate1);
        mImgOperate1.setOnClickListener(operateOnClickListener1);

        mStateName1 = (TextView) findViewById(R.id.detailTwoSwitchLblStateName1);
        mStateName1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit(R.id.detailTwoSwitchLblStateName1);
            }
        });
        mStateValue1 = (TextView) findViewById(R.id.detailTwoSwitchLblStateValue1);

        mStateName2 = (TextView) findViewById(R.id.detailTwoSwitchLblStateName2);
        mStateName2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit(R.id.detailTwoSwitchLblStateName2);
            }
        });
        mStateValue2 = (TextView) findViewById(R.id.detailTwoSwitchLblStateValue2);

        mStateName3 = (TextView) findViewById(R.id.detailFourSwitchLblStateName3);
        mStateName3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit(R.id.detailFourSwitchLblStateName3);
            }
        });
        mStateValue3 = (TextView) findViewById(R.id.detailFourSwitchLblStateValue3);

        mStateName4 = (TextView) findViewById(R.id.detailFourSwitchLblStateName4);
        mStateName4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit(R.id.detailFourSwitchLblStateName4);
            }
        });
        mStateValue4 = (TextView) findViewById(R.id.detailFourSwitchLblStateValue4);

        // 键2操作事件处理
        OnClickListener operateOnClickListener2 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate2 = (ImageView) findViewById(R.id.detailFourSwitchImgOperate2);
        mImgOperate2.setOnClickListener(operateOnClickListener2);

        // 键3操作事件处理
        OnClickListener operateOnClickListener3 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState3 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate3 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate3);
        mImgOperate3.setOnClickListener(operateOnClickListener3);

        // 键4操作事件处理
        OnClickListener operateOnClickListener4 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState4 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_4}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_PowerSwitch_4}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate4 = (ImageView) findViewById(R.id.detailFourSwitchImgOperate4);
        mImgOperate4.setOnClickListener(operateOnClickListener4);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout) findViewById(R.id.detailTwoSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailFourSwitchActivity2.this, mIOTId, mProductKey);
            }
        });

        mTimerIcTV = (TextView) findViewById(R.id.timer_ic_tv);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mTimerIcTV.setTypeface(iconfont);
        mBackLightIc.setTypeface(iconfont);

        mBackLightLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
                }
            }
        });

        initStatusBar();
        initKeyNickName();
        mBackLightRoot.setVisibility(View.VISIBLE);
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mHandler);
    }

    private static class MyResponseErrHandler extends Handler {
        private final WeakReference<DetailFourSwitchActivity2> ref;

        public MyResponseErrHandler(DetailFourSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailFourSwitchActivity2 activity = ref.get();
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
                    jsonObject.put(CTSL.FWS_P_PowerSwitch_1, activity.mStateName1.getText().toString());
                    jsonObject.put(CTSL.FWS_P_PowerSwitch_2, activity.mStateName2.getText().toString());
                    jsonObject.put(CTSL.FWS_P_PowerSwitch_3, activity.mStateName3.getText().toString());
                    jsonObject.put(CTSL.FWS_P_PowerSwitch_4, activity.mStateName4.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId, Constant.TAG_DEV_KEY_NICKNAME,
                            jsonObject.toJSONString(), null, null, null);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DetailFourSwitchActivity2> ref;

        public MyHandler(DetailFourSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailFourSwitchActivity2 activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
                    activity.mStateName1.setText(object.getString(CTSL.FWS_P_PowerSwitch_1));
                    activity.mStateName2.setText(object.getString(CTSL.FWS_P_PowerSwitch_2));
                    activity.mStateName3.setText(object.getString(CTSL.FWS_P_PowerSwitch_3));
                    activity.mStateName4.setText(object.getString(CTSL.FWS_P_PowerSwitch_4));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    activity.mStateName1.setText(activity.mKeyName1);
                    activity.mStateName2.setText(activity.mKeyName2);
                    activity.mStateName3.setText(activity.mKeyName3);
                    activity.mStateName4.setText(activity.mKeyName4);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.mResultObj);
                    ToastUtils.showShortToast(activity, R.string.set_success);
                    break;
                }
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
        if (resId == R.id.detailTwoSwitchLblStateName1) {
            // 按键1
            nameEt.setText(mStateName1.getText().toString());
        } else if (resId == R.id.detailTwoSwitchLblStateName2) {
            // 按键2
            nameEt.setText(mStateName2.getText().toString());
        } else if (resId == R.id.detailFourSwitchLblStateName3) {
            // 按键3
            nameEt.setText(mStateName3.getText().toString());
        } else if (resId == R.id.detailFourSwitchLblStateName4) {
            // 按键4
            nameEt.setText(mStateName4.getText().toString());
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
                        && mStateName1.getText().toString().length() > 10
                        && mStateName2.getText().toString().length() > 10
                        && mStateName3.getText().toString().length() > 10
                        && mStateName4.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(DetailFourSwitchActivity2.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mStateName1.getText().toString().length() == 0
                        && mStateName2.getText().toString().length() == 0
                        && mStateName3.getText().toString().length() == 0
                        && mStateName4.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(DetailFourSwitchActivity2.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(DetailFourSwitchActivity2.this, R.string.is_setting);
                if (resId == R.id.detailTwoSwitchLblStateName1) {
                    // 按键1
                    mKeyName1 = nameEt.getText().toString();
                    mKeyName2 = mStateName2.getText().toString();
                    mKeyName3 = mStateName3.getText().toString();
                    mKeyName4 = mStateName4.getText().toString();
                } else if (resId == R.id.detailTwoSwitchLblStateName2) {
                    // 按键2
                    mKeyName1 = mStateName1.getText().toString();
                    mKeyName2 = nameEt.getText().toString();
                    mKeyName3 = mStateName3.getText().toString();
                    mKeyName4 = mStateName4.getText().toString();
                } else if (resId == R.id.detailFourSwitchLblStateName3) {
                    // 按键3
                    mKeyName1 = mStateName1.getText().toString();
                    mKeyName2 = mStateName2.getText().toString();
                    mKeyName3 = nameEt.getText().toString();
                    mKeyName4 = mStateName4.getText().toString();
                } else if (resId == R.id.detailFourSwitchLblStateName4) {
                    // 按键4
                    mKeyName1 = mStateName1.getText().toString();
                    mKeyName2 = mStateName2.getText().toString();
                    mKeyName3 = mStateName3.getText().toString();
                    mKeyName4 = nameEt.getText().toString();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.FWS_P_PowerSwitch_1, mKeyName1);
                jsonObject.put(CTSL.FWS_P_PowerSwitch_2, mKeyName2);
                jsonObject.put(CTSL.FWS_P_PowerSwitch_3, mKeyName3);
                jsonObject.put(CTSL.FWS_P_PowerSwitch_4, mKeyName4);
                mResultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mHandler);
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

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.appbgcolor));
        }
    }
}