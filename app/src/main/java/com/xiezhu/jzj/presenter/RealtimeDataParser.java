package com.xiezhu.jzj.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.xiezhu.jzj.model.ERealtimeData;
import com.xiezhu.jzj.model.ETSL;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 实时数据解析器
 */
public class RealtimeDataParser {
    // 处理设备连接状态
    public static ERealtimeData.deviceConnectionStatusEntry processConnectStatus(String data) {
        if(data == null || data.length() == 0){
            return null;
        }

        ERealtimeData.deviceConnectionStatusEntry entry = new ERealtimeData.deviceConnectionStatusEntry();
        String json = data;
        if(!data.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        entry.ip = jsonObject.getString("ip");
        entry.iotId = jsonObject.getString("iotId");
        entry.productKey = jsonObject.getString("productKey");
        entry.deviceName = jsonObject.getString("deviceName");
        entry.statusLast = jsonObject.getIntValue("statusLast");
        if(jsonObject.getJSONObject("status") != null){
            entry.status = jsonObject.getJSONObject("status").getIntValue("value");
        }
        entry.ip = jsonObject.getString("ip");
        return entry;
    }

    // 处理子设备添加结果
    public static ERealtimeData.subDeviceJoinResultEntry proessSubDeviceJoinResult(String data) {
        if(data == null || data.length() == 0){
            return null;
        }

        ERealtimeData.subDeviceJoinResultEntry resultEntry = new ERealtimeData.subDeviceJoinResultEntry();
        String json = data;
        if(!data.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        resultEntry.status = jsonObject.getIntValue("status");
        resultEntry.subIotId = jsonObject.getString("subIotId");
        resultEntry.subProductKey = jsonObject.getString("subProductKey");
        resultEntry.subDeviceName = jsonObject.getString("subDeviceName");
        resultEntry.newGwIotId = jsonObject.getString("newGwIotId");
        resultEntry.newGwProductKey = jsonObject.getString("newGwProductKey");
        resultEntry.newGwDeviceName = jsonObject.getString("newGwDeviceName");
        return resultEntry;
    }

    // 处理属性
    public static ETSL.propertyEntry processProperty(String data) {
        if(data == null || data.length() == 0){
            return null;
        }

        ETSL.propertyEntry property = new ETSL.propertyEntry();
        String json = data;
        if(!data.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        property.iotId = jsonObject.getString("iotId");
        property.productKey = jsonObject.getString("productKey");
        if(jsonObject.getJSONObject("items") == null) {
            return null;
        }

        TSLHelper.parseProperty(property.productKey, jsonObject.getJSONObject("items"), property);
        return property;
    }
}
