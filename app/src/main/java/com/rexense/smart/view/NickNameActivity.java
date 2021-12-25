package com.rexense.smart.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.rexense.smart.BuildConfig;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityNicknameBinding;
import com.rexense.smart.event.RefreshMyinfo;
import com.rexense.smart.presenter.AccountManager;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.SpUtils;
import com.rexense.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

public class NickNameActivity extends BaseActivity {
    private ActivityNicknameBinding mViewBinding;

    private MyHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityNicknameBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.myinfo_nickname));

        initStatusBar();
        mViewBinding.nickNameEt.setText(SpUtils.getNickName(this));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onClick);
        mHandler = new MyHandler(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    void onClick(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            String nickNameStr = mViewBinding.nickNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(nickNameStr)) {
                ToastUtils.showToastCentrally(mActivity, mViewBinding.nickNameEt.getHint().toString());
                return;
            }
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_submitted);
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                AccountManager.updateCaccountsInfo(this, null, mViewBinding.nickNameEt.getText().toString(),
                        null, null, null, null,
                        Constant.MSG_QUEST_UPDATE_CACCOUNTS_INFO, Constant.MSG_QUEST_UPDATE_CACCOUNTS_INFO_ERROR, mHandler);
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("displayName", nickNameStr);
                OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
                oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
                    @Override
                    public void onSuccess(OpenAccountSession openAccountSession) {
                        ToastUtils.showToastCentrally(mActivity, getString(R.string.nick_name_modify_success));
                        EventBus.getDefault().post(new RefreshMyinfo());
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<NickNameActivity> ref;

        public MyHandler(NickNameActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NickNameActivity activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_UPDATE_CACCOUNTS_INFO: {
                    // 编辑用户信息
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    if (code == 200) {
                        SpUtils.putNickName(activity, activity.mViewBinding.nickNameEt.getText().toString());
                        ToastUtils.showLongToast(activity, R.string.submit_completed);
                        activity.finish();
                    } else {
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_UPDATE_CACCOUNTS_INFO_ERROR: {
                    // 编辑用户信息失败
                    QMUITipDialogUtil.dismiss();
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e);
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
            }
        }
    }
}
