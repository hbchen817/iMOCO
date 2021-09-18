package com.laffey.smart.utility;

import com.aliyun.iot.aep.sdk.framework.log.HttpLoggingInterceptor;
import com.vise.log.ViseLog;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    private RetrofitService service;

    public static RetrofitUtil instance;

    public static RetrofitUtil getInstance() {
        synchronized (RetrofitUtil.class) {
            if (instance == null) {
                instance = new RetrofitUtil();
            }
        }
        return instance;
    }

    public RetrofitUtil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.kuaidi100.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
        service = retrofit.create(RetrofitService.class);
    }

    /**
     * 获取OkHttpClient
     * 用于打印请求参数
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getOkHttpClient() {
        // 日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        // 新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //ViseLog.d("Http请求参数：" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        // 定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        // OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        return httpClientBuilder.build();
    }

    public RetrofitService getService() {
        return service;
    }
}
