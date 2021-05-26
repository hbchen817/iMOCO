package com.rexense.imoco.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityHistoryBinding;
import com.rexense.imoco.event.RefreshHistoryEvent;
import com.rexense.imoco.model.ItemHistoryMsg;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.LockManager;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gary
 * @time 2020/10/15 13:13
 */

public class HistoryActivity extends BaseActivity {
    private ActivityHistoryBinding mViewBinding;

    public static final String IOTID = "IOTID";
    private static final String[] TYPE_ALL = new String[]{};
    private static final String[] TYPE_ALARM = new String[]{"HijackingAlarm", "TamperAlarm", "DoorUnlockedAlarm", "ArmDoorOpenAlarm", "LockedAlarm"};
    private static final String[] TYPE_OPEN = new String[]{"DoorOpenNotification", "RemoteUnlockNotification"};
    private static final String[] TYPE_INFO = new String[]{"KeyDeletedNotification", "KeyAddedNotification", "LowElectricityAlarm", "ReportReset"};

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private String mIotID;
    private int mPageNo = 1;
    private final int PAGE_SIZE = 30;
    private MyResponseHandler mHandler;
    private long mStartTime;
    private String[] mCurrentType;

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mList.clear();
            mPageNo = 1;
            getData();
        }
    };
    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mPageNo++;
            getData();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);
        mIotID = getIntent().getStringExtra(IOTID);
        mHandler = new MyResponseHandler(this);
        initView();
        mViewBinding.srlFragmentMe.setOnRefreshListener(onRefreshListener);
        mViewBinding.srlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        mStartTime = 0;
        mCurrentType = TYPE_ALL;
        getData();

        initStatusBar();

        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
        mViewBinding.mTimeView.setOnClickListener(this::onViewClicked);
        mViewBinding.mTypeView.setOnClickListener(this::onViewClicked);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Subscribe
    public void refresh(RefreshHistoryEvent event) {
        mList.clear();
        mPageNo = 1;
        getData();
    }

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.history_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mViewBinding.mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mViewBinding.mRecyclerView.setAdapter(mAdapter);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.noRecordHint.setTypeface(iconfont);
    }

    private void getData() {
        LockManager.getLockHistory(mIotID, mStartTime, System.currentTimeMillis(), mCurrentType, mPageNo, PAGE_SIZE, mCommitFailureHandler, mResponseErrorHandler, mHandler);

    }

    public static void start(Context context, String iotID) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(IOTID, iotID);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        int resId = view.getId();
        if (resId == R.id.iv_toolbar_left) {
            finish();
        } else if (resId == R.id.mTimeView) {
            showTimeFilterDiaLog();
        } else if (resId == R.id.mTypeView) {
            showTypeFilterDiaLog();
        }
    }

    /**
     * 时间筛选弹窗
     */
    private void showTimeFilterDiaLog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_time_filter, null);
        builder.setView(view);
        builder.setCancelable(true);


        final Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        TextView all_time = view.findViewById(R.id.all_time);
        TextView last_week = view.findViewById(R.id.last_week);
        TextView last_month = view.findViewById(R.id.last_month);
        TextView last_three_month = view.findViewById(R.id.last_three_month);
        TextView last_half_year = view.findViewById(R.id.last_half_year);

        all_time.setOnClickListener(v -> {
            if (mStartTime != 0) {
                mViewBinding.mTimeText.setText(all_time.getText());
                mStartTime = 0;
                mList.clear();
                getData();
            }
            dialog.dismiss();
        });

        last_week.setOnClickListener(v -> {
            mViewBinding.mTimeText.setText(last_week.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_month.setOnClickListener(v -> {
            mViewBinding.mTimeText.setText(last_month.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_three_month.setOnClickListener(v -> {
            mViewBinding.mTimeText.setText(last_three_month.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30 * 3;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_half_year.setOnClickListener(v -> {
            mViewBinding.mTimeText.setText(last_half_year.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30 * 6;
            mList.clear();
            getData();
            dialog.dismiss();
        });

    }

    /**
     * 类别筛选弹窗
     */
    private void showTypeFilterDiaLog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_type_filter, null);
        builder.setView(view);
        builder.setCancelable(true);


        final Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);
        TextView all_record = view.findViewById(R.id.all_record);
        TextView alarm_record = view.findViewById(R.id.alarm_record);
        TextView open_record = view.findViewById(R.id.open_record);
        TextView info_record = view.findViewById(R.id.info_record);

        all_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_ALL) {
                mViewBinding.mTypeText.setText(all_record.getText());
                mCurrentType = TYPE_ALL;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        alarm_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_ALARM) {
                mViewBinding.mTypeText.setText(alarm_record.getText());
                mCurrentType = TYPE_ALARM;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        open_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_OPEN) {
                mViewBinding.mTypeText.setText(open_record.getText());
                mCurrentType = TYPE_OPEN;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        info_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_INFO) {
                mViewBinding.mTypeText.setText(info_record.getText());
                mCurrentType = TYPE_INFO;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });

    }

    private static class MyResponseHandler extends Handler {
        final WeakReference<HistoryActivity> mWeakReference;

        public MyResponseHandler(HistoryActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HistoryActivity activity = mWeakReference.get();
            if (activity == null) return;
            if (msg.what == Constant.MSG_CALLBACK_QUERY_HISTORY) {
                JSONObject js = JSON.parseObject((String) msg.obj);
                JSONArray array = js.getJSONArray("data");
                int size = array.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jo = array.getJSONObject(i);
                    if (jo.getIntValue("KeyID") == 103) {
                        continue;
                    }
                    ItemHistoryMsg item = new ItemHistoryMsg();
                    item.setTime(jo.getString("client_date"));
                    item.setEvent_code(jo.getString("event_code"));
                    item.setKeyID(jo.getString("KeyID"));
                    item.setLockType(jo.getIntValue("LockType"));
                    activity.mList.add(item);
                }
                activity.mViewBinding.noRecordHint.setVisibility(activity.mList.isEmpty() ? View.VISIBLE : View.GONE);
                activity.mAdapter.notifyDataSetChanged();
                SrlUtils.finishRefresh(activity.mViewBinding.srlFragmentMe, true);
                SrlUtils.finishLoadMore(activity.mViewBinding.srlFragmentMe, true);
            }
        }
    }
}