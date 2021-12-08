package com.rexense.wholehouse.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aliyun.iot.aep.component.router.Router;
import com.rexense.wholehouse.BuildConfig;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CTSL;
import com.rexense.wholehouse.view.AirConditionerConverterActivity;
import com.rexense.wholehouse.view.ColorLightDetailActivity;
import com.rexense.wholehouse.view.DetailFourSwitchActivity2;
import com.rexense.wholehouse.view.DetailGatewayActivity;
import com.rexense.wholehouse.view.DetailOneSwitchActivity2;
import com.rexense.wholehouse.view.DetailSensorActivity;
import com.rexense.wholehouse.view.DetailThreeSwitchActivity;
import com.rexense.wholehouse.view.DetailTwoSwitchActivity2;
import com.rexense.wholehouse.view.FourSceneSwitchActivity2;
import com.rexense.wholehouse.view.FourTwoSceneSwitchActivity;
import com.rexense.wholehouse.view.FullScreenSwitchActivity;
import com.rexense.wholehouse.view.LockDetailActivity;
import com.rexense.wholehouse.view.OneKeySceneDetailActivity2;
import com.rexense.wholehouse.view.OneWayCurtainsDetailActivity;
import com.rexense.wholehouse.view.SixFourSceneSwitchActivity;
import com.rexense.wholehouse.view.SixSceneSwitchActivity2;
import com.rexense.wholehouse.view.SixTwoSceneSwitchActivity2;
import com.rexense.wholehouse.view.ThreeSceneSwitchActivity2;
import com.rexense.wholehouse.view.TwoSceneSwitchActivity2;
import com.rexense.wholehouse.view.TwoWayCurtainsDetailActivity;
import com.rexense.wholehouse.view.USixSceneSwitchActivity2;
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
            case CTSL.PK_GATEWAY_RG4100_RY:
                // 网关处理
                //intent = new Intent(context, LockDetailActivity.class);
                intent = new Intent(context, DetailGatewayActivity.class);
                break;
            case CTSL.PK_ONEWAYSWITCH_HY:
            case CTSL.PK_ONEWAYSWITCH_YQS_XB:
            case CTSL.PK_ONEWAYSWITCH_YQS_ZR:
            case CTSL.PK_ONEWAYSWITCH_LF:
            case CTSL.PK_ONEWAY_DANHUO_RY:
            case CTSL.PK_ONEWAYSWITCH:
                // 一键开关处理
                intent = new Intent(context, DetailOneSwitchActivity2.class);
                break;
            case CTSL.PK_TWOWAYSWITCH:
            case CTSL.PK_TWOWAYSWITCH_HY:
            case CTSL.PK_TWOWAYSWITCH_MODULE_HY:
            case CTSL.PK_TWOWAYSWITCH_YQS_XB:
            case CTSL.PK_TWOWAYSWITCH_YQS_ZR:
            case CTSL.PK_TWOWAYSWITCH_LF:
            case CTSL.PK_TWOWAY_DANHUO_RY:
                // 两键开关处理
                intent = new Intent(context, DetailTwoSwitchActivity2.class);
                break;
            case CTSL.PK_FOURWAYSWITCH:
            case CTSL.PK_FOURWAYSWITCH_LF:
            case CTSL.PK_FOURWAYSWITCH_2: {
                intent = new Intent(context, DetailFourSwitchActivity2.class);
                break;
            }
            case CTSL.PK_DOORSENSOR_HM:
            case CTSL.PK_DOORSENSOR_MLK:
                // 门磁传感器处理
            case CTSL.PK_WATERSENSOR_HM:
            case CTSL.PK_WATERSENSOR_MLK:
                // 水浸传感器处理
            case CTSL.PK_GASSENSOR_HM:
                // 燃气传感器处理
            case CTSL.PK_GASSENSOR_MLK:
                // 燃气传感器处理
            case CTSL.PK_SMOKESENSOR_HM:
                // 烟雾传感器处理
            case CTSL.PK_SMOKESENSOR_MLK:
                // 烟雾传感器处理
            case CTSL.PK_PIRSENSOR_HM:
                // 人体热释传感器处理
            case CTSL.PK_PIRSENSOR_MLK:
                // 人体热释传感器处理
            case CTSL.PK_PM_TEMHUMSENSOR_HY:
            case CTSL.PK_PM_TEMHUMSENSOR_HY_PTM1005S:
            case CTSL.PK_TEMHUMSENSOR_MLK:
            case CTSL.PK_TEMHUMSENSOR_HM:
                // 温湿度传感器处理
            case CTSL.PK_REMOTECONTRILBUTTON:
                // 遥控按钮处理
                intent = new Intent(context, DetailSensorActivity.class);
                // 非燃气传感器有电源
                if (!productKey.equals(CTSL.PK_GASSENSOR_HM) &&
                        !productKey.equals(CTSL.PK_GASSENSOR_MLK)) {
                    intent.putExtra("isHasPowerSource", true);
                }
                break;
            case CTSL.PK_KDS_SMART_LOCK_A7:
            case CTSL.PK_KDS_SMART_LOCK_K100:
            case CTSL.PK_KDS_SMART_LOCK_S6:
            case CTSL.PK_MM_SMART_LOCK:
            case CTSL.PK_MS_SMART_LOCK:
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
            case CTSL.PK_U_SIX_SCENE_SWITCH:
            case CTSL.PK_U_SIX_SCENE_SWITCH_HY:
                intent = new Intent(context, USixSceneSwitchActivity2.class);
                break;
            case CTSL.PK_SIX_SCENE_SWITCH_YQS_XB:
            case CTSL.PK_SIX_SCENE_SWITCH_YQS_ZR:
            case CTSL.PK_SIX_SCENE_SWITCH:
                intent = new Intent(context, SixSceneSwitchActivity2.class);
                break;
            case CTSL.PK_ANY_TWO_SCENE_SWITCH:
            case CTSL.PK_TWO_SCENE_SWITCH:
                intent = new Intent(context, TwoSceneSwitchActivity2.class);
                break;
            case CTSL.PK_FOUR_TWO_SCENE_SWITCH_LF:
                // 二开关+二场景开关-拉斐
                intent = new Intent(context, FourTwoSceneSwitchActivity.class);
                break;
            case CTSL.PK_SIX_FOUR_SCENE_SWITCH_LF: {
                // 二开关+四场景开关-拉斐
                intent = new Intent(context, SixFourSceneSwitchActivity.class);
                break;
            }
            case CTSL.PK_THREE_SCENE_SWITCH:
                intent = new Intent(context, ThreeSceneSwitchActivity2.class);
                break;
            case CTSL.PK_ANY_FOUR_SCENE_SWITCH:
            case CTSL.PK_FOUR_SCENE_SWITCH:
            case CTSL.PK_FOUR_SCENE_SWITCH_LF:
                intent = new Intent(context, FourSceneSwitchActivity2.class);
                break;
            case CTSL.PK_FULL_SCREEN_SWITCH_HY: {
                // 全面屏开关
                ViseLog.d("iotId = " + iotId + " , productKey = " + productKey);
                intent = new Intent(context, FullScreenSwitchActivity.class);
                break;
            }
            case CTSL.PK_THREEWAYSWITCH_HY:
            case CTSL.PK_THREEWAYSWITCH_YQS_XB:
            case CTSL.PK_THREEWAYSWITCH_YQS_ZR:
            case CTSL.PK_THREEWAYSWITCH_LF:
            case CTSL.PK_THREEWAY_DANHUO_RY:
            case CTSL.PK_THREE_KEY_SWITCH: {
                // 三键开关
                intent = new Intent(context, DetailThreeSwitchActivity.class);
                break;
            }
            case CTSL.PK_ONEWAYWINDOWCURTAINS_LF:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_HY_U1:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_HY_U2:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_YQS_ZR:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_YQS_XB:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_LF_D8:
            case CTSL.PK_ONEWAYWINDOWCURTAINS_LF_D9: {
                // 单路窗帘
                //if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID))
                intent = new Intent(context, OneWayCurtainsDetailActivity.class);
                break;
            }
            case "a1UnLiHBScD":
            case CTSL.PK_TWOWAYWINDOWCURTAINS_LF:
            case CTSL.PK_TWOWAYWINDOWCURTAINS_YQS_ZR:
            case CTSL.PK_TWOWAYWINDOWCURTAINS_YQS_XB:
            case CTSL.PK_TWOWAYWINDOWCURTAINS_LF_D8:
            case CTSL.PK_TWOWAYWINDOWCURTAINS_LF_D9: {
                // 双路窗帘
                //if ("com.laffey.smart".equals(BuildConfig.APPLICATION_ID))
                intent = new Intent(context, TwoWayCurtainsDetailActivity.class);
                break;
            }
            case CTSL.PK_AIRCOMDITION_CONVERTER: {
                // 空调转换器-鸿雁
                intent = new Intent(context, AirConditionerConverterActivity.class);
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
