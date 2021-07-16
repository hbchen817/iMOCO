package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.databinding.ActivityDeviceActionBinding;
import com.xiezhu.jzj.model.ItemAction;
import com.xiezhu.jzj.presenter.DeviceBuffer;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceActionActivity extends BaseActivity {
    private ActivityDeviceActionBinding mViewBinding;

    private String mIotID;
    private String mDeviceName;
    private List<ItemAction> mList = new ArrayList<>();
    private BaseQuickAdapter<ItemAction, BaseViewHolder> mAdapter;
    private SceneManager mSceneManager;
    private MyHandler mMyHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityDeviceActionBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mSceneManager = new SceneManager(this);
        mMyHandler = new MyHandler(this);
        mIotID = getIntent().getStringExtra("iotID");
        mDeviceName = getIntent().getStringExtra("deviceName");
        mIdentifier = getIntent().getStringExtra("identifier");
        mActionValue = getIntent().getStringExtra("actionValue");

        initView();
        getData();

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

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText("选择动作");
        mViewBinding.includeToolbar.tvToolbarRight.setText("保存");
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(v -> {
            List<ItemAction> mSelectList = new ArrayList<>();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).isSelected()) {
                    mSelectList.add(mList.get(i));
                }
            }

            if (mSelectList.size() > 0) {
                EventBus.getDefault().post(mSelectList);
                //finish();
                Intent intent = new Intent(DeviceActionActivity.this, SwitchSceneActivity.class);
                startActivity(intent);
            } else {
                ToastUtils.showToastCentrally(mActivity, "请选择动作");
            }
        });
        initAdapter();
        mViewBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewBinding.mRecyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        mSceneManager.getDeviceAction(mIotID, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemAction, BaseViewHolder>(R.layout.item_action_choice, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, ItemAction itemAction) {
                baseViewHolder.setVisible(R.id.chooseImage, itemAction.isSelected());
                baseViewHolder.setText(R.id.actionName, itemAction.getActionName() + itemAction.getActionKey());
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ItemAction selectedItem = mList.get(position);
                selectedItem.setSelected(!selectedItem.isSelected());
                for (int i = 0; i < mList.size(); i++) {
                    if (i == position) {
                        continue;
                    }
                    if (mList.get(i).getActionName().equals(selectedItem.getActionName())) {
                        mList.get(i).setSelected(false);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private static class MyHandler extends Handler {
        final WeakReference<DeviceActionActivity> mWeakReference;

        public MyHandler(DeviceActionActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DeviceActionActivity activity = mWeakReference.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_SCENE_ABILITY_TSL) {
                JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                JSONArray simplifyAbilityDTOs = jsonObject.getJSONArray("simplifyAbilityDTOs");
                JSONObject abilityDsl = jsonObject.getJSONObject("abilityDsl");
                JSONArray services = abilityDsl.getJSONArray("services");
                JSONArray properties = abilityDsl.getJSONArray("properties");
                JSONArray events = abilityDsl.getJSONArray("events");
                int size = simplifyAbilityDTOs.size();
                for (int i = 0; i < size; i++) {
                    JSONObject ability = simplifyAbilityDTOs.getJSONObject(i);
                    int type = ability.getIntValue("type");//功能类型：1-属性；2-服务；3-事件
                    switch (type) {
                        case 1:
                            int propertiesSize = properties.size();
                            for (int j = 0; j < propertiesSize; j++) {
                                JSONObject property = properties.getJSONObject(j);
                                if (property.getString("identifier").equals(ability.getString("identifier"))) {
                                    JSONObject dataType = property.getJSONObject("dataType");
                                    String dataTypeValue = dataType.getString("type");
                                    JSONObject specs = dataType.getJSONObject("specs");
                                    switch (dataTypeValue) {
                                        case "enum":
                                        case "bool":
                                            for (Map.Entry<String, Object> map : specs.entrySet()) {
                                                String identifier = property.getString("identifier").trim();
                                                String name = null;

                                                try {
                                                    if (DeviceBuffer.getExtendedInfo(activity.mIotID) != null) {
                                                        name = DeviceBuffer.getExtendedInfo(activity.mIotID).getString(identifier);
                                                        if (name == null || name.length() == 0)
                                                            name = property.getString("name").trim();
                                                    } else name = property.getString("name").trim();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                ItemAction<String> itemAction = new ItemAction<String>();
                                                itemAction.setActionName(name);
                                                itemAction.setIdentifier(identifier);
                                                itemAction.setActionKey((String) map.getValue());
                                                itemAction.setActionValue(map.getKey());
                                                itemAction.setIotId(activity.mIotID);
                                                itemAction.setDeviceName(activity.mDeviceName);
                                                itemAction.setProductKey(abilityDsl.getJSONObject("profile").getString("productKey").trim());

                                                if (activity.mIdentifier != null && activity.mIdentifier.length() > 0 &&
                                                        activity.mActionValue != null && activity.mActionValue.length() > 0) {
                                                    if (activity.mIdentifier.equals(itemAction.getIdentifier()) && activity.mActionValue.equals(itemAction.getActionValue())) {
                                                        itemAction.setSelected(true);
                                                    }
                                                }

                                                activity.mList.add(itemAction);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                            break;
                    }
                }
                activity.mAdapter.notifyDataSetChanged();
            }
        }
    }


    public static void start(Context context, String iotID, String deviceName) {
        Intent intent = new Intent(context, DeviceActionActivity.class);
        intent.putExtra("iotID", iotID);
        intent.putExtra("deviceName", deviceName);
        context.startActivity(intent);
    }

    private String mIdentifier;
    private String mActionValue;

    public static void start(Context context, String iotID, String deviceName, String identifier, String actionValue) {
        Intent intent = new Intent(context, DeviceActionActivity.class);
        intent.putExtra("iotID", iotID);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("identifier", identifier);
        intent.putExtra("actionValue", actionValue);
        context.startActivity(intent);
    }
}
