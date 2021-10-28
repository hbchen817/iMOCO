package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLoginBinding;
import com.laffey.smart.presenter.AccountManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding mViewBinding;

    private static final String TEL_NUM = "tel_num";

    private Typeface mIconFont;
    private String mTelNum;

    private final List<String> mUserNameList = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> mUserNameAdapter;
    private MyHandler mHandler;

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
    }

    private void initRecyclerView() {
        /*JSONObject o = new JSONObject();
        o.put("17858421203", "");
        o.put("17858421204", "");
        o.put("17858421205", "");
        SpUtils.putUserName(this, o.toJSONString());*/

        JSONObject resutObject = JSONObject.parseObject(SpUtils.getUserName(this));
        if (resutObject != null) {
            for (Map.Entry<String, Object> entry : resutObject.entrySet()) {
                mUserNameList.add(entry.getKey());
            }
        }

        mUserNameAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_user_name, mUserNameList) {
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
        mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.userNameIc.setTypeface(mIconFont);
        mViewBinding.userPwdIc.setTypeface(mIconFont);
        mViewBinding.userNameClearIc.setTypeface(mIconFont);
        mViewBinding.userNameMoreIc.setTypeface(mIconFont);
        mViewBinding.pwdClearIc.setTypeface(mIconFont);
        mViewBinding.pwdShowIc.setTypeface(mIconFont);

        mViewBinding.userNameEt.addTextChangedListener(mUserNameTW);
        mViewBinding.pwdEt.addTextChangedListener(mPwdTW);

        mViewBinding.userNameClearIc.setOnClickListener(this);
        mViewBinding.pwdClearIc.setOnClickListener(this);
        mViewBinding.pwdShowIc.setOnClickListener(this);
        mViewBinding.freeRegistTv.setOnClickListener(this);
        mViewBinding.forgetPwdTv.setOnClickListener(this);
        mViewBinding.loginBtn.setOnClickListener(this);
        mViewBinding.userNameMoreIc.setOnClickListener(this);

        mHandler = new MyHandler(this);
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

    private final TextWatcher mUserNameTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0 && mViewBinding.pwdEt.getText().toString().length() > 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_appcolor));
            } else if (s.toString().length() == 0 || mViewBinding.pwdEt.getText().toString().length() == 0) {
                mViewBinding.loginBtn.setBackground(ContextCompat.getDrawable(LoginActivity.this,
                        R.drawable.shape_button_all_c));
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
            // 免费注册 01：注册02：短信登录03：密码找回
            VerifyCodeActivity.start(this, "01");
        } else if (v.getId() == mViewBinding.forgetPwdTv.getId()) {
            // 忘记密码 01：注册02：短信登录03：密码找回
            VerifyCodeActivity.start(this, "03");
        } else if (v.getId() == mViewBinding.loginBtn.getId()) {
            // 登录
            String telNum = mViewBinding.userNameEt.getText().toString();
            String pwd = mViewBinding.pwdEt.getText().toString();
            if (telNum.length() > 0 && pwd.length() > 0) {
                QMUITipDialogUtil.showLoadingDialg(this, R.string.is_logining);
                AccountManager.authAccountsPwd(telNum, pwd, Constant.MSG_QUEST_AUTH_ACCOUNTS_PWD,
                        Constant.MSG_QUEST_AUTH_ACCOUNTS_PWD_ERROR, mHandler);
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
        }
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
            switch (msg.what) {
                case Constant.MSG_QUEST_GET_AUTH_CODE: {
                    // 获取AuthCode
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("获取AuthCode = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        // 登录成功 authCode
                        String authCode = response.getString("authCode");

                        LoginBusiness.authCodeLogin(authCode, new ILoginCallback() {
                            @Override
                            public void onLoginSuccess() {
                                ToastUtils.showLongToast(activity, "登录成功");

                                JSONObject userName = JSONObject.parseObject(SpUtils.getUserName(activity));
                                SpUtils.putUserName(activity, userName.toJSONString());
                            }

                            @Override
                            public void onLoginFailed(int i, String s) {
                                ViseLog.e("登录失败 = " + s);
                                ToastUtils.showLongToast(activity, s);
                            }
                        });
                    } else {
                        QMUITipDialogUtil.dismiss();
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
                case Constant.MSG_QUEST_AUTH_ACCOUNTS_PWD: {
                    // 帐号密码认证、登录
                    JSONObject response = (JSONObject) msg.obj;
                    ViseLog.d("帐号密码认证、登录 = " + response.toJSONString());
                    int code = response.getInteger("code");
                    if (code == 200) {
                        // 登录成功
                        // ToastUtils.showLongToast(activity, "登录成功");
                        String accessToken = response.getString("accessToken");
                        String refreshToken = response.getString("refreshToken");
                        SpUtils.putAccessToken(activity, accessToken);
                        SpUtils.putRefreshToken(activity, refreshToken);

                        AccountManager.getAuthCode(activity, Constant.MSG_QUEST_GET_AUTH_CODE, Constant.MSG_QUEST_GET_AUTH_CODE_ERROR,
                                activity.mHandler);
                    } else {
                        QMUITipDialogUtil.dismiss();
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
                case Constant.MSG_QUEST_GET_AUTH_CODE_ERROR:// 获取AuthCode失败
                case Constant.MSG_QUEST_AUTH_ACCOUNTS_PWD_ERROR: {
                    // 帐号密码认证、登录失败
                    QMUITipDialogUtil.dismiss();
                    Throwable e = (Throwable) msg.obj;
                    ViseLog.e(e.getMessage());
                    ToastUtils.showLongToast(activity, e.getMessage());
                    break;
                }
            }
        }
    }
}