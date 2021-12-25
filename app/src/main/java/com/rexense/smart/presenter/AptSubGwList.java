package com.rexense.smart.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rexense.smart.R;
import com.rexense.smart.event.RefreshData;
import com.rexense.smart.model.EDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 设备列表适配器
 */
public class AptSubGwList extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView status;
        private TextView room;
    }

    private Context mContext;
    private List<EDevice.subGwEntry> mDeviceList;
    private final Map<String, String> mStateMap = new HashMap<>();
    private final Map<String, String> mStatusMap = new HashMap<>();

    // 构造
    public AptSubGwList(Context context) {
        super();
        mContext = context;
        mDeviceList = new ArrayList<EDevice.subGwEntry>();
        mStateMap.put("0", context.getString(R.string.connection_status_unable));
        mStateMap.put("1", context.getString(R.string.activated));

        mStatusMap.put("1", context.getString(R.string.connection_status_online));
        mStatusMap.put("3", context.getString(R.string.connection_status_offline));
    }

    // 设置数据
    public void setData(List<EDevice.subGwEntry> deviceList) {
        mDeviceList = deviceList;
        RefreshData.refreshDeviceNumberData();
    }

    // 清除数据
    public void clearData() {
        this.mDeviceList.clear();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_device, null, true);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.deviceListImgIcon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.deviceListLblName);
            viewHolder.status = (TextView) convertView.findViewById(R.id.devicelistLblStatus);
            viewHolder.room = (TextView) convertView.findViewById(R.id.devicelistLblRoom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position >= mDeviceList.size()) {
            viewHolder.name.setText("--");
            viewHolder.room.setText("--");
            viewHolder.status.setText("--");
            return convertView;
        }

        EDevice.subGwEntry entry = mDeviceList.get(position);
        Glide.with(mContext).load(entry.getImage()).into(viewHolder.icon);

        viewHolder.name.setText(entry.getNickname());
        viewHolder.status.setText(mStateMap.get(entry.getState()));
        if (!"-1".equals(entry.getStatus())) {
            viewHolder.status.setText(mStatusMap.get(entry.getStatus()));
        }
        viewHolder.room.setVisibility(View.GONE);

        return convertView;
    }
}