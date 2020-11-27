package com.xiezhu.jzj.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.message.MessageUtils;
import com.alibaba.sdk.android.openaccount.model.Result;
import com.alibaba.sdk.android.openaccount.task.TaskWithDialog;
import com.alibaba.sdk.android.openaccount.ui.RequestCode;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs.MobileRegisterFlow;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.model.CheckSmsCodeForRegisterResult;
import com.alibaba.sdk.android.openaccount.ui.model.SendSmsCodeForRegisterResult;
import com.alibaba.sdk.android.openaccount.ui.task.TaskWithToastMessage;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginDoubleCheckWebActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.SendSmsCodeActivity;
import com.alibaba.sdk.android.openaccount.ui.util.ToastUtils;
import com.alibaba.sdk.android.openaccount.ui.widget.NetworkCheckOnClickListener;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.alibaba.sdk.android.openaccount.util.RpcUtils;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.widget.NextStepButton;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

@ExtensionPoint
public class RegisterActivity extends SendSmsCodeActivity {
    protected String clientVerifyData;
    protected String smsToken;

    public RegisterActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            this.clientVerifyData = (String) savedInstanceState.get("clientVerifyData");
        }
        CheckBox checkbox = this.findViewById(ResourceUtils.getRId(this, "checkbox"));

        this.next.setOnClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                if (checkbox.isChecked()) {
                    RegisterActivity.this.goSetPwd((String) null, (String) null, (String) null);
                } else {
                    Toast.makeText(RegisterActivity.this, "请确认同意极智家用户协议和隐私政策", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View backImg = this.findViewById(ResourceUtils.getRId(this, "back_img_view"));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        this.addSendListener();
        this.mobileInputBox.setSupportForeignMobile(this, MobileRegisterFlow.mobileCountrySelectorActvityClazz, false);
    }

    private void goSetPwd(String sig, String nocToken, String cSessionId) {
        (new RegisterActivity.CheckSmsCodeForRegisterTask(this, sig, nocToken, cSessionId)).execute(new Void[0]);
    }

    protected void addSendListener() {
        this.smsCodeInputBox.addSendClickListener(new NetworkCheckOnClickListener() {
            public void afterCheck(View v) {
                (RegisterActivity.this.new SendSmsCodeForRegisterTask(RegisterActivity.this)).execute(new Void[0]);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("clientVerifyData", this.clientVerifyData);
    }

    protected LoginCallback getLoginCallback() {
        return OpenAccountUIServiceImpl._registerCallback;
    }

    protected void onUserCancel() {
        LoginCallback loginCallback = this.getLoginCallback();
        if (loginCallback != null) {
            loginCallback.onFailure(10100, MessageUtils.getMessageContent(10100, new Object[0]));
        }

    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_register";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REGISTER_NO_CAPTCHA_REQUEST_CODE) {
            if (resultCode == -1) {
                if (data != null && "nocaptcha".equals(data.getStringExtra("action"))) {
                    String cSessionId = data.getStringExtra("cSessionId");
                    String nocToken = data.getStringExtra("nocToken");
                    String sig = data.getStringExtra("sig");
                    this.goSetPwd(sig, nocToken, cSessionId);
                }
            } else {
                this.smsToken = "";
            }
        } else if (requestCode == 1 && resultCode == -1) {
            this.finishWithoutCallback();
            LoginCallback loginCallback = this.getLoginCallback();
            if (loginCallback != null) {
                loginCallback.onSuccess(this.sessionManagerService.getSession());
            }
        }

    }

    protected class CheckSmsCodeForRegisterTask extends TaskWithDialog<Void, Void, Result<CheckSmsCodeForRegisterResult>> {
        private String sig;
        private String nocToken;
        private String cSessionId;

        public CheckSmsCodeForRegisterTask(Activity activity, String sig, String nocToken, String cSessionId) {
            super(activity);
            this.sig = sig;
            this.nocToken = nocToken;
            this.cSessionId = cSessionId;
        }

        protected void doWhenException(Throwable t) {
            this.executorService.postUITask(new Runnable() {
                public void run() {
                    ToastUtils.toastSystemError(CheckSmsCodeForRegisterTask.this.context);
                }
            });
        }

        protected Result<CheckSmsCodeForRegisterResult> asyncExecute(Void... params) {
            Map<String, Object> checkSmsCodeForRegisterRequest = new HashMap();
            Map<String, Object> checkCodeRequest = new HashMap();
            checkSmsCodeForRegisterRequest.put("checkCodeRequest", checkCodeRequest);
            Map<String, Object> checkSmsCodeRequest = new HashMap();
            checkSmsCodeRequest.put("mobileLocationCode", RegisterActivity.this.mobileInputBox.getMobileLocationCode());
            checkSmsCodeRequest.put("mobile", RegisterActivity.this.mobileInputBox.getEditTextContent());
            checkSmsCodeRequest.put("smsCode", RegisterActivity.this.smsCodeInputBox.getInputBoxWithClear().getEditTextContent());
            if (!TextUtils.isEmpty(this.sig)) {
                checkCodeRequest.put("sig", this.sig);
            }

            if (!TextUtils.isEmpty(this.cSessionId)) {
                checkCodeRequest.put("csessionid", this.cSessionId);
            }

            if (!TextUtils.isEmpty(this.nocToken)) {
                checkCodeRequest.put("nctoken", this.nocToken);
                checkSmsCodeRequest.put("smsToken", RegisterActivity.this.smsToken);
            }

            RegisterActivity.this.smsToken = "";
            checkSmsCodeForRegisterRequest.put("checkSmsCodeRequest", checkSmsCodeRequest);
            return this.parseJsonResult(RpcUtils.invokeWithRiskControlInfo("request", checkSmsCodeForRegisterRequest, "checksmscodeforregister"));
        }

        protected Result<CheckSmsCodeForRegisterResult> parseJsonResult(Result<JSONObject> result) {
            return result.data == null ? Result.result(result.code, result.message) : Result.result(result.code, result.message, this.parseData((JSONObject) result.data));
        }

        protected CheckSmsCodeForRegisterResult parseData(JSONObject jsonObject) {
            CheckSmsCodeForRegisterResult checkSmsCodeForRegisterResult = new CheckSmsCodeForRegisterResult();
            checkSmsCodeForRegisterResult.token = jsonObject.optString("token");
            RegisterActivity.this.smsToken = checkSmsCodeForRegisterResult.token;
            checkSmsCodeForRegisterResult.checkCodeUrl = jsonObject.optString("checkCodeUrl");
            checkSmsCodeForRegisterResult.clientVerifyData = jsonObject.optString("clientVerifyData");
            return checkSmsCodeForRegisterResult;
        }

        protected void onPostExecute(Result<CheckSmsCodeForRegisterResult> result) {
            super.onPostExecute(result);

            try {
                if (result == null) {
                    ToastUtils.toastSystemError(this.context);
                    return;
                }

                switch (result.code) {
                    case 1:
                        if (result.data != null && !TextUtils.isEmpty(((CheckSmsCodeForRegisterResult) result.data).token)) {
                            Intent intent = new Intent(RegisterActivity.this, RegisterFillPasswordActivity.class);
                            intent.putExtra("token", ((CheckSmsCodeForRegisterResult) result.data).token);
                            intent.putExtra("loginId", RegisterActivity.this.mobileInputBox.getEditTextContent());
                            RegisterActivity.this.startActivityForResult(intent, 1);
                        }
                        break;
                    case 26053:
                        if (result.data != null && !TextUtils.isEmpty(((CheckSmsCodeForRegisterResult) result.data).clientVerifyData)) {
                            Builder builder = Uri.parse(((CheckSmsCodeForRegisterResult) result.data).clientVerifyData).buildUpon();
                            builder.appendQueryParameter("callback", "https://www.alipay.com/webviewbridge");
                            Intent h5Intent = new Intent(RegisterActivity.this, LoginDoubleCheckWebActivity.class);
                            h5Intent.putExtra("url", builder.toString());
                            h5Intent.putExtra("title", result.message);
                            h5Intent.putExtra("callback", "https://www.alipay.com/webviewbridge");
                            RegisterActivity.this.startActivityForResult(h5Intent, RequestCode.REGISTER_NO_CAPTCHA_REQUEST_CODE);
                            return;
                        }
                        break;
                    default:
                        if (TextUtils.isEmpty(result.message)) {
                            ToastUtils.toastSystemError(this.context);
                        } else {
                            ToastUtils.toast(this.context, result.message, result.code);
                        }
                }
            } catch (Throwable var4) {
                var4.printStackTrace();
                ToastUtils.toastSystemError(this.context);
            }

        }
    }

    protected class SendSmsCodeForRegisterTask extends TaskWithToastMessage<SendSmsCodeForRegisterResult> {
        public SendSmsCodeForRegisterTask(Activity activity) {
            super(activity);
        }

        protected Result<SendSmsCodeForRegisterResult> asyncExecute(Void... params) {
            RegisterActivity.this.clientVerifyData = null;
            Map<String, Object> m = new HashMap();
            m.put("mobile", RegisterActivity.this.mobileInputBox.getEditTextContent());
            m.put("mobileLocationCode", RegisterActivity.this.mobileInputBox.getMobileLocationCode());
            return this.parseJsonResult(RpcUtils.invokeWithRiskControlInfo("registerRequest", m, "sendsmscodeforregister"));
        }

        protected SendSmsCodeForRegisterResult parseData(JSONObject jsonObject) {
            SendSmsCodeForRegisterResult sendSmsCodeForRegisterResult = new SendSmsCodeForRegisterResult();
            sendSmsCodeForRegisterResult.checkCodeId = jsonObject.optString("checkCodeId");
            sendSmsCodeForRegisterResult.checkCodeUrl = jsonObject.optString("checkCodeUrl");
            sendSmsCodeForRegisterResult.clientVerifyData = jsonObject.optString("clientVerifyData");
            return sendSmsCodeForRegisterResult;
        }

        protected void doSuccessAfterToast(Result<SendSmsCodeForRegisterResult> result) {
            RegisterActivity.this.smsCodeInputBox.startTimer(RegisterActivity.this);
            RegisterActivity.this.smsCodeInputBox.requestFocus();
        }

        protected void doFailAfterToast(Result<SendSmsCodeForRegisterResult> result) {
            if (result.code == 26053) {
                RegisterActivity.this.clientVerifyData = ((SendSmsCodeForRegisterResult) result.data).clientVerifyData;
                RegisterActivity.this.smsCodeInputBox.startTimer(RegisterActivity.this);
            }

        }

        protected boolean toastMessageRequired(Result<SendSmsCodeForRegisterResult> result) {
            return result.code != 26053;
        }
    }
}
