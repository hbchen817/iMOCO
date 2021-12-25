package com.rexense.smart.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.smart.databinding.ActivityOneKeySceneBinding;

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
