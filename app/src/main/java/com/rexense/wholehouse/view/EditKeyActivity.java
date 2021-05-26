package com.rexense.wholehouse.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityEditKeyBinding;
import com.rexense.wholehouse.event.RefreshKeyListEvent;
import com.rexense.wholehouse.model.ItemUserKey;
import com.rexense.wholehouse.presenter.LockManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditKeyActivity extends BaseActivity {
    private ActivityEditKeyBinding mViewBinding;

    private ItemUserKey mKey;
    private String mIotId;
    private MyProcessHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mViewBinding.getRoot());

        mKey = (ItemUserKey) getIntent().getSerializableExtra("item");
        mIotId = getIntent().getStringExtra("iotId");
        mHandler = new MyProcessHandler(this);
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

    @SuppressLint("SetTextI18n")
    private void initView() {
        String keyType = "";
        String keyTitle = "";
        String keyPermission = "";
        switch (mKey.getLockUserType()) {
            case 1:
                keyType = "指纹";
                keyTitle = keyType + "钥匙";
                break;
            case 2:
                keyType = "密码";
                keyTitle = keyType + "钥匙";
                break;
            case 3:
                keyType = "卡";
                keyTitle = keyType + "钥匙";
                break;
            case 4:
                keyType = "机械钥匙";
                keyTitle = keyType;
                break;
            default:
                break;
        }
        switch (mKey.getLockUserPermType()) {
            case 1:
                keyPermission = "普通用户";
                break;
            case 2:
                keyPermission = "管理员用户";
                break;
            case 3:
                keyPermission = "胁迫用户";
                break;
            default:
                break;
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(keyTitle + mKey.getLockUserId() + "信息");
        mViewBinding.keyNo.setText(keyType + "编号");
        mViewBinding.keyIdText.setText(String.valueOf(mKey.getLockUserId()));
        mViewBinding.keyBelong.setText(keyType + "归属");
        mViewBinding.keyPermissionText.setText(keyPermission);
        mViewBinding.deleteKey.setText("删除" + keyType);

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.deleteKey.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_toolbar_left) {
            finish();
        } else if (id == R.id.delete_key) {
            LockManager.deleteKey(mKey.getLockUserId(), mKey.getLockUserType(), mIotId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
//                EventBus.getDefault().post(new RefreshKeyListEvent());
//                finish();
        }
    }

    public static void start(Context context, ItemUserKey itemUserKey, String iotID) {
        Intent intent = new Intent(context, EditKeyActivity.class);
        intent.putExtra("item", itemUserKey);
        intent.putExtra("iotId", iotID);
        context.startActivity(intent);
    }

    private static class MyProcessHandler extends Handler {
        WeakReference<EditKeyActivity> mWeakReference;

        public MyProcessHandler(EditKeyActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EditKeyActivity activity = mWeakReference.get();
            if (msg.what == Constant.MSG_CALLBACK_DELETE_KEY) {
                EventBus.getDefault().post(new RefreshKeyListEvent());
                activity.finish();
            }
        }
    }
}
