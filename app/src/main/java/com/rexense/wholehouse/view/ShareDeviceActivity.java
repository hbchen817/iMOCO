package com.rexense.wholehouse.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.databinding.ActivityMsgCenterBinding;
import com.rexense.wholehouse.event.ShareDeviceEvent;

import org.greenrobot.eventbus.EventBus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareDeviceActivity extends BaseActivity {
    private ActivityMsgCenterBinding mViewBinding;

    String[] type = new String[2];

    boolean selectMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMsgCenterBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarLeft.setVisibility(View.GONE);
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_select));
        mViewBinding.includeToolbar.tvToolbarLeft.setText(getString(R.string.share_device_cancel));
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.fragment3_share_device));

        type[0] = getString(R.string.share_device_my_device);
        type[1] = getString(R.string.share_device_share_device);
        initFragments();
        initStatusBar();

        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onClick);
        mViewBinding.includeToolbar.tvToolbarLeft.setOnClickListener(this::onClick);
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
            if (!selectMode) {
                selectMode = true;
                mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_share));
                mViewBinding.includeToolbar.tvToolbarLeft.setVisibility(View.VISIBLE);
                mViewBinding.includeToolbar.ivToolbarLeft.setVisibility(View.GONE);
                EventBus.getDefault().post(new ShareDeviceEvent("select"));
            } else {
                EventBus.getDefault().post(new ShareDeviceEvent("confirm"));
            }
        } else if (view.getId() == R.id.tv_toolbar_left) {
            mViewBinding.includeToolbar.tvToolbarLeft.setVisibility(View.GONE);
            mViewBinding.includeToolbar.ivToolbarLeft.setVisibility(View.VISIBLE);
            selectMode = false;
            mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_select));
            EventBus.getDefault().post(new ShareDeviceEvent("cancel"));
        }
    }

    private void initFragments() {
        mViewBinding.viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        mViewBinding.tabLayout.setupWithViewPager(mViewBinding.viewPager);
        mViewBinding.viewPager.setOffscreenPageLimit(type.length);
        mViewBinding.viewPager.setCurrentItem(0);
        mViewBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mViewBinding.includeToolbar.tvToolbarLeft.setVisibility(View.GONE);
                    mViewBinding.includeToolbar.ivToolbarLeft.setVisibility(View.VISIBLE);
                    selectMode = false;
                    mViewBinding.includeToolbar.tvToolbarRight.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ShareDeviceEvent("cancel"));
                } else {
                    mViewBinding.includeToolbar.tvToolbarRight.setVisibility(View.VISIBLE);
                    mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_select));
                }
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
            ShareDeviceFragment fragment = new ShareDeviceFragment();
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
