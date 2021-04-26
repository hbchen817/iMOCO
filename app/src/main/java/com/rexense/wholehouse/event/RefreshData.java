package com.rexense.wholehouse.event;


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

    // 刷新场景列表数据（首页）
    public static void refreshHomeSceneListData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_SCENE_LIST_DATA_HOME);
        EventBus.getDefault().post(event);
    }

    // 刷新房间列表数据
    public static void refreshRoomListData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_ROOM_LIST_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新设备列表房间数据
    public static void refreshDeviceListRoomData(String iotId){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_DEVICE_LIST_ROOM_DATA, iotId);
        EventBus.getDefault().post(event);
    }

    // 刷新设备状态数据
    public static void refreshDeviceStateData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_DEVICE_STATE_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新设备状态数据_备份
    public static void refreshDeviceStateDataFromBuffer() {
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_DEVICE_BUFFER_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新设备数量数据
    public static void refreshDeviceNumberData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_DEVICE_NUMBER_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新设备数量数据
    public static void refreshDeviceListData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_DEVICE_LIST_DATA);
        EventBus.getDefault().post(event);
    }

    // 刷新网关固件数据
    public static void refreshGatewayFirmwareData(){
        EEvent event = new EEvent(CEvent.EVENT_NAME_REFRESH_GATEWAY_FIRMWARE_DATA);
        EventBus.getDefault().post(event);
    }
}
