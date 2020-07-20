package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rexense.imoco.R;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.presenter.ConfigureNetwork;
import com.rexense.imoco.presenter.RealtimeDataParser;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ERealtimeData;
import com.rexense.imoco.widget.ComCircularProgress;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.Utility;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 允许子设备入网
 */
public class PermitJoinActivity extends BaseActivity {
    private String mGatewayIOTId;
    private String mProductKey;
    private String mProductName;
    private boolean mIsProhibit = false;
    private boolean mIsJoinSuccess = false;
    private ComCircularProgress mComCircularProgress;
    private RelativeLayout mPermit;
    private TextView mReaminSecond;
    private ConfigureNetwork mConfigNetwork;
    private final int mTimeoutSecond = 120;
    private final int mIntervalMS = 100;
    private Thread mJoinThread = null;

    // 处理允许入网进度
    private Handler prcessPermitJoinProgressHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            if(Constant.MSG_PERMITJOIN_STEP_START == msg.what) {
                // 开始处理
                mPermit.setVisibility(View.GONE);
                mComCircularProgress.setMaxProgress(mTimeoutSecond * 1000 / mIntervalMS);
                mComCircularProgress.setProgress(0);
            } else if(Constant.MSG_PERMITJOIN_REMAIN_SECOND == msg.what) {
                // 剩余秒数处理
                int progress = msg.arg1;
                if(0 == ((progress * mIntervalMS) % 1000)) {
                    int remain = ((mTimeoutSecond * 1000) - (progress * mIntervalMS)) / 1000;
                    mReaminSecond.setText("剩余" + remain + "秒");
                }
                mComCircularProgress.setProgress(progress);
            } else if(Constant.MSG_PERMITJOIN_TIMEOUT == msg.what) {
                // 超时处理
                Dialog.confirm(PermitJoinActivity.this, R.string.dialog_title, getString(R.string.permitjoin_timeout), R.drawable.dialog_fail, R.string.dialog_confirm, false);
                mPermit.setVisibility(View.VISIBLE);
            }
            return false;
        }
    });

    // API数据处理器
    private Handler mAPIProcessDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            if(Constant.MSG_CALLBACK_BINDSUBDEVICE == msg.what) {
                // 绑定子设备回调
                if(msg.obj != null && ((String)msg.obj).length() > 0) {
                    // 发送拒绝入网
                    sendPermitJoinCommand(1);
                    Message msg1 = new Message();
                    msg1.what = Constant.MSG_PERMITJOIN_STEP_END;
                    prcessPermitJoinProgressHandler.sendMessage(msg1);
                    Toast.makeText(PermitJoinActivity.this, getString(R.string.permitjoin_success), Toast.LENGTH_LONG).show();
                    mIsJoinSuccess = true;

                    // 发送刷新设备状态事件
                    RefreshData.refreshDeviceStateData();

                    // 中断加网线程
                    if(mJoinThread != null) {
                        if(!mJoinThread.isInterrupted()) {
                            mJoinThread.interrupt();
                        }
                        mJoinThread = null;
                    }
                    finish();
                }
            }
            return false;
        }
    });

    // 实时数据处理器
    private Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY:
                    // 处理子设备加网通知
                    ERealtimeData.subDeviceJoinResultEntry joinResultEntry = RealtimeDataParser.proessSubDeviceJoinResult((String)msg.obj);
                    if(joinResultEntry != null && joinResultEntry.subDeviceName != null && joinResultEntry.subDeviceName.length() > 0 &&
                            joinResultEntry.subProductKey != null && joinResultEntry.subProductKey.length() > 0) {
                        Logger.d(String.format("Received subdevice join callback:\r\n    device name: %s\r\n    product key: %s",
                                joinResultEntry.subDeviceName, joinResultEntry.subProductKey));
                        // 绑定子设备
                        if(joinResultEntry.subProductKey.equals(mProductKey) && joinResultEntry.status == Constant.ADD_STATUS_SUCCESS) {
                            mConfigNetwork.bindSubDevice(SystemParameter.getInstance().getHomeId(), mProductKey, joinResultEntry.subDeviceName, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permitjoin);

        Intent intent = getIntent();
        this.mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        this.mProductKey = intent.getStringExtra("productKey");
        this.mProductName = intent.getStringExtra("productName");

        this.mConfigNetwork = new ConfigureNetwork(this);

        this.initProcess();
    }

    @Override
    protected void onDestroy() {
        // 删除长连接实时数据子设备入网回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("PermitJoinJoinCallback");

        // 中断加网线程
        if(this.mJoinThread != null) {
            if(!this.mJoinThread.isInterrupted()) {
                this.mJoinThread.interrupt();
            }
            this.mJoinThread = null;
        }

        super.onDestroy();
    }

    // 初始化处理
    private void initProcess() {
        this.mComCircularProgress = (ComCircularProgress)findViewById(R.id.permitJoinCPProgress);
        this.mReaminSecond = (TextView)findViewById(R.id.permitJoinLblRemainSecond);

        TextView lblHint = (TextView) findViewById(R.id.permitJoinLblHint);
        lblHint.setText(String.format(getString(R.string.permitjoin_hint), this.mProductName));
        this.mPermit = (RelativeLayout)findViewById(R.id.permitJoinRLPermit);

        // 允许入网事件处理
        OnClickListener permitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsProhibit = false;
                sendPermitJoinCommand(mTimeoutSecond);
            }
        };
        TextView lblPermit = (TextView) findViewById(R.id.permitJoinLblPermit);
        ImageView imgPermit = (ImageView) findViewById(R.id.permitJoinImgPermit);
        lblPermit.setOnClickListener(permitOnClickListener);
        imgPermit.setOnClickListener(permitOnClickListener);

        // 拒绝入网事件处理
        OnClickListener cannelOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsProhibit = true;
                sendPermitJoinCommand(1);
                finish();
            }
        };
        TextView lblProhibit = (TextView) findViewById(R.id.permitJoinLblProhibit);
        ImageView imgProhibit = (ImageView) findViewById(R.id.permitJoinImgProhibit);
        lblProhibit.setOnClickListener(cannelOnClickListener);
        imgProhibit.setOnClickListener(cannelOnClickListener);

        // 追加长连接实时数据子设备入网回调处理器
        RealtimeDataReceiver.addJoinCallbackHandler("PermitJoinJoinCallback", this.mRealtimeDataHandler);

        // 发送允许入网120秒命令
        sendPermitJoinCommand(this.mTimeoutSecond);
    }

    // 发送允许入网命令
    private void sendPermitJoinCommand(int duration) {
        // 发送命令
        this.mConfigNetwork.permitJoinSubDevice(mGatewayIOTId, mProductKey, duration, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
        if(duration == 1) {
            return;
        }

        // 创建并启动允许入网计时线程
        this.mJoinThread = new Thread(){
            @Override
            public void run(){
                Message msg1 = new Message();
                msg1.what = Constant.MSG_PERMITJOIN_STEP_START;
                prcessPermitJoinProgressHandler.sendMessage(msg1);
                int count = 1;
                while (count <= (mTimeoutSecond * 1000 / mIntervalMS) && !Thread.interrupted()) {
                    if(mIsProhibit){
                        break;
                    }
                    if(mIsJoinSuccess){
                        break;
                    }

                    count++;
                    Utility.sleepMilliSecond(mIntervalMS);

                    // 发送等待进度
                    Message msg2 = new Message();
                    msg2.what = Constant.MSG_PERMITJOIN_REMAIN_SECOND;
                    msg2.arg1 = count;
                    prcessPermitJoinProgressHandler.sendMessage(msg2);
                }

                if(!mIsProhibit && !mIsJoinSuccess) {
                    Message msg3 = new Message();
                    msg3.what = Constant.MSG_PERMITJOIN_TIMEOUT;
                    prcessPermitJoinProgressHandler.sendMessage(msg3);
                }

                this.interrupt();
            }
        };
        this.mJoinThread.start();
    }
}