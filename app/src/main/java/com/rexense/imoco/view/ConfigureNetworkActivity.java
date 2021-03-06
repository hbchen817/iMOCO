package com.rexense.imoco.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CBLE;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityConfigurenetworkBinding;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EConfigureNetwork;
import com.rexense.imoco.presenter.ConfigureNetwork;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.utility.BLEService;
import com.rexense.imoco.utility.Dialog;
import com.rexense.imoco.utility.Logger;
import com.rexense.imoco.utility.QMUITipDialogUtil;
import com.rexense.imoco.utility.Utility;
import com.rexense.imoco.utility.WiFiHelper;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 网关蓝牙配网
 */
public class ConfigureNetworkActivity extends BaseActivity {
    private ActivityConfigurenetworkBinding mViewBinding;

    private String mProductKey;
    private String mBLEDviceAddress;
    private BLEService mBLEService;
    private boolean mConnectStatus = false;
    private ConfigureNetwork mConfigureNetwork;
    private final int mTimeoutSecond = 120;
    private final int mIntervalMS = 100;
    private boolean mConfigNetworkIsSuccess = false;
    private int mSendBLEDataStatusMachine = Constant.CONFIGNETWORK_SEND_STATUSMACHINE_0;
    private String mDeviceName;
    private String mToken;
    private Thread mTimeThread = null;
    private static boolean mIsDestory;
    private boolean mIsBinding;

