package com.rexense.imoco.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.presenter.AccountHelper;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.widget.DialogUtils;

import java.util.Hashtable;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TmallSpiritActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.bind_btn)
    TextView bindBtn;
    private String mAuthCode="TAOBAO";
    private int bindFlag=0;//0未绑定1已绑定

    private DialogInterface.OnClickListener unbindClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            unBindTaobao();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmall_spirit);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.tmall_spirit));

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

    private void checkBind(){
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

    private void unBindTaobao(){
        AccountHelper.unbindTaoBaoAccount("TAOBAO", mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETBINDTAOBAOACCOUNT:
                    String resultStr = (String) msg.obj;
                    JSONObject dataJson = JSONObject.parseObject(resultStr);
                    if (dataJson==null){
                        bindFlag = 0;
                        bindBtn.setText(getString(R.string.tmall_spirit_bind));
                    }else {
                        bindFlag = 1;
                        bindBtn.setText(getString(R.string.tmall_spirit_unbind));
                        String accountId = dataJson.getString("accountId");
                        String accountType = dataJson.getString("accountType");
                    }
                    break;
                case Constant.MSG_CALLBACK_BINDTAOBAO:
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.tmall_spirit_bind_success));
                    checkBind();
                    break;
                case Constant.MSG_CALLBACK_UNBINDTAOBAO:
                    ToastUtils.showToastCentrally(mActivity,getString(R.string.tmall_spirit_unbind_success));
                    checkBind();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @OnClick({R.id.bind_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_btn:
                if (bindFlag==1){//解绑
                    DialogUtils.showEnsureDialog(mActivity,unbindClickListener,getString(R.string.tmall_spirit_unbind_ensure),"");
                }else {//绑定
                    Intent intent = new Intent(mActivity,TmallSpiritActivity1.class);
                    startActivityForResult(intent,1);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){
            String AuthCode = data.getStringExtra("AuthCode");
            bindTaobao(AuthCode);
        }
    }
}
