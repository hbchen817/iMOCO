package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.presenter.ActivityRouter;
import com.xiezhu.jzj.presenter.DeviceBuffer;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.presenter.UserCenter;
import com.xiezhu.jzj.utility.ToastUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindSuccessActivity extends BaseActivity {

    private static final String EXTRA_IOT_ID = "EXTRA_IOT_ID";
    private static final String EXTRA_NICKNAME = "EXTRA_NICKNAME";

    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.ok)
    TextView mOkView;

    private String mIotId;
    private UserCenter mUserCenter;
    private String mNewNickName;
    private String mPK;
    private String mNickName;
    private int mOwned = 1;
    private int mStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_success);
        ButterKnife.bind(this);
        mTitle.setText(R.string.bind_result);
        mIotId = getIntent().getStringExtra(EXTRA_IOT_ID);
        mNickName = getIntent().getStringExtra(EXTRA_NICKNAME);
        mUserCenter = new UserCenter(this);
        Log.i("lzm", "nickName" + mNickName);
        if (mNickName.contains(getString(R.string.app_brand)) || mNickName.contains(getString(R.string.app_brand_1))){
            mNickName =  mNickName.replace(getString(R.string.app_brand), getString(R.string.app_brand_show));
            mNickName =  mNickName.replace(getString(R.string.app_brand_1), getString(R.string.app_brand_show_1));
            mUserCenter.setDeviceNickName(mIotId, mNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mOkView.setTypeface(iconfont);
        TSLHelper helper = new TSLHelper(this);
        helper.getBaseInformation(mIotId, null, null,new ProcessDataHandler(this));
        mUserCenter.getByAccountAndDev(mIotId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

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
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    // 设置设备昵称
                    mNewNickName = nameStr;
                    mUserCenter.setDeviceNickName(mIotId, nameStr, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    mNickName = mNewNickName;
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_SETDEVICENICKNAME:
                    Log.i("lzm", "nickname change success "+ (String)msg.obj);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIotId, mNickName);
                    break;
                case Constant.MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV:
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    Log.i("lzm", "MSG_CALLBACK_GET_BY_ACCOUNT_AND_DEV"+ msg.obj);
                    mNickName = jsonObject.getString("productName");
                    if (mNickName.contains(getString(R.string.app_brand))|| mNickName.contains(getString(R.string.app_brand_1))){
                        mNickName =  mNickName.replace(getString(R.string.app_brand), getString(R.string.app_brand_show));
                        mNickName =  mNickName.replace(getString(R.string.app_brand_1), getString(R.string.app_brand_show_1));
                        mUserCenter.setDeviceNickName(mIotId, mNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @OnClick({R.id.iv_toolbar_left, R.id.current_test_btn, R.id.edit_name_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.current_test_btn:
                if (mPK == null) {
                    ToastUtils.showLongToast(BindSuccessActivity.this, R.string.pls_try_again_later);
                } else {
                    ActivityRouter.toDetail(this, mIotId, mPK,
                            mStatus, mNickName, mOwned);
                    finish();
                }
                break;
            case R.id.edit_name_btn:
                showDeviceNameDialogEdit();
                break;
            default:
                break;
        }
    }

    public static void start(Context context, String iotId, String nickName) {
        Intent intent = new Intent(context, BindSuccessActivity.class);
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
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    JSONObject jsonObject = JSONObject.parseObject((String) msg.obj);
                    activity.mPK = jsonObject.getString("productKey");
                    activity.mStatus = jsonObject.getIntValue("status");
                    break;
                default:
                    break;
            }
        }
    }
}
