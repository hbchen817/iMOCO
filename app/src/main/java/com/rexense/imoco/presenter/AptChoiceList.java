package com.rexense.imoco.presenter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.model.EChoice;


/**
 * Creator: xieshaobing
 * creat time: 2020-06-11 09:29
 * Description: 通用选择列表适配器
 */
public class AptChoiceList extends BaseAdapter {
	private class ViewHolder {
		private TextView name;
		private CheckBox chkSelect;
		private ImageView imgSelect;
	}
	private Context mContext;
	private List<EChoice.itemEntry> mItems;
	private boolean mIsMultipleSelect;

	// 构造
	public AptChoiceList(Context context, List<EChoice.itemEntry> items, boolean isMultipleSelect) {
		super();
		this.mContext = context;
		this.mItems = items;
		this.mIsMultipleSelect = isMultipleSelect;
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mItems == null ? 0 : this.mItems.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mItems.size() ? null : this.mItems.get(arg0);
	}

	// 获取列表条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(this.mContext);
		convertView = inflater.inflate(R.layout.list_choice, null, true);
		viewHolder.name = (TextView) convertView.findViewById(R.id.choiceLblName);
		viewHolder.chkSelect = (CheckBox)convertView.findViewById(R.id.choiceChbSelect);
		viewHolder.imgSelect = (ImageView) convertView.findViewById(R.id.choiceImgSelect);
		if(this.mIsMultipleSelect){
			viewHolder.chkSelect.setTag(position);
			viewHolder.imgSelect.setVisibility(View.GONE);
			viewHolder.chkSelect.setVisibility(View.VISIBLE);
		} else {
			viewHolder.chkSelect.setVisibility(View.GONE);
			if(this.mItems.get(position).isSelected){
				viewHolder.imgSelect.setVisibility(View.VISIBLE);
			} else {
				viewHolder.imgSelect.setVisibility(View.GONE);
			}
		}
		convertView.setTag(viewHolder);
		viewHolder.name.setText(this.mItems.get(position).name);
		if(this.mIsMultipleSelect){
			viewHolder.chkSelect.setChecked(this.mItems.get(position).isSelected);
			viewHolder.chkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					final int index = (Integer)buttonView.getTag();
					mItems.get(index).isSelected = isChecked;
				}
			});
		}
		return convertView;
	}
}