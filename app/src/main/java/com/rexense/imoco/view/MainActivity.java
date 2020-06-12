package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.ActivityRouter;
import com.rexense.imoco.presenter.AptDeviceGrid;
import com.rexense.imoco.presenter.AptDeviceList;
import com.rexense.imoco.presenter.AptRoomList;
import com.rexense.imoco.presenter.AptSceneGrid;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.AccountHelper;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.model.EUser;
import com.rexense.imoco.contract.IAccount;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Configure;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 主程序
 */
public class MainActivity extends BaseActivity {
    private TextView mLblSceneTitle;
    private SceneManager mSceneManager = null;
    private HorizontalScrollView mHscSceneList;
    private GridView mGrdScene;
    private TextView mLblHome, mLblHomeDescription, mLblDeviceDescription;
    private TextView mLblDevice, mLblRoom, mLblShare;
    private RelativeLayout mRlDevice;
    private HomeSpaceManager mHomeSpaceManager = null;
    private UserCenter mUserCenter = null;
    private List<EScene.sceneListItemEntry> mSceneList = null;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private List<EHomeSpace.roomEntry> mRoomList = null;
    private ListView mListDevice, mListRoom, mListShare;
    private GridView mGridDevice;
    private AptDeviceList mAptDeviceList = null;
    private AptDeviceGrid mAptDeviceGrid = null;
    private AptRoomList mAptRoomList = null;
    private final int mScenePageSize = 30;
    private final int mDevicePageSize = 50;
    private final int mRoomPageSize = 20;
    private int mDeviceDisplayType = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSceneManager = new SceneManager(this);
        this.mLblSceneTitle = (TextView)findViewById(R.id.mainLblSceneTitle);
        this.mLblSceneTitle.setVisibility(View.GONE);
        this.mHscSceneList = (HorizontalScrollView)findViewById(R.id.mainSclSceneList) ;
        this.mHscSceneList.setVisibility(View.GONE);
        this.mGrdScene = (GridView)findViewById(R.id.mainGrdScene);
        this.mLblHome = (TextView)findViewById(R.id.mainLblHome);
        this.mLblHomeDescription = (TextView)findViewById(R.id.mainLblHomeDescription);
        this.mLblDeviceDescription = (TextView)findViewById(R.id.mainLblDeviceDescription);
        this.mLblDevice = (TextView)findViewById(R.id.mainLblDevice);
        this.mRlDevice = (RelativeLayout)findViewById(R.id.mainRlDevice);
        this.mLblRoom = (TextView)findViewById(R.id.mainLblRoom);
        this.mLblShare = (TextView)findViewById(R.id.mainLblShare);
        this.mListDevice = (ListView)findViewById(R.id.mainLstDevice);
        this.mGridDevice = (GridView)findViewById(R.id.mainGrdDevice);
        this.mListRoom = (ListView)findViewById(R.id.mainLstRoom);
        this.mListShare = (ListView)findViewById(R.id.mainLstShare);
        this.mHomeSpaceManager = new HomeSpaceManager(this);
        this.mUserCenter = new UserCenter(this);
        this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
        this.mRoomList = new ArrayList<EHomeSpace.roomEntry>();
        this.mAptDeviceList = new AptDeviceList(this);
        this.mAptDeviceGrid = new AptDeviceGrid(this);
        this.mAptRoomList = new AptRoomList(this);

        // 获取家信息
        SystemParameter.getInstance().setHomeId(Configure.getItem(this,"homeId", ""));
        SystemParameter.getInstance().setHomeName(Configure.getItem(this,"homeName", ""));
        if(SystemParameter.getInstance().getHomeId() != null && SystemParameter.getInstance().getHomeId().length() > 0) {
            this.mLblHome.setText(SystemParameter.getInstance().getHomeName());
            Logger.d("The current home id is " + SystemParameter.getInstance().getHomeId());
        } else {
            Logger.d("The current home id is null");
        }

