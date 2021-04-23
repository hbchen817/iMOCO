package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.utility.Dialog;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 产品配网引导
 */
public class ProductGuidanceActivity extends BaseActivity {
    private String mProductKey = "";
    private String mProductName = "";
    private int mNodeType;
    private String mGatewayIOTId = "";
    private int mGatewayNumber = 0;
    private List<EProduct.configGuidanceEntry> mGuidances;
    private ImageView mGuidanceIcon;
    private TextView mGuidanceCopywriting;
    private TextView mOperateCopywriting;
    private ImageView mOperateIcon;
    private CheckBox mChbIsRead;
    private int mStepCount, mCurrentStepIndex;
    private String[] mIgnoreList = {};

    // 数据处理器
    private Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETGUIDANCEINFOMATION:
                    // 处理获取产品配网引导信息
                    mGuidances = CloudDataParser.processConfigGuidanceInformation((String) msg.obj);
                    if (mGuidances != null) {
                        // 按照id进行升序排序
                        Collections.sort(mGuidances, new Comparator<EProduct.configGuidanceEntry>() {
                            @Override
                            public int compare(EProduct.configGuidanceEntry o1, EProduct.configGuidanceEntry o2) {
                                if (o1.id > o2.id) {
                                    return 1;
                                } else if (o1.id == o2.id) {
                                    return 0;
                                }
                                return -1;
                            }
                        });

                        mStepCount = 0;
                        if (mGuidances != null && mGuidances.size() > 0) {
                            mStepCount = mGuidances.size();
                            mCurrentStepIndex = 0;
                            guidance(mCurrentStepIndex);
                        }
                    }
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    // 配网步骤引导
    public void guidance(int stepIndex) {
        if ((this.mGuidances == null || this.mGuidances.size() == 0) && !mProductKey.equals(CTSL.PK_GATEWAY_RG4100)/*&& !mProductKey.equals(CTSL.PK_SIX_TWO_SCENE_SWITCH)*/) {
            return;
        }

        // 引导完作后的处理
        if (this.mCurrentStepIndex >= this.mStepCount || mProductKey.equals(CTSL.PK_GATEWAY_RG4100) /*|| mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH)*/) {
            if (!mChbIsRead.isChecked()) {
                Dialog.confirm(ProductGuidanceActivity.this, R.string.dialog_title, getString(R.string.productguidance_hint), R.drawable.dialog_prompt, R.string.dialog_confirm, false);
                return;
            }

            if (this.mNodeType == Constant.DEVICETYPE_GATEWAY) {
                if (mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                    ScanGatewayByNetActivity.start(ProductGuidanceActivity.this);
                } else {
                    // 选中的是网关则进入扫描蓝牙设备
                    Intent intent = new Intent(ProductGuidanceActivity.this, ScanBLEActivity.class);
                    intent.putExtra("productKey", mProductKey);
                    startActivity(intent);
                }
                finish();
            } else {
                // 选中的是子设备处理
                if (this.mGatewayNumber <= 0) {
                    // 如果没有网关则退出
                    Dialog.confirm(ProductGuidanceActivity.this, R.string.dialog_title, getString(R.string.choicegateway_nohasgatewayhint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
                } else {
                    if (this.mGatewayIOTId != null && this.mGatewayIOTId.length() > 0) {
                        // 如果网关已经选定则直接进入允许子设备入网
                        Intent intent = new Intent(ProductGuidanceActivity.this, PermitJoinActivity.class);
                        intent.putExtra("productKey", this.mProductKey);
                        intent.putExtra("productName", this.mProductName);
                        intent.putExtra("gatewayIOTId", this.mGatewayIOTId);
                        startActivity(intent);
                        finish();
                    } else {
                        // 如果网关没有选定先选择子设备所属的网关
                        Intent intent = new Intent(ProductGuidanceActivity.this, ChoiceGatewayActivity.class);
                        intent.putExtra("productKey", this.mProductKey);
                        intent.putExtra("productName", this.mProductName);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            return;
        }

        if (!mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
            // 加载引导内容
            mGuidanceCopywriting.setText(mGuidances.get(stepIndex).dnCopywriting);
            mOperateCopywriting.setText(mGuidances.get(stepIndex).buttonCopywriting);
            if (mCurrentStepIndex == mStepCount - 1) {
                mChbIsRead.setVisibility(View.VISIBLE);
            }
            if (!isDestroyed())
                Glide.with(this).load(mGuidances.get(stepIndex).dnGuideIcon).transition(new DrawableTransitionOptions().crossFade()).into(mGuidanceIcon);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productguidance);

        Intent intent = getIntent();
        this.mProductKey = intent.getStringExtra("productKey");
        this.mProductName = intent.getStringExtra("productName");
        this.mNodeType = intent.getIntExtra("nodeType", 0);
        this.mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        this.mGatewayNumber = intent.getIntExtra("gatewayNumber", 0);

        Log.i("lzm", "pk" + mProductKey);
        TextView title = (TextView) findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.productguidance_title);

        this.mGuidanceIcon = (ImageView) findViewById(R.id.productGuidanceImgIcon);
        this.mGuidanceIcon.setImageResource(R.drawable.transparent_bg);
        this.mGuidanceCopywriting = (TextView) findViewById(R.id.productGuidanceLblCopywriting);
        this.mChbIsRead = (CheckBox) findViewById(R.id.productGuidanceChbRead);

        this.mOperateCopywriting = (TextView) findViewById(R.id.productGuidanceLblOperate);
        this.mOperateIcon = (ImageView) findViewById(R.id.productGuidanceImgOperate);
        OnClickListener guidanceClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentStepIndex++;
                guidance(mCurrentStepIndex);
            }
        };
        this.mOperateCopywriting.setOnClickListener(guidanceClick);
        this.mOperateIcon.setOnClickListener(guidanceClick);

        ImageView back = (ImageView) findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (this.mProductKey.length() > 1 && !mProductKey.equals(CTSL.PK_GATEWAY_RG4100)/*&&!mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH)*/) {
            mChbIsRead.setVisibility(View.VISIBLE);
            mOperateCopywriting.setText(R.string.dialog_confirm);
            //获取产品配网引导信息
            new ProductHelper(this).getGuidanceInformation(this.mProductKey, this.mCommitFailureHandler, this.mResponseErrorHandler, this.processDataHandler);
        } else if (this.mProductKey.length() > 1 && (mProductKey.equals(CTSL.PK_GATEWAY_RG4100)/*||mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH)*/)) {
            //RG4100网关
            mGuidanceCopywriting.setText(R.string.gateway_guidance);
            mChbIsRead.setVisibility(View.VISIBLE);
            mOperateCopywriting.setText(R.string.dialog_confirm);
            if (!isDestroyed())
                Glide.with(this).load(R.drawable.icon_gateway_fton)
                        .transition(new DrawableTransitionOptions().crossFade()).into(this.mGuidanceIcon);
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //.with(getApplicationContext()).pauseRequests();
    }
}