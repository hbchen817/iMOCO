package com.rexense.wholehouse.presenter;

import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.contract.IAccount;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.sdk.APIChannel;
import com.rexense.wholehouse.sdk.Account;
import com.rexense.wholehouse.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 用户账号助手
 */
public class AccountHelper {
    // 登录
    public static void login(IAccount.loginCallback loginCallback) {
        Logger.d("Start calling login interface of login plug-in.");
        Account.callLogin(loginCallback);
    }

    // 登出
    public static void logout(IAccount.logoutCallback logoutCallback) {
        Logger.d("Start calling logout interface of login plug-in.");
        Account.callLogout(logoutCallback);
    }

    // 注销账号
    public static void unregister(Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UNREGISTER;
        requestParameterEntry.version = "1.0.6";
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UNREGISTER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取绑定的淘宝账号
    public static void getBindTaoBaoAccount(String accountType, Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authCode",accountType);
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETBINDTAOBAOACCOUNT;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("request", jsonObject);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETBINDTAOBAOACCOUNT;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 绑定淘宝账号
    public static void bindTaoBaoAccount(String authCode, Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_BINDTAOBAO;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("authCode", authCode);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_BINDTAOBAO;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 解除绑定淘宝账号
    public static void unbindTaoBaoAccount(String accountType, Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UNBINDTAOBAO;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("accountType", accountType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UNBINDTAOBAO;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
}