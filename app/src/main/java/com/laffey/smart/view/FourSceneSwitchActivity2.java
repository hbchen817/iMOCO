package com.laffey.smart.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.SceneBindEvent;
import com.laffey.smart.model.EAPIChannel;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.presenter.ImageProvider;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.utility.Logger;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class FourSceneSwitchActivity2 extends DetailActivity {
    @BindView(R.id.mSceneContentText1)
    TextView mSceneContentText1;//1
    @BindView(R.id.mSceneContentText2)
    TextView mSceneContentText2;//2
    @BindView(R.id.mSceneContentText3)
    TextView mSceneContentText3;//3
    @BindView(R.id.mSceneContentText4)
    TextView mSceneContentText4;//4
    @BindView(R.id.back_light_ic)
    TextView mBackLightIc;
    @BindView(R.id.back_light_tv)
    TextView mBackLightTV;
    @BindView(R.id.key_1_tv)
    TextView mKey1TV;
    @BindView(R.id.key_2_tv)
    TextView mKey2TV;
    @BindView(R.id.key_3_tv)
    TextView mKey3TV;
    @BindView(R.id.key_4_tv)
    TextView mKey4TV;
    @BindView(R.id.back_light_layout)
    RelativeLayout mBackLightLayout;

    private SceneManager mSceneManager;
    private MyHandler mMyHandler;
    private String[] mManualIDs = new String[4];
    private String[] mManualNames = new String[4];
    private String mCurrentKey;
    private String mExecuteScene = "";

    private int mBackLightState = 1;

    private TSLHelper mTSLHelper;
    private String mKeyName1;
    private String mKeyName2;
    private String mKeyName3;
    private String mKeyName4;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.PFS_BackLight) != null && propertyEntry.getPropertyValue(CTSL.PFS_BackLight).length() > 0) {
            mBackLightState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.PFS_BackLight));
            switch (mBackLightState) {
                case CTSL.STATUS_OFF: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.gray3));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.gray3));
                    break;
                }
                case CTSL.STATUS_ON: {
                    mBackLightIc.setTextColor(getResources().getColor(R.color.blue2));
                    mBackLightTV.setTextColor(getResources().getColor(R.color.blue2));
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mBackLightIc.setTypeface(iconfont);

        mTSLHelper = new TSLHelper(this);

        mMyHandler = new MyHandler(this);
        initView();
        initKeyNickName();
        getScenes();

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.appbgcolor));
        }
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

    @OnClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4, R.id.back_light_tv,
            R.id.key_1_tv, R.id.key_2_tv, R.id.key_3_tv, R.id.key_4_tv, R.id.back_light_layout})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.mSceneContentText1:
                if (mManualIDs[0] != null) {
                    mExecuteScene = mSceneContentText1.getText().toString();
                    mSceneManager.executeScene(mManualIDs[0], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_1);
                }
                break;
            case R.id.mSceneContentText2:
                if (mManualIDs[1] != null) {
                    mExecuteScene = mSceneContentText2.getText().toString();
                    mSceneManager.executeScene(mManualIDs[1], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_2);
                }
                break;
            case R.id.mSceneContentText3:
                if (mManualIDs[2] != null) {
                    mExecuteScene = mSceneContentText3.getText().toString();
                    mSceneManager.executeScene(mManualIDs[2], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_3);
                }
                break;
            case R.id.mSceneContentText4:
                if (mManualIDs[3] != null) {
                    mExecuteScene = mSceneContentText4.getText().toString();
                    mSceneManager.executeScene(mManualIDs[3], mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                } else {
                    SwitchSceneListActivity.start(this, mIOTId, CTSL.SCENE_SWITCH_KEY_CODE_4);
                }
                break;
            case R.id.back_light_layout: {
                // 背光
                if (mBackLightState == CTSL.STATUS_OFF) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PFS_BackLight}, new String[]{"" + CTSL.STATUS_ON});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.PFS_BackLight}, new String[]{"" + CTSL.STATUS_OFF});
                }
                break;
            }
            case R.id.key_1_tv: {
                // 按键1
                showKeyNameDialogEdit(R.id.key_1_tv);
                break;
            }
            case R.id.key_2_tv: {
                // 按键2
                showKeyNameDialogEdit(R.id.key_2_tv);
                break;
            }
            case R.id.key_3_tv: {
                // 按键3
                showKeyNameDialogEdit(R.id.key_3_tv);
                break;
            }
            case R.id.key_4_tv: {
                // 按键4
                showKeyNameDialogEdit(R.id.key_4_tv);
                break;
            }
            default:
                break;
        }
    }

    public String getExecuteScene() {
        return mExecuteScene;
    }

    @OnLongClick({R.id.mSceneContentText1, R.id.mSceneContentText2, R.id.mSceneContentText3, R.id.mSceneContentText4})
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
            default:
                break;
        }
        return true;
    }

    private final int TAG_GET_EXTENDED_PRO = 10000;

    private void initKeyNickName() {
        MyResponseErrHandler errHandler = new MyResponseErrHandler(this);
        mSceneManager.getExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, TAG_GET_EXTENDED_PRO, null, errHandler, mMyHandler);
    }

    private class MyResponseErrHandler extends Handler {
        private WeakReference<Activity> ref;

        public MyResponseErrHandler(Activity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
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
                } else if (responseErrorEntry.code == 6741) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKey1TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKey2TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKey3TV.getText().toString());
                    jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, mKey4TV.getText().toString());
                    mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), null, null, null);
                }
            }
        }
    }

    private class MyHandler extends Handler {
        final WeakReference<FourSceneSwitchActivity2> mWeakReference;

        public MyHandler(FourSceneSwitchActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            FourSceneSwitchActivity2 activity = mWeakReference.get();
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
                    ToastUtils.showLongToast(activity, String.format(activity.getString(R.string.main_scene_execute_hint),
                            activity.getExecuteScene()));
                    //Toast.makeText(activity, String.format(activity.getString(R.string.main_scene_execute_hint)
//                            , sceneId.equals(activity.mFirstManualSceneId) ? activity.mFirstManualSceneName : activity.mSecondManualSceneName), Toast.LENGTH_LONG).show();
                    break;
                case TAG_GET_EXTENDED_PRO: {
                    // 获取按键昵称
                    JSONObject object = JSONObject.parseObject((String) msg.obj);
                    mKey1TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_1));
                    mKey2TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_2));
                    mKey3TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_3));
                    mKey4TV.setText(object.getString(CTSL.SCENE_SWITCH_KEY_CODE_4));
                    break;
                }
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET: {
                    // 设置按键昵称
                    QMUITipDialogUtil.dismiss();
                    mKey1TV.setText(mKeyName1);
                    mKey2TV.setText(mKeyName2);
                    mKey3TV.setText(mKeyName3);
                    mKey4TV.setText(mKeyName4);
                    ToastUtils.showShortToast(FourSceneSwitchActivity2.this, R.string.set_success);
                    break;
                }
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

    // 显示按键名称修改对话框
    private void showKeyNameDialogEdit(int resId) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.key_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setHint(getString(R.string.pls_input_key_name));

        switch (resId) {
            case R.id.key_1_tv: {
                // 按键1
                nameEt.setText(mKey1TV.getText().toString());
                break;
            }
            case R.id.key_2_tv: {
                // 按键2
                nameEt.setText(mKey2TV.getText().toString());
                break;
            }
            case R.id.key_3_tv: {
                // 按键3
                nameEt.setText(mKey3TV.getText().toString());
                break;
            }
            case R.id.key_4_tv: {
                // 按键4
                nameEt.setText(mKey4TV.getText().toString());
                break;
            }
        }

        nameEt.setSelection(nameEt.getText().toString().length());
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
                QMUITipDialogUtil.showLoadingDialg(FourSceneSwitchActivity2.this, R.string.is_setting);
                switch (resId) {
                    case R.id.key_1_tv: {
                        // 按键1
                        mKeyName1 = nameEt.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        break;
                    }
                    case R.id.key_2_tv: {
                        // 按键2
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = nameEt.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        break;
                    }
                    case R.id.key_3_tv: {
                        // 按键3
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = nameEt.getText().toString();
                        mKeyName4 = mKey4TV.getText().toString();
                        break;
                    }
                    case R.id.key_4_tv: {
                        // 按键4
                        mKeyName1 = mKey1TV.getText().toString();
                        mKeyName2 = mKey2TV.getText().toString();
                        mKeyName3 = mKey3TV.getText().toString();
                        mKeyName4 = nameEt.getText().toString();
                        break;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_1, mKeyName1);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_2, mKeyName2);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_3, mKeyName3);
                jsonObject.put(CTSL.SCENE_SWITCH_KEY_CODE_4, mKeyName4);
                mSceneManager.setExtendedProperty(mIOTId, Constant.TAG_DEV_KEY_NICKNAME, jsonObject.toJSONString(), mCommitFailureHandler, mResponseErrorHandler, mMyHandler);
                dialog.dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}