package com.rexense.wholehouse.contract;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备物描述语言常量
 */
public class CTSL {
    // 定义设备ProductKey常量
    public static final String PK_GATEWAY                                        = "a1GuwJkxdQx";
    public static final String PK_ONEWAYSWITCH                                   = "a14mX84TVva";// 一键面板开关
    public static final String PK_TWOWAYSWITCH                                   = "a1zDZ6g36bK";// 两键面板开关
    public static final String PK_REMOTECONTRILBUTTON                            = "a1G306IUVoa";
    public static final String PK_SMART_LOCK                                     = "a1SlrD1NHyW";//临时密码StartDate
    public static final String PK_SIX_TWO_SCENE_SWITCH                           = "a1zf8jfGzTX";
    public static final String PK_SIX_SCENE_SWITCH                               = "a14vy6VXVTq";
    public static final String PK_U_SIX_SCENE_SWITCH                             = "a1KFQudxYT4";// 鸿雁U型场景开关
    public static final String PK_TWO_SCENE_SWITCH                               = "a1484Z8uJhc";
    public static final String PK_THREE_SCENE_SWITCH                             = "a15jCHqanke";
    public static final String PK_FOUR_SCENE_SWITCH                              = "a1RmEjSaiUa";
    public static final String PK_LIGHT                                          = "a1SdpzGgZCa";
    public static final String PK_ONE_SCENE_SWITCH                               = "a1k7IO1wPdY";//   a1HurDIuRiW
    public static final String PK_TEMHUMSENSOR                                   = "a1TG5jEuQCN";

    public static final String PK_CONTROL_LIGHT                                  = "a1SdpzGgZCa";//调光调色面板
    public static final String PK_BLACK_ONE_SWITCH                               = "a1VK0kgweyU";
    public static final String PK_BLACK_TWO_SWITCH                               = "a1Hi1QQEGPm";
    public static final String PK_BLACK_THREE_SWITCH                             = "a1uAwpNGcI7";
    public static final String PK_WHITE_THREE_SWITCH                             = "a1UpoJjIjCn";
    public static final String PK_BLACK_FOUR_SWITCH                              = "a1lX2ZUOTzE";
    public static final String PK_BLACK_USB                                      = "a1OLiwq8qSv";

    public static final String PK_RGB_COLOR_LIGHT_STRIP                          = "a1yzxlzMyR0";// RGB彩色灯带
    public static final String PK_HSL_COLOR_LIGHT                                = "a11jimE2TIi";// HSL彩色灯
    public static final String PK_HSL_COLOR_LIGHT_STRIP                          = "a1yARva6fVG";// HSL彩色灯带
    public static final String PK_CONTROL_COLOR_TEMP_LIGHT                       = "a1uXFxg1cAL";// 调光色温灯
    public static final String PK_CONTROL_COLOR_LIGHT                            = "a1mSlpQen3w";// 调光灯
    public static final String PK_CONTROL_COLOR_BROAD                            = "a1OlU25BrOu";// 调光调色面板
    public static final String PK_RGB_COLOR_LIGHT                                = "a1MAzhopgCh";// RGB彩色灯
    public static final String PK_AIRCOMDITION_FOUR                              = "a1ayoCqQpxx";// 空调四管制

    public static final String PK_FOURWAYSWITCH                                  = "a1pOIRmMTGF";// 四键开关
    public static final String PK_FOURWAYSWITCH_2                                = "a1ZuadYzjLs";// 四键开关
    public static final String PK_THREE_KEY_SWITCH                               = "a1MHtwEL9HE";// 三键面板开关

    public static final String PK_ANY_FOUR_SCENE_SWITCH                          = "a1Qb4P7DHAv";// 随意贴四键场景开关
    public static final String PK_ANY_TWO_SCENE_SWITCH                           = "a1WBTOxm1Z5";// 随意贴二键场景开关

    public static final String PK_ONE_KEY_SWITCH                                 = "a14mX84TVva";// 一键面板开关
    public static final String PK_TWO_KEY_SWITCH                                 = "a1zDZ6g36bK";// 两键面板开关

    public static final String PK_VRV_AC                                         = "a1hGRYll4OE";// 拉斐VRV温控器LFWK001
    public static final String PK_FAU                                            = "a1gIYJJMkrd";// 拉斐新风LFXF001

    public static final String PK_ONEWAYSWITCH_HY                                = "a1DcTTIWrC9";// 一键面板开关-鸿雁-HY0030
    public static final String PK_TWOWAYSWITCH_HY                                = "a1GUG9ePrOP";// 两键面板开关-鸿雁-HY0031
    public static final String PK_TWOWAYSWITCH_MODULE_HY                         = "a1y4aTCpDkw";// 两路智能开关模块-鸿雁-HY0122
    public static final String PK_THREEWAYSWITCH_HY                              = "a1tt2P7XzKe";// 三键面板开关-鸿雁-HY0032
    public static final String PK_U_SIX_SCENE_SWITCH_HY                          = "a1C6uQIrq0Y";// U型六场景开关-鸿雁-5f0c3b
    public static final String PK_FULL_SCREEN_SWITCH_HY                          = "a18MYdaR9Cd";// 全面屏开关-鸿雁-HY0134
    public static final String PK_10A_MEASURING_OUTLET_HY                        = "a15rJERCbFB";// 10A计量插座-鸿雁-HY0104-HY0105(usb)
    public static final String PK_PM_TEMHUMSENSOR_HY                             = "a1eZ835Td0z";// PM2.5温湿度传感器-鸿雁-01112b
    public static final String PK_PM_TEMHUMSENSOR_HY_PTM1005S                    = "a1SFtICNVPP";// 智能环境检测仪-鸿雁-FZB56+PCH01MK0.5
    public static final String PK_ONEWAYWINDOWCURTAINS_HY_U1                     = "a1VDne8nT4s";// 一路三键窗帘开关-鸿雁-?
    public static final String PK_ONEWAYWINDOWCURTAINS_HY_U2                     = "a14KMrCD2q6";// 一路三键窗帘开关-鸿雁-?
    public static final String PK_OUTLET_10A_HY_U1                               = "a1BSsZhVN6c";// 10A插座-鸿雁U1-?
    public static final String PK_OUTLET_10A_HY_U2                               = "a1DI9AfW8su";// 10A插座-鸿雁U2-?
    public static final String PK_OUTLET_16A_HY_U1                               = "a1DhrOJdnaG";// 16A插座-鸿雁U1-?
    public static final String PK_OUTLET_16A_HY_U2                               = "a1CrnMEzaIN";// 16A插座-鸿雁U2-?
    public static final String PK_16A_MEASURING_OUTLET_HY_U1                     = "a1MolqiDnOO";// 16A计量插座-鸿雁U1-?
    public static final String PK_16A_MEASURING_OUTLET_HY_U2                     = "a1Vqomw4iN8";// 16A计量插座-鸿雁U2-?
    public static final String PK_AIRCOMDITION_TWO_HY_U1                         = "a15fno9Hpb5";// 空调二管制-鸿雁U1-0212c3
    public static final String PK_AIRCOMDITION_TWO_HY_U2                         = "a1320Rmfc4i";// 空调二管制-鸿雁U2-?
    public static final String PK_WATER_FLOORHEAT_HY_U1                          = "a18mJ9TA9NU";// 水地暖-鸿雁U1-5e0edb
    public static final String PK_WATER_FLOORHEAT_HY_U2                          = "a10RQMMy5mt";// 水地暖-鸿雁U2-?
    public static final String PK_ELEC_FLOORHEAT_HY_U1                           = "a1B7kaf8Rrp";// 电地暖-鸿雁U1-?
    public static final String PK_ELEC_FLOORHEAT_HY_U2                           = "a1PTP3l9VPO";// 电地暖-鸿雁U2-?
    public static final String PK_FAU_HY_U1                                      = "a1C5uadGOon";// 新风-鸿雁U1-280eda
    public static final String PK_AIRCOMDITION_CONVERTER                         = "a1Gq6Y7JuXQ";// 空调转换器-鸿雁-RH9000

