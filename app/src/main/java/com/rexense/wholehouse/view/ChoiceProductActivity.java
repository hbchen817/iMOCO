package com.rexense.wholehouse.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import android.widget.AdapterView.OnItemClickListener;

import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.databinding.ActivityChoiceProductBinding;
import com.rexense.wholehouse.event.ShareDeviceSuccessEvent;
import com.rexense.wholehouse.model.ProductTeam;
import com.rexense.wholehouse.presenter.CloudDataParser;
import com.rexense.wholehouse.presenter.HomeSpaceManager;
import com.rexense.wholehouse.presenter.ProductHelper;
import com.rexense.wholehouse.presenter.ShareDeviceManager;
import com.rexense.wholehouse.presenter.SystemParameter;
import com.rexense.wholehouse.model.EHomeSpace;
import com.rexense.wholehouse.model.EProduct;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.utility.Dialog;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.SpUtils;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.viewholder.CountSectionAdapter;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 支持配网产品
 */
public class ChoiceProductActivity extends BaseActivity {
    private ActivityChoiceProductBinding mViewBinding;

    private final List<String> mLightCategoryKeyList = Arrays.asList("light", "Lamp");
    private final List<String> mElectricCategoryKeyList = Arrays.asList("WallSwitch", "SceneSwitch", "emergency_button", "Outlet", "Scene", "Dimming_panel", "switch", "outlet");
    private final List<String> mSafeCategoryKeyList = Arrays.asList("Siren", /*"emergency_button", */"SmartDoor", "azardWarningLamp", "AlarmSwitch", "Camera", "Cateyecamera", "DoorViewer", "Doorbell", "VideoDoorbell");
    private final List<String> mHomeCategoryKeyList = Arrays.asList("LocalControlCenter", "Curtain", "curtain", "Curtain_motor", "IRRemoteController", "WindowLinearActuator");
    private final List<String> mSensorCategoryKeyList = Arrays.asList("GasDetector", "WaterDetector", "SmokeAlarm", "DoorContact", "IRDetector", "Airbox", "TempHumiUnit", "airbox", "IlluminationSensor", "VibrationSensor");
    private final List<String> mEnvironmentalCategoryKeyList = Arrays.asList("airpurifier", "FAU", "AirConditioning", "FloorHeating", "aircondition");
    private final List<String> mLivingCategoryKeyList = Arrays.asList("ToiletSeat", "ElectricWaterHeater", "GasWaterHeater", "BathHeater", "hanger", "towelRack");
    private final List<String> mGatewayCategoryKeyList = Arrays.asList("Gateway", "GeneralGateway", "HomeLinkEdgeGateway");
    private final List<String> mOtherCategoryKeyList = Collections.singletonList("AutoDoor");

    private List<EProduct.configListEntry> mConfigProductListAll = null;
    private List<EProduct.configListEntry> mConfigProductList = null;
    private String mGatewayIOTId = "";
    private int mGatewayStatus = 0;
    private int mGatewayNumber = 0;
    private ShareDeviceManager shareDeviceManager;

    private ProductTeam mHYTeam, mRYTeam, mYQSTeam, mLFTeam, mDDTeam, mKDSTeam, mDYTeam, mHMTeam, mMLKTeam, mWSDTeam,
            mMMTeam, mMSTeam;
    private List<ProductTeam> mProductTeamList = new ArrayList<>();

