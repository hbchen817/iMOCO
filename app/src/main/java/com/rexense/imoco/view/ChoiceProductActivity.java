package com.rexense.imoco.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rexense.imoco.R;
import com.rexense.imoco.presenter.AptConfigProductList;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.ProductHelper;
import com.rexense.imoco.presenter.ShareDeviceManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.ToastUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private ShareDeviceManager shareDeviceManager;

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

        TextView title = (TextView)findViewById(R.id.tv_toolbar_title);
        title.setText(R.string.configproduct_title);
        ImageView scanImg = (ImageView) findViewById(R.id.iv_toolbar_right);
        scanImg.setImageResource(R.drawable.scan_img);
        scanImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
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
        shareDeviceManager = new ShareDeviceManager(mActivity);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
            } else {
                ToastUtils.showToastCentrally(mActivity,getString(R.string.camera_denied_and_dont_ask_msg));
            }
        }else {
            Intent intent = new Intent(mActivity, ScanActivity.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(this, ScanActivity.class);
                    startActivityForResult(intent,1);
                }else {
                    ToastUtils.showToastCentrally(this, getString(R.string.camera_denied_msg));
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==-1&&requestCode==1){
            String qrKey = data.getStringExtra("result");
            shareDeviceManager.scanQrcode(qrKey, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_SCANSHAREQRCODE:
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.share_device_scan_success));
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}