package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLoginBinding;
import com.laffey.smart.utility.SpUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding mViewBinding;

    private Typeface mIconFont;

    private final List<String> mUserNameList = new ArrayList<>();
    private BaseQuickAdapter<String, BaseViewHolder> mUserNameAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
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
        JSONObject o = new JSONObject();
        o.put("17858421203", "");
        o.put("17858421204", "");
        o.put("17858421205", "");
        SpUtils.putUserName(this, o.toJSONString());

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
            // 免费注册
            FreeRegistActivity.start(this);
        } else if (v.getId() == mViewBinding.forgetPwdTv.getId()) {
            // 忘记密码
            ViseLog.d("忘记密码");
        } else if (v.getId() == mViewBinding.loginBtn.getId()) {
            // 登录
            if (mViewBinding.userNameEt.getText().toString().length() > 0 &&
                    mViewBinding.pwdEt.getText().toString().length() > 0) {
                ViseLog.d("登录");
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
}