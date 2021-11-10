package com.laffey.smart.view;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

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

public class TimerLocalSceneListActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLocalSceneListBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private static final String DEVICE_ID = "device_id";
    private final int SCENE_LIST_REQUEST_CODE = 10000;
    private final int SCENE_LIST_RESULT_CODE = 10001;

    private BaseQuickAdapter<ItemSceneInGateway, BaseViewHolder> mAdapter;
    private final List<ItemSceneInGateway> mList = new ArrayList<>();

    private TypedArray mSceneBgs;
    private String mGatewayId;
    private String mDevIotId;
    private String mSceneType = "0";
    private String mGatewayMac;

    private MyHandler mHandler;
    private SceneManager mSceneManager;
    private int mDeletePos = 0;

    public static void start(Context context, String devId) {
        Intent intent = new Intent(context, TimerLocalSceneListActivity.class);
        intent.putExtra(DEVICE_ID, devId);
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
        /*queryMacByIotId();*/
        mDevIotId = getIntent().getStringExtra(DEVICE_ID);
        mSceneManager.getGWIotIdBySubIotId(this, mDevIotId,
                Constant.MSG_QUEST_GW_ID_BY_SUB_ID, Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR, mHandler);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<TimerLocalSceneListActivity> ref;

        public MyHandler(TimerLocalSceneListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TimerLocalSceneListActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    case Constant.MSG_QUEST_GW_ID_BY_SUB_ID: {
                        // 根据子设备iotId查询网关iotId
                        QMUITipDialogUtil.dismiss();
                        JSONObject response = (JSONObject) msg.obj;
                        int code = response.getInteger("code");
                        String gwId = response.getString("gwIotId");
                        if (code == 200) {
                            activity.mGatewayId = gwId;
                            if (Constant.IS_TEST_DATA) {
                                activity.mGatewayId = DeviceBuffer.getGatewayDevs().get(0).iotId;
                            }
                            activity.mGatewayMac = DeviceBuffer.getDeviceMac(activity.mGatewayId);
                            /*activity.mSceneManager.querySceneList(activity, activity.mGatewayMac, "0",
                                    Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, activity.mHandler);*/

                            for (ItemSceneInGateway scene : DeviceBuffer.getAllScene().values()) {
                                JSONObject appParams = scene.getAppParams();
                                if (appParams != null) {
                                    String switchIotId = appParams.getString("switchIotId");
                                    if (switchIotId == null || switchIotId.length() == 0)
                                        continue;
                                } else {
                                    continue;
                                }
                                ItemScene.Timer timer = scene.getSceneDetail().getTime();
                                if (timer == null || timer.getType() == null || timer.getType().length() == 0)
                                    continue;

                                activity.mList.add(scene);
                            }

                            if (activity.mList.size() == 0) {
                                activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                activity.mViewBinding.sceneRl.setVisibility(View.GONE);
                            } else {
                                activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                activity.mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                            }
                            activity.mAdapter.notifyDataSetChanged();
                            activity.mViewBinding.sceneRl.finishRefresh(true);
                        } else {
                            QMUITipDialogUtil.dismiss();
                            String message = response.getString("message");
                            String localizedMsg = response.getString("localizedMsg");
                            String errorMess = response.getString("errorMess");
                            if (message != null && message.length() > 0) {
                                ToastUtils.showLongToast(activity, message);
                            } else if (localizedMsg != null && localizedMsg.length() > 0) {
                                ToastUtils.showLongToast(activity, localizedMsg);
                            } else if (errorMess != null && errorMess.length() > 0) {
                                ToastUtils.showLongToast(activity, errorMess);
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            }
                        }
                        break;
                    }
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
                                    activity.mSceneManager.deleteScene(activity, activity.mList.get(activity.mDeletePos),
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
                        QMUITipDialogUtil.dismiss();
                        JSONObject response = (JSONObject) msg.obj;
                        int code = response.getInteger("code");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        ViseLog.d(GsonUtil.toJson(sceneList));
                        if (code == 0 || code == 200) {
                            if (sceneList != null) {
                                for (int i = 0; i < sceneList.size(); i++) {
                                    JSONObject sceneObj = sceneList.getJSONObject(i);
                                    ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                                    DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);
                                    JSONObject appParams = scene.getAppParams();
                                    if (appParams != null) {
                                        String switchIotId = appParams.getString("switchIotId");
                                        if (switchIotId == null || switchIotId.length() == 0)
                                            continue;
                                    } else {
                                        continue;
                                    }
                                    ItemScene.Timer timer = scene.getSceneDetail().getTime();
                                    if (timer == null || timer.getType().length() == 0) continue;

                                    activity.mList.add(scene);
                                }
                            }
                            if (activity.mList.size() == 0) {
                                activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                activity.mViewBinding.sceneRl.setVisibility(View.GONE);
                            } else {
                                activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                activity.mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                            }
                            activity.mAdapter.notifyDataSetChanged();
                            activity.mViewBinding.sceneRl.finishRefresh(true);
                        } else {
                            QMUITipDialogUtil.dismiss();
                            String message = response.getString("message");
                            String localizedMsg = response.getString("localizedMsg");
                            String errorMess = response.getString("errorMess");
                            if (message != null && message.length() > 0) {
                                ToastUtils.showLongToast(activity, message);
                            } else if (localizedMsg != null && localizedMsg.length() > 0) {
                                ToastUtils.showLongToast(activity, localizedMsg);
                            } else if (errorMess != null && errorMess.length() > 0) {
                                ToastUtils.showLongToast(activity, errorMess);
                            } else {
                                ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                            }
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
                            // ViseLog.d("mGatewayMac = " + activity.mGatewayMac);
                            // activity.querySceneList("chengxunfei", activity.mGatewayMac, activity.mSceneType);
                            activity.mSceneType = "0";
                            activity.mSceneManager.querySceneList(activity, activity.mGatewayMac, activity.mSceneType, Constant.MSG_QUEST_QUERY_SCENE_LIST,
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
                    case Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR:
                    case Constant.MSG_QUEST_DELETE_SCENE_ERROR:
                    case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR:
                    case Constant.MSG_QUEST_QUERY_MAC_BY_IOT_ID_ERROR: {
                        // 根据设备iotId获取设备mac失败
                        Throwable e = (Throwable) msg.obj;
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        activity.mSceneType = "0";
                        ToastUtils.showLongToast(activity, e.getMessage());
                        activity.mViewBinding.nodataTv.setText(e.getMessage());
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
                mSceneManager.querySceneList(TimerLocalSceneListActivity.this, mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RealtimeDataReceiver.addEventCallbackHandler("TimerLocalSceneListCallback", mHandler);
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

                Intent intent = new Intent(TimerLocalSceneListActivity.this, LocalSceneActivity.class);
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

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.detail_state_timer);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.createSceneTv.getId()) {
            // ViseLog.d("跳转 mGatewayId = " + mGatewayId + " , mGatewayMac = " + mGatewayMac);
            // EventBus.getDefault().postSticky(null);
            TimerLocalSceneActivity.start(this, mDevIotId, mGatewayId, mGatewayMac, SCENE_LIST_REQUEST_CODE);
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
                mList.clear();
                mSceneManager.querySceneList(this, mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            } else if (resultCode == Constant.DEL_SCENE_IN_LOCALSCENEACTIVITY) {
                // 删除场景
                ToastUtils.showLongToast(mActivity, R.string.scene_delete_successfully);
                mSceneType = "0";
                mList.clear();
                mSceneManager.querySceneList(this, mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            } else if (resultCode == Constant.RESULT_CODE_UPDATE_SCENE) {
                // 编辑场景
                mSceneType = "0";
                ToastUtils.showLongToast(this, R.string.scene_updated_successfully);
                mList.clear();
                mSceneManager.querySceneList(this, mGatewayMac, mSceneType,
                        Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mHandler);
            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, ItemSceneInGateway scene) {
        /*View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null, false);
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
                    mSceneManager.deleteScene(TimerLocalSceneListActivity.this, scene,
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
        window.setLayout(width - 150, height / 5);*/

        DialogUtils.showConfirmDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constant.IS_TEST_DATA) {
                    mSceneManager.deleteScene(TimerLocalSceneListActivity.this, scene,
                            Constant.MSG_QUEST_DELETE_SCENE, Constant.MSG_QUEST_DELETE_SCENE_ERROR, mHandler);
                } else {
                    mSceneManager.manageSceneService(mGatewayId, scene.getSceneDetail().getSceneId(), 3,
                            mCommitFailureHandler, mResponseErrorHandler, mHandler);
                }
            }
        }, content, title);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler("TimerLocalSceneListCallback");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}