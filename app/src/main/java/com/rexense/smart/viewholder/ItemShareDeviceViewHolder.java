package com.rexense.smart.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rexense.smart.R;
import com.rexense.smart.model.ItemShareDevice;
import com.rexense.smart.presenter.ImageProvider;

import java.util.List;

public class ItemShareDeviceViewHolder extends BaseViewHolder<ItemShareDevice> {
    public ItemShareDeviceViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemShareDevice model, int position, CommonAdapter adapter, List<Integer> payloads) {
        TextView device_name = (TextView) getView(R.id.device_name);
        ImageView device_img = (ImageView) getView(R.id.device_img);
        ImageView right_img = (ImageView) getView(R.id.right_img);// 0不显示 1 箭头 2 未选择 3 已选择
        View root_view = getView(R.id.root_view);

        String image = model.getImage();
        if (image != null && image.length() > 0)
            Glide.with(adapter.getmContext()).load(image).into(device_img);
        else {
            int imgSrcId = ImageProvider.genProductIcon(model.getProductKey());
            if (imgSrcId != 0) {
                device_img.setImageResource(imgSrcId);
            }
        }
        device_name.setText(model.getDeviceName());
        if (model.getStatus()==0){
            right_img.setVisibility(View.GONE);
        }else if (model.getStatus()==1){
            right_img.setImageResource(R.drawable.go);
        }else if (model.getStatus()==2){
            right_img.setImageResource(R.drawable.unselect);
        }else {
            right_img.setImageResource(R.drawable.dialog_ok);
        }

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
