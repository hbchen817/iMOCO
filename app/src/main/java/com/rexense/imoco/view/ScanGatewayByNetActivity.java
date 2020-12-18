package com.rexense.imoco.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EConfigureNetwork;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.ConfigureNetwork;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.rexense.imoco.widget.DialogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ScanGatewayByNetActivity extends BaseActivity {

    @BindView(R.id.includeTitleLblTitle)
    TextView includeTitleLblTitle;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.scanBleLblStop)
    TextView mStopView;
    @BindView(R.id.scanBLERLHint)
    RelativeLayout mHintView;
    @BindView(R.id.scanBLELl)
    LinearLayout mScanView;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private HashMap<String, DeviceInfo> mDeviceMap = new HashMap<>();
    private ProcessDataHandler mHandler;
    private ResponseErrorHandler mResponseErrorHandler;
    private Disposable mDisposable;
    private ProgressDialog mProgressDialog;
    private String mBindName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_gateway_net);
        ButterKnife.bind(this);
        initView();
        discovery();

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

    private void initView() {
        includeTitleLblTitle.setText(R.string.search_gate_way_device);
        mHandler = new ProcessDataHandler(this);
        mResponseErrorHandler = new ResponseErrorHandler(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                showConfirmDialog(index);
            }
        });
    }

    private void showConfirmDialog(int index) {
        DialogUtils.showEnsureDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 取消设备发现
                stopDiscovery();
                mHintView.setVisibility(View.GONE);
                ConfigureNetwork network = new ConfigureNetwork(ScanGatewayByNetActivity.this);
                EConfigureNetwork.bindDeviceParameterEntry parameter = new EConfigureNetwork.bindDeviceParameterEntry();
                parameter.homeId = SystemParameter.getInstance().getHomeId();
                ItemGateway gateway = (ItemGateway) mList.get(index);
                parameter.productKey = CTSL.PK_GATEWAY_RG4100;
                parameter.deviceName = gateway.getName();
                parameter.token = mDeviceMap.get(gateway.getName()).token;
                mBindName = gateway.getName();
                network.bindDevice(parameter, null, mResponseErrorHandler, mHandler);
                mProgressDialog = ProgressDialog.show(ScanGatewayByNetActivity.this, getString(R.string.gateway_bind_title), getString(R.string.gateway_bind_progress_hint), true);
                Observable.timer(15000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable disposable) {
                                mDisposable = disposable;
                            }

                            @Override
                            public void onNext(@NonNull Long number) {
                                mProgressDialog.dismiss();
                                Dialog.confirm(ScanGatewayByNetActivity.this, R.string.dialog_title, getString(R.string.confignetwork_timeout), R.drawable.dialog_fail, R.string.dialog_confirm, false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                    mDisposable.dispose();
                                    mProgressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (mDisposable != null && !mDisposable.isDisposed()) {
                                    mDisposable.dispose();
                                    mProgressDialog.dismiss();
                                }
                            }
                        });
            }
        }, getString(R.string.gateway_bind_confirm), null);
    }

    /**
     * 发现设备
     */
    private void discovery() {
        mHintView.setVisibility(View.VISIBLE);
        EnumSet<DiscoveryType> enumSet = EnumSet.noneOf(DiscoveryType.class);
        enumSet.add(DiscoveryType.LOCAL_ONLINE_DEVICE);
        LocalDeviceMgr.getInstance().startDiscovery(this, enumSet, null, new IDeviceDiscoveryListener() {
            @Override
            public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                List<FilterDevice> filterDevices = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    DeviceInfo deviceInfo = list.get(i);
                    if (!deviceInfo.productKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                        continue;
                    }
                    FilterDevice filterDevice = new FilterDevice();
                    filterDevice.deviceName = deviceInfo.deviceName;
                    filterDevices.add(filterDevice);
                    mDeviceMap.put(deviceInfo.deviceName, deviceInfo);
                }
                filter(filterDevices);
            }
        });
    }

    private void stopDiscovery() {
        LocalDeviceMgr.getInstance().stopDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDiscovery();
    }

    @OnClick({R.id.includeTitleImgBack, R.id.scanBleLblStop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.includeTitleImgBack:
                finish();
                break;
            case R.id.scanBleLblStop:
                stopDiscovery();
                mHintView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 过滤未绑定设备
     *
     * @param list
     */
    private void filter(List<FilterDevice> list) {
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_FILTER;
        requestParameterEntry.version = "1.0.7";
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_FILTER_DEVICE;
        requestParameterEntry.parameters = new HashMap<>();
        requestParameterEntry.parameters.put("iotDevices", parseFilterListToJsonArray(list));
        //提交
        new APIChannel().commit(requestParameterEntry, null, null, mHandler);
    }

    private JSONArray parseFilterListToJsonArray(List<FilterDevice> list) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            FilterDevice device = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productKey", device.productKey);
            jsonObject.put("deviceName", device.deviceName);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, ScanGatewayByNetActivity.class);
        context.startActivity(intent);
    }

    private class FilterDevice {
        private String productKey = CTSL.PK_GATEWAY_RG4100;
        private String deviceName;
    }

    private static class ResponseErrorHandler extends Handler {
        final WeakReference<ScanGatewayByNetActivity> mWeakReference;

        public ResponseErrorHandler(ScanGatewayByNetActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ScanGatewayByNetActivity activity = mWeakReference.get();
            if (activity.mDisposable != null && !activity.mDisposable.isDisposed()) {
                activity.mDisposable.dispose();
            }
            activity.mProgressDialog.dismiss();
            EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
            Dialog.confirm(activity, R.string.dialog_title, responseErrorEntry.message, R.drawable.dialog_fail, R.string.dialog_confirm, false);
        }
    }

    private static class ProcessDataHandler extends Handler {
        final WeakReference<ScanGatewayByNetActivity> mWeakReference;

        public ProcessDataHandler(ScanGatewayByNetActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ScanGatewayByNetActivity activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_FILTER_DEVICE:
                    JSONArray items = JSON.parseArray((String) msg.obj);
                    int size = items.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject o = items.getJSONObject(i);
                        ItemGateway itemGateway = new ItemGateway();
                        itemGateway.setName(o.getString("deviceName"));
                        itemGateway.setMac(activity.mDeviceMap.get(o.getString("deviceName")).mac);
                        activity.mList.add(itemGateway);
                        activity.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constant.MSG_CALLBACK_BINDEVICE:
                    if (activity.mDisposable != null && !activity.mDisposable.isDisposed()) {
                        activity.mDisposable.dispose();
                    }
                    activity.mProgressDialog.dismiss();
                    BindSuccessActivity.start(activity, JSON.parseObject((String) msg.obj).getString("iotId"), activity.mBindName);
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
