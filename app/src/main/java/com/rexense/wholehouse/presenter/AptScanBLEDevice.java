package com.rexense.wholehouse.presenter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.model.EBLE;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-06 09:29
 * Description: 支持配网产品列表适配器
 */
public class AptScanBLEDevice extends BaseAdapter {
	private class ViewHolder {
		TextView name;
		TextView address;
	}

	private Context mContext;
	// 定义蓝牙设备列表
	private List<EBLE.DeviceEntry> mDevices;

	// 构造
	public AptScanBLEDevice(Context context, List<EBLE.DeviceEntry> devices) {
		super();
		this.mContext = context;
		this.mDevices = devices;
	}

	// 添加设备
	public void addDevice(EBLE.DeviceEntry device) {
		// 检查设备是否已经存在
		boolean is_exist = false;
		if(this.mDevices != null && this.mDevices.size() > 0){
			for(EBLE.DeviceEntry deviceEntry : this.mDevices){
				if(device.getName().equals(deviceEntry.getName()) && device.getAddress().equals(deviceEntry.getAddress())){
					is_exist = true;
					break;
				}
			}
		}

		if(!is_exist) {
			this.mDevices.add(device);
		}

		this.notifyDataSetChanged();
	}

	public EBLE.DeviceEntry getDevice(int position) {
		return this.mDevices.get(position);
	}

	public void clear() {
		this.mDevices.clear();
	}

	@Override
	public int getCount() {
		return this.mDevices == null ? 0 : this.mDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return this.mDevices == null ? null : this.mDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.list_scanble, null, true);
			viewHolder.name = (TextView) convertView.findViewById(R.id.scanbleLblName);
			viewHolder.address = (TextView) convertView.findViewById(R.id.scanbleLblAddress);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(this.mDevices.get(position).getName());
		viewHolder.address.setText(this.mContext.getString(R.string.ble_address) + ":  " + this.mDevices.get(position).getAddress());
		return convertView;
	}
}

