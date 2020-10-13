package com.rexense.imoco.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rexense.imoco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/13 11:12
 */

public class LockDetailActivity extends DetailActivity {

    @BindView(R.id.includeDetailImgSetting)
    ImageView includeDetailImgSetting;
    @BindView(R.id.electricity_value)
    TextView mElectricityValue;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.includeDetailImgBack, R.id.all_record_btn, R.id.includeDetailImgSetting, R.id.includeDetailImgMore, R.id.mUserManagerView, R.id.mShortTimePasswordView, R.id.mKeyManagerView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.includeDetailImgBack:
                finish();
                break;
            case R.id.includeDetailImgSetting:
                break;
            case R.id.includeDetailImgMore:
                break;
            case R.id.mUserManagerView:

                break;
            case R.id.mShortTimePasswordView:
                break;
            case R.id.mKeyManagerView:
                break;
            case R.id.all_record_btn:
                break;
            default:
                break;
        }
    }
}