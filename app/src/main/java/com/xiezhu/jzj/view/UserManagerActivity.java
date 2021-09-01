package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.ItemUser;
import com.xiezhu.jzj.model.Visitable;
import com.xiezhu.jzj.presenter.UserCenter;
import com.xiezhu.jzj.utility.SrlUtils;
import com.xiezhu.jzj.viewholder.CommonAdapter;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/13 13:54
 */

public class UserManagerActivity extends BaseActivity {

    private static final String IOTID = "IOTID";

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.iv_toolbar_right)
    ImageView ivToolbarRight;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;

    private final List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private ProcessDataHandler mHandler;
    private int mPageNo = 1;
    private int mPageSize = 20;

    private String mIotId;

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mList.clear();
            mPageNo = 1;
            getData();
        }
    };
    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mPageNo++;
            getData();
        }
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
        setContentView(R.layout.activity_user_manager);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
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
        ivToolbarRight.setImageResource(R.drawable.add_gray);
        tvToolbarTitle.setText(R.string.lock_user_manager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new CommonAdapter(mList, this);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mAdapter);
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                ItemUser item = (ItemUser) mList.get(index);
                EditUserActivity.start(UserManagerActivity.this, item.getID(), item.getName());
            }
        });
    }

    private void getData() {
        mHandler = new ProcessDataHandler(getMainLooper(), this);
        UserCenter.queryVirtualUserListInAccount(mPageNo, mPageSize, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.iv_toolbar_right})
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

        public ProcessDataHandler(Looper looper, UserManagerActivity activity) {
            super(looper);
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            UserManagerActivity activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT:
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
                    SrlUtils.finishRefresh(activity.mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(activity.mSrlFragmentMe, true);
                    break;
                default:
                    break;
            }
        }
    }

    public static class RefreshUserEvent {

    }

}