package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalSceneBinding;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EAction;
import com.laffey.smart.model.ECondition;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EEventScene;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalSceneActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLocalSceneBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private static final String GATEWAY_MAC = "gateway_mac";

    private static final int TIMER_INTENT_TAG = 10000;
    private static final int TIMER_RANGE_INTENT_TAG = 10001;

    private String mGatewayId;// 网关iotid
    private String mGatewayMac;// 网关实际mac
    private String mSceneName;// 场景名称
    private String mSceneType = "0";// 场景类型,0:自动，1：手动
    private String mSceneEnable = "1";// 启用状态，0：禁用，1：启用
    private String mSceneTimer = "1";// 时间条件，0：无时间条件，1：有时间条件
    private String mSceneMode = "Any";//all any
    private ItemScene mScene;

    private Typeface mIconfont;
    private Map<String, String> mSymbols = new HashMap<>();

    private BaseQuickAdapter<ItemScene.Timer, BaseViewHolder> mTimerAdapter;
    private final List<ItemScene.Timer> mTimerList = new ArrayList<>();

    private final List<ECondition> mConditionList = new ArrayList<>();
    private BaseQuickAdapter<ECondition, BaseViewHolder> mConditionAdapter;

    private final List<EAction> mActionList = new ArrayList<>();
    private BaseQuickAdapter<EAction, BaseViewHolder> mActionAdapter;
    private SceneManager mSceneManager;
    private MyHandler mHandler;

    public static void start(Activity activity, String gatewayId, String gatewayMac, int requestCode) {
        Intent intent = new Intent(activity, LocalSceneActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(GATEWAY_MAC, gatewayMac);
        activity.startActivityForResult(intent, requestCode);
    }

    public static Intent getIntent(Activity activity, String gatewayId, String gatewayMac) {
        Intent intent = new Intent(activity, LocalSceneActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(GATEWAY_MAC, gatewayMac);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mSymbols.put(">", "大于");
        mSymbols.put("<", "小于");
        mSymbols.put(">=", "大于等于");
        mSymbols.put("<=", "小于等于");
        mSymbols.put("==", "等于");
        mSymbols.put("!=", "不等于");

        initStatusBar();
        initTimerAdapter();

        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mGatewayMac = getIntent().getStringExtra(GATEWAY_MAC);

        if (mGatewayId != null && mGatewayId.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_ID, mGatewayId);
        if (mGatewayMac != null && mGatewayMac.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_MAC, mGatewayMac);

        EventBus.getDefault().register(this);

        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initView();
        initConditionAdapter();
        initActionAdapter();

        // QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        initData();
    }

    private void initData() {
        // queryMacByIotId();
        mSceneManager = new SceneManager(this);
    }

    private void queryMacByIotId() {
        RetrofitUtil.getInstance().queryMacByIotId("chengxunfei", Constant.QUERY_MAC_BY_IOTID_VER, "xxxxxx", "i1cU8RQDuaUsaNvw4ScgeND83D")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        String mac = response.getString("mac");
                        if (code == 200) {
                            mGatewayMac = mac;
                        } else {
                            if (msg != null && msg.length() > 0)
                                ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                            else
                                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initConditionAdapter() {
        mConditionAdapter = new BaseQuickAdapter<ECondition, BaseViewHolder>(R.layout.item_condition_or_action_2, mConditionList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ECondition eCondition) {
                TextView icon = holder.getView(R.id.icon_iv);
                TextView goIv = holder.getView(R.id.go_iv);
                icon.setTypeface(mIconfont);
                goIv.setTypeface(mIconfont);
                icon.setText(R.string.icon_dev);

                if (DeviceBuffer.getDeviceInformation(eCondition.getIotId()) != null) {
                    holder.setText(R.id.title, DeviceBuffer.getDeviceInformation(eCondition.getIotId()).nickName);

                    StringBuilder desc = new StringBuilder();
                    desc.append(refreshConditionDesc(eCondition));
                    holder.setText(R.id.detail, desc.toString());
                } else {
                    holder.setText(R.id.title, getString(R.string.dev_does_not_exist));
                    holder.setTextColor(R.id.title, ContextCompat.getColor(LocalSceneActivity.this, R.color.red));

                    holder.setText(R.id.detail, "--");
                    holder.setTextColor(R.id.detail, ContextCompat.getColor(LocalSceneActivity.this, R.color.red));
                }

                holder.setVisible(R.id.divider, mConditionList.indexOf(eCondition) != 0);
            }
        };
        mConditionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ECondition eCondition = mConditionList.get(position);
                if (eCondition.getIotId() != null && eCondition.getIotId().length() > 0) {
                    eCondition.setTarget("LocalConditionValueActivity");
                    EventBus.getDefault().postSticky(eCondition);
                    Intent intent = new Intent(LocalSceneActivity.this, LocalConditionValueActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showLongToast(LocalSceneActivity.this, R.string.dev_does_not_exist);
                }
            }
        });
        mConditionAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showDelDialog(R.string.do_you_really_want_to_delete_the_current_option, position, DEL_NORMAL_CONDITION_TAG);
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.conditionRv.setLayoutManager(layoutManager);
        mViewBinding.conditionRv.setAdapter(mConditionAdapter);
    }

    private void initActionAdapter() {
        mActionAdapter = new BaseQuickAdapter<EAction, BaseViewHolder>(R.layout.item_condition_or_action_2, mActionList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, EAction eAction) {
                TextView icon = holder.getView(R.id.icon_iv);
                TextView goIv = holder.getView(R.id.go_iv);
                icon.setTypeface(mIconfont);
                goIv.setTypeface(mIconfont);

                String type = eAction.getAction().getType();
                if ("Command".equals(type)) {
                    icon.setText(R.string.icon_dev);
                    if (DeviceBuffer.getDeviceInformation(eAction.getIotId()) != null) {
                        holder.setText(R.id.title, DeviceBuffer.getDeviceInformation(eAction.getIotId()).nickName);

                        holder.setText(R.id.detail, refreshActionDesc(eAction));
                    } else {
                        holder.setText(R.id.title, getString(R.string.dev_does_not_exist));
                        holder.setTextColor(R.id.title, ContextCompat.getColor(LocalSceneActivity.this, R.color.red));
                        holder.setText(R.id.detail, "--");
                        holder.setTextColor(R.id.detail, ContextCompat.getColor(LocalSceneActivity.this, R.color.red));
                    }
                } else if ("Scene".equals(type)) {
                    icon.setText(R.string.icon_scene);
                    holder.setText(R.id.title, eAction.getKeyNickName())
                            .setText(R.id.detail, getString(R.string.rb_tab_two_desc));
                }
                holder.setVisible(R.id.divider, mActionList.indexOf(eAction) != 0);
            }
        };
        mActionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EAction eAction = mActionList.get(position);
                if ("Command".equals(eAction.getAction().getType())) {
                    if (eAction.getIotId() != null && eAction.getIotId().length() > 0) {
                        eAction.setTarget("LocalActionValueActivity");
                        EventBus.getDefault().postSticky(eAction);

                        Intent intent = new Intent(LocalSceneActivity.this, LocalActionValueActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.dev_does_not_exist);
                    }
                } else if ("Scene".equals(eAction.getAction().getType())) {
                    eAction.setTarget("LocalActionScenesActivity");
                    EventBus.getDefault().postSticky(eAction);

                    Intent intent = new Intent(LocalSceneActivity.this, LocalActionScenesActivity.class);
                    startActivity(intent);
                }
            }
        });
        mActionAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showDelDialog(R.string.do_you_really_want_to_delete_the_current_option, position, DEL_ACTION_TAG);
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.actionRv.setLayoutManager(layoutManager);
        mViewBinding.actionRv.setAdapter(mActionAdapter);
    }

    // 条件描述
    private String refreshConditionDesc(ECondition eCondition) {
        StringBuilder desc = new StringBuilder();
        String pk = DeviceBuffer.getDeviceInformation(eCondition.getIotId()).productKey;
        if (CTSL.PK_ONEWAYSWITCH.equals(pk) ||
                CTSL.PK_TWOWAYSWITCH.equals(pk) ||
                CTSL.PK_THREE_KEY_SWITCH.equals(pk) ||
                CTSL.PK_FOURWAYSWITCH_2.equals(pk) ||
                CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_ONE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SIX_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_OUTLET.equals(pk)) {
            if ("State".equals(eCondition.getCondition().getType())) {
                desc.append(eCondition.getKeyNickName() + "：");
                ViseLog.d(desc.toString());
                if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    desc.append(getString(R.string.oneswitch_state_on));
                } else if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    desc.append(getString(R.string.oneswitch_state_off));
                }
            } else if ("Event".equals(eCondition.getCondition().getType())) {
                desc.append(eCondition.getKeyNickName() + "：");
                if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        if (CTSL.PK_ONE_SCENE_SWITCH.equals(pk))
                            keyNickName = getString(R.string.key_0);
                        else keyNickName = getString(R.string.key_1);
                    }
                    desc.append(keyNickName);
                } else if ("2".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_2);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        keyNickName = getString(R.string.key_2);
                    }
                    desc.append(keyNickName);
                } else if ("3".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_3);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        keyNickName = getString(R.string.key_3);
                    }
                    desc.append(keyNickName);
                } else if ("4".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_4);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        keyNickName = getString(R.string.key_4);
                    }
                    desc.append(keyNickName);
                } else if ("5".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_5);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        keyNickName = getString(R.string.key_5);
                    }
                    desc.append(keyNickName);
                } else if ("6".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                    String keyNickName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SCENE_SWITCH_KEY_CODE_6);
                    if (keyNickName == null || keyNickName.length() == 0) {
                        keyNickName = getString(R.string.key_6);
                    }
                    desc.append(keyNickName);
                }
            }
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(pk) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(pk) ||
                CTSL.PK_VRV_AC.equals(pk) ||
                CTSL.PK_FLOORHEATING001.equals(pk) ||
                CTSL.PK_FAU.equals(pk)) {
            if ("State".equals(eCondition.getCondition().getType())) {
                if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                    desc.append(eCondition.getKeyNickName() + "：");
                    if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                        desc.append(getString(R.string.oneswitch_state_off));
                    } else if ("10".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                        desc.append(getString(R.string.oneswitch_state_on));
                    }
                } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                    desc.append(eCondition.getKeyNickName());
                    desc.append(mSymbols.get(eCondition.getCondition().getParameters().getCompareType()));
                    desc.append(eCondition.getCondition().getParameters().getCompareValue());
                    desc.append(getString(R.string.centigrade));
                } else if ("FanMode".equals(eCondition.getCondition().getParameters().getName())) {
                    desc.append(eCondition.getKeyNickName() + "：");
                    if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                        desc.append(getString(R.string.oneswitch_state_off));
                    } else if ("4".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                        desc.append(getString(R.string.oneswitch_state_on));
                    }
                }
            }
        } else if (CTSL.PK_LIGHT.equals(pk) || CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(pk)) {
            // 调光调色、单调光
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.oneswitch_state_off));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.oneswitch_state_on));
            }
        } else if (CTSL.PK_PIRSENSOR.equals(pk)) {
            // 红外传感器
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_motionnonhas));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_motionhas));
            }
        } else if (CTSL.PK_GASSENSOR.equals(pk)) {
            // 燃气感应器
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_gasnonhas));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_gashas));
            }
        } else if (CTSL.PK_TEMHUMSENSOR.equals(pk)) {
            // 温湿度传感器
            if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(eCondition.getKeyNickName());
                desc.append(mSymbols.get(eCondition.getCondition().getParameters().getCompareType()));
                desc.append(eCondition.getCondition().getParameters().getCompareValue());
                desc.append(getString(R.string.centigrade));
            } else if ("Humidity".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(eCondition.getKeyNickName());
                desc.append(mSymbols.get(eCondition.getCondition().getParameters().getCompareType()));
                desc.append(eCondition.getCondition().getParameters().getCompareValue());
            }
        } else if (CTSL.PK_SMOKESENSOR.equals(pk)) {
            // 烟雾传感器
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_smokenonhas));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_smokehas));
            }
        } else if (CTSL.PK_WATERSENSOR.equals(pk)) {
            // 水浸传感器
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_waternonhas));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_waterhas));
            }
        } else if (CTSL.PK_DOORSENSOR.equals(pk)) {
            // 门磁
            desc.append(eCondition.getKeyNickName() + "：");
            if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_contactclose));
            } else if ("1".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                desc.append(getString(R.string.sensorstate_contactopen));
            }
        } else if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(pk) ||
                CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(pk)) {
            desc.append(eCondition.getKeyNickName() + "：");
            switch (eCondition.getCondition().getParameters().getCompareValue()) {
                case "1": {
                    // 打开
                    desc.append(getString(R.string.open));
                    break;
                }
                case "2": {
                    // 关闭
                    desc.append(getString(R.string.close));
                    break;
                }
                case "0": {
                    // 暂停
                    desc.append(getString(R.string.stop));
                    break;
                }
            }
        }
        return desc.toString();
    }

    // 动作描述
    private String refreshActionDesc(EAction eAction) {
        StringBuilder desc = new StringBuilder();
        String pk = DeviceBuffer.getDeviceInformation(eAction.getIotId()).productKey;
        if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(pk) ||
                CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            String value = eAction.getAction().getParameters().getCommand().getString("Operate");
            switch (value) {
                case "0": {
                    // 打开
                    desc.append(getString(R.string.open));
                    break;
                }
                case "1": {
                    // 关闭
                    desc.append(getString(R.string.close));
                    break;
                }
                case "2": {
                    // 暂停
                    desc.append(getString(R.string.stop));
                    break;
                }
            }
        } else if (CTSL.PK_ONEWAYSWITCH.equals(pk) ||
                CTSL.PK_TWOWAYSWITCH.equals(pk) ||
                CTSL.PK_THREE_KEY_SWITCH.equals(pk) ||
                CTSL.PK_FOURWAYSWITCH_2.equals(pk) ||
                CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            String value = eAction.getAction().getParameters().getCommand().getString("State");
            switch (value) {
                case "1": {
                    // 打开
                    desc.append(getString(R.string.open));
                    break;
                }
                case "0": {
                    // 关闭
                    desc.append(getString(R.string.close));
                    break;
                }
            }
        } else if (CTSL.PK_OUTLET.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            String value = eAction.getAction().getParameters().getCommand().getString("State");
            switch (value) {
                case "1": {
                    // 打开
                    desc.append(getString(R.string.open));
                    break;
                }
                case "0": {
                    // 关闭
                    desc.append(getString(R.string.close));
                    break;
                }
            }
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(pk) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(pk) ||
                CTSL.PK_VRV_AC.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            JSONObject command = eAction.getAction().getParameters().getCommand();
            if (command.containsKey("WorkMode")) {
                String value = command.getString("WorkMode");
                if ("0".equals(value)) {
                    desc.append(getString(R.string.close));
                } else if ("10".equals(value)) {
                    desc.append(getString(R.string.open));
                } else if ("3".equals(value)) {
                    desc.append(getString(R.string.refrigeration));
                } else if ("4".equals(value)) {
                    desc.append(getString(R.string.heating));
                } else if ("7".equals(value)) {
                    desc.append(getString(R.string.air_supply));
                }
            } else if (command.containsKey("Temperature")) {
                String value = command.getString("Temperature");
                desc.append(value).append(getString(R.string.centigrade));
            } else if (command.containsKey("FanMode")) {
                String value = command.getString("FanMode");
                switch (value) {
                    case "1": {
                        desc.append(getString(R.string.fan_speed_low));
                        break;
                    }
                    case "2": {
                        desc.append(getString(R.string.fan_speed_mid));
                        break;
                    }
                    case "3": {
                        desc.append(getString(R.string.fan_speed_high));
                        break;
                    }
                    case "5": {
                        desc.append(getString(R.string.auto));
                        break;
                    }
                }
            }
        } else if (CTSL.PK_FLOORHEATING001.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            JSONObject command = eAction.getAction().getParameters().getCommand();
            if (command.containsKey("WorkMode")) {
                String value = command.getString("WorkMode");
                switch (value) {
                    case "0": {
                        desc.append(getString(R.string.close));
                        break;
                    }
                    case "10": {
                        desc.append(getString(R.string.open));
                        break;
                    }
                }
            } else if (command.containsKey("Temperature")) {
                String value = command.getString("Temperature");
                desc.append(value).append(getString(R.string.centigrade));
            }
        } else if (CTSL.PK_FAU.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            String value = eAction.getAction().getParameters().getCommand().getString("FanMode");
            switch (value) {
                case "0": {
                    desc.append(getString(R.string.close));
                    break;
                }
                case "4": {
                    desc.append(getString(R.string.open));
                    break;
                }
                case "1": {
                    desc.append(getString(R.string.fan_speed_low));
                    break;
                }
                case "2": {
                    desc.append(getString(R.string.fan_speed_mid));
                    break;
                }
                case "3": {
                    desc.append(getString(R.string.fan_speed_high));
                    break;
                }
                case "5": {
                    desc.append(getString(R.string.auto));
                    break;
                }
            }
        } else if (CTSL.PK_LIGHT.equals(pk) || CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(pk)) {
            desc.append(eAction.getKeyNickName() + "：");
            JSONObject command = eAction.getAction().getParameters().getCommand();
            if (command.containsKey("Level")) {
                desc.append(command.getString("Level"));
            } else if (command.containsKey("Temperature")) {
                desc.append(command.getString("Temperature"));
            }
        }
        return desc.toString();
    }

    private void initTimerAdapter() {
        mTimerAdapter = new BaseQuickAdapter<ItemScene.Timer, BaseViewHolder>(R.layout.item_condition_or_action_2, mTimerList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemScene.Timer timer) {
                TextView icon = holder.getView(R.id.icon_iv);
                TextView goIv = holder.getView(R.id.go_iv);
                icon.setTypeface(mIconfont);
                goIv.setTypeface(mIconfont);
                switch (timer.getType()) {
                    case "Timer": {
                        holder.setText(R.id.title, R.string.timer_point);
                        break;
                    }
                    case "TimeRange": {
                        holder.setText(R.id.title, R.string.time_range);
                        break;
                    }
                }
                holder.setText(R.id.detail, transformTimer(timer.getType(), timer.getCron()))
                        .setVisible(R.id.divider, false);
            }
        };
        mTimerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if ("Timer".equals(mTimerList.get(position).getType())) {
                    // 时间点
                    LocalTimeSelectorActivity.startForResult(LocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_INTENT_TAG);
                } else if ("TimeRange".equals(mTimerList.get(position).getType())) {
                    // 时间段
                    LocalTimeRangeSelectorActivity.startForResult(LocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_RANGE_INTENT_TAG);
                }
            }
        });
        mTimerAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showDelDialog(R.string.do_you_really_want_to_delete_the_current_option, 0, DEL_TIME_CONDITION_TAG);
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.timeConditionRv.setAdapter(mTimerAdapter);
        mViewBinding.timeConditionRv.setLayoutManager(layoutManager);
    }

    private final int DEL_TIME_CONDITION_TAG = 10001;
    private final int DEL_NORMAL_CONDITION_TAG = 10002;
    private final int DEL_ACTION_TAG = 10003;

    private void showDelDialog(int msgId, int pos, int tag) {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(LocalSceneActivity.this).create();
        alert.setIcon(R.drawable.dialog_quest);
        alert.setTitle(R.string.dialog_title);
        alert.setMessage(getResources().getString(msgId));
        //添加否按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //添加是按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                switch (tag) {
                    case DEL_TIME_CONDITION_TAG: {
                        // 删除时间条件
                        mTimerList.clear();
                        mTimerAdapter.notifyDataSetChanged();
                        mViewBinding.timeConditionRv.setVisibility(View.GONE);
                        mViewBinding.addTimeLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                    case DEL_NORMAL_CONDITION_TAG: {
                        // 删除条件
                        mConditionList.remove(pos);
                        mConditionAdapter.notifyDataSetChanged();
                        if (mConditionList.size() == 0) {
                            mViewBinding.conditionRv.setVisibility(View.GONE);
                            mViewBinding.addConditionLayout.setVisibility(View.VISIBLE);
                        } else {
                            mViewBinding.conditionRv.setVisibility(View.VISIBLE);
                            mViewBinding.addConditionLayout.setVisibility(View.GONE);
                        }
                        break;
                    }
                    case DEL_ACTION_TAG: {
                        // 删除动作
                        mActionList.remove(pos);
                        mActionAdapter.notifyDataSetChanged();
                        if (mActionList.size() == 0) {
                            mViewBinding.actionRv.setVisibility(View.GONE);
                            mViewBinding.addActionLayout.setVisibility(View.VISIBLE);
                        } else {
                            mViewBinding.actionRv.setVisibility(View.VISIBLE);
                            mViewBinding.addActionLayout.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
                arg0.dismiss();
            }
        });
        alert.show();
    }

    /**
     * 将cron转化为中文描述
     *
     * @param type 时间类型,Timer：时间点，TimeRange：时间段
     * @param cron 时间描述
     * @return 中文描述
     */
    private String transformTimer(String type, String cron) {
        StringBuilder time = new StringBuilder();

        String[] timers = cron.split(" ");
        if ("Timer".equals(type)) {
            // 时间点
            if (timers[1].length() == 1) {
                time.append("0" + timers[1] + ":");
            } else
                time.append(timers[1] + ":");
            if (timers[0].length() == 1)
                time.append("0" + timers[0] + " ");
            else time.append(timers[0] + " ");
        } else if ("TimeRange".equals(type)) {
            // 时间段
            String[] mins = timers[0].split("-");
            String beginMin = mins[0].length() == 1 ? ("0" + mins[0]) : (mins[0]);
            String endMin = mins[1].length() == 1 ? ("0" + mins[1]) : (mins[1]);

            String[] hours = timers[1].split("-");
            String beginHour = hours[0].length() == 1 ? ("0" + hours[0]) : (hours[0]);
            String endHour = hours[1].length() == 1 ? ("0" + hours[1]) : (hours[1]);

            time.append(beginHour + ":" + beginMin + "-" + endHour + ":" + endMin + " ");
        }

        if ("*".equals(timers[4])) {
            // 每天
            time.append(getString(R.string.everyday));
        } else if ("MON,TUE,WED,THU,FRI".equals(timers[4])) {
            // 工作日
            time.append(getString(R.string.set_time_workday_2));
        } else if ("SAT,SUN".equals(timers[4])) {
            // 周末
            time.append(getString(R.string.set_time_weekend_2));
        } else {
            // 自定义
            String[] custom = timers[4].split(",");
            for (int i = 0; i < custom.length; i++) {
                switch (custom[i]) {
                    case "MON": {
                        time.append(getString(R.string.week_1_all));
                        break;
                    }
                    case "TUE": {
                        time.append(getString(R.string.week_2_all));
                        break;
                    }
                    case "WED": {
                        time.append(getString(R.string.week_3_all));
                        break;
                    }
                    case "THU": {
                        time.append(getString(R.string.week_4_all));
                        break;
                    }
                    case "FRI": {
                        time.append(getString(R.string.week_5_all));
                        break;
                    }
                    case "SAT": {
                        time.append(getString(R.string.week_6_all));
                        break;
                    }
                    case "SUN": {
                        time.append(getString(R.string.week_0_all));
                        break;
                    }
                }

                if (i != custom.length - 1) {
                    time.append("，");
                }
            }
        }
        return time.toString();
    }

    private void initView() {
        mHandler = new MyHandler(this);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.nameGo.setTypeface(iconfont);
        mViewBinding.statusGo.setTypeface(iconfont);
        mViewBinding.timeEnableGo.setTypeface(iconfont);
        mViewBinding.conditionEnableGo.setTypeface(iconfont);
        mViewBinding.addTimeIv.setTypeface(iconfont);
        mViewBinding.addConditionIv.setTypeface(iconfont);
        mViewBinding.addActionIv.setTypeface(iconfont);

        mViewBinding.nameTv.setOnClickListener(this);
        mViewBinding.statusTv.setOnClickListener(this);
        mViewBinding.timeEnableTv.setOnClickListener(this);
        mViewBinding.conditionEnableTv.setOnClickListener(this);
        mViewBinding.addTimeConditionTv.setOnClickListener(this);
        mViewBinding.sceneModeTv.setOnClickListener(this);
        mViewBinding.addConditionTv.setOnClickListener(this);
        mViewBinding.addActionTv.setOnClickListener(this);

        mViewBinding.nameGo.setOnClickListener(this);
        mViewBinding.statusGo.setOnClickListener(this);
        mViewBinding.timeEnableGo.setOnClickListener(this);
        mViewBinding.conditionEnableGo.setOnClickListener(this);
        mViewBinding.delLayout.setOnClickListener(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.create_new_scene);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.share_device_commit);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mGatewayId = SpUtils.getStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_ID, "");
        mGatewayMac = SpUtils.getStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_MAC, "");
        // ViseLog.d("mGatewayMac = " + mGatewayMac);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.tvToolbarRight.getId()) {
            submitScene();
        } else if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.nameTv.getId() ||
                v.getId() == mViewBinding.nameGo.getId()) {
            showSceneNameDialogEdit();
        } else if (v.getId() == mViewBinding.statusTv.getId() ||
                v.getId() == mViewBinding.statusGo.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.scene_maintain_startusing));
            builder.addItem(getString(R.string.scene_maintain_stopusing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.statusTv.setText(tag);
                    if (position == 0) {
                        mSceneEnable = "1";
                    } else mSceneEnable = "0";
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.timeEnableTv.getId() ||
                v.getId() == mViewBinding.timeEnableGo.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.there_are));
            builder.addItem(getString(R.string.nothing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.addTimeConditionLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                    mViewBinding.timeEnableTv.setText(tag);
                    if (position == 0 && mTimerList.size() > 0 && "Timer".equals(mTimerList.get(0).getType())) {
                        // 当时间条件为时间点时，condition节点无效
                        mViewBinding.conditionEnableTv.setText(R.string.nothing);
                        mViewBinding.sceneModeLayout.setVisibility(View.GONE);
                    }
                    if (position == 0) {
                        mSceneTimer = "1";
                    } else mSceneTimer = "0";
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.conditionEnableTv.getId() ||
                v.getId() == mViewBinding.conditionEnableGo.getId()) {
            if (mTimerList.size() > 0 && "Timer".equals(mTimerList.get(0).getType()) && getString(R.string.there_are).equals(mViewBinding.timeEnableTv.getText().toString())) {
                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.condition_setting_is_invalid_when_time_trigger_is_point_in_time);
                return;
            }
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.there_are));
            builder.addItem(getString(R.string.nothing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.sceneModeLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                    mViewBinding.conditionEnableTv.setText(tag);

                    if (position == 0) {
                        mSceneType = "0";// 自动场景
                    } else mSceneType = "1";// 手动场景
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.addTimeConditionTv.getId() ||
                v.getId() == mViewBinding.addTimeIv.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addHeaderView(LayoutInflater.from(this).inflate(R.layout.custom_bottomlist_header, null));
            builder.addItem(getString(R.string.timer_point));
            builder.addItem(getString(R.string.time_range));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    switch (position) {
                        case 0: {
                            // 时间点
                            Intent intent = new Intent(LocalSceneActivity.this, LocalTimeSelectorActivity.class);
                            startActivityForResult(intent, TIMER_INTENT_TAG);
                            break;
                        }
                        case 1: {
                            // 时间段
                            Intent intent = new Intent(LocalSceneActivity.this, LocalTimeRangeSelectorActivity.class);
                            startActivityForResult(intent, TIMER_RANGE_INTENT_TAG);
                            break;
                        }
                    }
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.sceneModeTv.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.satisfy_any_of_the_following_conditions));
            builder.addItem(getString(R.string.satisfy_all_of_the_following_conditions));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    if (position == 0) {
                        mViewBinding.sceneModeTv.setText(R.string.satisfy_any_of_the_following_conditions);
                        mSceneMode = "Any";
                    } else if (position == 1) {
                        mViewBinding.sceneModeTv.setText(R.string.satisfy_all_of_the_following_conditions);
                        mSceneMode = "All";
                    }
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.addConditionTv.getId() ||
                v.getId() == mViewBinding.addConditionIv.getId()) {
            LocalConditionDevsActivity.start(this, mGatewayId);
        } else if (v.getId() == mViewBinding.addActionTv.getId() ||
                v.getId() == mViewBinding.addActionIv.getId()) {
            LocalActionTypeActivity.start(this, mGatewayId);
        } else if (v.getId() == mViewBinding.delLayout.getId()) {
            showConfirmDialog(getString(R.string.dialog_title), String.format(getString(R.string.do_you_want_del_scene),
                    mViewBinding.nameTv.getText().toString()), getString(R.string.dialog_cancel), getString(R.string.delete), mSceneId);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<LocalSceneActivity> ref;

        public MyHandler(LocalSceneActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LocalSceneActivity activity = ref.get();
            if (activity != null) {

            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, String sceneId) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView titleTV = (TextView) view.findViewById(R.id.title_tv);
        TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
        TextView disagreeTV = (TextView) view.findViewById(R.id.disagree_btn);
        TextView agreeTV = (TextView) view.findViewById(R.id.agree_btn);

        titleTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        disagreeTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        agreeTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        contentTV.setTextSize(getResources().getDimension(R.dimen.sp_6));

        titleTV.setText(title);
        contentTV.setText(content);
        disagreeTV.setText(cancel);
        agreeTV.setText(ok);

        disagreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        agreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QMUITipDialogUtil.showLoadingDialg(LocalSceneActivity.this, R.string.is_submitted);
                RetrofitUtil.getInstance()
                        .deleteScene("chengxunfei", mGatewayMac, sceneId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                                int code = response.getInteger("code");
                                String msg = response.getString("message");
                                boolean result = response.getBoolean("result");
                                if (code == 200) {
                                    if (result) {
                                        mSceneManager.manageSceneService(mGatewayId, mSceneId, "3", mCommitFailureHandler, mResponseErrorHandler, mHandler);
                                        QMUITipDialogUtil.dismiss();
                                        if ("1".equals(mSceneType)) {
                                            // 手动场景
                                            RefreshData.refreshHomeSceneListData();
                                        }
                                        setResult(10001);
                                        finish();
                                    } else {
                                        if (msg == null || msg.length() == 0) {
                                            ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                        } else
                                            ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                    }
                                } else {
                                    if (msg == null || msg.length() == 0) {
                                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                    } else
                                        ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                QMUITipDialogUtil.dismiss();
                                ToastUtils.showLongToast(LocalSceneActivity.this, e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        dialog.show();
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_white_solid));
        window.setLayout(width - 150, height / 5);
    }

    // 提交场景
    private void submitScene() {
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
        if (mScene == null) mScene = new ItemScene();
        mSceneName = mViewBinding.nameTv.getText().toString();

        if (getString(R.string.scene_maintain_startusing).equals(mViewBinding.statusTv.getText().toString())) {
            mSceneEnable = "1";// 启用
        } else if (getString(R.string.scene_maintain_stopusing).equals(mViewBinding.statusTv.getText().toString())) {
            mSceneEnable = "0";// 禁用
        }
        if (getString(R.string.there_are).equals(mViewBinding.timeEnableTv.getText().toString())) {
            mSceneTimer = "1";// 有时间条件
        } else if (getString(R.string.nothing).equals(mViewBinding.timeEnableTv.getText().toString())) {
            mSceneTimer = "0";// 无时间条件
        }

        if (getString(R.string.there_are).equals(mViewBinding.conditionEnableTv.getText().toString())) {
            mSceneType = "0";// 自动
        } else if (getString(R.string.nothing).equals(mViewBinding.conditionEnableTv.getText().toString())) {
            mSceneType = "1";// 手动
        }

        if (mSceneName == null || mSceneName.length() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_enter_a_scene_name);
        } else if ("1".equals(mSceneTimer) && mTimerList.size() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_add_timer_condition);
        } else if ("0".equals(mSceneType) && mConditionList.size() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_add_condition);
        } else if (mActionList.size() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_add_action);
        } else {
            mScene.setMac(mGatewayMac);
            mScene.setName(mSceneName);
            mScene.setType(mSceneType);// 场景类型
            mScene.setEnable(mSceneEnable);
            // 时间条件，0：无时间条件，1：有时间条件
            if ("1".equals(mSceneTimer)) {
                mScene.setTime(mTimerList.get(0));
            } else mScene.setTime(new ItemScene.Timer());
            mScene.setConditionMode(mSceneMode);

            List<ItemScene.Condition> conditionList = new ArrayList<>();
            for (ECondition eCondition : mConditionList) {
                conditionList.add(eCondition.getCondition());
            }
            mSceneType = "1";// 手动
            if ("1".equals(mSceneType)) {
                mScene.setConditions(new ArrayList<>());
            } else {
                mScene.setConditions(conditionList);
            }

            List<ItemScene.Action> actionList = new ArrayList<>();
            for (EAction eAction : mActionList) {
                actionList.add(eAction.getAction());
            }
            mScene.setActions(actionList);

            if (getString(R.string.create_new_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                RetrofitUtil.getInstance()
                        .addScene("chengxunfei", Constant.ADD_SCENE_VER, mScene)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                                ViseLog.d(GsonUtil.toJson(response));
                                QMUITipDialogUtil.dismiss();
                                int code = response.getInteger("code");
                                boolean result = response.getBoolean("result");
                                String sceneId = response.getString("sceneId");
                                String msg = response.getString("message");
                                if (code == 200) {
                                    if (result) {
                                        QMUITipDialogUtil.dismiss();
                                        mSceneManager.manageSceneService(mGatewayId, sceneId, "1", mCommitFailureHandler, mResponseErrorHandler, mHandler);
                                        if ("1".equals(mSceneType)) {
                                            // 手动场景
                                            RefreshData.refreshHomeSceneListData();
                                        }
                                        setResult(10001);
                                        finish();
                                    } else {
                                        if (msg == null || msg.length() == 0) {
                                            ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                        } else
                                            ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                    }
                                } else {
                                    if (msg == null || msg.length() == 0) {
                                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                    } else
                                        ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                ViseLog.e(e);
                                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else if (getString(R.string.edit_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                mScene.setSceneId(mSceneId);
                mScene.setMac(mGatewayMac);
                // ViseLog.d("更新场景消息：" + GsonUtil.toJson(mScene));

                RetrofitUtil.getInstance()
                        .updateScene("chengxunfei", mScene)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                                // ViseLog.d("更新场景返回: " + response);
                                QMUITipDialogUtil.dismiss();
                                boolean result = response.getBoolean("result");
                                int code = response.getInteger("code");
                                String sceneId = response.getString("sceneId");
                                String msg = response.getString("message");
                                if (code == 200) {
                                    if (result) {
                                        QMUITipDialogUtil.dismiss();
                                        mSceneManager.manageSceneService(mGatewayId, sceneId, "2", mCommitFailureHandler, mResponseErrorHandler, mHandler);
                                        if ("1".equals(mSceneType)) {
                                            // 手动场景
                                            RefreshData.refreshHomeSceneListData();
                                        }
                                        setResult(10001);
                                        finish();
                                    } else {
                                        if (msg == null || msg.length() == 0) {
                                            ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                        } else
                                            ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                    }
                                } else {
                                    if (msg == null || msg.length() == 0) {
                                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                    } else
                                        ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                ViseLog.e(e);
                                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TIMER_INTENT_TAG: {
                // 时间点
                if (resultCode == 0 && data != null) {
                    String result = data.getStringExtra("timer_selecter_result");
                    ItemScene.Timer timer = new ItemScene.Timer("Timer", result);
                    mTimerList.clear();
                    mTimerList.add(timer);
                    mTimerAdapter.notifyDataSetChanged();

                    mViewBinding.addTimeLayout.setVisibility(View.GONE);
                    mViewBinding.timeConditionRv.setVisibility(View.VISIBLE);

                    // 当时间条件为时间点时，condition节点无效
                    mViewBinding.conditionEnableTv.setText(R.string.nothing);
                    mViewBinding.sceneModeLayout.setVisibility(View.GONE);
                }
                break;
            }
            case TIMER_RANGE_INTENT_TAG: {
                // 时间段
                if (resultCode == 0 && data != null) {
                    String result = data.getStringExtra("timer_range_selecter_result");
                    ItemScene.Timer timer = new ItemScene.Timer("TimeRange", result);
                    mTimerList.clear();
                    mTimerList.add(timer);
                    mTimerAdapter.notifyDataSetChanged();

                    mViewBinding.addTimeLayout.setVisibility(View.GONE);
                    mViewBinding.timeConditionRv.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    // 显示场景名称修改对话框
    private void showSceneNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.scene_maintain_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);

        String name = mViewBinding.nameTv.getText().toString();
        if (name != null && name.length() > 0) {
            nameEt.setText(name);
            nameEt.setSelection(name.length());
        } else {
            nameEt.setText("");
        }

        nameEt.setHint(getString(R.string.pls_input_scene_name));
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
                String nameStr = nameEt.getText().toString().trim();
                mViewBinding.nameTv.setText(nameStr);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Object obj) {
        if (obj instanceof ECondition) {
            ECondition eCondition = (ECondition) obj;
            ViseLog.d(GsonUtil.toJson(eCondition));
            if ("LocalSceneActivity".equals(eCondition.getTarget())) {
                mConditionList.remove(eCondition);
                mConditionList.add(eCondition);
                mConditionAdapter.notifyDataSetChanged();
                mViewBinding.addConditionLayout.setVisibility(View.GONE);
                mViewBinding.conditionRv.setVisibility(View.VISIBLE);
            }
        } else if (obj instanceof EAction) {
            EAction eAction = (EAction) obj;
            if ("LocalSceneActivity".equals(eAction.getTarget())) {
                ViseLog.d(GsonUtil.toJson(eAction));
                mActionList.remove(eAction);
                mActionList.add(eAction);
                mActionAdapter.notifyDataSetChanged();
                mViewBinding.addActionLayout.setVisibility(View.GONE);
                mViewBinding.actionRv.setVisibility(View.VISIBLE);
            }
        } else if (obj instanceof EEventScene) {
            EEventScene eEventScene = (EEventScene) obj;
            if ("LocalSceneActivity".equals(eEventScene.getTarget())) {
                initScene(eEventScene);
            }
            EventBus.getDefault().removeStickyEvent(obj);
        }
    }

    private String mSceneId;

    // 编辑场景时，刷新界面
    private void initScene(EEventScene eEventScene) {
        mGatewayId = eEventScene.getGatewayId();
        mGatewayMac = eEventScene.getScene().getMac();

        // ViseLog.d("mGatewayId = " + mGatewayId + " mGatewayMac = " + mGatewayMac);

        if (mGatewayId != null && mGatewayId.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_ID, mGatewayId);
        if (mGatewayMac != null && mGatewayMac.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_MAC, mGatewayMac);

        ViseLog.d(GsonUtil.toJson(eEventScene));
        mSceneId = eEventScene.getScene().getSceneId();
        ItemScene scene = eEventScene.getScene();
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.edit_scene);
        mViewBinding.delLayout.setVisibility(View.VISIBLE);
        mViewBinding.nameTv.setText(scene.getName());
        if ("1".equals(scene.getEnable())) {
            // 启用
            mViewBinding.statusTv.setText(R.string.scene_maintain_startusing);
        } else if ("0".equals(scene.getEnable())) {
            // 停用
            mViewBinding.statusTv.setText(R.string.scene_maintain_stopusing);
        }
        ItemScene.Timer timer = scene.getTime();
        if (timer.getType() == null) {
            mViewBinding.timeEnableTv.setText(R.string.nothing);
            mViewBinding.addTimeConditionLayout.setVisibility(View.GONE);
        } else {
            mViewBinding.timeEnableTv.setText(R.string.there_are);
            mViewBinding.addTimeConditionLayout.setVisibility(View.VISIBLE);
            mTimerList.clear();
            mTimerList.add(timer);
            mTimerAdapter.notifyDataSetChanged();
            mViewBinding.addTimeLayout.setVisibility(View.GONE);
            mViewBinding.timeConditionRv.setVisibility(View.VISIBLE);
        }
        if ("0".equals(scene.getType())) {
            // 自动
            mViewBinding.conditionEnableTv.setText(R.string.there_are);
            mViewBinding.sceneModeLayout.setVisibility(View.VISIBLE);
        } else if ("1".equals(scene.getType())) {
            // 手动
            mViewBinding.conditionEnableTv.setText(R.string.nothing);
            mViewBinding.sceneModeLayout.setVisibility(View.GONE);
        }

        if ("All".equals(scene.getConditionMode())) {
            // 满足所有条件
            mViewBinding.sceneModeTv.setText(R.string.satisfy_all_of_the_following_conditions);
        } else if ("Any".equals(scene.getConditionMode())) {
            // 满足以下任一条件
            mViewBinding.sceneModeTv.setText(R.string.satisfy_any_of_the_following_conditions);
        }

        mConditionList.clear();
        for (ItemScene.Condition condition : scene.getConditions()) {
            ECondition eCondition = new ECondition();
            eCondition.setCondition(condition);
            mConditionList.add(eCondition);
        }

        ViseLog.d(GsonUtil.toJson(scene.getActions()));
        mActionList.clear();
        boolean hasScene = false;
        for (ItemScene.Action action : scene.getActions()) {
            EAction eAction = new EAction();
            eAction.setAction(action);
            if ("Scene".equals(action.getType())) {
                hasScene = true;
            }
            mActionList.add(eAction);
        }
        ViseLog.d("动作 ：" + GsonUtil.toJson(mActionList));
        if (mConditionList.size() > 0) {
            if (hasScene)
                querySceneList();
            else
                queryConditionIotIdByMac("chengxunfei", 0);
        } else {
            if (hasScene)
                querySceneList();
            else
                queryActionIotIdByMac("chengxunfei", 0);
        }
    }

    // 查询本地场景列表
    private void querySceneList() {
        RetrofitUtil.getInstance().querySceneList("chengxunfei", mGatewayMac, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 0) {
                            mSceneInfo.clear();
                            for (int i = 0; i < sceneList.size(); i++) {
                                JSONObject item = sceneList.getJSONObject(i);
                                mSceneInfo.put(item.getString("sceneId"), item.getString("name"));
                            }
                            if (mConditionList.size() > 0)
                                queryConditionIotIdByMac("chengxunfei", 0);
                            else queryActionIotIdByMac("chengxunfei", 0);
                        } else {
                            ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Map<String, String> mSceneInfo = new HashMap<>();

    // 根据Mac查询条件设备IotId
    private void queryConditionIotIdByMac(String token, int pos) {
        RetrofitUtil.getInstance()
                .queryIotIdByMac(token, "1", mConditionList.get(pos).getCondition().getParameters().getDeviceId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        ViseLog.d(GsonUtil.toJson(response));
                        /*int code = response.getInteger("code");
                        String msg = response.getString("message");
                        String iotId = response.getString("iotId");
                        if (code == 200) {
                            if (iotId != null && iotId.length() > 0) {
                                mConditionList.get(pos).setIotId(iotId);
                                String pk = DeviceBuffer.getDeviceInformation(iotId).productKey;
                                mConditionList.get(pos).setKeyNickName(refreshConditionDesc(pk, mConditionList.get(pos)));
                            } else {
                                if (msg == null || msg.length() == 0) {
                                    ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                } else ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                            }
                        } else {
                            if (msg == null || msg.length() == 0) {
                                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                            } else ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                        }*/

                        // 条件
                        EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(mConditionList.get(pos).getCondition().getParameters().getDeviceId());
                        if (entry != null) {
                            String iotId = entry.iotId;
                            mConditionList.get(pos).setIotId(iotId);
                            String pk = DeviceBuffer.getDeviceInformation(iotId).productKey;
                            mConditionList.get(pos).setKeyNickName(refreshConditionDesc(pk, mConditionList.get(pos)));
                        }
                        if (pos < mConditionList.size() - 1) {
                            queryConditionIotIdByMac(token, pos + 1);
                        } else {
                            mConditionAdapter.notifyDataSetChanged();
                            mViewBinding.addConditionLayout.setVisibility(View.GONE);
                            mViewBinding.conditionRv.setVisibility(View.VISIBLE);

                            queryActionIotIdByMac(token, 0);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 根据Mac查询动作设备IotId
    private void queryActionIotIdByMac(String token, int pos) {
        ItemScene.Action action = mActionList.get(pos).getAction();
        if ("Command".equals(action.getType())) {
            // 指令
            String deviceId = action.getParameters().getDeviceId();
            if (deviceId == null || deviceId.length() == 0) return;
            RetrofitUtil.getInstance()
                    .queryIotIdByMac(token, "1", deviceId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JSONObject>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        /*int code = response.getInteger("code");
                        String msg = response.getString("message");
                        String iotId = response.getString("iotId");
                        if (code == 200) {
                            if (iotId != null && iotId.length() > 0) {
                                mConditionList.get(pos).setIotId(iotId);
                                String pk = DeviceBuffer.getDeviceInformation(iotId).productKey;
                                mConditionList.get(pos).setKeyNickName(refreshConditionDesc(pk, mConditionList.get(pos)));
                            } else {
                                if (msg == null || msg.length() == 0) {
                                    ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                                } else ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                            }
                        } else {
                            if (msg == null || msg.length() == 0) {
                                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                            } else ToastUtils.showLongToast(LocalSceneActivity.this, msg);
                        }*/

                            // 动作
                            EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(deviceId);
                            if (entry != null) {
                                String iotId = entry.iotId;
                                mActionList.get(pos).setIotId(iotId);
                                String pk = DeviceBuffer.getDeviceInformation(iotId).productKey;
                                mActionList.get(pos).setKeyNickName(refreshActionDesc(pk, mActionList.get(pos)));
                            }
                            if (pos < mActionList.size() - 1) {
                                queryActionIotIdByMac(token, pos + 1);
                            } else {
                                mActionAdapter.notifyDataSetChanged();
                                mViewBinding.addActionLayout.setVisibility(View.GONE);
                                mViewBinding.actionRv.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            ViseLog.e(e);
                            ToastUtils.showLongToast(LocalSceneActivity.this, R.string.pls_try_again_later);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if ("Scene".equals(action.getType())) {
            // 场景
            mActionList.get(pos).setKeyNickName(mSceneInfo.get(action.getParameters().getSceneId()));
            if (pos < mActionList.size() - 1) {
                queryActionIotIdByMac(token, pos + 1);
            } else {
                mActionAdapter.notifyDataSetChanged();
                mViewBinding.addActionLayout.setVisibility(View.GONE);
                mViewBinding.actionRv.setVisibility(View.VISIBLE);
            }
        }
    }

    // 条件描述
    private String refreshConditionDesc(String pk, ECondition eCondition) {
        StringBuilder desc = new StringBuilder();
        if (CTSL.PK_ONE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SIX_SCENE_SWITCH.equals(pk)) {
            desc.append(getString(R.string.trigger_buttons_2));
        } else if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(pk)) {
            // 一路窗帘
            String keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.WC_CurtainConrtol);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.curtains_control);
            }
            desc.append(keyName);
        } else if (CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(pk)) {
            // 二路窗帘
            String endId = eCondition.getCondition().getParameters().getEndpointId();
            if ("1".equals(endId)) {
                String keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWC_CurtainConrtol);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.one_curtains);
                }
                desc.append(keyName);
            } else if ("2".equals(endId)) {
                String keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWC_InnerCurtainOperation);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.two_curtains);
                }
                desc.append(keyName);
            }
        } else if (CTSL.PK_ONEWAYSWITCH.equals(pk)) {
            // 一键面板
            String keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.OWS_P_PowerSwitch_1);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.power_switch);
            }
            desc.append(keyName);
        } else if (CTSL.PK_TWOWAYSWITCH.equals(pk)) {
            // 二键面板
            String endId = eCondition.getCondition().getParameters().getEndpointId();
            String keyName = null;
            if ("1".equals(endId)) {
                keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWS_P_PowerSwitch_1);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.one_way_powerswitch);
                }
            } else if ("2".equals(endId)) {
                keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWS_P_PowerSwitch_2);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.two_way_powerswitch);
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_THREE_KEY_SWITCH.equals(pk)) {
            // 三键面板
            String endId = eCondition.getCondition().getParameters().getEndpointId();
            String keyName = null;
            switch (endId) {
                case "1": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_1);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.one_way_powerswitch);
                    }
                    break;
                }
                case "2": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_2);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.two_way_powerswitch);
                    }
                    break;
                }
                case "3": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_3);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.three_way_powerswitch);
                    }
                    break;
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_FOURWAYSWITCH_2.equals(pk)) {
            // 四键面板
            String endId = eCondition.getCondition().getParameters().getEndpointId();
            String keyName = null;
            switch (endId) {
                case "1": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.FWS_P_PowerSwitch_1);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.one_way_powerswitch);
                    }
                    break;
                }
                case "2": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.FWS_P_PowerSwitch_2);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.two_way_powerswitch);
                    }
                    break;
                }
                case "3": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.FWS_P_PowerSwitch_3);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.three_way_powerswitch);
                    }
                    break;
                }
                case "4": {
                    keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.FWS_P_PowerSwitch_4);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.four_way_powerswitch);
                    }
                    break;
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(pk)) {
            // 六键四开二场景开关
            if ("Event".equals(eCondition.getCondition().getType())) {
                desc.append(getString(R.string.trigger_buttons_2));
            } else if ("State".equals(eCondition.getCondition().getType())) {
                String endId = eCondition.getCondition().getParameters().getEndpointId();
                String keyName = null;
                switch (endId) {
                    case "1": {
                        keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1);
                        if (keyName == null || keyName.length() == 0) {
                            keyName = getString(R.string.powerswitch_1);
                        }
                        break;
                    }
                    case "2": {
                        keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2);
                        if (keyName == null || keyName.length() == 0) {
                            keyName = getString(R.string.powerswitch_2);
                        }
                        break;
                    }
                    case "3": {
                        keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3);
                        if (keyName == null || keyName.length() == 0) {
                            keyName = getString(R.string.powerswitch_3);
                        }
                        break;
                    }
                    case "4": {
                        keyName = DeviceBuffer.getExtendedInfo(eCondition.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4);
                        if (keyName == null || keyName.length() == 0) {
                            keyName = getString(R.string.powerswitch_4);
                        }
                        break;
                    }
                }
                desc.append(keyName);
            }
        } else if (CTSL.PK_OUTLET.equals(pk)) {
            // 插座
            desc.append(getString(R.string.power_switch));
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(pk) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(pk) ||
                CTSL.PK_VRV_AC.equals(pk)) {
            // 空调二管制、四管制、VRV温控器
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature));
            }
        } else if (CTSL.PK_FLOORHEATING001.equals(pk)) {
            // 电地暖
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature));
            }
        } else if (CTSL.PK_FAU.equals(pk)) {
            // 新风
            if ("FanMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature));
            }
        } else if (CTSL.PK_LIGHT.equals(pk)) {
            // 调光调色
            desc.append(getString(R.string.power_switch));
        } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(pk)) {
            // 单调光
            desc.append(getString(R.string.power_switch));
        } else if (CTSL.PK_PIRSENSOR.equals(pk)) {
            // 人体红外感应器
            desc.append(getString(R.string.infrared_detection_status));
        } else if (CTSL.PK_GASSENSOR.equals(pk)) {
            // 燃气感应器
            desc.append(getString(R.string.gas_detection_status));
        } else if (CTSL.PK_TEMHUMSENSOR.equals(pk)) {
            // 温湿度传感器
            if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.detailsensor_temperature));
            } else if ("Humidity".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.detailsensor_humidity));
            }
        } else if (CTSL.PK_SMOKESENSOR.equals(pk)) {
            // 烟雾传感器
            desc.append(getString(R.string.smoke_detection_status));
        } else if (CTSL.PK_WATERSENSOR.equals(pk)) {
            // 水浸传感器
            desc.append(getString(R.string.water_detection_status));
        } else if (CTSL.PK_DOORSENSOR.equals(pk)) {
            // 门磁传感器
            desc.append(getString(R.string.door_detection_status));
        }
        return desc.toString();
    }

    // 动作描述
    private String refreshActionDesc(String pk, EAction eAction) {
        StringBuilder desc = new StringBuilder();
        if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(pk)) {
            // 一路窗帘
            String keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.WC_CurtainConrtol);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.curtains_control);
            }
            desc.append(keyName);
        } else if (CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(pk)) {
            // 二路窗帘
            String endId = eAction.getAction().getParameters().getEndpointId();
            if ("1".equals(endId)) {
                String keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWC_CurtainConrtol);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.one_curtains);
                }
                desc.append(keyName);
            } else if ("2".equals(endId)) {
                String keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWC_InnerCurtainOperation);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.two_curtains);
                }
                desc.append(keyName);
            }
        } else if (CTSL.PK_ONEWAYSWITCH.equals(pk)) {
            // 一键面板
            String keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.OWS_P_PowerSwitch_1);
            if (keyName == null || keyName.length() == 0) {
                keyName = getString(R.string.power_switch);
            }
            desc.append(keyName);
        } else if (CTSL.PK_TWOWAYSWITCH.equals(pk)) {
            // 二键面板
            String endId = eAction.getAction().getParameters().getEndpointId();
            String keyName = null;
            if ("1".equals(endId)) {
                keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWS_P_PowerSwitch_1);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.one_way_powerswitch);
                }
            } else if ("2".equals(endId)) {
                keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWS_P_PowerSwitch_2);
                if (keyName == null || keyName.length() == 0) {
                    keyName = getString(R.string.two_way_powerswitch);
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_THREE_KEY_SWITCH.equals(pk)) {
            // 三键面板
            String endId = eAction.getAction().getParameters().getEndpointId();
            String keyName = null;
            switch (endId) {
                case "1": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_1);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.one_way_powerswitch);
                    }
                    break;
                }
                case "2": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_2);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.two_way_powerswitch);
                    }
                    break;
                }
                case "3": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.TWS_P3_PowerSwitch_3);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.three_way_powerswitch);
                    }
                    break;
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_FOURWAYSWITCH_2.equals(pk)) {
            // 四键面板
            String endId = eAction.getAction().getParameters().getEndpointId();
            String keyName = null;
            switch (endId) {
                case "1": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.FWS_P_PowerSwitch_1);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.one_way_powerswitch);
                    }
                    break;
                }
                case "2": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.FWS_P_PowerSwitch_2);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.two_way_powerswitch);
                    }
                    break;
                }
                case "3": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.FWS_P_PowerSwitch_3);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.three_way_powerswitch);
                    }
                    break;
                }
                case "4": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.FWS_P_PowerSwitch_4);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.four_way_powerswitch);
                    }
                    break;
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(pk)) {
            // 六键四开二场景开关
            String endId = eAction.getAction().getParameters().getEndpointId();
            String keyName = null;
            switch (endId) {
                case "1": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.powerswitch_1);
                    }
                    break;
                }
                case "2": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.powerswitch_2);
                    }
                    break;
                }
                case "3": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.powerswitch_3);
                    }
                    break;
                }
                case "4": {
                    keyName = DeviceBuffer.getExtendedInfo(eAction.getIotId()).getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4);
                    if (keyName == null || keyName.length() == 0) {
                        keyName = getString(R.string.powerswitch_4);
                    }
                    break;
                }
            }
            desc.append(keyName);
        } else if (CTSL.PK_OUTLET.equals(pk)) {
            // 插座
            desc.append(getString(R.string.power_switch));
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(pk) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(pk) ||
                CTSL.PK_VRV_AC.equals(pk)) {
            // 空调二管制、四管制、VRV温控器
            String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
            String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
            String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch));
                else desc.append(getString(R.string.work_mode));
            } else if (fanMode != null && fanMode.length() > 0) {
                desc.append(getString(R.string.fan_speed));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature));
            }
        } else if (CTSL.PK_FLOORHEATING001.equals(pk)) {
            // 电地暖
            String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
            String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
            if (workMode != null && workMode.length() > 0) {
                desc.append(getString(R.string.power_switch));
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch));
                else desc.append(getString(R.string.work_mode));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature));
            }
        } else if (CTSL.PK_FAU.equals(pk)) {
            // 新风
            String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
            String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
            if (fanMode != null && fanMode.length() > 0) {
                if ("0".equals(fanMode) || "4".equals(fanMode))
                    desc.append(getString(R.string.power_switch));
                else desc.append(getString(R.string.fan_speed));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature));
            }
        } else if (CTSL.PK_LIGHT.equals(pk)) {
            // 调光调色
            String level = eAction.getAction().getParameters().getCommand().getString("Level");
            if (level != null && level.length() > 0) {
                desc.append(getString(R.string.lightness));
            }
        } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(pk)) {
            // 单调光
            String level = eAction.getAction().getParameters().getCommand().getString("Level");
            String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
            if (level != null && level.length() > 0) {
                desc.append(getString(R.string.lightness));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.color_temperature));
            }
        }
        return desc.toString();
    }
}