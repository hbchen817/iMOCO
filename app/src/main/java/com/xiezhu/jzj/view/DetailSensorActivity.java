package com.xiezhu.jzj.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.presenter.CodeMapper;
import com.xiezhu.jzj.presenter.ImageProvider;
import com.xiezhu.jzj.model.ETSL;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 传感器详细界面
 */
public class DetailSensorActivity extends DetailActivity {
    private boolean mIsHasPowerSource;
    private ImageView mIcon;
    private ImageView mStateIcon;
    private TextView mStateName;
    private TextView mStateValue;
    private ImageView mStateIcon2;
    private TextView mStateName2;
    private TextView mStateValue2;
    private TextView mPowerName;
    private TextView mPowerValue;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if(!super.updateState(propertyEntry))
        {
            return false;
        }

        // 电池电量处理
        if(this.mIsHasPowerSource) {
            if(propertyEntry.getPropertyValue(CTSL.P_P_BatteryPercentage) != null && propertyEntry.getPropertyValue(CTSL.P_P_BatteryPercentage).length() > 0) {
                ETSL.stateEntry powerEntry = CodeMapper.processPropertyState(DetailSensorActivity.this, mProductKey, CTSL.P_P_BatteryPercentage, propertyEntry.getPropertyValue(CTSL.P_P_BatteryPercentage));
                if(powerEntry != null && powerEntry.name != null && powerEntry.value != null) {
                    mPowerName.setText(powerEntry.name + ":");
                    mPowerValue.setText(powerEntry.value);
                }
            }
        }

        // 温湿度处理
        if(mProductKey.equals(CTSL.PK_TEMHUMSENSOR)) {
            if(propertyEntry.getPropertyValue(CTSL.THS_P_CurrentTemperature) != null && propertyEntry.getPropertyValue(CTSL.THS_P_CurrentTemperature).length() > 0) {
                ETSL.stateEntry tempEntry = CodeMapper.processPropertyState(DetailSensorActivity.this, mProductKey, CTSL.THS_P_CurrentTemperature, propertyEntry.getPropertyValue(CTSL.THS_P_CurrentTemperature));
                if(tempEntry != null && tempEntry.name != null && tempEntry.value != null) {
                    mStateIcon.setImageResource(ImageProvider.genProductPropertyIcon(mProductKey, CTSL.THS_P_CurrentTemperature));
                    mStateName.setText(tempEntry.name + ":");
                    mStateValue.setText(tempEntry.value);
                }
            }
            if(propertyEntry.getPropertyValue(CTSL.THS_P_CurrentHumidity) != null && propertyEntry.getPropertyValue(CTSL.THS_P_CurrentHumidity).length() > 0) {
                ETSL.stateEntry humEntry = CodeMapper.processPropertyState(DetailSensorActivity.this, mProductKey, CTSL.THS_P_CurrentHumidity, propertyEntry.getPropertyValue(CTSL.THS_P_CurrentHumidity));
                if(humEntry != null && humEntry.name != null && humEntry.value != null) {
                    mStateIcon2.setImageResource(ImageProvider.genProductPropertyIcon(mProductKey, CTSL.THS_P_CurrentHumidity));
                    mStateName2.setText(humEntry.name + ":");
                    mStateValue2.setText(humEntry.value);
                }
            }

            return true;
        }

        // 状态（非电池电量）处理
        ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(DetailSensorActivity.this, propertyEntry);
        if(stateEntry != null && stateEntry.name != null && stateEntry.value != null) {
            mStateName.setText(stateEntry.name + ":");
            mStateValue.setText(stateEntry.value);
            // 更新图标
            mIcon.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, stateEntry.rawName, stateEntry.rawValue));
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理

        mStateIcon = (ImageView) findViewById(R.id.detailSensorImgStateIcon);
        mStateName = (TextView) findViewById(R.id.detailSensorLblStateName);
        mStateValue = (TextView) findViewById(R.id.detailSensorLblStateValue);
        mIcon = (ImageView) findViewById(R.id.detailSensorImgIcon);
        mPowerName = (TextView)findViewById(R.id.detailSensorLblPowerName);
        mPowerValue = (TextView)findViewById(R.id.detailSensorLblPowerValue);

        // 初始化设备状态图标
        mIcon.setImageResource(ImageProvider.genProductIcon(mProductKey));
        if (mProductKey.equalsIgnoreCase(CTSL.PK_TEMHUMSENSOR)){
            mStateIcon.setImageResource(ImageProvider.genProductPropertyIcon(mProductKey, CTSL.THS_P_CurrentTemperature));
        }

        this.mIsHasPowerSource = getIntent().getBooleanExtra("isHasPowerSource", false);
        // 如果没有电池电源则不显示
        if (!this.mIsHasPowerSource) {
            RelativeLayout relativeLayoutPower = (RelativeLayout) findViewById(R.id.detailSensorRLPower);
            relativeLayoutPower.setVisibility(View.GONE);
        }

        // 遥控按钮无状态显示栏
        if(mProductKey.equals(CTSL.PK_REMOTECONTRILBUTTON)) {
            RelativeLayout relativeLayoutState = (RelativeLayout) findViewById(R.id.deteailSensorRLState);
            relativeLayoutState.setVisibility(View.GONE);
        }

        RelativeLayout mLayoutState2 = (RelativeLayout)findViewById(R.id.deteailSensorRLState2);
        mLayoutState2.setVisibility(View.GONE);
        // 温湿度要使用状态2显示栏
        if(mProductKey.equals(CTSL.PK_TEMHUMSENSOR)) {
            mLayoutState2.setVisibility(View.VISIBLE);
            mStateIcon2 = (ImageView) findViewById(R.id.detailSensorImgStateIcon2);
            mStateIcon2.setImageResource(ImageProvider.genProductPropertyIcon(mProductKey, CTSL.THS_P_CurrentHumidity));
            mStateName2 = (TextView) findViewById(R.id.detailSensorLblStateName2);
            mStateValue2 = (TextView) findViewById(R.id.detailSensorLblStateValue2);
        }
        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.sensor_detail_bg));
        }
    }
}