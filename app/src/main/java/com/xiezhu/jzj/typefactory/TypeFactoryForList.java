package com.xiezhu.jzj.typefactory;

import android.view.View;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.ItemAddRoomDevice;
import com.xiezhu.jzj.model.ItemColorLightScene;
import com.xiezhu.jzj.model.ItemGateway;
import com.xiezhu.jzj.model.ItemHistoryMsg;
import com.xiezhu.jzj.model.ItemMsgCenter;
import com.xiezhu.jzj.model.ItemSceneLog;
import com.xiezhu.jzj.model.ItemShareDevice;
import com.xiezhu.jzj.model.ItemUser;
import com.xiezhu.jzj.model.ItemUserKey;
import com.xiezhu.jzj.viewholder.BaseViewHolder;
import com.xiezhu.jzj.viewholder.ItemAddRoomDeviceViewHolder;
import com.xiezhu.jzj.viewholder.ItemColorLightSceneViewHolder;
import com.xiezhu.jzj.viewholder.ItemGatewayViewHolder;
import com.xiezhu.jzj.viewholder.ItemHistoryViewHolder;
import com.xiezhu.jzj.viewholder.ItemMsgCenterViewHolder;
import com.xiezhu.jzj.viewholder.ItemSceneLogViewHolder;
import com.xiezhu.jzj.viewholder.ItemShareDeviceViewHolder;
import com.xiezhu.jzj.viewholder.ItemUserKeyViewHolder;
import com.xiezhu.jzj.viewholder.ItemUserViewHolder;


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
