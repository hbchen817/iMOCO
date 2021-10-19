package com.laffey.smart.view;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.demoTest.SceneCatalogIdCache;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EEventScene;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.AptSceneList;
import com.laffey.smart.presenter.AptSceneModel;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment2 extends BaseFragment {
    @BindView(R.id.sceneImgAdd)
    protected ImageView mImgAdd;
    @BindView(R.id.sceneLblScene)
    protected TextView mLblScene;
    @BindView(R.id.sceneLblSceneDL)
    protected TextView mLblSceneDL;
    @BindView(R.id.sceneLblMy)
    protected TextView mLblMy;
    @BindView(R.id.sceneLblMyDL)
    protected TextView mLblMyDL;
    @BindView(R.id.sceneLstSceneModel)
    protected ListView mListSceneModel;
    @BindView(R.id.sceneLstMy)
    protected ListView mListMy;
    @BindView(R.id.sceneLstMy_rl)
    protected SmartRefreshLayout mListMyRL;
    @BindView(R.id.scene_nodata_view)
    protected LinearLayout mSceneNodataView;

    private final int SCENE_PAGE_SIZE = 50;
    private final int REQUEST_CODE = 1000;

    private SceneManager mSceneManager = null;
    private List<EScene.sceneModelEntry> mModelList = null;
    private List<EScene.sceneListItemEntry> mSceneList = new ArrayList<>();
    private AptSceneList mAptSceneList;
    private String mSceneType;
    private String mLocalSceneType;

    private final List<ItemSceneInGateway> mItemSceneList = new ArrayList<>();

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
            // this.startGetSceneList(CScene.TYPE_AUTOMATIC);
            SystemParameter.getInstance().setIsRefreshSceneListData(false);
            RefreshData.refreshSceneListData();
        } else {
            if (mSceneList.size() == 0) {
                mSceneNodataView.setVisibility(View.VISIBLE);
                mListMy.setVisibility(View.GONE);
            } else {
                mSceneNodataView.setVisibility(View.GONE);
                mListMy.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_index2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        View view = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mListMyRL.setEnableLoadMore(false);
        mListMyRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                    mItemSceneList.clear();
                    mSceneList.clear();
                    mLocalSceneType = "0";
                    mSceneManager.querySceneList("chengxunfei", "", mLocalSceneType,
                            Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
                } else {
                    RefreshData.refreshHomeSceneListData();

                    startGetSceneList(CScene.TYPE_AUTOMATIC);
                    SystemParameter.getInstance().setIsRefreshSceneListData(false);
                }
            }
        });
        mListMy.setOnItemLongClickListener(sceneListOnItemLongClickListener);
        mListMy.setOnItemClickListener(sceneListOnItemClickListener);
        initView();
        // 开始获取场景列表
        if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
            mLocalSceneType = "0";
            mSceneManager.querySceneList("chengxunfei", "", "0", Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
        } else {
            startGetSceneList(CScene.TYPE_AUTOMATIC);
        }
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
        mSceneManager = new SceneManager(mActivity);
        mModelList = mSceneManager.genSceneModelList();
        AptSceneModel aptSceneModel = new AptSceneModel(mActivity);
        aptSceneModel.setData(mModelList);
        mListSceneModel.setAdapter(aptSceneModel);
        mAptSceneList = new AptSceneList(mActivity, mSceneList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler,
                new AptSceneList.AptSceneListCallback() {
                    @Override
                    public void onDelItem(String sceneId) {
                        if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                            QMUITipDialogUtil.showLoadingDialg(mActivity, R.string.is_loading);
                            String gatewayMac = "";
                            for (ItemSceneInGateway scene : mItemSceneList) {
                                if (sceneId.equals(scene.getSceneDetail().getSceneId())) {
                                    gatewayMac = scene.getGwMac();
                                    break;
                                }
                            }
                            deleteScene(gatewayMac, sceneId);
                        } else {
                            if (mSceneList != null) {
                                for (int i = 0; i < mSceneList.size(); i++) {
                                    EScene.sceneListItemEntry entry = mSceneList.get(i);
                                    if (entry.id.equals(sceneId)) {
                                        mSceneList.remove(i);
                                        mAptSceneList.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (mSceneList.size() == 0) {
                                    mSceneNodataView.setVisibility(View.VISIBLE);
                                    mListMy.setVisibility(View.GONE);
                                } else {
                                    mSceneNodataView.setVisibility(View.GONE);
                                    mListMy.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mSceneNodataView.setVisibility(View.VISIBLE);
                                mListMy.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        mListMy.setAdapter(mAptSceneList);

        // 添加点击处理
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SystemParameter.getInstance().setIsRefreshSceneListData(true);
                //PluginHelper.createScene(mActivity, CScene.TYPE_IFTTT, SystemParameter.getInstance().getHomeId());
                if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                    List<EDevice.deviceEntry> list = DeviceBuffer.getGatewayDevs();
                    if (list.size() == 0) {
                        ToastUtils.showLongToast(mActivity, R.string.add_gateway_dev_first);
                    } else {
                        if (list.size() == 1) {
                            LocalSceneListActivity.start(mActivity, list.get(0).iotId);
                        } else {
                            LocalGatewayListActivity.start(mActivity);
                        }
                    }
                } else {
                    Intent intent = new Intent(mActivity, NewSceneActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 推荐场景点击处理
        mLblScene.setOnClickListener(new View.OnClickListener() {
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
        mLblMy.setOnClickListener(new View.OnClickListener() {
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
        mListSceneModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mModelList.get(position).code == CScene.SMC_NONE) {
                    return;
                }

                if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                    Intent intent = new Intent(mActivity, SceneModelActivity.class);
                    intent.putExtra("operateType", CScene.OPERATE_CREATE);
                    intent.putExtra("sceneModelCode", mModelList.get(position).code);
                    intent.putExtra("sceneModelName", getString(mModelList.get(position).name));
                    intent.putExtra("sceneModelIcon", mModelList.get(position).icon);
                    intent.putExtra("sceneNumber", mSceneList.size());
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Intent intent = new Intent(mActivity, SceneMaintainActivity.class);
                    intent.putExtra("operateType", CScene.OPERATE_CREATE);
                    intent.putExtra("sceneModelCode", mModelList.get(position).code);
                    intent.putExtra("sceneModelName", getString(mModelList.get(position).name));
                    intent.putExtra("sceneModelIcon", mModelList.get(position).icon);
                    intent.putExtra("sceneNumber", mSceneList.size());
                    startActivity(intent);
                }
            }
        });
    }

    private void deleteScene(String gatewayMac, String sceneId) {
        RetrofitUtil.getInstance()
                .deleteScene("chengxunfei", gatewayMac, sceneId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        String msg = response.getString("message");
                        if (code == 200) {
                            boolean result = false;
                            try {
                                result = response.getBoolean("result");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (result) {
                                mItemSceneList.clear();
                                mSceneList.clear();
                                EDevice.deviceEntry dev = DeviceBuffer.getDevByMac(gatewayMac);
                                if (dev != null) {
                                    DeviceBuffer.removeScene(sceneId);
                                    mSceneManager.manageSceneService(dev.iotId, sceneId, 3,
                                            mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                }
                                RefreshData.refreshHomeSceneListData();
                            } else {
                                if (msg == null || msg.length() == 0) {
                                    ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                                } else
                                    ToastUtils.showLongToast(mActivity, msg);
                            }
                        } else {
                            if (msg == null || msg.length() == 0) {
                                ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                            } else
                                ToastUtils.showLongToast(mActivity, msg);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(mActivity, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case 100: {
                    // 删除场景
                    String sceneId = data.getStringExtra("scene_id");
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
                    RefreshData.refreshHomeSceneListData();
                    break;
                }
                case 101: {
                    // 更新场景
                    String catalogId = data.getStringExtra("catalog_id");
                    String desc = data.getStringExtra("description");
                    boolean enable = data.getBooleanExtra("enable", true);
                    String id = data.getStringExtra("id");
                    String name = data.getStringExtra("name");
                    boolean valid = data.getBooleanExtra("valid", true);

                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                    entry.id = id;
                    entry.catalogId = catalogId;
                    entry.description = desc;
                    entry.enable = enable;
                    entry.name = name;
                    entry.valid = valid;

                    for (int i = 0; i < mSceneList.size(); i++) {
                        if (entry.id.equals(mSceneList.get(i).id)) {
                            mSceneList.set(i, entry);
                            break;
                        }
                    }
                    mAptSceneList.notifyDataSetChanged();
                    RefreshData.refreshHomeSceneListData();
                    break;
                }
                case 10001: {
                    // 新增、编辑场景
                    mLblScene.setTextColor(getResources().getColor(R.color.normal_font_color));
                    mLblSceneDL.setVisibility(View.INVISIBLE);
                    mLblMy.setTextColor(getResources().getColor(R.color.topic_color1));
                    mLblMyDL.setVisibility(View.VISIBLE);

                    mListSceneModel.setVisibility(View.GONE);
                    mListMy.setVisibility(View.VISIBLE);
                    QMUITipDialogUtil.showSuccessDialog(mActivity, R.string.scenario_created_successfully);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RefreshData.refreshHomeSceneListData();
                        }
                    }, 1000);
                    break;
                }
            }
        }
    }

    // 场景列表长按监听器
    private final AdapterView.OnItemLongClickListener sceneListOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mAptSceneList.setDelete(position);
            return true;
        }
    };

    // 场景列表单按监听器
    private final AdapterView.OnItemClickListener sceneListOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // 将删除隐藏掉
            mAptSceneList.hideDeleteButton();

            if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID)) {
                ItemScene itemScene = mItemSceneList.get(i).getSceneDetail();
                EDevice.deviceEntry dev = DeviceBuffer.getDevByMac(mItemSceneList.get(i).getGwMac());
                // dev = DeviceBuffer.getDevByMac("LUXE_TEST");

                if (dev != null) {
                    String gatewayId = dev.iotId;

                    EEventScene scene = new EEventScene();
                    scene.setTarget("LocalSceneActivity");
                    scene.setGatewayId(gatewayId);
                    scene.setScene(mItemSceneList.get(i).getSceneDetail());
                    scene.setGatewayMac(mItemSceneList.get(i).getGwMac());
                    EventBus.getDefault().postSticky(scene);

                    Intent intent = new Intent(mActivity, LocalSceneActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    ToastUtils.showLongToast(mActivity, R.string.gateway_dev_does_not_exist);
                }
            } else {
                // 获取场景模板代码
                int sceneModelCode = new SceneManager(mActivity).getSceneModelCode(mSceneList.get(i).description);
                if (sceneModelCode < CScene.SMC_NIGHT_RISE_ON) {
                    // 非模板场景处理
                    //PluginHelper.editScene(mActivity, CScene.TYPE_IFTTT, mSceneList.get(i).catalogId, SystemParameter.getInstance().getHomeId(), mSceneList.get(i).id);
                    //SystemParameter.getInstance().setIsRefreshSceneListData(true);

                    Intent intent = new Intent(mActivity, NewSceneActivity.class);
                    intent.putExtra("scene_id", mSceneList.get(i).id);
                    intent.putExtra("catalog_id", mSceneList.get(i).catalogId);
                    startActivityForResult(intent, 1000);
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
        }
    };

    // 开始获取场景列表
    private void startGetSceneList(String type) {
        mSceneType = type;
        if (mSceneList == null) {
            mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            if (mSceneType.equalsIgnoreCase(CScene.TYPE_AUTOMATIC)) {
                mSceneList.clear();
            }
        }
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), type, 1, SCENE_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR: {
                    // 获取本地场景列表失败
                    Throwable e = (Throwable) msg.obj;
                    ToastUtils.showLongToast(mActivity, e.getMessage());
                    break;
                }
                case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                    // 获取本地场景列表
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    // ViseLog.d("场景列表 = " + GsonUtil.toJson(sceneList));
                    if (code == 0 || code == 200) {
                        if (sceneList != null) {
                            for (int i = 0; i < sceneList.size(); i++) {
                                JSONObject sceneObj = sceneList.getJSONObject(i);
                                ItemSceneInGateway scene = JSONObject.toJavaObject(sceneObj, ItemSceneInGateway.class);
                                DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);

                                JSONObject appParams = scene.getAppParams();
                                if (appParams != null) {
                                    String switchIotId = appParams.getString("switchIotId");
                                    if (switchIotId != null && switchIotId.length() > 0) continue;
                                }

                                mItemSceneList.add(scene);
                                EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                                entry.id = scene.getSceneDetail().getSceneId();
                                entry.name = scene.getSceneDetail().getName();
                                entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                                entry.description = scene.getSceneDetail().getMac();
                                entry.catalogId = scene.getSceneDetail().getType();
                                mSceneList.add(entry);
                            }
                        }
                        if ("0".equals(mLocalSceneType)) {
                            mLocalSceneType = "1";
                            mSceneManager.querySceneList("chengxunfei", "", mLocalSceneType,
                                    Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
                        } else {
                            QMUITipDialogUtil.dismiss();
                            mAptSceneList.setData(mSceneList);
                            mListMyRL.finishRefresh(true);
                            if (mSceneList.size() > 0) {
                                mListMyRL.setVisibility(View.VISIBLE);
                                mListMy.setVisibility(View.VISIBLE);
                                mSceneNodataView.setVisibility(View.GONE);
                            } else {
                                mListMyRL.setVisibility(View.GONE);
                                mListMy.setVisibility(View.GONE);
                                mSceneNodataView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        mListMyRL.finishRefresh(false);
                        QMUITipDialogUtil.dismiss();
                        if (message != null && message.length() > 0)
                            ToastUtils.showLongToast(mActivity, message);
                        else
                            ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                    }
                    break;
                }
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
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), mSceneType, sceneList.pageNo + 1, SCENE_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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

                    if (mSceneList.size() == 0) {
                        mSceneNodataView.setVisibility(View.VISIBLE);
                        mListMy.setVisibility(View.GONE);
                    } else {
                        mSceneNodataView.setVisibility(View.GONE);
                        mListMy.setVisibility(View.VISIBLE);
                    }
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
        } else if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA_HOME)) {
            // 刷新主界面场景列表
            QMUITipDialogUtil.showLoadingDialg(mActivity, R.string.is_loading);
            mItemSceneList.clear();
            mSceneList.clear();
            mLocalSceneType = "0";
            mSceneManager.querySceneList("chengxunfei", "", mLocalSceneType,
                    Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
        }
    }
}