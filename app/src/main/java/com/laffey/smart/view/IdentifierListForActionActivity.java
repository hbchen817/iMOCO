package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.laffey.smart.demoTest.IdentifierItemForCA;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.laffey.smart.databinding.ActivityIdentifierListBinding;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class IdentifierListForActionActivity extends BaseActivity {
    private ActivityIdentifierListBinding mViewBinding;

    private String mNickName = "";
    private String mDevIot = "";
    private String mDevName = "";
    private String mProductKey = "";

    private SceneManager mSceneManager;
    private CallbackHandler mHandler;

    private List<IdentifierItemForCA> mList;
    private BaseQuickAdapter<IdentifierItemForCA, BaseViewHolder> mAdapter;
    private Typeface mIconfont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityIdentifierListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
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
                TextView goIcon = holder.getView(R.id.go_iv);
                goIcon.setTypeface(mIconfont);

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
        mViewBinding.recyclerRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mViewBinding.recyclerRl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.identifierRecycler.setLayoutManager(layoutManager);
        mViewBinding.identifierRecycler.setAdapter(mAdapter);

        mViewBinding.includeToolbar.tvToolbarTitle.setText(mNickName);

        mSceneManager = new SceneManager(this);
        mHandler = new CallbackHandler(this);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryIdentifierListForCA(mDevIot, 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private static class CallbackHandler extends Handler {
        private WeakReference<IdentifierListForActionActivity> weakRf;

        public CallbackHandler(IdentifierListForActionActivity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            IdentifierListForActionActivity actionActivity = weakRf.get();
            if (actionActivity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_IDENTIFIER_LIST) {
                actionActivity.mList.clear();
                String result = (String) msg.obj;
                if (result.substring(0, 1).equals("[")) {
                    result = "{\"data\":" + result + "}";
                }
                JSONObject o = JSON.parseObject(result);
                JSONArray a = o.getJSONArray("data");
                for (int i = 0; i < a.size(); i++) {
                    JSONObject o1 = a.getJSONObject(i);
                    IdentifierItemForCA item = JSONObject.parseObject(o1.toJSONString(), IdentifierItemForCA.class);

                    if (item.getType() == 1) {
                        // 属性
                        ActionEntry.Property property = new ActionEntry.Property();
                        property.setIotId(actionActivity.mDevIot);
                        String identifier = o1.getString("identifier");
                        property.setPropertyName(identifier);

                        JSONObject object = DeviceBuffer.getExtendedInfo(actionActivity.mDevIot);
                        if (object != null) {
                            String name = object.getString(identifier);
                            if (name != null) {
                                item.setName(name);
                            }
                        }

                        item.setObject(property);
                    } else if (item.getType() == 2) {
                        // 服务
                        ActionEntry.InvokeService service = new ActionEntry.InvokeService();
                        service.setIotId(actionActivity.mDevIot);
                        service.setServiceName(o1.getString("identifier"));
                        item.setObject(service);
                    }
                    item.setIotId(actionActivity.mDevIot);
                    item.setNickName(actionActivity.mNickName);
                    actionActivity.mList.add(item);
                }
                actionActivity.mAdapter.notifyDataSetChanged();
                QMUITipDialogUtil.dismiss();
                actionActivity.mViewBinding.recyclerRl.finishLoadMore(true);
                actionActivity.mViewBinding.recyclerRl.finishRefresh(true);
                if (a.size() == 0) {
                    ToastUtils.showLongToast(actionActivity, R.string.this_device_has_no_scene_action);
                }
            }
        }
    }
}