    public static final String PK_ONEWAYSWITCH_YQS_XB                            = "a1JdJgkgHOs";// 一键面板开关-粤奇胜肖邦-HY0001
    public static final String PK_TWOWAYSWITCH_YQS_XB                            = "a1M1NHS5qQP";// 两键面板开关-粤奇胜肖邦-HY0002
    public static final String PK_THREEWAYSWITCH_YQS_XB                          = "a12raDNHQWl";// 三键面板开关-粤奇胜肖邦-HY0003
    public static final String PK_SIX_SCENE_SWITCH_YQS_XB                        = "a1emXV7oUHE";// 三开六场景开关-粤奇胜肖邦-0106-G
    public static final String PK_ONEWAYSWITCH_YQS_ZR                            = "a1RxwXI9F1a";// 一键面板开关-粤奇胜智睿-？
    public static final String PK_TWOWAYSWITCH_YQS_ZR                            = "a19mS4sBYyd";// 两键面板开关-粤奇胜智睿-？
    public static final String PK_THREEWAYSWITCH_YQS_ZR                          = "a1J1xRl39gH";// 三键面板开关-粤奇胜智睿-？
    public static final String PK_SIX_SCENE_SWITCH_YQS_ZR                        = "a1q2LpdEHNL";// 三开六场景开关-粤奇胜智睿-？
    public static final String PK_ONEWAYWINDOWCURTAINS_YQS_ZR                    = "a1ivs8pYOXp";// 一路三键窗帘开关-粤奇胜智睿-?
    public static final String PK_ONEWAYWINDOWCURTAINS_YQS_XB                    = "a1NlxeGSUhk";// 一路三键窗帘开关-粤奇胜肖邦-?
    public static final String PK_TWOWAYWINDOWCURTAINS_YQS_ZR                    = "a1XnI3GX53E";// 两路六键窗帘开关-粤奇胜智睿-?
    public static final String PK_TWOWAYWINDOWCURTAINS_YQS_XB                    = "a1vPBDfi6Lm";// 两路六键窗帘开关-粤奇胜肖邦-?
    public static final String PK_OUTLET_10A_YQS                                 = "a1ElYXt09mL";// 10A插座-粤奇胜-?
    public static final String PK_10A_MEASURING_OUTLET_YQS                       = "a1q8qHB6jFl";// 10A计量插座-粤奇胜-?
    public static final String PK_OUTLET_16A_YQS                                 = "a1g6hfxATn9";// 16A插座-粤奇胜-?
    public static final String PK_16A_MEASURING_OUTLET_YQS                       = "a15kdxAmoMs";// 16A计量插座-粤奇胜-?

    public static final String PK_ONEWAYSWITCH_LF                                = "a1r1FYSVdPS";// 一键面板开关-拉斐-LF0001
    public static final String PK_TWOWAYSWITCH_LF                                = "a1c06gP2cFR";// 两键面板开关-拉斐-LF0002
    public static final String PK_THREEWAYSWITCH_LF                              = "a1TzI1KbSgh";// 三键面板开关-拉斐-LF0003
    public static final String PK_FOURWAYSWITCH_LF                               = "a1EESHO4gBR";// 四键面板开关-拉斐-LF0004
    public static final String PK_FOUR_SCENE_SWITCH_LF                           = "a135fBmsioZ";// 二开四场景开关-拉斐-LF0009
    public static final String PK_FOUR_TWO_SCENE_SWITCH_LF                       = "a1JL9rBTAye";// 二开关+二场景开关-拉斐-LF0054
    public static final String PK_SIX_FOUR_SCENE_SWITCH_LF                       = "a1A7M6g3dlK";// 二开关+四场景开关-拉斐-LF0058
    public static final String PK_OUTLET_10A_LF                                  = "a1mneuA9e7t";// 10A插座-拉斐-LF0031
    public static final String PK_ONEWAYWINDOWCURTAINS_LF                        = "a1th1FeuVij";// 窗帘单路开关-拉斐-LF0016
    public static final String PK_TWOWAYWINDOWCURTAINS_LF                        = "a1cs9K2oE8t";// 窗帘双路开关-拉斐-LF0017
    public static final String PK_FLOORHEATING001_LF                             = "a1sSgiInktz";// 拉菲地暖(电机)-拉斐-LF0020
    public static final String PK_AIRCOMDITION_TWO_LF                            = "a1N0PnSSB67";// 空调二管制-拉斐-LF0019
    public static final String PK_ONEWAYWINDOWCURTAINS_LF_D8                     = "a1frPuBITYB";// 一路三键窗帘开关-拉斐D8-?
    public static final String PK_ONEWAYWINDOWCURTAINS_LF_D9                     = "a1WGncVvB5l";// 一路三键窗帘开关-拉斐D9-?
    public static final String PK_TWOWAYWINDOWCURTAINS_LF_D8                     = "a1HtchMBFwD";// 两路六键窗帘开关-拉斐D8-?
    public static final String PK_TWOWAYWINDOWCURTAINS_LF_D9                     = "a1ZfxTCHjKT";// 两路六键窗帘开关-拉斐D9-?
    public static final String PK_10A_MEASURING_OUTLET_LF                        = "a1c8aaG7lC3";// 10A计量插座-拉斐-?
    public static final String PK_OUTLET_16A_LF                                  = "a1U2pgAaGnX";// 16A插座-拉斐-?
    public static final String PK_16A_MEASURING_OUTLET_LF                        = "a1me1Uuq41S";// 16A计量插座-拉斐-?
    public static final String PK_FAU_LF                                         = "a1u8WuSNAso";// 新风-拉斐-LF0021

