package com.rexense.smart.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import com.rexense.smart.R;
import com.rexense.smart.model.EWiFi;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: WiFi列表适配器
 */
public class AptWiFiList extends BaseAdapter {
	private class ViewHolder {
		private TextView name;
		private TextView description;
	}
	private Context mContext;
	private List<EWiFi.WiFiEntry> mWiFiList;

	// 构造
	public AptWiFiList(Context context, List<EWiFi.WiFiEntry> wifiList) {
		super();
		this.mContext = context;
		this.mWiFiList = wifiList;
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mWiFiList == null ? 0 : this.mWiFiList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mWiFiList.size() ? null : this.mWiFiList.get(arg0);
	}

	// 获取列表条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.list_wifi, null, true);
			viewHolder.name = (TextView) convertView.findViewById(R.id.wifiListLblName);
			viewHolder.description = (TextView) convertView.findViewById(R.id.wifiListLblDescription);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(this.mWiFiList.get(position).ssid);
		viewHolder.description.setText(String.format(this.mContext.getString(R.string.wifi_description), this.mWiFiList.get(position).level));
		return convertView;
	}
}