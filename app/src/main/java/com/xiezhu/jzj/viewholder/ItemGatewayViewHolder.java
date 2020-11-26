package com.xiezhu.jzj.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ItemGateway;

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

        device_name.setText("jzj智能家居网关RG4100");
        right_img.setText("Mac:" + model.getMac());
        icon.setImageResource(R.drawable.icon_gateway_fton);

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
