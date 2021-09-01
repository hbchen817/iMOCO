package com.laffey.smart.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySceneLogBinding;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.SrlUtils;
import com.laffey.smart.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SceneLogActivity extends BaseActivity {
    private ActivitySceneLogBinding mViewBinding;

    private CommonAdapter adapter;
    private List<Visitable> models = new ArrayList<Visitable>();
    private SceneManager sceneManager;
    private int page = 1;

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            page = 1;
            getData();
        }
    };

    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            page++;
            getData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneLogBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.fragment3_scene_log));

        sceneManager = new SceneManager(mActivity);
        adapter = new CommonAdapter(models, mActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mViewBinding.recycleView.setLayoutManager(layoutManager);
        mViewBinding.recycleView.setAdapter(adapter);
        mViewBinding.srlFragmentMe.setOnRefreshListener(onRefreshListener);
        mViewBinding.srlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);

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

    private void getData() {
        sceneManager.getSceneLogList(page, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_GETSCENELOG) {
                if (page == 1) {
                    models.clear();
                }
                models.addAll(CloudDataParser.processSceneLogList((String) msg.obj));
                adapter.notifyDataSetChanged();
                SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
                SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
                if (models.isEmpty()) {
                    mViewBinding.recycleView.setVisibility(View.GONE);
                    mViewBinding.sceneNodataView.setVisibility(View.VISIBLE);
                } else {
                    mViewBinding.recycleView.setVisibility(View.VISIBLE);
                    mViewBinding.sceneNodataView.setVisibility(View.GONE);
                }
            }
            return false;
        }
    });
}
