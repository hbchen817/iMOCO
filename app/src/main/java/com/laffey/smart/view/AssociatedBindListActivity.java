package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityAssociatedBindListBinding;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemBinding;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.vise.log.ViseLog;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AssociatedBindListActivity extends BaseActivity {
    private ActivityAssociatedBindListBinding mViewBinding = null;

    private static final String KEY_NAME = "key_name";
    private static final String KEY_VALUE = "key_value";
    private static final String IOT_ID = "iot_id";
    private static final String PRODUCT_KEY = "product_key";
    private static final String RESULT_KEY = "result_key";
    private static final int QUERY_BIND_LIST_TAG = 1000;
    private static final int GET_EXTEND_INFO = 1001;

    private String mKeyName;
    private int mKeyValue;
    private String mIotId;
    private String mGatewayId;
    private String mProductKey;
    private int mResult;

    private Typeface mIconfont;
    private SceneManager mSceneManager;
    private TSLHelper mTSLHelper;

    private MyHandler mHandler;

    // {"Function":"0006","SrcAddr":"CCCCCCFFFE95097D","SrcEndpointId":"3","DstAddrMode":"3","DstAddr":"CCCCCCFFFE95097D","DstEndpointId":"1"}
    private List<ItemBinding> mList = new ArrayList<>();
    private BaseQuickAdapter<ItemBinding, BaseViewHolder> mAdapter;

    public static void start(Context context, String iotId, String productKey, String keyName, int key) {
        Intent intent = new Intent(context, AssociatedBindListActivity.class);
        intent.putExtra(IOT_ID, iotId);// A->
        intent.putExtra(KEY_NAME, keyName);
        intent.putExtra(KEY_VALUE, key);
        intent.putExtra(PRODUCT_KEY, productKey);// A->
        context.startActivity(intent);
    }

    public static void start(Context context, String iotId, String productKey, String keyName, int key, int result) {
        Intent intent = new Intent(context, AssociatedBindListActivity.class);
        intent.putExtra(IOT_ID, iotId);// A->
        intent.putExtra(KEY_NAME, keyName);
        intent.putExtra(KEY_VALUE, key);
        intent.putExtra(RESULT_KEY, result);
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

        new SceneManager(this).getExtendedProperty(mIotId, Constant.TAG_GATEWAY_FOR_DEV, GET_EXTEND_INFO, mCommitFailureHandler, mResponseErrorHandler, mHandler);

        mList.add(new ItemBinding("0006", "CCCCCCFFFE95097D", "1", "3", "CCCCCCFFFE9509A9", "1"));
        mAdapter = new BaseQuickAdapter<ItemBinding, BaseViewHolder>(R.layout.item_associated_bind, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemBinding item) {
                EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(item.getDstAddr());
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(AssociatedBindListActivity.this).load(entry.image).into(imageView);

                holder.setText(R.id.dev_name_tv, entry.nickName);
                holder.getView(R.id.del_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialg(mList.indexOf(item));
                    }
                });
                JSONObject extend = DeviceBuffer.getExtendedInfo(entry.iotId);
                if (extend != null) {
                    switch (item.getDstEndpointId()) {
                        case "1": {
                            holder.setText(R.id.key_name_tv, extend.getString(CTSL.FWS_P_PowerSwitch_1));
                            break;
                        }
                        case "2": {
                            holder.setText(R.id.key_name_tv, extend.getString(CTSL.FWS_P_PowerSwitch_2));
                            break;
                        }
                        case "3": {
                            holder.setText(R.id.key_name_tv, extend.getString(CTSL.FWS_P_PowerSwitch_3));
                            break;
                        }
                        case "4": {
                            holder.setText(R.id.key_name_tv, extend.getString(CTSL.FWS_P_PowerSwitch_4));
                            break;
                        }
                    }
                } else {
                    switch (item.getDstEndpointId()) {
                        case "1": {
                            holder.setText(R.id.key_name_tv, R.string.one_way_powerswitch);
                            break;
                        }
                        case "2": {
                            holder.setText(R.id.key_name_tv, R.string.two_way_powerswitch);
                            break;
                        }
                        case "3": {
                            holder.setText(R.id.key_name_tv, R.string.three_way_powerswitch);
                            break;
                        }
                        case "4": {
                            holder.setText(R.id.key_name_tv, R.string.four_way_powerswitch);
                            break;
                        }
                    }
                }
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                boolean isDel = false;
                for (int i = 0; i < mList.size(); i++) {
                    if (adapter.getViewByPosition(i, R.id.del_tv).getVisibility() == View.VISIBLE) {
                        isDel = true;
                        break;
                    }
                }
                if (isDel) {
                    for (int i = 0; i < mList.size(); i++) {
                        adapter.getViewByPosition(i, R.id.del_tv).setVisibility(View.GONE);
                    }
                } else {
                    ItemBinding item = mList.get(position);
                    EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(item.getDstAddr());
                    SelectAssociatedKeyActivity.start(AssociatedBindListActivity.this, entry.productKey, mIotId, DeviceBuffer.getDeviceInformation(mIotId).deviceName,
                            entry.iotId, Integer.parseInt(item.getDstEndpointId()) - 1, mKeyValue, mProductKey,
                            Integer.parseInt(mList.get(position).getDstEndpointId()) - 1);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                for (int i = 0; i < mList.size(); i++) {
                    adapter.getViewByPosition(i, R.id.del_tv).setVisibility(i == position ? View.VISIBLE : View.GONE);
                }
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBinding.bindingRv.setLayoutManager(layoutManager);
        mViewBinding.bindingRv.setAdapter(mAdapter);
        if (mList.size() > 0) {
            mViewBinding.gridDevNodataView.setVisibility(View.GONE);
            mViewBinding.bindingRv.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.gridDevNodataView.setVisibility(View.VISIBLE);
            mViewBinding.bindingRv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", mHandler);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mResult = getIntent().getIntExtra(RESULT_KEY, -1);
        if (mResult == 0) {
            QMUITipDialogUtil.showSuccessDialog(this, R.string.submit_completed);
        }
    }

    private void showDialg(int pos) {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(AssociatedBindListActivity.this).create();
        alert.setIcon(R.drawable.dialog_quest);
        alert.setTitle(R.string.dialog_title);
        alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_associated_binding));
        //添加否按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //添加是按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                delBinding(pos);
            }
        });
        alert.show();
    }

    private String mFirstIotId;
    private String mSecondIotId;
    private EDevice.deviceEntry mDelDev;
    private ItemBinding mDelItem;

    private void delBinding(int pos) {
        QMUITipDialogUtil.showLoadingDialg(AssociatedBindListActivity.this, R.string.is_submitted);
        mDelItem = mList.get(pos);
        mDelDev = DeviceBuffer.getDevByMac(mDelItem.getDstAddr());

        mFirstIotId = mIotId;
        mSecondIotId = mDelDev.iotId;

        switch (mKeyValue) {
            case 1: {
                mTSLHelper.setProperty(mFirstIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mDelItem.getDstAddr(), mDelItem.getDstEndpointId()});
                break;
            }
            case 2: {
                mTSLHelper.setProperty(mFirstIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mDelItem.getDstAddr(), mDelItem.getDstEndpointId()});
                break;
            }
            case 3: {
                mTSLHelper.setProperty(mFirstIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mDelItem.getDstAddr(), mDelItem.getDstEndpointId()});
                break;
            }
            case 4: {
                mTSLHelper.setProperty(mFirstIotId, mProductKey, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mDelItem.getDstAddr(), mDelItem.getDstEndpointId()});
                break;
            }
        }
    }

    private static final int RESULT = 10000;

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
                switch (msg.what) {
                    case QUERY_BIND_LIST_TAG: {
                        // 查询绑定列表
                        ViseLog.d(new Gson().toJson(msg.obj));
                        break;
                    }
                    case Constant.MSG_CALLBACK_LNEVENTNOTIFY: {
                        break;
                    }
                    case GET_EXTEND_INFO: {
                        activity.mGatewayId = (String) msg.obj;
                        break;
                    }
                    case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY: {
                        // 处理属性通知回调
                        ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                        if (propertyEntry == null) break;
                        if ("U".equals(propertyEntry.getPropertyValue("ResponseType")) &&
                                "0".equals(propertyEntry.getPropertyValue("Result"))) {
                            if (activity.mFirstIotId.equals(propertyEntry.iotId)) {
                                switch (Integer.parseInt(activity.mDelItem.getDstEndpointId())) {
                                    case 1: {
                                        activity.mTSLHelper.setProperty(activity.mDelDev.iotId, activity.mDelDev.productKey, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                new String[]{"U", "0006", "3", activity.mDelItem.getSrcAddr(), activity.mDelItem.getSrcEndpointId()});
                                        break;
                                    }
                                    case 2: {
                                        activity.mTSLHelper.setProperty(activity.mDelDev.iotId, activity.mDelDev.productKey, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                new String[]{"U", "0006", "3", activity.mDelItem.getSrcAddr(), activity.mDelItem.getSrcEndpointId()});
                                        break;
                                    }
                                    case 3: {
                                        activity.mTSLHelper.setProperty(activity.mDelDev.iotId, activity.mDelDev.productKey, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                new String[]{"U", "0006", "3", activity.mDelItem.getSrcAddr(), activity.mDelItem.getSrcEndpointId()});
                                        break;
                                    }
                                    case 4: {
                                        activity.mTSLHelper.setProperty(activity.mDelDev.iotId, activity.mDelDev.productKey, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                new String[]{"U", "0006", "3", activity.mDelItem.getSrcAddr(), activity.mDelItem.getSrcEndpointId()});
                                        break;
                                    }
                                }
                            } else if (activity.mSecondIotId.equals(propertyEntry.iotId)) {
                                QMUITipDialogUtil.showSuccessDialog(activity, R.string.delete_the_success);
                            }
                        }
                        break;
                    }
                    case RESULT: {
                        QMUITipDialogUtil.showSuccessDialog(activity, R.string.delete_the_success);
                        break;
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
    }

    @Override
    protected void onStop() {
        super.onStop();
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
    }
}