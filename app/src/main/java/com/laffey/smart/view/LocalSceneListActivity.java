package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalSceneListBinding;
import com.laffey.smart.model.SceneListResponse;
import com.laffey.smart.utility.RetrofitUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LocalSceneListActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLocalSceneListBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";

    private BaseQuickAdapter<SceneItem, BaseViewHolder> mAdapter;
    private List<SceneItem> mList = new ArrayList<>();

    private TypedArray mSceneBgs;
    private String mGatewayId;

    public static void start(Context context, String gatewayId) {
        Intent intent = new Intent(context, LocalSceneListActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalSceneListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mSceneBgs = getResources().obtainTypedArray(R.array.scene_bgs);

        initStatusBar();
        initView();
        initAdapter();
        initData();
    }

    private void initData() {
        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);

        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景1"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景2"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景3"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景4"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景5"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景6"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景7"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景8"));
        mList.add(new SceneItem(UUID.randomUUID().toString(), "场景9"));
        if (mList.size() > 0) {
            mViewBinding.sceneRl.setVisibility(View.VISIBLE);
            mViewBinding.nodataView.setVisibility(View.GONE);
        } else {
            mViewBinding.sceneRl.setVisibility(View.GONE);
            mViewBinding.nodataView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();

        JSONObject obj = new JSONObject();
        obj.put("apiVer", "1.0");
        JSONObject params = new JSONObject();
        params.put("mac", "00-50-56-C0-00-10");
        params.put("type", "1");
        obj.put("params", params);

        RetrofitUtil.getInstance().getService()
                .querySceneList("chengxunfei", RetrofitUtil.convertToBody(obj.toJSONString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SceneListResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull SceneListResponse response) {
                        ViseLog.d(new Gson().toJson(response));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.d(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);

        mViewBinding.createSceneTv.setTypeface(iconfont);
        mViewBinding.createSceneTv.setOnClickListener(this);

        mViewBinding.sceneRl.setEnableLoadMore(false);
        mViewBinding.sceneRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<SceneItem, BaseViewHolder>(R.layout.item_scene, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, SceneItem item) {
                int pos = mList.indexOf(item);
                pos = pos % mSceneBgs.length();
                holder.setText(R.id.sceneName, item.getSceneName());
                holder.setGone(R.id.editMask, true);
                holder.setImageResource(R.id.image, mSceneBgs.getResourceId(pos, 0));
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mViewBinding.sceneRv.setLayoutManager(layoutManager);
        mViewBinding.sceneRv.setAdapter(mAdapter);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.scene_list);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.createSceneTv.getId()) {
            LocalSceneActivity.start(this, mGatewayId);
        }
    }

    private static class SceneItem {
        private String sceneId;
        private String sceneName;

        public SceneItem(String sceneId, String sceneName) {
            this.sceneId = sceneId;
            this.sceneName = sceneName;
        }

        public String getSceneId() {
            return sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }
    }
}