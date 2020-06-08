package com.rexense.imoco.view;

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
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.AptConfigProductList;
import com.rexense.imoco.presenter.AptSceneTrigger;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.utility.Dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-06 15:29
 * Description: 场景维护
 */
public class SceneMaintainActivity extends BaseActivity {
    private SceneManager mSceneManager;
    private int mOperateType, mSceneModelCode;
    private TextView mLblName;
    private List<EScene.triggerEntry> mTriggerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_maintain);

        Intent intent = getIntent();
        this.mOperateType = intent.getIntExtra("operateType", 1);
        this.mSceneModelCode = intent.getIntExtra("sceneModelCode", 1);

        this.mSceneManager = new SceneManager(this);
        TextView title = (TextView)findViewById(R.id.includeTitleLblTitle);
        title.setText(String.format("%s%s", getString(R.string.scene_maintain_create), intent.getStringExtra("sceneModelName")));
        this.mLblName = (TextView)findViewById(R.id.sceneMaintainLblName);
        this.mLblName.setText(intent.getStringExtra("sceneModelName"));
        ImageView icon = (ImageView)findViewById(R.id.sceneMaintainImgIcon);
        icon.setBackgroundResource(intent.getIntExtra("sceneModelIcon", 1));

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

        // 响应设备处理
        ListView lstResponse = (ListView)findViewById(R.id.sceneMaintainLstResponse);

        // 启用处理
        TextView lblUse = (TextView)findViewById(R.id.sceneMaintainLblUse);
        lblUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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

    // 数据处理器
    private Handler processDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    List<EProduct.configListEntry> mConfigProductList = CloudDataParser.processConfigProcductList((String)msg.obj);
                    // 触发器处理
                    ListView lstTrigger = (ListView)findViewById(R.id.sceneMaintainLstTrigger);
                    mTriggerList = mSceneManager.getTrigger(mSceneModelCode, mConfigProductList);
                    AptSceneTrigger aptSceneTrigger = new AptSceneTrigger(SceneMaintainActivity.this);
                    aptSceneTrigger.setData(mTriggerList);
                    lstTrigger.setAdapter(aptSceneTrigger);
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

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                default:
                    break;
            }
            return false;
        }
    });
}