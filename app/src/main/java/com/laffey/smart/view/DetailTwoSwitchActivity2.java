package com.laffey.smart.view;

import android.app.Activity;
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
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 两键开关详细界面
 */
public class DetailTwoSwitchActivity2 extends DetailActivity {
    private int mState1 = 0;
    private int mState2 = 0;
    private int mBackLightState = 1;
    private ImageView mImgOperate1;
    private ImageView mImgOperate2;
    private TextView mStateName1;
    private TextView mStateValue1;
    private TextView mStateName2;
    private TextView mStateValue2;
    private TSLHelper mTSLHelper;
    private TextView mTimerIcTV;
    private TextView mBackLightIc;
    private TextView mBackLightTV;
    private RelativeLayout mBackLightLayout;

    private final int TAG_GET_EXTENDED_PRO = 10000;
    private SceneManager mSceneManager;
    private MyHandler mHandler;
    private String mKeyName1;
    private String mKeyName2;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1));
            mImgOperate1.setBackgroundResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.TWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1));
            if (stateEntry != null) {
                //mStateName1.setText(stateEntry.name + ":");
                mStateValue1.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2));
            mImgOperate2.setBackgroundResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.TWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2));
            if (stateEntry != null) {
                //mStateName2.setText(stateEntry.name + ":");
                mStateValue2.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P_BackLightMode) != null && propertyEntry.getPropertyValue(CTSL.TWS_P_BackLightMode).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P_BackLightMode));
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

        this.mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);

        // 键1操作事件处理
        OnClickListener operateOnClickListener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate1 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate1);
        this.mImgOperate1.setOnClickListener(operateOnClickListener1);

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

        // 键2操作事件处理
        OnClickListener operateOnClickListener2 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate2 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate2);
        this.mImgOperate2.setOnClickListener(operateOnClickListener2);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout) findViewById(R.id.detailTwoSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailTwoSwitchActivity2.this, mIOTId, mProductKey);
            }
        });

        initStatusBar();

        mTimerIcTV = (TextView) findViewById(R.id.timer_ic_tv);
        mBackLightIc = (TextView) findViewById(R.id.back_light_ic);
        mBackLightTV = (TextView) findViewById(R.id.back_light_tv);
        mBackLightLayout = (RelativeLayout) findViewById(R.id.back_light_layout);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mTimerIcTV.setTypeface(iconfont);
        mBackLightIc.setTypeface(iconfont);

        mBackLightLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
                }
            }
        });

        initKeyNickName();
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
            case R.id.detailTwoSwitchLblStateName1: {
                // 按键1
                nameEt.setText(mStateName1.getText().toString());
                break;
            }
            case R.id.detailTwoSwitchLblStateName2: {
                // 按键2
                nameEt.setText(mStateName2.getText().toString());
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
                QMUITipDialogUtil.showLoadingDialg(DetailTwoSwitchActivity2.this, R.string.is_setting);
                switch (resId) {
                    case R.id.detailTwoSwitchLblStateName1: {
                        // 按键1
                        mKeyName1 = nameEt.getText().toString();
                        mKeyName2 = mStateName2.getText().toString();
                        break;
                    }
                    case R.id.detailTwoSwitchLblStateName2: {
                        // 按键2
                        mKeyName1 = mStateName1.getText().toString();
                        mKeyName2 = nameEt.getText().toString();
                        break;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.TWS_P_PowerSwitch_1, mKeyName1);
                jsonObject.put(CTSL.TWS_P_PowerSwitch_2, mKeyName2);
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
            getWindow().setStatusBarColor(getColor(R.color.appbgcolor));
        }
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mHandler);
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
                    jsonObject.put(CTSL.TWS_P_PowerSwitch_1, mStateName1.getText().toString());
                    jsonObject.put(CTSL.TWS_P_PowerSwitch_2, mStateName2.getText().toString());
                    mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), null, null, null);
                }
            }
        }
    }

    private class MyHandler extends Handler {
        private WeakReference<Activity> ref;

        public MyHandler(Activity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
            switch (msg.what) {
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    mStateName1.setText(object.getString(CTSL.TWS_P_PowerSwitch_1));
                    mStateName2.setText(object.getString(CTSL.TWS_P_PowerSwitch_2));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mStateName1.setText(mKeyName1);
                    mStateName2.setText(mKeyName2);
                    ToastUtils.showShortToast(DetailTwoSwitchActivity2.this, R.string.set_success);
                    break;
                }
            }
        }
    }
}