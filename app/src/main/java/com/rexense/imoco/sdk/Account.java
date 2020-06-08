package com.rexense.imoco.sdk;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.rexense.imoco.contract.IAccount;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 帐号
 */
public class Account {
    // 调用登录
    public static void callLogin(IAccount.loginCallback loginCallback) {
        IAccount.loginCallback mLoginCallback = loginCallback;
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                Logger.d("Complete calling login interface of login plug-in.");
                if(mLoginCallback != null)
                {
                    mLoginCallback.returnLoginResult(1);
                }
            }
            @Override
            public void onLoginFailed(int code, String error) {
                Logger.d("Complete calling login interface of login plug-in.");
                if(mLoginCallback != null)
                {
                    mLoginCallback.returnLoginResult(0);
                }
            }
        });
    }

    // 调用登出
    public static void callLogout(IAccount.logoutCallback logoutCallback) {
        IAccount.logoutCallback mLogoutCallback = logoutCallback;
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                Logger.d("Complete calling logout interface of login plug-in.");
                if(mLogoutCallback != null)
                {
                    mLogoutCallback.returnLogoutResult(1);
                }
            }
            @Override
            public void onLogoutFailed(int code, String error) {
                Logger.d("Complete calling logout interface of login plug-in.");
                if(mLogoutCallback != null)
                {
                    mLogoutCallback.returnLogoutResult(0);
                }
            }
        });
    }
}

