package com.laffey.smart.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.CScene;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.demoTest.ActionEntry;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.demoTest.IdentifierItemForCA;
import com.laffey.smart.demoTest.SceneCatalogIdCache;
import com.laffey.smart.model.EScene;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewSceneActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.name_tv)
    TextView mSceneNameTV;
    @BindView(R.id.name_go)
    TextView mSceneNameIV;
    @BindView(R.id.type_tv)
    TextView mSceneTypeTV;
    @BindView(R.id.type_go)
    TextView mSceneTypeIV;
    @BindView(R.id.status_tv)
    TextView mSceneStatusTV;
    @BindView(R.id.status_go)
    TextView mSceneStatusIV;
    @BindView(R.id.scene_mode_tv)
    TextView mSceneModeTV;
    @BindView(R.id.add_new_condition_iv)
    TextView mAddConditionIV;
    @BindView(R.id.add_new_condition_tv)
    TextView mAddConditionTV;
    @BindView(R.id.add_new_action_iv)
    TextView mAddActionIV;
    @BindView(R.id.add_new_action_tv)
    TextView mAddActionTV;
    @BindView(R.id.condition_recycler)
    RecyclerView mConditionRV;
    @BindView(R.id.add_condition_layout)
    RelativeLayout mAddConditionLayout;
    @BindView(R.id.condition_layout)
    LinearLayout mConditionLayout;
    @BindView(R.id.action_recycler)
    RecyclerView mActionRV;
    @BindView(R.id.add_action_layout)
    RelativeLayout mAddActionLayout;
    @BindView(R.id.del_tv)
    TextView mDelTV;
    @BindView(R.id.catalogId_layout)
    RelativeLayout mCatalogIdLayout;

    private String[] mTypeArray;
    private String[] mStatusArray;
    private String[] mModeArray;

    private String mSceneId = "";
    private String mSceneName = "";// 场景名称
    private String mCatalogId = CScene.TYPE_AUTOMATIC;// 0:手动场景 1:自动场景
    private boolean mEnable = true;// true:启用 false:停用
    private String mSceneMode = "any";// any:满足以下任一条件 all:满足以下所有条件

    private List<Object> mCaconditionList = new ArrayList<>();
    private List<IdentifierItemForCA> mIdentifierList = new ArrayList<>();
    private BaseQuickAdapter<Object, BaseViewHolder> mCaconditionAdapter;
    private LinearLayoutManager mCaconditionLayoutManager;

    private List<Object> mActionList = new ArrayList<>();
    private BaseQuickAdapter<Object, BaseViewHolder> mActionAdapter;
    private LinearLayoutManager mActionLayoutManager;

    private List<SceneActionActivity.SceneActionItem> mSceneActionList = new ArrayList<>();

    private SceneManager mSceneManager;
    private CallbackHandler mHandler;

    private boolean mValid = true;

    private Typeface mIconfont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene);
        ButterKnife.bind(this);

        mIconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mAddConditionIV.setTypeface(mIconfont);
        mAddActionIV.setTypeface(mIconfont);
        mSceneNameIV.setTypeface(mIconfont);
        mSceneTypeIV.setTypeface(mIconfont);
        mSceneStatusIV.setTypeface(mIconfont);

        mSceneManager = new SceneManager(this);
        initView();
        init();
        initStatusBar();
    }

    private void init() {
        mSceneId = getIntent().getStringExtra("scene_id");
        mCatalogId = getIntent().getStringExtra("catalog_id");
        mHandler = new CallbackHandler(this);
        if (mSceneId != null && mSceneId.length() > 0) {
            QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
            mDelTV.setVisibility(View.VISIBLE);
            mCatalogIdLayout.setVisibility(View.GONE);
            mSceneManager.querySceneDetail(mSceneId, mCatalogId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
        } else {
            mDelTV.setVisibility(View.GONE);
            mCatalogIdLayout.setVisibility(View.VISIBLE);
            mCatalogId = CScene.TYPE_AUTOMATIC;// 0:手动场景 1:自动场景
            mTitle.setText(getString(R.string.create_new_scene));
            mSceneNameTV.setText(getString(R.string.pls_input_scene_name));
            mSceneStatusTV.setText(getString(R.string.scene_maintain_startusing));
            mSceneTypeTV.setText(R.string.scenetype_automatic);
            mSceneModeTV.setText(R.string.satisfy_any_of_the_following_conditions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        mTypeArray = getResources().getStringArray(R.array.scene_type);
        mStatusArray = getResources().getStringArray(R.array.scene_status);
        mModeArray = getResources().getStringArray(R.array.scene_catalog_id);

        initConditionRV();
        initActionRV();
    }

    // 动作RecyclerView初始化
    private void initActionRV() {
        mActionAdapter = new BaseQuickAdapter<Object, BaseViewHolder>(R.layout.item_condition_or_action, mActionList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, Object o) {
                TextView goIcon = holder.getView(R.id.go_iv);
                goIcon.setTypeface(mIconfont);
                if (o instanceof ActionEntry.SendMsg) {
                    ActionEntry.SendMsg msg = (ActionEntry.SendMsg) o;
                    holder.setText(R.id.title, R.string.send_a_notification)
                            .setText(R.id.detail, msg.getMessage())
                            .setImageResource(R.id.icon_iv, R.drawable.notification);
                } else if (o instanceof ActionEntry.Trigger) {
                    ActionEntry.Trigger trigger = (ActionEntry.Trigger) o;
                    SceneActionActivity.SceneActionItem item = new SceneActionActivity.SceneActionItem();
                    for (int i = 0; i < mSceneActionList.size(); i++) {
                        if (trigger == mSceneActionList.get(i).getTrigger()) {
                            item = mSceneActionList.get(i);
                        }
                    }
                    String mode = CScene.TYPE_MANUAL.equals(item.getCatalogId()) ?
                            getString(R.string.scenetype_manual) : getString(R.string.scenetype_automatic);
                    holder.setImageResource(R.id.icon_iv, R.drawable.scene_appcolor)
                            .setText(R.id.title, item.getName())
                            .setText(R.id.detail, mode);
                } else if (o instanceof ActionEntry.Property) {
                    ActionEntry.Property property = (ActionEntry.Property) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                            break;
                        }
                    }

                    String desc = "";
                    if (item.getDesc() == null || item.getDesc().length() == 0) {
                        desc = item.getName().trim() + item.getValueName();
                    } else
                        desc = item.getDesc();

                    holder.setImageResource(R.id.icon_iv, R.drawable.condition_dev)
                            .setText(R.id.title, item.getNickName())
                            .setText(R.id.detail, desc);
                } else if (o instanceof ActionEntry.InvokeService) {
                    ActionEntry.InvokeService service = (ActionEntry.InvokeService) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                            break;
                        }
                    }
                    String desc = "";
                    if (item.getDesc() == null || item.getDesc().length() == 0) {
                        desc = item.getName().trim() + item.getValueName();
                    } else
                        desc = item.getDesc();

                    holder.setImageResource(R.id.icon_iv, R.drawable.condition_dev)
                            .setText(R.id.title, item.getNickName())
                            .setText(R.id.detail, desc);
                }
            }
        };
        mActionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Object o = mActionList.get(position);
                if (o instanceof ActionEntry.SendMsg) {
                    ActionEntry.SendMsg msg = (ActionEntry.SendMsg) o;
                    EventBus.getDefault().unregister(NewSceneActivity.this);
                    EventBus.getDefault().postSticky(msg);

                    Intent intent = new Intent(NewSceneActivity.this, NotificationActionActivity.class);
                    startActivity(intent);
                } else if (o instanceof ActionEntry.Trigger) {
                    ActionEntry.Trigger trigger = (ActionEntry.Trigger) o;
                    SceneActionActivity.SceneActionItem item = new SceneActionActivity.SceneActionItem();
                    for (int i = 0; i < mSceneActionList.size(); i++) {
                        if (trigger == mSceneActionList.get(i).getTrigger()) {
                            item = mSceneActionList.get(i);
                            break;
                        }
                    }

                    EventBus.getDefault().unregister(NewSceneActivity.this);
                    EventBus.getDefault().postSticky(item);

                    Intent intent = new Intent(NewSceneActivity.this, SceneActionActivity.class);
                    startActivity(intent);
                } else if (o instanceof ActionEntry.Property) {
                    ActionEntry.Property property = (ActionEntry.Property) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (int i = 0; i < mIdentifierList.size(); i++) {
                        if (property == mIdentifierList.get(i).getObject()) {
                            item = mIdentifierList.get(i);
                            break;
                        }
                    }
                    EventBus.getDefault().unregister(NewSceneActivity.this);
                    EventBus.getDefault().postSticky(item);

                    Intent intent = new Intent(NewSceneActivity.this, EditPropertyValueForActionActivity.class);
                    startActivity(intent);
                } else if (o instanceof ActionEntry.InvokeService) {
                    ActionEntry.InvokeService property = (ActionEntry.InvokeService) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (int i = 0; i < mIdentifierList.size(); i++) {
                        if (property == mIdentifierList.get(i).getObject()) {
                            item = mIdentifierList.get(i);
                            break;
                        }
                    }
                    EventBus.getDefault().unregister(NewSceneActivity.this);
                    EventBus.getDefault().postSticky(item);

                    Intent intent = new Intent(NewSceneActivity.this, EditPropertyValueForActionActivity.class);
                    startActivity(intent);
                }
            }
        });
        mActionAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(NewSceneActivity.this).create();
                alert.setIcon(R.drawable.dialog_quest);
                alert.setTitle(R.string.dialog_title);
                alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_action));
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
                        Object o = mActionList.get(position);
                        if (o instanceof ActionEntry.Property) {
                            for (int i = 0; i < mIdentifierList.size(); i++) {
                                if (o == mIdentifierList.get(i).getObject()) {
                                    mIdentifierList.remove(i);
                                    break;
                                }
                            }
                        } else if (o instanceof ActionEntry.Trigger) {
                            for (int i = 0; i < mSceneActionList.size(); i++) {
                                if (o == mSceneActionList.get(i).getTrigger()) {
                                    mSceneActionList.remove(i);
                                    break;
                                }
                            }
                        } else if (o instanceof ActionEntry.SendMsg) {

                        }

                        mActionList.remove(position);
                        mActionAdapter.notifyDataSetChanged();
                        if (mActionList.size() == 0)
                            mAddActionLayout.setVisibility(View.VISIBLE);
                    }
                });
                alert.show();
                return false;
            }
        });

        mActionLayoutManager = new LinearLayoutManager(this);
        mActionLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mActionRV.setLayoutManager(mActionLayoutManager);
        mActionRV.setAdapter(mActionAdapter);
    }

    // 条件RecyclerView初始化
    private void initConditionRV() {
        mCaconditionAdapter = new BaseQuickAdapter<Object, BaseViewHolder>(R.layout.item_condition_or_action, mCaconditionList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, Object o) {
                TextView goIcon = holder.getView(R.id.go_iv);
                goIcon.setTypeface(mIconfont);
                if (o instanceof CaConditionEntry.Timer) {
                    //icon.setText(getString(R.string.icon_timer_2));
                    CaConditionEntry.Timer timer = (CaConditionEntry.Timer) o;
                    holder.setImageResource(R.id.icon_iv, R.drawable.conditon_timer)
                            .setText(R.id.title, R.string.timer_point);
                    String cronType = timer.getCronType();
                    StringBuilder stringBuilder = new StringBuilder();
                    /*if (cronType.equals(Constant.TIMER_LINUX)) {
                        //  * * * 每天
                        //  ? * 1,2,3,4,5 工作日
                        //  ? * 6,7 周末
                        String cron = timer.getCron();
                        String[] crons = cron.split(" ");
                        String hour = crons[1].length() == 2 ? crons[1] : "0" + crons[1];
                        String min = crons[0].length() == 2 ? crons[0] : "0" + crons[0];
                        if ("*".equals(crons[crons.length - 1]))
                            stringBuilder.append(hour + ":" + min + " " + getString(R.string.everyday));
                        else if ("1,2,3,4,5".equals(crons[crons.length - 1]))
                            stringBuilder.append(hour + ":" + min + " " + getString(R.string.working_days));
                        else if ("6,7".equals(crons[crons.length - 1]))
                            stringBuilder.append(hour + ":" + min + " " + getString(R.string.weekend));
                        else {
                            stringBuilder.append(hour + ":" + min + " ");
                            stringBuilder.append(getRepeatString(crons[crons.length - 1]));
                        }
                    } else {
                        String cron = timer.getCron();
                        String[] crons = cron.split(" ");
                        String hour = crons[2].length() == 2 ? crons[2] : "0" + crons[2];
                        String min = crons[1].length() == 2 ? crons[1] : "0" + crons[1];
                        stringBuilder.append(crons[6] + "年" + crons[4] + "月" + crons[3] + "日 ");
                        stringBuilder.append(hour + ":" + min + " " + getString(R.string.do_once));
                    }*/

                    String cron = timer.getCron();
                    String[] crons = cron.split(" ");
                    String hour = crons[2].length() == 2 ? crons[2] : "0" + crons[2];
                    String min = crons[1].length() == 2 ? crons[1] : "0" + crons[1];

                    int cronsLength = crons.length;
                    if (!crons[cronsLength - 1].equals("*")) {
                        // 执行一次
                        stringBuilder.append(crons[cronsLength - 1] + "年" + crons[cronsLength - 3] + "月" + crons[cronsLength - 4] + "日 ");
                        stringBuilder.append(hour + ":" + min + " " + getString(R.string.do_once));
                    } else if (crons[cronsLength - 2].equals("?")) {
                        // 每天
                        stringBuilder.append(hour + ":" + min + " " + getString(R.string.everyday));
                    } else if (crons[cronsLength - 2].equals("mon,tue,wed,thu,fri")) {
                        // 工作日
                        stringBuilder.append(hour + ":" + min + " " + getString(R.string.working_days));
                    } else if (crons[cronsLength - 2].equals("sat,sun")) {
                        // 周末
                        stringBuilder.append(hour + ":" + min + " " + getString(R.string.weekend));
                    } else {
                        // 自定义
                        stringBuilder.append(hour + ":" + min + " ");
                        stringBuilder.append(getRepeatString(crons[5]));
                    }
                    holder.setText(R.id.detail, stringBuilder.toString());
                } else if (o instanceof CaConditionEntry.TimeRange) {
                    CaConditionEntry.TimeRange timeRange = (CaConditionEntry.TimeRange) o;
                    holder.setImageResource(R.id.icon_iv, R.drawable.condition_time_range)
                            .setText(R.id.title, R.string.time_range);
                    String beginTime = timeRange.getBeginDate();
                    String endTime = timeRange.getEndDate();
                    String repeat = timeRange.getRepeat();

                    StringBuilder stringBuilder = new StringBuilder();
                    if ("23:59:59".equals(timeRange.getEndDate()))
                        stringBuilder.append(getString(R.string.all_the_day));
                    else stringBuilder.append(beginTime + "-" + endTime);
                    if (repeat == null || repeat.length() == 0) {
                        stringBuilder.append(" " + getString(R.string.do_once));
                    } else {
                        stringBuilder.append(" " + getRepeatString(repeat));
                    }
                    holder.setText(R.id.detail, stringBuilder.toString());
                } else if (o instanceof CaConditionEntry.Property) {
                    CaConditionEntry.Property property = (CaConditionEntry.Property) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                            break;
                        }
                    }

                    String desc = "";
                    if (item.getDesc() == null || item.getDesc().length() == 0) {
                        desc = item.getName().trim() + item.getValueName();
                    } else
                        desc = item.getDesc();

                    holder.setImageResource(R.id.icon_iv, R.drawable.condition_dev)
                            .setText(R.id.title, item.getNickName())
                            .setText(R.id.detail, desc);
                } else if (o instanceof CaConditionEntry.Event) {
                    CaConditionEntry.Event event = (CaConditionEntry.Event) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                            break;
                        }
                    }
                    String desc = "";
                    if (item.getDesc() == null || item.getDesc().length() == 0) {
                        desc = item.getName().trim() + item.getValueName();
                    } else
                        desc = item.getDesc();

                    holder.setImageResource(R.id.icon_iv, R.drawable.condition_dev)
                            .setText(R.id.title, item.getNickName())
                            .setText(R.id.detail, desc);
                }
            }
        };
        mCaconditionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Object o = mCaconditionList.get(position);
                if (o instanceof CaConditionEntry.Timer) {
                    CaConditionEntry.Timer timer = (CaConditionEntry.Timer) o;

                    Message message = new Message();
                    message.what = Constant.SCENE_CONDITION_TIMER_EDIT;
                    message.obj = timer;

                    EventBus.getDefault().unregister(NewSceneActivity.this);

                    EventBus.getDefault().postSticky(message);

                    Intent intent = new Intent(NewSceneActivity.this, TimeSelectorActivity.class);
                    startActivity(intent);
                } else if (o instanceof CaConditionEntry.TimeRange) {
                    CaConditionEntry.TimeRange timeRange = (CaConditionEntry.TimeRange) o;

                    Message message = new Message();
                    message.what = Constant.SCENE_CONDITION_TIME_RANGE_EDIT;
                    message.obj = timeRange;

                    EventBus.getDefault().unregister(NewSceneActivity.this);

                    EventBus.getDefault().postSticky(message);

                    Intent intent = new Intent(NewSceneActivity.this, TimeRangeSelectorActivity.class);
                    startActivity(intent);
                } else if (o instanceof CaConditionEntry.Property) {
                    CaConditionEntry.Property property = (CaConditionEntry.Property) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                        }
                    }

                    EventBus.getDefault().unregister(NewSceneActivity.this);

                    EventBus.getDefault().postSticky(item);

                    Intent intent = new Intent(NewSceneActivity.this, EditPropertyValueActivity.class);
                    startActivity(intent);
                } else if (o instanceof CaConditionEntry.Event) {
                    CaConditionEntry.Event Event = (CaConditionEntry.Event) o;
                    IdentifierItemForCA item = new IdentifierItemForCA();
                    for (IdentifierItemForCA i : mIdentifierList) {
                        if (i.getObject() == o) {
                            item = i;
                        }
                    }

                    EventBus.getDefault().unregister(NewSceneActivity.this);

                    EventBus.getDefault().postSticky(item);

                    Intent intent = new Intent(NewSceneActivity.this, EditPropertyValueActivity.class);
                    startActivity(intent);
                }
            }
        });
        mCaconditionAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(NewSceneActivity.this).create();
                alert.setIcon(R.drawable.dialog_quest);
                alert.setTitle(R.string.dialog_title);
                alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_current_option));
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
                        Object o = mCaconditionList.get(position);
                        if (o instanceof CaConditionEntry.Timer) {
                            for (int i = 0; i < mIdentifierList.size(); i++) {
                                if (o == mIdentifierList.get(i).getObject()) {
                                    mIdentifierList.remove(i);
                                    break;
                                }
                            }
                        }

                        mCaconditionList.remove(position);
                        mCaconditionAdapter.notifyDataSetChanged();
                        if (mCaconditionList.size() == 0)
                            mAddConditionLayout.setVisibility(View.VISIBLE);
                    }
                });
                alert.show();
                return false;
            }
        });
        mCaconditionLayoutManager = new LinearLayoutManager(this);
        mCaconditionLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mConditionRV.setLayoutManager(mCaconditionLayoutManager);
        mConditionRV.setAdapter(mCaconditionAdapter);
    }

    private String getCompareTypeString(String compareType) {
        if ("<".equals(compareType))
            return getString(R.string.less_than);
        else if ("<=".equals(compareType))
            return getString(R.string.less_than_or_equal_to);
        else if ("==".equals(compareType))
            return getString(R.string.equal_to);
        else if (">".equals(compareType))
            return getString(R.string.greater_than);
        else if (">=".equals(compareType))
            return getString(R.string.great_than_or_equal_to);
        else if ("!=".equals(compareType))
            return getString(R.string.is_not_equal_to);
        return "";
    }

    private String getRepeatString(String repeat) {
        StringBuilder stringBuilder = new StringBuilder();
        if ("mon,tue,wed,thu,fri,sat,sun".equals(repeat))
            return getString(R.string.everyday);
        else if ("mon,tue,wed,thu,fri".equals(repeat))
            return getString(R.string.working_days);
        else if ("sat,sun".equals(repeat))
            return getString(R.string.weekend);
        String[] s = repeat.split(",");
        for (int i = 0; i < s.length; i++) {
            if (i == 0) {
                if ("mon".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_1_all));
                else if ("tue".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_2_all));
                else if ("wed".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_3_all));
                else if ("sun".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_0_all));
                else if ("thu".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_4_all));
                else if ("fri".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_5_all));
                else if ("sat".equals(s[i]))
                    stringBuilder.append(getString(R.string.week_6_all));
            } else {
                if ("mon".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_1_all));
                else if ("tue".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_2_all));
                else if ("wed".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_3_all));
                else if ("sun".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_0_all));
                else if ("thu".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_4_all));
                else if ("fri".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_5_all));
                else if ("sat".equals(s[i]))
                    stringBuilder.append(", " + getString(R.string.week_6_all));
            }
        }
        return stringBuilder.toString();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateConditionOrAction(Object o) {
        if (o != null && o instanceof CaConditionEntry.Timer) {
            // 时间点
            if (!mCaconditionList.contains(o))
                mCaconditionList.add(o);
            mCaconditionAdapter.notifyDataSetChanged();
            mAddConditionLayout.setVisibility(View.GONE);
        } else if (o != null && o instanceof CaConditionEntry.TimeRange) {
            // 时间段
            if (!mCaconditionList.contains(o))
                mCaconditionList.add(o);
            mCaconditionAdapter.notifyDataSetChanged();
            mAddConditionLayout.setVisibility(View.GONE);
        } else if (o != null && o instanceof IdentifierItemForCA) {
            IdentifierItemForCA item = (IdentifierItemForCA) o;
            if (!mIdentifierList.contains(item))
                mIdentifierList.add(item);
            Object o1 = item.getObject();
            if (o1 instanceof ActionEntry.Property) {
                // 动作-设备属性
                if (!mActionList.contains(o1))
                    mActionList.add(o1);
                mActionAdapter.notifyDataSetChanged();
                mAddActionLayout.setVisibility(View.GONE);
            } else if (o1 instanceof CaConditionEntry.Property) {
                // 条件-设备属性
                if (!mCaconditionList.contains(item.getObject()))
                    mCaconditionList.add(item.getObject());
                mCaconditionAdapter.notifyDataSetChanged();
                mAddConditionLayout.setVisibility(View.GONE);
            } else if (o1 instanceof ActionEntry.InvokeService) {
                // 服务
                if (!mActionList.contains(o1))
                    mActionList.add(o1);
                mActionAdapter.notifyDataSetChanged();
                mAddActionLayout.setVisibility(View.GONE);
            } else if (o1 instanceof CaConditionEntry.Event) {
                // 事件
                if (!mCaconditionList.contains(item.getObject()))
                    mCaconditionList.add(item.getObject());
                mCaconditionAdapter.notifyDataSetChanged();
                mAddConditionLayout.setVisibility(View.GONE);
            }
        } else if (o != null && o instanceof ActionEntry.SendMsg) {
            ActionEntry.SendMsg msg = (ActionEntry.SendMsg) o;
            if (!mActionList.contains(msg))
                mActionList.add(msg);

            mActionAdapter.notifyDataSetChanged();
            mAddActionLayout.setVisibility(View.GONE);
        } else if (o != null && o instanceof SceneActionActivity.SceneActionItem) {
            SceneActionActivity.SceneActionItem item = (SceneActionActivity.SceneActionItem) o;
            if (!mSceneActionList.contains(item)) {
                mSceneActionList.add(item);
            }
            ActionEntry.Trigger trigger = item.getTrigger();
            if (!mActionList.contains(trigger))
                mActionList.add(trigger);
            mActionAdapter.notifyDataSetChanged();
            mAddActionLayout.setVisibility(View.GONE);
        }
        EventBus.getDefault().removeStickyEvent(o);
    }

    @OnClick({R.id.name_tv, R.id.name_go, R.id.type_tv, R.id.type_go, R.id.status_tv, R.id.status_go,
            R.id.scene_mode_tv, R.id.add_new_condition_iv, R.id.add_new_condition_tv, R.id.add_new_action_iv,
            R.id.add_new_action_tv, R.id.tv_toolbar_right, R.id.del_tv})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del_tv: {
                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(NewSceneActivity.this).create();
                alert.setIcon(R.drawable.dialog_quest);
                alert.setTitle(R.string.dialog_title);
                alert.setMessage(getResources().getString(R.string.do_you_really_want_to_delete_the_current_scene));
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
                        arg0.dismiss();
                        mSceneManager.deleteScene(mSceneId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                        QMUITipDialogUtil.showLoadingDialg(NewSceneActivity.this, R.string.deleting_the_scene);
                    }
                });
                alert.show();
                break;
            }
            case R.id.tv_toolbar_right: {
                if (mSceneName == null || mSceneName.length() == 0) {
                    ToastUtils.showLongToast(this, R.string.pls_enter_a_scene_name);
                    return;
                }
                if (mActionList == null || mActionList.size() == 0) {
                    ToastUtils.showLongToast(this, R.string.pls_add_actions);
                    return;
                }
                if (CScene.TYPE_AUTOMATIC.equals(mCatalogId)) {
                    if (mCaconditionList == null || mCaconditionList.size() == 0) {
                        ToastUtils.showLongToast(this, R.string.pls_add_conditions);
                        return;
                    }

                    int timerCount = 0;
                    for (int i = 0; i < mCaconditionList.size(); i++) {
                        Object o = mCaconditionList.get(i);
                        if (o instanceof CaConditionEntry.Timer
                                || o instanceof CaConditionEntry.TimeRange)
                            timerCount++;
                    }
                    if (timerCount > 1) {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(this, R.string.time_condition_can_not_more_than_one);
                        return;
                    }
                }
                if ("any".equals(mSceneMode)) {
                    for (int i = 0; i < mCaconditionList.size(); i++) {
                        Object o = mCaconditionList.get(i);
                        if (o instanceof CaConditionEntry.TimeRange) {
                            QMUITipDialogUtil.dismiss();
                            ToastUtils.showLongToast(this, R.string.time_period_cannot_be_used_as_condition_to_satisfy_any_of_following_conditions);
                            return;
                        }
                    }
                } else {
                    for (int i = 0; i < mCaconditionList.size(); i++) {
                        Object o = mCaconditionList.get(i);
                        if (o instanceof CaConditionEntry.TimeRange && mCaconditionList.size() == 1) {
                            QMUITipDialogUtil.dismiss();
                            ToastUtils.showLongToast(this, R.string.time_period_cannot_be_only_one);
                            return;
                        }
                    }

                    // 时间点，事件上报
                    int timerCount = 0;
                    int eventCount = 0;
                    for (int i = 0; i < mCaconditionList.size(); i++) {
                        Object o = mCaconditionList.get(i);
                        if (o instanceof CaConditionEntry.Timer)
                            timerCount++;
                        else if (o instanceof CaConditionEntry.Event)
                            eventCount++;
                    }
                    if (timerCount > 0 && eventCount > 0) {
                        QMUITipDialogUtil.dismiss();
                        ToastUtils.showLongToast(this, R.string.timer_and_event_cannot_be_reported_as_meet_all_of_the_following_conditions_at_the_same_time);
                        return;
                    }
                }

                QMUITipDialogUtil.showLoadingDialg(this, R.string.is_uploading);

                if (mSceneId == null || mSceneId.length() == 0) {
                    EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                            mCatalogId, mSceneName, mSceneId);
                    mSceneManager.createCAScene(baseInfoEntry, mEnable, mSceneMode, mCaconditionList,
                            mActionList, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                } else {
                    EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(mSceneId, SystemParameter.getInstance().getHomeId(),
                            mCatalogId, mSceneName, mSceneId);
                    mSceneManager.updateCAScene(baseInfoEntry, mEnable, mSceneMode, mCaconditionList,
                            mActionList, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                }
                break;
            }
            case R.id.add_new_action_tv:
            case R.id.add_new_action_iv: {
                Intent intent = new Intent(this, AddActionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.add_new_condition_tv:
            case R.id.add_new_condition_iv: {
                Intent intent = new Intent(this, AddConditionActivity.class);

                boolean hasTimeCondition = false;
                for (int i = 0; i < mCaconditionList.size(); i++) {
                    Object o = mCaconditionList.get(i);
                    if (o instanceof CaConditionEntry.Timer || o instanceof CaConditionEntry.TimeRange) {
                        hasTimeCondition = true;
                        break;
                    }
                }

                intent.putExtra("has_time_condition", hasTimeCondition);
                startActivity(intent);
                break;
            }
            case R.id.scene_mode_tv: {
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
                for (int i = 0; i < mModeArray.length; i++) {
                    builder.addItem(mModeArray[i]);
                }
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        if (position == 0) mSceneMode = "any";
                        else mSceneMode = "all";
                        mSceneModeTV.setText(mModeArray[position]);
                        dialog.dismiss();
                    }
                });
                builder.build().show();
                break;
            }
            case R.id.status_go:
            case R.id.status_tv: {
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
                for (int i = 0; i < mStatusArray.length; i++) {
                    builder.addItem(mStatusArray[i]);
                }
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        if (position == 0) mEnable = true;
                        else mEnable = false;
                        mSceneStatusTV.setText(mStatusArray[position]);
                        dialog.dismiss();
                    }
                });
                builder.build().show();
                break;
            }
            case R.id.type_go:
            case R.id.type_tv: {
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
                for (int i = 0; i < mTypeArray.length; i++) {
                    builder.addItem(mTypeArray[i]);
                }
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        if (position == 0) mCatalogId = "1";
                        else mCatalogId = "0";
                        mConditionLayout.setVisibility("0".equals(mCatalogId) ? View.GONE : View.VISIBLE);
                        mSceneTypeTV.setText(mTypeArray[position]);
                        dialog.dismiss();
                    }
                });
                builder.build().show();
                break;
            }
            case R.id.name_go:
            case R.id.name_tv: {
                showSceneNameDialogEdit();
                break;
            }
        }
    }

    private class CallbackHandler extends Handler {
        private WeakReference<Activity> weakRf;

        public CallbackHandler(Activity activity) {
            weakRf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (weakRf.get() == null) return;
            switch (msg.what) {
                case Constant.MSG_CALLBACK_DELETESCENE: {
                    QMUITipDialogUtil.showSuccessDialog(NewSceneActivity.this, R.string.delete_the_success);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            QMUITipDialogUtil.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("scene_id", mSceneId);
                            setResult(100, intent);
                            finish();
                        }
                    }, 1000);
                    break;
                }
                case Constant.MSG_CALLBACK_CREATESCENE: {
                    SystemParameter.getInstance().setIsRefreshSceneListData(true);
                    QMUITipDialogUtil.showSuccessDialog(NewSceneActivity.this, R.string.scenario_created_successfully);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            QMUITipDialogUtil.dismiss();
                            finish();
                        }
                    }, 1000);
                    break;
                }
                case Constant.MSG_CALLBACK_UPDATE_SCENE: {
                    //JSONObject object = JSON.parseObject((String) msg.obj);
                    QMUITipDialogUtil.dismiss();
                    QMUITipDialogUtil.showSuccessDialog(NewSceneActivity.this, R.string.scene_updated_successfully);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            QMUITipDialogUtil.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("catalog_id", mCatalogId);
                            intent.putExtra("description", mSceneId);
                            intent.putExtra("enable", mEnable);
                            intent.putExtra("id", mSceneId);
                            intent.putExtra("name", mSceneName);
                            intent.putExtra("valid", mValid);
                            setResult(101, intent);

                            finish();
                        }
                    }, 1000);
                    break;
                }
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL: {
                    QMUITipDialogUtil.dismiss();
                    JSONObject object = JSON.parseObject((String) msg.obj);
                    ViseLog.d(new Gson().toJson(object));
                    mValid = object.getBoolean("valid");
                    mSceneName = object.getString("name");
                    mTitle.setText(mSceneName);
                    mSceneNameTV.setText(mSceneName);

                    mSceneTypeTV.setText(CScene.TYPE_MANUAL.equals(mCatalogId) ?
                            R.string.scenetype_manual : R.string.scenetype_automatic);
                    mConditionLayout.setVisibility(CScene.TYPE_MANUAL.equals(mCatalogId) ? View.GONE : View.VISIBLE);

                    mEnable = object.getBoolean("enable");
                    mSceneStatusTV.setText(mEnable ?
                            getString(R.string.scene_maintain_startusing) : getString(R.string.scene_maintain_stopusing));

                    mSceneMode = object.getString("mode");
                    if ("any".equals(mSceneMode))
                        mSceneModeTV.setText(R.string.satisfy_any_of_the_following_conditions);
                    else
                        mSceneModeTV.setText(R.string.satisfy_all_of_the_following_conditions);

                    JSONArray conditions = JSON.parseArray(object.getString("caConditionsJson"));
                    if (conditions.size() > 0) {
                        for (int i = 0; i < conditions.size(); i++) {
                            String s = conditions.getString(i);
                            JSONObject object1 = JSON.parseObject(s);
                            String uri = object1.getString("uri");
                            if (Constant.SCENE_CONDITION_TIMER.equals(uri)) {
                                // 时间点
                                CaConditionEntry.Timer timer = new CaConditionEntry.Timer();
                                timer.setCron(object1.getJSONObject("params").getString("cron"));
                                timer.setCronType(object1.getJSONObject("params").getString("cronType"));
                                timer.setTimezoneID(object1.getJSONObject("params").getString("timezoneID"));
                                mCaconditionList.add(timer);
                            } else if (Constant.SCENE_CONDITION_TIME_RANGE.equals(uri)) {
                                // 时间段
                                CaConditionEntry.TimeRange timeRange = new CaConditionEntry.TimeRange();
                                timeRange.setRepeat(object1.getJSONObject("params").getString("repeat"));
                                timeRange.setTimezoneID(object1.getJSONObject("params").getString("timezoneID"));
                                timeRange.setEndDate(object1.getJSONObject("params").getString("endDate"));
                                timeRange.setBeginDate(object1.getJSONObject("params").getString("beginDate"));
                                timeRange.setFormat(object1.getJSONObject("params").getString("format"));
                                mCaconditionList.add(timeRange);
                            } else if (Constant.SCENE_CONDITION_PROPERTY.equals(uri)) {
                                // 设备状态
                                IdentifierItemForCA item = new IdentifierItemForCA();
                                String iotId = object1.getJSONObject("params").getString("iotId");
                                item.setIotId(iotId);
                                item.setName(object1.getJSONObject("params").getString("localizedPropertyName"));

                                JSONObject jsonObject = DeviceBuffer.getExtendedInfo(iotId);
                                if (jsonObject != null) {
                                    String keyNickName = jsonObject.getString(object1.getJSONObject("params").getString("propertyName"));
                                    if (keyNickName != null) {
                                        item.setName(keyNickName);
                                    }
                                }

                                item.setNickName(object1.getJSONObject("params").getString("deviceNickName"));
                                String eventCode = object1.getJSONObject("params").getString("localizedEventCode");
                                if (eventCode == null) item.setType(1);
                                item.setValueName(object1.getJSONObject("params").getString("localizedCompareValueName"));

                                CaConditionEntry.Property property = new CaConditionEntry.Property();
                                property.setPropertyName(object1.getJSONObject("params").getString("propertyName"));
                                property.setCompareType(object1.getJSONObject("params").getString("compareType"));
                                property.setProductKey(object1.getJSONObject("params").getString("productKey"));
                                property.setDeviceName(object1.getJSONObject("params").getString("deviceName"));
                                property.setCompareValue(object1.getJSONObject("params").get("compareValue"));

                                if (!"==".equals(property.getCompareType()))
                                    item.setDesc(item.getName().trim() + getCompareTypeString(property.getCompareType()) + property.getCompareValue());

                                item.setObject(property);
                                mCaconditionList.add(property);
                                mIdentifierList.add(item);
                            } else if (Constant.SCENE_CONDITION_EVENT.equals(uri)) {
                                // 设备事件
                                IdentifierItemForCA item = new IdentifierItemForCA();

                                String localizedEventCode = object1.getJSONObject("params").getString("localizedEventCode");
                                String localizedPropertyName = object1.getJSONObject("params").getString("localizedPropertyName");
                                String compareType = object1.getJSONObject("params").getString("compareType");
                                Object compareValue = object1.getJSONObject("params").get("compareValue");
                                String compareTypeName = getString(R.string.equal_to);
                                if ("<".equals(compareType))
                                    compareTypeName = getString(R.string.less_than);
                                else if ("<=".equals(compareType))
                                    compareTypeName = getString(R.string.less_than_or_equal_to);
                                else if (">=".equals(compareType))
                                    compareTypeName = getString(R.string.great_than_or_equal_to);
                                else if ("!=".equals(compareType))
                                    compareTypeName = getString(R.string.is_not_equal_to);

                                item.setDesc(localizedEventCode + localizedPropertyName + compareTypeName + compareValue.toString());

                                String productKey = object1.getJSONObject("params").getString("productKey");
                                String iotId = object1.getJSONObject("params").getString("iotId");
                                if (Constant.KEY_NICK_NAME_PK.contains(productKey)) {
                                    String key = compareValue.toString();
                                    JSONObject jsonObject = DeviceBuffer.getExtendedInfo(iotId);
                                    if (jsonObject != null) {
                                        String keyName = jsonObject.getString(key);
                                        item.setDesc(getString(R.string.trigger_buttons) + keyName);
                                    } else {
                                        if ("1".equals(key)) {
                                            if (CTSL.PK_ONE_SCENE_SWITCH.equals(key))
                                                item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_0));
                                            else
                                                item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_1));
                                        } else if ("2".equals(key)) {
                                            item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_2));
                                        } else if ("3".equals(key)) {
                                            item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_3));
                                        } else if ("4".equals(key)) {
                                            item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_4));
                                        } else if ("5".equals(key)) {
                                            item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_5));
                                        } else if ("6".equals(key)) {
                                            item.setDesc(getString(R.string.trigger_buttons) + getString(R.string.key_6));
                                        }
                                    }
                                }

                                item.setIotId(object1.getJSONObject("params").getString("iotId"));
                                item.setName(localizedEventCode);
                                item.setNickName(object1.getJSONObject("params").getString("deviceNickName"));
                                item.setType(3);
                                item.setValueName(localizedPropertyName);

                                CaConditionEntry.Event event = new CaConditionEntry.Event();
                                event.setPropertyName(object1.getJSONObject("params").getString("propertyName"));
                                event.setCompareType(object1.getJSONObject("params").getString("compareType"));
                                event.setProductKey(object1.getJSONObject("params").getString("productKey"));
                                event.setDeviceName(object1.getJSONObject("params").getString("deviceName"));
                                event.setCompareValue(compareValue);
                                event.setEventCode(object1.getJSONObject("params").getString("eventCode"));

                                item.setObject(event);
                                mCaconditionList.add(event);
                                mIdentifierList.add(item);
                            }
                        }
                    }
                    mCaconditionAdapter.notifyDataSetChanged();
                    if (mCaconditionList != null && mCaconditionList.size() > 0) {
                        mAddConditionLayout.setVisibility(View.GONE);
                    }

                    JSONArray actions = JSON.parseArray(object.getString("actionsJson"));
                    if (actions.size() > 0) {
                        for (int i = 0; i < actions.size(); i++) {
                            String s = actions.getString(i);
                            JSONObject object1 = JSON.parseObject(s);
                            String uri = object1.getString("uri");
                            if (Constant.SCENE_ACTION_SEND.equals(uri)) {
                                ActionEntry.SendMsg sendMsg = new ActionEntry.SendMsg();

                                JSONObject params = object1.getJSONObject("params");
                                JSONObject customData = params.getJSONObject("customData");

                                sendMsg.setMessage(customData.getString("message"));
                                mActionList.add(sendMsg);
                            } else if (Constant.SCENE_ACTION_TRIGGER.equals(uri)) {
                                ActionEntry.Trigger trigger = new ActionEntry.Trigger();

                                String name = object1.getJSONObject("params").getString("name");
                                if (name != null) {
                                    String sceneId = object1.getJSONObject("params").getString("sceneId");
                                    String catalogId = SceneCatalogIdCache.getInstance().getValue(sceneId);
                                    trigger.setSceneId(sceneId);

                                    SceneActionActivity.SceneActionItem item = new SceneActionActivity.SceneActionItem();
                                    item.setCatalogId(catalogId);
                                    item.setId(sceneId);
                                    item.setChecked(true);
                                    item.setName(object1.getJSONObject("params").getString("name"));

                                    item.setTrigger(trigger);
                                    mActionList.add(trigger);
                                    mSceneActionList.add(item);
                                }
                            } else if (Constant.SCENE_ACTION_PROPERTY.equals(uri)) {
                                ActionEntry.Property property = new ActionEntry.Property();

                                IdentifierItemForCA item = new IdentifierItemForCA();
                                String iotId = object1.getJSONObject("params").getString("iotId");
                                item.setIotId(iotId);
                                item.setName(object1.getJSONObject("params").getString("localizedPropertyName"));

                                JSONObject jsonObject = DeviceBuffer.getExtendedInfo(iotId);
                                String propertyName = object1.getJSONObject("params").getString("propertyName");
                                if (jsonObject != null) {
                                    String keyNickName = jsonObject.getString(propertyName);
                                    if (keyNickName != null) {
                                        item.setName(keyNickName);
                                    }
                                }

                                item.setNickName(object1.getJSONObject("params").getString("deviceNickName"));
                                item.setType(1);
                                item.setValueName(object1.getJSONObject("params").getString("localizedCompareValueName"));

                                property.setPropertyName(object1.getJSONObject("params").getString("propertyName"));
                                property.setIotId(item.getIotId());
                                property.setPropertyValue(object1.getJSONObject("params").get("propertyValue"));

                                String propertyValue = object1.getJSONObject("params").get("propertyValue").toString();
                                String localizedCompareValueName = object1.getJSONObject("params").get("localizedCompareValueName").toString();
                                if (propertyValue != null && propertyValue.equals(localizedCompareValueName)) {
                                    item.setDesc(item.getName() + getString(R.string.equal_to) + propertyValue);
                                }

                                item.setObject(property);
                                mActionList.add(property);
                                mIdentifierList.add(item);
                            } else if (Constant.SCENE_ACTION_SERVICE.equals(uri)) {
                                ActionEntry.InvokeService service = new ActionEntry.InvokeService();

                                IdentifierItemForCA item = new IdentifierItemForCA();
                                item.setIotId(object1.getJSONObject("params").getString("iotId"));
                                item.setName(object1.getJSONObject("params").getString("localizedServiceName"));
                                item.setNickName(object1.getJSONObject("params").getString("deviceNickName"));
                                item.setType(2);
                                //item.setValueName(object1.getJSONObject("params").getString("localizedCompareValueName"));

                                Map<String, Object> map = new HashMap<>();
                                for (Map.Entry<String, Object> map1 : object1.getJSONObject("params").getJSONObject("serviceArgs").entrySet()) {
                                    map.put(map1.getKey(), map1.getValue());
                                }

                                service.setServiceName(object1.getJSONObject("params").getString("serviceName"));
                                service.setIotId(item.getIotId());
                                service.setServiceArgs(map);

                                item.setDesc(item.getName());

                                item.setObject(service);
                                mActionList.add(service);
                                mIdentifierList.add(item);
                            }
                        }
                    }
                    ViseLog.d(new Gson().toJson(mActionList));
                    mActionAdapter.notifyDataSetChanged();
                    if (mActionList == null || mActionList.size() == 0)
                        mAddActionLayout.setVisibility(View.VISIBLE);
                    else mAddActionLayout.setVisibility(View.GONE);
                    break;
                }
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
        if (mSceneName != null && mSceneName.length() > 0) {
            String name = mSceneNameTV.getText().toString();
            nameEt.setText(name);
            nameEt.setSelection(name.length());
        } else nameEt.setHint(getString(R.string.pls_input_scene_name));
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
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    mSceneNameTV.setText(nameEt.getText().toString());
                    mSceneName = nameStr;
                } else {
                    ToastUtils.showLongToast(NewSceneActivity.this, R.string.pls_input_scene_name);
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // EventBus.getDefault().unregister(this);
    }
}