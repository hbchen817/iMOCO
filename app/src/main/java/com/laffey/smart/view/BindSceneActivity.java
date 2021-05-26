package com.laffey.smart.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.databinding.ActivityBindSceneBinding;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.viewholder.CommonAdapter;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;

public class BindSceneActivity extends BaseActivity {
    private ActivityBindSceneBinding mViewBinding;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private SceneManager mSceneManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityBindSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mSceneManager = new SceneManager(this);
        initView();
    }

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText("场景绑定");
        mViewBinding.includeToolbar.tvToolbarRight.setText("绑定");
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(getResources().getColor(R.color.all_9));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mViewBinding.recycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mViewBinding.recycleView.setAdapter(mAdapter);

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
        mViewBinding.createSceneView.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        //this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), type, 1, this.mScenePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);

    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.tv_toolbar_right) {
        } else if (id == R.id.create_scene_view) {
            SystemParameter.getInstance().setIsRefreshSceneListData(true);
            PluginHelper.createScene(this, CScene.TYPE_IFTTT, SystemParameter.getInstance().getHomeId());
        }
    }
}
