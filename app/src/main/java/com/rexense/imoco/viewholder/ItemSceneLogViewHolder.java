package com.rexense.imoco.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemSceneLog;

import java.util.List;

public class ItemSceneLogViewHolder extends BaseViewHolder<ItemSceneLog> {
    public ItemSceneLogViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemSceneLog model, int position, CommonAdapter adapter, List<Integer> payloads) {
        TextView log_name = (TextView) getView(R.id.log_name);
        TextView log_time = (TextView) getView(R.id.log_time);
        TextView log_detail = (TextView) getView(R.id.log_detail);
        ImageView log_img = (ImageView) getView(R.id.log_img);
        log_img.setImageResource(R.drawable.tab_2_press);
        View root_view = getView(R.id.root_view);

        log_name.setText(model.getLogName());
        log_time.setText(model.getLogTime());
        log_detail.setText(adapter.getmContext().getString(model.getResult() == 0 ? R.string.scene_log_result_fail : R.string.scene_log_result_success));
        //Glide.with(adapter.getmContext()).load(model.getIcon()).into(log_img);

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