    public static final String PK_ONEWAY_DANHUO_RY                               = "a13mQibAGGf";// 一键单火开关-瑞瀛-RT0101
    public static final String PK_TWOWAY_DANHUO_RY                               = "a1FE80tCMhO";// 两键单火开关-瑞瀛-RT0102
    public static final String PK_THREEWAY_DANHUO_RY                             = "a1Ftp4WZKI1";// 三键单火开关-瑞瀛-RT0103
    public static final String PK_GATEWAY_RG4100_RY                              = "b17wpNLgyIe";// RG4100网关-瑞瀛

    public static final String PK_KDS_SMART_LOCK_A7                               = "a1Pmh01sy65";//智能锁-凯迪仕-142855 临时密码StartTime
    public static final String PK_KDS_SMART_LOCK_K100                             = "a1S3WQJiwhJ";//智能锁-凯迪仕-?
    public static final String PK_KDS_SMART_LOCK_S6                               = "a1pxAENkml3";//智能锁-凯迪仕-?

    public static final String PK_MM_SMART_LOCK                                   = "a1BZi3GhxP4";//智能锁-名门-?

    public static final String PK_MS_SMART_LOCK                                   = "a17421mNBde";//智能锁-曼申-?

    public static final String PK_DY_ELE_D82                                      = "a1aqlH5qWN8";//窗帘电机-D82-杜亚-onoff_curtain
    public static final String PK_DY_ELE_D52                                      = "a1aSCxdlCZQ";//窗帘电机-D52-杜亚-?
    public static final String PK_DY_ELE_DC                                       = "a1lvoRKHprq";//窗帘电机-直流-杜亚-?

    public static final String PK_WSD_ELE_DC                                      = "a1vfk06D7tx";//直流窗帘电机-威仕达-RLCMA01
    public static final String PK_WSD_ELE_AC                                      = "a163tg5bgpY";//交流窗帘电机-威仕达-?

    public static final String PK_BS_ELE                                          = "a1ojeDeJgFD";//窗帘电机-丙申-RH9000

    public static final String PK_DOORSENSOR_HM                                   = "a1cErKvfSFv";// 门磁传感器-海曼-DoorSensor-N-3.0
    public static final String PK_WATERSENSOR_HM                                  = "a1kUeHPrUxn";// 水浸传感器-海曼-WaterSensor-EM
    public static final String PK_PIRSENSOR_HM                                    = "a1E86BI3l3S";// 人体红外传感器-海曼-PIRSensor-N-3.0
    public static final String PK_SMOKESENSOR_HM                                  = "a1aSgTD95QR";// 烟雾传感器-海曼-SmokeSensor-EF-3.0
    public static final String PK_GASSENSOR_HM                                    = "a110ym9xmvS";// 燃气传感器-海曼-GASSensor-EM
    public static final String PK_TEMHUMSENSOR_HM                                 = "a10d8QkzrLu";// 温湿度传感器-海曼-?

    public static final String PK_DOORSENSOR_MLK                                  = "a1uxEMucJWM";// 门磁传感器-麦乐克-RH3001
    public static final String PK_TEMHUMSENSOR_MLK                                = "a1XHr0x5Ysa";// 温湿度传感器-麦乐克-RH3050
    public static final String PK_PIRSENSOR_MLK                                   = "a1k8MdaB0S3";// 人体红外传感器-麦乐克-RH3040
    public static final String PK_SMOKESENSOR_MLK                                 = "a1TVRM8gUyj";// 烟雾传感器-麦乐克-RH3010
    public static final String PK_GASSENSOR_MLK                                   = "a1XAGsws0cs";// 燃气传感器-麦乐克-RH3070
    public static final String PK_WATERSENSOR_MLK                                 = "a18zsto5W7k";// 水浸传感器-麦乐克-RH3020

    // 定义属性类型
    public static enum PTYPE {
        t_text,
        t_int32,
        t_double,
        t_bool,
        t_enum
    }

    // 定义公共属性常量
    public static final String P_P_BatteryPercentage = "BatteryPercentage";

    // 定义网关属性常量
    public static final String GW_P_ArmMode                                       = "ArmMode";
    public static final String GW_P_DoorBellSoundID                               = "DoorBellSoundID";
    public static final String GW_P_AlarmSoundVolume                              = "AlarmSoundVolume";
    public static final String GW_P_DoorBellSoundVolume                           = "DoorBellSoundVolume";
    public static final String GW_P_AlarmSoundID                                  = "AlarmSoundID";
    public static final Map<String, PTYPE> GW_Properties                          = new HashMap<String, PTYPE>() {
        {
            put(GW_P_DoorBellSoundID, PTYPE.t_int32);
            put(GW_P_AlarmSoundVolume, PTYPE.t_int32);
            put(GW_P_DoorBellSoundVolume, PTYPE.t_int32);
            put(GW_P_AlarmSoundID, PTYPE.t_int32);
            put(GW_P_ArmMode, PTYPE.t_bool);
        }
    };
    public static final String GW_P_ArmMode_deploy                                = "1";
    public static final String GW_P_ArmMode_disarm                                = "0";
    // 定义网关服务常量
    public static final String GW_S_InvokeMode                                    = "InvokeMode";
    public static final String GW_SA_InvokeMode_Voice                             = "InvokeVoice";
    public static final String GW_SA_InvokeMode_Voice1                            = "0";
    public static final String GW_SA_InvokeMode_Voice2                            = "1";

    //4100网关
    public static final String GW_4100_ZB_Band                                    = "ZB_Band";//ZB带宽
    public static final String GW_4100_NETWORK_KEY                                = "NETWORK_KEY";//网络密钥
    public static final String GW_4100_ZB_CO_MAC                                  = "ZB_CO_MAC";//ZB设备MAC地址
    public static final String GW_4100_EXT_PAN_ID                                 = "EXT_PAN_ID";//扩展网路标识
    public static final String GW_4100_ZB_Channel                                 = "ZB_Channel";//ZB通道
    public static final String GW_4100_ZB_PAN_ID                                  = "ZB_PAN_ID";//网路标识
    public static final Map<String, PTYPE> GW_4100_Properties                     = new HashMap<String, PTYPE>() {
        {
            put(GW_4100_ZB_Band, PTYPE.t_enum);
            put(GW_4100_NETWORK_KEY, PTYPE.t_text);
            put(GW_4100_ZB_CO_MAC, PTYPE.t_text);
            put(GW_4100_EXT_PAN_ID, PTYPE.t_text);
            put(GW_4100_ZB_Channel, PTYPE.t_int32);
            put(GW_4100_ZB_PAN_ID, PTYPE.t_text);
        }
    };

