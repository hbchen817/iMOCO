package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.CEvent;
import com.rexense.imoco.event.EEvent;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.AptSceneList;
import com.rexense.imoco.presenter.AptSceneModel;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.PluginHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.utility.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment2 extends BaseFragment {
    private ImageView mImgAdd;
    private TextView mLblScene, mLblSceneDL, mLblMy, mLblMyDL;
    private SceneManager mSceneManager = null;
    private List<EScene.sceneModelEntry> mModelList = null;
    private List<EScene.sceneListItemEntry> mSceneList = null;
    private AptSceneList mAptSceneList;
    private ListView mListSceneModel, mListMy;
    private final int mScenePageSize = 50;
    private String mSceneType;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 注销刷新场景数据事件
        EventBus.getDefault().unregister(this);
        mUnbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            // 订阅刷新场景数据事件
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新场景列表数据
        if (SystemParameter.getInstance().getIsRefreshSceneListData()) {
            //this.startGetSceneList(CScene.TYPE_AUTOMATIC);
            SystemParameter.getInstance().setIsRefreshSceneListData(false);
            RefreshData.refreshSceneListData();
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_index2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        mActivity = getActivity();
        View view = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, view);

        this.mImgAdd = (ImageView) view.findViewById(R.id.sceneImgAdd);
        this.mLblScene = (TextView) view.findViewById(R.id.sceneLblScene);
        this.mLblSceneDL = (TextView) view.findViewById(R.id.sceneLblSceneDL);
        this.mLblMy = (TextView) view.findViewById(R.id.sceneLblMy);
        this.mLblMyDL = (TextView) view.findViewById(R.id.sceneLblMyDL);
        this.mListSceneModel = (ListView) view.findViewById(R.id.sceneLstSceneModel);
        this.mListMy = (ListView) view.findViewById(R.id.sceneLstMy);
        initView();
        // 开始获取场景列表
        this.startGetSceneList(CScene.TYPE_AUTOMATIC);

        return view;
    }

    @Override
    protected void init() {

    }

    private void initView() {
        this.mSceneManager = new SceneManager(getActivity());
        this.mModelList = this.mSceneManager.genSceneModelList();
        AptSceneModel aptSceneModel = new AptSceneModel(getActivity());
        aptSceneModel.setData(this.mModelList);
        this.mListSceneModel.setAdapter(aptSceneModel);
        this.mAptSceneList = new AptSceneList(getActivity(), this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);

        mListMy.setAdapter(mAptSceneList);

        // 添加点击处理
        this.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemParameter.getInstance().setIsRefreshSceneListData(true);
                PluginHelper.createScene(getActivity(), CScene.TYPE_IFTTT, SystemParameter.getInstance().getHomeId());
            }
        });

        // 推荐场景点击处理
        this.mLblScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLblScene.setTextColor(getResources().getColor(R.color.topic_color1));
                mLblSceneDL.setVisibility(View.VISIBLE);
                mLblMy.setTextColor(getResources().getColor(R.color.normal_font_color));
                mLblMyDL.setVisibility(View.INVISIBLE);

                mListSceneModel.setVisibility(View.VISIBLE);
                mListMy.setVisibility(View.GONE);
            }
        });

        // 我的场景点击处理
        this.mLblMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLblScene.setTextColor(getResources().getColor(R.color.normal_font_color));
                mLblSceneDL.setVisibility(View.INVISIBLE);
                mLblMy.setTextColor(getResources().getColor(R.color.topic_color1));
                mLblMyDL.setVisibility(View.VISIBLE);

                mListSceneModel.setVisibility(View.GONE);
                mListMy.setVisibility(View.VISIBLE);
            }
        });

        // 场景模板点击处理
        this.mListSceneModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mModelList.get(position).code == CScene.SMC_NONE) {
                    return;
                }

                Intent intent = new Intent(getActivity(), SceneMaintainActivity.class);
                intent.putExtra("operateType", CScene.OPERATE_CREATE);
                intent.putExtra("sceneModelCode", mModelList.get(position).code);
                intent.putExtra("sceneModelName", getString(mModelList.get(position).name));
                intent.putExtra("sceneModelIcon", mModelList.get(position).icon);
                intent.putExtra("sceneNumber", mSceneList == null ? 0 : mSceneList.size());
                startActivity(intent);
            }
        });
    }

    // 场景列表长按监听器
    private AdapterView.OnItemLongClickListener sceneListOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mAptSceneList.setDelete(position);
            return true;
        }
    };

    // 场景列表单按监听器
    private AdapterView.OnItemClickListener sceneListOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // 将删除隐藏掉
            mAptSceneList.hideDeleteButton();

            // 获取场景模板代码
            int sceneModelCode = new SceneManager(mActivity).getSceneModelCode(mSceneList.get(i).description);
            if (sceneModelCode < CScene.SMC_NIGHT_RISE_ON) {
                // 非模板场景处理
                LightSceneActivity.start(mActivity, mSceneList.get(i), "");
                SystemParameter.getInstance().setIsRefreshSceneListData(true);
            } else {
                // 模板场景处理
                if (mSceneList.get(i).catalogId.equals(CScene.TYPE_MANUAL)) {
                    Intent intent = new Intent(mActivity, SceneMaintainActivity.class);
                    intent.putExtra("operateType", CScene.OPERATE_UPDATE);
                    intent.putExtra("sceneId", mSceneList.get(i).id);
                    intent.putExtra("name", mSceneList.get(i).name);
                    intent.putExtra("sceneModelCode", new SceneManager(mActivity).getSceneModelCode(mSceneList.get(i).description));
                    intent.putExtra("sceneModelIcon", ImageProvider.genSceneIcon(mActivity, mSceneList.get(i).description));
                    intent.putExtra("sceneNumber", mSceneList == null ? 0 : mSceneList.size());
                    mActivity.startActivity(intent);
                } else {
                    PluginHelper.editScene(mActivity, CScene.TYPE_IFTTT, CScene.TYPE_AUTOMATIC, SystemParameter.getInstance().getHomeId(), mSceneList.get(i).id);
                    SystemParameter.getInstance().setIsRefreshSceneListData(true);
                }
            }
        }
    };

    // 开始获取场景列表
    private void startGetSceneList(String type) {
        this.mSceneType = type;
        if (this.mSceneList == null) {
            this.mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            if (this.mSceneType.equalsIgnoreCase(CScene.TYPE_AUTOMATIC)) {
                this.mSceneList.clear();
            }
        }
        this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), type, 1, this.mScenePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            mSceneList.add(item);
                        }
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, sceneList.pageNo + 1, mScenePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 如果自动场景获取结束则开始获取手动场景
                            if (mSceneType.equals(CScene.TYPE_AUTOMATIC)) {
                                startGetSceneList(CScene.TYPE_MANUAL);
                            }
                            if (mSceneType.equals(CScene.TYPE_MANUAL)) {
                                // 数据获取完则设置场景列表数据
                                mAptSceneList.setData(mSceneList);
                                //mListMy.setAdapter(mAptSceneList);
                                mListMy.setOnItemLongClickListener(sceneListOnItemLongClickListener);
                                mListMy.setOnItemClickListener(sceneListOnItemClickListener);
                            }
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    // 处理删除列表数据
                    String sceneId = CloudDataParser.processDeleteSceneResult((String) msg.obj);
                    if (sceneId != null && sceneId.length() > 0) {
                        mAptSceneList.deleteData(sceneId);
                        RefreshData.refreshSceneListData();
                    }
                    ToastUtils.showToastCentrally(getActivity(), R.string.scene_delete_sucess);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 订阅刷新场景列表数据事件
    @Subscribe
    public void onRefreshSceneListData(EEvent eventEntry) {
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            startGetSceneList(CScene.TYPE_AUTOMATIC);
        }
    }
}