package com.rexense.wholehouse.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.model.ETSL;

import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-07 09:23
 * Description: 消息内容列表适配器
 */
public class AptContent extends BaseAdapter {
	private class ViewHolder {
		private TextView name;
	}
	private Context mContext;
	private List<ETSL.messageRecordContentEntry> mContentList;

	// 构造
	public AptContent(Context context, List<ETSL.messageRecordContentEntry> contentList) {
		super();
		this.mContext = context;
		this.mContentList = contentList;
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mContentList == null ? 0 : this.mContentList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mContentList.size() ? null : this.mContentList.get(arg0);
	}

	// 获取列表条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.list_content, null, true);
			viewHolder.name = (TextView) convertView.findViewById(R.id.contentListLblName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(this.mContentList.get(position).name);
		return convertView;
	}
}