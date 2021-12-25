package com.rexense.smart.presenter;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.smart.R;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.event.RefreshData;
import com.rexense.smart.model.EDevice;
import com.rexense.smart.model.ETSL;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AptDeviceListAdapter extends BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> {
    private Activity mActivity;
    private List<EDevice.deviceEntry> mList;

    public AptDeviceListAdapter(Activity activity, int layoutResId, @Nullable List<EDevice.deviceEntry> data) {
        super(layoutResId, data);
        this.mActivity = activity;
        this.mList = data;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, EDevice.deviceEntry deviceEntry) {
        ImageView icon = holder.getView(R.id.deviceListImgIcon);
        String image = deviceEntry.image;
        if (image != null && image.length() > 0)
            Glide.with(mActivity).load(image).into(icon);
        else {
            Glide.with(mActivity).load(ImageProvider.genProductIcon(deviceEntry.productKey))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(icon);
        }
        // deviceEntry.status = Constant.CONNECTION_STATUS_ONLINE;

        holder.setText(R.id.deviceListLblName, deviceEntry.nickName);
        holder.setText(R.id.devicelistLblRoom, deviceEntry.roomName);
        holder.setText(R.id.devicelistLblStatus, String.format(mActivity.getString(R.string.devicelist_status),
                CodeMapper.processConnectionStatus(mActivity, deviceEntry.status)));

        // 如果离线显示为浅灰色
        if (deviceEntry.status == Constant.CONNECTION_STATUS_OFFLINE) {
            holder.setTextColor(R.id.deviceListLblName, Color.parseColor("#AAAAAA"));
            holder.setTextColor(R.id.devicelistLblStatus, Color.parseColor("#AAAAAA"));
            holder.setTextColor(R.id.devicelistLblRoom, Color.parseColor("#AAAAAA"));
        } else {
            holder.setTextColor(R.id.deviceListLblName, Color.parseColor("#464645"));
            holder.setTextColor(R.id.devicelistLblStatus, Color.parseColor("#464645"));
            holder.setTextColor(R.id.devicelistLblRoom, Color.parseColor("#464645"));
            // 如果有属性状态则显示属性状态
            if (deviceEntry.stateTimes != null && deviceEntry.stateTimes.size() > 0
                    && !deviceEntry.productKey.equals(CTSL.PK_GATEWAY_RG4100)) {
                // 只有一种状态的处理
                if (deviceEntry.stateTimes.size() == 1) {
                    String txt = deviceEntry.stateTimes.get(0).time + " " + deviceEntry.stateTimes.get(0).value;
                    holder.setText(R.id.devicelistLblStatus, txt);
                    if (CTSL.PK_ONEWAYSWITCH.equals(deviceEntry.productKey)) {
                        JSONObject jsonObject = DeviceBuffer.getExtendedInfo(deviceEntry.iotId);
                        if (jsonObject != null) {
                            String name = jsonObject.getString(deviceEntry.stateTimes.get(0).name);
                            if (name != null) {
                                if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    txt = deviceEntry.stateTimes.get(0).time + " " + name + mActivity.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(0).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    txt = deviceEntry.stateTimes.get(0).time + " " + name + mActivity.getString(R.string.oneswitch_state_off);
                                }
                                holder.setText(R.id.devicelistLblStatus, txt);
                            }
                        }
                    } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(deviceEntry.productKey)) {
                        // 单调光面板
                        txt = deviceEntry.stateTimes.get(0).time + " 亮度：" + deviceEntry.stateTimes.get(0).value + "%";
                        holder.setText(R.id.devicelistLblStatus, txt);
                    }
                }
                // 有多种状态的处理
                if (deviceEntry.stateTimes.size() >= 2) {
                    // 目前只显示前两种状态
                    String state = deviceEntry.stateTimes.get(0).time + " " + deviceEntry.stateTimes.get(0).value + "  /  " +
                            deviceEntry.stateTimes.get(1).time + " " + deviceEntry.stateTimes.get(1).value;
                    holder.setText(R.id.devicelistLblStatus, state);
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
                                if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    state0 = deviceEntry.stateTimes.get(switchPos1).time + " " + name0 + mActivity.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(switchPos1).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    state0 = deviceEntry.stateTimes.get(switchPos1).time + " " + name0 + mActivity.getString(R.string.oneswitch_state_off);
                                }

                                if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_on))) {
                                    // 打开
                                    state1 = deviceEntry.stateTimes.get(switchPos2).time + " " + name1 + mActivity.getString(R.string.oneswitch_state_on);
                                } else if (deviceEntry.stateTimes.get(switchPos2).value.contains(mActivity.getString(R.string.oneswitch_state_off))) {
                                    // 关闭
                                    state1 = deviceEntry.stateTimes.get(switchPos2).time + " " + name1 + mActivity.getString(R.string.oneswitch_state_off);
                                }
                                state = state0 + "  /  " + state1;
                                if (state0 != null && state1 != null)
                                    holder.setText(R.id.devicelistLblStatus, state);
                            }
                        }
                    } else if (CTSL.PK_LIGHT.equals(deviceEntry.productKey)) {
                        // 调光调色面板
                        state = deviceEntry.stateTimes.get(0).time + " 亮度 " + deviceEntry.stateTimes.get(0).value + "%  /  " +
                                deviceEntry.stateTimes.get(1).time + " 色温 " + deviceEntry.stateTimes.get(1).value + "K";
                        holder.setText(R.id.devicelistLblStatus, state);
                    } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(deviceEntry.productKey)) {
                        // 单调光面板
                        state = deviceEntry.stateTimes.get(0).time + " 亮度 " + deviceEntry.stateTimes.get(0).value + "%";
                        holder.setText(R.id.devicelistLblStatus, state);
                    }
                }
            }
        }
    }

    // 更新状态数据
    public void refreshDeviceProperty(List<ETSL.propertyEntry> propertyEntryList) {
        for (ETSL.propertyEntry propertyEntry : propertyEntryList) {
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
        RefreshData.refreshDeviceNumberData();
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
        RefreshData.refreshDeviceNumberData();
    }
}
