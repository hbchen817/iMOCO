package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aliyun.iot.aep.component.router.Router;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.sdk.Account;
import com.laffey.smart.utility.AppUtils;

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
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.user_account)
    TextView userAccount;
    @BindView(R.id.head_img)
    ImageView headImg;

    private Intent mIntent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroyView() {
//        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
        // EventBus.getDefault().register(this);
        return R.layout.fragment_index3;
    }

    @Override
    protected void init() {
        tvToolbarTitle.setText(getString(R.string.rb_tab_three_desc));
        ivToolbarLeft.setVisibility(View.GONE);
        userAccount.setText(Account.getUserPhone());
    }

    // 点击事件处理
    @OnClick({R.id.my_info_view, R.id.advice_view, R.id.msg_view, R.id.share_device_view,
            R.id.service_view, R.id.aboutus_view, R.id.scene_log_view, R.id.tv_toolbar_right})
    void onClick(View view) {
        int id = view.getId();
        if (id == R.id.my_info_view) {
            mIntent = new Intent(mActivity, MyInfoActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.advice_view) {
            Bundle bundle = new Bundle();
            bundle.putString("mobileModel", Build.MODEL); // 手机型号
            bundle.putString("mobileSystem", Build.VERSION.RELEASE); // 手机系统
            bundle.putString("appVersion", AppUtils.getVersionName(mActivity)); // App版本
            Router.getInstance().toUrlForResult(getActivity(), Constant.PLUGIN_URL_ADVICE, 1, bundle);
        } else if (id == R.id.msg_view) {
            mIntent = new Intent(mActivity, MsgCenterActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.scene_log_view) {
            mIntent = new Intent(mActivity, SceneLogActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.share_device_view) {
            mIntent = new Intent(mActivity, ShareDeviceActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.service_view) {
            mIntent = new Intent(mActivity, TmallSpiritActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.aboutus_view) {
            mIntent = new Intent(mActivity, AboutUsActivity.class);
            startActivity(mIntent);
        }
    }
}
