package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLightSceneListBinding;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LightLocalSceneListActivity extends BaseActivity {
    private ActivityLightSceneListBinding mViewBinding;

    private final int LIGHT_LOCAL_SCENE_REQUEST_CODE = 10000;

    private String mIotId;
    private SceneManager mSceneManager;
    private List<EScene.sceneListItemEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder> mAdapter;

    private String mGwId;
    private String mGwMac;
    private final List<ItemSceneInGateway> mItemSceneList = new ArrayList<>();

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

        mGwId = getIntent().getStringExtra("gw_id");
        mGwMac = getIntent().getStringExtra("gw_mac");

        mSceneManager = new SceneManager(this);
        initAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mViewBinding.recycleView.setLayoutManager(gridLayoutManager);
        mViewBinding.recycleView.setAdapter(mAdapter);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
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
        /*if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            getList();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getList() {
        mSceneManager.querySceneList(this, mGwMac, "1",
                Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
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
            if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {

                LightLocalSceneActivity.start(this, null, mIotId, mGwId, mGwMac, mActivityTag, mList.get(position).id, LIGHT_LOCAL_SCENE_REQUEST_CODE);
            } else
                LightSceneActivity.start(LightLocalSceneListActivity.this, mList.get(position), mIotId, mActivityTag);
        });
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                    // 查询网关下本地场景列表
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    if (code == 0 || code == 200) {
                        if (sceneList != null) {
                            QMUITipDialogUtil.dismiss();
                            mItemSceneList.clear();
                            mList.clear();
                            for (int i = 0; i < sceneList.size(); i++) {
                                ItemSceneInGateway scene = JSONObject.parseObject(sceneList.get(i).toString(), ItemSceneInGateway.class);
                                JSONObject appParams = scene.getAppParams();
                                if (appParams == null) continue;
                                String switchIotId = appParams.getString("switchIotId");
                                if (switchIotId == null || switchIotId.length() == 0) {
                                    continue;
                                }
                                if (mIotId.equals(switchIotId)) {
                                    DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                                    mItemSceneList.add(scene);

                                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                                    entry.id = scene.getSceneDetail().getSceneId();
                                    entry.name = scene.getSceneDetail().getName();
                                    entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                                    entry.description = scene.getGwMac();
                                    mList.add(entry);
                                }
                            }
                            ViseLog.d("调光调色场景列表 mItemSceneList = " + GsonUtil.toJson(mItemSceneList) +
                                    "\nmList = " + GsonUtil.toJson(mList));
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(LightLocalSceneListActivity.this, response);
                    }
                    break;
                }
            }
            return false;
        }
    });

    public void onViewClicked(View view) {
        if (view.getId() == R.id.create_scene_view) {
            LightLocalSceneActivity.start(this, null, mIotId, mGwId, mGwMac, mActivityTag, null, LIGHT_LOCAL_SCENE_REQUEST_CODE);
        }
    }

    private String mActivityTag;

    public static void start(Context context, String iotId, String gwId, String gwMac, String tag) {
        Intent intent = new Intent(context, LightLocalSceneListActivity.class);
        intent.putExtra("extra", iotId);
        intent.putExtra("activity_tag", tag);
        intent.putExtra("gw_id", gwId);
        intent.putExtra("gw_mac", gwMac);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIGHT_LOCAL_SCENE_REQUEST_CODE) {
            if (resultCode == 10001) {
                getList();
                ToastUtils.showLongToast(this, R.string.scenario_created_successfully);
            } else if (resultCode == 10002) {
                getList();
                ToastUtils.showLongToast(this, R.string.scene_updated_successfully);
            } else if (resultCode == 10003) {
                getList();
                ToastUtils.showLongToast(this, R.string.scene_delete_successfully);
            }
        }
    }
}
