package com.rexense.wholehouse.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CScene;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.event.CEvent;
import com.rexense.wholehouse.event.EEvent;
import com.rexense.wholehouse.model.EScene;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.model.ItemColorLightScene;
import com.rexense.wholehouse.model.Visitable;
import com.rexense.wholehouse.presenter.CloudDataParser;
import com.rexense.wholehouse.presenter.PluginHelper;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.SystemParameter;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorLightDetailActivity extends DetailActivity {

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
                mLightnessText.setTextColor(getResources().getColor(R.color.blue4_2));
                mKText.setTextColor(getResources().getColor(R.color.blue4_2));
                mLightnessProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.color_light_progress_2));
                mLightnessProgressBar.setEnabled(false);
                mSwitchIC.setTextColor(getResources().getColor(R.color.blue4_2));
                mSwitchTV.setTextColor(getResources().getColor(R.color.blue4_2));
                mSceneIC.setTextColor(getResources().getColor(R.color.blue4_2));
                mSceneTV.setTextColor(getResources().getColor(R.color.blue4_2));
                mTimerIC.setTextColor(getResources().getColor(R.color.blue4_2));
                mTimerTV.setTextColor(getResources().getColor(R.color.blue4_2));
                mColorTemperatureText.setTextColor(getResources().getColor(R.color.blue4_2));
            } else {
                // 打开
                mLightnessText.setTextColor(getResources().getColor(R.color.blue4));
                mKText.setTextColor(getResources().getColor(R.color.blue4));
                mLightnessProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.color_light_progress));
                mLightnessProgressBar.setEnabled(true);
                mSwitchIC.setTextColor(getResources().getColor(R.color.blue4));
                mSwitchTV.setTextColor(getResources().getColor(R.color.blue4));
                mSceneIC.setTextColor(getResources().getColor(R.color.blue4));
                mSceneTV.setTextColor(getResources().getColor(R.color.blue4));
                mTimerIC.setTextColor(getResources().getColor(R.color.blue4));
                mTimerTV.setTextColor(getResources().getColor(R.color.blue4));
                mColorTemperatureText.setTextColor(getResources().getColor(R.color.blue4));
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

        EventBus.getDefault().register(this);
        this.mTSLHelper = new TSLHelper(this);
        this.mSceneManager = new SceneManager(this);
        mBackView.setImageResource(R.drawable.back_default);
        mTitleText.setTextColor(getResources().getColor(R.color.all_3));
        initView();
        initStatusBar();
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
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
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
                    ToastUtils.showShortToast(ColorLightDetailActivity.this, R.string.pls_turn_on_switch_first);
                    return;
                }
                int index = (int) view.getTag();
                mSceneManager.executeScene(((ItemColorLightScene) mList.get(index)).getId(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        });
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    for (int i = 0; i < mList.size(); i++) {
                        ItemColorLightScene itemEntry = (ItemColorLightScene) mList.get(i);
                        if (itemEntry.getId().equalsIgnoreCase(sceneId)) {
                            Toast.makeText(mActivity, String.format(getString(R.string.main_scene_execute_hint), itemEntry.getSceneName()), Toast.LENGTH_LONG).show();
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
        switch (view.getId()) {
            case R.id.timer_view:
                if (mState == CTSL.STATUS_ON)
                    PluginHelper.cloudTimer(ColorLightDetailActivity.this, mIOTId, CTSL.PK_LIGHT);
                break;
            case R.id.scene_view:
                if (mState == CTSL.STATUS_ON)
                    LightSceneListActivity.start(mActivity, mIOTId);
                break;
            case R.id.switch_view:
                if (mState == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            case R.id.temperatureLayout:
                if (mState == CTSL.STATUS_ON)
                    ColorTemperatureChoiceActivity.start(this, mColorTemperature);
                break;
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
