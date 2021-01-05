package com.laffey.smart.presenter;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.multidex.MultiDex;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.gary.hi.library.log.HiConsolePrinter;
import com.gary.hi.library.log.HiFilePrinter;
import com.gary.hi.library.log.HiLogConfig;
import com.gary.hi.library.log.HiLogManager;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.utility.CrashHandler;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.view.OALoginActivity;
import com.tencent.bugly.crashreport.CrashReport;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

//import leakcanary.LeakCanary;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 应用
 */
public class MocoApplication extends AApplication {

    public static Context sContext;
    public static final boolean IS_DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        //设置日志级别
        Logger.setLogLevel(2);
        CrashReport.initCrashReport(getApplicationContext(), "e66b11bc4e", BuildConfig.DEBUG);

        //安装MultiDex
        MultiDex.install(this);
        Logger.d("The MultiDex installing completed.");

        // 系统参数初始化
        SystemParameter.initProcess(this);

        // 初始化SDK
        Initializer.sdkProcess(this);

        //登录页为自定制的登录页
        OALoginAdapter adapter = (OALoginAdapter) LoginBusiness.getLoginAdapter();
        if (adapter != null) {
            adapter.setDefaultLoginClass(OALoginActivity.class);
        }
        initLog();
//        IoTSmart.setDebug(true);
//        IoTAPIClientImpl.getInstance().registerTracker(new Tracker() {
//            final String TAG = "APIGatewaySDKDele";
//            @Override
//            public void onSend(IoTRequest request) {
//                //ALog.i(TAG, "onSend:\r\n" + toString(request));
//                Log.i(TAG, "onSend:\r\n" + toString(request));
//            }
//            @Override
//            public void onRealSend(IoTRequestWrapper ioTRequest) {
//                //ALog.d(TAG, "onRealSend:\r\n" + toString(ioTRequest));
//                Log.d(TAG, "onRealSend:\r\n" + toString(ioTRequest));
//            }
//            @Override
//            public void onRawFailure(IoTRequestWrapper ioTRequest, Exception e) {
//                //ALog.d(TAG, "onRawFailure:\r\n" + toString(ioTRequest) + "ERROR-MESSAGE:" + e.getMessage());
//                Log.d(TAG, "onRawFailure:\r\n" + toString(ioTRequest) + "ERROR-MESSAGE:" + e.getMessage());
//                e.printStackTrace();
//            }
//            @Override
//            public void onFailure(IoTRequest request, Exception e) {
//                //ALog.i(TAG, "onFailure:\r\n" + toString(request) + "ERROR-MESSAGE:" + e.getMessage());
//                Log.i(TAG, "onFailure:\r\n" + toString(request) + "ERROR-MESSAGE:" + e.getMessage());
//            }
//            @Override
//            public void onRawResponse(IoTRequestWrapper request, IoTResponse response) {
//                //ALog.d(TAG, "onRawResponse:\r\n" + toString(request) + toString(response));
//                Log.d(TAG, "onRawResponse:\r\n" + toString(request) + toString(response));
//            }
//            @Override
//            public void onResponse(IoTRequest request, IoTResponse response) {
//                //ALog.i(TAG, "onResponse:\r\n" + toString(request) + toString(response));
//                Log.i(TAG, "onResponse:\r\n" + toString(request) + toString(response));
//            }
//            private String toString(IoTRequest request) {
//                return new StringBuilder("Request:").append("\r\n")
//                        .append("url:").append(request.getScheme()).append("://").append(null == request.getHost() ? "" : request.getHost()).append(request.getPath()).append("\r\n")
//                        .append("apiVersion:").append(request.getAPIVersion()).append("\r\n")
//                        .append("params:").append(null == request.getParams() ? "" : JSON.toJSONString(request.getParams())).append("\r\n").toString();
//            }
//            private String toString(IoTRequestWrapper wrapper) {
//                IoTRequest request = wrapper.request;
//                return new StringBuilder("Request:").append("\r\n")
//                        .append("id:").append(wrapper.payload.getId()).append("\r\n")
//                        .append("apiEnv:").append("apiEnv").append("\r\n")
//                        .append("url:").append(request.getScheme()).append("://").append(TextUtils.isEmpty(wrapper.request.getHost()) ? "" : wrapper.request.getHost()).append(request.getPath()).append("\r\n")
//                        .append("apiVersion:").append(request.getAPIVersion()).append("\r\n")
//                        .append("params:").append(null == request.getParams() ? "" : JSON.toJSONString(request.getParams())).append("\r\n")
//                        .append("payload:").append(JSON.toJSONString(wrapper.payload)).append("\r\n").toString();
//            }
//            private String toString(IoTResponse response) {
//                return new StringBuilder("Response:").append("\r\n")
//                        .append("id:").append(response.getId()).append("\r\n")
//                        .append("code:").append(response.getCode()).append("\r\n")
//                        .append("message:").append(response.getMessage()).append("\r\n")
//                        .append("localizedMsg:").append(response.getLocalizedMsg()).append("\r\n")
//                        .append("data:").append(null == response.getData() ? "" : response.getData().toString()).append("\r\n").toString();
//            }
//        });

        ViseLog.getLogConfig()
                .configAllowLog(BuildConfig.DEBUG)
                .configShowBorders(true)
                .configTagPrefix("wyy")
                .configLevel(Log.VERBOSE);
        ViseLog.plant(new LogcatTree());

        SystemParameter.getInstance().setSceneItemWidth(getSceneItemWidth());
    }

    private void initLog(){
        HiLogManager.init(new HiLogConfig() {
            @Override
            public JsonParser injectJsonParser() {
                return new JsonParser() {
                    @Override
                    public String toJson(Object o) {
                        return JSON.toJSONString(o);
                    }
                };
            }

            @Override
            public String getGlobalTag() {
                return "LZM";
            }

            @Override
            public boolean enable() {
                return IS_DEBUG;
            }

            @Override
            public boolean includeThread() {
                return false;
            }

            @Override
            public int stackTraceDepth() {
                return 0;
            }
        },new HiConsolePrinter(), HiFilePrinter.getInstance( getFilesDir().getPath()+"/HiLog", 7 * 24 * 60 * 60 * 1000));
    }

    // 根据屏幕大小获取场景水平列表的列宽
    public int getSceneItemWidth() {
        float scale = getResources().getDisplayMetrics().density;
        int ten10 = (int) (scale * 10 + 0.5f);// 网格组件左右margin值
        int six6 = (int) (scale * 6 + 0.5f);// 每一个item直接间隔宽度

        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        Display display = manager.getDefaultDisplay();
        display.getSize(p);

        return (p.x - 2 * ten10 - six6) / 2;// 屏幕上展示两个item
    }
}
