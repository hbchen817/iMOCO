package com.xiezhu.jzj.view;


import android.os.Bundle;
import android.view.View;

import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.FillPasswordActivity;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;

@ExtensionPoint
public class RegisterFillPasswordActivity extends FillPasswordActivity {
    public RegisterFillPasswordActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolBar.setVisibility(View.GONE);
        this.passwordInputBox.setUsePasswordMasking(OpenAccountUIConfigs.MobileRegisterFlow.usePasswordMaskingForRegister);
        View backImg = this.findViewById(ResourceUtils.getRId(this, "back_img_view"));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected String getTarget() {
        return "register";
    }

    protected String getRequestKey() {
        return "registerRequest";
    }

    protected LoginCallback getLoginCallback() {
        return OpenAccountUIServiceImpl._registerCallback;
    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_register_fill_password";
    }

    protected int getScenario() {
        return 2;
    }
}

