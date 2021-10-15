package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.adapter.LocalSceneAdapter;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySceneMaintainBinding;
import com.laffey.smart.databinding.ActivitySceneModelBinding;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EChoice;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EProduct;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.AptSceneParameter;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.ProductHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-06 15:29
 * Description: 场景维护
 */
public class SceneModelActivity extends BaseActivity {
    private ActivitySceneModelBinding mViewBinding;

    private final int PAGE_SIZE = 10;

    private SceneManager mSceneManager;
    private String mSceneId;
    private int mOperateType, mSceneModelCode, mSceneNumber;
    private List<EScene.parameterEntry> mParameterList;
    private LocalSceneAdapter mSceneAdapter;
    private int mSetTimeIndex = -1;
    private boolean mEnable = true;
    private long mClickTime = 0;

    private final List<EDevice.deviceEntry> mGatewayDevList = new ArrayList<>();
    private UserCenter mUserCenter;
    private EDevice.deviceEntry mGatewayEntry;

    // 显示场景名称修改对话框
    private void showSceneNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.scene_maintain_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(mViewBinding.sceneMaintainLblName.getText().toString());
        final android.app.Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
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
                String nameStr = nameEt.getText().toString().trim();
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    mViewBinding.sceneMaintainLblName.setText(nameEt.getText().toString());
                }
            }
        });
        cancelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneModelBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mSceneManager = new SceneManager(this);

        Intent intent = getIntent();
        mOperateType = intent.getIntExtra("operateType", 1);
        mSceneModelCode = intent.getIntExtra("sceneModelCode", 1);
        mSceneNumber = intent.getIntExtra("sceneNumber", 0);
        String name = intent.getStringExtra("name");
        mSceneId = intent.getStringExtra("sceneId");

        mViewBinding.sceneMaintainLblName.setMovementMethod(ScrollingMovementMethod.getInstance());
        mViewBinding.sceneMaintainImgIcon.setImageResource(intent.getIntExtra("sceneModelIcon", 1));

        if (mOperateType == CScene.OPERATE_CREATE) {
            mViewBinding.includeToolbar.includeTitleLblTitle.setText(String.format("%s%s", getString(R.string.scene_maintain_create), intent.getStringExtra("sceneModelName")));
            mViewBinding.sceneMaintainLblName.setText(intent.getStringExtra("sceneModelName"));
            mViewBinding.sceneMaintainLblOperate.setText(getString(R.string.scene_maintain_create));
        } else {
            mViewBinding.includeToolbar.includeTitleLblTitle.setText(String.format("%s%s", getString(R.string.scene_maintain_edit), name));
            mViewBinding.sceneMaintainLblName.setText(name);
            mViewBinding.sceneMaintainLblOperate.setText(getString(R.string.scene_maintain_edit));
        }

        // 修改场景名称处理
        mViewBinding.sceneMaintainImgName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSceneNameDialogEdit();
            }
        });

        // 设置使用状态处理
        mViewBinding.sceneMaintainImgEnable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EChoice.itemEntry> items = new ArrayList<EChoice.itemEntry>();
                items.add(new EChoice.itemEntry(getString(R.string.scene_maintain_startusing), "1", mEnable));
                items.add(new EChoice.itemEntry(getString(R.string.scene_maintain_stopusing), "0", !mEnable));
                Intent intent = new Intent(SceneModelActivity.this, ChoiceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.scene_maintain_setenable));
                bundle.putBoolean("isMultipleSelect", false);
                bundle.putInt("resultCode", Constant.RESULTCODE_CALLCHOICEACTIVITY_ENABLE);
                bundle.putSerializable("items", (Serializable) items);
                intent.putExtras(bundle);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLCHOICEACTIVITY);
            }
        });

        // 操作处理
        mViewBinding.sceneMaintainRlOperate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createScene();
            }
        });

        // 返回处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        // 获取支持配网产品列表
        // new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        mUserCenter = new UserCenter(this);
        mGatewayDevList.addAll(DeviceBuffer.getGatewayDevs());
        mGatewayEntry = mGatewayDevList.get(0);
        if (mGatewayDevList.size() == 1) {
            mViewBinding.gatewayIv.setVisibility(View.GONE);
        } else mViewBinding.gatewayIv.setVisibility(View.VISIBLE);
        mViewBinding.gatewayTv.setText(mGatewayEntry.nickName);
        mViewBinding.gatewayTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGatewayDevList.size() > 1) {
                    QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(SceneModelActivity.this);
                    for (EDevice.deviceEntry entry : mGatewayDevList) {
                        builder.addItem(entry.nickName);
                    }
                    builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            EDevice.deviceEntry entry = mGatewayDevList.get(position);
                            mViewBinding.gatewayTv.setText(entry.nickName);
                            mGatewayEntry = mGatewayDevList.get(position);
                            dialog.dismiss();
                        }
                    });
                    builder.build().show();
                }
            }
        });
        mUserCenter.getGatewaySubdeviceList(mGatewayEntry.iotId, 1, PAGE_SIZE, Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST,
                mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        initStatusBar();
    }

    // 创建场景
    private void createScene() {
        if (System.currentTimeMillis() - mClickTime < 3000) {
            mClickTime = System.currentTimeMillis();
            return;
        }
        ViseLog.d(GsonUtil.toJson(mParameterList));
        ViseLog.d(GsonUtil.toJson(createItemScene(mParameterList, mSceneModelCode)));
        // 检查参数
        if (!mSceneManager.checkParameter(mSceneNumber, mSceneModelCode, mParameterList)) {
            return;
        }
        ItemSceneInGateway scene = new ItemSceneInGateway();
        scene.setGwMac(mGatewayEntry.mac);
        scene.setSceneDetail(createItemScene(mParameterList, mSceneModelCode));

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
        RetrofitUtil.getInstance().addScene("chengxunfei", scene)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        boolean result = response.getBoolean("result");
                        String sceneId = response.getString("sceneId");
                        if (code == 200) {
                            if (result) {
                                mSceneManager.manageSceneService(mGatewayEntry.iotId, sceneId, 1, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                                setResult(10001);
                                finish();
                            } else {
                                if (msg == null || msg.length() == 0) {
                                    ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                                } else
                                    ToastUtils.showLongToast(mActivity, msg);
                            }
                        } else {
                            if (msg == null || msg.length() == 0) {
                                ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(mActivity, msg);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 将信息整理生成ItemScene对象
    private ItemScene createItemScene(List<EScene.parameterEntry> list, int modelCode) {
        ItemScene scene = new ItemScene();
        switch (modelCode) {
            case 1:// 起夜开灯
            case 2:// 无人关灯
            case 3:// 报警开灯
            case 4:// 遥控开灯
            case 5:// 开门亮灯
                scene.setType("0");// 自动场景
                break;
            case 10:// 回家模式
            case 11:// 离家模式
            case 12:// 睡觉模式
                scene.setType("1");// 手动场景
                break;
        }
        scene.setName(mViewBinding.sceneMaintainLblName.getText().toString());
        scene.setMac(mGatewayEntry.mac);
        scene.setEnable(mEnable ? "1" : "0");
        scene.setConditionMode("Any");
        List<ItemScene.Condition> conditionList = new ArrayList<>();
        ItemScene.Timer timer = new ItemScene.Timer();
        List<ItemScene.Action> actionList = new ArrayList<>();
        for (EScene.parameterEntry parameter : list) {
            if (parameter.type == CScene.SPT_TRIGGER && parameter.triggerEntry != null && parameter.triggerEntry.isSelected) {
                ItemScene.Condition condition = new ItemScene.Condition();
                condition.setType("State");
                ItemScene.ConditionParameter conditionParameter = new ItemScene.ConditionParameter();
                switch (parameter.triggerEntry.productKey) {
                    case CTSL.PK_GASSENSOR:// 燃气感应器
                    case CTSL.PK_WATERSENSOR:// 水浸传感器
                    case CTSL.PK_SMOKESENSOR:// 烟雾传感器
                    case CTSL.PK_PIRSENSOR: {
                        // 人体红外感应器
                        conditionParameter.setCompareType("==");
                        conditionParameter.setCompareValue(parameter.triggerEntry.state.rawValue);
                        conditionParameter.setDeviceId(DeviceBuffer.getDeviceMac(parameter.triggerEntry.iotId));
                        conditionParameter.setEndpointId("1");
                        conditionParameter.setName("Alarm");
                        break;
                    }
                }
                condition.setParameters(conditionParameter);
                conditionList.add(condition);
            } else if (parameter.type == CScene.SPT_CONDITION_TIME && parameter.conditionTimeEntry != null && parameter.conditionTimeEntry.isSelected) {
                timer.setType("TimeRange");
                StringBuilder cron = new StringBuilder();
                cron.append(parameter.conditionTimeEntry.beginMinute);
                cron.append("-");
                cron.append(parameter.conditionTimeEntry.endMinute);

                cron.append(" ");
                cron.append(parameter.conditionTimeEntry.beginHour);
                cron.append("-");
                cron.append(parameter.conditionTimeEntry.endHour);

                cron.append(" * * ");

                List<Integer> repeats = parameter.conditionTimeEntry.repeat;
                for (int i = 0; i < repeats.size(); i++) {
                    switch (repeats.get(i)) {
                        case 1: {
                            cron.append("MON");
                            break;
                        }
                        case 2: {
                            cron.append("TUE");
                            break;
                        }
                        case 3: {
                            cron.append("WED");
                            break;
                        }
                        case 4: {
                            cron.append("THU");
                            break;
                        }
                        case 5: {
                            cron.append("FRI");
                            break;
                        }
                        case 6: {
                            cron.append("SAT");
                            break;
                        }
                        case 7: {
                            cron.append("SUN");
                            break;
                        }
                    }
                    if (i < repeats.size() - 1) {
                        cron.append(",");
                    }
                }
                timer.setCron(cron.toString());
            } else if (parameter.type == CScene.SPT_RESPONSE && parameter.responseEntry != null && !parameter.responseEntry.isSelected) {
                // 响应
                ItemScene.Action action = new ItemScene.Action();
                action.setType("Command");
                switch (parameter.responseEntry.productKey) {
                    case CTSL.PK_ONEWAYSWITCH: {
                        // 一键面板
                        ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                        actionParameter.setEndpointId("1");
                        actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(parameter.responseEntry.iotId/*"5ucMXkpNYvGNudGYX4mK000000"*/));
                        actionParameter.setCommandType("0106");

                        JSONObject command = new JSONObject();
                        command.put("State", parameter.responseEntry.state.rawValue);
                        actionParameter.setCommand(command);
                        action.setParameters(actionParameter);
                        break;
                    }
                    case CTSL.PK_TWOWAYSWITCH: {
                        // 二键面板
                        String endId = "1";
                        if ("PowerSwitch_1".equals(parameter.responseEntry.state.rawName)) {
                            endId = "1";
                        } else if ("PowerSwitch_2".equals(parameter.responseEntry.state.rawName)) {
                            endId = "2";
                        }

                        ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                        actionParameter.setEndpointId(endId);
                        actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(parameter.responseEntry.iotId/*"qH55k2VLd6dGShi6RZSf000000"*/));
                        actionParameter.setCommandType("0106");

                        JSONObject command = new JSONObject();
                        command.put("State", parameter.responseEntry.state.rawValue);
                        actionParameter.setCommand(command);
                        action.setParameters(actionParameter);
                        break;
                    }
                    case CTSL.PK_THREE_KEY_SWITCH: {
                        // 三键面板
                        String endId = "1";
                        if ("PowerSwitch_1".equals(parameter.responseEntry.state.rawName)) {
                            endId = "1";
                        } else if ("PowerSwitch_2".equals(parameter.responseEntry.state.rawName)) {
                            endId = "2";
                        } else if ("PowerSwitch_3".equals(parameter.responseEntry.state.rawName)) {
                            endId = "3";
                        }

                        ItemScene.ActionParameter actionParameter = new ItemScene.ActionParameter();
                        actionParameter.setEndpointId(endId);
                        actionParameter.setDeviceId(DeviceBuffer.getDeviceMac(parameter.responseEntry.iotId));
                        actionParameter.setCommandType("0106");

                        JSONObject command = new JSONObject();
                        command.put("State", parameter.responseEntry.state.rawValue);
                        actionParameter.setCommand(command);
                        action.setParameters(actionParameter);
                        break;
                    }
                }
                actionList.add(action);
            }
        }
        scene.setConditions(conditionList);
        scene.setActions(actionList);
        scene.setTime(timer);
        return scene;
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    // 生成场景参数列表
    private void genSceneParameterList(List<EProduct.configListEntry> mConfigProductList) {
        if (!"com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
            mParameterList = mSceneManager.genSceneModelParameterList(mSceneModelCode, mConfigProductList);
        } else {
            mParameterList = mSceneManager.genSceneModelParameterList(mSceneModelCode, mConfigProductList, mGatewayEntry.iotId);
            ViseLog.d("生成场景参数列表 = " + GsonUtil.toJson(mParameterList));
            refreshModelList();
        }

        mSceneAdapter = new LocalSceneAdapter(this);
        mSceneAdapter.setData(mParameterList);
        mViewBinding.sceneMaintainLstParameter.setAdapter(mSceneAdapter);

        // 列表点击事件处理
        mViewBinding.sceneMaintainLstParameter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSetTimeIndex = -1;
                // 设置时间
                if (mParameterList.get(position).type == CScene.SPT_CONDITION_TIME) {
                    mSetTimeIndex = position;
                    Intent intent = new Intent(SceneModelActivity.this, SetTimeActivity.class);
                    String cron = mParameterList.get(position).conditionTimeEntry.genCronString();
                    intent.putExtra("cron", cron);
                    startActivityForResult(intent, Constant.REQUESTCODE_CALLSETTIMEACTIVITY);
                }
            }
        });
    }

    private void refreshModelList() {
        switch (mSceneModelCode) {
            case CScene.SMC_NIGHT_RISE_ON: {
                // 起夜开灯
                List<EDevice.deviceEntry> list = DeviceBuffer.getDevByPK(CTSL.PK_PIRSENSOR);
                //if ()
                break;
            }
        }
    }

    // 数据处理器
    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    List<EProduct.configListEntry> mConfigProductList = CloudDataParser.processConfigProcductList((String) msg.obj);
                    ViseLog.d("处理获取支持配网产品列表数据 = " + GsonUtil.toJson(mConfigProductList));

                    // 生成场景参数
                    genSceneParameterList(mConfigProductList);
                    if (mOperateType == CScene.OPERATE_UPDATE) {
                        // 获取场景详细信息
                        ViseLog.d("mSceneModelCode = " + mSceneModelCode);
                        mSceneManager.querySceneDetail(mSceneId, mSceneModelCode > CScene.SMC_AUTOMATIC_MAX ? CScene.TYPE_MANUAL : CScene.TYPE_AUTOMATIC, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                    } else {
                        QMUITipDialogUtil.dismiss();
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详细信息
                    EScene.processedDetailEntry detailEntry = CloudDataParser.processSceneDetailInformation((String) msg.obj);
                    ViseLog.d((String) msg.obj);
                    ViseLog.d(new Gson().toJson(detailEntry));
                    ViseLog.d(new Gson().toJson(mParameterList));
                    Log.i("lzm", "Detail" + (String) msg.obj);
                    mEnable = detailEntry.rawDetail.isEnable();
                    if (mEnable) {
                        mViewBinding.sceneMaintainLblEnable.setText(getString(R.string.scene_maintain_startusing));
                    } else {
                        mViewBinding.sceneMaintainLblEnable.setText(getString(R.string.scene_maintain_stopusing));
                    }
                    // 初始化场景参数
                    mSceneManager.initSceneParameterList(mParameterList, detailEntry);
                    mSceneAdapter.notifyDataSetChanged();
                    QMUITipDialogUtil.dismiss();
                    break;
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        //ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_create_success), mLblName.getText().toString()), 2000);
                        QMUITipDialogUtil.showSuccessDialog(SceneModelActivity.this,
                                String.format(getString(R.string.scene_maintain_create_success), mViewBinding.sceneMaintainLblName.getText().toString()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 发送刷新列表数据事件
                                QMUITipDialogUtil.dismiss();
                                RefreshData.refreshSceneListData();
                                finish();
                            }
                        }, 1000);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showToastCentrally(SceneModelActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mViewBinding.sceneMaintainLblName.getText().toString()));
                    }
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE: {
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        //ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mLblName.getText().toString()));
                        QMUITipDialogUtil.showSuccessDialog(SceneModelActivity.this,
                                String.format(getString(R.string.scene_maintain_edit_success), mViewBinding.sceneMaintainLblName.getText().toString()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                QMUITipDialogUtil.dismiss();
                                // 发送刷新列表数据事件
                                RefreshData.refreshSceneListData();
                                finish();
                            }
                        }, 1000);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showToastCentrally(SceneModelActivity.this, String.format(getString(R.string.scene_maintain_edit_failed),
                                mViewBinding.sceneMaintainLblName.getText().toString()));
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST: {
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    ViseLog.d("网关子设备列表 = " + GsonUtil.toJson(list) + "\n网关 = " + GsonUtil.toJson(mGatewayEntry));
                    if (list != null && list.data != null) {
                        for (EUser.deviceEntry e : list.data) {
                            DeviceBuffer.setGatewayId(e.iotId, mGatewayEntry.iotId);
                        }

                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getGatewaySubdeviceList(mGatewayEntry.iotId, list.pageNo + 1, PAGE_SIZE,
                                    mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                        } else {
                            // 数据获取完后
                            ViseLog.d(GsonUtil.toJson(DeviceBuffer.getAllDeviceInformation()));
                            new ProductHelper(SceneModelActivity.this).getConfigureList(Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                        }
                    }
                    break;
                }
                default:
                    break;
            }


            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理设置使用状态结果
        if (requestCode == Constant.REQUESTCODE_CALLCHOICEACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICEACTIVITY_ENABLE) {
            Bundle bundle = data.getExtras();
            String value = bundle.getString("value");
            if (value.equalsIgnoreCase("1")) {
                mEnable = true;
                mViewBinding.sceneMaintainLblEnable.setText(getString(R.string.scene_maintain_startusing));
            } else {
                mEnable = false;
                mViewBinding.sceneMaintainLblEnable.setText(getString(R.string.scene_maintain_stopusing));
            }
        }

        // 处理设置时间结果
        if (requestCode == Constant.REQUESTCODE_CALLSETTIMEACTIVITY && resultCode == Constant.RESULTCODE_CALLSETTIMEACTIVITY) {
            Bundle bundle = data.getExtras();
            EScene.conditionTimeEntry conditionTime = new EScene.conditionTimeEntry(bundle.getString("cron"));
            if (mSetTimeIndex >= 0) {
                mParameterList.get(mSetTimeIndex).conditionTimeEntry = conditionTime;
                mParameterList.get(mSetTimeIndex).conditionTimeEntry.isSelected = true;
                mSceneAdapter.notifyDataSetChanged();
            }
        }
    }
}