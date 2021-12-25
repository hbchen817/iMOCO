package com.rexense.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.smart.R;
import com.rexense.smart.contract.CScene;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityLightSceneListBinding;
import com.rexense.smart.event.CEvent;
import com.rexense.smart.event.EEvent;
import com.rexense.smart.model.EScene;
import com.rexense.smart.presenter.CloudDataParser;
import com.rexense.smart.presenter.SceneManager;
import com.rexense.smart.presenter.SystemParameter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LightSceneListActivity extends BaseActivity {
    private ActivityLightSceneListBinding mViewBinding;

    private String mIotId;
    private SceneManager mSceneManager;
    private List<EScene.sceneListItemEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightSceneListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mViewBinding.includeToolbar.tvToolbarTitle.setText("场景列表");
        initStatusBar();
        mIotId = getIntent().getStringExtra("extra");
        mActivityTag = getIntent().getStringExtra("activity_tag");
        mSceneManager = new SceneManager(this);
        initAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mViewBinding.recycleView.setLayoutManager(gridLayoutManager);
        mViewBinding.recycleView.setAdapter(mAdapter);
        getList();
        mViewBinding.createSceneView.setOnClickListener(this::onViewClicked);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Subscribe
    public void refreshSceneList(EEvent eventEntry) {
        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            getList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getList() {
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder>(R.layout.item_scene, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, EScene.sceneListItemEntry sceneListItemEntry) {
                baseViewHolder.setText(R.id.sceneName, sceneListItemEntry.name)
                        .setVisible(R.id.editMask, false);
            }
        };
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            LightSceneActivity.start(LightSceneListActivity.this, mList.get(position), mIotId, mActivityTag);
        });
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_QUERYSCENELIST) {// 处理获取场景列表数据
                EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                if (sceneList != null && sceneList.scenes != null) {
                    for (EScene.sceneListItemEntry item : sceneList.scenes) {
                        if (item.description.contains(mIotId)) {
                            mList.add(item);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (sceneList.scenes.size() >= sceneList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                }
            }
            return false;
        }
    });

    public void onViewClicked(View view) {
        if (view.getId() == R.id.create_scene_view) {
            LightSceneActivity.start(this, null, mIotId, mActivityTag);
        }
    }

    private String mActivityTag;

    public static void start(Context context, String iotId, String tag) {
        Intent intent = new Intent(context, LightSceneListActivity.class);
        intent.putExtra("extra", iotId);
        intent.putExtra("activity_tag", tag);
        context.startActivity(intent);
    }

}
