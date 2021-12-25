package com.rexense.smart.contract;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 帐号接口
 */
public class IAccount {
    // 登录回调接口
    public static interface loginCallback {
        // 返回登录结果
        // loginResult取值为：1表登录成功, 0表登录失败
        void returnLoginResult(int loginResult);
    }

    // 登出回调接口
    public static interface logoutCallback {
        // 返回登出结果
        // logoutResult取值为：1表登录成功, 0表登录失败
        void returnLogoutResult(int logoutResult);
    }
}

