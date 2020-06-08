package com.rexense.imoco.presenter;

import androidx.multidex.MultiDex;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.rexense.imoco.utility.Logger;

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
        SystemParameter.initProcess();

        // 初始化SDK
        Initializer.sdkProcess(this);
    }

}
