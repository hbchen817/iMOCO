package com.rexense.wholehouse.presenter;

import android.annotation.SuppressLint;
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
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.model.EHomeSpace;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.utility.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 设备网格适配器
 */
public class AptDeviceGrid extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView room;

        private TextView status;

        private TextView state;
        private TextView time;

        private TextView state1;
        private TextView state2;
    }

    private Context mContext;
    private List<EDevice.deviceEntry> mDeviceList;

    // 构造
    public AptDeviceGrid(Context context) {
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
        notifyDataSetChanged();
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

    // 获取网格条目视图
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_device, null, true);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.deviceGridImgIcon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.deviceGridLblName);
            viewHolder.room = (TextView) convertView.findViewById(R.id.deviceGridLblRoom);
            viewHolder.status = (TextView) convertView.findViewById(R.id.deviceGridLblStatus);
            viewHolder.state = (TextView) convertView.findViewById(R.id.deviceGridLblState);
            viewHolder.time = (TextView) convertView.findViewById(R.id.deviceGridLblTime);
            viewHolder.state1 = (TextView) convertView.findViewById(R.id.deviceGridLblState1);
            viewHolder.state2 = (TextView) convertView.findViewById(R.id.deviceGridLblState2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position >= mDeviceList.size()) {
            viewHolder.name.setText("--");
            viewHolder.room.setText("--");
            viewHolder.status.setText("--");

            viewHolder.state.setVisibility(View.GONE);
            viewHolder.state1.setVisibility(View.GONE);
            viewHolder.state2.setVisibility(View.GONE);
            viewHolder.time.setVisibility(View.GONE);
            ToastUtils.showShortToast(mContext, R.string.server_is_busy_and_try_again_later);
            return convertView;
        }

        String image = mDeviceList.get(position).image;
        EDevice.deviceEntry deviceEntry = mDeviceList.get(position);
        if (image != null && image.length() > 0)
            Glide.with(mContext).load(image).into(viewHolder.icon);
        else {
            Glide.with(mContext).load(ImageProvider.genProductIcon(mDeviceList.get(position).productKey))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(viewHolder.icon);
        }
        viewHolder.name.setText(mDeviceList.get(position).nickName);
        viewHolder.room.setText(mDeviceList.get(position).roomName);
        viewHolder.status.setText(String.format(this.mContext.getString(R.string.devicelist_status), CodeMapper.processConnectionStatus(mContext, mDeviceList.get(position).status)));

        // 如果离线显示为浅灰色
        viewHolder.state.setVisibility(View.GONE);
        viewHolder.state1.setVisibility(View.GONE);
        viewHolder.state2.setVisibility(View.GONE);
        viewHolder.time.setVisibility(View.GONE);
        if (mDeviceList.get(position).status == Constant.CONNECTION_STATUS_OFFLINE) {
            //viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.white3));
            viewHolder.room.setTextColor(mContext.getResources().getColor(R.color.white3));
            viewHolder.status.setVisibility(View.VISIBLE);
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.white3));
        } else {
            // viewHolder.name.setTextColor(Color.parseColor("#464645"));
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.normal_font_color));
            viewHolder.room.setTextColor(mContext.getResources().getColor(R.color.normal_font_color));
            viewHolder.status.setVisibility(View.VISIBLE);
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.normal_font_color));
            // 如果有属性状态则显示属性状态
            if (mDeviceList.get(position).stateTimes != null && mDeviceList.get(position).stateTimes.size() > 0
                    && !mDeviceList.get(position).productKey.equals(CTSL.PK_GATEWAY_RG4100_RY)) {
                viewHolder.status.setVisibility(View.GONE);
                // 只有一种状态的处理
                if (mDeviceList.get(position).stateTimes.size() == 1) {
                    viewHolder.state.setVisibility(View.VISIBLE);
                    viewHolder.time.setVisibility(View.VISIBLE);
                    viewHolder.state.setText(mDeviceList.get(position).stateTimes.get(0).value);
                    if (CTSL.PK_ONEWAYSWITCH.equals(deviceEntry.productKey)
                            || CTSL.PK_ONEWAYSWITCH_HY.equals(deviceEntry.productKey)
                            || CTSL.PK_ONEWAYSWITCH_YQS.equals(deviceEntry.productKey)
                            || CTSL.PK_ONEWAYSWITCH_LF.equals(deviceEntry.productKey)
                            || CTSL.PK_ONEWAY_DANHUO_RY.equals(deviceEntry.productKey)) {
                        JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                        if (jsonObject != null) {
                            String name = jsonObject.getString(CTSL.OWS_P_PowerSwitch_1);
                            if (name != null) {
                                if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    viewHolder.state.setText(name + mContext.getString(R.string.oneswitch_state_on));
                                } else if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    viewHolder.state.setText(name + mContext.getString(R.string.oneswitch_state_off));
                                }
                            }
                        }
                    }
                    viewHolder.time.setText(mDeviceList.get(position).stateTimes.get(0).time);
                }
                // 有多种状态的处理
                // 目前只显示前两种状态
                EDevice.deviceEntry devItem = mDeviceList.get(position);
                int stateTimesCount = devItem.stateTimes.size();
                if (stateTimesCount >= 2) {
                    viewHolder.state1.setVisibility(View.VISIBLE);
                    viewHolder.state2.setVisibility(View.VISIBLE);
                    if (stateTimesCount == 2) {
                        viewHolder.state1.setText(devItem.stateTimes.get(0).value + " / " + devItem.stateTimes.get(0).time);
                        viewHolder.state2.setText(devItem.stateTimes.get(1).value + " / " + devItem.stateTimes.get(1).time);
                        if (CTSL.PK_TWOWAYSWITCH.equals(deviceEntry.productKey)
                                || CTSL.PK_TWOWAYSWITCH_HY.equals(deviceEntry.productKey)
                                || CTSL.PK_TWOWAYSWITCH_YQS.equals(deviceEntry.productKey)
                                || CTSL.PK_TWOWAYSWITCH_LF.equals(deviceEntry.productKey)
                                || CTSL.PK_TWOWAY_DANHUO_RY.equals(deviceEntry.productKey)) {
                            JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                            if (jsonObject != null) {
                                String name1 = jsonObject.getString(deviceEntry.stateTimes.get(0).name);
                                if (name1 != null) {
                                    if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + devItem.stateTimes.get(0).time;
                                        viewHolder.state1.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(0).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + devItem.stateTimes.get(0).time;
                                        viewHolder.state1.setText(txt);
                                    }
                                }
                                String name2 = jsonObject.getString(deviceEntry.stateTimes.get(1).name);
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(1).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + devItem.stateTimes.get(1).time;
                                        viewHolder.state2.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(1).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + devItem.stateTimes.get(1).time;
                                        viewHolder.state2.setText(txt);
                                    }
                                }
                            }
                        } else if (CTSL.PK_LIGHT.equals(deviceEntry.productKey)) {
                            // 调光调色面板
                            String stateContent1 = "亮度 " + devItem.stateTimes.get(0).value + "% / " + devItem.stateTimes.get(0).time;
                            String stateContent2 = "色温 " + devItem.stateTimes.get(1).value + "K / " + devItem.stateTimes.get(1).time;
                            viewHolder.state1.setText(stateContent1);
                            viewHolder.state2.setText(stateContent2);
                        }
                    } else if (stateTimesCount == 3) {
                        viewHolder.state1.setText(mDeviceList.get(position).stateTimes.get(0).value);
                        viewHolder.state2.setText(mDeviceList.get(position).stateTimes.get(1).value + "");
                        if (CTSL.PK_THREE_KEY_SWITCH.equals(deviceEntry.productKey) ||
                                CTSL.PK_THREEWAYSWITCH_HY.equals(deviceEntry.productKey) ||
                                CTSL.PK_THREEWAYSWITCH_YQS.equals(deviceEntry.productKey) ||
                                CTSL.PK_THREEWAYSWITCH_LF.equals(deviceEntry.productKey) ||
                                CTSL.PK_THREEWAY_DANHUO_RY.equals(deviceEntry.productKey)) {
                            JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                            if (jsonObject != null) {
                                int switchPos1 = 0;
                                int switchPos2 = 0;
                                for (int i = 0; i < deviceEntry.stateTimes.size(); i++) {
                                    ETSL.stateTimeEntry timeEntry = deviceEntry.stateTimes.get(i);
                                    if (CTSL.TWS_P3_PowerSwitch_1.equals(timeEntry.name)) {
                                        switchPos1 = i;
                                        continue;
                                    } else if (CTSL.TWS_P3_PowerSwitch_2.equals(timeEntry.name)) {
                                        switchPos2 = i;
                                        continue;
                                    }
                                }

                                String name1 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos1).name);
                                String name2 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos2).name);
                                if (name1 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        viewHolder.state1.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        viewHolder.state1.setText(txt);
                                    }
                                }
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        viewHolder.state2.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        viewHolder.state2.setText(txt);
                                    }
                                }
                            }
                        }
                    } else {
                        viewHolder.state1.setText(mDeviceList.get(position).stateTimes.get(0).value);
                        viewHolder.state2.setText(mDeviceList.get(position).stateTimes.get(1).value + "");
                        if (CTSL.PK_FOURWAYSWITCH_2.equals(deviceEntry.productKey) ||
                                CTSL.PK_FOURWAYSWITCH_LF.equals(deviceEntry.productKey)) {
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
                                String name1 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos1).name);
                                String name2 = jsonObject.getString(deviceEntry.stateTimes.get(switchPos2).name);
                                if (name1 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        viewHolder.state1.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        viewHolder.state1.setText(txt);
                                    }
                                }
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        viewHolder.state2.setText(txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mContext.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mContext.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        viewHolder.state2.setText(txt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return convertView;
    }
}