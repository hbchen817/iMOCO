package com.laffey.smart.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.laffey.smart.R;
import com.laffey.smart.utility.LogcatFileManager;
import com.laffey.smart.utility.SpUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        mTitle.setText(getString(R.string.alog_title));

        QMUICommonListItemView itemWithSwitch = mGroupListView.createItemView(getString(R.string.alog_title));
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

        String path = getExternalCacheDir().getAbsolutePath();
        path = path.substring(path.indexOf("/Android/data"));

        QMUIGroupListView.newSection(this)
                .setDescription("日志内容生成txt文件，保存在" + path + "/Log/目录下")
                .addItemView(itemWithSwitch, null)
                .addTo(mGroupListView);
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
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @OnClick({R.id.tv_toolbar_right})
    protected void onViewClicked(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {
            finish();
        }
    }
}
