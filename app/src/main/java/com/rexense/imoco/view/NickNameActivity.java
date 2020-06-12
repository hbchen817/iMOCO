package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.widget.DialogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NickNameActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.nick_name_et)
    EditText nickNameEt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        ButterKnife.bind(this);
        tvToolbarRight.setText(getString(R.string.nick_name_save));
        tvToolbarTitle.setText(getString(R.string.myinfo_nickname));
    }

    @OnClick({R.id.tv_toolbar_right})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                String nickNameStr = nickNameEt.getText().toString().trim();
                if (TextUtils.isEmpty(nickNameStr)){
                    ToastUtils.showToastCentrally(mActivity,nickNameEt.getHint().toString());
                    return;
                }
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("displayName", nickNameStr);
                OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
                oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
                    @Override
                    public void onSuccess(OpenAccountSession openAccountSession) {
                        ToastUtils.showToastCentrally(mActivity,getString(R.string.nick_name_modify_success));
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
                break;
        }
    }

}
