package com.rexense.imoco.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aliyun.iot.aep.component.router.Router;
import com.rexense.imoco.BuildConfig;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.view.ColorLightDetailActivity;
import com.rexense.imoco.view.DetailFourSwitchActivity;
import com.rexense.imoco.view.DetailFourSwitchActivity2;
import com.rexense.imoco.view.DetailGatewayActivity;
import com.rexense.imoco.view.DetailOneSwitchActivity;
import com.rexense.imoco.view.DetailOneSwitchActivity2;
import com.rexense.imoco.view.DetailSensorActivity;
import com.rexense.imoco.view.DetailThreeSwitchActivity;
import com.rexense.imoco.view.DetailTwoSwitchActivity;
import com.rexense.imoco.view.DetailTwoSwitchActivity2;
import com.rexense.imoco.view.FourSceneSwitchActivity;
import com.rexense.imoco.view.FourSceneSwitchActivity2;
import com.rexense.imoco.view.FullScreenSwitchActivity;
import com.rexense.imoco.view.LockDetailActivity;
import com.rexense.imoco.view.OneKeySceneDetailActivity;
import com.rexense.imoco.view.OneKeySceneDetailActivity2;
import com.rexense.imoco.view.OneWayCurtainsDetailActivity;
import com.rexense.imoco.view.SixFourSceneSwitchActivity;
import com.rexense.imoco.view.SixSceneSwitchActivity;
import com.rexense.imoco.view.SixSceneSwitchActivity2;
import com.rexense.imoco.view.SixTwoSceneSwitchActivity;
import com.rexense.imoco.view.SixTwoSceneSwitchActivity2;
import com.rexense.imoco.view.ThreeSceneSwitchActivity;
import com.rexense.imoco.view.ThreeSceneSwitchActivity2;
import com.rexense.imoco.view.TwoSceneSwitchActivity;
import com.rexense.imoco.view.TwoSceneSwitchActivity2;
import com.rexense.imoco.view.TwoWayCurtainsDetailActivity;
import com.vise.log.ViseLog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-29 15:29
 * Description: 界面路由
 */
public class ActivityRouter {
    // 到详细界面
    public static void toDetail(Context context, String iotId, String productKey, int status, String name, int owned) {
        Intent intent = null;

        ViseLog.d("跳转插件 iotId = " + iotId + " , productKey = " + productKey
                + "\nBuildConfig.APPLICATION_ID = " + BuildConfig.APPLICATION_ID
                + "\nstatus = " + status
                + "\nname = " + name
                + "\nowned = " + owned);
        switch (productKey) {
            case CTSL.PK_GATEWAY:
            case CTSL.PK_GATEWAY_RG4100:
                // 网关处理
                //intent = new Intent(context, LockDetailActivity.class);
                intent = new Intent(context, DetailGatewayActivity.class);
                break;
            case CTSL.PK_ONEWAYSWITCH:
                // 一键开关处理
                intent = new Intent(context, DetailOneSwitchActivity2.class);
                break;
            case CTSL.PK_TWOWAYSWITCH:
                // 两键开关处理
                intent = new Intent(context, DetailTwoSwitchActivity2.class);
                break;
            case CTSL.PK_FOURWAYSWITCH:
            case CTSL.PK_FOURWAYSWITCH_2: {
                intent = new Intent(context, DetailFourSwitchActivity2.class);
                break;
            }
            case CTSL.PK_DOORSENSOR:
                // 门磁传感器处理
            case CTSL.PK_WATERSENSOR:
                // 水浸传感器处理
            case CTSL.PK_GASSENSOR:
                // 燃气传感器处理
            case CTSL.PK_SMOKESENSOR:
                // 烟雾传感器处理
            case CTSL.PK_PIRSENSOR:
                // 人体热释传感器处理
            case CTSL.PK_TEMHUMSENSOR:
                // 温湿度传感器处理
            case CTSL.PK_REMOTECONTRILBUTTON:
                // 遥控按钮处理
                intent = new Intent(context, DetailSensorActivity.class);
                // 非燃气传感器有电源
                if (!productKey.equals(CTSL.PK_GASSENSOR)) {
                    intent.putExtra("isHasPowerSource", true);
                }
                break;
            case CTSL.PK_SMART_LOCK_A7:
                intent = new Intent(context, LockDetailActivity.class);
                break;
            case CTSL.PK_LIGHT:
                intent = new Intent(context, ColorLightDetailActivity.class);
                break;
            case CTSL.PK_ONE_SCENE_SWITCH:
                intent = new Intent(context, OneKeySceneDetailActivity2.class);
                break;
            case CTSL.PK_SIX_TWO_SCENE_SWITCH:
                intent = new Intent(context, SixTwoSceneSwitchActivity2.class);
                break;
            case CTSL.PK_SIX_SCENE_SWITCH:
                intent = new Intent(context, SixSceneSwitchActivity2.class);
                break;
            case CTSL.PK_ANY_TWO_SCENE_SWITCH:
            case CTSL.PK_TWO_SCENE_SWITCH:
                intent = new Intent(context, TwoSceneSwitchActivity2.class);
                break;
            case CTSL.PK_THREE_SCENE_SWITCH:
                intent = new Intent(context, ThreeSceneSwitchActivity2.class);
                break;
            case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
            case CTSL.PK_FOUR_SCENE_SWITCH:
                intent = new Intent(context, FourSceneSwitchActivity2.class);
                break;
            case CTSL.TEST_PK_FULL_SCREEN_SWITCH: {
                // 全面屏开关
                ViseLog.d("iotId = " + iotId + " , productKey = " + productKey);
                intent = new Intent(context, FullScreenSwitchActivity.class);
                break;
            }
            case CTSL.PK_THREE_KEY_SWITCH: {
                // 三键开关
                intent = new Intent(context, DetailThreeSwitchActivity.class);
                break;
            }
            case CTSL.TEST_PK_ONEWAYWINDOWCURTAINS: {
                // 单路窗帘
                //if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID))
                intent = new Intent(context, OneWayCurtainsDetailActivity.class);
                break;
            }
            case CTSL.TEST_PK_TWOWAYWINDOWCURTAINS: {
                // 双路窗帘
                //if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID))
                intent = new Intent(context, TwoWayCurtainsDetailActivity.class);
                break;
            }
            default:
                String code = "link://router/" + productKey;
                Bundle bundle = new Bundle();
                bundle.putString("iotId", iotId); // 传入插件参数，没有参数则不需要这一行
                //Router.getInstance().toUrlForResult((Activity) context, code, 1, bundle);
                Router.getInstance().toUrl((Activity) context, code, bundle);
                break;
        }
        ViseLog.d("intent != null = " + (intent != null));
        if (intent != null) {
            intent.putExtra("iotId", iotId);
            intent.putExtra("productKey", productKey);
            intent.putExtra("status", status);
            intent.putExtra("name", name);
            intent.putExtra("owned", owned);
            context.startActivity(intent);
        }
    }
}
