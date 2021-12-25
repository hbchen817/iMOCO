package com.rexense.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityChoiceBinding;
import com.rexense.smart.model.EChoice;
import com.rexense.smart.presenter.AptChoiceList;

import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-11 15:29
 * Description: 通用选择
 */
public class ChoiceActivity extends Activity {
    private ActivityChoiceBinding mViewBinding;

    private List<EChoice.itemEntry> mItems = null;
    private int mResultCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(getIntent().getStringExtra("title"));
        mItems = (List<EChoice.itemEntry>) getIntent().getSerializableExtra("items");

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boolean isMultipleSelect = getIntent().getBooleanExtra("isMultipleSelect", false);
        mViewBinding.choiceLstItem.setAdapter(new AptChoiceList(this, mItems, isMultipleSelect));

        mResultCode = getIntent().getIntExtra("resultCode", Constant.RESULTCODE_CALLCHOICEACTIVITY_TIME);

        if (!isMultipleSelect) {
            // 单选处理
            mViewBinding.choiceRelOk.setVisibility(View.GONE);
            mViewBinding.choiceLstItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 返回当前选项
                    Intent intent = new Intent();
                    intent.putExtra("value", mItems.get(position).value);
                    setResult(mResultCode, intent);
                    finish();
                }
            });
        } else {
            // 多选处理
            mViewBinding.choiceRelOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 返回所选选项
                    String values = "";
                    int i = 0;
                    for (EChoice.itemEntry item : mItems) {
                        if (mItems.get(i).isSelected) {
                            if (values.length() > 0) {
                                values = values + ",";
                            }
                            values = values + mItems.get(i).value;
                        }
                        i++;
                    }

                    Intent intent = new Intent();
                    intent.putExtra("value", values);
                    setResult(mResultCode, intent);
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