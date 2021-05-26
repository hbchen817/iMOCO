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

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.ETSL;
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
        mContext = context;
        mDeviceList = new ArrayList<EDevice.deviceEntry>();
    }

    // 设置数据
    public void setData(List<EDevice.deviceEntry> deviceList) {
        mDeviceList = deviceList;
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
        if (mDeviceList.size() > 0) {
            for (EDevice.deviceEntry entry : mDeviceList) {
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

        deviceEntry.processStateTime(mContext, propertyName, propertyValue, timeStamp);
        notifyDataSetChanged();
        RefreshData.refreshDeviceNumberData();
    }

    // 更新房间数据
    public void updateRoomData(String iotId) {
        if (mDeviceList.size() > 0) {
            for (EDevice.deviceEntry entry : mDeviceList) {
                if (entry.iotId.equalsIgnoreCase(iotId)) {
                    // 获取房间信息
                    EHomeSpace.roomEntry roomEntry = DeviceBuffer.getDeviceRoomInfo(iotId);
                    if (roomEntry != null) {
                        entry.roomId = roomEntry.roomId;
                        entry.roomName = roomEntry.name;
                        notifyDataSetChanged();
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

        EDevice.deviceEntry deviceEntry = mDeviceList.get(position);
        String image = deviceEntry.image;
        if (image != null && image.length() > 0)
            Glide.with(mContext).load(image).into(viewHolder.icon);
        else {
            Glide.with(mContext).load(ImageProvider.genProductIcon(deviceEntry.productKey))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(viewHolder.icon);
        }

        viewHolder.name.setText(deviceEntry.nickName);
        viewHolder.room.setText(deviceEntry.roomName);
        viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(mContext, deviceEntry.status)));

        // 如果离线显示为浅灰色
        if (mDeviceList.get(position).status == Constant.CONNECTION_STATUS_OFFLINE) {
            viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.status.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.room.setTextColor(Color.parseColor("#AAAAAA"));
        } else {
            viewHolder.name.setTextColor(Color.parseColor("#464645"));
            viewHolder.status.setTextColor(Color.parseColor("#464645"));
            viewHolder.room.setTextColor(Color.parseColor("#464645"));
            // 如果有属性状态则显示属性状态
            if (deviceEntry.stateTimes != null && deviceEntry.stateTimes.size() > 0
                    && !deviceEntry.productKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                // 只有一种状态的处理
                if (deviceEntry.stateTimes.size() == 1) {
                    String txt = deviceEntry.stateTimes.get(0).time + " " + deviceEntry.stateTimes.get(0).value;
                    viewHolder.status.setText(txt);
                    if (CTSL.PK_ONEWAYSWITCH.equals(deviceEntry.productKey)) {
                        JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                        if (jsonObject != null) {
                            String name = jsonObject.getString(deviceEntry.stateTimes.get(0).name);
                            if (name != null) {
                                if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    txt = deviceEntry.stateTimes.get(0).time + " " + name + mContext.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    txt = deviceEntry.stateTimes.get(0).time + " " + name + mContext.getString(R.string.oneswitch_state_off);
                                }
                                viewHolder.status.setText(txt);
                            }
                        }
                    }
                }
                // 有多种状态的处理
                if (deviceEntry.stateTimes.size() >= 2) {
                    // 目前只显示前两种状态
                    String state = deviceEntry.stateTimes.get(0).time + " " + deviceEntry.stateTimes.get(0).value + "  /  " +
                            deviceEntry.stateTimes.get(1).time + " " + deviceEntry.stateTimes.get(1).value;
                    viewHolder.status.setText(state);
                    if (Constant.KEY_NICK_NAME_PK.contains(deviceEntry.productKey)) {
                        JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                        if (jsonObject != null) {
                            int switchPos1 = 0;
                            int switchPos2 = 0;
                            for (int i = 0; i < deviceEntry.stateTimes.size(); i++) {
                                ETSL.stateTimeEntry timeEntry = deviceEntry.stateTimes.get(i);
                                if (CTSL.FWS_P_PowerSwitch_1.equals(timeEntry.name)) {
                                    switchPos1 = i;
                                    continue;
                                } else if (CTSL.FWS_P_PowerSwitch_2.equals(timeEntry.name)) {
                                    switchPos2 = i;
                                    continue;
                                }
                            }

                            String name0 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos1).name);
                            String name1 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos2).name);
                            String state0 = null, state1 = null;
                            if (name0 != null && name1 != null) {
                                if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    state0 = deviceEntry.stateTimes.get(switchPos1).time + " " + name0 + mContext.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    state0 = deviceEntry.stateTimes.get(switchPos1).time + " " + name0 + mContext.getString(R.string.oneswitch_state_off);
                                }

                                if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    state1 = deviceEntry.stateTimes.get(switchPos2).time + " " + name1 + mContext.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    state1 = deviceEntry.stateTimes.get(switchPos2).time + " " + name1 + mContext.getString(R.string.oneswitch_state_off);
                                }
                                state = state0 + "  /  " + state1;
                                if (state0 != null && state1 != null)
                                    viewHolder.status.setText(state);
                            }
                        }
                    } else if (CTSL.PK_LIGHT.equals(deviceEntry.productKey)) {
                        // 调光调色面板
                        state = deviceEntry.stateTimes.get(0).time + " 亮度 " + deviceEntry.stateTimes.get(0).value + "%  /  " +
                                deviceEntry.stateTimes.get(1).time + " 色温 " + deviceEntry.stateTimes.get(1).value + "K";
                        viewHolder.status.setText(state);
                    }
                }
            }
        }

        return convertView;
    }
}