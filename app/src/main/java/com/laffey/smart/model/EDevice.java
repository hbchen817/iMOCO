package com.laffey.smart.model;

import android.content.Context;

import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.presenter.CodeMapper;
import com.laffey.smart.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备实体(包括网关及子设备)
 */
public class EDevice {
    // 设备实体
    public static class deviceEntry {
        public String iotId;
        public String nickName;
        public String deviceName;
        public String productKey;
        public String roomId;
        public String roomName;
        public String image;
        public int owned;
        public int status;
        public String nodeType;
        public String bindTime;
        public String gatewayId;
        public List<ETSL.stateTimeEntry> stateTimes;

        // 构造
        public deviceEntry(){
            this.iotId = "";
            this.nickName = "";
            this.deviceName = "";
            this.productKey = "";
            this.roomId = "";
            this.roomName = "";
            this.owned = 0;
            this.status = Constant.CONNECTION_STATUS_UNABLED;
            this.nodeType = "DEVICE";
            this.bindTime = "";
            this.stateTimes = new ArrayList<ETSL.stateTimeEntry>();
            this.image = "";
            this.gatewayId = "";
        }

        // 处理状态时间
        public void processStateTime(Context context, String propertyName, String propertyValue, long timeStamp){
            // 电池电量不处理
            if(propertyName.equalsIgnoreCase(CTSL.P_P_BatteryPercentage)){
                return;
            }

            // 进行属性值代码映射处理
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(context, this.productKey, propertyName, propertyValue);
            if(stateEntry == null){
                return;
            }

            // 如果是网关,则只处理布防模式
            if(this.productKey.equalsIgnoreCase(CTSL.PK_GATEWAY) && !propertyName.equalsIgnoreCase(CTSL.GW_P_ArmMode)){
                return;
            }

            // 时间处理
            String time = Utility.timeStampToHMString(timeStamp);

            ETSL.stateTimeEntry stateTimeEntry = null;
            for(ETSL.stateTimeEntry entry : this.stateTimes){
                if(entry.name.equalsIgnoreCase(propertyName)){
                    stateTimeEntry = entry;
                }
            }

            if(stateTimeEntry == null){
                stateTimeEntry = new ETSL.stateTimeEntry(this.productKey, propertyName, stateEntry.value, time);
                this.stateTimes.add(stateTimeEntry);
            } else {
                stateTimeEntry.update(this.productKey, propertyName, stateEntry.value, time);
            }
        }
    }
}
