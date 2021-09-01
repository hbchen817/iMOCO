package com.rexense.wholehouse.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityRoomDeviceBinding;
import com.rexense.wholehouse.event.RefreshRoomDevice;
import com.rexense.wholehouse.event.RefreshRoomName;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.presenter.ActivityRouter;
import com.rexense.wholehouse.presenter.AptDeviceList;
import com.rexense.wholehouse.presenter.CloudDataParser;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.HomeSpaceManager;
import com.rexense.wholehouse.presenter.SystemParameter;
import com.rexense.wholehouse.utility.SrlUtils;
import com.rexense.wholehouse.utility.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomDeviceActivity extends BaseActivity {
    private ActivityRoomDeviceBinding mViewBinding;

    private HomeSpaceManager homeSpaceManager;
    private int page = 1;
    private String roomId;
    private String homeId;
    private String roomName;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private AptDeviceList mAptDeviceList = null;

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            page = 1;
            getData();
        }
    };

    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            page++;
            getData();
        }
    };

    @Subscribe
    public void onRefreshRoomDevice(RefreshRoomDevice refreshRoomDevice) {
        page = 1;
        getData();
    }

    @Subscribe
    public void onRefreshRoomName(RefreshRoomName refreshRoomName) {
        roomName = refreshRoomName.getName();
        mViewBinding.includeToolbar.tvToolbarTitle.setText(roomName);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityRoomDeviceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);

        homeId = SystemParameter.getInstance().getHomeId();
        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        mViewBinding.includeToolbar.tvToolbarTitle.setText(roomName);
        mViewBinding.includeToolbar.ivToolbarRight.setImageResource(R.drawable.more_default);
        mViewBinding.includeToolbar.ivToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AddRoomDeviceActivity.class);
                intent.putExtra("roomName", getIntent().getStringExtra("roomName"));
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        homeSpaceManager = new HomeSpaceManager(mActivity);
        mAptDeviceList = new AptDeviceList(mActivity);
        mDeviceList = new ArrayList<EDevice.deviceEntry>();
        mAptDeviceList.setData(mDeviceList);
        mViewBinding.recycleView.setAdapter(mAptDeviceList);
        mViewBinding.recycleView.setOnItemClickListener(deviceListOnItemClickListener);

        mViewBinding.srlFragmentMe.setOnRefreshListener(onRefreshListener);
        mViewBinding.srlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        getData();

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

    private void getData() {
        homeSpaceManager.getRoomDevice(page, homeId, roomId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_GETDEVICEINROOM) {
                if (page == 1) {
                    mDeviceList.clear();
                }
                mDeviceList.addAll(CloudDataParser.processRoomDeviceList((String) msg.obj));
                mAptDeviceList.notifyDataSetChanged();
                SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
                SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
            }
            if (mDeviceList.isEmpty()) {
                mViewBinding.recycleView.setVisibility(View.GONE);
                mViewBinding.devNodataView.setVisibility(View.VISIBLE);
            } else {
                mViewBinding.recycleView.setVisibility(View.VISIBLE);
                mViewBinding.devNodataView.setVisibility(View.GONE);
            }
            return false;
        }
    });

    // 设备列表点击监听器
    private final AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                // 处理owned以在下级页面判断是否可以修改房间
                int owned = DeviceBuffer.getDeviceOwned(mDeviceList.get(position).iotId);
                if (mDeviceList.get(position) != null && mDeviceList.get(position).productKey != null)
                    ActivityRouter.toDetail(mActivity, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                            mDeviceList.get(position).status, mDeviceList.get(position).nickName, owned);
                else
                    ToastUtils.showLongToast(RoomDeviceActivity.this, R.string.pls_try_again_later);
            }
        }
    };

}
