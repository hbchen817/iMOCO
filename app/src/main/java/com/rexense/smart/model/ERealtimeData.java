package com.rexense.smart.model;


import android.os.Handler;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-018 15:29
 * Description: 实时数据实体
 */
public class ERealtimeData {
    // 回调处理器实体
    public static class callbackHandlerEntry {
        public Handler handler;
        public String topic;
        public int messageType;

        //构造
        public callbackHandlerEntry(Handler handler, String topic, int messageType) {
            this.handler = handler;
            this.topic = topic;
            this.messageType = messageType;
        }
    }

    // 设备连接状态实体
    public static class deviceConnectionStatusEntry {
        public String ip;
        public String iotId;
        public String deviceName;
        public String productKey;
        public int statusLast;
        public int status;

        // 构造
        public deviceConnectionStatusEntry() {
            this.ip = "";
            this.iotId = "";
            this.deviceName = "";
            this.productKey = "";
        }
    }

    // 子设备加网结果实体
    public static class subDeviceJoinResultEntry {
        public int status;
        public String subIotId;
        public String subProductKey;
        public String subDeviceName;
        public String newGwIotId;
        public String newGwProductKey;
        public String newGwDeviceName;

        // 构造
        public subDeviceJoinResultEntry() {
            this.subIotId = "";
            this.subProductKey = "";
            this.subDeviceName = "";
            this.newGwIotId = "";
            this.newGwProductKey = "";
            this.newGwDeviceName = "";
        }
    }
}