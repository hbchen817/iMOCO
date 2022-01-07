package com.laffey.smart.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.openaccount.util.safe.Base64;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityFreeRegistBinding;
import com.laffey.smart.presenter.AccountManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.VerifyView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

public class VerifyCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFreeRegistBinding mViewBinding;

    private static final int COUNT_DOWN_TAG = 200;
    private static final int COUNT_DOWN_TIME_TAG = 60;
    private static final String CODE_TYPE = "code_type";

    private Typeface mIconFont;
    private int mCountdownTime = 60;
    private MyHandler mHandler;
    private SMSReceiver mSMSReceiver;
    private String mCodeType;

    private SeekBar mVerifyViewSb;
    private VerifyView mVerifyView;
    private AlertDialog mVerifyViewDialog;
    private Bitmap mBackgroundBitmap;

    public static void start(Context context, String codeType) {
        Intent intent = new Intent(context, VerifyCodeActivity.class);
        intent.putExtra(CODE_TYPE, codeType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityFreeRegistBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
    }

    private void initView() {
        mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.phoneNumIc.setTypeface(mIconFont);
        mViewBinding.verificationCodeIc.setTypeface(mIconFont);
        mViewBinding.phoneNumClearIc.setTypeface(mIconFont);
        mViewBinding.verificationCodeClearIc.setTypeface(mIconFont);

        mViewBinding.topBar.includeDetailImgMore.setVisibility(View.GONE);
        mViewBinding.topBar.includeDetailLblTitle.setVisibility(View.GONE);

        mViewBinding.phoneNumEt.addTextChangedListener(mPhoneNumTW);
        mViewBinding.verificationCodeEt.addTextChangedListener(mVerifiCodeTW);
        mViewBinding.phoneNumClearIc.setOnClickListener(this);
        mViewBinding.topBar.includeDetailImgBack.setOnClickListener(this);
        mViewBinding.userAgreementTv.setOnClickListener(this);
        mViewBinding.privacyPolicyTv.setOnClickListener(this);
        mViewBinding.sendVerifiCodeTv.setOnClickListener(this);
        mViewBinding.verificationCodeClearIc.setOnClickListener(this);
        mViewBinding.nextStepBtn.setOnClickListener(this);

        mViewBinding.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
        mHandler = new MyHandler(this);

        mSMSReceiver = new SMSReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSReceiver, filter);

        mCodeType = getIntent().getStringExtra(CODE_TYPE);
        showSlidingValidationLayout();
    }

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mViewBinding.phoneNumEt.getText().toString().length() > 0 &&
                    mViewBinding.verificationCodeEt.getText().toString().length() > 0 && isChecked) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_appcolor));
            } else {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_all_c));
            }
        }
    };

    private final TextWatcher mVerifiCodeTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.phoneNumEt.getText().toString().length() > 0 &&
                    mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.phoneNumEt.getText().toString().length() == 0 ||
                    !mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_all_c));
            }
            mViewBinding.verificationCodeClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    private final TextWatcher mPhoneNumTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.verificationCodeEt.getText().toString().length() > 0 &&
                    mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.verificationCodeEt.getText().toString().length() == 0 ||
                    !mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(VerifyCodeActivity.this,
                        R.drawable.shape_button_all_c));
            }
            mViewBinding.phoneNumClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appcolor));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.phoneNumClearIc.getId()) {
            mViewBinding.phoneNumEt.setText("");
        } else if (v.getId() == mViewBinding.topBar.includeDetailImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.userAgreementTv.getId()) {
            // 用户协议
            if (getString(R.string.app_user_deal_url).length() == 0) {
                H5Activity.actionStart(this, Constant.USER_PROTOCOL_URL, getString(R.string.aboutus_user_deal));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_user_deal_url), getString(R.string.aboutus_user_deal));
            }
        } else if (v.getId() == mViewBinding.privacyPolicyTv.getId()) {
            // 隐私政策
            if (getString(R.string.app_privacy_policy_url).length() == 0) {
                H5Activity.actionStart(this, Constant.PRIVACY_POLICY_URL, getString(R.string.aboutus_privacy_policy));
            } else {
                H5Activity.actionStart(this, getString(R.string.app_privacy_policy_url), getString(R.string.aboutus_privacy_policy));
            }
        } else if (v.getId() == mViewBinding.sendVerifiCodeTv.getId()) {
            // 发送短信验证码
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mViewBinding.sendVerifiCodeTv.getWindowToken(), 0);

            String phoneNum = mViewBinding.phoneNumEt.getText().toString();
            if (phoneNum.length() > 0) {
                // 短信类型。01：注册02：短信登录03：密码找回 04: 修改密码
                sendSMSVerifyCode(phoneNum, mCodeType, null);
            } else ToastUtils.showLongToast(this, R.string.pls_input_phone_num);
        } else if (v.getId() == mViewBinding.verificationCodeClearIc.getId()) {
            mViewBinding.verificationCodeEt.setText("");
        } else if (v.getId() == mViewBinding.nextStepBtn.getId()) {
            // 下一步
            if (mViewBinding.phoneNumEt.getText().toString().length() > 0 &&
                    mViewBinding.verificationCodeEt.getText().toString().length() > 0 &&
                    mViewBinding.checkbox.isChecked()) {
                PwdSettingActivity.start(this, mViewBinding.phoneNumEt.getText().toString(),
                        mViewBinding.verificationCodeEt.getText().toString(), mCodeType, Constant.REQUESTCODE_CALLPWDSETTINGACTIVITY);
            }
        }
    }

    // 短信发送
    private void sendSMSVerifyCode(String phoneNum, String codeType, String pvCode) {
        AccountManager.sendSMSVerifyCode(this, phoneNum, codeType, pvCode, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 获取验证码
                QMUITipDialogUtil.dismiss();
                ViseLog.d("验证码请求 = " + response.toJSONString());
                int code = response.getInteger("code");
                String message = response.getString("message");
                if (code == 200) {
                    mVerifyViewDialog.dismiss();

                    if (!"Success!".equals(message)) {
                        ToastUtils.showLongToast(VerifyCodeActivity.this, R.string.frequently_send_and_pls_later);
                    } else {
                        mCountdownTime = COUNT_DOWN_TIME_TAG;
                        mViewBinding.sendVerifiCodeTv.setText(mCountdownTime + "秒");
                        mViewBinding.sendVerifiCodeTv.setClickable(false);

                        Message messageObj = mHandler.obtainMessage();
                        messageObj.what = COUNT_DOWN_TAG;

                        mHandler.sendMessageDelayed(messageObj, 1000);
                    }
                } else {
                    String resultCode = response.getString("errorCode");
                    if (resultCode == null) {
                        mVerifyViewDialog.dismiss();
                        String localizedMsg = response.getString("localizedMsg");
                        if (localizedMsg != null && localizedMsg.length() > 0) {
                            ToastUtils.showLongToast(VerifyCodeActivity.this, localizedMsg);
                        } else if (message != null && message.length() > 0) {
                            ToastUtils.showLongToast(VerifyCodeActivity.this, message);
                        }
                        return;
                    }
                    String resultMess = response.getString("errorMess");
                    switch (resultCode) {
                        case "03": {
                            // 请先获取图片验证码！
                            QMUITipDialogUtil.showLoadingDialg(VerifyCodeActivity.this, R.string.is_loading_pic);
                            getPVCode();
                            break;
                        }
                        case "04": {// 图片验证码错误！
                            getPVCode();
                            break;
                        }
                        case "01":// 单日短信发送总量超限制！
                        case "02":// 单日单ip短信发送总量超限制！
                        case "05": {// 频繁发送！
                            mVerifyViewDialog.dismiss();
                            ToastUtils.showLongToast(VerifyCodeActivity.this, resultMess);
                            LoginActivity.start(VerifyCodeActivity.this, null);
                            break;
                        }
                        case "06": {
                            // 短信发送失败！
                            mVerifyViewDialog.dismiss();
                            ToastUtils.showLongToast(VerifyCodeActivity.this, resultMess);
                            break;
                        }
                        default: {
                            if (message == null || message.length() == 0) {
                                ToastUtils.showLongToast(VerifyCodeActivity.this, R.string.pls_try_again_later);
                            } else ToastUtils.showLongToast(VerifyCodeActivity.this, message);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(VerifyCodeActivity.this, e.getMessage());
            }
        });
    }

    // 滑动图片获取（无token）
    private void getPVCode() {
        AccountManager.getPVCode(this, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 验证图片获取
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                if (code == 200) {
                    String floatImage = response.getString("floatImage");
                    String backgroundImage = response.getString("backgroundImage");

                    floatImage = floatImage.replace("data:image/png;base64,", "");
                    backgroundImage = backgroundImage.replace("data:image/png;base64,", "");
                    mBackgroundBitmap = base64ToBitmap(backgroundImage);
                    Bitmap floatBitmap = base64ToBitmap(floatImage);

                    float scaleValue = (float) (QMUIDisplayHelper.getScreenWidth(VerifyCodeActivity.this) - 120) / mBackgroundBitmap.getWidth();
                    ViseLog.d("QMUIDisplayHelper.getScreenWidth(VerifyCodeActivity.this) - 120 = " + (QMUIDisplayHelper.getScreenWidth(VerifyCodeActivity.this) - 120)
                            + "\nmBackgroundBitmap.getWidth() = " + mBackgroundBitmap.getWidth()
                            + "\nscaleValue = " + scaleValue);
                    mVerifyView.setWidthAndHeightAndScaleView(QMUIDisplayHelper.getScreenWidth(VerifyCodeActivity.this),
                            QMUIDisplayHelper.getScreenHeight(VerifyCodeActivity.this), scaleValue);

                    // mVerifyViewSb.setMax((int) (scaleValue * mBackgroundBitmap.getWidth()));
                    //mVerifyViewSb.setMax(QMUIDisplayHelper.getScreenWidth(VerifyCodeActivity.this) - 120);
                    mVerifyViewSb.setMax(100);
                    mVerifyViewSb.setProgress(0);

                    mVerifyView.setDrawBitmap(mBackgroundBitmap);
                    mVerifyView.setVerifyBitmap(floatBitmap);
                    mVerifyViewDialog.show();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(VerifyCodeActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                // 获取验证码失败
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(VerifyCodeActivity.this, e.getMessage());
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<VerifyCodeActivity> ref;

        public MyHandler(VerifyCodeActivity activity) {
            this.ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VerifyCodeActivity activity = ref.get();
            if (activity == null) return;
            if (msg.what == COUNT_DOWN_TAG) {
                if (activity.mCountdownTime > 0) {
                    activity.mCountdownTime--;
                    activity.mViewBinding.sendVerifiCodeTv.setText(activity.mCountdownTime + "秒");
                    activity.mViewBinding.sendVerifiCodeTv.setClickable(false);

                    Message messageObj = activity.mHandler.obtainMessage();
                    messageObj.what = COUNT_DOWN_TAG;

                    activity.mHandler.sendMessageDelayed(messageObj, 1000);
                } else {
                    activity.mViewBinding.sendVerifiCodeTv.setText(R.string.send_sms_verification_code);
                    activity.mViewBinding.sendVerifiCodeTv.setClickable(true);
                }
            }

        }
    }

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mVerifyView.setMove(progress * 0.01);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 真实滑动值 = 滑动值 * 图片宽度 / 滑动最大值
            // 滑动最大值 = mViewBinding.verifyViewSb.getMax()
            // 图片宽度 = mBackgroundBitmap.getWidth()
            // 滑动值 = mViewBinding.verifyViewSb.getProgress()
            // 小图片宽度 = mFloatBitmap.getWidth()
            // 真实值 = result
            // float result = (float) mVerifyViewSb.getProgress() * mBackgroundBitmap.getWidth() / mVerifyViewSb.getMax();
            float result = (float) mVerifyViewSb.getProgress() * mBackgroundBitmap.getWidth() / mVerifyViewSb.getMax();
            // result = result - 8;
            QMUITipDialogUtil.showLoadingDialg(VerifyCodeActivity.this, R.string.is_security_verification);
            sendSMSVerifyCode(mViewBinding.phoneNumEt.getText().toString(), mCodeType, String.valueOf(result));
        }
    };

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void showSlidingValidationLayout() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sliding_validation, null, false);
        mVerifyViewDialog = new AlertDialog.Builder(this).setView(view).create();

        mVerifyViewSb = (SeekBar) view.findViewById(R.id.verify_view_sb);
        mVerifyView = (VerifyView) view.findViewById(R.id.verify_view);

        mVerifyViewSb.setOnSeekBarChangeListener(mSeekBarChangeListener);

        Window window = mVerifyViewDialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // mVerifyViewDialog.show();
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_white_solid));

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width - 60;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        // window.setLayout(width - 60, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ViseLog.d("requestCode = " + requestCode + " , resultCode = " + resultCode);
        if (requestCode == Constant.REQUEST_SMS_VERIFY_CODE) {
            // 跳转到图片验证码界面（已取消）
            if (resultCode == 2) {
                // 短信已发送
                mCountdownTime = 60;
                mViewBinding.sendVerifiCodeTv.setText(mCountdownTime + "秒");
                mViewBinding.sendVerifiCodeTv.setClickable(false);

                Message message = mHandler.obtainMessage();
                message.what = COUNT_DOWN_TAG;

                mHandler.sendMessageDelayed(message, 1000);
            }
        } else if (requestCode == Constant.REQUESTCODE_CALLPWDSETTINGACTIVITY) {
            // 跳转到设置密码界面
            if (resultCode == Constant.RESULTCODE_CALLPWDSETTINGACTIVITY) {
                mViewBinding.verificationCodeEt.setText("");
            }
        }
    }

    private static class SMSReceiver extends BroadcastReceiver {
        private final VerifyCodeActivity activity;

        public SMSReceiver(VerifyCodeActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 进行获取短信的操作
            ViseLog.d("短信 number =  , body = ");
            getMsg(context, intent);
        }

        private void getMsg(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String number = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                ViseLog.d("短信 number = " + number + " , body = " + body);

                String keyS = "您的验证码为：";
                if (body.contains(keyS)) {
                    String verificationCode = body.substring(body.indexOf(keyS) + keyS.length(), body.indexOf(keyS) + keyS.length() + 6);
                    activity.mViewBinding.verificationCodeEt.setText(verificationCode);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSMSReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        QMUITipDialogUtil.dismiss();
    }
}