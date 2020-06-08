package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;


/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 产品助手
 */
public class ProductHelper {
    private Context mContext;

    // 构造
    public ProductHelper(Context context) {
        this.mContext = context;
    }

    // 获取支持配网产品列表
    public void getConfigureList(Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETCONFIGPROCDUCTLIST;
        requestParameterEntry.version = "1.1.3";
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取配网引导信息
    public void getGuidanceInformation(String productKey, Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETGUIDANCEINFORMATION;
        requestParameterEntry.version = "1.1.3";
        requestParameterEntry.addParameter("productKey", productKey);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETGUIDANCEINFOMATION;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
}
