package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CBLE;
import com.rexense.imoco.databinding.ActivityScanbleBinding;
import com.rexense.imoco.model.EBLE;
import com.rexense.imoco.contract.IBLE;
import com.rexense.imoco.presenter.AptScanBLEDevice;
import com.rexense.imoco.utility.BLEScanner;
import com.rexense.imoco.utility.Dialog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 扫描蓝牙设备
 */
public class ScanBLEActivity extends Activity {
    private ActivityScanbleBinding mViewBinding;

    private String mProductKey;
    private List<EBLE.DeviceEntry> mDevices = null;
    private AptScanBLEDevice mAScanBLEDevice = null;

    // 扫描蓝牙设备
    private void scanBLEDevice() {
        mViewBinding.scanBLERLHint.setVisibility(View.VISIBLE);
        mDevices.clear();
        mAScanBLEDevice.notifyDataSetChanged();
        // 如果支持蓝牙
        if (BLEScanner.isSupport()) {
            // 如果蓝牙启动成功
            if (BLEScanner.enabled()) {
                // 开始查找蓝牙设备
                if (BLEScanner.startDiscoveryDevice(CBLE.BLE_NAME_PREFIX, discoveryCallback)) {
                    mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.scanble_rescan);
                    return;
                }
            }
        }
        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.scanble_scan);
    }

    // 发现BLE设备回调
    private final IBLE.discoveryCallback discoveryCallback = new IBLE.discoveryCallback() {
        @Override
        public void returnFoundResult(EBLE.DeviceEntry deviceEntry) {
            mAScanBLEDevice.addDevice(deviceEntry);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityScanbleBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        if (!BLEScanner.isSupport()) {
            Dialog.confirm(this, R.string.dialog_title, getString(R.string.scanble_bleeror_hint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
        }

        mProductKey = getIntent().getStringExtra("productKey");

        // 初始化蓝牙扫描器
        BLEScanner.initProcess(this);

        // 设备列表处理
        mDevices = new ArrayList<EBLE.DeviceEntry>();
        mAScanBLEDevice = new AptScanBLEDevice(ScanBLEActivity.this, mDevices);
        mViewBinding.scanBLEDevice.setAdapter(mAScanBLEDevice);
        mViewBinding.scanBLEDevice.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取消BLE设备发现
                BLEScanner.cancelDiscoveryDevice();
                mViewBinding.scanBLERLHint.setVisibility(View.GONE);
                // 进入所选设备配网处理
                Intent intent = new Intent(ScanBLEActivity.this, ConfigureNetworkActivity.class);
                intent.putExtra("address", mDevices.get(position).getAddress());
                intent.putExtra("name", mDevices.get(position).getName());
                intent.putExtra("productKey", mProductKey);
                startActivity(intent);
            }
        });

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.scanble_scan);
        mViewBinding.includeToolbar.includeTitleLblTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBLEDevice();
            }
        });

        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEScanner.cancelDiscoveryDevice();
                finish();
            }
        });

        // 停止扫描处理
        mViewBinding.scanBleLblStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEScanner.cancelDiscoveryDevice();
                mViewBinding.scanBLERLHint.setVisibility(View.GONE);
            }
        });

        scanBLEDevice();

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

    @Override
    protected void onDestroy() {
        BLEScanner.endProcess();
        super.onDestroy();
    }
}