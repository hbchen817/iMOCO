package com.rexense.imoco.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.rexense.imoco.model.EBLE;
import com.rexense.imoco.contract.IBLE;

/**
 * Creator: xieshaobing
 * creat time:  14:16
 * Description: 低功耗蓝牙扫描器(用于发现蓝牙设备)
 */
public class BLEScanner {
	private static Context mContext;
	// 定义蓝牙适配器
	private static BluetoothAdapter mBluetoothAdapter = null;
	// 定义发现设备回调
	private static IBLE.discoveryCallback mDiscoveryCallback = null;
	private static boolean mIsStartDiscovery = false;
	private static String mBLENamePrefix = "";
	private static boolean mIsEnabled = false;

	// 初始化
	public static void initProcess(Context context) {
		mContext = context;
		// 注册接受蓝牙ACTION_FOUND的广播接收器
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, filter);
	}

	// 是否支持低功耗蓝牙
	public static boolean isSupport(){
		// 检查当前手机是否支持BLE蓝牙
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			Logger.e("No support for BLE！");
			return false;
		} else {
			Logger.d("Support for BLE.");
			return true;
		}
	}

	// 启用蓝牙
	public static boolean enabled(){
		mIsEnabled = false;
		if(!mBluetoothAdapter.isEnabled()){
			boolean r = mBluetoothAdapter.enable();
			if (!r){
				Logger.e("BLE start failed！");
			}
			return r;
		} else {
			Logger.d("Successfully Started BLE.");
			mIsEnabled = true;
			return true;
		}
	}

	// 开始发现蓝牙设备(找到的设备通过回调返回)
	public static boolean startDiscoveryDevice(String bleNamePrefix, IBLE.discoveryCallback discoveryCallback){
		if(!mIsEnabled){
			Logger.e("BLE start failed and can not discovery device！");
			mIsStartDiscovery = false;
			return false;
		} else {
			mBLENamePrefix = bleNamePrefix;
			mDiscoveryCallback = discoveryCallback;
			if(mIsStartDiscovery) {
				mBluetoothAdapter.cancelDiscovery();
			}
			mIsStartDiscovery = true;
			boolean r = mBluetoothAdapter.startDiscovery();
			if(r){
				Logger.d("BLE started discovering device.");
			} else {
				Logger.e("BLE failed to start discovering device!");
				//todo 权限申请
				Toast.makeText(mContext, "无蓝牙权限，请在应用程序信息中为应用开通权限", Toast.LENGTH_SHORT).show();
			}
			return r;
		}
	}

	// 取消发现蓝牙设备
	public static boolean cancelDiscoveryDevice(){
		if(mIsStartDiscovery){
			mIsStartDiscovery = false;
			return mBluetoothAdapter.cancelDiscovery();
		} else {
			return true;
		}
	}

	// 结束处理
	public static void endProcess() {
		cancelDiscoveryDevice();
		mContext.unregisterReceiver(mReceiver);
	}

	// 接受蓝牙ACTION_FOUND的广播接收器
	private static final BroadcastReceiver mReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			// 当Discovery发现了一个设备
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				// 从Intent中获取发现的BluetoothDevice
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 回调通知找到的设备
				if(mDiscoveryCallback != null){
					if(device != null && device.getName() != null && device.getName().length() > 0 &&
						device.getAddress() != null && device.getAddress().length() > 0 &&
						(mBLENamePrefix.length() == 0 || (mBLENamePrefix.length() > 0 && device.getName().indexOf(mBLENamePrefix) >= 0))) {
						mDiscoveryCallback.returnFoundResult(new EBLE.DeviceEntry(device.getName(), device.getAddress()));
					}
				}
			}
		}
	};
}
