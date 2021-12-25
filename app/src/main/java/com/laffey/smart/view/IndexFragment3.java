package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.component.router.Router;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.AccountManager;
import com.laffey.smart.sdk.Account;
import com.laffey.smart.utility.AppUtils;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment3 extends BaseFragment {

    @BindView(R.id.iv_toolbar_left)
    ImageView ivToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.user_account)
    TextView userAccount;
    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.scene_log_view)
    RelativeLayout mSceneLogView;

    private Intent mIntent;
    private MyHandler mHandler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroyView() {
//        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
        // EventBus.getDefault().register(this);
        return R.layout.fragment_index3;
    }

    @Override
    protected void init() {
        tvToolbarTitle.setText(getString(R.string.rb_tab_three_desc));
        ivToolbarLeft.setVisibility(View.GONE);
        userAccount.setText(Account.getUserPhone());

        mHandler = new MyHandler(this);
        QMUITipDialogUtil.showLoadingDialg(mActivity, R.string.is_loading);
        AccountManager.getCaccountsInfo(mActivity, Constant.MSG_QUEST_GET_CACCOUNTS_INFO,
                Constant.MSG_QUEST_GET_CACCOUNTS_INFO_ERROR, mHandler);

        mSceneLogView.setVisibility(View.GONE);
    }

    // 点击事件处理
    @OnClick({R.id.my_info_view, R.id.advice_view, R.id.msg_view, R.id.share_device_view,
            R.id.service_view, R.id.aboutus_view, R.id.scene_log_view, R.id.tv_toolbar_right})
    void onClick(View view) {
        int id = view.getId();
        if (id == R.id.my_info_view) {
            mIntent = new Intent(mActivity, MyInfoActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.advice_view) {
            Bundle bundle = new Bundle();
            bundle.putString("mobileModel", Build.MODEL); // 手机型号
            bundle.putString("mobileSystem", Build.VERSION.RELEASE); // 手机系统
            bundle.putString("appVersion", AppUtils.getVersionName(mActivity)); // App版本
            Router.getInstance().toUrlForResult(getActivity(), Constant.PLUGIN_URL_ADVICE, 1, bundle);
        } else if (id == R.id.msg_view) {
            mIntent = new Intent(mActivity, MsgCenterActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.scene_log_view) {
            mIntent = new Intent(mActivity, SceneLogActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.share_device_view) {
            mIntent = new Intent(mActivity, ShareDeviceActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.service_view) {
            mIntent = new Intent(mActivity, TmallSpiritActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.aboutus_view) {
            mIntent = new Intent(mActivity, AboutUsActivity.class);
            startActivity(mIntent);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<IndexFragment3> ref;

        public MyHandler(IndexFragment3 fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IndexFragment3 fragment = ref.get();
            if (fragment == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_GET_CACCOUNTS_INFO: {
                    // 查询用户信息
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    ViseLog.d("查询用户信息 = " + response.toJSONString());
                    if (code == 200) {
                        QMUITipDialogUtil.dismiss();

                        fragment.userAccount.setText(response.getString("accounts"));
                        SpUtils.putCaccountsInfo(fragment.mActivity, response.toJSONString());
                    } else {
                        RetrofitUtil.showErrorMsg(fragment.mActivity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_GET_CACCOUNTS_INFO_ERROR: {
                    // 查询用户信息失败
                    QMUITipDialogUtil.dismiss();
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e);
                    ToastUtils.showLongToast(fragment.mActivity, e.getMessage());
                    break;
                }
            }
        }
    }
}
