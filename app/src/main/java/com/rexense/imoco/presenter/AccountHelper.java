package com.rexense.imoco.presenter;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.contract.IAccount;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.Logger;

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

    // 更新账号昵称
    public static void modifyAccountNickName(String nickName, Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nickName",nickName);
        jsonObject.put("phone",Account.getUserPhone());
        jsonObject.put("appKey",Constant.APPKEY);
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_MODIFYACCOUNT;
        requestParameterEntry.version = "1.0.5";
//        requestParameterEntry.addParameter("identityId", homeId);
//        requestParameterEntry.addParameter("accountMetaV2", jsonObject);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_MODIFYACCOUNT;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
}