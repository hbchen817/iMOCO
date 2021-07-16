package com.xiezhu.jzj.presenter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EProduct;

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
        mContext = context;
        mProductList = productList;
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mProductList == null ? 0 : mProductList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mProductList.size() ? null : mProductList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (mProductList == null || mProductList.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_configproduct, null, true);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.configProductImgIcon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.configProductName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mProductList.get(position).image != null && mProductList.get(position).image.length() > 0)
            Glide.with(mContext).load(mProductList.get(position).image).into(viewHolder.icon);
        else
            viewHolder.icon.setImageResource(ImageProvider.genProductIcon(mProductList.get(position).productKey));

        viewHolder.name.setText(mProductList.get(position).name);

        return convertView;
    }
}