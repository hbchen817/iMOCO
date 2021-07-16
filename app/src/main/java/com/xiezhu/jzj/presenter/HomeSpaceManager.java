package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.os.Handler;

import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EAPIChannel;
import com.xiezhu.jzj.model.EHomeSpace;
import com.xiezhu.jzj.sdk.APIChannel;
import com.xiezhu.jzj.utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 家空间管理者
 */
public class HomeSpaceManager {
    private Context mContext;
    private static Map<String, EHomeSpace.roomEntry> mRoomBuffer = new HashMap<String, EHomeSpace.roomEntry>();

    // 构造
    public HomeSpaceManager(Context context) {
        this.mContext = context;
    }

    // 创建家
    public void createHome(String name,
                           Handler commitFailureHandler,
                           Handler responseErrorHandler,
                           Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CREATEHOME;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("name", name);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CREATEHOME;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取家列表
    public void getHomeList(int pageNo, int pageSize,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETHOMELIST;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("pageNo", Math.max(pageNo, 1));
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 20 ? 20 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETHOMELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取家房间列表
    public void getHomeRoomList(String homeId, int pageNo, int pageSize,
                                Handler commitFailureHandler,
                                Handler responseErrorHandler,
                                Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETHOMEROOMLIST;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("pageNo", Math.max(pageNo, 1));
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 20 ? 20 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETHOMEROOMLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取家设备列表
    public void getHomeDeviceList(String homeId, String roomId, int pageNo, int pageSize,
                                  Handler commitFailureHandler,
                                  Handler responseErrorHandler,
                                  Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETHOMEDEVICELIST;
        requestParameterEntry.version = "1.0.0";
        if (homeId != null && homeId.length() > 0) {
            requestParameterEntry.addParameter("homeId", homeId);
        }
        if (roomId != null && roomId.length() > 0) {
            requestParameterEntry.addParameter("roomId", roomId);
        }
        requestParameterEntry.addParameter("pageNo", Math.max(pageNo, 1));
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETHOMEDEVICELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取家网关列表
    public void getHomeGatewayList(String homeId, String roomId, int pageNo, int pageSize,
                                   Handler commitFailureHandler,
                                   Handler responseErrorHandler,
                                   Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETHOMEDEVICELIST;
        requestParameterEntry.version = "1.0.0";
        if (homeId != null && homeId.length() > 0) {
            requestParameterEntry.addParameter("homeId", homeId);
        }
        if (roomId != null && roomId.length() > 0) {
            requestParameterEntry.addParameter("roomId", roomId);
        }
        requestParameterEntry.addParameter("deviceNodeType", "GATEWAY");
        requestParameterEntry.addParameter("pageNo", Math.max(pageNo, 1));
        requestParameterEntry.addParameter("pageSize", pageSize <= 0 || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETHOMEGATWAYLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 更新房间设备
    public void updateRoomDevice(String homeId, String roomId, String iotId,
                                 Handler commitFailureHandler,
                                 Handler responseErrorHandler,
                                 Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATEDEVICEROOM;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("roomId", roomId);
        List<String> iotIdList = new ArrayList<String>();
        iotIdList.add(iotId);
        // 将房间原来的设备附加上
        Map<String, Integer> deviceList = DeviceBuffer.getRoomDeviceList(roomId);
        if (deviceList != null && deviceList.size() > 0) {
            for (String key : deviceList.keySet()) {
                if (!key.equals(iotId)) {
                    iotIdList.add(key);
                }
            }
        }
        requestParameterEntry.addParameter("iotIdList", iotIdList);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATEDEVICEROOM;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 更新房间设备（可批量）
    public void updateRoomDevices(String homeId, String roomId, ArrayList<String> iotIdList,
                                  Handler commitFailureHandler,
                                  Handler responseErrorHandler,
                                  Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATEDEVICEROOM;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("roomId", roomId);
        requestParameterEntry.addParameter("iotIdList", iotIdList);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATEDEVICEROOM;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取房间设备
    public void getRoomDevice(int pageNo, String homeId, String roomId,
                              Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETDEVICEINROOM;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", Constant.PAGE_SIZE);
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("roomId", roomId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETDEVICEINROOM;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 更新房间名称
    public void updateRoomInfo(String homeId, String roomId, String name,
                               Handler commitFailureHandler,
                               Handler responseErrorHandler,
                               Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }
        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATEROOM;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("name", name);
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("roomId", roomId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATEROOM;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 清除房间缓存数据
    public static void clearRoomBufferData() {
        mRoomBuffer.clear();
    }

    // 追加房间缓存数据
    public static void addRoomBufferData(List<EHomeSpace.roomEntry> roomEntryList) {
        if (roomEntryList == null || roomEntryList.size() == 0) {
            return;
        }

        for (EHomeSpace.roomEntry entry : roomEntryList) {
            if (mRoomBuffer.containsKey(entry.roomId)) {
                mRoomBuffer.remove(entry.roomId);
            }
            mRoomBuffer.put(entry.roomId, entry);
        }
    }

    // 获取房间缓存数据
    public static Map<String, EHomeSpace.roomEntry> getRoomBufferData() {
        return mRoomBuffer;
    }

    // 更新房间设备数量
    public static void updateRoomDeviceNumber(String roomId, int deviceNumberChange) {
        if (mRoomBuffer.containsKey(roomId)) {
            mRoomBuffer.get(roomId).deviceCnt = mRoomBuffer.get(roomId).deviceCnt + deviceNumberChange;
        }
    }
}
