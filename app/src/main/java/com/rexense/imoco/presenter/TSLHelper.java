package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.sdk.TSL;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-23 10:29
 * Description: 设备物描述语言助手
 */
public class TSLHelper {
    private Context mContext;

    // 构造
    public TSLHelper(Context context) {
        this.mContext = context;
    }

    // 获取物的基本信息
    public void getBaseInformation(String iotId,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETTHINGBASEINFORMATION;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETTHINGBASEINFO;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取属性
    public void getProperty(String iotId,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETTSLPROPERTY;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETTSLPROPERTY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取属性时间线数据（消息记录）
    public void getPropertyTimelineData(String iotId, String property, long startTime, long endTime, int maxCount, String order,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETTSLPROPERTYTIMELINEDATA;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("identifier", property);
        requestParameterEntry.addParameter("start", startTime);
        requestParameterEntry.addParameter("end", endTime);
        requestParameterEntry.addParameter("limit", maxCount > 200 ? 200 : maxCount);
        requestParameterEntry.addParameter("order", order);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取事件时间线数据（消息记录）
    public void getEventTimelineData(String iotId, String event, int contentType, long startTime, long endTime, int maxCount, String order,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETTSLEVENTTIMELINEDATA;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("identifier", event);
        String type = contentType == Constant.CONTENTTYPE_EVENT_ALERT ? CTSL.EVENTTYPE_ALERT : (contentType == Constant.CONTENTTYPE_EVENT_INFO ? CTSL.EVENTTYPE_INFO : CTSL.EVENTTYPE_ERROR);
        requestParameterEntry.addParameter("eventType", type);
        requestParameterEntry.addParameter("start", startTime);
        requestParameterEntry.addParameter("end", endTime);
        requestParameterEntry.addParameter("limit", maxCount > 200 ? 200 : maxCount);
        requestParameterEntry.addParameter("order", order);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETTSLEVENTTIMELINEDATA;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 解析属性
    public static void parseProperty(String productKey, JSONObject propertyItems, ETSL.propertyEntry propertyEntry) {
        if(propertyItems == null)
        {
            return;
        }

        // 如果有对应的属性配置文件则进行解析
        if(CTSL.propertyProfile.containsKey(productKey)) {
            JSONObject item;
            for(String key : CTSL.propertyProfile.get(productKey).keySet()) {
                item = propertyItems.getJSONObject(key);
                if(item != null){
                    propertyEntry.addProperty(key, item.getString("value"));
                    propertyEntry.addTime(key, item.getLong("time"));
                }
            }
        }
    }

    // 设置属性
    public void setProperty(String iotId, String productKey, String[] keys, String[] values) {
        this.setProperty(iotId, productKey, keys, values, CTSL.ControlType.SDK);
    }

    // 设置属性
    public void setProperty(String iotId, String productKey, String[] keys, String[] values, CTSL.ControlType controlType) {
        // 如果属性没有配置则退出
        if(!CTSL.propertyProfile.containsKey(productKey)) {
            return;
        }

        // 生成设置属性条目JSON字符串
        String items = genSetPropertyItems(productKey, keys, values);

        if(CTSL.ControlType.APIChanel == controlType) {
            // 设置请求参数
            EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
            requestParameterEntry.path = Constant.API_PATH_SETTSLPROPERTY;
            requestParameterEntry.version = "1.0.2";
            requestParameterEntry.addParameter("iotId", iotId);
            requestParameterEntry.addParameter("items", JSON.parseObject(items));
            //提交
            new APIChannel().commit(requestParameterEntry, null, null, null);
        } else {
            if(!RealtimeDataReceiver.getIsConnected()) {
                Toast.makeText(this.mContext, this.mContext.getString(R.string.longconnection_disconnected), Toast.LENGTH_LONG).show();
                return;
            }

            String parameter = String.format("{\"iotId\":\"%s\",\"items\":%s}", iotId, items);
            TSL.setProperty(iotId, parameter);
        }
    }

    // 获取消息记录内容
    public List<ETSL.messageRecordContentEntry> getMessageRecordContent(String productKey) {
        List<ETSL.messageRecordContentEntry> list = new ArrayList<ETSL.messageRecordContentEntry>();
        switch (productKey) {
            case CTSL.PK_GATEWAY:
                // 网关处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.GW_P_ArmMode, this.mContext.getString(R.string.moregateway_armmode), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.GW_P_AlarmSoundID, this.mContext.getString(R.string.moregateway_alarmbellid), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.GW_P_AlarmSoundVolume, this.mContext.getString(R.string.moregateway_alarmvolume), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.GW_P_DoorBellSoundVolume, this.mContext.getString(R.string.moregateway_bellvolume), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.GW_P_DoorBellSoundID, this.mContext.getString(R.string.moregateway_bellmusicid), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_ONEWAYSWITCH:
                // 一路开关处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.OWS_P_PowerSwitch_1, this.mContext.getString(R.string.oneswitch_state), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_TWOWAYSWITCH:
                // 两路开关处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.TWS_P_PowerSwitch_1, this.mContext.getString(R.string.twoswitch_state_1), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.TWS_P_PowerSwitch_2, this.mContext.getString(R.string.twoswitch_state_2), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_DOORSENSOR:
                // 门磁传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_E_ProtectionAlarm, this.mContext.getString(R.string.sensorstate_protectionalarm), Constant.CONTENTTYPE_EVENT_ALERT));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_E_TamperAlarm, this.mContext.getString(R.string.sensorstate_tamperalarm), Constant.CONTENTTYPE_EVENT_INFO));
                list.add(new ETSL.messageRecordContentEntry(CTSL.DS_P_ContactState, this.mContext.getString(R.string.sensorstate_contactname), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_WATERSENSOR:
                // 水浸传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.WS_P_WaterSensorState, this.mContext.getString(R.string.sensorstate_watername), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_GASSENSOR:
                // 燃气传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.GS_P_GasSensorState, this.mContext.getString(R.string.sensorstate_gasname), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_SMOKESENSOR:
                // 烟雾传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_E_TamperAlarm, this.mContext.getString(R.string.sensorstate_tamperalarm), Constant.CONTENTTYPE_EVENT_INFO));
                list.add(new ETSL.messageRecordContentEntry(CTSL.SS_P_SmokeSensorState, this.mContext.getString(R.string.sensorstate_smokename), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_PIRSENSOR:
                // 人体热释传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_E_ProtectionAlarm, this.mContext.getString(R.string.sensorstate_protectionalarm), Constant.CONTENTTYPE_EVENT_ALERT));
                list.add(new ETSL.messageRecordContentEntry(CTSL.PIR_P_MotionAlarmState, this.mContext.getString(R.string.sensorstate_motionname), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_TEMHUMSENSOR:
                // 温湿度传感器处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.THS_P_CurrentTemperature, this.mContext.getString(R.string.detailsensor_temperature), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.THS_P_CurrentHumidity, this.mContext.getString(R.string.detailsensor_humidity), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            case CTSL.PK_REMOTECONTRILBUTTON:
                // 遥控按钮处理
                list.add(new ETSL.messageRecordContentEntry(CTSL.RCB_P_EmergencyAlarm, this.mContext.getString(R.string.sensorstate_buttonstate), Constant.CONTENTTYPE_PROPERTY));
                list.add(new ETSL.messageRecordContentEntry(CTSL.P_P_BatteryPercentage, this.mContext.getString(R.string.sensorstate_powername), Constant.CONTENTTYPE_PROPERTY));
                break;
            default:
                return null;
        }
        return list;
    }

    // 处理属性消息记录
    public List<ETSL.messageRecordEntry> processPropertyMessageRecord(String productKey, List<ETSL.propertyTimelineEntry> timelineEntries) {
        if(timelineEntries == null || timelineEntries.size() == 0) {
            return null;
        }

        List<ETSL.messageRecordEntry> list = new ArrayList<ETSL.messageRecordEntry>();
        for(ETSL.propertyTimelineEntry entry : timelineEntries) {
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this.mContext, productKey, entry.property, entry.data);
            if(stateEntry != null) {
                ETSL.messageRecordEntry messageRecordEntry = new ETSL.messageRecordEntry(
                        "2",
                        Utility.timeStampToYMDString(entry.timestamp),
                        Utility.timeStampToHMSString(entry.timestamp) + stateEntry.name +this.mContext.getString(R.string.messagerecord_is) + stateEntry.value);
                list.add(messageRecordEntry);
            }
        }
        return list;
    }

    // 处理事件消息记录
    public List<ETSL.messageRecordEntry> processEventMessageRecord(String productKey, List<ETSL.eventTimelineEntry> timelineEntries) {
        if(timelineEntries == null || timelineEntries.size() == 0) {
            return null;
        }

        List<ETSL.messageRecordEntry> list = new ArrayList<ETSL.messageRecordEntry>();
        for(ETSL.eventTimelineEntry entry : timelineEntries) {
            ETSL.eventEntry eventEntry = CodeMapper.processEvent(this.mContext, productKey, entry.event, entry.data);
            if(eventEntry != null) {
                ETSL.messageRecordEntry messageRecordEntry = new ETSL.messageRecordEntry(
                        "2",
                        Utility.timeStampToYMDString(entry.timestamp),
                        Utility.timeStampToHMSString(entry.timestamp) + this.mContext.getString(R.string.messagerecord_happen) + eventEntry.value);
                list.add(messageRecordEntry);
            }
        }
        return list;
    }

    // 生成设置属性条目JSON字符串
    private static String genSetPropertyItems(String productKey, String[] keys, String[] values) {
        // 获取有效属性及类型
        List<String> v_keys = new ArrayList<String>();
        List<String> v_values = new ArrayList<String>();
        List<CTSL.PTYPE> v_types = new ArrayList<CTSL.PTYPE>();
        for(int i = 0; i < keys.length; i++) {
            if(CTSL.propertyProfile.get(productKey).containsKey(keys[i])) {
                v_keys.add(keys[i]);
                v_values.add(values[i]);
                v_types.add(CTSL.propertyProfile.get(productKey).get(keys[i]));
            }
        }

        StringBuilder items = new StringBuilder();
        items.append("{");
        for(int x = 0; x < v_keys.size(); x++) {
            if(items.toString().length() > 1) {
                items.append(",");
            }
            items.append(String.format("\"%s\":", v_keys.get(x)));
            switch (v_types.get(x)) {
                case t_bool:
                case t_int32:
                case t_double:
                case t_enum:
                    items.append(v_values.get(x));
                    break;
                default:
                    items.append(String.format("\"%s\"", v_values.get(x)));
                    break;
            }
            items.append("}");
        }
        return items.toString();
    }
}