    // 产品类型点击处理
    private void onProductTypeClick(int productType) {
        handleTypeColor(productType);
        if (this.mConfigProductListAll == null) {
            return;
        }

        mHYTeam.getProductList().clear();
        mRYTeam.getProductList().clear();
        mYQSTeam.getProductList().clear();
        mLFTeam.getProductList().clear();
        mDDTeam.getProductList().clear();
        mKDSTeam.getProductList().clear();
        mDYTeam.getProductList().clear();
        mHMTeam.getProductList().clear();
        mMLKTeam.getProductList().clear();
        mWSDTeam.getProductList().clear();
        mMMTeam.getProductList().clear();
        mMSTeam.getProductList().clear();

        mConfigProductList = new ArrayList<EProduct.configListEntry>();
        for (EProduct.configListEntry entry : this.mConfigProductListAll) {
            if (filterProductWithType(entry, productType)) {
                mConfigProductList.add(entry);

                if (CTSL.PK_ONEWAYSWITCH_HY.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYSWITCH_HY.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYSWITCH_MODULE_HY.equals(entry.productKey) ||
                        CTSL.PK_THREEWAYSWITCH_HY.equals(entry.productKey) ||
                        CTSL.PK_U_SIX_SCENE_SWITCH_HY.equals(entry.productKey) ||
                        CTSL.PK_FULL_SCREEN_SWITCH_HY.equals(entry.productKey) ||
                        CTSL.PK_10A_MEASURING_OUTLET_HY.equals(entry.productKey) ||
                        CTSL.PK_PM_TEMHUMSENSOR_HY.equals(entry.productKey) ||
                        CTSL.PK_PM_TEMHUMSENSOR_HY_PTM1005S.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_10A_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_10A_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_16A_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_16A_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_16A_MEASURING_OUTLET_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_16A_MEASURING_OUTLET_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_AIRCOMDITION_TWO_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_AIRCOMDITION_TWO_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_WATER_FLOORHEAT_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_WATER_FLOORHEAT_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_ELEC_FLOORHEAT_HY_U1.equals(entry.productKey) ||
                        CTSL.PK_ELEC_FLOORHEAT_HY_U2.equals(entry.productKey) ||
                        CTSL.PK_FAU_HY_U1.equals(entry.productKey)) {
                    mHYTeam.getProductList().add(entry);
                } else if (CTSL.PK_ONEWAYSWITCH_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYSWITCH_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_THREEWAYSWITCH_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_SIX_SCENE_SWITCH_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYSWITCH_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYSWITCH_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_THREEWAYSWITCH_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_SIX_SCENE_SWITCH_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYWINDOWCURTAINS_YQS_ZR.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYWINDOWCURTAINS_YQS_XB.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_10A_YQS.equals(entry.productKey) ||
                        CTSL.PK_10A_MEASURING_OUTLET_YQS.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_16A_YQS.equals(entry.productKey) ||
                        CTSL.PK_16A_MEASURING_OUTLET_YQS.equals(entry.productKey)) {
                    mYQSTeam.getProductList().add(entry);
                } else if (CTSL.PK_ONEWAYSWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYSWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_THREEWAYSWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_FOURWAYSWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_FOUR_SCENE_SWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_10A_LF.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_LF.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYWINDOWCURTAINS_LF.equals(entry.productKey) ||
                        CTSL.PK_FLOORHEATING001_LF.equals(entry.productKey) ||
                        CTSL.PK_AIRCOMDITION_TWO_LF.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_LF_D8.equals(entry.productKey) ||
                        CTSL.PK_ONEWAYWINDOWCURTAINS_LF_D9.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYWINDOWCURTAINS_LF_D8.equals(entry.productKey) ||
                        CTSL.PK_TWOWAYWINDOWCURTAINS_LF_D9.equals(entry.productKey) ||
                        CTSL.PK_10A_MEASURING_OUTLET_LF.equals(entry.productKey) ||
                        CTSL.PK_OUTLET_16A_LF.equals(entry.productKey) ||
                        CTSL.PK_16A_MEASURING_OUTLET_LF.equals(entry.productKey) ||
                        CTSL.PK_FAU_LF.equals(entry.productKey) ||
                        CTSL.PK_FOUR_TWO_SCENE_SWITCH_LF.equals(entry.productKey) ||
                        CTSL.PK_SIX_FOUR_SCENE_SWITCH_LF.equals(entry.productKey)) {
                    mLFTeam.getProductList().add(entry);
                } else if (CTSL.PK_ONEWAY_DANHUO_RY.equals(entry.productKey) ||
                        CTSL.PK_TWOWAY_DANHUO_RY.equals(entry.productKey) ||
                        CTSL.PK_THREEWAY_DANHUO_RY.equals(entry.productKey) ||
                        CTSL.PK_GATEWAY_RG4100_RY.equals(entry.productKey)) {
                    mRYTeam.getProductList().add(entry);
                } else if (CTSL.PK_KDS_SMART_LOCK_A7.equals(entry.productKey) ||
                        CTSL.PK_KDS_SMART_LOCK_K100.equals(entry.productKey) ||
                        CTSL.PK_KDS_SMART_LOCK_S6.equals(entry.productKey)) {
                    mKDSTeam.getProductList().add(entry);
                } else if (CTSL.PK_MM_SMART_LOCK.equals(entry.productKey)) {
                    mMMTeam.getProductList().add(entry);
                } else if (CTSL.PK_MS_SMART_LOCK.equals(entry.productKey)) {
                    mMSTeam.getProductList().add(entry);
                } else if (CTSL.PK_DY_ELE_D82.equals(entry.productKey) ||
                        CTSL.PK_DY_ELE_DC.equals(entry.productKey) ||
                        CTSL.PK_DY_ELE_D52.equals(entry.productKey)) {
                    mDYTeam.getProductList().add(entry);
                } else if (CTSL.PK_WSD_ELE_DC.equals(entry.productKey) ||
                        CTSL.PK_WSD_ELE_AC.equals(entry.productKey)) {
                    mWSDTeam.getProductList().add(entry);
                } else if (CTSL.PK_DOORSENSOR_HM.equals(entry.productKey) ||
                        CTSL.PK_WATERSENSOR_HM.equals(entry.productKey) ||
                        CTSL.PK_PIRSENSOR_HM.equals(entry.productKey) ||
                        CTSL.PK_SMOKESENSOR_HM.equals(entry.productKey) ||
                        CTSL.PK_GASSENSOR_HM.equals(entry.productKey) ||
                        CTSL.PK_TEMHUMSENSOR_HM.equals(entry.productKey)) {
                    mHMTeam.getProductList().add(entry);
                } else if (CTSL.PK_SMOKESENSOR_MLK.equals(entry.productKey) ||
                        CTSL.PK_PIRSENSOR_MLK.equals(entry.productKey) ||
                        CTSL.PK_TEMHUMSENSOR_MLK.equals(entry.productKey) ||
                        CTSL.PK_GASSENSOR_MLK.equals(entry.productKey) ||
                        CTSL.PK_WATERSENSOR_MLK.equals(entry.productKey) ||
                        CTSL.PK_DOORSENSOR_MLK.equals(entry.productKey)) {
                    mMLKTeam.getProductList().add(entry);
                } else {
                    mDDTeam.getProductList().add(entry);
                }
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
                this.mConfigProductList.add(entry);
            }
        }

        /*GridView grdProduct = (GridView) findViewById(R.id.choiceProductGrdProduct);
        AptConfigProductList adapter = new AptConfigProductList(ChoiceProductActivity.this, mConfigProductList);
        grdProduct.setAdapter(adapter);
        grdProduct.setOnItemClickListener(onItemClickProduct);*/

        mProductTeamList.clear();
        if (mHYTeam.getProductList().size() > 0)
            mProductTeamList.add(mHYTeam);
        if (mYQSTeam.getProductList().size() > 0)
            mProductTeamList.add(mYQSTeam);
        if (mLFTeam.getProductList().size() > 0)
            mProductTeamList.add(mLFTeam);
        if (mRYTeam.getProductList().size() > 0)
            mProductTeamList.add(mRYTeam);
        if (mKDSTeam.getProductList().size() > 0)
            mProductTeamList.add(mKDSTeam);
        if (mDYTeam.getProductList().size() > 0)
            mProductTeamList.add(mDYTeam);
        if (mHMTeam.getProductList().size() > 0)
            mProductTeamList.add(mHMTeam);
        if (mMLKTeam.getProductList().size() > 0)
            mProductTeamList.add(mMLKTeam);
        if (mDDTeam.getProductList().size() > 0)
            mProductTeamList.add(mDDTeam);
        if (mWSDTeam.getProductList().size() > 0)
            mProductTeamList.add(mWSDTeam);
        if (mMMTeam.getProductList().size() > 0)
            mProductTeamList.add(mMMTeam);
        if (mMSTeam.getProductList().size() > 0)
            mProductTeamList.add(mMSTeam);
        mCountSectionAdapter.notifyDataSetChanged();
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
        public boolean handleMessage(Message msg) {
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
        mViewBinding = ActivityChoiceProductBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.configproduct_title);
        mViewBinding.includeToolbar.ivToolbarRight.setImageResource(R.drawable.scan_img);
        mViewBinding.includeToolbar.ivToolbarRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        // 点击开关处理
        mViewBinding.choiceProductTypeSafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
            }
        });

        // 点击开关处理
        mViewBinding.choiceProductTypeLight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_LIGHT);
            }
        });
        // 点击开关处理
        mViewBinding.choiceProductTypeElectric.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_ELECTRIC);
            }
        });
        // 点击开关处理
        mViewBinding.choiceProductTypeHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_HOME);
            }
        });
        // 点击开关处理
        mViewBinding.choiceProductTypeEnvironmental.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_ENVIRONMENTAL);
            }
        });
        // 点击开关处理
        mViewBinding.choiceProductTypeLiving.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_LIVING);
            }
        });

        // 点击传感器处理
        mViewBinding.choiceProductTypeSensor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_SENSOR);
            }
        });

        // 点击网关处理
        mViewBinding.choiceProductTypeGateway.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductTypeClick(Constant.PRODUCT_TYPE_GATEWAY);
            }
        });
        // 点击网关处理
        mViewBinding.choiceProductTypeOther.setOnClickListener(new OnClickListener() {
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
            mViewBinding.choiceProductTypeGateway.setVisibility(View.GONE);
            mViewBinding.includeToolbar.ivToolbarRight.setVisibility(View.GONE);
        }
        shareDeviceManager = new ShareDeviceManager(mActivity);

        mHYTeam = new ProductTeam(getString(R.string.hong_yan));
        mRYTeam = new ProductTeam(getString(R.string.rui_ying));
        mYQSTeam = new ProductTeam(getString(R.string.yue_qi_sheng));
        mLFTeam = new ProductTeam(getString(R.string.la_fei));
        mKDSTeam = new ProductTeam(getString(R.string.kai_di_shi));
        mDYTeam = new ProductTeam(getString(R.string.du_ya));
        mHMTeam = new ProductTeam(getString(R.string.hai_man));
        mMLKTeam = new ProductTeam(getString(R.string.mai_le_ke));
        mWSDTeam = new ProductTeam(getString(R.string.wei_shi_da));
        mMMTeam = new ProductTeam(getString(R.string.ming_men));
        mMSTeam = new ProductTeam(getString(R.string.man_shen));
        mDDTeam = new ProductTeam(getString(R.string.others));

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        // 获取支持配网产品列表
        new ProductHelper(this).getConfigureList(mCommitFailureHandler, mResponseErrorHandler, processDataHandler);

        initStatusBar();
        setupRecycler();
    }

    private CountSectionAdapter mCountSectionAdapter;

    protected void setupRecycler() {
        mCountSectionAdapter = new CountSectionAdapter(this, mProductTeamList);
        mCountSectionAdapter.setListener(new CountSectionAdapter.CountSectionListener() {
            @Override
            public void onItemClicked(int section, int position) {
                ProductTeam team = mProductTeamList.get(section);
                EProduct.configListEntry entry = team.getProductList().get(position);

                // 如果是添加子设备
                if (entry.nodeType != Constant.DEVICETYPE_GATEWAY) {
                    // 如果网关已经选定但是网关不在线则退出处理
                    if (mGatewayIOTId != null && mGatewayIOTId.length() > 0 && mGatewayStatus != Constant.CONNECTION_STATUS_ONLINE) {
                        Dialog.confirm(ChoiceProductActivity.this, R.string.dialog_title, getString(R.string.configproduct_gateofflinehint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
                        return;
                    }
                }

                // 进入产品配网引导
                Intent intent = new Intent(ChoiceProductActivity.this, ProductGuidanceActivity.class);
                intent.putExtra("productKey", entry.productKey);
                intent.putExtra("productName", entry.name);
                intent.putExtra("nodeType", entry.nodeType);
                intent.putExtra("gatewayIOTId", mGatewayIOTId);
                intent.putExtra("gatewayNumber", mGatewayNumber);
                startActivity(intent);
            }
        });
        mViewBinding.productRecycler.setAdapter(mCountSectionAdapter);

        //GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        /*SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(mCountSectionAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.productRecycler.setLayoutManager(layoutManager);
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
            boolean hasRequstCamera = SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, SpUtils.PS_REQUEST_CAMERA_PERMISSION, false);
            // 未有权限
            // 第一次请求权限 false
            // 第一次请求权限拒绝，但未选择“不再提醒” true
            // 第一次请求权限拒绝，并选择“不再提醒” false
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || !hasRequstCamera) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

                SpUtils.putBooleanValue(this, SpUtils.SP_APP_INFO, SpUtils.PS_REQUEST_CAMERA_PERMISSION, true);
            } else {
                ToastUtils.showLongToast(mActivity, getString(R.string.camera_denied_and_dont_ask_msg));
            }

            ViseLog.d("flag = " + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
        } else {
            // 已经获取权限
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
                ToastUtils.showLongToast(this, R.string.camera_denied_msg);
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
        public boolean handleMessage(Message msg) {
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

        Iterator<EProduct.configListEntry> iterator = mConfigProductListAll.iterator();
        while (iterator.hasNext()) {
            EProduct.configListEntry next = iterator.next();
            if (mSafeCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeSafe.setVisibility(View.VISIBLE);
            } else if (mLightCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeLight.setVisibility(View.VISIBLE);
            } else if (mElectricCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeElectric.setVisibility(View.VISIBLE);
            } else if (mHomeCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeHome.setVisibility(View.VISIBLE);
            } else if (mSensorCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeSensor.setVisibility(View.VISIBLE);
            } else if (mEnvironmentalCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeEnvironmental.setVisibility(View.VISIBLE);
            } else if (mLivingCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeLiving.setVisibility(View.VISIBLE);
            } else if (mGatewayCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeGateway.setVisibility(View.VISIBLE);
            } else if (mOtherCategoryKeyList.contains(next.categoryKey)) {
                mViewBinding.choiceProductTypeOther.setVisibility(View.VISIBLE);
            }
        }
        if (mViewBinding.choiceProductTypeLight.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_LIGHT);
        } else if (mViewBinding.choiceProductTypeElectric.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_ELECTRIC);
        } else if (mViewBinding.choiceProductTypeSafe.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_SAFE);
        } else if (mViewBinding.choiceProductTypeHome.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_HOME);
        } else if (mViewBinding.choiceProductTypeSensor.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_SENSOR);
        } else if (mViewBinding.choiceProductTypeEnvironmental.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_ENVIRONMENTAL);
        } else if (mViewBinding.choiceProductTypeLiving.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_LIVING);
        } else if (mViewBinding.choiceProductTypeGateway.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_GATEWAY);
        } else if (mViewBinding.choiceProductTypeOther.getVisibility() == View.VISIBLE) {
            onProductTypeClick(Constant.PRODUCT_TYPE_OUTHOR);
        }
    }

    /**
     * 处理类别显示颜色
     *
     * @param productType 选中类别
     */
    private void handleTypeColor(int productType) {
        int appBgColor = getResources().getColor(R.color.appbgcolor);
        mViewBinding.choiceProductTypeLight.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeElectric.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeSafe.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeHome.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeSensor.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeEnvironmental.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeLiving.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeGateway.setBackgroundColor(appBgColor);
        mViewBinding.choiceProductTypeOther.setBackgroundColor(appBgColor);

        int normalFontColor = getResources().getColor(R.color.normal_font_color);
        mViewBinding.choiceProductTypeLight.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeElectric.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeSafe.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeHome.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeSensor.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeEnvironmental.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeLiving.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeGateway.setTextColor(normalFontColor);
        mViewBinding.choiceProductTypeOther.setTextColor(normalFontColor);

        if (productType == Constant.PRODUCT_TYPE_LIGHT) {
            mViewBinding.choiceProductTypeLight.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeLight.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_ELECTRIC) {
            mViewBinding.choiceProductTypeElectric.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeElectric.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_SAFE) {
            mViewBinding.choiceProductTypeSafe.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeSafe.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_HOME) {
            mViewBinding.choiceProductTypeHome.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeHome.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_SENSOR) {
            mViewBinding.choiceProductTypeSensor.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeSensor.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_ENVIRONMENTAL) {
            mViewBinding.choiceProductTypeEnvironmental.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeEnvironmental.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_LIVING) {
            mViewBinding.choiceProductTypeLiving.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeLiving.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_GATEWAY) {
            mViewBinding.choiceProductTypeGateway.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeGateway.setTextColor(getResources().getColor(R.color.topic_color1));
        } else if (productType == Constant.PRODUCT_TYPE_OUTHOR) {
            mViewBinding.choiceProductTypeOther.setBackgroundColor(Color.WHITE);
            mViewBinding.choiceProductTypeOther.setTextColor(getResources().getColor(R.color.topic_color1));
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