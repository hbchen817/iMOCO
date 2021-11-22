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
import android.widget.Toast;

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
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemBindList;
import com.laffey.smart.model.ItemBindRelation;
import com.laffey.smart.model.ItemBinding;
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

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociatedBindListActivity extends BaseActivity implements View.OnClickListener {
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
    private String mDevMac;
    private String mGatewayId;
    private String mProductKey;
    private int mResult;

    private Typeface mIconfont;
    private SceneManager mSceneManager;
    private TSLHelper mTSLHelper;

    private MyHandler mHandler;
    private ItemBindRelation mDelBindRelation;

    private final List<ItemBindRelation> mList = new ArrayList<>();
    private BaseQuickAdapter<ItemBindRelation, BaseViewHolder> mAdapter;
    private final Map<String, String> mActionMap = new HashMap<>();
    private final Map<String, String> mPropertyMap = new HashMap<>();
    private final Map<String, String> mKeyNameMap = new HashMap<>();
    private final JSONArray mBindingArray = new JSONArray();
    private ItemBindList mControlGroup;

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

        mIotId = getIntent().getStringExtra(IOT_ID);
        mDevMac = DeviceBuffer.getDeviceMac(mIotId);
        mKeyName = getIntent().getStringExtra(KEY_NAME);
        mKeyValue = getIntent().getIntExtra(KEY_VALUE, -1);
        mProductKey = getIntent().getStringExtra(PRODUCT_KEY);
        mHandler = new MyHandler(this);

        initStatusBar();
        mViewBinding.nameEditTv.setOnClickListener(this);
        initData();

        mTSLHelper = new TSLHelper(this);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        getGWIotIdBySubIotId();

        initAdapter();
    }

    // 根据子设备iotId获取网关iotId
    private void getGWIotIdBySubIotId() {
        SceneManager.getGWIotIdBySubIotId(this, mIotId, new SceneManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                // 网关id
                // 根据子设备iotId查询网关iotId
                int code = response.getInteger("code");
                if (code == 200) {
                    mGatewayId = response.getString("gwIotId");
                    queryBindingTable();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemBindRelation, BaseViewHolder>(R.layout.item_associated_bind, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemBindRelation item) {
                EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(item.getSubDevMac());
                ImageView imageView = holder.getView(R.id.dev_iv);
                TextView delView = holder.getView(R.id.del_tv);
                if (entry != null) {
                    Glide.with(AssociatedBindListActivity.this).load(entry.image).into(imageView);

                    holder.setText(R.id.dev_name_tv, entry.nickName);
                    holder.getView(R.id.del_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDelDialg(mList.indexOf(item));
                        }
                    });
                    delView.setVisibility(View.GONE);
                    JSONObject extend = DeviceBuffer.getExtendedInfo(entry.iotId);
                    if (extend != null && extend.toJSONString().length() > 2) {
                        holder.setText(R.id.key_name_tv, extend.getString(mPropertyMap.get(item.getEndpoint())));
                    } else {
                        holder.setText(R.id.key_name_tv, mKeyNameMap.get(item.getEndpoint()));
                    }
                    if (mDevMac.equals(item.getSubDevMac()) && String.valueOf(mKeyValue).equals(item.getEndpoint())) {
                        holder.setTextColor(R.id.dev_name_tv, ContextCompat.getColor(AssociatedBindListActivity.this, R.color.gray3));
                        holder.setTextColor(R.id.key_name_tv, ContextCompat.getColor(AssociatedBindListActivity.this, R.color.gray3));
                    } else {
                        holder.setTextColor(R.id.dev_name_tv, ContextCompat.getColor(AssociatedBindListActivity.this, R.color.black));
                        holder.setTextColor(R.id.key_name_tv, ContextCompat.getColor(AssociatedBindListActivity.this, R.color.black));
                    }
                }
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ItemBindRelation relation = mList.get(position);
                if (mDevMac.equals(relation.getSubDevMac()) && String.valueOf(mKeyValue).equals(relation.getEndpoint()))
                    return;
                SelectAssociatedKeyActivity.start(AssociatedBindListActivity.this, mIotId, String.valueOf(mKeyValue),
                        DeviceBuffer.getDevByMac(relation.getSubDevMac()).iotId, mGatewayId, true);
                mAdapter.notifyDataSetChanged();
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
        // RealtimeDataReceiver.addPropertyCallbackHandler(this.toString() + "Property", mHandler);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        queryBindingTable();
    }

    // 查询绑定列表
    private void queryBindingTable() {
        DeviceManager.getMultiControl(this, mDevMac, String.valueOf(mKeyValue), new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                // ViseLog.d("绑定关系 = \n" + GsonUtil.toJson(response));
                if (code == 200) {
                    mList.clear();
                    JSONArray bindList = response.getJSONArray("bindList");
                    if (bindList != null) {
                        mControlGroup = JSONObject.parseObject(response.toJSONString(), ItemBindList.class);
                        mViewBinding.groupNameTv.setText(mControlGroup.getName());
                        mControlGroup.setMac(DeviceBuffer.getDeviceMac(mGatewayId));
                        DeviceBuffer.addBindList(mDevMac + "-" + mKeyValue, mControlGroup);
                        for (int i = 0; i < bindList.size(); i++) {
                            ItemBindRelation relation = JSONObject.parseObject(bindList.getJSONObject(i).toJSONString(), ItemBindRelation.class);
                            mList.add(relation);
                        }
                    } else {
                        DeviceBuffer.addBindList(mDevMac + "-" + mKeyValue, null);
                    }
                    mAdapter.notifyDataSetChanged();
                    refreshNoDataView();
                } else {
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    private void showDelDialg(int pos) {
        for (ItemBindRelation relation : mList) {
            EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(relation.getSubDevMac());
            if (entry != null && entry.status == 3) {
                DialogUtils.showConfirmDialog(this, getString(R.string.dialog_title),
                        String.format(getString(R.string.is_offline_and_pls_retry), entry.nickName),
                        getString(R.string.dialog_confirm),
                        new DialogUtils.Callback() {
                            @Override
                            public void positive() {

                            }

                            @Override
                            public void negative() {

                            }
                        });
                return;
            }
        }

        DialogUtils.showConfirmDialog(this, R.string.dialog_title, R.string.do_you_really_want_to_delete_the_associated_binding,
                R.string.dialog_confirm, R.string.dialog_cancel, new DialogUtils.Callback() {
                    @Override
                    public void positive() {
                        mBindingArray.clear();
                        delBinding(pos);
                    }

                    @Override
                    public void negative() {

                    }
                });
    }

    private void delBinding(int pos) {
        QMUITipDialogUtil.showLoadingDialg(AssociatedBindListActivity.this, R.string.is_submitted);

        ItemBindList control = DeviceBuffer.getBindList(mDevMac + "-" + mKeyValue);
        if (mList.size() == 2) {
            // 如果最后还剩一条多控关系，直接删除绑定组
            delMultiControlGroup(control.getGroupId(), control.getMac());
        } else {
            mList.remove(pos);
            control.setBindList(mList);
            addOrEditMultiControl(control);
        }
    }

    // 删除多控组关系
    private void delMultiControlGroup(String groupId, String mac) {
        DeviceManager.delMultiControlGroup(this, groupId, mac, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    SceneManager.invokeManageControlGroupService(AssociatedBindListActivity.this, mGatewayId,
                            mControlGroup.getGroupId(), 3, null);
                    queryBindingTable();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    // 添加或编辑多控组关系
    private void addOrEditMultiControl(ItemBindList control) {
        DeviceManager.addOrEditMultiControl(this, control, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    SceneManager.invokeManageControlGroupService(AssociatedBindListActivity.this,
                            mGatewayId, mControlGroup.getGroupId(), 2, null);
                    queryBindingTable();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            // 返回
            finish();
        } else if (v.getId() == mViewBinding.includeToolbar.tvToolbarRight.getId()) {
            // 添加多控组关系
            if (DeviceBuffer.getDeviceInformation(mGatewayId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                // 网关离线
                ToastUtils.showLongToast(AssociatedBindListActivity.this, R.string.gw_is_offline_cannot_add_bind);
            } else if (DeviceBuffer.getDeviceInformation(mIotId).status == Constant.CONNECTION_STATUS_OFFLINE) {
                // 设备离线
                ToastUtils.showLongToast(AssociatedBindListActivity.this, R.string.dev_is_offline_cannot_add_bind);
            } else {
                if (mList.size() < 5) {
                    SelectAssociatedDevActivity.start(AssociatedBindListActivity.this, mIotId, mGatewayId, mKeyValue, mProductKey);
                } else {
                    ToastUtils.showLongToast(AssociatedBindListActivity.this, R.string.max_of_four_keys_can_be_temporarily_bound_to_one_key);
                }
            }
        } else if (v.getId() == mViewBinding.nameEditTv.getId()) {
            // 编辑多控组名称
            DialogUtils.showInputDialog(this, getString(R.string.binding_group_name_edit),
                    getString(R.string.pls_input_binding_group_name), mViewBinding.groupNameTv.getText().toString(), new DialogUtils.InputCallback() {
                        @Override
                        public void positive(String result) {
                            if (result == null || result.length() == 0) {
                                ToastUtils.showLongToast(AssociatedBindListActivity.this,
                                        R.string.binding_group_name_cannot_be_null);
                            } else {
                                QMUITipDialogUtil.showLoadingDialg(AssociatedBindListActivity.this, R.string.is_submitted);
                                editControlGroupName(result);
                            }
                        }

                        @Override
                        public void negative() {

                        }
                    });
        }
    }

    // 修改组昵称
    public void editControlGroupName(String name) {
        DeviceManager.editControlGroupName(this, mControlGroup.getGroupId(), name, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                QMUITipDialogUtil.dismiss();
                int code = response.getInteger("code");
                if (code == 200) {
                    mViewBinding.groupNameTv.setText(name);
                    mControlGroup.setName(name);
                    DeviceBuffer.getBindList(mDevMac + "-" + mKeyValue).setName(name);
                } else {
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                QMUITipDialogUtil.dismiss();
                ViseLog.e(e);
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> reference;
        private ItemBindRelation relation;

        public MyHandler(Activity activity) {
            this.reference = new WeakReference<>(activity);
        }

        public void setItemBindRelation(ItemBindRelation relation) {
            this.relation = relation;
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
                        ViseLog.d("双控 = " + GsonUtil.toJson(propertyEntry) + "\n" + DeviceBuffer.getDeviceInformation(activity.mIotId).mac);
                        if (propertyEntry == null) break;
                        if ("U".equals(propertyEntry.getPropertyValue("ResponseType")) &&
                                "0".equals(propertyEntry.getPropertyValue("Result"))) {
                            EDevice.deviceEntry entry = DeviceBuffer.getDevByMac(relation.getSubDevMac());
                            if (activity.mIotId.equals(propertyEntry.iotId)) {
                                if (entry == null) {
                                    activity.cancelBindRelation(relation);
                                } else {
                                    activity.mTSLHelper.setProperty(entry.iotId, entry.productKey, new String[]{activity.mActionMap.get(relation.getEndpoint()),
                                                    CTSL.FWS_P_FUNCTION, CTSL.FWS_P_DSTADDRMODE, CTSL.FWS_P_DSTADDR, CTSL.FWS_P_DSTENDPOINTID},
                                            new String[]{"U", "0006", "3", activity.mDevMac, String.valueOf(activity.mKeyValue)});
                                }
                            } else if (entry.iotId.equals(propertyEntry.iotId)) {
                                activity.cancelBindRelation(relation);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void cancelBindRelation(ItemBindRelation relation) {
        DeviceManager.cancelBindRelation(this, relation, new DeviceManager.Callback() {
            @Override
            public void onNext(JSONObject response) {
                int code = response.getInteger("code");
                if (code == 200) {
                    QMUITipDialogUtil.dismiss();
                    ToastUtils.showLongToast(AssociatedBindListActivity.this, R.string.unbinding_complete);
                    queryBindingTable();
                } else {
                    QMUITipDialogUtil.dismiss();
                    RetrofitUtil.showErrorMsg(AssociatedBindListActivity.this, response);
                }
            }

            @Override
            public void onError(Throwable e) {
                ViseLog.e(e);
                QMUITipDialogUtil.dismiss();
                ToastUtils.showLongToast(AssociatedBindListActivity.this, e.getMessage());
            }
        });
    }

    private void refreshNoDataView() {
        if (mList.size() > 0) {
            mViewBinding.bindrelationNodataView.setVisibility(View.GONE);
            mViewBinding.groupLayout.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.bindrelationNodataView.setVisibility(View.VISIBLE);
            mViewBinding.groupLayout.setVisibility(View.GONE);
        }
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(String.format(getString(R.string.control_group_list), mKeyName));
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.includeToolbar.tvToolbarRight.setText(R.string.icon_add_2);
        mViewBinding.includeToolbar.tvToolbarRight.setTextSize(30);
        mViewBinding.includeToolbar.tvToolbarRight.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.includeToolbar.tvToolbarRight.setTypeface(mIconfont);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.nameEditTv.setTypeface(mIconfont);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // RealtimeDataReceiver.deleteCallbackHandler(this.toString() + "Property");
    }
}