package com.laffey.smart.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySelectAssociatedKeyBinding;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemBindList;
import com.laffey.smart.model.ItemBindRelation;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.DeviceManager;
import com.laffey.smart.presenter.RealtimeDataParser;
import com.laffey.smart.presenter.RealtimeDataReceiver;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.sdk.APIChannel;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.RetrofitUtil;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.widget.DialogUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectAssociatedKeyActivity extends BaseActivity implements View.OnClickListener {
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

    private static final String ORIGIN_IOT_ID = "origin_iot_id";
    private static final String ORIGIN_END_ID = "origin_end_id";
    private static final String DESTINATION_IOT_ID = "destination_iot_id";
    private static final String DESTINATION_END_ID = "destination_end_id";
    private static final String IS_EDIT = "is_edit";

    private String mPk;
    private String mIotId;
    private String mAIotId;
    private int mSelectPos;
    private String mAMac;
    private String mBMac;
    private int mSrcEndId;
    private String mSrcPK;
    private int mEditPos;

    private String mFirstIotId;
    private String mSecondIotId;
    private String mFirstMac;
    private String mSecondMac;
    private String mGwId;

    private Typeface mIconFace;
    private final List<KeyItem> mList = new ArrayList<>();
    private BaseQuickAdapter<KeyItem, BaseViewHolder> mAdapter;
    private TSLHelper mTSLHelper;

    private MyHandler mHandler;
    private ItemBindList mBindListBuffer;

    private String mOriginIotId;
    private String mOriginMac;
    private String mOriginEndId;
    private String mOriginPK;
    private String mDestinationIotId;
    private String mDestinationEndId;
    private String mDestinationMac;
    private String mDestinationPK;
    private final Map<String, String> mLocalConfigMap = new HashMap<>();
    private final Map<String, String> mActionMap = new HashMap<>();
    private ResultHandler mResultHandler;
    private String mBindListName;
    private boolean mIsEdit = false;
    private String mCacheGroupId;

    private ItemBindList mTmpBindList;
    private ItemBindList mSourceBindList;// 原始绑定组信息
    private final List<ItemBindRelation> mDelBufferList = new ArrayList<>();
    private final List<ItemBindRelation> mAddBufferList = new ArrayList<>();

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

    public static void start(Activity activity, String originIotId, String originEndId, String destinationIotId, String gwId, boolean isEdit) {
        Intent intent = new Intent(activity, SelectAssociatedKeyActivity.class);
        intent.putExtra(ORIGIN_IOT_ID, originIotId);
        intent.putExtra(ORIGIN_END_ID, originEndId);
        intent.putExtra(DESTINATION_IOT_ID, destinationIotId);
        intent.putExtra(GW_ID, gwId);
        intent.putExtra(IS_EDIT, isEdit);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String originIotId, String originEndId, String destinationIotId, String destinationEndId, String gwId, boolean isEdit) {
        Intent intent = new Intent(activity, SelectAssociatedKeyActivity.class);
        intent.putExtra(ORIGIN_IOT_ID, originIotId);
        intent.putExtra(ORIGIN_END_ID, originEndId);
        intent.putExtra(DESTINATION_IOT_ID, destinationIotId);
        intent.putExtra(DESTINATION_END_ID, destinationEndId);
        intent.putExtra(GW_ID, gwId);
        intent.putExtra(IS_EDIT, isEdit);
        activity.startActivity(intent);
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

        mOriginIotId = getIntent().getStringExtra(ORIGIN_IOT_ID);
        mOriginMac = DeviceBuffer.getDeviceMac(mOriginIotId);
        mOriginEndId = getIntent().getStringExtra(ORIGIN_END_ID);
        mOriginPK = DeviceBuffer.getDeviceInformation(mOriginIotId).productKey;
        mDestinationIotId = getIntent().getStringExtra(DESTINATION_IOT_ID);
        mDestinationEndId = getIntent().getStringExtra(DESTINATION_END_ID);
        mDestinationMac = DeviceBuffer.getDeviceMac(mDestinationIotId);
        mDestinationPK = DeviceBuffer.getDeviceInformation(mDestinationIotId).productKey;
        mIsEdit = getIntent().getBooleanExtra(IS_EDIT, false);

        mLocalConfigMap.put("1", CTSL.FWS_P_LOCALCONFIG_1);
        mLocalConfigMap.put("2", CTSL.FWS_P_LOCALCONFIG_2);
        mLocalConfigMap.put("3", CTSL.FWS_P_LOCALCONFIG_3);
        mLocalConfigMap.put("4", CTSL.FWS_P_LOCALCONFIG_4);

        mActionMap.put("1", CTSL.FWS_P_ACTION_1);
        mActionMap.put("2", CTSL.FWS_P_ACTION_2);
        mActionMap.put("3", CTSL.FWS_P_ACTION_3);
        mActionMap.put("4", CTSL.FWS_P_ACTION_4);

        mHandler = new MyHandler(this);
        mResultHandler = new ResultHandler(this);
        // 添加实时数据属性回调处理器
        RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", mResultHandler);

        initStatusBar();

        /*EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mIotId);
        mBMac = entry.mac;*/

        initAdapter();

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);

        mTmpBindList = new ItemBindList();
        ItemBindList bindList = DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId);
        ViseLog.d("绑定关系缓存 = \n" + GsonUtil.toJson(bindList));
        if (bindList != null) {
            mTmpBindList = JSONObject.parseObject(GsonUtil.toJson(bindList), ItemBindList.class);
            mSourceBindList = JSONObject.parseObject(GsonUtil.toJson(bindList), ItemBindList.class);
            mCacheGroupId = bindList.getBindList().get(0).getGroupId();
            mBindListName = mTmpBindList.getName();
        }

        // 查询该设备的绑定状态
        queryInvalidKey();
        // initData();
        // ViseLog.d(GsonUtil.toJson(DeviceBuffer.getAllDeviceInformation()));
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<KeyItem, BaseViewHolder>(R.layout.item_identifier, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, KeyItem item) {
                holder.setText(R.id.name_tv, item.keyName)
                        .setBackgroundColor(R.id.root_layout, ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.white));
                TextView nameTV = holder.getView(R.id.name_tv);

                TextView goTV = holder.getView(R.id.go_iv);
                goTV.setText(R.string.icon_checked);
                goTV.setTypeface(mIconFace);
                goTV.setTextColor(ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.appcolor));
                goTV.setTextSize(getResources().getDimension(R.dimen.sp_8));
                if (!mIsEdit) {
                    // 添加多控组
                    if (mOriginMac.equals(mDestinationMac) && mOriginEndId.equals(String.valueOf(item.endPointId))) {
                        nameTV.setTextColor(ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.gray3));
                    } else {
                        nameTV.setTextColor(ContextCompat.getColor(SelectAssociatedKeyActivity.this, item.isBind ? R.color.gray3 : R.color.black));
                    }
                    goTV.setVisibility(item.isChecked ? View.VISIBLE : View.GONE);
                } else {
                    nameTV.setTextColor(ContextCompat.getColor(SelectAssociatedKeyActivity.this, item.isBind ? R.color.gray3 : R.color.black));
                    goTV.setVisibility(item.isChecked ? View.VISIBLE : View.GONE);
                }
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                TextView nameTV = (TextView) view.findViewById(R.id.name_tv);
                int color = nameTV.getCurrentTextColor();
                if (color == ContextCompat.getColor(SelectAssociatedKeyActivity.this, R.color.gray3))
                    return;

                if (!mIsEdit) {
                    // 多选
                    if (!mList.get(position).isChecked) {
                        int total = 0;
                        for (KeyItem item : mList) {
                            if (item.isChecked)
                                total++;
                        }

                        mBindListBuffer = DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId);
                        int bufferCount = 0;
                        if (mBindListBuffer != null && mBindListBuffer.getBindList() != null) {
                            bufferCount = mBindListBuffer.getBindList().size();
                        }
                        if (bufferCount + total < 5) {
                            mList.get(position).isChecked = !mList.get(position).isChecked;
                            mAdapter.notifyDataSetChanged();
                            editBindList(mList.get(position), position);
                        } else {
                            ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.max_of_four_keys_can_be_temporarily_bound_to_one_key);
                        }
                    } else {
                        mList.get(position).isChecked = false;
                        mAdapter.notifyDataSetChanged();
                        editBindList(mList.get(position), position);
                    }
                } else {
                    // 单选
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).isChecked = i == position;
                        editBindList(mList.get(i), i);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.keyRv.setLayoutManager(layoutManager);
        mViewBinding.keyRv.setAdapter(mAdapter);
    }

    private void editBindList(KeyItem item, int pos) {
        if (!mIsEdit) {
            if (item.isChecked) {
                ViseLog.d("mTmpBindList = \n" + GsonUtil.toJson(mTmpBindList));
                // 选中状态
                if (mTmpBindList != null && mTmpBindList.getBindList().size() == 6) {
                    ToastUtils.showLongToast(this, R.string.max_of_four_keys_can_be_temporarily_bound_to_one_key);
                } else {
                    if (mTmpBindList.getBindList().size() == 0) {
                        ItemBindRelation r = new ItemBindRelation();
                        r.setEndpoint(String.valueOf(pos + 1));
                        r.setSubDevMac(mDestinationMac);

                        mTmpBindList.getBindList().add(r);
                    } else {
                        boolean isContains = false;
                        for (ItemBindRelation relation : mTmpBindList.getBindList()) {
                            if (String.valueOf(pos + 1).equals(relation.getEndpoint()) &&
                                    mDestinationMac.equals(relation.getSubDevMac())) {
                                isContains = true;
                                break;
                            }
                        }
                        if (!isContains) {
                            ItemBindRelation r = new ItemBindRelation();
                            r.setEndpoint(String.valueOf(pos + 1));
                            r.setSubDevMac(mDestinationMac);

                            mTmpBindList.getBindList().add(r);
                        }
                    }
                }
            } else {
                // 取消选中
                List<ItemBindRelation> relations = mTmpBindList.getBindList();
                for (int i = 0; i < relations.size(); i++) {
                    ItemBindRelation relation = relations.get(i);
                    if (mDestinationMac.equals(relation.getSubDevMac()) && String.valueOf(pos + 1).equals(relation.getEndpoint())) {
                        relations.remove(i);
                        break;
                    }
                }
                mTmpBindList.setBindList(relations);
            }
        } else {
            // 编辑模式
            List<ItemBindRelation> list = new ArrayList<>();
            list.addAll(mSourceBindList.getBindList());
            for (ItemBindRelation relation : list) {
                if (mDestinationMac.equals(relation.getSubDevMac()) &&
                        mDestinationEndId.equals(relation.getEndpoint())) {
                    list.remove(relation);
                    break;
                }
            }
            if (item.isChecked) {
                ItemBindRelation r = new ItemBindRelation();
                r.setEndpoint(String.valueOf(pos + 1));
                r.setSubDevMac(mDestinationMac);

                list.add(r);
                mTmpBindList.setBindList(list);
            }
        }
        ViseLog.d("编辑后 = \n" + GsonUtil.toJson(mTmpBindList));
    }

    private void initData(List<ItemBindRelation> list) {
        JSONObject object = DeviceBuffer.getExtendedInfo(mDestinationIotId);
        switch (mDestinationPK) {
            case CTSL.PK_ONEWAYSWITCH: {
                // 一键
                mList.clear();
                KeyItem item;
                if (object != null && object.toJSONString().length() > 2) {
                    item = new KeyItem(object.getString(CTSL.OWS_P_PowerSwitch_1), 1);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                }
                checkKeyInvalidkey(list, mDestinationMac, "1", item);
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
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P_PowerSwitch_2), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
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

                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P3_PowerSwitch_2), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.TWS_P3_PowerSwitch_3), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
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
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_2), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_3), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.FWS_P_PowerSwitch_4), 4);
                    checkKeyInvalidkey(list, mDestinationMac, "4", item);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.four_way_powerswitch), 4);
                    checkKeyInvalidkey(list, mDestinationMac, "4", item);
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
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_2), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_3), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
                    mList.add(item);

                    item = new KeyItem(object.getString(CTSL.SIX_SCENE_SWITCH_P_POWER_4), 4);
                    checkKeyInvalidkey(list, mDestinationMac, "4", item);
                    mList.add(item);
                } else {
                    item = new KeyItem(getString(R.string.one_way_powerswitch), 1);
                    checkKeyInvalidkey(list, mDestinationMac, "1", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.two_way_powerswitch), 2);
                    checkKeyInvalidkey(list, mDestinationMac, "2", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.three_way_powerswitch), 3);
                    checkKeyInvalidkey(list, mDestinationMac, "3", item);
                    mList.add(item);

                    item = new KeyItem(getString(R.string.four_way_powerswitch), 4);
                    checkKeyInvalidkey(list, mDestinationMac, "4", item);
                    mList.add(item);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        mViewBinding.nodataView.setVisibility(View.GONE);
        mViewBinding.keyRv.setVisibility(View.VISIBLE);
        QMUITipDialogUtil.dismiss("477");
    }

    // 查询设备可以添加多控组的按键
    private void queryInvalidKey() {
        DeviceManager.getAvaliableKey(this, mDestinationMac, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                ViseLog.d("此设备所有绑定关系 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    JSONArray bindList = response.getJSONArray("bindList");
                    List<ItemBindRelation> list = new ArrayList<>();
                    if (bindList != null) {
                        for (int i = 0; i < bindList.size(); i++) {
                            JSONObject object = bindList.getJSONObject(i);
                            ItemBindRelation bind = JSONObject.parseObject(object.toJSONString(), ItemBindRelation.class);
                            list.add(bind);
                        }
                    }
                    initData(list);
                } else {
                    QMUITipDialogUtil.dismiss("498");
                    RetrofitUtil.showErrorMsg(SelectAssociatedKeyActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss("505");
                ViseLog.e(e);
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, e.getMessage());
            }
        });
    }

    private boolean isKeyInvalidkey(List<ItemBindRelation> list, String destinationMac, String endPointId) {
        if (list == null || list.size() == 0) {
            return false;
        } else {
            for (ItemBindRelation bind : list) {
                if (destinationMac.equals(bind.getMac()) && endPointId.equals(bind.getEndpoint())) {
                    return true;
                }
            }
            return false;
        }
    }

    private void checkKeyInvalidkey(List<ItemBindRelation> list, String destinationMac, String endPointId, KeyItem item) {
        if (list != null && list.size() > 0) {
            for (ItemBindRelation bind : list) {
                if (destinationMac.equals(bind.getSubDevMac()) && endPointId.equals(bind.getEndpoint())) {
                    item.isBind = true;
                    if (mIsEdit) {
                        if (bind.getSubDevMac().equals(mDestinationMac)) {
                            if (endPointId.equals(mDestinationEndId)) {
                                item.isBind = false;
                                item.isChecked = true;
                            } else {
                                item.isBind = true;
                                item.isChecked = false;
                            }
                        }
                    }
                    break;
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

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.select_key);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.sure);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
    }

    private void delBinding() {
        mFirstIotId = mAIotId;
        mSecondIotId = mIotId;

        mFirstMac = mAMac;
        mSecondMac = mBMac;

        mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{mActionMap.get(String.valueOf(mSrcEndId)), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"U", "0006", "3", mBMac, String.valueOf(mEditPos + 1)});
    }

    private void addBinding() {
        mFirstIotId = mAIotId;
        mSecondIotId = mIotId;

        mTSLHelper.setProperty(mIotId, mPk, new String[]{mLocalConfigMap.get(String.valueOf(mSelectPos + 1))}, new String[]{"" + CTSL.AUXILIARY_CONTROL});

        mTSLHelper.setProperty(mAIotId, mSrcPK, new String[]{mActionMap.get(String.valueOf(mSrcEndId)), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"B", "0006", "3", mBMac, String.valueOf((mSelectPos + 1))});
    }

    private void addBindRelation(ItemBindRelation relation) {
        mResultHandler.setItemBindRelation(relation);
        mTSLHelper.setProperty(mDestinationIotId, mDestinationPK,
                new String[]{mLocalConfigMap.get(relation.getEndpoint())}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
        mTSLHelper.setProperty(mOriginIotId, mOriginPK, new String[]{mActionMap.get(mOriginEndId), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"B", "0006", "3", mDestinationMac, String.valueOf(relation.getEndpoint())});
    }

    private void cancelBindRelation(ItemBindRelation relation) {
        mResultHandler.setItemBindRelation(relation);
        mTSLHelper.setProperty(mDestinationIotId, mDestinationPK,
                new String[]{mLocalConfigMap.get(relation.getEndpoint())}, new String[]{"" + CTSL.AUXILIARY_CONTROL});
        mTSLHelper.setProperty(mOriginIotId, mOriginPK, new String[]{mActionMap.get(mOriginEndId), CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                new String[]{"U", "0006", "3", mDestinationMac, String.valueOf(relation.getEndpoint())});
    }

    private static class ResultHandler extends Handler {
        private final WeakReference<SelectAssociatedKeyActivity> ref;
        private ItemBindRelation relation;

        public ResultHandler(SelectAssociatedKeyActivity activity) {
            ref = new WeakReference<>(activity);
        }

        public void setItemBindRelation(ItemBindRelation relation) {
            this.relation = relation;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SelectAssociatedKeyActivity activity = ref.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_LNPROPERTYNOTIFY) {
                ETSL.propertyEntry propertyEntry = RealtimeDataParser.processProperty((String) msg.obj);
                if ("B".equals(propertyEntry.getPropertyValue("ResponseType"))) {
                    // 双控绑定
                    if ("0".equals(propertyEntry.getPropertyValue("Result"))) {
                        if (propertyEntry.iotId.equals(activity.mOriginIotId)) {
                            activity.mTSLHelper.setProperty(activity.mDestinationIotId, activity.mDestinationPK,
                                    new String[]{activity.mActionMap.get(relation.getEndpoint()),
                                            CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                    new String[]{"B", "0006", "3", activity.mOriginMac, activity.mOriginEndId});
                        } else if (propertyEntry.iotId.equals(activity.mDestinationIotId)) {
                            int pos = activity.mAddBufferList.indexOf(relation);
                            if (pos < activity.mAddBufferList.size() - 1) {
                                activity.addBindRelation(activity.mAddBufferList.get(pos + 1));
                            } else {
                                // 提交到服务端
                                activity.addBindRelation2Service();
                            }
                        }
                    }
                } else if ("U".equals(propertyEntry.getPropertyValue("ResponseType"))) {
                    // 解除双控绑定
                    ViseLog.d("activity.mOriginIotId = " + activity.mOriginIotId +
                            "\nactivity.mDestinationIotId = " + activity.mDestinationIotId +
                            "\n网关返回解绑结果 = \n" + GsonUtil.toJson(propertyEntry));
                    if ("0".equals(propertyEntry.getPropertyValue("Result"))) {
                        if (propertyEntry.iotId.equals(activity.mOriginIotId)) {
                            activity.mTSLHelper.setProperty(activity.mDestinationIotId, activity.mDestinationPK,
                                    new String[]{activity.mActionMap.get(relation.getEndpoint()),
                                            CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                    new String[]{"U", "0006", "3", activity.mOriginMac, activity.mOriginEndId});
                        } else if (propertyEntry.iotId.equals(activity.mDestinationIotId)) {
                            int pos = activity.mDelBufferList.indexOf(relation);
                            ViseLog.d("pos = " + pos + " , listsize = " + activity.mDelBufferList.size());
                            if (pos < activity.mDelBufferList.size() - 1) {
                                activity.cancelBindRelation(activity.mDelBufferList.get(pos + 1));
                            } else {
                                // 提交到服务端
                                activity.cancelBindRelation2Service();
                            }
                        }
                    }
                }
            }
        }
    }

    private void editBindRelation() {
        ItemBindList bufferList = DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId);

        if (bufferList == null) {
            // 之前该按键无绑定关系，新建绑定
            mAddBufferList.clear();
            mAddBufferList.addAll(mTmpBindList.getBindList());

            ItemBindRelation relation = new ItemBindRelation();
            relation.setSubDevMac(mOriginMac);
            relation.setEndpoint(mOriginEndId);
            mAddBufferList.add(relation);

            ItemBindList control = new ItemBindList();
            control.setName(mBindListName);
            control.setMac(DeviceBuffer.getDeviceMac(mGwId));
            control.setBindList(mAddBufferList);
            ViseLog.d("提交多控组 = \n" + GsonUtil.toJson(control));

            // addBindRelation(mAddBufferList.get(0));
            addOrEditMultiControl(control);
        } else {
            //final List<ItemBindRelation> bufferBindList = new ArrayList<>();
            // bufferBindList.addAll(bufferList.getBindList());

            // 筛选出需要删除的关系
            /*mDelBufferList.clear();
            List<ItemBindRelation> tmpBindRelations = mTmpBindList.getBindList();
            for (ItemBindRelation bind : bufferBindList) {
                boolean isContain = false;
                for (ItemBindRelation relation : tmpBindRelations) {
                    if (relation.getMac().equals(bind.getMac()) &&
                            relation.getEndpoint().equals(bind.getEndpoint())) {
                        isContain = true;
                        break;
                    }
                }
                if (!isContain) {
                    mDelBufferList.add(bind);
                }
            }*/

            // 筛选出需要新增的关系
            /*ViseLog.d("对比关系-tmpBindRelations\n" + GsonUtil.toJson(tmpBindRelations) +
                    "\n对比关系-bufferDestMacList\n" + GsonUtil.toJson(bufferBindList));
            mAddBufferList.clear();
            for (ItemBindRelation relation : tmpBindRelations) {
                boolean isContains = false;
                for (ItemBindRelation destRelation : bufferBindList) {
                    if (relation.getMac().equals(destRelation.getMac()) &&
                            relation.getEndpoint().equals(destRelation.getEndpoint())) {
                        isContains = true;
                        break;
                    }
                }
                if (!isContains) {
                    mAddBufferList.add(relation);
                }
            }*/

            /*ViseLog.d("需要解绑的列表 = \n" + GsonUtil.toJson(mDelBufferList));
            ViseLog.d("需要绑定的列表 = \n" + GsonUtil.toJson(mAddBufferList));
            if (mDelBufferList.size() > 0) {
                cancelBindRelation(mDelBufferList.get(0));
            } else if (mAddBufferList.size() > 0) {
                addBindRelation(mAddBufferList.get(0));
            } else {
                QMUITipDialogUtil.dismiss("768");
                ToastUtils.showLongToast(this, R.string.binding_group_complete);
                Intent intent = new Intent(this, AssociatedBindListActivity.class);
                startActivity(intent);
            }*/
            mTmpBindList.setMac(DeviceBuffer.getDeviceMac(mGwId));
            ViseLog.d("编辑多控组 = \n" + GsonUtil.toJson(mTmpBindList));
            addOrEditMultiControl(mTmpBindList);
        }
    }

    private void addOrEditMultiControl(ItemBindList control) {
        DeviceManager.addOrEditMultiControl(this, control, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss("795");
                int code = response.getInteger("code");
                ViseLog.d("新增双控绑定 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.binding_group_complete);

                    String groupId = response.getString("groupId");
                    SceneManager.invokeManageControlGroupService(SelectAssociatedKeyActivity.this, mGwId, groupId,
                            DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId) == null ? 1 : 2,
                            null);

                    Intent intent = new Intent(SelectAssociatedKeyActivity.this, AssociatedBindListActivity.class);
                    startActivity(intent);
                } else {
                    RetrofitUtil.showErrorMsg(SelectAssociatedKeyActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, e.getMessage());
            }
        });
    }

    private void addBindRelation2Service() {
        List<ItemBindRelation> list = new ArrayList<>();
        if (DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId) == null) {
            // 之前此按键无双控绑定关系
            ItemBindRelation relation = new ItemBindRelation();
            relation.setMac(mOriginMac);
            // relation.setMainBind(true);
            relation.setEndpoint(mOriginEndId);

            list.add(relation);
            list.addAll(mAddBufferList);
        } else {
            // 之前此按键已经双控绑定
            list.addAll(mAddBufferList);
        }

        DeviceManager.addBindRelation(this, mBindListName, list, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss("795");
                int code = response.getInteger("code");
                ViseLog.d("新增双控绑定 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.binding_group_complete);
                    Intent intent = new Intent(SelectAssociatedKeyActivity.this, AssociatedBindListActivity.class);
                    startActivity(intent);
                } else {
                    RetrofitUtil.showErrorMsg(SelectAssociatedKeyActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss("810");
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, e.getMessage());
            }
        });
    }

    private void cancelBindRelation2Service() {
        ItemBindRelation mainRelation = null;
        for (ItemBindRelation relation : mDelBufferList) {
            if (/*relation.getMainBind()*/false) {
                mainRelation = relation;
                break;
            }
        }
        if (mainRelation != null) {
            // 如果删除绑定列表中存在主控，则只需要传主控关系，就可以删除整个绑定组
            cancelBindRelation2Service(mainRelation);
        } else {
            cancelBindRelation2Service(mDelBufferList.get(0));
        }
    }

    private void cancelBindRelation2Service(ItemBindRelation relation) {
        DeviceManager.cancelBindRelation(this, relation, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                ViseLog.d("解绑双控 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    ViseLog.d("解绑后添加绑定关系 = \n" + GsonUtil.toJson(mAddBufferList));
                    if (mAddBufferList.size() == 0) {
                        QMUITipDialogUtil.dismiss("841");
                        ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.unbinding_complete);
                        Intent intent = new Intent(SelectAssociatedKeyActivity.this, AssociatedBindListActivity.class);
                        startActivity(intent);
                    } else {
                        addBindRelation(mAddBufferList.get(0));
                    }
                } else {
                    QMUITipDialogUtil.dismiss("849");
                    RetrofitUtil.showErrorMsg(SelectAssociatedKeyActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss("857");
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, e.getMessage());
            }
        });
    }

    private final static int RESULT = 10000;
    private final static int RESULT_DEL = 10001;

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.tvToolbarRight.getId()) {
            // 确定
            if (DeviceBuffer.getDeviceInformation(mGwId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.gw_is_offline_cannot_add_bind);
                return;
            } else if (DeviceBuffer.getDeviceInformation(mOriginIotId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                String tip = String.format(getString(R.string.is_offline_cannot_add_bind),
                        DeviceBuffer.getDeviceInformation(mOriginIotId).nickName);
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, tip);
                return;
            } else if (DeviceBuffer.getDeviceInformation(mDestinationIotId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                String tip = String.format(getString(R.string.is_offline_cannot_add_bind),
                        DeviceBuffer.getDeviceInformation(mDestinationIotId).nickName);
                ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, tip);
                return;
            }
            boolean isChecked = false;
            for (KeyItem item : mList) {
                if (item.isChecked) {
                    isChecked = true;
                    break;
                }
            }
            if (!isChecked) {
                ToastUtils.showLongToast(this, R.string.pls_select_key);
                return;
            }
            if (DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId) == null) {
                showBindListNameDialogEdit();
            } else {
                QMUITipDialogUtil.showLoadingDialg(SelectAssociatedKeyActivity.this, R.string.is_submitted);
                editBindRelation();
            }
        } else if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        }
    }

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
                        ViseLog.d("绑定回调\n" + GsonUtil.toJson(propertyEntry));
                        if ("B".equals(propertyEntry.getPropertyValue("ResponseType"))) {
                            if ("0".equals(propertyEntry.getPropertyValue("Result"))) {
                                /*if (activity.mFirstIotId.equals(propertyEntry.iotId)) {
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
                                }*/
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
                                    activity.mTSLHelper.setProperty(activity.mIotId, activity.mPk, new String[]{activity.mActionMap.get(String.valueOf(activity.mEditPos + 1)),
                                                    CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                            new String[]{"U", "0006", "3", activity.mAMac, String.valueOf(activity.mSrcEndId)});
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
        public boolean isChecked;
        public boolean isBind;
        public String groupId;

        public KeyItem(String keyName, int endPointId) {
            this.keyName = keyName;
            this.endPointId = endPointId;
            this.isChecked = false;
            this.isBind = false;
        }
    }

    // 显示场景名称修改对话框
    private void showBindListNameDialogEdit() {
        DialogUtils.showInputDialog(this, getString(R.string.dialog_title), getString(R.string.pls_input_binding_group_name),
                "", new DialogUtils.InputCallback() {
                    @Override
                    public void positive(String result) {
                        mBindListName = result;
                        if (mBindListName == null || mBindListName.length() == 0) {
                            ToastUtils.showLongToast(SelectAssociatedKeyActivity.this, R.string.binding_group_name_cannot_be_null);
                        } else {
                            QMUITipDialogUtil.showLoadingDialg(SelectAssociatedKeyActivity.this, R.string.is_submitted);
                            editBindRelation();
                        }
                    }

                    @Override
                    public void negative() {

                    }
                });
    }
}