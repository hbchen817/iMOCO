package com.rexense.imoco.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshMyinfo;
import com.rexense.imoco.event.ShareDeviceSuccessEvent;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SceneLogActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;

    private CommonAdapter adapter;
    private List<Visitable> models = new ArrayList<Visitable>();
    private LinearLayoutManager layoutManager;
    private SceneManager sceneManager;
    private int page = 1;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            page=1;
            getData();
        }
    };

    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            page++;
            getData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_log);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.fragment3_scene_log));

        sceneManager = new SceneManager(mActivity);
        adapter = new CommonAdapter(models, mActivity);
        layoutManager = new LinearLayoutManager(mActivity);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);

        getData();
    }

    private void getData(){
        sceneManager.getSceneLogList(page,mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETSCENELOG:
                    if (page==1){
                        models.clear();
                    }
                    models.addAll(CloudDataParser.processSceneLogList((String) msg.obj));
                    adapter.notifyDataSetChanged();
                    SrlUtils.finishRefresh(mSrlFragmentMe,true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe,true);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
