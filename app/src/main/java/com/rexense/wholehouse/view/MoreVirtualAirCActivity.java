package com.rexense.wholehouse.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
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
import com.rexense.wholehouse.databinding.ActivityMoreVirtualAirCBinding;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.sdk.APIChannel;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.widget.DialogUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoreVirtualAirCActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMoreVirtualAirCBinding mViewBinding;

    private static final String DEVICES_NICK_NAMES = "virtual_device_nick_names";
    private static final String IOT_ID = "iot_id";
    private static final String END_POINT = "end_point";
    private static final String NICKNAMES = "nicknames";

    private String mIotId;
    private String mEndPoint;
    private String mNicknames;

    private PopupWindow mNamePopWindow;
    private RelativeLayout mPopRootView;
    private RecyclerView mRoomRV;

    public static void start(Activity activity, String iotId, String endPoint, String nicknames, int requestCode) {
        Intent intent = new Intent(activity, MoreVirtualAirCActivity.class);
        intent.putExtra(IOT_ID, iotId);
        intent.putExtra(END_POINT, endPoint);
        intent.putExtra(NICKNAMES, nicknames);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMoreVirtualAirCBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initData();
        initStatusBar();
        initView();
    }

    private void initView() {
        mViewBinding.nickNameTv.setText(mViewBinding.topBar.includeDetailLblTitle.getText().toString());
        mViewBinding.nickNameTv.setOnClickListener(this);
        mViewBinding.nickNameGo.setOnClickListener(this);
        mViewBinding.unbindBgIv.setOnClickListener(this);
    }

    private void initData() {
        mIotId = getIntent().getStringExtra(IOT_ID);
        mEndPoint = getIntent().getStringExtra(END_POINT);
        mNicknames = getIntent().getStringExtra(NICKNAMES);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        mViewBinding.topBar.includeDetailRl.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mViewBinding.topBar.includeDetailImgBack.setOnClickListener(this);
        mViewBinding.topBar.includeDetailImgMore.setVisibility(View.GONE);
        String[] names = mNicknames.split(",");
        mViewBinding.topBar.includeDetailLblTitle.setText(names[Integer.parseInt(mEndPoint) - 1]);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.topBar.includeDetailImgBack.getId()) {
            finish();
        } else if (v.getId() == mViewBinding.nickNameTv.getId() ||
                v.getId() == mViewBinding.nickNameGo.getId()) {
            showRoomListView();
            mViewBinding.rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                    ViseLog.d("mViewBinding.rootLayout.getBottom() = " + mViewBinding.rootLayout.getBottom() +
                            "\nr.bottom = " + r.bottom +
                            "\nheight = " + QMUIDisplayHelper.getScreenHeight(MoreVirtualAirCActivity.this));
                    if (r.bottom != QMUIDisplayHelper.getScreenHeight(MoreVirtualAirCActivity.this)) {
                        mRoomRV.setVisibility(View.VISIBLE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPopRootView.getLayoutParams();
                        params.height = QMUIDisplayHelper.getScreenHeight(MoreVirtualAirCActivity.this) -
                                (QMUIDisplayHelper.getScreenHeight(MoreVirtualAirCActivity.this) - r.bottom +
                                        QMUIDisplayHelper.getStatusBarHeight(MoreVirtualAirCActivity.this));
                        mPopRootView.setLayoutParams(params);
                    } else {
                        mRoomRV.setVisibility(View.GONE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPopRootView.getLayoutParams();
                        params.height = QMUIDisplayHelper.getScreenHeight(MoreVirtualAirCActivity.this);
                        mPopRootView.setLayoutParams(params);
                    }
                }
            });
        } else if (v.getId() == mViewBinding.unbindBgIv.getId()) {
            DialogUtils.showConfirmDialog(this, getString(R.string.dialog_title),
                    String.format(getString(R.string.do_you_want_to_delete_virtual_air), mViewBinding.topBar.includeDetailLblTitle.getText().toString()),
                    getString(R.string.dialog_confirm), getString(R.string.dialog_cancel),
                    new DialogUtils.Callback() {
                        @Override
                        public void positive() {
                            QMUITipDialogUtil.showLoadingDialg(MoreVirtualAirCActivity.this, R.string.is_loading);
                            updateNickname(" ");
                        }

                        @Override
                        public void negative() {

                        }
                    });
        }
    }

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

    private void showRoomListView() {
        if (mNamePopWindow == null) {
            View roomListView = View.inflate(this, R.layout.pop_aircondition_name, null);
            TextView dialogTitleTV = roomListView.findViewById(R.id.dialogEditLblTitle);
            dialogTitleTV.setText(R.string.update_name);
            LinearLayout editLayout = roomListView.findViewById(R.id.edit_layout);
            mPopRootView = roomListView.findViewById(R.id.root_layout);
            mRoomRV = roomListView.findViewById(R.id.room_rv);
            EditText nameET = roomListView.findViewById(R.id.dialogEditTxtEditItem);
            nameET.setText(mViewBinding.topBar.includeDetailLblTitle.getText().toString());
            nameET.setFilters(new InputFilter[]{mInputFilter, mLengthFilter});

            TextView cancelTV = roomListView.findViewById(R.id.dialogEditLblCancel);
            TextView okTV = roomListView.findViewById(R.id.dialogEditLblConfirm);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameET.setText(mViewBinding.topBar.includeDetailLblTitle.getText().toString());
                    mRoomRV.setVisibility(View.GONE);
                    mNamePopWindow.dismiss();
                }
            });
            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNamePopWindow.dismiss();
                    QMUITipDialogUtil.showLoadingDialg(MoreVirtualAirCActivity.this, R.string.is_submitted);
                    updateNickname(nameET.getText().toString());
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

    private void updateNickname(String nickname) {
        ViseLog.d("修改前 " + mNicknames);
        StringBuilder sb = new StringBuilder();
        String[] names = mNicknames.split(",");
        for (int i = 0; i < names.length; i++) {
            if (String.valueOf(i + 1).equals(mEndPoint)) {
                names[i] = nickname;
            }
            if (i == 0) {
                sb.append(names[i]);
            } else {
                sb.append("," + names[i]);
            }
        }
        ViseLog.d("修改后 " + sb.toString());
        SceneManager.setExtendedProperty(this, mIotId, DEVICES_NICK_NAMES, sb.toString(), new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(MoreVirtualAirCActivity.this, failEntry);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                responseError(MoreVirtualAirCActivity.this, errorEntry);
            }

            @Override
            public void onProcessData(String result) {
                QMUITipDialogUtil.dismiss();
                if (nickname.trim().length() > 0) {
                    mViewBinding.topBar.includeDetailLblTitle.setText(nickname);
                    mViewBinding.nickNameTv.setText(nickname);

                    Intent intent = new Intent(Constant.ACTION_UPDATE_VIRTUAL_AIRC_NICKNAME);
                    intent.putExtra(DEVICES_NICK_NAMES, sb.toString());
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(DEVICES_NICK_NAMES, sb.toString());
                    setResult(Constant.RESULTCODE_CALLDELAIRCONDITIONER, intent);
                    finish();
                }
            }
        });
    }
}