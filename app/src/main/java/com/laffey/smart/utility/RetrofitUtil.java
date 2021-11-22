package com.laffey.smart.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.model.ItemBindList;
import com.laffey.smart.model.ItemBindRelation;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.view.LoginActivity;
import com.vise.log.ViseLog;

import java.util.List;

import io.reactivex.Observable;

public class RetrofitUtil {
    private static RetrofitUtil instance;

    private static int mRetryCount = 0;

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

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(Context context, String plantForm, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(Context context, String iotId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", Constant.PLANT_FORM);
        params.put("iotId", iotId);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    public Observable<JSONObject> queryMacByIotId(Activity activity, List<String> iotIdList) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_MAC_BY_IOTID_VER);

        JSONObject params = new JSONObject();
        params.put("platform", Constant.PLANT_FORM);
        params.put("iotIdList", iotIdList);

        object.put("params", params);

        return getRetrofitService().queryMacByIotId(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 查询本地场景列表
    public Observable<JSONObject> querySceneList(Context context, String mac, String type) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.QUERY_SCENE_LIST_VER);

        JSONObject params = new JSONObject();
        if (mac != null && mac.length() > 0)
            params.put("mac", mac);
        if (type != null && type.length() > 0)
            params.put("type", type);

        object.put("params", params);

        // ViseLog.d("查询本地场景列表 SpUtils.getAccessToken(context) = " + SpUtils.getAccessToken(context) + "\nmac = " + mac);
        return getRetrofitService().querySceneList(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(Context context, String apiVer, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", apiVer);
        object.put("params", scene);
        // ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 增加本地场景
    public Observable<JSONObject> addScene(Context context, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        object.put("params", scene);
        // ViseLog.d(GsonUtil.toJson(object));
        return getRetrofitService().addScene(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除场景
    public Observable<JSONObject> deleteScene(Context context, String gatewayMac, String sceneId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DELETE_SCENE_VER);

        JSONObject params = new JSONObject();
        params.put("mac", gatewayMac);
        params.put("sceneId", sceneId);
        object.put("params", params);

        // ViseLog.d("删除场景ssss sceneId = " + sceneId + " , context = " + context.getClass().toString());
        return getRetrofitService().deleteScene(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 更新场景
    public Observable<JSONObject> updateScene(Context context, ItemSceneInGateway scene) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.UPDATE_SCENE_VER);
        object.put("params", scene);
        return getRetrofitService().updateScene(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 根据Mac查询IotId
    public Observable<JSONObject> queryIotIdByMac(Context context, String plantForm, String mac) {
        JSONObject obj = new JSONObject();
        obj.put("apiVer", Constant.QUERY_IOT_ID_BY_MAC_VER);

        JSONObject params = new JSONObject();
        params.put("plantForm", plantForm);
        params.put("mac", mac);

        obj.put("params", params);

        return getRetrofitService().queryIotIdByMac(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(obj.toJSONString()));
    }

    // 根据子设备iotId查询网关iotId
    public Observable<JSONObject> getGWIotIdBySubIotId(Context context, String subId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SCENE_VER);
        JSONObject params = new JSONObject();
        params.put("plantForm", Constant.PLANT_FORM);
        params.put("subIotId", subId);
        object.put("params", params);
        return getRetrofitService().getGWIotIdBySubIotId(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 滑动图片获取
    public Observable<JSONObject> getPVCode() {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_PV_CODE_VER);
        JSONObject params = new JSONObject();
        object.put("params", params);

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

    // 短信验证码认证、登录
    public Observable<JSONObject> authAccountsVC(String telNum, String verifyCode) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.AUTH_ACCOUNTS_PWD_VER);
        JSONObject params = new JSONObject();
        params.put("appKey", Constant.APP_KEY);
        params.put("telNum", telNum);
        params.put("verifyCode", verifyCode);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().authAccountsVC(AppUtils.getPesudoUniqueID(),
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
        object.put("apiVer", Constant.GET_AUTH_CODE_VER);
        JSONObject params = new JSONObject();
        params.put("clientId", Constant.CLIENT_ID);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().getAuthCode(AppUtils.getPesudoUniqueID(),
                SpUtils.getAccessToken(context), ERetrofit.convertToBody(object.toJSONString()));
    }

    // token刷新
    public Observable<JSONObject> refreshToken(Context context) {
        ViseLog.d("refreshtoken = " + SpUtils.getRefreshToken(context));
        return new ERetrofit(Constant.ACCOUNT_URL).getService().refreshToken(
                SpUtils.getRefreshToken(context), AppUtils.getPesudoUniqueID());
    }

    // 密码修改
    public Observable<JSONObject> pwdChange(Context context, String oldPwd, String newPwd) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.PWD_CHANGE_VER);
        JSONObject params = new JSONObject();
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);
        params.put("newPwdConfirm", newPwd);
        object.put("params", params);

        return new ERetrofit(Constant.ACCOUNT_URL).getService().pwdChange(SpUtils.getAccessToken(context),
                AppUtils.getPesudoUniqueID(), ERetrofit.convertToBody(object.toJSONString()));
    }

    // 用户注销接口（账号系统）
    public Observable<JSONObject> cancellation(Context context) {
        return getRetrofitService().cancellation(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID());
    }

    // 用户注销接口（iot系统）
    public Observable<JSONObject> cancellationIot(Context context) {
        return getRetrofitService().cancellationIot(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID());
    }

    // 查询用户信息
    public Observable<JSONObject> getCaccountsInfo(Context context) {
        return getRetrofitService().getCaccountsInfo(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID());
    }

    // 编辑用户信息
    public Observable<JSONObject> updateCaccountsInfo(Context context, String email, String nickName,
                                                      String headPortrait, String gender, String area, String personalSignature) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.UPDATE_CACCOUNTS_INFO_VER);
        JSONObject params = new JSONObject();
        if (email != null && email.length() > 0)
            params.put("email", email);
        if (nickName != null && nickName.length() > 0)
            params.put("nickname", nickName);
        if (headPortrait != null && headPortrait.length() > 0)
            params.put("headPortrait", headPortrait);
        if (gender != null && gender.length() > 0)
            object.put("gender", gender);
        if (area != null && area.length() > 0)
            object.put("area", area);
        if (personalSignature != null && personalSignature.length() > 0)
            object.put("personalSignature", personalSignature);
        object.put("params", params);

        return getRetrofitService().updateCaccountsInfo(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取网关下的子网关列表
    public Observable<JSONObject> getSubGwList(Context context, String mac, String state) {
        // state : 子网关状态 0-未激活，1-已激活，2-所有
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_SUB_GW_LIST_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("state", state);
        object.put("params", params);

        return getRetrofitService().getSubGwList(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 添加子网关信息
    public Observable<JSONObject> addSubGw(Context context, String mac, String subGatewayMac, String nickName, String position) {
        // state : 子网关状态 0-未激活，1-已激活，2-所有
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_SUB_GW_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("subGatewayMac", subGatewayMac);
        params.put("nickname", nickName);
        params.put("position", position);
        object.put("params", params);

        return getRetrofitService().addSubGw(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 修改子网关信息
    public Observable<JSONObject> updateSubGw(Context context, String mac, String subMac, String nickname, String position) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.UPDATE_SUB_GW_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("subMac", subMac);
        params.put("nickname", nickname);
        params.put("position", position);
        object.put("params", params);

        return getRetrofitService().updateSubGw(SpUtils.getAccessToken(context), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除子网关信息
    public Observable<JSONObject> deleteSubGw(Activity activity, String mac, String subGatewayMac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DELETE_SUB_GW_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("subGatewayMac", subGatewayMac);
        object.put("params", params);

        return getRetrofitService().deleteSubGw(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取绑定关系
    public Observable<JSONObject> getBindRelation(Activity activity, String mac, String endpoint, String groupId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_BIND_RELATION_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("endpoint", endpoint);
        if (groupId != null && groupId.length() > 0)
            params.put("groupId", groupId);
        object.put("params", params);

        return getRetrofitService().getBindRelation(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取Mac所有路绑定关系
    public Observable<JSONObject> getAllBindRelation(Activity activity, String mac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_ALL_BIND_RELATION_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        object.put("params", params);

        return getRetrofitService().getAllBindRelation(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 添加绑定关系
    public Observable<JSONObject> addBindRelation(Activity activity, String name, List<ItemBindRelation> list) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_BIND_RELATION_VER);
        JSONObject params = new JSONObject();
        params.put("name", name);
        params.put("bindList", JSONObject.parseArray(GsonUtil.toJson(list)));
        object.put("params", params);

        return getRetrofitService().addBindRelation(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 取消绑定关系
    public Observable<JSONObject> cancelBindRelation(Activity activity, String mac, String endpoint, boolean mainBind, String groupId) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.CANCEL_BIND_RELATION_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        params.put("endpoint", endpoint);
        params.put("mainBind", mainBind);
        params.put("groupId", groupId);
        object.put("params", params);

        return getRetrofitService().cancelBindRelation(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取多控组列表
    public Observable<JSONObject> getMultiControl(Activity activity, ItemBindRelation relation) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.CANCEL_BIND_RELATION_VER);
        JSONObject params = new JSONObject();
        params.put("subDevMac", relation.getSubDevMac());
        params.put("endpoint", relation.getEndpoint());
        object.put("params", params);

        return getRetrofitService().getMultiControl(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取多控组列表
    public Observable<JSONObject> getMultiControl(Activity activity, String subDevMac, String endpoint) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_MULTI_CONTROL_VER);
        JSONObject params = new JSONObject();
        params.put("subDevMac", subDevMac);
        params.put("endpoint", endpoint);
        object.put("params", params);

        return getRetrofitService().getMultiControl(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 添加/编辑多控组
    public Observable<JSONObject> addOrEditMultiControl(Activity activity, ItemBindList bindList) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.ADD_OR_EDIT_MULTI_CONTROL_VER);
        JSONObject params = new JSONObject();
        params.put("name", bindList.getName());
        params.put("mac", bindList.getMac());
        if (bindList.getGroupId() != null && bindList.getGroupId().length() > 0)
            params.put("groupId", bindList.getGroupId());
        params.put("bindList", bindList.getBindList());
        object.put("params", params);

        return getRetrofitService().addOrEditMultiControl(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除组关系接口
    public Observable<JSONObject> delMultiControlGroup(Activity activity, String groupId, String mac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DEL_MULTI_CONTROL_GROUP_VER);
        JSONObject params = new JSONObject();
        params.put("groupId", groupId);
        params.put("mac", mac);
        object.put("params", params);

        return getRetrofitService().delMultiControlGroup(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取子网关所关联的主网关Mac，校验子网关有没有被绑定
    public Observable<JSONObject> verifySubGwMac(Activity activity, String subMac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.VERIFY_SUB_GW_MAC_VER);
        JSONObject params = new JSONObject();
        params.put("subMac", subMac);
        object.put("params", params);

        return getRetrofitService().verifySubGwMac(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 修改组昵称
    public Observable<JSONObject> editControlGroupName(Activity activity, String groupId, String nickname) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.EDIT_CONTROL_GROUP_NAME_VER);
        JSONObject params = new JSONObject();
        params.put("groupId", groupId);
        params.put("nickname", nickname);
        object.put("params", params);

        return getRetrofitService().editControlGroupName(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 删除子设备相关多控组
    public Observable<JSONObject> deleteSubMacControlGroup(Activity activity, String subDevMac, String mac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DEL_SUB_MAC_CONTROL_GROUP_VER);
        JSONObject params = new JSONObject();
        params.put("subDevMac", subDevMac);
        params.put("mac", mac);
        object.put("params", params);

        return getRetrofitService().deleteSubMacControlGroup(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 获取Mac被绑定的路
    public Observable<JSONObject> getAvaliableKey(Activity activity, String subDevMac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.GET_AVAILABLE_KEY_VER);
        JSONObject params = new JSONObject();
        params.put("subDevMac", subDevMac);
        object.put("params", params);

        return getRetrofitService().getAvaliableKey(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    // 清空主网关下的多控组
    public Observable<JSONObject> deleteMacControlGroup(Activity activity, String mac) {
        JSONObject object = new JSONObject();
        object.put("apiVer", Constant.DEL_MAC_CONTROL_GROUP_VER);
        JSONObject params = new JSONObject();
        params.put("mac", mac);
        object.put("params", params);

        return getRetrofitService().deleteMacControlGroup(SpUtils.getAccessToken(activity), AppUtils.getPesudoUniqueID(),
                ERetrofit.convertToBody(object.toJSONString()));
    }

    public static void showErrorMsg(Activity activity, JSONObject response) {
        String message = response.getString("message");
        String localizedMsg = response.getString("localizedMsg");
        String errorMess = response.getString("errorMess");
        String errorCode = response.getString("errorCode");
        if (message != null && message.length() > 0) {
            ToastUtils.showLongToast(activity, message);
        } else if (localizedMsg != null && localizedMsg.length() > 0) {
            ToastUtils.showLongToast(activity, localizedMsg);
        } else if (errorMess != null && errorMess.length() > 0) {
            ToastUtils.showLongToast(activity, errorMess);
        } else {
            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
        }
        int code = response.getInteger("code");
        if (code == 10100) {
            LoginActivity.start(activity, null);
        }
    }

    public static void showErrorMsg(Activity activity, JSONObject response, Callback callback) {
        int code = response.getInteger("code");
        if (code == 10100) {
            LoginActivity.start(activity, null);
            activity.finish();
        } else if (code == 401) {
            if (mRetryCount <= 2) {
                mRetryCount++;
                UserCenter.refreshToken(activity, new UserCenter.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        int code = response.getInteger("code");
                        if (code == 200) {
                            String accessToken = response.getString("accessToken");
                            String refreshToken = response.getString("refreshToken");
                            SpUtils.putAccessToken(activity, accessToken);
                            SpUtils.putRefreshToken(activity, refreshToken);
                            callback.onNext(response);
                        } else {
                            LoginActivity.start(activity, null);
                            activity.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoginActivity.start(activity, null);
                        activity.finish();
                    }
                });
            } else {
                mRetryCount = 0;
                LoginActivity.start(activity, null);
                activity.finish();
            }
        } else {
            String message = response.getString("message");
            String localizedMsg = response.getString("localizedMsg");
            String errorMess = response.getString("errorMess");
            String errorCode = response.getString("errorCode");
            if (message != null && message.length() > 0) {
                ToastUtils.showLongToast(activity, message);
            } else if (localizedMsg != null && localizedMsg.length() > 0) {
                ToastUtils.showLongToast(activity, localizedMsg);
            } else if (errorMess != null && errorMess.length() > 0) {
                if ("05".equals(errorCode)) {
                    // 发送较为频繁,1小时后重试!
                    activity.finish();
                }
                ToastUtils.showLongToast(activity, errorMess);
            } else {
                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
            }
        }
    }

    public static interface Callback {
        void onNext(JSONObject response);

        void onError(Throwable e);
    }
}
