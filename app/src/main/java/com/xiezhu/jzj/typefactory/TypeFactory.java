package com.xiezhu.jzj.typefactory;

import android.view.View;

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
