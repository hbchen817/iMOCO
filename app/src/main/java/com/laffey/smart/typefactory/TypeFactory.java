package com.laffey.smart.typefactory;

import android.view.View;

import com.laffey.smart.model.ItemAddRoomDevice;
import com.laffey.smart.model.ItemColorLightScene;
import com.laffey.smart.model.ItemGateway;
import com.laffey.smart.model.ItemHistoryMsg;
import com.laffey.smart.model.ItemMsgCenter;
import com.laffey.smart.model.ItemSceneLog;
import com.laffey.smart.model.ItemShareDevice;
import com.laffey.smart.model.ItemUser;
import com.laffey.smart.model.ItemUserKey;
import com.laffey.smart.viewholder.BaseViewHolder;


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