    // 定义一键开关属性常量
    public static final String OWS_P_PowerSwitch_1                                = "PowerSwitch_1";
    public static final String OWS_P_BackLightMode                                = "BackLightMode";
    public static final Map<String, PTYPE> OWS_Properties                         = new HashMap<String, PTYPE>() {
        {
            put(OWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(OWS_P_BackLightMode, PTYPE.t_enum);
        }
    };

    // 定义三开六场景开关属性常量
    public static final String PSS_BackLightMode                                  = "backLight";
    public static final Map<String, PTYPE> PSS_Properties                         = new HashMap<String, PTYPE>() {
        {
            put(PSS_BackLightMode, PTYPE.t_enum);
        }
    };

    // 定义一场景开关属性常量
    public static final String POS_BackLight                                      = "backLight";
    public static final Map<String, PTYPE> POS_Properties                         = new HashMap<String, PTYPE>() {
        {
            put(POS_BackLight, PTYPE.t_enum);
        }
    };

    // 定义大三开场景开关属性常量
    public static final String PTS_BackLight                                      = "backLight";
    public static final Map<String, PTYPE> PTS_Properties                         = new HashMap<String, PTYPE>() {
        {
            put(PTS_BackLight, PTYPE.t_enum);
        }
    };

    // 定义大二开场景开关属性常量
    public static final String P2S_BackLight                                      = "backLight";
    public static final Map<String, PTYPE> P2S_Properties                         = new HashMap<String, PTYPE>() {
        {
            put(P2S_BackLight, PTYPE.t_bool);
        }
    };

    // 定义二开四场景开关属性常量
    public static final String PFS_BackLight              = "backLight";
    public static final Map<String, PTYPE> PFS_Properties = new HashMap<String, PTYPE>() {
        {
            put(PFS_BackLight, PTYPE.t_enum);
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
    public static final String PK_OUTLET_Backlight              = "backlight";
    public static final Map<String, PTYPE> PK_OUTLET_Properties = new HashMap<String, PTYPE>() {
        {
            put(PK_OUTLET_Backlight, PTYPE.t_enum);
        }
    };

    //定义调光调色属性常量
    public static final String LIGHT_P_POWER                         = "PowerSwitch";
    public static final String LIGHT_P_BRIGHTNESS                    = "brightness";
    public static final String LIGHT_P_COLOR_TEMPERATURE             = "colorTemperature";
    public static final Map<String, PTYPE> LIGHT_Properties          = new HashMap<String, PTYPE>() {
        {
            put(LIGHT_P_POWER, PTYPE.t_int32);
            put(LIGHT_P_BRIGHTNESS, PTYPE.t_int32);
            put(LIGHT_P_COLOR_TEMPERATURE, PTYPE.t_int32);
        }
    };

    //定义六键四开二场景属性常量
    public static final String SIX_SCENE_SWITCH_P_POWER_1              = "PowerSwitch_1";
    public static final String             SIX_SCENE_SWITCH_P_POWER_2  = "PowerSwitch_2";
    public static final String             SIX_SCENE_SWITCH_P_POWER_3  = "PowerSwitch_3";
    public static final String             SIX_SCENE_SWITCH_P_POWER_4  = "PowerSwitch_4";
    public static final String             SIX_SCENE_SWITCH_KEY_CODE_1 = "5";
    public static final String             SIX_SCENE_SWITCH_KEY_CODE_2 = "6";
    public static final String             SIX_SCENE_SWITCH_BackLight  = "backLight";
    public static final Map<String, PTYPE> SIX_SCENE_SWITCH_Properties = new HashMap<String, PTYPE>() {
        {
            put(SIX_SCENE_SWITCH_P_POWER_1, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_2, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_3, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_P_POWER_4, PTYPE.t_int32);
            put(SIX_SCENE_SWITCH_BackLight, PTYPE.t_bool);
        }
    };

    //定义二开关+二场景属性常量
    public static final String FOUR_TWO_SCENE_SWITCH_POWER_1                       = "PowerSwitch_1";
    public static final String FOUR_TWO_SCENE_SWITCH_POWER_2                       = "PowerSwitch_2";
    public static final String FOUR_TWO_SCENE_SWITCH_KEY_CODE_3                    = "3";
    public static final String FOUR_TWO_SCENE_SWITCH_KEY_CODE_4                    = "4";
    public static final String FOUR_TWO_SCENE_SWITCH_BackLight                     = "backLight";
    public static final Map<String, PTYPE> FOUR_TWO_SCENE_SWITCH_Properties        = new HashMap<String, PTYPE>() {
        {
            put(FOUR_TWO_SCENE_SWITCH_POWER_1, PTYPE.t_bool);
            put(FOUR_TWO_SCENE_SWITCH_POWER_2, PTYPE.t_bool);
            put(FOUR_TWO_SCENE_SWITCH_KEY_CODE_3, PTYPE.t_int32);
            put(FOUR_TWO_SCENE_SWITCH_KEY_CODE_4, PTYPE.t_int32);
            put(FOUR_TWO_SCENE_SWITCH_BackLight, PTYPE.t_bool);
        }
    };

    //定义二开关+四场景属性常量
    public static final String SIX_FOUR_SCENE_SWITCH_POWER_1                       = "PowerSwitch_1";
    public static final String SIX_FOUR_SCENE_SWITCH_POWER_2                       = "PowerSwitch_2";
    public static final String SIX_FOUR_SCENE_SWITCH_BackLight                     = "backLight";
    public static final Map<String, PTYPE> SIX_FOUR_SCENE_SWITCH_Properties        = new HashMap<String, PTYPE>() {
        {
            put(SIX_FOUR_SCENE_SWITCH_POWER_1, PTYPE.t_bool);
            put(SIX_FOUR_SCENE_SWITCH_POWER_2, PTYPE.t_bool);
            put(SIX_FOUR_SCENE_SWITCH_BackLight, PTYPE.t_bool);
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
    public static final String             FWS_P_PowerSwitch_1 = "PowerSwitch_1";
    public static final String             FWS_P_PowerSwitch_2 = "PowerSwitch_2";
    public static final String             FWS_P_PowerSwitch_3 = "PowerSwitch_3";
    public static final String             FWS_P_PowerSwitch_4 = "PowerSwitch_4";
    public static final String             FWS_P_BackLightMode = "BackLightMode";
    public static final Map<String, PTYPE> FWS_Properties      = new HashMap<String, PTYPE>() {
        {
            put(FWS_P_PowerSwitch_1, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_2, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_3, PTYPE.t_bool);
            put(FWS_P_PowerSwitch_4, PTYPE.t_bool);
            put(FWS_P_BackLightMode, PTYPE.t_enum);
        }
    };

    // 单路窗帘
    public static final String             WC_CurtainConrtol = "curtainConrtol";
    public static final String             WC_BackLight      = "backLight";
    public static final Map<String, PTYPE> WC_Properties     = new HashMap<String, PTYPE>() {
        {
            put(WC_CurtainConrtol, PTYPE.t_enum);
            put(WC_BackLight, PTYPE.t_enum);
        }
    };

    // 双路窗帘
    public static final String             TWC_CurtainConrtol        = "curtainConrtol";
    public static final String             TWC_InnerCurtainOperation = "InnerCurtainOperation";
    public static final String             TWC_BackLight             = "backLight";
    public static final Map<String, PTYPE> TWC_Properties            = new HashMap<String, PTYPE>() {
        {
            put(TWC_CurtainConrtol, PTYPE.t_enum);
            put(TWC_InnerCurtainOperation, PTYPE.t_enum);
            put(TWC_BackLight, PTYPE.t_enum);
        }
    };

    // 定义空调二管制属性常量
    public static final String             AIRC_T_PowerSwitch       = "PowerSwitch";
    public static final String             AIRC_T_CurrentHumidity   = "CurrentHumidity";
    public static final String             AIRC_T_TargetTemperature = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_T_Properties        = new HashMap<String, PTYPE>() {
        {
            put(AIRC_T_PowerSwitch, PTYPE.t_bool);
            put(AIRC_T_CurrentHumidity, PTYPE.t_double);
            put(AIRC_T_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义鸿雁空调转换器属性常量
    public static final String             AIRC_Converter_Nickname_        = "Nickname_";
    public static final String             AIRC_Converter_Nickname_1       = "Nickname_1";
    public static final String             AIRC_Converter_Nickname_2       = "Nickname_2";
    public static final String             AIRC_Converter_Nickname_3       = "Nickname_3";
    public static final String             AIRC_Converter_Nickname_4       = "Nickname_4";
    public static final String             AIRC_Converter_Nickname_5       = "Nickname_5";
    public static final String             AIRC_Converter_Nickname_6       = "Nickname_6";
    public static final String             AIRC_Converter_Nickname_7       = "Nickname_7";
    public static final String             AIRC_Converter_Nickname_8       = "Nickname_8";
    public static final String             AIRC_Converter_Nickname_9       = "Nickname_9";
    public static final String             AIRC_Converter_Nickname_10      = "Nickname_10";
    public static final String             AIRC_Converter_Nickname_11      = "Nickname_11";
    public static final String             AIRC_Converter_Nickname_12      = "Nickname_12";
    public static final String             AIRC_Converter_Nickname_13      = "Nickname_13";
    public static final String             AIRC_Converter_Nickname_14      = "Nickname_14";
    public static final String             AIRC_Converter_Nickname_15      = "Nickname_15";
    public static final String             AIRC_Converter_Nickname_16      = "Nickname_16";

    public static final String             AIRC_Converter_PowerSwitch_        = "PowerSwitch_";
    public static final String             AIRC_Converter_PowerSwitch_1       = "PowerSwitch_1";
    public static final String             AIRC_Converter_PowerSwitch_2       = "PowerSwitch_2";
    public static final String             AIRC_Converter_PowerSwitch_3       = "PowerSwitch_3";
    public static final String             AIRC_Converter_PowerSwitch_4       = "PowerSwitch_4";
    public static final String             AIRC_Converter_PowerSwitch_5       = "PowerSwitch_5";
    public static final String             AIRC_Converter_PowerSwitch_6       = "PowerSwitch_6";
    public static final String             AIRC_Converter_PowerSwitch_7       = "PowerSwitch_7";
    public static final String             AIRC_Converter_PowerSwitch_8       = "PowerSwitch_8";
    public static final String             AIRC_Converter_PowerSwitch_9       = "PowerSwitch_9";
    public static final String             AIRC_Converter_PowerSwitch_10      = "PowerSwitch_10";
    public static final String             AIRC_Converter_PowerSwitch_11      = "PowerSwitch_11";
    public static final String             AIRC_Converter_PowerSwitch_12      = "PowerSwitch_12";
    public static final String             AIRC_Converter_PowerSwitch_13      = "PowerSwitch_13";
    public static final String             AIRC_Converter_PowerSwitch_14      = "PowerSwitch_14";
    public static final String             AIRC_Converter_PowerSwitch_15      = "PowerSwitch_15";
    public static final String             AIRC_Converter_PowerSwitch_16      = "PowerSwitch_16";

    public static final String             AIRC_Converter_CurrentTemperature_        = "CurrentTemperature_";
    public static final String             AIRC_Converter_CurrentTemperature_1       = "CurrentTemperature_1";
    public static final String             AIRC_Converter_CurrentTemperature_2       = "CurrentTemperature_2";
    public static final String             AIRC_Converter_CurrentTemperature_3       = "CurrentTemperature_3";
    public static final String             AIRC_Converter_CurrentTemperature_4       = "CurrentTemperature_4";
    public static final String             AIRC_Converter_CurrentTemperature_5       = "CurrentTemperature_5";
    public static final String             AIRC_Converter_CurrentTemperature_6       = "CurrentTemperature_6";
    public static final String             AIRC_Converter_CurrentTemperature_7       = "CurrentTemperature_7";
    public static final String             AIRC_Converter_CurrentTemperature_8       = "CurrentTemperature_8";
    public static final String             AIRC_Converter_CurrentTemperature_9       = "CurrentTemperature_9";
    public static final String             AIRC_Converter_CurrentTemperature_10      = "CurrentTemperature_10";
    public static final String             AIRC_Converter_CurrentTemperature_11      = "CurrentTemperature_11";
    public static final String             AIRC_Converter_CurrentTemperature_12      = "CurrentTemperature_12";
    public static final String             AIRC_Converter_CurrentTemperature_13      = "CurrentTemperature_13";
    public static final String             AIRC_Converter_CurrentTemperature_14      = "CurrentTemperature_14";
    public static final String             AIRC_Converter_CurrentTemperature_15      = "CurrentTemperature_15";
    public static final String             AIRC_Converter_CurrentTemperature_16      = "CurrentTemperature_16";

    public static final String             AIRC_Converter_TargetTemperature_        = "TargetTemperature_";
    public static final String             AIRC_Converter_TargetTemperature_1       = "TargetTemperature_1";
    public static final String             AIRC_Converter_TargetTemperature_2       = "TargetTemperature_2";
    public static final String             AIRC_Converter_TargetTemperature_3       = "TargetTemperature_3";
    public static final String             AIRC_Converter_TargetTemperature_4       = "TargetTemperature_4";
    public static final String             AIRC_Converter_TargetTemperature_5       = "TargetTemperature_5";
    public static final String             AIRC_Converter_TargetTemperature_6       = "TargetTemperature_6";
    public static final String             AIRC_Converter_TargetTemperature_7       = "TargetTemperature_7";
    public static final String             AIRC_Converter_TargetTemperature_8       = "TargetTemperature_8";
    public static final String             AIRC_Converter_TargetTemperature_9       = "TargetTemperature_9";
    public static final String             AIRC_Converter_TargetTemperature_10      = "TargetTemperature_10";
    public static final String             AIRC_Converter_TargetTemperature_11      = "TargetTemperature_11";
    public static final String             AIRC_Converter_TargetTemperature_12      = "TargetTemperature_12";
    public static final String             AIRC_Converter_TargetTemperature_13      = "TargetTemperature_13";
    public static final String             AIRC_Converter_TargetTemperature_14      = "TargetTemperature_14";
    public static final String             AIRC_Converter_TargetTemperature_15      = "TargetTemperature_15";
    public static final String             AIRC_Converter_TargetTemperature_16      = "TargetTemperature_16";

    public static final String             AIRC_Converter_WindSpeed_        = "WindSpeed_";
    public static final String             AIRC_Converter_WindSpeed_1       = "WindSpeed_1";
    public static final String             AIRC_Converter_WindSpeed_2       = "WindSpeed_2";
    public static final String             AIRC_Converter_WindSpeed_3       = "WindSpeed_3";
    public static final String             AIRC_Converter_WindSpeed_4       = "WindSpeed_4";
    public static final String             AIRC_Converter_WindSpeed_5       = "WindSpeed_5";
    public static final String             AIRC_Converter_WindSpeed_6       = "WindSpeed_6";
    public static final String             AIRC_Converter_WindSpeed_7       = "WindSpeed_7";
    public static final String             AIRC_Converter_WindSpeed_8       = "WindSpeed_8";
    public static final String             AIRC_Converter_WindSpeed_9       = "WindSpeed_9";
    public static final String             AIRC_Converter_WindSpeed_10      = "WindSpeed_10";
    public static final String             AIRC_Converter_WindSpeed_11      = "WindSpeed_11";
    public static final String             AIRC_Converter_WindSpeed_12      = "WindSpeed_12";
    public static final String             AIRC_Converter_WindSpeed_13      = "WindSpeed_13";
    public static final String             AIRC_Converter_WindSpeed_14      = "WindSpeed_14";
    public static final String             AIRC_Converter_WindSpeed_15      = "WindSpeed_15";
    public static final String             AIRC_Converter_WindSpeed_16      = "WindSpeed_16";

    public static final String             AIRC_Converter_WorkMode_        = "WorkMode_";
    public static final String             AIRC_Converter_WorkMode_1       = "WorkMode_1";
    public static final String             AIRC_Converter_WorkMode_2       = "WorkMode_2";
    public static final String             AIRC_Converter_WorkMode_3       = "WorkMode_3";
    public static final String             AIRC_Converter_WorkMode_4       = "WorkMode_4";
    public static final String             AIRC_Converter_WorkMode_5       = "WorkMode_5";
    public static final String             AIRC_Converter_WorkMode_6       = "WorkMode_6";
    public static final String             AIRC_Converter_WorkMode_7       = "WorkMode_7";
    public static final String             AIRC_Converter_WorkMode_8       = "WorkMode_8";
    public static final String             AIRC_Converter_WorkMode_9       = "WorkMode_9";
    public static final String             AIRC_Converter_WorkMode_10      = "WorkMode_10";
    public static final String             AIRC_Converter_WorkMode_11      = "WorkMode_11";
    public static final String             AIRC_Converter_WorkMode_12      = "WorkMode_12";
    public static final String             AIRC_Converter_WorkMode_13      = "WorkMode_13";
    public static final String             AIRC_Converter_WorkMode_14      = "WorkMode_14";
    public static final String             AIRC_Converter_WorkMode_15      = "WorkMode_15";
    public static final String             AIRC_Converter_WorkMode_16      = "WorkMode_16";
    public static final Map<String, PTYPE> AIRC_Converter_Properties        = new HashMap<String, PTYPE>() {
        {
            put(AIRC_Converter_Nickname_1, PTYPE.t_text);
            put(AIRC_Converter_Nickname_2, PTYPE.t_text);
            put(AIRC_Converter_Nickname_3, PTYPE.t_text);
            put(AIRC_Converter_Nickname_4, PTYPE.t_text);
            put(AIRC_Converter_Nickname_5, PTYPE.t_text);
            put(AIRC_Converter_Nickname_6, PTYPE.t_text);
            put(AIRC_Converter_Nickname_7, PTYPE.t_text);
            put(AIRC_Converter_Nickname_8, PTYPE.t_text);
            put(AIRC_Converter_Nickname_9, PTYPE.t_text);
            put(AIRC_Converter_Nickname_10, PTYPE.t_text);
            put(AIRC_Converter_Nickname_11, PTYPE.t_text);
            put(AIRC_Converter_Nickname_12, PTYPE.t_text);
            put(AIRC_Converter_Nickname_13, PTYPE.t_text);
            put(AIRC_Converter_Nickname_14, PTYPE.t_text);
            put(AIRC_Converter_Nickname_15, PTYPE.t_text);
            put(AIRC_Converter_Nickname_16, PTYPE.t_text);

            put(AIRC_Converter_PowerSwitch_1, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_2, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_3, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_4, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_5, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_6, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_7, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_8, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_9, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_10, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_11, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_12, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_13, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_14, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_15, PTYPE.t_bool);
            put(AIRC_Converter_PowerSwitch_16, PTYPE.t_bool);

            put(AIRC_Converter_CurrentTemperature_1, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_2, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_3, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_4, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_5, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_6, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_7, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_8, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_9, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_10, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_11, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_12, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_13, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_14, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_15, PTYPE.t_double);
            put(AIRC_Converter_CurrentTemperature_16, PTYPE.t_double);

            put(AIRC_Converter_TargetTemperature_1, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_2, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_3, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_4, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_5, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_6, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_7, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_8, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_9, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_10, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_11, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_12, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_13, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_14, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_15, PTYPE.t_double);
            put(AIRC_Converter_TargetTemperature_16, PTYPE.t_double);

            put(AIRC_Converter_WindSpeed_1, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_2, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_3, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_4, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_5, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_6, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_7, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_8, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_9, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_10, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_11, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_12, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_13, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_14, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_15, PTYPE.t_enum);
            put(AIRC_Converter_WindSpeed_16, PTYPE.t_enum);

            put(AIRC_Converter_WorkMode_1, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_2, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_3, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_4, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_5, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_6, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_7, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_8, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_9, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_10, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_11, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_12, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_13, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_14, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_15, PTYPE.t_enum);
            put(AIRC_Converter_WorkMode_16, PTYPE.t_enum);
        }
    };

    // 定义空调四管制属性常量
    public static final String             AIRC_F_PowerSwitch       = "PowerSwitch";
    public static final String             AIRC_F_CurrentHumidity   = "CurrentHumidity";
    public static final String             AIRC_F_TargetTemperature = "TargetTemperature";
    public static final Map<String, PTYPE> AIRC_F_Properties        = new HashMap<String, PTYPE>() {
        {
            put(AIRC_F_PowerSwitch, PTYPE.t_bool);
            put(AIRC_F_CurrentHumidity, PTYPE.t_double);
            put(AIRC_F_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义拉菲地暖(电机)属性常量
    public static final String             FLOORH_001_PowerSwitch       = "PowerSwitch";
    public static final String             FLOORH_001_CurrentHumidity   = "CurrentHumidity";
    public static final String             FLOORH_001_TargetTemperature = "TargetTemperature";
    public static final Map<String, PTYPE> FLOORH_001_Properties        = new HashMap<String, PTYPE>() {
        {
            put(FLOORH_001_PowerSwitch, PTYPE.t_bool);
            put(FLOORH_001_CurrentHumidity, PTYPE.t_double);
            put(FLOORH_001_TargetTemperature, PTYPE.t_double);
        }
    };

    // 定义三键开关属性常量
    public static final String             TWS_P3_PowerSwitch_1 = "PowerSwitch_1";
    public static final String             TWS_P3_PowerSwitch_2 = "PowerSwitch_2";
    public static final String             TWS_P3_PowerSwitch_3 = "PowerSwitch_3";
    public static final String             TWS_P3_BackLightMode = "BackLightMode";
    public static final Map<String, PTYPE> TWS_3_Properties     = new HashMap<String, PTYPE>() {
        {
            put(TWS_P3_PowerSwitch_1, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_2, PTYPE.t_bool);
            put(TWS_P3_PowerSwitch_3, PTYPE.t_bool);
            put(TWS_P3_BackLightMode, PTYPE.t_enum);
        }
    };

    // 定义遥控按钮属性常量
    public static final String             RCB_P_EmergencyAlarm         = "EmergencyAlarm";
    public static final String             RCB_P_BatteryPercentage      = "BatteryPercentage";
    public static final Map<String, PTYPE> RCB_Properties               = new HashMap<String, PTYPE>() {
        {
            put(RCB_P_EmergencyAlarm, PTYPE.t_enum);
            put(RCB_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String             RCB_P_EmergencyAlarm_Trigger = "1";

    // 定义燃气传感器属性常量
    public static final String             GS_P_GasSensorState     = "GasSensorState";
    public static final Map<String, PTYPE> GS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(GS_P_GasSensorState, PTYPE.t_enum);
        }
    };
    public static final String             GS_P_GasSensorState_Has = "1";

    // 定义门磁传感器属性常量
    public static final String             DS_P_ContactState      = "ContactState";
    public static final Map<String, PTYPE> DS_Properties          = new HashMap<String, PTYPE>() {
        {
            put(DS_P_ContactState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String             DS_P_ContactState_Open = "1";

    // 定义烟雾传感器属性常量
    public static final String             SS_P_SmokeSensorState     = "SmokeSensorState";
    public static final Map<String, PTYPE> SS_Properties             = new HashMap<String, PTYPE>() {
        {
            put(SS_P_SmokeSensorState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String             SS_P_SmokeSensorState_Has = "1";

    // 定义温湿度传感器属性常量
    public static final String             THS_P_CurrentTemperature = "CurrentTemperature";
    public static final String             THS_P_CurrentHumidity    = "CurrentHumidity";
    public static final String             THS_P_PM25               = "PM25";
    public static final Map<String, PTYPE> THS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(THS_P_CurrentTemperature, PTYPE.t_double);
            put(THS_P_CurrentHumidity, PTYPE.t_double);
            put(THS_P_PM25, PTYPE.t_int32);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };

    // 全面屏开关 TargetTemperature_3
    public static final String             FSS_CurrentTemperature_1 = "CurrentTemperature_1";
    public static final String             FSS_TargetTemperature_1  = "TargetTemperature_1";
    public static final String             FSS_WorkMode_1           = "WorkMode_1";
    public static final String             FSS_PowerSwitch_1        = "PowerSwitch_1";
    public static final String             FSS_WindSpeed_1          = "WindSpeed_1";
    public static final String             FSS_PowerSwitch_2        = "PowerSwitch_2";
    public static final String             FSS_WindSpeed_2          = "WindSpeed_2";
    public static final String             FSS_PowerSwitch_3        = "PowerSwitch_3";
    public static final String             FSS_TargetTemperature_3  = "TargetTemperature_3";
    public static final Map<String, PTYPE> FSS_Properties           = new HashMap<String, PTYPE>() {
        {
            put(FSS_CurrentTemperature_1, PTYPE.t_double);
            put(FSS_TargetTemperature_1, PTYPE.t_double);
            put(FSS_WorkMode_1, PTYPE.t_enum);
            put(FSS_PowerSwitch_1, PTYPE.t_bool);
            put(FSS_WindSpeed_1, PTYPE.t_enum);
            put(FSS_PowerSwitch_2, PTYPE.t_bool);
            put(FSS_WindSpeed_2, PTYPE.t_enum);
            put(FSS_PowerSwitch_3, PTYPE.t_bool);
            put(FSS_TargetTemperature_3, PTYPE.t_double);
        }
    };

    // 定义水浸传感器属性常量
    public static final String             WS_P_WaterSensorState     = "WaterSensorState";
    public static final Map<String, PTYPE> WS_Properties             = new HashMap<String, PTYPE>() {
        {
            put(WS_P_WaterSensorState, PTYPE.t_enum);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String             WS_P_WaterSensorState_Has = "1";

    // 定义人体红外感应器属性常量
    public static final String             PIR_P_MotionAlarmState       = "MotionAlarmState";
    public static final Map<String, PTYPE> PIR_Properties               = new HashMap<String, PTYPE>() {
        {
            put(PIR_P_MotionAlarmState, PTYPE.t_bool);
            put(P_P_BatteryPercentage, PTYPE.t_double);
        }
    };
    public static final String             PIR_P_MotionAlarmState_Has   = "1";
    public static final String             PIR_P_MotionAlarmState_NoHas = "0";

    // 定义RGB彩色灯带属性常量
    public static final String             RGBLS_brightness               = "brightness";
    public static final String             RGBLS_powerstate               = "powerstate";
    public static final String             RGBLS_LightType                = "LightType";
    public static final String             RGBLS_mode                     = "mode";
    public static final String             RGBLS_colorTemperatureInKelvin = "colorTemperatureInKelvin";
    public static final String             RGBLS_LightMode                = "LightMode";
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
    public static final String             SL_batterypercentage = "BatteryPercentage";
    public static final String             SL_lockstate         = "LockState";
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
            put(PK_GATEWAY_RG4100_RY, GW_4100_Properties);
            put(PK_ONEWAYSWITCH_HY, OWS_Properties);
            put(PK_ONEWAYSWITCH_YQS_XB, OWS_Properties);
            put(PK_ONEWAYSWITCH_YQS_ZR, OWS_Properties);
            put(PK_ONEWAYSWITCH_LF, OWS_Properties);
            put(PK_ONEWAY_DANHUO_RY, OWS_Properties);
            put(PK_ONEWAYSWITCH, OWS_Properties);
            put(PK_TWOWAYSWITCH, TWS_Properties);
            put(PK_TWOWAYSWITCH_HY, TWS_Properties);
            put(PK_TWOWAYSWITCH_MODULE_HY, TWS_Properties);
            put(PK_TWOWAYSWITCH_YQS_XB, TWS_Properties);
            put(PK_TWOWAYSWITCH_YQS_ZR, TWS_Properties);
            put(PK_TWOWAYSWITCH_LF, TWS_Properties);
            put(PK_TWOWAY_DANHUO_RY, TWS_Properties);
            put(PK_GASSENSOR_HM, GS_Properties);
            put(PK_GASSENSOR_MLK, GS_Properties);
            put(PK_SMOKESENSOR_HM, SS_Properties);
            put(PK_SMOKESENSOR_MLK, SS_Properties);
            put(PK_PM_TEMHUMSENSOR_HY, THS_Properties);
            put(PK_PM_TEMHUMSENSOR_HY_PTM1005S, THS_Properties);
            put(PK_TEMHUMSENSOR_MLK, THS_Properties);
            put(PK_TEMHUMSENSOR_HM, THS_Properties);
            put(PK_WATERSENSOR_HM, WS_Properties);
            put(PK_WATERSENSOR_MLK, WS_Properties);
            put(PK_DOORSENSOR_HM, DS_Properties);
            put(PK_DOORSENSOR_MLK, DS_Properties);
            put(PK_PIRSENSOR_HM, PIR_Properties);
            put(PK_PIRSENSOR_MLK, PIR_Properties);
            put(PK_REMOTECONTRILBUTTON, RCB_Properties);
            put(PK_LIGHT, LIGHT_Properties);
            put(PK_RGB_COLOR_LIGHT_STRIP, RGBLS_Properties);
            put(PK_FOURWAYSWITCH, FWS_Properties);
            put(PK_FOURWAYSWITCH_2, FWS_Properties);
            put(PK_FOURWAYSWITCH_LF, FWS_Properties);
            put(PK_SIX_TWO_SCENE_SWITCH, SIX_SCENE_SWITCH_Properties);
            put(PK_AIRCOMDITION_TWO_LF, AIRC_T_Properties);
            put(PK_AIRCOMDITION_TWO_HY_U1, AIRC_T_Properties);
            put(PK_AIRCOMDITION_TWO_HY_U2, AIRC_T_Properties);
            put(PK_AIRCOMDITION_FOUR, AIRC_F_Properties);
            put(PK_FLOORHEATING001_LF, FLOORH_001_Properties);
            put(PK_WATER_FLOORHEAT_HY_U1, FLOORH_001_Properties);
            put(PK_WATER_FLOORHEAT_HY_U2, FLOORH_001_Properties);
            put(PK_ELEC_FLOORHEAT_HY_U1, FLOORH_001_Properties);
            put(PK_ELEC_FLOORHEAT_HY_U2, FLOORH_001_Properties);
            put(PK_THREE_KEY_SWITCH, TWS_3_Properties);
            put(PK_THREEWAYSWITCH_HY, TWS_3_Properties);
            put(PK_THREEWAYSWITCH_YQS_XB, TWS_3_Properties);
            put(PK_THREEWAYSWITCH_YQS_ZR, TWS_3_Properties);
            put(PK_THREEWAYSWITCH_LF, TWS_3_Properties);
            put(PK_THREEWAY_DANHUO_RY, TWS_3_Properties);
            put(PK_KDS_SMART_LOCK_A7, SL_Properties);
            put(PK_KDS_SMART_LOCK_K100, SL_Properties);
            put(PK_KDS_SMART_LOCK_S6, SL_Properties);
            put(PK_MM_SMART_LOCK, SL_Properties);
            put(PK_MS_SMART_LOCK, SL_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_LF, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_HY_U1, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_HY_U2, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_YQS_ZR, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_YQS_XB, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_LF_D8, WC_Properties);
            put(PK_ONEWAYWINDOWCURTAINS_LF_D9, WC_Properties);
            put(PK_TWOWAYWINDOWCURTAINS_LF, TWC_Properties);
            put(PK_TWOWAYWINDOWCURTAINS_YQS_ZR, TWC_Properties);
            put(PK_TWOWAYWINDOWCURTAINS_YQS_XB, TWC_Properties);
            put(PK_TWOWAYWINDOWCURTAINS_LF_D8, TWC_Properties);
            put(PK_TWOWAYWINDOWCURTAINS_LF_D9, TWC_Properties);
            put(PK_SIX_SCENE_SWITCH, PSS_Properties);
            put(PK_FOUR_TWO_SCENE_SWITCH_LF, FOUR_TWO_SCENE_SWITCH_Properties);
            put(PK_SIX_FOUR_SCENE_SWITCH_LF, SIX_FOUR_SCENE_SWITCH_Properties);
            put(PK_OUTLET_10A_LF, PK_OUTLET_Properties);
            put(PK_OUTLET_10A_HY_U1, PK_OUTLET_Properties);
            put(PK_OUTLET_10A_HY_U2, PK_OUTLET_Properties);
            put(PK_OUTLET_16A_HY_U1, PK_OUTLET_Properties);
            put(PK_OUTLET_16A_HY_U2, PK_OUTLET_Properties);
            put(PK_OUTLET_16A_LF, PK_OUTLET_Properties);
            put(PK_OUTLET_16A_YQS, PK_OUTLET_Properties);
            put(PK_OUTLET_10A_YQS, PK_OUTLET_Properties);
            put(PK_FOUR_SCENE_SWITCH, PFS_Properties);
            put(PK_FOUR_SCENE_SWITCH_LF, PFS_Properties);
            put(PK_ONE_SCENE_SWITCH, POS_Properties);
            put(PK_THREE_SCENE_SWITCH, PTS_Properties);
            put(PK_TWO_SCENE_SWITCH, P2S_Properties);
            put(PK_FULL_SCREEN_SWITCH_HY, FSS_Properties);
            put(PK_AIRCOMDITION_CONVERTER, AIRC_Converter_Properties);
        }
    };

    // 定义防撬事件常量
    public static final String P_E_TamperAlarm     = "TamperAlarm";
    // 定义布防报警事件常量
    public static final String P_E_ProtectionAlarm = "ProtectionAlarm";

    // 定义状态常量
    public static final int STATUS_ON  = 1;
    public static final int STATUS_OFF = 0;

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