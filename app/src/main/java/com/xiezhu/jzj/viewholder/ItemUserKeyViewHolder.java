package com.xiezhu.jzj.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ItemUserKey;

import java.util.List;

public class ItemUserKeyViewHolder extends BaseViewHolder<ItemUserKey> {
    public ItemUserKeyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemUserKey model, int position, CommonAdapter adapter, List<Integer> payloads) {

        View header = getView(R.id.user_header);
        header.setVisibility(model.isHaveHeader() ? View.VISIBLE : View.GONE);
        TextView userNameView = (TextView) getView(R.id.user_name);
        userNameView.setText(model.getUserName());
        View keyView = getView(R.id.key_view);
        TextView key_name = (TextView) getView(R.id.key_name);

        if (TextUtils.isEmpty(model.getKeyNickName())) {
            StringBuilder stringBuilder = new StringBuilder();
            switch (model.getLockUserType()) {
                case 1:
                    stringBuilder.append("指纹");
                    break;
                case 2:
                    stringBuilder.append("密码");
                    break;
                case 3:
                    stringBuilder.append("卡");
                    break;
                case 4:
                    stringBuilder.append("机械");
                    break;
                default:
                    break;
            }
            stringBuilder.append("钥匙");
            stringBuilder.append(model.getLockUserId());
            key_name.setText(stringBuilder.toString());
        } else {
            key_name.setText(model.getKeyNickName());
        }

        keyView.setTag(position);
        keyView.setOnClickListener(adapter.getOnClickListener());
    }
}
