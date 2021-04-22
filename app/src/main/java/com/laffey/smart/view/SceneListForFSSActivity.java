package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.GridItemDecoration;
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
    @BindView(R.id.includeDetailRl)
    RelativeLayout mTopbar;
    @BindView(R.id.includeDetailImgBack)
    ImageView mTopbarBack;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTopbarTitle;
    @BindView(R.id.includeDetailImgMore)
    ImageView mTopbarMore;
    @BindView(R.id.scene_rv)
    RecyclerView mSceneRV;
    @BindView(R.id.scene_sfl)
    SmartRefreshLayout mSceneSFL;

    private List<SceneItem> mList = new ArrayList<>();
    private BaseQuickAdapter<SceneItem, BaseViewHolder> mSceneAdapter;

    private SceneManager mSceneManager;
    private String mCurrentKey;
    private MyHandler mMyHandler;

    private int mClickedPos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list_for_full_screen);

        ButterKnife.bind(this);
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
        mSceneRV.setLayoutManager(layoutManager);
        mSceneRV.addItemDecoration(new GridItemDecoration(2, QMUIDisplayHelper.dp2px(this, 10)));
        mSceneRV.setAdapter(mSceneAdapter);

        mSceneSFL.setEnableLoadMore(false);
        mSceneSFL.setOnRefreshListener(new OnRefreshListener() {
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
        ViseLog.d("绑定场景");
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
        mTopbarTitle.setText(R.string.rb_tab_two_desc);
        mTopbarBack.setOnClickListener(new View.OnClickListener() {
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

    private class MyHandler extends Handler {
        final WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null) return;
            if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET) {
                ViseLog.d("获取扩展信息：" + (String) msg.obj);
                JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);

                int pos = Integer.parseInt(mCurrentKey);
                if (jsonObject.getString("name") == null) {
                    mList.get(pos - 1).setSceneName(getString(R.string.no_bind_scene));
                } else {
                    mList.get(pos - 1).setSceneName(jsonObject.getString("name"));
                }
                mList.get(pos - 1).setSceneId(jsonObject.getString("msId"));
                mList.get(pos - 1).setAsId(jsonObject.getString("asId"));
                if (pos <= 11) {
                    mCurrentKey = String.valueOf(pos + 1);
                    mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
                } else {
                    mSceneSFL.finishRefresh(true);
                    QMUITipDialogUtil.dismiss();
                    mSceneAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == Constant.MSG_CALLBACK_EXECUTESCENE) {
                ViseLog.d((String) msg.obj);
                String sceneId = (String) msg.obj;
                String sceneName = null;
                for (int i = 0; i < mList.size(); i++) {
                    SceneItem item = mList.get(i);
                    if (sceneId.equals(item.getSceneId())) {
                        sceneName = item.getSceneName();
                        break;
                    }
                }
                ToastUtils.showLongToast(SceneListForFSSActivity.this, String.format(getString(R.string.main_scene_execute_hint_2),
                        sceneName));
            } else if (msg.what == Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET) {
                QMUITipDialogUtil.showLoadingDialg(SceneListForFSSActivity.this, R.string.is_loading);
                getScenes();
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
                        mSceneSFL.finishRefresh(true);
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

        mTopbar.setBackgroundColor(Color.WHITE);
        mTopbarMore.setVisibility(View.GONE);
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