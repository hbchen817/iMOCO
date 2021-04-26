package com.rexense.wholehouse.typefactory;

import android.view.View;

import com.rexense.wholehouse.model.ItemAddRoomDevice;
import com.rexense.wholehouse.model.ItemColorLightScene;
import com.rexense.wholehouse.model.ItemGateway;
import com.rexense.wholehouse.model.ItemHistoryMsg;
import com.rexense.wholehouse.model.ItemMsgCenter;
import com.rexense.wholehouse.model.ItemSceneLog;
import com.rexense.wholehouse.model.ItemShareDevice;
import com.rexense.wholehouse.model.ItemUser;
import com.rexense.wholehouse.model.ItemUserKey;
import com.rexense.wholehouse.viewholder.BaseViewHolder;


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
