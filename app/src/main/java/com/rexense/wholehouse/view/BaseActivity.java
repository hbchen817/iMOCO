package com.rexense.wholehouse.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ResponseMessageUtil;
import com.rexense.wholehouse.utility.ToastUtils;

import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 基础界面
 */
public class BaseActivity extends FragmentActivity {
    // 提交失败处理器
    protected Handler mCommitFailureHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dismissQMUIDialog();
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
                Toast.makeText(BaseActivity.this, getString(R.string.api_commitfailure_hint), Toast.LENGTH_LONG).show();
                notifyFailureOrError(1);
            }
            return false;
        }
    });

    // 响应错误处理器
    protected Handler mResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dismissQMUIDialog();
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
//                    if (!LoginBusiness.isLogin()) {
//                        Logger.e("LoginBusiness.isLogin() = false");
                    logOut();
                    return false;
//                    } else {
//                        Logger.e("LoginBusiness.isLogin() = true");
//                    }
//                    LoginBusiness.refreshSession(true, new IRefreshSessionCallback() {
//                        @Override
//                        public void onRefreshSuccess() {
//                            Logger.e("刷新Session成功");
//                        }
//
//                        @Override
//                        public void onRefreshFailed() {
//                            Logger.e("刷新Session失败 账户在其它端登录 退出登录");
//                            logOut();
//                        }
//                    });
                }
                //非OTA信息查询失败才作提示
                if (!responseErrorEntry.path.equalsIgnoreCase(Constant.API_PATH_GETOTAFIRMWAREINFO)
                        && responseErrorEntry.code != 403 && responseErrorEntry.code != 6401) {
                    // 6401: 拓扑关系不存在
                    // 403：请求被禁止
                    // 28700: 设备未和用户绑定
                    // 10360: scene rule not exist
                    //Toast.makeText(BaseActivity.this, String.format(getString(R.string.api_responseerror), responseErrorEntry.path, responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                    Toast.makeText(BaseActivity.this, TextUtils.isEmpty(responseErrorEntry.localizedMsg) ? getString(R.string.api_responseerror_hint) : ResponseMessageUtil.replaceMessage(responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                }
                notifyResponseError(responseErrorEntry.code);
                notifyFailureOrError(2);
            }
            return false;
        }
    });

    // 通知提交失败或响应错误
    protected void notifyFailureOrError(int type) {
    }

    protected void notifyResponseError(int type) {
    }

    protected void dismissQMUIDialog() {
        QMUITipDialogUtil.dismiss();
    }

    public Context mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Log.i("当前Activity:", getClass().getSimpleName());
    }

    protected void logOut() {//todo 其他设备登录后强制退出
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                QMUITipDialogUtil.dismiss();
                ToastUtils.showToastCentrally(mActivity, getString(R.string.account_other_device_login));
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                IndexActivity.mainActivity.finish();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onLogoutFailed(int code, String error) {
                QMUITipDialogUtil.dismiss();
                ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
            }
        });

    }

    public void back(View view) {
        finish();
    }
}