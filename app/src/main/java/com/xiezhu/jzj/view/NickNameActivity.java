package com.xiezhu.jzj.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.event.RefreshMyinfo;
import com.xiezhu.jzj.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;

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

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @OnClick({R.id.tv_toolbar_right})
    void onClick(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            String nickNameStr = nickNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(nickNameStr)) {
                ToastUtils.showToastCentrally(mActivity, nickNameEt.getHint().toString());
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
