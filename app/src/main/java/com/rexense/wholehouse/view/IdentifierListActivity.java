package com.rexense.wholehouse.view;

import android.app.Activity;
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
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityIdentifierListBinding;
import com.rexense.wholehouse.demoTest.CaConditionEntry;
import com.rexense.wholehouse.demoTest.IdentifierItemForCA;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.utility.GsonUtil;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
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

public class IdentifierListActivity extends BaseActivity {
    private ActivityIdentifierListBinding mViewBinding;

    private String mNickName = "";
    private String mDevIot = "";
    private String mDevName = "";
    private String mProductKey = "";
    private String mVirtualEndPoint = "";

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
        mVirtualEndPoint = getIntent().getStringExtra("virtual_end_point");
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

                Intent intent = new Intent(IdentifierListActivity.this, EditPropertyValueActivity.class);
                startActivity(intent);
            }
        });
        mViewBinding.recyclerRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 0, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        mViewBinding.recyclerRl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mSceneManager.queryIdentifierListForCA(mDevIot, 0, mCommitFailureHandler, mResponseErrorHandler, mHandler);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.identifierRecycler.setLayoutManager(layoutManager);
        mViewBinding.identifierRecycler.setAdapter(mAdapter);

        if (CTSL.PK_AIRCOMDITION_CONVERTER.equals(mProductKey)) {
            mViewBinding.includeToolbar.tvToolbarTitle.setText(DeviceBuffer.getAirConditioner(mDevIot + "_" + mVirtualEndPoint).getNickname());
        } else {
            mViewBinding.includeToolbar.tvToolbarTitle.setText(mNickName);
        }

        mSceneManager = new SceneManager(this);
        mHandler = new CallbackHandler(this);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryIdentifierListForCA(mDevIot, 0, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private static class CallbackHandler extends Handler {
        private final WeakReference<IdentifierListActivity> weakRf;

        public CallbackHandler(IdentifierListActivity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            IdentifierListActivity activity = weakRf.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_IDENTIFIER_LIST) {
                activity.mList.clear();
                String result = (String) msg.obj;
                if (result.substring(0, 1).equals("[")) {
                    result = "{\"data\":" + result + "}";
                }
                ViseLog.d(GsonUtil.toJson(JSON.parseObject(result)));
                JSONObject o = JSON.parseObject(result);
                JSONArray a = o.getJSONArray("data");
                for (int i = 0; i < a.size(); i++) {
                    JSONObject o1 = a.getJSONObject(i);
                    IdentifierItemForCA item = new Gson().fromJson(o1.toJSONString(), IdentifierItemForCA.class);

                    if (item.getType() == 1) {
                        // 属性
                        CaConditionEntry.Property property = new CaConditionEntry.Property();
                        property.setProductKey(activity.mProductKey);
                        property.setDeviceName(activity.mDevName);
                        String identifier = o1.getString("identifier");
                        property.setPropertyName(identifier);

                        JSONObject object = DeviceBuffer.getExtendedInfo(activity.mDevIot);
                        if (object != null) {
                            String name = object.getString(identifier);
                            if (name != null) {
                                item.setName(name);
                            }
                        }

                        item.setObject(property);
                        if (CTSL.PK_AIRCOMDITION_CONVERTER.equals(activity.mProductKey)) {
                            if (identifier.contains("_")) {
                                String[] identifiers = identifier.split("_");
                                if (!identifiers[identifiers.length - 1].equals(activity.mVirtualEndPoint)) {
                                    item.setObject(null);
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    String name = item.getName();
                                    if (name.contains("_")) {
                                        String[] names = name.split("_");
                                        for (int j = 0; j < names.length - 1; j++) {
                                            sb.append(names[j]);
                                        }
                                    }
                                    if (sb.toString().length() > 0) {
                                        item.setName(sb.toString());
                                    }
                                }
                            }
                        }
                    } else if (item.getType() == 3) {
                        // 事件
                        CaConditionEntry.Event event = new CaConditionEntry.Event();
                        event.setProductKey(activity.mProductKey);
                        event.setDeviceName(activity.mDevName);
                        event.setEventCode(o1.getString("identifier"));
                        item.setObject(event);

                        if (Constant.KEY_NICK_NAME_PK.contains(activity.mProductKey)) {
                            item.setName(activity.getString(R.string.trigger_buttons_2));
                        }
                    }
                    item.setIotId(activity.mDevIot);
                    item.setNickName(activity.mNickName);
                    if (item.getObject() != null)
                        activity.mList.add(item);
                }
                ViseLog.d(GsonUtil.toJson(activity.mList));
                activity.mAdapter.notifyDataSetChanged();
                QMUITipDialogUtil.dismiss();
                activity.mViewBinding.recyclerRl.finishLoadMore(true);
                activity.mViewBinding.recyclerRl.finishRefresh(true);
                if (a == null || a.size() == 0) {
                    ToastUtils.showShortToast(activity, R.string.this_device_has_no_scene_condition);
                }
            }
        }
    }
}