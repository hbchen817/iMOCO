package com.rexense.smart.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.model.ERetrofit;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.view.LoginActivity;
import com.vise.log.ViseLog;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
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

    // 滑动图片获取（无token）
    public static void getPVCode(Activity activity, Callback callback) {
        RetrofitUtil.getInstance().getPVCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getPVCode(activity, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
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

    // 短信发送（无token）
    public static void sendSMSVerifyCode(Activity activity, String telNum, String codeType, String pvCode,
                                         Callback callback) {
        RetrofitUtil.getInstance().sendSMSVerifyCode(telNum, codeType, pvCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    sendSMSVerifyCode(activity, telNum, codeType, pvCode, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
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

    // 帐号注册（无token）
    public static void accountsReg(Activity activity, String telNum, String pwd, String verifyCode,
                                   Callback callback) {
        RetrofitUtil.getInstance().accountsReg(telNum, pwd, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    accountsReg(activity, telNum, pwd, verifyCode, callback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LoginActivity.start(activity, null);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
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

    // 帐号密码认证、登录（无token）
    public static void authAccountsPwd(Activity activity, String accounts, String pwd, Callback callback) {
        RetrofitUtil.getInstance().authAccountsPwd(accounts, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    authAccountsPwd(activity, accounts, pwd, callback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LoginActivity.start(activity, null);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 短信验证码认证、登录（无token）
    public static void authAccountsVC(Activity activity, String telNum, String verifyCode, Callback callback) {
        RetrofitUtil.getInstance().authAccountsVC(telNum, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    authAccountsVC(activity, telNum, verifyCode, callback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LoginActivity.start(activity, null);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
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

    // 密码重置（无token）
    public static void pwdReset(Activity activity, String telNum, String pwd, String verifyCode,
                                Callback callback) {
        RetrofitUtil.getInstance().pwdReset(telNum, pwd, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    pwdReset(activity, telNum, pwd, verifyCode, callback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LoginActivity.start(activity, null);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取AuthCode
    public static void getAuthCode(Context context, int resultTag, int errorTag, Handler resulthandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().getAuthCode(context);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    // 获取AuthCode
    public static void getAuthCode(Activity activity, Callback callback) {
        RetrofitUtil.getInstance().getAuthCode(activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getAuthCode(activity, callback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LoginActivity.start(activity, null);
                                    activity.finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 密码修改
    public static void pwdChange(Context context, String oldPwd, String newPwd, int resultTag,
                                 int errorTag, Handler resultHandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().pwdChange(context, oldPwd, newPwd);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    // 用户注销接口（账号系统）
    public static void cancellation(Context context, int resultTag, int errorTag, Handler resultHandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().cancellation(context);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    // 用户注销接口（iot系统）
    public static void cancellationIot(Context context, int resultTag, int errorTag, Handler resultHandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().cancellationIot(context);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    // 查询用户信息
    public static void getCaccountsInfo(Context context, int resultTag, int errorTag, Handler resultHandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().getCaccountsInfo(context);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    // 编辑用户信息
    public static void updateCaccountsInfo(Context context, String email, String nickName, String headPortrait,
                                           String gender, String area, String personalSignature,
                                           int resultTag, int errorTag, Handler resultHandler) {
        Observable.just(new JSONObject())
                .flatMap(new Function<JSONObject, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(@NonNull JSONObject jsonObject) throws Exception {
                        return RetrofitUtil.getInstance().updateCaccountsInfo(context, email, nickName,
                                headPortrait, gender, area, personalSignature);
                    }
                })
                .subscribeOn(Schedulers.io())
                .retryWhen(ERetrofit.retryTokenFun(context))
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

    public interface Callback {
        void onNext(JSONObject response);

        void onError(Throwable e);
    }
}
