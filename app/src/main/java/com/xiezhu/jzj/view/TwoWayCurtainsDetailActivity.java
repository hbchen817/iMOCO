package com.xiezhu.jzj.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xiezhu.jzj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 两路窗帘Activity
 */

public class TwoWayCurtainsDetailActivity extends AppCompatActivity {

    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.two_status)
    TextView mTwoStatus;
    @BindView(R.id.close_curtains)
    TextView mCloseCurtains;
    @BindView(R.id.open_curtains)
    TextView mOpenCurtains;
    @BindView(R.id.stop_curtains)
    TextView mStopCurtains;
    @BindView(R.id.two_close_curtains)
    TextView mTwoCloseCurtains;
    @BindView(R.id.two_open_curtains)
    TextView mTwoOpenCurtains;
    @BindView(R.id.two_stop_curtains)
    TextView mTwoStopCurtains;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_way_curtains);
        ButterKnife.bind(this);
    }

    private void setSwitch(int id) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.one_switch_background);
        mCloseCurtains.setBackground(id == mCloseCurtains.getId() ? drawable : null);
        mOpenCurtains.setBackground(id == mOpenCurtains.getId() ? drawable : null);
        mStopCurtains.setBackground(id == mStopCurtains.getId() ? drawable : null);
    }

    private void setSecondSwitch(int id) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.one_switch_background);
        mTwoCloseCurtains.setBackground(id == mTwoCloseCurtains.getId() ? drawable : null);
        mTwoOpenCurtains.setBackground(id == mTwoOpenCurtains.getId() ? drawable : null);
        mTwoStopCurtains.setBackground(id == mTwoStopCurtains.getId() ? drawable : null);
    }

    @OnClick({R.id.close_curtains, R.id.open_curtains, R.id.stop_curtains, R.id.two_close_curtains, R.id.two_open_curtains, R.id.two_stop_curtains})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.close_curtains || view.getId() == R.id.open_curtains || view.getId() == R.id.stop_curtains) {
            mStatus.setText(((TextView) view).getText());
            setSwitch(view.getId());
        } else if (view.getId() == R.id.two_close_curtains || view.getId() == R.id.two_open_curtains || view.getId() == R.id.two_stop_curtains) {
            mTwoStatus.setText(((TextView) view).getText());
            setSecondSwitch(view.getId());
        }
    }
}