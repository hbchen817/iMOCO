package com.laffey.smart.view;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityChoiceContentBinding;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.AptContent;
import com.laffey.smart.presenter.TSLHelper;
import com.vise.log.ViseLog;

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