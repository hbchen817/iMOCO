package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.MsgCenterManager;
import com.rexense.imoco.viewholder.CommonAdapter;

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
    private String[] msgTypeArr = {"device","share","announcement"};
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
        type = getArguments().getInt("type");

        adapter = new CommonAdapter(models, mActivity);
        layoutManager = new LinearLayoutManager(mActivity);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.agree_btn:

                        break;
                    case R.id.disagree_btn:

                        break;
                }
            }
        });
        // 获取消息列表
        msgCenterManager.getMsgList(msgTypeArr[type], mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
//        getData();
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
                default:
                    break;
            }
            return false;
        }
    });

    private void getData(){
        for (int i=0;i<5;i++){
            ItemMsgCenter itemMsgCenter = new ItemMsgCenter();
            itemMsgCenter.setTitle("消息标题");
            itemMsgCenter.setContent("消息呢容");
            itemMsgCenter.setShowBtnView(type==1);
            models.add(itemMsgCenter);
        }
        adapter.notifyDataSetChanged();
    }

}
