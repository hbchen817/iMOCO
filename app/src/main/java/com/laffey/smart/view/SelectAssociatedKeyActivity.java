package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySelectAssociatedKeyBinding;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SelectAssociatedKeyActivity extends BaseActivity {
    private ActivitySelectAssociatedKeyBinding mViewBinding;

    private static final String PRODUCT_KEY = "product_key";
    private static final String IOT_ID = "iot_id";
    private static final String A_IOT_ID = "a_iot_id";
    private static final String SELECT_POS = "select_pos";
    private static final String A_MAC = "a_mac";
    private static final String SRC_ENDPOINT_ID = "src_endpoint_id";
    private static final String SRC_PRODUCT_KEY = "src_product_key";
    private static final String EDIT_POSITION = "edit_pos";
    private static final String GW_ID = "gw_id";

    private String mPk;
    private String mIotId;
    private String mAIotId;
    private int mSelectPos;
    private String mAMac;
    private String mBMac;
    private int mSrcEndId;
    private String mSrcPK;
    private int mEditPos;
    private String mGwId;

    private String mFirstIotId;
    private String mSecondIotId;
    private String mFirstMac;
    private String mSecondMac;

    private Typeface mIconFace;
    private List<KeyItem> mList = new ArrayList<>();
    private BaseQuickAdapter<KeyItem, BaseViewHolder> mAdapter;
    private TSLHelper mTSLHelper;

    private MyHandler mHandler;

    public static void start(Context context, String pk, String aIotId, String aMac, String iotId, int pos, int srcEndId, String srcPK, String gwId) {
        Intent intent = new Intent(context, SelectAssociatedKeyActivity.class);
        intent.putExtra(PRODUCT_KEY, pk);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(A_IOT_ID, aIotId);
        intent.putExtra(SELECT_POS, pos);
        intent.putExtra(A_MAC, aMac);
        intent.putExtra(SRC_ENDPOINT_ID, srcEndId);
        intent.putExtra(SRC_PRODUCT_KEY, srcPK);
        intent.putExtra(GW_ID, gwId);
        context.startActivity(intent);
    }

    public static void start(Context context, String pk, String aIotId, String aMac, String iotId, int pos, int srcEndId, String srcPK, String gwId, int editPos) {
        Intent intent = new Intent(context, SelectAssociatedKeyActivity.class);
        intent.putExtra(PRODUCT_KEY, pk);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(A_IOT_ID, aIotId);
        intent.putExtra(SELECT_POS, pos);
        intent.putExtra(A_MAC, aMac);
        intent.putExtra(SRC_ENDPOINT_ID, srcEndId);
        intent.putExtra(SRC_PRODUCT_KEY, srcPK);
        intent.putExtra(EDIT_POSITION, editPos);
        intent.putExtra(GW_ID, gwId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySelectAssociatedKeyBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mPk = getIntent().getStringExtra(PRODUCT_KEY);
        mIotId = getIntent().getStringExtra(IOT_ID);
        mAIotId = getIntent().getStringExtra(A_IOT_ID);
        mSelectPos = getIntent().getIntExtra(SELECT_POS, 0);
        mAMac = getIntent().getStringExtra(A_MAC);
        mSrcEndId = getIntent().getIntExtra(SRC_ENDPOINT_ID, 0);
        mSrcPK = getIntent().getStringExtra(SRC_PRODUCT_KEY);
        mEditPos = getIntent().getIntExtra(EDIT_POSITION, -1);
        mGwId = getIntent().getStringExtra(GW_ID);
        mTSLHelper = new TSLHelper(this);
        mIconFace = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);

        mHandler = new MyHandler(this);
        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", mHandler);

        initStatusBar();

        EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mIotId);
        mBMac = entry.mac;

        mAdapter = new BaseQuickAdapter<KeyItem, BaseViewHolder>(R.layout.item_identifier, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, KeyItem item) {
                holder.setText(R.id.name_tv, item.keyName)
                        .setBackgroundColor(R.id.root_layout, ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.white));
                TextView goTV = holder.getView(R.id.go_iv);
                goTV.setText(R.string.icon_checked);
                goTV.setTypeface(mIconFace);
                goTV.setTextColor(ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.appcolor));
                goTV.setTextSize(getResources().getDimension(R.dimen.sp_8));
                goTV.setVisibility(mSelectPos == mList.indexOf(item) ? View.VISIBLE : View.GONE);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mSelectPos = position;
                mAdapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.keyRv.setLayoutManager(layoutManager);
        mViewBinding.keyRv.setAdapter(mAdapter);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        initData();
    }

    private void initData() {
        JSONObject object = DeviceBuffer.getExtendedInfo(mIotId);
        switch (mPk) {
            case CTSL.PK_ONEWAYSWITCH: {
                // 一键
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.OWS_P_PowerSwitch_1), 1);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                }
                mList.add(item);
                mAdapter.notifyDataSetChanged();
                break;
            }
            case CTSL.PK_TWOWAYSWITCH: {
                // 二键
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.TWS_P_PowerSwitch_1), 1);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P_PowerSwitch_2), 2);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    mList.add(item);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
            case CTSL.PK_THREE_KEY_SWITCH: {
                // 三键
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.TWS_P3_PowerSwitch_1), 1);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P3_PowerSwitch_2), 2);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P3_PowerSwitch_3), 3);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    mList.add(item);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
            case CTSL.PK_FOURWAYSWITCH_2: {
                // 四键
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_1), 1);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_2), 2);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_3), 3);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_4), 4);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.four_way_powerswitch), 4);
                    mList.add(item);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
            case CTSL.PK_SIX_TWO_SCENE_SWITCH: {
                // 六键四开关二场景
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_1), 1);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2), 2);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3), 3);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4), 4);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.four_way_powerswitch), 4);
                    mList.add(item);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        mViewBinding.nodataView.setVisibility(View.GONE);
        mViewBinding.keyRv.setVisibility(View.VISIBLE);
        QMUITipDialogUtil.dismiss();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.select_key);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.sure);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceBuffer.getDeviceInformation(mGwId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                    ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.gw_is_offline_cannot_create_scene);
                    return;
                } else if (DeviceBuffer.getDeviceInformation(mIotId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                    String tip = String.format(getString(R.string.is_offline_cannot_add_bind),
                            DeviceBuffer.getDeviceInformation(mIotId).nickName);
                    ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, tip);
                    return;
                } else if (DeviceBuffer.getDeviceInformation(mAIotId).status == Constant.CONNECTION_STATUS_OFFLINE){
                    String tip = String.format(getString(R.string.is_offline_cannot_add_bind),
                            DeviceBuffer.getDeviceInformation(mAIotId).nickName);
                    ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, tip);
                    return;
                }
                QMUITipDialogUtil.showLoadingDialg(SelectAssociatedKeyActivity.this, R.string.is_submitted);
                if (mEditPos != -1 && mEditPos != mSelectPos) {
                    delBinding();
                } else {
                    addBinding();
                }
            }
        });
    }

    private void delBinding() {
        mFirstIotId = mAIotId;
        mSecondIotId = mIotId;

        mFirstMac = mAMac;
        mSecondMac = mBMac;

        switch (mSrcEndId) {
            case 1: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mBMac, String.valueOf(mEditPos + 1)});
                break;
            }
            case 2: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mBMac, String.valueOf(mEditPos + 1)});
                break;
            }
            case 3: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mBMac, String.valueOf(mEditPos + 1)});
                break;
            }
            case 4: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"U", "0006", "3", mBMac, String.valueOf(mEditPos + 1)});
                break;
            }
        }
    }

    private void addBinding() {
        mFirstIotId = mAIotId;
        mSecondIotId = mIotId;

        switch (mSelectPos) {
            case 0: {
                mTSLHelper.setProperty(mIotId, mPk, new String[]{CTSL.FWS_P_LOCALCONFIG_1}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                break;
            }
            case 1: {
                mTSLHelper.setProperty(mIotId, mPk, new String[]{CTSL.FWS_P_LOCALCONFIG_2}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                break;
            }
            case 2: {
                mTSLHelper.setProperty(mIotId, mPk, new String[]{CTSL.FWS_P_LOCALCONFIG_3}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                break;
            }
            case 3: {
                mTSLHelper.setProperty(mIotId, mPk, new String[]{CTSL.FWS_P_LOCALCONFIG_4}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
                break;
            }
        }

        ViseLog.d("mAMac == " + mAMac + " , mBMac = " + mBMac + " , mSelectPos = " + (mSelectPos + 1) + " , mSrcEndId = " + mSrcEndId);
        switch (mSrcEndId) {
            case 1: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"B", "0006", "3", mBMac, String.valueOf((mSelectPos + 1))});
                break;
            }
            case 2: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"B", "0006", "3", mBMac, String.valueOf((mSelectPos + 1))});
                break;
            }
            case 3: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"B", "0006", "3", mBMac, String.valueOf((mSelectPos + 1))});
                break;
            }
            case 4: {
                mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                        new String[]{"B", "0006", "3", mBMac, String.valueOf((mSelectPos + 1))});
                break;
            }
        }
    }

    private final static int RESULT = 10000;
    private final static int RESULT_DEL = 10001;

    private static class MyHandler extends Handler {
        private final WeakReference<SelectAssociatedKeyActivity> ref;

        public MyHandler(SelectAssociatedKeyActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SelectAssociatedKeyActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    case Constant.MSG_CALLBACK_LNPROPERTYNOTIFY: {
                        // 处理属性通知回调
                        ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                        ViseLog.d("绑定回调 = " + GsonUtil.toJson(propertyEntry));
                        if ("B".equals(propertyEntry.getPropertyValue("ResponseType"))) {
                            if ("0".equals(propertyEntry.getPropertyValue("Result"))) {
                                if (activity.mFirstIotId.equals(propertyEntry.iotId)) {
                                    switch (activity.mSelectPos + 1) {
                                        case 1: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"B", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 2: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"B", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 3: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"B", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 4: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"B", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                    }
                                } else if (activity.mSecondIotId.equals(propertyEntry.iotId)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(activity.mAIotId);
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    QMUITipDialogUtil.dismiss();
                                                    AssociatedBindListActivity.start(activity, activity.mAIotId, entry.productKey, entry.nickName, activity.mSrcEndId, 0);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            } else {
                                if ("142".equals(propertyEntry.getPropertyValue("Result")))
                                    QMUITipDialogUtil.showFailDialog(activity, R.string.too_much_del_before);
                                else
                                    QMUITipDialogUtil.showFailDialog(activity, R.string.bind_fail);
                            }
                        } else if ("U".equals(propertyEntry.getPropertyValue("ResponseType"))) {
                            ViseLog.d(new Gson().toJson(propertyEntry));
                            if ("0".equals(propertyEntry.getPropertyValue("Result"))) {
                                if (activity.mFirstIotId.equals(propertyEntry.iotId)) {
                                    switch (activity.mEditPos + 1) {
                                        case 1: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_1, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"U", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 2: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_2, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"U", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 3: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_3, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"U", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                        case 4: {
                                            activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{CTSL.FWS_P_ACTION_4, CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                                    new String[]{"U", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
                                            break;
                                        }
                                    }
                                } else if (activity.mSecondIotId.equals(propertyEntry.iotId)) {
                                    activity.addBinding();
                                }
                            } else if (!"136".equals(propertyEntry.getPropertyValue("Result"))) {
                                QMUITipDialogUtil.showFailDialog(activity, R.string.edit_fail);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
    }

    private class KeyItem {
        public String keyName;
        public int endPointId;

        public KeyItem(String keyName, int endPointId) {
            this.keyName = keyName;
            this.endPointId = endPointId;
        }
    }
}