package com.xiezhu.jzj.view;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.presenter.AptContent;
import com.xiezhu.jzj.presenter.TSLHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-06 19:16
 * Description: 选择消息记录内容
 */
public class ChoiceContentActivity extends BaseActivity {
    private List<ETSL.messageRecordContentEntry> mContents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_content);

        String productKey = getIntent().getStringExtra("productKey");
        mContents = new TSLHelper(this).getMessageRecordContent(productKey);

        TextView title = findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.choicecontent_title);

        // 回退处理
        ImageView imgAdd = findViewById(R.id.includeTitleImgBack);
        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 选择内容处理
        if (mContents != null && mContents.size() > 0) {
            ListView lstProduct = (ListView) findViewById(R.id.choiceContentLstContent);
            AptContent adapter = new AptContent(this, mContents);
            lstProduct.setAdapter(adapter);
            lstProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }
}