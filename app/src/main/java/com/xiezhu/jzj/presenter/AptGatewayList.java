package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 设备列表适配器
 */
public class AptGatewayList extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView status;
    }

    private Context mContext;
    private List<EDevice.deviceEntry> mDeviceList;

    // 构造
    public AptGatewayList(Context context) {
        super();
        mContext = context;
        mDeviceList = new ArrayList<EDevice.deviceEntry>();
    }

    // 设置数据
    public void setData(List<EDevice.deviceEntry> deviceList) {
        mDeviceList = deviceList;
    }

    // 清除数据
    public void clearData() {
        mDeviceList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mDeviceList == null ? 0 : mDeviceList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mDeviceList.size() ? null : mDeviceList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (mDeviceList == null || mDeviceList.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_gateway, null, true);
            viewHolder.icon = convertView.findViewById(R.id.deviceListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.deviceListLblName);
            viewHolder.status = convertView.findViewById(R.id.devicelistLblStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(ImageProvider.genProductIcon(mDeviceList.get(position).productKey));
        viewHolder.name.setText(mDeviceList.get(position).nickName);
        viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(mContext, mDeviceList.get(position).status)));
        return convertView;
    }
}