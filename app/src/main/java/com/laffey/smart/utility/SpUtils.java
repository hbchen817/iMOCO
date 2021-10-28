package com.laffey.smart.utility;

import android.content.Context;
import android.content.SharedPreferences;

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
        return sp.getString("user_access_token", "");
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
}
