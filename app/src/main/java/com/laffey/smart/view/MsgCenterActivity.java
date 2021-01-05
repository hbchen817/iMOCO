package com.laffey.smart.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshMsgCenter;
import com.laffey.smart.presenter.MsgCenterManager;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MsgCenterActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private MsgCenterManager msgCenterManager;
    String[] type = new String[3];
    private String[] msgTypeArr = {"device","share","announcement"};
    private int currentPosition=0;
    private DialogInterface.OnClickListener clearMsgListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (currentPosition==1){
                msgCenterManager.clearShareNoticeList(mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }else {
                msgCenterManager.clearMsgList(msgTypeArr[currentPosition], mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_center);
        ButterKnife.bind(this);
        tvToolbarRight.setText(getString(R.string.msg_center_clear));
        tvToolbarTitle.setText(getString(R.string.fragment3_msg_center));

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
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_CLEARMESSAGERECORD:
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.msg_center_clear_success));
                    EventBus.getDefault().post(new RefreshMsgCenter(0));
                    break;
                case Constant.MSG_CALLBACK_CLEARSHARENOTICELIST:
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.msg_center_clear_success));
                    EventBus.getDefault().post(new RefreshMsgCenter(1));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @OnClick({R.id.tv_toolbar_right})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                String confirmMsg="";
                if (currentPosition==0){
                    confirmMsg = getString(R.string.msg_center_clear_device_msg);
                }else if (currentPosition==1){
                    confirmMsg = getString(R.string.msg_center_clear_share_msg);
                }else if (currentPosition==2){
                    confirmMsg = getString(R.string.msg_center_clear_notice_msg);
                }
                DialogUtils.showEnsureDialog(mActivity,clearMsgListener,confirmMsg,"");
                break;
        }
    }

    private void initFragments() {
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(type.length);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

}
