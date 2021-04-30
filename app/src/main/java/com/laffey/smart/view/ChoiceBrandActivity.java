package com.laffey.smart.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceBrandActivity extends BaseActivity {
    @BindView(R.id.iv_toolbar_left)
    ImageView mToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.search_ic)
    TextView mSearchIcon;
    @BindView(R.id.clear_ic)
    TextView mClearIcon;
    @BindView(R.id.search_et)
    EditText mSearchET;

    private String mDevTid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_brand);

        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mSearchIcon.setTypeface(iconfont);
        mClearIcon.setTypeface(iconfont);

        initStatusBar();
        init();
    }

    private void init() {
        mDevTid = getIntent().getStringExtra("dev_tid");
        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null || s.toString().length() > 0) {
                    mClearIcon.setVisibility(View.VISIBLE);
                } else {
                    mClearIcon.setVisibility(View.INVISIBLE);
                }
            }
        });
        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ViseLog.d("EditorInfo.IME_ACTION_SEARCH " + v.getText().toString());
                }
                return false;
            }
        });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mToolbarTitle.setText(R.string.configproduct_title);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.clear_ic})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left: {
                finish();
                break;
            }
            case R.id.clear_ic: {
                mSearchET.setText("");
                mClearIcon.setVisibility(View.INVISIBLE);
                hideSoftInput();
                break;
            }
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