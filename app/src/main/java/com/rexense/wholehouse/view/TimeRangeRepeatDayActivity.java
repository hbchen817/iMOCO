package com.rexense.wholehouse.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.utility.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeRangeRepeatDayActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.repeat_recycler)
    RecyclerView mRepeatRV;

    private BaseQuickAdapter<RepeatDay, BaseViewHolder> mRepeatAdapter;
    private List<RepeatDay> mDayList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    private String mCustomWeekDayResult = null;

    private Typeface mIconfont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_repeat_day);
        ButterKnife.bind(this);

        mIconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");

        initStatusBar();
        mCustomWeekDayResult = getIntent().getStringExtra("custom_day");

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
        mTitle.setText(getString(R.string.custom));
        tvToolbarRight.setText(getString(R.string.nick_name_save));
        tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = false;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mDayList.size(); i++) {
                    RepeatDay day = mDayList.get(i);
                    if (day.isChecked) {
                        isChecked = true;
                        String dayValue = String.valueOf(day.getPos()+1);
                        if (stringBuilder.toString().length() == 0) {
                            stringBuilder.append(dayValue);
                        } else stringBuilder.append("," + dayValue);
                    }
                }
                if (!isChecked)
                    ToastUtils.showLongToast(TimeRangeRepeatDayActivity.this, R.string.choose_at_least_one_date);
                else {
                    Intent intent = new Intent();
                    intent.putExtra("custom_repeat_result", stringBuilder.toString());
                    setResult(1, intent);
                    finish();
                }
            }
        });

        String[] days = getResources().getStringArray(R.array.repeat_days);
        for (int i = 0; i < days.length; i++) {
            boolean isContains = false;
            if (mCustomWeekDayResult != null) {
                String[] s = mCustomWeekDayResult.split(",");
                for (int j = 0; j < s.length; j++) {
                    if (i == Integer.parseInt(s[j]) - 1) {
                        isContains = true;
                        break;
                    }
                }
            }
            mDayList.add(new RepeatDay(days[i], isContains, i));
        }

        mRepeatAdapter = new BaseQuickAdapter<RepeatDay, BaseViewHolder>(R.layout.item_simple_checked, mDayList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, RepeatDay day) {
                TextView itemChecked = holder.getView(R.id.item_checked);
                itemChecked.setTypeface(mIconfont);
                holder.setText(R.id.item_title, day.getName())
                        .setVisible(R.id.item_checked, day.isChecked())
                        .setVisible(R.id.item_divider, day.getPos() != 0);
            }
        };
        mRepeatAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                RepeatDay day = mDayList.get(position);
                boolean isChecked = day.isChecked();
                day.setChecked(!isChecked);
                mDayList.set(position, day);
                mRepeatAdapter.notifyDataSetChanged();
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRepeatRV.setLayoutManager(mLayoutManager);
        mRepeatRV.setAdapter(mRepeatAdapter);
    }

    private class RepeatDay {
        private String name;
        private boolean isChecked = false;
        private int pos = 0;

        public RepeatDay(String name, boolean isChecked, int pos) {
            this.name = name;
            this.isChecked = isChecked;
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}