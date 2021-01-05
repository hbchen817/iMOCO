package com.laffey.smart.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.laffey.smart.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OneKeySceneActivity extends AppCompatActivity {

    @BindView(R.id.mSceneContentText)
    TextView mSceneContentText;

    private boolean mBindScene;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_scene);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mSceneContentText)
    public void onViewClicked() {
        if (mBindScene) {

        } else {

        }
    }
}
