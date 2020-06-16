package com.rexense.imoco.typefactory;

import android.view.View;

import com.rexense.imoco.model.ItemMsgCenter;
import com.rexense.imoco.model.ItemShareDevice;
import com.rexense.imoco.viewholder.BaseViewHolder;


/**
 * Created by yq05481 on 2016/12/30.
 */

public interface TypeFactory {
    int type(ItemMsgCenter item);
    int type(ItemShareDevice item);

    BaseViewHolder createViewHolder(int type, View itemView);
}
