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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalSceneListBinding;
import com.laffey.smart.model.EEventScene;
import com.laffey.smart.model.ItemScene;
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

    private BaseQuickAdapter<ItemScene, BaseViewHolder> mAdapter;
    private final List<ItemScene> mList = new ArrayList<>();

    private TypedArray mSceneBgs;
    private String mGatewayId;
    private String mSceneType = "0";
    private String mGatewayMac;

    private MyHandler mHandler;
    private SceneManager mSceneManager;

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
        ViseLog.d("LocalSceneListActivity mGatewayId = " + mGatewayId);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        queryMacByIotId();
    }

    // 根据IotId查询网关Mac
    private void queryMacByIotId() {
        RetrofitUtil.getInstance().queryMacByIotId("chengxunfei", Constant.QUERY_MAC_BY_IOTID_VER, "xxxxxx", "i1cU8RQDuaUsaNvw4ScgeND83D")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        String mac = response.getString("mac");
                        mSceneType = "0";
                        if (code == 200) {
                            mGatewayMac = mac;
                            // ViseLog.d("mGatewayMac = " + mGatewayMac);
                            querySceneList("chengxunfei", mGatewayMac, mSceneType);
                        } else {
                            if (msg != null && msg.length() > 0) {
                                ToastUtils.showLongToast(LocalSceneListActivity.this, msg);
                                mViewBinding.nodataTv.setText(msg);
                            } else {
                                ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                                mViewBinding.nodataTv.setText(R.string.pls_try_again_later);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        mSceneType = "0";
                        ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                        mViewBinding.nodataTv.setText(R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

            }
        }
    }

    // 查询本地场景列表
    private void querySceneList(String token, String mac, String type) {
        RetrofitUtil.getInstance().querySceneList(token, mac, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        JSONArray sceneList = response.getJSONArray("sceneList");
                        if (code == 0 || code == 200) {
                            if (sceneList != null) {
                                for (int i = 0; i < sceneList.size(); i++) {
                                    JSONObject sceneObj = sceneList.getJSONObject(i);
                                    ItemScene scene = JSONObject.toJavaObject(sceneObj, ItemScene.class);
                                    mList.add(scene);
                                }
                            }
                            if ("0".equals(mSceneType)) {
                                mSceneType = "1";
                                querySceneList("chengxunfei", mGatewayMac, mSceneType);
                            } else if ("1".equals(mSceneType)) {
                                QMUITipDialogUtil.dismiss();
                                if (mList.size() == 0) {
                                    mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                    mViewBinding.sceneRl.setVisibility(View.GONE);
                                } else {
                                    mViewBinding.nodataView.setVisibility(View.GONE);
                                    mViewBinding.sceneRl.setVisibility(View.VISIBLE);
                                }
                                mAdapter.notifyDataSetChanged();
                                mSceneType = "0";
                                mViewBinding.sceneRl.finishRefresh(true);
                            }
                        } else {
                            QMUITipDialogUtil.dismiss();
                            mSceneType = "0";
                            if (msg != null && msg.length() > 0)
                                ToastUtils.showLongToast(LocalSceneListActivity.this, msg);
                            else
                                ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                            mViewBinding.sceneRl.finishRefresh(false);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        mSceneType = "0";
                        ViseLog.e(e);
                        ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                querySceneList("chengxunfei", mGatewayMac, mSceneType);
            }
        });
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemScene, BaseViewHolder>(R.layout.item_scene, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemScene item) {
                int pos = mList.indexOf(item);
                pos = pos % mSceneBgs.length();
                holder.setText(R.id.sceneName, item.getName());
                holder.setGone(R.id.editMask, true);
                holder.setImageResource(R.id.image, mSceneBgs.getResourceId(pos, 0));
            }
        };
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showConfirmDialog(getString(R.string.dialog_title), String.format(getString(R.string.do_you_want_del_scene),
                        mList.get(position).getName()), getString(R.string.dialog_cancel), getString(R.string.delete), mList.get(position));
                return true;
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EEventScene scene = new EEventScene();
                scene.setTarget("LocalSceneActivity");
                scene.setGatewayId(mGatewayId);
                scene.setScene(mList.get(position));
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
                querySceneList("chengxunfei", mGatewayMac, mSceneType);
            }
        }
    }

    private void showConfirmDialog(String title, String content, String cancel, String ok, ItemScene scene) {
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
                RetrofitUtil.getInstance()
                        .deleteScene("chengxunfei", mGatewayMac, scene.getSceneId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                                int code = response.getInteger("code");
                                String msg = response.getString("message");
                                boolean result = response.getBoolean("result");
                                if (code == 200) {
                                    if (result) {
                                        ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.scene_delete_sucess);
                                        mSceneManager.manageSceneService(mGatewayId, scene.getSceneId(), "3", mCommitFailureHandler, mResponseErrorHandler, mHandler);
                                        mList.remove(scene);
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        if (msg == null || msg.length() == 0) {
                                            ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                                        } else
                                            ToastUtils.showLongToast(LocalSceneListActivity.this, msg);
                                    }
                                } else {
                                    if (msg == null || msg.length() == 0) {
                                        ToastUtils.showLongToast(LocalSceneListActivity.this, R.string.pls_try_again_later);
                                    } else
                                        ToastUtils.showLongToast(LocalSceneListActivity.this, msg);
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        dialog.show();
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_white_solid));
        window.setLayout(width - 150, height / 5);
    }
}