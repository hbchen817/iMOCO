package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: OTA实体
 */
public class EOTA {
    // 固件实体
    public static class firmwareEntry {
        public String currentVersion;
        public String version;
        public String currentTimestamp;
        public String timestamp;
        public String size;
        public String md5;
        public String name;
        public String url;
        public String desc;

        // 构造
        public firmwareEntry(){
            this.currentVersion = "";
            this.version = "";
            this.currentTimestamp = "";
            this.timestamp = "";
            this.size = "";
            this.md5 = "";
            this.name = "";
            this.url = "";
            this.desc = "";
        }
    }
}

