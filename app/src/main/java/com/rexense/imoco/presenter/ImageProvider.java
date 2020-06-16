package com.rexense.imoco.presenter;

import android.content.Context;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;

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
                return R.drawable.icon_gateway;
        }
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
            return R.drawable.scene_background1;
        }

        if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_night_rise_on))){
            return R.drawable.scene_background1;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_unmanned_off))) {
            return R.drawable.scene_background2;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_alarm_on))) {
            return R.drawable.scene_background3;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_remote_control_on))) {
            return R.drawable.scene_background4;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_open_door_on))) {
            return R.drawable.scene_background5;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_bell_play))) {
            return R.drawable.scene_background6;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_alarm_play))) {
            return R.drawable.scene_background7;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_pir_deploy_alarm))) {
            return R.drawable.scene_background8;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_door_deploy_alarm))) {
            return R.drawable.scene_background9;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_go_home_pattern))) {
            return R.drawable.scene_background10;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_leave_home_pattern))) {
            return R.drawable.scene_background11;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_sleep_pattern))) {
            return R.drawable.scene_background12;
        } else if(sceneDescription.equalsIgnoreCase(context.getString(R.string.scenemodel_getup_pattern))) {
            return R.drawable.scene_background13;
        }

        return R.drawable.scene_background1;
    }
}
