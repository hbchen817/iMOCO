package com.laffey.smart.presenter;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemBindList;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.utility.GsonUtil;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备缓存器
 */
public class DeviceBuffer {
    private static Map<String, EDevice.deviceEntry> mBuffer = new HashMap<String, EDevice.deviceEntry>();
    private static Map<String, JSONObject> mExtendedBuffer = new HashMap<String, JSONObject>();
    private static Map<String, String> mCacheBuffer = new HashMap<>();
    private static Map<String, ItemSceneInGateway> mSceneBuffer = new LinkedHashMap<>();
    private static Map<String, ItemBindList> mBindRelationBuffer = new HashMap<>();
    private static Map<String, EDevice.subGwEntry> mSubGwBuffer = new HashMap<>();

    // 初始化处理
    public static void initProcess() {
        mBuffer.clear();
    }

    public static void initSceneBuffer() {
        mSceneBuffer.clear();
    }

    public static void initExtendedBuffer() {
        mExtendedBuffer.clear();
    }

    public static void initBindRelationBuffer() {
        mBindRelationBuffer.clear();
    }

    public static void initSubGw() {
        mSubGwBuffer.clear();
    }

    public static void addSubGw(String subMac, EDevice.subGwEntry entry) {
        mSubGwBuffer.put(subMac, entry);
    }

    public static EDevice.subGwEntry getSubGw(String subMac) {
        if (subMac != null && mSubGwBuffer.containsKey(subMac)) {
            return mSubGwBuffer.get(subMac);
        }
        return null;
    }

    public static void removeSubGw(String subMac) {
        if (subMac != null && mSubGwBuffer.containsKey(subMac)) {
            mSubGwBuffer.remove(subMac);
        }
    }

    public static void addBindList(String macAndKey, ItemBindList list) {
        if (macAndKey != null && macAndKey.length() > 0)
            mBindRelationBuffer.put(macAndKey, list);
    }

    public static void removeBindList(String macAndKey) {
        if (macAndKey != null && macAndKey.length() > 0)
            mBindRelationBuffer.remove(macAndKey);
    }

    public static ItemBindList getBindList(String macAndKey) {
        if (macAndKey != null && macAndKey.length() > 0 &&
                mBindRelationBuffer.containsKey(macAndKey)) {
            return mBindRelationBuffer.get(macAndKey);
        }
        return null;
    }

    public static Map<String, ItemBindList> getAllBindList() {
        return mBindRelationBuffer;
    }

    public static void addCacheInfo(String key, String value) {
        if (key != null && key.length() > 0) {
            mCacheBuffer.put(key, value);
        }
    }

    public static String getCacheInfo(String key) {
        if (key != null && key.length() > 0 && mCacheBuffer.containsKey(key)) {
            return mCacheBuffer.get(key);
        } else
            return null;
    }

    // 添加场景
    public static void addScene(String sceneId, ItemSceneInGateway scene) {
        if (sceneId != null && scene != null) {
            mSceneBuffer.put(sceneId, scene);
        }
    }

    // 删除场景
    public static void removeScene(String sceneId) {
        if (sceneId != null && mSceneBuffer.containsKey(sceneId)) {
            mSceneBuffer.remove(sceneId);
        }
    }

    // 根据关联场景id获取场景信息
    public static ItemSceneInGateway getSceneByCid(String cId, String keyCode) {
        for (ItemSceneInGateway scene : mSceneBuffer.values()) {
            if (scene.getAppParams() != null && cId.equals(scene.getAppParams().getString("cId"))) {
                if (scene.getAppParams() != null && keyCode.equals(scene.getAppParams().getString("key")))
                    return scene;
            }
        }
        return null;
    }

    // 根据设备id获取场景列表
    public static List<ItemSceneInGateway> getScenesBySwitchIotId(String switchIotId) {
        List<ItemSceneInGateway> list = new ArrayList<>();
        for (ItemSceneInGateway scene : mSceneBuffer.values()) {
            if (scene.getAppParams() != null && switchIotId.equals(scene.getAppParams().getString("switchIotId"))) {
                list.add(scene);
            }
        }
        return list;
    }

    // 获取所有场景
    public static Map<String, ItemSceneInGateway> getAllScene() {
        return mSceneBuffer;
    }

    // 获取指定网关下手动场景列表（非场景绑定）
    public static List<ItemSceneInGateway> getAllScene(String gwMac) {
        List<ItemSceneInGateway> list = new ArrayList<>();
        for (ItemSceneInGateway scene : mSceneBuffer.values()) {
            if (gwMac.equals(scene.getGwMac())) {
                JSONObject appParams = scene.getAppParams();
                String switchIotId = null;
                if (appParams != null) {
                    switchIotId = appParams.getString("switchIotId");
                }
                if (switchIotId != null && switchIotId.length() > 0) continue;
                list.add(scene);
            }
        }
        return list;
    }

