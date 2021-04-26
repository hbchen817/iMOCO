package com.rexense.wholehouse.view;

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
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.event.RefreshHistoryEvent;
import com.rexense.wholehouse.model.ItemHistoryMsg;
import com.rexense.wholehouse.model.Visitable;
import com.rexense.wholehouse.presenter.LockManager;
import com.rexense.wholehouse.utility.SrlUtils;
import com.rexense.wholehouse.viewholder.CommonAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/15 13:13
 */

public class HistoryActivity extends BaseActivity {

    public static final String IOTID = "IOTID";
    private static final String[] TYPE_ALL = new String[]{};
    private static final String[] TYPE_ALARM = new String[]{"HijackingAlarm", "TamperAlarm", "DoorUnlockedAlarm", "ArmDoorOpenAlarm", "LockedAlarm"};
    private static final String[] TYPE_OPEN = new String[]{"DoorOpenNotification", "RemoteUnlockNotification"};
    private static final String[] TYPE_INFO = new String[]{"KeyDeletedNotification", "KeyAddedNotification", "LowElectricityAlarm", "ReportReset"};

    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.mTimeText)
    TextView mTimeText;
    @BindView(R.id.mTypeText)
    TextView mTypeText;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl_fragment_me)
    SmartRefreshLayout mSrlFragmentMe;
    @BindView(R.id.no_record_hint)
    TextView mNoRecordHint;

    private List<Visitable> mList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private String mIotID;
    private int mPageNo = 1;
    private int mPageSize = 30;
    private MyResponseHandler mHandler;
    private long mStartTime;
    private String[] mCurrentType;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mList.clear();
            mPageNo = 1;
            getData();
        }
    };
    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
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
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mIotID = getIntent().getStringExtra(IOTID);
        mHandler = new MyResponseHandler(this);
        initView();
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);
        mStartTime = 0;
        mCurrentType = TYPE_ALL;
        getData();

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

    @Subscribe
    public void refresh(RefreshHistoryEvent event) {
        mList.clear();
        mPageNo = 1;
        getData();
    }

    private void initView() {
        mTitle.setText(R.string.history_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mList, this);
        mRecyclerView.setAdapter(mAdapter);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mNoRecordHint.setTypeface(iconfont);
    }

    private void getData() {
        LockManager.getLockHistory(mIotID, mStartTime, System.currentTimeMillis(), mCurrentType, mPageNo, mPageSize, mCommitFailureHandler, mResponseErrorHandler, mHandler);

    }

    public static void start(Context context, String iotID) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(IOTID, iotID);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.mTimeView, R.id.mTypeView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left:
                finish();
                break;
            case R.id.mTimeView:
                showTimeFilterDiaLog();
                break;
            case R.id.mTypeView:
                showTypeFilterDiaLog();
                break;
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
                mTimeText.setText(all_time.getText());
                mStartTime = 0;
                mList.clear();
                getData();
            }
            dialog.dismiss();
        });

        last_week.setOnClickListener(v -> {
            mTimeText.setText(last_week.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_month.setOnClickListener(v -> {
            mTimeText.setText(last_month.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_three_month.setOnClickListener(v -> {
            mTimeText.setText(last_three_month.getText());
            mStartTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30 * 3;
            mList.clear();
            getData();
            dialog.dismiss();
        });
        last_half_year.setOnClickListener(v -> {
            mTimeText.setText(last_half_year.getText());
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
                mTypeText.setText(all_record.getText());
                mCurrentType = TYPE_ALL;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        alarm_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_ALARM) {
                mTypeText.setText(alarm_record.getText());
                mCurrentType = TYPE_ALARM;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        open_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_OPEN) {
                mTypeText.setText(open_record.getText());
                mCurrentType = TYPE_OPEN;
                mList.clear();
                mPageNo = 1;
                getData();
            }
            dialog.dismiss();
        });
        info_record.setOnClickListener(v -> {
            if (mCurrentType != TYPE_INFO) {
                mTypeText.setText(info_record.getText());
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
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERY_HISTORY:
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
                    activity.mNoRecordHint.setVisibility(activity.mList.isEmpty() ? View.VISIBLE : View.GONE);
                    activity.mAdapter.notifyDataSetChanged();
                    SrlUtils.finishRefresh(activity.mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(activity.mSrlFragmentMe, true);
                    break;
                default:
                    break;
            }
        }
    }
}