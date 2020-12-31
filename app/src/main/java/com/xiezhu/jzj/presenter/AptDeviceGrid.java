package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.EHomeSpace;

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

		private TextView state;
		private TextView time;

		private TextView state1;
		private TextView state2;
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
		this.notifyDataSetChanged();
	}

	// 更新状态数据
	public void updateStateData(String iotId, String propertyName, String propertyValue, long timeStamp) {
		boolean isExist = false;
		EDevice.deviceEntry deviceEntry = null;
		if (this.mDeviceList.size() > 0) {
			for (EDevice.deviceEntry entry : this.mDeviceList) {
				if (entry.iotId.equalsIgnoreCase(iotId)) {
					isExist = true;
					deviceEntry = entry;
					break;
				}
			}
		}
		if (!isExist) {
			return;
		}

		deviceEntry.processStateTime(this.mContext, propertyName, propertyValue, timeStamp);
		this.notifyDataSetChanged();
	}

	// 更新房间数据
	public void updateRoomData(String iotId) {
		if (this.mDeviceList.size() > 0) {
			for (EDevice.deviceEntry entry : this.mDeviceList) {
				if (entry.iotId.equalsIgnoreCase(iotId)) {
					// 获取房间信息
					EHomeSpace.roomEntry roomEntry = DeviceBuffer.getDeviceRoomInfo(iotId);
					if(roomEntry != null){
						entry.roomId = roomEntry.roomId;
						entry.roomName = roomEntry.name;
						this.notifyDataSetChanged();
					}
					break;
				}
			}
		}
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
			viewHolder.state = (TextView) convertView.findViewById(R.id.deviceGridLblState);
			viewHolder.time = (TextView) convertView.findViewById(R.id.deviceGridLblTime);
			viewHolder.state1 = (TextView) convertView.findViewById(R.id.deviceGridLblState1);
			viewHolder.state2 = (TextView) convertView.findViewById(R.id.deviceGridLblState2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageResource(ImageProvider.genProductIcon(this.mDeviceList.get(position).productKey));
		viewHolder.name.setText(ProductHelper.replaceBrand(this.mDeviceList.get(position).nickName));
		viewHolder.room.setText(this.mDeviceList.get(position).roomName);
		viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(this.mContext, this.mDeviceList.get(position).status)));

		// 如果离线显示为浅灰色
		viewHolder.state.setVisibility(View.GONE);
		viewHolder.state1.setVisibility(View.GONE);
		viewHolder.state2.setVisibility(View.GONE);
		viewHolder.time.setVisibility(View.GONE);
		if(this.mDeviceList.get(position).status == Constant.CONNECTION_STATUS_OFFLINE) {
			viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
			viewHolder.room.setTextColor(Color.parseColor("#AAAAAA"));
			viewHolder.status.setVisibility(View.VISIBLE);
			viewHolder.status.setTextColor(Color.parseColor("#AAAAAA"));
		} else {
			viewHolder.name.setTextColor(Color.parseColor("#464645"));
			viewHolder.room.setTextColor(Color.parseColor("#464645"));
			viewHolder.status.setVisibility(View.VISIBLE);
			viewHolder.status.setTextColor(Color.parseColor("#464645"));
			// 如果有属性状态则显示属性状态
			if(this.mDeviceList.get(position).stateTimes != null && this.mDeviceList.get(position).stateTimes.size() > 0){
				viewHolder.status.setVisibility(View.GONE);
				// 只有一种状态的处理
				if(this.mDeviceList.get(position).stateTimes.size() == 1){
					viewHolder.state.setVisibility(View.VISIBLE);
					viewHolder.time.setVisibility(View.VISIBLE);
					viewHolder.state.setText(this.mDeviceList.get(position).stateTimes.get(0).value);
					viewHolder.time.setText(this.mDeviceList.get(position).stateTimes.get(0).time);
				}
				// 有多种状态的处理
				// 目前只显示前两种状态
				int stateTimesCount = this.mDeviceList.get(position).stateTimes.size();
				if (stateTimesCount >= 2) {
					viewHolder.state1.setVisibility(View.VISIBLE);
					viewHolder.state2.setVisibility(View.VISIBLE);
					if (stateTimesCount == 2) {
						viewHolder.state1.setText(this.mDeviceList.get(position).stateTimes.get(0).value + " / " + this.mDeviceList.get(position).stateTimes.get(0).time);
						viewHolder.state2.setText(this.mDeviceList.get(position).stateTimes.get(1).value + " / " + this.mDeviceList.get(position).stateTimes.get(1).time);
					} else if (stateTimesCount == 3) {
						viewHolder.state1.setText(this.mDeviceList.get(position).stateTimes.get(0).value + " / " + this.mDeviceList.get(position).stateTimes.get(1).value);
						viewHolder.state2.setText(this.mDeviceList.get(position).stateTimes.get(2).value);
					} else {
						viewHolder.state1.setText(this.mDeviceList.get(position).stateTimes.get(0).value + " / " + this.mDeviceList.get(position).stateTimes.get(1).value);
						viewHolder.state2.setText(this.mDeviceList.get(position).stateTimes.get(2).value + " / " + this.mDeviceList.get(position).stateTimes.get(3).value);
					}
				}
			}
		}

		return convertView;
	}
}