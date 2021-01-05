package com.laffey.smart.presenter;

import android.os.Handler;

import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.Logger;

import java.util.ArrayList;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-28 15:29
 * Description: 固件空中升级助手
 */
public class OTAHelper {
    // 获取固件信息
    public static void getFirmwareInformation(String iotId,
                                              Handler commitFailureHandler,
                                              Handler responseErrorHandler,
                                              Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETOTAFIRMWAREINFO;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETOTAFIRMWAREINFO;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 确认固件升级
    public static void upgradeFirmware(ArrayList<String> iotIds,
                                       Handler commitFailureHandler,
                                       Handler responseErrorHandler,
                                       Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPGRADEFIRMWARE;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotIds", iotIds);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPGRADEFIRMWARE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
}
