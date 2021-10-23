package com.laffey.smart.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.laffey.smart.R;
import com.laffey.smart.databinding.ActivityMyinfoBinding;
import com.laffey.smart.event.RefreshMyinfo;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.sdk.Account;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMyinfoBinding mViewBinding;

    public static MyInfoActivity myInfoActivity;
    private Intent intent;

    private final DialogInterface.OnClickListener logoutConfirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            LoginBusiness.logout(new ILogoutCallback() {
                @Override
                public void onLogoutSuccess() {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_success));
                    DeviceBuffer.initSceneBuffer();
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    IndexActivity.mainActivity.finish();
                    finish();
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onLogoutFailed(int code, String error) {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_failed) + error);
                }
            });
        }
    };

    @Subscribe
    public void onRefreshMyInfo(RefreshMyinfo refreshMyinfo) {
        mViewBinding.nickName.setText(Account.getUserNick());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMyinfoBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        EventBus.getDefault().register(this);
        myInfoActivity = this;
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.myinfo_title));
        mViewBinding.nickName.setText(Account.getUserNick());
        mViewBinding.userAccount.setText(Account.getUserPhone());

        initStatusBar();
        mViewBinding.headImg.setOnClickListener(this);
        mViewBinding.nickNameView.setOnClickListener(this);
        mViewBinding.changePassword.setOnClickListener(this);
        mViewBinding.deleteAccount.setOnClickListener(this);
        mViewBinding.logoutBtn.setOnClickListener(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.head_img) {

        } else if (v.getId() == R.id.nick_name_view) {
            intent = new Intent(mActivity, NickNameActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.change_password) {
            OpenAccountUIService openAccountUIService = (OpenAccountUIService) OpenAccountSDK.getService(OpenAccountUIService.class);
            openAccountUIService.showResetPassword(this, ResetPasswordActivity.class, null);
        } else if (v.getId() == R.id.delete_account) {
            intent = new Intent(mActivity, DeleteAccountActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.logout_btn) {
            DialogUtils.showEnsureDialog(mActivity, logoutConfirmListener, getString(R.string.myinfo_logout_tips), null);
        }
    }

}
