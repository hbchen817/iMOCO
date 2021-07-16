package com.xiezhu.jzj.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.event.ShareDeviceSuccessEvent;
import com.xiezhu.jzj.presenter.AptConfigProductList;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.HomeSpaceManager;
import com.xiezhu.jzj.presenter.ProductHelper;
import com.xiezhu.jzj.presenter.ShareDeviceManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.model.EHomeSpace;
import com.xiezhu.jzj.model.EProduct;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.utility.Dialog;
import com.xiezhu.jzj.utility.QMUITipDialogUtil;
import com.xiezhu.jzj.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 支持配网产品
 */
public class ChoiceProductActivity extends BaseActivity {
    private final List<String> mLightCategoryKeyList = Arrays.asList("light", "Lamp");
    private final List<String> mElectricCategoryKeyList = Arrays.asList("WallSwitch", "SceneSwitch", "emergency_button", "Outlet", "Scene", "Dimming_panel");
    private final List<String> mSafeCategoryKeyList = Arrays.asList("Siren", "emergency_button", "SmartDoor", "azardWarningLamp", "AlarmSwitch", "Camera", "Cateyecamera", "DoorViewer", "Doorbell", "VideoDoorbell");
    private final List<String> mHomeCategoryKeyList = Arrays.asList("LocalControlCenter", "Curtain", "Curtain_motor", "IRRemoteController", "WindowLinearActuator");
    private final List<String> mSensorCategoryKeyList = Arrays.asList("GasDetector", "WaterDetector", "SmokeAlarm", "DoorContact", "IRDetector", "Airbox", "TempHumiUnit", "airbox", "IlluminationSensor", "VibrationSensor");
    private final List<String> mEnvironmentalCategoryKeyList = Arrays.asList("airpurifier", "FAU", "AirConditioning", "FloorHeating", "aircondition");
    private final List<String> mLivingCategoryKeyList = Arrays.asList("ToiletSeat", "ElectricWaterHeater", "GasWaterHeater", "BathHeater", "hanger", "towelRack");
    private final List<String> mGatewayCategoryKeyList = Arrays.asList("Gateway", "GeneralGateway", "HomeLinkEdgeGateway");
    private final List<String> mOtherCategoryKeyList = Arrays.asList("AutoDoor");

    private List<EProduct.configListEntry> mConfigProductListAll = null;
    private List<EProduct.configListEntry> mConfigProductList = null;
    private String mGatewayIOTId = "";
    private int mGatewayStatus = 0;
    private int mGatewayNumber = 0;
    private ShareDeviceManager shareDeviceManager;
    private TextView mLblSafe, mLblSensor, mLblGateway, mLblLight, mLblElectric, mLblHome, mLblEnvironmental, mLblLiving, mLblOther;

    // 产品类型点击处理
    private void onProductTypeClick(int productType) {
        handleTypeColor(productType);
        if (mConfigProductListAll == null) {
            return;
        }

        mConfigProductList = new ArrayList<EProduct.configListEntry>();
        for (EProduct.configListEntry entry : mConfigProductListAll) {
            if (filterProductWithType(entry, productType)) {
                mConfigProductList.add(entry);
            }
        }

        //添加携住设备在开关类型中
        if (SystemParameter.getInstance().getIsAddXZDevice().equalsIgnoreCase("Yes") &&
                productType == Constant.PRODUCT_TYPE_ELECTRIC) {
            String[] productKeys = new String[]{"1", "2", "3", "4", "5"};
            String[] names = new String[]{"D3单火1键", "D3单火2键", "D3单火3键", "D3单火4键", "D3智能插座"};
            for (int i = 0; i < productKeys.length; i++) {
                EProduct.configListEntry entry = new EProduct.configListEntry();
                entry.productKey = productKeys[i];
                entry.name = names[i];
                mConfigProductList.add(entry);
            }
        }

        GridView grdProduct = findViewById(R.id.choiceProductGrdProduct);
        AptConfigProductList adapter = new AptConfigProductList(ChoiceProductActivity.this, mConfigProductList);
        grdProduct.setAdapter(adapter);
        grdProduct.setOnItemClickListener(onItemClickProduct);
    }

