package com.rexense.imoco.view;


import android.os.Bundle;
import android.view.View;

import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.FillPasswordActivity;

@ExtensionPoint
public class ResetPasswordFillPasswordActivity extends FillPasswordActivity {
    public ResetPasswordFillPasswordActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);
        this.passwordInputBox.setUsePasswordMasking(OpenAccountUIConfigs.MobileResetPasswordLoginFlow.usePasswordMaskingForReset);
    }

    protected String getTarget() {
        return "resetpassword";
    }

    protected String getRequestKey() {
        return "resetPasswordRequest";
    }

    protected LoginCallback getLoginCallback() {
        return OpenAccountUIServiceImpl._resetPasswordCallback;
    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_reset_password_fill_password";
    }

    protected int getScenario() {
        return 3;
    }
}

