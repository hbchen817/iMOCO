package com.rexense.imoco.presenter;

import com.rexense.imoco.contract.IAccount;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 用户账号助手
 */
public class AccountHelper {
    // 登录
    public static void login(IAccount.loginCallback loginCallback) {
        Logger.d("Start calling login interface of login plug-in.");
        Account.callLogin(loginCallback);
    }

    // 登出
    public static void logout(IAccount.logoutCallback logoutCallback) {
        Logger.d("Start calling logout interface of login plug-in.");
        Account.callLogout(logoutCallback);
    }
}