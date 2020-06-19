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

            OTAHelper.upgradeFirmware(iotIdList,mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            //定时器模拟升级过程
//            timer.schedule(task,1000,1000);
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
        RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler("UpgradeFirmwareCallback");
    }

    private int num = 0;
    private Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            num = num + new Random().nextInt(10);
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                super.handleMessage(msg);
                int percent = num;//升级进度

                FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) processView.getLayoutParams();
                if (percent<100){
                    processTv.setText(percent+"%");
                    layoutParams.width = percent*processBgView.getWidth()/100;
                    processView.setLayoutParams(layoutParams);
                }else {
                    //TODO 升级完成后的操作
                    processTv.setText("100%");
                    layoutParams.width = processBgView.getWidth();
                    processView.setLayoutParams(layoutParams);
                    timer.cancel();
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.upgrade_success));
                    finish();
                }
            }
        }
    };

    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_UPGRADEFIRMWARE:
//                    RealtimeDataReceiver.addOTACallbackHandler("UpgradeFirmwareCallback", mRealtimeDataHandler);
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
                    //step -1：表示升级失败，-2：表示下载失败，-3：表示校验失败，-4：表示烧写失败 1-100为进度
                    int step = resultJson.getInteger("step");
                    if (step>=0&&step<=100){
                        FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) processView.getLayoutParams();
                        if (step<100){//升级中
                            processTv.setText(step+"%");
                            layoutParams.width = step*processBgView.getWidth()/100;
                            processView.setLayoutParams(layoutParams);
                        }else {//升级完成
                            processTv.setText("100%");
                            layoutParams.width = processBgView.getWidth();
                            processView.setLayoutParams(layoutParams);
                            timer.cancel();
                            ToastUtils.showToastCentrally(mActivity,getString(R.string.upgrade_success));
                            EventBus.getDefault().post(new RefreshFirmwareVersion());
                            finish();
                        }
                    }else {
                        String failMsg = "";
                        switch (step){
                            case -1:
                                failMsg = getString(R.string.upgrade_result_1);
                                break;
                            case -2:
                                failMsg = getString(R.string.upgrade_result_2);
                                break;
                            case -3:
                                failMsg = getString(R.string.upgrade_result_3);
                                break;
                            case -4:
                                failMsg = getString(R.string.upgrade_result_4);
                                break;
                        }
                        DialogUtils.showMsgDialog(mActivity,failMsg);
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
                DialogUtils.showEnsureDialog(mActivity,onClickListener,getString(R.string.upgrading_please_waiting_here),"");
                break;
        }
    }

}
