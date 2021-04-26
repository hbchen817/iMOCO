package com.rexense.wholehouse.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.model.ETSL;
import com.rexense.wholehouse.presenter.CodeMapper;
import com.rexense.wholehouse.presenter.ImageProvider;
import com.rexense.wholehouse.presenter.PluginHelper;
import com.rexense.wholehouse.presenter.TSLHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-12-07 17:14
 * Description: 四键开关详细界面
 */
public class DetailFourSwitchActivity extends DetailActivity {
    private int mState1 = 0;
    private int mState2 = 0;
    private int mState3 = 0;
    private int mState4 = 0;
    private ImageView mImgOperate1;
    private ImageView mImgOperate2;
    private ImageView mImgOperate3;
    private ImageView mImgOperate4;
    private TextView mStateName1;
    private TextView mStateValue1;
    private TextView mStateName2;
    private TextView mStateValue2;
    private TextView mStateName3;
    private TextView mStateValue3;
    private TextView mStateName4;
    private TextView mStateValue4;
    private TSLHelper mTSLHelper;

    // 更新状态
    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if(!super.updateState(propertyEntry))
        {
            return false;
        }

        if(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1).length() > 0) {
            mState1 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1));
            mImgOperate1.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_1, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_1));
            if(stateEntry != null) {
                mStateName1.setText(stateEntry.name + ":");
                mStateValue1.setText(stateEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2).length() > 0) {
            mState2 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2));
            mImgOperate2.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_2, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_2));
            if(stateEntry != null) {
                mStateName2.setText(stateEntry.name + ":");
                mStateValue2.setText(stateEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3).length() > 0) {
            mState3 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3));
            mImgOperate3.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_3, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_3));
            if(stateEntry != null) {
                mStateName3.setText(stateEntry.name + ":");
                mStateValue3.setText(stateEntry.value);
            }
        }
        if(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4) != null && propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4).length() > 0) {
            mState4 = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4));
            mImgOperate4.setImageResource(ImageProvider.genDeviceStateIcon(mProductKey, CTSL.FWS_P_PowerSwitch_4, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4)));
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(this, mProductKey, CTSL.FWS_P_PowerSwitch_4, propertyEntry.getPropertyValue(CTSL.FWS_P_PowerSwitch_4));
            if(stateEntry != null) {
                mStateName4.setText(stateEntry.name + ":");
                mStateValue4.setText(stateEntry.value);
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：公共操作已经在父类中处理

        this.mTSLHelper = new TSLHelper(this);

        // 键1操作事件处理
        OnClickListener operateOnClickListener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mState1 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_1}, new String[] {"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_1}, new String[] {"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate1 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate1);
        this.mImgOperate1.setOnClickListener(operateOnClickListener1);

        mStateName1 = (TextView) findViewById(R.id.detailTwoSwitchLblStateName1);
        mStateValue1 = (TextView) findViewById(R.id.detailTwoSwitchLblStateValue1);
        mStateName2 = (TextView) findViewById(R.id.detailTwoSwitchLblStateName2);
        mStateValue2 = (TextView) findViewById(R.id.detailTwoSwitchLblStateValue2);
        mStateName3 = (TextView) findViewById(R.id.detailFourSwitchLblStateName3);
        mStateValue3 = (TextView) findViewById(R.id.detailFourSwitchLblStateValue3);
        mStateName4 = (TextView) findViewById(R.id.detailFourSwitchLblStateName4);
        mStateValue4 = (TextView) findViewById(R.id.detailFourSwitchLblStateValue4);

        // 键2操作事件处理
        OnClickListener operateOnClickListener2 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mState2 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_2}, new String[] {"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_2}, new String[] {"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate2 = (ImageView) findViewById(R.id.detailFourSwitchImgOperate2);
        this.mImgOperate2.setOnClickListener(operateOnClickListener2);

        // 键3操作事件处理
        OnClickListener operateOnClickListener3 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mState3 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_3}, new String[] {"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_3}, new String[] {"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate3 = (ImageView) findViewById(R.id.detailTwoSwitchImgOperate3);
        this.mImgOperate3.setOnClickListener(operateOnClickListener3);

        // 键4操作事件处理
        OnClickListener operateOnClickListener4 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mState4 == CTSL.STATUS_ON) {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_4}, new String[] {"" + CTSL.STATUS_OFF});
                } else {
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[] {CTSL.FWS_P_PowerSwitch_4}, new String[] {"" + CTSL.STATUS_ON});
                }
            }
        };
        this.mImgOperate4 = (ImageView) findViewById(R.id.detailFourSwitchImgOperate4);
        this.mImgOperate4.setOnClickListener(operateOnClickListener4);

        // 云端定时处理
        RelativeLayout timer = (RelativeLayout)findViewById(R.id.detailTwoSwitchRLTimer);
        timer.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                PluginHelper.cloudTimer(DetailFourSwitchActivity.this, mIOTId, mProductKey);
            }
        });
    }
}