package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshMsgCenter;
import com.rexense.imoco.event.ShareDeviceSuccessEvent;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.MsgCenterManager;
import com.rexense.imoco.presenter.ShareDeviceManager;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class MsgCenterFragment extends BaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;

    private CommonAdapter adapter;
    private List<Visitable> models = new ArrayList<Visitable>();
    private Intent intent;
    private int type;
    private MsgCenterManager msgCenterManager;
    private ShareDeviceManager shareDeviceManager;
    private String[] msgTypeArr = {"device", "share", "announcement"};
    private ArrayList<String> recordIdList = new ArrayList<>();
    private boolean agreeFlag;
    private int page = 1;

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            if (type == 1) {
                page = 1;
            } else {
                page = 0;
            }
            getData();
        }
    };

    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            if (type == 1) {
                page++;
            } else {
                page = page + Constant.PAGE_SIZE;
            }
            getData();
        }
    };

    @Subscribe
    public void onRefreshMsg(RefreshMsgCenter refreshMsgCenter) {
        if (type == 1 && refreshMsgCenter.getType() == 1) {//刷新设备共享消息
            page = 1;
            getData();
        }
        if (type != 1 && refreshMsgCenter.getType() != 1) {//刷新其他消息
            page = 0;
            getData();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
        EventBus.getDefault().register(this);
        return R.layout.fragment_msg_center;
    }

    @Override
    protected void init() {
        msgCenterManager = new MsgCenterManager(getActivity());
        shareDeviceManager = new ShareDeviceManager(getActivity());
        type = getArguments().getInt("type");
        if (type == 1) {
            page = 1;
        } else {
            page = 0;
        }

        adapter = new CommonAdapter(models, mActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = (int) view.getTag();
                ItemMsgCenter itemMsgCenter = (ItemMsgCenter) models.get(p);
                if (view.getId() == R.id.agree_btn) {
                    agreeFlag = true;
                    recordIdList.clear();
                    recordIdList.add(itemMsgCenter.getRecordId());
                    shareDeviceManager.confirmShare(1, recordIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                } else if (view.getId() == R.id.disagree_btn) {
                    agreeFlag = false;
                    recordIdList.clear();
                    recordIdList.add(itemMsgCenter.getRecordId());
                    shareDeviceManager.confirmShare(0, recordIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        getData();
    }

    private void getData() {
        // 获取消息列表
        if (type == 1) {
            msgCenterManager.getShareNoticeList(page, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else {
            msgCenterManager.getMsgList(page, msgTypeArr[type], mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_MSGCENTER:
                    if (page == 0) {
                        models.clear();
                    }
                    models.addAll(CloudDataParser.processMsgCenterList((String) msg.obj));
                    adapter.notifyDataSetChanged();
                    SrlUtils.finishRefresh(mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe, true);
                    break;
                case Constant.MSG_CALLBACK_SHARENOTICELIST:
                    if (page == 1) {
                        models.clear();
                    }
                    models.addAll(CloudDataParser.processShareDeviceNoticeList((String) msg.obj));
                    adapter.notifyDataSetChanged();
                    SrlUtils.finishRefresh(mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe, true);
                    break;
                case Constant.MSG_CALLBACK_CONFIRMSHARE:
                    ToastUtils.showToastCentrally(mActivity, getString(agreeFlag ? R.string.msg_center_agree_success : R.string.msg_center_disagree_success));
                    EventBus.getDefault().post(new ShareDeviceSuccessEvent());
                    //刷新页面数据
                    page = 1;
                    getData();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

}
