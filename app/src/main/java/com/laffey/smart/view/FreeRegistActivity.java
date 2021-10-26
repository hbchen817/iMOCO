package com.laffey.smart.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityFreeRegistBinding;
import com.vise.log.ViseLog;

public class FreeRegistActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFreeRegistBinding mViewBinding;

    private Typeface mIconFont;

    public static void start(Context context) {
        Intent intent = new Intent(context, FreeRegistActivity.class);
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
    }

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mViewBinding.phoneNumEt.getText().toString().length() > 0 &&
                    mViewBinding.verificationCodeEt.getText().toString().length() > 0 && isChecked) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
                        R.drawable.shape_button_appcolor));
            } else {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
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
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.phoneNumEt.getText().toString().length() == 0 ||
                    !mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
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
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.verificationCodeEt.getText().toString().length() == 0 ||
                    !mViewBinding.checkbox.isChecked()) {
                mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(FreeRegistActivity.this,
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
            ViseLog.d("发送短信验证码");
        } else if (v.getId() == mViewBinding.verificationCodeClearIc.getId()) {
            mViewBinding.verificationCodeEt.setText("");
        } else if (v.getId() == mViewBinding.nextStepBtn.getId()) {
            // 下一步
            if (mViewBinding.phoneNumEt.getText().toString().length() > 0 &&
                    mViewBinding.verificationCodeEt.getText().toString().length() > 0 &&
                    mViewBinding.checkbox.isChecked()) {
                ViseLog.d("下一步");
            }
        }
    }
}