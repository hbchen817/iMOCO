package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityKeyManagerBinding;
import com.rexense.imoco.event.RefreshKeyListEvent;
import com.rexense.imoco.model.ItemUserKey;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.LockManager;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gary
 * @time 2020/10/19 10:12
 */

public class KeyManagerActivity extends BaseActivity {

    private static final String IOTID = "IOTID";

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private String mIotId;
    private MyHandler mHandler;
    private HashMap<String, String> mUserMap = new HashMap<>();

    private ActivityKeyManagerBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityKeyManagerBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        EventBus.getDefault().register(this);

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.lock_key_manager);
        mIotId = getIntent().getStringExtra(IOTID);
        mHandler = new MyHandler(this);
        initView();
        getData();

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        UserCenter.queryVirtualUserListInAccount(1, 20, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    @Subscribe
    public void refresh(RefreshKeyListEvent event) {
        mList.clear();
        getData();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBinding.recycleView.setLayoutManager(layoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mViewBinding.recycleView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                EditKeyActivity.start(KeyManagerActivity.this, (ItemUserKey) mList.get(index), mIotId);
            }
        });
        mViewBinding.includeToolbar.tvToolbarLeft.setOnClickListener(this::onViewClicked);
    }

    public static void start(Context context, String iotId) {
        Intent intent = new Intent(context, KeyManagerActivity.class);
        intent.putExtra(IOTID, iotId);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_toolbar_left) {
            finish();
        }
    }

    private static class MyHandler extends Handler {
        final WeakReference<KeyManagerActivity> mWeakReference;

        public MyHandler(KeyManagerActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            KeyManagerActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERY_USER_IN_DEVICE:
                    JSONArray userArray = JSON.parseArray((String) msg.obj);
                    int size1 = userArray.size();
                    for (int i = 0; i < size1; i++) {
                        JSONObject user = userArray.getJSONObject(i);
                        JSONArray attrList = user.getJSONArray("attrList");
                        for (int j = 0; j < attrList.size(); j++) {
                            JSONObject attr = attrList.getJSONObject(i);
                            if (attr.getString("attrKey").equalsIgnoreCase("name")) {
                                activity.mUserMap.put(user.getString("userId"), attr.getString("attrValue"));
                                break;
                            }
                        }
                        LockManager.queryKeyByUser(user.getString("userId"), activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT:
                    JSONObject result = JSON.parseObject((String) msg.obj);
                    long total = result.getLongValue("total");
                    int pageNo = result.getIntValue("pageNo");
                    int pageSize = result.getIntValue("pageSize");
                    JSONArray users = result.getJSONArray("data");
                    int size = users.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject user = users.getJSONObject(i);
                        activity.mUserMap.put(user.getString("userId"), user.getJSONArray("attrList").getJSONObject(0).getString("attrValue"));
                        LockManager.queryKeyByUser(user.getString("userId"), activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                    }
                    if (pageSize * pageNo < total) {
                        pageNo++;
                        UserCenter.queryVirtualUserListInAccount(pageNo, pageSize, activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERY_KEY_BY_USER:
                    JSONArray keyArray = JSON.parseArray((String) msg.obj);
                    for (int i = 0; i < keyArray.size(); i++) {
                        JSONObject key = keyArray.getJSONObject(i);
                        ItemUserKey itemUserKey = new ItemUserKey();
                        itemUserKey.setHaveHeader(i == 0);
                        itemUserKey.setKeyNickName(key.getString("keyNickName"));
                        itemUserKey.setLockUserId(key.getString("lockUserId"));
                        itemUserKey.setLockUserType(key.getIntValue("lockUserType"));
                        itemUserKey.setLockUserPermType(key.getIntValue("lockUserPermType"));
                        itemUserKey.setUserId(key.getString("userId"));
                        itemUserKey.setUserName(activity.mUserMap.get(key.getString("userId")));
                        activity.mList.add(itemUserKey);
                    }
                    activity.mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
    }
}