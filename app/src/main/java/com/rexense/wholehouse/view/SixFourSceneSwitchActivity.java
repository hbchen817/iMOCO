package com.rexense.wholehouse.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.event.SceneBindEvent;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.ImageProvider;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gary
 * @time 2021-01-26 9:39
 */

public class SixFourSceneSwitchActivity extends DetailActivity implements View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.switch1)
    ImageView mSwitch1;
    @BindView(R.id.switch2)
    ImageView mSwitch2;
    @BindView(R.id.mSceneContentText)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText2;//2
    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText3;//3
    @BindView(R.id.mSceneContentText4)
    TextView mSceneContentText4;//4

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String mCurrentKey;
    private int mState1;
    private int mState2;
    private TSLHelper mTSLHelper;
    private String[] mManualIDs = new String[4];
    private String[] mManualNames = new String[4];

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
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mMyHandler = new MyHandler(this);
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
        mSceneContentText1.setOnClickListener(this);
        mSceneContentText1.setOnLongClickListener(this);
        mSceneContentText2.setOnClickListener(this);
        mSceneContentText2.setOnLongClickListener(this);
        mSceneContentText3.setOnClickListener(this);
        mSceneContentText3.setOnLongClickListener(this);
        mSceneContentText4.setOnClickListener(this);
        mSceneContentText4.setOnLongClickListener(this);
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
        } else if (view.getId() == R.id.mSceneContentText) {
            // 1
            if (mManualIDs[0] != null) {
                mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1);
            }
        } else if (view.getId() == R.id.mSceneContentText3) {
            // 2
            if (mManualIDs[1] != null) {
                mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2);
            }
        } else if (view.getId() == R.id.mSceneContentText1) {
            // 3
            if (mManualIDs[2] != null) {
                mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3);
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            // 4
            if (mManualIDs[3] != null) {
                mSceneManager.executeScene(mManualIDs[3], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
            } else {
                SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.mSceneContentText) {
            // 1
            if (mManualIDs[0] != null) {
                EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1, mSceneContentText1.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText3) {
            // 2
            if (mManualIDs[1] != null) {
                EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2, mSceneContentText2.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText1) {
            // 3
            if (mManualIDs[2] != null) {
                EditSceneBindActivity.start(this, "按键三", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3, mSceneContentText3.getText().toString());
            }
        } else if (view.getId() == R.id.mSceneContentText4) {
            // 4
            if (mManualIDs[3] != null) {
                EditSceneBindActivity.start(this, "按键四", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4, mSceneContentText4.getText().toString());
            }
        }
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<SixFourSceneSwitchActivity> mWeakReference;

        public MyHandler(SixFourSceneSwitchActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixFourSceneSwitchActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        switch (activity.mCurrentKey) {
                            case CTSL.SCENE_SWITCH_KEY_CODE_1:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText1.setText(jsonObject.getString("name"));
                                    activity.mManualNames[0] = jsonObject.getString("name");
                                    activity.mManualIDs[0] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText1.setText(R.string.no_bind_scene);
                                    activity.mManualNames[0] = null;
                                    activity.mManualIDs[1] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_2;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_2:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText2.setText(jsonObject.getString("name"));
                                    activity.mManualNames[1] = jsonObject.getString("name");
                                    activity.mManualIDs[1] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText2.setText(R.string.no_bind_scene);
                                    activity.mManualNames[1] = null;
                                    activity.mManualIDs[1] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_3;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_3:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText3.setText(jsonObject.getString("name"));
                                    activity.mManualNames[2] = jsonObject.getString("name");
                                    activity.mManualIDs[2] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText3.setText(R.string.no_bind_scene);
                                    activity.mManualNames[2] = null;
                                    activity.mManualIDs[2] = null;
                                }
                                activity.mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_4;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SCENE_SWITCH_KEY_CODE_4:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText4.setText(jsonObject.getString("name"));
                                    activity.mManualNames[3] = jsonObject.getString("name");
                                    activity.mManualIDs[3] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText4.setText(R.string.no_bind_scene);
                                    activity.mManualNames[3] = null;
                                    activity.mManualIDs[3] = null;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    for (int i = 0; i < activity.mManualIDs.length; i++) {
                        if (sceneId.equals(activity.mManualIDs[i])) {
                            ToastUtils.showLongToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint)
                                    , activity.mManualNames[i]));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
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
            }
            return false;
        }
    });
}