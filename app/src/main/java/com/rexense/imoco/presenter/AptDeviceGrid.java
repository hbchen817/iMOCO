package com.rexense.imoco.presenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 设备网格适配器
 */
public class AptDeviceGrid extends BaseAdapter {
	private class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView room;
		private TextView status;
	}
	private Context mContext;
	private List<EDevice.deviceEntry> mDeviceList;

	// 构造
	public AptDeviceGrid(Context context) {
		super();
		this.mContext = context;
		this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
	}

	// 设置数据
	public void setData(List<EDevice.deviceEntry> deviceList) {
		this.mDeviceList = deviceList;
	}

	// 清除数据
	public void clearData() {
		this.mDeviceList.clear();
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mDeviceList == null ? 0 : this.mDeviceList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mDeviceList.size() ? null : this.mDeviceList.get(arg0);
	}

	// 获取网格条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.grid_device, null, true);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.deviceGridImgIcon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.deviceGridLblName);
			viewHolder.room = (TextView) convertView.findViewById(R.id.deviceGridLblRoom);
			viewHolder.status = (TextView) convertView.findViewById(R.id.deviceGridLblStatus);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageResource(ImageProvider.genProductIcon(this.mDeviceList.get(position).productKey));
		viewHolder.name.setText(this.mDeviceList.get(position).nickName);
		viewHolder.room.setText(this.mDeviceList.get(position).roomName);
		viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(this.mContext, this.mDeviceList.get(position).status)));

		// 如果离线显示为浅灰色
		if(this.mDeviceList.get(position).status == Constant.CONNECTION_STATUS_OFFLINE) {
			viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
			viewHolder.room.setTextColor(Color.parseColor("#AAAAAA"));
			viewHolder.status.setTextColor(Color.parseColor("#AAAAAA"));
		} else {
			viewHolder.name.setTextColor(Color.parseColor("#464645"));
			viewHolder.room.setTextColor(Color.parseColor("#464645"));
			viewHolder.status.setTextColor(Color.parseColor("#464645"));
		}

		return convertView;
	}
}