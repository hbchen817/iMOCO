package com.xiezhu.jzj.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiezhu.jzj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/9/27 13:51
 */

public class OneKeySceneDetailActivity extends AppCompatActivity {

    @BindView(R.id.device_image_view)
    ImageView mDeviceImageView;
    @BindView(R.id.mSceneContentText)
    TextView mSceneContentText;
    @BindView(R.id.includeDetailRl)
    RelativeLayout includeDetailRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_scene);
        ButterKnife.bind(this);
        includeDetailRl.setBackgroundColor(0xFFFFFFFF);
    }

    @OnClick(R.id.mSceneContentText)
    public void onViewClicked() {

    }
}