    // 蓝牙服务连接
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBLEService = ((BLEService.LocalBinder) service).getService();
            if (!mBLEService.initialize()) {
                Logger.e("Unable to initialize Bluetooth Service!");
                finish();
            }
            // 连接到指定设备
            mBLEService.connect(mBLEDviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBLEService = null;
        }
    };

    // GATT广播接收器
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (CBLE.ACTION_GATT_CONNECTED.equals(action)) {
                // 连接处理
                mConnectStatus = true;
                Logger.d("Received connection action of BLE");
            } else if (CBLE.ACTION_GATT_DISCONNECTED.equals(action)) {
                // 断开处理
                mConnectStatus = false;
                Logger.e("Received disconnection action of BLE");
            } else if (CBLE.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // 服务发现处理
                // 设置特征通知以实现数据接收(凡是要的特征值都要进行通知设置)
                mBLEService.setCharacteristicNotification(CBLE.READ_WRITE_SERVICE_UUID, CBLE.READ_WRITE_CHARACTERISTIC_UUID, true);
            } else if (CBLE.ACTION_DATA_AVAILABLE.equals(action)) {
                // 有效数据处理
                String serviceUUID = intent.getStringExtra(CBLE.EXTRA_SERVICE_UUID);
                String characteristicUUID = intent.getStringExtra(CBLE.EXTRA_CHARACTERISTIC_UUID);
                if (serviceUUID != null && characteristicUUID != null) {
                    if (serviceUUID.equals(CBLE.READ_WRITE_SERVICE_UUID) && characteristicUUID.equals(CBLE.READ_WRITE_CHARACTERISTIC_UUID)) {
                        byte[] data = intent.getByteArrayExtra(CBLE.EXTRA_DATA);
                        if (data != null && data.length > 0) {
                            mConfigureNetwork.parseBLEResponseData(data, mProductKey, prcessBLEResponseDataHandler);
                        }
                    }
                }
            }
        }
    };

    // 处理蓝牙响应数据
    private final Handler prcessBLEResponseDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_PARSE_CONFIGNETWORKFRAME == msg.what) {
                EConfigureNetwork.parseResultEntry resultEntry = (EConfigureNetwork.parseResultEntry) msg.obj;
                Logger.d("Received the response information from BLE device:" + String.format(Locale.getDefault(),
                        "\r\n    cmd: %d\r\n    ack: %d\r\n    data: %s", resultEntry.cmd, resultEntry.ack, resultEntry.content));
                if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_SENDYZ) {
                    // 处理验证
                    mDeviceName = resultEntry.content;
                    // 验证失败处理
                    if (resultEntry.ack != Constant.CONFIGNETWORK_ACK_SUCCESS) {
                        Dialog.confirm(ConfigureNetworkActivity.this, R.string.dialog_title, String.format(getString(R.string.confignetwork_ackyzfail), resultEntry.ack), R.drawable.dialog_fail, R.string.dialog_confirm, false);
                        Message msgProgress = new Message();
                        msgProgress.what = Constant.MSG_CONFIGNETWORK_FAILURE;
                        prcessConfigNetworkProgressHandler.sendMessage(msgProgress);
                    }
                } else if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_SENDSSID) {
                    // 处理SSID确认
                    if (resultEntry.ack == Constant.CONFIGNETWORK_ACK_SUCCESS) {
                        mSendBLEDataStatusMachine = Constant.CONFIGNETWORK_SEND_STATUSMACHINE_1;
                    }
                } else if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_SENDPASSWORD) {
                    // 处理Password确认
                    if (resultEntry.ack == Constant.CONFIGNETWORK_ACK_SUCCESS) {
                        mSendBLEDataStatusMachine = Constant.CONFIGNETWORK_SEND_STATUSMACHINE_2;
                    }
                } else if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_RECEIVEDN) {
                    // 处理DeviceName
                    if (resultEntry.ack == Constant.CONFIGNETWORK_FRAME_NONACK) {
                        mDeviceName = resultEntry.content;
                        bindGatewayDevice();
                    }
                } else if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_RECEIVETOKEN) {
                    // 处理Token
                    if (resultEntry.ack == Constant.CONFIGNETWORK_FRAME_NONACK) {
                        mToken = resultEntry.content;
                        bindGatewayDevice();
                    }
                } else if (resultEntry.cmd == Constant.CONFIGNETWORK_CMD_RECEIVESTATUS) {
                    // 处理网关连网状态
                    if (resultEntry.ack != Constant.CONFIGNETWORK_ACK_SUCCESS) {
                        Message msg1 = new Message();
                        msg1.what = Constant.MSG_CONFIGNETWORK_FAILURE;
                        prcessConfigNetworkProgressHandler.sendMessage(msg1);
                    }
                }
            }
            return false;
        }
    });

    // 处理配网进度
    private final Handler prcessConfigNetworkProgressHandler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CONFIGNETWORK_STEP_START == msg.what) {
                // 开始处理
                mViewBinding.permitJoinLblRemainSecond.setText("0");
                mViewBinding.permitJoinCPProgress.setProgress(0);
            } else if (Constant.MSG_CONFIGNETWORK_REMAIN_SECOND == msg.what) {
                // 剩余秒数处理
                int progress = msg.arg1;
                if (0 == ((progress * mIntervalMS) % 1000)) {
                    int process = (progress * mIntervalMS) / (mTimeoutSecond * 10);
                    mViewBinding.permitJoinLblRemainSecond.setText(process + "");
                    mViewBinding.permitJoinCPProgress.setProgress(process);
                }
            } else if (Constant.MSG_CONFIGNETWORK_STEP_END == msg.what) {
                // 结束处理
                mViewBinding.processLayout.setVisibility(View.GONE);
                mViewBinding.passwordLayout.setVisibility(View.VISIBLE);
                mIsBinding = false;
            } else if (Constant.MSG_CONFIGNETWORK_FAILURE == msg.what) {
                // 失败处理
                mViewBinding.processLayout.setVisibility(View.GONE);
                mViewBinding.passwordLayout.setVisibility(View.VISIBLE);
                mIsBinding = false;
            } else if (Constant.MSG_CONFIGNETWORK_TIMEOUT == msg.what) {
                // 超时处理
                Log.i("lzm", "timeout");
                if (!mIsDestory) {
                    mViewBinding.processLayout.setVisibility(View.GONE);
                    mViewBinding.passwordLayout.setVisibility(View.VISIBLE);
                    mIsBinding = false;
                    Dialog.confirm(ConfigureNetworkActivity.this, R.string.dialog_title, getString(R.string.confignetwork_timeout), R.drawable.dialog_fail, R.string.dialog_confirm, false);
                }
            }
            return false;
        }
    });

    // GATT广播过滤器
    private static IntentFilter mGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CBLE.ACTION_GATT_CONNECTED);
        intentFilter.addAction(CBLE.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(CBLE.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(CBLE.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // 绑定设备回调
    private final Handler mBindDeviceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_BINDEVICE == msg.what) {
                Message message = new Message();
                message.what = Constant.MSG_CONFIGNETWORK_STEP_END;
                prcessConfigNetworkProgressHandler.sendMessage(message);
                mConfigNetworkIsSuccess = true;

                // 发送刷新设备状态事件
                RefreshData.refreshDeviceStateData();

                // 中断计时线程
                if (mTimeThread != null) {
                    if (!mTimeThread.isInterrupted()) {
                        mTimeThread.interrupt();
                    }
                    mTimeThread = null;
                }
                JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                mIotId = jsonObject.getString("iotId");
                QMUITipDialogUtil.showLoadingDialg(ConfigureNetworkActivity.this, R.string.is_loading);
                //Dialog.confirm(ConfigureNetworkActivity.this, R.string.dialog_title, getString(R.string.confignetwork_success), R.drawable.dialog_ok, R.string.dialog_confirm, true);
                new UserCenter(ConfigureNetworkActivity.this).getByAccountAndDev(jsonObject.getString("iotId"),
                        mCommitFailureHandler, mResponseErrorHandler, new ApiDataHandler(ConfigureNetworkActivity.this));
            }
            return false;
        }
    });

    private String mIotId = null;

    @Override
    protected void notifyFailureOrError(int type) {
        super.notifyFailureOrError(type);
        QMUITipDialogUtil.dismiss();
        String devName = getIntent().getStringExtra("name");
        if (mIotId != null && mIotId.length() > 0 && devName != null && devName.length() > 0) {
            BindSuccessActivity.start(this, mIotId, getIntent().getStringExtra("name"));
            finish();
        }
    }

    private static class ApiDataHandler extends Handler {
        private final WeakReference<ConfigureNetworkActivity> ref;

        public ApiDataHandler(ConfigureNetworkActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ConfigureNetworkActivity activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV) {
                QMUITipDialogUtil.dismiss();
                JSONObject object = JSON.parseObject((String) msg.obj);
                BindSuccessActivity.start(activity, object.getString("iotId"), object.getString("productName"));
                activity.finish();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityConfigurenetworkBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mIsDestory = false;
        mViewBinding.includeToolbar.tvToolbarTitle.setText("网关配网");
        mViewBinding.includeToolbar.tvToolbarLeft.setOnClickListener(this::onViewClicked);
        Intent intent = getIntent();
        mBLEDviceAddress = intent.getStringExtra("address");
        mProductKey = intent.getStringExtra("productKey");

        mConfigureNetwork = new ConfigureNetwork(this);

        // 处理关闭键盘
        mViewBinding.configureNetworkLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
            }
        });

        // 注册GATT广播接收器
        registerReceiver(mGattUpdateReceiver, mGattUpdateIntentFilter());

        // 创建并绑定蓝牙服务
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        initProcess();

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

    @Override
    protected void onDestroy() {
        // 卸载GATT广播接收器
        unregisterReceiver(mGattUpdateReceiver);

        //解绑蓝牙连接服务
        unbindService(mServiceConnection);
        mBLEService.close();
        mBLEService = null;

        // 中断计时线程
        if (mTimeThread != null) {
            if (!mTimeThread.isInterrupted()) {
                mTimeThread.interrupt();
            }
            mTimeThread = null;
        }
        prcessConfigNetworkProgressHandler.removeCallbacksAndMessages(null);
        mIsDestory = true;
        super.onDestroy();
    }

    // 关闭键盘
    private void closeKeyboard() {
        final View view = getWindow().peekDecorView();
        if (view != null && view.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // 初始化处理
    private void initProcess() {
        WiFiHelper wiFiHelper = new WiFiHelper(this);
        mViewBinding.configureNetworkTxtSSID.setText(wiFiHelper.getWIFIName());

        // 选择WiFi处理
        OnClickListener choiceOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 选择消息记录内容
                Intent in = new Intent(ConfigureNetworkActivity.this, ChoiceWiFiActivity.class);
                startActivityForResult(in, Constant.REQUESTCODE_CALLCHOICEWIFIACTIVITY);
            }
        };
        mViewBinding.configureNetworkImgChoice.setOnClickListener(choiceOnClickListener);

        // 配置事件处理
        OnClickListener configOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lzm", "click");
                if (!mIsBinding) {
                    closeKeyboard();
                    sendSSIDAndPassword();
                }
            }
        };
        mViewBinding.configNetworkImgConfig.setOnClickListener(configOnClickListener);
        mViewBinding.configureNetworkLblConfig.setOnClickListener(configOnClickListener);

        // 放弃事件处理
        OnClickListener cannelOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 中断计时线程
                if (mTimeThread != null) {
                    if (!mTimeThread.isInterrupted()) {
                        mTimeThread.interrupt();
                        mTimeThread = null;
                    }
                }
                finish();
            }
        };
        mViewBinding.configureNetworkLblCancel.setOnClickListener(cannelOnClickListener);
        mViewBinding.configureNetworkImgCancel.setOnClickListener(cannelOnClickListener);
    }

    // 向蓝牙设备发送路由器SSID与密码
    private void sendSSIDAndPassword() {
        Log.i("lzm", "send 1");
        if (!mConnectStatus) {
            Dialog.confirm(this, R.string.dialog_title, getString(R.string.confignetwork_noconnect), R.drawable.dialog_fail, R.string.dialog_confirm, false);
            mViewBinding.configureNetworkTxtSSID.requestFocus();
            return;
        }
        if (mViewBinding.configureNetworkTxtSSID.getText().toString().equals("")) {
            Dialog.confirm(this, R.string.dialog_title, getString(R.string.confignetwork_ssid), R.drawable.dialog_fail, R.string.dialog_confirm, false);
            mViewBinding.configureNetworkTxtSSID.requestFocus();
            return;
        }
        if (mViewBinding.configureNetworkTxtPwd.getText().toString().equals("")) {
            Dialog.confirm(this, R.string.dialog_title, getString(R.string.confignetwork_pwd), R.drawable.dialog_fail, R.string.dialog_confirm, false);
            mViewBinding.configureNetworkTxtPwd.requestFocus();
            return;
        }
        Log.i("lzm", "send 2");

        mDeviceName = "";
        mToken = "";
        mSendBLEDataStatusMachine = Constant.CONFIGNETWORK_SEND_STATUSMACHINE_0;
        mViewBinding.passwordLayout.setVisibility(View.GONE);
        mViewBinding.processLayout.setVisibility(View.VISIBLE);
        Log.i("lzm", "send 3");

        mIsBinding = true;
        /*try {
            Thread.sleep(600);
        } catch (Exception ex) {
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 创建并运行配网剩余秒数提示线程
                mConfigNetworkIsSuccess = false;
                mTimeThread = new Thread() {
                    @Override
                    public void run() {
                        Message msg1 = new Message();
                        msg1.what = Constant.MSG_CONFIGNETWORK_STEP_START;
                        prcessConfigNetworkProgressHandler.sendMessage(msg1);
                        int count = 1;
                        int retryTime = 0;
                        int retryTimeMax = 3;
                        while (count <= (mTimeoutSecond * 1000 / mIntervalMS) && !this.isInterrupted()) {
                            // 发送SSID
                            if (mSendBLEDataStatusMachine == Constant.CONFIGNETWORK_SEND_STATUSMACHINE_0) {
                                ConfigureNetwork.sendDataToBLE(mBLEService, mViewBinding.configureNetworkTxtSSID.getText().toString(), mProductKey, Constant.CONFIGNETWORK_CMD_SENDSSID);
                                Utility.sleepMilliSecond(mIntervalMS);
                                Message msg2 = new Message();
                                msg2.what = Constant.MSG_CONFIGNETWORK_REMAIN_SECOND;
                                msg2.arg1 = count;
                                prcessConfigNetworkProgressHandler.sendMessage(msg2);
                                Utility.sleepMilliSecond(mIntervalMS);
                                count++;
                                Message msg3 = new Message();
                                msg3.what = Constant.MSG_CONFIGNETWORK_REMAIN_SECOND;
                                msg3.arg1 = count;
                                prcessConfigNetworkProgressHandler.sendMessage(msg3);
                            }

                            // 发送密码
                            if (mSendBLEDataStatusMachine == Constant.CONFIGNETWORK_SEND_STATUSMACHINE_1) {
                                ConfigureNetwork.sendDataToBLE(mBLEService, mViewBinding.configureNetworkTxtPwd.getText().toString(), mProductKey, Constant.CONFIGNETWORK_CMD_SENDPASSWORD);
                                count++;
                                Utility.sleepMilliSecond(mIntervalMS);
                                Message msg4 = new Message();
                                msg4.what = Constant.MSG_CONFIGNETWORK_REMAIN_SECOND;
                                msg4.arg1 = count;
                                prcessConfigNetworkProgressHandler.sendMessage(msg4);
                                count++;
                                Utility.sleepMilliSecond(mIntervalMS);
                                Message msg5 = new Message();
                                msg5.what = Constant.MSG_CONFIGNETWORK_REMAIN_SECOND;
                                msg5.arg1 = count;
                                prcessConfigNetworkProgressHandler.sendMessage(msg5);
                            }

                            if (mConfigNetworkIsSuccess) {
                                break;
                            }

                            count++;
                            Utility.sleepMilliSecond(mIntervalMS);

                            // 发送等待进度
                            Message msg6 = new Message();
                            msg6.what = Constant.MSG_CONFIGNETWORK_REMAIN_SECOND;
                            msg6.arg1 = count;
                            prcessConfigNetworkProgressHandler.sendMessage(msg6);
                        }

                        Log.i("lzm", "send + " + isInterrupted());
                        // 配网超时处理
                        if (!mConfigNetworkIsSuccess && count > (mTimeoutSecond * 1000 / mIntervalMS) && !isInterrupted()) {
                            Message msg3 = new Message();
                            msg3.what = Constant.MSG_CONFIGNETWORK_TIMEOUT;
                            Log.i("lzm", "send");
                            prcessConfigNetworkProgressHandler.sendMessage(msg3);
                        }

                        interrupt();
                    }
                };
                mTimeThread.start();
            }
        }, 600);
    }

    // 绑定网关设备
    private void bindGatewayDevice() {
        Logger.d(String.format("The information of the gateway:\r\n    DeviceName: %s\r\n    Token: %s", mDeviceName, mToken));

        if (mToken == null || mToken.length() == 0 || mDeviceName == null || mDeviceName.length() == 0) {
            return;
        }

        // 等待5秒,以确保服务器端处理Token完成。
        /*try {
            Thread.sleep(5 * 1000);
        } catch (Exception ex) {
        }*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 构造参数
                EConfigureNetwork.bindDeviceParameterEntry parameter = new EConfigureNetwork.bindDeviceParameterEntry();
                parameter.homeId = SystemParameter.getInstance().getHomeId();
                parameter.productKey = mProductKey;
                parameter.deviceName = mDeviceName;
                parameter.token = mToken;

                mConfigureNetwork.bindDevice(parameter, mCommitFailureHandler, mResponseErrorHandler, mBindDeviceHandler);
            }
        }, 5 * 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUESTCODE_CALLCHOICEWIFIACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICEWIFIACTIVITY) {
            mViewBinding.configureNetworkTxtSSID.setText(data.getStringExtra("ssid"));
        }
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_toolbar_left) {
            if (mTimeThread != null) {
                if (!mTimeThread.isInterrupted()) {
                    mTimeThread.interrupt();
                    mTimeThread = null;
                }
            }
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mTimeThread != null) {
            if (!mTimeThread.isInterrupted()) {
                mTimeThread.interrupt();
                mTimeThread = null;
            }
        }
        finish();
    }
}