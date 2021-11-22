package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.databinding.ActivityChoiceGatewayBinding;
import com.laffey.smart.presenter.AptGatewayList;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.utility.Dialog;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

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
                    DialogUtils.showConfirmDialog(ChoiceGatewayActivity.this, R.string.dialog_title,
                            R.string.choicegateway_nohasgatewayhint, R.string.dialog_confirm,
                            new DialogUtils.Callback() {
                                @Override
                                public void positive() {
                                    ChoiceGatewayActivity.this.finish();
                                }

                                @Override
                                public void negative() {

                                }
                            });
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
                                DialogUtils.showConfirmDialog(ChoiceGatewayActivity.this, R.string.dialog_title, R.string.choicegateway_gateofflinehint,
                                        R.string.dialog_confirm, new DialogUtils.Callback() {
                                            @Override
                                            public void positive() {

                                            }

                                            @Override
                                            public void negative() {

                                            }
                                        });
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