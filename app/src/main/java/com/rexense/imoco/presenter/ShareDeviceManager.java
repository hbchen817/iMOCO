package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

import java.util.ArrayList;

/**
 * Creator: fyy
 * creat time: 2020-06-15 15:20
 * Description: 设备共享管理者
 */
public class ShareDeviceManager {
    private Context mContext;

    // 构造
    public ShareDeviceManager(Context context) {
        this.mContext = context;
    }

    // 获取二维码
    public void getQrcode(ArrayList<String> iotIdList,
                          Handler commitFailureHandler,
                          Handler responseErrorHandler,
                          Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GENERATEQRCODE;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotIdList", iotIdList);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SHAREQRCODE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 扫描二维码
    public void scanQrcode(String qrKey,
                          Handler commitFailureHandler,
                          Handler responseErrorHandler,
                          Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_SCANSHAREQRCODE;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("qrKey", qrKey);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SCANSHAREQRCODE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

}
