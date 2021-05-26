package com.laffey.smart.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLightSceneListBinding;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwitchSceneListActivity extends BaseActivity {
    private ActivityLightSceneListBinding mViewBinding;

    private String mIotId;
    private SceneManager mSceneManager;
    private List<EScene.sceneListItemEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder> mAdapter;
    private int mClickPosition = -1;
    private String mKeyCode;
    private int mBindPosition;
    private JSONObject mExtendedJsonObject;
    private String mAutoSceneID;
    private TypedArray mSceneBgs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLightSceneListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mIotId = getIntent().getStringExtra("extra");
        mKeyCode = getIntent().getStringExtra("keyCode");
        mViewBinding.includeToolbar.tvToolbarTitle.setText("场景绑定");
        mSceneManager = new SceneManager(this);
        mSceneBgs = getResources().obtainTypedArray(R.array.scene_bgs);
        initAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mViewBinding.recycleView.setLayoutManager(gridLayoutManager);
        mViewBinding.recycleView.setAdapter(mAdapter);
        mViewBinding.createSceneView.setOnClickListener(this::onViewClicked);
        getList();

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

    @Subscribe
    public void refreshSceneList(EEvent eventEntry) {
        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            getList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getList() {
        mClickPosition = -1;
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20,
                mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            if (item.description.contains(mIotId)) {
                                mList.add(item);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL,
                                    sceneList.pageNo + 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        ViseLog.d("处理获取拓展数据 = " + (String) msg.obj);
                        mExtendedJsonObject = JSON.parseObject((String) msg.obj);
                        if (!mExtendedJsonObject.isEmpty()) {
                            //更换绑定场景
                            String autoSceneId = mExtendedJsonObject.getString("asId");
                            EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                                    CScene.TYPE_AUTOMATIC, mList.get(mBindPosition).name, mIotId);
                            baseInfoEntry.enable = true;
                            baseInfoEntry.sceneId = autoSceneId;
                            EScene.triggerEntry entry = new EScene.triggerEntry();
                            EDevice.deviceEntry deviceInformation = DeviceBuffer.getDeviceInformation(mIotId);
                            entry.productKey = deviceInformation.productKey;
                            entry.deviceName = deviceInformation.deviceName;
                            entry.state = new ETSL.stateEntry("", "", "", mKeyCode);
                            mSceneManager.updateSwitchAutoScene(baseInfoEntry, entry, mList.get(mBindPosition).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                            break;
                        }
                    }
                    createAutoScene();
                    break;
                case Constant.MSG_CALLBACK_CREATE_SWITCH_AUTO_SCENE:
                    mAutoSceneID = (String) msg.obj;
                    String sceneId_update = CloudDataParser.processCreateSceneResult((String) msg.obj);
                    if (sceneId_update != null && sceneId_update.length() > 0) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("asId", mAutoSceneID);
                        jsonObject.put("keyNo", mKeyCode);
                        jsonObject.put("name", mList.get(mBindPosition).name);
                        jsonObject.put("msId", mList.get(mBindPosition).id);
                        mSceneManager.setExtendedProperty(mIotId, mKeyCode, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    } else {
                        ToastUtils.showToastCentrally(mActivity, "绑定失败");
                    }
                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET:
                    ToastUtils.showToastCentrally(mActivity, "绑定成功");
                    EventBus.getDefault().post(new SceneBindEvent(mList.get(mBindPosition).name));
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATESCENE: {
                    mAutoSceneID = (String) msg.obj;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("asId", mAutoSceneID);
                    jsonObject.put("keyNo", mKeyCode);
                    jsonObject.put("name", mList.get(mBindPosition).name);
                    jsonObject.put("msId", mList.get(mBindPosition).id);
                    mSceneManager.setExtendedProperty(mIotId, mKeyCode, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    private void createAutoScene() {
        EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                CScene.TYPE_AUTOMATIC, mList.get(mBindPosition).name, mIotId);
        baseInfoEntry.enable = true;
        EScene.triggerEntry entry = new EScene.triggerEntry();
        EDevice.deviceEntry deviceInformation = DeviceBuffer.getDeviceInformation(mIotId);
        entry.productKey = deviceInformation.productKey;
        entry.deviceName = deviceInformation.deviceName;
        entry.state = new ETSL.stateEntry("", "", "", mKeyCode);
        mSceneManager.createSwitchAutoScene(baseInfoEntry, entry, mList.get(mBindPosition).id, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EScene.sceneListItemEntry, BaseViewHolder>(R.layout.item_scene, mList) {

            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, EScene.sceneListItemEntry sceneListItemEntry) {
                int i = mList.indexOf(sceneListItemEntry);
                i = i % mSceneBgs.length();
                baseViewHolder.setText(R.id.sceneName, sceneListItemEntry.name);
                baseViewHolder.setGone(R.id.editMask, mClickPosition != baseViewHolder.getAdapterPosition());
                baseViewHolder.setImageResource(R.id.image, mSceneBgs.getResourceId(i, 0));
            }
        };
        mAdapter.addChildClickViewIds(R.id.editBtn, R.id.bindBtn);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            mClickPosition = position;
            mAdapter.notifyDataSetChanged();
        });
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.editBtn) {
                    SwitchSceneActivity.start(mActivity, mList.get(position), mIotId);
                } else if (view.getId() == R.id.bindBtn) {
                    mBindPosition = position;
                    mSceneManager.getExtendedProperty(mIotId, mKeyCode,
                            mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mAPIDataHandler);
                }
                mClickPosition = -1;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.create_scene_view) {
            SwitchSceneActivity.start(this, null, mIotId);
        }
    }

    public static void start(Context context, String iotId, String keyCode) {
        Intent intent = new Intent(context, SwitchSceneListActivity.class);
        intent.putExtra("extra", iotId);
        intent.putExtra("keyCode", keyCode);
        context.startActivity(intent);
    }

    // 响应错误处理器
    protected Handler mExtendedPropertyResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry) msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if (responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()) {
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception code: %s", responseErrorEntry.code));
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                if (responseErrorEntry.code == 401 || responseErrorEntry.code == 29003) {//检查用户是否登录了其他App
                    Logger.e("401 identityId is null 检查用户是否登录了其他App");
                    logOut();
                    return false;
                }
                createAutoScene();
            }
            return false;
        }
    });
}
