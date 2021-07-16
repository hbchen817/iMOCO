package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EWiFi;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: WiFi列表适配器
 */
public class AptWiFiList extends BaseAdapter {
    private class ViewHolder {
        private TextView name;
        private TextView description;
    }

    private Context mContext;
    private List<EWiFi.WiFiEntry> mWiFiList;

    // 构造
    public AptWiFiList(Context context, List<EWiFi.WiFiEntry> wifiList) {
        super();
        mContext = context;
        mWiFiList = wifiList;
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mWiFiList == null ? 0 : mWiFiList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mWiFiList.size() ? null : mWiFiList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_wifi, null, true);
            viewHolder.name = convertView.findViewById(R.id.wifiListLblName);
            viewHolder.description = convertView.findViewById(R.id.wifiListLblDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mWiFiList == null || mWiFiList.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }
        viewHolder.name.setText(mWiFiList.get(position).ssid);
        viewHolder.description.setText(String.format(this.mContext.getString(R.string.wifi_description), mWiFiList.get(position).level));
        return convertView;
    }
}