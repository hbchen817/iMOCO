package com.rexense.imoco.view;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.sdk.Account;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment3 extends BaseFragment {

    @BindView(R.id.iv_toolbar_left)
    ImageView ivToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.user_account)
    TextView userAccount;
    @BindView(R.id.head_img)
    ImageView headImg;
    private Intent intent;

    @Override
    public void onDestroyView() {
//        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
//        EventBus.getDefault().register(this);
        return R.layout.fragment_index3;
    }

    @Override
    protected void init() {
        tvToolbarTitle.setText(getString(R.string.rb_tab_three_desc));
        ivToolbarLeft.setVisibility(View.GONE);
        userAccount.setText(Account.getUserPhone());
    }

    @OnClick({R.id.my_info_view, R.id.advice_view, R.id.msg_view, R.id.share_device_view, R.id.service_view, R.id.aboutus_view,R.id.scene_log_view})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_info_view:
                intent = new Intent(mActivity, MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.advice_view:
                break;
            case R.id.msg_view:
                break;
            case R.id.scene_log_view:
                break;
            case R.id.share_device_view:
                break;
            case R.id.service_view:
                break;
            case R.id.aboutus_view:
                break;
        }
    }


}
