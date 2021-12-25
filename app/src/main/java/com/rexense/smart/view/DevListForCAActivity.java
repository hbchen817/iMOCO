package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityDevListForCaBinding;
import com.rexense.smart.demoTest.ResponseDevListForCA;
import com.rexense.smart.presenter.SceneManager;
import com.rexense.smart.presenter.SystemParameter;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DevListForCAActivity extends BaseActivity {
    private ActivityDevListForCaBinding mViewBinding;

    private CallbackHandler mHandler;

    private SceneManager mSceneManager;

    private int mPageNo = 1;
    private static final int PAGE_SIZE = 20;

    private List<ResponseDevListForCA.DevItem> mList;
    private BaseQuickAdapter<ResponseDevListForCA.DevItem, BaseViewHolder> mAdapter;

    private Typeface mIconfont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityDevListForCaBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);

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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.device_list));

        mHandler = new CallbackHandler(this);
        mSceneManager = new SceneManager(this);

        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<ResponseDevListForCA.DevItem, BaseViewHolder>(R.layout.item_dev, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ResponseDevListForCA.DevItem item) {
                TextView goTV = holder.getView(R.id.og_iv);
                goTV.setTypeface(mIconfont);

                holder.setText(R.id.dev_name_tv, item.getNickName())
                        .setVisible(R.id.divider, mList.indexOf(item) != 0);
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(DevListForCAActivity.this).load(item.getImage()).into(imageView);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ViseLog.d(mList.get(position).getIotId() + "\n" + mList.get(position).getNickName());
                Intent intent = new Intent(DevListForCAActivity.this, IdentifierListActivity.class);
                intent.putExtra("nick_name", mList.get(position).getNickName());
                intent.putExtra("dev_name", mList.get(position).getDeviceName());
                intent.putExtra("dev_iot", mList.get(position).getIotId());
                intent.putExtra("product_key", mList.get(position).getProductKey());
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.devRecycler.setLayoutManager(layoutManager);
        mViewBinding.devRecycler.setAdapter(mAdapter);

        mViewBinding.recyclerRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageNo = 1;
                mList.clear();
                mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), mPageNo, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mViewBinding.recyclerRl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPageNo++;
                mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), mPageNo, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), mPageNo, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private static class CallbackHandler extends Handler {
        private final WeakReference<DevListForCAActivity> weakRf;

        public CallbackHandler(DevListForCAActivity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DevListForCAActivity activity = weakRf.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_QUERY_DEV_LIST_FOR_CA) {// 获取支持TCA的设备列表
                JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                ViseLog.d(new Gson().toJson(jsonObject));
                ResponseDevListForCA responseEntry = JSONObject.parseObject(new Gson().toJson(jsonObject), ResponseDevListForCA.class);
                activity.mList.addAll(responseEntry.getData());
                if (responseEntry.getData().size() >= PAGE_SIZE) {
                    activity.mPageNo++;
                    activity.mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), activity.mPageNo, PAGE_SIZE,
                            activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                } else {
                    if (responseEntry.getData().size() == 0)
                        activity.mPageNo--;
                }
                activity.mAdapter.notifyDataSetChanged();
                activity.mViewBinding.recyclerRl.finishRefresh(true);
                activity.mViewBinding.recyclerRl.finishLoadMore(true);
                QMUITipDialogUtil.dismiss();
            }
        }
    }
}