package com.laffey.smart.viewholder;

import android.view.View;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.model.ItemUser;

import java.util.List;

public class ItemUserViewHolder extends BaseViewHolder<ItemUser> {
    public ItemUserViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemUser model, int position, CommonAdapter adapter, List<Integer> payloads) {
        View root_view = getView(R.id.root_view);

        TextView device_name = (TextView) getView(R.id.name);
        device_name.setText(model.getName());

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}