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
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
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
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.vise.log.ViseLog;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final List<ItemBinding> mList = new ArrayList<>();
    private BaseQuickAdapter<ItemBinding, BaseViewHolder> mAdapter;
    private final Map<String, String> mActionMap = new HashMap<>();
    private final Map<String, String> mPropertyMap = new HashMap<>();
    private final Map<String, String> mKeyNameMap = new HashMap<>();
    private final JSONArray mBindingArray = new JSONArray();

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

    private void initData() {
        mActionMap.put("1", CTSL.FWS_P_ACTION_1);
        mActionMap.put("2", CTSL.FWS_P_ACTION_2);
        mActionMap.put("3", CTSL.FWS_P_ACTION_3);
        mActionMap.put("4", CTSL.FWS_P_ACTION_4);

        mPropertyMap.put("1", CTSL.FWS_P_PowerSwitch_1);
        mPropertyMap.put("2", CTSL.FWS_P_PowerSwitch_2);
        mPropertyMap.put("3", CTSL.FWS_P_PowerSwitch_3);
        mPropertyMap.put("4", CTSL.FWS_P_PowerSwitch_4);

        mKeyNameMap.put("1", getString(R.string.one_way_powerswitch));
        mKeyNameMap.put("2", getString(R.string.two_way_powerswitch));
        mKeyNameMap.put("3", getString(R.string.three_way_powerswitch));
        mKeyNameMap.put("4", getString(R.string.four_way_powerswitch));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAssociatedBindListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        // ViseLog.d("缓存 = " + GsonUtil.toJson(DeviceBuffer.getAllDeviceInformation()));

        mIotId = getIntent().getStringExtra(IOT_ID);
        mKeyName = getIntent().getStringExtra(KEY_NAME);
        mKeyValue = getIntent().getIntExtra(KEY_VALUE, -1);
        mProductKey = getIntent().getStringExtra(PRODUCT_KEY);
        mHandler = new MyHandler(this);

        initStatusBar();
        initData();

        mTSLHelper = new TSLHelper(this);
        queryBindingTable();

        new SceneManager(this).getExtendedProperty(mIotId, Constant.TAG_GATEWAY_FOR_DEV, GET_EXTEND_INFO, mCommitFailureHandler, mResponseErrorHandler, mHandler);

        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemBinding, BaseViewHolder>(R.layout.item_associated_bind, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemBinding item) {
                EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(item.getDstAddr());
                ImageView imageView = holder.getView(R.id.dev_iv);
                TextView delView = holder.getView(R.id.del_tv);
                if (entry != null) {
                    Glide.with(AssociatedBindListActivity.this).load(entry.image).into(imageView);

                    holder.setText(R.id.dev_name_tv, entry.nickName);
                    holder.getView(R.id.del_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialg(mList.indexOf(item));
                        }
                    });
                    delView.setVisibility(View.GONE);
                    JSONObject extend = DeviceBuffer.getExtendedInfo(entry.iotId);
                    if (extend != null) {
                        holder.setText(R.id.key_name_tv, extend.getString(mPropertyMap.get(item.getDstEndpointId())));
                    } else {
                        holder.setText(R.id.key_name_tv, mKeyNameMap.get(item.getDstEndpointId()));
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
                    SelectAssociatedKeyActivity.start(AssociatedBindListActivity.this, entry.productKey, mIotId, DeviceBuffer.getDeviceInformation(mIotId).mac,
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
        refreshNoDataView();
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
            mList.clear();
            mBindingArray.clear();
            // 添加实时数据属性回调处理器
            RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", mHandler);
            queryBindingTable();
        }
    }

    // 查询绑定列表
    private void queryBindingTable() {
        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mTSLHelper.setProperty(mIotId, mProductKey, new String[]{mActionMap.get(String.valueOf(mKeyValue)), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"Q", "xxx", "xxx", "xxx", "xxx"});
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
                mBindingArray.clear();
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

        mTSLHelper.setProperty(mFirstIotId, mProductKey, new String[]{mActionMap.get(String.valueOf(mKeyValue)),
                        CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"U", "0006", "3", mDelItem.getDstAddr(), mDelItem.getDstEndpointId()});
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
                                activity.mTSLHelper.setProperty(activity.mDelDev.iotId, activity.mDelDev.productKey, new String[]{activity.mActionMap.get(activity.mDelItem.getDstEndpointId()), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                        new String[]{"U", "0006", "3", activity.mDelItem.getSrcAddr(), activity.mDelItem.getSrcEndpointId()});
                            } else if (activity.mSecondIotId.equals(propertyEntry.iotId)) {
                                activity.mList.clear();
                                activity.queryBindingTable();
                            }
                        } else if (propertyEntry.getPropertyValue(CTSL.FWS_P_BINDINGTABLE) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_BINDINGTABLE).length() > 0) {
                            String bindingTable = propertyEntry.getPropertyValue(CTSL.FWS_P_BINDINGTABLE);
                            String totalCount = propertyEntry.getPropertyValue(CTSL.TOTAL_COUNT);
                            activity.mBindingArray.addAll(JSONArray.parseArray(bindingTable));
                            // ViseLog.d("totalCount = " + totalCount + " , array = " + activity.mBindingArray.size());
                            if (Integer.parseInt(totalCount) == activity.mBindingArray.size()) {
                                activity.refreshBindingTable(activity.mBindingArray);
                            }
                        }
                        break;
                    }
                    case RESULT: {
                        QMUITipDialogUtil.showSuccessDialog(activity, R.string.delete_the_success);
                        int pos = activity.mList.indexOf(activity.mDelItem);
                        activity.mList.remove(pos);
                        activity.mAdapter.notifyDataSetChanged();
                        activity.refreshNoDataView();
                        break;
                    }
                }
            }
        }
    }

    private void refreshBindingTable(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            ItemBinding item = JSONObject.parseObject(array.get(i).toString(), ItemBinding.class);
            if (String.valueOf(mKeyValue).equals(item.getSrcEndpointId())) {
                EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(item.getDstAddr());
                if (entry != null)
                    mList.add(item);
                else {
                    mFirstIotId = "";
                    mSecondIotId = "";

                    mTSLHelper.setProperty(mIotId, mProductKey, new String[]{mActionMap.get(item.getSrcEndpointId()), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                            new String[]{"U", "0006", "3", item.getDstAddr(), item.getDstEndpointId()});
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        refreshNoDataView();
        QMUITipDialogUtil.dismiss();
    }

    private void refreshNoDataView() {
        if (mList.size() > 0) {
            mViewBinding.gridDevNodataView.setVisibility(View.GONE);
            mViewBinding.bindingRv.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.gridDevNodataView.setVisibility(View.VISIBLE);
            mViewBinding.bindingRv.setVisibility(View.GONE);
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