package com.rexense.imoco.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemAddRoomDevice;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.presenter.ImageProvider;

import java.util.List;

public class ItemGatewayViewHolder extends BaseViewHolder<ItemGateway> {
    public ItemGatewayViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemGateway model, int position, CommonAdapter adapter, List<Integer> payloads) {
        View root_view = getView(R.id.root_view);
        TextView device_name = (TextView) getView(R.id.name);
        ImageView icon = (ImageView) getView(R.id.icon);
        TextView right_img = (TextView) getView(R.id.mac);

        device_name.setText("瑞瀛智能家居网关RG4100");
        right_img.setText("Mac:" + model.getMac());
        icon.setImageResource(R.drawable.icon_gateway_fton);

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
