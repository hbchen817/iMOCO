package com.rexense.imoco.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.imoco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 单路窗帘Activity
 */

public class OneWayCurtainsDetailActivity extends AppCompatActivity {

    @BindView(R.id.status)
    TextView mStatusText;
    @BindView(R.id.close_curtains)
    TextView mCloseCurtains;
    @BindView(R.id.open_curtains)
    TextView mOpenCurtains;
    @BindView(R.id.stop_curtains)
    TextView mStopCurtains;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_way_window_curtains);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.close_curtains, R.id.open_curtains, R.id.stop_curtains})
    public void onViewClicked(View view) {
        mStatusText.setText(((TextView) view).getText());
        setSwitch(view.getId());
    }

    private void setSwitch(int id) {
        Drawable drawable = getResources().getDrawable(R.drawable.one_switch_background);
        mCloseCurtains.setBackground(id == mCloseCurtains.getId() ? drawable : null);
        mOpenCurtains.setBackground(id == mOpenCurtains.getId() ? drawable : null);
        mStopCurtains.setBackground(id == mStopCurtains.getId() ? drawable : null);
    }
}