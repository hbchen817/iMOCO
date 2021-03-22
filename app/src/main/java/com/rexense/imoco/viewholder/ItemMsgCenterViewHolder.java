package com.rexense.imoco.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.R;
import com.rexense.imoco.presenter.ImageProvider;

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
        if(title.getText().toString().equals("")){
            title.setText(model.getContent());
            content.setVisibility(View.GONE);
        } else {
            content.setText(model.getContent());
        }
        time.setText(model.getTime());
        btn_view.setVisibility(model.isShowBtnView()?View.VISIBLE:View.GONE);

        String image = model.getProductImg();
        if (image != null && image.length()>0)
            Glide.with(adapter.getmContext()).load(image).into(msg_img);
        else {
            int imgSrcId = ImageProvider.genProductIcon(model.getProductKey());
            if (imgSrcId != 0) {
                msg_img.setImageResource(imgSrcId);
            } else {
                if (model.getProductImg() != null && model.getProductImg().length() > 0) {
                    Glide.with(adapter.getmContext()).load(model.getProductImg()).into(msg_img);
                } else {
                    msg_img.setImageResource(R.drawable.notify);
                }
            }
        }

        agree_btn.setTag(position);
        agree_btn.setOnClickListener(adapter.getOnClickListener());
        disagree_btn.setTag(position);
        disagree_btn.setOnClickListener(adapter.getOnClickListener());
    }
}
