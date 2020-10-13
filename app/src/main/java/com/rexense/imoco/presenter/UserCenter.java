package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;


import java.util.ArrayList;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-17 15:29
 * Description: 用户中心(实现用户服务相关功能)
 */
public class UserCenter {
    private Context mContext;

    // 构造
    public UserCenter(Context context) {
        this.mContext = context;
    }

    // 获取用户绑定网关绑定子设备列表
    public void getGatewaySubdeviceList(String gatewayIOTId, int pageNo, int pageSize,
                                        Handler commitFailureHandler,
                                        Handler responseErrorHandler,
                                        Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETGATEWAYSUBDEVICELIST;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", gatewayIOTId);
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取用户绑定的设备列表
    public void getDeviceList(int pageNo, int pageSize,
                              Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETUSERDEVICELIST;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETUSERDEVICTLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 设置设备昵称
    public void setDeviceNickName(String iotId, String nickName,
                                  Handler commitFailureHandler,
                                  Handler responseErrorHandler,
                                  Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_SETDEVICENICKNAME;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("nickName", nickName);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SETDEVICENICKNAME;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 解绑用户和设备
    public void unbindDevice(String deviceId,
                             Handler commitFailureHandler,
                             Handler responseErrorHandler,
                             Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UNBINDDEVICE;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", deviceId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UNBINDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 创建虚拟用户
     * @param name 虚拟用户名称
     */
    public static void createVirtualUser(String name,
                                         Handler commitFailureHandler,
                                         Handler responseErrorHandler,
                                         @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CREATE_USER;
        requestParameterEntry.version = "1.0.6";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attrKey", "name");
        jsonObject.put("attrValue", name);
        jsonArray.add(jsonObject);
        requestParameterEntry.addParameter("attrList", jsonArray);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CREATE_USER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 查询账号下的虚拟用户
     */
    public static void queryVirtualUserListInAccount(int pageNo,//从1开始
                                         int pageSize,
                                         Handler commitFailureHandler,
                                         Handler responseErrorHandler,
                                         Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERY_USER_IN_ACCOUNT;
        requestParameterEntry.version = "1.0.6";
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 编辑用户
     */
    public static void updateVirtualUser(String userID,
                                         String newName,
                                         Handler commitFailureHandler,
                                         Handler responseErrorHandler,
                                         Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attrKey", "name");
        jsonObject.put("attrValue", newName);
        jsonArray.add(jsonObject);
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATE_USER;
        requestParameterEntry.version = "1.0.6";
        requestParameterEntry.addParameter("virtualUserId", userID);
        requestParameterEntry.addParameter("opType", 2);
        requestParameterEntry.addParameter("attrList", jsonArray);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATE_USER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }


}
