package com.rexense.smart.model;

import android.content.Context;

import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.presenter.CodeMapper;
import com.rexense.smart.utility.Utility;

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
        public String mac;
        public String productKey;
        public String roomId;
        public String roomName;
        public String image;
        public int owned;// 设备和用户的关系，可取值：0（表示分享者），1（表示拥有者）。
        public int status;// 设备状态。0（表示未激活）；1（表示在线）；3（表示离线）；8（表示禁用）。
        public String nodeType;
        public String bindTime;
        public String gatewayId;
        public List<ETSL.stateTimeEntry> stateTimes;

        // 构造
        public deviceEntry() {
            this.iotId = "";
            this.nickName = "";
            this.deviceName = "";
            this.mac = "";
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
        public void processStateTime(Context context, String propertyName, String propertyValue, long timeStamp) {
            // 电池电量不处理
            if (propertyName.equalsIgnoreCase(CTSL.P_P_BatteryPercentage)) {
                return;
            }

            // 进行属性值代码映射处理
            ETSL.stateEntry stateEntry = CodeMapper.processPropertyState(context, this.productKey, propertyName, propertyValue);
            if (stateEntry == null) {
                return;
            }

            // 如果是网关,则只处理布防模式
            if (this.productKey.equalsIgnoreCase(CTSL.PK_GATEWAY) && !propertyName.equalsIgnoreCase(CTSL.GW_P_ArmMode)) {
                return;
            }

            // 时间处理
            String time = Utility.timeStampToHMString(timeStamp);

            ETSL.stateTimeEntry stateTimeEntry = null;
            for (ETSL.stateTimeEntry entry : this.stateTimes) {
                if (entry.name.equalsIgnoreCase(propertyName)) {
                    stateTimeEntry = entry;
                }
            }

            if (stateTimeEntry == null) {
                stateTimeEntry = new ETSL.stateTimeEntry(this.productKey, propertyName, stateEntry.value, time);
                this.stateTimes.add(stateTimeEntry);
            } else {
                stateTimeEntry.update(this.productKey, propertyName, stateEntry.value, time);
            }
        }
    }

    // 子网关实体
    public static class subGwEntry {
        private String mac;
        private String nickname;
        private String position;// 位置
        private String firmwareVersion;
        private String createTime;
        private String activateTime;// 激活时间
        private String image;
        private String state;// 0-未激活，1-已激活
        private String status = "-1";//1-在线 3-离线

        public subGwEntry() {
        }

        public String getActivateTime() {
            return activateTime;
        }

        public void setActivateTime(String activateTime) {
            this.activateTime = activateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
