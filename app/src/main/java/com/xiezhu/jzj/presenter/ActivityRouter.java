package com.xiezhu.jzj.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aliyun.iot.aep.component.router.Router;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.view.ColorLightDetailActivity;
import com.xiezhu.jzj.view.DetailFourSwitchActivity;
import com.xiezhu.jzj.view.DetailGatewayActivity;
import com.xiezhu.jzj.view.DetailOneSwitchActivity;
import com.xiezhu.jzj.view.DetailSensorActivity;
import com.xiezhu.jzj.view.DetailThreeSwitchActivity;
import com.xiezhu.jzj.view.DetailTwoSwitchActivity;
import com.xiezhu.jzj.view.LockDetailActivity;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-29 15:29
 * Description: 界面路由
 */
public class ActivityRouter {
    // 到详细界面
    public static void toDetail(Context context, String iotId, String productKey, int status, String name, int owned) {
        Intent intent = null;
        switch (productKey) {
            case CTSL.PK_GATEWAY:
            case CTSL.PK_GATEWAY_RG4100:
                // 网关处理
                //intent = new Intent(context, LockDetailActivity.class);
                intent = new Intent(context, DetailGatewayActivity.class);
                break;
            case CTSL.PK_ONEWAYSWITCH:
                // 一键开关处理
                intent = new Intent(context, DetailOneSwitchActivity.class);
                break;
            case CTSL.PK_TWOWAYSWITCH:
                // 两键开关处理
                intent = new Intent(context, DetailTwoSwitchActivity.class);
                break;
            case CTSL.PK_WHITE_THREE_SWITCH:
                // 三键开关处理
                intent = new Intent(context, DetailThreeSwitchActivity.class);
                break;
				case CTSL.PK_FOURWAYSWITCH:
                // 四键开关处理
                intent = new Intent(context, DetailFourSwitchActivity.class);
                break;
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
            case CTSL.PK_SMART_LOCK:
                intent = new Intent(context, LockDetailActivity.class);
                break;
            case CTSL.PK_LIGHT:
                intent = new Intent(context, ColorLightDetailActivity.class);
                break;
            default:
                String code = "link://router/" + productKey;
                Bundle bundle = new Bundle();
                bundle.putString("iotId", iotId); // 传入插件参数，没有参数则不需要这一行
                Router.getInstance().toUrlForResult((Activity) context, code, 1, bundle);
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
