package com.laffey.smart.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.model.EScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-05 09:29
 * Description: 场景触发器设备列表适配器
 */
public class AptSceneTrigger extends BaseAdapter {
	private class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView state;
		private CheckBox select;
		private TextView noHas;
	}
	private Context mContext;
	private List<EScene.triggerEntry> mTriggerList;

	// 构造
	public AptSceneTrigger(Context context) {
		super();
		this.mContext = context;
		this.mTriggerList = new ArrayList<EScene.triggerEntry>();
	}

	// 设置数据
	public void setData(List<EScene.triggerEntry> triggerList) {
		this.mTriggerList = triggerList;
	}

	// 清除数据
	public void clearData() {
		this.mTriggerList.clear();
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mTriggerList == null ? 0 : this.mTriggerList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mTriggerList.size() ? null : this.mTriggerList.get(arg0);
	}

	// 获取网格条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(this.mContext);
		convertView = inflater.inflate(R.layout.list_trigger, null, true);
		viewHolder.icon = (ImageView) convertView.findViewById(R.id.triggerListImgIcon);
		viewHolder.name = (TextView) convertView.findViewById(R.id.triggerListLblName);
		viewHolder.state = (TextView) convertView.findViewById(R.id.triggerListLblState);
		viewHolder.select = (CheckBox) convertView.findViewById(R.id.triggerlistChkSelect);
		viewHolder.noHas = (TextView) convertView.findViewById(R.id.triggerlistLblNohas);
		convertView.setTag(viewHolder);
		viewHolder.icon.setBackgroundResource(ImageProvider.genProductIcon(this.mTriggerList.get(position).productKey));
		viewHolder.name.setText(this.mTriggerList.get(position).name);
		if(this.mTriggerList.get(position).state != null) {
			viewHolder.state.setText(this.mTriggerList.get(position).state.value);
		}
		if(this.mTriggerList.get(position).iotId.equals("")){
			// 无设备处理
			viewHolder.select.setVisibility(View.GONE);
			viewHolder.noHas.setVisibility(View.VISIBLE);
		} else {
			// 具体设备处理
			viewHolder.select.setVisibility(View.VISIBLE);
			viewHolder.noHas.setVisibility(View.GONE);
		}
		return convertView;
	}
}