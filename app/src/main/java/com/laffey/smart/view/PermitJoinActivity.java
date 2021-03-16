package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.ERealtimeData;
import com.laffey.smart.model.EScene;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.ConfigureNetwork;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.Dialog;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.Utility;
import com.laffey.smart.widget.ComCircularProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 允许子设备入网
 */
public class PermitJoinActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.permitJoinCPProgress)
    ComCircularProgress mProgressBar;
    private String mGatewayIOTId;
    private String mProductKey;
    private String mProductName;
    private boolean mIsProhibit = false;
    private boolean mIsJoinSuccess = false;
    private TextView mReaminSecond;
    private ConfigureNetwork mConfigNetwork;
    private final int mTimeoutSecond = 120;
    private final int mIntervalMS = 100;
    private Thread mJoinThread = null;

    private String mSubDeviceName;
    private String mSubDeviceIotId;

    // 处理允许入网进度
    private Handler prcessPermitJoinProgressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_PERMITJOIN_STEP_START == msg.what) {
                // 开始处理
                mReaminSecond.setText("0");
                mProgressBar.setProgress(0);
            } else if (Constant.MSG_PERMITJOIN_REMAIN_SECOND == msg.what) {
                // 剩余秒数处理
                int progress = msg.arg1;
                if (0 == ((progress * mIntervalMS) % 1000)) {
                    int process = (progress * mIntervalMS) / (mTimeoutSecond * 10);
                    mReaminSecond.setText(process + "");
                    mProgressBar.setProgress(process);
                }
            } else if (Constant.MSG_PERMITJOIN_TIMEOUT == msg.what) {
                // 超时处理
                Dialog.confirm(PermitJoinActivity.this, R.string.dialog_title, getString(R.string.permitjoin_timeout), R.drawable.dialog_fail, R.string.dialog_confirm, true);
            }
            return false;
        }
    });

    private void deviceHandle(SceneManager mSceneManager) {
        switch (mProductKey) {
            case CTSL.PK_LIGHT:
                mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_ONE_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_TWO_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_THREE_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_FOUR_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_SIX_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_3, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SCENE_SWITCH_KEY_CODE_4, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                mSceneManager.setExtendedProperty(mSubDeviceIotId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, "{}", mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                break;
            default:
                break;
        }
    }

    // API数据处理器
    private Handler mAPIProcessDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            SceneManager mSceneManager = new SceneManager(mActivity);
            if (Constant.MSG_CALLBACK_BINDSUBDEVICE == msg.what) {
                // 绑定子设备回调
                if (msg.obj != null && ((String) msg.obj).length() > 0) {
                    // 发送拒绝入网
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPermitJoinCommand(1);
                        }
                    }, 5000);
                    Message msg1 = new Message();
                    msg1.what = Constant.MSG_PERMITJOIN_STEP_END;
                    prcessPermitJoinProgressHandler.sendMessage(msg1);
                    Toast.makeText(PermitJoinActivity.this, getString(R.string.permitjoin_success), Toast.LENGTH_LONG).show();
                    mIsJoinSuccess = true;
                    // 发送刷新设备状态事件
                    RefreshData.refreshDeviceStateData();
                    deviceHandle(mSceneManager);
                    BindSuccessActivity.start(PermitJoinActivity.this, mSubDeviceIotId, mSubDeviceName);

                    // 发送刷新设备列表事件
                    RefreshData.refreshDeviceListData();
                    SystemParameter.getInstance().setIsRefreshDeviceData(true);

                    // 中断加网线程
                    if (mJoinThread != null) {
                        if (!mJoinThread.isInterrupted()) {
                            mJoinThread.interrupt();
                        }
                        mJoinThread = null;
                    }
                    finish();
                }
            } else if (Constant.MSG_CALLBACK_QUERYSCENELIST == msg.what) {
                // 处理获取场景列表数据
                EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                if (sceneList != null && sceneList.scenes != null) {
                    for (EScene.sceneListItemEntry item : sceneList.scenes) {
                        if (item.description.equals(mSubDeviceIotId)) {
                            mSceneManager.deleteScene(item.id, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                        }
                    }
                    if (sceneList.scenes.size() >= sceneList.pageSize) {
                        // 数据没有获取完则获取下一页数据
                        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                    }
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
                    Log.i("lzm", "(String) msg.obj" + (String) msg.obj);
                    ERealtimeData.subDeviceJoinResultEntry joinResultEntry = RealtimeDataParser.proessSubDeviceJoinResult((String) msg.obj);
                    mSubDeviceName = joinResultEntry.subDeviceName;
                    mSubDeviceIotId = joinResultEntry.subIotId;
                    if (joinResultEntry != null && joinResultEntry.subDeviceName != null && joinResultEntry.subDeviceName.length() > 0 &&
                            joinResultEntry.subProductKey != null && joinResultEntry.subProductKey.length() > 0) {
                        Logger.d(String.format("Received subdevice join callback:\r\n    device name: %s\r\n    product key: %s",
                                joinResultEntry.subDeviceName, joinResultEntry.subProductKey));
                        // 绑定子设备
                        if (joinResultEntry.subProductKey.equals(mProductKey) && joinResultEntry.status == Constant.ADD_STATUS_SUCCESS) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mConfigNetwork.bindSubDevice(SystemParameter.getInstance().getHomeId(), mProductKey, joinResultEntry.subDeviceName, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
                                }
                            }, 2000);
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
        ButterKnife.bind(this);

        Intent intent = getIntent();
        this.mGatewayIOTId = intent.getStringExtra("gatewayIOTId");
        this.mProductKey = intent.getStringExtra("productKey");
        this.mProductName = intent.getStringExtra("productName");

        this.mConfigNetwork = new ConfigureNetwork(this);
        tvToolbarTitle.setText("添加设备");
        this.initProcess();

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        // 删除长连接实时数据子设备入网回调处理器
        RealtimeDataReceiver.deleteCallbackHandler("PermitJoinJoinCallback");

        // 中断加网线程
        if (this.mJoinThread != null) {
            if (!this.mJoinThread.isInterrupted()) {
                this.mJoinThread.interrupt();
            }
            this.mJoinThread = null;
        }

        super.onDestroy();
    }

    // 初始化处理
    private void initProcess() {
        this.mReaminSecond = (TextView) findViewById(R.id.permitJoinLblRemainSecond);


        // 允许入网事件处理
        OnClickListener permitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsProhibit = false;
                sendPermitJoinCommand(mTimeoutSecond);
            }
        };

        // 追加长连接实时数据子设备入网回调处理器
        RealtimeDataReceiver.addJoinCallbackHandler("PermitJoinJoinCallback", this.mRealtimeDataHandler);

        // 发送允许入网120秒命令
        sendPermitJoinCommand(this.mTimeoutSecond);
    }

    // 发送允许入网命令
    private void sendPermitJoinCommand(int duration) {
        // 发送命令
        this.mConfigNetwork.permitJoinSubDevice(mGatewayIOTId, mProductKey, duration, mCommitFailureHandler, mResponseErrorHandler, mAPIProcessDataHandler);
        if (duration == 1) {
            return;
        }

        // 创建并启动允许入网计时线程
        this.mJoinThread = new Thread() {
            @Override
            public void run() {
                Message msg1 = new Message();
                msg1.what = Constant.MSG_PERMITJOIN_STEP_START;
                prcessPermitJoinProgressHandler.sendMessage(msg1);
                int count = 1;
                while (count <= (mTimeoutSecond * 1000 / mIntervalMS) && !Thread.interrupted()) {
                    if (mIsProhibit) {
                        break;
                    }
                    if (mIsJoinSuccess) {
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

                if (!mIsProhibit && !mIsJoinSuccess) {
                    Message msg3 = new Message();
                    msg3.what = Constant.MSG_PERMITJOIN_TIMEOUT;
                    prcessPermitJoinProgressHandler.sendMessage(msg3);
                }

                this.interrupt();
            }
        };
        this.mJoinThread.start();
    }

    @OnClick(R.id.iv_toolbar_left)
    public void onViewClicked() {
        mIsProhibit = true;
        sendPermitJoinCommand(1);
        finish();
    }
}