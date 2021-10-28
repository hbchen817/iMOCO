package com.laffey.smart.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.utility.RetrofitUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccountManager {

    // 滑动图片获取
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

    // 短信发送
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

    // 帐号注册
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

    // 帐号密码认证、登录
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

    // 密码重置
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
}
