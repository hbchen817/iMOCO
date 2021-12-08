package com.rexense.wholehouse.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityVirtualAirConditionersBinding;
import com.rexense.wholehouse.model.AirConditionerConverter;
import com.rexense.wholehouse.model.EAPIChannel;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.presenter.DeviceBuffer;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.sdk.APIChannel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VirtualAirConditionersActivity extends BaseActivity implements View.OnClickListener {
    private ActivityVirtualAirConditionersBinding mViewBinding;

    private final static String IOT_ID = "iot_id";
    private static final String DEVICES_NICK_NAMES = "virtual_device_nick_names";

    private String mIotId;
    private Typeface mIconfont;
    private String mVirtualAirConditionerImg;
    private final List<AirConditionerConverter.AirConditioner> mList = new ArrayList<>();
    private BaseQuickAdapter<AirConditionerConverter.AirConditioner, BaseViewHolder> mAdapter;

    public static void start(Activity activity, String iotId) {
        Intent intent = new Intent(activity, VirtualAirConditionersActivity.class);
        intent.putExtra(IOT_ID, iotId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityVirtualAirConditionersBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mIotId = getIntent().getStringExtra(IOT_ID);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mVirtualAirConditionerImg = DeviceBuffer.getDeviceInformation(mIotId).image;

        initStatusBar();
        initAdapter();
        getExtendedProperty();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this);
        mViewBinding.includeToolbar.tvToolbarTitle.setText(DeviceBuffer.getDeviceInformation(mIotId).nickName);
    }

    private void getExtendedProperty() {
        SceneManager.getExtendedProperty(this, mIotId, DEVICES_NICK_NAMES, new APIChannel.Callback() {
            @Override
            public void onFailure(EAPIChannel.commitFailEntry failEntry) {
                commitFailure(VirtualAirConditionersActivity.this, failEntry);
                mViewBinding.recyclerRl.finishRefresh(false);
            }

            @Override
            public void onResponseError(EAPIChannel.responseErrorEntry errorEntry) {
                if (errorEntry.code == 6741) {
                    // 6741: 无扩展信息
                    mViewBinding.devNodataView.setVisibility(View.VISIBLE);
                    mViewBinding.devRecycler.setVisibility(View.GONE);
                } else {
                    responseError(VirtualAirConditionersActivity.this, errorEntry);
                }
                mViewBinding.recyclerRl.finishRefresh(false);
            }

            @Override
            public void onProcessData(String result) {
                mViewBinding.recyclerRl.finishRefresh(true);
                ViseLog.d("result = " + result);
                mList.clear();
                String[] names = result.split(",");
                for (int i = 0; i < names.length; i++) {
                    if (names[i] != null && names[i].trim().length() > 0) {
                        AirConditionerConverter.AirConditioner conditioner = new AirConditionerConverter.AirConditioner();
                        conditioner.setNickname(names[i]);
                        conditioner.setEndPoint(String.valueOf(i + 1));
                        mList.add(conditioner);
                        DeviceBuffer.addAirConditioner(mIotId + "_" + (i + 1), conditioner);
                    }
                }
                ViseLog.d("mList.size() = " + mList.size());
                if (mList.size() == 0) {
                    mViewBinding.devNodataView.setVisibility(View.VISIBLE);
                    mViewBinding.devRecycler.setVisibility(View.GONE);
                } else {
                    mViewBinding.devNodataView.setVisibility(View.GONE);
                    mViewBinding.devRecycler.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<AirConditionerConverter.AirConditioner, BaseViewHolder>(R.layout.item_dev, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, AirConditionerConverter.AirConditioner conditioner) {
                TextView goTV = holder.getView(R.id.og_iv);
                goTV.setTypeface(mIconfont);

                holder.setText(R.id.dev_name_tv, conditioner.getNickname())
                        .setVisible(R.id.divider, mList.indexOf(conditioner) != 0);
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(VirtualAirConditionersActivity.this).load(mVirtualAirConditionerImg).into(imageView);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                EDevice.deviceEntry entry = DeviceBuffer.getDeviceInformation(mIotId);

                Intent intent = new Intent(VirtualAirConditionersActivity.this, IdentifierListActivity.class);
                intent.putExtra("nick_name", entry.nickName);
                intent.putExtra("dev_name", entry.deviceName);
                intent.putExtra("dev_iot", mIotId);
                intent.putExtra("product_key", entry.productKey);
                intent.putExtra("virtual_end_point", mList.get(position).getEndPoint());
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.devRecycler.setLayoutManager(layoutManager);
        mViewBinding.devRecycler.setAdapter(mAdapter);

        mViewBinding.recyclerRl.setEnableLoadMore(false);
        mViewBinding.recyclerRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                DeviceBuffer.initAirConditioner();
                getExtendedProperty();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mViewBinding.includeToolbar.ivToolbarLeft.getId()) {
            finish();
        }
    }
}