package com.laffey.smart.view;

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
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.CEvent;
import com.laffey.smart.event.EEvent;
import com.laffey.smart.model.EScene;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.model.ItemColorLightScene;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.viewholder.CommonAdapter;

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

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private int mLightness;
    private int mColorTemperature;
    private int mState;
    private TSLHelper mTSLHelper;
    private SceneManager mSceneManager;

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
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
                mSceneManager.executeScene(((ItemColorLightScene) mList.get(index)).getId(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        });
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
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

    @OnClick({R.id.timer_view, R.id.scene_view, R.id.switch_view, R.id.temperatureLayout})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.timer_view) {
            if (mState == CTSL.STATUS_ON)
                PluginHelper.cloudTimer(LightDetailActivity.this, mIOTId, CTSL.PK_ONE_WAY_DIMMABLE_LIGHT);
        } else if (view.getId() == R.id.scene_view) {
            if (mState == CTSL.STATUS_ON)
                LightSceneListActivity.start(mActivity, mIOTId, "LightDetailActivity");
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