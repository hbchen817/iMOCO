package com.laffey.smart.utility;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.MacByIotIdRequest;
import com.laffey.smart.model.MacByIotIdResponse;
import com.laffey.smart.model.SceneListResponse;
import com.laffey.smart.model.TestResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitService {

    // 查询本地场景列表
    @POST(Constant.QUERY_SCENE_LIST)
    Observable<JSONObject> querySceneList(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 根据IotId查询Mac
    @POST(Constant.QUERY_MAC_BY_IOTID)
    Observable<JSONObject> queryMacByIotId(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 获取数据转换规则
    @POST(Constant.GET_DATA_CONVERSION_RULES)
    Observable<JSONObject> getDataConversionRules(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 增加本地场景
    @POST(Constant.ADD_SCENE)
    Observable<JSONObject> addScene(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 删除场景
    @POST(Constant.DELETE_SCENE)
    Observable<JSONObject> deleteScene(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 更新场景
    @POST(Constant.UPDATE_SCENE)
    Observable<JSONObject> updateScene(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 根据Mac查询IotId
    @POST(Constant.QUERY_IOT_ID_BY_MAC)
    Observable<JSONObject> queryIotIdByMac(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 根据子设备iotId查询网关iotId
    @POST(Constant.QUERY_GW_ID_BY_SUB_ID)
    Observable<JSONObject> getGWIotIdBySubIotId(@Header("REX_TOKEN") String token, @Body RequestBody body);

    // 滑动图片获取
    @POST(Constant.GET_PV_CODE)
    Observable<JSONObject> getPVCode(@Header("DEVICE_ID") String devId, @Body RequestBody body);

    // 短信发送
    @POST(Constant.SEND_SMS_VERIFY_CODE)
    Observable<JSONObject> sendSMSVerifyCode(@Header("DEVICE_ID") String devId, @Body RequestBody body);

    // 帐号注册
    @POST(Constant.ACCOUNTS_REG)
    Observable<JSONObject> accountsReg(@Header("DEVICE_ID") String devId, @Body RequestBody body);

    // 帐号密码认证、登录
    @POST(Constant.AUTH_ACCOUNTS_PWD)
    Observable<JSONObject> authAccountsPwd(@Header("DEVICE_ID") String devId, @Body RequestBody body);

    // 密码重置
    @POST(Constant.PWD_RESET)
    Observable<JSONObject> pwdReset(@Header("DEVICE_ID") String devId, @Body RequestBody body);

    // 获取AuthCode
    @POST(Constant.GET_AUTH_CODE)
    Observable<JSONObject> getAuthCode(@Header("DEVICE_ID") String devId, @Header("REX_TOKEN") String token, @Body RequestBody body);

    @FormUrlEncoded
    @POST("/query")
    Observable<JSONObject> test(@Field("type") String type, @Field("postid") String postid);
}
