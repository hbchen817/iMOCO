package com.rexense.imoco.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.model.EOTA;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.model.EUser;
import com.rexense.imoco.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 云数据解析器
 */
public class CloudDataParser {
    // 处理支持配网产品列表
    public static List<EProduct.configListEntry> processConfigProcductList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        List<EProduct.configListEntry> list = new ArrayList<EProduct.configListEntry>();
        String json = cloudData;
        if(cloudData.substring(0, 1).equals("[")){
            json = "{\"data\":" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                EProduct.configListEntry entry = new EProduct.configListEntry();
                entry.productKey = item.getString("productKey");
                entry.name = item.getString("name");
                entry.categoryId = item.getInteger("categoryId");
                entry.categoryKey = item.getString("categoryKey");
                entry.categoryName = item.getString("categoryName");
                entry.image = item.getString("image");
                entry.nodeType = item.getInteger("nodeType");
                entry.status = item.getInteger("status");
                list.add(entry);
            }
        }
        return list;
    }

    // 处理创建家结果
    public static String processCreateHomeResult(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return "";
        }

        if(cloudData.indexOf(":") < 0){
            return cloudData;
        }

        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("homeId");
    }

    // 处理家列表
    public static EHomeSpace.homeListEntry processHomeList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EHomeSpace.homeListEntry homeList = new EHomeSpace.homeListEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        homeList.total = jsonObject.getIntValue("total");
        homeList.pageNo = jsonObject.getIntValue("pageNo");
        homeList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                homeList.addData(item.getString("homeId"), item.getString("name"), item.getString("myRole"), item.getBoolean("currentHome"), item.getLong("createMillis"));
            }
        }
        return homeList;
    }

    // 处理家房间列表
    public static EHomeSpace.roomListEntry processHomeRoomList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EHomeSpace.roomListEntry roomList = new EHomeSpace.roomListEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        roomList.total = jsonObject.getIntValue("total");
        roomList.pageNo = jsonObject.getIntValue("pageNo");
        roomList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                roomList.addData(item.getString("roomId"), item.getString("name"), item.getIntValue("deviceCnt"), item.getLong("createMillis"));
            }
        }
        return roomList;
    }

    // 处理家设备列表
    public static EHomeSpace.homeDeviceListEntry processHomeDeviceList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EHomeSpace.homeDeviceListEntry deviceList = new EHomeSpace.homeDeviceListEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        deviceList.total = jsonObject.getIntValue("total");
        deviceList.pageNo = jsonObject.getIntValue("pageNo");
        deviceList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            JSONArray subDeviceList;
            JSONArray propertyList;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                EHomeSpace.deviceEntry device = new EHomeSpace.deviceEntry();
                device.iotId = item.getString("iotId");
                device.deviceName = item.getString("deviceName");
                device.homeId = item.getString("homeId");
                device.homeName = item.getString("homeName");
                device.roomId = item.getString("roomId");
                device.roomName = item.getString("roomName");
                device.productKey = item.getString("productKey");
                device.productName = item.getString("productName");
                device.productImage = item.getString("productImage");
                device.productModel = item.getString("productModel");
                device.categoryKey = item.getString("categoryKey");
                device.categoryImage = item.getString("categoryImage");
                device.panelPageRouterUrl = item.getString("panelPageRouterUrl");
                device.nickName = item.getString("nickName");
                if(device.nickName == null || device.nickName.length() == 0) {
                    device.nickName = device.productName;
                }
                device.netType = item.getString("netType");
                device.thingType = item.getString("thingType");
                device.status = item.getIntValue("status");
                device.nodeType = item.getString("nodeType");
                device.description = item.getString("description");

                subDeviceList = item.getJSONArray("subDeviceIotIdList");
                if(subDeviceList != null){
                    for(int x = 0; x < subDeviceList.size(); x++){
                        device.addSubDevice(subDeviceList.getString(x));
                    }
                }

                propertyList = item.getJSONArray("propertyList");
                if(propertyList != null){
                    JSONObject propertyItem;
                    for(int y = 0; y < propertyList.size(); y++){
                        EHomeSpace.devicePropertyEntry property = new EHomeSpace.devicePropertyEntry();
                        propertyItem = propertyList.getJSONObject(y);
                        property.identifier = propertyItem.getString("identifier");
                        property.name = propertyItem.getString("name");
                        property.value = propertyItem.getString("value");
                        property.dataType = propertyItem.getString("dataType");
                        property.imageUrl = propertyItem.getString("imageUrl");
                        device.addProperty(property);
                    }
                }

                deviceList.addData(device);
            }
        }
        return deviceList;
    }

    // 处理产品配网引导信息
    public static List<EProduct.configGuidanceEntry> processConfigGuidanceInformation(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        List<EProduct.configGuidanceEntry> list = new ArrayList<EProduct.configGuidanceEntry>();
        String json = cloudData;
        if(cloudData.substring(0, 1).equals("[")){
            json = "{\"data\":" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                EProduct.configGuidanceEntry entry = new EProduct.configGuidanceEntry();
                entry.id = item.getInteger("id");
                entry.helpTitle = item.getString("helpTitle");
                entry.helpIcon = item.getString("helpIcon");
                entry.helpCopywriting = item.getString("helpCopywriting");
                entry.dnGuideIcon = item.getString("dnGuideIcon");
                entry.dnCopywriting = item.getString("dnCopywriting");
                entry.buttonCopywriting = item.getString("buttonCopywriting");
                list.add(entry);
            }
        }
        return list;
    }

    // 处理网关子设备列表
    public static EUser.gatewaySubdeviceListEntry processGatewaySubdeviceList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EUser.gatewaySubdeviceListEntry subdeviceList = new EUser.gatewaySubdeviceListEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        subdeviceList.total = jsonObject.getIntValue("total");
        subdeviceList.pageNo = jsonObject.getIntValue("pageNo");
        subdeviceList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                EUser.deviceEntry subdevice = new EUser.deviceEntry();
                subdevice.iotId = item.getString("iotId");
                subdevice.deviceName = item.getString("deviceName");
                subdevice.productKey = item.getString("productKey");
                subdevice.nickName = item.getString("nickName");
                subdevice.image = item.getString("image");
                subdevice.status = item.getIntValue("status");
                subdeviceList.addSubdevice(subdevice);
            }
        }
        return subdeviceList;
    }

    // 处理用户绑定设备列表
    public static EUser.bindDeviceListEntry processUserDeviceList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EUser.bindDeviceListEntry deviceList = new EUser.bindDeviceListEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        deviceList.total = jsonObject.getIntValue("total");
        deviceList.pageNo = jsonObject.getIntValue("pageNo");
        deviceList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray data = jsonObject.getJSONArray("data");
        if(data != null){
            JSONObject item;
            for(int i = 0; i < data.size(); i++){
                item = data.getJSONObject(i);
                EUser.deviceEntry device = new EUser.deviceEntry();
                device.iotId = item.getString("iotId");
                device.deviceName = item.getString("deviceName");
                device.productKey = item.getString("productKey");
                device.nickName = item.getString("nickName");
                device.image = item.getString("productImage");
                device.status = item.getIntValue("status");
                device.nodeType = item.getString("nodeType");
                if(item.getDate("bindTime") != null) {
                    device.bindTime = Utility.timeStampToLongString(item.getLongValue("bindTime"));
                }
                deviceList.addDevice(device);
            }
        }
        return deviceList;
    }

    // 处理设备物的基本信息
    public static ETSL.thingBaseInforEntry processThingBaseInformation(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        ETSL.thingBaseInforEntry thingBaseInforEntry = new ETSL.thingBaseInforEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        thingBaseInforEntry.productKey = jsonObject.getString("productKey");
        thingBaseInforEntry.firmwareVersion = jsonObject.getString("firmwareVersion");

        return thingBaseInforEntry;
    }

    // 处理OTA固件信息
    public static EOTA.firmwareEntry processOTAFirmwareInformation(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EOTA.firmwareEntry firmwareEntry = new EOTA.firmwareEntry();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        firmwareEntry.currentVersion = jsonObject.getString("currentVersion");
        firmwareEntry.version = jsonObject.getString("version");
        firmwareEntry.currentTimestamp = jsonObject.getString("currentTimestamp");
        firmwareEntry.timestamp = jsonObject.getString("timestamp");
        firmwareEntry.size = jsonObject.getString("size");
        firmwareEntry.md5 = jsonObject.getString("md5");
        firmwareEntry.name = jsonObject.getString("name");
        firmwareEntry.url = jsonObject.getString("url");
        firmwareEntry.desc = jsonObject.getString("desc");

        return firmwareEntry;
    }

    // 处理属性时间线数据
    public static ETSL.propertyTimelineListEntry processPropertyTimelineData(String cloudData) {
        ETSL.propertyTimelineListEntry list = new ETSL.propertyTimelineListEntry();
        if(cloudData == null || cloudData.length() == 0){
            return list;
        }

        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray items = jsonObject.getJSONArray("items");
        if(items != null){
            JSONObject item;
            for(int i = 0; i < items.size(); i++){
                item = items.getJSONObject(i);
                if(list.minTimeStamp == 0) {
                    list.minTimeStamp = item.getLongValue("timestamp");
                } else {
                    if(item.getLongValue("timestamp") < list.minTimeStamp) {
                        list.minTimeStamp = item.getLongValue("timestamp");
                    }
                 }
                list.add(item.getString("property"), item.getString("data"), item.getLongValue("timestamp"));
            }
        }
        return list;
    }

    // 处理事件时间线数据
    public static ETSL.eventTimelineListEntry processEventTimelineData(String cloudData) {
        ETSL.eventTimelineListEntry list = new ETSL.eventTimelineListEntry();
        if(cloudData == null || cloudData.length() == 0){
            return list;
        }

        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray items = jsonObject.getJSONArray("items");
        if(items != null){
            JSONObject item;
            for(int i = 0; i < items.size(); i++){
                item = items.getJSONObject(i);
                if(list.minTimeStamp == 0) {
                    list.minTimeStamp = item.getLongValue("timestamp");
                } else {
                    if(item.getLongValue("timestamp") < list.minTimeStamp) {
                        list.minTimeStamp = item.getLongValue("timestamp");
                    }
                }
                list.add(item.getString("eventCode"), item.getString("eventBody"), item.getLongValue("timestamp"));
            }
        }
        return list;
    }

    // 处理场景列表
    public static EScene.sceneListEntry processSceneList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }

        EScene.sceneListEntry sceneList = new EScene.sceneListEntry ();
        String json = cloudData;
        if(!cloudData.substring(0, 1).equals("{")){
            json = "{" + json + "}";
        }
        JSONObject jsonObject = JSON.parseObject(json);
        sceneList.total = jsonObject.getIntValue("total");
        sceneList.pageNo = jsonObject.getIntValue("pageNo");
        sceneList.pageSize = jsonObject.getIntValue("pageSize");
        JSONArray scenes = jsonObject.getJSONArray("scenes");
        if(scenes != null){
            JSONObject item;
            for(int i = 0; i < scenes.size(); i++){
                item = scenes.getJSONObject(i);
                EScene.sceneListItemEntry itemEntry = new EScene.sceneListItemEntry();
                itemEntry.id = item.getString("id");
                itemEntry.status = item.getInteger("status");
                itemEntry.enable = item.getBoolean("enable");
                itemEntry.name = item.getString("name");
                itemEntry.description = item.getString("description");
                itemEntry.valid = item.getBoolean("valid");
                itemEntry.groupId = item.getString("groupId");
                sceneList.addData(itemEntry);
            }
        }
        return sceneList;
    }
}