        // 添加设备处理
        ImageView imgAdd = (ImageView)findViewById(R.id.mainImgAdd);
        imgAdd.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!SystemParameter.getInstance().getIsLogin()) {
                    Dialog.confirmLogin(MainActivity.this, R.string.dialog_title, getString(R.string.dialog_unlogin), R.drawable.dialog_fail, R.string.dialog_ok, mAPIDataHandler);
                    return;
                }

                Intent intent = new Intent(MainActivity.this, ChoiceProductActivity.class);
                startActivity(intent);
            }
        });

        // 设备点击处理
        this.mLblDevice.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mLblDevice.setBackgroundResource(R.drawable.shape_frame_txt);
                mLblDevice.setTextColor(Color.parseColor("#FFFFFF"));
                mLblRoom.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblRoom.setTextColor(Color.parseColor("#464645"));
                mLblShare.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblShare.setTextColor(Color.parseColor("#464645"));
                mRlDevice.setVisibility(View.VISIBLE);
                if(mDeviceDisplayType == 1) {
                    mGridDevice.setVisibility(View.VISIBLE);
                    mListDevice.setVisibility(View.GONE);
                } else {
                    mGridDevice.setVisibility(View.GONE);
                    mListDevice.setVisibility(View.VISIBLE);
                }
                mListRoom.setVisibility(View.GONE);
                mListShare.setVisibility(View.GONE);
            }
        });

        // 房间点击处理
        this.mLblRoom.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mLblDevice.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblDevice.setTextColor(Color.parseColor("#464645"));
                mLblRoom.setBackgroundResource(R.drawable.shape_frame_txt);
                mLblRoom.setTextColor(Color.parseColor("#FFFFFF"));
                mLblShare.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblShare.setTextColor(Color.parseColor("#464645"));
                mRlDevice.setVisibility(View.GONE);
                mGridDevice.setVisibility(View.GONE);
                mListDevice.setVisibility(View.GONE);
                mListRoom.setVisibility(View.VISIBLE);
                mListShare.setVisibility(View.GONE);
            }
        });

        // 分享点击处理
        this.mLblShare.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mLblDevice.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblDevice.setTextColor(Color.parseColor("#464645"));
                mLblRoom.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblRoom.setTextColor(Color.parseColor("#464645"));
                mLblShare.setBackgroundResource(R.drawable.shape_frame_txt);
                mLblShare.setTextColor(Color.parseColor("#FFFFFF"));
                mRlDevice.setVisibility(View.GONE);
                mGridDevice.setVisibility(View.GONE);
                mListDevice.setVisibility(View.GONE);
                mListRoom.setVisibility(View.GONE);
                mListShare.setVisibility(View.VISIBLE);
            }
        });

        // 网格显示处理
        ImageView imgGrid = (ImageView)findViewById(R.id.mainImgGrid);
        imgGrid.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mGridDevice.setVisibility(View.VISIBLE);
                mListDevice.setVisibility(View.GONE);
                mDeviceDisplayType = 1;
            }
        });

        // 列表显示处理
        ImageView imgList = (ImageView)findViewById(R.id.mainImgList);
        imgList.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mListDevice.setVisibility(View.VISIBLE);
                mGridDevice.setVisibility(View.GONE);
                mDeviceDisplayType = 2;
            }
        });

        // 场景处理
        ImageView imgScene = (ImageView)findViewById(R.id.includeBottomImgScene);
        imgScene.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });

        // 用户处理
        ImageView imgUser = (ImageView)findViewById(R.id.includeBottomImgUser);
        imgUser.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        // 登录
        AccountHelper.login(new IAccount.loginCallback() {
            @Override
            public void returnLoginResult(int loginResult) {
                if(loginResult == 1)
                {
                    Logger.i(getString(R.string.login_success));
                    SystemParameter.getInstance().setIsLogin(true);

                    // 登录后异步处理
                    Message msg = new Message();
                    msg.what = Constant.MSG_POSTLOGINPORCESS;
                    mAPIDataHandler.sendMessage(msg);
                }
                else
                {
                    Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                    Logger.e(getString(R.string.login_failed));
                    SystemParameter.getInstance().setIsLogin(false);
                }
            }
        });
    }

    // 设置场景水平列表
    private void setSceneList(List<EScene.sceneListItemEntry> list){
        if(list == null || list.size() == 0) {
            this.mLblSceneTitle.setVisibility(View.GONE);
            this.mHscSceneList.setVisibility(View.GONE);
            return;
        }

        this.mLblSceneTitle.setVisibility(View.VISIBLE);
        this.mHscSceneList.setVisibility(View.VISIBLE);
        int size = list.size();
        int length = 130;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridViewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置GirdView布局参数(横向布局的关键)
        this.mGrdScene.setLayoutParams(params);
        // 设置列表项宽
        this.mGrdScene.setColumnWidth(itemWidth);
        // 设置列表项水平间距
        this.mGrdScene.setHorizontalSpacing(5);
        this.mGrdScene.setStretchMode(GridView.NO_STRETCH);
        // 设置列数量为列表集合数
        this.mGrdScene.setNumColumns(size);

        AptSceneGrid aptScene = new AptSceneGrid(this);
        aptScene.setData(list);
        this.mGrdScene.setAdapter(aptScene);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 刷新设备数据
        if(SystemParameter.getInstance().getIsRefreshData()) {
            // 如果绑定或解绑定了网关则重新获取设备列表
            this.startGetDeviceList();
            SystemParameter.getInstance().setIsRefreshData(false);
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
            }
        }

        // 刷新房间列表数据
        syncRoomListData();
    }

    @Override
    protected void onDestroy() {
        // 删除实时数据回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("MainStatusCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainJoinCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainPropertyCallback");
        RealtimeDataReceiver.deleteCallbackHandler("MainEventCallback");

        // 登出
        AccountHelper.logout(new IAccount.logoutCallback() {
            @Override
            public void returnLogoutResult(int logoutResult) {
                if(logoutResult == 1)
                {
                    Logger.i(getString(R.string.logout_success));
                }
                else
                {
                    Logger.e(getString(R.string.logout_failed));
                }
            }
        });

        super.onDestroy();
    }

    // 设置实时数据处理
    private void setRealtimeDataProcess() {
        // 添加实时数据回调处理器
        RealtimeDataReceiver.addStatusCallbackHandler("MainStatusCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addJoinCallbackHandler("MainJoinCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addPropertyCallbackHandler("MainPropertyCallback", this.mRealtimeDataHandler);
        RealtimeDataReceiver.addEventCallbackHandler("MainEventCallback", this.mRealtimeDataHandler);
    }

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mDeviceList != null && position < mDeviceList.size()) {
                ActivityRouter.toDetail(MainActivity.this, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey, mDeviceList.get(position).status, mDeviceList.get(position).nickName);
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
        if(this.mSceneList == null) {
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
        // 获取用户设备列表
        this.mUserCenter.getDeviceList(1, this.mDevicePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // 设备统计
    private void deviceCount() {
        int online = 0;
        int total = this.mDeviceList == null ? 0 : this.mDeviceList.size();
        if(total > 0) {
            for(EDevice.deviceEntry device : this.mDeviceList) {
                if(device.status == Constant.CONNECTION_STATUS_ONLINE) {
                    online ++;
                }
            }
        }
        mLblDeviceDescription.setText(String.format(getString(R.string.main_device_description), total, online));
    }

    // 同步设备列表数据
    private void syncDeviceListData() {
        Map<String, EDevice.deviceEntry> all = DeviceBuffer.getAllDeviceInformation();

        if(all != null && all.size() > 0) {
            this.mDeviceList.clear();
            // 网关排前面
            for(EDevice.deviceEntry e: all.values()) {
                if(e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    this.mDeviceList.add(deviceEntry);
                }
            }
            for(EDevice.deviceEntry e: all.values()) {
                if(!e.nodeType.equals(Constant.NODETYPE_GATEWAY)) {
                    EDevice.deviceEntry deviceEntry = new EDevice.deviceEntry();
                    deviceEntry.iotId = e.iotId;
                    deviceEntry.nickName = e.nickName;
                    deviceEntry.productKey = e.productKey;
                    deviceEntry.status = e.status;
                    this.mDeviceList.add(deviceEntry);
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

            this.deviceCount();
        }
    }

    // 同步房间列表数据
    private void syncRoomListData() {
        Map<String, EHomeSpace.roomEntry> all = HomeSpaceManager.getRoomBufferData();

        if(all != null && all.size() > 0) {
            this.mRoomList.clear();
            for(EHomeSpace.roomEntry e: all.values()) {
                EHomeSpace.roomEntry roomEntry = new EHomeSpace.roomEntry();
                roomEntry.roomId = e.roomId;
                roomEntry.name = e.name;
                roomEntry.deviceCnt = e.deviceCnt;
                this.mRoomList.add(roomEntry);
            }

            // 处理列表点击事件
            this.mAptRoomList.setData(this.mRoomList);
            this.mListRoom.setAdapter(this.mAptRoomList);
            //this.mListRoom.setOnItemClickListener(deviceListOnItemClickListener);
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_POSTLOGINPORCESS:
                    // 初始化实时数据接收器
                    RealtimeDataReceiver.initProcess();
                    // 设置实时数据处理
                    setRealtimeDataProcess();
                    // 获取家列表
                    mHomeSpaceManager.getHomeList(1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    // 开始获取场景列表
                    startGetSceneList();
                    break;
                case Constant.MSG_CALLBACK_CREATEHOME:
                    // 处理创建家数据
                    String homeId = CloudDataParser.processCreateHomeResult((String)msg.obj);
                    if(homeId != null && homeId.length() > 0){
                        SystemParameter.getInstance().setHomeId(homeId);
                        Logger.d("HomeId is " + SystemParameter.getInstance().getHomeId());
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String)msg.obj);
                    if(sceneList != null && sceneList.scenes != null) {
                        for(EScene.sceneListItemEntry item : sceneList.scenes) {
                            mSceneList.add(item);
                        }
                        if(sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, mScenePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则设置场景列表数据
                            setSceneList(mSceneList);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMELIST:
                    // 处理获取家列表数据
                    EHomeSpace.homeListEntry homeList = CloudDataParser.processHomeList((String)msg.obj);
                    if(homeList == null || homeList.total == 0 || homeList.data == null || homeList.data.size() == 0) {
                        // 如果没有创建家空间则自动创建我的家
                        mHomeSpaceManager.createHome(getString(R.string.homespace_defaulthome), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else {
                        // 如果没有选择家或只有一个家则默认选择第一个
                        if(SystemParameter.getInstance().getHomeId() == null || SystemParameter.getInstance().getHomeId().length() == 0 || homeList.total == 1) {
                            SystemParameter.getInstance().setHomeId(homeList.data.get(0).homeId);
                            SystemParameter.getInstance().setHomeName(homeList.data.get(0).name);
                            // 作为配置保存
                            Configure.setItem(MainActivity.this, "homeId", SystemParameter.getInstance().getHomeId());
                            Configure.setItem(MainActivity.this, "homeName", SystemParameter.getInstance().getHomeName());
                            mLblHome.setText(SystemParameter.getInstance().getHomeName());
                        }
                        // 开始获取家房间列表
                        startGetRoomList();
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表数据
                    EHomeSpace.roomListEntry roomList = CloudDataParser.processHomeRoomList((String)msg.obj);
                    if(roomList != null && roomList.data != null) {
                        // 向缓存追加数据
                        HomeSpaceManager.addRoomBufferData(roomList.data);
                        if(roomList.data.size() >= roomList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), roomList.pageNo + 1, mRoomPageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            syncRoomListData();
                            // 数据获取完则开始获取设备列表数据
                            startGetDeviceList();
                            mLblHomeDescription.setText(String.format(getString(R.string.main_home_description), SystemParameter.getInstance().getHomeName(), HomeSpaceManager.getRoomBufferData().size()));
                            // 不进行显示
                            //mLblHomeDescription.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_GETHOMEDEVICELIST:
                    // 处理获取家设备列表数据
                    EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList((String)msg.obj);
                    if(homeDeviceList != null && homeDeviceList.data != null) {
                        // 向缓存追加家列表数据
                        DeviceBuffer.addHomeDeviceList(homeDeviceList);
                        if(homeDeviceList.data.size() >= homeDeviceList.pageSize) {
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
                    EUser.bindDeviceListEntry userBindDeviceList = CloudDataParser.processUserDeviceList((String)msg.obj);
                    if(userBindDeviceList != null && userBindDeviceList.data != null) {
                        // 向缓存追加用户绑定设备列表数据
                        DeviceBuffer.addUserBindDeviceList(userBindDeviceList);
                        if(userBindDeviceList.data.size() >= userBindDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mUserCenter.getDeviceList(userBindDeviceList.pageNo + 1, mDevicePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则同步刷新设备列表数据
                            syncDeviceListData();
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
                    ERealtimeData.deviceConnectionStatusEntry entry = RealtimeDataParser.processConnectStatus((String)msg.obj);
                    boolean isFound = false;
                    if(entry != null && mAptDeviceList != null && mDeviceList != null && mDeviceList != null) {
                        for(int i = 0; i < mDeviceList.size(); i++) {
                            if(mDeviceList.get(i).iotId.equals(entry.iotId)) {
                                mDeviceList.get(i).status = entry.status;
                                isFound = true;
                                break;
                            }
                        }
                        // 刷新数据
                        mAptDeviceList.notifyDataSetChanged();
                        mAptDeviceGrid.notifyDataSetChanged();
                        deviceCount();
                    }
                    if(!isFound) {
                        // 开始获取设备列表
                        startGetDeviceList();
                    }
                    break;
                case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
                    // 开始获取设备列表
                    startGetDeviceList();
                    break;
                case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY:
                    // 处理属性通知
                    ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String)msg.obj);
                    if(propertyEntry != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\r\niotId: " + propertyEntry.iotId);
                        sb.append("\r\nproductKey: " + propertyEntry.productKey);
                        for(String name: propertyEntry.properties.keySet()) {
                            sb.append("\r\n" + name + ": " + propertyEntry.properties.get(name));
                        }
                        Logger.d("收到属性：" + sb.toString());
                    }
                    break;
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    // 处理事件通知
                    Logger.d("收到事件：" + (String)msg.obj);
                    break;
                default:
                    break;
            }

            return false;
        }
    });
}