package com.laffey.smart.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EOTA;
import com.laffey.smart.model.EProduct;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemMsgCenter;
import com.laffey.smart.model.ItemSceneLog;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.utility.TimeUtils;
import com.laffey.smart.utility.Utility;

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
                Log.i("lzm", "json =" + item.toJSONString());
                entry.name = ProductHelper.replaceBrand(entry.name);
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
                device.productName = ProductHelper.replaceBrand(device.productName);
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
                if(item.getString("strategy") != null && !item.getString("strategy").equalsIgnoreCase("smart config") && !item.getString("strategy").equalsIgnoreCase("zigbee")){
                    continue;
                }
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
                subdevice.deviceName = ProductHelper.replaceBrand(subdevice.deviceName);
                subdevice.productKey = item.getString("productKey");
                subdevice.nickName = item.getString("nickName");
                subdevice.nickName = ProductHelper.replaceBrand(subdevice.nickName);
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
                device.deviceName = ProductHelper.replaceBrand(device.deviceName);
                device.productKey = item.getString("productKey");
                device.productName = item.getString("productName");
                device.productName = ProductHelper.replaceBrand(device.productName);
                device.nickName = item.getString("nickName");
                if(device.nickName == null || device.nickName.length() == 0){
                    device.nickName = device.productName;
                }
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

    // 处理创建场景结果
    public static String processCreateSceneResult(String cloudData) {
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
        return jsonObject.getString("sceneId");
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
                itemEntry.enable = item.getBoolean("enable");
                itemEntry.name = item.getString("name");
                itemEntry.description = item.getString("description");
                itemEntry.valid = item.getBoolean("valid");
                itemEntry.catalogId = item.getString("catalogId");
                sceneList.addData(itemEntry);
            }
        }
        return sceneList;
    }

    // 处理删除场景结果
    public static String processDeleteSceneResult(String cloudData) {
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
        return jsonObject.getString("sceneId");
    }

    // 处理消息中心列表
    public static List<Visitable> processMsgCenterList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }
        List<Visitable> msgList = new ArrayList<>();
        JSONObject dataJson = JSONObject.parseObject(cloudData);
        JSONArray dataArr = dataJson.getJSONArray("data");
        if (dataArr!=null){
            for (int i=0;i<dataArr.size();i++){
                JSONObject msgJson = dataArr.getJSONObject(i);
                ItemMsgCenter itemMsgCenter = new ItemMsgCenter();
                String body = msgJson.getString("body");
                if(body!=null) {
                    body = ProductHelper.replaceBrand(body);
                    itemMsgCenter.setContent(body);
                }
                itemMsgCenter.setTime(TimeUtils.getYmdhms(msgJson.getLong("gmtModified")));

                JSONObject extData = msgJson.getJSONObject("extData");
                if (extData!=null){
                    String nickName = extData.getString("nickName");
                    if (nickName!=null){
                        nickName = ProductHelper.replaceBrand(nickName);
                        itemMsgCenter.setTitle(nickName);
                        itemMsgCenter.setContent(body.replace(nickName, ""));
                    }else {
                        String productName = extData.getString("productName");
                        if(productName!=null) {
                            productName = ProductHelper.replaceBrand(productName);
                            itemMsgCenter.setTitle(productName);
                            itemMsgCenter.setContent(body.replace(productName, ""));
                        }
                    }
                    itemMsgCenter.setProductKey(extData.getString("productKey"));
                }
                msgList.add(itemMsgCenter);
            }
        }
        return msgList;
    }

    // 处理消息中心共享设备消息列表
    public static List<Visitable> processShareDeviceNoticeList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }
        List<Visitable> msgList = new ArrayList<>();
        JSONObject dataJson = JSONObject.parseObject(cloudData);
        JSONArray dataArr = dataJson.getJSONArray("data");
        if (dataArr!=null){
            for (int i=0;i<dataArr.size();i++){
                JSONObject msgJson = dataArr.getJSONObject(i);
                ItemMsgCenter itemMsgCenter = new ItemMsgCenter();
                String description = msgJson.getString("description");
                if(description != null){
                    itemMsgCenter.setContent(ProductHelper.replaceBrand(description));
                }
                int status = msgJson.getInteger("status");//-1: 初始化0：同意1：拒绝2：取消3：过期4：抢占5：删除6：发起者已解绑99：异常
                int isReceiver = msgJson.getInteger("isReceiver");//当前用户是否是共享设备接收者 0否1是
                itemMsgCenter.setStatus(status);
                itemMsgCenter.setShowBtnView(status==-1&&isReceiver==1);
                itemMsgCenter.setTime(TimeUtils.getYmdhms(msgJson.getLong("gmtModified")));

                String productName = msgJson.getString("productName");
                productName = ProductHelper.replaceBrand(productName);
                itemMsgCenter.setTitle(productName);
                itemMsgCenter.setProductImg(msgJson.getString("productImage"));

                itemMsgCenter.setRecordId(msgJson.getString("recordId"));
                itemMsgCenter.setBatchId(msgJson.getString("batchId"));
                msgList.add(itemMsgCenter);
            }
        }
        return msgList;
    }

    // 场景日志列表
    public static List<Visitable> processSceneLogList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }
        List<Visitable> logList = new ArrayList<>();
        JSONObject dataJson = JSONObject.parseObject(cloudData);
        JSONArray dataArr = dataJson.getJSONArray("logs");
        if (dataArr!=null){
            for (int i=0;i<dataArr.size();i++){
                JSONObject logJson = dataArr.getJSONObject(i);
                ItemSceneLog itemSceneLog = new ItemSceneLog();
                itemSceneLog.setIcon(logJson.getString("icon"));
                itemSceneLog.setId(logJson.getString("id"));
                itemSceneLog.setLogName(logJson.getString("sceneName"));
                itemSceneLog.setLogTime(TimeUtils.getYmdhms(logJson.getLong("time")));
                itemSceneLog.setResult(logJson.getInteger("result"));
                logList.add(itemSceneLog);
            }
        }
        return logList;
    }

    // 房间设备列表
    public static List<EDevice.deviceEntry> processRoomDeviceList(String cloudData) {
        if(cloudData == null || cloudData.length() == 0){
            return null;
        }
        List<EDevice.deviceEntry> deviceList = new ArrayList<>();
        JSONObject dataJson = JSONObject.parseObject(cloudData);
        JSONArray dataArr = dataJson.getJSONArray("data");
        if (dataArr!=null){
            for (int i=0;i<dataArr.size();i++){
                JSONObject jsonObject = dataArr.getJSONObject(i);
                EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                deviceEntry.iotId = jsonObject.getString("iotId");
                deviceEntry.deviceName = jsonObject.getString("deviceName");
                deviceEntry.deviceName = ProductHelper.replaceBrand(deviceEntry.deviceName);
                deviceEntry.nodeType = jsonObject.getString("nodeType");
                deviceEntry.nickName = jsonObject.getString("nickName");
                if(TextUtils.isEmpty(deviceEntry.nickName)){
                    deviceEntry.nickName = jsonObject.getString("productName");
                }
                deviceEntry.nickName = ProductHelper.replaceBrand(deviceEntry.nickName);
                deviceEntry.productKey = jsonObject.getString("productKey");
                deviceEntry.status = jsonObject.getInteger("status");
                deviceList.add(deviceEntry);
            }
        }
        return deviceList;
    }

    // 处理场景详细信息
    public static EScene.processedDetailEntry processSceneDetailInformation(String cloudData){
        EScene.rawDetailEntry rawDetailEntry = new EScene.rawDetailEntry();
        rawDetailEntry = JSON.parseObject(cloudData, EScene.rawDetailEntry.class);
        EScene.processedDetailEntry detailEntry = new EScene.processedDetailEntry();
        detailEntry.rawDetail = rawDetailEntry;
        // 触发处理
        if(rawDetailEntry.getTriggersJson() != null && rawDetailEntry.getTriggersJson().length() > 0){
            detailEntry.addTrigger(rawDetailEntry.getTriggersJson());
        }
        // 条件处理
        if(rawDetailEntry.getConditionsJson() != null && rawDetailEntry.getConditionsJson().length() > 0){
            detailEntry.addCondition(rawDetailEntry.getConditionsJson());
        }
        // 响应动作处理
        if(rawDetailEntry.getActionsJson() != null && rawDetailEntry.getActionsJson().size() > 0){
            for(String item : rawDetailEntry.getActionsJson()){
                detailEntry.addAction(item);
            }
        }
        return  detailEntry;
    }
}
