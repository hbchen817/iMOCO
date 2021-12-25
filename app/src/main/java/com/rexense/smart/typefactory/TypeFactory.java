package com.rexense.smart.typefactory;

import android.view.View;

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


/**
 * Created by yq05481 on 2016/12/30.
 */

public interface TypeFactory {
    int type(ItemMsgCenter item);
    int type(ItemShareDevice item);
    int type(ItemSceneLog item);
    int type(ItemAddRoomDevice item);
    int type(ItemGateway item);
    int type(ItemUser item);
    int type(ItemHistoryMsg item);
    int type(ItemUserKey item);
    int type(ItemColorLightScene item);

    BaseViewHolder createViewHolder(int type, View itemView);
}
