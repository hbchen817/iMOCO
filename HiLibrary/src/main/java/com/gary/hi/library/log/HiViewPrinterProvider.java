package com.gary.hi.library.log;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gary.hi.library.utli.HiDisplayUtil;

/**
 * 实现悬浮窗效果 日志可视化辅助类
 */
public class HiViewPrinterProvider {
    public static final String TAG_FLOATING_VIEW = "TAG_FLOATING_VIEW";
    public static final String TAG_LOG_VIEW = "TAG_LOG_VIEW";

    private FrameLayout rootView;
    private View floatingView;
    private boolean isOpen;
    private FrameLayout logView;
    private RecyclerView recyclerView;

    public HiViewPrinterProvider(FrameLayout rootView, RecyclerView recyclerView) {
        this.rootView = rootView;
        this.recyclerView = recyclerView;
    }

    /**
     * 显示悬浮窗(HiLog 不包含日志)
     */
    public void showFloatingView() {
        if (rootView.findViewWithTag(TAG_FLOATING_VIEW) != null) {//已经添加过悬浮窗不再重复添加
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        floatingView = genFloatingView();
        floatingView.setTag(TAG_FLOATING_VIEW);
        floatingView.setBackgroundColor(Color.BLACK);
        floatingView.setAlpha(0.8f);
        params.bottomMargin = HiDisplayUtil.dp2px(100, rootView.getResources());
        floatingView.setLayoutParams(params);
        rootView.addView(floatingView);
    }

    /**
     * 隐藏悬浮按钮
     */
    public void closeFloatingView() {
        rootView.removeView(genFloatingView());
    }

    /**
     * 生成悬浮窗
     * @return
     */
    private View genFloatingView() {
        if (floatingView != null) {
            return floatingView;
        }
        TextView textView = new TextView(rootView.getContext());
        textView.setOnClickListener(view -> {
            if (isOpen) {

            } else {
                showLogView();
            }
        });
        textView.setText("HiLog");
        return floatingView = textView;
    }

    /**
     * 显示日志
     */
    private void showLogView() {
        if (rootView.findViewWithTag(TAG_LOG_VIEW) != null) {
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(160, rootView.getResources()));
        params.gravity = Gravity.BOTTOM;
        View logView = genLogView();
        logView.setTag(TAG_LOG_VIEW);
        rootView.addView(logView, params);
        isOpen = true;
    }

    /**
     * 生成LogView
     * @return
     */
    private View genLogView() {
        if (logView != null) {
            return logView;
        }
        FrameLayout logView = new FrameLayout(rootView.getContext());
        logView.setBackgroundColor(Color.BLACK);
        logView.addView(recyclerView);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        //关闭按钮
        TextView closeView = new TextView(rootView.getContext());
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLogView();
            }
        });
        closeView.setText("Close");
        logView.addView(closeView, params);
        return this.logView = logView;
    }

    /**
     * 关闭LogView
     */
    public void closeLogView() {
        isOpen = false;
        rootView.removeView(genLogView());
    }
}
