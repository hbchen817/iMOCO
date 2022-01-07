package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalConditionDevsBinding;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EUser;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LocalActionDevsActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLocalConditionDevsBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private final int PAGE_SIZE = 10;

    private int mGatewayPageNo = 1;
    private int mConditionPageNo = 1;

    private String mGatewayId;
    private MyHandler mHandler;
    private Typeface mIconfont;

    private UserCenter mUserCenter;
    private SceneManager mSceneManager;

    private List<EDevice.deviceEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> mAdapter;

    private List<EDevice.deviceEntry> mGatewayDevs = new ArrayList<>();
    private List<EDevice.deviceEntry> mConditionDevs = new ArrayList<>();

    public static void start(Context context, String gatewayId) {
        Intent intent = new Intent(context, LocalActionDevsActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalConditionDevsBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        initStatusBar();
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mAdapter = new BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder>(R.layout.item_dev, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, EDevice.deviceEntry item) {
                RelativeLayout rootLayout = holder.getView(R.id.root_layout);
                rootLayout.setBackground(null);
                TextView ogTV = holder.getView(R.id.og_iv);
                ogTV.setTypeface(mIconfont);

                holder.setText(R.id.dev_name_tv, item.nickName)
                        .setVisible(R.id.divider, mList.indexOf(item) != 0);
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(LocalActionDevsActivity.this).load(item.image).into(imageView);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EDevice.deviceEntry entry = mList.get(position);
                LocalActionIdentifierActivity.start(LocalActionDevsActivity.this, entry.nickName, entry.iotId, entry.deviceName, entry.productKey);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.devRv.setLayoutManager(layoutManager);
        mViewBinding.devRv.setAdapter(mAdapter);

        mViewBinding.devRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mList.clear();
                mGatewayPageNo = 1;
                new UserCenter(LocalActionDevsActivity.this).getGatewaySubdeviceList(mGatewayId, mGatewayPageNo, PAGE_SIZE,
                        mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mViewBinding.devRl.setEnableLoadMore(false);
    }

    private void initData() {
        mHandler = new MyHandler(this);
        mUserCenter = new UserCenter(this);
        mSceneManager = new SceneManager(this);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        new UserCenter(this).getGatewaySubdeviceList(mGatewayId, 1, PAGE_SIZE,
                mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<LocalActionDevsActivity> ref;

        public MyHandler(LocalActionDevsActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LocalActionDevsActivity activity = ref.get();
            if (activity != null) {
                if (msg.what == Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST) {
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    if (list != null && list.data != null) {
                        for (EUser.deviceEntry e : list.data) {
                            if (Constant.ACTION_DEVS_PK.contains(e.productKey)) {
                                EDevice.deviceEntry entry = new EDevice.deviceEntry();
                                entry.iotId = e.iotId;
                                entry.nickName = e.nickName;
                                entry.productKey = e.productKey;
                                entry.status = e.status;
                                entry.owned = DeviceBuffer.getDeviceOwned(e.iotId);
                                entry.image = e.image;
                                activity.mList.add(entry);
                            }
                        }

                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            activity.mUserCenter.getGatewaySubdeviceList(activity.mGatewayId, list.pageNo + 1, activity.PAGE_SIZE, activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                        } else {
                            // 数据获取完则加载显示
                            /*activity.mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), 1, activity.PAGE_SIZE,
                                    activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);*/

                            activity.mAdapter.notifyDataSetChanged();
                            if (activity.mList.size() > 0) {
                                activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                activity.mViewBinding.devRl.setVisibility(View.VISIBLE);
                            } else {
                                activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                activity.mViewBinding.devRl.setVisibility(View.GONE);
                            }
                            activity.mViewBinding.devRl.finishRefresh(true);
                            QMUITipDialogUtil.dismiss();
                        }
                    }
                } else if (msg.what == Constant.MSG_CALLBACK_QUERY_DEV_LIST_FOR_CA) {
                    // 可以作为条件的设备列表
                    JSONObject resultObj = JSON.parseObject((String) msg.obj);
                    ViseLog.d(new Gson().toJson(resultObj));
                    JSONArray datas = resultObj.getJSONArray("data");
                    for (int i = 0; i < datas.size(); i++) {
                        for (int j = 0; j < activity.mGatewayDevs.size(); j++) {
                            String iotId = datas.getJSONObject(i).getString("iotId");
                            if (activity.mGatewayDevs.get(j).iotId.equals(iotId)) {
                                activity.mList.add(activity.mGatewayDevs.get(j));
                            }
                        }
                    }
                    if (datas.size() >= resultObj.getInteger("pageSize")) {
                        // 数据没有获取完则获取下一页数据
                        activity.mSceneManager.queryDevListInHomeForCA(1, SystemParameter.getInstance().getHomeId(), resultObj.getInteger("pageNo") + 1, activity.PAGE_SIZE,
                                activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                    } else {
                        // 数据获取完则加载显示
                        activity.mAdapter.notifyDataSetChanged();
                        if (activity.mGatewayDevs.size() > 0) {
                            activity.mViewBinding.nodataView.setVisibility(View.GONE);
                            activity.mViewBinding.devRl.setVisibility(View.VISIBLE);
                        } else {
                            activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                            activity.mViewBinding.devRl.setVisibility(View.GONE);
                        }
                        activity.mViewBinding.devRl.finishRefresh(true);
                        QMUITipDialogUtil.dismiss();
                    }
                }
            }
        }
    }

    @Override
    protected void notifyResponseError(int type) {
        super.notifyResponseError(type);
        mViewBinding.devRl.finishRefresh(true);
        QMUITipDialogUtil.showFailDialog(this, R.string.pls_try_again_later);
    }

    @Override
    protected void notifyFailureOrError(int type) {
        super.notifyFailureOrError(type);
        mViewBinding.devRl.finishRefresh(true);
        QMUITipDialogUtil.showFailDialog(this, R.string.pls_try_again_later);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.select_action_dev);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        }
    }
}