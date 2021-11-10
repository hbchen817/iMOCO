package com.laffey.smart.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 一键开关详细界面
 */
public class DetailThreeSwitchActivity extends DetailActivity implements OnClickListener {
    private int mState1 = 0;
    private int mState2 = 0;
    private int mState3 = 0;
    private int mBackLightState = 1;
    private ImageView mImgOperate1;
    private ImageView mImgOperate2;
    private ImageView mImgOperate3;
    private TextView mStateName1;
    private TextView mStateName2;
    private TextView mStateName3;
    private TextView mStateValue1;
    private TextView mStateValue2;
    private TextView mStateValue3;
    private TextView mBackLightIc;
    private TextView mBackLightTV;
    private RelativeLayout mTimerLayout;
    private RelativeLayout mBackLightLayout;
    private RelativeLayout mAssociatedLayout;
    private LinearLayout mRootLayout;

    private static final int TAG_GET_EXTENDED_PRO = 10000;
    private SceneManager mSceneManager;
    private MyHandler mHandler;
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;

    private TSLHelper mTSLHelper;
    private Typeface mIconFont;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_1));
            mImgOperate1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P3_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.TWS_P3_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_1));
            if (stateEntry != null) {
                //mStateName.setText(stateEntry.name + ":");
                mStateValue1.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_2));
            mImgOperate2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P3_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_2)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.TWS_P3_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_2));
            if (stateEntry != null) {
                //mStateName.setText(stateEntry.name + ":");
                mStateValue2.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_3));
            mImgOperate3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P3_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_3)));
            ETSL.stateEntry stateEntry = CodeMapper.processSwitchPropertyState(this, mProductKey, CTSL.TWS_P3_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.TWS_P3_PowerSwitch_3));
            if (stateEntry != null) {
                //mStateName.setText(stateEntry.name + ":");
                mStateValue3.setText(stateEntry.value);
            }
        }

        if (propertyEntry.getPropertyValue(CTSL.TWS_P3_BackLightMode) != null && propertyEntry.getPropertyValue(CTSL.TWS_P3_BackLightMode).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P3_BackLightMode));
            switch (mBackLightState) {
                case 0: {
                    // 关闭
                    mBackLightIc.setTextColor(ContextCompat.getColor(this, R.color.gray3));
                    mBackLightTV.setTextColor(ContextCompat.getColor(this, R.color.gray3));
                    break;
                }
                case 1: {
                    // 打开
                    mBackLightIc.setTextColor(ContextCompat.getColor(this, R.color.blue2));
                    mBackLightTV.setTextColor(ContextCompat.getColor(this, R.color.blue2));
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

        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);

        TextView timerIcTV = (TextView) findViewById(R.id.timer_ic_tv);
        mBackLightIc = (TextView) findViewById(R.id.back_light_ic);
        mBackLightTV = (TextView) findViewById(R.id.back_light_tv);
        TextView associatedTv = (TextView) findViewById(R.id.associated_tv);
        mRootLayout = (LinearLayout) findViewById(R.id.detailTwoSwitchLl);

        mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        timerIcTV.setTypeface(mIconFont);
        mBackLightIc.setTypeface(mIconFont);
        associatedTv.setTypeface(mIconFont);

        // 设备操作事件处理
        mImgOperate1 = (ImageView) findViewById(R.id.detailThreeSwitchImgOperate1);
        mImgOperate1.setOnClickListener(this);

        mImgOperate2 = (ImageView) findViewById(R.id.detailThreeSwitchImgOperate2);
        mImgOperate2.setOnClickListener(this);

        mImgOperate3 = (ImageView) findViewById(R.id.detailThreeSwitchImgOperate3);
        mImgOperate3.setOnClickListener(this);

        mStateName1 = (TextView) findViewById(R.id.detailThreeSwitchLblStateName1);
        mStateName1.setOnClickListener(this);
        mStateValue1 = (TextView) findViewById(R.id.detailThreeSwitchLblStateValue1);

        mStateName2 = (TextView) findViewById(R.id.detailThreeSwitchLblStateName2);
        mStateName2.setOnClickListener(this);
        mStateValue2 = (TextView) findViewById(R.id.detailThreeSwitchLblStateValue2);

        mStateName3 = (TextView) findViewById(R.id.detailThreeSwitchLblStateName3);
        mStateName3.setOnClickListener(this);
        mStateValue3 = (TextView) findViewById(R.id.detailThreeSwitchLblStateValue3);

        // 云端定时处理
        mTimerLayout = (RelativeLayout) findViewById(R.id.timer_layout);
        mTimerLayout.setOnClickListener(this);
        // 背光灯控制
        mBackLightLayout = (RelativeLayout) findViewById(R.id.back_light_layout);
        mBackLightLayout.setOnClickListener(this);

        // 云端定时处理
        /*RelativeLayout timer = (RelativeLayout) findViewById(R.id.timer_layout);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailThreeSwitchActivity.this, mIOTId, mProductKey);
            }
        });*/

        initStatusBar();
        initKeyNickName();

        mAssociatedLayout = (RelativeLayout) findViewById(R.id.associated_layout);
        mAssociatedLayout.setOnClickListener(this);
    }

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mHandler);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mAssociatedLayout.getId()) {
            // 双控
            showAssociatedPopupWindow();
        } else if (v.getId() == mImgOperate1.getId()) {
            // 按键触发1
            if (mState1 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (v.getId() == mImgOperate2.getId()) {
            // 触发按键2
            if (mState2 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (v.getId() == mImgOperate3.getId()) {
            // 触发按键3
            if (mState3 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (v.getId() == mStateName1.getId()) {
            // 按键1昵称
            showKeyNameDialogEdit(R.id.detailThreeSwitchLblStateName1);
        } else if (v.getId() == mStateName2.getId()) {
            // 按键2昵称
            showKeyNameDialogEdit(R.id.detailThreeSwitchLblStateName2);
        } else if (v.getId() == mStateName3.getId()) {
            // 按键3昵称
            showKeyNameDialogEdit(R.id.detailThreeSwitchLblStateName3);
        } else if (v.getId() == mTimerLayout.getId()) {
            // 定时
            PluginHelper.cloudTimer(DetailThreeSwitchActivity.this, mIOTId, mProductKey);
        } else if (v.getId() == mBackLightLayout.getId()) {
            // 背光灯
            if (mBackLightState == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P3_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
            }
        }
    }

    // 双控弹窗
    private void showAssociatedPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_associated_switch, null);

        RecyclerView recyclerView = contentView.findViewById(R.id.associated_rv);
        List<String> list = new ArrayList<>();
        list.add(mStateName1.getText().toString());
        list.add(mStateName2.getText().toString());
        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_key, list) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                int pos = list.indexOf(s);
                holder.setText(R.id.key_tv, s);
                TextView nameTV = holder.getView(R.id.key_tv);
                nameTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (pos) {
                            case 0: {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_LOCALCONFIG_1}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                                break;
                            }
                            case 1: {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_LOCALCONFIG_2}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                                break;
                            }
                            case 2: {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_LOCALCONFIG_3}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                                break;
                            }
                            case 3: {
                                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FWS_P_LOCALCONFIG_4}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                                break;
                            }
                        }
                        AssociatedBindListActivity.start(DetailThreeSwitchActivity.this, mIOTId, mProductKey, s, pos + 1);
                    }
                });
                TextView goTv = holder.getView(R.id.go_tv);
                goTv.setTypeface(mIconFont);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setBackgroundAlpha(0.4f);
        PopupWindow mAssociatedPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mAssociatedPopupWindow.setTouchable(true);
        mAssociatedPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        mAssociatedPopupWindow.setAnimationStyle(R.style.pop_anim);
        mAssociatedPopupWindow.showAtLocation(mRootLayout, Gravity.BOTTOM, 0, 0);
    }

    private void setBackgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private static class MyResponseErrHandler extends Handler {
        private final WeakReference<DetailThreeSwitchActivity> ref;

        public MyResponseErrHandler(DetailThreeSwitchActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailThreeSwitchActivity activity = ref.get();
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
                    jsonObject.put(CTSL.TWS_P3_PowerSwitch_1, activity.mStateName1.getText().toString());
                    jsonObject.put(CTSL.TWS_P3_PowerSwitch_2, activity.mStateName2.getText().toString());
                    jsonObject.put(CTSL.TWS_P3_PowerSwitch_3, activity.mStateName3.getText().toString());
                    activity.mSceneManager.setExtendedProperty(activity.mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                            null, null, null);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DetailThreeSwitchActivity> ref;

        public MyHandler(DetailThreeSwitchActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailThreeSwitchActivity activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    if (object.toJSONString().length() == 2) break;
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, object);
                    activity.mStateName1.setText(object.getString(CTSL.TWS_P3_PowerSwitch_1));
                    activity.mStateName2.setText(object.getString(CTSL.TWS_P3_PowerSwitch_2));
                    activity.mStateName3.setText(object.getString(CTSL.TWS_P3_PowerSwitch_3));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    activity.mStateName1.setText(activity.mKeyName1);
                    activity.mStateName2.setText(activity.mKeyName2);
                    activity.mStateName3.setText(activity.mKeyName3);
                    DeviceBuffer.addExtendedInfo(activity.mIOTId, activity.mResultObj);
                    ToastUtils.showShortToast(activity, R.string.set_success);
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

        if (resId == R.id.detailThreeSwitchLblStateName1) {
            // 按键1
            nameEt.setText(mStateName1.getText().toString());
        } else if (resId == R.id.detailThreeSwitchLblStateName2) {
            // 按键2
            nameEt.setText(mStateName2.getText().toString());
        } else if (resId == R.id.detailThreeSwitchLblStateName3) {
            // 按键3
            nameEt.setText(mStateName3.getText().toString());
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
        confirmView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEt.getText().toString().length() > 10
                        && mStateName1.getText().toString().length() > 10
                        && mStateName2.getText().toString().length() > 10
                        && mStateName3.getText().toString().length() > 10) {
                    ToastUtils.showShortToast(DetailThreeSwitchActivity.this, R.string.length_of_key_name_cannot_be_greater_than_10);
                    return;
                } else if (nameEt.getText().toString().length() == 0
                        && mStateName1.getText().toString().length() == 0
                        && mStateName2.getText().toString().length() == 0
                        && mStateName3.getText().toString().length() == 0) {
                    ToastUtils.showShortToast(DetailThreeSwitchActivity.this, R.string.key_name_cannot_be_empty);
                    return;
                }

                QMUITipDialogUtil.showLoadingDialg(DetailThreeSwitchActivity.this, R.string.is_setting);
                if (resId == R.id.detailThreeSwitchLblStateName1) {
                    // 按键1
                    mKeyName1 = nameEt.getText().toString();
                    mKeyName2 = mStateName2.getText().toString();
                    mKeyName3 = mStateName3.getText().toString();
                } else if (resId == R.id.detailThreeSwitchLblStateName2) {
                    // 按键2
                    mKeyName1 = mStateName1.getText().toString();
                    mKeyName2 = nameEt.getText().toString();
                    mKeyName3 = mStateName3.getText().toString();
                } else if (resId == R.id.detailThreeSwitchLblStateName3) {
                    // 按键3
                    mKeyName1 = mStateName1.getText().toString();
                    mKeyName2 = mStateName2.getText().toString();
                    mKeyName3 = nameEt.getText().toString();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.TWS_P3_PowerSwitch_1, mKeyName1);
                jsonObject.put(CTSL.TWS_P3_PowerSwitch_2, mKeyName2);
                jsonObject.put(CTSL.TWS_P3_PowerSwitch_3, mKeyName3);
                mResultObj = jsonObject;
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mHandler);
                dialog.dismiss();
            }
        });
        cancelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}