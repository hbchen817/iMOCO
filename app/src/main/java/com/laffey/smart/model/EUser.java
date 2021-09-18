package com.laffey.smart.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-26 16:29
 * Description: 用户实体
 */
public class EUser {
    // 设备实体
    public static class deviceEntry {
        public String iotId;
        public String productKey;
        public String productName;
        public String deviceName;
        public String nickName;
        public String image;
        public int status;
        public int owned;
        public String nodeType;
        public String bindTime;
        public String gatewayId;

        // 构造
        public deviceEntry() {
            this.iotId = "";
            this.productKey = "";
            this.productName = "";
            this.deviceName = "";
            this.nickName = "";
            this.image = "";
            this.status = 0;
            this.owned = 0;
            this.nodeType = "DEVICE";
            this.bindTime = "";
            this.gatewayId = "";
        }
    }

    // 网关子设备列表实体
    public static class gatewaySubdeviceListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<EUser.deviceEntry> data;

        // 构造
        public gatewaySubdeviceListEntry(){
            this.data = new ArrayList<EUser.deviceEntry>();
        }

        // 添加子设备
        public void addSubdevice(EUser.deviceEntry subdeviceEntry) {
            this.data.add(subdeviceEntry);
        }
    }

    // 用户绑定设备列表实体
    public static class bindDeviceListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<EUser.deviceEntry> data;

        // 构造
        public bindDeviceListEntry(){
            this.data = new ArrayList<EUser.deviceEntry>();
        }

        // 添加设备
        public void addDevice(EUser.deviceEntry deviceEntry) {
            this.data.add(deviceEntry);
        }
    }
}

