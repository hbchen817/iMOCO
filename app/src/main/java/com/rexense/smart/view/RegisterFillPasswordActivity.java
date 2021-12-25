package com.rexense.smart.view;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.annotation.ExtensionPoint;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIConfigs;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.FillPasswordActivity;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;

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

