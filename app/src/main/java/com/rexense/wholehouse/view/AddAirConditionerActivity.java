package com.rexense.wholehouse.view;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityAddAirConditionerBinding;
import com.rexense.wholehouse.model.AirConditionerConverter;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.TSLHelper;
import com.rexense.wholehouse.sdk.APIChannel;
import com.rexense.wholehouse.utility.GsonUtil;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAirConditionerActivity extends BaseActivity implements View.OnClickListener {
    private static final String DEVICES_NICK_NAME = "virtual_device_nick_names";

    private static final String IOT_ID = "iot_id";
    private static final String PRODUCT_KEY = "product_key";
    private static final String VIRTUAL_INDEX = "virtual_index";

    private ActivityAddAirConditionerBinding mViewBinding;

    private Typeface mIconFont;

    private int mEndPoint = 1;
    private TSLHelper mTSLHelper;
    private String mIOTId;
    private String mProductKey;
    private String mVirtualIndex;
    private String[] mVirtualIndexs = new String[16];

    private int mPowerSwitchStatus = CTSL.STATUS_ON;
    private String mAirCName;

    public static void start(Activity activity, String iotId, String productKey, String virtualIndex, int requestCode) {
        Intent intent = new Intent(activity, AddAirConditionerActivity.class);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(PRODUCT_KEY, productKey);
        intent.putExtra(VIRTUAL_INDEX, virtualIndex);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAddAirConditionerBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        initData();
    }

    private void initView() {
        mIconFont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.switchBtn.setTypeface(mIconFont);
        mViewBinding.switchBtn.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
        mViewBinding.previousIc.setTypeface(mIconFont);
        mViewBinding.previousIc.setOnClickListener(this);
        mViewBinding.nextIc.setTypeface(mIconFont);
        mViewBinding.nextIc.setOnClickListener(this);
        mViewBinding.switchBtn.setOnClickListener(this);
        mViewBinding.noTv.setOnClickListener(this);
        mViewBinding.yesTv.setOnClickListener(this);
    }

    private void initData() {

        mTSLHelper = new TSLHelper(this);
        mIOTId = getIntent().getStringExtra(IOT_ID);
        mProductKey = getIntent().getStringExtra(PRODUCT_KEY);
        mVirtualIndex = getIntent().getStringExtra(VIRTUAL_INDEX);
        if (mVirtualIndex == null || mVirtualIndex.length() == 0) {
            for (int i = 0; i < 16; i++) {
                mVirtualIndexs[i] = " ";
            }
        } else {
            String[] indexs = mVirtualIndex.split(",");
            ViseLog.d(indexs);
            System.arraycopy(indexs, 0, mVirtualIndexs, 0, 16);
        }
        mEndPoint = 1;
        for (int i = 0; i < 16; i++) {
            if (mVirtualIndexs[i] == null || mVirtualIndexs[i].trim().length() == 0) {
                mEndPoint = i + 1;
                break;
            }
        }
        ViseLog.d("mVirtualIndex = " + mVirtualIndex + " , mEndPoint = " + mEndPoint);
        mViewBinding.devNumTv.setText(String.format(getString(R.string.trying_aircondition_control), mEndPoint));
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        mViewBinding.topBar.includeDetailRl.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mViewBinding.topBar.includeDetailImgMore.setVisibility(View.GONE);
        mViewBinding.topBar.includeDetailLblTitle.setText(R.string.add_airconditioner);
        mViewBinding.topBar.includeDetailImgBack.setOnClickListener(this);
    }

    private void subForIndex(int index) {
        index = index - 2;
        ViseLog.d("index = " + index);
        if (index >= 0 && index < mVirtualIndexs.length) {
            ViseLog.d("mVirtualIndexs[index] = " + mVirtualIndexs[index]);
            if (mVirtualIndexs[index] == null || mVirtualIndexs[index].trim().length() == 0) {
                if (index == 0) mEndPoint = 1;
                else mEndPoint = index + 1;
            } else {
                subForIndex(index + 1);
            }
        }
    }

    private void addForIndex(int index) {
        ViseLog.d("index = " + index);
        if (index >= 0 && index < mVirtualIndexs.length) {
            ViseLog.d("mVirtualIndexs[index] = " + mVirtualIndexs[index]);
            if (mVirtualIndexs[index] == null || mVirtualIndexs[index].trim().length() == 0) {
                mEndPoint = index + 1;
            } else {
                addForIndex(index + 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.topBar.includeDetailImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.previousIc.getId()) {
            int pos = mEndPoint;
            subForIndex(pos);
            mViewBinding.devNumTv.setText(String.format(getString(R.string.trying_aircondition_control), mEndPoint));
            mViewBinding.switchBtn.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mPowerSwitchStatus = CTSL.STATUS_ON;
        } else if (v.getId() == mViewBinding.nextIc.getId() ||
                v.getId() == mViewBinding.noTv.getId()) {
            int pos = mEndPoint;
            addForIndex(pos);
            mViewBinding.devNumTv.setText(String.format(getString(R.string.trying_aircondition_control), mEndPoint));
            mViewBinding.switchBtn.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
            mPowerSwitchStatus = CTSL.STATUS_ON;
        } else if (v.getId() == mViewBinding.switchBtn.getId()) {
            ViseLog.d("开关");
            if (mPowerSwitchStatus == CTSL.STATUS_ON) {
                mViewBinding.switchBtn.setTextColor(ContextCompat.getColor(this, R.color.all_8_2));
                mPowerSwitchStatus = CTSL.STATUS_OFF;
            } else {
                mViewBinding.switchBtn.setTextColor(ContextCompat.getColor(this, R.color.appcolor));
                mPowerSwitchStatus = CTSL.STATUS_ON;
            }
            mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.AIRC_Converter_PowerSwitch_ + mEndPoint}, new String[]{"" + mPowerSwitchStatus});
        } else if (v.getId() == mViewBinding.yesTv.getId()) {
            // 确认
            showRoomList();
            mViewBinding.rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                    ViseLog.d("mViewBinding.rootLayout.getBottom() = " + mViewBinding.rootLayout.getBottom() +
                            "\nr.bottom = " + r.bottom +
                            "\nheight = " + QMUIDisplayHelper.getScreenHeight(AddAirConditionerActivity.this));
                    if (r.bottom != QMUIDisplayHelper.getScreenHeight(AddAirConditionerActivity.this)) {
                        mRoomRV.setVisibility(View.VISIBLE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPopRootView.getLayoutParams();
                        params.height = QMUIDisplayHelper.getScreenHeight(AddAirConditionerActivity.this) -
                                (QMUIDisplayHelper.getScreenHeight(AddAirConditionerActivity.this) - r.bottom +
                                        QMUIDisplayHelper.getStatusBarHeight(AddAirConditionerActivity.this));
                        mPopRootView.setLayoutParams(params);
                    } else {
                        mRoomRV.setVisibility(View.GONE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPopRootView.getLayoutParams();
                        params.height = QMUIDisplayHelper.getScreenHeight(AddAirConditionerActivity.this);
                        mPopRootView.setLayoutParams(params);
                    }
                }
            });
        }
    }

    private RelativeLayout mPopRootView;
    private PopupWindow mNamePopWindow;
    private RecyclerView mRoomRV;

    private final InputFilter mInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String regEx = "[, ]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(source.toString());
            if (matcher.find() || source.equals(" ")) return "";
            return null;
        }
    };
    private final InputFilter.LengthFilter mLengthFilter = new InputFilter.LengthFilter(9);

    private void showRoomList() {
        if (mNamePopWindow == null) {
            View roomListView = View.inflate(this, R.layout.pop_aircondition_name, null);
            LinearLayout editLayout = roomListView.findViewById(R.id.edit_layout);
            mPopRootView = roomListView.findViewById(R.id.root_layout);
            mRoomRV = roomListView.findViewById(R.id.room_rv);
            EditText nameET = roomListView.findViewById(R.id.dialogEditTxtEditItem);
            nameET.setFilters(new InputFilter[]{mInputFilter, mLengthFilter});

            TextView cancelTV = roomListView.findViewById(R.id.dialogEditLblCancel);
            TextView okTV = roomListView.findViewById(R.id.dialogEditLblConfirm);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameET.setText("");
                    mRoomRV.setVisibility(View.GONE);
                    mNamePopWindow.dismiss();
                }
            });
            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNamePopWindow.dismiss();

                    mAirCName = nameET.getText().toString();

                    mVirtualIndexs[mEndPoint - 1] = mAirCName;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 16; i++) {
                        if (i == 0) {
                            sb.append(mVirtualIndexs[i]);
                        } else {
                            sb.append("," + mVirtualIndexs[i]);
                        }
                    }
                    ViseLog.d("sb.toString() = " + sb.toString());
                    setExtendedProperty(DEVICES_NICK_NAME, sb.toString());
                }
            });

            List<String> list = new ArrayList<>();
            list.add(getString(R.string.room_restaurant));
            list.add(getString(R.string.room_kitchen));
            list.add(getString(R.string.room_mainbedroom));
            list.add(getString(R.string.room_livingroom));
            list.add(getString(R.string.room_study));
            list.add(getString(R.string.room_2thbedroom));

            BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_room_grid, list) {
                @Override
                protected void convert(@NotNull BaseViewHolder holder, String s) {
                    holder.setText(R.id.room_name_tv, s);
                    QMUIRoundButton button = holder.getView(R.id.room_name_tv);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringBuilder stringBuilder = new StringBuilder();

                            stringBuilder.append(nameET.getText().toString());
                            stringBuilder.append(s);
                            nameET.setText(stringBuilder.toString());
                            nameET.setSelection(nameET.getText().toString().length());
                        }
                    });
                }
            };
            GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
            mRoomRV.setLayoutManager(layoutManager);
            mRoomRV.setAdapter(adapter);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) editLayout.getLayoutParams();
            params.width = QMUIDisplayHelper.getScreenWidth(this) * 4 / 5;
            editLayout.setLayoutParams(params);

            mNamePopWindow = new PopupWindow(roomListView, QMUIDisplayHelper.getScreenWidth(this), QMUIDisplayHelper.getScreenHeight(this));
            mNamePopWindow.setOutsideTouchable(true);
            mNamePopWindow.setFocusable(true);
            mNamePopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            mNamePopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        mNamePopWindow.showAtLocation(mViewBinding.rootLayout, Gravity.BOTTOM, 0, 0);
    }

    private void delExtendedProperty(String key, String value) {
        SceneManager.delExtendedProperty(this, mIOTId, key, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                ViseLog.d("onFailure = \n" + GsonUtil.toJson(failEntry));
                commitFailure(AddAirConditionerActivity.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                ViseLog.d("onResponseError = \n" + GsonUtil.toJson(errorEntry));
                if (errorEntry.code == 6741) {
                    setExtendedProperty(key, value);
                } else
                    responseError(AddAirConditionerActivity.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                setExtendedProperty(key, value);
            }
        });
    }

    private void setExtendedProperty(String key, String value) {
        ViseLog.d("mIOTId = " + mIOTId);
        SceneManager.setExtendedProperty(this, mIOTId, key, value, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(AddAirConditionerActivity.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(AddAirConditionerActivity.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                ViseLog.d("key = " + key + " , result = " + result);

                ViseLog.d("mTmpVirtualIndex = " + value);
                Intent intent = new Intent();
                intent.putExtra(DEVICES_NICK_NAME, value);
                setResult(Constant.RESULTCODE_CALLADDAIRCONDITIONER, intent);
                finish();
            }
        });
    }
}