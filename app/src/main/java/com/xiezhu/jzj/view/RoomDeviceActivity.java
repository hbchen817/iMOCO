package com.xiezhu.jzj.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.event.RefreshRoomDevice;
import com.xiezhu.jzj.event.RefreshRoomName;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.presenter.ActivityRouter;
import com.xiezhu.jzj.presenter.AptDeviceList;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.DeviceBuffer;
import com.xiezhu.jzj.presenter.HomeSpaceManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.utility.SrlUtils;
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

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.recycle_view)
    ListView mListDevice;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;
    @BindView(R.id.iv_toolbar_right)
    ImageView ivToolbarRight;

    private HomeSpaceManager homeSpaceManager;
    private int page = 1;
    private String roomId;
    private String homeId;
    private String roomName;
    private List<EDevice.deviceEntry> mDeviceList = null;
    private AptDeviceList mAptDeviceList = null;
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

    @Subscribe
    public void onRefreshRoomDevice(RefreshRoomDevice refreshRoomDevice){
        page=1;
        getData();
    }
    @Subscribe
    public void onRefreshRoomName(RefreshRoomName refreshRoomName){
        roomName = refreshRoomName.getName();
        tvToolbarTitle.setText(roomName);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_device);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        homeId = SystemParameter.getInstance().getHomeId();
        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        tvToolbarTitle.setText(roomName);
        ivToolbarRight.setImageResource(R.drawable.more_default);
        ivToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,AddRoomDeviceActivity.class);
                intent.putExtra("roomName",getIntent().getStringExtra("roomName"));
                intent.putExtra("roomId",roomId);
                startActivity(intent);
            }
        });

        homeSpaceManager = new HomeSpaceManager(mActivity);
        this.mAptDeviceList = new AptDeviceList(mActivity);
        this.mDeviceList = new ArrayList<EDevice.deviceEntry>();
        this.mAptDeviceList.setData(this.mDeviceList);
        mListDevice.setAdapter(this.mAptDeviceList);
        mListDevice.setOnItemClickListener(deviceListOnItemClickListener);

        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        getData();
    }

    private void getData(){
        homeSpaceManager.getRoomDevice(page,homeId,roomId,mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETDEVICEINROOM:
                    if (page==1){
                        mDeviceList.clear();
                    }
                    mDeviceList.addAll(CloudDataParser.processRoomDeviceList((String) msg.obj));
                    mAptDeviceList.notifyDataSetChanged();
                    SrlUtils.finishRefresh(mSrlFragmentMe,true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe,true);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDeviceList != null && position < mDeviceList.size()) {
                // 处理owned以在下级页面判断是否可以修改房间
                int owned = DeviceBuffer.getDeviceOwned(mDeviceList.get(position).iotId);
                ActivityRouter.toDetail(mActivity, mDeviceList.get(position).iotId, mDeviceList.get(position).productKey,
                        mDeviceList.get(position).status, mDeviceList.get(position).nickName, owned);
            }
        }
    };

}
