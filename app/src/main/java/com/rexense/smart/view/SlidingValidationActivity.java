package com.rexense.smart.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.openaccount.util.safe.Base64;
import com.rexense.smart.BuildConfig;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivitySlidingValidationBinding;
import com.rexense.smart.presenter.AccountManager;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.ToastUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.vise.log.ViseLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SlidingValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySlidingValidationBinding mViewBinding;

    private static final String TEL_NUM = "tel_num";
    private static final String CODE_TYPE = "code_type";

    private ResultHandler mHandler;
    private String mFloatImage = null;
    private String mBackgroundImage = null;

    private Bitmap mFloatBitmap;
    private Bitmap mBackgroundBitmap;
    private String mTelNum;
    private String mCodeType;

    public static void start(Activity activity, String telNum, String codeType, int requestCode) {
        Intent intent = new Intent(activity, SlidingValidationActivity.class);
        intent.putExtra(TEL_NUM, telNum);
        intent.putExtra(CODE_TYPE, codeType);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySlidingValidationBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initData();

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading_pic);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appcolor));
        }
        mViewBinding.includeToolbar.includeDetailLblTitle.setText(R.string.security_verification);
        mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.GONE);
        mViewBinding.includeToolbar.includeDetailRl.setBackground(ContextCompat.getDrawable(this, R.color.appcolor));

        mViewBinding.includeToolbar.includeDetailImgBack.setOnClickListener(this);
    }

    private void initData() {
        mHandler = new ResultHandler(this);
        AccountManager.getPVCode(Constant.MSG_QUEST_GET_PV_CODE, Constant.MSG_QUEST_GET_PV_CODE_ERROR, mHandler);
        mViewBinding.verifyViewSb.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mViewBinding.verifyView.setOnClickListener(this);

        mTelNum = getIntent().getStringExtra(TEL_NUM);
        mCodeType = getIntent().getStringExtra(CODE_TYPE);
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mViewBinding.verifyView.setMove(progress * 0.001);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 真实滑动值 = 滑动值 * 图片宽度 / 滑动最大值
            /*ViseLog.d("滑动最大值 = " + mViewBinding.verifyViewSb.getMax()
                    + "\n图片宽度 = " + mBackgroundBitmap.getWidth()
                    + "\n滑动值 = " + mViewBinding.verifyViewSb.getProgress()
                    + "\n小图片宽度 = " + mFloatBitmap.getWidth());*/
            float result = (float) mViewBinding.verifyViewSb.getProgress() * mBackgroundBitmap.getWidth() / mViewBinding.verifyViewSb.getMax();
            result = result - 10;
            // ViseLog.d("真实值 = " + result);
            QMUITipDialogUtil.showLoadingDialg(SlidingValidationActivity.this, R.string.is_security_verification);
            AccountManager.sendSMSVerifyCode(mTelNum, mCodeType, String.valueOf(result),
                    Constant.MSG_QUEST_SEND_SMS_VERIFY_CODE, Constant.MSG_QUEST_SEND_SMS_VERIFY_CODE_ERROR, mHandler);
        }
    };

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.includeDetailImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.verifyView.getId()) {
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading_pic);
            AccountManager.getPVCode(Constant.MSG_QUEST_GET_PV_CODE, Constant.MSG_QUEST_GET_PV_CODE_ERROR, mHandler);
        }
    }

    private static class ResultHandler extends Handler {
        private final WeakReference<SlidingValidationActivity> ref;

        public ResultHandler(SlidingValidationActivity activity) {
            this.ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SlidingValidationActivity activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_SEND_SMS_VERIFY_CODE: {
                    // 短信发送
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("短信发送 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        activity.setResult(2);
                        activity.finish();
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(activity, response);
                        AccountManager.getPVCode(Constant.MSG_QUEST_GET_PV_CODE, Constant.MSG_QUEST_GET_PV_CODE_ERROR, activity.mHandler);
                    }
                    break;
                }
                case Constant.MSG_QUEST_GET_PV_CODE: {
                    // 验证图片获取
                    QMUITipDialogUtil.dismiss();
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    if (code == 200) {
                        activity.mFloatImage = response.getString("floatImage");
                        activity.mBackgroundImage = response.getString("backgroundImage");

                        //ViseLog.d("mBackgroundImage = " + activity.mBackgroundImage);
                        // Log.d("wyylog", activity.mBackgroundImage.length() + "");
                        activity.mFloatImage = activity.mFloatImage.replace("data:image/png;base64,", "");
                        activity.mBackgroundImage = activity.mBackgroundImage.replace("data:image/png;base64,", "");
                        activity.mBackgroundBitmap = activity.base64ToBitmap(activity.mBackgroundImage);
                        activity.mFloatBitmap = activity.base64ToBitmap(activity.mFloatImage);

                        float scaleValue = (float) (QMUIDisplayHelper.getScreenWidth(activity) - 120) / activity.mBackgroundBitmap.getWidth();
                        activity.mViewBinding.verifyView.setWidthAndHeightAndScaleView(QMUIDisplayHelper.getScreenWidth(activity),
                                QMUIDisplayHelper.getScreenHeight(activity), scaleValue);

                        activity.mViewBinding.verifyViewSb.setMax((int) (scaleValue * activity.mBackgroundBitmap.getWidth()));
                        activity.mViewBinding.verifyViewSb.setProgress(0);

                        activity.mViewBinding.verifyView.setDrawBitmap(activity.mBackgroundBitmap);
                        activity.mViewBinding.verifyView.setVerifyBitmap(activity.mFloatBitmap);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_SEND_SMS_VERIFY_CODE_ERROR:
                case Constant.MSG_QUEST_GET_PV_CODE_ERROR: {
                    // 验证图片获取失败
                    QMUITipDialogUtil.dismiss();
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e.getMessage());
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
            }
        }
    }

    private void saveBitmap(Bitmap bitmap) {
        String name = BuildConfig.APPLICATION_ID.replace(".", "_");
        File dirFile = new File(Environment.getExternalStorageDirectory() + "/" + name + "_crash_log/");
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(dirFile, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}