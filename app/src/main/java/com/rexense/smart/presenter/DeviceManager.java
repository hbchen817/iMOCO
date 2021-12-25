package com.rexense.smart.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.model.ItemBindList;
import com.rexense.smart.model.ItemBindRelation;
import com.rexense.smart.utility.RetrofitUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceManager {

    // 获取绑定关系
    public static void getBindRelation(Activity activity, String mac, String endpoint, String groupId, Callback callback) {
        RetrofitUtil.getInstance()
                .getBindRelation(activity, mac, endpoint, groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getBindRelation(activity, mac, endpoint, groupId, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取Mac所有路绑定关系
    public static void getAllBindRelation(Activity activity, String mac, Callback callback) {
        RetrofitUtil.getInstance()
                .getAllBindRelation(activity, mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getAllBindRelation(activity, mac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 添加绑定关系
    public static void addBindRelation(Activity activity, String name, List<ItemBindRelation> list, Callback callback) {
        RetrofitUtil.getInstance()
                .addBindRelation(activity, name, list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    addBindRelation(activity, name, list, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 取消绑定关系
    public static void cancelBindRelation(Activity activity, String mac, String endpoint, boolean mainBind, String groupId, Callback callback) {
        RetrofitUtil.getInstance()
                .cancelBindRelation(activity, mac, endpoint, mainBind, groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    cancelBindRelation(activity, mac, endpoint, mainBind, groupId, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 取消绑定关系
    public static void cancelBindRelation(Activity activity, ItemBindRelation relation, Callback callback) {
        RetrofitUtil.getInstance()
                .cancelBindRelation(activity, relation.getMac(), relation.getEndpoint(), /*relation.getMainBind()*/false, relation.getGroupId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    cancelBindRelation(activity, relation, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 根据iotId查询mac
    public static void queryMacByIotId(Activity activity, List<String> iotIdList, Callback callback) {
        RetrofitUtil.getInstance()
                .queryMacByIotId(activity, iotIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    queryMacByIotId(activity, iotIdList, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取多控组列表
    public static void getMultiControl(Activity activity, ItemBindRelation relation, Callback callback) {
        RetrofitUtil.getInstance()
                .getMultiControl(activity, relation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getMultiControl(activity, relation, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取多控组列表
    public static void getMultiControl(Activity activity, String subDevMac, String endpoint, Callback callback) {
        RetrofitUtil.getInstance()
                .getMultiControl(activity, subDevMac, endpoint)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getMultiControl(activity, subDevMac, endpoint, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 添加/编辑多控组
    public static void addOrEditMultiControl(Activity activity, ItemBindList bindList, Callback callback) {
        RetrofitUtil.getInstance()
                .addOrEditMultiControl(activity, bindList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    addOrEditMultiControl(activity, bindList, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 删除组关系接口
    public static void delMultiControlGroup(Activity activity, String groupId, String mac, Callback callback) {
        RetrofitUtil.getInstance()
                .delMultiControlGroup(activity, groupId, mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    delMultiControlGroup(activity, groupId, mac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取子网关所关联的主网关Mac，校验子网关有没有被绑定
    public static void verifySubGwMac(Activity activity, String subMac, Callback callback) {
        RetrofitUtil.getInstance()
                .verifySubGwMac(activity, subMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    verifySubGwMac(activity, subMac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 修改组昵称
    public static void editControlGroupName(Activity activity, String groupId, String nickname, Callback callback) {
        RetrofitUtil.getInstance()
                .editControlGroupName(activity, groupId, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    editControlGroupName(activity, groupId, nickname, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 删除子设备相关多控组
    public static void deleteSubMacControlGroup(Activity activity, String subDevMac, String mac, Callback callback) {
        RetrofitUtil.getInstance()
                .deleteSubMacControlGroup(activity, subDevMac, mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    deleteSubMacControlGroup(activity, subDevMac, mac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取Mac被绑定的路
    public static void getAvaliableKey(Activity activity, String subDevMac, Callback callback) {
        RetrofitUtil.getInstance()
                .getAvaliableKey(activity, subDevMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getAvaliableKey(activity, subDevMac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 清空主网关下的多控组
    public static void deleteMacControlGroup(Activity activity, String mac, Callback callback) {
        RetrofitUtil.getInstance()
                .deleteMacControlGroup(activity, mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    deleteMacControlGroup(activity, mac, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 网关绑定到项目
    public static void gwBindToProject(Activity activity, String productKey, String deviceName, Callback callback) {
        RetrofitUtil.getInstance()
                .gwBindToProject(activity, productKey, deviceName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    gwBindToProject(activity, productKey, deviceName, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 获取数据转换规则
    public static void getDataConversionRules(Activity activity, String productKey, Callback callback) {
        RetrofitUtil.getInstance()
                .getDataConversionRules(activity, productKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        if (code != 401) {
                            callback.onNext(response);
                        } else {
                            RetrofitUtil.showErrorMsg(activity, response, new RetrofitUtil.Callback() {
                                @Override
                                public void onNext(JSONObject response) {
                                    getDataConversionRules(activity, productKey, callback);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        callback.onError(e);
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
