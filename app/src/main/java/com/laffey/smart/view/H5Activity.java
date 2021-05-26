package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.just.agentweb.AgentWeb;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityH5Binding;
import com.laffey.smart.utility.Network;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class H5Activity extends BaseActivity {
    private ActivityH5Binding mViewBinding;

    private static final String TAG = "H5Activity";

    private AgentWeb mAgentWeb;

    /**
     * 当前要加载的url
     */
    private String mUrl;

    private final OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            ViseLog.d("重新加载当前url");
            // 重新加载当前url
            mAgentWeb.getUrlLoader().loadUrl(mUrl);
        }
    };

    private final WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // 在RefreshLayout处于刷新状态情况下,判断当前网络状态,
            if (mViewBinding.srlH5.getState() == RefreshState.Refreshing) {
                // 如果网络正常, 则设置刷新成功; 如果网络不可用, 则设置刷新失败;
                if (Network.isNetworkAvailable(H5Activity.this)) {
                    mViewBinding.srlH5.finishRefresh(true);
                } else {
                    mViewBinding.srlH5.finishRefresh(false);
                }
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            ViseLog.d("onReceivedSslError error = " + error.toString());
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            ViseLog.d("onReceivedSslError error = " + errorResponse.toString());
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            ViseLog.d("onReceivedError 1111111111111 = " + error.getDescription().toString() + " , " + error.getErrorCode());
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            ViseLog.d("onReceivedError 2222222222222 = " + description + " , " + failingUrl);
        }
    };

    /**
     * 供要开启H5Activity的界面调用, 并传递参数
     */
    public static void actionStart(Context context, String url, String title) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityH5Binding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initView();
        initData();
        initListener();

        initStatusBar();

        mViewBinding.contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void initView() {
        // 设置自定义Toolbar的左返回按钮
        mViewBinding.toolbarLayout.ivToolbarLeft.setImageResource(R.drawable.back_default);
        mViewBinding.toolbarLayout.ivToolbarLeft.setOnClickListener(this::onClick);
    }

    private void initData() {
        Intent intent = getIntent();

        mUrl = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");

        ViseLog.d(mUrl);

        if (TextUtils.isEmpty(mUrl)) {
            mUrl = "";
        }

        if (!TextUtils.isEmpty(title)) {
            // 显示title
            mViewBinding.toolbarLayout.tvToolbarTitle.setText(title);
        }

        if (Constant.USER_PROTOCOL_URL.equals(mUrl)) {
            mViewBinding.contentTv.setText(getString(R.string.user_protocol_txt));
        } else if (Constant.PRIVACY_POLICY_URL.equals(mUrl)) {
            mViewBinding.contentTv.setText(getString(R.string.privacy_policy_txt));
        } else {
            mViewBinding.srlH5.setVisibility(View.GONE);
            mViewBinding.contentTv.setVisibility(View.VISIBLE);
            // 加载url
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(mViewBinding.flH5Container, new FrameLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setWebViewClient(mWebViewClient)
                    .setMainFrameErrorView(R.layout.layout_h5_error_page, -1)
                    .createAgentWeb()
                    .ready()
                    .go(mUrl);
        }
    }

    private void initListener() {
        mViewBinding.srlH5.setOnRefreshListener(mOnRefreshListener);
    }

    void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_toolbar_left) {
            // 关闭当前界面
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 点击返回键时, 如果网页可以回退, 实现网页回退; 如果网页不能回退, 执行默认操作
        if (mAgentWeb != null && mAgentWeb.getWebLifeCycle() != null) {
            if (!mAgentWeb.back()) {
                super.onBackPressed();
            }
        } else super.onBackPressed();
    }
}
