package com.rexense.imoco.presenter;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.sdk.APIChannel;

/**
 * @author Gary
 * @time 2020/10/14 9:32
 */

public class LockManager {

    /**
     * 查询历史记录
     *
     * @param iotId 设备id
     * @param start 开始时间 ms
     * @param end   结束时间
     */
    public static void getLockHistory(String iotId,
                                      long start,
                                      long end,
                                      String[] type,
                                      int pageNo,
                                      int pageSize,
                                      Handler commitFailureHandler,
                                      Handler responseErrorHandler,
                                      @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERY_HISTORY;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("iotid", iotId);
        requestParameterEntry.addParameter("start", start);
        requestParameterEntry.addParameter("end", end);
        requestParameterEntry.addParameter("events", type);
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_HISTORY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 设置临时密码
     * @param iotId
     * @param key
     * @param start
     * @param end
     * @param commitFailureHandler
     * @param responseErrorHandler
     * @param processDataHandler
     */
    public static void setTemporaryKey(String iotId,
                                       String key,
                                       String start,
                                       String end,
                                       Handler commitFailureHandler,
                                       Handler responseErrorHandler,
                                       @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_TEMPORARY_KEY;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("iotid", iotId);
        requestParameterEntry.addParameter("identifier", "AddOTP");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OTP",key);
        jsonObject.put("StartDate", start);
        jsonObject.put("EndDate", end);
        jsonObject.put("ValidCount", 1);
        requestParameterEntry.addParameter("args", jsonObject);

        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_HISTORY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //todo 过滤未绑定用户钥匙
    public static void filterUnbindKey(String iotId,
                                       String keyID,
                                       String keyType,
                                       String lockUserPermType,
                                       Handler commitFailureHandler,
                                       Handler responseErrorHandler,
                                       @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_FILTER_UNBIND_KEY;
        requestParameterEntry.version = "1.0.2";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotId",iotId);
        jsonObject.put("lockUserId", keyID);
        jsonObject.put("lockUserType", keyType);
        jsonObject.put("lockUserPermType", lockUserPermType);
        JSONArray array = new JSONArray();
        array.add(jsonObject);
        requestParameterEntry.addParameter("originalLockUsers", array);

        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_FILTER_UNBIND_KEY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }


    /**
     * 钥匙与虚拟用户绑定
     * @param virtualUserId 虚拟用户ID
     * @param lockUserId 钥匙ID
     * @param lockUserType 钥匙类型
     * @param lockUserPermType 钥匙权限
     * @param iotId 设备ID
     */
    public static void bindUserKey(String virtualUserId,
                                   String lockUserId,
                                   int lockUserType,
                                   int lockUserPermType,
                                   String iotId,
                                   Handler commitFailureHandler,
                                   Handler responseErrorHandler,
                                   @NonNull Handler processDataHandler){

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_KEY_USER_BIND;
        requestParameterEntry.version = "1.0.1";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotId",iotId);
        jsonObject.put("virtualUserId", virtualUserId);
        jsonObject.put("lockUserId", lockUserId);
        jsonObject.put("lockUserType", lockUserType);
        jsonObject.put("lockUserPermType", lockUserPermType);
        JSONArray array = new JSONArray();
        array.add(jsonObject);
        requestParameterEntry.addParameter("originalLockUsers", array);

        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_FILTER_UNBIND_KEY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);

    }

}