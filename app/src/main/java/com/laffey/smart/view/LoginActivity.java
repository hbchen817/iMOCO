package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.openaccount.util.safe.Base64;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLoginBinding;
import com.laffey.smart.presenter.AccountManager;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.VerifyView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding mViewBinding;

    private static final String TEL_NUM = "tel_num";

    private String mTelNum;

    private final List<String> mUserNameList = new ArrayList<>();
    /**
     * 第一次按返回键的时间, 默认为0
     */
    private long mFirstPressTime = 0;

    private int mCountdownTime = 60;
    private MyHandler mHandler;
    private SeekBar mVerifyViewSb;
    private VerifyView mVerifyView;
    private AlertDialog mVerifyViewDialog;
    private Bitmap mBackgroundBitmap;

    public static void start(Context context, String telNum) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(TEL_NUM, telNum);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        initRecyclerView();
        initCache();
        mHandler = new MyHandler(this);
    }

    private void initCache() {
        DeviceBuffer.initSubGw();
    }

    private void initRecyclerView() {
        JSONObject resutObject = JSONObject.parseObject(SpUtils.getUserName(this));
        if (resutObject != null) {
            for (Map.Entry<String, Object> entry : resutObject.entrySet()) {
                mUserNameList.add(entry.getKey());
            }
        }

        BaseQuickAdapter<String, BaseViewHolder> mUserNameAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_user_name, mUserNameList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                String result = s.substring(0, 3) + "****" + s.substring(7, s.length());
                holder.setText(R.id.user_name_tv, result);
            }
        };
        mUserNameAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mViewBinding.userNameEt.setText(mUserNameList.get(position));
                mViewBinding.userNameEt.setSelection(mViewBinding.userNameEt.getText().toString().length());
                mViewBinding.pwdEt.setText("");
                mViewBinding.userNameListLayout.setVisibility(View.GONE);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.userNameRv.setLayoutManager(layoutManager);
        mViewBinding.userNameRv.setAdapter(mUserNameAdapter);
    }

    private void initView() {
        Typeface mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.userNameIc.setTypeface(mIconFont);
        mViewBinding.userPwdIc.setTypeface(mIconFont);
        mViewBinding.userNameClearIc.setTypeface(mIconFont);
        mViewBinding.userNameMoreIc.setTypeface(mIconFont);
        mViewBinding.pwdClearIc.setTypeface(mIconFont);
        mViewBinding.pwdShowIc.setTypeface(mIconFont);
        mViewBinding.verificationCodeIc.setTypeface(mIconFont);
        mViewBinding.loginMethodsIc.setTypeface(mIconFont);
        mViewBinding.verificationCodeClearIc.setTypeface(mIconFont);

        mViewBinding.userNameEt.addTextChangedListener(mUserNameTW);
        mViewBinding.pwdEt.addTextChangedListener(mPwdTW);
        mViewBinding.verificationCodeEt.addTextChangedListener(mVerifyCodeTW);

        mViewBinding.userNameClearIc.setOnClickListener(this);
        mViewBinding.pwdClearIc.setOnClickListener(this);
        mViewBinding.pwdShowIc.setOnClickListener(this);
        mViewBinding.freeRegistTv.setOnClickListener(this);
        mViewBinding.forgetPwdTv.setOnClickListener(this);
        mViewBinding.loginBtn.setOnClickListener(this);
        mViewBinding.userNameMoreIc.setOnClickListener(this);
        mViewBinding.sendVerifiCodeTv.setOnClickListener(this);
        mViewBinding.loginMethodsLayout.setOnClickListener(this);
        mViewBinding.verificationCodeClearIc.setOnClickListener(this);

        showSlidingValidationLayout();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mTelNum = getIntent().getStringExtra(TEL_NUM);
        if (mTelNum != null && mTelNum.length() > 0) {
            mViewBinding.userNameEt.setText(mTelNum);
            ToastUtils.showLongToast(this, R.string.regist_success);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.pwdEt.setText("");
        mViewBinding.verificationCodeEt.setText("");
    }

    private final TextWatcher mUserNameTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mViewBinding.userPwdLayout.getVisibility() == View.VISIBLE) {
                // 账号登录
                if (s.toString().length() > 0 && mViewBinding.pwdEt.getText().toString().length() > 0) {
                    mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                            R.drawable.shape_button_appcolor));
                } else if (s.toString().length() == 0 || mViewBinding.pwdEt.getText().toString().length() == 0) {
                    mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                            R.drawable.shape_button_all_c));
                }
            } else {
                // 短信登录
                if (s.toString().length() > 0 && mViewBinding.verificationCodeEt.getText().toString().length() > 0) {
                    mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                            R.drawable.shape_button_appcolor));
                } else if (s.toString().length() == 0 || mViewBinding.verificationCodeEt.getText().toString().length() == 0) {
                    mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                            R.drawable.shape_button_all_c));
                }
            }
            mViewBinding.userNameClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
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
            if (s.toString().length() > 0 && mViewBinding.userNameEt.getText().toString().length() > 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.userNameEt.getText().toString().length() == 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_all_c));
            }
            mViewBinding.pwdClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    private final TextWatcher mVerifyCodeTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.userNameEt.getText().toString().length() > 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.userNameEt.getText().toString().length() == 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_all_c));
            }
            mViewBinding.verificationCodeClearIc.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
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
        if (v.getId() == mViewBinding.userNameClearIc.getId()) {
            mViewBinding.userNameEt.setText("");
        } else if (v.getId() == mViewBinding.pwdClearIc.getId()) {
            mViewBinding.pwdEt.setText("");
        } else if (v.getId() == mViewBinding.verificationCodeClearIc.getId()) {
            mViewBinding.verificationCodeEt.setText("");
        } else if (v.getId() == mViewBinding.pwdShowIc.getId()) {
            if (mViewBinding.pwdEt.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mViewBinding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mViewBinding.pwdShowIc.setText(R.string.icon_show);
            } else {
                mViewBinding.pwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mViewBinding.pwdShowIc.setText(R.string.icon_hide);
            }
            mViewBinding.pwdEt.setSelection(mViewBinding.pwdEt.getText().toString().length());
        } else if (v.getId() == mViewBinding.freeRegistTv.getId()) {
            // 免费注册 01：注册02：短信登录03：密码找回 04: 修改密码
            VerifyCodeActivity.start(this, "01");
        } else if (v.getId() == mViewBinding.forgetPwdTv.getId()) {
            // 忘记密码 01：注册02：短信登录03：密码找回 04: 修改密码
            VerifyCodeActivity.start(this, "03");
        } else if (v.getId() == mViewBinding.loginBtn.getId()) {
            // 登录
            if (mViewBinding.userPwdLayout.getVisibility() == View.VISIBLE) {
                // 账号登录
                String telNum = mViewBinding.userNameEt.getText().toString();
                String pwd = mViewBinding.pwdEt.getText().toString();
                if (telNum.length() > 0 && pwd.length() > 0) {
                    QMUITipDialogUtil.showLoadingDialg(LoginActivity.this, R.string.is_logining);
                    authAccountsPwd(LoginActivity.this, telNum, pwd);
                }
            } else {
                // 短信登录
                String telNum = mViewBinding.userNameEt.getText().toString();
                String verifyCode = mViewBinding.verificationCodeEt.getText().toString();
                QMUITipDialogUtil.showLoadingDialg(this, R.string.is_logining);
                authAccountsVC(telNum, verifyCode);
            }
        } else if (v.getId() == mViewBinding.userNameMoreIc.getId()) {
            // 登录过的账号
            if (mViewBinding.userNameListLayout.getVisibility() == View.GONE) {
                mViewBinding.userNameListLayout.setVisibility(View.VISIBLE);
                mViewBinding.userNameMoreIc.setText(R.string.icon_up_arrow);
            } else {
                mViewBinding.userNameListLayout.setVisibility(View.GONE);
                mViewBinding.userNameMoreIc.setText(R.string.icon_more);
            }
        } else if (v.getId() == mViewBinding.sendVerifiCodeTv.getId()) {
            // 请求短信验证码
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mViewBinding.sendVerifiCodeTv.getWindowToken(), 0);

            String phoneNum = mViewBinding.userNameEt.getText().toString();
            if (phoneNum.length() > 0) {
                // 短信类型。01：注册02：短信登录03：密码找回 04: 修改密码
                sendSMSVerifyCode(null);
            } else ToastUtils.showLongToast(this, R.string.pls_input_phone_num);
        } else if (v.getId() == mViewBinding.loginMethodsLayout.getId()) {
            // 切换登录方式
            if (mViewBinding.userPwdLayout.getVisibility() == View.VISIBLE) {
                // 切换到短信登录
                mViewBinding.userPwdLayout.setVisibility(View.GONE);
                mViewBinding.verificationCodeLayout.setVisibility(View.VISIBLE);
                mViewBinding.accountManagerLayout.setVisibility(View.GONE);
                mViewBinding.loginMethodsIc.setText(R.string.icon_phone);
                mViewBinding.loginMethodsTv.setText(R.string.pwd_login_methods);
            } else {
                // 切换到密码登录
                mViewBinding.userPwdLayout.setVisibility(View.VISIBLE);
                mViewBinding.verificationCodeLayout.setVisibility(View.GONE);
                mViewBinding.accountManagerLayout.setVisibility(View.VISIBLE);
                mViewBinding.loginMethodsIc.setText(R.string.icon_msg);
                mViewBinding.loginMethodsTv.setText(R.string.msg_login_methods);
            }
        }
    }

    // 短信验证码认证、登录
    private void authAccountsVC(String telNum, String verifyCode) {
        AccountManager.authAccountsVC(this, telNum, verifyCode, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // ViseLog.d("短信验证码登录 = \n" + GsonUtil.toJson(response));
                int code = response.getInteger("code");
                if (code == 200) {
                    // 登录成功
                    String accessToken = response.getString("accessToken");
                    String refreshToken = response.getString("refreshToken");
                    SpUtils.putAccessToken(LoginActivity.this, accessToken);
                    SpUtils.putRefreshToken(LoginActivity.this, refreshToken);
                    SpUtils.putRefreshTokenTime(LoginActivity.this, System.currentTimeMillis());

                    getAuthCode(LoginActivity.this);
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(LoginActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(LoginActivity.this, e.getMessage());
            }
        });
    }

    // 请求登录短信验证码
    private void sendSMSVerifyCode(String pvCode) {
        // 短信类型。01：注册02：短信登录03：密码找回 04: 修改密码
        AccountManager.sendSMSVerifyCode(this, mViewBinding.userNameEt.getText().toString(),
                "02", pvCode, new AccountManager.Callback() {
                    @Override
                    public void onNext(JSONObject response) {
                        int code = response.getInteger("code");
                        ViseLog.d("请求短信验证码 = \n" + GsonUtil.toJson(response));
                        if (code == 200) {
                            QMUITipDialogUtil.dismiss();
                            mVerifyViewDialog.dismiss();

                            mCountdownTime = 60;
                            mViewBinding.sendVerifiCodeTv.setText(mCountdownTime + "秒");
                            mViewBinding.sendVerifiCodeTv.setClickable(false);

                            mHandler.sendEmptyMessageDelayed(0, 1000);
                        } else {
                            QMUITipDialogUtil.dismiss();
                            String resultCode = response.getString("errorCode");
                            switch (resultCode) {
                                case "03": // 请先获取图片验证码！
                                case "04": {
                                    // 图片验证码错误！
                                    QMUITipDialogUtil.showLoadingDialg(LoginActivity.this, R.string.is_loading_pic);
                                    getPVCode();
                                    break;
                                }
                                case "01":// 单日短信发送总量超限制！
                                case "02":// 单日单ip短信发送总量超限制！
                                case "05":// 频繁发送！
                                case "06": {
                                    // 短信发送失败！
                                    RetrofitUtil.showErrorMsg(LoginActivity.this, response);
                                    break;
                                }
                                default: {
                                    RetrofitUtil.showErrorMsg(LoginActivity.this, response);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ViseLog.d(e);
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(LoginActivity.this, e.getMessage());
                    }
                });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> ref;

        public MyHandler(LoginActivity activity) {
            this.ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginActivity activity = ref.get();
            if (activity == null) return;
            if (activity.mCountdownTime > 0) {
                activity.mCountdownTime--;
                activity.mViewBinding.sendVerifiCodeTv.setText(activity.mCountdownTime + "秒");
                activity.mViewBinding.sendVerifiCodeTv.setClickable(false);
                activity.mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                activity.mViewBinding.sendVerifiCodeTv.setText(R.string.send_sms_verification_code);
                activity.mViewBinding.sendVerifiCodeTv.setClickable(true);
            }
        }
    }

    // 获取验证图片
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

                    float scaleValue = (float) (QMUIDisplayHelper.getScreenWidth(LoginActivity.this) - 120) / mBackgroundBitmap.getWidth();
                    mVerifyView.setWidthAndHeightAndScaleView(QMUIDisplayHelper.getScreenWidth(LoginActivity.this),
                            QMUIDisplayHelper.getScreenHeight(LoginActivity.this), scaleValue);

                    mVerifyViewSb.setMax((int) (scaleValue * mBackgroundBitmap.getWidth()));
                    mVerifyViewSb.setProgress(0);

                    mVerifyView.setDrawBitmap(mBackgroundBitmap);
                    mVerifyView.setVerifyBitmap(floatBitmap);
                    mVerifyViewDialog.show();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(LoginActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
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

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mVerifyView.setMove(progress * 0.001);
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
            float result = (float) mVerifyViewSb.getProgress() * mBackgroundBitmap.getWidth() / mVerifyViewSb.getMax();
            result = result - 10;
            ViseLog.d("真实值 = " + result);
            QMUITipDialogUtil.showLoadingDialg(LoginActivity.this, R.string.is_security_verification);
            sendSMSVerifyCode(String.valueOf(result));
        }
    };

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void authAccountsPwd(Activity activity, String accounts, String pwd) {
        AccountManager.authAccountsPwd(activity, accounts, pwd, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                ViseLog.d("帐号密码认证、登录 = " + response.toJSONString());
                int code = response.getInteger("code");
                if (code == 200) {
                    // 登录成功
                    String accessToken = response.getString("accessToken");
                    String refreshToken = response.getString("refreshToken");
                    SpUtils.putAccessToken(activity, accessToken);
                    SpUtils.putRefreshToken(activity, refreshToken);
                    SpUtils.putRefreshTokenTime(activity, System.currentTimeMillis());

                    getAuthCode(activity);
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(activity, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(activity, e.getMessage());
                ViseLog.e(e);
            }
        });
    }

    private void getAuthCode(Activity activity) {
        AccountManager.getAuthCode(activity, new AccountManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 获取AuthCode
                ViseLog.d("获取AuthCode = " + response.toJSONString());
                int code = response.getInteger("code");
                if (code == 200) {
                    // 登录成功 authCode
                    String authCode = response.getString("authCode");
                    LoginBusiness.authCodeLogin(authCode, new ILoginCallback() {
                        @Override
                        public void onLoginSuccess() {
                            QMUITipDialogUtil.dismiss();
                            ToastUtils.showLongToast(activity, "登录成功");

                            try {
                                JSONObject userName = JSONObject.parseObject(SpUtils.getUserName(activity));
                                if (userName == null)
                                    userName = new JSONObject();
                                userName.put(mViewBinding.userNameEt.getText().toString(), "");
                                SpUtils.putUserName(activity, userName.toJSONString());
                            } catch (Exception e) {
                                ViseLog.e(e);
                            }

                            Intent intent = new Intent(activity, IndexActivity.class);
                            activity.startActivity(intent);
                        }

                        @Override
                        public void onLoginFailed(int i, String s) {
                            ViseLog.e("i = " + i + " , s = " + s);
                            QMUITipDialogUtil.dismiss();
                            ToastUtils.showLongToast(activity, s);
                        }
                    });
                } else {
                    QMUITipDialogUtil.dismiss();
                    ViseLog.e(response.toJSONString());
                    RetrofitUtil.showErrorMsg(activity, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(activity, e.getMessage());
                ViseLog.e(e);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 第二次按返回键的时间, 为当前系统时间
        long secondPressTime = System.currentTimeMillis();

        // 设置一个为时2秒的间隔时间
        long intervalTime = 2000;

        if (secondPressTime - mFirstPressTime <= intervalTime) {
            // 如果两次点按返回键的间隔时间小于2秒, 直接退出程序
            // finish();
            // ThreadPool.MainThreadHandler.getInstance().post(() -> Process.killProcess(Process.myPid()), 0);
            System.exit(0);
        } else {
            // 如果两次点按返回键的间隔时间不小于2秒, 弹吐司提示用户
            ToastUtils.showToastCentrally(this, getString(R.string.press_again_to_exit));

            // 将第一次点按返回键的时间置为系统的当前时间
            mFirstPressTime = System.currentTimeMillis();
        }
    }
}