package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalSceneListBinding;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EEventScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LocalSceneListActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLocalSceneListBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private final int SCENE_LIST_REQUEST_CODE = 10000;
    private final int SCENE_LIST_RESULT_CODE = 10001;

    private BaseQuickAdapter<ItemSceneInGateway, BaseViewHolder> mAdapter;
    private final List<ItemSceneInGateway> mList = new ArrayList<>();

    private TypedArray mSceneBgs;
    private String mGatewayId;
    private String mSceneType = "0";
    private String mGatewayMac;

    private MyHandler mHandler;
    private SceneManager mSceneManager;
    private int mDeletePos = 0;

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
        mGatewayMac = DeviceBuffer.getDeviceMac(mGatewayId);
    }

    private void loadAllScene() {
        mList.clear();
        for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
            JSONObject appParams = scene.getAppParams();
            if (appParams != null) {
                String switchIotId = appParams.getString("switchIotId");
                if (switchIotId != null && switchIotId.length() > 0)
                    continue;
            }
            if (!scene.getGwMac().equals(mGatewayMac)) return;

            mList.add(scene);
        }
        if (mList.size() == 0) {
            mViewBinding.nodataView.setVisibility(View.VISIBLE);
            mViewBinding.sceneRl.setVisibility(View.GONE);
        } else {
            mViewBinding.nodataView.setVisibility(View.GONE);
            mViewBinding.sceneRl.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<LocalSceneListActivity> ref;

        public MyHandler(LocalSceneListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LocalSceneListActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    /*case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                        // 删除网关下的场景
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        JSONObject value = jsonObject.getJSONObject("value");
                        String identifier = jsonObject.getString("identifier");
                        if ("ManageSceneNotification".equals(identifier)) {
                            String type = value.getString("Type");
                            String status = value.getString("Status");
                            // status  0: 成功  1: 失败
                            if ("0".equals(status)) {
                                // type  1: 增加场景  2: 编辑场景  3: 删除场景
                                if ("3".equals(type)) {
                                    activity.mSceneManager.deleteScene(activity, activity.mList.get(activity.mDeletePos),
                                            Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mHandler);
                                }
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            }
                        }
                        break;
                    }*/
                    case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                        // 获取网关下本地场景列表
                        JSONObject response = (JSONObject) msg.obj;
                        int code = response.getInteger("code");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 0 || code == 200) {
                            if ("0".equals(activity.mSceneType)) DeviceBuffer.initSceneBuffer();
                            if (sceneList != null) {
                                for (int i = 0; i < sceneList.size(); i++) {
                                    JSONObject sceneObj = sceneList.getJSONObject(i);
                                    ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                                    DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                                    JSONObject appParams = scene.getAppParams();
                                    if (appParams != null) {
                                        String switchIotId = appParams.getString("switchIotId");
                                        if (switchIotId != null && switchIotId.length() > 0)
                                            continue;
                                    }

                                    activity.mList.add(scene);
                                }
                            }
                            if ("0".equals(activity.mSceneType)) {
                                activity.mSceneType = "1";
                                // querySceneList("chengxunfei", mGatewayMac, mSceneType);
                                activity.mSceneManager.querySceneList(activity, activity.mGatewayMac, activity.mSceneType,
                                        Constant.MSG_QUEST_QUERY_SCENE_LIST,
                                        Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, activity.mHandler);
                            } else if ("1".equals(activity.mSceneType)) {
                                QMUITipDialogUtil.dismiss();
                                if (activity.mList.size() == 0) {
                                    activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                    activity.mViewBinding.sceneRl.setVisibility(View.GONE);
                                } else {
                                    activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                    activity.mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                                }
                                activity.mAdapter.notifyDataSetChanged();
                                activity.mSceneType = "0";
                                activity.mViewBinding.sceneRl.finishRefresh(true);
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            activity.mSceneType = "0";
                            RetrofitUtil.showErrorMsg(activity, response);
                            activity.mViewBinding.sceneRl.finishRefresh(false);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void initView() {
        mSceneManager = new SceneManager(this);
        mHandler = new MyHandler(this);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);

        mViewBinding.createSceneTv.setTypeface(iconfont);
        mViewBinding.createSceneTv.setOnClickListener(this);

        mViewBinding.sceneRl.setEnableLoadMore(false);
        mViewBinding.sceneRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                querySceneList();
            }
        });
    }

    private void querySceneList() {
        SceneManager.querySceneList(this, mGatewayMac, "", new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                // ViseLog.d("场景列表 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    DeviceBuffer.initSceneBuffer();
                    if (sceneList != null) {
                        mList.clear();
                        for (int i = 0; i < sceneList.size(); i++) {
                            JSONObject sceneObj = sceneList.getJSONObject(i);
                            ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                            DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                            JSONObject appParams = scene.getAppParams();
                            if (appParams != null) {
                                String switchIotId = appParams.getString("switchIotId");
                                if (switchIotId != null && switchIotId.length() > 0)
                                    continue;
                            }

                            mList.add(scene);
                        }
                    }
                    QMUITipDialogUtil.dismiss();
                    if (mList.size() == 0) {
                        mViewBinding.nodataView.setVisibility(View.VISIBLE);
                        mViewBinding.sceneRl.setVisibility(View.GONE);
                    } else {
                        mViewBinding.nodataView.setVisibility(View.GONE);
                        mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    mViewBinding.sceneRl.finishRefresh(true);
                } else {
                    QMUITipDialogUtil.dismiss();
                    mViewBinding.sceneRl.finishRefresh(false);
                    RetrofitUtil.showErrorMsg(LocalSceneListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(LocalSceneListActivity.this, e.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllScene();
        RealtimeDataReceiver.addEventCallbackHandler("LocalSceneListCallback", mHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemSceneInGateway, BaseViewHolder>(R.layout.item_scene, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemSceneInGateway item) {
                int pos = mList.indexOf(item);
                pos = pos % mSceneBgs.length();
                holder.setText(R.id.sceneName, item.getSceneDetail().getName());
                holder.setGone(R.id.editMask, true);
                holder.setImageResource(R.id.image, mSceneBgs.getResourceId(pos, 0));
            }
        };
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                mDeletePos = position;
                showConfirmDialog(getString(R.string.dialog_title), String.format(getString(R.string.do_you_want_del_scene),
                        mList.get(position).getSceneDetail().getName()), getString(R.string.dialog_cancel), getString(R.string.delete), mList.get(position));
                return true;
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EEventScene scene = new EEventScene();
                scene.setTarget("LocalSceneActivity");
                scene.setGatewayId(mGatewayId);
                scene.setScene(mList.get(position).getSceneDetail());
                scene.setGatewayMac(mList.get(position).getGwMac());
                EventBus.getDefault().postSticky(scene);

                Intent intent = new Intent(LocalSceneListActivity.this, LocalSceneActivity.class);
                startActivityForResult(intent, SCENE_LIST_REQUEST_CODE);
            }
        });
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
            // EventBus.getDefault().postSticky(null);
            LocalSceneActivity.start(this, mGatewayId, mGatewayMac, SCENE_LIST_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCENE_LIST_REQUEST_CODE) {
            if (resultCode == Constant.ADD_LOCAL_SCENE) {
                // 新增场景
                mSceneType = "0";
                ToastUtils.showLongToast(this, R.string.scenario_created_successfully);
                //loadAllScene();
                querySceneList();
            } else if (resultCode == Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY) {
                // 删除场景
                ToastUtils.showLongToast(mActivity, R.string.scene_delete_successfully);
                mSceneType = "0";
                //loadAllScene();
                querySceneList();
            } else if (resultCode == Constant.RESULT_CODE_UPDATE_SCENE) {
                // 编辑场景
                mSceneType = "0";
                ToastUtils.showLongToast(this, R.string.scene_updated_successfully);
                // loadAllScene();
                querySceneList();
            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, ItemSceneInGateway scene) {
        DialogUtils.showConfirmDialog(this, title, content, ok, cancel, new DialogUtils.Callback() {
            @Override
            public void positive() {
                SceneManager.deleteScene(LocalSceneListActivity.this, mGatewayMac, scene.getSceneDetail().getSceneId(), new SceneManager.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        // 删除网关下本地场景
                        int code = response.getInteger("code");
                        String sceneId = response.getString("sceneId");
                        if (code == 200) {
                            boolean result = response.getBoolean("result");
                            if (result) {
                                ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.scene_delete_sucess);
                                DeviceBuffer.removeScene(sceneId);
                                for (ItemSceneInGateway scene : mList) {
                                    if (scene.getSceneDetail().getSceneId().equals(sceneId)) {
                                        mList.remove(scene);
                                        break;
                                    }
                                }
                                SceneManager.manageSceneService(mGatewayId, sceneId, 3,
                                        mCommitFailureHandler, mResponseErrorHandler, mHandler);
                                mAdapter.notifyDataSetChanged();
                                RefreshData.refreshHomeSceneListData();
                                if (mList.size() == 0) {
                                    mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                    mViewBinding.sceneRl.setVisibility(View.GONE);
                                } else {
                                    mViewBinding.nodataView.setVisibility(View.GONE);
                                    mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                                }
                            } else {
                                QMUITipDialogUtil.dismiss();
                                RetrofitUtil.showErrorMsg(LocalSceneListActivity.this, response);
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            RetrofitUtil.showErrorMsg(LocalSceneListActivity.this, response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalSceneListActivity.this, e.getMessage());
                    }
                });
            }

            @Override
            public void negative() {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler("LocalSceneListCallback");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}