package com.laffey.smart.view;

import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySubGwInfoBinding;
import com.laffey.smart.event.RefreshData;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.presenter.ActivityRouter;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class SubGwInfoActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySubGwInfoBinding mViewBinding;

    private static final String GW_ID = "gw_id";
    private static final String SUB_GW_INFO = "sub_gw_info";

    private EDevice.subGwEntry mSubGwEntry;
    private final Map<String, String> mStateMap = new HashMap<>();
    private String mGwId;
    private String mGwMac;
    private MyHandler mHandler;

    public static void start(Activity activity, String gwId, String subEntry, int requestCode) {
        Intent intent = new Intent(activity, SubGwInfoActivity.class);
        intent.putExtra(GW_ID, gwId);
        intent.putExtra(SUB_GW_INFO, subEntry);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySubGwInfoBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RealtimeDataReceiver.addEventCallbackHandler("SubGwCallback", mHandler);
    }

    private void initData() {
        mHandler = new MyHandler(this);
        mStateMap.put("0", getString(R.string.connection_status_unable));
        mStateMap.put("1", getString(R.string.activated));

        mSubGwEntry = JSONObject.parseObject(getIntent().getStringExtra(SUB_GW_INFO), EDevice.subGwEntry.class);
        mGwId = getIntent().getStringExtra(GW_ID);
        mGwMac = DeviceBuffer.getDeviceMac(mGwId);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(mSubGwEntry.getNickname());

        mViewBinding.subGwNameTv.setText(mSubGwEntry.getNickname());
        mViewBinding.subGwStateTv.setText(mStateMap.get(mSubGwEntry.getState()));
        mViewBinding.subGwMacTv.setText(mSubGwEntry.getMac());
        mViewBinding.subGwVerTv.setText(mSubGwEntry.getFirmwareVersion());

        if (mSubGwEntry.getActivateTime() == null ||
                mSubGwEntry.getActivateTime().length() == 0) {
            mViewBinding.bindTimeTitleTv.setText(R.string.create_time);
            mViewBinding.subGwBindtimeTv.setText(mSubGwEntry.getCreateTime());
        } else {
            mViewBinding.bindTimeTitleTv.setText(R.string.activate_time);
            mViewBinding.subGwBindtimeTv.setText(mSubGwEntry.getActivateTime());
        }

        mViewBinding.subGwNameTv.setOnClickListener(this);
        mViewBinding.subGwNameIv.setOnClickListener(this);
        mViewBinding.unbindLayout.setOnClickListener(this);

        RealtimeDataReceiver.addEventCallbackHandler("DetailGatewayCallback", new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                    // 处理触发手动场景
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    ViseLog.d("jsonObject = \n" + GsonUtil.toJson(jsonObject));
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if ("SubGatewayStatusNotification".equals(identifier)) {
                        String status = value.getString("Status");
                        String mac = value.getString("Mac");
                        // status  1: 在线  3: 离线
                        synchronized (DetailGatewayActivity.class) {
                            if (mac.equals(mSubGwEntry.getMac())) {
                                if ("1".equals(status)) {
                                    mViewBinding.subGwStateTv.setText(R.string.connection_status_online);
                                } else if ("3".equals(status)) {
                                    mViewBinding.subGwStateTv.setText(R.string.connection_status_offline);
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }));
        SceneManager.querySubGatewayStatusService(this, mGwId,
                mSubGwEntry.getMac(), null);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.subGwNameTv.getId() ||
                v.getId() == mViewBinding.subGwNameIv.getId()) {
            // 修改昵称
            showSubGwNameDialogEdit();
        } else if (v.getId() == mViewBinding.unbindLayout.getId()) {
            // 解绑子网关
            DialogUtils.showConfirmDialog(this, R.string.dialog_title, R.string.whether_to_del_sub_gw,
                    R.string.dialog_confirm, R.string.dialog_cancel, new DialogUtils.Callback() {
                        @Override
                        public void positive() {
                            JSONObject object = new JSONObject();
                            object.put("Mac", mSubGwEntry.getMac());
                            object.put("Operate", "D");
                            deleteSubGw();
                        }

                        @Override
                        public void negative() {

                        }
                    });
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SubGwInfoActivity> ref;

        public MyHandler(SubGwInfoActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SubGwInfoActivity activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {
                JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                JSONObject value = jsonObject.getJSONObject("value");
                String identifier = jsonObject.getString("identifier");
                if ("OperateSubGatewayNotification".equals(identifier)) {
                    String operate = value.getString("Operate");
                    String status = value.getString("Status");
                    // status  0: 成功  1: 失败
                    if ("0".equals(status)) {
                        // operate  A: 添加  D: 删除
                        if ("D".equals(operate)) {
                            activity.deleteSubGw();
                        }
                    } else {
                        ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                    }
                }
            }
        }
    }

    private void deleteSubGw() {
        UserCenter.deleteSubGw(this, mGwMac, mSubGwEntry.getMac(), new UserCenter.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                if (code == 200) {
                    ToastUtils.showLongToast(SubGwInfoActivity.this, R.string.delete_the_success);
                    EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mGwId);
                    DeviceBuffer.removeSubGw(mSubGwEntry.getMac());

                    JSONObject object = new JSONObject();
                    object.put("Mac", mSubGwEntry.getMac());
                    object.put("Operate", "D");
                    SceneManager.invokeService(mGwId, "OperateSubGatewayService", object, 1,
                            mCommitFailureHandler, mResponseErrorHandler, new Handler());

                    ActivityRouter.toDetail(SubGwInfoActivity.this, mGwId, entry.productKey,
                            entry.status, entry.nickName, entry.owned);
                } else {
                    RetrofitUtil.showErrorMsg(SubGwInfoActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(SubGwInfoActivity.this, e.getMessage());
            }
        });
    }

    // 显示场景名称修改对话框
    private void showSubGwNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.sub_gw_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);

        String name = mViewBinding.subGwNameTv.getText().toString();
        if (name.length() > 0) {
            nameEt.setText(name);
            nameEt.setSelection(name.length());
        } else {
            nameEt.setText("");
        }

        nameEt.setHint(getString(R.string.pls_input_sub_gw_name));
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_320);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEt.getText().toString().trim();
                dialog.dismiss();
                QMUITipDialogUtil.showLoadingDialg(SubGwInfoActivity.this, R.string.is_submitted);
                updateSubGw(nameStr);
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void updateSubGw(String name) {
        UserCenter.updateSubGw(this, mGwMac, mSubGwEntry.getMac(),
                name, mSubGwEntry.getPosition(), new UserCenter.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        // ViseLog.d("修改昵称 = \n" + GsonUtil.toJson(response));
                        if (code == 200) {
                            mViewBinding.subGwNameTv.setText(name);
                            EDevice.subGwEntry subGwEntry = DeviceBuffer.getSubGw(mSubGwEntry.getMac());
                            if (subGwEntry != null) {
                                subGwEntry.setNickname(name);
                                DeviceBuffer.addSubGw(mSubGwEntry.getMac(), subGwEntry);
                                RefreshData.refreshDeviceStateDataFromBuffer();
                            }
                            ToastUtils.showLongToast(SubGwInfoActivity.this, R.string.modify_success);
                        } else {
                            RetrofitUtil.showErrorMsg(SubGwInfoActivity.this, response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ViseLog.e(e);
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(SubGwInfoActivity.this, e.getMessage());
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler("SubGwCallback");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler("DetailGatewayCallback");
    }
}