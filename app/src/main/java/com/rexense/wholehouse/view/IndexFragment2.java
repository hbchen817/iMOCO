package com.rexense.wholehouse.view;

import android.app.Activity;
import android.content.Context;
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

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CScene;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.demoTest.SceneCatalogIdCache;
import com.rexense.wholehouse.event.CEvent;
import com.rexense.wholehouse.event.EEvent;
import com.rexense.wholehouse.event.RefreshData;
import com.rexense.wholehouse.model.EScene;
import com.rexense.wholehouse.presenter.AptSceneList;
import com.rexense.wholehouse.presenter.AptSceneModel;
import com.rexense.wholehouse.presenter.CloudDataParser;
import com.rexense.wholehouse.presenter.ImageProvider;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.SystemParameter;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
    private List<EScene.sceneListItemEntry> mSceneList = new ArrayList<>();
    private AptSceneList mAptSceneList;
    private ListView mListSceneModel, mListMy;
    private final int mScenePageSize = 50;
    private String mSceneType;
    private SmartRefreshLayout mListMyRL;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

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
        //mActivity = getActivity();
        View view = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, view);

        this.mImgAdd = (ImageView) view.findViewById(R.id.sceneImgAdd);
        this.mLblScene = (TextView) view.findViewById(R.id.sceneLblScene);
        this.mLblSceneDL = (TextView) view.findViewById(R.id.sceneLblSceneDL);
        this.mLblMy = (TextView) view.findViewById(R.id.sceneLblMy);
        this.mLblMyDL = (TextView) view.findViewById(R.id.sceneLblMyDL);
        this.mListSceneModel = (ListView) view.findViewById(R.id.sceneLstSceneModel);
        this.mListMy = (ListView) view.findViewById(R.id.sceneLstMy);
        mListMyRL = (SmartRefreshLayout) view.findViewById(R.id.sceneLstMy_rl);
        mListMyRL.setEnableLoadMore(false);
        mListMyRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshData.refreshHomeSceneListData();

                startGetSceneList(CScene.TYPE_AUTOMATIC);
                SystemParameter.getInstance().setIsRefreshSceneListData(false);
            }
        });
        initView();
        // 开始获取场景列表
        this.startGetSceneList(CScene.TYPE_AUTOMATIC);

        return view;
    }

    @Override
    protected void dismissQMUIDialog() {
        super.dismissQMUIDialog();
        mListMyRL.finishRefresh(false);
    }

    @Override
    protected void init() {

    }

    private void initView() {
        this.mSceneManager = new SceneManager(mActivity);
        this.mModelList = this.mSceneManager.genSceneModelList();
        AptSceneModel aptSceneModel = new AptSceneModel(mActivity);
        aptSceneModel.setData(this.mModelList);
        this.mListSceneModel.setAdapter(aptSceneModel);
        this.mAptSceneList = new AptSceneList(mActivity, mSceneList, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler,
                new AptSceneList.AptSceneListCallback() {
                    @Override
                    public void onDelItem(String sceneId) {
                        if (mSceneList != null) {
                            for (int i = 0; i < mSceneList.size(); i++) {
                                EScene.sceneListItemEntry entry = mSceneList.get(i);
                                if (entry.id.equals(sceneId)) {
                                    mSceneList.remove(i);
                                    mAptSceneList.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                });

        mListMy.setAdapter(mAptSceneList);

        // 添加点击处理
        this.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SystemParameter.getInstance().setIsRefreshSceneListData(true);
                //PluginHelper.createScene(mActivity, CScene.TYPE_IFTTT, SystemParameter.getInstance().getHomeId());
                Intent intent = new Intent(mActivity, NewSceneActivity.class);
                startActivity(intent);
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

                Intent intent = new Intent(mActivity, SceneMaintainActivity.class);
                intent.putExtra("operateType", CScene.OPERATE_CREATE);
                intent.putExtra("sceneModelCode", mModelList.get(position).code);
                intent.putExtra("sceneModelName", getString(mModelList.get(position).name));
                intent.putExtra("sceneModelIcon", mModelList.get(position).icon);
                intent.putExtra("sceneNumber", mSceneList.size());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            switch (resultCode){
                case 100:{
                    // 删除场景
                    String sceneId = data.getStringExtra("scene_id");
                    if (mSceneList != null){
                        for (int i=0;i<mSceneList.size();i++){
                            EScene.sceneListItemEntry entry = mSceneList.get(i);
                            if (entry.id.equals(sceneId)) {
                                mSceneList.remove(i);
                                mAptSceneList.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    RefreshData.refreshHomeSceneListData();
                    break;
                }
                case 101:{
                    // 更新场景
                    String catalogId = data.getStringExtra("catalog_id");
                    String desc = data.getStringExtra("description");
                    boolean enable = data.getBooleanExtra("enable",true);
                    String id = data.getStringExtra("id");
                    String name = data.getStringExtra("name");
                    boolean valid = data.getBooleanExtra("valid",true);

                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                    entry.id = id;
                    entry.catalogId = catalogId;
                    entry.description = desc;
                    entry.enable = enable;
                    entry.name = name;
                    entry.valid = valid;

                    for (int i=0;i<mSceneList.size();i++){
                        if (entry.id.equals(mSceneList.get(i).id)){
                            mSceneList.set(i, entry);
                            break;
                        }
                    }
                    mAptSceneList.notifyDataSetChanged();
                    RefreshData.refreshHomeSceneListData();
                    break;
                }
            }
        }
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
                //PluginHelper.editScene(mActivity, CScene.TYPE_IFTTT, mSceneList.get(i).catalogId, SystemParameter.getInstance().getHomeId(), mSceneList.get(i).id);
                //SystemParameter.getInstance().setIsRefreshSceneListData(true);

                Intent intent = new Intent(mActivity, NewSceneActivity.class);
                intent.putExtra("scene_id", mSceneList.get(i).id);
                intent.putExtra("catalog_id", mSceneList.get(i).catalogId);
                startActivityForResult(intent,1000);
            } else {
                // 模板场景处理
                if (mSceneList.get(i).catalogId.equals(CScene.TYPE_MANUAL)) {
                    Intent intent = new Intent(mActivity, SceneMaintainActivity.class);
                    intent.putExtra("operateType", CScene.OPERATE_UPDATE);
                    intent.putExtra("sceneId", mSceneList.get(i).id);
                    intent.putExtra("name", mSceneList.get(i).name);
                    intent.putExtra("sceneModelCode", new SceneManager(mActivity).getSceneModelCode(mSceneList.get(i).description));
                    intent.putExtra("sceneModelIcon", ImageProvider.genSceneIcon(mActivity, mSceneList.get(i).description));
                    intent.putExtra("sceneNumber", mSceneList.size());
                    mActivity.startActivity(intent);
                } else {
                    //PluginHelper.editScene(mActivity, CScene.TYPE_IFTTT, CScene.TYPE_AUTOMATIC, SystemParameter.getInstance().getHomeId(), mSceneList.get(i).id);
                    //SystemParameter.getInstance().setIsRefreshSceneListData(true);
                    Intent intent = new Intent(mActivity, SceneMaintainActivity.class);
                    intent.putExtra("operateType", CScene.OPERATE_UPDATE);
                    intent.putExtra("sceneId", mSceneList.get(i).id);
                    intent.putExtra("name", mSceneList.get(i).name);
                    intent.putExtra("sceneModelCode", new SceneManager(mActivity).getSceneModelCode(mSceneList.get(i).description));
                    intent.putExtra("sceneModelIcon", ImageProvider.genSceneIcon(mActivity, mSceneList.get(i).description));
                    intent.putExtra("sceneNumber", mSceneList.size());
                    mActivity.startActivity(intent);
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
                            //ViseLog.d(new Gson().toJson(item));
                            if (!item.description.contains("mode == CA,")) {
                                SceneCatalogIdCache.getInstance().put(item.id, item.catalogId);
                                mSceneList.add(item);
                            }
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
                                mAptSceneList.notifyDataSetChanged();
                                //mListMy.setAdapter(mAptSceneList);
                                mListMy.setOnItemLongClickListener(sceneListOnItemLongClickListener);
                                mListMy.setOnItemClickListener(sceneListOnItemClickListener);
                            }
                            QMUITipDialogUtil.dismiss();
                        }
                    } else {
                        QMUITipDialogUtil.dismiss();
                    }
                    mListMyRL.finishRefresh(true);
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    // 处理删除列表数据
                    String sceneId = CloudDataParser.processDeleteSceneResult((String) msg.obj);
                    QMUITipDialogUtil.showSuccessDialog(mActivity, R.string.scene_delete_sucess);
                    if (sceneId != null && sceneId.length() > 0) {
                        mAptSceneList.deleteData(sceneId);
                        RefreshData.refreshHomeSceneListData();
                    }
                    //ToastUtils.showToastCentrally(mActivity, R.string.scene_delete_sucess);
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
            SystemParameter.getInstance().setIsRefreshSceneListData(false);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    QMUITipDialogUtil.showLoadingDialg(mActivity, getString(R.string.is_loading));
                }
            });
        }
    }
}