package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityAddSubGwBinding;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.presenter.ActivityRouter;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.DeviceManager;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AddSubGwActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddSubGwBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";
    private static final String SUB_GATEWAY_MAC = "sub_gateway_mac";

    private String mGwId;
    private String mGwMac;
    private String mSubGwMac;

    public static void start(Activity activity, String gwId, String subGwMac, int requestCode) {
        Intent intent = new Intent(activity, AddSubGwActivity.class);
        intent.putExtra(GATEWAY_ID, gwId);
        intent.putExtra(SUB_GATEWAY_MAC, subGwMac);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAddSubGwBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initData();
    }

    private void initData() {
        mGwId = getIntent().getStringExtra(GATEWAY_ID);
        mGwMac = DeviceBuffer.getDeviceMac(mGwId);
        mSubGwMac = getIntent().getStringExtra(SUB_GATEWAY_MAC);

        mViewBinding.subGwMacTv.setText(mSubGwMac);
        mViewBinding.subGwNameTv.setOnClickListener(this);
        mViewBinding.subGwNameIv.setOnClickListener(this);

        mViewBinding.subGwNameTv.setText(getString(R.string.sub_gateway) + " *" + mSubGwMac.substring(mSubGwMac.length() - 4, mSubGwMac.length()));
        if (mGwMac == null || mGwMac.length() == 0) {
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
            queryMac();
        }
    }

    private void queryMac() {
        List<String> iotList = new ArrayList<>();
        iotList.add(mGwId);
        DeviceManager.queryMacByIotId(this, iotList, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                ViseLog.d("iot - mac = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray iotIdAndMacList = response.getJSONArray("iotIdAndMacList");
                    for (int i = 0; i < iotIdAndMacList.size(); i++) {
                        JSONObject o = iotIdAndMacList.getJSONObject(i);
                        String iotId = o.getString("iotId");
                        String mac = o.getString("mac");
                        if (mGwId.equals(iotId)) {
                            mGwMac = mac;
                        }
                        DeviceBuffer.updateDeviceMac(iotId, mac);
                    }
                    QMUITipDialogUtil.dismiss();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AddSubGwActivity.this, response, Constant.QUERY_MAC_BY_IOTID);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(mActivity, e.getMessage() + ":\n" + Constant.QUERY_MAC_BY_IOTID);
            }
        });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.add_sub_gateway);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.share_device_commit);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.includeToolbar.tvToolbarRight.getId()) {
            // 提交
            submitSubGw(R.string.whether_to_add_sub_gw_info, R.string.dialog_title);
        } else if (v.getId() == mViewBinding.subGwNameTv.getId() ||
                v.getId() == mViewBinding.subGwNameIv.getId()) {
            // 编辑
            showSubGwNameDialogEdit();
        }
    }

    // 提交子网关信息
    private void submitSubGw(int msg, int title) {
        DialogUtils.showConfirmDialog(this, title, msg, R.string.dialog_confirm, R.string.dialog_cancel, new DialogUtils.Callback() {
            @Override
            public void positive() {
                QMUITipDialogUtil.showLoadingDialg(AddSubGwActivity.this, R.string.is_submitted);
                verifySubGwMac();
            }

            @Override
            public void negative() {

            }
        });
    }

    // 判断子网关是否已经被绑定
    private void verifySubGwMac() {
        DeviceManager.verifySubGwMac(this, mSubGwMac, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    String mac = response.getString("mac");
                    if (mac == null || mac.length() == 0) {
                        addSubGw();
                    } else {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(AddSubGwActivity.this, R.string.sub_gw_has_been_binded);
                    }
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AddSubGwActivity.this, response, Constant.VERIFY_SUB_GW_MAC);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AddSubGwActivity.this, e.getMessage());
            }
        });
    }

    // 添加子网关信息
    private void addSubGw() {
        UserCenter.addSubGw(this, mGwMac, mSubGwMac, mViewBinding.subGwNameTv.getText().toString(),
                "0", new UserCenter.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        QMUITipDialogUtil.dismiss();
                        int code = response.getInteger("code");
                        if (code == 200) {
                            ToastUtils.showLongToast(AddSubGwActivity.this, R.string.submit_completed);
                            EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mGwId);
                            DetailGatewayActivity.start(AddSubGwActivity.this, mGwId, entry.productKey,
                                    entry.status, entry.nickName, entry.owned, true);

                            // 通知网关新增子网关白名单
                            JSONObject object = new JSONObject();
                            object.put("Mac", mSubGwMac);
                            object.put("Operate", "A");
                            SceneManager.invokeService(mGwId, "OperateSubGatewayService", object, 1,
                                    mCommitFailureHandler, mResponseErrorHandler, new Handler());

                            finish();
                        } else {
                            RetrofitUtil.showErrorMsg(AddSubGwActivity.this, response, Constant.ADD_SUB_GW);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 添加子网关信息
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        ToastUtils.showLongToast(AddSubGwActivity.this, e.getMessage());

                                /*EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mGwId);
                                DetailGatewayActivity.start(AddSubGwActivity.this, mGwId, entry.productKey,
                                        entry.status, entry.nickName, entry.owned, true);
                                finish();*/
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
                mViewBinding.subGwNameTv.setText(nameStr);
                dialog.dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}