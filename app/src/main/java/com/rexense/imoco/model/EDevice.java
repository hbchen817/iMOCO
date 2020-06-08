package com.rexense.imoco.model;

import com.rexense.imoco.contract.Constant;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备实体(包括网关及子设备)
 */
public class EDevice {
    // 设备实体
    public static class deviceEntry {
        public String iotId;
        public String nickName;
        public String productKey;
        public String roomId;
        public String roomName;
        public int owned;
        public int status;
        public String nodeType;
        public String bindTime;

        // 构造
        public deviceEntry(){
            this.iotId = "";
            this.nickName = "";
            this.productKey = "";
            this.roomId = "";
            this.roomName = "";
            this.owned = 0;
            this.status = Constant.CONNECTION_STATUS_UNABLED;
            this.nodeType = "DEVICE";
            this.bindTime = "";
        }
    }
}

