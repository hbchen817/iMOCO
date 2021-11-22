package com.laffey.smart.model;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.TimeUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.framework.log.HttpLoggingInterceptor;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.MocoApplication;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.RetrofitService;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.view.LoginActivity;
import com.vise.log.ViseLog;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ERetrofit {
    private RetrofitService service;

    public static ERetrofit instance;
    public static int count = 0;

    public static ERetrofit getInstance() {
        synchronized (ERetrofit.class) {
            if (instance == null) {
                instance = new ERetrofit();
            }
        }
        return instance;
    }

    public static ERetrofit getInstance(String url) {
        synchronized (ERetrofit.class) {
            if (instance == null) {
                instance = new ERetrofit(url);
            }
        }
        return instance;
    }

    public ERetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("https://192.168.1.102:5443")
                .baseUrl(Constant.ACCOUNT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
        service = retrofit.create(RetrofitService.class);
    }

    public ERetrofit(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
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
                // ViseLog.d("Http请求参数：" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        // 定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        // OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        // https证书
        //httpClientBuilder.sslSocketFactory(getSSLContextForCertificate("dssServer.cer").getSocketFactory(), getTrustManager());
        // httpClientBuilder.sslSocketFactory(getSSLContextForCertificate("rexiotServer.cer").getSocketFactory(), getTrustManager());
        httpClientBuilder.sslSocketFactory(getSSLContextForCertificate("new6.cer").getSocketFactory(), getTrustManager());
        httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);
        return httpClientBuilder.build();
    }

    public RetrofitService getService() {
        return service;
    }

    public static RequestBody convertToBody(String json) {
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    /**
     * 读取证书内容到KeyStore中
     *
     * @param fileName
     * @return
     */
    private static KeyStore getkeyStore(String fileName) {
        KeyStore keyStore = null;
        try {
            AssetManager assetManager = MocoApplication.getInstance().getAssets();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = assetManager.open(fileName);
            Certificate ca;
            try {
                ca = cf.generateCertificate(is);
            } finally {
                is.close();
            }
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    /**
     * 生成SSLContext以便用于Retrofit的配置中
     *
     * @param fileName
     * @return
     */
    private static SSLContext getSSLContextForCertificate(String fileName) {
        SSLContext sslContext = null;
        try {
            KeyStore keyStore = getkeyStore(fileName);
            sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (Exception e) {
            ViseLog.e(e);
            e.printStackTrace();
        }
        return sslContext;
    }

    private static X509TrustManager getTrustManager() {
        X509TrustManager x509TrustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            x509TrustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            ViseLog.e(e);
            e.printStackTrace();
        }
        return x509TrustManager;
    }

    // token超时自动获取刷新
    public static Function<Observable<Throwable>, ObservableSource<?>> retryTokenFun(Context context) {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@io.reactivex.annotations.NonNull Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof HttpException) {
                            HttpException exception = (HttpException) throwable;
                            ViseLog.d("刷新token = " + exception.code());
                            if (exception.code() == 401) {
                                if (count == 0) {
                                    count++;
                                    Observable<JSONObject> o = RetrofitUtil.getInstance().refreshToken(context)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .unsubscribeOn(Schedulers.io())
                                            .doOnNext(new Consumer<JSONObject>() {
                                                @Override
                                                public void accept(JSONObject jsonObject) throws Exception {
                                                    String accessToken = jsonObject.getString("accessToken");
                                                    String refreshToken = jsonObject.getString("refreshToken");
                                                    SpUtils.putAccessToken(context, accessToken);
                                                    SpUtils.putRefreshToken(context, refreshToken);
                                                }
                                            });
                                    ViseLog.d("o = " + GsonUtil.toJson(o));
                                    return o;
                                } else {
                                    count = 0;
                                    LoginActivity.start(context, null);
                                    return Observable.error(throwable);
                                }
                            } else return Observable.error(throwable);
                        } else
                            return Observable.error(throwable);
                    }
                });
            }
        };
    }

}
