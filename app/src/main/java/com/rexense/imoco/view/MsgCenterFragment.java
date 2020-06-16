package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.ShareDeviceSuccessEvent;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.MsgCenterManager;
import com.rexense.imoco.presenter.ShareDeviceManager;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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

    private CommonAdapter adapter;
    private List<Visitable> models = new ArrayList<Visitable>();
    private LinearLayoutManager layoutManager;
    private Intent intent;
    private int type;
    private MsgCenterManager msgCenterManager;
    private ShareDeviceManager shareDeviceManager;
    private String[] msgTypeArr = {"device","share","announcement"};
    private ArrayList<String> recordIdList = new ArrayList<>();
    private boolean agreeFlag;
    @Override
    public void onDestroyView() {
//        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
//        EventBus.getDefault().register(this);
        return R.layout.fragment_msg_center;
    }

    @Override
    protected void init() {
        msgCenterManager = new MsgCenterManager(getActivity());
        shareDeviceManager = new ShareDeviceManager(getActivity());
        type = getArguments().getInt("type");

        adapter = new CommonAdapter(models, mActivity);
        layoutManager = new LinearLayoutManager(mActivity);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = (int) view.getTag();
                ItemMsgCenter itemMsgCenter = (ItemMsgCenter) models.get(p);
                switch (view.getId()){
                    case R.id.agree_btn:
                        agreeFlag = true;
                        recordIdList.clear();
                        recordIdList.add(itemMsgCenter.getRecordId());
                        shareDeviceManager.confirmShare(1,recordIdList,mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        break;
                    case R.id.disagree_btn:
                        agreeFlag = false;
                        recordIdList.clear();
                        recordIdList.add(itemMsgCenter.getRecordId());
                        shareDeviceManager.confirmShare(0,recordIdList,mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        break;
                }
            }
        });
        // 获取消息列表
        if (type==1){
            msgCenterManager.getShareNoticeList(1,20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }else {
            msgCenterManager.getMsgList(msgTypeArr[type], mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_MSGCENTER:
                    models.addAll(CloudDataParser.processMsgCenterList((String) msg.obj));
                    adapter.notifyDataSetChanged();
                    break;
                case Constant.MSG_CALLBACK_SHARENOTICELIST:
                    models.addAll(CloudDataParser.processShareDeviceNoticeList((String) msg.obj));
                    adapter.notifyDataSetChanged();
                    break;
                case Constant.MSG_CALLBACK_CONFIRMSHARE:
                    ToastUtils.showToastCentrally(mActivity,getString(agreeFlag?R.string.msg_center_agree_success:R.string.msg_center_disagree_success));
                    EventBus.getDefault().post(new ShareDeviceSuccessEvent());
                    models.clear();
                    msgCenterManager.getShareNoticeList(1,20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

}