    // 获取指定网关下场景列表
    public static List<ItemSceneInGateway> getAllSceneInGW(String gwMac) {
        List<ItemSceneInGateway> list = new ArrayList<>();
        for (ItemSceneInGateway scene : mSceneBuffer.values()) {
            if (gwMac.equals(scene.getGwMac())) {
                list.add(scene);
            }
        }
        return list;
    }

    // 获取场景
    public static ItemSceneInGateway getScene(String sceneId) {
        if (sceneId != null && mSceneBuffer.containsKey(sceneId)) {
            return mSceneBuffer.get(sceneId);
        }
        return null;
    }

    // 获取自动或手动场景 0：自动  1：手动
    public static List<ItemSceneInGateway> getScenesByType(String type) {
        List<ItemSceneInGateway> list = new ArrayList<>();
        for (ItemSceneInGateway scene : mSceneBuffer.values()) {
            if (scene.getSceneDetail().getType().equals(type)) {
                list.add(scene);
            }
        }
        return list;
    }

    // 添加扩展信息
    public static void addExtendedInfo(String iotId, JSONObject info) {
        if (iotId != null && info != null) {
            mExtendedBuffer.put(iotId, info);
        }
    }

    // 获取扩展信息
    public static JSONObject getExtendedInfo(String iotId) {
        if (iotId != null) {
            if (mExtendedBuffer.containsKey(iotId)) {
                return mExtendedBuffer.get(iotId);
            }
        }
        return new JSONObject();
    }

    // 删除扩展信息
    public static void removeExtendedInfo(String iotId) {
        if (iotId != null) {
            if (mExtendedBuffer.containsKey(iotId)) {
                mExtendedBuffer.remove(iotId);
            }
        }
    }

    // 追加家设备列表
    public static void addHomeDeviceList(EHomeSpace.homeDeviceListEntry homeListEntry) {
        if (homeListEntry != null && homeListEntry.data != null && homeListEntry.data.size() > 0) {
            for (EHomeSpace.deviceEntry entry : homeListEntry.data) {
                if (!mBuffer.containsKey(entry.iotId)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = entry.iotId;
                    deviceEntry.nickName = entry.nickName;
                    deviceEntry.deviceName = entry.deviceName;
                    deviceEntry.mac = entry.mac;
                    deviceEntry.productKey = entry.productKey;
                    deviceEntry.roomId = entry.roomId == null ? "" : entry.roomId;
                    deviceEntry.roomName = entry.roomName == null ? "" : entry.roomName;
                    deviceEntry.owned = 1;
                    deviceEntry.status = entry.status;
                    deviceEntry.nodeType = entry.nodeType;
                    deviceEntry.image = entry.image;
                    mBuffer.put(entry.iotId, deviceEntry);
                } else {
                    EDevice.deviceEntry deviceEntry = mBuffer.get(entry.iotId);
                    if (deviceEntry != null) {
                        deviceEntry.iotId = entry.iotId;
                        deviceEntry.nickName = entry.nickName;
                        deviceEntry.deviceName = entry.deviceName;
                        deviceEntry.productKey = entry.productKey;
                        deviceEntry.roomId = entry.roomId == null ? "" : entry.roomId;
                        deviceEntry.roomName = entry.roomName == null ? "" : entry.roomName;
                        deviceEntry.owned = 1;
                        deviceEntry.status = entry.status;
                        deviceEntry.nodeType = entry.nodeType;
                        deviceEntry.image = entry.image;
                    }
                }
            }
        }
    }

    // 追加用户绑定设备列表
    public static void addUserBindDeviceList(EUser.bindDeviceListEntry userListEntry) {
        if (userListEntry != null && userListEntry.data != null && userListEntry.data.size() > 0) {
            for (EUser.deviceEntry entry : userListEntry.data) {
                if (mBuffer.containsKey(entry.iotId)) {
                    Objects.requireNonNull(mBuffer.get(entry.iotId)).bindTime = entry.bindTime;
                    Objects.requireNonNull(mBuffer.get(entry.iotId)).status = entry.status;
                } else {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = entry.iotId;
                    deviceEntry.nickName = entry.nickName;
                    deviceEntry.deviceName = entry.deviceName;
                    deviceEntry.mac = entry.mac;
                    deviceEntry.productKey = entry.productKey;
                    deviceEntry.owned = entry.owned;
                    deviceEntry.bindTime = entry.bindTime;
                    deviceEntry.status = entry.status;
                    deviceEntry.nodeType = entry.nodeType;
                    deviceEntry.image = entry.image;
                    mBuffer.put(entry.iotId, deviceEntry);
                }
            }
        }
    }

    // 删除设备
    public static void deleteDevice(String iotId) {
        if (mBuffer.containsKey(iotId)) {
            mBuffer.remove(iotId);
        }
    }

    // 获取所有设备信息
    public static Map<String, EDevice.deviceEntry> getAllDeviceInformation() {
        return mBuffer;
    }

