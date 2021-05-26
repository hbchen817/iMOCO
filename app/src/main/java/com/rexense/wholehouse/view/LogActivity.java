package com.rexense.wholehouse.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.rexense.wholehouse.BuildConfig;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.databinding.ActivityLogBinding;
import com.rexense.wholehouse.utility.LogcatFileManager;
import com.rexense.wholehouse.utility.SpUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends BaseActivity {
    private ActivityLogBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLogBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.alog_title));

        QMUICommonListItemView apkTime = mViewBinding.groupListView.createItemView("打包时间");
        apkTime.setDetailText(BuildConfig.APK_TIME);

        QMUICommonListItemView itemWithSwitch = mViewBinding.groupListView.createItemView(getString(R.string.alog_title));
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

        String path = getExternalCacheDir().getAbsolutePath();
        path = path.substring(path.indexOf("/Android/data"));

        QMUIGroupListView.newSection(this)
                .setDescription("日志内容生成txt文件，保存在" + path + "/Log/目录下")
                .addItemView(apkTime, null)
                .addItemView(itemWithSwitch, null)
                .addTo(mViewBinding.groupListView);
        itemWithSwitch.getSwitch().setChecked(SpUtils.getBooleanValue(this, SpUtils.SP_APP_INFO, "log_state", false));

        itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.putBooleanValue(LogActivity.this, SpUtils.SP_APP_INFO, "log_state", isChecked);
                if (isChecked) {
                    String path = getApplicationContext().getExternalCacheDir() + "/Log/";
                    LogcatFileManager.getInstance().start(path);
                } else {
                    LogcatFileManager.getInstance().stop();
                }
            }
        });
        initStatusBar();
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    protected void onViewClicked(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            finish();
        }
    }
}
