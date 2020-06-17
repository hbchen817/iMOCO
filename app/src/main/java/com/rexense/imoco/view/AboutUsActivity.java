package com.rexense.imoco.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.utility.AppUtils;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.fragment3_about_us));

        versionTv.setText(AppUtils.getVersionName(mActivity));
    }

    @OnClick({R.id.evaluate_view,R.id.privacy_policy_view,R.id.user_deal_view,R.id.opensourse_deal_view,R.id.aboutus_view})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.evaluate_view:
                break;
            case R.id.privacy_policy_view:
                H5Activity.actionStart(mActivity, Constant.PRIVACY_POLICY_URL,getString(R.string.aboutus_privacy_policy));
                break;
            case R.id.user_deal_view:
                H5Activity.actionStart(mActivity, Constant.USER_PROTOCOL_URL,getString(R.string.aboutus_user_deal));
                break;
            case R.id.opensourse_deal_view:
                break;
            case R.id.aboutus_view:
                break;
        }
    }

}
