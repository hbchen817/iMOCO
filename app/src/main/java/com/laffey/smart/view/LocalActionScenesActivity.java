package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalActionScenesBinding;
import com.laffey.smart.model.EAction;
import com.laffey.smart.model.EActionScene;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalActionScenesActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLocalActionScenesBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";

    private String mGatewayId;
    private String mGatewayMac;

    private final List<ItemScene> mSceneList = new ArrayList<>();
    private BaseQuickAdapter<ItemScene, BaseViewHolder> mSceneAdapter;

    private Typeface mIconfont;
    private EAction mEAction;

    private int mSelectPos = -1;

    public static void start(Context context, String gatewayId) {
        Intent intent = new Intent(context, LocalActionScenesActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalActionScenesBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mEAction = new EAction();
        EventBus.getDefault().register(this);
        initStatusBar();
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        mSceneAdapter = new BaseQuickAdapter<ItemScene, BaseViewHolder>(R.layout.item_action_scene, mSceneList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemScene itemScene) {
                TextView icon = holder.getView(R.id.icon_iv);
                icon.setText(R.string.icon_scene);
                icon.setTypeface(mIconfont);

                TextView select = holder.getView(R.id.go_iv);
                select.setTypeface(mIconfont);
                select.setText(R.string.icon_checked);
                select.setTextColor(ContextCompat.getColor(LocalActionScenesActivity.this, R.color.appcolor));

                holder.setText(R.id.title, itemScene.getName());

                if (mSelectPos != mSceneList.indexOf(itemScene)) {
                    select.setVisibility(View.INVISIBLE);
                } else {
                    select.setVisibility(View.VISIBLE);
                }

                if (mSceneList.indexOf(itemScene) == 0) {
                    holder.setVisible(R.id.divider, false);
                }
            }
        };
        mSceneAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@androidx.annotation.NonNull BaseQuickAdapter<?, ?> adapter, @androidx.annotation.NonNull View view, int position) {
                mSelectPos = position;
                adapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.sceneRv.setLayoutManager(layoutManager);
        mViewBinding.sceneRv.setAdapter(mSceneAdapter);
    }

    private void initData() {
        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);

        if (Constant.IS_TEST_DATA) {
            mGatewayId = "i1cU8RQDuaUsaNvw4ScgeND83D";
        }
        queryMacByIotId("chengxunfei", Constant.QUERY_MAC_BY_IOTID_VER, "xxxxxx", mGatewayId);
    }

    // 根据IotId查询Mac
    private void queryMacByIotId(String token, String apiVer, String plantForm, String iotId) {
        RetrofitUtil.getInstance()
                .queryMacByIotId(token, apiVer, plantForm, iotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        String mac = response.getString("mac");
                        String msg = response.getString("message");
                        ViseLog.d(response.toJSONString());
                        if (code == 200) {
                            if (mac == null || mac.length() == 0) {
                                ToastUtils.showLongToast(LocalActionScenesActivity.this, R.string.MAC_does_not_exist);
                            }
                            if (Constant.IS_TEST_DATA) {
                                mac = "LUXE_TEST";
                            }
                            mGatewayMac = mac;
                            querySceneList(token, mac, "1");
                        } else if (code == 404) {
                            ToastUtils.showLongToast(LocalActionScenesActivity.this, msg);
                        } else {
                            ToastUtils.showLongToast(LocalActionScenesActivity.this, R.string.pls_try_again_later);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalActionScenesActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 查询本地场景列表
    private void querySceneList(String token, String mac, String type) {
        RetrofitUtil.getInstance().querySceneList(token, mac, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 200) {
                            for (int i = 0; i < sceneList.size(); i++) {
                                JSONObject item = sceneList.getJSONObject(i);
                                ItemSceneInGateway sceneInGateway = new Gson().fromJson(item.toJSONString(), ItemSceneInGateway.class);
                                if (sceneInGateway.getAppParams() != null) {
                                    String key = sceneInGateway.getAppParams().getString("key");
                                    if (key != null && key.length() > 0) {
                                        continue;
                                    }
                                }

                                ItemScene scene = new ItemScene();
                                scene.setName(sceneInGateway.getSceneDetail().getName());
                                scene.setSceneId(sceneInGateway.getSceneDetail().getSceneId());
                                if (mEAction.getAction() != null && mEAction.getAction().getParameters() != null) {
                                    if (scene.getSceneId().equals(mEAction.getAction().getParameters().getSceneId())) {
                                        mSelectPos = i;
                                    }
                                }
                                mSceneList.add(scene);
                            }
                            if (mSceneList.size() == 0) {
                                mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                mViewBinding.sceneRl.setVisibility(View.GONE);
                            } else {
                                mViewBinding.nodataView.setVisibility(View.GONE);
                                mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                            }
                            mSceneAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showLongToast(LocalActionScenesActivity.this, msg);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalActionScenesActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.select_the_scene));
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.nick_name_save);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.includeToolbar.tvToolbarRight.getId()) {
            String target = DeviceBuffer.getCacheInfo("LocalSceneTag");
            mEAction.setTarget(target);
            ItemScene.Action action = new ItemScene.Action();
            action.setType("Scene");

            ItemScene.ActionParameter parameter = new ItemScene.ActionParameter();
            parameter.setSceneId(mSceneList.get(mSelectPos).getSceneId());

            action.setParameters(parameter);
            mEAction.setKeyNickName(mSceneList.get(mSelectPos).getName());
            mEAction.setAction(action);

            EventBus.getDefault().postSticky(mEAction);
            if ("LocalSceneActivity".equals(target)) {
                Intent intent = new Intent(this, LocalSceneActivity.class);
                startActivity(intent);
            } else if ("SwitchLocalSceneActivity".equals(target)) {
                Intent intent = new Intent(this, SwitchLocalSceneActivity.class);
                startActivity(intent);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Object obj) {
        if (obj instanceof EAction) {
            mEAction = (EAction) obj;
            if ("LocalActionScenesActivity".equals(mEAction.getTarget())) {
                ViseLog.d(GsonUtil.toJson(mEAction));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}