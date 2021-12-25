package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.smart.BuildConfig;
import com.rexense.smart.R;
import com.rexense.smart.contract.CScene;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.event.CEvent;
import com.rexense.smart.event.EEvent;
import com.rexense.smart.model.EScene;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.model.ItemColorLightScene;
import com.rexense.smart.model.ItemScene;
import com.rexense.smart.model.ItemSceneInGateway;
import com.rexense.smart.model.Visitable;
import com.rexense.smart.presenter.CloudDataParser;
import com.rexense.smart.presenter.DeviceBuffer;
import com.rexense.smart.presenter.PluginHelper;
import com.rexense.smart.presenter.SceneManager;
import com.rexense.smart.presenter.SystemParameter;
import com.rexense.smart.presenter.TSLHelper;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LightDetailActivity extends DetailActivity {

    @BindView(R.id.includeDetailImgBack)
    ImageView mBackView;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTitleText;
    @BindView(R.id.lightnessText)
    TextView mLightnessText;
    @BindView(R.id.kText)
    TextView mKText;
    @BindView(R.id.colorTemperature)
    TextView mColorTemperatureText;
    @BindView(R.id.switch_ic)
    TextView mSwitchIC;
    @BindView(R.id.switch_tv)
    TextView mSwitchTV;
    @BindView(R.id.scene_ic)
    TextView mSceneIC;
    @BindView(R.id.scene_tv)
    TextView mSceneTV;
    @BindView(R.id.timer_ic)
    TextView mTimerIC;
    @BindView(R.id.timer_tv)
    TextView mTimerTV;
    @BindView(R.id.lightnessProgressBar)
    SeekBar mLightnessProgressBar;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.temperatureLayout)
    LinearLayout mTemperatureLayout;
    @BindView(R.id.color_temp_ic)
    TextView mColorTempIc;
    @BindView(R.id.temp_value_layout)
    LinearLayout mTempValueLayout;
    @BindView(R.id.scene_view)
    LinearLayout mSceneViewLayout;

    private List<Visitable> mList = new ArrayList<>();
    private List<ItemSceneInGateway> mItemSceneList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private int mLightness;
    private int mColorTemperature;
    private int mState;
    private TSLHelper mTSLHelper;
    private SceneManager mSceneManager;
    private String mGatewayId;
    private String mGatewayMac;

    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.LIGHT_P_BRIGHTNESS) != null && propertyEntry.getPropertyValue(CTSL.LIGHT_P_BRIGHTNESS).length() > 0) {
            mLightness = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.LIGHT_P_BRIGHTNESS));
            mLightnessText.setText(String.valueOf(mLightness));
            mLightnessProgressBar.setOnSeekBarChangeListener(null);
            mLightnessProgressBar.setProgress(mLightness);
            mLightnessProgressBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }

        if (propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE) != null && propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE).length() > 0) {
            mColorTemperature = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE));
            mKText.setText(String.valueOf(mColorTemperature));
            mColorTemperatureText.setText(String.valueOf(mColorTemperature));
        }

        if (propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER) != null && propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER).length() > 0) {
            mState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER));
            if (mState == 0) {
                // 关闭
                int blue42 = getResources().getColor(R.color.appcolor2);
                mLightnessText.setTextColor(blue42);
                mKText.setTextColor(blue42);
                mLightnessProgressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.color_light_progress_for_appcolor_2));
                mLightnessProgressBar.setEnabled(false);
                mSwitchIC.setTextColor(blue42);
                mSwitchTV.setTextColor(blue42);
                mSceneIC.setTextColor(blue42);
                mSceneTV.setTextColor(blue42);
                mTimerIC.setTextColor(blue42);
                mTimerTV.setTextColor(blue42);
                mColorTemperatureText.setTextColor(blue42);
            } else {
                // 打开
                int blue4 = getResources().getColor(R.color.appcolor);
                mLightnessText.setTextColor(blue4);
                mKText.setTextColor(blue4);
                mLightnessProgressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.color_light_progress_for_appcolor));
                mLightnessProgressBar.setEnabled(true);
                mSwitchIC.setTextColor(blue4);
                mSwitchTV.setTextColor(blue4);
                mSceneIC.setTextColor(blue4);
                mSceneTV.setTextColor(blue4);
                mTimerIC.setTextColor(blue4);
                mTimerTV.setTextColor(blue4);
                mColorTemperatureText.setTextColor(blue4);
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mSwitchIC.setTypeface(iconfont);
        mSceneIC.setTypeface(iconfont);
        mTimerIC.setTypeface(iconfont);
        mColorTempIc.setTypeface(iconfont);

        EventBus.getDefault().register(this);
        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mBackView.setImageResource(R.drawable.back_default);
        mTitleText.setTextColor(getResources().getColor(R.color.all_3));
        initView();
        initStatusBar();
        mTemperatureLayout.setVisibility(View.GONE);
        mTempValueLayout.setVisibility(View.GONE);

        if (!Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20,
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID))
            getGatewayId(mIOTId);
    }

    // 获取面板所属网关iotId
    private void getGatewayId(String iotId) {
        mSceneManager.getGWIotIdBySubIotId(this, iotId, Constant.MSG_QUEST_GW_ID_BY_SUB_ID,
                Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR, mAPIDataHandler);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appbgcolor));
        }
    }

    @Subscribe
    public void refreshSceneList(EEvent eventEntry) {
        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mLightnessText.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_BRIGHTNESS}, new String[]{"" + mLightnessProgressBar.getProgress()});
        }
    };

    private void initView() {
        mLightnessProgressBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecycleView.setAdapter(mAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRecycleView);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState == CTSL.STATUS_OFF) {
                    ToastUtils.showShortToast(LightDetailActivity.this, R.string.pls_turn_on_switch_first);
                    return;
                }
                int index = (int) view.getTag();
                if (!Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                    mSceneManager.executeScene(((ItemColorLightScene) mList.get(index)).getId(),
                            mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                } else {
                    ItemColorLightScene colorLightScene = (ItemColorLightScene) mList.get(index);
                    String msg = String.format(getString(R.string.main_scene_execute_hint_2),
                            colorLightScene.getSceneName());
                    ToastUtils.showLongToast(LightDetailActivity.this, msg);
                    SceneManager.invokeLocalSceneService(LightDetailActivity.this, mGatewayId,
                            colorLightScene.getId(), null);
                }
            }
        });

        // 分享设备无法添加、编辑场景
        if (DeviceBuffer.getDeviceInformation(mIOTId).owned == 0) {
            mSceneViewLayout.setVisibility(View.GONE);
        } else {
            mSceneViewLayout.setVisibility(View.VISIBLE);
        }
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUEST_QUERY_SCENE_LIST: {
                    // 查询网关下本地场景列表
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    JSONArray sceneList = response.getJSONArray("sceneList");
                    if (code == 0 || code == 200) {
                        if (sceneList != null) {
                            // ViseLog.d(GsonUtil.toJson(sceneList));
                            mItemSceneList.clear();
                            mList.clear();
                            for (int i = 0; i < sceneList.size(); i++) {
                                ItemSceneInGateway scene = JSONObject.parseObject(sceneList.get(i).toString(), ItemSceneInGateway.class);
                                JSONObject appParams = scene.getAppParams();
                                if (appParams == null) continue;
                                String switchIotId = appParams.getString("switchIotId");
                                if (mIOTId.equals(switchIotId)) {
                                    mItemSceneList.add(scene);
                                    mList.add(createColorLightScene(scene));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            // ViseLog.d("场景列表 = " + GsonUtil.toJson(mItemSceneList));
                        }
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(LightDetailActivity.this, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID: {
                    // 根据子设备iotId查询网关iotId
                    JSONObject response = (JSONObject) msg.obj;
                    int code = response.getInteger("code");
                    String message = response.getString("message");
                    String gwId = response.getString("gwIotId");
                    if (code == 200) {
                        mGatewayId = gwId;
                        if (Constant.IS_TEST_DATA) {
                            mGatewayId = DeviceBuffer.getGatewayDevs().get(0).iotId;
                        }
                        mGatewayMac = DeviceBuffer.getDeviceMac(mGatewayId);
                        mSceneManager.querySceneList(LightDetailActivity.this, mGatewayMac, "1",
                                Constant.MSG_QUEST_QUERY_SCENE_LIST, Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR, mAPIDataHandler);
                    } else {
                        QMUITipDialogUtil.dismiss();
                        RetrofitUtil.showErrorMsg(LightDetailActivity.this, response);
                    }
                    break;
                }
                case Constant.MSG_QUEST_QUERY_SCENE_LIST_ERROR:
                case Constant.MSG_QUEST_GW_ID_BY_SUB_ID_ERROR: {
                    // 根据子设备iotId查询网关iotId失败
                    QMUITipDialogUtil.dismiss();
                    Throwable e = (Throwable) msg.obj;
                    ToastUtils.showLongToast(LightDetailActivity.this, e.getMessage());
                    break;
                }
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    for (int i = 0; i < mList.size(); i++) {
                        ItemColorLightScene itemEntry = (ItemColorLightScene) mList.get(i);
                        if (itemEntry.getId().equalsIgnoreCase(sceneId)) {
                            ToastUtils.showShortToast(mActivity, String.format(getString(R.string.main_scene_execute_hint_2), itemEntry.getSceneName()));
                            break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String) msg.obj);
                    if (sceneList != null && sceneList.scenes != null) {
                        for (EScene.sceneListItemEntry item : sceneList.scenes) {
                            if (item.description.contains(mIOTId)) {
                                ItemColorLightScene scene = new ItemColorLightScene(item.id, item.name);
                                mSceneManager.querySceneDetail(item.id, "0", mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                                mList.add(scene);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL:
                    // 处理获取场景详情
                    JSONObject result = JSON.parseObject((String) msg.obj);
                    String id = result.getString("id");
                    for (int i = 0; i < mList.size(); i++) {
                        ItemColorLightScene scene = (ItemColorLightScene) mList.get(i);
                        if (id.equalsIgnoreCase(scene.getId())) {
                            JSONArray actionsJson = result.getJSONArray("actionsJson");
                            for (int j = 0; j < actionsJson.size(); j++) {
                                JSONObject jsonObject = JSON.parseObject(actionsJson.getString(j));
                                JSONObject params = jsonObject.getJSONObject("params");
                                if (params.getString("propertyName").equalsIgnoreCase(CTSL.LIGHT_P_BRIGHTNESS)) {
                                    scene.setLightness(params.getIntValue("propertyValue"));
                                } else {
                                    scene.setK(params.getIntValue("propertyValue"));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 获取调光调色面板场景
    private ItemColorLightScene createColorLightScene(ItemSceneInGateway scene) {
        ItemColorLightScene lightScene = new ItemColorLightScene(scene.getSceneDetail().getSceneId(), scene.getSceneDetail().getName());
        List<ItemScene.Action> actionList = scene.getSceneDetail().getActions();
        for (ItemScene.Action action : actionList) {
            JSONObject command = action.getParameters().getCommand();
            String level = command.getString(CTSL.PK_LIGHT_BRIGHTNESS_PARAM);
            String temperature = command.getString(CTSL.PK_LIGHT_COLOR_TEMP_PARAM);
            if (level != null && level.length() > 0) {
                lightScene.setLightness(Integer.parseInt(level));
            }
            if (temperature != null && temperature.length() > 0) {
                lightScene.setK(Integer.parseInt(temperature));
            }
        }
        return lightScene;
    }

    @OnClick({R.id.timer_view, R.id.scene_view, R.id.switch_view, R.id.temperatureLayout})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.timer_view) {
            if (mState == CTSL.STATUS_ON)
                PluginHelper.cloudTimer(LightDetailActivity.this, mIOTId, CTSL.PK_ONE_WAY_DIMMABLE_LIGHT);
        } else if (view.getId() == R.id.scene_view) {
            if (mState == CTSL.STATUS_ON) {
                if (!Constant.PACKAGE_NAME.equals(BuildConfig.APPLICATION_ID)) {
                    LightSceneListActivity.start(mActivity, mIOTId, "LightDetailActivity");
                } else {
                    LightLocalSceneListActivity.start(mActivity, mIOTId, mGatewayId, mGatewayMac, "LightDetailActivity");
                }
            }
        } else if (view.getId() == R.id.switch_view) {
            if (mState == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.temperatureLayout) {
            if (mState == CTSL.STATUS_ON)
                ColorTemperatureChoiceActivity.start(this, mColorTemperature);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int temperature = data.getIntExtra("temperature", 0);
            mColorTemperature = temperature;
            mKText.setText(String.valueOf(mColorTemperature));
            mColorTemperatureText.setText(String.valueOf(mColorTemperature));
            mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_COLOR_TEMPERATURE}, new String[]{"" + temperature});
        }
    }
}