    // 产品条目点击事件
    private final OnItemClickListener onItemClickProduct = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 如果是添加子设备
            if (mConfigProductList.get(position).nodeType != Constant.DEVICETYPE_GATEWAY) {
                // 如果网关已经选定但是网关不在线则退出处理
                if (mGatewayIOTId != null && mGatewayIOTId.length() > 0 && mGatewayStatus != Constant.CONNECTION_STATUS_ONLINE) {
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
    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST:
                    // 处理获取支持配网产品列表数据
                    mConfigProductListAll = CloudDataParser.processConfigProcductList((String) msg.obj);
                    if (mConfigProductListAll != null) {
                        // 如果网关已经确定则不包含网关
                        if (mGatewayIOTId != null && mGatewayIOTId.length() > 0) {
                            int count = mConfigProductListAll.size() - 1;
                            for (int i = count; i >= 0; i--) {
                                if (mConfigProductListAll.get(i).nodeType == Constant.DEVICETYPE_GATEWAY) {
                                    mConfigProductListAll.remove(i);
                                }
                            }
                        }
                        new HomeSpaceManager(ChoiceProductActivity.this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, 50, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                        handleTypeVisible();
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEGATWAYLIST:
                    // 处理获取家网关数据
                    EHomeSpace.homeDeviceListEntry gateways = CloudDataParser.processHomeDeviceList((String) msg.obj);
                    if (gateways != null && gateways.data != null && gateways.data.size() > 0) {
                        mGatewayNumber = gateways.total;
                        // 如果只有一个网关则默认选定这个网关
                        if (gateways.total == 1) {
                            mGatewayIOTId = gateways.data.get(0).iotId;
                            mGatewayStatus = gateways.data.get(0).status;
                        }
                    }
                    QMUITipDialogUtil.dismiss();
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

        TextView title = findViewById(R.id.tv_toolbar_title);
        title.setText(R.string.configproduct_title);
        ImageView scanImg = findViewById(R.id.iv_toolbar_right);
        scanImg.setImageResource(R.drawable.scan_img);
        scanImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        mLblLight = findViewById(R.id.choiceProductTypeLight);
        mLblElectric = findViewById(R.id.choiceProductTypeElectric);
        mLblSafe = findViewById(R.id.choiceProductTypeSafe);
        mLblHome = findViewById(R.id.choiceProductTypeHome);
        mLblSensor = findViewById(R.id.choiceProductTypeSensor);
        mLblEnvironmental = findViewById(R.id.choiceProductTypeEnvironmental);
        mLblLiving = findViewById(R.id.choiceProductTypeLiving);
        mLblGateway = findViewById(R.id.choiceProductTypeGateway);
        mLblOther = findViewById(R.id.choiceProductTypeOther);

        // 点击开关处理
        mLblSafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
            }
        });

        // 点击开关处理
        mLblLight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_LIGHT);
            }
        });
        // 点击开关处理
        mLblElectric.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_ELECTRIC);
            }
        });
        // 点击开关处理
        mLblHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_HOME);
            }
        });
        // 点击开关处理
        mLblEnvironmental.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_ENVIRONMENTAL);
            }
        });
        // 点击开关处理
        mLblLiving.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_LIVING);
            }
        });

        // 点击传感器处理
        mLblSensor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SENSOR);
            }
        });

        // 点击网关处理
        mLblGateway.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_GATEWAY);
            }
        });
        // 点击网关处理
        mLblOther.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_OUTHOR);
            }
        });

        Intent intent = getIntent();
        mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        mGatewayStatus = intent.getIntExtra("gatewayStatus", Constant.CONNECTION_STATUS_UNABLED);

        // 没有指定网关时获取网关列表以获取网关的数量
        if (mGatewayIOTId == null || mGatewayIOTId.length() == 0) {
            //new HomeSpaceManager(this).getHomeGatewayList(SystemParameter.getInstance().getHomeId(), "", 1, 50, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else {
            mGatewayNumber = 1;
            mLblGateway.setVisibility(View.GONE);
            scanImg.setVisibility(View.GONE);
        }
        shareDeviceManager = new ShareDeviceManager(mActivity);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        // 获取支持配网产品列表
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.camera_denied_and_dont_ask_msg));
            }
        } else {
            Intent intent = new Intent(mActivity, ScanActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent, 1);
            } else {
                ToastUtils.showToastCentrally(this, getString(R.string.camera_denied_msg));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            String qrKey = data.getStringExtra("result");
            shareDeviceManager.scanQrcode(qrKey, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_SCANSHAREQRCODE) {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.share_device_scan_success));
                EventBus.getDefault().post(new ShareDeviceSuccessEvent());
            }
            return false;
        }
    });

    /**
     * 处理类别显示
     */
    private void handleTypeVisible() {

        for (EProduct.configListEntry next : mConfigProductListAll) {
            if (mSafeCategoryKeyList.contains(next.categoryKey)) {
                mLblSafe.setVisibility(View.VISIBLE);
            } else if (mLightCategoryKeyList.contains(next.categoryKey)) {
                mLblLight.setVisibility(View.VISIBLE);
            } else if (mElectricCategoryKeyList.contains(next.categoryKey)) {
                mLblElectric.setVisibility(View.VISIBLE);
            } else if (mHomeCategoryKeyList.contains(next.categoryKey)) {
                mLblHome.setVisibility(View.VISIBLE);
            } else if (mSensorCategoryKeyList.contains(next.categoryKey)) {
                mLblSensor.setVisibility(View.VISIBLE);
            } else if (mEnvironmentalCategoryKeyList.contains(next.categoryKey)) {
                mLblEnvironmental.setVisibility(View.VISIBLE);
            } else if (mLivingCategoryKeyList.contains(next.categoryKey)) {
                mLblLiving.setVisibility(View.VISIBLE);
            } else if (mGatewayCategoryKeyList.contains(next.categoryKey)) {
                mLblGateway.setVisibility(View.VISIBLE);
            } else if (mOtherCategoryKeyList.contains(next.categoryKey)) {
                mLblOther.setVisibility(View.VISIBLE);
            }
        }
        if (mLblLight.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_LIGHT);
        } else if (mLblElectric.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_ELECTRIC);
        } else if (mLblSafe.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
        } else if (mLblHome.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_HOME);
        } else if (mLblSensor.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_SENSOR);
        } else if (mLblEnvironmental.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_ENVIRONMENTAL);
        } else if (mLblLiving.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_LIVING);
        } else if (mLblGateway.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_GATEWAY);
        } else if (mLblOther.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_OUTHOR);
        }
    }

    /**
     * 处理类别显示颜色
     *
     * @param productType 选中类别
     */
    private void handleTypeColor(int productType) {
        mLblLight.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblElectric.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblSafe.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblHome.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblSensor.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblEnvironmental.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblLiving.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblGateway.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));
        mLblOther.setBackgroundColor(ContextCompat.getColor(this, R.color.appbgcolor));

        mLblLight.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblElectric.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblSafe.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblHome.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblSensor.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblEnvironmental.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblLiving.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblGateway.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));
        mLblOther.setTextColor(ContextCompat.getColor(this, R.color.normal_font_color));

        if (productType == Constant.PRODUCT_TYPE_LIGHT) {
            mLblLight.setBackgroundColor(Color.WHITE);
            mLblLight.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_ELECTRIC) {
            mLblElectric.setBackgroundColor(Color.WHITE);
            mLblElectric.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_SAFE) {
            mLblSafe.setBackgroundColor(Color.WHITE);
            mLblSafe.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_HOME) {
            mLblHome.setBackgroundColor(Color.WHITE);
            mLblHome.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_SENSOR) {
            mLblSensor.setBackgroundColor(Color.WHITE);
            mLblSensor.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_ENVIRONMENTAL) {
            mLblEnvironmental.setBackgroundColor(Color.WHITE);
            mLblEnvironmental.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_LIVING) {
            mLblLiving.setBackgroundColor(Color.WHITE);
            mLblLiving.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_GATEWAY) {
            mLblGateway.setBackgroundColor(Color.WHITE);
            mLblGateway.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_OUTHOR) {
            mLblOther.setBackgroundColor(Color.WHITE);
            mLblOther.setTextColor(ContextCompat.getColor(this, R.color.topic_color1));
        }
    }

    /**
     * 过滤产品是否符合选中类别
     *
     * @param entry       产品
     * @param productType 类别
     * @return true 符合
     */
    private boolean filterProductWithType(EProduct.configListEntry entry, int productType) {
        if (productType == Constant.PRODUCT_TYPE_ELECTRIC && mElectricCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_SENSOR && mSensorCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_GATEWAY && mGatewayCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_SAFE && mSafeCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_LIGHT && mLightCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_ENVIRONMENTAL && mEnvironmentalCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_HOME && mHomeCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_OUTHOR && mOtherCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        } else if (productType == Constant.PRODUCT_TYPE_LIVING && mLivingCategoryKeyList.contains(entry.categoryKey)) {
            return true;
        }
        return false;
    }
}