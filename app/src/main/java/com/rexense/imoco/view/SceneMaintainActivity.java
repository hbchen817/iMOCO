package com.rexense.imoco.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivitySceneMaintainBinding;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EChoice;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.AptSceneParameter;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.ToastUtils;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-06 15:29
 * Description: 场景维护
 */
public class SceneMaintainActivity extends BaseActivity {
    private ActivitySceneMaintainBinding mViewBinding;

    private SceneManager mSceneManager;
    private String mSceneId;
    private int mOperateType, mSceneModelCode, mSceneNumber;
    private List<EScene.parameterEntry> mParameterList;
    private AptSceneParameter mAptSceneParameter;
    private int mSetTimeIndex = -1;
    private boolean mEnable = true;
    private long mClickTime = 0;

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
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    mViewBinding.sceneMaintainLblName.setText(nameEt.getText().toString());
                }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneMaintainBinding.inflate(getLayoutInflater());
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
                Intent intent = new Intent(SceneMaintainActivity.this, ChoiceActivity.class);
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
                if (System.currentTimeMillis() - mClickTime < 3000) {
                    mClickTime = System.currentTimeMillis();
                    return;
                }
                // 检查参数
                if (!mSceneManager.checkParameter(mSceneNumber, mSceneModelCode, mParameterList)) {
                    return;
                }

                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        mSceneModelCode > CScene.SMC_AUTOMATIC_MAX ? CScene.TYPE_MANUAL : CScene.TYPE_AUTOMATIC,
                        mViewBinding.sceneMaintainLblName.getText().toString(), mSceneManager.getSceneModelName(mSceneModelCode));
                baseInfoEntry.enable = mEnable;
                QMUITipDialogUtil.showLoadingDialg(SceneMaintainActivity.this, R.string.is_uploading);
                if (mOperateType == CScene.OPERATE_CREATE) {
                    ViseLog.d(new Gson().toJson(mParameterList));
                    // 创建场景
                    switch (mSceneModelCode) {
                        case 1:// 起夜开灯
                        case 5:// 开门亮灯
                        case 6:// 门铃播报
                        case 7:// 报警播报
                        case 8:// 红外布防报警
                        case 9:// 门磁布防报警
                        case 10:// 回家模式
                        case 11:// 离家模式
                        case 12:// 睡觉模式
                        case 13:// 起床模式
                        case 2: {// 无人关灯
                            mSceneManager.createCAModel(baseInfoEntry, mParameterList, "all", mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            break;
                        }
                        case 3:// 报警开灯
                        case 4: {// 遥控开灯
                            mSceneManager.createCAModel(baseInfoEntry, mParameterList, "any", mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            break;
                        }
                    }
                    //mSceneManager.create(baseInfoEntry, mParameterList, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                } else {
                    // 修改场景
                    baseInfoEntry.sceneId = mSceneId;
                    //mSceneManager.update(baseInfoEntry, mParameterList, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                    switch (mSceneModelCode) {
                        case 1:// 起夜开灯
                        case 5:// 开门亮灯
                        case 6:// 门铃播报
                        case 7:// 报警播报
                        case 8:// 红外布防报警
                        case 9:// 门磁布防报警
                        case 10:// 回家模式
                        case 11:// 离家模式
                        case 12:// 睡觉模式
                        case 13:// 起床模式
                        case 2: {// 无人关灯
                            mSceneManager.updateCAModel(baseInfoEntry, mParameterList, "all", mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            break;
                        }
                        case 3:// 报警开灯
                        case 4: {// 遥控开灯
                            mSceneManager.updateCAModel(baseInfoEntry, mParameterList, "any", mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                            break;
                        }
                    }
                }
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
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        initStatusBar();
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
        mParameterList = mSceneManager.genSceneModelParameterList(mSceneModelCode, mConfigProductList);
        mAptSceneParameter = new AptSceneParameter(this);
        mAptSceneParameter.setData(mParameterList);
        mViewBinding.sceneMaintainLstParameter.setAdapter(mAptSceneParameter);

        // 列表点击事件处理
        mViewBinding.sceneMaintainLstParameter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSetTimeIndex = -1;
                // 设置时间
                if (mParameterList.get(position).type == CScene.SPT_CONDITION_TIME) {
                    mSetTimeIndex = position;
                    Intent intent = new Intent(SceneMaintainActivity.this, SetTimeActivity.class);
                    String cron = mParameterList.get(position).conditionTimeEntry.genCronString();
                    intent.putExtra("cron", cron);
                    startActivityForResult(intent, Constant.REQUESTCODE_CALLSETTIMEACTIVITY);
                }
            }
        });
    }

    // 数据处理器
    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    List<EProduct.configListEntry> mConfigProductList = CloudDataParser.processConfigProcductList((String) msg.obj);

                    // 生成场景参数
                    genSceneParameterList(mConfigProductList);
                    if (mOperateType == CScene.OPERATE_UPDATE) {
                        // 获取场景详细信息
                        Log.i("lzm", "mSceneModelCode =" + mSceneModelCode);
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
                    mAptSceneParameter.notifyDataSetChanged();
                    QMUITipDialogUtil.dismiss();
                    break;
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        //ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_create_success), mLblName.getText().toString()), 2000);
                        QMUITipDialogUtil.showSuccessDialog(SceneMaintainActivity.this,
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
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mViewBinding.sceneMaintainLblName.getText().toString()));
                    }
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        //ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mLblName.getText().toString()));
                        QMUITipDialogUtil.showSuccessDialog(SceneMaintainActivity.this,
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
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_edit_failed),
                                mViewBinding.sceneMaintainLblName.getText().toString()));
                    }
                    break;
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
                mAptSceneParameter.notifyDataSetChanged();
            }
        }
    }
}