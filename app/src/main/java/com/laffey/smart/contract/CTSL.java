package com.laffey.smart.contract;

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
    public static final String PK_ONEWAYSWITCH              = "a1EEhmdntAu";// a1X2BnFW2fx
    public static final String PK_TWOWAYSWITCH              = "a1pVHRVmHqD";// a1C9G2We8Da
    public static final String PK_GASSENSOR                 = "a1Av67v91qU";// a1Kwz77cZAu
    public static final String PK_SMOKESENSOR               = "a1CqMuzQngS";// a1pSa8eunPc
    public static final String PK_TEMHUMSENSOR              = "a1mg6JYZclJ";// a1TG5jEuQCN
    public static final String PK_WATERSENSOR               = "a12Eombgqx9";// a1aq6sh5Txh
    public static final String PK_DOORSENSOR                = "a1JEX9onhmy";// a1q32AasDLg
    public static final String PK_PIRSENSOR                 = "a1NiGilMJbc";// a1CzbnRNzCR
    public static final String PK_REMOTECONTRILBUTTON       = "a1G306IUVoa";
    public static final String PK_SMART_LOCK_A7             = "a19ONFPQn0u";//临时密码StartTime
    public static final String PK_SMART_LOCK                = "a1SlrD1NHyW";//临时密码StartDate
    public static final String PK_SIX_TWO_SCENE_SWITCH      = "a1q6m3k0GkW";// a1zf8jfGzTX
    public static final String PK_SIX_SCENE_SWITCH          = "a1MLvTtRmWo";
    public static final String PK_U_SIX_SCENE_SWITCH        = "a1KFQudxYT4";// 鸿雁U型场景开关
    public static final String PK_SIX_SCENE_SWITCH_YQSXB    = "a1mox25h8ec";// 粤奇胜肖邦六场景开关
    public static final String PK_TWO_SCENE_SWITCH          = "a14kD7IKWqp";
    public static final String PK_THREE_SCENE_SWITCH        = "a1S5xUUURz4";
    public static final String PK_FOUR_SCENE_SWITCH         = "a1uaG4drs1e";
    public static final String PK_LIGHT                     = "a1OlU25BrOu";// 调光调色面板
    public static final String PK_ONE_WAY_DIMMABLE_LIGHT    = "a1CybIIFZIr";// 单调光面板
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
    public static final String PK_CONTROL_COLOR_BROAD       = "a1OlU25BrOu";// 调光调色面板
    public static final String PK_RGB_COLOR_LIGHT           = "a1MAzhopgCh";// RGB彩色灯
    public static final String PK_AIRCOMDITION_TWO          = "a1SlOjihHnP";// 空调二管制
    public static final String PK_AIRCOMDITION_FOUR         = "a1ayoCqQpxx";// 空调四管制

    public static final String PK_FOURWAYSWITCH             = "a1pOIRmMTGF";// 四键开关
    public static final String PK_FOURWAYSWITCH_2           = "a1q9trgD6NK";// 四键开关
    public static final String PK_FLOORHEATING001           = "a1R4ZpwDMM8";// 拉菲地暖(电机)
    public static final String PK_THREE_KEY_SWITCH          = "a1orzEATqCz";// 三键面板开关

    public static final String PK_ANY_FOUR_SCENE_SWITCH     = "a1Qb4P7DHAv";// 随意贴四键场景开关
    public static final String PK_ANY_TWO_SCENE_SWITCH      = "a1WBTOxm1Z5";// 随意贴二键场景开关

    public static final String PK_ONE_KEY_SWITCH            = "a1EEhmdntAu";// 一键面板开关
    public static final String PK_TWO_KEY_SWITCH            = "a1pVHRVmHqD";// 两键面板开关

    public static final String PK_VRV_AC                    = "a1hGRYll4OE";// 拉斐VRV温控器LFWK001
    public static final String PK_FAU                       = "a1msNJjnfnQ";// 拉斐新风LFXF001
    public static final String PK_OUTLET                    = "a1x0DJE7Eqw";// 拉斐二三极插座LFS002

    public static final String TEST_PK_ONEWAYWINDOWCURTAINS = "a12uBMtiWKz";// 单路窗帘
    public static final String TEST_PK_TWOWAYWINDOWCURTAINS = "a1UnLiHBScD";// 双路窗帘

    public static final String TEST_PK_FULL_SCREEN_SWITCH   = "a1EnbCHcEHj";// 全面屏开关
    public static final String PK_MULTI_THREE_IN_ONE        = "a1FSqDW2oMb";// 三合一设备 a1FSqDW2oMb
    public static final String PK_MULTI_AC_AND_FH           = "a1VMREvnLBo";// 二合一设备（空调+地暖） a13Jtj64Qw8
    public static final String PK_MULTI_AC_AND_FA           = "a1RrcMQxHeu";// 二合一设备（空调+新风） a1uoYw7k9JY
    public static final String PK_MULTI_FH_AND_FA           = "a1Srgbtm3vO";// 二合一设备（空调+地暖） a1ROVRWwzQu
    public static final String PK_SYT_ONE_SCENE_SWITCH      = "a12xbYPkc8L";// 大一开随意贴场景开关
    public static final String PK_SYT_TWO_SCENE_SWITCH      = "a18mQdo6uiV";// 大二开随意贴场景开关
    public static final String PK_SYT_THREE_SCENE_SWITCH    = "a1iE33TE4Jh";// 大三开随意贴场景开关
    public static final String PK_SYT_FOUR_SCENE_SWITCH     = "a1hYWbhA7Uz";// 二开四随意贴场景开关
    public static final String PK_SYT_SIX_SCENE_SWITCH      = "a1vF8jUXxu0";// 三开六随意贴场景开关

    // 定义属性类型
    public static enum PTYPE {
        t_text,
        t_int32,
        t_double,
        t_bool,
        t_enum,
    }

    // 定义公共属性常量
    public static final String P_P_BatteryPercentage = "BatteryPercentage";

    // 定义网关属性常量
    public static final String GW_P_ArmMode                         = "ArmMode";
    public static final String GW_P_DoorBellSoundID                 = "DoorBellSoundID";
    public static final String GW_P_AlarmSoundVolume                = "AlarmSoundVolume";
    public static final String GW_P_DoorBellSoundVolume             = "DoorBellSoundVolume";
    public static final String GW_P_AlarmSoundID                    = "AlarmSoundID";
    public static final Map<String, PTYPE> GW_Properties            = new HashMap<String, PTYPE>() {
        {
            put(GW_P_DoorBellSoundID, PTYPE.t_int32);
            put(GW_P_AlarmSoundVolume, PTYPE.t_int32);
            put(GW_P_DoorBellSoundVolume, PTYPE.t_int32);
            put(GW_P_AlarmSoundID, PTYPE.t_int32);
            put(GW_P_ArmMode, PTYPE.t_bool);
        }
    };
    public static final String GW_P_ArmMode_deploy             = "1";
    public static final String GW_P_ArmMode_disarm             = "0";
    // 定义网关服务常量
    public static final String GW_S_InvokeMode                 = "InvokeMode";
    public static final String GW_SA_InvokeMode_Voice          = "InvokeVoice";
    public static final String GW_SA_InvokeMode_Voice1         = "0";
    public static final String GW_SA_InvokeMode_Voice2         = "1";

    //4100网关
    public static final String GW_4100_ZB_Band                 = "ZB_Band";//ZB带宽
    public static final String GW_4100_NETWORK_KEY             = "NETWORK_KEY";//网络密钥
    public static final String GW_4100_ZB_CO_MAC               = "ZB_CO_MAC";//ZB设备MAC地址
    public static final String GW_4100_EXT_PAN_ID              = "EXT_PAN_ID";//扩展网路标识
    public static final String GW_4100_ZB_Channel              = "ZB_Channel";//ZB通道
    public static final String GW_4100_ZB_PAN_ID               = "ZB_PAN_ID";//网路标识
    public static final String GW_4100_subGWList               = "subGWList";//网路标识
    public static final Map<String, PTYPE> GW_4100_Properties  = new HashMap<String, PTYPE>() {
        {
            put(GW_4100_ZB_Band, PTYPE.t_enum);
            put(GW_4100_NETWORK_KEY, PTYPE.t_text);
            put(GW_4100_ZB_CO_MAC, PTYPE.t_text);
            put(GW_4100_EXT_PAN_ID, PTYPE.t_text);
            put(GW_4100_ZB_Channel, PTYPE.t_int32);
            put(GW_4100_ZB_PAN_ID, PTYPE.t_text);
            put(GW_4100_subGWList, PTYPE.t_text);
        }
    };

    // 定义一键开关属性常量
    public static final String OWS_P_PowerSwitch_1             = "PowerSwitch_1";
    public static final String OWS_P_BackLightMode             = "BackLightMode";
    public static final Map<String, PTYPE> OWS_Properties      = new HashMap<String, PTYPE>() {
        {
            put(OWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(OWS_P_BackLightMode, PTYPE.t_enum);
        }
    };



    // 定义三开六场景开关属性常量
    public static final String PSS_BackLightMode             = "BackLightMode";
    public static final Map<String, PTYPE> PSS_Properties    = new HashMap<String, PTYPE>() {
        {
            put(PSS_BackLightMode, PTYPE.t_enum);
            put(BATTERY, PTYPE.t_int32);
        }
    };

    public static final String BATTERY                    = "BatteryPercentage";

    // 定义一场景开关属性常量
    public static final String POS_BackLight              = "BackLightMode";
    public static final Map<String, PTYPE> POS_Properties = new HashMap<String, PTYPE>() {
        {
            put(POS_BackLight, PTYPE.t_enum);
            put(BATTERY,PTYPE.t_int32);
        }
    };

    // 定义大三开场景开关属性常量
    public static final String PTS_BackLight              = "BackLightMode";
    public static final Map<String, PTYPE> PTS_Properties = new HashMap<String, PTYPE>() {
        {
            put(PTS_BackLight, PTYPE.t_enum);
            put(BATTERY, PTYPE.t_int32);
        }
    };

    // 定义大二开场景开关属性常量
    public static final String P2S_BackLight              = "BackLightMode";
    public static final Map<String, PTYPE> P2S_Properties = new HashMap<String, PTYPE>() {
        {
            put(P2S_BackLight, PTYPE.t_bool);
        }
    };

    // 定义二开四场景开关属性常量
    public static final String PFS_BackLight              = "BackLightMode";
    public static final Map<String, PTYPE> PFS_Properties = new HashMap<String, PTYPE>() {
        {
            put(PFS_BackLight, PTYPE.t_enum);
            put(BATTERY, PTYPE.t_int32);
        }
    };

    public static String S_P_PowerSwitch_On  = "1";
    public static String S_P_PowerSwitch_Off = "0";

    // 定义两键开关属性常量
    public static final String TWS_P_PowerSwitch_1             = "PowerSwitch_1";
    public static final String TWS_P_PowerSwitch_2             = "PowerSwitch_2";
    public static final String TWS_P_BackLightMode             = "BackLightMode";
    public static final Map<String, PTYPE> TWS_Properties      = new HashMap<String, PTYPE>() {
        {
            put(TWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P_PowerSwitch_2, PTYPE.t_bool);
            put(TWS_P_BackLightMode, PTYPE.t_enum);
        }
    };

    // 定义二三极插座属性常量
    public static final String PK_OUTLET_Backlight              = "BackLightMode";
    public static final Map<String, PTYPE> PK_OUTLET_Properties = new HashMap<String, PTYPE>() {
        {
            put(PK_OUTLET_Backlight, PTYPE.t_enum);
        }
    };

    //定义调光调色属性常量
    public static final String LIGHT_P_POWER                         = "PowerSwitch";
    public static final String LIGHT_P_BRIGHTNESS                    = "Brightness";
    public static final String LIGHT_P_COLOR_TEMPERATURE             = "ColorTemperature";
    public static final Map<String, PTYPE> LIGHT_Properties          = new HashMap<String, PTYPE>() {
        {
            put(LIGHT_P_POWER, PTYPE.t_int32);
            put(LIGHT_P_BRIGHTNESS, PTYPE.t_int32);
            put(LIGHT_P_COLOR_TEMPERATURE, PTYPE.t_int32);
        }
    };
    // 调光调色面板-亮度设置参数 PK_LIGHT
    public static final String PK_LIGHT_BRIGHTNESS_ENDPOINTID   = "1";
    public static final String PK_LIGHT_BRIGHTNESS_COMMAND_TYPE = "0107";
    public static final String PK_LIGHT_BRIGHTNESS_PARAM        = "Level";

    // 调光调色面板-色温设置参数 PK_LIGHT
    public static final String PK_LIGHT_COLOR_TEMP_ENDPOINTID   = "1";
    public static final String PK_LIGHT_COLOR_TEMP_COMMAND_TYPE = "0108";
    public static final String PK_LIGHT_COLOR_TEMP_PARAM        = "Temperature";

    //定义六键四开二场景属性常量
    public static final String SIX_SCENE_SWITCH_P_POWER_1              = "PowerSwitch_1";
    public static final String SIX_SCENE_SWITCH_P_POWER_2              = "PowerSwitch_2";
    public static final String SIX_SCENE_SWITCH_P_POWER_3              = "PowerSwitch_3";
    public static final String SIX_SCENE_SWITCH_P_POWER_4              = "PowerSwitch_4";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_1             = "5";
    public static final String SIX_SCENE_SWITCH_KEY_CODE_2             = "6";
    public static final String SIX_SCENE_SWITCH_BackLight              = "BackLightMode";
    public static final Map<String, PTYPE> SIX_SCENE_SWITCH_Properties = new HashMap<String, PTYPE>() {
        {
            put(SIX_SCENE_SWITCH_P_POWER_1, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_2, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_3, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_4, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_BackLight, PTYPE.t_bool);

            put(FWS_P_LOCALCONFIG_1, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_2, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_3, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_4, PTYPE.t_enum);
            put(FWS_P_ACTION_1, PTYPE.t_text);
            put(FWS_P_ACTION_2, PTYPE.t_text);
            put(FWS_P_ACTION_3, PTYPE.t_text);
            put(FWS_P_ACTION_4, PTYPE.t_text);
            put(FWS_P_BINDINGTABLE, PTYPE.t_text);
            put(FWS_P_FUNCTION, PTYPE.t_text);
            put(FWS_P_DSTADDRMODE, PTYPE.t_text);
            put(FWS_P_DSTADDR, PTYPE.t_text);
            put(FWS_P_RESPONSETYPE, PTYPE.t_text);
            put(FWS_P_RESULT, PTYPE.t_text);
            put(FWS_P_DSTENDPOINTID, PTYPE.t_text);
            put(TOTAL_COUNT, PTYPE.t_text);
            put(START_INDEX, PTYPE.t_text);
        }
    };

    public static final String SCENE_SWITCH_KEY_CODE_1  = "1";
    public static final String SCENE_SWITCH_KEY_CODE_2  = "2";
    public static final String SCENE_SWITCH_KEY_CODE_3  = "3";
    public static final String SCENE_SWITCH_KEY_CODE_4  = "4";
    public static final String SCENE_SWITCH_KEY_CODE_5  = "5";
    public static final String SCENE_SWITCH_KEY_CODE_6  = "6";
    public static final String SCENE_SWITCH_KEY_CODE_7  = "7";
    public static final String SCENE_SWITCH_KEY_CODE_8  = "8";
    public static final String SCENE_SWITCH_KEY_CODE_9  = "9";
    public static final String SCENE_SWITCH_KEY_CODE_10 = "10";
    public static final String SCENE_SWITCH_KEY_CODE_11 = "11";
    public static final String SCENE_SWITCH_KEY_CODE_12 = "12";

    // 定义四键开关属性常量
    public static final String FWS_P_PowerSwitch_1             = "PowerSwitch_1";
    public static final String FWS_P_PowerSwitch_2             = "PowerSwitch_2";
    public static final String FWS_P_PowerSwitch_3             = "PowerSwitch_3";
    public static final String FWS_P_PowerSwitch_4             = "PowerSwitch_4";
    public static final String FWS_P_LOCALCONFIG_1             = "LocalConfig_1";
    public static final String FWS_P_LOCALCONFIG_2             = "LocalConfig_2";
    public static final String FWS_P_LOCALCONFIG_3             = "LocalConfig_3";
    public static final String FWS_P_LOCALCONFIG_4             = "LocalConfig_4";
    public static final String FWS_P_ACTION_1                  = "Action_1";
    public static final String FWS_P_ACTION_2                  = "Action_2";
    public static final String FWS_P_ACTION_3                  = "Action_3";
    public static final String FWS_P_ACTION_4                  = "Action_4";
    public static final String FWS_P_BINDINGTABLE              = "BindingTable";

    public static final String FWS_P_FUNCTION                  = "Function";
    public static final String FWS_P_DSTADDRMODE               = "DstAddrMode";
    public static final String FWS_P_DSTADDR                   = "DstAddr";
    public static final String FWS_P_DSTENDPOINTID             = "DstEndpointId";

    public static final String FWS_P_RESPONSETYPE              = "ResponseType";
    public static final String FWS_P_RESULT                    = "Result";
    public static final String TOTAL_COUNT                     = "TotalCount";
    public static final String START_INDEX                     = "StartIndex";

    public static final String FWS_P_BackLightMode             = "BackLightMode";
    public static final Map<String, PTYPE> FWS_Properties      = new HashMap<String, PTYPE>() {
        {
            put(FWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_2, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_3, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_4, PTYPE.t_bool);
            put(FWS_P_LOCALCONFIG_1, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_2, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_3, PTYPE.t_enum);
            put(FWS_P_LOCALCONFIG_4, PTYPE.t_enum);
            put(FWS_P_ACTION_1, PTYPE.t_text);
            put(FWS_P_ACTION_2, PTYPE.t_text);
            put(FWS_P_ACTION_3, PTYPE.t_text);
            put(FWS_P_ACTION_4, PTYPE.t_text);
            put(FWS_P_BINDINGTABLE, PTYPE.t_text);
            put(FWS_P_FUNCTION, PTYPE.t_text);
            put(FWS_P_DSTADDRMODE, PTYPE.t_text);
            put(FWS_P_DSTADDR, PTYPE.t_text);
            put(FWS_P_RESPONSETYPE, PTYPE.t_text);
            put(FWS_P_RESULT, PTYPE.t_text);
            put(FWS_P_DSTENDPOINTID, PTYPE.t_text);
            put(TOTAL_COUNT, PTYPE.t_text);
            put(START_INDEX, PTYPE.t_text);
            put(FWS_P_BackLightMode, PTYPE.t_enum);
        }
    };

    // 单路窗帘
    public static final String WC_CurtainConrtol             = "CurtainControl_1";
    public static final String WC_BackLight                  = "BackLightMode";
    public static final Map<String, PTYPE> WC_Properties     = new HashMap<String, PTYPE>() {
        {
            put(WC_CurtainConrtol, PTYPE.t_enum);
            put(WC_BackLight, PTYPE.t_enum);
        }
    };

    // 双路窗帘
    public static final String TWC_CurtainConrtol                    = "CurtainControl_1";
    public static final String TWC_InnerCurtainOperation             = "CurtainControl_2";
    public static final String TWC_BackLight                         = "BackLightMode";
    public static final Map<String, PTYPE> TWC_Properties            = new HashMap<String, PTYPE>() {
        {
            put(TWC_CurtainConrtol, PTYPE.t_enum);
            put(TWC_InnerCurtainOperation, PTYPE.t_enum);
            put(TWC_BackLight, PTYPE.t_enum);
        }
    };

    // 定义空调二管制属性常量
    public static final String AIRC_T_PowerSwitch                   = "PowerSwitch";
    public static final String AIRC_T_CurrentHumidity               = "CurrentHumidity";
    public static final String AIRC_T_TargetTemperature             = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_T_Properties        = new HashMap<String, PTYPE>() {
        {
            put(AIRC_T_PowerSwitch, PTYPE.t_bool);
            put(AIRC_T_CurrentHumidity, PTYPE.t_double);
            put(AIRC_T_TargetTemperature, PTYPE.t_double);
            put(M3I1_PowerSwitch_AirConditioner, PTYPE.t_bool);
            put(M3I1_CurrentTemperature_AirConditioner, PTYPE.t_int32);
            put(M3I1_TargetTemperature_AirConditioner, PTYPE.t_int32);
            put(M3I1_WindSpeed_AirConditioner, PTYPE.t_enum);
            put(M3I1_WorkMode_AirConditioner, PTYPE.t_enum);
        }
    };

    // 定义空调四管制属性常量
    public static final String AIRC_F_PowerSwitch                   = "PowerSwitch";
    public static final String AIRC_F_CurrentHumidity               = "CurrentHumidity";
    public static final String AIRC_F_TargetTemperature             = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_F_Properties        = new HashMap<String, PTYPE>() {
        {
            put(AIRC_F_PowerSwitch, PTYPE.t_bool);
            put(AIRC_F_CurrentHumidity, PTYPE.t_double);
            put(AIRC_F_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义拉菲地暖(电机)属性常量
    public static final String FLOORH_001_PowerSwitch                   = "PowerSwitch";
    public static final String FLOORH_001_CurrentHumidity               = "CurrentHumidity";
    public static final String FLOORH_001_TargetTemperature             = "TargetTemperature";
    public static final Map<String, PTYPE> FLOORH_001_Properties        = new HashMap<String, PTYPE>() {
        {
            put(FLOORH_001_PowerSwitch, PTYPE.t_bool);
            put(FLOORH_001_CurrentHumidity, PTYPE.t_double);
            put(FLOORH_001_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义三键开关属性常量
    public static final String TWS_P3_PowerSwitch_1             = "PowerSwitch_1";
    public static final String TWS_P3_PowerSwitch_2             = "PowerSwitch_2";
    public static final String TWS_P3_PowerSwitch_3             = "PowerSwitch_3";
    public static final String TWS_P3_BackLightMode             = "BackLightMode";
    public static final Map<String, PTYPE> TWS_3_Properties     = new HashMap<String, PTYPE>() {
        {
            put(TWS_P3_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_2, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_3, PTYPE.t_bool);
            put(TWS_P3_BackLightMode, PTYPE.t_enum);
        }
    };

    // 定义遥控按钮属性常量
    public static final String RCB_P_EmergencyAlarm                     = "EmergencyAlarm";
    public static final String RCB_P_BatteryPercentage                  = "BatteryPercentage";
    public static final Map<String, PTYPE> RCB_Properties               = new HashMap<String, PTYPE>() {
        {
            put(RCB_P_EmergencyAlarm, PTYPE.t_enum);
            put(RCB_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String RCB_P_EmergencyAlarm_Trigger = "1";

    // 定义燃气传感器属性常量
    public static final String GS_P_GasSensorState                 = "GasSensorState";
    public static final Map<String, PTYPE> GS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(GS_P_GasSensorState, PTYPE.t_enum);
        }
    };
    public static final String GS_P_GasSensorState_Has             = "1";

    // 定义门磁传感器属性常量
    public static final String DS_P_ContactState                  = "ContactState";
    public static final Map<String, PTYPE> DS_Properties          = new HashMap<String, PTYPE>() {
        {
            put(DS_P_ContactState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String DS_P_ContactState_Open = "1";

    // 定义烟雾传感器属性常量
    public static final String SS_P_SmokeSensorState                 = "SmokeSensorState";
    public static final Map<String, PTYPE> SS_Properties             = new HashMap<String, PTYPE>() {
        {
            put(SS_P_SmokeSensorState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String SS_P_SmokeSensorState_Has = "1";

    // 定义温湿度传感器属性常量
    public static final String THS_P_CurrentTemperature             = "CurrentTemperature";
    public static final String THS_P_CurrentHumidity                = "CurrentHumidity";
    public static final Map<String, PTYPE> THS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(THS_P_CurrentTemperature, PTYPE.t_double);
            put(THS_P_CurrentHumidity, PTYPE.t_double);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };

    // 全面屏开关 TargetTemperature_3
    public static final String FSS_CurrentTemperature_1             = "CurrentTemperature_1";
    public static final String FSS_CurrentTemperature_2             = "CurrentTemperature_2";
    public static final String FSS_TargetTemperature_1              = "TargetTemperature_1";
    public static final String FSS_WorkMode_1                       = "WorkMode_1";
    public static final String FSS_PowerSwitch_1                    = "PowerSwitch_1";
    public static final String FSS_WindSpeed_1                      = "WindSpeed_1";
    public static final String FSS_PowerSwitch_2                    = "PowerSwitch_2";
    public static final String FSS_WindSpeed_2                      = "WindSpeed_2";
    public static final String FSS_WindSpeed_3                      = "WindSpeed_3";
    public static final String FSS_PowerSwitch_3                    = "PowerSwitch_3";
    public static final String FSS_TargetTemperature_3              = "TargetTemperature_3";
    public static final String FSS_TargetTemperature_2              = "TargetTemperature_2";
    public static final Map<String, PTYPE> FSS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(FSS_CurrentTemperature_1, PTYPE.t_double);
            put(FSS_TargetTemperature_1, PTYPE.t_double);
            put(FSS_WorkMode_1, PTYPE.t_enum);
            put(FSS_PowerSwitch_1, PTYPE.t_bool);
            put(FSS_WindSpeed_1, PTYPE.t_enum);
            put(FSS_PowerSwitch_2, PTYPE.t_bool);
            put(FSS_WindSpeed_2, PTYPE.t_enum);
            put(FSS_WindSpeed_3, PTYPE.t_enum);
            put(FSS_PowerSwitch_3, PTYPE.t_bool);
            put(FSS_TargetTemperature_3, PTYPE.t_double);
            put(FSS_TargetTemperature_2, PTYPE.t_double);
            put(FSS_CurrentTemperature_2, PTYPE.t_double);
        }
    };

    // 三合一 PK_MULTI_THREE_IN_ONE
    public static final String M3I1_PowerSwitch_AirConditioner        = "PowerSwitch_AirConditioner";
    public static final String M3I1_CurrentTemperature_AirConditioner = "CurrentTemp_AirConditioner";
    public static final String M3I1_TargetTemperature_AirConditioner  = "TargetTemp_AirConditioner";
    public static final String M3I1_WindSpeed_AirConditioner          = "WindSpeed_AirConditioner";
    public static final String M3I1_WorkMode_AirConditioner           = "WorkMode_AirConditioner";
    public static final String M3I1_PowerSwitch_FloorHeating          = "PowerSwitch_FloorHeating";
    public static final String M3I1_TargetTemperature_FloorHeating    = "TargetTemp_FloorHeating";
    public static final String M3I1_CurrentTemperature_FloorHeating   = "CurrentTemp_FloorHeating";
    public static final String M3I1_AutoWorkMode_FloorHeating         = "AutoWorkMode_FloorHeating";
    public static final String M3I1_CurrentTemp_FreshAir              = "CurrentTemp_FreshAir";
    public static final String M3I1_WindSpeed_FreshAir                = "WindSpeed_FreshAir";
    public static final String M3I1_PowerSwitch_FreshAir              = "PowerSwitch_FreshAir";
    public static final Map<String, PTYPE> M3I1_Properties           = new HashMap<String, PTYPE>() {
        {
            put(M3I1_PowerSwitch_AirConditioner, PTYPE.t_bool);
            put(M3I1_CurrentTemperature_AirConditioner, PTYPE.t_int32);
            put(M3I1_TargetTemperature_AirConditioner, PTYPE.t_int32);
            put(M3I1_WindSpeed_AirConditioner, PTYPE.t_enum);
            put(M3I1_WorkMode_AirConditioner, PTYPE.t_enum);
            put(M3I1_PowerSwitch_FloorHeating, PTYPE.t_bool);
            put(M3I1_TargetTemperature_FloorHeating, PTYPE.t_int32);
            put(M3I1_CurrentTemperature_FloorHeating, PTYPE.t_int32);
            put(M3I1_WindSpeed_FreshAir, PTYPE.t_enum);
            put(M3I1_PowerSwitch_FreshAir, PTYPE.t_enum);
            put(M3I1_AutoWorkMode_FloorHeating, PTYPE.t_enum);
            put(M3I1_CurrentTemp_FreshAir, PTYPE.t_int32);
        }
    };

    // 定义水浸传感器属性常量
    public static final String WS_P_WaterSensorState                 = "WaterSensorState";
    public static final Map<String, PTYPE> WS_Properties             = new HashMap<String, PTYPE>() {
        {
            put(WS_P_WaterSensorState, PTYPE.t_enum);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String WS_P_WaterSensorState_Has             = "1";

    // 定义人体红外感应器属性常量
    public static final String PIR_P_MotionAlarmState                   = "MotionAlarmState";
    public static final Map<String, PTYPE> PIR_Properties               = new HashMap<String, PTYPE>() {
        {
            put(PIR_P_MotionAlarmState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String PIR_P_MotionAlarmState_Has               = "1";
    public static final String PIR_P_MotionAlarmState_NoHas             = "0";

    // 定义RGB彩色灯带属性常量
    public static final String RGBLS_brightness                           = "brightness";
    public static final String RGBLS_powerstate                           = "powerstate";
    public static final String RGBLS_LightType                            = "LightType";
    public static final String RGBLS_mode                                 = "mode";
    public static final String RGBLS_colorTemperatureInKelvin             = "colorTemperatureInKelvin";
    public static final String RGBLS_LightMode                            = "LightMode";
    public static final Map<String, PTYPE> RGBLS_Properties               = new HashMap<String, PTYPE>() {
        {
            put(RGBLS_brightness, PTYPE.t_int32);
            put(RGBLS_powerstate, PTYPE.t_bool);
            put(RGBLS_LightType, PTYPE.t_enum);
            put(RGBLS_mode, PTYPE.t_enum);
            put(RGBLS_colorTemperatureInKelvin, PTYPE.t_int32);
            put(RGBLS_LightMode, PTYPE.t_enum);
        }
    };

    // 智能锁A7
    public static final String SL_batterypercentage             = "BatteryPercentage";
    public static final String SL_lockstate                     = "LockState";
    public static final Map<String, PTYPE> SL_Properties        = new HashMap<String, PTYPE>() {
        {
            put(SL_batterypercentage, PTYPE.t_int32);
            put(SL_lockstate, PTYPE.t_enum);
        }
    };

    // 定义属性配置文件
    public static final Map<String, Map<String, PTYPE>> propertyProfile = new HashMap<String, Map<String, PTYPE>>() {
        {
            put(PK_GATEWAY, GW_Properties);
            put(PK_GATEWAY_RG4100, GW_4100_Properties);
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
            put(PK_ONE_WAY_DIMMABLE_LIGHT, LIGHT_Properties);
            put(PK_RGB_COLOR_LIGHT_STRIP, RGBLS_Properties);
            put(PK_FOURWAYSWITCH, FWS_Properties);
            put(PK_FOURWAYSWITCH_2, FWS_Properties);
            put(PK_SIX_TWO_SCENE_SWITCH, SIX_SCENE_SWITCH_Properties);
            put(PK_AIRCOMDITION_TWO, AIRC_T_Properties);
            put(PK_AIRCOMDITION_FOUR, AIRC_F_Properties);
            // put(PK_FLOORHEATING001, FLOORH_001_Properties);
            put(PK_FLOORHEATING001, M3I1_Properties);
            put(PK_THREE_KEY_SWITCH, TWS_3_Properties);
            put(PK_SMART_LOCK_A7, SL_Properties);
            put(TEST_PK_ONEWAYWINDOWCURTAINS, WC_Properties);
            put(TEST_PK_TWOWAYWINDOWCURTAINS, TWC_Properties);
            put(PK_SIX_SCENE_SWITCH, PSS_Properties);
            put(PK_SYT_SIX_SCENE_SWITCH, PSS_Properties);
            put(PK_OUTLET, PK_OUTLET_Properties);
            put(PK_FOUR_SCENE_SWITCH, PFS_Properties);
            put(PK_SYT_FOUR_SCENE_SWITCH, PFS_Properties);
            put(PK_ONE_SCENE_SWITCH, POS_Properties);
            put(PK_SYT_ONE_SCENE_SWITCH, POS_Properties);
            put(PK_THREE_SCENE_SWITCH, PTS_Properties);
            put(PK_SYT_THREE_SCENE_SWITCH, PTS_Properties);
            put(PK_TWO_SCENE_SWITCH, P2S_Properties);
            put(PK_SYT_TWO_SCENE_SWITCH, P2S_Properties);
            put(TEST_PK_FULL_SCREEN_SWITCH, FSS_Properties);
            put(PK_MULTI_THREE_IN_ONE, M3I1_Properties);
            put(PK_FAU, M3I1_Properties);
            put(PK_MULTI_AC_AND_FH, M3I1_Properties);
            put(PK_MULTI_AC_AND_FA, M3I1_Properties);
            put(PK_MULTI_FH_AND_FA, M3I1_Properties);
        }
    };

    // 定义防撬事件常量
    public static final String P_E_TamperAlarm     = "TamperAlarm";
    // 定义布防报警事件常量
    public static final String P_E_ProtectionAlarm = "ProtectionAlarm";

    // 定义状态常量
    public static final int STATUS_ON  = 1;
    public static final int STATUS_OFF = 0;

    public static final int MASTER_CONTROL     = 2;// 主控
    public static final int AUXILIARY_CONTROL  = 0;// 辅控

    // 窗帘状态常量
    public static final int WC_STATUS_STOP  = 0;
    public static final int WC_STATUS_OPEN  = 1;
    public static final int WC_STATUS_CLOSE = 2;

    // 定义控制类型
    public static enum ControlType {
        APIChanel,
        SDK
    }

    // 定义事件类型
    public static final String EVENTTYPE_INFO  = "info";
    public static final String EVENTTYPE_ALERT = "alert";
    public static final String EVENTTYPE_ERROR = "err";
}