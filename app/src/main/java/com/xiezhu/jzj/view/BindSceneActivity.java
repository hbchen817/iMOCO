package com.xiezhu.jzj.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.model.Visitable;
import com.xiezhu.jzj.presenter.PluginHelper;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.viewholder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindSceneActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private SceneManager mSceneManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_scene);
        ButterKnife.bind(this);
        this.mSceneManager = new SceneManager(this);
        initView();
    }

    private void initView() {
        tvToolbarTitle.setText("场景绑定");
        tvToolbarRight.setText("绑定");
        tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.all_9));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        //this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), type, 1, this.mScenePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);

    }

    @OnClick({R.id.iv_toolbar_left, R.id.tv_toolbar_right, R.id.create_scene_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.tv_toolbar_right:
                break;
            case R.id.create_scene_view:
                SystemParameter.getInstance().setIsRefreshSceneListData(true);
                PluginHelper.createScene(this, CScene.TYPE_IFTTT, SystemParameter.getInstance().getHomeId());
                break;
        }
    }
}
