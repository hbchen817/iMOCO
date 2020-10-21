package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/19 10:12
 */

public class KeyManagerActivity extends AppCompatActivity {

    private static final String IOTID = "IOTID";

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private String mIotId;
    private MyHandler mHandler;
    private HashMap<String, String> mUserMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_manager);
        ButterKnife.bind(this);
        mIotId = getIntent().getStringExtra(IOTID);
        mHandler = new MyHandler(this);
        EventBus.getDefault().register(this);
        initView();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        UserCenter.queryVirtualUserListInDevice(mIotId, null, null, mHandler);
    }

    @Subscribe
    public void refresh(RefreshKeyListEvent event) {
        mList.clear();
        getData();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        mAdapter = new CommonAdapter(mList, this);
        recycleView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                EditKeyActivity.start(KeyManagerActivity.this, (ItemUserKey) mList.get(index), mIotId);
            }
        });
    }

    public static void start(Context context, String iotId) {
        Intent intent = new Intent(context, KeyManagerActivity.class);
        intent.putExtra(IOTID, iotId);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_toolbar_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
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
                        LockManager.queryKeyByUser(user.getString("userId"), null, null, this);
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

    public static class RefreshKeyListEvent {

    }
}