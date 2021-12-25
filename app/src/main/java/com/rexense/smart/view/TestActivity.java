package com.rexense.smart.view;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityTestBinding;
import com.rexense.smart.model.EAPIChannel;
import com.rexense.smart.model.EDevice;
import com.rexense.smart.model.EHomeSpace;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.model.EUser;
import com.rexense.smart.presenter.AptDeviceGridAdapter;
import com.rexense.smart.presenter.AptDeviceListAdapter;
import com.rexense.smart.presenter.CloudDataParser;
import com.rexense.smart.presenter.DeviceBuffer;
import com.rexense.smart.presenter.HomeSpaceManager;
import com.rexense.smart.presenter.SystemParameter;
import com.rexense.smart.presenter.TSLHelper;
import com.rexense.smart.presenter.UserCenter;
import com.rexense.smart.sdk.APIChannel;
import com.rexense.smart.utility.GsonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private ActivityTestBinding mViewBinding;

    private final List<EDevice.deviceEntry> mList = new ArrayList<>();
    private AptDeviceGridAdapter mGridAdapter;
    private AptDeviceListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initAdapter();
        initData();
        initView();
    }

    private void initView() {
        mViewBinding.devSrl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    private void initAdapter() {
        /*mGridAdapter = new AptDeviceGridAdapter(this, R.layout.grid_device, mList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mViewBinding.devRl.setLayoutManager(layoutManager);
        mViewBinding.devRl.setAdapter(mGridAdapter);*/

        mListAdapter = new AptDeviceListAdapter(this, R.layout.list_device_2, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.devRl.setLayoutManager(layoutManager);
        mViewBinding.devRl.setAdapter(mListAdapter);
    }

    private void initData() {
        startGetDeviceList(1);
    }

    // 开始获取设备列表
    private void startGetDeviceList(int pageNo) {
        // 获取家设备列表
        HomeSpaceManager.getHomeDeviceList(this, SystemParameter.getInstance().getHomeId(), "", pageNo, 20,
                Constant.MSG_CALLBACK_GETHOMEDEVICELIST, new APIChannel.Callback() {
                    @Override
                    public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                        commitFailure(TestActivity.this, failEntry);
                        mViewBinding.devSrl.finishRefresh(false);
                    }

                    @Override
                    public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                        responseError(TestActivity.this, errorEntry);
                        mViewBinding.devSrl.finishRefresh(false);
                    }

                    @Override
                    public void onProcessData(String result) {
                        // 处理获取家设备列表数据
                        ViseLog.d("------------\n" + GsonUtil.toJson(JSONObject.parseObject(result)));
                        EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList(result);
                        DeviceBuffer.initProcess();
                        if (homeDeviceList != null && homeDeviceList.data != null) {
                            // 向缓存追加家列表数据
                            DeviceBuffer.addHomeDeviceList(homeDeviceList);
                            if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                                // 数据没有获取完则获取下一页数据
                                startGetDeviceList(homeDeviceList.pageNo + 1);
                            } else {
                                // 数据获取完则同步刷新设备列表数据
                                getDeviceList(1);
                            }
                        }
                    }
                });
    }

    // 数据获取完则同步刷新设备列表数据
    private void getDeviceList(int pageNo) {
        UserCenter.getDeviceList(this, pageNo, 20, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(TestActivity.this, failEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(TestActivity.this, errorEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取用户设备列表数据
                EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList(result);
                // ViseLog.d("设备列表 = \n" + GsonUtil.toJson(userBindDeviceList));
                if (userBindDeviceList != null && userBindDeviceList.data != null) {
                    // 向缓存追加用户绑定设备列表数据
                    DeviceBuffer.addUserBindDeviceList(userBindDeviceList);
                    if (userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        getDeviceList(userBindDeviceList.pageNo + 1);
                    } else {
                        // 数据获取完则同步刷新设备列表数据
                        // 开始主动获取设备属性
                        syncDeviceListData();
                        getDeviceProperty(0);
                    }
                } else {
                    // 开始主动获取设备属性
                    getDeviceProperty(0);
                }
            }
        });
    }

    private final List<ETSL.propertyEntry> mPropertyEntryList = new ArrayList<>();

    // 主动获取设备属性
    private void getDeviceProperty(int pos) {
        if (mList == null || mList.size() == 0) {
            mViewBinding.devSrl.finishRefresh(true);
            return;
        }
        TSLHelper.getProperty(TestActivity.this, mList.get(pos).iotId, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(TestActivity.this, failEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(TestActivity.this, errorEntry);
                mViewBinding.devSrl.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取属性回调
                if (pos == 0) {
                    mPropertyEntryList.clear();
                }
                ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                JSONObject items = JSON.parseObject(result);
                ViseLog.d("设备属性 = \n" + GsonUtil.toJson(items));
                if (items != null) {
                    TSLHelper.parseProperty(mList.get(pos).productKey, items, propertyEntry);
                    propertyEntry.iotId = mList.get(pos).iotId;
                    mPropertyEntryList.add(propertyEntry);
                    // 继续获取
                    if (pos < mList.size() - 1) {
                        getDeviceProperty(pos + 1);
                    } else {
                        for (ETSL.propertyEntry propertyEntry1 : mPropertyEntryList) {
                            boolean isExist = false;
                            EDevice.deviceEntry deviceEntry = null;
                            if (mList.size() > 0) {
                                for (EDevice.deviceEntry entry : mList) {
                                    if (entry.iotId.equalsIgnoreCase(propertyEntry1.iotId)) {
                                        isExist = true;
                                        deviceEntry = entry;
                                        break;
                                    }
                                }
                            }
                            if (!isExist) {
                                continue;
                            }
                            for (String name : propertyEntry1.properties.keySet()) {
                                if (propertyEntry1.properties.containsKey(name) && propertyEntry1.times.containsKey(name)) {
                                    deviceEntry.processStateTime(TestActivity.this, name, propertyEntry1.properties.get(name), propertyEntry1.times.get(name));
                                }
                            }
                        }
                        //mGridAdapter.notifyDataSetChanged();
                        mListAdapter.notifyDataSetChanged();
                        mViewBinding.devSrl.finishRefresh(true);
                    }
                } else {
                    mViewBinding.devSrl.finishRefresh(true);
                }
            }
        });
    }

    // 同步设备列表数据
    private void syncDeviceListData() {
        Map<String, EDevice.deviceEntry> all = DeviceBuffer.getAllDeviceInformation();
        // ViseLog.d(GsonUtil.toJson(all));
        int size = mList.size();
        mList.clear();
        if (all != null && all.size() > 0) {
            // 网关排前面
            for (EDevice.deviceEntry e : all.values()) {
                if (e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    deviceEntry.owned = e.owned;
                    deviceEntry.roomName = e.roomName;
                    deviceEntry.image = e.image;
                    mList.add(deviceEntry);
                }
            }
            for (EDevice.deviceEntry e : all.values()) {
                if (!e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    deviceEntry.owned = e.owned;
                    deviceEntry.roomName = e.roomName;
                    deviceEntry.image = e.image;
                    mList.add(deviceEntry);
                }
            }
        }
        if (size != mList.size()) {
            //mGridAdapter.notifyDataSetChanged();
            mListAdapter.notifyDataSetChanged();
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText("test");
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.icon_add_2);
        mViewBinding.includeToolbar.tvToolbarRight.setTextSize(30);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.includeToolbar.tvToolbarRight.setTypeface(iconfont);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        }
    }
}