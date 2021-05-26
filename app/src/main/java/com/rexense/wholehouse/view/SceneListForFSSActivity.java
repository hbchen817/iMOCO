package com.rexense.wholehouse.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivitySceneListForFullScreenBinding;
import com.rexense.wholehouse.event.SceneBindEvent;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.widget.GridItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SceneListForFSSActivity extends DetailActivity {
    private ActivitySceneListForFullScreenBinding mViewBinding;

    private List<SceneItem> mList = new ArrayList<>();
    private BaseQuickAdapter<SceneItem, BaseViewHolder> mSceneAdapter;

    private SceneManager mSceneManager;
    private String mCurrentKey;
    private MyHandler mMyHandler;

    private int mClickedPos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneListForFullScreenBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mSceneManager = new SceneManager(this);

        initStatusBar();
        initView();

    }

    private void initView() {
        mSceneAdapter = new BaseQuickAdapter<SceneItem, BaseViewHolder>(R.layout.list_scene_for_full_screen, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, SceneItem sceneItem) {
                holder.setText(R.id.key_name_tv, sceneItem.getKeyName())
                        .setText(R.id.scene_name_tv, sceneItem.getSceneName());
            }
        };
        mSceneAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SceneItem item = mList.get(position);
                if (item.getSceneId() != null) {
                    mClickedPos = position;
                    mSceneManager.executeScene(item.getSceneId(), mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(SceneListForFSSActivity.this, mIOTId, String.valueOf(position + 1));
                }
            }
        });
        mSceneAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                SceneItem item = mList.get(position);
                if (item.getSceneId() != null) {
                    EditSceneBindActivity.start(SceneListForFSSActivity.this, item.getKeyName(), mIOTId, String.valueOf(position + 1), item.getSceneName());
                }
                return false;
            }
        });

        String[] keyNames = getResources().getStringArray(R.array.scene_item_names);
        for (int i = 0; i < keyNames.length; i++) {
            SceneItem item = new SceneItem(keyNames[i], getString(R.string.no_bind_scene));
            mList.add(item);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mViewBinding.sceneRv.setLayoutManager(layoutManager);
        mViewBinding.sceneRv.addItemDecoration(new GridItemDecoration(2, QMUIDisplayHelper.dp2px(this, 10)));
        mViewBinding.sceneRv.setAdapter(mSceneAdapter);

        mViewBinding.sceneSfl.setEnableLoadMore(false);
        mViewBinding.sceneSfl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getScenes();
            }
        });

        mMyHandler = new MyHandler(this);
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        getScenes();
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.includeToolbar.includeDetailLblTitle.setText(R.string.rb_tab_two_desc);
        mViewBinding.includeToolbar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getScenes() {
        mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_1;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    private static class MyHandler extends Handler {
        final WeakReference<SceneListForFSSActivity> mWeakReference;

        public MyHandler(SceneListForFSSActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SceneListForFSSActivity activity = mWeakReference.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                ViseLog.d("获取扩展信息：" + (String) msg.obj);
                JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);

                int pos = Integer.parseInt(activity.mCurrentKey);
                if (jsonObject.getString("name") == null) {
                    activity.mList.get(pos - 1).setSceneName(activity.getString(R.string.no_bind_scene));
                } else {
                    activity.mList.get(pos - 1).setSceneName(jsonObject.getString("name"));
                }
                activity.mList.get(pos - 1).setSceneId(jsonObject.getString("msId"));
                activity.mList.get(pos - 1).setAsId(jsonObject.getString("asId"));
                if (pos <= 11) {
                    activity.mCurrentKey = String.valueOf(pos + 1);
                    activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey, activity.mCommitFailureHandler,
                            activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                } else {
                    activity.mViewBinding.sceneSfl.finishRefresh(true);
                    QMUITipDialogUtil.dismiss();
                    activity.mSceneAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == Constant.MSG_CALLBACK_EXECUTESCENE) {
                ViseLog.d((String) msg.obj);
                String sceneId = (String) msg.obj;
                String sceneName = null;
                for (int i = 0; i < activity.mList.size(); i++) {
                    SceneItem item = activity.mList.get(i);
                    if (sceneId.equals(item.getSceneId())) {
                        sceneName = item.getSceneName();
                        break;
                    }
                }
                ToastUtils.showLongToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint_2),
                        sceneName));
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                QMUITipDialogUtil.showLoadingDialg(activity, R.string.is_loading);
                activity.getScenes();
            }
        }
    }

    // 响应错误处理器
    protected Handler mExtendedPropertyResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                ViseLog.d(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
                    logOut();
                    return false;
                } else if (responseErrorEntry.code == 6741) {
                    // 扩展信息不存在
                    int pos = Integer.parseInt(mCurrentKey);
                    if (pos <= 11) {
                        mCurrentKey = String.valueOf(pos + 1);
                        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        mViewBinding.sceneSfl.finishRefresh(true);
                        mSceneAdapter.notifyDataSetChanged();
                    }
                } else if (responseErrorEntry.code == 10360) {
                    // 场景不存在
                    ToastUtils.showShortToast(SceneListForFSSActivity.this, R.string.scene_does_not_exist);
                    String autoSceneId = mList.get(mClickedPos).getAsId();
                    mSceneManager.deleteScene(autoSceneId, null, null, null);
                    mSceneManager.setExtendedProperty(mIOTId, String.valueOf(mClickedPos + 1), "{}", null
                            , null, mMyHandler);
                }
            }
            return false;
        }
    });

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.includeDetailRl.setBackgroundColor(Color.WHITE);
        mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.GONE);
    }

    private class SceneItem {
        private String keyName;
        private String sceneName;
        private String sceneId;
        private String asId;

        public SceneItem(String keyName, String sceneName) {
            this.keyName = keyName;
            this.sceneName = sceneName;
        }

        public String getAsId() {
            return asId;
        }

        public void setAsId(String asId) {
            this.asId = asId;
        }

        public String getSceneId() {
            return sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }
    }
}