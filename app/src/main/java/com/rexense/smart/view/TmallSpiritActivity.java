package com.rexense.smart.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityTmallSpiritBinding;
import com.rexense.smart.presenter.AccountHelper;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.widget.DialogUtils;

import androidx.annotation.Nullable;

public class TmallSpiritActivity extends BaseActivity {
    private ActivityTmallSpiritBinding mViewBinding;

    private final String mAuthCode = "TAOBAO";
    private int bindFlag = 0;//0未绑定1已绑定

    private final DialogInterface.OnClickListener unbindClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            unBindTaobao();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTmallSpiritBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.tmall_spirit));

        initStatusBar();
        mViewBinding.bindBtn.setOnClickListener(this::onClick);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void checkBind() {
        AccountHelper.getBindTaoBaoAccount("TAOBAO", mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBind();
    }

    public void bindTaobao(String authCode) {
        AccountHelper.bindTaoBaoAccount(authCode, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private void unBindTaobao() {
        AccountHelper.unbindTaoBaoAccount("TAOBAO", mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETBINDTAOBAOACCOUNT:
                    String resultStr = (String) msg.obj;
                    JSONObject dataJson = JSONObject.parseObject(resultStr);
                    if (dataJson == null) {
                        bindFlag = 0;
                        mViewBinding.bindBtn.setText(getString(R.string.tmall_spirit_bind));
                    } else {
                        bindFlag = 1;
                        mViewBinding.bindBtn.setText(getString(R.string.tmall_spirit_unbind));
                        String accountId = dataJson.getString("accountId");
                        String accountType = dataJson.getString("accountType");
                    }
                    break;
                case Constant.MSG_CALLBACK_BINDTAOBAO:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.tmall_spirit_bind_success));
                    checkBind();
                    break;
                case Constant.MSG_CALLBACK_UNBINDTAOBAO:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.tmall_spirit_unbind_success));
                    checkBind();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    void onClick(View view) {
        if (view.getId() == R.id.bind_btn) {
            if (bindFlag == 1) {//解绑
                DialogUtils.showEnsureDialog(mActivity, unbindClickListener, getString(R.string.tmall_spirit_unbind_ensure), "");
            } else {//绑定
                Intent intent = new Intent(mActivity, TmallSpiritActivity1.class);
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String AuthCode = data.getStringExtra("AuthCode");
            bindTaobao(AuthCode);
        }
    }
}
