package com.rexense.imoco.view;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshData;
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
    private TextView mLblName;
    private List<EScene.parameterEntry> mParameterList;
    private AptSceneParameter mAptSceneParameter;
    private int mSetTimeIndex = -1;

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

        TextView title = (TextView)findViewById(R.id.includeTitleLblTitle);
        TextView operate = (TextView)findViewById(R.id.sceneMaintainLblOperate);
        ImageView icon = (ImageView)findViewById(R.id.sceneMaintainImgIcon);
        this.mLblName = (TextView)findViewById(R.id.sceneMaintainLblName);
        icon.setBackgroundResource(intent.getIntExtra("sceneModelIcon", 1));

        if(this.mOperateType == CScene.OPERATE_CREATE){
            title.setText(String.format("%s%s", getString(R.string.scene_maintain_create), intent.getStringExtra("sceneModelName")));
            this.mLblName.setText(intent.getStringExtra("sceneModelName"));
            operate.setText(getString(R.string.scene_maintain_create));
        } else {
            title.setText(String.format("%s%s", getString(R.string.scene_maintain_edit), this.mName));
            this.mLblName.setText(this.mName);
            operate.setText(getString(R.string.scene_maintain_edit));
            // 获取场景详细信息
            this.mSceneManager.querySceneDetail(this.mSceneId, CScene.TYPE_MANUAL, this.mCommitFailureHandler, this.mResponseErrorHandler, this.processDataHandler);
        }

        // 修改场景名称处理
        ImageView editName = (ImageView) findViewById(R.id.sceneMaintainImgName);
        editName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mSceneName = new EditText(SceneMaintainActivity.this);
                mSceneName.setText(mLblName.getText().toString());
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(SceneMaintainActivity.this);
                mDialogBuilder.setTitle(R.string.moredevice_namehint);
                mDialogBuilder.setIcon(R.drawable.dialog_prompt);
                mDialogBuilder.setView(mSceneName);
                mDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLblName.setText(mSceneName.getText().toString());
                    }
                });
                mDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mDialogBuilder.setCancelable(true);
                AlertDialog dialog = mDialogBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        // 操作处理
        RelativeLayout rlOperate = (RelativeLayout)findViewById(R.id.sceneMaintainRlOperate);
        rlOperate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查参数
                if(!mSceneManager.checkParameter(mSceneNumber, mSceneModelCode, mParameterList)){
                    return;
                }

                if(mOperateType == CScene.OPERATE_CREATE){
                    // 创建场景
                    EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                            mSceneModelCode >= CScene.SMC_GO_HOME_PATTERN ? CScene.TYPE_MANUAL : CScene.TYPE_AUTOMATIC,
                            mLblName.getText().toString(), mSceneManager.getSceneDescription(mSceneModelCode));
                    mSceneManager.create(baseInfoEntry, mParameterList, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                } else {
                    // 修改场景

                }
            }
        });

        // 返回处理
        ImageView back = (ImageView)findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取支持配网产品列表
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
    }

    // 生成场景参数列表
    private void genSceneParameterList(List<EProduct.configListEntry> mConfigProductList){
        this.mParameterList = this.mSceneManager.genSceneModelParameterList(mSceneModelCode, mConfigProductList);
        mAptSceneParameter = new AptSceneParameter(SceneMaintainActivity.this);
        mAptSceneParameter.setData(this.mParameterList);
        ListView lstParameter = (ListView)findViewById(R.id.sceneMaintainLstParameter);
        lstParameter.setAdapter(mAptSceneParameter);

        // 列表点击事件处理
        lstParameter.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSetTimeIndex = -1;
                // 设置时间
                if(mParameterList.get(position).type == CScene.SPT_CONDITION_TIME){
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
    private Handler processDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    List<EProduct.configListEntry> mConfigProductList = CloudDataParser.processConfigProcductList((String)msg.obj);
                    // 生成场景参数
                    genSceneParameterList(mConfigProductList);
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详细信息
                    Logger.d("Scene detail information is : " + msg.obj.toString());
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
                case Constant.MSG_CALLBACK_EDITSCENE:
                    // 处理修改场景结果
                    String sceneId_edit = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_edit != null && sceneId_edit.length() > 0) {
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
        // 处理设置时间结果
        if(requestCode == Constant.REQUESTCODE_CALLSETTIMEACTIVITY && resultCode == Constant.RESULTCODE_CALLSETTIMEACTIVITY){
            Bundle bundle = data.getExtras();
            EScene.conditionTimeEntry conditionTime = new EScene.conditionTimeEntry(bundle.getString("cron"));
            if(mSetTimeIndex >= 0){
                mParameterList.get(mSetTimeIndex).conditionTimeEntry = conditionTime;
                mAptSceneParameter.notifyDataSetChanged();
            }
        }
    }
}