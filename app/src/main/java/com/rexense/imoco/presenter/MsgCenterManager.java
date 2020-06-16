package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

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
    public void getMsgList(String messageType,
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
        requestParameterEntry.addParameter("nextToken", 0);
        requestParameterEntry.addParameter("maxResults", 100);
        requestParameterEntry.addParameter("type", "MESSAGE");
        requestParameterEntry.addParameter("messageType", messageType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_MSGCENTER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

}
