package com.laffey.smart.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gary.hi.library.log.HiLog;
import com.google.gson.Gson;
import com.laffey.smart.BuildConfig;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshHistoryEvent;
import com.laffey.smart.event.RefreshRoomDevice;
import com.laffey.smart.event.RefreshRoomName;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.event.ShareDeviceSuccessEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.ERealtimeData;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.model.ItemSceneInGateway;
import com.laffey.smart.presenter.ActivityRouter;
import com.laffey.smart.presenter.AptDeviceGrid;
import com.laffey.smart.presenter.AptDeviceGridAdapter;
import com.laffey.smart.presenter.AptDeviceList;
import com.laffey.smart.presenter.AptDeviceListAdapter;
import com.laffey.smart.presenter.AptRoomList;
import com.laffey.smart.presenter.AptSceneGrid;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.DeviceManager;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.LockManager;
import com.laffey.smart.presenter.MocoApplication;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.Configure;
import com.laffey.smart.utility.Dialog;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.utility.Utility;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import rx.functions.Func1;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment1 extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.mainLblSceneTitle)
    protected TextView mLblSceneTitle;
    @BindView(R.id.mainSclSceneList)
    protected HorizontalScrollView mHscSceneList;
    @BindView(R.id.mainGrdScene)
    protected GridView mGrdScene;
    @BindView(R.id.mainLblHome)
    protected TextView mLblHome;
    @BindView(R.id.mainLblHomeDescription)
    protected TextView mLblHomeDescription;
    @BindView(R.id.mainLblDeviceDescription)
    protected TextView mLblDeviceDescription;
    @BindView(R.id.mainLblDevice)
    protected TextView mLblDevice;
    @BindView(R.id.mainLblDeviceDL)
    protected TextView mLblDeviceDL;
    @BindView(R.id.mainLblRoom)
    protected TextView mLblRoom;
    @BindView(R.id.mainLblRoomDL)
    protected TextView mLblRoomDL;
    @BindView(R.id.mainLblShare)
    protected TextView mLblShare;
    @BindView(R.id.mainLblShareDL)
    protected TextView mLblShareDL;
    @BindView(R.id.mainRlDevice)
    protected RelativeLayout mRlDevice;
    @BindView(R.id.mainLstDevice)
    protected RecyclerView mListDevice;
    @BindView(R.id.mainLstRoom)
    protected ListView mListRoom;
    @BindView(R.id.mainLstShare)
    protected ListView mListShare;
    @BindView(R.id.mainGrdDevice)
    protected RecyclerView mGridDevice;
    @BindView(R.id.all_device_view)
    protected View allDeviceView;
    @BindView(R.id.share_device_view)
    protected View shareDeviceView;
    // @BindView(R.id.alldevice_nodata_view)
    // protected View allDeviceNoDataView;
    @BindView(R.id.list_dev_nodata_view)
    protected LinearLayout mListDevNodataView;
    @BindView(R.id.grid_dev_nodata_view)
    protected LinearLayout mGridDevNodataView;
    @BindView(R.id.sharedevice_nodata_view)
    protected View shareDeviceNoDataView;
    @BindView(R.id.grid_rl)
    protected SmartRefreshLayout mGridRL;
    @BindView(R.id.list_rl)
    protected SmartRefreshLayout mListRL;
    @BindView(R.id.mainImgAdd)
    protected ImageView mImgAdd;
    @BindView(R.id.mainImgGrid)
    protected ImageView mImgGrid;
    @BindView(R.id.mainImgList)
    protected ImageView mImgList;

    private static final int SCENE_PAGE_SIZE = 50;
    private final int DEV_PAGE_SIZE = 50;
    private final int ROOM_PAGE_SIZE = 20;
    private static final int TAG_GET_EXTENDED_PRO = 10000;

    private HomeSpaceManager mHomeSpaceManager = null;
    private UserCenter mUserCenter = null;
    private List<EScene.sceneListItemEntry> mSceneList = new ArrayList<>();
    private List<EDevice.deviceEntry> mDeviceList = null;
    private List<EDevice.deviceEntry> mShareDeviceList = null;
    private List<EHomeSpace.roomEntry> mRoomList = null;

    private AptDeviceList mAptShareDeviceList = null;
    private AptDeviceList mAptDeviceList = null;
    private AptDeviceGrid mAptDeviceGrid = null;
    private AptDeviceGridAdapter mAptDeviceGridAdapter;
    private AptDeviceListAdapter mAptDeviceListAdapter;
    private AptRoomList mAptRoomList = null;
    private int mGetPropertyIndex = 0;
    private String mCurrentGetPropertyIotId, mCurrentProductKey;

    private ProgressDialog mProgressDialog = null;

    private int mDeviceDisplayType = 1;
    private boolean mIsContinuouslyGetState = true;

    private String mLockUserId;
    private int mLockType;
    private String mIotId;

    private GetSceneHandler mGetSceneHandler;
    private SceneManager mSceneManager = null;

    private boolean mRefreshExtendedBuffer = true;

    @Override
    protected int setLayout() {
        // 注册事件总线
        EventBus.getDefault().register(this);
        return R.layout.fragment_index1;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        View view = inflater.inflate(setLayout(), container, false);
        // 绑定ButterKnife
        mUnbinder = ButterKnife.bind(this, view);

        mSceneManager = new SceneManager(mActivity);
        mLblSceneTitle.setVisibility(View.GONE);
        mHscSceneList.setVisibility(View.GONE);
        mListRL.setEnableLoadMore(false);
        mListRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startGetSceneList2();
                // startGetDeviceList2();
            }
        });
        mGridRL.setEnableLoadMore(false);
        mGridRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startGetSceneList2();
                // startGetDeviceList2();
            }
        });
        initView();
        if (LoginBusiness.isLogin()) {
            // 登录后异步处理
            mProgressDialog = ProgressDialog.show(mActivity, getString(R.string.main_init_hint_title), getString(R.string.main_init_hint), true);
            HiLog.i("开始建立长连接 initProcess");
            // 初始化实时数据接收器
            RealtimeDataReceiver.initProcess();
            // 设置实时数据处理
            setRealtimeDataProcess();
            getHomeList(1);
        }
        return view;
    }

    // 家列表
    private void getHomeList(int pageNo) {
        HomeSpaceManager.getHomeList(mActivity, pageNo, 20, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取家列表数据
                EHomeSpace.homeListEntry homeList = CloudDataParser.processHomeList(result);
                if (homeList == null || homeList.total == 0 || homeList.data == null || homeList.data.size() == 0) {
                    // 如果没有创建家空间则自动创建我的家
                    createHome(getString(R.string.homespace_defaulthome));
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                } else {
                    // 如果没有选择家或只有一个家则默认选择第一个
                    if (SystemParameter.getInstance().getHomeId() == null || SystemParameter.getInstance().getHomeId().length() == 0 || homeList.total == 1) {
                        SystemParameter.getInstance().setHomeId(homeList.data.get(0).homeId);
                        SystemParameter.getInstance().setHomeName(homeList.data.get(0).name);
                        // 作为配置保存
                        Configure.setItem(mActivity, "homeId", SystemParameter.getInstance().getHomeId());
                        Configure.setItem(mActivity, "homeName", SystemParameter.getInstance().getHomeName());
                        mLblHome.setText(SystemParameter.getInstance().getHomeName());
                    }
                    // 开始获取家房间列表
                    startGetRoomList(1);
                }
            }
        });
    }

    // 创建家
    private void createHome(String name) {
        HomeSpaceManager.createHome(mActivity, name, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                // 处理创建家数据
                String homeId = CloudDataParser.processCreateHomeResult(result);
                if (homeId != null && homeId.length() > 0) {
                    SystemParameter.getInstance().setHomeId(homeId);
                    Logger.d("HomeId is " + SystemParameter.getInstance().getHomeId());
                }
            }
        });
    }

    @Override
    protected void init() {
    }

    private void initView() {
        mHomeSpaceManager = new HomeSpaceManager(mActivity);
        mUserCenter = new UserCenter(mActivity);
        mDeviceList = new ArrayList<EDevice.deviceEntry>();
        mShareDeviceList = new ArrayList<EDevice.deviceEntry>();
        mRoomList = new ArrayList<EHomeSpace.roomEntry>();
        mAptShareDeviceList = new AptDeviceList(mActivity);
        mAptDeviceList = new AptDeviceList(mActivity);
        mAptDeviceGrid = new AptDeviceGrid(mActivity);
        mAptRoomList = new AptRoomList(mActivity);

        mAptDeviceGridAdapter = new AptDeviceGridAdapter(mActivity, R.layout.grid_device, mDeviceList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        mGridDevice.setLayoutManager(gridLayoutManager);
        mGridDevice.setAdapter(mAptDeviceGridAdapter);
        mAptDeviceGridAdapter.setOnItemClickListener(mGridItemClickListener);

        mAptDeviceListAdapter = new AptDeviceListAdapter(mActivity, R.layout.list_device_2, mDeviceList);
        LinearLayoutManager listLayoutManager = new LinearLayoutManager(mActivity);
        listLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mListDevice.setLayoutManager(listLayoutManager);
        mListDevice.setAdapter(mAptDeviceListAdapter);
        mAptDeviceListAdapter.setOnItemClickListener(mGridItemClickListener);

        // 获取家信息
        SystemParameter.getInstance().setHomeId(Configure.getItem(mActivity, "homeId", ""));
        SystemParameter.getInstance().setHomeName(Configure.getItem(mActivity, "homeName", ""));
        if (SystemParameter.getInstance().getHomeId() != null && SystemParameter.getInstance().getHomeId().length() > 0) {
            mLblHome.setText(SystemParameter.getInstance().getHomeName());
            Logger.d("The current home id is " + SystemParameter.getInstance().getHomeId());
        } else {
            Logger.d("The current home id is null");
        }

        // 添加设备处理
        mImgAdd.setOnClickListener(this);

        // 设备点击处理
        mLblDevice.setOnClickListener(this);

        // 房间点击处理
        mLblRoom.setOnClickListener(this);

        // 分享点击处理
        mLblShare.setOnClickListener(this);

        // 设备网格显示处理
        mImgGrid.setOnClickListener(this);

        // 设备列表显示处理
        mImgList.setOnClickListener(this);
        mGetSceneHandler = new GetSceneHandler(mActivity.getMainLooper(), this);
    }

    @Override
    protected void notifyFailureOrError(int type) {
        super.notifyFailureOrError(type);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (type == 10360) {
            mSceneList.clear();
            setSceneList(mSceneList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RealtimeDataReceiver.addEventCallbackHandler("IndexfragmentLocalSceneCallback", mAPIDataHandler);
        refreshData();
    }

    @Override
    public void onDestroyView() {
        // 删除实时数据回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("MainStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainJoinCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainPropertyCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainEventCallback");
        RealtimeDataReceiver.deleteCallbackHandler("IndexfragmentLocalSceneCallback");
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private final List<ETSL.propertyEntry> mPropertyEntryList = new ArrayList<>();

    // 主动获取设备属性
    private void getDeviceProperty(int pos) {
        // ViseLog.d("主动获取设备属性 mDeviceList.size() = " + mDeviceList.size());
        if (mDeviceList == null || mDeviceList.size() == 0) {
            mGridRL.finishRefresh(true);
            mListRL.finishRefresh(true);
            return;
        }
        if (mGetPropertyIndex < 0 || mGetPropertyIndex >= mDeviceList.size()) {
            mGridRL.finishRefresh(true);
            mListRL.finishRefresh(true);
            return;
        }
        TSLHelper.getProperty(mActivity, mDeviceList.get(pos).iotId, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                if (pos == 0) {
                    mPropertyEntryList.clear();
                }
                // 处理获取属性回调
                ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                JSONObject items = JSON.parseObject(result);
                ViseLog.d("设备属性 = \n" + GsonUtil.toJson(items));
                if (items != null) {
                    TSLHelper.parseProperty(mDeviceList.get(pos).productKey, items, propertyEntry);
                    propertyEntry.iotId = mDeviceList.get(pos).iotId;
                    mPropertyEntryList.add(propertyEntry);
                    // 继续获取
                    if (pos < mDeviceList.size() - 1) {
                        getDeviceProperty(pos + 1);
                    } else {
                        // mAptDeviceGrid.updateStateData(mPropertyEntryList);
                        mAptDeviceGridAdapter.refreshDeviceProperty(mPropertyEntryList);
                        //mAptDeviceList.updateStateData(mPropertyEntryList);
                        mAptDeviceListAdapter.refreshDeviceProperty(mPropertyEntryList);
                        mGridRL.finishRefresh(true);
                        mListRL.finishRefresh(true);
                    }
                } else {
                    mGridRL.finishRefresh(true);
                    mListRL.finishRefresh(true);
                }
            }
        });
    }

    private void getDeviceProperty(String iotId) {
        if (iotId == null || iotId.length() == 0 || mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        mIsContinuouslyGetState = false;
        int index = 0;
        boolean isHas = false;
        for (EDevice.deviceEntry entry : mDeviceList) {
            if (entry.iotId.equalsIgnoreCase(iotId)) {
                mGetPropertyIndex = index;
                isHas = true;
                break;
            }
            index++;
        }

        if (!isHas || mGetPropertyIndex < 0 || mGetPropertyIndex >= mDeviceList.size()) {
            return;
        }

        mCurrentGetPropertyIotId = mDeviceList.get(mGetPropertyIndex).iotId;
        mCurrentProductKey = mDeviceList.get(mGetPropertyIndex).productKey;
        TSLHelper.getProperty(mActivity, mCurrentGetPropertyIotId, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取属性回调
                ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                JSONObject items = JSON.parseObject(result);
                // ViseLog.d("设备属性 = \n" + GsonUtil.toJson(items));
                if (items != null) {
                    TSLHelper.parseProperty(mCurrentProductKey, items, propertyEntry);
                    propertyEntry.iotId = mCurrentGetPropertyIotId;
                    for (String name : propertyEntry.properties.keySet()) {
                        if (propertyEntry.properties.containsKey(name) && propertyEntry.times.containsKey(name)) {
                            // mAptDeviceGrid.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), propertyEntry.times.get(name));
                            mAptDeviceGridAdapter.refreshDeviceProperty(propertyEntry.iotId, name,
                                    propertyEntry.properties.get(name), propertyEntry.times.get(name));
                            // mAptDeviceList.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), propertyEntry.times.get(name));
                            mAptDeviceListAdapter.refreshDeviceProperty(propertyEntry.iotId, name,
                                    propertyEntry.properties.get(name), propertyEntry.times.get(name));
                        }
                    }
                    mGridRL.finishRefresh(true);
                    mListRL.finishRefresh(true);
                } else {
                    mGridRL.finishRefresh(true);
                    mListRL.finishRefresh(true);
                }
            }
        });
    }

    //刷新数据
    private void refreshData() {
        // 刷新设备数据
        ViseLog.d("SystemParameter.getInstance().getIsRefreshDeviceData() = " + SystemParameter.getInstance().getIsRefreshDeviceData());
        if (SystemParameter.getInstance().getIsRefreshDeviceData()) {
            // 如果绑定或解绑定了网关则重新获取设备列表
            startGetDeviceList(1);
            // querySceneList(mActivity, "", "0");
            SystemParameter.getInstance().setIsRefreshDeviceData(false);
        } else {
            if (mDeviceList != null && mDeviceList.size() > 0) {
                EDevice.deviceEntry bufferEntry, displayEntry;
                for (int i = mDeviceList.size() - 1; i >= 0; i--) {
                    displayEntry = mDeviceList.get(i);
                    bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                    if (bufferEntry != null) {
                        // 更新备注名称
                        displayEntry.nickName = bufferEntry.nickName;
                    } else {
                        // 删除不存在的数据
                        mDeviceList.remove(i);
                    }
                }
                /*mAptDeviceList.notifyDataSetChanged();
                mAptDeviceGrid.notifyDataSetChanged();*/
                mAptDeviceListAdapter.notifyDataSetChanged();
                mAptDeviceGridAdapter.notifyDataSetChanged();
            }
            if (mDeviceList == null) {
                mListDevNodataView.setVisibility(View.VISIBLE);
                mGridDevNodataView.setVisibility(View.VISIBLE);
                mGridDevice.setVisibility(View.GONE);
                mListDevice.setVisibility(View.GONE);
            } else {
                if (mDeviceList.isEmpty()) {
                    mListDevNodataView.setVisibility(View.VISIBLE);
                    mGridDevNodataView.setVisibility(View.VISIBLE);
                    mGridDevice.setVisibility(View.GONE);
                    mListDevice.setVisibility(View.GONE);
                } else {
                    mListDevNodataView.setVisibility(View.GONE);
                    mGridDevNodataView.setVisibility(View.GONE);
                    mGridDevice.setVisibility(View.VISIBLE);
                    mListDevice.setVisibility(View.VISIBLE);
                }
            }

            // 分享设备处理
            if (mShareDeviceList != null && mShareDeviceList.size() > 0) {
                EDevice.deviceEntry bufferEntry, displayEntry;
                for (int i = mShareDeviceList.size() - 1; i >= 0; i--) {
                    displayEntry = mShareDeviceList.get(i);
                    bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                    if (bufferEntry != null) {
                        // 更新备注名称
                        displayEntry.nickName = bufferEntry.nickName;
                    } else {
                        // 删除不存在的数据
                        mShareDeviceList.remove(i);
                    }
                }
                mAptShareDeviceList.notifyDataSetChanged();
            }
            if (mShareDeviceList == null) {
                shareDeviceNoDataView.setVisibility(View.VISIBLE);
                mListShare.setVisibility(View.GONE);
            } else {
                if (mShareDeviceList.isEmpty()) {
                    shareDeviceNoDataView.setVisibility(View.VISIBLE);
                    mListShare.setVisibility(View.GONE);
                } else {
                    shareDeviceNoDataView.setVisibility(View.GONE);
                    mListShare.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // 设置场景水平列表
    private void setSceneList(List<EScene.sceneListItemEntry> list) {
        // ViseLog.d("设置场景水平列表 = " + GsonUtil.toJson(list));
        if (list == null || list.size() == 0) {
            mLblSceneTitle.setVisibility(View.GONE);
            mHscSceneList.setVisibility(View.GONE);
            return;
        }

        mHscSceneList.setVisibility(View.VISIBLE);
        int size = list.size();
        int length = 126;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        // 设置网格宽度(包括所有列宽与列之间的距离)
        float lengthDensity = length * density;
        if (SystemParameter.getInstance().getSceneItemWidth() != 0)
            lengthDensity = SystemParameter.getInstance().getSceneItemWidth();
        int gridViewWidth = (int) (size * lengthDensity + (size - 1) * 6 * density);
        // 设置列宽
        int itemWidth = (int) lengthDensity;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置GirdView布局参数(横向布局的关键)
        mGrdScene.setLayoutParams(params);
        // 设置列宽
        mGrdScene.setColumnWidth(itemWidth);
        // 设置列比例模式
        mGrdScene.setStretchMode(GridView.NO_STRETCH);
        // 设置列数量为列表集合数
        mGrdScene.setNumColumns(size);

        AptSceneGrid aptScene = new AptSceneGrid(mActivity);
        aptScene.setData(list);
        mGrdScene.setAdapter(aptScene);
        mGrdScene.setOnItemClickListener(sceneListOnItemClickListener);
    }

    // 设置实时数据处理
    private void setRealtimeDataProcess() {
        // 添加实时数据回调处理器
        RealtimeDataReceiver.addStatusCallbackHandler("MainStatusCallback", mRealtimeDataHandler);
        RealtimeDataReceiver.addJoinCallbackHandler("MainJoinCallback", mRealtimeDataHandler);
        RealtimeDataReceiver.addPropertyCallbackHandler("MainPropertyCallback", mRealtimeDataHandler);
        RealtimeDataReceiver.addEventCallbackHandler("MainEventCallback", mRealtimeDataHandler);
        RealtimeDataReceiver.addThingEventCallbackHandler("MainEventCallback", mRealtimeDataHandler);
        RealtimeDataReceiver.addEventCallbackHandler("IndexfragmentLocalSceneCallback", mAPIDataHandler);
    }

    // 设备列表点击监听器
    private final AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                if (mDeviceList.get(position) != null && mDeviceList.get(position).productKey != null) {
                    // ViseLog.d("mDeviceList.get(position) = " + GsonUtil.toJson(mDeviceList.get(position)));
                    ActivityRouter.toDetail(mActivity, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                            mDeviceList.get(position).status, mDeviceList.get(position).nickName, mDeviceList.get(position).owned);
                } else {
                    ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                }
            }
        }
    };

    private final OnItemClickListener mGridItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                if (mDeviceList.get(position) != null && mDeviceList.get(position).productKey != null) {
                    // ViseLog.d("mDeviceList.get(position) = " + GsonUtil.toJson(mDeviceList.get(position)));
                    ActivityRouter.toDetail(mActivity, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                            mDeviceList.get(position).status, mDeviceList.get(position).nickName, mDeviceList.get(position).owned);
                } else {
                    ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                }
            }
        }
    };

    // 分享设备列表点击监听器
    private final AdapterView.OnItemClickListener shareDeviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mShareDeviceList != null && position < mShareDeviceList.size()) {
                if (mShareDeviceList.get(position) != null && mShareDeviceList.get(position).productKey != null) {
                    ActivityRouter.toDetail(mActivity, mShareDeviceList.get(position).iotId, mShareDeviceList.get(position).productKey,
                            mShareDeviceList.get(position).status, mShareDeviceList.get(position).nickName, mShareDeviceList.get(position).owned);
                } else ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
            }
        }
    };

    // 房间列表点击监听器
    private final AdapterView.OnItemClickListener roomListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mRoomList != null && position < mRoomList.size()) {
                EHomeSpace.roomEntry roomEntry = mRoomList.get(position);
                Intent intent = new Intent(mActivity, RoomDeviceActivity.class);
                intent.putExtra("roomId", roomEntry.roomId);
                intent.putExtra("roomName", roomEntry.name);
                startActivity(intent);
            }
        }
    };

    // 一键场景列表点击监听器
    private final AdapterView.OnItemClickListener sceneListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSceneList != null && mSceneList.size() > 0) {
                if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                    EScene.sceneListItemEntry scene = mSceneList.get(position);
                    // ViseLog.d("一键场景列表点击监听器 = \n" + GsonUtil.toJson(scene));

                    if (!scene.valid) {
                        ToastUtils.showLongToast(mActivity, R.string.scene_is_invaild);
                        return;
                    }

                    String gatewayMac = scene.description;
                    EDevice.deviceEntry dev = DeviceBuffer.getDevByMac(gatewayMac);

                    if (dev != null) {
                        QMUITipDialogUtil.showLoadingDialg(mActivity, R.string.click_scene);
                        SceneManager.invokeLocalSceneService(mActivity, dev.iotId, scene.id, null);
                    } else {
                        ToastUtils.showLongToast(mActivity, R.string.pls_try_again_later);
                    }
                } else {
                    new SceneManager(mActivity).executeScene(mSceneList.get(position).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        }
    };

    // 开始获取房间列表
    private void startGetRoomList(int pageNo) {
        if (pageNo == 1)
            HomeSpaceManager.clearRoomBufferData();
        HomeSpaceManager.getHomeRoomList(mActivity, SystemParameter.getInstance().getHomeId(), pageNo, ROOM_PAGE_SIZE, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取家房间列表数据
                EHomeSpace.roomListEntry roomList = CloudDataParser.processHomeRoomList(result);
                if (roomList != null && roomList.data != null) {
                    // 向缓存追加数据
                    HomeSpaceManager.addRoomBufferData(roomList.data);
                    if (roomList.data.size() >= roomList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        startGetRoomList(roomList.pageNo + 1);
                    } else {
                        syncRoomListData();
                        // 开始获取场景列表数据
                        startGetSceneList();
                        mLblHomeDescription.setText(String.format(getString(R.string.main_home_description),
                                SystemParameter.getInstance().getHomeName(), HomeSpaceManager.getRoomBufferData().size()));
                    }
                } else {
                    // 开始获取场景列表数据
                    startGetSceneList();
                }
            }
        });
    }

    // 开始获取场景列表
    private void startGetSceneList() {
        if (mSceneList == null) {
            mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            mSceneList.clear();
        }
        if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
            querySceneList();
            // 数据获取完则开始获取设备列表数据
            // startGetDeviceList();
        } else
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, SCENE_PAGE_SIZE,
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // 开始获取场景列表
    private void startGetSceneList2() {
        if (mSceneList == null) {
            mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            mSceneList.clear();
        }
        if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
            querySceneList();
            // 数据获取完则开始获取设备列表数据
        } else
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, SCENE_PAGE_SIZE,
                    mCommitFailureHandler, mResponseErrorHandler, mGetSceneHandler);
    }

    // 查询本地场景列表
    private void querySceneList() {
        SceneManager.querySceneList(mActivity, "", "", new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                ViseLog.d("场景列表 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    mSceneList.clear();
                    DeviceBuffer.initSceneBuffer();
                    if (sceneList != null) {
                        for (int i = 0; i < sceneList.size(); i++) {
                            JSONObject sceneObj = sceneList.getJSONObject(i);
                            try {
                                ItemSceneInGateway scene = JSONObject.parseObject(sceneObj.toJSONString(), ItemSceneInGateway.class);

                                DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);

                                if ("1".equals(scene.getSceneDetail().getType())) {
                                    JSONObject appParams = scene.getAppParams();
                                    if (appParams != null) {
                                        String switchIotId = appParams.getString("switchIotId");
                                        if (switchIotId != null && switchIotId.length() > 0)
                                            continue;
                                    }

                                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                                    entry.id = scene.getSceneDetail().getSceneId();
                                    entry.name = scene.getSceneDetail().getName();
                                    entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                                    entry.description = scene.getGwMac();
                                    mSceneList.add(entry);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // 数据获取完则设置场景列表数据
                    setSceneList(mSceneList);
                    startGetDeviceList(1);
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(mActivity, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(mActivity, e.getMessage());
            }
        });
    }

    // 查询本地场景列表
    private void queryOnlySceneList() {
        SceneManager.querySceneList(mActivity, "", "", new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                ViseLog.d("场景列表 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    mSceneList.clear();
                    DeviceBuffer.initSceneBuffer();
                    if (sceneList != null) {
                        for (int i = 0; i < sceneList.size(); i++) {
                            JSONObject sceneObj = sceneList.getJSONObject(i);
                            try {
                                ItemSceneInGateway scene = JSONObject.parseObject(sceneObj.toJSONString(), ItemSceneInGateway.class);

                                DeviceBuffer.addScene(scene.getSceneDetail().getSceneId(), scene);

                                if ("1".equals(scene.getSceneDetail().getType())) {
                                    JSONObject appParams = scene.getAppParams();
                                    if (appParams != null) {
                                        String switchIotId = appParams.getString("switchIotId");
                                        if (switchIotId != null && switchIotId.length() > 0)
                                            continue;
                                    }

                                    EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
                                    entry.id = scene.getSceneDetail().getSceneId();
                                    entry.name = scene.getSceneDetail().getName();
                                    entry.valid = !"0".equals(scene.getSceneDetail().getEnable());
                                    entry.description = scene.getGwMac();
                                    mSceneList.add(entry);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // 数据获取完则设置场景列表数据
                    setSceneList(mSceneList);
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(mActivity, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(mActivity, e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mImgAdd.getId()) {
            // 添加设备
            if (!LoginBusiness.isLogin()) {
                Dialog.confirmLogin(mActivity, R.string.dialog_title, getString(R.string.dialog_unlogin),
                        R.drawable.dialog_fail, R.string.dialog_ok, mAPIDataHandler);
                return;
            }

            Intent intent = new Intent(mActivity, ChoiceProductActivity.class);
            startActivity(intent);
            /*for (int i=1;i<10;i++){
                try {
                    SceneManager.manageSceneService("WBLCC4jdP7zG6Xdfmuke000000", String.valueOf(i), 3,
                            mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        } else if (v.getId() == mLblDevice.getId()) {
            // “设备”选项卡
            mLblDevice.setTextColor(getResources().getColor(R.color.topic_color1));
            mLblDeviceDL.setVisibility(View.VISIBLE);
            mLblRoom.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblRoomDL.setVisibility(View.INVISIBLE);
            mLblShare.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblShareDL.setVisibility(View.INVISIBLE);

            mRlDevice.setVisibility(View.VISIBLE);
            allDeviceView.setVisibility(View.VISIBLE);
            if (mDeviceDisplayType == 1) {
                mGridRL.setVisibility(View.VISIBLE);
                mListRL.setVisibility(View.GONE);
            } else {
                mGridRL.setVisibility(View.GONE);
                mListRL.setVisibility(View.VISIBLE);
            }
            mListRoom.setVisibility(View.GONE);
            shareDeviceView.setVisibility(View.GONE);
        } else if (v.getId() == mLblRoom.getId()) {
            // “房间”选项卡
            mLblDevice.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblDeviceDL.setVisibility(View.INVISIBLE);
            mLblRoom.setTextColor(getResources().getColor(R.color.topic_color1));
            mLblRoomDL.setVisibility(View.VISIBLE);
            mLblShare.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblShareDL.setVisibility(View.INVISIBLE);

            mRlDevice.setVisibility(View.GONE);
            allDeviceView.setVisibility(View.GONE);
            mListRoom.setVisibility(View.VISIBLE);
            shareDeviceView.setVisibility(View.GONE);
        } else if (v.getId() == mLblShare.getId()) {
            // “分享”选项卡
            mLblDevice.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblDeviceDL.setVisibility(View.INVISIBLE);
            mLblRoom.setTextColor(getResources().getColor(R.color.normal_font_color));
            mLblRoomDL.setVisibility(View.INVISIBLE);
            mLblShare.setTextColor(getResources().getColor(R.color.topic_color1));
            mLblShareDL.setVisibility(View.VISIBLE);

            mRlDevice.setVisibility(View.GONE);
            allDeviceView.setVisibility(View.GONE);
            mListRoom.setVisibility(View.GONE);
            shareDeviceView.setVisibility(View.VISIBLE);
        } else if (v.getId() == mImgGrid.getId()) {
            // 设备网格显示
            mGridRL.setVisibility(View.VISIBLE);
            mListRL.setVisibility(View.GONE);
            mDeviceDisplayType = 1;
            mImgGrid.setAlpha((float) 1.0);
            mImgList.setAlpha((float) 0.4);
        } else if (v.getId() == mImgList.getId()) {
            // 设备列表显示
            mListRL.setVisibility(View.VISIBLE);
            mGridRL.setVisibility(View.GONE);
            mDeviceDisplayType = 2;
            mImgGrid.setAlpha((float) 0.4);
            mImgList.setAlpha((float) 1.0);
        }
    }

    private static class GetSceneHandler extends Handler {
        private final WeakReference<IndexFragment1> ref;

        public GetSceneHandler(Looper looper, IndexFragment1 fragment) {
            super(looper);
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            IndexFragment1 fragment = ref.get();
            if (fragment == null) return;
            if (msg.what == Constant.MSG_CALLBACK_QUERYSCENELIST) {
                // 处理获取场景列表数据
                //ViseLog.d("处理获取场景列表数据 = " + (String) msg.obj);
                EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                if (sceneList != null && sceneList.scenes != null) {
                    for (EScene.sceneListItemEntry item : sceneList.scenes) {
                        if (!item.description.contains("mode == CA,")) {
                            fragment.mSceneList.add(item);
                        }
                    }
                    if (sceneList.scenes.size() >= sceneList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        fragment.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1,
                                SCENE_PAGE_SIZE, fragment.mCommitFailureHandler, fragment.mResponseErrorHandler, fragment.mGetSceneHandler);
                    } else {
                        // 数据获取完则设置场景列表数据
                        fragment.setSceneList(fragment.mSceneList);
                    }
                }
            }
        }
    }

    // 开始获取设备列表
    private void startGetDeviceList(int pageNo) {
        // 获取家设备列表
        HomeSpaceManager.getHomeDeviceList(mActivity, SystemParameter.getInstance().getHomeId(), "", pageNo, DEV_PAGE_SIZE,
                Constant.MSG_CALLBACK_GETHOMEDEVICELIST, new APIChannel.Callback() {
                    @Override
                    public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                        commitFailure(mActivity, failEntry);
                        mGridRL.finishRefresh(false);
                        mListRL.finishRefresh(false);
                    }

                    @Override
                    public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                        responseError(mActivity, errorEntry);
                        mGridRL.finishRefresh(false);
                        mListRL.finishRefresh(false);
                    }

                    @Override
                    public void onProcessData(String result) {
                        // 处理获取家设备列表数据
                        ViseLog.d("------------\n" + GsonUtil.toJson(JSONObject.parseObject(result)));
                        EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList(result);
                        DeviceBuffer.initProcess();
                        if (homeDeviceList != null && homeDeviceList.data != null) {
                            // 向缓存追加家列表数据
                            DeviceBuffer.addHomeDeviceList(homeDeviceList);
                            if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                                // 数据没有获取完则获取下一页数据
                                startGetDeviceList(homeDeviceList.pageNo + 1);
                            } else {
                                // 数据获取完则同步刷新设备列表数据
                                getDeviceList(1);
                            }
                        }
                    }
                });
    }

    // 数据获取完则同步刷新设备列表数据
    private void getDeviceList(int pageNo) {
        UserCenter.getDeviceList(mActivity, pageNo, DEV_PAGE_SIZE, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(mActivity, failEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(mActivity, errorEntry);
                mGridRL.finishRefresh(false);
                mListRL.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                // 处理获取用户设备列表数据
                EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList(result);
                // ViseLog.d("设备列表 = \n" + GsonUtil.toJson(userBindDeviceList));
                if (userBindDeviceList != null && userBindDeviceList.data != null) {
                    // 向缓存追加用户绑定设备列表数据
                    DeviceBuffer.addUserBindDeviceList(userBindDeviceList);
                    if (userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        getDeviceList(userBindDeviceList.pageNo + 1);
                    } else {
                        // 数据获取完则同步刷新设备列表数据
                        syncDeviceListData();
                        // 开始主动获取设备属性
                        mGetPropertyIndex = 0;
                        mIsContinuouslyGetState = true;
                        getDeviceProperty(0);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                } else {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    // 开始主动获取设备属性
                    mGetPropertyIndex = 0;
                    mIsContinuouslyGetState = true;
                    getDeviceProperty(0);
                }
            }
        });
    }

    // 设备统计
    private void deviceCount() {
        int online = 0;
        int total = mDeviceList == null ? 0 : mDeviceList.size();
        if (total > 0) {
            for (EDevice.deviceEntry device : mDeviceList) {
                if (device.status == Constant.CONNECTION_STATUS_ONLINE) {
                    online++;
                }
            }
        }
        mLblDeviceDescription.setText(String.format(getString(R.string.main_device_description), total, online));
    }

    // 获取设备昵称
    private void getKeyName(int pos) {
        if (mRefreshExtendedBuffer) {
            if (Constant.KEY_NICK_NAME_PK.contains(mDeviceList.get(pos).productKey)) {
                SceneManager.getExtendedProperty(mActivity, mDeviceList.get(pos).iotId, Constant.TAG_DEV_KEY_NICKNAME, new APIChannel.Callback() {
                    @Override
                    public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                        commitFailure(mActivity, failEntry);
                        if (pos < mDeviceList.size() - 1) {
                            getKeyName(pos + 1);
                        } else {
                            mRefreshExtendedBuffer = false;
                            QMUITipDialogUtil.dismiss();
                        }
                    }

                    @Override
                    public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                        responseError(mActivity, errorEntry);
                        if (pos < mDeviceList.size() - 1) {
                            getKeyName(pos + 1);
                        } else {
                            mRefreshExtendedBuffer = false;
                            QMUITipDialogUtil.dismiss();
                        }
                    }

                    @Override
                    public void onProcessData(String result) {
                        // ViseLog.d("pos = " + pos + "\nname = " + mDeviceList.get(pos).nickName + "\n按键昵称 = \n" + GsonUtil.toJson(JSONObject.parseObject(result)));
                        JSONObject object = JSONObject.parseObject(result);
                        DeviceBuffer.addExtendedInfo(mDeviceList.get(pos).iotId, object);
                        if (pos < mDeviceList.size() - 1) {
                            getKeyName(pos + 1);
                        } else {
                            mRefreshExtendedBuffer = false;
                            QMUITipDialogUtil.dismiss();
                        }
                    }
                });
            } else {
                if (pos < mDeviceList.size() - 1) {
                    getKeyName(pos + 1);
                } else {
                    mRefreshExtendedBuffer = false;
                    QMUITipDialogUtil.dismiss();
                }
            }
        }
    }

    // 同步设备列表数据
    private void syncDeviceListData() {
        Map<String, EDevice.deviceEntry> all = DeviceBuffer.getAllDeviceInformation();
        // ViseLog.d(GsonUtil.toJson(all));
        int size = mDeviceList.size();
        mDeviceList.clear();
        mShareDeviceList.clear();
        /*if (mAptDeviceGrid != null) mAptDeviceGrid.notifyDataSetChanged();
        if (mAptDeviceList != null) mAptDeviceList.notifyDataSetChanged();*/
        if (mAptShareDeviceList != null) mAptShareDeviceList.notifyDataSetChanged();
        if (all != null && all.size() > 0) {
            // 网关排前面
            for (EDevice.deviceEntry e : all.values()) {
                if (e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    deviceEntry.owned = e.owned;
                    deviceEntry.roomName = e.roomName;
                    deviceEntry.image = e.image;
                    mDeviceList.add(deviceEntry);

                    // 分享设备处理
                    if (e.owned == 0) {
                        EDevice.deviceEntry deviceEntryShare = new EDevice.deviceEntry();
                        deviceEntryShare.iotId = e.iotId;
                        deviceEntryShare.nickName = e.nickName;
                        deviceEntryShare.productKey = e.productKey;
                        deviceEntryShare.status = e.status;
                        deviceEntryShare.owned = e.owned;
                        deviceEntryShare.roomName = e.roomName;
                        deviceEntryShare.image = e.image;
                        mShareDeviceList.add(deviceEntryShare);
                    }
                }
            }
            for (EDevice.deviceEntry e : all.values()) {
                if (!e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    deviceEntry.owned = e.owned;
                    deviceEntry.roomName = e.roomName;
                    deviceEntry.image = e.image;
                    mDeviceList.add(deviceEntry);

                    // 分享设备处理
                    if (e.owned == 0) {
                        EDevice.deviceEntry deviceEntryShare = new EDevice.deviceEntry();
                        deviceEntryShare.iotId = e.iotId;
                        deviceEntryShare.nickName = e.nickName;
                        deviceEntryShare.productKey = e.productKey;
                        deviceEntryShare.status = e.status;
                        deviceEntryShare.owned = e.owned;
                        deviceEntryShare.roomName = e.roomName;
                        deviceEntryShare.image = e.image;
                        mShareDeviceList.add(deviceEntryShare);
                    }
                }
            }
        }
        ViseLog.d("size = " + size + " , mDeviceList.size() = " + mDeviceList.size());
        if (size != mDeviceList.size()) {
            mAptDeviceGridAdapter.notifyDataSetChanged();
            mAptDeviceListAdapter.notifyDataSetChanged();
        }

        if (mDeviceList.size() > 0)
            queryMac();
        else
            refreshListView();
    }

    private void queryMac() {
        List<String> iotList = new ArrayList<>();
        for (EDevice.deviceEntry entry : mDeviceList) {
            iotList.add(entry.iotId);
        }
        DeviceManager.queryMacByIotId(mActivity, iotList, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                // ViseLog.d("iot - mac = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray iotIdAndMacList = response.getJSONArray("iotIdAndMacList");
                    for (int i = 0; i < iotIdAndMacList.size(); i++) {
                        JSONObject o = iotIdAndMacList.getJSONObject(i);
                        String iotId = o.getString("iotId");
                        String mac = o.getString("mac");
                        DeviceBuffer.updateDeviceMac(iotId, mac);
                    }
                    refreshListView();
                } else {
                    if (iotList.size() == 1) {
                        String iotId = iotList.get(0);
                        String pk = DeviceBuffer.getDeviceInformation(iotId).productKey;
                        if (CTSL.PK_GATEWAY_RG4100.equals(pk)) {
                            String msg = response.getString("message");
                            if ("mac not found!".equals(msg)) {
                                refreshListView();
                                return;
                            }
                        }
                    }
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(mActivity, response, Constant.QUERY_MAC_BY_IOTID);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(mActivity, e.getMessage() + ":\n" + Constant.QUERY_MAC_BY_IOTID);
            }
        });
    }

    private void refreshListView() {
        // 处理设备列表
        // mAptDeviceList.setData(mDeviceList);
        // mListDevice.setAdapter(mAptDeviceList);
        // mListDevice.setOnItemClickListener(deviceListOnItemClickListener);

        // 处理设备网格
        // mAptDeviceGrid.setData(mDeviceList);
        // mGridDevice.setAdapter(mAptDeviceGrid);
        // mGridDevice.setOnItemClickListener(deviceListOnItemClickListener);

        // 分享设备
        mAptShareDeviceList.setData(mShareDeviceList);
        mListShare.setAdapter(mAptShareDeviceList);
        mListShare.setOnItemClickListener(shareDeviceListOnItemClickListener);

        // allDeviceNoDataView.setVisibility(mDeviceList.isEmpty() ? View.VISIBLE : View.GONE);
        if (mDeviceList == null) {
            mListDevNodataView.setVisibility(View.VISIBLE);
            mGridDevNodataView.setVisibility(View.VISIBLE);
            mGridDevice.setVisibility(View.GONE);
            mListDevice.setVisibility(View.GONE);
        } else {
            if (mDeviceList.isEmpty()) {
                mListDevNodataView.setVisibility(View.VISIBLE);
                mGridDevNodataView.setVisibility(View.VISIBLE);
                mGridDevice.setVisibility(View.GONE);
                mListDevice.setVisibility(View.GONE);
            } else {
                mListDevNodataView.setVisibility(View.GONE);
                mGridDevNodataView.setVisibility(View.GONE);
                mGridDevice.setVisibility(View.VISIBLE);
                mListDevice.setVisibility(View.VISIBLE);
            }
        }

        if (mShareDeviceList == null) {
            shareDeviceNoDataView.setVisibility(View.VISIBLE);
            mListShare.setVisibility(View.GONE);
        } else {
            if (mShareDeviceList.isEmpty()) {
                shareDeviceNoDataView.setVisibility(View.VISIBLE);
                mListShare.setVisibility(View.GONE);
            } else {
                shareDeviceNoDataView.setVisibility(View.GONE);
                mListShare.setVisibility(View.VISIBLE);
            }
        }
        deviceCount();
        if (mDeviceList.size() > 0) {
            getKeyName(0);
        }
    }

    // 同步房间列表数据
    private void syncRoomListData() {
        Map<String, EHomeSpace.roomEntry> all = HomeSpaceManager.getRoomBufferData();

        if (all != null && all.size() > 0) {
            mRoomList.clear();
            for (EHomeSpace.roomEntry e : all.values()) {
                EHomeSpace.roomEntry roomEntry = new EHomeSpace.roomEntry();
                roomEntry.roomId = e.roomId;
                roomEntry.name = e.name;
                roomEntry.deviceCnt = e.deviceCnt;
                mRoomList.add(roomEntry);
            }

            // 处理列表点击事件
            mAptRoomList.setData(mRoomList);
            mListRoom.setAdapter(mAptRoomList);
            mListRoom.setOnItemClickListener(roomListOnItemClickListener);
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    // 处理触发手动场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("InvokeLocalSceneNotification".equals(identifier)) {
                        String status = value.getString("Status");
                        // status  0: 成功  1: 失败
                        QMUITipDialogUtil.dismiss();
                        if ("0".equals(status)) {
                            String name = DeviceBuffer.getScene(value.getString("SceneId")).getSceneDetail().getName();
                            if (name != null && name.length() > 0) {
                                String tip = String.format(getString(R.string.main_scene_execute_hint_2), name);
                                ToastUtils.showLongToast(mActivity, tip);
                            } else {
                                ToastUtils.showLongToast(mActivity, R.string.perform_scene);
                            }
                        } else {
                            ToastUtils.showLongToast(mActivity, R.string.scene_do_fail);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_CREATEHOME:
                    // 处理创建家数据
                    String homeId = CloudDataParser.processCreateHomeResult((String) msg.obj);
                    if (homeId != null && homeId.length() > 0) {
                        SystemParameter.getInstance().setHomeId(homeId);
                        Logger.d("HomeId is " + SystemParameter.getInstance().getHomeId());
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMELIST:
                    // 处理获取家列表数据
                    EHomeSpace.homeListEntry homeList = CloudDataParser.processHomeList((String) msg.obj);
                    if (homeList == null || homeList.total == 0 || homeList.data == null || homeList.data.size() == 0) {
                        // 如果没有创建家空间则自动创建我的家
                        mHomeSpaceManager.createHome(getString(R.string.homespace_defaulthome), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    } else {
                        // 如果没有选择家或只有一个家则默认选择第一个
                        if (SystemParameter.getInstance().getHomeId() == null || SystemParameter.getInstance().getHomeId().length() == 0 || homeList.total == 1) {
                            SystemParameter.getInstance().setHomeId(homeList.data.get(0).homeId);
                            SystemParameter.getInstance().setHomeName(homeList.data.get(0).name);
                            // 作为配置保存
                            Configure.setItem(mActivity, "homeId", SystemParameter.getInstance().getHomeId());
                            Configure.setItem(mActivity, "homeName", SystemParameter.getInstance().getHomeName());
                            mLblHome.setText(SystemParameter.getInstance().getHomeName());
                        }
                        // 开始获取家房间列表
                        startGetRoomList(1);
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表数据
                    EHomeSpace.roomListEntry roomList = CloudDataParser.processHomeRoomList((String) msg.obj);
                    if (roomList != null && roomList.data != null) {
                        // 向缓存追加数据
                        HomeSpaceManager.addRoomBufferData(roomList.data);
                        if (roomList.data.size() >= roomList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), roomList.pageNo + 1, ROOM_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            syncRoomListData();
                            // 开始获取场景列表数据
                            startGetSceneList();
                            mLblHomeDescription.setText(String.format(getString(R.string.main_home_description), SystemParameter.getInstance().getHomeName(), HomeSpaceManager.getRoomBufferData().size()));
                        }
                    } else {
                        // 开始获取场景列表数据
                        startGetSceneList();
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            if (!item.description.contains("mode == CA,")) {
                                mSceneList.add(item);
                            }
                        }
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, SCENE_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则设置场景列表数据
                            setSceneList(mSceneList);
                        }
                    }
                    // 数据获取完则开始获取设备列表数据
                    startGetDeviceList(1);
                    break;
                case Constant.MSG_CALLBACK_GETHOMEDEVICELIST:
                    // 处理获取家设备列表数据
                    // ViseLog.d("------------" + (String) msg.obj);
                    EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList((String) msg.obj);
                    DeviceBuffer.initProcess();
                    if (homeDeviceList != null && homeDeviceList.data != null) {
                        // 向缓存追加家列表数据
                        DeviceBuffer.addHomeDeviceList(homeDeviceList);
                        if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mHomeSpaceManager.getHomeDeviceList(SystemParameter.getInstance().getHomeId(), "", homeDeviceList.pageNo + 1, DEV_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则同步刷新设备列表数据
                            mUserCenter.getDeviceList(1, DEV_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_GETUSERDEVICTLIST:
                    // 处理获取用户设备列表数据
                    EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList((String) msg.obj);
                    // ViseLog.d("设备列表 = \n" + GsonUtil.toJson(userBindDeviceList));
                    if (userBindDeviceList != null && userBindDeviceList.data != null) {
                        // 向缓存追加用户绑定设备列表数据
                        DeviceBuffer.addUserBindDeviceList(userBindDeviceList);
                        if (userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getDeviceList(userBindDeviceList.pageNo + 1, DEV_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则同步刷新设备列表数据
                            syncDeviceListData();
                            // 开始主动获取设备属性
                            mGetPropertyIndex = 0;
                            mIsContinuouslyGetState = true;
                            getDeviceProperty(0);
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                        }
                    } else {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        // 开始主动获取设备属性
                        mGetPropertyIndex = 0;
                        mIsContinuouslyGetState = true;
                        getDeviceProperty(0);
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    for (int i = 0; i < mSceneList.size(); i++) {
                        EScene.sceneListItemEntry itemEntry = mSceneList.get(i);
                        if (itemEntry.id.equalsIgnoreCase(sceneId)) {
                            ToastUtils.showLongToastCentrally(mActivity, String.format(getString(R.string.main_scene_execute_hint_2), itemEntry.name));
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void dismissQMUIDialog() {
        super.dismissQMUIDialog();
        QMUITipDialogUtil.dismiss();
        mGridRL.finishRefresh(false);
        mListRL.finishRefresh(false);
    }

    // 实时数据处理器
    private final Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNSTATUSNOTIFY:
                    // 处理连接状态通知
                    ERealtimeData.deviceConnectionStatusEntry entry = RealtimeDataParser.processConnectStatus((String) msg.obj);
                    ViseLog.d("处理连接状态通知 = \n" + GsonUtil.toJson(entry));
                    if (entry != null && mAptDeviceList != null && mDeviceList != null) {
                        for (int i = 0; i < mDeviceList.size(); i++) {
                            if (mDeviceList.get(i).iotId.equalsIgnoreCase(entry.iotId)) {
                                mDeviceList.get(i).status = entry.status;
                                DeviceBuffer.updateDeviceStatus(entry.iotId, entry.status);
                                // 如果变为在线则要重新获取状态1
                                if (entry.status == Constant.CONNECTION_STATUS_ONLINE) {
                                    Log.i("lzm", "状态改变 在线获取属性 iotId = " + entry.iotId);
                                    //getDeviceProperty(entry.iotId);
                                    mAptDeviceGridAdapter.notifyDataSetChanged();
                                    mAptDeviceListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.i("lzm", "状态改变 离线 iotId = " + entry.iotId);
                                    mAptDeviceGridAdapter.notifyDataSetChanged();
                                    mAptDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        // 刷新数据
                        // mAptDeviceList.notifyDataSetChanged();
                        // mAptDeviceGrid.notifyDataSetChanged();
                        deviceCount();
                    }
                    break;
                // case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
                case Constant.MSG_CALLBACK_LNTHINGEVENTNOTIFY: {
                    // 开始获取设备列表
                    ViseLog.d("开始获取设备列表 = \n" + GsonUtil.toJson(msg.obj));
                    JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);
                    String identifier = jsonObject.getString("identifier");
                    if ("awss.BindNotify".equals(identifier)) {
                        JSONObject value = jsonObject.getJSONObject("value");
                        if (value != null) {
                            String operation = value.getString("operation");
                            String productKey = value.getString("productKey");
                            if ("Unbind".equalsIgnoreCase(operation)/* && CTSL.PK_GATEWAY_RG4100.equals(productKey)*/) {
                                startGetDeviceList(1);
                            }
                        }
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                    // 处理属性通知
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                    ViseLog.d("处理属性通知 = \n" + GsonUtil.toJson(propertyEntry));
                    if (propertyEntry != null) {
                        for (String name : propertyEntry.properties.keySet()) {
                            mAptDeviceGridAdapter.refreshDeviceProperty(propertyEntry.iotId, name, propertyEntry.properties.get(name), Utility.getCurrentTimeStamp());
                            mAptDeviceListAdapter.refreshDeviceProperty(propertyEntry.iotId, name, propertyEntry.properties.get(name), Utility.getCurrentTimeStamp());
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    // 处理事件通知
                    Logger.d("收到事件：" + (String) msg.obj);
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject params = jsonObject.getJSONObject("params");
                    if (params != null) {
                        JSONObject value = params.getJSONObject("value");
                        String identifier = params.getString("identifier");
                        switch (identifier) {
                            case "KeyDeletedNotification":
                                mLockUserId = value.getString("KeyID");
                                mLockType = value.getIntValue("LockType");
                                mIotId = value.getString("iotId");
                                LockManager.getUserByKey(mLockUserId, mLockType, mIotId, mCommitFailureHandler, mResponseErrorHandler, mRealtimeDataHandler);
                            case "HijackingAlarm":
                            case "TamperAlarm":
                            case "DoorUnlockedAlarm":
                            case "ArmDoorOpenAlarm":
                            case "LockedAlarm":
                            case "DoorOpenNotification":
                            case "KeyAddedNotification":
                            case "LowElectricityAlarm":
                            case "ReportReset":
                                EventBus.getDefault().post(new RefreshHistoryEvent());
                                break;
                            case "RemoteUnlockNotification":
                                EventBus.getDefault().post(new RefreshHistoryEvent());
                                ToastUtils.showToastCentrally(mActivity, "远程开门" + (value.getIntValue("Status") == 0 ? "成功" : "失败"));
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_KEY_USER_GET:
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject user = JSON.parseObject((String) msg.obj);
                        LockManager.userKeyUnbind(user.getString("userId"), mLockUserId, mLockType, mIotId,
                                mCommitFailureHandler, mResponseErrorHandler, mRealtimeDataHandler);
                    }
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    // 订阅刷新数据事件
    @Subscribe
    public void onRefreshRoomData(EEvent eventEntry) {
        // 刷新场景列表数据（首页）
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA_HOME_ONLY)) {
            queryOnlySceneList();
            return;
        }

        // 刷新设备按键昵称
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_KEY_NAME)) {
            mAptDeviceGridAdapter.notifyDataSetChanged();
            mAptDeviceListAdapter.notifyDataSetChanged();
            return;
        }

        // ViseLog.d("订阅刷新数据事件 eventEntry.name = " + eventEntry.name);
        // 处理刷新房间列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_ROOM_LIST_DATA)) {
            syncRoomListData();
            return;
        }

        // 处理刷新设备列表房间数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_LIST_ROOM_DATA)) {
            mAptDeviceGrid.updateRoomData(eventEntry.parameter);
            mAptDeviceList.updateRoomData(eventEntry.parameter);
            deviceCount();
            return;
        }

        // 处理刷新设备状态数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_STATE_DATA)) {
            // 通过开始获取设备列表来触发获取状态
            startGetDeviceList(1);
            deviceCount();
            return;
        }

        // 处理刷新设备状态数据_备份
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_BUFFER_DATA)) {
            // 通过开始获取设备列表来触发获取状态
            refreshData();
            return;
        }

        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            startGetSceneList();
        }

        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA_HOME)) {
            startGetSceneList2();
        }

        // 处理刷新设备数量数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_NUMBER_DATA)) {
            deviceCount();
        }
    }

    // 订阅共享设备成功事件
    @Subscribe
    public void shareDeviceSuccess(ShareDeviceSuccessEvent shareDeviceSuccessEvent) {
        startGetDeviceList(1);
    }

    // 订阅刷新房间设备列表数据事件
    @Subscribe
    public void onRefreshRoomDevice(RefreshRoomDevice refreshRoomDevice) {
        startGetRoomList(1);
    }

    // 订阅刷新房间房间名称事件
    @Subscribe
    public void onRefreshRoomName(RefreshRoomName refreshRoomName) {
        startGetRoomList(1);
    }

    private static class ExtendedHandler extends Handler {
        private final WeakReference<IndexFragment1> ref;
        private final String iotId;

        public ExtendedHandler(IndexFragment1 fragment, String iotId) {
            ref = new WeakReference<>(fragment);
            this.iotId = iotId;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            IndexFragment1 fragment = ref.get();
            if (fragment == null) return;
            if (msg.what == TAG_GET_EXTENDED_PRO) {
                QMUITipDialogUtil.dismiss();
                JSONObject object = JSONObject.parseObject((String) msg.obj);
                DeviceBuffer.addExtendedInfo(iotId, object);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        QMUITipDialogUtil.dismiss();
        RealtimeDataReceiver.deleteCallbackHandler("IndexfragmentLocalSceneCallback");
    }
}
