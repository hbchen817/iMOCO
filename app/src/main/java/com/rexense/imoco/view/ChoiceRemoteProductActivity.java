package com.rexense.imoco.view;

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
import com.rexense.imoco.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceRemoteProductActivity extends BaseActivity {
    @BindView(R.id.iv_toolbar_left)
    ImageView mToolbarLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.product_recycler)
    RecyclerView mProductRV;

    private List<ProductItem> mList = new ArrayList<>();
    private BaseQuickAdapter<ProductItem, BaseViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_remote_product);

        ButterKnife.bind(this);
        initStatusBar();
        init();
    }

    private void init() {
        String[] itemTitles = getResources().getStringArray(R.array.remote_product_titles);
        TypedArray itemIcons = getResources().obtainTypedArray(R.array.remote_product_ics);
        for (int i = 0; i < itemTitles.length; i++) {
            ProductItem item = new ProductItem(itemIcons.getResourceId(i, 0), itemTitles[i]);
            if (i == 0)
                item.setTid("2");
            else if (i == 1)
                item.setTid("7");
            else if (i == 2)
                item.setTid("6");
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
        mProductRV.setLayoutManager(layoutManager);
        //mProductRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mProductRV.setAdapter(mAdapter);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mToolbarTitle.setText(R.string.configproduct_title);
    }

    @OnClick({R.id.iv_toolbar_left})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_left: {
                finish();
                break;
            }
        }
    }

    private class ProductItem {
        private int icon;
        private String title;
        private String tid;

        public ProductItem(int icon, String title, String tid) {
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

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }
    }
}