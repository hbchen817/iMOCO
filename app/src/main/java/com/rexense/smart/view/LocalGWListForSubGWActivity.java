package com.rexense.smart.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityLocalGatewayListBinding;
import com.rexense.smart.model.EDevice;
import com.rexense.smart.presenter.DeviceBuffer;
import com.rexense.smart.utility.QMUITipDialogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocalGWListForSubGWActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLocalGatewayListBinding mViewBinding;

    private static final String QR_KEY = "qr_key";

    private final int SCENE_LIST_REQUEST_CODE = 10000;

    private final List<EDevice.deviceEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> mAdapter;

    private String mQrKey;

    public static void start(Context context, String qrKey) {
        Intent intent = new Intent(context, LocalGWListForSubGWActivity.class);
        intent.putExtra(QR_KEY, qrKey);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLocalGatewayListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initAdapter();
        initData();
    }

    private void initData() {
        mList.addAll(DeviceBuffer.getGatewayDevs("1"));
        mAdapter.notifyDataSetChanged();
        mQrKey = getIntent().getStringExtra(QR_KEY);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder>(R.layout.item_gateway_2, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, EDevice.deviceEntry entry) {
                if (mList.indexOf(entry) == 0) {
                    holder.setVisible(R.id.divider, false);
                } else holder.setVisible(R.id.divider, true);
                ImageView imageView = holder.getView(R.id.icon);
                Glide.with(LocalGWListForSubGWActivity.this).load(entry.image).into(imageView);
                holder.setText(R.id.name, entry.nickName)
                        .setText(R.id.mac, getString(R.string.moredevice_id_2) + entry.deviceName);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EDevice.deviceEntry dev = mList.get(position);
                // LocalSceneActivity.start(LocalGWListForSubGWActivity.this, dev.iotId, dev.mac, SCENE_LIST_REQUEST_CODE);
                // LocalSceneActivity.start(LocalGatewayListActivity.this, dev.iotId, "00-68-EB-C5-66-23", SCENE_LIST_REQUEST_CODE);
                AddSubGwActivity.start(LocalGWListForSubGWActivity.this, dev.iotId, mQrKey, Constant.REQUESTCODE_CALLADDSUBGWACTIVITY);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.gatewayRv.setLayoutManager(layoutManager);
        mViewBinding.gatewayRv.setAdapter(mAdapter);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.select_gateway_dev_first);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCENE_LIST_REQUEST_CODE) {
            if (resultCode == 10001) {
                QMUITipDialogUtil.showSuccessDialog(this, R.string.scenario_created_successfully);
            }
        }
    }
}