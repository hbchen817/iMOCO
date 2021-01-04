package com.rexense.imoco.view;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.datepicker.CustomDatePicker;
import com.rexense.imoco.datepicker.DateFormatUtils;
import com.rexense.imoco.datepicker.PickerView;
import com.rexense.imoco.event.RefreshHistoryEvent;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.model.ItemHistoryMsg;
import com.rexense.imoco.model.ItemUser;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.LockManager;
import com.rexense.imoco.presenter.RealtimeDataReceiver;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.utility.SrlUtils;
import com.rexense.imoco.utility.TimeUtils;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.viewholder.CommonAdapter;
import com.rexense.imoco.widget.DialogUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Gary
 * @time 2020/10/13 11:12
 */

public class LockDetailActivity extends DetailActivity {

    private static final String[] TYPE_OPEN = new String[]{"DoorOpenNotification", "RemoteUnlockNotification"};

    @BindView(R.id.includeDetailImgSetting)
    ImageView includeDetailImgSetting;
    @BindView(R.id.electricity_value)
    TextView mElectricityValue;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.mRemoteOpenView)
    View mRemoteOpenView;
    @BindView(R.id.no_record_hint)
    TextView mNoRecordHint;
    @BindView(R.id.icon_remote_open)
    TextView mIconRemoteOpen;
    @BindView(R.id.icon_user_manager)
    TextView mIconUserManager;
    @BindView(R.id.icon_temporary_password)
    TextView mIconTemporaryPassword;
    @BindView(R.id.icon_key_manager)
    TextView mIconKeyManager;
    @BindView(R.id.icon_lock)
    TextView mIconLock;

    private CustomDatePicker mStartTimerPicker;
    private String mTemporaryKey;
    private long mKeyTime;
    private LockHandler mHandler;
    private ItemUser mSelectedUser;
    private List<ItemUser> mUserList = new ArrayList<>();
    private UnbindKey mCurrentUnBindUser;
    private List<Visitable> mHistoryList = new ArrayList<>();
    private CommonAdapter mAdapter;
    private int mPageNo = 1;

    private String[] mLockStates;
    private PickerView mPicker;
    private boolean mRefreshPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mHandler = new LockHandler(this);
        mLockStates = getResources().getStringArray(R.array.smart_lock_state_a7);
        RealtimeDataReceiver.addEventCallbackHandler("LockEventCallback", mHandler);
        includeDetailImgSetting.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new CommonAdapter(mHistoryList, this);
        recycleView.setAdapter(mAdapter);
        if (mOwned == 1) {
//            mRemoteOpenView.setVisibility(View.GONE);
            getUserList();
        }
        getOpenRecord();

        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.topic_color2));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void refresh(RefreshHistoryEvent event) {
        mHistoryList.clear();
        LockManager.getLockHistory(mIOTId, 0, System.currentTimeMillis(), TYPE_OPEN, 1, 10, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    @Subscribe
    public void refreshUsers(UserManagerActivity.RefreshUserEvent event) {
        if (mPicker != null) {
            mRefreshPicker = true;
            mUserList.clear();
            getUserList();
        }
    }

    private void getOpenRecord() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mNoRecordHint.setTypeface(iconfont);
        mIconRemoteOpen.setTypeface(iconfont);
        mIconUserManager.setTypeface(iconfont);
        mIconTemporaryPassword.setTypeface(iconfont);
        mIconKeyManager.setTypeface(iconfont);
        mIconLock.setTypeface(iconfont);
        LockManager.getLockHistory(mIOTId, 0, System.currentTimeMillis(), TYPE_OPEN, 1, 10, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private void initTimerPicker() {
        String beginTime = TimeUtils.getDatePickerNowTime();
        String endTime = TimeUtils.getDatePickerEndTime();

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mStartTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                String randomKey = getRandomKey();
                mTemporaryKey = randomKey;
                mKeyTime = timestamp;
                String startTime = DateFormatUtils.long2Str(timestamp, true);
                String endTime = DateFormatUtils.long2Str(timestamp + 1000 * 60 * 5, true);
                ViseLog.d("mIOTId ========= " + mIOTId);
                if (mProductKey.equals(CTSL.PK_SMART_LOCK_A7)) {
                    LockManager.setTemporaryKey(mIOTId, randomKey, startTime, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                } else {
                    LockManager.setTemporaryKey(mIOTId, randomKey, startTime, endTime, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                }
            }
        }, beginTime, endTime, true);
        // 允许点击屏幕或物理返回键关闭
        mStartTimerPicker.setCancelable(true);
        // 显示时和分
        mStartTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mStartTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mStartTimerPicker.setCanShowAnim(true);
    }

    /**
     * 获取虚拟用户列表
     */
    private void getUserList() {
        UserCenter.queryVirtualUserListInAccount(mPageNo, 20, mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    /**
     * 随机一个6位密码
     *
     * @return 密码
     */
    private String getRandomKey() {
        String source = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(source.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }

    /**
     * 临时密码DiaLog
     *
     * @param key
     * @param start
     */
    private void showTemporaryKeyDialog(String key, long start) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_temporary_key, null);
        builder.setView(view);
        builder.setCancelable(true);

        TextView completeBtn = view.findViewById(R.id.mCompleteBtn);
        TextView temporaryKeyText = view.findViewById(R.id.mTemporaryKeyText);
        TextView timeHintText = view.findViewById(R.id.mTimeHintText);
        TextView copyBtn = view.findViewById(R.id.mCopyBtn);

        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_color);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.height = getResources().getDimensionPixelOffset(R.dimen.dp_230);
        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(params);//这行要放在dialog.show()之后才有效

        temporaryKeyText.setText(key);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.temporary_key_time_format));
        String format = simpleDateFormat.format(new Date(start));
        timeHintText.setText(format);
        completeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        copyBtn.setOnClickListener(v1 -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", key);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(mActivity, "复制成功", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 绑定钥匙Dialog
     *
     * @param name
     */
    private void showBindKeyDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_key_bind_user, null);
        builder.setView(view);
        builder.setCancelable(true);

        TextView key_name_text = view.findViewById(R.id.key_name_text);
        TextView btn_sure = view.findViewById(R.id.btn_sure);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        LinearLayout dialogOneView = view.findViewById(R.id.dialog_one);
        LinearLayout dialogTwoView = view.findViewById(R.id.dialog_two);
        TextView btn_sure_two = view.findViewById(R.id.btn_sure_two);
        TextView btn_cancel_two = view.findViewById(R.id.btn_cancel_two);
        LinearLayout belongView = view.findViewById(R.id.belong_view);
        TextView belongUserName = view.findViewById(R.id.user_name);
        if (mSelectedUser != null)
            belongUserName.setText(mSelectedUser.getName());
        mPicker = view.findViewById(R.id.picker);

        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_color);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //params.height = getResources().getDimensionPixelOffset(R.dimen.dp_100);
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_280);
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);//这行要放在dialog.show()之后才有效


        key_name_text.setText(name);
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btn_sure.setOnClickListener(v1 -> {
            if (mSelectedUser != null && mCurrentUnBindUser != null) {
                LockManager.bindUserKey(mSelectedUser.getID(), mCurrentUnBindUser.keyId, mCurrentUnBindUser.keyType, mCurrentUnBindUser.keyPermission, mIOTId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                dialog.dismiss();
            } else if (mSelectedUser == null) {
                ToastUtils.showLongToast(LockDetailActivity.this, R.string.pls_select_user_first);
            } else if (mCurrentUnBindUser == null) {
                ToastUtils.showLongToast(LockDetailActivity.this, R.string.pls_add_key_first);
            }
        });
        belongView.setOnClickListener(v2 -> {
            dialogOneView.setVisibility(View.GONE);
            dialogTwoView.setVisibility(View.VISIBLE);
        });
        btn_cancel_two.setOnClickListener(v2 -> {
            dialogOneView.setVisibility(View.VISIBLE);
            dialogTwoView.setVisibility(View.GONE);
        });
        btn_sure_two.setOnClickListener(v3 -> {
            if (mPicker.getSelectedIndex() == 0) {
                CreateUserActivity.start(this);
            } else {
                if (mSelectedUser == null) mSelectedUser = mUserList.get(0);
                belongUserName.setText(mSelectedUser.getName());
                dialogOneView.setVisibility(View.VISIBLE);
                dialogTwoView.setVisibility(View.GONE);
            }
        });
        List<String> data = new ArrayList<>();
        data.add("创建新用户");//这里加入了一个默认的新建选项 index要-1
        for (int i = 0; i < mUserList.size(); i++) {
            data.add(mUserList.get(i).getName());
        }
        mPicker.setDataList(data);
        mPicker.setCanScrollLoop(false);
        mPicker.setSelected(1);
        // mSelectedUser = mUserList.get(0);
        mPicker.setOnSelectListener(new PickerView.OnSelectListener() {
            @Override
            public void onSelect(View view, String selected) {
                if (mPicker.getSelectedIndex() > 0) {
                    mSelectedUser = mUserList.get(mPicker.getSelectedIndex() - 1);
                }
            }
        });
    }

    @OnClick({R.id.includeDetailImgBack, R.id.all_record_btn, R.id.includeDetailImgSetting, R.id.mUserManagerView, R.id.mShortTimePasswordView, R.id.mKeyManagerView, R.id.mRemoteOpenView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.includeDetailImgBack:
                finish();
                break;
            case R.id.includeDetailImgSetting:
                if (mOwned == 1) {
                    if (mCurrentUnBindUser == null)
                        showBindKeyDialog(getString(R.string.no_key));
                    else {

                        StringBuffer name = new StringBuffer();
                        switch (mCurrentUnBindUser.keyType) {//开锁方式
                            /*case "5"://临时钥匙
                                return;*/
                            case 1:
                                name.append("指纹钥匙");
                                break;
                            case 2:
                                name.append("密码钥匙");
                                break;
                            case 3:
                                name.append("卡钥匙");
                                break;
                            case 4:
                                name.append("机械钥匙");
                                break;
                            default://其他钥匙 指纹 密码 卡 机械钥匙
//                                    LockManager.filterUnbindKey(activity.mIOTId, value.getString("KeyID"), value.getIntValue("LockType"), value.getIntValue("UserLimit"), activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                                break;
                        }
                        showBindKeyDialog(name.append(mCurrentUnBindUser.keyId).toString());
                        // showBindKeyDialog("指纹钥匙1");
                    }
                }
                break;
            case R.id.mUserManagerView:
                if (mOwned == 1) {
                    UserManagerActivity.start(this, mIOTId);
                } else {
                    DialogUtils.showMsgDialog(this, "被分享用户暂无此权限");
                }
                break;
            case R.id.mShortTimePasswordView:
                mStartTimerPicker = null;
                initTimerPicker();
                mStartTimerPicker.show(System.currentTimeMillis());
                break;
            case R.id.mKeyManagerView:
                if (mOwned == 1) {
                    KeyManagerActivity.start(this, mIOTId);
                } else {
                    DialogUtils.showMsgDialog(this, "被分享用户暂无此权限");
                }
                break;
            case R.id.all_record_btn:
                HistoryActivity.start(this, mIOTId);
                break;
            case R.id.mRemoteOpenView:
                DialogUtils.showConfirmDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LockManager.remoteOpen(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mHandler);
                    }
                }, "您确定要远程开门吗？", "远程开门确认");
                break;
            default:
                break;
        }
    }

    private static class LockHandler extends Handler {
        final WeakReference<LockDetailActivity> mWeakReference;

        public LockHandler(LockDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            LockDetailActivity activity = mWeakReference.get();
            switch (msg.what) {
                case Constant.MSG_CALLBACK_LNEVENTNOTIFY:
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONObject value = jsonObject.getJSONObject("value");
                    String identifier = jsonObject.getString("identifier");
                    if (identifier == null) {
                        break;
                    }
                    switch (identifier) {
                        case "KeyAddedNotification"://添加钥匙
                            EventBus.getDefault().post(new RefreshHistoryEvent());
                            StringBuffer name = new StringBuffer();

                            switch (value.getString("LockType")) {//开锁方式
                                case "5"://临时钥匙
                                    if (value.getIntValue("Status") == 0) {
                                        activity.showTemporaryKeyDialog(activity.mTemporaryKey, activity.mKeyTime);
                                    } else {
                                        Toast.makeText(activity, "创建临时密码失败", Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                case "1":
                                    name.append("指纹钥匙");
                                    break;
                                case "2":
                                    name.append("密码钥匙");
                                    break;
                                case "3":
                                    name.append("卡钥匙");
                                    break;
                                case "4":
                                    name.append("机械钥匙");
                                    break;
                                default://其他钥匙 指纹 密码 卡 机械钥匙
//                                    LockManager.filterUnbindKey(activity.mIOTId, value.getString("KeyID"), value.getIntValue("LockType"), value.getIntValue("UserLimit"), activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                                    break;
                            }
                            activity.mCurrentUnBindUser = new UnbindKey();
                            activity.mCurrentUnBindUser.keyId = value.getString("KeyID");
                            activity.mCurrentUnBindUser.keyType = value.getIntValue("LockType");
                            activity.mCurrentUnBindUser.keyPermission = value.getIntValue("UserLimit");
                            if (activity.mOwned == 1) {
                                activity.showBindKeyDialog(name.append(value.getString("KeyID")).toString());
                            }
                            break;
                        case "HijackingAlarm":
                        case "TamperAlarm":
                        case "DoorUnlockedAlarm":
                        case "ArmDoorOpenAlarm":
                        case "LockedAlarm":
                        case "DoorOpenNotification":
                        case "KeyDeletedNotification":
                        case "LowElectricityAlarm":
                        case "ReportReset":
                            EventBus.getDefault().post(new RefreshHistoryEvent());
                            break;
                        case "RemoteUnlockNotification":
                            EventBus.getDefault().post(new RefreshHistoryEvent());
                            ToastUtils.showToastCentrally(activity, "远程开门" + (value.getIntValue("Status") == 0 ? "成功" : "失败"));
                            break;
                        default:
                            break;
                    }
                    if (identifier.equalsIgnoreCase("KeyAddedNotification") &&
                            value.getString("LockType").equalsIgnoreCase("5")) {
                        if (value.getIntValue("Status") == 0) {
                            activity.showTemporaryKeyDialog(activity.mTemporaryKey, activity.mKeyTime);
                        } else {
                            Toast.makeText(activity, "创建临时密码失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_FILTER_UNBIND_KEY:
                    JSONArray array = JSON.parseArray((String) msg.obj);
                    JSONObject data = array.getJSONObject(0);
                    String iotId = data.getString("iotId");
                    if (iotId.equalsIgnoreCase(activity.mIOTId)) {
                        int lockUserPermType = data.getIntValue("lockUserPermType");//1 普通 2 管理 3 胁迫
                        if (lockUserPermType == 0) {//未绑定
                            StringBuffer name = new StringBuffer();
                            switch (data.getIntValue("lockUserType")) {
                                case 1:
                                    name.append("指纹钥匙");
                                    break;
                                case 2:
                                    name.append("密码钥匙");
                                    break;
                                case 3:
                                    name.append("卡钥匙");
                                    break;
                                case 4:
                                    name.append("机械钥匙");
                                    break;
                                default:
                                    break;
                            }
                            activity.mCurrentUnBindUser = new UnbindKey();
                            activity.mCurrentUnBindUser.keyId = data.getString("lockUserId");
                            activity.mCurrentUnBindUser.keyType = data.getIntValue("lockUserType");
                            activity.mCurrentUnBindUser.keyPermission = data.getIntValue("lockUserPermType");
                            activity.showBindKeyDialog(name.append(data.getString("lockUserId")).toString());
                        }
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERY_USER_IN_ACCOUNT:
                    JSONObject result = JSON.parseObject((String) msg.obj);
                    long total = result.getLongValue("total");
                    int pageNo = result.getIntValue("pageNo");
                    int pageSize = result.getIntValue("pageSize");
                    JSONArray users = result.getJSONArray("data");
                    int size = users.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject user = users.getJSONObject(i);
                        ItemUser itemUser = new ItemUser();
                        itemUser.setID(user.getString("userId"));
                        JSONArray attrList = user.getJSONArray("attrList");
                        itemUser.setName(attrList.getJSONObject(0).getString("attrValue"));
                        activity.mUserList.add(itemUser);
                    }
                    if (pageSize * pageNo < total) {
                        pageNo++;
                        UserCenter.queryVirtualUserListInAccount(pageNo, pageSize, activity.mCommitFailureHandler, activity.mResponseErrorHandler, this);
                    } else if (activity.mRefreshPicker) {
                        List<String> pickList = new ArrayList<>();
                        pickList.add("创建新用户");//这里加入了一个默认的新建选项 index要-1
                        for (int i = 0; i < activity.mUserList.size(); i++) {
                            pickList.add(activity.mUserList.get(i).getName());
                        }
                        activity.mPicker.setDataList(pickList);
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERY_USER_IN_DEVICE:
                    JSONArray userArray = JSON.parseArray((String) msg.obj);
                    int size1 = userArray.size();
                    for (int i = 0; i < size1; i++) {
                        JSONObject user = userArray.getJSONObject(i);
                        ItemUser itemUser = new ItemUser();
                        itemUser.setID(user.getString("userId"));
                        JSONArray attrList = user.getJSONArray("attrList");
                        for (int j = 0; j < attrList.size(); j++) {
                            JSONObject attr = attrList.getJSONObject(i);
                            if (attr.getString("attrKey").equalsIgnoreCase("name")) {
                                itemUser.setName(attr.getString("attrValue"));
                                break;
                            }
                        }
                        activity.mUserList.add(itemUser);
                    }
                    break;
                case Constant.MSG_CALLBACK_QUERY_HISTORY:
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        JSONObject js = JSON.parseObject((String) msg.obj);
                        JSONArray historyArray = js.getJSONArray("data");
                        int historySize = historyArray.size();
                        for (int i = 0; i < historySize; i++) {
                            JSONObject jo = historyArray.getJSONObject(i);
                            if (jo.getIntValue("KeyID") == 103) {
                                continue;
                            }
                            ItemHistoryMsg item = new ItemHistoryMsg();
                            item.setTime(jo.getString("client_date"));
                            item.setEvent_code(jo.getString("event_code"));
                            item.setKeyID(jo.getString("KeyID"));
                            item.setLockType(jo.getIntValue("LockType"));
                            activity.mHistoryList.add(item);
                        }
                        if (activity.mHistoryList.size() > 0) {
                            activity.mNoRecordHint.setVisibility(View.GONE);
                        }
                        activity.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constant.MSG_CALLBACK_KEY_USER_BIND:
                    break;
                default:
                    break;
            }
        }
    }

    public static class UnbindKey {
        public String keyId;
        public int keyType;
        public int keyPermission;
    }

    @Override
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        String s = new Gson().toJson(propertyEntry);
        ViseLog.d("updateState:\n" + s);
        if (!super.updateState(propertyEntry)) {
            return false;
        }

        ViseLog.d("propertyEntry.getPropertyValue(CTSL.SL_lockstate) = " + propertyEntry.getPropertyValue(CTSL.SL_lockstate));

        if (propertyEntry.getPropertyValue(CTSL.SL_batterypercentage) != null && propertyEntry.getPropertyValue(CTSL.SL_batterypercentage).length() > 0) {
            mElectricityValue.setText(String.valueOf(propertyEntry.getPropertyValue(CTSL.SL_batterypercentage)) + "%");
        }
        if (propertyEntry.getPropertyValue(CTSL.SL_lockstate) != null && propertyEntry.getPropertyValue(CTSL.SL_lockstate).length() > 0) {
            int lockState = Integer.parseInt(propertyEntry.getPropertyValue(CTSL.SL_lockstate));
            ViseLog.d("lockState = " + lockState);
            /*if (lockState >= 0 && lockState <= 3)
                mIconLock.setText(mLockStates[lockState]);
            else mIconLock.setText(getString(R.string.unknown_state));*/
            if (lockState == 0) mIconLock.setText(getString(R.string.icon_lock));
            else if (lockState == 1) mIconLock.setText(getString(R.string.icon_unlock));
        }
        return true;
    }
}