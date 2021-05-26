package com.laffey.smart.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.laffey.smart.R;
import com.laffey.smart.databinding.ActivityOneKeySceneBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OneKeySceneActivity extends AppCompatActivity {
    private ActivityOneKeySceneBinding mViewBinding;

    private boolean mBindScene;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityOneKeySceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.mSceneContentText1.setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        if (mBindScene) {

        } else {

        }
    }
}
