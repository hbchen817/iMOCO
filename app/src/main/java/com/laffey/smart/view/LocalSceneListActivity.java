package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
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
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        queryMacByIotId();
    }

    // 根据IotId查询网关Mac
    private void queryMacByIotId() {
        if (Constant.IS_TEST_DATA) {
            mGatewayId = "i1cU8RQDuaUsaNvw4ScgeND83D";
        }
        mSceneManager.queryMacByIotId("chengxunfei", mGatewayId, Constant.MSG_QUEST_QUERY_MAC_BY_IOT_ID,
                Constant.MSG_QUEST_QUERY_MAC_BY_IOT_ID_ERROR, mHandler);
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
                    case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
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
                                    activity.mSceneManager.deleteScene("chengxunfei", activity.mList.get(activity.mDeletePos),
                                            Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, activity.mHandler);
                                }
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            }
                        }
                        break;
                    }
                    case Constant.MSG_QUEST_DELETE_SCENE: {
                        // 删除网关下本地场景
                        JSONObject response = (JSONObject) msg.obj;
                        int code = response.getInteger("code");
                        String message = response.getString("message");
                        String sceneId = response.getString("sceneId");
                        if (code == 200) {
                            boolean result = response.getBoolean("result");
                            if (result) {
                                DeviceBuffer.removeScene(sceneId);
                                ToastUtils.showLongToast(activity, R.string.scene_delete_sucess);
                                for (ItemSceneInGateway scene : activity.mList) {
                                    if (scene.getSceneDetail().getSceneId().equals(sceneId)) {
                                        activity.mList.remove(scene);
                                        break;
                                    }
                                }
                                activity.mAdapter.notifyDataSetChanged();
                                RefreshData.refreshHomeSceneListData();
                                if (activity.mList.size() == 0) {
                                    activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                    activity.mViewBinding.sceneRl.setVisibility(View.GONE);
                                } else {
                                    activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                    activity.mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (message == null || message.length() == 0) {
                                    ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                                } else
                                    ToastUtils.showLongToast(activity, message);
                            }
                        } else {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(activity, message);
                        }
                        break;
                    }
                    case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                        // 获取网关下本地场景列表
                        JSONObject response = (JSONObject) msg.obj;
                        int code = response.getInteger("code");
                        String message = response.getString("message");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 0 || code == 200) {
                            if (sceneList != null) {
                                for (int i = 0; i < sceneList.size(); i++) {
                                    JSONObject sceneObj = sceneList.getJSONObject(i);
                                    ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);

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
                                activity.mSceneManager.querySceneList("chengxunfei", activity.mGatewayMac, activity.mSceneType,
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
                            if (message != null && message.length() > 0)
                                ToastUtils.showLongToast(activity, message);
                            else
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            activity.mViewBinding.sceneRl.finishRefresh(false);
                        }
                        break;
                    }
                    case Constant.MSG_QUEST_QUERY_MAC_BY_IOT_ID: {
                        // 根据设备iotId获取设备mac
                        JSONObject response = (JSONObject) msg.obj;
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String message = response.getString("message");
                        String mac = response.getString("mac");
                        activity.mSceneType = "0";
                        if (code == 200) {
                            if (Constant.IS_TEST_DATA) {
                                mac = "LUXE_TEST";
                            }
                            activity.mGatewayMac = mac;
                            // ViseLog.d("mGatewayMac = " + mGatewayMac);
                            // activity.querySceneList("chengxunfei", activity.mGatewayMac, activity.mSceneType);
                            activity.mSceneType = "0";
                            activity.mSceneManager.querySceneList("chengxunfei", activity.mGatewayMac, activity.mSceneType, Constant.MSG_QUEST_QUERY_SCENE_LIST,
                                    Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, activity.mHandler);
                        } else {
                            if (message != null && message.length() > 0) {
                                ToastUtils.showLongToast(activity, message);
                                activity.mViewBinding.nodataTv.setText(message);
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                                activity.mViewBinding.nodataTv.setText(R.string.pls_try_again_later);
                            }
                        }
                        break;
                    }
                    case Constant.MSG_QUEST_DELETE_SCENE_ERROR:
                    case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR:
                    case Constant.MSG_QUEST_QUERY_MAC_BY_IOT_ID_ERROR: {
                        // 根据设备iotId获取设备mac失败
                        Throwable e = (Throwable) msg.obj;
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        activity.mSceneType = "0";
                        ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        activity.mViewBinding.nodataTv.setText(R.string.pls_try_again_later);
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
                mSceneType = "0";
                mList.clear();
                mSceneManager.querySceneList("chengxunfei", mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            }
        });
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
            // ViseLog.d("跳转 mGatewayId = " + mGatewayId + " , mGatewayMac = " + mGatewayMac);
            LocalSceneActivity.start(this, mGatewayId, mGatewayMac, SCENE_LIST_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCENE_LIST_REQUEST_CODE) {
            if (resultCode == SCENE_LIST_RESULT_CODE) {
                mSceneType = "0";
                QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
                mList.clear();
                mSceneManager.querySceneList("chengxunfei", mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, ItemSceneInGateway scene) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView titleTV = (TextView) view.findViewById(R.id.title_tv);
        TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
        TextView disagreeTV = (TextView) view.findViewById(R.id.disagree_btn);
        TextView agreeTV = (TextView) view.findViewById(R.id.agree_btn);

        titleTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        disagreeTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        agreeTV.setTextSize(getResources().getDimension(R.dimen.sp_6));
        contentTV.setTextSize(getResources().getDimension(R.dimen.sp_6));

        titleTV.setText(title);
        contentTV.setText(content);
        disagreeTV.setText(cancel);
        agreeTV.setText(ok);

        disagreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        agreeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Constant.IS_TEST_DATA) {
                    mSceneManager.deleteScene("chengxunfei", scene,
                            Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, mHandler);
                } else {
                    mSceneManager.manageSceneService(mGatewayId, scene.getSceneDetail().getSceneId(), 3,
                            mCommitFailureHandler, mResponseErrorHandler, mHandler);
                }
            }
        });

        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        dialog.show();
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_white_solid));
        window.setLayout(width - 150, height / 5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler("LocalSceneListCallback");
    }
}