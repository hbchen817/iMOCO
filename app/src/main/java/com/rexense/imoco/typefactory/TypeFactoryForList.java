package com.rexense.imoco.typefactory;

import android.view.View;

import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemAddRoomDevice;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.model.ItemHistoryMsg;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.ItemSceneLog;
import com.rexense.imoco.model.ItemShareDevice;
import com.rexense.imoco.model.ItemUser;
import com.rexense.imoco.viewholder.BaseViewHolder;
import com.rexense.imoco.viewholder.ItemAddRoomDeviceViewHolder;
import com.rexense.imoco.viewholder.ItemGatewayViewHolder;
import com.rexense.imoco.viewholder.ItemHistoryViewHolder;
import com.rexense.imoco.viewholder.ItemMsgCenterViewHolder;
import com.rexense.imoco.viewholder.ItemSceneLogViewHolder;
import com.rexense.imoco.viewholder.ItemShareDeviceViewHolder;
import com.rexense.imoco.viewholder.ItemUserViewHolder;


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
    public int type(ItemSceneLog model) {
        return R.layout.item_scene_log;
    }
    @Override
    public int type(ItemAddRoomDevice model) {
        return R.layout.item_add_room_device;
    }
    @Override
    public int type(ItemGateway model) {
        return R.layout.item_gateway;
    }
    @Override
    public int type(ItemUser model) {
        return R.layout.item_user;
    }
    @Override
    public int type(ItemHistoryMsg model) {
        return R.layout.item_history;
    }

    @Override
    public BaseViewHolder createViewHolder(int type, View itemView) {
        switch (type){
            case R.layout.item_msg_center:
                    return new ItemMsgCenterViewHolder(itemView);
            case R.layout.item_share_device:
                    return new ItemShareDeviceViewHolder(itemView);
            case R.layout.item_scene_log:
                    return new ItemSceneLogViewHolder(itemView);
            case R.layout.item_add_room_device:
                    return new ItemAddRoomDeviceViewHolder(itemView);
            case R.layout.item_gateway:
                    return new ItemGatewayViewHolder(itemView);
            case R.layout.item_user:
                    return new ItemUserViewHolder(itemView);
            case R.layout.item_history:
                    return new ItemHistoryViewHolder(itemView);
            default:
                    return null;
        }
    }
}
