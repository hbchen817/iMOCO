package com.xiezhu.jzj.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.presenter.CodeMapper;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.presenter.PluginHelper;
import com.xiezhu.jzj.presenter.TSLHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 三键开关详细界面
 */
public class DetailThreeSwitchActivity extends DetailActivity {
    private int mState1 = 0;
    private int mState2 = 0;
    private int mState3 = 0;
    private ImageView mImgOperate1;
    private ImageView mImgOperate2;
    private ImageView mImgOperate3;
    private TextView mStateName1;
    private TextView mStateValue1;
    private TextView mStateName2;
    private TextView mStateValue2;
    private TextView mStateName3;
    private TextView mStateValue3;
    private TSLHelper mTSLHelper;

    // 更新状态
    @SuppressLint("SetTextI18n")
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        ViseLog.d(new Gson().toJson(propertyEntry));
        if (propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1));
            mImgOperate1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.TWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_1));
            if (stateEntry != null) {
                mStateName1.setText(stateEntry.name + ":");
                mStateValue1.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2));
            mImgOperate2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.TWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.TWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.TWS_P_PowerSwitch_2));
            if (stateEntry != null) {
                mStateName2.setText(stateEntry.name + ":");
                mStateValue2.setText(stateEntry.value);
            }
        }
        if (propertyEntry.getPropertyValue(CTSL.THS_P_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.THS_P_PowerSwitch_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.THS_P_PowerSwitch_3));
            mImgOperate3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.THS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.THS_P_PowerSwitch_3)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.THS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.THS_P_PowerSwitch_3));
            if (stateEntry != null) {
                mStateName3.setText(stateEntry.name + ":");
                mStateValue3.setText(stateEntry.value);
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理

        mTSLHelper = new TSLHelper(this);

        // 键1操作事件处理
        OnClickListener operateOnClickListener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate1 = findViewById(R.id.detailTwoSwitchImgOperate1);
        mImgOperate1.setOnClickListener(operateOnClickListener1);

        mStateName1 = findViewById(R.id.detailTwoSwitchLblStateName1);
        mStateValue1 = findViewById(R.id.detailTwoSwitchLblStateValue1);
        mStateName2 = findViewById(R.id.detailTwoSwitchLblStateName2);
        mStateValue2 = findViewById(R.id.detailTwoSwitchLblStateValue2);
        mStateName3 = findViewById(R.id.detailTwoSwitchLblStateName3);
        mStateValue3 = findViewById(R.id.detailTwoSwitchLblStateValue3);

        // 键2操作事件处理
        OnClickListener operateOnClickListener2 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.TWS_P_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate2 = findViewById(R.id.detailTwoSwitchImgOperate2);
        mImgOperate2.setOnClickListener(operateOnClickListener2);
        // 键2操作事件处理
        OnClickListener operateOnClickListener3 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState3 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.THS_P_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.THS_P_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_ON});
                }
            }
        };
        mImgOperate3 = findViewById(R.id.detailTwoSwitchImgOperate3);
        mImgOperate3.setOnClickListener(operateOnClickListener3);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout) findViewById(R.id.detailTwoSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailThreeSwitchActivity.this, mIOTId, mProductKey);
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