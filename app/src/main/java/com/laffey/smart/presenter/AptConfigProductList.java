package com.laffey.smart.presenter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.laffey.smart.R;
import com.laffey.smart.model.EProduct;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-06 09:29
 * Description: 支持配网产品列表适配器
 */
public class AptConfigProductList extends BaseAdapter {
	private class ViewHolder {
		private ImageView icon;
		private TextView name;
	}
	private Context mContext;
	private List<EProduct.configListEntry> mProductList;

	// 构造
	public AptConfigProductList(Context context, List<EProduct.configListEntry> productList) {
		super();
		this.mContext = context;
        this.mProductList = productList;
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mProductList == null ? 0 : this.mProductList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mProductList.size() ? null : this.mProductList.get(arg0);
	}

	// 获取列表条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			convertView = inflater.inflate(R.layout.grid_configproduct, null, true);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.configProductImgIcon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.configProductName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (this.mProductList.get(position).image != null && this.mProductList.get(position).image.length() > 0)
			Glide.with(mContext).load(this.mProductList.get(position).image).into(viewHolder.icon);
		else {
			Glide.with(mContext).load(ImageProvider.genProductIcon(this.mProductList.get(position).productKey))
				.transition(new DrawableTransitionOptions().crossFade()).into(viewHolder.icon);
		}

		viewHolder.name.setText(this.mProductList.get(position).name);

		return convertView;
	}
}