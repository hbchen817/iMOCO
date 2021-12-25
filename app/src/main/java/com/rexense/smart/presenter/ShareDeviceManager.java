package com.rexense.smart.presenter;

import android.content.Context;
import android.os.Handler;

import com.rexense.smart.contract.Constant;
import com.rexense.smart.model.EAPIChannel;
import com.rexense.smart.sdk.APIChannel;
import com.rexense.smart.utility.Logger;

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

    // 通过手机账号分享设备
    public void shareDeviceByMobile(ArrayList<String> iotIdList,String mobile,
                          Handler commitFailureHandler,
                          Handler responseErrorHandler,
                          Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_SHAREDEVICEORSCENE;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("iotIdList", iotIdList);
        requestParameterEntry.addParameter("accountAttr", mobile);
        requestParameterEntry.addParameter("accountAttrType", "MOBILE");
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SHAREDEVICEORSCENE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 同意或拒绝设备共享
    public void confirmShare(int agree,ArrayList<String> recordIdList,
                          Handler commitFailureHandler,
                          Handler responseErrorHandler,
                          Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CONFIRMSHARE;
        requestParameterEntry.version = "1.0.7";
        requestParameterEntry.addParameter("recordIdList", recordIdList);
        requestParameterEntry.addParameter("agree", agree);//0：不同意；1：同意
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CONFIRMSHARE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

}
