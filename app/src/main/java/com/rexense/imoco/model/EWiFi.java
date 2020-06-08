package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-07 15:29
 * Description: WiFi实体
 */
public class EWiFi {
    // 设备实体
    public static class WiFiEntry {
        public String bSSID;
        public String ssid;
        public int level;

        // 构造
        public WiFiEntry(String bSSID, String ssid, int level) {
            this.bSSID = bSSID;
            this.ssid = ssid;
            this.level = level;
        }
    }
}