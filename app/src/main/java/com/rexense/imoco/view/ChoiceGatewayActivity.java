package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.databinding.ActivityChoiceGatewayBinding;
import com.rexense.imoco.presenter.AptGatewayList;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.utility.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 选择网关
 */
public class ChoiceGatewayActivity extends BaseActivity {
    private ActivityChoiceGatewayBinding mViewBinding;

    private String mProductKey;
    private String mProductName;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private final int PAGE_SIZE = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceGatewayBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mProductKey = getIntent().getStringExtra("productKey");
        mProductName = getIntent().getStringExtra("productName");
        mDeviceList = new ArrayList<EDevice.deviceEntry>();

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.choicegateway_title);

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(v -> finish());

        // 获取网关
        new HomeSpaceManager(this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

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

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_GETHOMEGATWAYLIST) {
                // 处理获取家网关列表数据
                EHomeSpace.homeDeviceListEntry list = CloudDataParser.processHomeDeviceList((String) msg.obj);
                if (list == null || list.total == 0) {
                    Dialog.confirm(ChoiceGatewayActivity.this, R.string.dialog_title, getString(R.string.choicegateway_nohasgatewayhint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
                } else {
                    for (EHomeSpace.deviceEntry e : list.data) {
                        EDevice.deviceEntry entry = new EDevice.deviceEntry();
                        entry.iotId = e.iotId;
                        entry.nickName = e.nickName;
                        entry.productKey = e.productKey;
                        entry.status = e.status;
                        mDeviceList.add(entry);
                    }
                    if (list.data.size() >= list.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        new HomeSpaceManager(ChoiceGatewayActivity.this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", list.pageNo + 1, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else {
                        // 数据获取完则加载显示
                        AptGatewayList adapter = new AptGatewayList(ChoiceGatewayActivity.this);
                        adapter.setData(mDeviceList);
                        mViewBinding.choiceGatewayLstGateway.setAdapter(adapter);
                        mViewBinding.choiceGatewayLstGateway.setOnItemClickListener((parent, view, position, id) -> {
                            if (mDeviceList.get(position).status != Constant.CONNECTION_STATUS_ONLINE) {
                                Dialog.confirm(ChoiceGatewayActivity.this, R.string.dialog_title, getString(R.string.choicegateway_gateofflinehint), R.drawable.dialog_fail, R.string.dialog_confirm, false);
                                return;
                            }
                            // 进入允许子设备入网
                            Intent intent = new Intent(ChoiceGatewayActivity.this, PermitJoinActivity.class);
                            intent.putExtra("productKey", mProductKey);
                            intent.putExtra("productName", mProductName);
                            intent.putExtra("gatewayIOTId", mDeviceList.get(position).iotId);
                            startActivity(intent);
                        });
                    }
                }
            }
            return false;
        }
    });
}