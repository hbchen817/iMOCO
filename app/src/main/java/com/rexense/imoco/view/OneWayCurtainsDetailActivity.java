package com.rexense.imoco.view;

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
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.PluginHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 单路窗帘Activity
 */

public class OneWayCurtainsDetailActivity extends DetailActivity {

    @BindView(R.id.status)
    TextView mStatusText;
    @BindView(R.id.close_curtains)
    TextView mCloseCurtains;
    @BindView(R.id.open_curtains)
    TextView mOpenCurtains;
    @BindView(R.id.stop_curtains)
    TextView mStopCurtains;
    @BindView(R.id.timer_ic_tv)
    TextView mTimerIcTV;
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.key_1_tv)
    TextView mKeyName1TV;
    @BindView(R.id.timer_layout)
    RelativeLayout mTimerLayout;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;
    @BindView(R.id.title_tv)
    TextView mTitleTV;

    private int mState;
    private int mBackLightState = 1;
    private TSLHelper mTSLHelper;
    private final int TAG_GET_EXTENDED_PRO = 10000;
    private SceneManager mSceneManager;
    private MyHandler mHandler;
    private String mKeyName;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.WC_CurtainConrtol) != null && propertyEntry.getPropertyValue(CTSL.WC_CurtainConrtol).length() > 0) {
            mState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.WC_CurtainConrtol));
            switch (mState) {
                case 0: {
                    // 暂停窗帘
                    mStatusText.setText(getString(R.string.stop_curtains_2));
                    setSwitch(R.id.stop_curtains);
                    break;
                }
                case 1: {
                    // 打开窗帘
                    mStatusText.setText(getString(R.string.open_curtains));
                    setSwitch(R.id.open_curtains);
                    break;
                }
                case 2: {
                    // 关闭窗帘
                    mStatusText.setText(getString(R.string.close_curtains));
                    setSwitch(R.id.close_curtains);
                    break;
                }
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.WC_BackLight) != null && propertyEntry.getPropertyValue(CTSL.WC_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.WC_BackLight));
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mTimerIcTV.setTypeface(iconfont);
        mBackLightIc.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);

        initStatusBar();
        mKeyName1TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyNameDialogEdit();
            }
        });

        mTimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(OneWayCurtainsDetailActivity.this, mIOTId, mProductKey);
            }
        });

        mBackLightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViseLog.d("mBackLightState = " + mBackLightState);
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.WC_BackLight}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.WC_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
                }
            }
        });
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
                    jsonObject.put(CTSL.WC_CurtainConrtol, mKeyName1TV.getText().toString());
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
                    mKeyName1TV.setText(object.getString(CTSL.WC_CurtainConrtol));
                    mTitleTV.setText(object.getString(CTSL.WC_CurtainConrtol));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKeyName1TV.setText(mKeyName);
                    mTitleTV.setText(mKeyName);
                    DeviceBuffer.addExtendedInfo(mIOTId, mResultObj);
                    ToastUtils.showShortToast(OneWayCurtainsDetailActivity.this, R.string.set_success);
                    break;
                }
            }
        }
    }

    private JSONObject mResultObj;

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
        nameEt.setText(mKeyName1TV.getText().toString());
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
                        && mKeyName1TV.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(OneWayCurtainsDetailActivity.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mKeyName1TV.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(OneWayCurtainsDetailActivity.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(OneWayCurtainsDetailActivity.this, R.string.is_setting);
                mKeyName = nameEt.getText().toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.WC_CurtainConrtol, mKeyName);
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

    @OnClick({R.id.close_curtains, R.id.open_curtains, R.id.stop_curtains})
    public void onViewClicked(View view) {
        mStatusText.setText(((TextView) view).getText());
        setSwitch(view.getId());
        switch (view.getId()) {
            case R.id.close_curtains: {
                // 关闭窗帘
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.WC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_CLOSE});
                break;
            }
            case R.id.open_curtains: {
                // 打开窗帘
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.WC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_OPEN});
                break;
            }
            case R.id.stop_curtains: {
                // 暂停窗帘
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.WC_CurtainConrtol}, new String[]{"" + CTSL.WC_STATUS_STOP});
                break;
            }
        }
    }

    private void setSwitch(int id) {
        Drawable drawable = getResources().getDrawable(R.drawable.one_switch_background);
        mCloseCurtains.setBackground(id == mCloseCurtains.getId() ? drawable : null);
        mOpenCurtains.setBackground(id == mOpenCurtains.getId() ? drawable : null);
        mStopCurtains.setBackground(id == mStopCurtains.getId() ? drawable : null);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.appbgcolor));
        }
    }
}