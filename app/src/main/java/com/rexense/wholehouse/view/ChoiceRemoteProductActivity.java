package com.rexense.wholehouse.view;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.databinding.ActivityChoiceRemoteProductBinding;
import com.rexense.wholehouse.utility.JDInterfaceImplUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceRemoteProductActivity extends BaseActivity {
    private ActivityChoiceRemoteProductBinding mViewBinding;

    private List<ProductItem> mList = new ArrayList<>();
    private BaseQuickAdapter<ProductItem, BaseViewHolder> mAdapter;
    private TypedArray mItemIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceRemoteProductBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        init();
    }

    private void init() {
        String[] itemTitles = getResources().getStringArray(R.array.remote_product_titles);
        mItemIcons = getResources().obtainTypedArray(R.array.remote_product_ics);
        for (int i = 0; i < itemTitles.length; i++) {
            ProductItem item = new ProductItem(mItemIcons.getResourceId(i, 0), itemTitles[i]);
            if (i == 0)
                item.setTid(Constant.REMOTE_CONTROL_TV);
            else if (i == 1)
                item.setTid(Constant.REMOTE_CONTROL_AIR_CONDITIONER);
            else if (i == 2)
                item.setTid(Constant.REMOTE_CONTROL_FAN);
            mList.add(item);
        }
        mAdapter = new BaseQuickAdapter<ProductItem, BaseViewHolder>(/*R.layout.item_remote_product*/R.layout.item_remote_product_grid, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ProductItem item) {
                holder.setImageResource(R.id.item_icon, item.getIcon())
                        .setText(R.id.item_title, item.getTitle());
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(ChoiceRemoteProductActivity.this, ChoiceBrandActivity.class);
                intent.putExtra("dev_tid", mList.get(position).getTid());
                startActivity(intent);
            }
        });

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);*/
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mViewBinding.productRecycler.setLayoutManager(layoutManager);
        //mViewBinding.productRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mViewBinding.productRecycler.setAdapter(mAdapter);
        mViewBinding.includeToolbar.ivToolbarLeft.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mItemIcons.recycle();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(R.string.configproduct_title);
    }

    protected void onViewClicked(View view) {
        if (view.getId() == R.id.iv_toolbar_left) {
            finish();
        }
    }

    private class ProductItem {
        private int icon;
        private String title;
        private int tid;

        public ProductItem(int icon, String title, int tid) {
            this.icon = icon;
            this.title = title;
            this.tid = tid;
        }

        public ProductItem(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }
    }
}