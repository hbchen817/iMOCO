package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityUserManagerBinding;
import com.rexense.imoco.model.ItemUser;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gary
 * @time 2020/10/13 13:54
 */

public class UserManagerActivity extends BaseActivity {
    private ActivityUserManagerBinding mViewBinding;

    private static final String IOTID = "IOTID";

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private ProcessDataHandler mHandler;
    private int mPageNo = 1;

    private String mIotId;

    private final OnRefreshListener onRefreshListener = refreshLayout -> {
        mList.clear();
        mPageNo = 1;
        getData();
    };

    private final OnLoadMoreListener onLoadMoreListener = refreshLayout -> {
        mPageNo++;
        getData();
    };

    @Subscribe
    public void refresh(RefreshUserEvent event) {
        mList.clear();
        mPageNo = 1;
        getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityUserManagerBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        EventBus.getDefault().register(this);

        mIotId = getIntent().getStringExtra(IOTID);
        initView();
        getData();

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

    private void initView() {
        mViewBinding.includeToolbar.ivToolbarRight.setImageResource(R.drawable.add_gray);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.lock_user_manager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new CommonAdapter(mList, this);
        mViewBinding.recycleView.setLayoutManager(layoutManager);
        mViewBinding.recycleView.setAdapter(mAdapter);
        mViewBinding.srlFragmentMe.setOnRefreshListener(onRefreshListener);
        mViewBinding.srlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        mAdapter.setOnClickListener(view -> {
            int index = (int) view.getTag();
            ItemUser item = (ItemUser) mList.get(index);
            EditUserActivity.start(UserManagerActivity.this, item.getID(), item.getName());
        });

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.ivToolbarRight.setOnClickListener(this::onViewClicked);
    }

    private void getData() {
        mHandler = new ProcessDataHandler(this);
        int PAGE_SIZE = 20;
        UserCenter.queryVirtualUserListInAccount(mPageNo, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_toolbar_left) {
            finish();
        } else if (view.getId() == R.id.iv_toolbar_right) {
            CreateUserActivity.start(this);
        }
    }

    public static void start(Context context, String iotId) {
        Intent intent = new Intent(context, UserManagerActivity.class);
        intent.putExtra(IOTID, iotId);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(Constant.MSG_CALLBACK_QUERY_USER_IN_DEVICE);
        mHandler = null;
        EventBus.getDefault().unregister(this);
    }

    private static class ProcessDataHandler extends Handler {
        final WeakReference<UserManagerActivity> mWeakReference;

        public ProcessDataHandler(UserManagerActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            UserManagerActivity activity = mWeakReference.get();
            if (msg.what == Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT) {
                JSONObject result = JSONObject.parseObject((String) msg.obj);
                JSONArray data = result.getJSONArray("data");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    ItemUser itemUser = new ItemUser();
                    itemUser.setID(jsonObject.getString("userId"));
                    JSONArray attrList = jsonObject.getJSONArray("attrList");
                    itemUser.setName(attrList.getJSONObject(0).getString("attrValue"));
                    activity.mList.add(itemUser);
                }
                activity.mAdapter.notifyDataSetChanged();
                SrlUtils.finishRefresh(activity.mViewBinding.srlFragmentMe, true);
                SrlUtils.finishLoadMore(activity.mViewBinding.srlFragmentMe, true);
            }
        }
    }

    public static class RefreshUserEvent {

    }

}