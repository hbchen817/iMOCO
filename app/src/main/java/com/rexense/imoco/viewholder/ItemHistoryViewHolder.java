package com.rexense.imoco.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.model.ItemHistoryMsg;

import java.util.List;

public class ItemHistoryViewHolder extends BaseViewHolder<ItemHistoryMsg> {
    public ItemHistoryViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemHistoryMsg model, int position, CommonAdapter adapter, List<Integer> payloads) {
        View root_view = getView(R.id.root_view);
        TextView name = (TextView) getView(R.id.name);
        ImageView icon = (ImageView) getView(R.id.icon);
        TextView time = (TextView) getView(R.id.time);

        name.setText(model.getContent());
        time.setText(model.getTime());
        switch (model.getType()) {

            default:
                icon.setImageResource(R.drawable.icon_gateway_fton);
                break;
        }

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
