package com.xiezhu.jzj.presenter;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EAPIChannel;
import com.xiezhu.jzj.sdk.APIChannel;

import java.util.Arrays;

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
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(type));
        requestParameterEntry.addParameter("events", jsonArray);
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_HISTORY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 设置临时密码
     *
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
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("identifier", "AddOTP");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OTP", key);
        jsonObject.put("StartDate", start);
        jsonObject.put("EndDate", end);
        jsonObject.put("ValidCount", 1);
        requestParameterEntry.addParameter("args", jsonObject);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_LNEVENTNOTIFY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 设置临时密码
     *
     * @param iotId
     * @param key
     * @param startTime
     * @param commitFailureHandler
     * @param responseErrorHandler
     * @param processDataHandler
     */
    public static void setTemporaryKey(String iotId,
                                       String key,
                                       String startTime,
                                       Handler commitFailureHandler,
                                       Handler responseErrorHandler,
                                       @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_TEMPORARY_KEY;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("identifier", "AddOTP");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OTP", key);
        jsonObject.put("StartTime", startTime);
        jsonObject.put("ValidTime", 600);
        requestParameterEntry.addParameter("args", jsonObject);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_TEMPORARY_KEY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //远程开门
    public static void remoteOpen(String iotId,
                                  Handler commitFailureHandler,
                                  Handler responseErrorHandler,
                                  @NonNull Handler processDataHandler) {
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_TEMPORARY_KEY;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("identifier", "RemoteUnlock");
        JSONObject jsonObject = new JSONObject();
        requestParameterEntry.addParameter("args", jsonObject);
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    public static void filterUnbindKey(String iotId,
                                       String keyID,
                                       int keyType,
                                       int lockUserPermType,
                                       Handler commitFailureHandler,
                                       Handler responseErrorHandler,
                                       @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_FILTER_UNBIND_KEY;
        requestParameterEntry.version = "1.0.1";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iotId", iotId);
        jsonObject.put("lockUserId", keyID);
        jsonObject.put("lockUserType", keyType);
        jsonObject.put("lockUserPermType", lockUserPermType);
        jsonArray.add(jsonObject);
        requestParameterEntry.addParameter("originalLockUsers", jsonArray);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_FILTER_UNBIND_KEY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }


    /**
     * 钥匙与虚拟用户绑定
     *
     * @param virtualUserId    虚拟用户ID
     * @param lockUserId       钥匙ID
     * @param lockUserType     钥匙类型
     * @param lockUserPermType 钥匙权限
     * @param iotId            设备ID
     */
    public static void bindUserKey(String virtualUserId,
                                   String lockUserId,
                                   int lockUserType,
                                   int lockUserPermType,
                                   String iotId,
                                   Handler commitFailureHandler,
                                   Handler responseErrorHandler,
                                   @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_KEY_USER_BIND;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("virtualUserId", virtualUserId);
        requestParameterEntry.addParameter("lockUserId", lockUserId);
        requestParameterEntry.addParameter("lockUserType", lockUserType);
        requestParameterEntry.addParameter("lockUserPermType", lockUserPermType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_KEY_USER_BIND;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);

    }

    /**
     * 查询虚拟用户绑定的key
     *
     * @param virtualUserId 虚拟用户ID
     */
    public static void queryKeyByUser(String virtualUserId,
                                      Handler commitFailureHandler,
                                      Handler responseErrorHandler,
                                      @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERY_KEY_BY_USER;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("virtualUserId", virtualUserId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_KEY_BY_USER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 删除设备的钥匙信息
     *
     * @param lockUserId   钥匙Id
     * @param lockUserType 钥匙类型
     * @param iotId        iotid
     */
    public static void deleteKey(String lockUserId,
                                 int lockUserType,
                                 String iotId,
                                 Handler commitFailureHandler,
                                 Handler responseErrorHandler,
                                 @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_DELETE_KEY;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("lockUserId", lockUserId);
        requestParameterEntry.addParameter("lockUserType", lockUserType);
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_DELETE_KEY;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 查询钥匙对应的用户
     *
     * @param lockUserId   钥匙Id
     * @param lockUserType 钥匙类型
     * @param iotId        iotid
     */
    public static void getUserByKey(String lockUserId,
                                    int lockUserType,
                                    String iotId,
                                    Handler commitFailureHandler,
                                    Handler responseErrorHandler,
                                    @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_KEY_USER_GET;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("lockUserId", lockUserId);
        requestParameterEntry.addParameter("lockUserType", lockUserType);
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_KEY_USER_GET;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 解绑用户-钥匙
     */
    public static void userKeyUnbind(String virtualUserId,
                                     String lockUserId,
                                     int lockUserType,
                                     String iotId,
                                     Handler commitFailureHandler,
                                     Handler responseErrorHandler,
                                     @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_USER_KEY_UNBIND;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("virtualUserId", virtualUserId);
        requestParameterEntry.addParameter("lockUserId", lockUserId);
        requestParameterEntry.addParameter("lockUserType", lockUserType);
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_KEY_USER_UNBIND;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }


}