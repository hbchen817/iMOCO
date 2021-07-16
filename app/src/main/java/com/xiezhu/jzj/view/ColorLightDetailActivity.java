package com.xiezhu.jzj.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.event.CEvent;
import com.xiezhu.jzj.event.EEvent;
import com.xiezhu.jzj.model.EScene;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.model.ItemColorLightScene;
import com.xiezhu.jzj.model.Visitable;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.CodeMapper;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.PluginHelper;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.presenter.SystemParameter;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

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
//            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.LIGHT_P_BRIGHTNESS, propertyEntry.getPropertyValue(CTSL.LIGHT_P_BRIGHTNESS));
//            if (stateEntry != null) {
            mLightnessText.setText(String.valueOf(mLightness));
            mLightnessProgressBar.setProgress(mLightness);
//            }
        }

        if (propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE) != null && propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE).length() > 0) {
            mColorTemperature = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE));
//            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.LIGHT_P_COLOR_TEMPERATURE, propertyEntry.getPropertyValue(CTSL.LIGHT_P_COLOR_TEMPERATURE));
//            if (stateEntry != null) {
            mKText.setText(String.valueOf(mColorTemperature));
            mColorTemperatureText.setText(String.valueOf(mColorTemperature));
//            }
        }

        if (propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER) != null && propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER).length() > 0) {
            mState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.LIGHT_P_POWER, propertyEntry.getPropertyValue(CTSL.LIGHT_P_POWER));
            if (stateEntry != null) {

            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mTSLHelper = new TSLHelper(this);
        mSceneManager = new SceneManager(this);
        mBackView.setImageResource(R.drawable.back_default);
        mTitleText.setTextColor(ContextCompat.getColor(this, R.color.all_3));
        initView();
        mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20,
                mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
    }

    @Subscribe
    public void refreshSceneList(EEvent eventEntry) {
        // 处理刷新手动执行场景列表数据
        if (eventEntry.name.equalsIgnoreCase(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA)) {
            mList.clear();
            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, 20,
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mLightnessProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mLightnessText.setText(String.valueOf(i));
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_BRIGHTNESS}, new String[]{"" + i});
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                int index = (int) view.getTag();
                mSceneManager.executeScene(((ItemColorLightScene) mList.get(index)).getId(), mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        });
    }

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
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
        if (view.getId() == R.id.timer_view) {
            PluginHelper.cloudTimer(ColorLightDetailActivity.this, mIOTId, CTSL.PK_LIGHT);
        } else if (view.getId() == R.id.scene_view) {
            LightSceneListActivity.start(mActivity, mIOTId);
        } else if (view.getId() == R.id.switch_view) {
            if (mState == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.LIGHT_P_POWER}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.temperatureLayout) {
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
