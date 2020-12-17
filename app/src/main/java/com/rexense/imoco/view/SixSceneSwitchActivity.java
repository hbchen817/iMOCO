package com.rexense.imoco.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.SceneBindEvent;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.utility.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class SixSceneSwitchActivity extends DetailActivity {

    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//2
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText3;//3
    @BindView(R.id.mSceneContentText4)
    TextView mSceneContentText4;//4
    @BindView(R.id.mSceneContentText5)
    TextView mSceneContentText5;//5
    @BindView(R.id.mSceneContentText6)
    TextView mSceneContentText6;//6

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[6];
    private String[] mManualNames = new String[6];
    private String mCurrentKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mMyHandler = new MyHandler(this);
        initView();
        getScenes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mSceneManager = new SceneManager(this);
    }

    private void getScenes() {
        mCurrentKey = CTSL.SCENE_SWITCH_KEY_CODE_1;
        mSceneManager.getExtendedProperty(mIOTId, mCurrentKey, mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
    }

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        getScenes();
    }

    @OnClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4, R.id.mSceneContentText5, R.id.mSceneContentText6})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText2:
                if (mManualIDs[1] != null) {
                    mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText3:
                if (mManualIDs[2] != null) {
                    mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText4:
                if (mManualIDs[3] != null) {
                    mSceneManager.executeScene(mManualIDs[3], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText5:
                if (mManualIDs[4] != null) {
                    mSceneManager.executeScene(mManualIDs[4], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText6:
                if (mManualIDs[5] != null) {
                    mSceneManager.executeScene(mManualIDs[5], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2);
                }
                break;
            default:
                break;
        }
    }


    @OnLongClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4, R.id.mSceneContentText5, R.id.mSceneContentText6})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    EditSceneBindActivity.start(this, "按键一", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1, mSceneContentText1.getText().toString());
                }
                break;
            case R.id.mSceneContentText2:
                if (mManualIDs[1] != null) {
                    EditSceneBindActivity.start(this, "按键二", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2, mSceneContentText2.getText().toString());
                }
                break;
            case R.id.mSceneContentText3:
                if (mManualIDs[2] != null) {
                    EditSceneBindActivity.start(this, "按键三", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3, mSceneContentText3.getText().toString());
                }
                break;
            case R.id.mSceneContentText4:
                if (mManualIDs[3] != null) {
                    EditSceneBindActivity.start(this, "按键四", mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4, mSceneContentText4.getText().toString());
                }
                break;
            case R.id.mSceneContentText5:
                if (mManualIDs[4] != null) {
                    EditSceneBindActivity.start(this, "按键五", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_1, mSceneContentText5.getText().toString());
                }
                break;
            case R.id.mSceneContentText6:
                if (mManualIDs[5] != null) {
                    EditSceneBindActivity.start(this, "按键六", mIOTId, CTSL.SIX_SCENE_SWITCH_KEY_CODE_2, mSceneContentText6.getText().toString());
                }
                break;
            default:
                break;
        }
        return true;
    }


    private static class MyHandler extends Handler {
        final WeakReference<SixSceneSwitchActivity> mWeakReference;

        public MyHandler(SixSceneSwitchActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SixSceneSwitchActivity activity = mWeakReference.get();
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
                                }
                                activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_1;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SIX_SCENE_SWITCH_KEY_CODE_1:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText5.setText(jsonObject.getString("name"));
                                    activity.mManualNames[4] = jsonObject.getString("name");
                                    activity.mManualIDs[4] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText5.setText(R.string.no_bind_scene);
                                    activity.mManualNames[4] = null;
                                }
                                activity.mCurrentKey = CTSL.SIX_SCENE_SWITCH_KEY_CODE_2;
                                activity.mSceneManager.getExtendedProperty(activity.mIOTId, activity.mCurrentKey,
                                        activity.mCommitFailureHandler, activity.mExtendedPropertyResponseErrorHandler, activity.mMyHandler);
                                break;
                            case CTSL.SIX_SCENE_SWITCH_KEY_CODE_2:
                                if (!jsonObject.isEmpty()) {
                                    activity.mSceneContentText6.setText(jsonObject.getString("name"));
                                    activity.mManualNames[5] = jsonObject.getString("name");
                                    activity.mManualIDs[5] = jsonObject.getString("msId");
                                } else {
                                    activity.mSceneContentText6.setText(R.string.no_bind_scene);
                                    activity.mManualNames[5] = null;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_EXECUTESCENE:
                    String sceneId = (String) msg.obj;
                    //Toast.makeText(activity, String.format(activity.getString(R.string.main_scene_execute_hint)
//                            , sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName), Toast.LENGTH_LONG).show();
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

