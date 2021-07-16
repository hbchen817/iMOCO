package com.xiezhu.jzj.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiezhu.jzj.BuildConfig;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.utility.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.version_tv)
    TextView versionTv;
    @BindView(R.id.service_tel_view)
    RelativeLayout mServiceTelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.fragment3_about_us));

        // 隐藏隐私政策
        if (SystemParameter.getInstance().getIsHidePrivacyPolicy().equalsIgnoreCase("Yes")) {
            RelativeLayout privacy_policy_view = findViewById(R.id.privacy_policy_view);
            privacy_policy_view.setVisibility(View.GONE);
        }

        // 隐藏用户协议
        if (SystemParameter.getInstance().getIsHideUserDeal().equalsIgnoreCase("Yes")) {
            RelativeLayout user_deal_view = findViewById(R.id.user_deal_view);
            user_deal_view.setVisibility(View.GONE);
        }

        versionTv.setText(AppUtils.getVersionName(mActivity));

        if ("com.rexense.imoco".equals(BuildConfig.APPLICATION_ID)) {
            mServiceTelView.setVisibility(View.VISIBLE);
        } else if ("com.xiezhu.jzj".equals(BuildConfig.APPLICATION_ID))
            mServiceTelView.setVisibility(View.GONE);
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

    @OnClick({R.id.evaluate_view, R.id.privacy_policy_view, R.id.user_deal_view, R.id.opensourse_deal_view, R.id.aboutus_view})
    void onClick(View view) {
        if (view.getId() == R.id.evaluate_view) {

        } else if (view.getId() == R.id.privacy_policy_view) {
            if (getString(R.string.app_privacy_policy_url).length() == 0) {
                H5Activity.actionStart(mActivity, Constant.PRIVACY_POLICY_URL, getString(R.string.aboutus_privacy_policy));
            } else {
                H5Activity.actionStart(mActivity, getString(R.string.app_privacy_policy_url), getString(R.string.aboutus_privacy_policy));
            }
        } else if (view.getId() == R.id.user_deal_view) {
            if (getString(R.string.app_user_deal_url).length() == 0) {
                H5Activity.actionStart(mActivity, Constant.USER_PROTOCOL_URL, getString(R.string.aboutus_user_deal));
            } else {
                H5Activity.actionStart(mActivity, getString(R.string.app_user_deal_url), getString(R.string.aboutus_user_deal));
            }
        } else if (view.getId() == R.id.opensourse_deal_view) {

        } else if (view.getId() == R.id.aboutus_view) {

        }
    }

}
