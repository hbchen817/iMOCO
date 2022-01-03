package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.associated_tv)
    TextView mAssociatedTV;
    @BindView(R.id.associated_layout)
    RelativeLayout mAssociatedLayout;
    @BindView(R.id.associated_root_layout)
    RelativeLayout mAssociatedRootLayout;
    @BindView(R.id.root_layout)
    LinearLayout mRootLayout;

    private final String LOCAL_SCENE_CALL_BACK = "SixTwoSceneSwitchActivity2LocalSceneCallback";
    private final int BIND_SCENE_REQUEST_CODE = 10000;
    private final int EDIT_LOCAL_SCENE = 10001;

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

    private String mPressedKey = "1";
    private DelSceneHandler mDelSceneHandler;
    private Typeface mIconfont;

    private String mGatewayId;
    private String mGatewayMac;
    private ItemSceneInGateway m5Scene;
    private ItemSceneInGateway m6Scene;

    private long mDoubleClickedTime = 0;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }
        QMUITipDialogUtil.dismiss();
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

        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mTimerIcTV.setTypeface(mIconfont);
        mBackLightIc.setTypeface(mIconfont);
        mAssociatedTV.setTypeface(mIconfont);

        mMyHandler = new MyHandler(this);
        mTSLHelper = new TSLHelper(this);
        mDelSceneHandler = new DelSceneHandler(this);
        initView();
        initKeyNickName();
        getScenes();
        initStatusBar();
        // 双控
        mAssociatedLayout.setOnClickListener(this);

        if (DeviceBuffer.getDeviceOwned(mIOTId) == 1) {
            // 拥有者
            mAssociatedRootLayout.setVisibility(View.VISIBLE);
        } else {
            // 分享者
            mAssociatedRootLayout.setVisibility(View.GONE);
        }
    }

    // 获取面板所属网关iotId
    private void getGatewayId(String iotId) {
        SceneManager.getGWIotIdBySubIotId(this, iotId, new SceneManager.Callback() {

            @Override
            public void onNext(JSONObject response) {
                // 根据子设备iotId查询网关iotId
                int code = response.getInteger("code");
                if (code == 200) {
                    mGatewayId = response.getString("gwIotId");
                    mGatewayMac = DeviceBuffer.getDeviceMac(mGatewayId);
                    querySceneName();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SixTwoSceneSwitchActivity2.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(SixTwoSceneSwitchActivity2.this, e.getMessage());
            }
        });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appbgcolor2));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initKeyNickName() {
        /*mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);*/
        SceneManager.getExtendedProperty(this, mIOTId, Constant.TAG_DEV_KEY_NICKNAME, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(SixTwoSceneSwitchActivity2.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(SixTwoSceneSwitchActivity2.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                JSONObject object = JSONObject.parseObject(result);
                // ViseLog.d("按键昵称 = \n" + GsonUtil.toJson(object));
                if (object.toJSONString().length() > 2) {
                    mKey1TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1));
                    mKey2TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2));
                    mKey3TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3));
                    mKey4TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4));
                    mKey5TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1));
                    mKey6TV.setText(object.getString(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2));
                    DeviceBuffer.addExtendedInfo(mIOTId, object);
                }
            }
        });
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
        mKey1TV.setOnLongClickListener(this);
        mKey2TV.setOnLongClickListener(this);
        mKey3TV.setOnLongClickListener(this);
        mKey4TV.setOnLongClickListener(this);
        mKey5TV.setOnLongClickListener(this);
        mKey6TV.setOnLongClickListener(this);

        mSceneManager = new SceneManager(this);
    }

    private void getScenes() {
        mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_1;
        /*mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET,
                mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);*/
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.switch1) {
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_btn);
                if (mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (view.getId() == R.id.switch2) {
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_btn);
                if (mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (view.getId() == R.id.switch3) {
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_btn);
                if (mState3 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (view.getId() == R.id.switch4) {
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_btn);
                if (mState4 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (view.getId() == R.id.mSceneContentText2) {
            // 场景按键5
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                    if (m5Scene != null) {
                        if ("1".equals(m5Scene.getSceneDetail().getEnable())) {
                            QMUITipDialogUtil.showLoadingDialg(this, R.string.click_scene);
                            SceneManager.invokeLocalSceneService(this, mGatewayId,
                                    m5Scene.getSceneDetail().getSceneId(), null);
                        } else {
                            // 禁用
                            ToastUtils.showLongToast(this, R.string.scene_is_invaild);
                        }
                    } else {
                        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1) {
                            SwitchLocalSceneListActivity.start(this, mIOTId, mGatewayId, mGatewayMac,
                                    CTSL.SCENE_SWITCH_KEY_CODE_5, BIND_SCENE_REQUEST_CODE);
                        } else {
                            ToastUtils.showLongToast(this, R.string.sharing_dev_does_not_support_edit_scene);
                        }
                    }
                }
                mDoubleClickedTime = System.currentTimeMillis();
            } else {
                if (mFirstManualSceneId != null) {
                    mPressedKey = "5";
                    mSceneManager.executeScene(mFirstManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            // 场景按键6
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                    if (m6Scene != null) {
                        if ("1".equals(m6Scene.getSceneDetail().getEnable())) {
                            QMUITipDialogUtil.showLoadingDialg(this, R.string.click_scene);
                            SceneManager.invokeLocalSceneService(this, mGatewayId,
                                    m6Scene.getSceneDetail().getSceneId(), null);
                        } else {
                            // 禁用
                            ToastUtils.showLongToast(this, R.string.scene_is_invaild);
                        }
                    } else {
                        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1) {
                            SwitchLocalSceneListActivity.start(this, mIOTId, mGatewayId, mGatewayMac,
                                    CTSL.SCENE_SWITCH_KEY_CODE_6, BIND_SCENE_REQUEST_CODE);
                        } else {
                            ToastUtils.showLongToast(this, R.string.sharing_dev_does_not_support_edit_scene);
                        }
                    }
                }
                mDoubleClickedTime = System.currentTimeMillis();
            } else {
                if (mSecondManualSceneId != null) {
                    mPressedKey = "6";
                    mSceneManager.executeScene(mSecondManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2);
                }
            }
        } else if (view.getId() == R.id.timer_layout) {
            // 定时
            PluginHelper.cloudTimer(SixTwoSceneSwitchActivity2.this, mIOTId, mProductKey);
        } else if (view.getId() == R.id.back_light_layout) {
            // 背光
            if (System.currentTimeMillis() - mDoubleClickedTime >= 1000) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.click_backlight);
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
                }
            }
            mDoubleClickedTime = System.currentTimeMillis();
        } else if (view.getId() == R.id.associated_layout) {
            // 双控
            showAssociatedPopupWindow();
        }
    }

    private void showAssociatedPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_associated_switch, null);

        RecyclerView recyclerView = contentView.findViewById(R.id.associated_rv);
        List<String> list = new ArrayList<>();
        list.add(mKey1TV.getText().toString());
        list.add(mKey2TV.getText().toString());
        list.add(mKey3TV.getText().toString());
        list.add(mKey4TV.getText().toString());
        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_key, list) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                int pos = list.indexOf(s);
                holder.setText(R.id.key_tv, s);
                TextView nameTV = holder.getView(R.id.key_tv);
                nameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        AssociatedBindListActivity.start(SixTwoSceneSwitchActivity2.this, mIOTId, mProductKey, s, pos + 1);
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
        PopupWindow associatedPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        associatedPopupWindow.setTouchable(true);
        associatedPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        associatedPopupWindow.setAnimationStyle(R.style.pop_anim);
        associatedPopupWindow.showAtLocation(mRootLayout, Gravity.BOTTOM, 0, 0);
    }

    private void setBackgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
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
        private WeakReference<SixTwoSceneSwitchActivity2> ref;

        public DelSceneHandler(SixTwoSceneSwitchActivity2 activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SixTwoSceneSwitchActivity2 activity = ref.get();
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
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_1, mKeyName1);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_2, mKeyName2);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_3, mKeyName3);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_P_POWER_4, mKeyName4);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mKeyName5);
                jsonObject.put(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mKeyName6);
                resultObj = jsonObject;
                setKeyName(resultObj);
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

    // 设置按键昵称
    private void setKeyName(JSONObject resultObj) {
        SceneManager.setExtendedProperty(this, mIOTId, Constant.TAG_DEV_KEY_NICKNAME, resultObj.toJSONString(), new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(SixTwoSceneSwitchActivity2.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(SixTwoSceneSwitchActivity2.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                // 设置按键昵称
                ViseLog.d("设置按键昵称 = \n" + GsonUtil.toJson(JSONObject.parseObject(result)));
                QMUITipDialogUtil.dismiss();
                mKey1TV.setText(mKeyName1);
                mKey2TV.setText(mKeyName2);
                mKey3TV.setText(mKeyName3);
                mKey4TV.setText(mKeyName4);
                mKey5TV.setText(mKeyName5);
                mKey6TV.setText(mKeyName6);
                DeviceBuffer.addExtendedInfo(mIOTId, resultObj);
                RefreshData.refreshDeviceKeyName();
                ToastUtils.showShortToast(SixTwoSceneSwitchActivity2.this, R.string.set_success);
            }
        });
    }

    private JSONObject resultObj;

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.mSceneContentText2) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1) {
                    if (m5Scene != null) {
                        EditLocalSceneBindActivity.start(this, mKey5TV.getText().toString(), mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1,
                                mSceneContentText2.getText().toString(), mGatewayId, mGatewayMac, m5Scene.getSceneDetail().getSceneId(), EDIT_LOCAL_SCENE);
                    }
                }
            } else {
                if (mFirstManualSceneId != null) {
                    EditSceneBindActivity.start(this, mKeyName5, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_5, mSceneContentText2.getText().toString());
                }
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 1) {
                    if (m6Scene != null)
                        EditLocalSceneBindActivity.start(this, mKey6TV.getText().toString(), mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2,
                                mSceneContentText5.getText().toString(), mGatewayId, mGatewayMac, m6Scene.getSceneDetail().getSceneId(), EDIT_LOCAL_SCENE);
                }
            } else {
                if (mSecondManualSceneId != null) {
                    EditSceneBindActivity.start(this, mKeyName6, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mSceneContentText5.getText().toString());
                }
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
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<SixTwoSceneSwitchActivity2> mWeakReference;

        public MyHandler(SixTwoSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixTwoSceneSwitchActivity2 activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET: {
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
                }
                case Constant.MSG_CALLBACK_EXECUTESCENE: {
                    String sceneId = (String) msg.obj;
                    ToastUtils.showShortToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint_2),
                            sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName));
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

    private void initSceneView() {
        mSceneContentText2.setText(R.string.no_bind_scene);
        mSceneContentText5.setText(R.string.no_bind_scene);
        m5Scene = null;
        m6Scene = null;
    }

    // 获取按键绑定场景的名称
    private void querySceneName() {
        mSceneContentText2.setText(R.string.no_bind_scene);
        mSceneContentText5.setText(R.string.no_bind_scene);
        for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
            if (scene.getAppParams() == null) continue;
            String switchIotId = scene.getAppParams().getString("switchIotId");
            if (switchIotId == null || switchIotId.length() == 0) {
                continue;
            } else if (!switchIotId.contains(mIOTId)) continue;
            String key = scene.getAppParams().getString("key");
            if (key == null) continue;
            if (key.contains(CTSL.SCENE_SWITCH_KEY_CODE_5) && "1".equals(scene.getSceneDetail().getType())) {
                mSceneContentText2.setText(scene.getSceneDetail().getName());
                m5Scene = scene;
            }
            if (key.contains(CTSL.SCENE_SWITCH_KEY_CODE_6) && "1".equals(scene.getSceneDetail().getType())) {
                mSceneContentText5.setText(scene.getSceneDetail().getName());
                m6Scene = scene;
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

    @Override
    protected void onResume() {
        super.onResume();
        addEventCallbackHandler();
        if (mGatewayMac == null || mGatewayMac.length() == 0)
            getGatewayId(mIOTId);
        else {
            querySceneName();
        }
    }

    private void addEventCallbackHandler() {
        RealtimeDataReceiver.addEventCallbackHandler(LOCAL_SCENE_CALL_BACK, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                    // 处理触发手动场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("InvokeLocalSceneNotification".equals(identifier)) {
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        QMUITipDialogUtil.dismiss();
                        if ("0".equals(status)) {
                            String name = DeviceBuffer.getScene(value.getString("SceneId")).getSceneDetail().getName();
                            if (name != null && name.length() > 0) {
                                String tip = String.format(getString(R.string.main_scene_execute_hint_2), name);
                                ToastUtils.showLongToast(SixTwoSceneSwitchActivity2.this, tip);
                            } else {
                                ToastUtils.showLongToast(SixTwoSceneSwitchActivity2.this, R.string.perform_scene);
                            }
                        } else {
                            ToastUtils.showLongToast(SixTwoSceneSwitchActivity2.this, R.string.scene_do_fail);
                        }
                    }
                }
                return false;
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCAL_SCENE) {
            if (resultCode == 2) {
                initSceneView();
                ToastUtils.showLongToast(this, R.string.unbind_scene_success);
            }
        } else if (requestCode == BIND_SCENE_REQUEST_CODE) {
            if (resultCode == 2) {
                ToastUtils.showLongToast(this, R.string.bind_scene_success);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        QMUITipDialogUtil.dismiss();
        RealtimeDataReceiver.deleteCallbackHandler(LOCAL_SCENE_CALL_BACK);
    }
}

