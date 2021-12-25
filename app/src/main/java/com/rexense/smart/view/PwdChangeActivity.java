package com.rexense.smart.view;

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
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityPwdChangeBinding;
import com.rexense.smart.presenter.AccountManager;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;

public class PwdChangeActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPwdChangeBinding mViewBinding;

    private MyHandler mHandler;

    public static void start(Context context) {
        Intent intent = new Intent(context, PwdChangeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityPwdChangeBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.oldPwdIc.setTypeface(iconfont);
        mViewBinding.newPwdIc.setTypeface(iconfont);
        mViewBinding.oldPwdClearIc.setTypeface(iconfont);
        mViewBinding.newPwdClearIc.setTypeface(iconfont);
        mViewBinding.oldPwdShowIc.setTypeface(iconfont);
        mViewBinding.newPwdShowIc.setTypeface(iconfont);

        mHandler = new MyHandler(this);
    }

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
        mViewBinding.oldPwdClearIc.setOnClickListener(this);
        mViewBinding.oldPwdShowIc.setOnClickListener(this);
        mViewBinding.newPwdClearIc.setOnClickListener(this);
        mViewBinding.newPwdShowIc.setOnClickListener(this);
        mViewBinding.userAgreementTv.setOnClickListener(this);
        mViewBinding.privacyPolicyTv.setOnClickListener(this);
        disableNextBtn();

        mViewBinding.oldPwdEt.addTextChangedListener(mOldPwdTW);
        mViewBinding.newPwdEt.addTextChangedListener(mNewPwdTW);
        mViewBinding.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    private void enableNextBtn() {
        mViewBinding.nextStepBtn.setOnClickListener(this);
        mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(this,
                R.drawable.shape_button_appcolor));
    }

    private void disableNextBtn() {
        mViewBinding.nextStepBtn.setOnClickListener(null);
        mViewBinding.nextStepBtn.setBackground(ContextCompat.getDrawable(this,
                R.drawable.shape_button_all_c));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.topBar.includeDetailImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.oldPwdClearIc.getId()) {
            mViewBinding.oldPwdEt.setText("");
        } else if (v.getId() == mViewBinding.oldPwdShowIc.getId()) {
            if (mViewBinding.oldPwdEt.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mViewBinding.oldPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mViewBinding.oldPwdShowIc.setText(R.string.icon_show);
            } else {
                mViewBinding.oldPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mViewBinding.oldPwdShowIc.setText(R.string.icon_hide);
            }
            mViewBinding.oldPwdEt.setSelection(mViewBinding.oldPwdEt.getText().toString().length());
        } else if (v.getId() == mViewBinding.newPwdClearIc.getId()) {
            mViewBinding.newPwdEt.setText("");
        } else if (v.getId() == mViewBinding.newPwdShowIc.getId()) {
            if (mViewBinding.newPwdEt.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mViewBinding.newPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mViewBinding.newPwdShowIc.setText(R.string.icon_show);
            } else {
                mViewBinding.newPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mViewBinding.newPwdShowIc.setText(R.string.icon_hide);
            }
            mViewBinding.newPwdEt.setSelection(mViewBinding.newPwdEt.getText().toString().length());
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
        } else if (v.getId() == mViewBinding.nextStepBtn.getId()) {
            // 下一步
            // 密码复杂度，长度8-16位，至少含字母和数字，不能包含空格
            String oldPwd = mViewBinding.oldPwdEt.getText().toString();
            String newPwd = mViewBinding.newPwdEt.getText().toString();
            if (oldPwd.length() > 16 || oldPwd.length() < 8) {
                ToastUtils.showLongToast(this, R.string.old_pwd_must_contain_8_to_16_characters);
            } else if (newPwd.length() > 16 || newPwd.length() < 8) {
                ToastUtils.showLongToast(this, R.string.new_pwd_must_contain_8_to_16_characters);
            } else {
                if (isDigitAndLetter(oldPwd) && isDigitAndLetter(newPwd)) {
                    AccountManager.pwdChange(this, oldPwd, newPwd, Constant.MSG_QUEST_PWD_CHANGE,
                            Constant.MSG_QUEST_PWD_CHANGE_ERROR, mHandler);
                } else {
                    ToastUtils.showLongToast(this, R.string.pwd_must_contain_digit_and_letter);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<PwdChangeActivity> ref;

        public MyHandler(PwdChangeActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PwdChangeActivity activity = ref.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_QUEST_PWD_CHANGE: {
                    // 密码修改
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    if (code == 200) {
                        ToastUtils.showLongToast(activity, R.string.modify_success);
                        activity.finish();
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(activity, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_PWD_CHANGE_ERROR: {
                    // 密码修改失败
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e.getMessage());
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
            }
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

    private final TextWatcher mOldPwdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.checkbox.isChecked() && mViewBinding.newPwdEt.getText().toString().length() > 0) {
                enableNextBtn();
            } else if (s.toString().length() == 0 || !mViewBinding.checkbox.isChecked() || mViewBinding.newPwdEt.getText().toString().length() <= 0) {
                disableNextBtn();
            }
            mViewBinding.oldPwdClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    private final TextWatcher mNewPwdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.checkbox.isChecked() && mViewBinding.oldPwdEt.getText().toString().length() > 0) {
                enableNextBtn();
            } else if (s.toString().length() == 0 || !mViewBinding.checkbox.isChecked() || mViewBinding.oldPwdEt.getText().toString().length() <= 0) {
                disableNextBtn();
            }
            mViewBinding.newPwdClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mViewBinding.newPwdEt.getText().toString().length() > 0 && isChecked &&
                    mViewBinding.oldPwdEt.getText().toString().length() > 0) {
                enableNextBtn();
            } else {
                disableNextBtn();
            }
        }
    };
}