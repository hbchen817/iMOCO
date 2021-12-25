package com.rexense.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.ConfigManager;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.executor.ExecutorService;
import com.alibaba.sdk.android.openaccount.model.LoginResult;
import com.alibaba.sdk.android.openaccount.model.Result;
import com.alibaba.sdk.android.openaccount.model.SessionData;
import com.alibaba.sdk.android.openaccount.task.TaskWithDialog;
import com.alibaba.sdk.android.openaccount.trace.AliSDKLogger;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.RequestCode;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.task.TaskWithToastMessage;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginDoubleCheckWebActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginIVWebActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.SendSmsCodeActivity;
import com.alibaba.sdk.android.openaccount.ui.util.ToastUtils;
import com.alibaba.sdk.android.openaccount.ui.widget.NetworkCheckOnClickListener;
import com.alibaba.sdk.android.openaccount.util.OpenAccountUtils;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.alibaba.sdk.android.openaccount.util.RpcUtils;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@ExtensionPoint
public class LoginWithSmsCodeActivity extends SendSmsCodeActivity {
    private static final String TAG = "LoginWithSmsCodeActivity";
    private String smsCheckTrustToken;
    private String mMobile;
    public String mLocationCode;

    public LoginWithSmsCodeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);
        setViewListener(this);
        mobileInputBox.setSupportForeignMobile(this, OpenAccountUIConfigs.MobileResetPasswordLoginFlow.mobileCountrySelectorActvityClazz, false);
        if (OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportResetPasswordWithNick) {
            if (mobileInputBox.getInputHint() == null) {
                mobileInputBox.setInputHint(ResourceUtils.getString(this, "ali_sdk_openaccount_dynamic_text_mobile_or_login_id"));
            }

            mobileInputBox.setInputType(1);
        } else {
            if (mobileInputBox.getInputHint() == null) {
                mobileInputBox.setInputHint(ResourceUtils.getString(this, "ali_sdk_openaccount_dynamic_text_mobile"));
            }

            mobileInputBox.setInputType(2);
        }

        try {
            mMobile = getIntent().getStringExtra("mobile");
            mLocationCode = getIntent().getStringExtra("LocationCode");
        } catch (Exception ignored) {
        }

        if (!OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportForeignMobileNumbers) {
            if (!TextUtils.isEmpty(mMobile) && OpenAccountUtils.isNumeric(mMobile)) {
                mobileInputBox.getEditText().setText(mMobile);
                mobileInputBox.getEditText().setEnabled(false);
                mobileInputBox.getEditText().setFocusable(false);
                mobileInputBox.getClearTextView().setVisibility(View.GONE);
                sendSMS();
            }
        } else if (!TextUtils.isEmpty(mLocationCode)) {
            mobileInputBox.setMobileLocationCode(mLocationCode);
        }

        View backImg = findViewById(ResourceUtils.getRId(this, "back_img_view"));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        View accountLoginView = findViewById(ResourceUtils.getRId(this, "account_login_view"));
        accountLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 关闭输入键盘处理
        ImageView background = this.findViewById(ResourceUtils.getRId(this, "loginImgBackground"));
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getWindow().peekDecorView();
                if (view != null && view.getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    boolean b = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        TextView yonghuxieyi = this.findViewById(ResourceUtils.getRId(this, "yonghuxieyi"));
        yonghuxieyi.setOnClickListener(v -> {
            if (getString(R.string.app_user_deal_url).length() == 0) {
                H5Activity.actionStart(this, Constant.USER_PROTOCOL_URL, getString(R.string.aboutus_user_deal));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_user_deal_url), getString(R.string.aboutus_user_deal));
            }
        });
        TextView yinsi = this.findViewById(ResourceUtils.getRId(this, "yinsi"));
        yinsi.setOnClickListener(v -> {
            if (getString(R.string.app_privacy_policy_url).length() == 0) {
                H5Activity.actionStart(this, Constant.PRIVACY_POLICY_URL, getString(R.string.aboutus_privacy_policy));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_privacy_policy_url), getString(R.string.aboutus_privacy_policy));
            }
        });
    }

    public void setViewListener(Activity activity) {
        next.setText(ResourceUtils.getString(this, "ali_sdk_openaccount_text_login"));
        next.setOnClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                goCheckSMSCode((String) null, (String) null, (String) null);
            }
        });
        smsCodeInputBox.addSendClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                sendSMS();
            }
        });
    }

    private void sendSMS() {
        (new LoginWithSmsCodeActivity.SendSmsCodeTask(this)).execute(new Void[0]);
    }

    protected void goCheckSMSCode(String cSessionId, String nocToken, String sig) {
        (new LoginWithSmsCodeActivity.LoginWithSmsCodeTask(this, cSessionId, nocToken, sig)).execute(new Void[0]);
    }

    protected LoginCallback getLoginCallback() {
        return OpenAccountUIServiceImpl._loginWithSmsCodeCallback;
    }

    protected void onUserCancel() {
        LoginCallback loginCallback = this.getLoginCallback();
    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_login_with_sms_code_password";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.NO_CAPTCHA_REQUEST_CODE) {
            if (resultCode == -1) {
                if (data != null && "nocaptcha".equals(data.getStringExtra("action"))) {
                    String cSessionId = data.getStringExtra("cSessionId");
                    String nocToken = data.getStringExtra("nocToken");
                    String sig = data.getStringExtra("sig");
                    goCheckSMSCode(cSessionId, nocToken, sig);
                }
            } else {
                smsCheckTrustToken = "";
            }
        } else if (requestCode == 1 && resultCode == -1) {
            finishWithoutCallback();
            LoginCallback loginCallback = getLoginCallback();
            if (loginCallback != null) {
                loginCallback.onSuccess(sessionManagerService.getSession());
            }
        }

    }

    protected void onPwdLoginFail(int code, String message) {
        if (TextUtils.isEmpty(message)) {
            ToastUtils.toastSystemError(getApplicationContext());
        } else {
            ToastUtils.toast(getApplicationContext(), message, code);
        }

    }

    private void hideSoftInputForHw() {
        if ("HUAWEI".equalsIgnoreCase(Build.MANUFACTURER) && Build.VERSION.SDK_INT >= 27) {
            InputMethodManager manager = (InputMethodManager) ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            if (manager != null) {
                manager.hideSoftInputFromWindow(mobileInputBox.getEditText().getWindowToken(), 2);
            }
        }

    }

    private void loginSuccess() {
        AliSDKLogger.i("LoginWithSmsCodeActivity", "loginSuccess");
        ((ExecutorService) OpenAccountSDK.getService(ExecutorService.class)).postUITask(new Runnable() {
            public void run() {
                LoginCallback loginCallback = getLoginCallback();
                if (loginCallback != null) {
                    AliSDKLogger.i("LoginWithSmsCodeActivity", "loginCallback != null");
                    loginCallback.onSuccess(sessionManagerService.getSession());
                }

                ((ExecutorService) OpenAccountSDK.getService(ExecutorService.class)).postUITask(new Runnable() {
                    public void run() {
                        finishWithoutCallback();
                    }
                });
            }
        });
    }

    protected class LoginWithSmsCodeTask extends TaskWithDialog<Void, Void, Result<LoginResult>> {
        private String loginId;
        private String password;
        private String sig;
        private String nocToken;
        private String cSessionId;

        public LoginWithSmsCodeTask(Activity activity, String sig, String nocToken, String cSessionId) {
            super(activity);
            this.sig = sig;
            this.nocToken = nocToken;
            this.cSessionId = cSessionId;
        }

        protected Result<LoginResult> asyncExecute(Void... params) {
            LoginWithSmsCodeActivity.this.hideSoftInputForHw();
            Map<String, Object> checkSmsCodeForRegisterRequest = new HashMap();
            Map<String, Object> checkSmsCodeRequest = new HashMap();
            if (!TextUtils.isEmpty(this.sig)) {
                checkSmsCodeRequest.put("sig", this.sig);
            }

            if (!TextUtils.isEmpty(this.cSessionId)) {
                checkSmsCodeRequest.put("csessionid", this.cSessionId);
            }

            if (!TextUtils.isEmpty(this.nocToken)) {
                checkSmsCodeRequest.put("nctoken", this.nocToken);
            }

            checkSmsCodeRequest.put("mobileLocationCode", LoginWithSmsCodeActivity.this.mobileInputBox.getMobileLocationCode());
            checkSmsCodeRequest.put("mobile", LoginWithSmsCodeActivity.this.mobileInputBox.getEditTextContent());
            checkSmsCodeRequest.put("version", 0);
            checkSmsCodeRequest.put("securityMobile", false);
            checkSmsCodeRequest.put("smsCode", LoginWithSmsCodeActivity.this.smsCodeInputBox.getInputBoxWithClear().getEditTextContent());
            checkSmsCodeForRegisterRequest.put("checkSmsCodeRequest", checkSmsCodeRequest);
            Result<LoginResult> result = OpenAccountUtils.toLoginResult(RpcUtils.pureInvokeWithRiskControlInfo("request", checkSmsCodeForRegisterRequest, "loginwithoutpassword"));
            return result;
        }

        protected void doWhenException(Throwable t) {
            this.executorService.postUITask(new Runnable() {
                public void run() {
                    ToastUtils.toastSystemError(LoginWithSmsCodeActivity.LoginWithSmsCodeTask.this.context);
                }
            });
        }

        protected void onPostExecute(Result<LoginResult> result) {
            this.dismissProgressDialog();
            super.onPostExecute(result);

            try {
                if (result == null) {
                    if (ConfigManager.getInstance().isSupportOfflineLogin()) {
                        ToastUtils.toastNetworkError(this.context);
                    } else {
                        ToastUtils.toastSystemError(this.context);
                    }
                } else {
                    Uri.Builder builder;
                    Intent h5Intent;
                    switch (result.code) {
                        case 1:
                            if (result.data != null && ((LoginResult) result.data).loginSuccessResult != null) {
                                SessionData sessionData = OpenAccountUtils.createSessionDataFromLoginSuccessResult(((LoginResult) result.data).loginSuccessResult);
                                if (sessionData.scenario == null) {
                                    sessionData.scenario = 1;
                                }

                                LoginWithSmsCodeActivity.this.sessionManagerService.updateSession(sessionData);
                                String accountName = ((LoginResult) result.data).userInputName;
                                if (TextUtils.isEmpty(accountName)) {
                                    accountName = this.loginId;
                                }

                                if (ConfigManager.getInstance().isSupportOfflineLogin()) {
                                    OpenAccountSDK.getSqliteUtil().saveToSqlite(this.loginId, this.password);
                                }

                                LoginWithSmsCodeActivity.this.loginSuccess();
                                return;
                            }
                            break;
                        case 2:
                            SessionData sessionData1 = OpenAccountUtils.createSessionDataFromLoginSuccessResult(((LoginResult) result.data).loginSuccessResult);
                            if (sessionData1.scenario == null) {
                                sessionData1.scenario = 1;
                            }

                            LoginWithSmsCodeActivity.this.sessionManagerService.updateSession(sessionData1);
                            LoginWithSmsCodeActivity.this.loginSuccess();
                            break;
                        case 26053:
                            if (result.data != null && ((LoginResult) result.data).checkCodeResult != null && !TextUtils.isEmpty(((LoginResult) result.data).checkCodeResult.clientVerifyData)) {
                                builder = Uri.parse(((LoginResult) result.data).checkCodeResult.clientVerifyData).buildUpon();
                                builder.appendQueryParameter("callback", "https://www.alipay.com/webviewbridge");
                                h5Intent = new Intent(LoginWithSmsCodeActivity.this, LoginDoubleCheckWebActivity.class);
                                h5Intent.putExtra("url", builder.toString());
                                h5Intent.putExtra("title", result.message);
                                h5Intent.putExtra("callback", "https://www.alipay.com/webviewbridge");
                                LoginWithSmsCodeActivity.this.startActivityForResult(h5Intent, RequestCode.NO_CAPTCHA_REQUEST_CODE);
                                return;
                            }
                            break;
                        case 26152:
                            if (result.data != null && ((LoginResult) result.data).checkCodeResult != null && !TextUtils.isEmpty(((LoginResult) result.data).checkCodeResult.clientVerifyData)) {
                                builder = Uri.parse(((LoginResult) result.data).checkCodeResult.clientVerifyData).buildUpon();
                                builder.appendQueryParameter("callback", "https://www.alipay.com/webviewbridge");
                                h5Intent = new Intent(LoginWithSmsCodeActivity.this, LoginIVWebActivity.class);
                                h5Intent.putExtra("url", builder.toString());
                                h5Intent.putExtra("title", result.message);
                                h5Intent.putExtra("callback", "https://www.alipay.com/webviewbridge");
                                LoginWithSmsCodeActivity.this.startActivityForResult(h5Intent, RequestCode.RISK_IV_REQUEST_CODE);
                            }
                            break;
                        default:
                            if (TextUtils.equals(result.type, "CALLBACK") && LoginWithSmsCodeActivity.this.getLoginCallback() != null) {
                                LoginWithSmsCodeActivity.this.getLoginCallback().onFailure(result.code, result.message);
                                return;
                            }

                            LoginWithSmsCodeActivity.this.onPwdLoginFail(result.code, result.message);
                    }
                }
            } catch (Throwable var5) {
                AliSDKLogger.e("LoginWithSmsCodeActivity", "after post execute error", var5);
                ToastUtils.toastSystemError(this.context);
            }

        }
    }

    protected class SendSmsCodeTask extends TaskWithToastMessage<Void> {
        public SendSmsCodeTask(Activity activity) {
            super(activity);
        }

        protected Result<Void> asyncExecute(Void... params) {
            Map<String, Object> m = new HashMap();
            m.put("mobile", LoginWithSmsCodeActivity.this.mobileInputBox.getEditTextContent());
            m.put("mobileLocationCode", LoginWithSmsCodeActivity.this.mobileInputBox.getMobileLocationCode());
            m.put("smsActionType", "sdk_no_password_login");
            m.put("securityMobile", false);
            return this.parseJsonResult(RpcUtils.invokeWithRiskControlInfo("sendSmsCodeRequest", m, "sendsmscode"));
        }

        protected Void parseData(JSONObject jsonObject) {
            return null;
        }

        protected void doSuccessAfterToast(Result<Void> result) {
            LoginWithSmsCodeActivity.this.smsCodeInputBox.startTimer(LoginWithSmsCodeActivity.this);
            LoginWithSmsCodeActivity.this.smsCodeInputBox.requestFocus();
        }

        protected void doFailAfterToast(Result<Void> result) {
        }
    }
}