package com.laffey.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.demoTest.ResponseDevListForCA;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevListForCAActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.recycler_rl)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.dev_recycler)
    RecyclerView mDevRecycler;

    private CallbackHandler mHandler;

    private SceneManager mSceneManager;

    private int mPageNo = 1;
    private final int PAGE_SIZE = 20;

    private List<ResponseDevListForCA.DevItem> mList;
    private BaseQuickAdapter<ResponseDevListForCA.DevItem, BaseViewHolder> mAdapter;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list_for_ca);
        ButterKnife.bind(this);

        initStatusBar();
        initView();
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
        mTitle.setText(getString(R.string.device_list));

        mHandler = new CallbackHandler(this);
        mSceneManager = new SceneManager(this);

        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<ResponseDevListForCA.DevItem, BaseViewHolder>(R.layout.item_dev, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ResponseDevListForCA.DevItem item) {
                holder.setText(R.id.dev_name_tv, item.getNickName())
                        .setVisible(R.id.divider, mList.indexOf(item) != 0);
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(DevListForCAActivity.this).load(item.getImage()).into(imageView);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ViseLog.d(mList.get(position).getIotId()+"\n"+mList.get(position).getNickName());
                Intent intent = new Intent(DevListForCAActivity.this,IdentifierListActivity.class);
                intent.putExtra("nick_name", mList.get(position).getNickName());
                intent.putExtra("dev_name", mList.get(position).getDeviceName());
                intent.putExtra("dev_iot", mList.get(position).getIotId());
                intent.putExtra("product_key", mList.get(position).getProductKey());
                startActivity(intent);
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mDevRecycler.setLayoutManager(mLayoutManager);
        mDevRecycler.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageNo = 1;
                mList.clear();
                mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(),mPageNo,PAGE_SIZE,mCommitFailureHandler,mResponseErrorHandler,mHandler);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPageNo++;
                mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(),mPageNo,PAGE_SIZE,mCommitFailureHandler,mResponseErrorHandler,mHandler);
            }
        });

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(),mPageNo,PAGE_SIZE,mCommitFailureHandler,mResponseErrorHandler,mHandler);
    }

    private class CallbackHandler extends Handler{
        private WeakReference<Activity> weakRf;

        public CallbackHandler(Activity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakRf.get() == null) return;
            switch (msg.what){
                case Constant.MSG_CALLBACK_QUERY_DEV_LIST_FOR_CA:{
                    // 获取支持TCA的设备列表
                    JSONObject jsonObject = JSON.parseObject((String)msg.obj);
                    ViseLog.d(new Gson().toJson(jsonObject));
                    ResponseDevListForCA responseEntry = new Gson().fromJson(new Gson().toJson(jsonObject),ResponseDevListForCA.class);
                    mList.addAll(responseEntry.getData());
                    if (responseEntry.getData().size() >= PAGE_SIZE){
                        mPageNo++;
                        mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(),mPageNo,PAGE_SIZE,mCommitFailureHandler,mResponseErrorHandler,mHandler);
                    } else {
                        if (responseEntry.getData().size() == 0)
                            mPageNo--;
                    }
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.finishRefresh(true);
                    mRefreshLayout.finishLoadMore(true);
                    QMUITipDialogUtil.dismiss();
                    break;
                }
            }
        }
    }
}