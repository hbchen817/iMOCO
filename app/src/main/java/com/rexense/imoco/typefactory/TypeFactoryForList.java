package com.rexense.imoco.typefactory;

import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.ItemShareDevice;
import com.rexense.imoco.viewholder.BaseViewHolder;
import com.rexense.imoco.viewholder.ItemMsgCenterViewHolder;
import com.rexense.imoco.viewholder.ItemShareDeviceViewHolder;


/**
 * Created by yq05481 on 2016/12/30.
 */

public class TypeFactoryForList implements TypeFactory {

    @Override
    public int type(ItemMsgCenter model) {
        return R.layout.item_msg_center;
    }
    @Override
    public int type(ItemShareDevice model) {
        return R.layout.item_share_device;
    }

    @Override
    public BaseViewHolder createViewHolder(int type, View itemView) {
        switch (type){
            case R.layout.item_msg_center:
                    return new ItemMsgCenterViewHolder(itemView);
            case R.layout.item_share_device:
                    return new ItemShareDeviceViewHolder(itemView);
            default:
                    return null;
        }
    }
}
