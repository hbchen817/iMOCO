package com.rexense.wholehouse.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityEditSceneBindBinding;
import com.rexense.wholehouse.event.SceneBindEvent;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.utility.Logger;
import com.rexense.wholehouse.utility.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditSceneBindActivity extends BaseActivity {
    private ActivityEditSceneBindBinding mViewBinding;

    private String mIotId;
    private String mKeyCode;
    private SceneManager mSceneManager;
    private MyHandler mMyHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityEditSceneBindBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mSceneManager = new SceneManager(this);
        mMyHandler = new MyHandler(this);
        mIotId = getIntent().getStringExtra("iotId");
        mKeyCode = getIntent().getStringExtra("keyCode");
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

    @Subscribe
    public void refreshSceneName(SceneBindEvent event) {
        mViewBinding.mSceneContentText.setText(event.sceneName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        String title = getIntent().getStringExtra("title");
        mViewBinding.includeToolbar.tvToolbarTitle.setText(title + "绑定场景");
        mViewBinding.mSceneContentText.setText(getIntent().getStringExtra("sceneName"));

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.mSceneContentText.setOnClickListener(this::onViewClicked);
        mViewBinding.unbind.setOnClickListener(this::onViewClicked);
    }

    public static void start(Context context, String title, String iotId, String keyCode, String sceneName) {
        Intent intent = new Intent(context, EditSceneBindActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("iotId", iotId);
        intent.putExtra("keyCode", keyCode);
        intent.putExtra("sceneName", sceneName);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.mSceneContentText) {
            SwitchSceneListActivity.start(this, mIotId, mKeyCode);
        } else if (id == R.id.unbind) {
            mSceneManager.getExtendedProperty(mIotId, mKeyCode,
                    mCommitFailureHandler, mExtendedPropertyResponseErrorHandler, mMyHandler);
        }
    }

    private static class MyHandler extends Handler {
        final WeakReference<EditSceneBindActivity> mWeakReference;

        public MyHandler(EditSceneBindActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EditSceneBindActivity activity = mWeakReference.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET:
                    //处理获取拓展数据
                    if (msg.obj != null && !TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                        String keyNo = jsonObject.getString("keyNo");
                        if (keyNo != null && keyNo.equals(activity.mKeyCode)) {
                            String autoSceneId = jsonObject.getString("asId");
                            activity.mSceneManager.deleteScene(autoSceneId, activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mMyHandler);
                            activity.mSceneManager.setExtendedProperty(activity.mIotId, activity.mKeyCode, "{}", activity.mCommitFailureHandler
                                    , activity.mResponseErrorHandler, activity.mMyHandler);
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_DELETESCENE:
                    //处理删除场景

                    break;
                case Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET:
                    //处理设置数据
                    ToastUtils.showToastCentrally(activity, "解绑成功");
                    EventBus.getDefault().post(new SceneBindEvent(""));
                    activity.finish();
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
