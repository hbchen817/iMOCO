package com.rexense.imoco.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityDeviceQrcodeBinding;
import com.rexense.imoco.presenter.ShareDeviceManager;
import com.rexense.imoco.utility.ToastUtils;

import java.util.ArrayList;
import java.util.Hashtable;

public class DeviceQrcodeActivity extends BaseActivity {
    private ActivityDeviceQrcodeBinding mViewBinding;

    private ArrayList<String> iotIdList = new ArrayList<>();
    private ShareDeviceManager shareDeviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityDeviceQrcodeBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.share_device_commit));
        mViewBinding.includeToolbar.tvToolbarRight.setVisibility(View.GONE);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.fragment3_share_device));
        iotIdList = getIntent().getStringArrayListExtra("iotIdList");
        shareDeviceManager = new ShareDeviceManager(mActivity);

        shareDeviceManager.getQrcode(iotIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        initStatusBar();
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onClick);
        mViewBinding.qrcodeTv.setOnClickListener(this::onClick);
        mViewBinding.accountTv.setOnClickListener(this::onClick);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_SHAREQRCODE:
                    String resultStr = (String) msg.obj;
                    if (!TextUtils.isEmpty(resultStr)) {
                        JSONObject jsonObject = JSONObject.parseObject(resultStr);
                        String content = jsonObject.getString("qrKey");
                        int size = 240;
                        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
                        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 字符转码格式设置
                        hints.put(EncodeHintType.ERROR_CORRECTION, "H"); // 容错级别设置 默认为L
                        hints.put(EncodeHintType.MARGIN, "4"); // 空白边距设置
                        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

                        int[] pixels = new int[size * size];
                        for (int y = 0; y < size; y++) {
                            for (int x = 0; x < size; x++) {
                                if (bitMatrix.get(x, y)) { // 黑色色块像素设置
                                    pixels[y * size + x] = -16777216;
                                } else { // 白色色块像素设置
                                    pixels[y * size + x] = -1;
                                }
                            }
                        }
                        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

                        mViewBinding.qrcodeImg.setImageBitmap(bitmap);
                    }
                    break;
                case Constant.MSG_CALLBACK_SHAREDEVICEORSCENE:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.share_device_share_success));
                    finish();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_toolbar_right) {
            String mobileStr = mViewBinding.mobileAccountEt.getText().toString().trim();
            if (TextUtils.isEmpty(mobileStr)) {
                ToastUtils.showToastCentrally(mActivity, mViewBinding.mobileAccountEt.getHint().toString());
                return;
            }
            shareDeviceManager.shareDeviceByMobile(iotIdList, mobileStr, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else if (id == R.id.qrcode_tv) {
            mViewBinding.qrcodeTv.setTextColor(getResources().getColor(R.color.appcolor));
            mViewBinding.accountTv.setTextColor(getResources().getColor(R.color.black));
            mViewBinding.qrcodeView.setVisibility(View.VISIBLE);
            mViewBinding.accountView.setVisibility(View.GONE);
            mViewBinding.includeToolbar.tvToolbarRight.setVisibility(View.GONE);
        } else if (id == R.id.account_tv) {
            mViewBinding.qrcodeTv.setTextColor(getResources().getColor(R.color.black));
            mViewBinding.accountTv.setTextColor(getResources().getColor(R.color.appcolor));
            mViewBinding.qrcodeView.setVisibility(View.GONE);
            mViewBinding.accountView.setVisibility(View.VISIBLE);
            mViewBinding.includeToolbar.tvToolbarRight.setVisibility(View.VISIBLE);
        }
    }

}
