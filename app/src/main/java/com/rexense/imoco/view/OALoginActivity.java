package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oauth.OauthPlateform;
import com.alibaba.sdk.android.openaccount.OauthService;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.LayoutMapping;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailRegisterCallback;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailResetPasswordCallback;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.alibaba.sdk.android.openaccount.ui.widget.InputBoxWithClear;
import com.alibaba.sdk.android.openaccount.ui.widget.MobileInputBoxWithClear;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.rexense.imoco.R;


public class OALoginActivity extends LoginActivity implements View.OnClickListener{
//
//    private RegisterSelectorDialogFragment registerSelectorDialogFragment;
//    private ResetSelectorDialogFragment resetSelectorDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //显示登录页和手机忘记密码页的选择国家区号
        OpenAccountUIConfigs.AccountPasswordLoginFlow.supportForeignMobileNumbers = false;
        OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportForeignMobileNumbers = false;
        super.onCreate(savedInstanceState);
//        findViewById(R.id.btn_facebook).setOnClickListener(this);
        mToolBar.setVisibility(View.GONE);

        LayoutMapping.put(InputBoxWithClear.class,R.layout.ali_sdk_openaccount_input_box);
        LayoutMapping.put(MobileInputBoxWithClear.class,R.layout.ali_sdk_openaccount_mobile_input_box);
//        registerSelectorDialogFragment = new RegisterSelectorDialogFragment();
//        registerSelectorDialogFragment.setOnClickListener(registerListenr);
//
//        resetSelectorDialogFragment = new ResetSelectorDialogFragment();
//        resetSelectorDialogFragment.setOnClickListener(resetListenr);


        this.resetPasswordTV = this.findViewById(ResourceUtils.getRId(this, "reset_password"));
        this.resetPasswordTV.setOnClickListener(resetListenr);
//        if (this.resetPasswordTV != null) {
//            this.resetPasswordTV.setOnClickListener(v -> resetSelectorDialogFragment.showAllowingStateLoss(getSupportFragmentManager(), ""));
//        }
        this.registerTV = this.findViewById(ResourceUtils.getRId(this, "register"));
        this.registerTV.setOnClickListener(registerListenr);

        this.loginWithSmsCodeTV = (TextView)this.findViewById(ResourceUtils.getRId(this, "login_with_sms_code"));
        if (this.loginWithSmsCodeTV != null) {
            this.loginWithSmsCodeTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    OpenAccountUIService openAccountUIService = (OpenAccountUIService)OpenAccountSDK.getService(OpenAccountUIService.class);
                    openAccountUIService.showLoginWithSmsCode(OALoginActivity.this, LoginWithSmsCodeActivity.class, getResetPasswordLoginCallback());
                }
            });
        }
//        if (this.registerTV != null) {
//            this.registerTV.setOnClickListener(v -> registerSelectorDialogFragment.showAllowingStateLoss(getSupportFragmentManager(), ""));
//        }
        TextView loginWithSmsCode = this.findViewById(ResourceUtils.getRId(this, "login_with_sms_code"));
        Drawable drawable = getResources().getDrawable(R.drawable.login_sms);
        int sizeDp = getResources().getDimensionPixelSize(R.dimen.dp_40);
        // 设置图片的大小
        drawable.setBounds(0, 0, sizeDp, sizeDp);
        // 设置图片的位置，左、上、右、下
        loginWithSmsCode.setCompoundDrawables(null, drawable, null, null);

    }


    @Override
    public void onClick(View v) {
        OauthService oauthService = OpenAccountSDK.getService(OauthService.class);
        int oaCode = OauthPlateform.GOOGLE;
        try {
            oauthService.oauth(this, oaCode, new LoginCallback() {
                @Override
                public void onSuccess(OpenAccountSession session) {
                    LoginCallback loginCallback = OALoginActivity.this.getLoginCallback();
                    if (loginCallback != null) {
                        loginCallback.onSuccess(session);
                    }
                    OALoginActivity.this.finishWithoutCallback();
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.e(TAG, "onFailure: " + msg);
                    Toast.makeText(OALoginActivity.this, "oauth 失败 code = " + code + " message = " + msg, Toast.LENGTH_LONG).show();

                    LoginCallback loginCallback = OALoginActivity.this.getLoginCallback();
                    if (loginCallback != null) {
                        loginCallback.onFailure(code, msg);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener registerListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OpenAccountUIService openAccountUIService = OpenAccountSDK.getService(OpenAccountUIService.class);
//            openAccountUIService.showRegister(OALoginActivity.this, null);
            openAccountUIService.showRegister(OALoginActivity.this,RegisterActivity.class,null);
//            registerSelectorDialogFragment.dismissAllowingStateLoss();
        }
    };


    private View.OnClickListener resetListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            forgetPhonePassword(v);
//            resetSelectorDialogFragment.dismissAllowingStateLoss();
        }
    };

    public void forgetPhonePassword(View view) {
//        super.forgetPassword(view);
        OpenAccountUIService openAccountUIService = (OpenAccountUIService)OpenAccountSDK.getService(OpenAccountUIService.class);
        openAccountUIService.showResetPassword(this, ResetPasswordActivity.class, null);
    }

    public void forgetMailPassword(View view) {
        OpenAccountUIService openAccountUIService = (OpenAccountUIService) OpenAccountSDK.getService(OpenAccountUIService.class);
        openAccountUIService.showEmailResetPassword(this, this.getEmailResetPasswordCallback());
    }

    private EmailResetPasswordCallback getEmailResetPasswordCallback() {
        return new EmailResetPasswordCallback() {

            @Override
            public void onSuccess(OpenAccountSession session) {
                LoginCallback callback = getLoginCallback();
                if (callback != null) {
                    callback.onSuccess(session);

                }
                finishWithoutCallback();
            }

            @Override
            public void onFailure(int code, String message) {
                LoginCallback callback = getLoginCallback();
                if (callback != null) {
                    callback.onFailure(code, message);
                }
            }

            @Override
            public void onEmailSent(String email) {
                Toast.makeText(getApplicationContext(), email + " 已经发送了", Toast.LENGTH_LONG).show();
            }

        };
    }

    private EmailRegisterCallback getEmailRegisterCallback() {
        return new EmailRegisterCallback() {

            @Override
            public void onSuccess(OpenAccountSession session) {
                LoginCallback callback = getLoginCallback();
                if (callback != null) {
                    callback.onSuccess(session);
                }
                finishWithoutCallback();
            }

            @Override
            public void onFailure(int code, String message) {
                LoginCallback callback = getLoginCallback();
                if (callback != null) {
                    callback.onFailure(code, message);
                }
            }

            @Override
            public void onEmailSent(String email) {
                Toast.makeText(getApplicationContext(), email + " 已经发送了", Toast.LENGTH_LONG).show();
            }

        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OauthService service = OpenAccountSDK.getService(OauthService.class);
        if (service != null) {
            service.authorizeCallback(requestCode, resultCode, data);
        }
    }


    protected final void TRANSPARENT() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(OALanguageUtils.attachBaseContext(newBase));
//    }

    @Override
    protected String getLayoutName() {
        return "ali_sdk_openaccount_login2";
    }

}