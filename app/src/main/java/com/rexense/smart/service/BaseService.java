package com.rexense.smart.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.model.EAPIChannel;
import com.rexense.smart.utility.Logger;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.view.IndexActivity;
import com.rexense.smart.view.StartActivity;

import java.util.Map;

public class BaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 提交失败处理器
    protected Handler mCommitFailureHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APICOMMITFAIL == msg.what) {
                EAPIChannel.commitFailEntry commitFailEntry = (EAPIChannel.commitFailEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]失败:", commitFailEntry.path));
                if (commitFailEntry.parameters != null && commitFailEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : commitFailEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                Logger.e(sb.toString());
                String exceptionInfo = commitFailEntry.exception != null ? commitFailEntry.exception.getMessage() : "";
                //Toast.makeText(BaseActivity.this, String.format(getString(R.string.api_commitfailure), commitFailEntry.path, exceptionInfo), Toast.LENGTH_LONG).show();
                Toast.makeText(BaseService.this, getString(R.string.api_commitfailure_hint), Toast.LENGTH_LONG).show();
                notifyFailureOrError(1);
            }
            return false;
        }
    });

    // 响应错误处理器
    protected Handler mResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
//                    LoginBusiness.refreshSession(true, new IRefreshSessionCallback() {
//                        @Override
//                        public void onRefreshSuccess() {
//                            Logger.e("刷新Session成功");
//                        }
//
//                        @Override
//                        public void onRefreshFailed() {
//                            Logger.e("刷新Session失败 账户在其它端登录 退出登录");
                            logOut();
                            return false;
//                        }
//                    });
                }
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
//                    LoginBusiness.refreshSession(true, new IRefreshSessionCallback() {
//                        @Override
//                        public void onRefreshSuccess() {
//                            Logger.e("刷新Session成功");
//                        }
//
//                        @Override
//                        public void onRefreshFailed() {
//                            Logger.e("刷新Session失败 账户在其它端登录 退出登录");
                    logOut();
                    return false;
//                        }
//                    });
                }
                //非OTA信息查询失败才作提示
                if (!responseErrorEntry.path.equalsIgnoreCase(Constant.API_PATH_GETOTAFIRMWAREINFO)) {
                    //Toast.makeText(BaseActivity.this, String.format(getString(R.string.api_responseerror), responseErrorEntry.path, responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                    Toast.makeText(BaseService.this, TextUtils.isEmpty(responseErrorEntry.localizedMsg) ? getString(R.string.api_responseerror_hint) : responseErrorEntry.localizedMsg, Toast.LENGTH_LONG).show();
                }
                notifyFailureOrError(2);
            }
            return false;
        }
    });

    // 通知提交失败或响应错误
    protected void notifyFailureOrError(int type) {
    }

    protected void logOut() {//todo 其他设备登录后强制退出
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                ToastUtils.showToastCentrally(BaseService.this, getString(R.string.account_other_device_login));
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                IndexActivity.mainActivity.finish();
            }

            @Override
            public void onLogoutFailed(int code, String error) {
                ToastUtils.showToastCentrally(BaseService.this, getString(R.string.account_logout_failed) + error);
            }
        });
    }
}
