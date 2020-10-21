package com.rexense.imoco.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshRoomDevice;
import com.rexense.imoco.event.RefreshRoomName;
import com.rexense.imoco.event.CEvent;
import com.rexense.imoco.event.EEvent;
import com.rexense.imoco.event.ShareDeviceSuccessEvent;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.model.EUser;
import com.rexense.imoco.presenter.ActivityRouter;
import com.rexense.imoco.presenter.AptDeviceGrid;
import com.rexense.imoco.presenter.AptDeviceList;
import com.rexense.imoco.presenter.AptRoomList;
import com.rexense.imoco.presenter.AptSceneGrid;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.service.DeleteKeyService;
import com.rexense.imoco.utility.Configure;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment1 extends BaseFragment {
    private ProgressDialog mProgressDialog = null;

    private TextView mLblSceneTitle;
    private SceneManager mSceneManager = null;
    private HorizontalScrollView mHscSceneList;
    private GridView mGrdScene;
    private TextView mLblHome, mLblHomeDescription, mLblDeviceDescription;
    private TextView mLblDevice, mLblDeviceDL, mLblRoom, mLblRoomDL, mLblShare, mLblShareDL;
    private RelativeLayout mRlDevice;
    private HomeSpaceManager mHomeSpaceManager = null;
    private UserCenter mUserCenter = null;
    private List<EScene.sceneListItemEntry> mSceneList = null;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private List<EDevice.deviceEntry> mShareDeviceList = null;
    private List<EHomeSpace.roomEntry> mRoomList = null;
    private ListView mListDevice, mListRoom, mListShare;
    private GridView mGridDevice;
    private View allDeviceView,shareDeviceView;
    private View allDeviceNoDataView,shareDeviceNoDataView;

    private AptDeviceList mAptShareDeviceList = null;
    private AptDeviceList mAptDeviceList = null;
    private AptDeviceGrid mAptDeviceGrid = null;
    private AptRoomList mAptRoomList = null;
    private int mGetPropertyIndex = 0;
    private String mCurrentGetPropertyIotId, mCurrentProductKey;
    private final int mScenePageSize = 50;
    private final int mDevicePageSize = 50;
    private final int mRoomPageSize = 20;
    private int mDeviceDisplayType = 1;
    private boolean mIsContinuouslyGetState = true;

    private ImageView imgAdd, imgGrid, imgList;

    @Override
    protected int setLayout() {
        // 注册事件总线
        EventBus.getDefault().register(this);
        return R.layout.fragment_index1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        mActivity = getActivity();

        View view = inflater.inflate(setLayout(), container, false);
        // 绑定ButterKnife
        mUnbinder = ButterKnife.bind(this, view);

        this.mSceneManager = new SceneManager(getActivity());
        this.mLblSceneTitle = (TextView) view.findViewById(R.id.mainLblSceneTitle);
        this.mLblSceneTitle.setVisibility(View.GONE);
        this.mHscSceneList = (HorizontalScrollView) view.findViewById(R.id.mainSclSceneList);
        this.mHscSceneList.setVisibility(View.GONE);
        this.mGrdScene = (GridView) view.findViewById(R.id.mainGrdScene);
        this.mLblHome = (TextView) view.findViewById(R.id.mainLblHome);
        this.mLblHomeDescription = (TextView) view.findViewById(R.id.mainLblHomeDescription);
        this.mLblDeviceDescription = (TextView) view.findViewById(R.id.mainLblDeviceDescription);
        this.mLblDevice = (TextView) view.findViewById(R.id.mainLblDevice);
        this.mLblDeviceDL = (TextView) view.findViewById(R.id.mainLblDeviceDL);
        this.mRlDevice = (RelativeLayout) view.findViewById(R.id.mainRlDevice);
        this.mLblRoom = (TextView) view.findViewById(R.id.mainLblRoom);
        this.mLblRoomDL = (TextView) view.findViewById(R.id.mainLblRoomDL);
        this.mLblShare = (TextView) view.findViewById(R.id.mainLblShare);
        this.mLblShareDL = (TextView) view.findViewById(R.id.mainLblShareDL);
        this.mListDevice = (ListView) view.findViewById(R.id.mainLstDevice);
        this.mGridDevice = (GridView) view.findViewById(R.id.mainGrdDevice);
        this.mListRoom = (ListView) view.findViewById(R.id.mainLstRoom);
        this.mListShare = (ListView) view.findViewById(R.id.mainLstShare);
        this.allDeviceView = view.findViewById(R.id.all_device_view);
        this.shareDeviceView = view.findViewById(R.id.share_device_view);
        this.allDeviceNoDataView = view.findViewById(R.id.alldevice_nodata_view);
        this.shareDeviceNoDataView = view.findViewById(R.id.sharedevice_nodata_view);

        imgGrid = (ImageView) view.findViewById(R.id.mainImgGrid);
        imgList = (ImageView) view.findViewById(R.id.mainImgList);
        imgAdd = (ImageView)view.findViewById(R.id.mainImgAdd);
        initView();
        if (LoginBusiness.isLogin()){
            // 登录后异步处理
            Message msg = new Message();
            msg.what = Constant.MSG_POSTLOGINPORCESS;
            mAPIDataHandler.sendMessage(msg);
            this.mProgressDialog = ProgressDialog.show(mActivity, getString(R.string.main_init_hint_title), getString(R.string.main_init_hint), true);
        }
        return view;
    }

    @Override
    protected void init() {
    }
    private void initView() {
        this.mHomeSpaceManager = new HomeSpaceManager(getActivity());
        this.mUserCenter = new UserCenter(getActivity());
        this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
        this.mShareDeviceList = new ArrayList<EDevice.deviceEntry>();
        this.mRoomList = new ArrayList<EHomeSpace.roomEntry>();
        this.mAptShareDeviceList = new AptDeviceList(getActivity());
        this.mAptDeviceList = new AptDeviceList(getActivity());
        this.mAptDeviceGrid = new AptDeviceGrid(getActivity());
        this.mAptRoomList = new AptRoomList(getActivity());

        // 获取家信息
        SystemParameter.getInstance().setHomeId(Configure.getItem(getActivity(), "homeId", ""));
        SystemParameter.getInstance().setHomeName(Configure.getItem(getActivity(), "homeName", ""));
        if (SystemParameter.getInstance().getHomeId() != null && SystemParameter.getInstance().getHomeId().length() > 0) {
            this.mLblHome.setText(SystemParameter.getInstance().getHomeName());
            Logger.d("The current home id is " + SystemParameter.getInstance().getHomeId());
        } else {
            Logger.d("The current home id is null");
        }

        // 添加设备处理
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginBusiness.isLogin()){
                    Dialog.confirmLogin(getActivity(), R.string.dialog_title, getString(R.string.dialog_unlogin), R.drawable.dialog_fail, R.string.dialog_ok, mAPIDataHandler);
                    return;
                }

                Intent intent = new Intent(getActivity(), ChoiceProductActivity.class);
                startActivity(intent);
            }
        });

        // 设备点击处理
        this.mLblDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLblDevice.setTextColor(getResources().getColor(R.color.topic_color1));
                mLblDeviceDL.setVisibility(View.VISIBLE);
                mLblRoom.setTextColor(getResources().getColor(R.color.normal_font_color));
                mLblRoomDL.setVisibility(View.INVISIBLE);
                mLblShare.setTextColor(getResources().getColor(R.color.normal_font_color));
                mLblShareDL.setVisibility(View.INVISIBLE);

                mRlDevice.setVisibility(View.VISIBLE);
                allDeviceView.setVisibility(View.VISIBLE);
                if (mDeviceDisplayType == 1) {
                    mGridDevice.setVisibility(View.VISIBLE);
                    mListDevice.setVisibility(View.GONE);
                } else {
                    mGridDevice.setVisibility(View.GONE);
                    mListDevice.setVisibility(View.VISIBLE);
                }
                mListRoom.setVisibility(View.GONE);
                shareDeviceView.setVisibility(View.GONE);
            }
        });

        // 房间点击处理
        this.mLblRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // 分享点击处理
        this.mLblShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // 设备网格显示处理
        imgGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridDevice.setVisibility(View.VISIBLE);
                mListDevice.setVisibility(View.GONE);
                mDeviceDisplayType = 1;
                imgGrid.setAlpha((float) 1.0);
                imgList.setAlpha((float) 0.4);
            }
        });

        // 设备列表显示处理
        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListDevice.setVisibility(View.VISIBLE);
                mGridDevice.setVisibility(View.GONE);
                mDeviceDisplayType = 2;
                imgGrid.setAlpha((float) 0.4);
                imgList.setAlpha((float) 1.0);
            }
        });
    }

    @Override
    protected void notifyFailureOrError(int type){
        super.notifyFailureOrError(type);
        if(this.mProgressDialog != null){
            this.mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        // 删除实时数据回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("MainStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainJoinCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainPropertyCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainEventCallback");
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        mUnbinder.unbind();
        super.onDestroyView();
    }

    // 主动获取设备属性
    private void getDeviceProperty(){
        if(this.mDeviceList == null || this.mDeviceList.size() == 0){
            return;
        }
        if(this.mGetPropertyIndex < 0 || this.mGetPropertyIndex >= this.mDeviceList.size()){
            return;
        }
        this.mCurrentGetPropertyIotId = this.mDeviceList.get(this.mGetPropertyIndex).iotId;
        this.mCurrentProductKey = this.mDeviceList.get(this.mGetPropertyIndex).productKey;
        new TSLHelper(getActivity()).getProperty(this.mCurrentGetPropertyIotId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }
    private void getDeviceProperty(String iotId){
        if(iotId == null || iotId.length() == 0 || this.mDeviceList == null || this.mDeviceList.size() == 0){
            return;
        }

        this.mIsContinuouslyGetState = false;
        int index = 0;
        boolean isHas = false;
        for(EDevice.deviceEntry entry : this.mDeviceList){
            if(entry.iotId.equalsIgnoreCase(iotId)){
                this.mGetPropertyIndex = index;
                isHas = true;
                break;
            }
            index++;
        }

        if(isHas == false || this.mGetPropertyIndex < 0 || this.mGetPropertyIndex >= this.mDeviceList.size()){
            return;
        }

        this.mCurrentGetPropertyIotId = this.mDeviceList.get(this.mGetPropertyIndex).iotId;
        this.mCurrentProductKey = this.mDeviceList.get(this.mGetPropertyIndex).productKey;
        new TSLHelper(getActivity()).getProperty(this.mCurrentGetPropertyIotId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    //刷新数据
    private void refreshData(){
        // 刷新设备数据
        if(SystemParameter.getInstance().getIsRefreshDeviceData()) {
            // 如果绑定或解绑定了网关则重新获取设备列表
            this.startGetDeviceList();
            SystemParameter.getInstance().setIsRefreshDeviceData(false);
        } else {
            if(this.mDeviceList != null && this.mDeviceList.size() > 0) {
                EDevice.deviceEntry bufferEntry, displayEntry;
                for(int i = this.mDeviceList.size() - 1; i >= 0; i--)
                {
                    displayEntry = this.mDeviceList.get(i);
                    bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                    if(bufferEntry != null) {
                        // 更新备注名称
                        displayEntry.nickName = bufferEntry.nickName;
                    } else {
                        // 删除不存在的数据
                        this.mDeviceList.remove(i);
                    }
                }
                this.mAptDeviceList.notifyDataSetChanged();
                this.mAptDeviceGrid.notifyDataSetChanged();
                allDeviceNoDataView.setVisibility(mDeviceList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            // 分享设备处理
            if(this.mShareDeviceList != null && this.mShareDeviceList.size() > 0) {
                EDevice.deviceEntry bufferEntry, displayEntry;
                for(int i = this.mShareDeviceList.size() - 1; i >= 0; i--)
                {
                    displayEntry = this.mShareDeviceList.get(i);
                    bufferEntry = DeviceBuffer.getDeviceInformation(displayEntry.iotId);
                    if(bufferEntry != null) {
                        // 更新备注名称
                        displayEntry.nickName = bufferEntry.nickName;
                    } else {
                        // 删除不存在的数据
                        this.mShareDeviceList.remove(i);
                    }
                }
                this.mAptShareDeviceList.notifyDataSetChanged();
                shareDeviceNoDataView.setVisibility(mShareDeviceList.isEmpty() ? View.VISIBLE: View.GONE);
            }
        }
    }

    // 设置场景水平列表
    private void setSceneList(List<EScene.sceneListItemEntry> list) {
        if (list == null || list.size() == 0) {
            this.mLblSceneTitle.setVisibility(View.GONE);
            this.mHscSceneList.setVisibility(View.GONE);
            return;
        }

        this.mHscSceneList.setVisibility(View.VISIBLE);
        int size = list.size();
        int length = 126;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        // 设置网格宽度(包括所有列宽与列之间的距离)
        int gridViewWidth = (int) (size * length * density + (size - 1) * 6 * density);
        // 设置列宽
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置GirdView布局参数(横向布局的关键)
        this.mGrdScene.setLayoutParams(params);
        // 设置列宽
        this.mGrdScene.setColumnWidth(itemWidth);
        // 设置列比例模式
        this.mGrdScene.setStretchMode(GridView.NO_STRETCH);
        // 设置列数量为列表集合数
        this.mGrdScene.setNumColumns(size);

        AptSceneGrid aptScene = new AptSceneGrid(getActivity());
        aptScene.setData(list);
        this.mGrdScene.setAdapter(aptScene);
        this.mGrdScene.setOnItemClickListener(this.sceneListOnItemClickListener);
    }

    // 设置实时数据处理
    private void setRealtimeDataProcess() {
        // 添加实时数据回调处理器
        RealtimeDataReceiver.addStatusCallbackHandler("MainStatusCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addJoinCallbackHandler("MainJoinCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addPropertyCallbackHandler("MainPropertyCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addEventCallbackHandler("MainEventCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addThingEventCallbackHandler("MainEventCallback", this.mRealtimeDataHandler);
    }

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                ActivityRouter.toDetail(getActivity(), mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                        mDeviceList.get(position).status, mDeviceList.get(position).nickName, mDeviceList.get(position).owned);
            }
        }
    };

    // 分享设备列表点击监听器
    private AdapterView.OnItemClickListener shareDeviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mShareDeviceList != null && position < mShareDeviceList.size()) {
                ActivityRouter.toDetail(getActivity(), mShareDeviceList.get(position).iotId, mShareDeviceList.get(position).productKey,
                        mShareDeviceList.get(position).status, mShareDeviceList.get(position).nickName, mShareDeviceList.get(position).owned);
            }
        }
    };

    // 房间列表点击监听器
    private AdapterView.OnItemClickListener roomListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mRoomList != null && position < mRoomList.size()) {
                EHomeSpace.roomEntry roomEntry = mRoomList.get(position);
                Intent intent = new Intent(mActivity,RoomDeviceActivity.class);
                intent.putExtra("roomId",roomEntry.roomId);
                intent.putExtra("roomName",roomEntry.name);
                startActivity(intent);
            }
        }
    };

    // 一键场景列表点击监听器
    private AdapterView.OnItemClickListener sceneListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mSceneList != null && mSceneList.size() > 0) {
                new SceneManager(getActivity()).executeScene(mSceneList.get(position).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                Toast.makeText(getActivity(), String.format(getString(R.string.main_scene_execute_hint), mSceneList.get(position).name), Toast.LENGTH_LONG).show();
            }
        }
    };

    // 开始获取房间列表
    private void startGetRoomList() {
        HomeSpaceManager.clearRoomBufferData();
        this.mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, this.mRoomPageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // 开始获取场景列表
    private void startGetSceneList() {
        if (this.mSceneList == null) {
            this.mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            this.mSceneList.clear();
        }
        this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, this.mScenePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // 开始获取设备列表
    private void startGetDeviceList() {
        // 初始化处理设备缓存器
        DeviceBuffer.initProcess();
        this.mAptDeviceList.clearData();
        // 获取家设备列表
        this.mHomeSpaceManager.getHomeDeviceList(SystemParameter.getInstance().getHomeId(), "", 1, this.mDevicePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
        try{
            Thread.sleep(200);
        }catch (Exception ex){}
        // 获取用户设备列表
        this.mUserCenter.getDeviceList(1, this.mDevicePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // 设备统计
    private void deviceCount() {
        int online = 0;
        int total = this.mDeviceList == null ? 0 : this.mDeviceList.size();
        if (total > 0) {
            for (EDevice.deviceEntry device : this.mDeviceList) {
                if (device.status == Constant.CONNECTION_STATUS_ONLINE) {
                    online++;
                }
            }
        }
        mLblDeviceDescription.setText(String.format(getString(R.string.main_device_description), total, online));
    }

    // 同步设备列表数据
    private void syncDeviceListData() {
        Map<String, EDevice.deviceEntry> all = DeviceBuffer.getAllDeviceInformation();
        this.mDeviceList.clear();
        this.mShareDeviceList.clear();
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
                    this.mDeviceList.add(deviceEntry);

                    // 分享设备处理
                    if (e.owned == 0){
                        EDevice.deviceEntry deviceEntryShare = new EDevice.deviceEntry();
                        deviceEntryShare.iotId = e.iotId;
                        deviceEntryShare.nickName = e.nickName;
                        deviceEntryShare.productKey = e.productKey;
                        deviceEntryShare.status = e.status;
                        deviceEntryShare.owned = e.owned;
                        deviceEntryShare.roomName = e.roomName;
                        this.mShareDeviceList.add(deviceEntryShare);
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
                    this.mDeviceList.add(deviceEntry);

                    // 分享设备处理
                    if (e.owned == 0){
                        EDevice.deviceEntry deviceEntryShare = new EDevice.deviceEntry();
                        deviceEntryShare.iotId = e.iotId;
                        deviceEntryShare.nickName = e.nickName;
                        deviceEntryShare.productKey = e.productKey;
                        deviceEntryShare.status = e.status;
                        deviceEntryShare.owned = e.owned;
                        deviceEntryShare.roomName = e.roomName;
                        this.mShareDeviceList.add(deviceEntryShare);
                    }
                }
            }
        }

        // 处理设备列表
        this.mAptDeviceList.setData(this.mDeviceList);
        this.mListDevice.setAdapter(this.mAptDeviceList);
        this.mListDevice.setOnItemClickListener(deviceListOnItemClickListener);

        // 处理设备网格
        this.mAptDeviceGrid.setData(this.mDeviceList);
        this.mGridDevice.setAdapter(this.mAptDeviceGrid);
        this.mGridDevice.setOnItemClickListener(deviceListOnItemClickListener);

        // 分享设备
        this.mAptShareDeviceList.setData(this.mShareDeviceList);
        this.mListShare.setAdapter(this.mAptShareDeviceList);
        this.mListShare.setOnItemClickListener(shareDeviceListOnItemClickListener);

        allDeviceNoDataView.setVisibility(mDeviceList.isEmpty()?View.VISIBLE:View.GONE);
        shareDeviceNoDataView.setVisibility(mShareDeviceList.isEmpty()?View.VISIBLE:View.GONE);
        this.deviceCount();
    }

    // 同步房间列表数据
    private void syncRoomListData() {
        Map<String, EHomeSpace.roomEntry> all = HomeSpaceManager.getRoomBufferData();

        if (all != null && all.size() > 0) {
            this.mRoomList.clear();
            for (EHomeSpace.roomEntry e : all.values()) {
                EHomeSpace.roomEntry roomEntry = new EHomeSpace.roomEntry();
                roomEntry.roomId = e.roomId;
                roomEntry.name = e.name;
                roomEntry.deviceCnt = e.deviceCnt;
                this.mRoomList.add(roomEntry);
            }

            // 处理列表点击事件
            this.mAptRoomList.setData(this.mRoomList);
            this.mListRoom.setAdapter(this.mAptRoomList);
            this.mListRoom.setOnItemClickListener(roomListOnItemClickListener);
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_POSTLOGINPORCESS:
                    // 初始化实时数据接收器
                    RealtimeDataReceiver.initProcess();
                    // 设置实时数据处理
                    setRealtimeDataProcess();
                    Intent intent = new Intent(mActivity, DeleteKeyService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mActivity.startForegroundService(intent);
                    } else {
                        mActivity.startService(intent);
                    }
                    // 获取家列表
                    mHomeSpaceManager.getHomeList(1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
                        if(mProgressDialog != null){
                            mProgressDialog.dismiss();
                        }
                    } else {
                        // 如果没有选择家或只有一个家则默认选择第一个
                        if (SystemParameter.getInstance().getHomeId() == null || SystemParameter.getInstance().getHomeId().length() == 0 || homeList.total == 1) {
                            SystemParameter.getInstance().setHomeId(homeList.data.get(0).homeId);
                            SystemParameter.getInstance().setHomeName(homeList.data.get(0).name);
                            // 作为配置保存
                            Configure.setItem(getActivity(), "homeId", SystemParameter.getInstance().getHomeId());
                            Configure.setItem(getActivity(), "homeName", SystemParameter.getInstance().getHomeName());
                            mLblHome.setText(SystemParameter.getInstance().getHomeName());
                        }
                        // 开始获取家房间列表
                        startGetRoomList();
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
                            mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), roomList.pageNo + 1, mRoomPageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
                            mSceneList.add(item);
                        }
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, mScenePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则设置场景列表数据
                            setSceneList(mSceneList);
                        }
                    }
                    // 数据获取完则开始获取设备列表数据
                    startGetDeviceList();
                    break;
                case Constant.MSG_CALLBACK_GETHOMEDEVICELIST:
                    // 处理获取家设备列表数据
                    EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList((String) msg.obj);
                    if (homeDeviceList != null && homeDeviceList.data != null) {
                        // 向缓存追加家列表数据
                        DeviceBuffer.addHomeDeviceList(homeDeviceList);
                        if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mHomeSpaceManager.getHomeDeviceList(SystemParameter.getInstance().getHomeId(), "", homeDeviceList.pageNo + 1, mDevicePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则同步刷新设备列表数据
                            syncDeviceListData();
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_GETUSERDEVICTLIST:
                    // 处理获取用户设备列表数据
                    EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList((String) msg.obj);
                    if (userBindDeviceList != null && userBindDeviceList.data != null) {
                        // 向缓存追加用户绑定设备列表数据
                        DeviceBuffer.addUserBindDeviceList(userBindDeviceList);
                        if (userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getDeviceList(userBindDeviceList.pageNo + 1, mDevicePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则同步刷新设备列表数据
                            syncDeviceListData();
                            // 开始主动获取设备属性
                            mGetPropertyIndex = 0;
                            mIsContinuouslyGetState = true;
                            getDeviceProperty();
                            if(mProgressDialog != null){
                                mProgressDialog.dismiss();
                            }
                        }
                    } else {
                        if(mProgressDialog != null){
                            mProgressDialog.dismiss();
                        }
                        // 开始主动获取设备属性
                        mGetPropertyIndex = 0;
                        mIsContinuouslyGetState = true;
                        getDeviceProperty();
                    }
                    break;
                case Constant.MSG_CALLBACK_GETTSLPROPERTY:
                    // 处理获取属性回调
                    ETSL.propertyEntry propertyEntry = new ETSL.propertyEntry();
                    JSONObject items = JSON.parseObject((String)msg.obj);
                    if(items != null) {
                        TSLHelper.parseProperty(mCurrentProductKey, items, propertyEntry);
                        propertyEntry.iotId = mCurrentGetPropertyIotId;
                        if (propertyEntry != null) {
                            for (String name : propertyEntry.properties.keySet()) {
                                if(propertyEntry.properties.containsKey(name) && propertyEntry.times.containsKey(name)){
                                    mAptDeviceGrid.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), propertyEntry.times.get(name));
                                    mAptDeviceList.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), propertyEntry.times.get(name));
                                }
                            }
                        }
                        // 继续获取
                        if(mIsContinuouslyGetState){
                            mGetPropertyIndex++;
                            getDeviceProperty();
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 实时数据处理器
    private Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNSTATUSNOTIFY:
                    // 处理连接状态通知
                    ERealtimeData.deviceConnectionStatusEntry entry = RealtimeDataParser.processConnectStatus((String) msg.obj);
                    if (entry != null && mAptDeviceList != null && mDeviceList != null && mDeviceList != null) {
                        for (int i = 0; i < mDeviceList.size(); i++) {
                            if (mDeviceList.get(i).iotId.equalsIgnoreCase(entry.iotId)) {
                                mDeviceList.get(i).status = entry.status;
                                // 如果变为在线则要重新获取状态
                                if(entry.status == Constant.CONNECTION_STATUS_ONLINE){
                                    getDeviceProperty(entry.iotId);
                                }
                                break;
                            }
                        }
                        // 刷新数据
                        mAptDeviceList.notifyDataSetChanged();
                        mAptDeviceGrid.notifyDataSetChanged();
                        deviceCount();
                    }
                    break;
                case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
                case Constant.MSG_CALLBACK_LNTHINGEVENTNOTIFY:
                    // 开始获取设备列表
                    startGetDeviceList();
                    break;
                case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                    // 处理属性通知
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                    if (propertyEntry != null) {
                        for (String name : propertyEntry.properties.keySet()) {
                            mAptDeviceGrid.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), Utility.getCurrentTimeStamp());
                            mAptDeviceList.updateStateData(propertyEntry.iotId, name, propertyEntry.properties.get(name), Utility.getCurrentTimeStamp());
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    // 处理事件通知
                    Logger.d("收到事件：" + (String) msg.obj);
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    // 订阅刷新数据事件
    @Subscribe
    public void onRefreshRoomData(EEvent eventEntry){
        // 处理刷新房间列表数据
        if(eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_ROOM_LIST_DATA)){
            this.syncRoomListData();
            return;
        }

        // 处理刷新设备列表房间数据
        if(eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_LIST_ROOM_DATA)){
            this.mAptDeviceGrid.updateRoomData(eventEntry.parameter);
            this.mAptDeviceList.updateRoomData(eventEntry.parameter);
            this.deviceCount();
            return;
        }

        // 处理刷新设备状态数据
        if(eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_STATE_DATA)){
            // 通过开始获取设备列表来触发获取状态
            this.startGetDeviceList();
            this.deviceCount();
            return;
        }

        // 处理刷新手动执行场景列表数据
        if(eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)){
            this.startGetSceneList();
        }

        // 处理刷新设备数量数据
        if(eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_DEVICE_NUMBER_DATA)){
            this.deviceCount();
        }
    }

    // 订阅共享设备成功事件
    @Subscribe
    public void shareDeviceSuccess(ShareDeviceSuccessEvent shareDeviceSuccessEvent){
        startGetDeviceList();
    }

    // 订阅刷新房间设备列表数据事件
    @Subscribe
    public void onRefreshRoomDevice(RefreshRoomDevice refreshRoomDevice){
        startGetRoomList();
    }

    // 订阅刷新房间房间名称事件
    @Subscribe
    public void onRefreshRoomName(RefreshRoomName refreshRoomName){
        startGetRoomList();
    }
}
