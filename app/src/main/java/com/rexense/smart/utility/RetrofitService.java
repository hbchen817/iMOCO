package com.rexense.smart.utility;

import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.contract.Constant;

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
    Observable<JSONObject> querySceneList(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 根据IotId查询Mac
    @POST(Constant.QUERY_MAC_BY_IOTID)
    Observable<JSONObject> queryMacByIotId(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 增加本地场景
    @POST(Constant.ADD_SCENE)
    Observable<JSONObject> addScene(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 删除场景
    @POST(Constant.DELETE_SCENE)
    Observable<JSONObject> deleteScene(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 更新场景
    @POST(Constant.UPDATE_SCENE)
    Observable<JSONObject> updateScene(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 根据Mac查询IotId
    @POST(Constant.QUERY_IOT_ID_BY_MAC)
    Observable<JSONObject> queryIotIdByMac(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 根据子设备iotId查询网关iotId
    @POST(Constant.QUERY_GW_ID_BY_SUB_ID)
    Observable<JSONObject> getGWIotIdBySubIotId(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 滑动图片获取
    @POST(Constant.GET_PV_CODE)
    Observable<JSONObject> getPVCode(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 短信发送
    @POST(Constant.SEND_SMS_VERIFY_CODE)
    Observable<JSONObject> sendSMSVerifyCode(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 帐号注册
    @POST(Constant.ACCOUNTS_REG)
    Observable<JSONObject> accountsReg(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 帐号密码认证、登录
    @POST(Constant.AUTH_ACCOUNTS_PWD)
    Observable<JSONObject> authAccountsPwd(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 短信验证码认证、登录
    @POST(Constant.AUTH_ACCOUNTS_VC)
    Observable<JSONObject> authAccountsVC(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 密码重置
    @POST(Constant.PWD_RESET)
    Observable<JSONObject> pwdReset(@Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 获取AuthCode
    @POST(Constant.GET_AUTH_CODE)
    Observable<JSONObject> getAuthCode(@Header("DEVICE-ID") String devId, @Header("REX-TOKEN") String token, @Body RequestBody body);

    // token刷新
    @POST(Constant.REFRESH_TOKEN)
    Observable<JSONObject> refreshToken(@Header("REX-REFRESH-TOKEN") String refreshToken, @Header("DEVICE-ID") String devId);

    // 密码修改
    @POST(Constant.PWD_CHANGE)
    Observable<JSONObject> pwdChange(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    // 用户注销接口（账号系统）
    @POST(Constant.CANCELLATION)
    Observable<JSONObject> cancellation(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId);

    // 用户注销接口（iot系统）
    @POST(Constant.CANCELLATION_IOT)
    Observable<JSONObject> cancellationIot(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId);

    // 查询用户信息
    @POST(Constant.GET_CACCOUNTS_INFO)
    Observable<JSONObject> getCaccountsInfo(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId);

    // 编辑用户信息
    @POST(Constant.UPDATE_CACCOUNTS_INFO)
    Observable<JSONObject> updateCaccountsInfo(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取网关下的子网关列表
    @POST(Constant.GET_SUB_GW_LIST)
    Observable<JSONObject> getSubGwList(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取子网关所关联的主网关Mac，校验子网关有没有被绑定
    @POST(Constant.VERIFY_SUB_GW_MAC)
    Observable<JSONObject> verifySubGwMac(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 添加子网关信息
    @POST(Constant.ADD_SUB_GW)
    Observable<JSONObject> addSubGw(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 修改子网关信息
    @POST(Constant.UPDATE_SUB_GW)
    Observable<JSONObject> updateSubGw(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 删除子网关信息
    @POST(Constant.DELETE_SUB_GW)
    Observable<JSONObject> deleteSubGw(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取绑定关系
    @POST(Constant.GET_BIND_RELATION)
    Observable<JSONObject> getBindRelation(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取Mac所有路绑定关系
    @POST(Constant.GET_ALL_BIND_RELATION)
    Observable<JSONObject> getAllBindRelation(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 添加绑定关系
    @POST(Constant.ADD_BIND_RELATION)
    Observable<JSONObject> addBindRelation(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 取消绑定关系
    @POST(Constant.CANCEL_BIND_RELATION)
    Observable<JSONObject> cancelBindRelation(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取多控组列表
    @POST(Constant.GET_MULTI_CONTROL)
    Observable<JSONObject> getMultiControl(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 添加/编辑多控组
    @POST(Constant.ADD_OR_EDIT_MULTI_CONTROL)
    Observable<JSONObject> addOrEditMultiControl(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 删除组关系接口
    @POST(Constant.DEL_MULTI_CONTROL_GROUP)
    Observable<JSONObject> delMultiControlGroup(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 修改组昵称
    @POST(Constant.EDIT_CONTROL_GROUP_NAME)
    Observable<JSONObject> editControlGroupName(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 删除子设备相关多控组
    @POST(Constant.DEL_SUB_MAC_CONTROL_GROUP)
    Observable<JSONObject> deleteSubMacControlGroup(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取Mac被绑定的路
    @POST(Constant.GET_AVAILABLE_KEY)
    Observable<JSONObject> getAvaliableKey(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 清空主网关下的多控组
    @POST(Constant.DEL_MAC_CONTROL_GROUP)
    Observable<JSONObject> deleteMacControlGroup(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 网关绑定到项目
    @POST(Constant.GW_BIND_TO_PROJECT)
    Observable<JSONObject> gwBindToProject(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody Body);

    // 获取数据转换规则
    @POST(Constant.GET_DATA_CONVERSION_RULES)
    Observable<JSONObject> getDataConversionRules(@Header("REX-TOKEN") String token, @Header("DEVICE-ID") String devId, @Body RequestBody body);

    @FormUrlEncoded
    @POST("/query")
    Observable<JSONObject> test(@Field("type") String type, @Field("postid") String postid);
}
