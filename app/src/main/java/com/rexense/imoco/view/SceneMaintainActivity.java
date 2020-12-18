package com.rexense.imoco.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EChoice;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.AptSceneParameter;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.ToastUtils;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-06 15:29
 * Description: 场景维护
 */
public class SceneMaintainActivity extends BaseActivity {
    private SceneManager mSceneManager;
    private String mName, mSceneId;
    private int mOperateType, mSceneModelCode, mSceneNumber;
    private TextView mLblName, mLblEnable;
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
        nameEt.setText(this.mLblName.getText().toString());
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
                    mLblName.setText(nameEt.getText().toString());
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
        setContentView(R.layout.activity_scene_maintain);

        this.mSceneManager = new SceneManager(this);

        Intent intent = getIntent();
        this.mOperateType = intent.getIntExtra("operateType", 1);
        this.mSceneModelCode = intent.getIntExtra("sceneModelCode", 1);
        this.mSceneNumber = intent.getIntExtra("sceneNumber", 0);
        this.mName = intent.getStringExtra("name");
        this.mSceneId = intent.getStringExtra("sceneId");

        TextView title = (TextView) findViewById(R.id.includeTitleLblTitle);
        ImageView icon = (ImageView) findViewById(R.id.sceneMaintainImgIcon);
        this.mLblName = (TextView) findViewById(R.id.sceneMaintainLblName);
        this.mLblName.setMovementMethod(ScrollingMovementMethod.getInstance());
        this.mLblEnable = (TextView) findViewById(R.id.sceneMaintainLblEnable);
        icon.setImageResource(intent.getIntExtra("sceneModelIcon", 1));

        TextView lblOperate = (TextView) findViewById(R.id.sceneMaintainLblOperate);
        if (this.mOperateType == CScene.OPERATE_CREATE) {
            title.setText(String.format("%s%s", getString(R.string.scene_maintain_create), intent.getStringExtra("sceneModelName")));
            this.mLblName.setText(intent.getStringExtra("sceneModelName"));
            lblOperate.setText(getString(R.string.scene_maintain_create));
        } else {
            title.setText(String.format("%s%s", getString(R.string.scene_maintain_edit), this.mName));
            this.mLblName.setText(this.mName);
            lblOperate.setText(getString(R.string.scene_maintain_edit));
        }

        // 修改场景名称处理
        ImageView editName = (ImageView) findViewById(R.id.sceneMaintainImgName);
        editName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSceneNameDialogEdit();
            }
        });

        // 设置使用状态处理
        ImageView setEnable = (ImageView) findViewById(R.id.sceneMaintainImgEnable);
        setEnable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EChoice.itemEntry> items = new ArrayList<EChoice.itemEntry>();
                items.add(new EChoice.itemEntry(getString(R.string.scene_maintain_startusing), "1", mEnable));
                items.add(new EChoice.itemEntry(getString(R.string.scene_maintain_stopusing), "0", !mEnable ? true : false));
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
        RelativeLayout rlOperate = (RelativeLayout) findViewById(R.id.sceneMaintainRlOperate);
        rlOperate.setOnClickListener(new OnClickListener() {
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
                        mLblName.getText().toString(), mSceneManager.getSceneModelName(mSceneModelCode));
                baseInfoEntry.enable = mEnable;
                if (mOperateType == CScene.OPERATE_CREATE) {
                    // 创建场景
                    mSceneManager.create(baseInfoEntry, mParameterList, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                } else {
                    // 修改场景
                    baseInfoEntry.sceneId = mSceneId;
                    mSceneManager.update(baseInfoEntry, mParameterList, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                }
            }
        });

        // 返回处理
        ImageView back = (ImageView) findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        this.mParameterList = this.mSceneManager.genSceneModelParameterList(mSceneModelCode, mConfigProductList);
        mAptSceneParameter = new AptSceneParameter(SceneMaintainActivity.this);
        mAptSceneParameter.setData(this.mParameterList);
        ListView lstParameter = (ListView) findViewById(R.id.sceneMaintainLstParameter);
        lstParameter.setAdapter(mAptSceneParameter);

        // 列表点击事件处理
        lstParameter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    private Handler processDataHandler = new Handler(new Handler.Callback() {
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
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详细信息
                    EScene.processedDetailEntry detailEntry = CloudDataParser.processSceneDetailInformation((String) msg.obj);
                    Log.i("lzm", "Detail" + (String) msg.obj);
                    mEnable = detailEntry.rawDetail.isEnable();
                    if (mEnable) {
                        mLblEnable.setText(getString(R.string.scene_maintain_startusing));
                    } else {
                        mLblEnable.setText(getString(R.string.scene_maintain_stopusing));
                    }
                    // 初始化场景参数
                    mSceneManager.initSceneParameterList(mParameterList, detailEntry);
                    mAptSceneParameter.notifyDataSetChanged();
                    break;
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_create_success), mLblName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_create_failed), mLblName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_edit_success), mLblName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(SceneMaintainActivity.this, String.format(getString(R.string.scene_maintain_edit_failed), mLblName.getText().toString()));
                    }
                    finish();
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
                mLblEnable.setText(getString(R.string.scene_maintain_startusing));
            } else {
                mEnable = false;
                mLblEnable.setText(getString(R.string.scene_maintain_stopusing));
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