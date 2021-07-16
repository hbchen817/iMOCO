package com.xiezhu.jzj.view;

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
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.ProductHelper;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EProduct;
import com.xiezhu.jzj.utility.Dialog;

import org.jetbrains.annotations.NotNull;

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
    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_GETGUIDANCEINFOMATION) {// 处理获取产品配网引导信息
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
            }

            return false;
        }
    });

    // 配网步骤引导
    public void guidance(int stepIndex) {
        if ((mGuidances == null || mGuidances.size() == 0) && !mProductKey.equals(CTSL.PK_GATEWAY_RG4100) && !mProductKey.equals(CTSL.PK_SIX_TWO_SCENE_SWITCH)) {
            return;
        }

        // 引导完作后的处理
        if (mCurrentStepIndex >= mStepCount || mProductKey.equals(CTSL.PK_GATEWAY_RG4100) || mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH)) {
            if (!mChbIsRead.isChecked()) {
                Dialog.confirm(this, R.string.dialog_title, getString(R.string.productguidance_hint), R.drawable.dialog_prompt, R.string.dialog_confirm, false);
                return;
            }

            if (mNodeType == Constant.DEVICETYPE_GATEWAY) {
                if (mProductKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                    ScanGatewayByNetActivity.start(this);
                } else {
                    // 选中的是网关则进入扫描蓝牙设备
                    Intent intent = new Intent(this, ScanBLEActivity.class);
                    intent.putExtra("productKey", mProductKey);
                    startActivity(intent);
                }
                finish();
            } else {
                // 选中的是子设备处理
                if (mGatewayNumber <= 0) {
                    // 如果没有网关则退出
                    Dialog.confirm(this, R.string.dialog_title, getString(R.string.choicegateway_nohasgatewayhint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
                } else {
                    if (mGatewayIOTId != null && mGatewayIOTId.length() > 0) {
                        // 如果网关已经选定则直接进入允许子设备入网
                        Intent intent = new Intent(this, PermitJoinActivity.class);
                        intent.putExtra("productKey", mProductKey);
                        intent.putExtra("productName", mProductName);
                        intent.putExtra("gatewayIOTId", mGatewayIOTId);
                        startActivity(intent);
                        finish();
                    } else {
                        // 如果网关没有选定先选择子设备所属的网关
                        Intent intent = new Intent(this, ChoiceGatewayActivity.class);
                        intent.putExtra("productKey", mProductKey);
                        intent.putExtra("productName", mProductName);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            return;
        }

        // 加载引导内容
        mGuidanceCopywriting.setText(mGuidances.get(stepIndex).dnCopywriting);
        mOperateCopywriting.setText(mGuidances.get(stepIndex).buttonCopywriting);
        if (mCurrentStepIndex == mStepCount - 1) {
            mChbIsRead.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(mGuidances.get(stepIndex).dnGuideIcon).into(mGuidanceIcon);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productguidance);

        Intent intent = getIntent();
        mProductKey = intent.getStringExtra("productKey");
        mProductName = intent.getStringExtra("productName");
        mNodeType = intent.getIntExtra("nodeType", 0);
        mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        mGatewayNumber = intent.getIntExtra("gatewayNumber", 0);

        Log.i("lzm", "pk" + mProductKey);
        TextView title = findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.productguidance_title);

        mGuidanceIcon = findViewById(R.id.productGuidanceImgIcon);
        mGuidanceIcon.setImageResource(ImageProvider.genProductIcon(mProductKey));
        mGuidanceCopywriting = findViewById(R.id.productGuidanceLblCopywriting);
        mChbIsRead = findViewById(R.id.productGuidanceChbRead);

        mOperateCopywriting = findViewById(R.id.productGuidanceLblOperate);
        mOperateIcon = findViewById(R.id.productGuidanceImgOperate);
        OnClickListener guidanceClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentStepIndex++;
                guidance(mCurrentStepIndex);
            }
        };
        mOperateCopywriting.setOnClickListener(guidanceClick);
        mOperateIcon.setOnClickListener(guidanceClick);

        ImageView back = findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mProductKey.length() > 1 && !mProductKey.equals(CTSL.PK_GATEWAY_RG4100) && !mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH)) {
            //获取产品配网引导信息
            new ProductHelper(this).getGuidanceInformation(mProductKey, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else if (mProductKey.length() > 1 && (mProductKey.equals(CTSL.PK_GATEWAY_RG4100) || mProductKey.equalsIgnoreCase(CTSL.PK_SIX_TWO_SCENE_SWITCH))) {
            //RG4100网关
            mGuidanceCopywriting.setText(R.string.gateway_guidance);
            mChbIsRead.setVisibility(View.VISIBLE);
            mOperateCopywriting.setText(R.string.dialog_confirm);
            Glide.with(this).load(R.drawable.icon_gateway_fton).into(mGuidanceIcon);
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
}