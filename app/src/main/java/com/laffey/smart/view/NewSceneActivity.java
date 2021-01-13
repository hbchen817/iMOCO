package com.laffey.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.utility.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.vise.log.ViseLog;

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
    ImageView mSceneNameIV;
    @BindView(R.id.type_tv)
    TextView mSceneTypeTV;
    @BindView(R.id.type_go)
    ImageView mSceneTypeIV;
    @BindView(R.id.status_tv)
    TextView mSceneStatusTV;
    @BindView(R.id.status_go)
    ImageView mSceneStatusIV;
    @BindView(R.id.scene_mode_tv)
    TextView mSceneModeTV;
    @BindView(R.id.add_new_condition_iv)
    ImageView mAddConditionIV;
    @BindView(R.id.add_new_condition_tv)
    TextView mAddConditionTV;
    @BindView(R.id.add_new_action_iv)
    ImageView mAddActionIV;
    @BindView(R.id.add_new_action_tv)
    TextView mAddActionTV;

    private String[] mTypeArray;
    private String[] mStatusArray;
    private String[] mModeArray;

    private String mSceneName = "";// 场景名称
    private String mCatalogId = "1";// 0:手动场景 1:自动场景
    private boolean mEnable = true;// true:启用 false:停用
    private String mSceneMode = "any";// any:满足以下任一条件 all:满足以下所有条件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene);
        ButterKnife.bind(this);

        initStatusBar();
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
        mTitle.setText(getString(R.string.create_new_scene));
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        mTypeArray = getResources().getStringArray(R.array.scene_type);
        mStatusArray = getResources().getStringArray(R.array.scene_status);
        mModeArray = getResources().getStringArray(R.array.scene_catalog_id);
    }

    @OnClick({R.id.name_tv, R.id.name_go, R.id.type_tv, R.id.type_go, R.id.status_tv, R.id.status_go,
            R.id.scene_mode_tv, R.id.add_new_condition_iv, R.id.add_new_condition_tv, R.id.add_new_action_iv,
            R.id.add_new_action_tv,R.id.tv_toolbar_right})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:{
                ViseLog.d("保存");
                break;
            }
            case R.id.add_new_action_tv:
            case R.id.add_new_action_iv: {
                ViseLog.d("添加动作");
                break;
            }
            case R.id.add_new_condition_tv:
            case R.id.add_new_condition_iv: {
                Intent intent = new Intent(this, AddConditionActivity.class);
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

    // 显示场景名称修改对话框
    private void showSceneNameDialogEdit() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.dialogEditLblTitle);
        titleTv.setText(getString(R.string.scene_maintain_name_edit));
        final EditText nameEt = (EditText) view.findViewById(R.id.dialogEditTxtEditItem);
        nameEt.setText(mSceneNameTV.getText().toString());
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
}