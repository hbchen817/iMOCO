package com.rexense.imoco.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.presenter.ShareDeviceManager;
import com.rexense.imoco.utility.ToastUtils;

import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceQrcodeActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.mobile_account_et)
    EditText mobileAccountEt;
    @BindView(R.id.qrcode_tv)
    TextView qrcodeTv;
    @BindView(R.id.account_tv)
    TextView accountTv;
    @BindView(R.id.qrcode_view)
    LinearLayout qrcodeView;
    @BindView(R.id.account_view)
    LinearLayout accountView;
    @BindView(R.id.qrcode_img)
    ImageView qrcodeImg;

    private ArrayList<String> iotIdList = new ArrayList<>();
    private ShareDeviceManager shareDeviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_qrcode);
        ButterKnife.bind(this);
        tvToolbarRight.setText(getString(R.string.share_device_commit));
        tvToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle.setText(getString(R.string.fragment3_share_device));
        iotIdList = getIntent().getStringArrayListExtra("iotIdList");
        shareDeviceManager = new ShareDeviceManager(mActivity);

        shareDeviceManager.getQrcode(iotIdList, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_SHAREQRCODE:
                    String resultStr = (String) msg.obj;
                    if (!TextUtils.isEmpty(resultStr)) {
                        JSONObject jsonObject = JSONObject.parseObject(resultStr);
                        String qrcodeStr = jsonObject.getString("qrKey");


                        String content = qrcodeStr;
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

                        qrcodeImg.setImageBitmap(bitmap);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @OnClick({R.id.tv_toolbar_right, R.id.qrcode_tv, R.id.account_tv})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                String nickNameStr = mobileAccountEt.getText().toString().trim();
                if (TextUtils.isEmpty(nickNameStr)) {
                    ToastUtils.showToastCentrally(mActivity, mobileAccountEt.getHint().toString());
                    return;
                }
                break;
            case R.id.qrcode_tv:
                qrcodeTv.setTextColor(getResources().getColor(R.color.appcolor));
                accountTv.setTextColor(getResources().getColor(R.color.black));
                qrcodeView.setVisibility(View.VISIBLE);
                accountView.setVisibility(View.GONE);
                tvToolbarRight.setVisibility(View.GONE);
                break;
            case R.id.account_tv:
                qrcodeTv.setTextColor(getResources().getColor(R.color.black));
                accountTv.setTextColor(getResources().getColor(R.color.appcolor));
                qrcodeView.setVisibility(View.GONE);
                accountView.setVisibility(View.VISIBLE);
                tvToolbarRight.setVisibility(View.VISIBLE);
                break;
        }
    }

}
