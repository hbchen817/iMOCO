package com.rexense.imoco.contract;

import com.rexense.imoco.model.EBLE;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-07 15:29
 * Description: BLE接口
 */
public class IBLE {
    // 发现设备回调接口
    public static interface discoveryCallback {
        // 返回找到结果
        void returnFoundResult(EBLE.DeviceEntry deviceEntry);
    }
}

