package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.LockManager;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/15 13:13
 */

public class HistoryActivity extends AppCompatActivity {

    public static final String IOTID = "IOTID";
    private static final String[] TYPE_ALL = null;
    private static final String[] TYPE_ALARM = new String[]{"Hijac****Alarm"};
    private static final String[] TYPE_OPEN = new String[]{"Door****Notification"};
    private static final String[] TYPE_INFO = new String[]{"DoorUnl****dAlarm"};

    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.mTimeText)
    TextView mTimeText;
    @BindView(R.id.mTypeText)
    TextView mTypeText;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private String mIotID;
    private int mPageNo;
    private int mPageSize = 30;
    private MyResponseHandler mHandler;
    private long mStartTime;
    private String[] mCurrentType;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        mIotID = getIntent().getStringExtra(IOTID);
        mHandler = new MyResponseHandler(this);
        initView();
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        mStartTime = 0;
        mCurrentType = TYPE_ALL;
        getData();
    }

    private void initView() {
        mTitle.setText(R.string.history_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        LockManager.getLockHistory(mIotID, mStartTime, System.currentTimeMillis(), mCurrentType, mPageNo, mPageSize, null, null, mHandler);

    }

    public static void start(Context context, String iotID) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(IOTID, iotID);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.mTimeView, R.id.mTypeView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.mTimeView:
                break;
            case R.id.mTypeView:
                break;
        }
    }

    private static class MyResponseHandler extends Handler {
        final WeakReference<HistoryActivity> mWeakReference;

        public MyResponseHandler(HistoryActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HistoryActivity activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERY_HISTORY:
                    JSONArray array = JSON.parseArray((String) msg.obj);
                    int size = array.size();
                    for (int i = 0; i < size; i++) {

                    }
                    SrlUtils.finishRefresh(activity.mSrlFragmentMe, true);
                    break;
                default:
                    break;
            }
        }
    }
}