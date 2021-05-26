package com.rexense.wholehouse.view;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.jdsh.sdk.ir.JdshIRInterfaceImpl;
import com.jdsh.sdk.ir.model.Brand;
import com.jdsh.sdk.ir.model.BrandResult;
import com.jdsh.sdk.ir.model.DeviceTypeResult;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityChoiceBrandBinding;
import com.rexense.wholehouse.utility.JDInterfaceImplUtil;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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