package com.laffey.smart.utility;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.model.ItemScene;
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
    public Observable<JSONObject> queryMacByIotId(String token, String apiVer, String plantForm, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", apiVer);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(String token, String plantForm, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(String token, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", "xxxxxx");
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 查询本地场景列表
    public Observable<JSONObject> querySceneList(String token, String mac, String type) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_SCENE_LIST_VER);

        JSONObject params = new JSONObject();
        if (mac != null && mac.length() > 0)
            params.put("mac", mac);
        params.put("type", type);

        object.put("params", params);

        return getRetrofitService().querySceneList(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(String token, String apiVer, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", apiVer);
        object.put("params", scene);
        ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(String token, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        object.put("params", scene);
        // ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除场景
    public Observable<JSONObject> deleteScene(String token, String gatewayMac, String sceneId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DELETE_SCENE_VER);

        JSONObject params = new JSONObject();
        params.put("mac", gatewayMac);
        params.put("sceneId", sceneId);
        object.put("params", params);

        return getRetrofitService().deleteScene(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 更新场景
    public Observable<JSONObject> updateScene(String token, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.UPDATE_SCENE_VER);
        object.put("params", scene);
        return getRetrofitService().updateScene(token, ERetrofit.convertToBody(object.toJSONString()));
    }

    // 根据Mac查询IotId
    public Observable<JSONObject> queryIotIdByMac(String token, String plantForm, String mac) {
        JSONObject obj = new JSONObject();
        obj.put("apiVer", Constant.QUERY_IOT_ID_BY_MAC_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("mac", mac);

        obj.put("params", params);

        return getRetrofitService().queryIotIdByMac(token, ERetrofit.convertToBody(obj.toJSONString()));
    }

    // 根据子设备iotId查询网关iotId
    public Observable<JSONObject> getGWIotIdBySubIotId(String token, String subId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        JSONObject params = new JSONObject();
        params.put("plantForm", Constant.PLANT_FORM);
        params.put("subIotId", subId);
        object.put("params", params);
        return getRetrofitService().getGWIotIdBySubIotId(token, ERetrofit.convertToBody(object.toJSONString()));
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
}
