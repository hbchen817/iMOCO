package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.databinding.ActivityFeedbackBinding;

public class FeedbackActivity extends BaseActivity {
    private ActivityFeedbackBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.fragment3_app_feedback);

        mViewBinding.feedbackBugView.setOnClickListener(this::onViewClicked);
        mViewBinding.featureSuggestionsView.setOnClickListener(this::onViewClicked);
        mViewBinding.otherProblemsView.setOnClickListener(this::onViewClicked);
    }

    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.feedback_bug_view) {
            Intent intent = new Intent(this, FeedbackContentActivity.class);
            intent.putExtra("feedback_type", 101);// 反馈故障
            startActivity(intent);
        } else if (id == R.id.feature_suggestions_view) {
            Intent intent = new Intent(this, FeedbackContentActivity.class);
            intent.putExtra("feedback_type", 102);// 功能建议
            startActivity(intent);
        } else if (id == R.id.other_problems_view) {
            Intent intent = new Intent(this, FeedbackContentActivity.class);
            intent.putExtra("feedback_type", 103);// 其他问题
            startActivity(intent);
        }
    }
}
