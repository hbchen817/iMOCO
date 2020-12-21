package com.rexense.imoco.presenter;

import android.content.Context;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ETSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-15 15:29
 * Description: 代码映射器
 */
public class CodeMapper {
    // 处理设备加入状态
    public static String processAddStatus(Context context, int status) {
        switch (status) {
            case Constant.ADD_STATUS_SUCCESS:
                return context.getString(R.string.add_status_success);
            case Constant.ADD_STATUS_OTHERBIND:
                return context.getString(R.string.add_status_otherbind);
            case Constant.ADD_STATUS_FAIL:
                return context.getString(R.string.add_status_fail);
            default:
                return context.getString(R.string.add_status_logout);
        }
    }

    // 处理设备连接状态
    public static String processConnectionStatus(Context context, int status) {
        switch (status) {
            case Constant.CONNECTION_STATUS_UNABLED:
                return context.getString(R.string.connection_status_unable);
            case Constant.CONNECTION_STATUS_ONLINE:
                return context.getString(R.string.connection_status_online);
            case Constant.CONNECTION_STATUS_OFFLINE:
                return context.getString(R.string.connection_status_offline);
            default:
                return context.getString(R.string.connection_status_prohibit);
        }
    }

    // 处理属性状态(非电池电量)
    public static ETSL.stateEntry processPropertyState(Context context, ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return null;
        }

