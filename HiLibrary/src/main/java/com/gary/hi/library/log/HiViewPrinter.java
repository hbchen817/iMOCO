package com.gary.hi.library.log;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gary.hi.library.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 将Log显示在界面上
 */
public class HiViewPrinter implements HiLogPrinter {

    private RecyclerView mRecyclerView;
    private LogAdapter adapter;
    private final HiViewPrinterProvider mPrinterProvider;
    private final LinearLayoutManager mLinearLayoutManager;

    public HiViewPrinter(Activity activity) {
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        mRecyclerView = new RecyclerView(activity);
        adapter = new LogAdapter(LayoutInflater.from(activity));
        mLinearLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mPrinterProvider = new HiViewPrinterProvider(rootView, mRecyclerView);
    }

    @Override
    public void print(@NotNull HiLogConfig config, int level, String tag, @NotNull String printString) {
        HiLogMo hiLogMo = new HiLogMo(System.currentTimeMillis(), level, tag, printString);
        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        int itemCount = adapter.getItemCount();
        adapter.addItem(hiLogMo);
        Log.i("lzm", lastVisibleItemPosition + "||" + itemCount);
        if (lastVisibleItemPosition == itemCount - 1) {
            //仿照AS日志滚动效果 如果在查看之前的日志 不进行滚动 否则滚动到最新的日志位置
            mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    public HiViewPrinterProvider getmPrinterProvider() {
        return mPrinterProvider;
    }

    /**
     * 通过RecyclerView实现Log的显示 这里是Adapter
     */
    private static class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {
        private LayoutInflater inflater;
        private List<HiLogMo> logs = new ArrayList<>();

        public void addItem(HiLogMo log) {
            logs.add(log);
            notifyItemInserted(logs.size() - 1);
        }

        public LogAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.hilog_item, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            HiLogMo hiLogMo = logs.get(position);
            int color = getHighLightColor(hiLogMo.level);
            holder.messageTextView.setTextColor(color);
            holder.tagTextView.setTextColor(color);
            holder.tagTextView.setText(hiLogMo.getFlattened());
            holder.messageTextView.setText(hiLogMo.log);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        /**
         * 根据Log级别获取颜色
         *
         * @param level
         * @return
         */
        private int getHighLightColor(int level) {
            switch (level) {
                case HiLogType.V:
                    return 0xffbbbbbb;
                case HiLogType.D:
                    return 0xffffffff;
                case HiLogType.I:
                    return 0xff6a8759;
                case HiLogType.W:
                    return 0xffbbb529;
                case HiLogType.E:
                    return 0xffff6b68;
                case HiLogType.A:
                    return 0xffffff00;
                default:
                    return 0xffbbbbbb;
            }
        }
    }

    /**
     * Log View Holder
     * Tag + Message
     */
    private static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;
        TextView messageTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = (TextView) itemView.findViewById(R.id.tag);
            messageTextView = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
