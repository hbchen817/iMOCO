package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityPwdSettingBinding;
import com.laffey.smart.presenter.AccountManager;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

public class PwdSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPwdSettingBinding mViewBinding;

    private static final String TEL_NUM = "tel_num";
    private static final String VERIFY_CODE = "verify_code";
    private static final String CODE_TYPE = "code_type";

    private String mTelNum;
    private String mVerifyCode;
    // 01：注册02：短信登录03：密码找回
    private String mCodeType;

    private MyHandler mHandler;

    public static void start(Context context, String telNum, String verifyCode, String codeType) {
        Intent intent = new Intent(context, PwdSettingActivity.class);
        intent.putExtra(TEL_NUM, telNum);
        intent.putExtra(VERIFY_CODE, verifyCode);
        intent.putExtra(CODE_TYPE, codeType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityPwdSettingBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        initData();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.userPwdIc.setTypeface(iconfont);
        mViewBinding.pwdShowIc.setTypeface(iconfont);
        mViewBinding.pwdClearIc.setTypeface(iconfont);

        mViewBinding.pwdEt.addTextChangedListener(mPwdTW);
        mViewBinding.pwdClearIc.setOnClickListener(this);
        mViewBinding.pwdShowIc.setOnClickListener(this);
        mViewBinding.nextStepBtn.setOnClickListener(this);
        mViewBinding.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    private void initData() {
        mTelNum = getIntent().getStringExtra(TEL_NUM);
        mVerifyCode = getIntent().getStringExtra(VERIFY_CODE);
        mCodeType = getIntent().getStringExtra(CODE_TYPE);

        mHandler = new MyHandler(this);
    }

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mViewBinding.pwdEt.getText().toString().length() > 0 && isChecked) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(PwdSettingActivity.this,
                        R.drawable.shape_button_appcolor));
            } else {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(PwdSettingActivity.this,
                        R.drawable.shape_button_all_c));
            }
        }
    };

    private final TextWatcher mPwdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(PwdSettingActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || !mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(PwdSettingActivity.this,
                        R.drawable.shape_button_all_c));
            }
            mViewBinding.pwdClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appcolor));
        }

        mViewBinding.topBar.includeDetailLblTitle.setVisibility(View.GONE);
        mViewBinding.topBar.includeDetailImgMore.setVisibility(View.GONE);

        mViewBinding.topBar.includeDetailImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.pwdClearIc.getId()) {
            mViewBinding.pwdEt.setText("");
        } else if (v.getId() == mViewBinding.pwdShowIc.getId()) {
            if (mViewBinding.pwdEt.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mViewBinding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mViewBinding.pwdShowIc.setText(R.string.icon_show);
            } else {
                mViewBinding.pwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mViewBinding.pwdShowIc.setText(R.string.icon_hide);
            }
            mViewBinding.pwdEt.setSelection(mViewBinding.pwdEt.getText().toString().length());
        } else if (v.getId() == mViewBinding.nextStepBtn.getId()) {
            // 下一步
            // 密码复杂度，长度8-16位，至少含字母和数字，不能包含空格
            String pwd = mViewBinding.pwdEt.getText().toString();
            if (pwd.length() > 16 || pwd.length() < 8) {
                ToastUtils.showLongToast(this, R.string.pwd_must_contain_8_to_16_characters);
            } else {
                if (isDigitAndLetter(pwd)) {
                    // 01:注册账号 03:找回密码
                    if ("01".equals(mCodeType)) {
                        AccountManager.accountsReg(mTelNum, mViewBinding.pwdEt.getText().toString(), mVerifyCode,
                                Constant.MSG_QUEST_ACCOUNTS_REG, Constant.MSG_QUEST_ACCOUNTS_REG_ERROR, mHandler);
                    } else if ("03".equals(mCodeType)) {
                        AccountManager.pwdReset(mTelNum, mViewBinding.pwdEt.getText().toString(), mVerifyCode,
                                Constant.MSG_QUEST_PWD_RESET, Constant.MSG_QUEST_PWD_RESET_ERROR, mHandler);
                    }
                } else {
                    ToastUtils.showLongToast(this, R.string.pwd_must_contain_digit_and_letter);
                }
            }
        } else if (v.getId() == mViewBinding.topBar.includeDetailImgBack.getId()) {
            finish();
        }
    }

    // 密码含字母和数字
    private boolean isDigitAndLetter(String pwd) {
        boolean isDigit = false;
        boolean isLetter = false;
        for (int i = 0; i < pwd.length(); i++) {
            if (Character.isDigit(pwd.charAt(i))) {
                isDigit = true;
            } else if (Character.isLetter(pwd.charAt(i))) {
                isLetter = true;
            }
            if (isDigit && isLetter) {
                return true;
            }
        }
        return false;
    }

    private static class MyHandler extends Handler {
        private WeakReference<PwdSettingActivity> ref;

        public MyHandler(PwdSettingActivity activity) {
            this.ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PwdSettingActivity activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_PWD_RESET: {
                    // 密码重置
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("密码重置 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        ViseLog.d("activity.mTelNum = " + activity.mTelNum);
                        LoginActivity.start(activity, activity.mTelNum);
                    } else {
                        String message = response.getString("message");
                        String localizedMsg = response.getString("localizedMsg");
                        String errorMess = response.getString("errorMess");
                        if (message != null && message.length() > 0) {
                            ToastUtils.showLongToast(activity, message);
                        } else if (localizedMsg != null && localizedMsg.length() > 0) {
                            ToastUtils.showLongToast(activity, localizedMsg);
                        } else if (errorMess != null && errorMess.length() > 0) {
                            ToastUtils.showLongToast(activity, errorMess);
                        } else {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        }
                    }
                    break;
                }
                case Constant.MSG_QUEST_ACCOUNTS_REG: {
                    // 帐号注册
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("帐号注册 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        ViseLog.d("activity.mTelNum = " + activity.mTelNum);
                        LoginActivity.start(activity, activity.mTelNum);
                    } else {
                        String message = response.getString("message");
                        String localizedMsg = response.getString("localizedMsg");
                        String errorMess = response.getString("errorMess");
                        if (message != null && message.length() > 0) {
                            ToastUtils.showLongToast(activity, message);
                        } else if (localizedMsg != null && localizedMsg.length() > 0) {
                            ToastUtils.showLongToast(activity, localizedMsg);
                        } else if (errorMess != null && errorMess.length() > 0) {
                            ToastUtils.showLongToast(activity, errorMess);
                        } else {
                            ToastUtils.showLongToast(activity, R.string.pls_try_again_later);
                        }
                    }
                    break;
                }
                case Constant.MSG_QUEST_PWD_RESET_ERROR:// 密码重置失败
                case Constant.MSG_QUEST_ACCOUNTS_REG_ERROR: {
                    // 帐号注册失败
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e.getMessage());
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
            }
        }
    }
}