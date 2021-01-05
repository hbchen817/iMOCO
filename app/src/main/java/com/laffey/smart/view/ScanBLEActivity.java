package com.laffey.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.laffey.smart.R;
import com.laffey.smart.contract.CBLE;
import com.laffey.smart.model.EBLE;
import com.laffey.smart.contract.IBLE;
import com.laffey.smart.presenter.AptScanBLEDevice;
import com.laffey.smart.utility.BLEScanner;
import com.laffey.smart.utility.Dialog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 扫描蓝牙设备
 */
public class ScanBLEActivity extends Activity {
    private String mProductKey;
    private List<EBLE.DeviceEntry> mDevices = null;
    private AptScanBLEDevice mAScanBLEDevice = null;
    private TextView mLblTitle;
    private ListView mLstDevice;
    private RelativeLayout mScanHint;

    // 扫描蓝牙设备
    private void scanBLEDevice(){
        this.mScanHint.setVisibility(View.VISIBLE);
        this.mDevices.clear();
        this.mAScanBLEDevice.notifyDataSetChanged();
        // 如果支持蓝牙
        if(BLEScanner.isSupport()){
            // 如果蓝牙启动成功
            if(BLEScanner.enabled()){
                // 开始查找蓝牙设备
                if(BLEScanner.startDiscoveryDevice(CBLE.BLE_NAME_PREFIX, this.discoveryCallback)){
                    this.mLblTitle.setText(R.string.scanble_rescan);
                    return;
                }
            }
        }
        this.mLblTitle.setText(R.string.scanble_scan);
    }

    // 发现BLE设备回调
    private IBLE.discoveryCallback discoveryCallback = new IBLE.discoveryCallback(){
        @Override
        public void returnFoundResult(EBLE.DeviceEntry deviceEntry) {
            mAScanBLEDevice.addDevice(deviceEntry);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanble);

        if(!BLEScanner.isSupport()) {
            Dialog.confirm(this, R.string.dialog_title, getString(R.string.scanble_bleeror_hint), R.drawable.dialog_fail, R.string.dialog_confirm, true);
        }

        this.mProductKey = getIntent().getStringExtra("productKey");

        // 初始化蓝牙扫描器
        BLEScanner.initProcess(this);

        // 设备列表处理
        this.mLstDevice = (ListView)findViewById(R.id.scanBLEDevice);
        this.mDevices = new ArrayList<EBLE.DeviceEntry>();
        this.mAScanBLEDevice = new AptScanBLEDevice(ScanBLEActivity.this, this.mDevices);
        this.mLstDevice.setAdapter(mAScanBLEDevice);
        this.mLstDevice.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取消BLE设备发现
                BLEScanner.cancelDiscoveryDevice();
                mScanHint.setVisibility(View.GONE);
                // 进入所选设备配网处理
                Intent intent = new Intent(ScanBLEActivity.this, ConfigureNetworkActivity.class);
                intent.putExtra("address", mDevices.get(position).getAddress());
                intent.putExtra("name", mDevices.get(position).getName());
                intent.putExtra("productKey", mProductKey);
                startActivity(intent);
            }
        });

        this.mLblTitle = (TextView)findViewById(R.id.includeTitleLblTitle);
        this.mLblTitle.setText(R.string.scanble_scan);
        this.mLblTitle.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                scanBLEDevice();
            }
        });

        ImageView back = (ImageView)findViewById(R.id.includeTitleImgBack);
        back.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BLEScanner.cancelDiscoveryDevice();
                finish();
            }
        });

        this.mScanHint = (RelativeLayout)findViewById(R.id.scanBLERLHint);
        // 停止扫描处理
        TextView stopScan = (TextView)findViewById(R.id.scanBleLblStop);
        stopScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEScanner.cancelDiscoveryDevice();
                mScanHint.setVisibility(View.GONE);
            }
        });

        this.scanBLEDevice();

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