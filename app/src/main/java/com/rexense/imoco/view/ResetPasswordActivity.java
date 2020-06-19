package com.rexense.imoco.view;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.message.MessageUtils;
import com.alibaba.sdk.android.openaccount.model.Result;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.RequestCode;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.model.CheckSmsCodeForResetPasswordResult;
import com.alibaba.sdk.android.openaccount.ui.task.TaskWithToastMessage;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginDoubleCheckWebActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.SendSmsCodeActivity;
import com.alibaba.sdk.android.openaccount.ui.widget.NetworkCheckOnClickListener;
import com.alibaba.sdk.android.openaccount.util.OpenAccountRiskControlContext;
import com.alibaba.sdk.android.openaccount.util.OpenAccountUtils;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.alibaba.sdk.android.openaccount.util.RpcUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@ExtensionPoint
public class ResetPasswordActivity extends SendSmsCodeActivity {
    private String smsCheckTrustToken;
    private String mMobile;
    public String mLocationCode;

    public ResetPasswordActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);
        this.setViewListener(this);

        View backImg = this.findViewById(ResourceUtils.getRId(this, "back_img_view"));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        this.mobileInputBox.setSupportForeignMobile(this, OpenAccountUIConfigs.MobileResetPasswordLoginFlow.mobileCountrySelectorActvityClazz, false);
        if (OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportResetPasswordWithNick) {
            if (this.mobileInputBox.getInputHint() == null) {
                this.mobileInputBox.setInputHint(ResourceUtils.getString(this, "ali_sdk_openaccount_dynamic_text_mobile_or_login_id"));
            }

            this.mobileInputBox.setInputType(1);
        } else {
            if (this.mobileInputBox.getInputHint() == null) {
                this.mobileInputBox.setInputHint(ResourceUtils.getString(this, "ali_sdk_openaccount_dynamic_text_mobile"));
            }

            this.mobileInputBox.setInputType(2);
        }

        try {
            this.mMobile = this.getIntent().getStringExtra("mobile");
            this.mLocationCode = this.getIntent().getStringExtra("LocationCode");
        } catch (Exception var3) {
        }

        if (!OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportForeignMobileNumbers) {
            if (!TextUtils.isEmpty(this.mMobile) && OpenAccountUtils.isNumeric(this.mMobile)) {
                this.mobileInputBox.getEditText().setText(this.mMobile);
                this.mobileInputBox.getEditText().setEnabled(false);
                this.mobileInputBox.getEditText().setFocusable(false);
                this.mobileInputBox.getClearTextView().setVisibility(View.GONE);
                this.sendSMS();
            }
        } else if (!TextUtils.isEmpty(this.mLocationCode)) {
            this.mobileInputBox.setMobileLocationCode(this.mLocationCode);
        }

    }

    public void setViewListener(Activity activity) {
        this.next.setOnClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                ResetPasswordActivity.this.goCheckSMSCode((String)null, (String)null, (String)null);
            }
        });
        this.smsCodeInputBox.addSendClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                ResetPasswordActivity.this.sendSMS();
            }
        });
    }

    private void sendSMS() {
        (new ResetPasswordActivity.SendSmsCodeForResetPasswordTask(this)).execute(new Void[0]);
    }

    protected void goCheckSMSCode(String cSessionId, String nocToken, String sig) {
        (new ResetPasswordActivity.CheckSmsCodeForResetPasswordTask(this, cSessionId, nocToken, sig)).execute(new Void[0]);
    }

    protected LoginCallback getLoginCallback() {
        return OpenAccountUIServiceImpl._resetPasswordCallback;
    }

    protected void onUserCancel() {
        LoginCallback loginCallback = this.getLoginCallback();
        if (loginCallback != null) {
            loginCallback.onFailure(10101, MessageUtils.getMessageContent(10101, new Object[0]));
        }

    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_reset_password";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.NO_CAPTCHA_REQUEST_CODE) {
            if (resultCode == -1) {
                if (data != null && "nocaptcha".equals(data.getStringExtra("action"))) {
                    String cSessionId = data.getStringExtra("cSessionId");
                    String nocToken = data.getStringExtra("nocToken");
                    String sig = data.getStringExtra("sig");
                    this.goCheckSMSCode(cSessionId, nocToken, sig);
                }
            } else {
                this.smsCheckTrustToken = "";
            }
        } else if (requestCode == 1 && resultCode == -1) {
            this.finishWithoutCallback();
            LoginCallback loginCallback = this.getLoginCallback();
            if (loginCallback != null) {
                loginCallback.onSuccess(this.sessionManagerService.getSession());
            }
        }

    }

    protected class CheckSmsCodeForResetPasswordTask extends TaskWithToastMessage<CheckSmsCodeForResetPasswordResult> {
        private String cSessionId;
        private String nocToken;
        private String sig;

        public CheckSmsCodeForResetPasswordTask(Activity activity, String cSessionId, String nocToken, String sig) {
            super(activity);
            this.cSessionId = cSessionId;
            this.nocToken = nocToken;
            this.sig = sig;
        }

        protected Result<CheckSmsCodeForResetPasswordResult> asyncExecute(Void... params) {
            Map<String, Object> checkSmsCodeForRegisterRequest = new HashMap();
            Map<String, Object> checkCodeRequest = new HashMap();
            checkSmsCodeForRegisterRequest.put("checkCodeRequest", checkCodeRequest);
            Map<String, Object> checkSmsCodeRequest = new HashMap();
            if (!TextUtils.isEmpty(this.sig)) {
                checkCodeRequest.put("sig", this.sig);
            }

            if (!TextUtils.isEmpty(this.nocToken)) {
                checkCodeRequest.put("nctoken", this.nocToken);
                checkSmsCodeRequest.put("smsToken", ResetPasswordActivity.this.smsCheckTrustToken);
            }

            ResetPasswordActivity.this.smsCheckTrustToken = "";
            if (!TextUtils.isEmpty(this.cSessionId)) {
                checkCodeRequest.put("csessionid", this.cSessionId);
            }

            checkSmsCodeRequest.put("mobileLocationCode", ResetPasswordActivity.this.mobileInputBox.getMobileLocationCode());
            checkSmsCodeRequest.put("userId", ResetPasswordActivity.this.mobileInputBox.getEditTextContent());
            checkSmsCodeRequest.put("version", 1);
            checkSmsCodeRequest.put("smsCode", ResetPasswordActivity.this.smsCodeInputBox.getInputBoxWithClear().getEditTextContent());
            checkSmsCodeForRegisterRequest.put("checkSmsCodeRequest", checkSmsCodeRequest);
            return this.parseJsonResult(RpcUtils.invokeWithRiskControlInfo("request", checkSmsCodeForRegisterRequest, "checksmscodeforresetpassword"));
        }

        protected CheckSmsCodeForResetPasswordResult parseData(JSONObject jsonObject) {
            CheckSmsCodeForResetPasswordResult checkSmsCodeForResetPasswordResult = new CheckSmsCodeForResetPasswordResult();
            checkSmsCodeForResetPasswordResult.token = jsonObject.optString("token");
            checkSmsCodeForResetPasswordResult.clientVerifyData = jsonObject.optString("clientVerifyData");
            checkSmsCodeForResetPasswordResult.smsCheckTrustToken = jsonObject.optString("smsCheckTrustToken");
            return checkSmsCodeForResetPasswordResult;
        }

        protected void doSuccessAfterToast(Result<CheckSmsCodeForResetPasswordResult> result) {
            Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordFillPasswordActivity.class);
            intent.putExtra("token", ((CheckSmsCodeForResetPasswordResult)result.data).token);
            intent.putExtra("loginId", ResetPasswordActivity.this.mobileInputBox.getEditTextContent());
            ResetPasswordActivity.this.startActivityForResult(intent, 1);
        }

        protected void doFailAfterToast(Result<CheckSmsCodeForResetPasswordResult> result) {
            if (result.code == 26053 && result.data != null && !TextUtils.isEmpty(((CheckSmsCodeForResetPasswordResult)result.data).clientVerifyData)) {
                ResetPasswordActivity.this.smsCheckTrustToken = ((CheckSmsCodeForResetPasswordResult)result.data).smsCheckTrustToken;
                Uri.Builder builder = Uri.parse(((CheckSmsCodeForResetPasswordResult)result.data).clientVerifyData).buildUpon();
                builder.appendQueryParameter("callback", "https://www.alipay.com/webviewbridge");
                Intent h5Intent = new Intent(ResetPasswordActivity.this, LoginDoubleCheckWebActivity.class);
                h5Intent.putExtra("url", builder.toString());
                h5Intent.putExtra("title", result.message);
                h5Intent.putExtra("callback", "https://www.alipay.com/webviewbridge");
                ResetPasswordActivity.this.startActivityForResult(h5Intent, RequestCode.NO_CAPTCHA_REQUEST_CODE);
            }
        }

        protected boolean toastMessageRequired(Result<CheckSmsCodeForResetPasswordResult> result) {
            return result.code != 26053;
        }
    }

    protected class SendSmsCodeForResetPasswordTask extends TaskWithToastMessage<Void> {
        public SendSmsCodeForResetPasswordTask(Activity activity) {
            super(activity);
        }

        protected Result<Void> asyncExecute(Void... params) {
            Map<String, Object> m = new HashMap();
            m.put("mobileLocationCode", ResetPasswordActivity.this.mobileInputBox.getMobileLocationCode());
            if (OpenAccountUIConfigs.MobileResetPasswordLoginFlow.supportResetPasswordWithNick) {
                m.put("userId", ResetPasswordActivity.this.mobileInputBox.getEditTextContent());
                m.put("version", 1);
            } else {
                m.put("mobile", ResetPasswordActivity.this.mobileInputBox.getEditTextContent());
            }

            m.put("riskControlInfo", OpenAccountRiskControlContext.buildRiskContext());
            return this.parseJsonResult(RpcUtils.invokeWithRiskControlInfo("sendSmsCodeForResetPasswordRequest", m, "sendsmscodeforresetpassword"));
        }

        protected Void parseData(JSONObject jsonObject) {
            return null;
        }

        protected void doSuccessAfterToast(Result<Void> result) {
            ResetPasswordActivity.this.smsCodeInputBox.startTimer(ResetPasswordActivity.this);
            ResetPasswordActivity.this.smsCodeInputBox.requestFocus();
        }

        protected void doFailAfterToast(Result<Void> result) {
        }
    }
}
