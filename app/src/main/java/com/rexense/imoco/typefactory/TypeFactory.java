package com.rexense.imoco.typefactory;

import android.view.View;

import com.rexense.imoco.model.ItemAddRoomDevice;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.ItemSceneLog;
import com.rexense.imoco.model.ItemShareDevice;
import com.rexense.imoco.viewholder.BaseViewHolder;


/**
 * Created by yq05481 on 2016/12/30.
 */

public interface TypeFactory {
    int type(ItemMsgCenter item);
    int type(ItemShareDevice item);
    int type(ItemSceneLog item);
    int type(ItemAddRoomDevice item);
    int type(ItemGateway item);

    BaseViewHolder createViewHolder(int type, View itemView);
}
