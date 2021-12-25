package com.rexense.smart.typefactory;

import android.view.View;

import com.rexense.smart.R;
import com.rexense.smart.model.ItemAddRoomDevice;
import com.rexense.smart.model.ItemColorLightScene;
import com.rexense.smart.model.ItemGateway;
import com.rexense.smart.model.ItemHistoryMsg;
import com.rexense.smart.model.ItemMsgCenter;
import com.rexense.smart.model.ItemSceneLog;
import com.rexense.smart.model.ItemShareDevice;
import com.rexense.smart.model.ItemUser;
import com.rexense.smart.model.ItemUserKey;
import com.rexense.smart.viewholder.BaseViewHolder;
import com.rexense.smart.viewholder.ItemAddRoomDeviceViewHolder;
import com.rexense.smart.viewholder.ItemColorLightSceneViewHolder;
import com.rexense.smart.viewholder.ItemGatewayViewHolder;
import com.rexense.smart.viewholder.ItemHistoryViewHolder;
import com.rexense.smart.viewholder.ItemMsgCenterViewHolder;
import com.rexense.smart.viewholder.ItemSceneLogViewHolder;
import com.rexense.smart.viewholder.ItemShareDeviceViewHolder;
import com.rexense.smart.viewholder.ItemUserKeyViewHolder;
import com.rexense.smart.viewholder.ItemUserViewHolder;


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
    public int type(ItemUserKey model) {
        return R.layout.item_user_key;
    }
    @Override
    public int type(ItemColorLightScene model) {
        return R.layout.item_color_light_scene;
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
            case R.layout.item_user_key:
                    return new ItemUserKeyViewHolder(itemView);
            case R.layout.item_color_light_scene:
                    return new ItemColorLightSceneViewHolder(itemView);
            default:
                    return null;
        }
    }
}