        ETSL.stateEntry stateEntry = null;
        for (String key : propertyEntry.properties.keySet()) {
            if (!key.equals(CTSL.P_P_BatteryPercentage)) {
                stateEntry = processPropertyState(context, propertyEntry.productKey, key, propertyEntry.properties.get(key));
                if (stateEntry != null) {
                    break;
                }
            }
        }
        return stateEntry;
    }

    // 处理属性状态
    public static ETSL.stateEntry processPropertyState(Context context, String productKey, String propertyName, String propertyValue) {
        if (productKey == null || propertyName == null || propertyValue == null || propertyValue.length() == 0) {
            return null;
        }

        // 电池电量处理
        if (propertyName.equals(CTSL.P_P_BatteryPercentage)) {
            return new ETSL.stateEntry(context.getString(R.string.detailsensor_power), propertyName, propertyValue + "%", propertyValue);
        }

        String mapName = "", mapValue = "";
        switch (productKey) {
            case CTSL.PK_GATEWAY:
                // 网关状态
                if (propertyName.equals(CTSL.GW_P_ArmMode)) {
                    mapName = context.getString(R.string.moregateway_armmode);
                    mapValue = context.getString(R.string.moregateway_armmode_cancel);
                    if (propertyValue.equals(CTSL.GW_P_ArmMode_deploy)) {
                        mapValue = context.getString(R.string.moregateway_armmode_deploy);
                    }
                } else if (propertyName.equals(CTSL.GW_P_AlarmSoundID)) {
                    mapName = context.getString(R.string.moregateway_alarmbellid);
                    mapValue = propertyValue;
                } else if (propertyName.equals(CTSL.GW_P_AlarmSoundVolume)) {
                    mapName = context.getString(R.string.moregateway_alarmvolume);
                    mapValue = propertyValue + "%";
                } else if (propertyName.equals(CTSL.GW_P_DoorBellSoundVolume)) {
                    mapName = context.getString(R.string.moregateway_bellvolume);
                    mapValue = propertyValue + "%";
                } else if (propertyName.equals(CTSL.GW_P_DoorBellSoundID)) {
                    mapName = context.getString(R.string.moregateway_bellmusicid);
                    mapValue = propertyValue;
                }
                break;
            case CTSL.PK_ONEWAYSWITCH:
                // 一路开关状态
                if (propertyName.equals(CTSL.OWS_P_PowerSwitch_1)) {
                    mapName = context.getString(R.string.oneswitch_state);
                    mapValue = context.getString(R.string.oneswitch_state_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.oneswitch_state_on);
                    }
                }
                break;
            case CTSL.PK_TWOWAYSWITCH:
                // 两路开关状态
                if (propertyName.equals(CTSL.TWS_P_PowerSwitch_1)) {
                    mapName = context.getString(R.string.twoswitch_state_1);
                    mapValue = context.getString(R.string.twoswitch_state_1_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.twoswitch_state_1_on);
                    }
                } else if (propertyName.equals(CTSL.TWS_P_PowerSwitch_2)) {
                    mapName = context.getString(R.string.twoswitch_state_2);
                    mapValue = context.getString(R.string.twoswitch_state_2_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.twoswitch_state_2_on);
                    }
                }
                break;
            case CTSL.PK_FOURWAYSWITCH:
                // 四路开关状态
                if (propertyName.equals(CTSL.FWS_P_PowerSwitch_1)) {
                    mapName = context.getString(R.string.twoswitch_state_1);
                    mapValue = context.getString(R.string.twoswitch_state_1_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.twoswitch_state_1_on);
                    }
                } else if (propertyName.equals(CTSL.FWS_P_PowerSwitch_2)) {
                    mapName = context.getString(R.string.twoswitch_state_2);
                    mapValue = context.getString(R.string.twoswitch_state_2_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.twoswitch_state_2_on);
                    }
                } else if (propertyName.equals(CTSL.FWS_P_PowerSwitch_3)) {
                    mapName = context.getString(R.string.fourswitch_state_3);
                    mapValue = context.getString(R.string.fourswitch_state_3_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.fourswitch_state_3_on);
                    }
                } else if (propertyName.equals(CTSL.FWS_P_PowerSwitch_4)) {
                    mapName = context.getString(R.string.fourswitch_state_4);
                    mapValue = context.getString(R.string.fourswitch_state_4_off);
                    if (propertyValue.equals(CTSL.S_P_PowerSwitch_On)) {
                        mapValue = context.getString(R.string.fourswitch_state_4_on);
                    }
                }
                break;
            case CTSL.PK_DOORSENSOR:
                // 处理门磁传感器状态
                if (propertyName.equals(CTSL.DS_P_ContactState)) {
                    mapName = context.getString(R.string.sensorstate_contactname);
                    mapValue = context.getString(R.string.sensorstate_contactclose);
                    if (propertyValue.equals(CTSL.DS_P_ContactState_Open)) {
                        mapValue = context.getString(R.string.sensorstate_contactopen);
                    }
                }
                break;
            case CTSL.PK_GASSENSOR:
                // 处理燃气传感器状态
                if (propertyName.equals(CTSL.GS_P_GasSensorState)) {
                    mapName = context.getString(R.string.sensorstate_gasname);
                    mapValue = context.getString(R.string.sensorstate_gasnonhas);
                    if (propertyValue.equals(CTSL.GS_P_GasSensorState_Has)) {
                        mapValue = context.getString(R.string.sensorstate_gashas);
                    }
                }
                break;
            case CTSL.PK_SMOKESENSOR:
                // 处理烟雾传感器状态
                if (propertyName.equals(CTSL.SS_P_SmokeSensorState)) {
                    mapName = context.getString(R.string.sensorstate_smokename);
                    mapValue = context.getString(R.string.sensorstate_smokenonhas);
                    if (propertyValue.equals(CTSL.SS_P_SmokeSensorState_Has)) {
                        mapValue = context.getString(R.string.sensorstate_smokehas);
                    }
                }
                break;
            case CTSL.PK_WATERSENSOR:
                // 处理水浸传感器状态
                if (propertyName.equals(CTSL.WS_P_WaterSensorState)) {
                    mapName = context.getString(R.string.sensorstate_watername);
                    mapValue = context.getString(R.string.sensorstate_waternonhas);
                    if (propertyValue.equals(CTSL.WS_P_WaterSensorState_Has)) {
                        mapValue = context.getString(R.string.sensorstate_waterhas);
                    }
                }
                break;
            case CTSL.PK_PIRSENSOR:
                // 处理人体热释放传感器状态
                if (propertyName.equals(CTSL.PIR_P_MotionAlarmState)) {
                    mapName = context.getString(R.string.sensorstate_motionname);
                    mapValue = context.getString(R.string.sensorstate_motionnonhas);
                    if (propertyValue.equals(CTSL.PIR_P_MotionAlarmState_Has)) {
                        mapValue = context.getString(R.string.sensorstate_motionhas);
                    }
                }
                break;
            case CTSL.PK_LIGHT:
                //调光调色面板
                if (propertyName.equals(CTSL.LIGHT_P_BRIGHTNESS)) {
                    mapName = context.getString(R.string.lightness);
                    mapValue = propertyValue;
                } else if (propertyName.equals(CTSL.LIGHT_P_COLOR_TEMPERATURE)) {
                    mapName = context.getString(R.string.color_temperature);
                    mapValue = propertyValue;
                }
                break;
            case CTSL.PK_TEMHUMSENSOR:
                // 处理温湿度状态
                if (propertyName.equals(CTSL.THS_P_CurrentTemperature)) {
                    mapName = context.getString(R.string.detailsensor_temperature);
                    double d = Double.valueOf(propertyValue);
                    mapValue = String.format("%.2f℃", d);
                } else if (propertyName.equals(CTSL.THS_P_CurrentHumidity)) {
                    mapName = context.getString(R.string.detailsensor_humidity);
                    mapValue = propertyValue + "%";
                }
                break;
            case CTSL.PK_REMOTECONTRILBUTTON:
                // 处理遥控按钮状态
                if (propertyName.equals(CTSL.RCB_P_EmergencyAlarm)) {
                    mapName = context.getString(R.string.sensorstate_buttonstate);
                    mapValue = context.getString(R.string.sensorstate_trigger);
                }
                break;
            default:
                break;
        }
        if (mapName != null && mapValue != null && mapName.length() > 0 && mapValue.length() > 0) {
            return new ETSL.stateEntry(mapName, propertyName, mapValue, propertyValue);
        } else {
            return null;
        }
    }

    // 获取属性触发状态
    public static List<ETSL.stateEntry> getPropertyTriggerState(Context context, String productKey, int sceneModelCode) {
        List<ETSL.stateEntry> list = new ArrayList<ETSL.stateEntry>();
        switch (sceneModelCode) {
            // 起夜开灯、红外布防报警处理
            case CScene.SMC_NIGHT_RISE_ON:
            case CScene.SMC_PIR_DEPLOY_ALARM:
                if (productKey.equalsIgnoreCase(CTSL.PK_PIRSENSOR)) {
                    // 处理人体热释放传感器状态
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.sensorstate_motionname), CTSL.PIR_P_MotionAlarmState,
                            context.getString(R.string.sensorstate_motionhas), CTSL.PIR_P_MotionAlarmState_Has);
                    list.add(stateEntry);
                }
                break;
            // 无人关灯处理
            case CScene.SMC_UNMANNED_OFF:
                if (productKey.equalsIgnoreCase(CTSL.PK_PIRSENSOR)) {
                    // 处理人体热释放传感器状态
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.sensorstate_motionname), CTSL.PIR_P_MotionAlarmState,
                            context.getString(R.string.sensorstate_motionnonhas), CTSL.PIR_P_MotionAlarmState_NoHas);
                    list.add(stateEntry);
                }
                break;
            // 报警开灯处理
            case CScene.SMC_ALARM_ON:
                ETSL.stateEntry stateEntry_alarm = null;
                if (productKey.equalsIgnoreCase(CTSL.PK_SMOKESENSOR)) {
                    // 处理烟感传感器状态
                    stateEntry_alarm = new ETSL.stateEntry(context.getString(R.string.sensorstate_smokename), CTSL.SS_P_SmokeSensorState,
                            context.getString(R.string.sensorstate_smokehas), CTSL.SS_P_SmokeSensorState_Has);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_WATERSENSOR)) {
                    // 处理水浸传感器状态
                    stateEntry_alarm = new ETSL.stateEntry(context.getString(R.string.sensorstate_watername), CTSL.WS_P_WaterSensorState,
                            context.getString(R.string.sensorstate_waterhas), CTSL.WS_P_WaterSensorState_Has);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_GASSENSOR)) {
                    // 处理燃气传感器状态
                    stateEntry_alarm = new ETSL.stateEntry(context.getString(R.string.sensorstate_gasname), CTSL.GS_P_GasSensorState,
                            context.getString(R.string.sensorstate_gashas), CTSL.GS_P_GasSensorState_Has);
                }
                if (stateEntry_alarm != null) {
                    list.add(stateEntry_alarm);
                }
                break;
            // 遥控开灯、门铃播报、报警播报处理
            case CScene.SMC_REMOTE_CONTROL_ON:
            case CScene.SMC_BELL_PLAY:
            case CScene.SMC_ALARM_PLAY:
                if (productKey.equalsIgnoreCase(CTSL.PK_REMOTECONTRILBUTTON)) {
                    // 处理遥控按钮状态
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.sensorstate_buttonstate), CTSL.RCB_P_EmergencyAlarm,
                            context.getString(R.string.sensorstate_trigger), CTSL.RCB_P_EmergencyAlarm_Trigger);
                    list.add(stateEntry);
                }
                break;
            // 开门亮灯、门磁布防报警处理
            case CScene.SMC_OPEN_DOOR_ON:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                if (productKey.equalsIgnoreCase(CTSL.PK_DOORSENSOR)) {
                    // 处理门磁传感器状态
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.sensorstate_contactname), CTSL.DS_P_ContactState,
                            context.getString(R.string.sensorstate_contactopen), CTSL.DS_P_ContactState_Open);
                    list.add(stateEntry);
                }
                break;
            default:
                break;
        }
        return list;
    }

    // 获取属性条件状态
    public static List<ETSL.stateEntry> getPropertyConditionState(Context context, String productKey, int sceneModelCode) {
        List<ETSL.stateEntry> list = new ArrayList<ETSL.stateEntry>();
        switch (sceneModelCode) {
            // 红外布防报警、门磁布防报警处理
            case CScene.SMC_PIR_DEPLOY_ALARM:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关布防状态
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.gateway_armmode), CTSL.GW_P_ArmMode,
                            context.getString(R.string.gateway_armmode_deploy), CTSL.GW_P_ArmMode_deploy);
                    list.add(stateEntry);
                }
                break;
            default:
                break;
        }
        return list;
    }

    // 获取属性响应状态
    public static List<ETSL.stateEntry> getPropertyResponseState(Context context, String productKey, int sceneModelCode) {
        List<ETSL.stateEntry> list = new ArrayList<ETSL.stateEntry>();
        switch (sceneModelCode) {
            // 起夜开灯、报警开灯、遥控开灯、开门亮灯处理
            case CScene.SMC_NIGHT_RISE_ON:
            case CScene.SMC_ALARM_ON:
            case CScene.SMC_REMOTE_CONTROL_ON:
            case CScene.SMC_OPEN_DOOR_ON:
                if (productKey.equalsIgnoreCase(CTSL.PK_ONEWAYSWITCH)) {
                    // 处理一键单火开关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.oneswitch_state), CTSL.OWS_P_PowerSwitch_1,
                            context.getString(R.string.oneswitch_state_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_TWOWAYSWITCH)) {
                    // 处理两键单火开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1), CTSL.TWS_P_PowerSwitch_1,
                            context.getString(R.string.twoswitch_state_1_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2), CTSL.TWS_P_PowerSwitch_2,
                            context.getString(R.string.twoswitch_state_2_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_FOURWAYSWITCH)) {
                    // 处理四键开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1),
                            CTSL.FWS_P_PowerSwitch_1, context.getString(R.string.twoswitch_state_1_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2),
                            CTSL.FWS_P_PowerSwitch_2, context.getString(R.string.twoswitch_state_2_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry3 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_3),
                            CTSL.FWS_P_PowerSwitch_3, context.getString(R.string.fourswitch_state_3_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry4 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_4),
                            CTSL.FWS_P_PowerSwitch_4, context.getString(R.string.fourswitch_state_4_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                    list.add(stateEntry3);
                    list.add(stateEntry4);
                }
                break;
            // 无人关灯处理
            case CScene.SMC_UNMANNED_OFF:
                if (productKey.equalsIgnoreCase(CTSL.PK_ONEWAYSWITCH)) {
                    // 处理一键单火开关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.oneswitch_state), CTSL.OWS_P_PowerSwitch_1,
                            context.getString(R.string.oneswitch_state_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_TWOWAYSWITCH)) {
                    // 处理两键单火开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1), CTSL.TWS_P_PowerSwitch_1,
                            context.getString(R.string.twoswitch_state_1_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2), CTSL.TWS_P_PowerSwitch_2,
                            context.getString(R.string.twoswitch_state_2_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_FOURWAYSWITCH)) {
                    // 处理四键开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1),
                            CTSL.FWS_P_PowerSwitch_1, context.getString(R.string.twoswitch_state_1_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2),
                            CTSL.FWS_P_PowerSwitch_2, context.getString(R.string.twoswitch_state_2_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry3 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_3),
                            CTSL.FWS_P_PowerSwitch_3, context.getString(R.string.fourswitch_state_3_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry4 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_4),
                            CTSL.FWS_P_PowerSwitch_4, context.getString(R.string.fourswitch_state_4_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                    list.add(stateEntry3);
                    list.add(stateEntry4);
                }
                break;
            // 回家模式处理
            case CScene.SMC_GO_HOME_PATTERN:
                if (productKey.equalsIgnoreCase(CTSL.PK_ONEWAYSWITCH)) {
                    // 处理一键单火开关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.oneswitch_state), CTSL.OWS_P_PowerSwitch_1,
                            context.getString(R.string.oneswitch_state_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_TWOWAYSWITCH)) {
                    // 处理两键单火开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1), CTSL.TWS_P_PowerSwitch_1,
                            context.getString(R.string.twoswitch_state_1_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2), CTSL.TWS_P_PowerSwitch_2,
                            context.getString(R.string.twoswitch_state_2_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_FOURWAYSWITCH)) {
                    // 处理四键开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1), CTSL.FWS_P_PowerSwitch_1,
                            context.getString(R.string.twoswitch_state_1_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2), CTSL.FWS_P_PowerSwitch_2,
                            context.getString(R.string.twoswitch_state_2_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry3 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_3), CTSL.FWS_P_PowerSwitch_3,
                            context.getString(R.string.fourswitch_state_3_on), CTSL.S_P_PowerSwitch_On);
                    ETSL.stateEntry stateEntry4 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_4), CTSL.FWS_P_PowerSwitch_4,
                            context.getString(R.string.fourswitch_state_4_on), CTSL.S_P_PowerSwitch_On);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                    list.add(stateEntry3);
                    list.add(stateEntry4);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.gateway_armmode), CTSL.GW_P_ArmMode,
                            context.getString(R.string.gateway_armmode_disarm), CTSL.GW_P_ArmMode_disarm);
                    list.add(stateEntry);
                }
                break;
            // 离家模式、睡觉模式处理
            case CScene.SMC_LEAVE_HOME_PATTERN:
            case CScene.SMC_SLEEP_PATTERN:
                if (productKey.equalsIgnoreCase(CTSL.PK_ONEWAYSWITCH)) {
                    // 处理一键单火开关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.oneswitch_state), CTSL.OWS_P_PowerSwitch_1,
                            context.getString(R.string.oneswitch_state_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_TWOWAYSWITCH)) {
                    // 处理两键单火开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1), CTSL.TWS_P_PowerSwitch_1,
                            context.getString(R.string.twoswitch_state_1_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2), CTSL.TWS_P_PowerSwitch_2,
                            context.getString(R.string.twoswitch_state_2_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_FOURWAYSWITCH)) {
                    // 处理四键开关
                    ETSL.stateEntry stateEntry1 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_1),
                            CTSL.FWS_P_PowerSwitch_1, context.getString(R.string.twoswitch_state_1_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry2 = new ETSL.stateEntry(context.getString(R.string.twoswitch_state_2),
                            CTSL.FWS_P_PowerSwitch_2, context.getString(R.string.twoswitch_state_2_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry3 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_3),
                            CTSL.FWS_P_PowerSwitch_3, context.getString(R.string.fourswitch_state_3_off), CTSL.S_P_PowerSwitch_Off);
                    ETSL.stateEntry stateEntry4 = new ETSL.stateEntry(context.getString(R.string.fourswitch_state_4),
                            CTSL.FWS_P_PowerSwitch_4, context.getString(R.string.fourswitch_state_4_off), CTSL.S_P_PowerSwitch_Off);
                    list.add(stateEntry1);
                    list.add(stateEntry2);
                    list.add(stateEntry3);
                    list.add(stateEntry4);
                } else if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.gateway_armmode), CTSL.GW_P_ArmMode,
                            context.getString(R.string.gateway_armmode_deploy), CTSL.GW_P_ArmMode_deploy);
                    list.add(stateEntry);
                }
                break;
            // 起床模式处理
            case CScene.SMC_GETUP_PATTERN:
                if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关
                    ETSL.stateEntry stateEntry = new ETSL.stateEntry(context.getString(R.string.gateway_armmode), CTSL.GW_P_ArmMode,
                            context.getString(R.string.gateway_armmode_disarm), CTSL.GW_P_ArmMode_disarm);
                    list.add(stateEntry);
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 获取服务响应动作
    public static List<ETSL.serviceEntry> getServiceResponseAction(Context context, String productKey, int sceneModelCode) {
        List<ETSL.serviceEntry> list = new ArrayList<ETSL.serviceEntry>();
        switch (sceneModelCode) {
            // 门铃播报处理
            case CScene.SMC_BELL_PLAY:
                if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关
                    ETSL.serviceEntry serviceEntry = new ETSL.serviceEntry(context.getString(R.string.gateway_service_invokevoice1), CTSL.GW_S_InvokeMode);
                    serviceEntry.addArg(context.getString(R.string.gateway_service_invokevoice1),
                            CTSL.GW_SA_InvokeMode_Voice, context.getString(R.string.gateway_service_invokevoice1), CTSL.GW_SA_InvokeMode_Voice1);
                    list.add(serviceEntry);
                }
                break;
            // 报警播报、门磁布防报警、红外布防报警处理
            case CScene.SMC_ALARM_PLAY:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
            case CScene.SMC_PIR_DEPLOY_ALARM:
                if (productKey.equalsIgnoreCase(CTSL.PK_GATEWAY)) {
                    // 处理网关
                    ETSL.serviceEntry serviceEntry = new ETSL.serviceEntry(context.getString(R.string.gateway_service_invokevoice2), CTSL.GW_S_InvokeMode);
                    serviceEntry.addArg(context.getString(R.string.gateway_service_invokevoice2),
                            CTSL.GW_SA_InvokeMode_Voice, context.getString(R.string.gateway_service_invokevoice2), CTSL.GW_SA_InvokeMode_Voice2);
                    list.add(serviceEntry);
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 处理事件
    public static ETSL.eventEntry processEvent(Context context, String productKey, String eventCode, String eventBody) {
        if (eventCode == null) {
            return null;
        }

        String mapName = "", mapValue = "";
        switch (productKey) {
            case CTSL.PK_DOORSENSOR:
                // 处理门磁传感器事件
                if (eventCode.equals(CTSL.P_E_ProtectionAlarm)) {
                    mapName = context.getString(R.string.sensorstate_protectionalarm);
                    mapValue = context.getString(R.string.sensorstate_protectionalarm);
                } else if (eventCode.equals(CTSL.P_E_TamperAlarm)) {
                    mapName = context.getString(R.string.sensorstate_tamperalarm);
                    mapValue = context.getString(R.string.sensorstate_tamperalarm);
                }
                break;
            case CTSL.PK_SMOKESENSOR:
                // 处理烟雾传感器事件
                if (eventCode.equals(CTSL.P_E_TamperAlarm)) {
                    mapName = context.getString(R.string.sensorstate_tamperalarm);
                    mapValue = context.getString(R.string.sensorstate_tamperalarm);
                }
                break;
            case CTSL.PK_PIRSENSOR:
                // 处理人体热释放传感器事件
                if (eventCode.equals(CTSL.P_E_ProtectionAlarm)) {
                    mapName = context.getString(R.string.sensorstate_protectionalarm);
                    mapValue = context.getString(R.string.sensorstate_protectionalarm);
                }
                break;
            default:
                break;
        }
        if (mapName != null && mapValue != null && mapName.length() > 0 && mapValue.length() > 0) {
            return new ETSL.eventEntry(mapName, mapValue);
        } else {
            return null;
        }
    }
}
