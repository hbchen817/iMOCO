package com.rexense.smart.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityTmallSpirit1Binding;

public class TmallSpiritActivity1 extends BaseActivity implements View.OnClickListener {
    private ActivityTmallSpirit1Binding mViewBinding;

    private String mAuthCode = "TAOBAO";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTmallSpirit1Binding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.tmall_spirit));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        //支持javascript
        mViewBinding.webView.getSettings().setJavaScriptEnabled(true);
        mViewBinding.webView.setWebViewClient(new WebViewClient() {
            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                mViewBinding.includeToolbar.tvToolbarTitle.setText(view.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isTokenUrl(url)) {
                    Intent intent = new Intent();
                    intent.putExtra("AuthCode", mAuthCode);
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return false;
            }
        });
        String tmallUrl = "https://oauth.taobao.com/authorize?response_type=code&client_id=" +
                Constant.APPKEY +
                "&redirect_uri=" +
                Constant.TAOBAOREDIRECTURI +
                "&view=wap";
        mViewBinding.webView.loadUrl(tmallUrl);

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private boolean isTokenUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("code=")) {
                String[] urlArray = url.split("code=");
                if (urlArray.length > 1) {
                    String[] paramArray = urlArray[1].split("&");
                    if (paramArray.length > 1) {
                        mAuthCode = paramArray[0];
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_toolbar_right) {

        }
    }
}
