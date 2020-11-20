package com.xiezhu.jzj.viewholder;

import android.view.View;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ItemColorLightScene;

import java.util.List;

public class ItemColorLightSceneViewHolder extends BaseViewHolder<ItemColorLightScene> {

    public ItemColorLightSceneViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemColorLightScene model, int position, CommonAdapter adapter, List<Integer> payloads) {
        View root_view = getView(R.id.root_view);

        TextView scene_name = (TextView) getView(R.id.scene_name);
        TextView kvalue = (TextView) getView(R.id.kvalue);
        TextView lightnessValue = (TextView) getView(R.id.lightnessValue);

        scene_name.setText(model.getSceneName());
        kvalue.setText(model.getK());
        lightnessValue.setText(model.getLightness());

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
