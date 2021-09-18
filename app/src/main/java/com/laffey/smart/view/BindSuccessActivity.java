package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityBindSuccessBinding;
import com.laffey.smart.presenter.ActivityRouter;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

public class BindSuccessActivity extends BaseActivity {
    private ActivityBindSuccessBinding mViewBinding;

    private static final String EXTRA_IOT_ID = "EXTRA_IOT_ID";
    private static final String EXTRA_NICKNAME = "EXTRA_NICKNAME";
    private static final String GATEWAY_ID = "GATEWAY_ID";

    private String mIotId;
    private String mGatewayId;
    private UserCenter mUserCenter;
    private String mNewNickName;
    private String mPK;
    private String mNickName;
    private int mOwned = 1;
    private int mStatus;

    private String mDeviceName = "";

    private ProcessDataHandler mProcessHandler;
    private TSLHelper mTSLHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityBindSuccessBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.bind_result);
        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mIotId = getIntent().getStringExtra(EXTRA_IOT_ID);
        mNickName = getIntent().getStringExtra(EXTRA_NICKNAME);
        mDeviceName = mNickName;
        mUserCenter = new UserCenter(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.ok.setTypeface(iconfont);
        if (mNickName.contains(getString(R.string.app_brand))) {
            mNickName = mNickName.replace(getString(R.string.app_brand), getString(R.string.app_brand_show));
            mUserCenter.setDeviceNickName(mIotId, mNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else {
            mUserCenter.getByAccountAndDev(mIotId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
        mProcessHandler = new ProcessDataHandler(this);
        mTSLHelper = new TSLHelper(this);
        mTSLHelper.getBaseInformation(mIotId, null, null, mProcessHandler);

        initStatusBar();
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.currentTestBtn.setOnClickListener(this::onViewClicked);
        mViewBinding.editNameBtn.setOnClickListener(this::onViewClicked);

        if (!mIotId.equals(mGatewayId)) {
            // TAG_GATEWAY_FOR_DEV
            new SceneManager(this).setExtendedProperty(mIotId, Constant.TAG_GATEWAY_FOR_DEV, mGatewayId, null, null, null);
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    // 显示设备名称修改对话框
    private void showDeviceNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.moregateway_editname));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(mNickName);
        nameEt.setSelection(mNickName.length());
        final android.app.Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_320);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(v -> {
            String nameStr = nameEt.getText().toString().trim();
            if (!nameStr.equals("")) {
                dialog.dismiss();
                // 设置设备昵称
                mNewNickName = nameStr;
                mUserCenter.setDeviceNickName(mIotId, nameStr, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                mNickName = mNewNickName;
            } else {
                ToastUtils.showShortToast(BindSuccessActivity.this, R.string.dev_name_cannot_be_empty);
            }
        });
        cancelView.setOnClickListener(v -> dialog.dismiss());
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_SETDEVICENICKNAME:
                    ViseLog.d("nickname change success " + (String) msg.obj);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIotId, mNickName);
                    break;
                case Constant.MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV:
                    // 查询用户和设备的关系
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    mNickName = jsonObject.getString("productName");
                    if (mNickName.contains(getString(R.string.app_brand))) {
                        mNickName = mNickName.replace(getString(R.string.app_brand), getString(R.string.app_brand_show));
                        mUserCenter.setDeviceNickName(mIotId, mNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.current_test_btn) {
            if (mPK == null) {
                ToastUtils.showLongToast(this, R.string.pls_try_again_later);
                mTSLHelper.getBaseInformation(mIotId, null, null, mProcessHandler);
            } else {
                ActivityRouter.toDetail(this, mIotId, mPK,
                        mStatus, mNickName, mOwned);
                finish();
            }
        } else if (id == R.id.edit_name_btn) {
            if (mDeviceName.equals(mNickName)) {
                ToastUtils.showLongToast(this, R.string.pls_try_again_later);
                if (mNickName.contains(getString(R.string.app_brand))) {
                    mNickName = mNickName.replace(getString(R.string.app_brand), getString(R.string.app_brand_show));
                    mUserCenter.setDeviceNickName(mIotId, mNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                } else {
                    mUserCenter.getByAccountAndDev(mIotId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            } else {
                showDeviceNameDialogEdit();
            }
        }
    }

    public static void start(Context context, String gatewayId, String iotId, String nickName) {
        Intent intent = new Intent(context, BindSuccessActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(EXTRA_IOT_ID, iotId);
        intent.putExtra(EXTRA_NICKNAME, nickName);
        context.startActivity(intent);
    }

    private static class ProcessDataHandler extends Handler {
        final WeakReference<BindSuccessActivity> mWeakReference;

        public ProcessDataHandler(BindSuccessActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            BindSuccessActivity activity = mWeakReference.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_GETTHINGBASEINFO) {
                JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);
                activity.mPK = jsonObject.getString("productKey");
                activity.mStatus = jsonObject.getIntValue("status");
            }
        }
    }
}
