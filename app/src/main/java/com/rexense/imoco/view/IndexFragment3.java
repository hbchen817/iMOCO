package com.rexense.imoco.view;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.rexense.imoco.R;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment3 extends BaseFragment {

    private Intent intent;


    @Override
    public void onDestroyView() {
//        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
//        EventBus.getDefault().register(this);
        return R.layout.fragment_index3;
    }

    @Override
    protected void init() {

    }

}
