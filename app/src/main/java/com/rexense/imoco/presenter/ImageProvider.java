package com.rexense.imoco.presenter;

import android.content.Context;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.sdk.TSL;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-24 10:29
 * Description: 图片提供者
 */
public class ImageProvider {
    // 生成产品图标
    public static int genProductIcon(String productKey) {
        if (productKey==null){
            return R.drawable.icon_gateway;
        }
        switch (productKey) {
            case CTSL.PK_GATEWAY:
                return R.drawable.icon_gateway;
            case CTSL.PK_ONEWAYSWITCH:
                return R.drawable.icon_oneswitch;
            case CTSL.PK_TWOWAYSWITCH:
                return R.drawable.icon_twoswitch;
            case CTSL.PK_REMOTECONTRILBUTTON:
                return R.drawable.icon_button;
            case CTSL.PK_DOORSENSOR:
                return R.drawable.icon_doorsensor;
            case CTSL.PK_GASSENSOR:
                return R.drawable.icon_gassensor;
            case CTSL.PK_SMOKESENSOR:
                return R.drawable.icon_smokesensor;
            case CTSL.PK_WATERSENSOR:
                return R.drawable.icon_watersensor;
            case CTSL.PK_PIRSENSOR:
                return R.drawable.icon_pirsensor;
            case CTSL.PK_TEMHUMSENSOR:
                return R.drawable.icon_thsensor;
            default:
                break;
        }
        return R.drawable.icon_gateway;
    }

    // 生成产品属性图标
    public static int genProductPropertyIcon(String productKey, String propertyName) {
        if (productKey==null){
            return R.drawable.icon_gateway;
        }
        switch (productKey) {
            case CTSL.PK_DOORSENSOR:
                return R.drawable.state_icon_door;
            case CTSL.PK_PIRSENSOR:
                return R.drawable.state_icon_pir;
            case CTSL.PK_GASSENSOR:
                return R.drawable.state_icon_smoke;
            case CTSL.PK_SMOKESENSOR:
                return R.drawable.state_icon_gas;
            case CTSL.PK_WATERSENSOR:
                return R.drawable.state_icon_water;
            case CTSL.PK_TEMHUMSENSOR:
                if(propertyName.equalsIgnoreCase(CTSL.THS_P_CurrentHumidity)){
                    return R.drawable.state_icon_humdity;
                }
                if(propertyName.equalsIgnoreCase(CTSL.THS_P_CurrentTemperature)){
                    return R.drawable.state_icon_temp;
                }
                break;
            default:
                break;
        }
        return R.drawable.icon_gateway;
    }

    // 生成设备状态图标
    public static int genDeviceStateIcon(String productKey, String property, String state) {
        switch (productKey) {
            case CTSL.PK_GATEWAY:
                // 网关处理
                if(property.equals(CTSL.GW_P_ArmMode)) {
                    if(state.equals(CTSL.GW_P_ArmMode_deploy)) {
                        return R.drawable.security_deploy;
                    } else {
                        return R.drawable.security_cancel;
                    }
                }
            case CTSL.PK_ONEWAYSWITCH:
                // 一键开关处理
                if(property.equals(CTSL.OWS_P_PowerSwitch_1)) {
                    if(state.equals(CTSL.S_P_PowerSwitch_On)) {
                        return R.drawable.state_oneswitch_on;
                    } else {
                        return R.drawable.state_oneswitch_off;
                    }
                }
            case CTSL.PK_TWOWAYSWITCH:
                // 两键开关处理
                if(property.equals(CTSL.TWS_P_PowerSwitch_1)) {
                    // 第一路处理
                    if(state.equals(CTSL.S_P_PowerSwitch_On)) {
                        return R.drawable.state_twoswitch_1_on;
                    } else {
                        return R.drawable.state_twoswitch_1_off;
                    }
                } else if(property.equals(CTSL.TWS_P_PowerSwitch_2)) {
                    // 第二路处理
                    if(state.equals(CTSL.S_P_PowerSwitch_On)) {
                        return R.drawable.state_twoswitch_2_on;
                    } else {
                        return R.drawable.state_twoswitch_2_off;
                    }
                }
            case CTSL.PK_DOORSENSOR:
                // 门磁传感器处理
                if(property.equals(CTSL.DS_P_ContactState)) {
                    if(state.equals(CTSL.DS_P_ContactState_Open)) {
                        return R.drawable.state_doorsensor_open;
                    } else {
                        return R.drawable.state_doorsensor_close;
                    }
                }
            case CTSL.PK_GASSENSOR:
                // 燃气传感器处理
                if(property.equals(CTSL.GS_P_GasSensorState)) {
                    if(state.equals(CTSL.GS_P_GasSensorState_Has)) {
                        return R.drawable.state_gassensor_has;
                    } else {
                        return R.drawable.state_gassensor_nonhas;
                    }
                }
            case CTSL.PK_SMOKESENSOR:
                // 烟雾传感器处理
                if(property.equals(CTSL.SS_P_SmokeSensorState)) {
                    if(state.equals(CTSL.SS_P_SmokeSensorState_Has)) {
                        return R.drawable.state_smokesensor_has;
                    } else {
                        return R.drawable.state_smokesensor_nonhas;
                    }
                }
            case CTSL.PK_WATERSENSOR:
                // 水浸传感器处理
                if(property.equals(CTSL.WS_P_WaterSensorState)) {
                    if(state.equals(CTSL.WS_P_WaterSensorState_Has)) {
                        return R.drawable.state_watersensor_has;
                    } else {
                        return R.drawable.state_watersensor_nonhas;
                    }
                }
            case CTSL.PK_PIRSENSOR:
                // 人体热释传感器处理
                if(property.equals(CTSL.PIR_P_MotionAlarmState)) {
                    if(state.equals(CTSL.PIR_P_MotionAlarmState_Has)) {
                        return R.drawable.state_pirsensor_has;
                    } else {
                        return R.drawable.state_pirsensor_nonhas;
                    }
                }
            default:
                return  genProductIcon(productKey);
        }
    }

    // 获取场景图标
    public static int genSceneIcon(Context context, String sceneDescription){
        if(sceneDescription == null) {
            return R.drawable.scene_night_rise_on;
        }

        if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_night_rise_on))){
            return R.drawable.scene_night_rise_on;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_unmanned_off))) {
            return R.drawable.scene_unmanned_off;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_alarm_on))) {
            return R.drawable.scene_alarm_on;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_remote_control_on))) {
            return R.drawable.scene_remote_control_on;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_open_door_on))) {
            return R.drawable.scene_open_door_on;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_bell_play))) {
            return R.drawable.scene_bell_play;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_alarm_play))) {
            return R.drawable.scene_alarm_play;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_pir_deploy_alarm))) {
            return R.drawable.scene_pir_deploy_alarm;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_door_deploy_alarm))) {
            return R.drawable.scene_door_deploy_alarm;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_go_home_pattern))) {
            return R.drawable.scene_go_home_pattern;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_leave_home_pattern))) {
            return R.drawable.scene_leave_home_pattern;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_sleep_pattern))) {
            return R.drawable.scene_sleep_pattern;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_getup_pattern))) {
            return R.drawable.scene_setup_pattern;
        }

        return R.drawable.scene_night_rise_on;
    }
}
