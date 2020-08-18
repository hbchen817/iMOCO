package com.rexense.imoco.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.event.RefreshFirmwareVersion;
import com.rexense.imoco.presenter.ImageProvider;
import com.rexense.imoco.presenter.OTAHelper;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.widget.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpgradeFirmwareActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.device_img)
    ImageView deviceImg;
    @BindView(R.id.current_version_tv)
    TextView currentVersionTv;
    @BindView(R.id.new_version_tv)
    TextView newVersionTv;
    @BindView(R.id.version_info_view)
    LinearLayout versionInfoView;
    @BindView(R.id.process_view)
    View processView;
    @BindView(R.id.process_bg_view)
    View processBgView;
    @BindView(R.id.process_tv)
    TextView processTv;
    @BindView(R.id.progress_view)
    LinearLayout progressView;
    @BindView(R.id.upgrade_btn)
    TextView upgradeBtn;
    private String iotId;
    private String productKey;
    private boolean upgradingFlag;
    private String theNewVersion;
    private String currentVersion;
    private ArrayList<String> iotIdList=new ArrayList<>();

    private DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            versionInfoView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            upgradeBtn.setVisibility(View.INVISIBLE);

            OTAHelper.upgradeFirmware(iotIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            //定时器模拟升级进度
            mProgressTimer.schedule(mProgressTask, 1000, 1000);
        }
    };

    private DialogInterface.OnClickListener onSuccessClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            mProgressTimer.cancel();
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_firmware);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.upgrade_firmware));
        iotId = getIntent().getStringExtra("iotId");
        iotIdList.add(iotId);
        productKey = getIntent().getStringExtra("productKey");
        currentVersion = getIntent().getStringExtra("currentVersion");
        theNewVersion = getIntent().getStringExtra("theNewVersion");

        deviceImg.setImageResource(ImageProvider.genProductIcon(productKey));
        currentVersionTv.setText(currentVersion);
        newVersionTv.setText(theNewVersion);

        // 增加OTA升级回调处理理器
        RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);
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

    private Handler mProgressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                //模拟升级进度
                int percent = mProgressNum;
                FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) processView.getLayoutParams();
                if (percent < 100){
                    processTv.setText(percent+"%");
                    layoutParams.width = percent*processBgView.getWidth()/100;
                    processView.setLayoutParams(layoutParams);
                } else {
                    processTv.setText("100%");
                    layoutParams.width = processBgView.getWidth();
                    processView.setLayoutParams(layoutParams);
                }
            }
            return false;
        }
    });

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_UPGRADEFIRMWARE:
                    // RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);
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
            switch (msg.what){
                case Constant.MSG_CALLBACK_LNOTAUPGRADENOTIFY:
                    JSONObject resultJson = JSONObject.parseObject((String) msg.obj);
                    //upgradeStatus表示升级结果，可取值包括，0：待升级或待确认，1：升级中，2：升级异常，3：升级失败，4：升级完成。
                    int upgradeStatus = resultJson.getInteger("upgradeStatus");
                    if(1 == upgradeStatus){
                        //step -1：表示升级失败，-2：表示下载失败，-3：表示校验失败，-4：表示烧写失败 1-100为进度
                        int step = resultJson.getInteger("step");
                        if (step >= 0 && step <= 100){
                            // 如果模拟进度小于实际进度则强制为实际进度
                            if(step > mProgressNum){
                                mProgressNum = step;
                                Message message = new Message();
                                message.what = 1;
                                mProgressHandler.sendMessage(message);
                            }
                        } else {
                            String failMsg = "";
                            switch (step){
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
                        switch (upgradeStatus){
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
                        if(4 == upgradeStatus){
                            // 升级成功处理
                            FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) processView.getLayoutParams();
                            processTv.setText("100%");
                            layoutParams.width = processBgView.getWidth();
                            processView.setLayoutParams(layoutParams);
                            // 发送刷新网关固件数据事件
                            RefreshData.refreshGatewayFirmwareData();
                            DialogUtils.showConfirmDialog(mActivity, onSuccessClickListener, hintMsg,getString(R.string.dialog_title));
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

    @OnClick({R.id.upgrade_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.upgrade_btn:
                DialogUtils.showEnsureDialog(mActivity, onClickListener, getString(R.string.upgrading_please_waiting_here),"");
                break;
        }
    }
}
