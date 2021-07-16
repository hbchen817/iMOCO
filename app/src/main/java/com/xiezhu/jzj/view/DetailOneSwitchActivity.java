package com.xiezhu.jzj.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.presenter.CodeMapper;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.PluginHelper;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.model.ETSL;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 一键开关详细界面
 */
public class DetailOneSwitchActivity extends DetailActivity {
    private int mState = 0;
    private ImageView mImgOperate;
    private TextView mStateName;
    private TextView mStateValue;
    private TSLHelper mTSLHelper;

    // 更新状态
    @SuppressLint("SetTextI18n")
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        if (propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1).length() > 0) {
            mState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1));
            mImgOperate.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.OWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.OWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.OWS_P_PowerSwitch_1));
            if (stateEntry != null) {
                mStateName.setText(stateEntry.name + ":");
                mStateValue.setText(stateEntry.value);
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理

        mTSLHelper = new TSLHelper(this);

        // 设备操作事件处理
        OnClickListener operateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.OWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate = (ImageView) findViewById(R.id.detailOneSwitchImgOperate);
        mImgOperate.setOnClickListener(operateOnClickListener);

        mStateName = (TextView) findViewById(R.id.detailOneSwitchLblStateName);
        mStateValue = (TextView) findViewById(R.id.detailOneSwitchLblStateValue);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout) findViewById(R.id.detailOneSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailOneSwitchActivity.this, mIOTId, mProductKey);
            }
        });

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setStatusBarColor(getColor(R.color.sensor_detail_bg));
        }
    }
}