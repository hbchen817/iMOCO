package com.xiezhu.jzj.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ItemAddRoomDevice;
import com.xiezhu.jzj.presenter.ImageProvider;

import java.util.List;

public class ItemAddRoomDeviceViewHolder extends BaseViewHolder<ItemAddRoomDevice> {
    public ItemAddRoomDeviceViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemAddRoomDevice model, int position, CommonAdapter adapter, List<Integer> payloads) {
        TextView device_name = (TextView) getView(R.id.device_name);
        ImageView device_img = (ImageView) getView(R.id.device_img);
        ImageView right_img = (ImageView) getView(R.id.right_img);// 0不显示 1 箭头 2 未选择 3 已选择

        int imgSrcId = ImageProvider.genProductIcon(model.getProductKey());
        if (imgSrcId!=0){
            device_img.setImageResource(imgSrcId);
        }
        device_name.setText(model.getDeviceName());
        right_img.setImageResource(model.getType()==0?R.drawable.add_device:R.drawable.delete_device);

        right_img.setTag(position);
        right_img.setOnClickListener(adapter.getOnClickListener());
    }
}
