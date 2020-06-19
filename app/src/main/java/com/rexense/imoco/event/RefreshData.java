package com.rexense.imoco.event;


import org.greenrobot.eventbus.EventBus;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-13 15:29
 * Description: 刷数事件
 */
public class RefreshData {
    // 刷新场景列表数据
    public static void refreshSceneListData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新房间列表数据
    public static void refreshRoomListData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_ROOM_LIST_DATA);
        EventBus.getDefault().post(event);
    }
}
