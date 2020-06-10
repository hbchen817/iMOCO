package com.rexense.imoco.contract;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备物描述语言常量
 */
public class CTSL {
    // 定义设备ProductKey常量
    public static final String PK_GATEWAY                   = "a1GuwJkxdQx";
    public static final String PK_ONEWAYSWITCH              = "a1X2BnFW2fx";
    public static final String PK_TWOWAYSWITCH              = "a1C9G2We8Da";
    public static final String PK_GASSENSOR                 = "a1Kwz77cZAu";
    public static final String PK_SMOKESENSOR               = "a1pSa8eunPc";
    public static final String PK_TEMHUMSENSOR              = "a1TG5jEuQCN";
    public static final String PK_WATERSENSOR               = "a1aq6sh5Txh";
    public static final String PK_DOORSENSOR                = "a1q32AasDLg";
    public static final String PK_PIRSENSOR                 = "a1CzbnRNzCR";
    public static final String PK_REMOTECONTRILBUTTON       = "a1G306IUVoa";

    // 定义属性类型
    public static enum PTYPE
    {
        t_text,
        t_int32,
        t_double,
        t_bool,
        t_enum
    };

    // 定义公共属性常量
    public static final String P_P_BatteryPercentage        = "BatteryPercentage";

    // 定义网关属性常量
    public static final String GW_P_ArmMode                 = "ArmMode";
    public static final String GW_P_DoorBellSoundID         = "DoorBellSoundID";
    public static final String GW_P_AlarmSoundVolume        = "AlarmSoundVolume";
    public static final String GW_P_DoorBellSoundVolume     = "DoorBellSoundVolume";
    public static final String GW_P_AlarmSoundID            = "AlarmSoundID";
    public static final Map<String, PTYPE> GW_Properties    = new HashMap<String, PTYPE>(){
        {
            put(GW_P_DoorBellSoundID, PTYPE.t_int32);
            put(GW_P_AlarmSoundVolume, PTYPE.t_int32);
            put(GW_P_DoorBellSoundVolume, PTYPE.t_int32);
            put(GW_P_AlarmSoundID, PTYPE.t_int32);
            put(GW_P_ArmMode, PTYPE.t_bool);
        }
    };
    public static final String GW_P_ArmMode_deploy          = "1";
    public static final String GW_P_ArmMode_disarm          = "0";
    // 定义网关服务常量
    public static final String GW_S_InvokeMode              = "InvokeMode";
    public static final String GW_SA_InvokeMode_Voice       = "InvokeVoice";
    public static final String GW_SA_InvokeMode_Voice1      = "0";
    public static final String GW_SA_InvokeMode_Voice2      = "1";

    // 定义一键开关属性常量
    public static final String OWS_P_PowerSwitch_1          = "PowerSwitch_1";
    public static final Map<String, PTYPE> OWS_Properties   = new HashMap<String, PTYPE>(){
        {
            put(OWS_P_PowerSwitch_1, PTYPE.t_bool);
        }
    };
    public static String S_P_PowerSwitch_On                 = "1";
    public static String S_P_PowerSwitch_Off                = "0";

    // 定义两键开关属性常量
    public static final String TWS_P_PowerSwitch_1          = "PowerSwitch_1";
    public static final String TWS_P_PowerSwitch_2          = "PowerSwitch_2";
    public static final Map<String, PTYPE> TWS_Properties   = new HashMap<String, PTYPE>(){
        {
            put(TWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P_PowerSwitch_2, PTYPE.t_bool);
        }
    };

    // 定义遥控按钮属性常量
    public static final String RCB_P_EmergencyAlarm         = "EmergencyAlarm";
    public static final String RCB_P_BatteryPercentage      = "BatteryPercentage";
    public static final Map<String, PTYPE> RCB_Properties   = new HashMap<String, PTYPE>(){
        {
            put(RCB_P_EmergencyAlarm, PTYPE.t_enum);
            put(RCB_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String RCB_P_EmergencyAlarm_Trigger = "1";

    // 定义燃气传感器属性常量
    public static final String GS_P_GasSensorState          = "GasSensorState";
    public static final Map<String, PTYPE> GS_Properties    = new HashMap<String, PTYPE>(){
        {
            put(GS_P_GasSensorState, PTYPE.t_enum);
        }
    };
    public static final String GS_P_GasSensorState_Has      = "1";

    // 定义门磁传感器属性常量
    public static final String DS_P_ContactState            = "ContactState";
    public static final Map<String, PTYPE> DS_Properties    = new HashMap<String, PTYPE>(){
        {
            put(DS_P_ContactState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String DS_P_ContactState_Open       = "1";

    // 定义烟雾传感器属性常量
    public static final String SS_P_SmokeSensorState        = "SmokeSensorState";
    public static final Map<String, PTYPE> SS_Properties    = new HashMap<String, PTYPE>(){
        {
            put(SS_P_SmokeSensorState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String SS_P_SmokeSensorState_Has    = "1";

    // 定义温湿度传感器属性常量
    public static final String THS_P_CurrentTemperature     = "CurrentTemperature";
    public static final String THS_P_CurrentHumidity        = "CurrentHumidity";
    public static final Map<String, PTYPE> THS_Properties   = new HashMap<String, PTYPE>(){
        {
            put(THS_P_CurrentTemperature, PTYPE.t_double);
            put(THS_P_CurrentHumidity, PTYPE.t_double);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };

    // 定义水浸传感器属性常量
    public static final String WS_P_WaterSensorState        = "WaterSensorState";
    public static final Map<String, PTYPE> WS_Properties    = new HashMap<String, PTYPE>(){
        {
            put(WS_P_WaterSensorState, PTYPE.t_enum);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String WS_P_WaterSensorState_Has    = "1";

    // 定义人体红外感应器属性常量
    public static final String PIR_P_MotionAlarmState       = "MotionAlarmState";
    public static final Map<String, PTYPE> PIR_Properties   = new HashMap<String, PTYPE>(){
        {
            put(PIR_P_MotionAlarmState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String PIR_P_MotionAlarmState_Has   = "1";
    public static final String PIR_P_MotionAlarmState_NoHas = "0";

    // 定义属性配置文件
    public static final Map<String, Map<String, PTYPE>> propertyProfile = new HashMap<String, Map<String, PTYPE>>() {
        {
            put(PK_GATEWAY, GW_Properties);
            put(PK_ONEWAYSWITCH, OWS_Properties);
            put(PK_TWOWAYSWITCH, TWS_Properties);
            put(PK_GASSENSOR, GS_Properties);
            put(PK_SMOKESENSOR, SS_Properties);
            put(PK_TEMHUMSENSOR, THS_Properties);
            put(PK_WATERSENSOR, WS_Properties);
            put(PK_DOORSENSOR, DS_Properties);
            put(PK_PIRSENSOR, PIR_Properties);
            put(PK_REMOTECONTRILBUTTON, RCB_Properties);
        }
    };

    // 定义防撬事件常量
    public static final String P_E_TamperAlarm              = "TamperAlarm";
    // 定义布防报警事件常量
    public static final String P_E_ProtectionAlarm          = "ProtectionAlarm";

    // 定义状态常量
    public static final int STATUS_ON                       = 1;
    public static final int STATUS_OFF                      = 0;

    // 定义控制类型
    public static enum ControlType {
        APIChanel,
        SDK
    }

    // 定义事件类型
    public static final String EVENTTYPE_INFO               = "info";
    public static final String EVENTTYPE_ALERT              = "alert";
    public static final String EVENTTYPE_ERROR              = "err";
}