package com.xiezhu.jzj.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.databinding.ActivitySwitchSceneBinding;
import com.xiezhu.jzj.event.RefreshData;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.EScene;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.model.ItemAction;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.DeviceBuffer;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.utility.ToastUtils;
import com.xiezhu.jzj.view.BaseActivity;
import com.xiezhu.jzj.view.DeviceActionActivity;
import com.xiezhu.jzj.view.SceneSwitchDeviceListActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwitchSceneActivity extends BaseActivity {
    private ActivitySwitchSceneBinding mViewBinding;

    private EScene.sceneListItemEntry mScene;
    private SceneManager mSceneManager;
    private String mIotID;
    private BaseQuickAdapter mAdapter;
    private List<ItemAction> mList = new ArrayList<>();

    private int mEditPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySwitchSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mViewBinding.includeToolbar.tvToolbarRight.setText("保存");
        mScene = (EScene.sceneListItemEntry) getIntent().getSerializableExtra("extra");
        mIotID = getIntent().getStringExtra("iotID");
        mSceneManager = new SceneManager(this);
        initView();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void actionChoose(List<ItemAction> itemActions) {
        Iterator<ItemAction> iterator = itemActions.iterator();
        while (iterator.hasNext()) {
            ItemAction itemAction = iterator.next();
            for (int j = 0; j < mList.size(); j++) {
                ItemAction itemAction1 = mList.get(j);
                if (itemAction.getIotId().equals(itemAction1.getIotId()) &&
                        itemAction.getIdentifier().equals(itemAction1.getIdentifier())) {
                    itemAction1.setActionKey(itemAction.getActionKey());
                    itemAction1.setActionValue(itemAction.getActionValue());
                    iterator.remove();
                }
            }
        }

        if (mEditPos > -1) mList.remove(mEditPos);
        mList.addAll(itemActions);
        mAdapter.notifyDataSetChanged();
    }

    private String getDevIcon(String pk) {
        Map<String, EDevice.deviceEntry> entryMap = DeviceBuffer.getAllDeviceInformation();
        Iterator<Map.Entry<String, EDevice.deviceEntry>> entries = entryMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, EDevice.deviceEntry> entry = entries.next();
            if (pk.equals(entry.getValue().productKey)) {
                return entry.getValue().image;
            }
        }
        return null;
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<ItemAction, BaseViewHolder>(R.layout.item_action, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, ItemAction visitable) {
                baseViewHolder.setText(R.id.actionDeviceName, visitable.getDeviceName());
                baseViewHolder.setText(R.id.actionContent, visitable.getActionName() + visitable.getActionKey());
                //baseViewHolder.setImageResource(R.id.actionImage, ImageProvider.genProductIcon(visitable.getProductKey()));
                ImageView image = baseViewHolder.getView(R.id.actionImage);
                Glide.with(SwitchSceneActivity.this).load(getDevIcon(visitable.getProductKey())).into(image);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mEditPos = position;
                ItemAction itemAction = mList.get(position);
                ViseLog.d(new Gson().toJson(itemAction));
                DeviceActionActivity.start(mActivity, itemAction.getIotId(), itemAction.getDeviceName(), itemAction.getIdentifier(), String.valueOf(itemAction.getActionValue()));
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(SwitchSceneActivity.this).create();
                alert.setIcon(R.drawable.dialog_quest);
                alert.setTitle(R.string.dialog_title);
                alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_action));
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
                    public void onClick(DialogInterface dialog, int pos) {
                        mList.remove(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                alert.show();
                return false;
            }
        });
    }

    // 显示设备名称修改对话框
    private void showDeviceNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText("输入场景名称");
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(mViewBinding.sceneName.getText());
        if (mViewBinding.sceneName.getText() != null && mViewBinding.sceneName.getText().toString() != null) {
            int length = mViewBinding.sceneName.getText().toString().length();
            nameEt.setSelection(length);
        }
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_320);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEt.getText().toString().trim();
                if (nameStr.length() > 10) {
                    ToastUtils.showToastCentrally(mActivity, "场景名称过长");
                    return;
                }
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    mViewBinding.sceneName.setText(nameStr);
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void initView() {
        initAdapter();
        mViewBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewBinding.mRecyclerView.setAdapter(mAdapter);
        if (mScene == null) {
            //创建场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("创建场景");
            mViewBinding.deleteButton.setVisibility(View.GONE);
        } else {
            //编辑场景
            mViewBinding.includeToolbar.tvToolbarTitle.setText("编辑场景");
            mViewBinding.sceneName.setText(mScene.name);
            mSceneManager.querySceneDetail(mScene.id, CScene.TYPE_MANUAL, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        }
        mViewBinding.nameLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.addBtn.setOnClickListener(this::onViewClicked);
        mViewBinding.deleteButton.setOnClickListener(this::onViewClicked);
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
    }

    public static void start(Context context, EScene.sceneListItemEntry item, String iotID) {
        Intent intent = new Intent(context, SwitchSceneActivity.class);
        intent.putExtra("extra", item);
        intent.putExtra("iotID", iotID);
        context.startActivity(intent);
    }

    private final Handler processDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_CREATESCENE:
                    // 处理创建场景结果
                    String sceneId_create = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_create != null && sceneId_create.length() > 0) {
                        ToastUtils.showToastCentrally(SwitchSceneActivity.this, String.format(getString(R.string.scene_maintain_create_success),
                                mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(SwitchSceneActivity.this, String.format(getString(R.string.scene_maintain_create_failed),
                                mViewBinding.sceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE:
                    // 处理修改场景结果
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        ToastUtils.showToastCentrally(SwitchSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_success),
                                mViewBinding.sceneName.getText().toString()));
                        // 发送刷新列表数据事件
                        RefreshData.refreshSceneListData();
                    } else {
                        ToastUtils.showToastCentrally(SwitchSceneActivity.this, String.format(getString(R.string.scene_maintain_edit_failed),
                                mViewBinding.sceneName.getText().toString()));
                    }
                    finish();
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    // 处理删除列表数据
                    String sceneId = CloudDataParser.processDeleteSceneResult((String) msg.obj);
                    ToastUtils.showToastCentrally(mActivity, R.string.scene_delete_sucess);
                    finish();
                    RefreshData.refreshSceneListData();
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详情
                    JSONObject result = JSON.parseObject((String) msg.obj);
                    JSONArray actionsJson = result.getJSONArray("actionsJson");
                    for (int j = 0; j < actionsJson.size(); j++) {
                        JSONObject jsonObject = JSON.parseObject(actionsJson.getString(j));
                        JSONObject params = jsonObject.getJSONObject("params");

                        String iotID = params.getString("iotId");
                        String identifier = params.getString("identifier");
                        String name = params.getString("localizedPropertyName");
                        try {
                            if (DeviceBuffer.getExtendedInfo(iotID) != null) {
                                String n = DeviceBuffer.getExtendedInfo(iotID).getString(identifier);
                                if (n != null)
                                    name = n;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ItemAction<String> itemAction = new ItemAction<>();
                        itemAction.setIotId(params.getString("iotId"));
                        itemAction.setIdentifier(identifier);
                        itemAction.setProductKey(params.getString("productKey"));
                        itemAction.setActionKey(params.getString("localizedCompareValueName"));
                        itemAction.setActionName(name);
                        itemAction.setActionValue(params.getString("compareValue"));
                        mSceneManager.getDeviceAction(params.getString("iotId"), mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                        EDevice.deviceEntry device = DeviceBuffer.getDeviceInformation(params.getString("iotId"));
                        if (device != null) {
                            itemAction.setDeviceName(device.nickName);
                            mList.add(itemAction);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_SCENE_ABILITY_TSL:
                    JSONObject tsl = JSON.parseObject((String) msg.obj);
                    JSONObject abilityDsl = tsl.getJSONObject("abilityDsl");
                    String productKey = abilityDsl.getJSONObject("profile").getString("productKey");
                    for (int i = 0; i < mList.size(); i++) {
                        ItemAction itemAction = mList.get(i);
                        if (itemAction.getProductKey().equals(productKey)) {
                            JSONArray properties = abilityDsl.getJSONArray("properties");
                            for (int j = 0; j < properties.size(); j++) {
                                JSONObject jsonObject = properties.getJSONObject(j);
                                if (jsonObject.getString("identifier").equals(itemAction.getIdentifier())) {

                                    String identifier = itemAction.getIdentifier();
                                    String name = null;

                                    try {
                                        if (DeviceBuffer.getExtendedInfo(itemAction.getIotId()) != null) {
                                            name = DeviceBuffer.getExtendedInfo(itemAction.getIotId()).getString(identifier);
                                            if (name == null)
                                                name = jsonObject.getString("name").trim();
                                        } else name = jsonObject.getString("name").trim();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    itemAction.setActionName(name);
                                    JSONObject dataType = jsonObject.getJSONObject("dataType");
                                    JSONObject specs = dataType.getJSONObject("specs");
                                    switch (dataType.getString("type")) {
                                        case "enum":
                                        case "bool":
                                            for (Map.Entry<String, Object> map : specs.entrySet()) {
                                                if (itemAction.getActionValue().toString().equals(map.getKey())) {
                                                    itemAction.setActionKey(map.getValue().toString());
                                                }
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void notifyResponseError(int type) {
        super.notifyResponseError(type);
        if (type == 28700) {
            // 设备未和用户绑定
            if (mScene != null) {
                new SceneManager(this).deleteScene(mScene.id, null, null, processDataHandler);
            }
        }
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.nameLayout) {
            showDeviceNameDialogEdit();
        } else if (view.getId() == R.id.addBtn) {
            mEditPos = -1;
            SceneSwitchDeviceListActivity.start(this);
        } else if (view.getId() == R.id.deleteButton) {
            mSceneManager.deleteScene(mScene.id, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
        } else if (view.getId() == R.id.tv_toolbar_right) {
            if (mScene == null) {
                //创建场景
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, mViewBinding.sceneName.getText().toString(), mIotID);
                baseInfoEntry.enable = true;
                List<EScene.responseEntry> parameters = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    ItemAction itemAction = mList.get(i);
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = itemAction.getIotId();
                    entry.state = new ETSL.stateEntry("", itemAction.getIdentifier(), "", itemAction.getActionValue().toString());
                    parameters.add(entry);
                }
                mSceneManager.createCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
            } else {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, mViewBinding.sceneName.getText().toString(), "mode == CA," + mIotID);
                baseInfoEntry.enable = true;
                baseInfoEntry.sceneId = mScene.id;
                List<EScene.responseEntry> parameters = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    ItemAction itemAction = mList.get(i);
                    EScene.responseEntry entry = new EScene.responseEntry();
                    entry.iotId = itemAction.getIotId();
                    entry.state = new ETSL.stateEntry("", itemAction.getIdentifier(), "", itemAction.getActionValue().toString());
                    parameters.add(entry);
                }
                mSceneManager.updateCAScene(baseInfoEntry, parameters, mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
            }
        }
    }
}
