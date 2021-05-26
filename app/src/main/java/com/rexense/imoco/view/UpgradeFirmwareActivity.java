package com.rexense.imoco.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityUpgradeFirmwareBinding;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.OTAHelper;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.widget.DialogUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeFirmwareActivity extends BaseActivity {
    private ActivityUpgradeFirmwareBinding mViewBinding;

    //private String iotId;
    //private String productKey;
    //private boolean upgradingFlag;
    //private String theNewVersion;
    //private String currentVersion;
    private ArrayList<String> iotIdList = new ArrayList<>();

    private final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            mViewBinding.versionInfoView.setVisibility(View.GONE);
            mViewBinding.progressView.setVisibility(View.VISIBLE);
            mViewBinding.upgradeBtn.setVisibility(View.INVISIBLE);

            OTAHelper.upgradeFirmware(iotIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            //定时器模拟升级进度
            mProgressTimer.schedule(mProgressTask, 1000, 1000);
        }
    };

    private final DialogInterface.OnClickListener onSuccessClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            mProgressTimer.cancel();
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityUpgradeFirmwareBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.upgrade_firmware));
        String iotId = getIntent().getStringExtra("iotId");
        iotIdList.add(iotId);
        String productKey = getIntent().getStringExtra("productKey");
        String currentVersion = getIntent().getStringExtra("currentVersion");
        String theNewVersion = getIntent().getStringExtra("theNewVersion");

        mViewBinding.deviceImg.setImageResource(ImageProvider.genProductIcon(productKey));
        mViewBinding.currentVersionTv.setText(currentVersion);
        mViewBinding.newVersionTv.setText(theNewVersion);

        // 增加OTA升级回调处理理器
        RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);

        mViewBinding.upgradeBtn.setOnClickListener(this::onClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 删除OTA升级回调处理理器
        RealtimeDataReceiver.deleteCallbackHandler("UpgradeFirmwareCallback");
    }

    private int mProgressNum = 0;
    private Timer mProgressTimer = new Timer();
    TimerTask mProgressTask = new TimerTask() {
        @Override
        public void run() {
            mProgressNum++;
            Message message = new Message();
            message.what = 1;
            mProgressHandler.sendMessage(message);
        }
    };

    private final Handler mProgressHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                //模拟升级进度
                int percent = mProgressNum;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewBinding.processView.getLayoutParams();
                if (percent < 100) {
                    mViewBinding.processTv.setText(percent + "%");
                    layoutParams.width = percent * mViewBinding.processBgView.getWidth() / 100;
                    mViewBinding.processView.setLayoutParams(layoutParams);
                } else {
                    mViewBinding.processTv.setText("100%");
                    layoutParams.width = mViewBinding.processBgView.getWidth();
                    mViewBinding.processView.setLayoutParams(layoutParams);
                }
            }
            return false;
        }
    });

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Constant.MSG_CALLBACK_UPGRADEFIRMWARE) {
                // RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);
            }
            return false;
        }
    });

    // 实时数据处理器
    private final Handler mRealtimeDataHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNOTAUPGRADENOTIFY:
                    JSONObject resultJson = JSONObject.parseObject((String) msg.obj);
                    //upgradeStatus表示升级结果，可取值包括，0：待升级或待确认，1：升级中，2：升级异常，3：升级失败，4：升级完成。
                    int upgradeStatus = resultJson.getInteger("upgradeStatus");
                    if (1 == upgradeStatus) {
                        //step -1：表示升级失败，-2：表示下载失败，-3：表示校验失败，-4：表示烧写失败 1-100为进度
                        int step = resultJson.getInteger("step");
                        if (step >= 0 && step <= 100) {
                            // 如果模拟进度小于实际进度则强制为实际进度
                            if (step > mProgressNum) {
                                mProgressNum = step;
                                Message message = new Message();
                                message.what = 1;
                                mProgressHandler.sendMessage(message);
                            }
                        } else {
                            String failMsg = "";
                            switch (step) {
                                case -1:
                                    failMsg = getString(R.string.upgrade_step_1);
                                    break;
                                case -2:
                                    failMsg = getString(R.string.upgrade_step_2);
                                    break;
                                case -3:
                                    failMsg = getString(R.string.upgrade_step_3);
                                    break;
                                case -4:
                                    failMsg = getString(R.string.upgrade_step_4);
                                    break;
                            }
                            DialogUtils.showMsgDialog(mActivity, failMsg);
                        }
                    } else {
                        String hintMsg = "";
                        switch (upgradeStatus) {
                            case 0:
                                hintMsg = getString(R.string.upgrade_status_0);
                                break;
                            case 2:
                                hintMsg = getString(R.string.upgrade_status_2);
                                break;
                            case 3:
                                hintMsg = getString(R.string.upgrade_status_3);
                                break;
                            case 4:
                                hintMsg = getString(R.string.upgrade_status_4);
                                break;
                        }
                        if (4 == upgradeStatus) {
                            // 升级成功处理
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewBinding.processView.getLayoutParams();
                            mViewBinding.processTv.setText("100%");
                            layoutParams.width = mViewBinding.processBgView.getWidth();
                            mViewBinding.processView.setLayoutParams(layoutParams);
                            // 发送刷新网关固件数据事件
                            RefreshData.refreshGatewayFirmwareData();
                            DialogUtils.showConfirmDialog(mActivity, onSuccessClickListener, hintMsg, getString(R.string.dialog_title));
                        } else {
                            // 升级失败处理
                            DialogUtils.showMsgDialog(mActivity, hintMsg);
                        }
                    }

                    break;
            }
            return false;
        }
    });

    void onClick(View view) {
        if (view.getId() == R.id.upgrade_btn) {
            DialogUtils.showEnsureDialog(mActivity, onClickListener, getString(R.string.upgrading_please_waiting_here), "");
        }
    }
}
