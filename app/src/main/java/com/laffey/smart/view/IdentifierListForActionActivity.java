package com.laffey.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.demoTest.ActionEntry;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.demoTest.IdentifierItemForCA;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IdentifierListForActionActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.recycler_rl)
    SmartRefreshLayout mIdentifierRL;
    @BindView(R.id.identifier_recycler)
    RecyclerView mIdentifierRV;

    private String mNickName = "";
    private String mDevIot = "";
    private String mDevName = "";
    private String mProductKey = "";

    private SceneManager mSceneManager;
    private CallbackHandler mHandler;

    private List<IdentifierItemForCA> mList;
    private BaseQuickAdapter<IdentifierItemForCA, BaseViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifier_list);
        ButterKnife.bind(this);

        initStatusBar();
        initView();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void initView() {
        mNickName = getIntent().getStringExtra("nick_name");
        mDevIot = getIntent().getStringExtra("dev_iot");
        mDevName = getIntent().getStringExtra("dev_name");
        mProductKey = getIntent().getStringExtra("product_key");
        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<IdentifierItemForCA, BaseViewHolder>(R.layout.item_identifier, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, IdentifierItemForCA identifier) {
                int i = mList.indexOf(identifier);
                holder.setText(R.id.name_tv, identifier.getName())
                        .setVisible(R.id.divider, i != 0);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EventBus.getDefault().postSticky(mList.get(position));

                Intent intent = new Intent(IdentifierListForActionActivity.this, EditPropertyValueForActionActivity.class);
                startActivity(intent);
            }
        });
        mIdentifierRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mIdentifierRL.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mIdentifierRV.setLayoutManager(mLayoutManager);
        mIdentifierRV.setAdapter(mAdapter);

        mTitle.setText(mNickName);

        mSceneManager = new SceneManager(this);
        mHandler = new CallbackHandler(this);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private class CallbackHandler extends Handler {
        private WeakReference<Activity> weakRf;

        public CallbackHandler(Activity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (weakRf.get() == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_IDENTIFIER_LIST: {
                    mList.clear();
                    String result = (String) msg.obj;
                    if (result.substring(0, 1).equals("[")) {
                        result = "{\"data\":" + result + "}";
                    }
                    ViseLog.d(new Gson().toJson(JSON.parseObject(result)));
                    JSONObject o = JSON.parseObject(result);
                    JSONArray a = o.getJSONArray("data");
                    for (int i = 0; i < a.size(); i++) {
                        JSONObject o1 = a.getJSONObject(i);
                        IdentifierItemForCA item = new Gson().fromJson(o1.toJSONString(), IdentifierItemForCA.class);

                        if (item.getType() == 1) {
                            // 属性
                            ActionEntry.Property property = new ActionEntry.Property();
                            property.setIotId(mDevIot);
                            property.setPropertyName(o1.getString("identifier"));
                            item.setObject(property);
                        } else if (item.getType() == 2){
                            // 服务
                            ActionEntry.InvokeService service = new ActionEntry.InvokeService();
                            service.setIotId(mDevIot);
                            service.setServiceName(o1.getString("identifier"));
                            item.setObject(service);
                        }
                        item.setIotId(mDevIot);
                        item.setNickName(mNickName);
                        mList.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                    QMUITipDialogUtil.dismiss();
                    mIdentifierRL.finishLoadMore(true);
                    mIdentifierRL.finishRefresh(true);
                    if (a.size() == 0){
                        ToastUtils.showLongToast(IdentifierListForActionActivity.this, R.string.this_device_has_no_scene_action);
                    }
                    break;
                }
            }
        }
    }
}