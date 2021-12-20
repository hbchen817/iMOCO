package com.laffey.smart.view;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
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
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
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
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SwitchLocalSceneActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLocalSceneBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private static final String GATEWAY_MAC = "gateway_mac";
    private static final String SWITCH_CODE = "switch_code";
    private static final String IOT_ID = "iot_id";
    private static final String DEV_MAC = "dev_mac";

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
    private String mKeyCode;// 键值
    private String mIotId;
    private String mDevMac;
    private String mSceneId;
    private String mAutoSceneId;
    private final List<String> mAutoSceneIds = new ArrayList<>();
    private JSONObject mAppParams;

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
    private String[] mCIds;
    private List<ItemSceneInGateway> mAutoScenes = new ArrayList<>();

    private ECondition mTmpECondition = null;
    private EAction mTmpEAction = null;

    public static void start(Activity activity, String gatewayId, String gatewayMac, String iotId, String devMac, String keyCode, int requestCode) {
        Intent intent = new Intent(activity, SwitchLocalSceneActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(GATEWAY_MAC, gatewayMac);
        intent.putExtra(SWITCH_CODE, keyCode);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(DEV_MAC, devMac);
        activity.startActivityForResult(intent, requestCode);
    }

    public static Intent getIntent(Activity activity, String gatewayId, String gatewayMac) {
        Intent intent = new Intent(activity, SwitchLocalSceneActivity.class);
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
        mKeyCode = getIntent().getStringExtra(SWITCH_CODE);
        mIotId = getIntent().getStringExtra(IOT_ID);
        mDevMac = getIntent().getStringExtra(DEV_MAC);

        if (mGatewayId != null && mGatewayId.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_ID, mGatewayId);
        if (mGatewayMac != null && mGatewayMac.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_MAC, mGatewayMac);

        initConditionAdapter();
        initActionAdapter();
        EventBus.getDefault().register(this);

        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initView();
        // QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        initData();
        RealtimeDataReceiver.addEventCallbackHandler("SwitchLocalSceneCallback", mHandler);

        // ViseLog.d("场景缓存 = " + GsonUtil.toJson(DeviceBuffer.getAllScene()));
        /*mSceneManager.manageSceneService(mGatewayId, "283", 3,
                null, null, null);*/
    }

    private void initData() {
        // queryMacByIotId();
        mSceneManager = new SceneManager(this);
        DeviceBuffer.addCacheInfo("LocalSceneTag", "SwitchLocalSceneActivity");

        ViseLog.d("mSceneId = " + mSceneId + "\n场景缓存 = \n" + GsonUtil.toJson(DeviceBuffer.getAllScene()));
        if (mSceneId != null && mSceneId.length() > 0 && DeviceBuffer.getScene(mSceneId) != null &&
                DeviceBuffer.getScene(mSceneId).getAppParams() != null) {
            mAutoSceneId = DeviceBuffer.getScene(mSceneId).getAppParams().getString("cId");
            if (mAutoSceneId != null && mAutoSceneId.length() > 0) {
                String[] tmp = mAutoSceneId.split(",");
                if (tmp != null && tmp.length > 0) {
                    for (String id : tmp) {
                        if (DeviceBuffer.getScene(id) != null)
                            mAutoSceneIds.add(id);
                    }
                }
            }
        }
        ViseLog.d("mAutoSceneId = " + mAutoSceneId);
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
                    holder.setTextColor(R.id.title, ContextCompat.getColor(SwitchLocalSceneActivity.this, R.color.red));

                    holder.setText(R.id.detail, "--");
                    holder.setTextColor(R.id.detail, ContextCompat.getColor(SwitchLocalSceneActivity.this, R.color.red));
                }

                holder.setVisible(R.id.divider, mConditionList.indexOf(eCondition) != 0);
            }
        };
        mConditionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ECondition eCondition = mConditionList.get(position);
                if (eCondition.getIotId() != null && eCondition.getIotId().length() > 0) {
                    mTmpECondition = JSONObject.parseObject(GsonUtil.toJson(eCondition), ECondition.class);
                    eCondition.setTarget("LocalConditionValueActivity");
                    EventBus.getDefault().postSticky(eCondition);
                    Intent intent = new Intent(SwitchLocalSceneActivity.this, LocalConditionValueActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showLongToast(SwitchLocalSceneActivity.this, R.string.dev_does_not_exist);
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
                    EDevice.deviceEntry dev = DeviceBuffer.getDeviceInformation(eAction.getIotId());
                    if (dev != null) {
                        holder.setText(R.id.title, dev.nickName);

                        holder.setText(R.id.detail, refreshActionDesc(eAction));
                    } else {
                        holder.setText(R.id.title, getString(R.string.dev_does_not_exist));
                        holder.setTextColor(R.id.title, ContextCompat.getColor(SwitchLocalSceneActivity.this, R.color.red));
                        holder.setText(R.id.detail, "--");
                        holder.setTextColor(R.id.detail, ContextCompat.getColor(SwitchLocalSceneActivity.this, R.color.red));
                    }
                } else if ("Scene".equals(type)) {
                    String keyNickName = null;
                    ItemSceneInGateway scene = DeviceBuffer.getScene(eAction.getAction().getParameters().getSceneId());
                    if (scene != null) {
                        keyNickName = scene.getSceneDetail().getName();
                    }
                    icon.setText(R.string.icon_scene);
                    if (keyNickName != null && keyNickName.length() > 0) {
                        holder.setText(R.id.title, keyNickName)
                                .setText(R.id.detail, getString(R.string.rb_tab_two_desc));
                    } else {
                        holder.setText(R.id.title, getString(R.string.scene_does_not_exist))
                                .setText(R.id.detail, getString(R.string.rb_tab_two_desc));
                        holder.setTextColor(R.id.title, ContextCompat.getColor(SwitchLocalSceneActivity.this, R.color.red));
                    }
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
                        mTmpEAction = JSONObject.parseObject(GsonUtil.toJson(eAction), EAction.class);
                        eAction.setTarget("LocalActionValueActivity");
                        EventBus.getDefault().postSticky(eAction);

                        Intent intent = new Intent(SwitchLocalSceneActivity.this, LocalActionValueActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtils.showLongToast(SwitchLocalSceneActivity.this, R.string.dev_does_not_exist);
                    }
                } else if ("Scene".equals(eAction.getAction().getType())) {
                    TextView title = view.findViewById(R.id.title);
                    if (getString(R.string.scene_does_not_exist).equals(title.getText().toString())) {
                        ToastUtils.showLongToast(SwitchLocalSceneActivity.this, R.string.scene_does_not_exist);
                    } else {
                        mTmpEAction = JSONObject.parseObject(GsonUtil.toJson(eAction), EAction.class);
                        eAction.setTarget("LocalActionScenesActivity");
                        EventBus.getDefault().postSticky(eAction);

                        LocalActionScenesActivity.start(SwitchLocalSceneActivity.this,
                                mGatewayId, mIotId, mSceneId, "SwitchLocalSceneActivity");
                    }
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
                CTSL.PK_SYT_ONE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SIX_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_SIX_SCENE_SWITCH.equals(pk) ||
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
                        if (CTSL.PK_ONE_SCENE_SWITCH.equals(pk) ||
                                CTSL.PK_SYT_ONE_SCENE_SWITCH.equals(pk))
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
                CTSL.PK_FAU.equals(pk) ||
                CTSL.PK_MULTI_THREE_IN_ONE.equals(pk) ||
                CTSL.PK_MULTI_AC_AND_FH.equals(pk) ||
                CTSL.PK_MULTI_AC_AND_FA.equals(pk) ||
                CTSL.PK_MULTI_FH_AND_FA.equals(pk)) {
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
                } else if ("AutoWorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                    desc.append(eCondition.getKeyNickName() + "：");
                    if ("0".equals(eCondition.getCondition().getParameters().getCompareValue())) {
                        desc.append(getString(R.string.oneswitch_state_off));
                    } else if ("10".equals(eCondition.getCondition().getParameters().getCompareValue())) {
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
                case "2": {
                    // 反转
                    desc.append(getString(R.string.reverse));
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
                CTSL.PK_VRV_AC.equals(pk) ||
                CTSL.PK_MULTI_THREE_IN_ONE.equals(pk) ||
                CTSL.PK_MULTI_AC_AND_FH.equals(pk) ||
                CTSL.PK_MULTI_AC_AND_FA.equals(pk) ||
                CTSL.PK_MULTI_FH_AND_FA.equals(pk) ||
                CTSL.PK_FLOORHEATING001.equals(pk) ||
                CTSL.PK_FAU.equals(pk)) {
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
                    case "0": {
                        desc.append(getString(R.string.close));
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
                    case "4": {
                        desc.append(getString(R.string.open));
                        break;
                    }
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
                    LocalTimeSelectorActivity.startForResult(SwitchLocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_INTENT_TAG);
                } else if ("TimeRange".equals(mTimerList.get(position).getType())) {
                    // 时间段
                    LocalTimeRangeSelectorActivity.startForResult(SwitchLocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_RANGE_INTENT_TAG);
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
        AlertDialog alert = new AlertDialog.Builder(SwitchLocalSceneActivity.this).create();
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

        mViewBinding.timeEnableLayout.setVisibility(View.GONE);
        mViewBinding.timeEnableDivider.setVisibility(View.GONE);
        mViewBinding.conditionEnableLayout.setVisibility(View.GONE);
        mViewBinding.addTimeConditionLayout.setVisibility(View.GONE);
        mViewBinding.sceneModeLayout.setVisibility(View.GONE);
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
        DeviceBuffer.addCacheInfo("LocalSceneTag", "SwitchLocalSceneActivity");
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
                ToastUtils.showLongToast(SwitchLocalSceneActivity.this, R.string.condition_setting_is_invalid_when_time_trigger_is_point_in_time);
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
                            Intent intent = new Intent(SwitchLocalSceneActivity.this, LocalTimeSelectorActivity.class);
                            startActivityForResult(intent, TIMER_INTENT_TAG);
                            break;
                        }
                        case 1: {
                            // 时间段
                            Intent intent = new Intent(SwitchLocalSceneActivity.this, LocalTimeRangeSelectorActivity.class);
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
            LocalActionTypeActivity.start(this, mGatewayId, mIotId, "SwitchLocalSceneActivity");
        } else if (v.getId() == mViewBinding.delLayout.getId()) {
            showConfirmDialog(getString(R.string.dialog_title), String.format(getString(R.string.do_you_want_del_scene),
                    mViewBinding.nameTv.getText().toString()), getString(R.string.dialog_cancel), getString(R.string.delete), mSceneId);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SwitchLocalSceneActivity> ref;

        public MyHandler(SwitchLocalSceneActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SwitchLocalSceneActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    /*case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                        // 删除网关下的场景
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        JSONObject value = jsonObject.getJSONObject("value");
                        String identifier = jsonObject.getString("identifier");
                        if ("ManageSceneNotification".equals(identifier)) {
                            String type = value.getString("Type");
                            String status = value.getString("Status");
                            // status  0: 成功  1: 失败
                            if ("0".equals(status)) {
                                // type  1: 增加场景  2: 编辑场景  3: 删除场景
                                if ("3".equals(type)) {
                                    if (DeviceBuffer.getScene(activity.mSceneId) != null) {
                                        activity.mSceneManager.deleteScene(activity, activity.mGatewayMac, activity.mSceneId,
                                                Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mHandler);
                                    } else {
                                        activity.mSceneManager.deleteScene(activity, activity.mGatewayMac, activity.mAutoSceneId,
                                                Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mHandler);
                                    }
                                }
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            }
                        }
                        break;
                    }*/
                    case Constant.MSG_QUEST_UPDATE_SCENE: {
                        // 更新本地场景
                        JSONObject response = (JSONObject) msg.obj;
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String sceneId = response.getString("sceneId");
                        String message = response.getString("message");
                        if (code == 200) {
                            boolean result = response.getBoolean("result");
                            if (result) {
                                SceneManager.manageSceneService(activity.mGatewayId, sceneId, 2,
                                        activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                                if (activity.mAutoScenes == null || activity.mAutoScenes.size() == 0) {
                                    QMUITipDialogUtil.dismiss();
                                    // RefreshData.refreshHomeSceneListData();
                                    activity.setResult(Constant.RESULT_CODE_UPDATE_SCENE);
                                    activity.finish();
                                } else {
                                    if (sceneId.equals(activity.mSubmitScene.getSceneDetail().getSceneId())) {
                                        activity.mSceneManager.updateScene(activity, activity.mAutoScenes.get(0),
                                                Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, activity.mHandler);
                                    } else {
                                        int pos = 0;
                                        for (int i = 0; i < activity.mAutoScenes.size(); i++) {
                                            if (sceneId.equals(activity.mAutoScenes.get(i).getSceneDetail().getSceneId())) {
                                                pos = i + 1;
                                                break;
                                            }
                                        }
                                        if (pos <= activity.mAutoScenes.size() - 1) {
                                            activity.mSceneManager.updateScene(activity, activity.mAutoScenes.get(pos),
                                                    Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, activity.mHandler);
                                        } else {
                                            QMUITipDialogUtil.dismiss();
                                            // RefreshData.refreshHomeSceneListData();
                                            activity.setResult(Constant.RESULT_CODE_UPDATE_SCENE);
                                            activity.finish();
                                        }
                                    }
                                }
                            } else {
                                if (message == null || message.length() == 0) {
                                    ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                                } else
                                    ToastUtils.showLongToast(activity, message);
                            }
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response);
                        }
                        break;
                    }
                    case Constant.MSG_QUEST_ADD_SCENE_ERROR:
                    case Constant.MSG_QUEST_UPDATE_SCENE_ERROR: {
                        // 更新本地场景失败
                        QMUITipDialogUtil.dismiss();
                        Throwable e = (Throwable) msg.obj;
                        ViseLog.e(e);
                        ToastUtils.showLongToast(activity, e.getMessage());
                        break;
                    }
                    case Constant.MSG_QUEST_ADD_SCENE: {
                        // 新增本地场景
                        JSONObject response = (JSONObject) msg.obj;
                        ViseLog.d(GsonUtil.toJson(response));
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String sceneId = response.getString("sceneId");
                        String message = response.getString("message");
                        if (code == 200) {
                            boolean result = response.getBoolean("result");
                            if (result) {
                                QMUITipDialogUtil.dismiss();
                                SceneManager.manageSceneService(activity.mGatewayId, sceneId, 1,
                                        activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                                // RefreshData.refreshHomeSceneListData();
                                activity.setResult(Constant.ADD_LOCAL_SCENE);
                                activity.finish();
                            } else {
                                if (message == null || message.length() == 0) {
                                    ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                                } else
                                    ToastUtils.showLongToast(activity, message);
                            }
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, String sceneId) {
        DialogUtils.showConfirmDialog(this, title, content, ok, cancel, new DialogUtils.Callback() {
            @Override
            public void positive() {
                QMUITipDialogUtil.showLoadingDialg(SwitchLocalSceneActivity.this, R.string.is_submitted);
                deleteScene(mSceneId);
            }

            @Override
            public void negative() {

            }
        });
    }

    private void deleteScene(String sceneId) {
        SceneManager.deleteScene(this, DeviceBuffer.getDeviceMac(mGatewayId), sceneId, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    if (mSceneId != null && mSceneId.equals(sceneId)) {
                        DeviceBuffer.removeScene(sceneId);
                        SceneManager.manageSceneService(mGatewayId, sceneId, 3,
                                mCommitFailureHandler, mResponseErrorHandler, mHandler);
                        if (mAutoSceneIds != null && mAutoSceneIds.size() > 0) {
                            deleteScene(mAutoSceneIds.get(0));
                        } else {
                            QMUITipDialogUtil.dismiss();
                            RefreshData.refreshHomeSceneListData();
                            setResult(Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY);
                            finish();
                        }
                    } else {
                        if (sceneId != null) {
                            int pos = -1;
                            for (int i = 0; i < mAutoSceneIds.size(); i++) {
                                if (sceneId.equals(mAutoSceneIds.get(i))) {
                                    pos = i + 1;
                                    break;
                                }
                            }
                            DeviceBuffer.removeScene(sceneId);
                            SceneManager.manageSceneService(mGatewayId, sceneId, 3,
                                    mCommitFailureHandler, mResponseErrorHandler, mHandler);
                            if (pos >= 0 && pos < mAutoSceneIds.size()) {
                                deleteScene(mAutoSceneIds.get(pos));
                            } else {
                                QMUITipDialogUtil.dismiss();
                                RefreshData.refreshHomeSceneListData();
                                setResult(Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY);
                                finish();
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            RefreshData.refreshHomeSceneListData();
                            setResult(Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY);
                            finish();
                        }
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(SwitchLocalSceneActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(SwitchLocalSceneActivity.this, e.getMessage());
            }
        });
    }

    // 提交场景
    private void submitScene() {
        EDevice.deviceEntry gwDev = DeviceBuffer.getDeviceInformation(mGatewayId);
        if (gwDev.status == Constant.CONNECTION_STATUS_OFFLINE) {
            // 网关离线，无法创建、编辑场景
            if (getString(R.string.create_new_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                // 网关离线，无法创建场景
                ToastUtils.showLongToast(this, R.string.gw_is_offline_cannot_create_scene);
            } else if (getString(R.string.edit_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                ToastUtils.showLongToast(this, R.string.gw_is_offline_cannot_edit_scene);
            }
            return;
        }

        if (mScene == null) mScene = new ItemScene();
        mSceneName = mViewBinding.nameTv.getText().toString();

        if (getString(R.string.scene_maintain_startusing).equals(mViewBinding.statusTv.getText().toString())) {
            mSceneEnable = "1";// 启用
        } else if (getString(R.string.scene_maintain_stopusing).equals(mViewBinding.statusTv.getText().toString())) {
            mSceneEnable = "0";// 禁用
        }
        mSceneTimer = "0";// 无时间条件
        mSceneType = "1";// 手动

        if (mSceneName == null || mSceneName.length() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_enter_a_scene_name);
        } else if (mActionList.size() == 0) {
            ToastUtils.showLongToast(this, R.string.pls_add_action);
        } else {
            mScene.setMac(mGatewayMac);
            mScene.setName(mSceneName);
            mScene.setType(mSceneType);// 场景类型
            mScene.setEnable(mSceneEnable);
            // 无时间条件
            mScene.setTime(new ItemScene.Timer());
            mScene.setConditionMode(mSceneMode);

            mScene.setConditions(new ArrayList<>());

            List<ItemScene.Action> actionList = new ArrayList<>();
            for (EAction eAction : mActionList) {
                actionList.add(eAction.getAction());
            }
            mScene.setActions(actionList);
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
            if (getString(R.string.create_new_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                JSONObject appParams = new JSONObject();
                appParams.put("type", "e");// 场景开关
                appParams.put("switchIotId", mIotId);// 场景开关iotId

                ItemSceneInGateway scene = new ItemSceneInGateway();
                scene.setGwMac(mGatewayMac);
                scene.setAppParams(appParams);
                scene.setSceneDetail(mScene);

                // ViseLog.d("新建场景 = " + GsonUtil.toJson(scene));
                mSceneManager.addScene(this, scene, Constant.MSG_QUEST_ADD_SCENE, Constant.MSG_QUEST_ADD_SCENE_ERROR, mHandler);
            } else if (getString(R.string.edit_scene).equals(mViewBinding.includeToolbar.tvToolbarTitle.getText().toString())) {
                mSubmitScene = new ItemSceneInGateway();
                mSubmitScene.setGwMac(mGatewayMac);
                mSubmitScene.setAppParams(mAppParams);
                mScene.setSceneId(mSceneId);
                mSubmitScene.setSceneDetail(mScene);

                String cId = mAppParams.getString("cId");
                if (cId != null && cId.length() > 0) {
                    mCIds = cId.split(",");
                }

                // ViseLog.d("更新场景消息：" + GsonUtil.toJson(scene));
                if (mCIds == null || mCIds.length == 0) {
                    mAutoScenes.clear();
                    mSceneManager.updateScene(this, mSubmitScene, Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, mHandler);
                } else {
                    mAutoScenes.clear();
                    mAutoScenes.addAll(getAutoScenes(mSubmitScene));
                    mSceneManager.updateScene(this, mSubmitScene, Constant.MSG_QUEST_UPDATE_SCENE, Constant.MSG_QUEST_UPDATE_SCENE_ERROR, mHandler);
                }
            }
        }
    }

    // 根据手动场景解析关联的自动场景
    private List<ItemSceneInGateway> getAutoScenes(ItemSceneInGateway scene) {
        List<ItemSceneInGateway> list = new ArrayList<>();
        JSONObject appParams = scene.getAppParams();
        if (appParams != null) {
            String cId = appParams.getString("cId");
            if (cId != null || cId.length() > 0) {
                String[] cIds = cId.split(",");
                for (int i = 0; i < cIds.length; i++) {
                    ItemSceneInGateway tmp = DeviceBuffer.getScene(cIds[i]);
                    tmp.getSceneDetail().setActions(scene.getSceneDetail().getActions());
                    tmp.getSceneDetail().setName(scene.getSceneDetail().getName());
                    tmp.getSceneDetail().setEnable(scene.getSceneDetail().getEnable());
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    private ItemSceneInGateway mSubmitScene;

    private ItemScene.Condition getCondition(String eventType, String name, String keyCode) {
        ItemScene.Condition condition = new ItemScene.Condition();
        condition.setType("Event");

        ItemScene.ConditionParameter parameter = new ItemScene.ConditionParameter();
        parameter.setDeviceId(DeviceBuffer.getDeviceMac(mIotId));
        parameter.setEndpointId(keyCode);
        parameter.setEventType(eventType);
        parameter.setParameterName(name);
        parameter.setCompareType("==");
        parameter.setCompareValue(keyCode);

        condition.setParameters(parameter);
        return condition;
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
        RealtimeDataReceiver.deleteCallbackHandler("SwitchLocalSceneCallback");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Object obj) {
        if (obj instanceof ECondition) {
            ECondition eCondition = (ECondition) obj;
            if ("SwitchLocalSceneActivity".equals(eCondition.getTarget())) {
                mConditionList.remove(eCondition);
                mConditionList.add(eCondition);
                mConditionAdapter.notifyDataSetChanged();
                mViewBinding.addConditionLayout.setVisibility(View.GONE);
                mViewBinding.conditionRv.setVisibility(View.VISIBLE);
                EventBus.getDefault().removeStickyEvent(obj);
            }
        } else if (obj instanceof EAction) {
            EAction eAction = (EAction) obj;
            if ("SwitchLocalSceneActivity".equals(eAction.getTarget())) {
                List<EAction> preList = new ArrayList<>();
                preList.add(eAction);

                List<EAction> afterList = new ArrayList<>();
                afterList.addAll(mActionList);
                afterList.remove(eAction);

                boolean isContains = compareAction(preList, afterList);
                if (isContains) {
                    ToastUtils.showLongToast(this, R.string.actions_duplicate_pls_select_again);
                    mActionList.set(mActionList.indexOf(eAction), mTmpEAction);
                    mActionAdapter.notifyDataSetChanged();
                    return;
                }
                List<EAction> eActionList = new ArrayList<>();
                eActionList.add(eAction);
                boolean isSame = compareConditionAndAction(mConditionList, eActionList);// 比较条件与动作是否相同
                if (isSame) {
                    ToastUtils.showLongToast(this, R.string.action_can_not_be_condition_at_same_time);
                    mActionList.set(mActionList.indexOf(eAction), mTmpEAction);
                    mActionAdapter.notifyDataSetChanged();
                    return;
                }

                if (!mActionList.contains(eAction)) {
                    mActionList.add(eAction);
                } else {
                    mActionList.set(mActionList.indexOf(eAction), eAction);
                }
                mActionAdapter.notifyDataSetChanged();
                mViewBinding.addActionLayout.setVisibility(View.GONE);
                mViewBinding.actionRv.setVisibility(View.VISIBLE);
                EventBus.getDefault().removeStickyEvent(obj);
            }
        } else if (obj instanceof EEventScene) {
            EEventScene eEventScene = JSONObject.parseObject(GsonUtil.toJson(obj), EEventScene.class);
            if ("SwitchLocalSceneActivity".equals(eEventScene.getTarget())) {
                initScene(eEventScene);
                EventBus.getDefault().removeStickyEvent(obj);
            }
        }
    }

    // 比较条件是否相同 true: 相同  false: 不同
    private boolean compareCondition(ItemScene.Condition preCondition, List<ECondition> eConditionList) {
        boolean isContains = false;
        for (ECondition eCondition : eConditionList) {
            ItemScene.Condition afterCondition = eCondition.getCondition();
            String preConditionS = GsonUtil.toJson(preCondition);
            String afterConditionS = GsonUtil.toJson(afterCondition);
            if (preConditionS.equals(afterConditionS)) {
                return true;
            }
        }
        return isContains;
    }

    // 比较动作是否相同 true: 相同  false: 不同
    private boolean compareAction(List<EAction> preList, List<EAction> afterList) {
        boolean isContains = false;

        List<ItemScene.Action> preActionList = queryAllActions(preList);
        List<ItemScene.Action> afterActionList = queryAllActions(afterList);
        for (ItemScene.Action preAction : preActionList) {
            for (ItemScene.Action afterAction : afterActionList) {
                String preActionS = GsonUtil.toJson(preAction);
                String afterActionS = GsonUtil.toJson(afterAction);
                if (preActionS == null && afterActionS == null)
                    return true;
                assert preActionS != null;
                if (preActionS.equals(afterActionS)) {
                    return true;
                }
            }
        }
        return isContains;
    }

    // 比较场景条件与动作是否相同 true: 相同  false: 不同
    private boolean compareConditionAndAction(List<ECondition> eConditionList, List<EAction> eActionList) {
        for (ECondition eCondition : eConditionList) {
            ItemScene.Condition condition = eCondition.getCondition();
            String devId = condition.getParameters().getDeviceId();
            String endpointId = condition.getParameters().getEndpointId();
            String name = condition.getParameters().getName();
            String compareValue = condition.getParameters().getCompareValue();

            List<ItemScene.Action> actionList = queryAllActions(eActionList);
            for (ItemScene.Action action : actionList) {
                ItemScene.ActionParameter actionParameter = action.getParameters();
                if (devId.equals(actionParameter.getDeviceId())
                        && endpointId.equals(actionParameter.getEndpointId())
                        && actionParameter.getCommand() != null
                        && compareValue.equals(actionParameter.getCommand().getString(name))) {
                    return true;
                }
            }
        }
        return false;
    }

    // 获取手动场景下所有设备动作
    private List<ItemScene.Action> queryAcionsInScene(String sceneId) {
        List<ItemScene.Action> list = new ArrayList<>();
        ItemSceneInGateway scene = DeviceBuffer.getScene(sceneId);
        if (scene != null) {
            List<ItemScene.Action> actionList = scene.getSceneDetail().getActions();
            for (ItemScene.Action action : actionList) {
                if ("Command".equals(action.getType())) {
                    list.add(action);
                } else if ("Scene".equals(action.getType())) {
                    String actionSceneId = action.getParameters().getSceneId();
                    list.addAll(queryAcionsInScene(actionSceneId));
                }
            }
        }
        return list;
    }

    // 获取所有场景动作
    private List<ItemScene.Action> queryAllActions(List<EAction> list) {
        List<ItemScene.Action> actionList = new ArrayList<>();
        for (EAction eAction : list) {
            ItemScene.Action action = eAction.getAction();
            if ("Command".equals(action.getType())) {
                actionList.add(action);
            } else if ("Scene".equals(action.getType())) {
                actionList.addAll(queryAcionsInScene(action.getParameters().getSceneId()));
            }
        }
        return actionList;
    }

    // 编辑场景时，刷新界面
    private void initScene(EEventScene eEventScene) {
        mGatewayId = eEventScene.getGatewayId();
        mGatewayMac = eEventScene.getGatewayMac();
        mAppParams = eEventScene.getAppParams();

        if (mGatewayId != null && mGatewayId.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_ID, mGatewayId);
        if (mGatewayMac != null && mGatewayMac.length() > 0)
            SpUtils.putStringValue(this, SpUtils.SP_DEVS_INFO, GATEWAY_MAC, mGatewayMac);

        // ViseLog.d(GsonUtil.toJson(eEventScene));
        mIotId = eEventScene.getAppParams().getString("switchIotId");
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

        if (mConditionList.size() == 0) {
            mViewBinding.conditionEnableTv.setText(R.string.nothing);
            mViewBinding.sceneModeLayout.setVisibility(View.GONE);
        } else {
            mViewBinding.conditionEnableTv.setText(R.string.there_are);
            mViewBinding.sceneModeLayout.setVisibility(View.VISIBLE);
        }

        // ViseLog.d(GsonUtil.toJson(scene.getActions()));
        mActionList.clear();
        boolean hasScene = false;
        for (ItemScene.Action action : scene.getActions()) {
            EAction eAction = new EAction();
            eAction.setAction(action);
            ViseLog.d("eAction = \n" + GsonUtil.toJson(eAction));
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
        List<ItemSceneInGateway> list = DeviceBuffer.getAllScene(mGatewayMac);
        mSceneInfo.clear();
        for (int i = 0; i < list.size(); i++) {
            ItemSceneInGateway scene = list.get(i);
            mSceneInfo.put(scene.getSceneDetail().getSceneId(), scene.getSceneDetail().getName());
        }
        if (mConditionList.size() > 0)
            queryConditionIotIdByMac("chengxunfei", 0);
        else queryActionIotIdByMac("chengxunfei", 0);
    }

    private Map<String, String> mSceneInfo = new HashMap<>();

    // 根据Mac查询条件设备IotId
    private void queryConditionIotIdByMac(String token, int pos) {
        // 条件
        EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(mConditionList.get(pos).getCondition().getParameters().getDeviceId());
        if (entry != null) {
            String iotId = entry.iotId;
            mConditionList.get(pos).setIotId(iotId);
            String pk = entry.productKey;
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

    // 根据Mac查询动作设备IotId
    private void queryActionIotIdByMac(String token, int pos) {
        ItemScene.Action action = mActionList.get(pos).getAction();
        if ("Command".equals(action.getType())) {
            // 指令
            String deviceId = action.getParameters().getDeviceId();
            if (deviceId == null || deviceId.length() == 0) return;
            // 动作
            EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(deviceId);
            if (entry != null) {
                String iotId = entry.iotId;
                mActionList.get(pos).setIotId(iotId);
                String pk = entry.productKey;
                mActionList.get(pos).setKeyNickName(refreshActionDesc(pk, mActionList.get(pos)));
            }
            if (pos < mActionList.size() - 1) {
                queryActionIotIdByMac(token, pos + 1);
            } else {
                mActionAdapter.notifyDataSetChanged();
                mViewBinding.addActionLayout.setVisibility(View.GONE);
                mViewBinding.actionRv.setVisibility(View.VISIBLE);
            }
        } else if ("Scene".equals(action.getType())) {
            // 场景
            if (pos < mActionList.size() - 1) {
                queryActionIotIdByMac(token, pos + 1);
            } else {
                ViseLog.d("mActionList ==== \n" + GsonUtil.toJson(mActionList));
                mActionAdapter.notifyDataSetChanged();
                mViewBinding.addActionLayout.setVisibility(View.GONE);
                mViewBinding.actionRv.setVisibility(View.VISIBLE);
            }
        }
    }

    // 获取条件描述前缀
    private String refreshConditionDesc(String pk, ECondition eCondition) {
        StringBuilder desc = new StringBuilder();
        if (CTSL.PK_ONE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_ONE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_TWO_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_THREE_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_FOUR_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SIX_SCENE_SWITCH.equals(pk) ||
                CTSL.PK_SYT_SIX_SCENE_SWITCH.equals(pk)) {
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
        } else if (CTSL.PK_MULTI_THREE_IN_ONE.equals(pk)) {
            // 三合一温控器
            refreshMulti3To1ConditionDesc(desc, eCondition);
        } else if (CTSL.PK_MULTI_AC_AND_FH.equals(pk)) {
            // 空调+地暖二合一温控器
            refreshMultiACAndFHConditionDesc(desc, eCondition);
        } else if (CTSL.PK_MULTI_AC_AND_FA.equals(pk)) {
            // 空调+新风二合一温控器
            refreshMultiACAndFAConditionDesc(desc, eCondition);
        } else if (CTSL.PK_MULTI_FH_AND_FA.equals(pk)) {
            // 地暖+新风二合一温控器
            refreshMultiFHAndFAConditionDesc(desc, eCondition);
        }
        return desc.toString();
    }

    // 获取地暖+新风二合一温控器条件描述前缀
    private void refreshMultiFHAndFAConditionDesc(StringBuilder desc, ECondition eCondition) {
        String endpointId = eCondition.getCondition().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 地暖
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_floorheat));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_floorheat));
            } else if ("AutoWorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.heating_status_floorheat));
            }
        } else if ("2".equals(endpointId)) {
            // 新风
            if ("FanMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_freshair));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_freshair));
            }
        }
    }

    // 获取空调+新风二合一温控器条件描述前缀
    private void refreshMultiACAndFAConditionDesc(StringBuilder desc, ECondition eCondition) {
        String endpointId = eCondition.getCondition().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_airconditioner));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 新风
            if ("FanMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_freshair));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_freshair));
            }
        }
    }

    // 获取空调+地暖二合一温控器条件描述前缀
    private void refreshMultiACAndFHConditionDesc(StringBuilder desc, ECondition eCondition) {
        String endpointId = eCondition.getCondition().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_airconditioner));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 地暖
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_floorheat));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_floorheat));
            } else if ("AutoWorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.heating_status_floorheat));
            }
        }
    }

    // 获取三合一温控器条件描述前缀
    private void refreshMulti3To1ConditionDesc(StringBuilder desc, ECondition eCondition) {
        String endpointId = eCondition.getCondition().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_airconditioner));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 地暖
            if ("WorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_floorheat));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_floorheat));
            } else if ("AutoWorkMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.heating_status_floorheat));
            }
        } else if ("3".equals(endpointId)) {
            // 新风
            if ("FanMode".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.power_switch_freshair));
            } else if ("Temperature".equals(eCondition.getCondition().getParameters().getName())) {
                desc.append(getString(R.string.current_temperature_freshair));
            }
        }
    }

    // 获取动作描述前缀
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
        } else if (CTSL.PK_MULTI_THREE_IN_ONE.equals(pk)) {
            // 三合一温控器
            refreshMulti3To1ActionDesc(desc, eAction);
        } else if (CTSL.PK_MULTI_AC_AND_FH.equals(pk)) {
            // 空调+地暖二合一温控器
            refreshMultiACAndFHActionDesc(desc, eAction);
        } else if (CTSL.PK_MULTI_AC_AND_FA.equals(pk)) {
            // 空调+新风二合一温控器
            refreshMultiACAndFAActionDesc(desc, eAction);
        } else if (CTSL.PK_MULTI_FH_AND_FA.equals(pk)) {
            // 地暖+新风二合一温控器
            refreshMultiFHAndFAActionDesc(desc, eAction);
        }
        return desc.toString();
    }

    // 地暖+新风二合一温控器
    private void refreshMultiFHAndFAActionDesc(StringBuilder desc, EAction eAction) {
        String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
        String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
        String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
        String endpointId = eAction.getAction().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 地暖
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_floorheat));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_floorheat));
            }
        } else if ("2".equals(endpointId)) {
            // 新风
            if (fanMode != null && fanMode.length() > 0) {
                if ("0".equals(fanMode) || "4".equals(fanMode)) {
                    desc.append(getString(R.string.power_switch_freshair));
                } else
                    desc.append(getString(R.string.fan_speed_freshair));
            }
        }
    }

    // 空调+新风二合一温控器
    private void refreshMultiACAndFAActionDesc(StringBuilder desc, EAction eAction) {
        String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
        String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
        String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
        String endpointId = eAction.getAction().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_airconditioner));
                else desc.append(getString(R.string.work_mode_airconditioner));
            } else if (fanMode != null && fanMode.length() > 0) {
                desc.append(getString(R.string.fan_speed_airconditioner));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 新风
            if (fanMode != null && fanMode.length() > 0) {
                if ("0".equals(fanMode) || "4".equals(fanMode)) {
                    desc.append(getString(R.string.power_switch_freshair));
                } else
                    desc.append(getString(R.string.fan_speed_freshair));
            }
        }
    }

    // 空调+地暖二合一温控器
    private void refreshMultiACAndFHActionDesc(StringBuilder desc, EAction eAction) {
        String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
        String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
        String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
        String endpointId = eAction.getAction().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_airconditioner));
                else desc.append(getString(R.string.work_mode_airconditioner));
            } else if (fanMode != null && fanMode.length() > 0) {
                desc.append(getString(R.string.fan_speed_airconditioner));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 地暖
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_floorheat));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_floorheat));
            }
        }
    }

    // 三合一温控器
    private void refreshMulti3To1ActionDesc(StringBuilder desc, EAction eAction) {
        String workMode = eAction.getAction().getParameters().getCommand().getString("WorkMode");
        String fanMode = eAction.getAction().getParameters().getCommand().getString("FanMode");
        String temperature = eAction.getAction().getParameters().getCommand().getString("Temperature");
        String endpointId = eAction.getAction().getParameters().getEndpointId();
        if ("1".equals(endpointId)) {
            // 空调
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_airconditioner));
                else desc.append(getString(R.string.work_mode_airconditioner));
            } else if (fanMode != null && fanMode.length() > 0) {
                desc.append(getString(R.string.fan_speed_airconditioner));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_airconditioner));
            }
        } else if ("2".equals(endpointId)) {
            // 地暖
            if (workMode != null && workMode.length() > 0) {
                if ("0".equals(workMode) || "10".equals(workMode))
                    desc.append(getString(R.string.power_switch_floorheat));
            } else if (temperature != null && temperature.length() > 0) {
                desc.append(getString(R.string.target_temperature_floorheat));
            }
        } else if ("3".equals(endpointId)) {
            // 新风
            if (fanMode != null && fanMode.length() > 0) {
                if ("0".equals(fanMode) || "4".equals(fanMode)) {
                    desc.append(getString(R.string.power_switch_freshair));
                } else
                    desc.append(getString(R.string.fan_speed_freshair));
            }
        }
    }
}