package com.rexense.imoco.sdk;

import androidx.annotation.Nullable;

import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 物的描述语言
 */
public class TSL {
    // 设置属性
    public static void setProperty(String iotId, String parameter) {
        PanelDevice panelDevice = new PanelDevice(iotId);
        panelDevice.setProperties(parameter, new IPanelCallback() {
            @Override
            public void onComplete(boolean b, @Nullable Object o) {
                if(b) {
                    Logger.d(String.format("Successfully set property:\r\n    iotId: %s\r\n    parameter: %s", iotId, parameter));
                } else {
                    Logger.e(String.format("Failed set property:\r\n    iotId: %s\r\n    parameter: %s\r\n    reason: %s", iotId, parameter, o.toString()));
                }
            }
        });
    }
}

