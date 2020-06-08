package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.rexense.imoco.R;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-30 15:29
 * Description: 我的
 */
public class UserActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // 主页处理
        ImageView imgHome = (ImageView)findViewById(R.id.includeBottomImgHome);
        imgHome.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 场景处理
        ImageView imgScene = (ImageView)findViewById(R.id.includeBottomImgScene);
        imgScene.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, SceneActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                default:
                    break;
            }
            return false;
        }
    });
}