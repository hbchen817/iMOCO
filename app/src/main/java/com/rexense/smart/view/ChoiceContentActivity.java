package com.rexense.smart.view;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityChoiceContentBinding;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.presenter.AptContent;
import com.rexense.smart.presenter.TSLHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-06 19:16
 * Description: 选择消息记录内容
 */
public class ChoiceContentActivity extends BaseActivity {
    private List<ETSL.messageRecordContentEntry> mContents;

    private ActivityChoiceContentBinding mViewBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceContentBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        String productKey = getIntent().getStringExtra("productKey");
        mContents = new TSLHelper(this).getMessageRecordContent(productKey);

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.choicecontent_title);

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 选择内容处理
        if (mContents != null && mContents.size() > 0) {
            AptContent adapter = new AptContent(this, mContents);
            mViewBinding.choiceContentLstContent.setAdapter(adapter);
            mViewBinding.choiceContentLstContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 返回所选内容
                    Intent intent = new Intent();
                    intent.putExtra("id", mContents.get(position).id);
                    intent.putExtra("name", mContents.get(position).name);
                    intent.putExtra("type", mContents.get(position).type);
                    setResult(Constant.RESULTCODE_CALLCHOICECONTENTACTIVITY, intent);
                    finish();
                }
            });
        }

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}