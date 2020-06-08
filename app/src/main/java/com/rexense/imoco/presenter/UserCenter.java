package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

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
        if(processDataHandler == null){
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
        if(processDataHandler == null){
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
        if(processDataHandler == null){
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
        if(processDataHandler == null){
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
}
