package com.rexense.imoco.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.rexense.imoco.BuildConfig;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.SpUtils;

public class OALoginActivity extends LoginActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //显示登录页和手机忘记密码页的选择国家区号
        OpenAccountUIConfigs.AccountPasswordLoginFlow.supportForeignMobileNumbers = false;
        OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportForeignMobileNumbers = false;
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);

        LayoutMapping.put(InputBoxWithClear.class,R.layout.ali_sdk_openaccount_input_box);
        LayoutMapping.put(MobileInputBoxWithClear.class,R.layout.ali_sdk_openaccount_mobile_input_box);

        this.resetPasswordTV = this.findViewById(ResourceUtils.getRId(this, "reset_password"));
        this.resetPasswordTV.setOnClickListener(resetListenr);
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
        TextView loginWithSmsCode = this.findViewById(ResourceUtils.getRId(this, "login_with_sms_code"));
        Drawable drawable = getResources().getDrawable(R.drawable.login_sms);
        int sizeDp = getResources().getDimensionPixelSize(R.dimen.dp_40);
        // 设置图片的大小
        drawable.setBounds(0, 0, sizeDp, sizeDp);
        // 设置图片的位置，左、上、右、下
        loginWithSmsCode.setCompoundDrawables(null, drawable, null, null);

        // 关闭输入键盘处理
        ImageView background = (ImageView)findViewById(R.id.loginImgBackground);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getWindow().peekDecorView();
                if (view != null && view.getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        TextView yonghuxieyi = this.findViewById(ResourceUtils.getRId(this,"yonghuxieyi"));
        yonghuxieyi.setOnClickListener(v->{
            if(getString(R.string.app_user_deal_url).length() == 0){
                H5Activity.actionStart(this, Constant.USER_PROTOCOL_URL,getString(R.string.aboutus_user_deal));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_user_deal_url),getString(R.string.aboutus_user_deal));
            }
        });
        TextView yinsi = this.findViewById(ResourceUtils.getRId(this,"yinsi"));
        yinsi.setOnClickListener(v->{
            if(getString(R.string.app_privacy_policy_url).length() == 0){
                H5Activity.actionStart(this, Constant.PRIVACY_POLICY_URL,getString(R.string.aboutus_privacy_policy));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_privacy_policy_url),getString(R.string.aboutus_privacy_policy));
            }
        });
        boolean isFirst = SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, "show_policy", false);
        if (!isFirst) {
            showPrivacyPolicyDialog();
        }
        //initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ("com.rexense.imoco".equals(BuildConfig.APPLICATION_ID)) {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow().setStatusBarColor(getResources().getColor(R.color.login_bg_2_color));
            }
        }
    }

    private void showPrivacyPolicyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView linkTV = (TextView) view.findViewById(R.id.policy_link);
        TextView disagreeTV = (TextView) view.findViewById(R.id.disagree_btn);
        TextView agreeTV = (TextView) view.findViewById(R.id.agree_btn);

        linkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                H5Activity.actionStart(OALoginActivity.this, Constant.PRIVACY_POLICY_URL, getString(R.string.aboutus_privacy_policy));
            }
        });
        disagreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPrivacyPolicyCheckDialog();
            }
        });
        agreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.putBooleanValue(OALoginActivity.this, SpUtils.SP_APP_INFO, "show_policy", true);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showPrivacyPolicyCheckDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy_check, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView lgoutTV = (TextView) view.findViewById(R.id.lgout_btn);
        TextView checkAgainTV = (TextView) view.findViewById(R.id.check_again_btn);

        lgoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        checkAgainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showPrivacyPolicyDialog();
            }
        });

        dialog.show();
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
            openAccountUIService.showRegister(OALoginActivity.this,RegisterActivity.class,null);
        }
    };

    private View.OnClickListener resetListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            forgetPhonePassword(v);
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

    @Override
    protected String getLayoutName() {
        return "ali_sdk_openaccount_login2";
    }

}