package com.xiezhu.jzj.presenter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EBLE;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-06 09:29
 * Description: 支持配网产品列表适配器
 */
public class AptScanBLEDevice extends BaseAdapter {
    private class ViewHolder {
        TextView name;
        TextView address;
    }

    private Context mContext;
    // 定义蓝牙设备列表
    private List<EBLE.DeviceEntry> mDevices;

    // 构造
    public AptScanBLEDevice(Context context, List<EBLE.DeviceEntry> devices) {
        super();
        mContext = context;
        mDevices = devices;
    }

    // 添加设备
    public void addDevice(EBLE.DeviceEntry device) {
        // 检查设备是否已经存在
        boolean is_exist = false;
        if (mDevices != null && mDevices.size() > 0) {
            for (EBLE.DeviceEntry deviceEntry : mDevices) {
                if (device.getName().equals(deviceEntry.getName()) && device.getAddress().equals(deviceEntry.getAddress())) {
                    is_exist = true;
                    break;
                }
            }
        }

        if (!is_exist && mDevices != null) {
            mDevices.add(device);
        }

        notifyDataSetChanged();
    }

    public EBLE.DeviceEntry getDevice(int position) {
        return mDevices.get(position);
    }

    public void clear() {
        mDevices.clear();
    }

    @Override
    public int getCount() {
        return mDevices == null ? 0 : mDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mDevices == null ? null : mDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_scanble, null, true);
            viewHolder.name = convertView.findViewById(R.id.scanbleLblName);
            viewHolder.address = convertView.findViewById(R.id.scanbleLblAddress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mDevices == null || mDevices.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }
        viewHolder.name.setText(mDevices.get(position).getName());
        viewHolder.address.setText(this.mContext.getString(R.string.ble_address) + ":  " + mDevices.get(position).getAddress());
        return convertView;
    }
}

