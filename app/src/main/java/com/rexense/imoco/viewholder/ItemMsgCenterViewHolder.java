package com.rexense.imoco.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.R;

import java.util.List;

public class ItemMsgCenterViewHolder extends BaseViewHolder<ItemMsgCenter> {
    public ItemMsgCenterViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemMsgCenter model, int position, CommonAdapter adapter, List<Integer> payloads) {
        TextView title = (TextView) getView(R.id.title);
        TextView content = (TextView) getView(R.id.content);
        TextView time = (TextView) getView(R.id.time);
        TextView agree_btn = (TextView) getView(R.id.agree_btn);
        TextView disagree_btn = (TextView) getView(R.id.disagree_btn);
        ImageView msg_img = (ImageView) getView(R.id.msg_img);
        View btn_view = getView(R.id.btn_view);

        title.setText(model.getTitle());
        content.setText(model.getContent());
        time.setText(model.getTime());
        btn_view.setVisibility(model.isShowBtnView()?View.VISIBLE:View.GONE);

        agree_btn.setTag(position);
        agree_btn.setOnClickListener(adapter.getOnClickListener());
        disagree_btn.setTag(position);
        disagree_btn.setOnClickListener(adapter.getOnClickListener());
    }
}
