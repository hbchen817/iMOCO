package com.laffey.smart.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
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
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ResponseMessageUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 一键开关详细界面
 */
public class DetailOneSwitchActivity2 extends DetailActivity implements OnClickListener, View.OnLongClickListener {
    private static final int TAG_GET_EXTENDED_PRO = 10000;

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
    private RelativeLayout mAssociatedLayout;
    private RelativeLayout mAssociatedRootLayout;
    private LinearLayout mRootLayout;

    private SceneManager mSceneManager;
    private MyHandler mhandler;
    private String mKeyName;

    private Typeface mIconfont;
    private PopupWindow mAssociatedPopupWindow;

    private long mDoubleClickedTime = 0;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        ViseLog.d(new Gson().toJson(propertyEntry));
        if (!super.updateState(propertyEntry)) {
            return false;
        }
        QMUITipDialogUtil.dismiss();
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
        mImgOperate = findViewById(R.id.detailOneSwitchImgOperate);
        mImgOperate.setOnClickListener(this);

        mStateName = findViewById(R.id.detailOneSwitchLblStateName);
        mStateName.setOnLongClickListener(this);

        mStateValue = findViewById(R.id.detailOneSwitchLblStateValue);
        mRootLayout = findViewById(R.id.detailOneSwitchLl);

        TextView timerIc = findViewById(R.id.timer_ic_tv);
        mBacklightTV = findViewById(R.id.back_light_txt);
        mBacklightIc = findViewById(R.id.back_light_tv);
        TextView associatedTv = findViewById(R.id.associated_tv);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        timerIc.setTypeface(mIconfont);
        mBacklightIc.setTypeface(mIconfont);
        associatedTv.setTypeface(mIconfont);

        // 云端定时处理
        RelativeLayout timer = findViewById(R.id.detailOneSwitchRLTimer);
        timer.setOnClickListener(this);

        initStatusBar();

        mBackLightLayout = findViewById(R.id.back_light_layout);
        mBackLightLayout.setOnClickListener(this);
        mBackLightRoot = findViewById(R.id.back_light_root);

        mAssociatedLayout = findViewById(R.id.associated_layout);
        mAssociatedLayout.setOnClickListener(this);

        mhandler = new MyHandler(this);
        mSceneManager = new SceneManager(this);
        initKeyNickName();

        mBackLightRoot.setVisibility(View.VISIBLE);

        mAssociatedRootLayout = findViewById(R.id.associated_root_layout);
        if (DeviceBuffer.getDeviceOwned(mIOTId) == 1) {
            // 拥有者
            mAssociatedRootLayout.setVisibility(View.VISIBLE);
        } else {
            // 分享者
            mAssociatedRootLayout.setVisibility(View.GONE);
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

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mhandler);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mAssociatedLayout.getId()) {
            // 双控
            showAssociatedPopupWindow();
        } else if (v.getId() == mImgOperate.getId()) {
            // 按键触发 时间间隔1.5秒
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_btn);
                if (mState == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (v.getId() == R.id.detailOneSwitchRLTimer) {
            // 定时
            PluginHelper.cloudTimer(this, mIOTId, mProductKey);
        } else if (v.getId() == mBackLightLayout.getId()) {
            // 背光灯 时间间隔1.5秒
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_scene);
                if (mBackLightState == 0)
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_ON});
                else
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_BackLightMode}, new String[]{"" + CTSL.STATUS_OFF});
            }
            mDoubleClickedTime = System.currentTimeMillis();
        }
    }

    private void showAssociatedPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_associated_switch, null);

        RecyclerView recyclerView = contentView.findViewById(R.id.associated_rv);
        List<String> list = new ArrayList<>();
        list.add(mStateName.getText().toString());
        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_key, list) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                int pos = list.indexOf(s);
                holder.setText(R.id.key_tv, s);
                TextView nameTV = holder.getView(R.id.key_tv);
                nameTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mAssociatedPopupWindow.dismiss();
                        /*switch (pos) {
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
                        }*/
                        AssociatedBindListActivity.start(DetailOneSwitchActivity2.this, mIOTId, mProductKey, s, pos + 1);
                    }
                });
                TextView goTv = holder.getView(R.id.go_tv);
                goTv.setTypeface(mIconfont);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setBackgroundAlpha(0.4f);
        mAssociatedPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == mStateName.getId()) {
            // 按键昵称
            showKeyNameDialogEdit();
        }
        return false;
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
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(),
                        mCommitFailureHandler, mResponseErrorHandler, mhandler);
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
                ViseLog.d("object = \n" + GsonUtil.toJson(object));
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