package com.laffey.smart.view;

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

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.event.RefreshKeyListEvent;
import com.laffey.smart.model.ItemUserKey;
import com.laffey.smart.presenter.LockManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditKeyActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.key_no)
    TextView keyNo;
    @BindView(R.id.key_id_text)
    TextView keyIdText;
    @BindView(R.id.key_belong)
    TextView keyBelong;
    @BindView(R.id.key_permission_text)
    TextView keyPermissionText;
    @BindView(R.id.delete_key)
    TextView deleteKey;

    private ItemUserKey mKey;
    private String mIotId;
    private MyProcessHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_key);
        ButterKnife.bind(this);
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
        tvToolbarTitle.setText(keyTitle + mKey.getLockUserId() + "信息");
        keyNo.setText(keyType + "编号");
        keyIdText.setText(String.valueOf(mKey.getLockUserId()));
        keyBelong.setText(keyType + "归属");
        keyPermissionText.setText(keyPermission);
        deleteKey.setText("删除" + keyType);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.delete_key})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.delete_key:
                LockManager.deleteKey(mKey.getLockUserId(), mKey.getLockUserType(), mIotId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
//                EventBus.getDefault().post(new RefreshKeyListEvent());
//                finish();
                break;
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
            switch (msg.what) {
                case Constant.MSG_CALLBACK_DELETE_KEY:
                    EventBus.getDefault().post(new RefreshKeyListEvent());
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
