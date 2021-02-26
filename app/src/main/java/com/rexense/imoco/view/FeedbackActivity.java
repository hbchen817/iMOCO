package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.imoco.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.feedback_bug_view)
    RelativeLayout mFeedbackBugView;
    @BindView(R.id.feature_suggestions_view)
    RelativeLayout mFeatureSuggestionsView;
    @BindView(R.id.other_problems_view)
    RelativeLayout mOtherProblemsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initStatusBar();
        initView();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void initView() {
        mTitle.setText(R.string.fragment3_app_feedback);
    }

    @OnClick({R.id.feedback_bug_view, R.id.feature_suggestions_view, R.id.other_problems_view})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.feedback_bug_view: {
                Intent intent = new Intent(this, FeedbackContentActivity.class);
                intent.putExtra("feedback_type", 101);// 反馈故障
                startActivity(intent);
                break;
            }
            case R.id.feature_suggestions_view: {
                Intent intent = new Intent(this, FeedbackContentActivity.class);
                intent.putExtra("feedback_type", 102);// 功能建议
                startActivity(intent);
                break;
            }
            case R.id.other_problems_view: {
                Intent intent = new Intent(this, FeedbackContentActivity.class);
                intent.putExtra("feedback_type", 103);// 其他问题
                startActivity(intent);
                break;
            }
        }
    }
}
