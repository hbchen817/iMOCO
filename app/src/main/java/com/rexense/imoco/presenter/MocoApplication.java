package com.rexense.imoco.presenter;

import androidx.multidex.MultiDex;

import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.view.OALoginActivity;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 应用
 */
public class MocoApplication extends AApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        //设置日志级别
        Logger.setLogLevel(2);

        //安装MultiDex
        MultiDex.install(this);
        Logger.d("The MultiDex installing completed.");

        // 系统参数初始化
        SystemParameter.initProcess(this);

        // 初始化SDK
        Initializer.sdkProcess(this);

        //登录页为我们自己的登录页
        OALoginAdapter adapter = (OALoginAdapter) LoginBusiness.getLoginAdapter();
        if (adapter != null) {
            adapter.setDefaultLoginClass(OALoginActivity.class);
        }

    }

}
