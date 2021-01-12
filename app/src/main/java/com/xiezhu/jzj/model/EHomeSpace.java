package com.xiezhu.jzj.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-13 15:29
 * Description: 家空间实体
 */
public class EHomeSpace {
    // 家实体
    public static class homeEntry {
        public String homeId;
        public String name;
        public String myRole;
        public boolean currentHome;
        public long createMillis;

        // 构造
        public homeEntry() {
            this.homeId = "";
            this.name = "";
            this.myRole = "";
        }
    }

    // 家列表实体
    public static class homeListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<homeEntry> data;

        // 构造
        public homeListEntry() {
            this.data = new ArrayList<homeEntry>();
        }

        // 添加数据
        public void addData(String homeId, String name, String myRole, boolean currentHome, long createMillis) {
            homeEntry home = new homeEntry();
            home.homeId = homeId;
            home.name = name;
            home.myRole = myRole;
            home.currentHome = currentHome;
            home.createMillis = createMillis;
            this.data.add(home);
        }
    }

    // 房间实体
    public static class roomEntry {
        public String roomId;
        public String name;
        public int deviceCnt;
        public long createMillis;

        // 构造
        public roomEntry() {
            this.roomId = "";
            this.name = "";
        }
    }

    // 房间列表实体
    public static class roomListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<roomEntry> data;

        // 构造
        public roomListEntry() {
            this.data = new ArrayList<roomEntry>();
        }

        // 添加数据
        public void addData(String roomId, String name, int deviceCnt, long createMillis) {
            roomEntry room = new roomEntry();
            room.roomId = roomId;
            room.name = name;
            room.deviceCnt = deviceCnt;
            room.createMillis = createMillis;
            this.data.add(room);
        }
    }

    // 设备属性实体
    public static class devicePropertyEntry {
        public String identifier;
        public String name;
        public String value;
        public String dataType;
        public String imageUrl;

        // 构造
        public devicePropertyEntry() {
            this.identifier = "";
            this.name = "";
            this.value = "";
            this.dataType = "";
            this.imageUrl = "";
        }
    }

    // 设备实体
    public static class deviceEntry {
        public String iotId;
        public String deviceName;
        public String homeId;
        public String homeName;
        public String roomId;
        public String roomName;
        public String productKey;
        public String productName;
        public String productImage;
        public String productModel;
        public String categoryKey;
        public String categoryImage;
        public String panelPageRouterUrl;
        public String nickName;
        public String netType;
        public String thingType;
        public int status;
        public String nodeType;
        public String description;
        public List<String> subDeviceIotIdList;
        public List<devicePropertyEntry> propertyList;
        public String image;

        // 构造
        public deviceEntry() {
            this.iotId = "";
            this.deviceName = "";
            this.homeId = "";
            this.homeName = "";
            this.roomId = "";
            this.roomName = "";
            this.productKey = "";
            this.productName = "";
            this.productImage = "";
            this.productModel = "";
            this.categoryKey = "";
            this.categoryImage = "";
            this.panelPageRouterUrl = "";
            this.nickName = "";
            this.netType = "";
            this.thingType = "";
            this.nodeType = "";
            this.description = "";
            this.subDeviceIotIdList = new ArrayList<String>();
            this.propertyList = new ArrayList<devicePropertyEntry>();
            this.image = "";
        }

        // 添加子设备
        public void addSubDevice(String subDevice){
            this.subDeviceIotIdList.add(subDevice);
        }

        // 添加属性
        public void addProperty(devicePropertyEntry entry){
            this.propertyList.add(entry);
        }
    }

    // 家设备列表实体
    public static class homeDeviceListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<deviceEntry> data;

        // 构造
        public homeDeviceListEntry() {
            this.data = new ArrayList<deviceEntry>();
        }

        // 添加数据
        public void addData(deviceEntry entry) {
            this.data.add(entry);
        }
    }
}

