package com.laffey.smart.utility;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.model.ItemSceneInGateway;
import com.vise.log.ViseLog;

import io.reactivex.Observable;

public class RetrofitUtil {
    private static RetrofitUtil instance;

    public static RetrofitUtil getInstance() {
        synchronized (RetrofitUtil.class) {
            if (instance == null) {
                instance = new RetrofitUtil();
            }
            return instance;
        }
    }

    public RetrofitUtil() {

    }

    private RetrofitService getRetrofitService() {
        return ERetrofit.getInstance().getService();
    }

    // 根据IotId查询Mac
    public Observable<JSONObject> queryMacByIotId(Context context, String apiVer, String plantForm, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", apiVer);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(Context context, String plantForm, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(Context context, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", Constant.PLANT_FORM);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 查询本地场景列表
    public Observable<JSONObject> querySceneList(Context context, String mac, String type) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_SCENE_LIST_VER);

        JSONObject params = new JSONObject();
        if (mac != null && mac.length() > 0)
            params.put("mac", mac);
        params.put("type", type);

        object.put("params", params);

        ViseLog.d("查询本地场景列表 SpUtils.getAccessToken(context) = " + SpUtils.getAccessToken(context));
        return getRetrofitService().querySceneList(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
        // String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJhcHBLZXkiOiJucFJ5aWZpYyIsImV4cCI6MTYzNTgzMDQ5MiwidHlwZSI6IkFDQ0VTUyIsImlhdCI6MTYzNTgyMzI5MiwiYWNjb3VudHNJZCI6ImZhODNlMDMxMDIwMjRhODU4NmVlOTY0ODIyMzVjYjZkIn0.GnTYLbn69xYnUWdkG6O6NaYlzp9AVyQEdrYCKL3eCHW63_Q3tELcOF-K536TBP-u1jbI_a7K2lI0HupR9SUiMEH_MsnMdEC_y77_kH4zIYm7uAPacGOOqU94-uywowrSaQMdoSc-DJh5RYfev0SnERAZO_vBRTLbQcppzqx9zrMfYjoWWK7i-EjmNvzHD6GVCzHDXA2Plfvv2xsKwwOlXiQvopdH_ufwu7LeHXf5yDZI0m4-GwYP45bmpth-VYSC-QmdVCtaHx0lPoGrcvN0Meq8vNj7Pjk5ri9g8HusFJXzOGXZ1cUIDdMvwjebxKUPyQk71-4i994fP4Iqv22ZWNU0nzWkbPoApglOrEZK7ijZDw__puFqFY7g-7rSUb-_MQEucdERMGm6jyWTq0lR0-yTAg6Em51TdufyYdOz0Vq2NL3F0YTTf-HXFyY-iZrXm5F651O2EPNju1zu-gaMXb9qAj6-oySAczLgzkiFHm4Q0Wo0meKrzGeW_tEzL1_o";
        // return getRetrofitService().querySceneList(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(Context context, String apiVer, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", apiVer);
        object.put("params", scene);
        ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(Context context, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        object.put("params", scene);
        // ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除场景
    public Observable<JSONObject> deleteScene(Context context, String gatewayMac, String sceneId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DELETE_SCENE_VER);

        JSONObject params = new JSONObject();
        params.put("mac", gatewayMac);
        params.put("sceneId", sceneId);
        object.put("params", params);

        ViseLog.d("删除场景ssss sceneId = " + sceneId + " , context = " + context.getClass().toString());
        return getRetrofitService().deleteScene(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 更新场景
    public Observable<JSONObject> updateScene(Context context, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.UPDATE_SCENE_VER);
        object.put("params", scene);
        return getRetrofitService().updateScene(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 根据Mac查询IotId
    public Observable<JSONObject> queryIotIdByMac(Context context, String plantForm, String mac) {
        JSONObject obj = new JSONObject();
        obj.put("apiVer", Constant.QUERY_IOT_ID_BY_MAC_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("mac", mac);

        obj.put("params", params);

        return getRetrofitService().queryIotIdByMac(SpUtils.getAccessToken(context), ERetrofit.convertToBody(obj.toJSONString()));
    }

    // 根据子设备iotId查询网关iotId
    public Observable<JSONObject> getGWIotIdBySubIotId(Context context, String subId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        JSONObject params = new JSONObject();
        params.put("plantForm", Constant.PLANT_FORM);
        params.put("subIotId", subId);
        object.put("params", params);
        return getRetrofitService().getGWIotIdBySubIotId(SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 滑动图片获取
    public Observable<JSONObject> getPVCode() {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_PV_CODE_VER);
        JSONObject params = new JSONObject();
        object.put("params", params);

        //return getRetrofitService().getPVCode(AppUtils.getPesudoUniqueID(), ERetrofit.convertToBody(object.toJSONString()));
        ViseLog.d("AppUtils.getPesudoUniqueID() = " + AppUtils.getPesudoUniqueID());
        return new ERetrofit(Constant.ACCOUNT_URL).getService().getPVCode(AppUtils.getPesudoUniqueID(), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 短信发送
    public Observable<JSONObject> sendSMSVerifyCode(String telNum, String codeType, String pvCode) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.SEND_SMS_VERIFY_CODE_VER);
        JSONObject params = new JSONObject();
        params.put("appKey", Constant.APP_KEY);
        params.put("telNum", telNum);
        params.put("codeType", codeType);
        if (pvCode != null && pvCode.length() > 0)
            params.put("pvCode", pvCode);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().sendSMSVerifyCode(AppUtils.getPesudoUniqueID(), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 帐号注册
    public Observable<JSONObject> accountsReg(String telNum, String pwd, String verifyCode) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ACCOUNTS_REG_VER);
        JSONObject params = new JSONObject();
        params.put("appKey", Constant.APP_KEY);
        params.put("telNum", telNum);
        params.put("pwd", pwd);
        params.put("pwdConfirm", pwd);
        params.put("verifyCode", verifyCode);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().accountsReg(AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 帐号密码认证、登录
    public Observable<JSONObject> authAccountsPwd(String accounts, String pwd) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.AUTH_ACCOUNTS_PWD_VER);
        JSONObject params = new JSONObject();
        params.put("appKey", Constant.APP_KEY);
        params.put("accounts", accounts);
        params.put("pwd", pwd);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().authAccountsPwd(AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 密码重置
    public Observable<JSONObject> pwdReset(String telNum, String pwd, String verifyCode) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.PWD_RESET_VER);
        JSONObject params = new JSONObject();
        params.put("appKey", Constant.APP_KEY);
        params.put("telNum", telNum);
        params.put("verifyCode", verifyCode);
        params.put("pwd", pwd);
        params.put("pwdConfirm", pwd);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().pwdReset(AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取AuthCode
    public Observable<JSONObject> getAuthCode(Context context) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.PWD_RESET_VER);
        JSONObject params = new JSONObject();
        params.put("clientId", Constant.CLIENT_ID);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().getAuthCode(AppUtils.getPesudoUniqueID(),
                SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // token刷新
    public Observable<JSONObject> refreshToken(Context context) {
        return new ERetrofit(Constant.ACCOUNT_URL).getService().refreshToken(
                SpUtils.getRefreshToken(context));
    }

    // 密码修改
    public Observable<JSONObject> pwdChange(Context context, String oldPwd, String newPwd) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.PWD_RESET_VER);
        JSONObject params = new JSONObject();
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);
        params.put("newPwdConfirm", newPwd);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().pwdChange(SpUtils.getAccessToken(context),
                AppUtils.getPesudoUniqueID(), ERetrofit.convertToBody(object.toJSONString()));
    }
}
