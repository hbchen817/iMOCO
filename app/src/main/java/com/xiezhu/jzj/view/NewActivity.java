package com.xiezhu.jzj.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.utility.SpUtils;
import com.xiezhu.jzj.widget.IndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewActivity extends BaseActivity {


    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.page_indicator)
    IndicatorView mPageIndicator;

    /**
     * ViewPager的每个页面集合
     */
    private List<View> views;
    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);
        boolean isFirst = SpUtils.getBooleanValue(mActivity, SpUtils.SP_APP_INFO, SpUtils.KEY_IS_FIRST, false);
        if (isFirst) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.new_one, null));
        views.add(inflater.inflate(R.layout.new_two, null));
        views.add(inflater.inflate(R.layout.new_three, null));
        myPagerAdapter = new MyPagerAdapter(this, views);
        mViewPager.setAdapter(myPagerAdapter);
        mPageIndicator.setViewPager(mViewPager);
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViews;
        private Activity mContext;

        public MyPagerAdapter(Activity context, List<View> views) {
            this.mViews = views;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mViews.get(arg1));
        }

        /**
         * 实例化页卡，如果变为最后一页，则获取它的button并且添加点击事件
         */
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mViews.get(arg1), 0);
            if (arg1 == mViews.size() - 1) {
                TextView enterBtn = arg0
                        .findViewById(R.id.open);
                enterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SpUtils.putBooleanValue(mActivity, SpUtils.SP_APP_INFO, SpUtils.KEY_IS_FIRST, true);
                        startActivity(new Intent(NewActivity.this, StartActivity.class));
                        finish();
                    }
                });
            }
            return mViews.get(arg1);
        }
    }


}