    // 为获取网关设备列表
    public static List<EDevice.deviceEntry> queryGwDevList() {
        List<EDevice.deviceEntry> list = new ArrayList<>();
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if ("GATEWAY".equals(entry.nodeType)) {
                list.add(entry);
            }
        }
        return list;
    }

    // 获取同类设备信息
    public static Map<String, EDevice.deviceEntry> getSameTypeDeviceInformation(String productKey) {
        Map<String, EDevice.deviceEntry> list = new HashMap<String, EDevice.deviceEntry>();
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (entry.productKey.equalsIgnoreCase(productKey)) {
                list.put(entry.iotId, entry);
            }
        }

        return list;
    }

    // 获取同类设备信息（单一网关）
    public static Map<String, EDevice.deviceEntry> getSameTypeDeviceInformation(String productKey, String gatewayId) {
        Map<String, EDevice.deviceEntry> list = new HashMap<String, EDevice.deviceEntry>();
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (entry.productKey.equalsIgnoreCase(productKey) && entry.gatewayId.equalsIgnoreCase(gatewayId)) {
                list.put(entry.iotId, entry);
            }
        }
        return list;
    }

    // 通过deviceName获取设备信息
    public static EDevice.deviceEntry getDevByDeviceName(String deviceName) {
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (deviceName.equals(entry.deviceName)) {
                return entry;
            }
        }
        return null;
    }

    // 设置每个设备所属的gatewayId
    public static void setGatewayId(String iotId, String gatewayId) {
        if (mBuffer.containsKey(iotId)) {
            Objects.requireNonNull(mBuffer.get(iotId)).gatewayId = gatewayId;
        }
    }

    // 获取网关设备信息
    public static List<EDevice.deviceEntry> getGatewayDevs() {
        List<EDevice.deviceEntry> list = new ArrayList<>();
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if ("GATEWAY".equals(entry.nodeType)) {
                list.add(entry);
            }
        }
        return list;
    }

    // 通过mac获取设备信息
    public static EDevice.deviceEntry getDevByMac(String mac) {
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (mac.equals(entry.mac)) {
                return entry;
            }
        }
        return null;
    }

    // 通过ProductKey获取设备信息（非共享设备）
    public static List<EDevice.deviceEntry> getDevByPK(String pk) {
        List<EDevice.deviceEntry> list = new ArrayList<>();
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (pk.equals(entry.productKey) && entry.owned == 1) {
                list.add(entry);
            }
        }
        return list;
    }

    // 更新设备房间
    public static void updateDeviceRoom(String iotId, String roomId, String roomName) {
        if (mBuffer.containsKey(iotId)) {
            // 原房间设备数量减1
            HomeSpaceManager.updateRoomDeviceNumber(mBuffer.get(iotId).roomId, -1);
            mBuffer.get(iotId).roomId = roomId;
            mBuffer.get(iotId).roomName = roomName;
            // 新房间设备数量加1
            HomeSpaceManager.updateRoomDeviceNumber(roomId, 1);
        }
    }

    // 更新设备备注名称
    public static void updateDeviceNickName(String iotId, String nickName) {
        if (mBuffer.containsKey(iotId)) {
            mBuffer.get(iotId).nickName = nickName;
        }
    }

    // 更新设备mac
    public static void updateDeviceMac(String iotId, String mac) {
        if (mBuffer.containsKey(iotId)) {
            mBuffer.get(iotId).mac = mac;
        }
    }

    // 获取设备mac
    public static String getDeviceMac(String iotId) {
        if (mBuffer.containsKey(iotId)) {
            return mBuffer.get(iotId).mac;
        }
        return "";
    }

    // 获取设备缓存信息
    public static EDevice.deviceEntry getDeviceInformation(String iotId) {
        if (!mBuffer.containsKey(iotId)) {
            return null;
        } else {
            return mBuffer.get(iotId);
        }
    }

    // 修改设备在线、离线状态
    public static void updateDeviceStatus(String iotId, int status) {
        if (mBuffer.containsKey(iotId)) {
            mBuffer.get(iotId).status = status;
        }
    }

    // 获取房间设备列表
    public static Map<String, Integer> getRoomDeviceList(String roomId) {
        if (roomId == null || roomId.length() == 0) {
            return null;
        }
        Map<String, Integer> list = new HashMap<String, Integer>();
        Integer index = 1;
        for (EDevice.deviceEntry entry : mBuffer.values()) {
            if (entry.roomId.equals(roomId)) {
                list.put(entry.iotId, index++);
            }
        }
        return list;
    }

    // 获取设备的Owned
    public static int getDeviceOwned(String iotId) {
        if (mBuffer.containsKey(iotId)) {
            return mBuffer.get(iotId).owned;
        } else {
            return 0;
        }
    }

    // 获取设备的房间信息
    public static EHomeSpace.roomEntry getDeviceRoomInfo(String iotId) {
        if (mBuffer.containsKey(iotId)) {
            EHomeSpace.roomEntry roomEntry = new EHomeSpace.roomEntry();
            roomEntry.roomId = mBuffer.get(iotId).roomId;
            roomEntry.name = mBuffer.get(iotId).roomName;
            return roomEntry;
        } else {
            return null;
        }
    }
}
