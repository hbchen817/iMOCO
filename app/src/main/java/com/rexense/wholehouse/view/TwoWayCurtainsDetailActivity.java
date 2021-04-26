package com.rexense.wholehouse.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.PluginHelper;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 两路窗帘Activity
 */

public class TwoWayCurtainsDetailActivity extends DetailActivity {
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.two_status)
    TextView mTwoStatus;
    @BindView(R.id.close_curtains)
    TextView mCloseCurtains;
    @BindView(R.id.open_curtains)
    TextView mOpenCurtains;
    @BindView(R.id.stop_curtains)
    TextView mStopCurtains;
    @BindView(R.id.two_close_curtains)
    TextView mTwoCloseCurtains;
    @BindView(R.id.two_open_curtains)
    TextView mTwoOpenCurtains;
    @BindView(R.id.two_stop_curtains)
    TextView mTwoStopCurtains;
    @BindView(R.id.timer_ic_tv)
    TextView mTimerIcTV;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.key_1_tv)
    TextView mKeyName1TV;
    @BindView(R.id.key_2_tv)
    TextView mKeyName2TV;
    @BindView(R.id.timer_layout)
    RelativeLayout mTimerLayout;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.title_1_tv)
    TextView mTitle1TV;
    @BindView(R.id.title_2_tv)
    TextView mTitle2TV;

    private TSLHelper mTSLHelper;
    private SceneManager mSceneManager;
    private final int TAG_GET_EXTENDED_PRO = 10000;
    private MyHandler mHandler;
    private String mKeyName1;
    private String mKeyName2;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        ViseLog.d(new Gson().toJson(propertyEntry));
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.TWC_CurtainConrtol) != null && propertyEntry.getPropertyValue(CTSL.TWC_CurtainConrtol).length() > 0) {
            int state = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWC_CurtainConrtol));
            switch (state) {
                case 0: {
                    // 暂停窗帘
                    mStatus.setText(getString(R.string.stop_curtains_2));
                    setSwitch(R.id.stop_curtains);
                    break;
                }
                case 1: {
                    // 打开窗帘
                    mStatus.setText(getString(R.string.open_curtains));
                    setSwitch(R.id.open_curtains);
                    break;
                }
                case 2: {
                    // 关闭窗帘
                    mStatus.setText(getString(R.string.close_curtains));
                    setSwitch(R.id.close_curtains);
                    break;
                }
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.TWC_InnerCurtainOperation) != null && propertyEntry.getPropertyValue(CTSL.TWC_InnerCurtainOperation).length() > 0) {
            int state = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWC_InnerCurtainOperation));
            switch (state) {
                case 0: {
                    // 暂停窗帘
                    mTwoStatus.setText(getString(R.string.stop_curtains_2));
                    setSecondSwitch(R.id.two_stop_curtains);
                    break;
                }
                case 1: {
                    // 打开窗帘
                    mTwoStatus.setText(getString(R.string.open_curtains));
                    setSecondSwitch(R.id.two_open_curtains);
                    break;
                }
                case 2: {
                    // 关闭窗帘
                    mTwoStatus.setText(getString(R.string.close_curtains));
                    setSecondSwitch(R.id.two_close_curtains);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_two_way_curtains);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mTimerIcTV.setTypeface(iconfont);
        mBackLightTV.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);

        initStatusBar();
        initKeyNickName();
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
                    jsonObject.put(CTSL.TWC_CurtainConrtol, mKeyName1TV.getText().toString());
                    jsonObject.put(CTSL.TWC_InnerCurtainOperation, mKeyName2TV.getText().toString());
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
                    DeviceBuffer.addExtendedInfo(mIOTId, object);
                    mKeyName1TV.setText(object.getString(CTSL.TWC_CurtainConrtol));
                    mKeyName2TV.setText(object.getString(CTSL.TWC_InnerCurtainOperation));
                    mTitle1TV.setText(object.getString(CTSL.TWC_CurtainConrtol));
                    mTitle2TV.setText(object.getString(CTSL.TWC_InnerCurtainOperation));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKeyName1TV.setText(mKeyName1);
                    mTitle1TV.setText(mKeyName1);
                    mKeyName2TV.setText(mKeyName2);
                    mTitle2TV.setText(mKeyName2);
                    DeviceBuffer.addExtendedInfo(mIOTId, mResultObj);
                    ToastUtils.showShortToast(TwoWayCurtainsDetailActivity.this, R.string.set_success);
                    break;
                }
            }
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.appbgcolor));
        }
    }

    private void setSwitch(int id) {
        Drawable drawable = getResources().getDrawable(R.drawable.one_switch_background);
        mCloseCurtains.setBackground(id == mCloseCurtains.getId() ? drawable : null);
        mOpenCurtains.setBackground(id == mOpenCurtains.getId() ? drawable : null);
        mStopCurtains.setBackground(id == mStopCurtains.getId() ? drawable : null);
    }

    private void setSecondSwitch(int id) {
        Drawable drawable = getResources().getDrawable(R.drawable.one_switch_background);
        mTwoCloseCurtains.setBackground(id == mTwoCloseCurtains.getId() ? drawable : null);
        mTwoOpenCurtains.setBackground(id == mTwoOpenCurtains.getId() ? drawable : null);
        mTwoStopCurtains.setBackground(id == mTwoStopCurtains.getId() ? drawable : null);
    }

    @OnClick({R.id.close_curtains, R.id.open_curtains, R.id.stop_curtains, R.id.two_close_curtains, R.id.two_open_curtains, R.id.two_stop_curtains,
            R.id.key_1_tv, R.id.key_2_tv, R.id.timer_layout, R.id.back_light_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close_curtains: {
                mStatus.setText(((TextView) view).getText());
                setSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_CLOSE});
                break;
            }
            case R.id.open_curtains: {
                mStatus.setText(((TextView) view).getText());
                setSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_OPEN});
                break;
            }
            case R.id.stop_curtains: {
                mStatus.setText(((TextView) view).getText());
                setSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_STOP});
                break;
            }
            case R.id.two_close_curtains: {
                mTwoStatus.setText(((TextView) view).getText());
                setSecondSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_InnerCurtainOperation}, new String[]{"" + CTSL.WC_STATUS_CLOSE});
                break;
            }
            case R.id.two_open_curtains: {
                mTwoStatus.setText(((TextView) view).getText());
                setSecondSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_InnerCurtainOperation}, new String[]{"" + CTSL.WC_STATUS_OPEN});
                break;
            }
            case R.id.two_stop_curtains:
                mTwoStatus.setText(((TextView) view).getText());
                setSecondSwitch(view.getId());
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_InnerCurtainOperation}, new String[]{"" + CTSL.WC_STATUS_STOP});
                break;
            case R.id.key_1_tv: {
                // 一路窗帘
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWC_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
                showKeyNameDialogEdit(R.id.key_1_tv);
                break;
            }
            case R.id.key_2_tv: {
                // 二路窗帘
                showKeyNameDialogEdit(R.id.key_2_tv);
                break;
            }
            case R.id.timer_layout: {
                // 定时
                PluginHelper.cloudTimer(TwoWayCurtainsDetailActivity.this, mIOTId, mProductKey);
                break;
            }
            case R.id.back_light_layout: {
                // 背光
                ViseLog.d("背光");
                break;
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
                        && mKeyName2TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(TwoWayCurtainsDetailActivity.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKeyName1TV.getText().toString().length() == 0
                        && mKeyName2TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(TwoWayCurtainsDetailActivity.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(TwoWayCurtainsDetailActivity.this, R.string.is_setting);
                switch (resId) {
                    case R.id.key_1_tv: {
                        // 按键1
                        mKeyName1 = nameEt.getText().toString();
                        mKeyName2 = mKeyName2TV.getText().toString();
                        break;
                    }
                    case R.id.key_2_tv: {
                        // 按键2
                        mKeyName1 = mKeyName1TV.getText().toString();
                        mKeyName2 = nameEt.getText().toString();
                        break;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.TWC_CurtainConrtol, mKeyName1);
                jsonObject.put(CTSL.TWC_InnerCurtainOperation, mKeyName2);
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
}