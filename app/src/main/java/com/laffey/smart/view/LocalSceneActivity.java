package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityLocalSceneBinding;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.model.ItemScene;
import com.laffey.smart.utility.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocalSceneActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLocalSceneBinding mViewBinding;

    private static final String GATEWAY_ID = "gateway_id";

    private static final int TIMER_INTENT_TAG = 10000;
    private static final int TIMER_RANGE_INTENT_TAG = 10001;

    private String mGatewayId;

    private Typeface mIconfont;

    private BaseQuickAdapter<ItemScene.Timer, BaseViewHolder> mTimerAdapter;
    private final List<ItemScene.Timer> mTimerList = new ArrayList<>();

    public static void start(Context context, String gatewayId) {
        Intent intent = new Intent(context, LocalSceneActivity.class);
        intent.putExtra(GATEWAY_ID, gatewayId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalSceneBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initStatusBar();
        initView();
        initTimerAdapter();
    }

    private void initTimerAdapter() {
        mTimerAdapter = new BaseQuickAdapter<ItemScene.Timer, BaseViewHolder>(R.layout.item_condition_or_action_2, mTimerList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ItemScene.Timer timer) {
                TextView icon = holder.getView(R.id.icon_iv);
                TextView goIv = holder.getView(R.id.go_iv);
                icon.setTypeface(mIconfont);
                goIv.setTypeface(mIconfont);
                switch (timer.getType()) {
                    case "Timer": {
                        holder.setText(R.id.title, R.string.timer_point);
                        break;
                    }
                    case "TimeRange": {
                        holder.setText(R.id.title, R.string.time_range);
                        break;
                    }
                }
                holder.setText(R.id.detail, transformTimer(timer.getType(), timer.getCron()))
                        .setVisible(R.id.divider, false);
            }
        };
        mTimerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if ("Timer".equals(mTimerList.get(position).getType())) {
                    // 时间点
                    LocalTimeSelectorActivity.startForResult(LocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_INTENT_TAG);
                } else if ("TimeRange".equals(mTimerList.get(position).getType())) {
                    // 时间段
                    LocalTimeRangeSelectorActivity.startForResult(LocalSceneActivity.this, mTimerList.get(position).getCron(), TIMER_RANGE_INTENT_TAG);
                }
            }
        });
        mTimerAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showDelDialog(R.string.do_you_really_want_to_delete_the_current_option, 0, DEL_TIME_CONDITION_TAG);
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.timeConditionRv.setAdapter(mTimerAdapter);
        mViewBinding.timeConditionRv.setLayoutManager(layoutManager);
    }

    private final int DEL_TIME_CONDITION_TAG = 10001;
    private final int DEL_NORMAL_CONDITION_TAG = 10002;
    private final int DEL_ACTION_TAG = 10003;

    private void showDelDialog(int msgId, int pos, int tag) {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(LocalSceneActivity.this).create();
        alert.setIcon(R.drawable.dialog_quest);
        alert.setTitle(R.string.dialog_title);
        alert.setMessage(getResources().getString(msgId));
        //添加否按钮
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //添加是按钮
        alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                switch (tag) {
                    case DEL_TIME_CONDITION_TAG: {
                        // 删除时间条件
                        mTimerList.clear();
                        mTimerAdapter.notifyDataSetChanged();
                        mViewBinding.timeConditionRv.setVisibility(View.GONE);
                        mViewBinding.addTimeLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                    case DEL_NORMAL_CONDITION_TAG: {
                        // 删除条件
                        break;
                    }
                    case DEL_ACTION_TAG: {
                        // 删除动作
                        break;
                    }
                }
                arg0.dismiss();
            }
        });
        alert.show();
    }

    /**
     * 将cron转化为中文描述
     *
     * @param type 时间类型,Timer：时间点，TimeRange：时间段
     * @param cron 时间描述
     * @return 中文描述
     */
    private String transformTimer(String type, String cron) {
        StringBuilder time = new StringBuilder();

        String[] timers = cron.split(" ");
        if ("Timer".equals(type)) {
            // 时间点
            if (timers[1].length() == 1) {
                time.append("0" + timers[1] + ":");
            } else
                time.append(timers[1] + ":");
            if (timers[0].length() == 1)
                time.append("0" + timers[0] + " ");
            else time.append(timers[0] + " ");
        } else if ("TimeRange".equals(type)) {
            // 时间段
            String[] mins = timers[0].split("-");
            String beginMin = mins[0].length() == 1 ? ("0" + mins[0]) : (mins[0]);
            String endMin = mins[1].length() == 1 ? ("0" + mins[1]) : (mins[1]);

            String[] hours = timers[1].split("-");
            String beginHour = hours[0].length() == 1 ? ("0" + hours[0]) : (hours[0]);
            String endHour = hours[1].length() == 1 ? ("0" + hours[1]) : (hours[1]);

            time.append(beginHour + ":" + beginMin + "-" + endHour + ":" + endMin + " ");
        }

        if ("*".equals(timers[4])) {
            // 每天
            time.append(getString(R.string.everyday));
        } else if ("MON,TUE,WED,THU,FRI".equals(timers[4])) {
            // 工作日
            time.append(getString(R.string.set_time_workday_2));
        } else if ("SAT,SUN".equals(timers[4])) {
            // 周末
            time.append(getString(R.string.set_time_weekend_2));
        } else {
            // 自定义
            String[] custom = timers[4].split(",");
            for (int i = 0; i < custom.length; i++) {
                switch (custom[i]) {
                    case "MON": {
                        time.append(getString(R.string.week_1_all));
                        break;
                    }
                    case "TUE": {
                        time.append(getString(R.string.week_2_all));
                        break;
                    }
                    case "WED": {
                        time.append(getString(R.string.week_3_all));
                        break;
                    }
                    case "THU": {
                        time.append(getString(R.string.week_4_all));
                        break;
                    }
                    case "FRI": {
                        time.append(getString(R.string.week_5_all));
                        break;
                    }
                    case "SAT": {
                        time.append(getString(R.string.week_6_all));
                        break;
                    }
                    case "SUN": {
                        time.append(getString(R.string.week_0_all));
                        break;
                    }
                }

                if (i != custom.length - 1) {
                    time.append("，");
                }
            }
        }
        return time.toString();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.nameGo.setTypeface(iconfont);
        mViewBinding.statusGo.setTypeface(iconfont);
        mViewBinding.timeEnableGo.setTypeface(iconfont);
        mViewBinding.conditionEnableGo.setTypeface(iconfont);
        mViewBinding.addTimeIv.setTypeface(iconfont);
        mViewBinding.addConditionIv.setTypeface(iconfont);
        mViewBinding.addActionIv.setTypeface(iconfont);

        mViewBinding.nameTv.setOnClickListener(this);
        mViewBinding.statusTv.setOnClickListener(this);
        mViewBinding.timeEnableTv.setOnClickListener(this);
        mViewBinding.conditionEnableTv.setOnClickListener(this);
        mViewBinding.addTimeConditionTv.setOnClickListener(this);
        mViewBinding.sceneModeTv.setOnClickListener(this);
        mViewBinding.addConditionTv.setOnClickListener(this);
        mViewBinding.addActionTv.setOnClickListener(this);

        mViewBinding.nameGo.setOnClickListener(this);
        mViewBinding.statusGo.setOnClickListener(this);
        mViewBinding.timeEnableGo.setOnClickListener(this);
        mViewBinding.conditionEnableGo.setOnClickListener(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.create_new_scene);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.nameTv.getId() ||
                v.getId() == mViewBinding.nameGo.getId()) {
            showSceneNameDialogEdit();
        } else if (v.getId() == mViewBinding.statusTv.getId() ||
                v.getId() == mViewBinding.statusGo.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.scene_maintain_startusing));
            builder.addItem(getString(R.string.scene_maintain_stopusing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.statusTv.setText(tag);
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.timeEnableTv.getId() ||
                v.getId() == mViewBinding.timeEnableGo.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.there_are));
            builder.addItem(getString(R.string.nothing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.addTimeConditionLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                    mViewBinding.timeEnableTv.setText(tag);
                    if (position == 0 && mTimerList.size() > 0 && "Timer".equals(mTimerList.get(0).getType())) {
                        // 当时间条件为时间点时，condition节点无效
                        mViewBinding.conditionEnableTv.setText(R.string.nothing);
                        mViewBinding.sceneModeLayout.setVisibility(View.GONE);
                    }
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.conditionEnableTv.getId() ||
                v.getId() == mViewBinding.conditionEnableGo.getId()) {
            if (mTimerList.size() > 0 && "Timer".equals(mTimerList.get(0).getType()) && getString(R.string.there_are).equals(mViewBinding.timeEnableTv.getText().toString())) {
                ToastUtils.showLongToast(LocalSceneActivity.this, R.string.condition_setting_is_invalid_when_time_trigger_is_point_in_time);
                return;
            }
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.there_are));
            builder.addItem(getString(R.string.nothing));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    mViewBinding.sceneModeLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                    mViewBinding.conditionEnableTv.setText(tag);
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.addTimeConditionTv.getId() ||
                v.getId() == mViewBinding.addTimeIv.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addHeaderView(LayoutInflater.from(this).inflate(R.layout.custom_bottomlist_header, null));
            builder.addItem(getString(R.string.timer_point));
            builder.addItem(getString(R.string.time_range));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    switch (position) {
                        case 0: {
                            // 时间点
                            Intent intent = new Intent(LocalSceneActivity.this, LocalTimeSelectorActivity.class);
                            startActivityForResult(intent, TIMER_INTENT_TAG);
                            break;
                        }
                        case 1: {
                            // 时间段
                            Intent intent = new Intent(LocalSceneActivity.this, LocalTimeRangeSelectorActivity.class);
                            startActivityForResult(intent, TIMER_RANGE_INTENT_TAG);
                            break;
                        }
                    }
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.sceneModeTv.getId()) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
            builder.addItem(getString(R.string.satisfy_any_of_the_following_conditions));
            builder.addItem(getString(R.string.satisfy_all_of_the_following_conditions));
            builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                @Override
                public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                    dialog.dismiss();
                    if (position == 0) {
                        mViewBinding.sceneModeTv.setText(R.string.satisfy_any_of_the_following_conditions);
                    } else if (position == 1) {
                        mViewBinding.sceneModeTv.setText(R.string.satisfy_all_of_the_following_conditions);
                    }
                }
            });
            builder.build().show();
        } else if (v.getId() == mViewBinding.addConditionTv.getId() ||
                v.getId() == mViewBinding.addConditionIv.getId()) {
            LocalConditionDevsActivity.start(this, mGatewayId);
        } else if (v.getId() == mViewBinding.addActionTv.getId() ||
                v.getId() == mViewBinding.addActionIv.getId()) {
            ViseLog.d("添加动作");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TIMER_INTENT_TAG: {
                // 时间点
                if (resultCode == 0 && data != null) {
                    String result = data.getStringExtra("timer_selecter_result");
                    ItemScene.Timer timer = new ItemScene.Timer("Timer", result);
                    mTimerList.clear();
                    mTimerList.add(timer);
                    mTimerAdapter.notifyDataSetChanged();

                    mViewBinding.addTimeLayout.setVisibility(View.GONE);
                    mViewBinding.timeConditionRv.setVisibility(View.VISIBLE);

                    // 当时间条件为时间点时，condition节点无效
                    mViewBinding.conditionEnableTv.setText(R.string.nothing);
                    mViewBinding.sceneModeLayout.setVisibility(View.GONE);
                }
                break;
            }
            case TIMER_RANGE_INTENT_TAG: {
                // 时间段
                if (resultCode == 0 && data != null) {
                    String result = data.getStringExtra("timer_range_selecter_result");
                    ItemScene.Timer timer = new ItemScene.Timer("TimeRange", result);
                    mTimerList.clear();
                    mTimerList.add(timer);
                    mTimerAdapter.notifyDataSetChanged();

                    mViewBinding.addTimeLayout.setVisibility(View.GONE);
                    mViewBinding.timeConditionRv.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    // 显示场景名称修改对话框
    private void showSceneNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.scene_maintain_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);

        String name = mViewBinding.nameTv.getText().toString();
        if (name != null && name.length() > 0) {
            nameEt.setText(name);
            nameEt.setSelection(name.length());
        } else {
            nameEt.setText("");
        }

        nameEt.setHint(getString(R.string.pls_input_scene_name));
        final android.app.Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_320);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.dialogEditLblConfirm);
        View cancelView = view.findViewById(R.id.dialogEditLblCancel);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEt.getText().toString().trim();
                mViewBinding.nameTv.setText(nameStr);
                dialog.dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}