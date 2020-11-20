package com.xiezhu.jzj.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.event.ShareDeviceEvent;

import org.greenrobot.eventbus.EventBus;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareDeviceActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView tvToolbarLeft;
    @BindView(R.id.iv_toolbar_left)
    ImageView ivToolbarLeft;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    String[] type = new String[2];
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    boolean selectMode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_center);
        ButterKnife.bind(this);
        tvToolbarLeft.setVisibility(View.GONE);
        tvToolbarRight.setText(getString(R.string.share_device_select));
        tvToolbarLeft.setText(getString(R.string.share_device_cancel));
        tvToolbarTitle.setText(getString(R.string.fragment3_share_device));

        type[0] = getString(R.string.share_device_my_device);
        type[1] = getString(R.string.share_device_share_device);
        initFragments();
    }

    @OnClick({R.id.tv_toolbar_right,R.id.tv_toolbar_left})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                if (!selectMode){
                    selectMode = true;
                    tvToolbarRight.setText(getString(R.string.share_device_share));
                    tvToolbarLeft.setVisibility(View.VISIBLE);
                    ivToolbarLeft.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ShareDeviceEvent("select"));
                }else {
                    EventBus.getDefault().post(new ShareDeviceEvent("confirm"));
                }
                break;
            case R.id.tv_toolbar_left://取消
                tvToolbarLeft.setVisibility(View.GONE);
                ivToolbarLeft.setVisibility(View.VISIBLE);
                selectMode = false;
                tvToolbarRight.setText(getString(R.string.share_device_select));
                EventBus.getDefault().post(new ShareDeviceEvent("cancel"));
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
                if (position==1){
                    tvToolbarLeft.setVisibility(View.GONE);
                    ivToolbarLeft.setVisibility(View.VISIBLE);
                    selectMode = false;
                    tvToolbarRight.setVisibility(View.GONE);
                    EventBus.getDefault().post(new ShareDeviceEvent("cancel"));
                }else {
                    tvToolbarRight.setVisibility(View.VISIBLE);
                    tvToolbarRight.setText(getString(R.string.share_device_select));
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
