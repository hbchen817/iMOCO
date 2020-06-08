package com.rexense.imoco.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.presenter.AptConfigProductList;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.Dialog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 支持配网产品
 */
public class ChoiceProductActivity extends BaseActivity {
    private List<EProduct.configListEntry> mConfigProductList = null;
    private String mGatewayIOTId = "";
    private int mGatewayStatus = 0;
    private int mGatewayNumber = 0;

    // 数据处理器
    private Handler processDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    mConfigProductList = CloudDataParser.processConfigProcductList((String)msg.obj);

                    // 按照节点类型降速排序(即网关排在最前)
                    if(mConfigProductList != null) {
                        Collections.sort(mConfigProductList, new Comparator<EProduct.configListEntry>() {
                            @Override
                            public int compare(EProduct.configListEntry o1, EProduct.configListEntry o2) {
                                if(o1.nodeType > o2.nodeType) {
                                    return -1;
                                } else if(o1.nodeType == o2.nodeType) {
                                    return 0;
                                }
                                return 1;
                            }
                        });
                    }

                    if(mConfigProductList != null) {
                        // 如果不包含网关
                        if(mGatewayIOTId != null && mGatewayIOTId.length() > 0) {
                            int count = mConfigProductList.size() - 1;
                            for(int i = count; i >= 0; i--) {
                                if(mConfigProductList.get(i).nodeType == Constant.DEVICETYPE_GATEWAY) {
                                    mConfigProductList.remove(i);
                                }
                            }
                        }
                        ListView lstProduct = (ListView)findViewById(R.id.choiceProductLstProduct);
                        AptConfigProductList adapter = new AptConfigProductList(ChoiceProductActivity.this, mConfigProductList);
                        lstProduct.setAdapter(adapter);
                        lstProduct.setOnItemClickListener(new OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // 如果是添加子设备
                                if(mConfigProductList.get(position).nodeType != Constant.DEVICETYPE_GATEWAY) {
                                    // 如果网关已经选定但是网关不在线则退出处理
                                    if(mGatewayIOTId != null && mGatewayIOTId.length() > 0 && mGatewayStatus != Constant.CONNECTION_STATUS_ONLINE) {
                                        Dialog.confirm(ChoiceProductActivity.this, R.string.dialog_title, getString(R.string.configproduct_gateofflinehint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
                                        return;
                                    }
                                }

                                // 进入产品配网引导
                                Intent intent = new Intent(ChoiceProductActivity.this, ProductGuidanceActivity.class);
                                intent.putExtra("productKey", mConfigProductList.get(position).productKey);
                                intent.putExtra("productName", mConfigProductList.get(position).name);
                                intent.putExtra("nodeType", mConfigProductList.get(position).nodeType);
                                intent.putExtra("gatewayIOTId", mGatewayIOTId);
                                intent.putExtra("gatewayNumber", mGatewayNumber);
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEGATWAYLIST:
                    // 处理获取家网关数据
                    EHomeSpace.homeDeviceListEntry gateways = CloudDataParser.processHomeDeviceList((String)msg.obj);
                    if(gateways != null && gateways.data != null && gateways.data.size() > 0) {
                        mGatewayNumber = gateways.total;
                        // 如果只有一个网关则默认选定这个网关
                        if(gateways.total == 1) {
                            mGatewayIOTId = gateways.data.get(0).iotId;
                            mGatewayStatus = gateways.data.get(0).status;
                        }
                    }
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_product);

        TextView title = (TextView)findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.configproduct_title);

        ImageView back = (ImageView)findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        this.mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        this.mGatewayStatus = intent.getIntExtra("gatewayStatus", Constant.CONNECTION_STATUS_UNABLED);

        // 获取支持配网产品列表
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        // 没有指定网关时获取网关
        if(this.mGatewayIOTId == null || this.mGatewayIOTId.length() == 0) {
            new HomeSpaceManager(this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, 50,mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else {
            this.mGatewayNumber = 1;
        }
    }
}