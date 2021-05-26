package com.rexense.imoco.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.rexense.imoco.R;
import com.rexense.imoco.databinding.ActivityNicknameBinding;
import com.rexense.imoco.event.RefreshMyinfo;
import com.rexense.imoco.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;
import java.util.Map;

public class NickNameActivity extends BaseActivity {

    private ActivityNicknameBinding mViewBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityNicknameBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.myinfo_nickname));

        initStatusBar();
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onClick);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    void onClick(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            String nickNameStr = mViewBinding.nickNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(nickNameStr)) {
                ToastUtils.showToastCentrally(mActivity, mViewBinding.nickNameEt.getHint().toString());
                return;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("displayName", nickNameStr);
            OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
            oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
                @Override
                public void onSuccess(OpenAccountSession openAccountSession) {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.nick_name_modify_success));
                    EventBus.getDefault().post(new RefreshMyinfo());
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                }
            });
        }
    }

}
