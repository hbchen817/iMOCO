package com.rexense.imoco.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
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
import com.rexense.imoco.utility.ResponseMessageUtil;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 一键开关详细界面
 */
public class DetailOneSwitchActivity2 extends DetailActivity {
    private int mState = 0;
    private int mBackLightState = 1;
    private ImageView mImgOperate;
    private TextView mStateName;
    private TextView mStateValue;
    private TextView mBacklightIc;
    private TextView mBacklightTV;
    private TSLHelper mTSLHelper;
    private RelativeLayout mBackLightLayout;
    private RelativeLayout mBackLightRoot;

    private SceneManager mSceneManager;
    private MyHandler mhandler;
    private String mKeyName;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        ViseLog.d(new Gson().toJson(propertyEntry));
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1).length() > 0) {
            mState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1));
            mImgOperate.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.OWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.OWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1));
            if (stateEntry != null) {
                //mStateName.setText(stateEntry.name + ":");
                mStateValue.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.OWS_P_BackLightMode) != null && propertyEntry.getPropertyValue(CTSL.OWS_P_BackLightMode).length() > 0) {
            int state = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.OWS_P_BackLightMode));
            mBackLightState = state;
            switch (state) {
                case 0: {
                    // 关闭背光
                    mBacklightIc.setTextColor(getResources().getColor(R.color.gray3));
                    mBacklightTV.setTextColor(getResources().getColor(R.color.gray3));
                    break;
                }
                case 1: {
                    // 打开背光
                    mBacklightIc.setTextColor(getResources().getColor(R.color.blue2));
                    mBacklightTV.setTextColor(getResources().getColor(R.color.blue2));
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

        // 设备操作事件处理
        OnClickListener operateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate = (ImageView) findViewById(R.id.detailOneSwitchImgOperate);
        mImgOperate.setOnClickListener(operateOnClickListener);

        mStateName = (TextView) findViewById(R.id.detailOneSwitchLblStateName);
        mStateName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit();
            }
        });

        mStateValue = (TextView) findViewById(R.id.detailOneSwitchLblStateValue);

        TextView timerIc = (TextView) findViewById(R.id.timer_ic_tv);
        mBacklightTV = (TextView) findViewById(R.id.back_light_txt);
        mBacklightIc = (TextView) findViewById(R.id.back_light_tv);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        timerIc.setTypeface(iconfont);
        mBacklightIc.setTypeface(iconfont);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout) findViewById(R.id.detailOneSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailOneSwitchActivity2.this, mIOTId, mProductKey);
            }
        });

        initStatusBar();

        mBackLightLayout = (RelativeLayout) findViewById(R.id.back_light_layout);
        mBackLightLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackLightState == 0)
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
                else
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
            }
        });
        mBackLightRoot = (RelativeLayout) findViewById(R.id.back_light_root);

        mhandler = new MyHandler(this);
        mSceneManager = new SceneManager(this);
        //mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, mCommitFailureHandler, mResponseErrorHandler, mhandler);
        initKeyNickName();

        mBackLightRoot.setVisibility(View.VISIBLE);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.appbgcolor));
        }
    }

    private static final int TAG_GET_EXTENDED_PRO = 10000;

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mhandler);
    }

    private static class MyResponseErrHandler extends Handler {
        private final WeakReference<DetailOneSwitchActivity2> ref;

        public MyResponseErrHandler(DetailOneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailOneSwitchActivity2 activity = ref.get();
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
                    jsonObject.put(CTSL.OWS_P_PowerSwitch_1, activity.mStateName.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                            null, null, null);
                }
            }
        }
    }

    // 显示按键名称修改对话框
    private void showKeyNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.key_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setHint(getString(R.string.pls_input_key_name));
        nameEt.setText(mStateName.getText().toString());
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
                if (nameEt.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(DetailOneSwitchActivity2.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(DetailOneSwitchActivity2.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(DetailOneSwitchActivity2.this, R.string.is_setting);
                mKeyName = nameEt.getText().toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.OWS_P_PowerSwitch_1, mKeyName);
                mResultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mhandler);
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

    private JSONObject mResultObj;

    private static class MyHandler extends Handler {
        private final WeakReference<DetailOneSwitchActivity2> ref;

        public MyHandler(DetailOneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailOneSwitchActivity2 activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                String keyName = jsonObject.getString(CTSL.OWS_P_PowerSwitch_1);
                activity.mStateName.setText(keyName);
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                QMUITipDialogUtil.dismiss();
                activity.mStateName.setText(activity.mKeyName);
                DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.mResultObj);
                ToastUtils.showShortToast(activity, R.string.set_success);
            } else if (msg.what == Constant.MSG_CALLBACK_IDENTIFIER_LIST) {
                String result = (String) msg.obj;
                if (result.substring(0, 1).equals("[")) {
                    result = "{\"data\":" + result + "}";
                    JSONObject o = JSON.parseObject(result);
                    JSONArray a = o.getJSONArray("data");
                    for (int i = 0; i < a.size(); i++) {
                        JSONObject object = a.getJSONObject(i);
                        String key = object.getString("identifier");
                        if (CTSL.OWS_P_PowerSwitch_1.equals(key)) {
                            String name = object.getString("name");
                            activity.mStateName.setText(name.trim());
                        }
                    }
                }
            } else if (msg.what == TAG_GET_EXTENDED_PRO) {
                JSONObject object = JSONObject.parseObject((String) msg.obj);
                DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
                activity.mStateName.setText(object.getString(CTSL.OWS_P_PowerSwitch_1));
            }
        }
    }

    // 响应错误处理器
    protected Handler mResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dismissQMUIDialog();
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                ViseLog.d(new Gson().toJson(responseErrorEntry));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()));
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
                //非OTA信息查询失败才作提示
                if (!responseErrorEntry.path.equalsIgnoreCase(Constant.API_PATH_GETOTAFIRMWAREINFO)
                        && (responseErrorEntry.code != 6741)) {
                    Toast.makeText(DetailOneSwitchActivity2.this, TextUtils.isEmpty(responseErrorEntry.localizedMsg) ? getString(R.string.api_responseerror_hint) : ResponseMessageUtil.replaceMessage(responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                }
                notifyFailureOrError(2);
            }
            return false;
        }
    });
}