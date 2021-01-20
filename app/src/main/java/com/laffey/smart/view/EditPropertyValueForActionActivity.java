package com.laffey.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.cncoderx.wheelview.Wheel3DView;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.demoTest.ActionEntry;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.demoTest.IdentifierItemForCA;
import com.laffey.smart.demoTest.ServiceInputData;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPropertyValueForActionActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.value_rv)
    RecyclerView mValueRV;
    @BindView(R.id.event_layout)
    LinearLayout mEventLayout;
    @BindView(R.id.compare_value_wv)
    Wheel3DView mCompareValueWV;
    @BindView(R.id.compare_type_wv)
    Wheel3DView mCompareTypeWV;
    @BindView(R.id.unit_tv)
    TextView mUnitTV;
    @BindView(R.id.name_tv)
    TextView mNameTV;
    @BindView(R.id.service_rv)
    RecyclerView mServiceRV;

    private IdentifierItemForCA mIdentifier;
    private EventValue mEventValue;
    private List<String> mEventValueList = new ArrayList<>();

    private SceneManager mSceneManager;
    private CallbackHandler mHandler;

    private List<PropertyValue> mList;
    private BaseQuickAdapter<PropertyValue, BaseViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;

    private String[] mCompareTypes;

    private List<ServiceInputData> mServiceInputDataList = new ArrayList<>();
    private BaseQuickAdapter<ServiceInputData, BaseViewHolder> mServiceAdapter;
    private LinearLayoutManager mServiceLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property_value);
        ButterKnife.bind(this);

        mCompareTypes = new String[]{getString(R.string.equal_to)};

        mCompareTypeWV.setEntries(mCompareTypes);
        mCompareTypeWV.setCyclic(false);

        initStatusBar();
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        mSceneManager = new SceneManager(this);
        mHandler = new CallbackHandler(this);

        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<PropertyValue, BaseViewHolder>(R.layout.item_simple_checked, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, PropertyValue value) {
                int pos = mList.indexOf(value);
                holder.setText(R.id.item_title, value.getKey())
                        .setVisible(R.id.item_checked, value.isChecked())
                        .setVisible(R.id.item_divider, pos != 0);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setChecked(i == position);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mValueRV.setLayoutManager(mLayoutManager);
        mValueRV.setAdapter(mAdapter);

        mServiceAdapter = new BaseQuickAdapter<ServiceInputData, BaseViewHolder>(R.layout.item_service_inputdata, mServiceInputDataList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ServiceInputData item) {
                String content = "";
                for (Map.Entry<String, Object> map : item.getDataTypeMap().entrySet()) {
                    if (map.getValue().toString().equals(item.getSelectValue())) {
                        content = map.getKey();
                        break;
                    }
                }

                holder.setText(R.id.title_tv, item.getName())
                        .setText(R.id.content_tv, content);
            }
        };
        mServiceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ServiceInputData item = mServiceInputDataList.get(position);
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(EditPropertyValueForActionActivity.this);
                for (Map.Entry<String, Object> map : item.getDataTypeMap().entrySet()) {
                    builder.addItem(map.getKey());
                }
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int pos, String tag) {
                        ViseLog.d(tag);
                        Map<String, Object> map = item.getDataTypeMap();
                        String value = (String) map.get(tag);
                        mServiceInputDataList.get(position).setSelectValue(value);
                        mServiceInputDataList.get(position).setSelectName(tag);
                        dialog.dismiss();
                        mServiceAdapter.notifyDataSetChanged();
                    }
                });
                builder.build().show();
            }
        });
        mServiceLayoutManager = new LinearLayoutManager(this);
        mServiceLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mServiceRV.setLayoutManager(mServiceLayoutManager);
        mServiceRV.setAdapter(mServiceAdapter);

        EventBus.getDefault().register(this);

        tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdentifier.getType() == 1) {
                    // 属性
                    if (mEventLayout.getVisibility() == View.VISIBLE) {
                        String compareValue = mEventValueList.get(mCompareValueWV.getCurrentIndex());
                        Object result = 0;
                        if (compareValue.contains(".")) result = Double.parseDouble(compareValue);
                        else result = Integer.parseInt(compareValue);

                        ActionEntry.Property property = (ActionEntry.Property) mIdentifier.getObject();
                        property.setPropertyValue(result);
                        mIdentifier.setObject(property);
                        mIdentifier.setDesc(mIdentifier.getName() + getString(R.string.equal_to) + compareValue + mUnitTV.getText().toString());

                        EventBus.getDefault().unregister(EditPropertyValueForActionActivity.this);
                        EventBus.getDefault().postSticky(mIdentifier);

                        Intent intent = new Intent(EditPropertyValueForActionActivity.this, NewSceneActivity.class);
                        startActivity(intent);
                    } else if (mValueRV.getVisibility() == View.VISIBLE) {
                        ActionEntry.Property property = (ActionEntry.Property) mIdentifier.getObject();
                        PropertyValue value = new PropertyValue();
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).isChecked()) {
                                value = mList.get(i);
                                break;
                            }
                        }
                        Object result = null;
                        if (((String) value.getValue()).contains("."))
                            result = Double.parseDouble((String) value.getValue());
                        else result = Integer.parseInt((String) value.getValue());
                        property.setPropertyName(property.getPropertyName());
                        property.setPropertyValue(result);

                        mIdentifier.setValueName(value.getKey());
                        mIdentifier.setObject(property);

                        EventBus.getDefault().unregister(EditPropertyValueForActionActivity.this);
                        EventBus.getDefault().postSticky(mIdentifier);

                        Intent intent = new Intent(EditPropertyValueForActionActivity.this, NewSceneActivity.class);
                        startActivity(intent);
                    }
                } else if (mIdentifier.getType() == 3) {
                    // 事件
                    if (mEventLayout.getVisibility() == View.VISIBLE) {
                        String compareValue = mEventValueList.get(mCompareValueWV.getCurrentIndex());
                        Object result = 0;
                        if (compareValue.contains(".")) result = Double.parseDouble(compareValue);
                        else result = Integer.parseInt(compareValue);

                        CaConditionEntry.Event event = (CaConditionEntry.Event) mIdentifier.getObject();
                        event.setProductKey(event.getProductKey());
                        event.setDeviceName(event.getDeviceName());
                        event.setEventCode(event.getEventCode());
                        event.setPropertyName(mEventValue.getIdentifier());
                        event.setCompareType(mCompareTypes[mCompareTypeWV.getCurrentIndex()]);
                        event.setCompareValue(result);

                        mIdentifier.setValueName(mEventValue.getName());
                        mIdentifier.setObject(event);
                        mIdentifier.setDesc(mIdentifier.getName() + getCompareTypeString(event.getCompareType()) + compareValue + mUnitTV.getText().toString());

                        EventBus.getDefault().unregister(EditPropertyValueForActionActivity.this);
                        EventBus.getDefault().postSticky(mIdentifier);

                        Intent intent = new Intent(EditPropertyValueForActionActivity.this, NewSceneActivity.class);
                        startActivity(intent);
                    } else if (mValueRV.getVisibility() == View.VISIBLE) {
                        CaConditionEntry.Event event = (CaConditionEntry.Event) mIdentifier.getObject();
                        PropertyValue value = new PropertyValue();
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).isChecked()) {
                                value = mList.get(i);
                                break;
                            }
                        }

                        String compareValue = (String) value.getValue();
                        Object result = 0;
                        if (compareValue.contains(".")) result = Double.parseDouble(compareValue);
                        else result = Integer.parseInt(compareValue);

                        event.setCompareValue(result);
                        mIdentifier.setValueName(value.getKey());
                        mIdentifier.setObject(event);

                        EventBus.getDefault().unregister(EditPropertyValueForActionActivity.this);
                        EventBus.getDefault().postSticky(mIdentifier);

                        Intent intent = new Intent(EditPropertyValueForActionActivity.this, NewSceneActivity.class);
                        startActivity(intent);
                    }
                } else if (mIdentifier.getType() == 2) {
                    // 服务
                    ViseLog.d(new Gson().toJson(mIdentifier));
                    ViseLog.d(new Gson().toJson(mServiceInputDataList));

                    Map<String, Object> map = new HashMap<>();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < mServiceInputDataList.size(); i++) {
                        ServiceInputData data = mServiceInputDataList.get(i);
                        String value = data.getSelectValue();

                        if (i == 0) {
                            stringBuilder.append(data.getName() + data.getSelectName());
                        } else {
                            stringBuilder.append("，"+data.getName() + data.getSelectName());
                        }

                        if ("enum".equals(data.getDataType().getType()) || "bool".equals(data.getDataType().getType())) {
                            if (value.contains(".")) {
                                map.put(data.getIdentifier(), Double.parseDouble(value));
                            } else {
                                map.put(data.getIdentifier(), Integer.parseInt(value));
                            }
                        } else
                            map.put(data.getIdentifier(), (Object) data.getSelectValue());
                    }
                    ((ActionEntry.InvokeService) mIdentifier.getObject()).setServiceArgs(map);
                    mIdentifier.setDesc(stringBuilder.toString());

                    EventBus.getDefault().unregister(EditPropertyValueForActionActivity.this);
                    EventBus.getDefault().postSticky(mIdentifier);

                    Intent intent = new Intent(EditPropertyValueForActionActivity.this, NewSceneActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String getCompareTypeString(String compareType) {
        if ("<".equals(compareType))
            return getString(R.string.less_than);
        else if ("<=".equals(compareType))
            return getString(R.string.less_than_or_equal_to);
        else if ("==".equals(compareType))
            return getString(R.string.equal_to);
        else if (">".equals(compareType))
            return getString(R.string.greater_than);
        else if (">=".equals(compareType))
            return getString(R.string.great_than_or_equal_to);
        else if ("!=".equals(compareType))
            return getString(R.string.is_not_equal_to);
        return "";
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(IdentifierItemForCA item) {
        mIdentifier = item;
        mTitle.setText(mIdentifier.getName());

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        mSceneManager.queryTSLListForCA(item.getIotId(), 2, mCommitFailureHandler, mResponseErrorHandler, mHandler);

        EventBus.getDefault().removeStickyEvent(item);
    }

    private class CallbackHandler extends Handler {
        private WeakReference<Activity> weakRf;

        public CallbackHandler(Activity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (weakRf.get() == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_TSL_LIST: {
                    JSONObject o = JSON.parseObject((String) msg.obj);
                    ViseLog.d(new Gson().toJson(o));
                    JSONObject abilityDsl = o.getJSONObject("abilityDsl");
                    switch (mIdentifier.getType()) {
                        case 1: {
                            // 属性
                            JSONArray array = abilityDsl.getJSONArray("properties");
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject o1 = array.getJSONObject(i);
                                ActionEntry.Property property = (ActionEntry.Property) mIdentifier.getObject();
                                if (property.getPropertyName().equals(o1.getString("identifier"))) {
                                    JSONObject dataType = o1.getJSONObject("dataType");
                                    if ("bool".equals(dataType.getString("type")) || "enum".equals(dataType.getString("type"))) {
                                        mList.clear();
                                        mValueRV.setVisibility(View.VISIBLE);
                                        mEventLayout.setVisibility(View.GONE);
                                        for (Map.Entry<String, Object> map : dataType.getJSONObject("specs").entrySet()) {
                                            PropertyValue value = new PropertyValue();
                                            value.setKey((String) map.getValue());
                                            value.setValue(map.getKey());
                                            if (property.getPropertyValue() != null && map.getKey().equals(property.getPropertyValue() + ""))
                                                value.setChecked(true);
                                            mList.add(value);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } else if ("int".equals(dataType.getString("type")) || "double".equals(dataType.getString("type"))) {
                                        mValueRV.setVisibility(View.GONE);
                                        mEventLayout.setVisibility(View.VISIBLE);
                                        mEventValue = new EventValue();
                                        mNameTV.setText(mIdentifier.getName());
                                        for (Map.Entry<String, Object> map : dataType.getJSONObject("specs").entrySet()) {
                                            if ("max".equals(map.getKey()))
                                                mEventValue.setMax((String) map.getValue());
                                            else if ("min".equals(map.getKey()))
                                                mEventValue.setMin((String) map.getValue());
                                            else if ("step".equals(map.getKey()))
                                                mEventValue.setStep((String) map.getValue());
                                            else if ("unitName".equals(map.getKey()))
                                                mEventValue.setUnitName((String) map.getValue());
                                            else if ("unit".equals(map.getKey()))
                                                mEventValue.setUnit((String) map.getValue());
                                        }
                                        mEventValue.setType(dataType.getString("type"));

                                        mIdentifier.setValueName(o1.getString("name"));

                                        int currentPos = 0;
                                        mEventValueList.clear();
                                        if ("int".equals(mEventValue.getType())) {
                                            int min = Integer.parseInt(mEventValue.getMin());
                                            int max = Integer.parseInt(mEventValue.getMax());
                                            int step = Integer.parseInt(mEventValue.getStep());
                                            int z = 0;
                                            for (int j = min; j <= max; ) {
                                                mEventValueList.add(String.valueOf(j));

                                                if (property.getPropertyValue() != null && (int) property.getPropertyValue() == j)
                                                    currentPos = z;
                                                z++;
                                                j = j + step;
                                            }
                                        } else if ("double".equals(mEventValue.getType())) {
                                            double min = Double.parseDouble(mEventValue.getMin());
                                            double max = Double.parseDouble(mEventValue.getMax());
                                            double step = Double.parseDouble(mEventValue.getStep());
                                            int z = 0;
                                            for (double j = min; j <= max; ) {
                                                mEventValueList.add(String.valueOf(j));

                                                if (property.getPropertyValue() != null && Double.parseDouble(property.getPropertyValue().toString()) == j)
                                                    currentPos = z;
                                                z++;

                                                BigDecimal jBig = new BigDecimal(Double.toString(j));
                                                BigDecimal sBig = new BigDecimal(Double.toString(step));
                                                j = jBig.add(sBig).doubleValue();
                                            }
                                        }
                                        mCompareValueWV.setEntries(mEventValueList);
                                        mCompareValueWV.setCurrentIndex(currentPos);

                                        /*if ("<".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(0);
                                        else if ("<=".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(1);
                                        else if ("==".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(2);
                                        else if (">=".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(3);
                                        else if (">".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(4);
                                        else if ("!=".equals(property.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(5);*/

                                        if (mEventValue.getUnit() != null && mEventValue.getUnit().length() > 0) {
                                            mUnitTV.setVisibility(View.VISIBLE);
                                            mUnitTV.setText(mEventValue.getUnit());
                                        } else if (mEventValue.getUnitName() != null && mEventValue.getUnitName().length() > 0) {
                                            mUnitTV.setVisibility(View.VISIBLE);
                                            mUnitTV.setText(mEventValue.getUnitName());
                                        } else mUnitTV.setVisibility(View.GONE);
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case 3: {
                            // 事件
                            JSONArray array = abilityDsl.getJSONArray("events");
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject o1 = array.getJSONObject(i);
                                CaConditionEntry.Event event = (CaConditionEntry.Event) mIdentifier.getObject();
                                if (event.getEventCode().equals(o1.getString("identifier"))) {
                                    JSONArray outputDatas = o1.getJSONArray("outputData");
                                    JSONObject outputData = outputDatas.getJSONObject(0);
                                    JSONObject dataType = outputData.getJSONObject("dataType");
                                    mNameTV.setText(outputData.getString("name"));
                                    if ("int".equals(dataType.getString("type")) || "double".equals(dataType.getString("type"))) {
                                        mValueRV.setVisibility(View.GONE);
                                        mEventLayout.setVisibility(View.VISIBLE);
                                        mEventValue = new EventValue();
                                        mEventValue.setName(outputData.getString("name"));
                                        mEventValue.setIdentifier(outputData.getString("identifier"));
                                        for (Map.Entry<String, Object> map : dataType.getJSONObject("specs").entrySet()) {
                                            if ("max".equals(map.getKey()))
                                                mEventValue.setMax((String) map.getValue());
                                            else if ("min".equals(map.getKey()))
                                                mEventValue.setMin((String) map.getValue());
                                            else if ("step".equals(map.getKey()))
                                                mEventValue.setStep((String) map.getValue());
                                            else if ("unitName".equals(map.getKey()))
                                                mEventValue.setUnitName((String) map.getValue());
                                        }
                                        mEventValue.setType(dataType.getString("type"));

                                        int currentPos = 0;
                                        mEventValueList.clear();
                                        if ("int".equals(mEventValue.getType())) {
                                            int min = Integer.parseInt(mEventValue.getMin());
                                            int max = Integer.parseInt(mEventValue.getMax());
                                            int step = Integer.parseInt(mEventValue.getStep());
                                            int z = 0;
                                            for (int j = min; j <= max; ) {
                                                mEventValueList.add(String.valueOf(j));

                                                if (event.getCompareValue() != null && (int) event.getCompareValue() == j)
                                                    currentPos = z;
                                                z++;
                                                j = j + step;
                                            }
                                        } else if ("double".equals(mEventValue.getType())) {
                                            double min = Double.parseDouble(mEventValue.getMin());
                                            double max = Double.parseDouble(mEventValue.getMax());
                                            double step = Double.parseDouble(mEventValue.getStep());
                                            int z = 0;
                                            for (double j = min; j <= max; ) {
                                                mEventValueList.add(String.valueOf(j));

                                                if (event.getCompareValue() != null && Double.parseDouble(event.getCompareValue().toString()) == j)
                                                    currentPos = z;
                                                z++;
                                                BigDecimal jBig = new BigDecimal(Double.toString(j));
                                                BigDecimal sBig = new BigDecimal(Double.toString(step));
                                                j = jBig.add(sBig).doubleValue();
                                            }
                                        }
                                        mCompareValueWV.setEntries(mEventValueList);
                                        mCompareValueWV.setCurrentIndex(currentPos);

                                        /*if ("<".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(0);
                                        else if ("<=".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(1);
                                        else if ("==".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(2);
                                        else if (">=".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(3);
                                        else if (">".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(4);
                                        else if ("!=".equals(event.getCompareType()))
                                            mCompareTypeWV.setCurrentIndex(5);*/

                                        if (mEventValue.getUnit() != null && mEventValue.getUnit().length() > 0) {
                                            mUnitTV.setVisibility(View.VISIBLE);
                                            mUnitTV.setText(mEventValue.getUnit());
                                        } else if (mEventValue.getUnitName() != null && mEventValue.getUnitName().length() > 0) {
                                            mUnitTV.setVisibility(View.VISIBLE);
                                            mUnitTV.setText(mEventValue.getUnitName());
                                        } else mUnitTV.setVisibility(View.GONE);
                                    } else if ("enum".equals(dataType.getString("type"))) {
                                        mList.clear();
                                        mValueRV.setVisibility(View.VISIBLE);
                                        mEventLayout.setVisibility(View.GONE);
                                        ((CaConditionEntry.Event) mIdentifier.getObject()).setPropertyName(outputData.getString("identifier"));
                                        ((CaConditionEntry.Event) mIdentifier.getObject()).setCompareType("==");
                                        for (Map.Entry<String, Object> map : dataType.getJSONObject("specs").entrySet()) {
                                            PropertyValue value = new PropertyValue();
                                            value.setKey((String) map.getValue());
                                            value.setValue(map.getKey());
                                            if (event.getCompareValue() != null && map.getKey().equals(event.getCompareValue() + ""))
                                                value.setChecked(true);
                                            mList.add(value);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case 2: {
                            // 服务
                            mEventLayout.setVisibility(View.GONE);
                            mValueRV.setVisibility(View.GONE);
                            mServiceRV.setVisibility(View.VISIBLE);
                            JSONArray array = abilityDsl.getJSONArray("services");
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject service = array.getJSONObject(i);
                                ActionEntry.InvokeService invokeService = (ActionEntry.InvokeService) mIdentifier.getObject();
                                if (invokeService.getServiceName().equals(service.getString("identifier"))) {
                                    mServiceInputDataList.clear();
                                    JSONArray inputDatas = service.getJSONArray("inputData");
                                    Map<String, Object> serviceArgs = (Map<String, Object>) invokeService.getServiceArgs();
                                    for (int j = 0; j < inputDatas.size(); j++) {
                                        ServiceInputData dataEntry = new ServiceInputData();
                                        JSONObject inputData = inputDatas.getJSONObject(j);
                                        JSONObject dataType = inputData.getJSONObject("dataType");
                                        dataEntry.setIdentifier(inputData.getString("identifier"));
                                        dataEntry.setName(inputData.getString("name"));
                                        dataEntry.setType(dataType.getString("type"));
                                        if ("enum".equals(dataType.getString("type")) || "bool".equals(dataType.getString("type"))) {
                                            for (Map.Entry<String, Object> map : dataType.getJSONObject("specs").entrySet()) {
                                                dataEntry.put((String) map.getValue(), map.getKey());
                                                if (serviceArgs == null) {
                                                    dataEntry.setSelectValue(map.getKey());
                                                    dataEntry.setSelectName(map.getValue().toString());
                                                } else {
                                                    dataEntry.setSelectValue(String.valueOf(serviceArgs.get(dataEntry.getIdentifier())));
                                                    dataEntry.setSelectName(dataEntry.getIdentifier());
                                                }
                                            }
                                        }
                                        ViseLog.d(new Gson().toJson(dataEntry));
                                        mServiceInputDataList.add(dataEntry);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    QMUITipDialogUtil.dismiss();
                    break;
                }
            }
        }
    }

    private class PropertyValue {
        private String key;
        private Object value;
        private boolean isChecked = false;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    private class EventValue {
        private String max;
        private String min;
        private String step;
        private String unitName;
        private String name = "";
        private String identifier;
        private String type;
        private String unit;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public String getUnitName() {
            if (unitName == null || "无".equals(unitName))
                unitName = "";
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}