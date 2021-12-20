package com.laffey.smart.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    // 获取用户绑定网关绑定子设备列表
    public static void getGatewaySubdeviceList(Activity activity, String gatewayIOTId, int pageNo, int pageSize,
                                        APIChannel.Callback callback) {
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETGATEWAYSUBDEVICELIST;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", gatewayIOTId);
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST;
        //提交
        new APIChannel().commit(activity, requestParameterEntry, callback);
    }

    // 获取用户绑定网关绑定子设备列表
    public void getGatewaySubdeviceList(String gatewayIOTId, int pageNo, int pageSize, int tag,
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
        requestParameterEntry.callbackMessageType = tag;
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

    // 获取用户绑定的设备列表
    public static void getDeviceList(Activity activity, int pageNo, int pageSize, APIChannel.Callback callback) {
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETUSERDEVICELIST;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETUSERDEVICTLIST;
        //提交
        new APIChannel().commit(activity, requestParameterEntry, callback);
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

    //查询设备与用户的关系
    public void getByAccountAndDev(String iotId,
                                   Handler commitFailureHandler,
                                   Handler responseErrorHandler,
                                   Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GET_BY_ACCOUNT_AND_DEV;
        requestParameterEntry.version = "1.0.6";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV;
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
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("iotId", deviceId);
        requestParameterEntry.addParameter("unbindSubdevice", true);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UNBINDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 解绑子设备
    public void unbindSubDevice(String productKey, String deviceName,
                                Handler commitFailureHandler,
                                Handler responseErrorHandler,
                                Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = "/awss/subdevice/unbind";
        requestParameterEntry.version = "1.0.7";
        requestParameterEntry.addParameter("productKey", productKey);
        requestParameterEntry.addParameter("deviceName", deviceName);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UNBINDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 解绑子设备
    public void unbindSubDevice(String productKey, String deviceName, int tag,
                                Handler commitFailureHandler,
                                Handler responseErrorHandler,
                                Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = "/awss/subdevice/unbind";
        requestParameterEntry.version = "1.0.7";
        requestParameterEntry.addParameter("productKey", productKey);
        requestParameterEntry.addParameter("deviceName", deviceName);
        requestParameterEntry.callbackMessageType = tag;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    /**
     * 创建虚拟用户
     *
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
     * 删除虚拟用户
     *
     * @param virtualUserId 虚拟用户的ID
     */
    public static void deleteVirtualUser(String virtualUserId,
                                         Handler commitFailureHandler,
                                         Handler responseErrorHandler,
                                         @NonNull Handler processDataHandler) {

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_DELETE_USER;
        requestParameterEntry.version = "1.0.6";
        requestParameterEntry.addParameter("virtualUserId", virtualUserId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_DELETE_USER;
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
     * 查询设备下的虚拟用户列表
     */
    public static void queryVirtualUserListInDevice(String iotId,
                                                    Handler commitFailureHandler,
                                                    Handler responseErrorHandler,
                                                    Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERY_USER_IN_DEVICE;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERY_USER_IN_DEVICE;
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

    /**
     * 提交意见反馈
     */
    public void submitFeedback(String mobileSystem, String appVersion, int type, String content, String mobileModel, String contact, String topic,
                               Handler commitFailureHandler,
                               Handler responseErrorHandler,
                               Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_FEEDBACK_ADD;
        requestParameterEntry.version = "1.0.3";
        requestParameterEntry.addParameter("mobileSystem", mobileSystem);
        requestParameterEntry.addParameter("appVersion", appVersion);
        requestParameterEntry.addParameter("type", type);
        requestParameterEntry.addParameter("content", content);
        requestParameterEntry.addParameter("mobileModel", mobileModel);
        requestParameterEntry.addParameter("contact", contact);
        requestParameterEntry.addParameter("topic", topic);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SUBMIT_FEEDBACK;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取网关下的子网关列表
    public static void getSubGwList(Activity activity, String mac, String state, Callback callback) {
        RetrofitUtil.getInstance()
                .getSubGwList(activity, mac, state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getSubGwList(activity, mac, state, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 添加子网关信息
    public static void addSubGw(Activity activity, String mac, String subGatewayMac, String nickName, String position,
                                Callback callback) {
        RetrofitUtil.getInstance()
                .addSubGw(activity, mac, subGatewayMac, nickName, position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    addSubGw(activity, mac, subGatewayMac, nickName, position, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 修改子网关信息
    public static void updateSubGw(Activity activity, String mac, String subMac, String nickname, String position, Callback callback) {
        RetrofitUtil.getInstance()
                .updateSubGw(activity, mac, subMac, nickname, position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    updateSubGw(activity, mac, subMac, nickname, position, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 删除子网关信息
    public static void deleteSubGw(Activity activity, String mac, String subGatewayMac, Callback callback) {
        RetrofitUtil.getInstance()
                .deleteSubGw(activity, mac, subGatewayMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    deleteSubGw(activity, mac, subGatewayMac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void refreshToken(Context context, Callback callback) {
        RetrofitUtil.getInstance().refreshToken(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        callback.onNext(response);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static interface Callback {
        void onNext(JSONObject response);

        void onError(Throwable e);
    }
}
