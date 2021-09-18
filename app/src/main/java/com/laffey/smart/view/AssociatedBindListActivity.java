package com.laffey.smart.view;

import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityAssociatedBindListBinding;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.vise.log.ViseLog;

import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;

public class AssociatedBindListActivity extends BaseActivity {
    private ActivityAssociatedBindListBinding mViewBinding = null;

    private static final String KEY_NAME = "key_name";
    private static final String KEY_VALUE = "key_value";
    private static final String IOT_ID = "iot_id";
    private static final String PRODUCT_KEY = "product_key";
    private static final int QUERY_BIND_LIST_TAG = 1000;
    private static final int GET_EXTEND_INFO = 1001;

    private String mKeyName;
    private int mKeyValue;
    private String mIotId;
    private String mGatewayId;
    private String mProductKey;

    private Typeface mIconfont;
    private SceneManager mSceneManager;
    private TSLHelper mTSLHelper;

    private MyHandler mHandler;

    public static void start(Context context, String iotId, String productKey, String keyName, int key) {
        Intent intent = new Intent(context, AssociatedBindListActivity.class);
        intent.putExtra(IOT_ID, iotId);// A->
        intent.putExtra(KEY_NAME, keyName);
        intent.putExtra(KEY_VALUE, key);
        intent.putExtra(PRODUCT_KEY, productKey);// A->
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAssociatedBindListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mIotId = getIntent().getStringExtra(IOT_ID);
        mKeyName = getIntent().getStringExtra(KEY_NAME);
        mKeyValue = getIntent().getIntExtra(KEY_VALUE, -1);
        mProductKey = getIntent().getStringExtra(PRODUCT_KEY);
        mHandler = new MyHandler(this);

        initStatusBar();

        JSONObject two = new JSONObject();
        JSONObject one = new JSONObject();
        one.put("Action", "Q");
        two.put("Content", one);

        /*mSceneManager = new SceneManager(this);
        RealtimeDataReceiver.addEventCallbackHandler("BindingInformationTableNotification", mHandler);*/
        //mSceneManager.invokeService(mIotId, "Binding", two, QUERY_BIND_LIST_TAG, mCommitFailureHandler, mResponseErrorHandler, mHandler);

        mTSLHelper = new TSLHelper(this);
        switch (mKeyValue) {
            case 1: {
                // mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_1}, new String[]{"Q"});
                mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"Q", "xxx", "xxx", "xxx", "xxx"});
                break;
            }
            case 2: {
                // mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_2}, new String[]{"Q"});
                mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"Q", "xxx", "xxx", "xxx", "xxx"});
                break;
            }
            case 3: {
                // mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_3}, new String[]{"Q"});
                mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"Q", "xxx", "xxx", "xxx", "xxx"});
                break;
            }
            case 4: {
                // mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_4}, new String[]{"Q"});
                mTSLHelper.setProperty(mIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"Q", "xxx", "xxx", "xxx", "xxx"});
                break;
            }
        }


        //ViseLog.d(new Gson().toJson(DeviceBuffer.getAllDeviceInformation()));
        new SceneManager(this).getExtendedProperty(mIotId, Constant.TAG_GATEWAY_FOR_DEV, GET_EXTEND_INFO, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> reference;

        public MyHandler(Activity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AssociatedBindListActivity activity = (AssociatedBindListActivity) reference.get();
            if (activity != null) {
                if (msg.what == QUERY_BIND_LIST_TAG) {
                    // 查询绑定列表
                    ViseLog.d(new Gson().toJson(msg.obj));
                } else if (msg.what == Constant.MSG_CALLBACK_LNEVENTNOTIFY) {

                } else if (msg.what == GET_EXTEND_INFO) {
                    activity.mGatewayId = (String) msg.obj;
                }
            }
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(String.format(getString(R.string.bind_list), mKeyName));
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.icon_add_2);
        mViewBinding.includeToolbar.tvToolbarRight.setTextSize(30);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.includeToolbar.tvToolbarRight.setTypeface(mIconfont);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectAssociatedDevActivity.start(AssociatedBindListActivity.this, mIotId, mGatewayId, mKeyValue, mProductKey);
            }
        });
    }
}