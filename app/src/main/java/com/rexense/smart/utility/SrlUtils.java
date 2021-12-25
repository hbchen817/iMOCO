package com.rexense.smart.utility;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

/**
 * @author fyy
 * @date 2020/6/16
 */
public class SrlUtils {

    public static void finishRefresh(SmartRefreshLayout srl, boolean successOrNot) {

        if (srl != null && srl.getState() == RefreshState.Refreshing) {
            srl.finishRefresh(successOrNot);
        }
    }

    public static void finishLoadMore(SmartRefreshLayout srl, boolean successOrNot) {

        if (srl != null && srl.getState() == RefreshState.Loading) {
            srl.finishLoadMore(successOrNot);
        }
    }
}
