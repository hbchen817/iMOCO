package com.rexense.imoco.view;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rexense.imoco.R;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.viewholder.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorLightDetailActivity extends AppCompatActivity {

    @BindView(R.id.lightnessText)
    TextView mLightnessText;
    @BindView(R.id.kText)
    TextView mKText;
    @BindView(R.id.lightnessProgressBar)
    SeekBar mLightnessProgressBar;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_light);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mLightnessProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mLightnessText.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecycleView.setAdapter(mAdapter);
    }

    @OnClick({R.id.timer_view, R.id.scene_view, R.id.switch_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.timer_view:
                break;
            case R.id.scene_view:
                break;
            case R.id.switch_view:
                break;
        }
    }
}
