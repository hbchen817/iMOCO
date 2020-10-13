package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.event.ShareDeviceSuccessEvent;
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

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 支持配网产品
 */
public class ChoiceProductActivity extends BaseActivity {
    private List<EProduct.configListEntry> mConfigProductListAll = null;
    private List<EProduct.configListEntry> mConfigProductList = null;
    private String mGatewayIOTId = "";
    private int mGatewayStatus = 0;
    private int mGatewayNumber = 0;
    private ShareDeviceManager shareDeviceManager;
    private TextView mLblSafe,mLblSwitch, mLblSensor, mLblGateway;

    // 产品类型点击处理
    private void onProductTypeClick(int productType){
        if(productType == Constant.PRODUCT_TYPE_SWITCH){
            this.mLblSwitch.setBackgroundColor(Color.WHITE);
            this.mLblSensor.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblGateway.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSwitch.setTextColor(getResources().getColor(R.color.topic_color1));
            this.mLblSensor.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblGateway.setTextColor(getResources().getColor(R.color.normal_font_color));
        } else if(productType == Constant.PRODUCT_TYPE_SENSOR){
            this.mLblSwitch.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSensor.setBackgroundColor(Color.WHITE);
            this.mLblGateway.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSwitch.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSensor.setTextColor(getResources().getColor(R.color.topic_color1));
            this.mLblGateway.setTextColor(getResources().getColor(R.color.normal_font_color));
        } else if(productType == Constant.PRODUCT_TYPE_GATEWAY){
            this.mLblSwitch.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSensor.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblGateway.setBackgroundColor(Color.WHITE);
            this.mLblSafe.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSwitch.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSensor.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblGateway.setTextColor(getResources().getColor(R.color.topic_color1));
        }else if(productType == Constant.PRODUCT_TYPE_SAFE){
            this.mLblSwitch.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSensor.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblGateway.setBackgroundColor(getResources().getColor(R.color.appbgcolor));
            this.mLblSafe.setBackgroundColor(Color.WHITE);
            this.mLblSafe.setTextColor(getResources().getColor(R.color.topic_color1));
            this.mLblSwitch.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblSensor.setTextColor(getResources().getColor(R.color.normal_font_color));
            this.mLblGateway.setTextColor(getResources().getColor(R.color.normal_font_color));
        }

        if(this.mConfigProductListAll == null){
            return;
        }

        mConfigProductList = new ArrayList<EProduct.configListEntry>();
        for(EProduct.configListEntry entry : this.mConfigProductListAll){
            if(productType == Constant.PRODUCT_TYPE_SWITCH){
                // 开关处理
                if(entry.productKey.equalsIgnoreCase(CTSL.PK_ONEWAYSWITCH) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_TWOWAYSWITCH) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_REMOTECONTRILBUTTON)){
                    this.mConfigProductList.add(entry);
                }

            } else if(productType == Constant.PRODUCT_TYPE_SENSOR){
                // 传感器处理
                if(entry.productKey.equalsIgnoreCase(CTSL.PK_DOORSENSOR) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_PIRSENSOR) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_SMOKESENSOR) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_GASSENSOR) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_WATERSENSOR) ||
                        entry.productKey.equalsIgnoreCase(CTSL.PK_TEMHUMSENSOR)){
                    this.mConfigProductList.add(entry);
                }
            } else if(productType == Constant.PRODUCT_TYPE_GATEWAY){
                // 网关处理
                if(entry.productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)||entry.productKey.equalsIgnoreCase(CTSL.PK_GATEWAY_RG4100)){
                    this.mConfigProductList.add(entry);
                }
            }else if(productType == Constant.PRODUCT_TYPE_SAFE){
                //安防处理
                if (entry.productKey.equalsIgnoreCase(CTSL.PK_SMART_LOCK)){
                    this.mConfigProductList.add(entry);
                }
            }
        }

        //添加携住设备在开关类型中
        if(SystemParameter.getInstance().getIsAddXZDevice().equalsIgnoreCase("Yes") &&
                productType == Constant.PRODUCT_TYPE_SWITCH){
            String[] productKeys = new String[]{"1", "2", "3", "4", "5"};
            String[] names = new String[]{"D3单火1键", "D3单火2键", "D3单火3键", "D3单火4键", "D3智能插座"};
            for(int i = 0; i < productKeys.length; i++){
                EProduct.configListEntry entry = new EProduct.configListEntry();
                entry.productKey = productKeys[i];
                entry.name = names[i];
                this.mConfigProductList.add(entry);
            }
        }

        GridView grdProduct = (GridView)findViewById(R.id.choiceProductGrdProduct);
        AptConfigProductList adapter = new AptConfigProductList(ChoiceProductActivity.this, mConfigProductList);
        grdProduct.setAdapter(adapter);
        grdProduct.setOnItemClickListener(onItemClickProduct);
    }

    // 产品条目点击事件
    private OnItemClickListener onItemClickProduct = new OnItemClickListener(){
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
    };

    // 数据处理器
    private Handler processDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    mConfigProductListAll = CloudDataParser.processConfigProcductList((String)msg.obj);
                    if(mConfigProductListAll != null) {
                        // 如果网关已经确定则不包含网关
                        if(mGatewayIOTId != null && mGatewayIOTId.length() > 0) {
                            int count = mConfigProductListAll.size() - 1;
                            for(int i = count; i >= 0; i--) {
                                if(mConfigProductListAll.get(i).nodeType == Constant.DEVICETYPE_GATEWAY) {
                                    mConfigProductListAll.remove(i);
                                }
                            }
                        }
                        onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
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

        this.mLblSafe = (TextView)findViewById(R.id.choiceProductTypeSafe);
        this.mLblSwitch = (TextView)findViewById(R.id.choiceProductTypeSwitch);
        this.mLblSensor = (TextView)findViewById(R.id.choiceProductTypeSensor);
        this.mLblGateway = (TextView)findViewById(R.id.choiceProductTypeGateway);

        // 点击开关处理
        this.mLblSafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
            }
        });

        // 点击开关处理
        this.mLblSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SWITCH);
            }
        });

        // 点击传感器处理
        this.mLblSensor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SENSOR);
            }
        });

        // 点击网关处理
        this.mLblGateway.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_GATEWAY);
            }
        });

        Intent intent = getIntent();
        this.mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        this.mGatewayStatus = intent.getIntExtra("gatewayStatus", Constant.CONNECTION_STATUS_UNABLED);

        // 获取支持配网产品列表
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        // 没有指定网关时获取网关列表以获取网关的数量
        if(this.mGatewayIOTId == null || this.mGatewayIOTId.length() == 0) {
            new HomeSpaceManager(this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, 50,mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else {
            this.mGatewayNumber = 1;
            this.mLblGateway.setVisibility(View.GONE);
            scanImg.setVisibility(View.GONE);
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
                    EventBus.getDefault().post(new ShareDeviceSuccessEvent());
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}