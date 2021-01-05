package com.laffey.smart.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.laffey.smart.model.EWiFi;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-12 15:29
 * Description: WiFi助手
 */
public class WiFiHelper {
	private WifiManager mWifi;

	// 构造
	public WiFiHelper(Context context) {
		// 判断wifi是否开启
		this.mWifi = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if(!this.mWifi.isWifiEnabled()){
			if(this.mWifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
				this.mWifi.setWifiEnabled(true);
		}

		//如果API level是大于等于23(Android 6.0)时判断是否具有定位权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context,	Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
					Logger.d("Open location permission");
				}
				//请求定位权限
				ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
			}
		}
	}

	// 获取WiFi的SSID列表
	public List<EWiFi.WiFiEntry> getSSIDList() {
		List<ScanResult> scanResults = this.mWifi.getScanResults();
		if(scanResults != null && scanResults.size() > 0) {
			// 按信号强度进行降序排序
			Collections.sort(scanResults, new Comparator<ScanResult>() {
				@Override
				public int compare(ScanResult o1, ScanResult o2) {
					if(o1.level > o2.level) {
						return -1;
					} else if(o1.level == o2.level) {
						return 0;
					}
					return 1;
				}
			});
			List<EWiFi.WiFiEntry> list = new ArrayList<EWiFi.WiFiEntry>();
			StringBuilder sb = new StringBuilder();
			sb.append("The SSID of the wifi:");
			for(ScanResult sr : scanResults) {
				if(sr.SSID != null && sr.SSID.length() > 0) {
					if(!list.contains(sr)) {
						list.add(new EWiFi.WiFiEntry(sr.BSSID, sr.SSID, sr.level));
						sb.append(String.format("\r\n    BSSID[%s], SSID[%s], Level[%d]", sr.BSSID, sr.SSID, sr.level));
					}
				}
			}
			Logger.d(sb.toString());
			return list;
		} else {
			Logger.e("SSID list is null");
			return null;
		}
	}

	/**
	 * 获取当前连接的wifi名称
	 * @return ssid
	 */
	public String getWIFIName() {
		WifiInfo info = mWifi.getConnectionInfo();
		return info != null ? info.getSSID().replace("\"", "") : "";
	}
}
