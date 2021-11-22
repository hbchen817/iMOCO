package com.laffey.smart.view;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivitySelectAssociatedDevBinding;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.EUser;
import com.laffey.smart.model.ItemBindList;
import com.laffey.smart.model.ItemBindRelation;
import com.laffey.smart.presenter.CloudDataParser;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.UserCenter;
import com.laffey.smart.utility.GsonUtil;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.utility.SpUtils;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SelectAssociatedDevActivity extends BaseActivity {
    private ActivitySelectAssociatedDevBinding mViewBinding;

    private static final String IOT_ID = "iot_id";
    private static final String GATEWAY_ID = "gateway_id";
    private static final String SRC_ENDPOINT_ID = "src_endpoint_id";
    private static final String SRC_PRODUCT_KEY = "src_product_key";
    private final int PAGE_SIZE = 10;

    private String mOriginIotId;
    private String mGatewayId;
    private String mOriginMac;
    private int mOriginEndId;
    private String mSrcPK;
    private ItemBindList mCacheBindList;
    private final List<ItemBindRelation> mCacheRelations = new ArrayList<>();

    private MyHandler mHandler;
    private List<EDevice.deviceEntry> mList = new ArrayList<>();
    private BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder> mAdapter;

    private Typeface mIconfont;

    public static void start(Context context, String srcIotId, String gatewayId, int srcEndId, String srcPK) {
        Intent intent = new Intent(context, SelectAssociatedDevActivity.class);
        intent.putExtra(IOT_ID, srcIotId);// A->
        intent.putExtra(GATEWAY_ID, gatewayId);
        intent.putExtra(SRC_ENDPOINT_ID, srcEndId);
        intent.putExtra(SRC_PRODUCT_KEY, srcPK);// A->
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySelectAssociatedDevBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mOriginIotId = getIntent().getStringExtra(IOT_ID);
        mGatewayId = getIntent().getStringExtra(GATEWAY_ID);
        mOriginEndId = getIntent().getIntExtra(SRC_ENDPOINT_ID, -1);
        mSrcPK = getIntent().getStringExtra(SRC_PRODUCT_KEY);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mHandler = new MyHandler(this);

        mOriginMac = DeviceBuffer.getDeviceInformation(mOriginIotId).mac;
        mCacheBindList = DeviceBuffer.getBindList(mOriginMac + "-" + mOriginEndId);
        if (mCacheBindList != null) {
            mCacheRelations.addAll(mCacheBindList.getBindList());
        }

        initStatusBar();
        initAdapter();

        QMUITipDialogUtil.showLoadingDialg(this, R.string.is_loading);
        new UserCenter(SelectAssociatedDevActivity.this).getGatewaySubdeviceList(mGatewayId, 1, PAGE_SIZE,
                mCommitFailureHandler, mResponseErrorHandler, mHandler);
    }

    private void initAdapter() {
        mAdapter = new BaseQuickAdapter<EDevice.deviceEntry, BaseViewHolder>(R.layout.item_dev, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, EDevice.deviceEntry item) {
                TextView ogTV = holder.getView(R.id.og_iv);
                ogTV.setTypeface(mIconfont);

                holder.setText(R.id.dev_name_tv, item.nickName)
                        .setVisible(R.id.divider, mList.indexOf(item) != 0);
                ImageView imageView = holder.getView(R.id.dev_iv);
                Glide.with(SelectAssociatedDevActivity.this).load(item.image).into(imageView);

                ogTV.setTextColor(ContextCompat.getColor(SelectAssociatedDevActivity.this, R.color.black));
                holder.setTextColor(R.id.dev_name_tv, ContextCompat.getColor(SelectAssociatedDevActivity.this, R.color.black));
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                /*boolean isBinded = false;
                for (ItemBindRelation relation : mCacheRelations) {
                    if (relation.getMac().equals(mList.get(position).mac)) {
                        isBinded = true;
                        break;
                    }
                }
                if (!isBinded) {
                    EDevice.deviceEntry deviceEntry = mList.get(position);
                    SelectAssociatedKeyActivity.start(SelectAssociatedDevActivity.this, mOriginIotId, String.valueOf(mOriginEndId),
                            deviceEntry.iotId, mGatewayId, false);
                } else {
                    ToastUtils.showLongToast(SelectAssociatedDevActivity.this, R.string.dev_has_been_binded);
                }*/
                EDevice.deviceEntry deviceEntry = mList.get(position);
                SelectAssociatedKeyActivity.start(SelectAssociatedDevActivity.this, mOriginIotId, String.valueOf(mOriginEndId),
                        deviceEntry.iotId, mGatewayId, false);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.devRv.setLayoutManager(layoutManager);
        mViewBinding.devRv.setAdapter(mAdapter);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.select_dev);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SelectAssociatedDevActivity> reference;

        public MyHandler(SelectAssociatedDevActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SelectAssociatedDevActivity activity = reference.get();
            if (activity != null) {
                if (msg.what == Constant.MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST) {
                    EUser.gatewaySubdeviceListEntry list = CloudDataParser.processGatewaySubdeviceList((String) msg.obj);
                    ViseLog.d("网关下子设备 = \n" + GsonUtil.toJson(list));
                    if (list != null && list.data != null) {
                        for (EUser.deviceEntry e : list.data) {
                            if (e.productKey.equals(CTSL.PK_ONEWAYSWITCH) ||
                                    e.productKey.equals(CTSL.PK_TWOWAYSWITCH) ||
                                    e.productKey.equals(CTSL.PK_THREE_KEY_SWITCH) ||
                                    e.productKey.equals(CTSL.PK_FOURWAYSWITCH_2) ||
                                    e.productKey.equals(CTSL.PK_SIX_TWO_SCENE_SWITCH)) {
                                EDevice.deviceEntry entry = new EDevice.deviceEntry();
                                entry.iotId = e.iotId;
                                entry.nickName = e.nickName;
                                entry.productKey = e.productKey;
                                entry.status = e.status;
                                entry.owned = DeviceBuffer.getDeviceOwned(e.iotId);
                                entry.image = e.image;
                                entry.mac = DeviceBuffer.getDeviceMac(e.iotId);
                                activity.mList.add(entry);
                            }
                        }
                        if (list.data.size() >= list.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            new UserCenter(activity).getGatewaySubdeviceList(activity.mGatewayId, list.pageNo + 1, activity.PAGE_SIZE, activity.mCommitFailureHandler, activity.mResponseErrorHandler, activity.mHandler);
                        } else {
                            // 数据获取完则加载显示
                            activity.mAdapter.notifyDataSetChanged();
                            if (activity.mList.size() > 0) {
                                activity.mViewBinding.nodataView.setVisibility(View.GONE);
                                activity.mViewBinding.devRv.setVisibility(View.VISIBLE);
                            } else {
                                activity.mViewBinding.nodataView.setVisibility(View.VISIBLE);
                                activity.mViewBinding.devRv.setVisibility(View.GONE);
                            }
                            QMUITipDialogUtil.dismiss();
                        }
                    }
                }
            }
        }
    }
}