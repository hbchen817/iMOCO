package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
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
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ResponseMessageUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.util.HashMap;
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
                //Toast.makeText(getActivity(), String.format(getString(R.string.api_commitfailure), commitFailEntry.path, exceptionInfo), Toast.LENGTH_LONG).show();
                ToastUtils.showLongToast(mActivity, R.string.api_commitfailure_hint);
                notifyFailureOrError(1);
            }
            return false;
        }
    });

    protected void commitFailure(Activity activity, EAPIChannel.commitFailEntry failEntry) {
        ViseLog.e("activity = " + activity.getLocalClassName() + "\nfailEntry = \n" + GsonUtil.toJson(failEntry));
        QMUITipDialogUtil.dismiss();
        notifyFailureOrError(1);
        ToastUtils.showLongToast(activity, failEntry.exception.getMessage());
    }

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
                ToastUtils.showLongToast(mActivity, TextUtils.isEmpty(responseErrorEntry.localizedMsg) ? getString(R.string.api_responseerror_hint) : ResponseMessageUtil.replaceMessage(responseErrorEntry.localizedMsg));
                if (responseErrorEntry.code == 10360) {
                    // 场景不存在
                    notifyFailureOrError(10360);
                } else
                    notifyFailureOrError(2);
            }
            return false;
        }
    });
    public final String TAG = getClass().getSimpleName();

    protected void responseError(Activity activity, EAPIChannel.responseErrorEntry errorEntry) {
        ViseLog.e("activity = " + activity.getLocalClassName() + "\nerrorEntry = \n" + GsonUtil.toJson(errorEntry));
        QMUITipDialogUtil.dismiss();
        if (errorEntry.code == 401 || errorEntry.code == 29003) {
            //检查用户是否登录了其他App
            logOut();
        } else if (errorEntry.code != 6741 && errorEntry.code != 429 && errorEntry.code != 29004) {
            // 6741: 无扩展信息
            // 429: 请求频繁
            // 29004：device is unbind
            String msg = mErrorMap.get(errorEntry.code);
            if (msg != null && msg.length() > 0) {
                ToastUtils.showLongToast(activity, msg + "\n" + errorEntry.path);
            } else {
                ToastUtils.showLongToast(activity, errorEntry.localizedMsg + "\n" + errorEntry.path);
            }
        }
    }

    // 通知提交失败或响应错误
    protected void notifyFailureOrError(int type) {
    }

    protected void dismissQMUIDialog() {

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    /**
     * Fragment所依赖的Activity的上下文
     */
    public Activity mActivity;

    public Unbinder mUnbinder;
    private View rootView;

    private final Map<Integer, String> mErrorMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        //mActivity = getActivity();

        rootView = inflater.inflate(setLayout(), container, false);

        // 绑定ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        mErrorMap.put(200, "请求成功");
        mErrorMap.put(400, "请求错误");
        mErrorMap.put(401, "请求认证错误");
        mErrorMap.put(403, "请求被禁止");
        mErrorMap.put(404, "服务未找到");
        mErrorMap.put(429, "请求频繁");
        mErrorMap.put(460, "请求参数错误");
        mErrorMap.put(500, "服务端错误");
        mErrorMap.put(503, "服务不可用");
        mErrorMap.put(2000, "请求参数错误");
        mErrorMap.put(2062, "identityId不存在");
        mErrorMap.put(2063, "设备不存在");
        mErrorMap.put(2065, "设备和账号未绑定");
        mErrorMap.put(2066, "该用户不是设备的管理员");
        mErrorMap.put(2073, "该设备的分享模式不支持生成二维码，例如抢占式设备不支持分享");
        mErrorMap.put(2074, "调用消息中心错误");

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
