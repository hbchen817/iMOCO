package com.rexense.imoco.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rexense.imoco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditSceneBindActivity extends AppCompatActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.mSceneContentText)
    TextView mSceneContentText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_scene_bind);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        String title = getIntent().getStringExtra("title");
        tvToolbarTitle.setText(title + "绑定场景");
    }

    public static void start(Context context, String title) {
        Intent intent = new Intent(context, EditSceneBindActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.mSceneContentText, R.id.unbind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.mSceneContentText:
                break;
            case R.id.unbind:
                break;
        }
    }
}
