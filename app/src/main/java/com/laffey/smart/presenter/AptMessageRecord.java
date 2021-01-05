package com.laffey.smart.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-04 15:51
 * Description: 消息记录列表适配器
 */
public class AptMessageRecord extends BaseAdapter {
	private class ViewHolder {
		private TextView day;
		private TextView week;
		private TextView line;
		private TextView data;
	}
	private Context mContext;
	private List<ETSL.messageRecordEntry> mMessageRecords;
	private String mLastDay;

	// 获取星期
	private String getDayofWeek(String dateTime) {
		Calendar cal = Calendar.getInstance();
		if (dateTime.equals("")) {
			cal.setTime(new Date(System.currentTimeMillis()));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			Date date;
			try {
				date = sdf.parse(dateTime);
			} catch (ParseException e) {
				date = null;
				e.printStackTrace();
			}
			if (date != null) {
				cal.setTime(new Date(date.getTime()));
			}
		}
		int index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		String[] weeks = {
				this.mContext.getString(R.string.week_0_all),
				this.mContext.getString(R.string.week_1_all),
				this.mContext.getString(R.string.week_2_all),
				this.mContext.getString(R.string.week_3_all),
				this.mContext.getString(R.string.week_4_all),
				this.mContext.getString(R.string.week_5_all),
				this.mContext.getString(R.string.week_6_all)
		};

		return weeks[index];
	}

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
			// 额外添加日期处理
			if(!entry.day.equals(this.mLastDay)) {
				// 前一条结束处理
				if(this.mMessageRecords.size() >= 3 && !this.mMessageRecords.get(this.mMessageRecords.size() - 1).type.equals("1") &&
						!this.mMessageRecords.get(this.mMessageRecords.size() - 2).type.equals("1")){
					this.mMessageRecords.get(this.mMessageRecords.size() - 1).type = "4";
				}

				String day = entry.day;
				if(day.equals(Utility.timeStampToYMDString(Utility.getCurrentTimeStamp()))) {
					day = this.mContext.getString(R.string.messagerecord_today);
				} else if(day.equals(Utility.timeStampToYMDString(Utility.getCurrentTimeStamp() - 24 * 60 * 60 *1000))) {
					day = this.mContext.getString(R.string.messagerecord_yesterday);
				}
				ETSL.messageRecordEntry dayEntry = new ETSL.messageRecordEntry("1", day, "");
				this.mMessageRecords.add(dayEntry);
				this.mLastDay = entry.day;

				// 计算星期
				dayEntry.week = this.getDayofWeek(entry.day);
			}

			// 本条开始处理
			if(this.mMessageRecords.size() > 0 && this.mMessageRecords.get(this.mMessageRecords.size() - 1).type.equals("1")){
				entry.type = "2";
			}

			// 本条中间处理
			if(this.mMessageRecords.size() > 0 && Integer.parseInt(this.mMessageRecords.get(this.mMessageRecords.size() - 1).type) >= 2){
				entry.type = "3";
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
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(this.mContext);
		if(this.mMessageRecords.get(position).type.equals("1")) {
			convertView = inflater.inflate(R.layout.list_messagerecord_day, null, true);
			viewHolder.day = (TextView)convertView.findViewById(R.id.messageRecordListLblDay);
			viewHolder.week = (TextView)convertView.findViewById(R.id.messageRecordListLblWeek);
		} else {
			if(this.mMessageRecords.get(position).type.equals("2")){
				convertView = inflater.inflate(R.layout.list_messagerecord_begin, null, true);
				// 如果只有1条
				if((position - 1) >= 0 && position < (this.mMessageRecords.size() - 1)){
					if(this.mMessageRecords.get(position - 1).type.equals(this.mMessageRecords.get(position + 1).type)){
						viewHolder.line = convertView.findViewById(R.id.messageRecordListLblLine);
						viewHolder.line.setVisibility(View.GONE);
					}
				}
			} else if(this.mMessageRecords.get(position).type.equals("3")){
				convertView = inflater.inflate(R.layout.list_messagerecord_middle, null, true);
			} else {
				convertView = inflater.inflate(R.layout.list_messagerecord_end, null, true);
			}
			viewHolder.data = (TextView) convertView.findViewById(R.id.messageRecordListLblDescription);
		}
		convertView.setTag(viewHolder);

		if(this.mMessageRecords.get(position).type.equals("1")) {
			viewHolder.day.setText(this.mMessageRecords.get(position).day);
			viewHolder.week.setText(this.mMessageRecords.get(position).week);
		} else {
			viewHolder.data.setText(this.mMessageRecords.get(position).description);
		}

		return convertView;
	}
}