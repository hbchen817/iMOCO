package com.laffey.smart.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 设备列表适配器
 */
public class AptDeviceList extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView status;
        private TextView room;
    }

    private Context mContext;
    private List<EDevice.deviceEntry> mDeviceList;

    // 构造
    public AptDeviceList(Context context) {
        super();
        this.mContext = context;
        this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
    }

    // 设置数据
    public void setData(List<EDevice.deviceEntry> deviceList) {
        this.mDeviceList = deviceList;
        RefreshData.refreshDeviceNumberData();
    }

    // 清除数据
    public void clearData() {
        this.mDeviceList.clear();
    }

    // 更新状态数据
    public void updateStateData(String iotId, String propertyName, String propertyValue, long timeStamp) {
        boolean isExist = false;
        EDevice.deviceEntry deviceEntry = null;
        if (this.mDeviceList.size() > 0) {
            for (EDevice.deviceEntry entry : this.mDeviceList) {
                if (entry.iotId.equalsIgnoreCase(iotId)) {
                    isExist = true;
                    deviceEntry = entry;
                    break;
                }
            }
        }
        if (!isExist) {
            return;
        }

        deviceEntry.processStateTime(this.mContext, propertyName, propertyValue, timeStamp);
        this.notifyDataSetChanged();
        RefreshData.refreshDeviceNumberData();
    }

    // 更新房间数据
    public void updateRoomData(String iotId) {
        if (this.mDeviceList.size() > 0) {
            for (EDevice.deviceEntry entry : this.mDeviceList) {
                if (entry.iotId.equalsIgnoreCase(iotId)) {
                    // 获取房间信息
                    EHomeSpace.roomEntry roomEntry = DeviceBuffer.getDeviceRoomInfo(iotId);
                    if (roomEntry != null) {
                        entry.roomId = roomEntry.roomId;
                        entry.roomName = roomEntry.name;
                        this.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
        RefreshData.refreshDeviceNumberData();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return this.mDeviceList == null ? 0 : this.mDeviceList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > this.mDeviceList.size() ? null : this.mDeviceList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            convertView = inflater.inflate(R.layout.list_device, null, true);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.deviceListImgIcon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.deviceListLblName);
            viewHolder.status = (TextView) convertView.findViewById(R.id.devicelistLblStatus);
            viewHolder.room = (TextView) convertView.findViewById(R.id.devicelistLblRoom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String image = this.mDeviceList.get(position).image;
        if (image != null && image.length() > 0)
            Glide.with(mContext).load(image).into(viewHolder.icon);
        else {
            Glide.with(mContext).load(ImageProvider.genProductIcon(this.mDeviceList.get(position).productKey))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(viewHolder.icon);
        }

        viewHolder.name.setText(this.mDeviceList.get(position).nickName);
        viewHolder.room.setText(this.mDeviceList.get(position).roomName);
        viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(this.mContext, this.mDeviceList.get(position).status)));

        // 如果离线显示为浅灰色
        if (this.mDeviceList.get(position).status == Constant.CONNECTION_STATUS_OFFLINE) {
            viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.status.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.room.setTextColor(Color.parseColor("#AAAAAA"));
        } else {
            viewHolder.name.setTextColor(Color.parseColor("#464645"));
            viewHolder.status.setTextColor(Color.parseColor("#464645"));
            viewHolder.room.setTextColor(Color.parseColor("#464645"));
            // 如果有属性状态则显示属性状态
            if (this.mDeviceList.get(position).stateTimes != null && this.mDeviceList.get(position).stateTimes.size() > 0
                    && !this.mDeviceList.get(position).productKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                // 只有一种状态的处理
                if (this.mDeviceList.get(position).stateTimes.size() == 1) {
                    viewHolder.status.setText(this.mDeviceList.get(position).stateTimes.get(0).time + " " + this.mDeviceList.get(position).stateTimes.get(0).value);
                }
                // 有多种状态的处理
                if (this.mDeviceList.get(position).stateTimes.size() >= 2) {
                    // 目前只显示前两种状态
                    String state = this.mDeviceList.get(position).stateTimes.get(0).time + " " + this.mDeviceList.get(position).stateTimes.get(0).value + "  /  " +
                            this.mDeviceList.get(position).stateTimes.get(1).time + " " + this.mDeviceList.get(position).stateTimes.get(1).value;
                    viewHolder.status.setText(state);
                }
            }
        }

        return convertView;
    }
}