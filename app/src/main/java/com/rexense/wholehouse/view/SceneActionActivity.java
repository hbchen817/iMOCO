package com.rexense.wholehouse.view;

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
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CScene;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivitySceneActionBinding;
import com.rexense.wholehouse.demoTest.ActionEntry;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.SystemParameter;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SceneActionActivity extends BaseActivity {
    private ActivitySceneActionBinding mViewBinding;

    private SceneManager mSceneManager;
    private CallbackHandler mHandler;

    private final int PAGE_SIZE = 20;

    private List<SceneActionItem> mList = new ArrayList<>();
    private BaseQuickAdapter<SceneActionItem, BaseViewHolder> mAdapter;

    private SceneActionItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneActionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        EventBus.getDefault().register(this);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, 1, PAGE_SIZE,
                mCommitFailureHandler, mResponseErrorHandler, mHandler);
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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.select_the_scene));
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem == null) {
                    mItem = new SceneActionItem();
                    mItem.setTrigger(new ActionEntry.Trigger());
                }
                SceneActionItem item = null;
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isChecked()) {
                        item = mList.get(i);
                        break;
                    }
                }
                if (item == null) {
                    ToastUtils.showLongToast(SceneActionActivity.this,
                            R.string.pls_select_the_scene_first);
                    return;
                }
                mItem.setId(item.getId());
                mItem.setName(item.getName());
                mItem.setCatalogId(item.getCatalogId());
                mItem.getTrigger().setSceneId(item.getId());

                EventBus.getDefault().unregister(SceneActionActivity.this);
                EventBus.getDefault().postSticky(mItem);

                Intent intent = new Intent(SceneActionActivity.this, NewSceneActivity.class);
                startActivity(intent);
            }
        });

        mHandler = new CallbackHandler(this);
        mSceneManager = new SceneManager(this);

        mAdapter = new BaseQuickAdapter<SceneActionItem, BaseViewHolder>(R.layout.item_scene_action, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, SceneActionItem item) {
                String mode = "0".equals(item.getCatalogId()) ?
                        getString(R.string.scenetype_manual) : getString(R.string.scenetype_automatic);
                holder.setText(R.id.title, item.getName())
                        .setText(R.id.detail, mode)
                        .setVisible(R.id.selector_iv, item.isChecked());
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setChecked(i == position);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.sceneRecycler.setLayoutManager(layoutManager);
        mViewBinding.sceneRecycler.setAdapter(mAdapter);
    }

    private String mSceneType = CScene.TYPE_AUTOMATIC;

    private static class CallbackHandler extends Handler {
        private final WeakReference<SceneActionActivity> weakRf;

        public CallbackHandler(SceneActionActivity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            SceneActionActivity activity = weakRf.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_QUERYSCENELIST) {
                JSONObject o = JSON.parseObject((String) msg.obj);
                int total = o.getInteger("total");
                int pageSize = o.getInteger("pageSize");
                int pageNo = o.getInteger("pageNo");
                JSONArray array = o.getJSONArray("scenes");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    if (!object.getString("description").contains("mode == CA,")) {
                        SceneActionItem item = new Gson().fromJson(array.get(i).toString(), SceneActionItem.class);
                        ActionEntry.Trigger trigger = new ActionEntry.Trigger();
                        trigger.setSceneId(item.getId());
                        item.setTrigger(trigger);
                        if (activity.mItem != null && activity.mItem.getId().equals(item.getId()))
                            item.setChecked(true);
                        activity.mList.add(item);
                    }
                }
                if (array.size() > 0 && CScene.TYPE_AUTOMATIC.equals(activity.mSceneType)) {
                    activity.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), activity.mSceneType, pageNo + 1, activity.PAGE_SIZE,
                            activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                } else if (array.size() == 0 && CScene.TYPE_AUTOMATIC.equals(activity.mSceneType)) {
                    activity.mSceneType = CScene.TYPE_MANUAL;
                    activity.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), activity.mSceneType, 1, activity.PAGE_SIZE,
                            activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                } else if (array.size() > 0 && CScene.TYPE_MANUAL.equals(activity.mSceneType)) {
                    activity.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), activity.mSceneType, pageNo + 1, activity.PAGE_SIZE,
                            activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                }
                activity.mAdapter.notifyDataSetChanged();
                QMUITipDialogUtil.dismiss();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(SceneActionItem item) {
        mItem = item;
    }

    public static class SceneActionItem {
        private String id;
        private String name;
        private String catalogId;// 0:手动 1:自动
        private ActionEntry.Trigger trigger;
        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public ActionEntry.Trigger getTrigger() {
            return trigger;
        }

        public void setTrigger(ActionEntry.Trigger trigger) {
            this.trigger = trigger;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(String catalogId) {
            this.catalogId = catalogId;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}