package com.rexense.wholehouse.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.event.RefreshMyinfo;
import com.rexense.wholehouse.sdk.Account;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyInfoActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.head_img)
    ImageView headImg;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.user_account)
    TextView userAccount;
    public static MyInfoActivity myInfoActivity;
    private Intent intent;
    private DialogInterface.OnClickListener logoutConfirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            LoginBusiness.logout(new ILogoutCallback() {
                @Override
                public void onLogoutSuccess() {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.account_logout_success));
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    IndexActivity.mainActivity.finish();
                    finish();
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onLogoutFailed(int code, String error) {
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.account_logout_failed) + error);
                }
            });
        }
    };

    @Subscribe
    public void onRefreshMyInfo(RefreshMyinfo refreshMyinfo){
        nickName.setText(Account.getUserNick());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        myInfoActivity = this;
        tvToolbarTitle.setText(getString(R.string.myinfo_title));
        nickName.setText(Account.getUserNick());
        userAccount.setText(Account.getUserPhone());

        initStatusBar();
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

    @OnClick({R.id.head_img,R.id.nick_name_view,R.id.change_password,R.id.delete_account,R.id.logout_btn})
    void onClick(View view){
        switch (view.getId()){
            case R.id.head_img:
                break;
            case R.id.nick_name_view:
                intent = new Intent(mActivity,NickNameActivity.class);
                startActivity(intent);
                break;
            case R.id.change_password:
                OpenAccountUIService openAccountUIService = (OpenAccountUIService) OpenAccountSDK.getService(OpenAccountUIService.class);
                openAccountUIService.showResetPassword(this, ResetPasswordActivity.class, null);
                break;
            case R.id.delete_account:
                intent = new Intent(mActivity,DeleteAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_btn:
                DialogUtils.showEnsureDialog(mActivity,logoutConfirmListener,getString(R.string.myinfo_logout_tips),null);
                break;
        }
    }

}
