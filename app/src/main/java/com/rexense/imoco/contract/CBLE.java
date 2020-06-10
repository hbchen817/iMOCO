package com.rexense.imoco.contract;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-07 15:29
 * Description: BLE常量
 */
public class CBLE {
    // BLE设备名称前缀
    public final static String BLE_NAME_PREFIX                  = "iMOCO";

    // 定义广播的定作
    public final static String ACTION_GATT_CONNECTED            = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED         = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED  = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE            = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    // 定义广播数据的Key
    public final static String EXTRA_SERVICE_UUID               = "com.example.bluetooth.le.EXTRA_SUUID";
    public final static String EXTRA_CHARACTERISTIC_UUID        = "com.example.bluetooth.le.EXTRA_CUUID";
    public final static String EXTRA_DATA                       = "com.example.bluetooth.le.EXTRA_DATA";

    // 定义要读写的服务及特征(要根据项目中采用的蓝牙芯片进行修改)
    public static final String READ_WRITE_SERVICE_UUID          = "91BC9BBE-ED4C-4CEE-8E7D-D8516C886547";
    public static final String READ_WRITE_CHARACTERISTIC_UUID   = "7BA2A753-97AF-4E83-AF8B-7A413F8DBBDD";


}