package com.xiezhu.jzj.view;

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

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.presenter.AptGatewayList;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.HomeSpaceManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.EHomeSpace;
import com.xiezhu.jzj.utility.Dialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 选择网关
 */
public class ChoiceGatewayActivity extends BaseActivity {
    private String mProductKey;
    private String mProductName;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private final int mPageSize = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_gateway);

        mProductKey = getIntent().getStringExtra("productKey");
        mProductName = getIntent().getStringExtra("productName");
        mDeviceList = new ArrayList<EDevice.deviceEntry>();

        TextView title = findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.choicegateway_title);

        // 回退处理
        ImageView imgAdd = findViewById(R.id.includeTitleImgBack);
        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取网关
        new HomeSpaceManager(this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, mPageSize,
                mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

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
        public boolean handleMessage(@NotNull Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_GETHOMEGATWAYLIST) {// 处理获取家网关列表数据
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
                        new HomeSpaceManager(ChoiceGatewayActivity.this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", list.pageNo + 1, mPageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else {
                        // 数据获取完则加载显示
                        ListView lstProduct = (ListView) findViewById(R.id.choiceGatewayLstGateway);
                        AptGatewayList adapter = new AptGatewayList(ChoiceGatewayActivity.this);
                        adapter.setData(mDeviceList);
                        lstProduct.setAdapter(adapter);
                        lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                            }
                        });
                    }
                }
            }
            return false;
        }
    });
}