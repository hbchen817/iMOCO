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
    public static final String PK_GATEWAY_RG4100            = "b17wpNLgyIe";
    public static final String PK_ONEWAYSWITCH              = "a1X2BnFW2fx";
    public static final String PK_TWOWAYSWITCH              = "a1C9G2We8Da";
    public static final String PK_GASSENSOR                 = "a1Kwz77cZAu";
    public static final String PK_SMOKESENSOR               = "a1pSa8eunPc";
    public static final String PK_TEMHUMSENSOR              = "a1TG5jEuQCN";
    public static final String PK_WATERSENSOR               = "a1aq6sh5Txh";
    public static final String PK_DOORSENSOR                = "a1q32AasDLg";
    public static final String PK_PIRSENSOR                 = "a1CzbnRNzCR";
    public static final String PK_REMOTECONTRILBUTTON       = "a1G306IUVoa";
    public static final String PK_SMART_LOCK                = "a19ONFPQn0u";
    public static final String PK_SIX_TWO_SCENE_SWITCH      = "a1zf8jfGzTX";
    public static final String PK_SIX_SCENE_SWITCH          = "a1MLvTtRmWo";
    public static final String PK_TWO_SCENE_SWITCH          = "a14kD7IKWqp";
    public static final String PK_THREE_SCENE_SWITCH        = "a1S5xUUURz4";
    public static final String PK_FOUR_SCENE_SWITCH         = "a1uaG4drs1e";
    public static final String PK_LIGHT                     = "a1SdpzGgZCa";
    public static final String PK_ONE_SCENE_SWITCH          = "a1HurDIuRiW";

    public static final String PK_WSD_ELE                   = "a1DnAUOVHtX";//微仕达电机pk
    public static final String PK_CONTROL_LIGHT             = "a1SdpzGgZCa";//调光调色面板
    public static final String PK_BLACK_ONE_SWITCH          = "a1VK0kgweyU";
    public static final String PK_BLACK_TWO_SWITCH          = "a1Hi1QQEGPm";
    public static final String PK_BLACK_THREE_SWITCH        = "a1uAwpNGcI7";
    public static final String PK_WHITE_THREE_SWITCH        = "a1UpoJjIjCn";
    public static final String PK_BLACK_FOUR_SWITCH         = "a1lX2ZUOTzE";
    public static final String PK_BLACK_USB                 = "a1OLiwq8qSv";

    public static final String PK_RGB_COLOR_LIGHT_STRIP     = "a1yzxlzMyR0";// RGB彩色灯带
    public static final String PK_HSL_COLOR_LIGHT           = "a11jimE2TIi";// HSL彩色灯
    public static final String PK_HSL_COLOR_LIGHT_STRIP     = "a1yARva6fVG";// HSL彩色灯带
    public static final String PK_CONTROL_COLOR_TEMP_LIGHT  = "a1uXFxg1cAL";// 调光色温灯
    public static final String PK_CONTROL_COLOR_LIGHT       = "a1mSlpQen3w";// 调光灯
    public static final String PK_RGB_COLOR_LIGHT           = "a1MAzhopgCh";// RGB彩色灯
    public static final String PK_AIRCOMDITION_TWO          = "a1SlOjihHnP";// 空调二管制
    public static final String PK_AIRCOMDITION_FOUR         = "a1ayoCqQpxx";// 空调四管制

    public static final String PK_FOURWAYSWITCH             = "a1pOIRmMTGF";// 四键开关
    public static final String PK_FOURWAYSWITCH_2           = "a1q9trgD6NK";// 四键开关
    public static final String PK_FLOORHEATING001           = "a1R4ZpwDMM8";// 拉菲地暖(电机)
    public static final String PK_THREE_KEY_SWITCH          = "a1orzEATqCz";// 三键面板开关

    public static final String TEST_PK_ONEWAYWINDOWCURTAINS = "TEST_PK_ONEWAYWINDOWCURTAINS";

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
	
	//定义调光调色属性常量
    public static final String LIGHT_P_POWER                = "PowerSwitch";
    public static final String LIGHT_P_BRIGHTNESS           = "brightness";
    public static final String LIGHT_P_COLOR_TEMPERATURE    = "colorTemperature";
    public static final Map<String, PTYPE> LIGHT_Properties = new HashMap<String, PTYPE>(){
        {
            put(LIGHT_P_POWER, PTYPE.t_int32);
            put(LIGHT_P_BRIGHTNESS, PTYPE.t_int32);
            put(LIGHT_P_COLOR_TEMPERATURE, PTYPE.t_int32);
        }
    };

    //定义六键四开二场景属性常量
    public static final String SIX_SCENE_SWITCH_P_POWER_1    = "PowerSwitch_1";
    public static final String SIX_SCENE_SWITCH_P_POWER_2    = "PowerSwitch_2";
    public static final String SIX_SCENE_SWITCH_P_POWER_3    = "PowerSwitch_3";
    public static final String SIX_SCENE_SWITCH_P_POWER_4    = "PowerSwitch_4";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_1   = "5";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_2   = "6";
    public static final Map<String, PTYPE> SIX_SCENE_SWITCH_Properties = new HashMap<String, PTYPE>(){
        {
            put(SIX_SCENE_SWITCH_P_POWER_1, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_2, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_3, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_4, PTYPE.t_int32);
        }
    };

    public static final String SCENE_SWITCH_KEY_CODE_1       = "1";
    public static final String SCENE_SWITCH_KEY_CODE_2       = "2";
    public static final String SCENE_SWITCH_KEY_CODE_3       = "3";
    public static final String SCENE_SWITCH_KEY_CODE_4       = "4";

    // 定义四键开关属性常量
    public static final String FWS_P_PowerSwitch_1          = "PowerSwitch_1";
    public static final String FWS_P_PowerSwitch_2          = "PowerSwitch_2";
    public static final String FWS_P_PowerSwitch_3          = "PowerSwitch_3";
    public static final String FWS_P_PowerSwitch_4          = "PowerSwitch_4";
    public static final Map<String, PTYPE> FWS_Properties   = new HashMap<String, PTYPE>(){
        {
            put(FWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_2, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_3, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_4, PTYPE.t_bool);
        }
    };

    // 定义空调二管制属性常量
    public static final String AIRC_T_PowerSwitch              = "PowerSwitch";
    public static final String AIRC_T_CurrentHumidity          = "CurrentHumidity";
    public static final String AIRC_T_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_T_Properties   = new HashMap<String, PTYPE>(){
        {
            put(AIRC_T_PowerSwitch, PTYPE.t_bool);
            put(AIRC_T_CurrentHumidity, PTYPE.t_double);
            put(AIRC_T_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义空调四管制属性常量
    public static final String AIRC_F_PowerSwitch              = "PowerSwitch";
    public static final String AIRC_F_CurrentHumidity          = "CurrentHumidity";
    public static final String AIRC_F_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_F_Properties   = new HashMap<String, PTYPE>(){
        {
            put(AIRC_F_PowerSwitch, PTYPE.t_bool);
            put(AIRC_F_CurrentHumidity, PTYPE.t_double);
            put(AIRC_F_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义拉菲地暖(电机)属性常量
    public static final String FLOORH_001_PowerSwitch              = "PowerSwitch";
    public static final String FLOORH_001_CurrentHumidity          = "CurrentHumidity";
    public static final String FLOORH_001_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> FLOORH_001_Properties   = new HashMap<String, PTYPE>(){
        {
            put(FLOORH_001_PowerSwitch, PTYPE.t_bool);
            put(FLOORH_001_CurrentHumidity, PTYPE.t_double);
            put(FLOORH_001_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义三键开关属性常量
    public static final String TWS_P3_PowerSwitch_1          = "PowerSwitch_1";
    public static final String TWS_P3_PowerSwitch_2          = "PowerSwitch_2";
    public static final String TWS_P3_PowerSwitch_3          = "PowerSwitch_3";
    public static final Map<String, PTYPE> TWS_3_Properties   = new HashMap<String, PTYPE>(){
        {
            put(TWS_P3_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_2, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_3, PTYPE.t_bool);
        }
    };

    //定义调光调色属性常量
    public static final String LIGHT_P_POWER                = "PowerSwitch";
    public static final String LIGHT_P_BRIGHTNESS           = "brightness";
    public static final String LIGHT_P_COLOR_TEMPERATURE    = "colorTemperature";
    public static final Map<String, PTYPE> LIGHT_Properties = new HashMap<String, PTYPE>(){
        {
            put(LIGHT_P_POWER, PTYPE.t_int32);
            put(LIGHT_P_BRIGHTNESS, PTYPE.t_int32);
            put(LIGHT_P_COLOR_TEMPERATURE, PTYPE.t_int32);
        }
    };

    //定义六键四开二场景属性常量
    public static final String SIX_SCENE_SWITCH_P_POWER_1    = "PowerSwitch_1";
    public static final String SIX_SCENE_SWITCH_P_POWER_2    = "PowerSwitch_2";
    public static final String SIX_SCENE_SWITCH_P_POWER_3    = "PowerSwitch_3";
    public static final String SIX_SCENE_SWITCH_P_POWER_4    = "PowerSwitch_4";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_1   = "5";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_2   = "6";
    public static final Map<String, PTYPE> SIX_SCENE_SWITCH_Properties = new HashMap<String, PTYPE>(){
        {
            put(SIX_SCENE_SWITCH_P_POWER_1, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_2, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_3, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_4, PTYPE.t_int32);
        }
    };

    public static final String SCENE_SWITCH_KEY_CODE_1       = "1";
    public static final String SCENE_SWITCH_KEY_CODE_2       = "2";
    public static final String SCENE_SWITCH_KEY_CODE_3       = "3";
    public static final String SCENE_SWITCH_KEY_CODE_4       = "4";

    // 定义四键开关属性常量
    public static final String FWS_P_PowerSwitch_1          = "PowerSwitch_1";
    public static final String FWS_P_PowerSwitch_2          = "PowerSwitch_2";
    public static final String FWS_P_PowerSwitch_3          = "PowerSwitch_3";
    public static final String FWS_P_PowerSwitch_4          = "PowerSwitch_4";
    public static final Map<String, PTYPE> FWS_Properties   = new HashMap<String, PTYPE>(){
        {
            put(FWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_2, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_3, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_4, PTYPE.t_bool);
        }
    };

    // 定义空调二管制属性常量
    public static final String AIRC_T_PowerSwitch              = "PowerSwitch";
    public static final String AIRC_T_CurrentHumidity          = "CurrentHumidity";
    public static final String AIRC_T_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_T_Properties   = new HashMap<String, PTYPE>(){
        {
            put(AIRC_T_PowerSwitch, PTYPE.t_bool);
            put(AIRC_T_CurrentHumidity, PTYPE.t_double);
            put(AIRC_T_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义空调四管制属性常量
    public static final String AIRC_F_PowerSwitch              = "PowerSwitch";
    public static final String AIRC_F_CurrentHumidity          = "CurrentHumidity";
    public static final String AIRC_F_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_F_Properties   = new HashMap<String, PTYPE>(){
        {
            put(AIRC_F_PowerSwitch, PTYPE.t_bool);
            put(AIRC_F_CurrentHumidity, PTYPE.t_double);
            put(AIRC_F_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义拉菲地暖(电机)属性常量
    public static final String FLOORH_001_PowerSwitch              = "PowerSwitch";
    public static final String FLOORH_001_CurrentHumidity          = "CurrentHumidity";
    public static final String FLOORH_001_TargetTemperature        = "TargetTemperature";
    public static final Map<String, PTYPE> FLOORH_001_Properties   = new HashMap<String, PTYPE>(){
        {
            put(FLOORH_001_PowerSwitch, PTYPE.t_bool);
            put(FLOORH_001_CurrentHumidity, PTYPE.t_double);
            put(FLOORH_001_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义三键开关属性常量
    public static final String TWS_P3_PowerSwitch_1          = "PowerSwitch_1";
    public static final String TWS_P3_PowerSwitch_2          = "PowerSwitch_2";
    public static final String TWS_P3_PowerSwitch_3          = "PowerSwitch_3";
    public static final Map<String, PTYPE> TWS_3_Properties   = new HashMap<String, PTYPE>(){
        {
            put(TWS_P3_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_2, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_3, PTYPE.t_bool);
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

    // 定义RGB彩色灯带属性常量
    public static final String RGBLS_brightness = "brightness";
    public static final String RGBLS_powerstate = "powerstate";
    public static final String RGBLS_LightType = "LightType";
    public static final String RGBLS_mode = "mode";
    public static final String RGBLS_colorTemperatureInKelvin = "colorTemperatureInKelvin";
    public static final String RGBLS_LightMode = "LightMode";
    public static final Map<String, PTYPE> RGBLS_Properties = new HashMap<String, PTYPE>() {
        {
            put(RGBLS_brightness, PTYPE.t_int32);
            put(RGBLS_powerstate, PTYPE.t_bool);
            put(RGBLS_LightType, PTYPE.t_enum);
            put(RGBLS_mode, PTYPE.t_enum);
            put(RGBLS_colorTemperatureInKelvin, PTYPE.t_int32);
            put(RGBLS_LightMode, PTYPE.t_enum);
        }
    };

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
			put(PK_LIGHT, LIGHT_Properties);
            put(PK_RGB_COLOR_LIGHT_STRIP, RGBLS_Properties);
            put(PK_FOURWAYSWITCH, FWS_Properties);
            put(PK_FOURWAYSWITCH_2, FWS_Properties);
            put(PK_SIX_TWO_SCENE_SWITCH, SIX_SCENE_SWITCH_Properties);
            put(PK_AIRCOMDITION_TWO, AIRC_T_Properties);
            put(PK_AIRCOMDITION_FOUR, AIRC_F_Properties);
            put(PK_FLOORHEATING001, FLOORH_001_Properties);
            put(PK_THREE_KEY_SWITCH, TWS_3_Properties);
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