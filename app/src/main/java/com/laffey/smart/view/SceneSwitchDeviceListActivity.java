package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySceneSwitchDeviceListBinding;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EHomeSpace;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemAction;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.UserCenter;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SceneSwitchDeviceListActivity extends BaseActivity {
    private ActivitySceneSwitchDeviceListBinding mViewBinding;

    private final int DEV_PAGE_SIZE = 50;

    private List<EDevice.deviceEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> mAdapter;
    private HomeSpaceManager mHomeSpaceManager = null;
    private UserCenter mUserCenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySceneSwitchDeviceListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mHomeSpaceManager = new HomeSpaceManager(this);
        mUserCenter = new UserCenter(this);
        initView();
//        startGetDeviceList();
        getDeviceData();

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void actionChoose(ItemAction itemAction) {
        finish();
    }

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText("选择设备");
        initAdapter();
        mViewBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewBinding.mRecyclerView.setAdapter(mAdapter);
    }

    // 开始获取设备列表
    private void startGetDeviceList() {
        // 初始化处理设备缓存器
        DeviceBuffer.initProcess();
        mList.clear();
        // 获取家设备列表
        mHomeSpaceManager.getHomeDeviceList(SystemParameter.getInstance().getHomeId(), "", 1, DEV_PAGE_SIZE,
                mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取用户设备列表
                mUserCenter.getDeviceList(1, DEV_PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        }, 200);
    }

    // 同步设备列表数据
    private void syncDeviceListData() {
        Map<String, EDevice.deviceEntry> all = DeviceBuffer.getAllDeviceInformation();
        mList.clear();
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
                    mList.add(deviceEntry);
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
                    mList.add(deviceEntry);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETHOMEDEVICELIST:
                    // 处理获取家设备列表数据
                    EHomeSpace.homeDeviceListEntry homeDeviceList = CloudDataParser.processHomeDeviceList((String) msg.obj);
                    if (homeDeviceList != null && homeDeviceList.data != null) {
                        // 向缓存追加家列表数据
                        DeviceBuffer.addHomeDeviceList(homeDeviceList);
                        if (homeDeviceList.data.size() >= homeDeviceList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mHomeSpaceManager.getHomeDeviceList(SystemParameter.getInstance().getHomeId(), "", homeDeviceList.pageNo + 1, DEV_PAGE_SIZE,
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
                            mUserCenter.getDeviceList(userBindDeviceList.pageNo + 1, DEV_PAGE_SIZE,
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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

    private void getDeviceData() {
        Map<String, EDevice.deviceEntry> entryMap = DeviceBuffer.getAllDeviceInformation();
        Iterator<Map.Entry<String, EDevice.deviceEntry>> entries = entryMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, EDevice.deviceEntry> entry = entries.next();
            mList.add(entry.getValue());
        }
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder>(R.layout.item_device, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, EDevice.deviceEntry deviceEntry) {
                baseViewHolder.setText(R.id.deviceName, deviceEntry.nickName);
                // baseViewHolder.setImageResource(R.id.deviceImageView, ImageProvider.genProductIcon(deviceEntry.productKey));
                ImageView imageView = baseViewHolder.getView(R.id.deviceImageView);
                Glide.with(SceneSwitchDeviceListActivity.this).load(deviceEntry.image).into(imageView);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                DeviceActionActivity.start(mActivity, mList.get(position).iotId, mList.get(position).nickName);
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SceneSwitchDeviceListActivity.class);
        context.startActivity(intent);
    }
}
