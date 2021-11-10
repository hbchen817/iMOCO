package com.laffey.smart.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.presenter.DeviceBuffer;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class SpUtils {

    public static final String SP_USER_INFO = "sp_user_info";
    public static final String SP_APP_INFO = "sp_app_info";
    public static final String SP_DEVS_INFO = "sp_devs_info";

    public static final String KEY_IS_FIRST = "key_is_first";//是否首次启动app

    public static final String PS_REQUEST_CAMERA_PERMISSION = "request_camera";

    /**
     * 存储boolean类型的数据
     */
    public static void putBooleanValue(Context context, String spName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取boolean类型的数据
     */
    public static boolean getBooleanValue(Context context, String spName, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 存储String类型的数据
     */
    public static void putStringValue(Context context, String spName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取String类型的数据
     */
    public static String getStringValue(Context context, String spName, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * 存储Long类型的数据
     */
    public static void putLongValue(Context context, String spName, String key, Long value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 获取Long类型的数据
     */
    public static Long getLongValue(Context context, String spName, String key, Long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    /**
     * 存储int类型的数据
     */
    public static void putIntValue(Context context, String spName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 获取int类型的数据
     */
    public static int getIntValue(Context context, String spName, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }


    /**
     * 删除关键字
     *
     * @param context
     * @param spName
     * @param key
     */
    public static void removeKey(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 记录本机登录过的账号
     */
    public static void putUserName(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name_list", value);
        editor.apply();
    }

    /**
     * 清除所有本机登录过的账号
     */
    public static void removeAllUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user_name_list");
        editor.apply();
    }

    /**
     * 获取本机登录过的账号（json字符串）
     */
    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        return sp.getString("user_name_list", "");
    }

    // 保存accessToken
    public static void putAccessToken(Context context, String accessToken) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_access_token", accessToken);
        editor.apply();
    }

    // 获取accessToken
    public static String getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        return sp.getString("user_access_token", "chengxunfei");
    }

    // 清除accessToken
    public static void removeAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user_access_token");
        editor.apply();
    }

    // 保存refreshToken
    public static void putRefreshToken(Context context, String refreshToken) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_refresh_token", refreshToken);
        editor.apply();
    }

    // 获取refreshToken
    public static String getRefreshToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        return sp.getString("user_refresh_token", "");
    }

    // 清除refreshToken
    public static void removeRefreshToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user_refresh_token");
        editor.apply();
    }

    // 保存refreshToken获取时间
    public static void putRefreshTokenTime(Context context, long time) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("user_refresh_token_time", time);
        editor.apply();
    }

    // 获取refreshToken保存时间
    public static long getRefreshTokenTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        return sp.getLong("user_refresh_token_time", -1);
    }

    // 清除refreshToken保存时间
    public static void removeRefreshTokenTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user_refresh_token_time");
        editor.apply();
    }

    // 保存用户信息
    public static void putCaccountsInfo(Context context, String info) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("caccounts_info", info);
        editor.apply();
    }

    // 用户账号
    public static String getCaccounts(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        String info = sp.getString("caccounts_info", null);
        if (info == null || info.length() == 0) {
            return null;
        } else {
            JSONObject o = JSONObject.parseObject(info);
            return o.getString("accounts");
        }
    }

    // 用户手机号
    public static String getTelNum(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        String info = sp.getString("caccounts_info", null);
        if (info == null || info.length() == 0) {
            return null;
        } else {
            JSONObject o = JSONObject.parseObject(info);
            return o.getString("telNum");
        }
    }

    // 用户昵称
    public static String getNickName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        String info = sp.getString("caccounts_info", null);
        if (info == null || info.length() == 0) {
            return null;
        } else {
            JSONObject o = JSONObject.parseObject(info);
            return o.getString("nickname");
        }
    }

    public static void putNickName(Context context, String nickName) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        String info = sp.getString("caccounts_info", null);
        if (info != null && info.length() >= 0) {
            JSONObject o = JSONObject.parseObject(info);
            o.put("nickname", nickName);

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("caccounts_info", o.toJSONString());
            editor.apply();
        }
    }

    // 用户注销接口
    public static void cancellation(Context context) {
        DeviceBuffer.initSceneBuffer();
        DeviceBuffer.initProcess();
        DeviceBuffer.initExtendedBuffer();

        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user_access_token");
        editor.remove("user_refresh_token");
        editor.remove("user_refresh_token_time");
        editor.remove("caccounts_info");
        editor.apply();
    }
}
