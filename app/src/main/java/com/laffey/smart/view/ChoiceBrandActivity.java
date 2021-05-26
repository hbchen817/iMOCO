package com.laffey.smart.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityChoiceBrandBinding;
import com.vise.log.ViseLog;

public class ChoiceBrandActivity extends BaseActivity {
    private ActivityChoiceBrandBinding mViewBinding;

    private String mDevTid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceBrandBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.searchIc.setTypeface(iconfont);
        mViewBinding.clearIc.setTypeface(iconfont);

        initStatusBar();
        init();
    }

    private void init() {
        mDevTid = getIntent().getStringExtra("dev_tid");
        mViewBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null || s.toString().length() > 0) {
                    mViewBinding.clearIc.setVisibility(View.VISIBLE);
                } else {
                    mViewBinding.clearIc.setVisibility(View.INVISIBLE);
                }
            }
        });
        mViewBinding.searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ViseLog.d("EditorInfo.IME_ACTION_SEARCH " + v.getText().toString());
            }
            return false;
        });
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.clearIc.setOnClickListener(this::onViewClicked);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.configproduct_title);
    }

    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.clear_ic) {
            mViewBinding.searchEt.setText("");
            mViewBinding.clearIc.setVisibility(View.INVISIBLE);
            hideSoftInput();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}