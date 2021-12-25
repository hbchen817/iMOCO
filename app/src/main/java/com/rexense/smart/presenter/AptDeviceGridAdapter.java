package com.rexense.smart.presenter;

import android.app.Activity;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.smart.R;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.model.EDevice;
import com.rexense.smart.model.ETSL;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AptDeviceGridAdapter extends BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> {
    private Activity mActivity;
    private List<EDevice.deviceEntry> mList;

    public AptDeviceGridAdapter(Activity activity, int layoutResId, @Nullable List<EDevice.deviceEntry> data) {
        super(layoutResId, data);
        mActivity = activity;
        mList = data;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, EDevice.deviceEntry deviceEntry) {
        initItem(holder, deviceEntry);
    }

    private void initItem(BaseViewHolder holder, EDevice.deviceEntry deviceEntry) {
        holder.setText(R.id.deviceGridLblName, deviceEntry.nickName);
        holder.setText(R.id.deviceGridLblRoom, deviceEntry.roomName);
        holder.setVisible(R.id.deviceGridLblStatus, true);
        holder.setText(R.id.deviceGridLblStatus, String.format(mActivity.getString(R.string.devicelist_status),
                CodeMapper.processConnectionStatus(mActivity, deviceEntry.status)));
        ImageView icon = holder.getView(R.id.deviceGridImgIcon);

        if (deviceEntry.image != null && deviceEntry.image.length() > 0)
            Glide.with(mActivity).load(deviceEntry.image).into(icon);
        else {
            Glide.with(mActivity).load(ImageProvider.genProductIcon(deviceEntry.productKey))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(icon);
        }

        // 如果离线显示为浅灰色
        holder.setVisible(R.id.deviceGridLblState, false);
        holder.setVisible(R.id.deviceGridLblState1, false);
        holder.setVisible(R.id.deviceGridLblState2, false);
        holder.setVisible(R.id.deviceGridLblTime, false);

        if (deviceEntry.status == Constant.CONNECTION_STATUS_OFFLINE) {
            // 离线
            //viewHolder.name.setTextColor(Color.parseColor("#AAAAAA"));
            holder.setTextColor(R.id.deviceGridLblName, mActivity.getResources().getColor(R.color.white3));
            holder.setTextColor(R.id.deviceGridLblRoom, mActivity.getResources().getColor(R.color.white3));
            holder.setVisible(R.id.deviceGridLblStatus, true);
            holder.setTextColor(R.id.deviceGridLblStatus, mActivity.getResources().getColor(R.color.white3));
        } else {
            // 在线
            // viewHolder.name.setTextColor(Color.parseColor("#464645"));
            holder.setTextColor(R.id.deviceGridLblName, mActivity.getResources().getColor(R.color.normal_font_color));
            holder.setTextColor(R.id.deviceGridLblRoom, mActivity.getResources().getColor(R.color.normal_font_color));
            holder.setVisible(R.id.deviceGridLblStatus, true);
            holder.setTextColor(R.id.deviceGridLblStatus, mActivity.getResources().getColor(R.color.normal_font_color));
            // ViseLog.d("pk = " + mDeviceList.get(position).productKey + " , " + mDeviceList.get(position).stateTimes.size());
            // 如果有属性状态则显示属性状态
            // EDevice.deviceEntry devItem = deviceEntry;
            if (deviceEntry.stateTimes != null && deviceEntry.stateTimes.size() > 0
                    && !deviceEntry.productKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                int stateTimesCount = deviceEntry.stateTimes.size();
                holder.setVisible(R.id.deviceGridLblStatus, false);
                if (stateTimesCount == 1) {
                    // 只有一种状态的处理
                    holder.setVisible(R.id.deviceGridLblState, true);
                    holder.setVisible(R.id.deviceGridLblTime, true);
                    holder.setText(R.id.deviceGridLblState, deviceEntry.stateTimes.get(0).value);
                    if (CTSL.PK_ONEWAYSWITCH.equals(deviceEntry.productKey)) {
                        // 一路开关
                        JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                        if (jsonObject != null) {
                            String name = jsonObject.getString(CTSL.OWS_P_PowerSwitch_1);
                            if (name != null) {
                                if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    holder.setText(R.id.deviceGridLblState, name + mActivity.getString(R.string.oneswitch_state_on));
                                } else if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    holder.setText(R.id.deviceGridLblState, name + mActivity.getString(R.string.oneswitch_state_off));
                                }
                            }
                        }
                    } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(deviceEntry.productKey)) {
                        // 单调光面板
                        holder.setText(R.id.deviceGridLblState, "亮度：" + deviceEntry.stateTimes.get(0).value + "%");
                    }
                    holder.setText(R.id.deviceGridLblTime, deviceEntry.stateTimes.get(0).time);
                } else if (stateTimesCount >= 2) {
                    // 2种或更多状态的处理
                    holder.setVisible(R.id.deviceGridLblState1, true);
                    holder.setVisible(R.id.deviceGridLblState2, true);
                    if (stateTimesCount == 2) {
                        holder.setText(R.id.deviceGridLblState1, deviceEntry.stateTimes.get(0).value + " / " + deviceEntry.stateTimes.get(0).time);
                        holder.setText(R.id.deviceGridLblState2, deviceEntry.stateTimes.get(1).value + " / " + deviceEntry.stateTimes.get(1).time);
                        if (CTSL.PK_TWOWAYSWITCH.equals(deviceEntry.productKey)) {
                            JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                            if (jsonObject != null) {
                                String name1 = jsonObject.getString(deviceEntry.stateTimes.get(0).name);
                                if (name1 != null) {
                                    if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(0).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    } else if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(0).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    }
                                }
                                String name2 = jsonObject.getString(deviceEntry.stateTimes.get(1).name);
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(1).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(1).time;
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    } else if (deviceEntry.stateTimes.get(1).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(1).time;
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    }
                                }
                            }
                        } else if (CTSL.PK_LIGHT.equals(deviceEntry.productKey)) {
                            // 调光调色面板
                            String stateContent1 = "亮度 " + deviceEntry.stateTimes.get(0).value + "% / " + deviceEntry.stateTimes.get(0).time;
                            String stateContent2 = "色温 " + deviceEntry.stateTimes.get(1).value + "K / " + deviceEntry.stateTimes.get(1).time;
                            holder.setText(R.id.deviceGridLblState1, stateContent1);
                            holder.setText(R.id.deviceGridLblState2, stateContent2);
                        }
                    } else if (stateTimesCount == 3) {
                        holder.setText(R.id.deviceGridLblState1, deviceEntry.stateTimes.get(0).value);
                        holder.setText(R.id.deviceGridLblState2, deviceEntry.stateTimes.get(1).value + "");
                        if (CTSL.PK_THREE_KEY_SWITCH.equals(deviceEntry.productKey)) {
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
                                    if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    }
                                }
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    }
                                }
                            }
                        }
                    } else {
                        holder.setText(R.id.deviceGridLblState1, deviceEntry.stateTimes.get(0).value);
                        holder.setText(R.id.deviceGridLblState2, deviceEntry.stateTimes.get(1).value + "");
                        if (CTSL.PK_FOURWAYSWITCH_2.equals(deviceEntry.productKey)) {
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
                                    if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name1 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos1).time;
                                        holder.setText(R.id.deviceGridLblState1, txt);
                                    }
                                }
                                if (name2 != null) {
                                    if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                        // 打开
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_on) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                        // 关闭
                                        String txt = name2 + mActivity.getString(R.string.oneswitch_state_off) +
                                                " / " + deviceEntry.stateTimes.get(switchPos2).time + "";
                                        holder.setText(R.id.deviceGridLblState2, txt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void refreshDeviceProperty(List<ETSL.propertyEntry> list) {
        for (ETSL.propertyEntry propertyEntry : list) {
            boolean isExist = false;
            EDevice.deviceEntry deviceEntry = null;
            if (mList.size() > 0) {
                for (EDevice.deviceEntry entry : mList) {
                    if (entry.iotId.equalsIgnoreCase(propertyEntry.iotId)) {
                        isExist = true;
                        deviceEntry = entry;
                        break;
                    }
                }
            }
            if (!isExist) {
                continue;
            }
            for (String name : propertyEntry.properties.keySet()) {
                if (propertyEntry.properties.containsKey(name) && propertyEntry.times.containsKey(name)) {
                    deviceEntry.processStateTime(mActivity, name, propertyEntry.properties.get(name), propertyEntry.times.get(name));
                }
            }
        }
        notifyDataSetChanged();
    }

    // 更新状态数据
    public void refreshDeviceProperty(String iotId, String propertyName, String propertyValue, long timeStamp) {
        boolean isExist = false;
        EDevice.deviceEntry deviceEntry = null;
        if (mList.size() > 0) {
            for (EDevice.deviceEntry entry : mList) {
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

        deviceEntry.processStateTime(mActivity, propertyName, propertyValue, timeStamp);
        notifyDataSetChanged();
    }
}
