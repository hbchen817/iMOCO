package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

public class PwdSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPwdSettingBinding mViewBinding;

    private static final String TEL_NUM = "tel_num";
    private static final String VERIFY_CODE = "verify_code";
    private static final String CODE_TYPE = "code_type";

    private String mTelNum;
    private String mVerifyCode;
    // 01：注册02：短信登录03：密码找回
    private String mCodeType;

    public static void start(Activity activity, String telNum, String verifyCode, String codeType, int requestCode) {
        Intent intent = new Intent(activity, PwdSettingActivity.class);
        intent.putExtra(TEL_NUM, telNum);
        intent.putExtra(VERIFY_CODE, verifyCode);
        intent.putExtra(CODE_TYPE, codeType);
        activity.startActivityForResult(intent, requestCode);
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
        mViewBinding.userAgreementTv.setOnClickListener(this);
        mViewBinding.privacyPolicyTv.setOnClickListener(this);
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
            if (!mViewBinding.checkbox.isChecked()) return;
            // 下一步
            // 密码复杂度，长度8-16位，至少含字母和数字，不能包含空格
            String pwd = mViewBinding.pwdEt.getText().toString();
            if (pwd.length() > 16 || pwd.length() < 8) {
                ToastUtils.showLongToast(this, R.string.pwd_must_contain_8_to_16_characters);
            } else {
                if (isDigitAndLetter(pwd)) {
                    // 01:注册账号 03:找回密码
                    if ("01".equals(mCodeType)) {
                        accountsReg();
                    } else if ("03".equals(mCodeType)) {
                        pwdReset(mTelNum, mViewBinding.pwdEt.getText().toString(), mVerifyCode);
                    }
                } else {
                    ToastUtils.showLongToast(this, R.string.pwd_must_contain_digit_and_letter);
                }
            }
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
        }
    }

    // 密码重置（无token）
    private void pwdReset(String telNum, String pwd, String verifyCode) {
        AccountManager.pwdReset(this, telNum, pwd, verifyCode, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 密码重置
                ViseLog.d("密码重置 = " + response.toJSONString());
                int code = response.getInteger("code");
                if (code == 200) {
                    LoginActivity.start(PwdSettingActivity.this, mTelNum);
                } else {
                    String errorCode = response.getString("errorCode");
                    RetrofitUtil.showErrorMsg(PwdSettingActivity.this, response);
                    if ("06".equals(errorCode) ||
                            "02".equals(errorCode)) {
                        // 02：验证码校验失败次数过多！
                        // 06：该手机号未注册!
                        LoginActivity.start(PwdSettingActivity.this, null);
                    } else if ("03".equals(errorCode) ||
                            "04".equals(errorCode) ||
                            "05".equals(errorCode)) {
                        // 03：请先获取验证码!
                        // 04：验证码错误!
                        // 05：验证码错误!
                        setResult(Constant.RESULTCODE_CALLPWDSETTINGACTIVITY);
                        finish();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                // 帐号注册失败
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e.getMessage());
                ToastUtils.showLongToast(PwdSettingActivity.this, e.getMessage());
            }
        });
    }

    // 账号注册
    private void accountsReg() {
        AccountManager.accountsReg(this, mTelNum, mViewBinding.pwdEt.getText().toString(), mVerifyCode,
                new AccountManager.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        // 帐号注册
                        ViseLog.d("帐号注册 = " + response.toJSONString());
                        int code = response.getInteger("code");
                        if (code == 200) {
                            LoginActivity.start(PwdSettingActivity.this, mTelNum);
                        } else {
                            String errorCode = response.getString("errorCode");
                            QMUITipDialogUtil.dismiss();
                            RetrofitUtil.showErrorMsg(PwdSettingActivity.this, response);
                            if ("02".equals(errorCode) ||
                                    "06".equals(errorCode)) {
                                // 02：验证码校验失败次数过多！
                                // 02：该手机号已经注册!
                                LoginActivity.start(PwdSettingActivity.this, null);
                            } else if ("03".equals(errorCode) ||
                                    "04".equals(errorCode) ||
                                    "05".equals(errorCode)) {
                                // 03：请先获取验证码!
                                // 04：验证码已失效，请重新获取!
                                // 05：验证码错误!
                                setResult(Constant.RESULTCODE_CALLPWDSETTINGACTIVITY);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        QMUITipDialogUtil.dismiss();
                        ViseLog.e(e);
                        ToastUtils.showLongToast(PwdSettingActivity.this, e.getMessage());
                    }
                });
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

    @Override
    protected void onStop() {
        super.onStop();
        QMUITipDialogUtil.dismiss();
    }
}