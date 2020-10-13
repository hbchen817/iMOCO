package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ItemUser;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


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

public class UserManagerActivity extends AppCompatActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.iv_toolbar_right)
    ImageView ivToolbarRight;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private ProcessDataHandler mHandler;
    private int mPageNo = 1;
    private int mPageSize = 20;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mList.clear();
            mPageNo = 1;
            getData();
        }
    };
    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
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
        ButterKnife.bind(this);
        initView();
        getData();
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
        mHandler = new ProcessDataHandler(this);
        UserCenter.queryVirtualUserListInAccount(mPageNo, mPageSize, null, null, mHandler);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.iv_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.iv_toolbar_right:
                CreateUserActivity.start(this);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UserManagerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT);
        mHandler = null;
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
                    break;
                default:
                    break;
            }
        }
    }

    public static class RefreshUserEvent {

    }

}