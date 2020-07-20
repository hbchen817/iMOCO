package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creator: fyy
 * creat time: 2020-06-15 15:20
 * Description: 消息中心管理者
 */
public class MsgCenterManager {
    private Context mContext;

    // 构造
    public MsgCenterManager(Context context) {
        this.mContext = context;
    }

    // 获取消息列表
    public void getMsgList(int pageNo,String messageType,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_MESSAGECENTER;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("nextToken", pageNo);
        requestParameterEntry.addParameter("maxResults", Constant.PAGE_SIZE);
        requestParameterEntry.addParameter("type", "MESSAGE");
        requestParameterEntry.addParameter("messageType", messageType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_MSGCENTER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 清空消息列表
    public void clearMsgList(String messageType,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CLEARMESSAGERECORD;
        requestParameterEntry.version = "1.0.6";

        JSONObject requestDTO = new JSONObject();
        requestDTO.put("type","NOTICE");
        requestDTO.put("messageType",messageType);
        requestParameterEntry.addParameter("requestDTO", requestDTO);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CLEARMESSAGERECORD;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 获取共享设备通知列表
    public void getShareNoticeList(int pageNo,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_SHARENOTICELIST;
        requestParameterEntry.version = "1.0.7";
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", Constant.PAGE_SIZE);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SHARENOTICELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 清空共享设备通知列表
    public void clearShareNoticeList(Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CLEARSHARENOTICELIST;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CLEARSHARENOTICELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

}
