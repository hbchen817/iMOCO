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
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-04 15:51
 * Description: 消息记录列表适配器
 */
public class AptMessageRecord extends BaseAdapter {
	private class ViewHolder {
		private TextView day;
		private ImageView icon;
		private TextView data;
	}
	private Context mContext;
	private List<ETSL.messageRecordEntry> mMessageRecords;
	private String mLastDay;

	// 构造
	public AptMessageRecord(Context context) {
		super();
		this.mContext = context;
		this.mMessageRecords = new ArrayList<ETSL.messageRecordEntry>();
		this.mLastDay = "";
	}

	// 追加数据
	public void addData(List<ETSL.messageRecordEntry> messageRecords) {
		if(messageRecords == null || messageRecords.size() == 0) {
			return;
		}
		for(ETSL.messageRecordEntry entry : messageRecords) {
			if(!entry.day.equals(this.mLastDay)) {
				// 额外添加日期处理
				String day = entry.day;
				if(day.equals(Utility.timeStampToYMDString(Utility.getCurrentTimeStamp()))) {
					day = this.mContext.getString(R.string.messagerecord_today) + " (" + day + ")";
				} else if(day.equals(Utility.timeStampToYMDString(Utility.getCurrentTimeStamp() - 24 * 60 * 60 *1000))) {
					day = this.mContext.getString(R.string.messagerecord_yesterday) + " (" + day + ")";
				}
				ETSL.messageRecordEntry dayEntry = new ETSL.messageRecordEntry("1", day, "");
				this.mMessageRecords.add(dayEntry);
				this.mLastDay = entry.day;
			}
			this.mMessageRecords.add(entry);
		}
		this.notifyDataSetChanged();
	}

	// 清除数据
	public void clearData() {
		this.mLastDay = "";
		this.mMessageRecords.clear();
		this.notifyDataSetChanged();
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mMessageRecords == null ? 0 : this.mMessageRecords.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mMessageRecords.size() ? null : this.mMessageRecords.get(arg0);
	}

	// 获取列表条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.list_messagerecord, null, true);
			viewHolder.day = (TextView) convertView.findViewById(R.id.messageRecordListLblDay);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.messageRecordListImgIcon);
			viewHolder.data = (TextView) convertView.findViewById(R.id.messageRecordListLblDescription);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if(this.mMessageRecords.get(position).type.equals("2")) {
			viewHolder.day.setVisibility(View.GONE);
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.data.setVisibility(View.VISIBLE);
			viewHolder.data.setText(this.mMessageRecords.get(position).description);
		} else {
			viewHolder.day.setVisibility(View.VISIBLE);
			viewHolder.icon.setVisibility(View.INVISIBLE);
			viewHolder.data.setVisibility(View.GONE);
			viewHolder.day.setText(this.mMessageRecords.get(position).day);
			viewHolder.day.setTextColor(Color.rgb(0x00, 0x00, 0xFF));
		}

		return convertView;
	}
}