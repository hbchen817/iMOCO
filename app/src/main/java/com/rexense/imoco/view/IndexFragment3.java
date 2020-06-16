package com.rexense.imoco.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.google.zxing.qrcode.encoder.QRCode;
import com.rexense.imoco.R;
import com.rexense.imoco.sdk.Account;
import com.rexense.imoco.utility.AppUtils;
import com.rexense.imoco.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    @OnClick({R.id.my_info_view, R.id.advice_view, R.id.msg_view, R.id.share_device_view, R.id.service_view, R.id.aboutus_view,R.id.scene_log_view,
    R.id.tv_toolbar_right})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_info_view:
                intent = new Intent(mActivity, MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.advice_view:
                String code = "link://router/feedback";
                Bundle bundle = new Bundle();
                bundle.putString("mobileModel",android.os.Build.MODEL); // 手机型号
                bundle.putString("mobileSystem",android.os.Build.VERSION.RELEASE); // 手机系统
                bundle.putString("appVersion", AppUtils.getVersionName(mActivity)); // App版本
                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
                break;
            case R.id.msg_view:
                intent = new Intent(mActivity, MsgCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.scene_log_view:
                break;
            case R.id.share_device_view:
                intent = new Intent(mActivity, ShareDeviceActivity.class);
                startActivity(intent);
                break;
            case R.id.service_view:
                break;
            case R.id.aboutus_view:
                break;
        }
    }


}
