package com.xiezhu.jzj.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiezhu.jzj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreServiceActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_service);
        ButterKnife.bind(this);
        tvToolbarTitle.setText(getString(R.string.fragment3_more_service));
    }

    @OnClick({R.id.tv_toolbar_right})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                break;
        }
    }

}
