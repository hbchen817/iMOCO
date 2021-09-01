package com.xiezhu.jzj.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.event.SceneBindEvent;
import com.xiezhu.jzj.model.EAPIChannel;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.presenter.CodeMapper;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.SceneManager;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.utility.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SixTwoSceneSwitchActivity extends DetailActivity implements View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.switch1)
    ImageView mSwitch1;
    @BindView(R.id.switch2)
    ImageView mSwitch2;
    @BindView(R.id.switch3)
    ImageView mSwitch3;
    @BindView(R.id.switch4)
    ImageView mSwitch4;
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//5
    @BindView(R.id.mSceneContentText5)
    TextView mSceneContentText5;//6

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String mFirstManualSceneId;
    private String mSecondManualSceneId;
    private String mFirstManualSceneName;
    private String mSecondManualSceneName;
    private String mCurrentKey;
    private int mState1;
    private int mState2;
    private int mState3;
    private int mState4;
    private TSLHelper mTSLHelper;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1));
            mSwitch1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_1, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_1)));
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2));
            mSwitch2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_2, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_2)));
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3));
            mSwitch3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_3, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3)));
        }
        if (propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4) != null && propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4).length() > 0) {
            mState4 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_4));
            mSwitch4.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.SIX_SCENE_SWITCH_P_POWER_4, propertyEntry.getPropertyValue(CTSL.SIX_SCENE_SWITCH_P_POWER_3)));
        }
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mMyHandler = new MyHandler(getMainLooper(), this);
        mTSLHelper = new TSLHelper(this);
        initView();
        getScenes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mSwitch1.setOnClickListener(this);
        mSwitch2.setOnClickListener(this);
        mSwitch3.setOnClickListener(this);
        mSwitch4.setOnClickListener(this);
        mSceneContentText2.setOnClickListener(this);
        mSceneContentText5.setOnClickListener(this);
        mSceneContentText2.setOnLongClickListener(this);
        mSceneContentText5.setOnLongClickListener(this);
        mSceneManager = new SceneManager(this);
    }

    private void getScenes() {
        mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_1;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.switch1) {
            if (mState1 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_1}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.switch2) {
            if (mState2 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_2}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.switch3) {
            if (mState3 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_3}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.switch4) {
            if (mState4 == CTSL.STATUS_ON) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.SIX_SCENE_SWITCH_P_POWER_4}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.mSceneContentText2) {
            if (mFirstManualSceneId != null) {
                mSceneManager.executeScene(mFirstManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mSecondManualSceneId != null) {
                mSceneManager.executeScene(mSecondManualSceneId, mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.mSceneContentText2) {
            if (mFirstManualSceneId != null) {
                EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mSceneContentText2.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText5) {
            if (mSecondManualSceneId != null) {
                EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mSceneContentText5.getText().toString());
            }
        }
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<SixTwoSceneSwitchActivity> mWeakReference;

        public MyHandler(Looper looper, SixTwoSceneSwitchActivity activity) {
            super(looper);
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixTwoSceneSwitchActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        if (activity.mCurrentKey.equals(CTSL.SIX_SCENE_SWITCH_KEY_CODE_1)) {
                            if (!jsonObject.isEmpty()) {
                                activity.mSceneContentText2.setText(jsonObject.getString("name"));
                                activity.mFirstManualSceneName = jsonObject.getString("name");
                                activity.mFirstManualSceneId = jsonObject.getString("msId");
                            } else {
                                activity.mSceneContentText2.setText(R.string.no_bind_scene);
                                activity.mFirstManualSceneId = null;
                                activity.mFirstManualSceneName = null;
                            }
                            activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_2;
                            activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                    activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                        } else if (activity.mCurrentKey.equals(CTSL.SIX_SCENE_SWITCH_KEY_CODE_2)) {
                            if (!jsonObject.isEmpty()) {
                                activity.mSceneContentText5.setText(jsonObject.getString("name"));
                                activity.mSecondManualSceneName = jsonObject.getString("name");
                                activity.mSecondManualSceneId = jsonObject.getString("msId");
                            } else {
                                activity.mSceneContentText5.setText(R.string.no_bind_scene);
                                activity.mSecondManualSceneId = null;
                                activity.mSecondManualSceneName = null;
                            }
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    Toast.makeText(activity, String.format(activity.getString(R.string.main_scene_execute_hint)
                            , sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName), Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    // 响应错误处理器
    protected Handler mExtendedPropertyResponseErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
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
            }
            return false;
        }
    });
}

