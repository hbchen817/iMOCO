package com.rexense.imoco.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 初始化者
 */
public class Initializer {
    // SDK初始化处理
    public static void sdkProcess(Context context) {
        // 设置参数
        IoTSmart.InitConfig initConfig = new IoTSmart.InitConfig();
        // REGION_ALL: 支持连接全球多个接入点，如果您只在中国内地出货，请设置为“REGION_CHINA_ONLY”，表示直连中国内地接入点。
        initConfig.setRegionType(IoTSmart.REGION_CHINA_ONLY);
        // 对应控制台上的测试版（PRODUCT_ENV_DEV）和正式版（PRODUCT_ENV_PROD）
        if(Constant.APPKEY.equalsIgnoreCase("29162669")){
            // 对应控制台上的测试版（PRODUCT_ENV_DEV）
            initConfig.setProductEnv(IoTSmart.PRODUCT_ENV_DEV);
        } else {
            // 对应控制台上的正式版（PRODUCT_ENV_PROD）
            initConfig.setProductEnv(IoTSmart.PRODUCT_ENV_PROD);
        }
        // 是否打开日志
        initConfig.setDebug(true);

        // 定制三方通道的离线推送，目前支持华为、小米和FCM
        IoTSmart.PushConfig pushConfig = new IoTSmart.PushConfig();
        // 替换为从FCM平台申请的id
        pushConfig.fcmApplicationId = "fcmid";
        // 替换为从FCM平台申请的sendid
        pushConfig.fcmSendId = "fcmsendid";
        // 替换为从小米平台申请的AppID
        pushConfig.xiaomiAppId = "XiaoMiAppId";
        // 替换为从小米平台申请的AppKey
        pushConfig.xiaomiAppkey = "XiaoMiAppKey";
        // 华为推送通道需要在AndroidManifest.xml里面添加从华为评审申请的appId
        initConfig.setPushConfig(pushConfig);

        //设置国家
        IoTSmart.Country country = new IoTSmart.Country();
        country.areaName = "中国大陆";
        country.code = "86";
        country.pinyin = "ZhongGuoDaLu";
        country.isoCode = "CHN";
        country.domainAbbreviation = "CN";
        IoTSmart.setCountry(country, null);

        // 初始化（App须继承自AApplication，否则会报错）
        IoTSmart.init((MocoApplication)context.getApplicationContext(), initConfig);
        //IoTSmart.setProductScope(IoTSmart.PRODUCT_SCOPE_PUBLISHED);
        Logger.d("The SDK initialization completed.");
    }
}
