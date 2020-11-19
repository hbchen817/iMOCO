package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.IRefreshSessionCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.ResponseMessageUtil;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.utility.ToastUtils;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public abstract class BaseFragment extends Fragment {
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
                //Toast.makeText(getActivity(), String.format(getString(R.string.api_commitfailure), commitFailEntry.path, exceptionInfo), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), getString(R.string.api_commitfailure_hint), Toast.LENGTH_LONG).show();
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
                //Toast.makeText(getActivity(), String.format(getString(R.string.api_responseerror), responseErrorEntry.path, responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), TextUtils.isEmpty(responseErrorEntry.localizedMsg) ? getString(R.string.api_responseerror_hint) : ResponseMessageUtil.replaceMessage(responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
                notifyFailureOrError(2);
            }
            return false;
        }
    });
    public final String TAG = getClass().getSimpleName();

    // 通知提交失败或响应错误
    protected void notifyFailureOrError(int type) {
    }

    protected void logOut() {//todo 其他设备登录后强制退出
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.account_other_device_login));
                Intent intent = new Intent(mActivity.getApplicationContext(), StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                IndexActivity.mainActivity.finish();
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }

            @Override
            public void onLogoutFailed(int code, String error) {
                ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
            }
        });
    }

    /**
     * Fragment所依赖的Activity的上下文
     */
    public Activity mActivity;

    public Unbinder mUnbinder;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        mActivity = getActivity();

        rootView = inflater.inflate(setLayout(), container, false);

        // 绑定ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        init();

        return rootView;
    }

    /**
     * 设置当前碎片的布局
     *
     * @return 布局文件的id
     */
    protected abstract int setLayout();

    /**
     * 做一些初始化的工作, 如initView, initData, initAdapter, initListener等
     */
    protected abstract void init();

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 反绑定ButterKnife
        mUnbinder.unbind();
    }

}
