package com.laffey.smart.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class AccountManager {

    // 滑动图片获取（无token）
    public static void getPVCode(int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().getPVCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 短信发送（无token）
    public static void sendSMSVerifyCode(String telNum, String codeType, String pvCode,
                                         int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().sendSMSVerifyCode(telNum, codeType, pvCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 帐号注册（无token）
    public static void accountsReg(String telNum, String pwd, String verifyCode,
                                   int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().accountsReg(telNum, pwd, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 帐号密码认证、登录（无token）
    public static void authAccountsPwd(String accounts, String pwd,
                                       int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().authAccountsPwd(accounts, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 密码重置（无token）
    public static void pwdReset(String telNum, String pwd, String verifyCode,
                                int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().pwdReset(telNum, pwd, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取AuthCode
    public static void getAuthCode(Context context, int resultTag, int errorTag, Handler resulthandler) {
        RetrofitUtil.getInstance().getAuthCode(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resulthandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException exception = (HttpException) e;
                            ViseLog.d(exception.code() + " , " + exception.message());
                            if (exception.code() == 401) {
                                // 重新申请token
                            } else {
                                Message msg = resulthandler.obtainMessage();
                                msg.what = errorTag;
                                msg.obj = e;
                                msg.sendToTarget();
                            }
                        } else {
                            Message msg = resulthandler.obtainMessage();
                            msg.what = errorTag;
                            msg.obj = e;
                            msg.sendToTarget();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // token刷新
    public static void refreshToken(Context context, int resultTag, int errorTag, Handler resultHandler) {
        RetrofitUtil.getInstance().refreshToken(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code == 200) {
                            String accessToken = response.getString("accessToken");
                            String refreshToken = response.getString("refreshToken");
                            SpUtils.putAccessToken(context, accessToken);
                            SpUtils.putRefreshToken(context, refreshToken);

                            getAuthCode(context, resultTag, errorTag, resultHandler);
                        } else {
                            Message msg = resultHandler.obtainMessage();
                            msg.what = resultTag;
                            msg.obj = response;
                            msg.sendToTarget();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resultHandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 密码修改
    public static void pwdChange(Context context, String oldPwd, String newPwd, int resultTag,
                                 int errorTag, Handler resultHandler) {
        RetrofitUtil.getInstance().pwdChange(context, oldPwd, newPwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        Message msg = resultHandler.obtainMessage();
                        msg.what = resultTag;
                        msg.obj = response;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message msg = resultHandler.obtainMessage();
                        msg.what = errorTag;
                        msg.obj = e;
                        msg.sendToTarget();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
