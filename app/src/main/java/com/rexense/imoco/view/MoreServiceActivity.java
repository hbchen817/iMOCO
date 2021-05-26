package com.rexense.imoco.view;

import android.os.Bundle;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.databinding.ActivityMoreServiceBinding;

public class MoreServiceActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMoreServiceBinding mViewBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMoreServiceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        mViewBinding.includeCommonToolbar.tvToolbarTitle.setText(getString(R.string.fragment3_more_service));

        mViewBinding.includeCommonToolbar.tvToolbarRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_toolbar_right) {

        }
    }

}
