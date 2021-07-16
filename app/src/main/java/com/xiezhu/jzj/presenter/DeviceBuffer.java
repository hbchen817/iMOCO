package com.xiezhu.jzj.presenter;

import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.EHomeSpace;
import com.xiezhu.jzj.model.EUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    private static Map<String, EHomeSpace.homeEntry> mHomeBuffer = new HashMap<>();

    // 初始化处理
    public static void initProcess() {
        mBuffer.clear();
    }

    // 添加家信息
    public static void addHomeInfo(String homeId, EHomeSpace.homeEntry entry) {
        if (homeId != null && entry != null) {
            mHomeBuffer.put(homeId, entry);
        }
    }

    // 添加家信息
    public static void addHomeInfo(List<EHomeSpace.homeEntry> list) {
        for (EHomeSpace.homeEntry entry : list) {
            mHomeBuffer.put(entry.homeId, entry);
        }
    }

    // 获取家信息
    public static EHomeSpace.homeEntry getHomeInfo(String homeId) {
        if (homeId != null)
            return mHomeBuffer.get(homeId);
        return null;
    }

    // 获取家列表
    public static List<EHomeSpace.homeEntry> getHomeInfoList() {
        List<EHomeSpace.homeEntry> list = new ArrayList<>();
        list.addAll(mHomeBuffer.values());
        return list;
    }

    // 删除家信息
    public static void removeHomeInfo(String homeId) {
        if (mHomeBuffer.containsKey(homeId)) {
            mHomeBuffer.remove(homeId);
        }
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
        return null;
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
                } else {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = entry.iotId;
                    deviceEntry.nickName = entry.nickName;
                    deviceEntry.deviceName = entry.deviceName;
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

    // 获取设备缓存信息
    public static EDevice.deviceEntry getDeviceInformation(String iotId) {
        if (!mBuffer.containsKey(iotId)) {
            return null;
        } else {
            return mBuffer.get(iotId);
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
