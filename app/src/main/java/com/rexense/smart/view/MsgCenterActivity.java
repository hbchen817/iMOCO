package com.rexense.smart.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityMsgCenterBinding;
import com.rexense.smart.event.RefreshMsgCenter;
import com.rexense.smart.presenter.MsgCenterManager;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MsgCenterActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMsgCenterBinding mViewBinding;

    private MsgCenterManager msgCenterManager;
    String[] type = new String[3];
    private final String[] msgTypeArr = {"device", "share", "announcement"};
    private int currentPosition = 0;
    private final DialogInterface.OnClickListener clearMsgListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (currentPosition == 1) {
                msgCenterManager.clearShareNoticeList(mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            } else {
                msgCenterManager.clearMsgList(msgTypeArr[currentPosition], mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMsgCenterBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.msg_center_clear));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.fragment3_msg_center));

        currentPosition = getIntent().getIntExtra("current_pos", 0);

        type[0] = getString(R.string.msg_center_device);
        type[1] = getString(R.string.msg_center_share);
        type[2] = getString(R.string.msg_center_notice);
        msgCenterManager = new MsgCenterManager(mActivity);
        initFragments();

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

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_CLEARMESSAGERECORD:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.msg_center_clear_success));
                    EventBus.getDefault().post(new RefreshMsgCenter(0));
                    break;
                case Constant.MSG_CALLBACK_CLEARSHARENOTICELIST:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.msg_center_clear_success));
                    EventBus.getDefault().post(new RefreshMsgCenter(1));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_toolbar_right) {
            String confirmMsg = "";
            if (currentPosition == 0) {
                confirmMsg = getString(R.string.msg_center_clear_device_msg);
            } else if (currentPosition == 1) {
                confirmMsg = getString(R.string.msg_center_clear_share_msg);
            } else if (currentPosition == 2) {
                confirmMsg = getString(R.string.msg_center_clear_notice_msg);
            }
            DialogUtils.showEnsureDialog(mActivity, clearMsgListener, confirmMsg, "");
        }
    }

    private void initFragments() {
        mViewBinding.viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        mViewBinding.tabLayout.setupWithViewPager(mViewBinding.viewPager);
        mViewBinding.viewPager.setOffscreenPageLimit(type.length);
        mViewBinding.viewPager.setCurrentItem(currentPosition);
        mViewBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return type.length;
        }

        @Override
        public Fragment getItem(int position) {
            MsgCenterFragment fragment = new MsgCenterFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return type[position];
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
