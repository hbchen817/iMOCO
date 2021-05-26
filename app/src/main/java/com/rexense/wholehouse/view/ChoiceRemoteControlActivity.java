package com.rexense.wholehouse.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jdsh.sdk.ir.model.MatchRemoteControl;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.utility.JDInterfaceImplUtil;
import com.rexense.wholehouse.utility.QMUITipDialogUtil;
import com.rexense.wholehouse.utility.ToastUtils;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceRemoteControlActivity extends AppCompatActivity {
    @BindView(R.id.iv_toolbar_left)
    ImageView mToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.previous_tv)
    TextView mPreviousIcon;
    @BindView(R.id.next_tv)
    TextView mNextIcon;
    @BindView(R.id.switch_tv)
    TextView mSwitchIcon;
    @BindView(R.id.model_tv)
    TextView mModelNumTV;
    @BindView(R.id.model_name)
    TextView mModelNameTV;

    private int mDevTid = -1;
    private int mBrandId = -1;

    private List<MatchRemoteControl> mControlList = new ArrayList<>();
    private int mCurrentPosition = 0;
    private MatchRemoteControl mCurrControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_remote_control);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mPreviousIcon.setTypeface(iconfont);
        mNextIcon.setTypeface(iconfont);
        mSwitchIcon.setTypeface(iconfont);

        initStatusBar();
        init();
    }

    private void init() {
        mDevTid = getIntent().getIntExtra("dev_id", -1);
        mBrandId = getIntent().getIntExtra("brand_id", -1);
        ViseLog.d("mDevTid = " + mDevTid + " , mBrandId = " + mBrandId);

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mControlList.clear();
                mControlList.addAll(JDInterfaceImplUtil.getInstance().getRemoteControls(mBrandId, mDevTid, 4, 0));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
                        QMUITipDialogUtil.dismiss();
                    }
                });
            }
        }).start();
    }

    private void refreshUI() {
        String s = String.format(getString(R.string.trying_remote_control_model), mCurrentPosition + 1, mControlList.size());
        mModelNumTV.setText(s);
        mCurrControl = mControlList.get(mCurrentPosition);
        if (mCurrControl.getRmodel() != null && mCurrControl.getRmodel().length() > 0)
            mModelNameTV.setText(mCurrControl.getName() + " - " + mCurrControl.getRmodel());
        else mModelNameTV.setText(mCurrControl.getName());
        if (mCurrentPosition == 0) mPreviousIcon.setVisibility(View.INVISIBLE);
        else mPreviousIcon.setVisibility(View.VISIBLE);
        if (mCurrentPosition == mControlList.size() - 1)
            mNextIcon.setVisibility(View.INVISIBLE);
        else mNextIcon.setVisibility(View.VISIBLE);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mToolbarTitle.setText(R.string.add_remote_control);
    }

    @OnClick({R.id.iv_toolbar_left, R.id.previous_tv, R.id.next_tv, R.id.no_tv, R.id.yes_tv})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left: {
                finish();
                break;
            }
            case R.id.previous_tv: {
                if (mCurrentPosition > 0) {
                    mCurrentPosition--;
                }
                refreshUI();
                break;
            }
            case R.id.next_tv: {
                if (mCurrentPosition < mControlList.size() - 1) {
                    mCurrentPosition++;
                }
                refreshUI();
                break;
            }
            case R.id.no_tv: {
                if (mCurrentPosition >= 0 && mCurrentPosition < mControlList.size() - 1) {
                    mCurrentPosition++;
                } else if (mCurrentPosition == mControlList.size() - 1) {
                    mCurrentPosition--;
                }
                refreshUI();
                break;
            }
            case R.id.yes_tv: {
                showControlNameDialogEdit();
                break;
            }
        }
    }

    // 显示遥控器名称修改对话框
    private void showControlNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.remote_control_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        String controlName = mModelNameTV.getText().toString();
        nameEt.setText(controlName);
        nameEt.setSelection(controlName.length());
        nameEt.setHint(getString(R.string.pls_input_name));
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
                } else {
                    ToastUtils.showLongToast(ChoiceRemoteControlActivity.this, R.string.pls_input_name);
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
}