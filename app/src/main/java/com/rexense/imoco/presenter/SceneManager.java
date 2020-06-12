package com.rexense.imoco.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-14 15:29
 * Description: 场景管理器
 */
public class SceneManager {
    private Context mContext;

    // 构造
    public SceneManager(Context context) {
        this.mContext = context;
    }

    // 查询场景列表
    public void querySceneList(String homeId, String catalogId, int pageNo, int pageSize,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERYSCENELIST;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("catalogId", catalogId);
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0  || pageSize > 30 ? 30 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERYSCENELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 生成场景模板列表
    public List<EScene.sceneModelEntry> genSceneModelList() {
        List<EScene.sceneModelEntry> list = new ArrayList<EScene.sceneModelEntry>();
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_recommend, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NIGHT_RISE_ON, R.string.scenemodel_night_rise_on, R.drawable.scen_background1));
        list.add(new EScene.sceneModelEntry(CScene.SMC_UNMANNED_OFF, R.string.scenemodel_unmanned_off, R.drawable.scen_background2));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_ON, R.string.scenemodel_alarm_on, R.drawable.scen_background3));
        list.add(new EScene.sceneModelEntry(CScene.SMC_REMOTE_CONTROL_ON, R.string.scenemodel_remote_control_on, R.drawable.scen_background4));
        list.add(new EScene.sceneModelEntry(CScene.SMC_OPEN_DOOR_ON, R.string.scenemodel_open_door_on, R.drawable.scen_background5));
        list.add(new EScene.sceneModelEntry(CScene.SMC_BELL_PLAY, R.string.scenemodel_bell_play, R.drawable.scen_background6));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_PLAY, R.string.scenemodel_alarm_play, R.drawable.scen_background7));
        list.add(new EScene.sceneModelEntry(CScene.SMC_PIR_DEPLOY_ALARM, R.string.scenemodel_pir_deploy_alarm, R.drawable.scen_background8));
        list.add(new EScene.sceneModelEntry(CScene.SMC_DOOR_DEPLOY_ALARM, R.string.scenemodel_door_deploy_alarm, R.drawable.scen_background9));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_one_key, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GO_HOME_PATTERN, R.string.scenemodel_go_home_pattern, R.drawable.scen_background10));
        list.add(new EScene.sceneModelEntry(CScene.SMC_LEAVE_HOME_PATTERN, R.string.scenemodel_leave_home_pattern, R.drawable.scen_background11));
        list.add(new EScene.sceneModelEntry(CScene.SMC_SLEEP_PATTERN, R.string.scenemodel_sleep_pattern, R.drawable.scen_background12));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GETUP_PATTERN, R.string.scenemodel_getup_pattern, R.drawable.scen_background13));
        return list;
    }

    // 获取场景描述
    public String getSceneDescription(int sceneModelCode){
        switch (sceneModelCode){
            case CScene.SMC_NIGHT_RISE_ON:
                return this.mContext.getString(R.string.scenemodel_night_rise_on);
            default:
                break;
        }

        return "";
    }

    // 生成指定场景模板参数列表
    public List<EScene.parameterEntry> genSceneModelParameterList(int sceneModelCode, List<EProduct.configListEntry> productList){
        List<EScene.parameterEntry> list = new ArrayList<EScene.parameterEntry>();

        // 触发设备处理
        List<EScene.triggerEntry> triggerEntries = this.getTrigger(sceneModelCode, productList);
        if(triggerEntries != null && triggerEntries.size() > 0){
            // 触发标题处理
            EScene.parameterEntry parameterTitle = new EScene.parameterEntry();
            parameterTitle.type = CScene.SPT_TRIGGER_TITLE;
            parameterTitle.typeName = this.mContext.getString(R.string.scene_maintain_trigger);
            list.add(parameterTitle);
            // 触发设备处理
            for(EScene.triggerEntry triggerEntry : triggerEntries){
                EScene.parameterEntry parameter = new EScene.parameterEntry();
                parameter.type = CScene.SPT_TRIGGER;
                parameter.triggerEntry = triggerEntry;
                list.add(parameter);
            }
        }

        // 时间条件处理
        EScene.conditionTimeEntry conditionTimeEntry = this.getConditionTime(sceneModelCode);
        // 状态条件处理
        List<EScene.conditionStateEntry> conditionStateEntries = this.getConditionState(sceneModelCode, productList);
        if(conditionTimeEntry != null || (conditionStateEntries != null && conditionStateEntries.size() > 0)){
            // 条件标题处理
            EScene.parameterEntry parameterTitle = new EScene.parameterEntry();
            parameterTitle.type = CScene.SPT_CONDITION_TITLE;
            parameterTitle.typeName = this.mContext.getString(R.string.scene_maintain_condition);
            list.add(parameterTitle);
            // 时间条件处理
            if(conditionTimeEntry != null) {
                EScene.parameterEntry parameterTime = new EScene.parameterEntry();
                parameterTime.type = CScene.SPT_CONDITION_TIME;
                parameterTime.conditionTimeEntry = conditionTimeEntry;
                list.add(parameterTime);
            }
            // 状态条件处理
            if(conditionStateEntries != null && conditionStateEntries.size() > 0){
                // 响应设备处理
                for(EScene.conditionStateEntry conditionStateEntry: conditionStateEntries){
                    EScene.parameterEntry parameter = new EScene.parameterEntry();
                    parameter.type = CScene.SPT_CONDITION_STATE;
                    parameter.conditionStateEntry = conditionStateEntry;
                    list.add(parameter);
                }
            }
        }

        // 响应设备处理
        List<EScene.responseEntry> responseEntries = this.getResponse(sceneModelCode, productList);
        if(responseEntries != null && responseEntries.size() > 0){
            // 响应标题处理
            EScene.parameterEntry parameterTitle = new EScene.parameterEntry();
            parameterTitle.type = CScene.SPT_RESPONSE_TITLE;
            parameterTitle.typeName = this.mContext.getString(R.string.scene_maintain_reponse);
            // 一键场景处理
            if(sceneModelCode >= CScene.SMC_GO_HOME_PATTERN){
                parameterTitle.typeName = this.mContext.getString(R.string.scene_maintain_reponse_only_action);
            }
            list.add(parameterTitle);
            // 响应设备处理
            for(EScene.responseEntry responseEntry : responseEntries){
                EScene.parameterEntry parameter = new EScene.parameterEntry();
                parameter.type = CScene.SPT_RESPONSE;
                parameter.responseEntry = responseEntry;
                list.add(parameter);
            }
        }

        return list;
    }

    // 检查参数(0失败,1成功)
    public int checkParameter(Context context, int sceneModelCode, List<EScene.parameterEntry> parameters){


        return 1;
    }

    // 获取指定场景模板的触发设备
    private List<EScene.triggerEntry> getTrigger(int sceneModelCode, List<EProduct.configListEntry> productList){
        List<EScene.triggerEntry> list = new ArrayList<EScene.triggerEntry>();
        switch (sceneModelCode){
            // 起夜开灯、红外布防报警处理
            case CScene.SMC_NIGHT_RISE_ON:
            case CScene.SMC_PIR_DEPLOY_ALARM:
                // 获取PIR
                Map<String, EDevice.deviceEntry> pir_has = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_PIRSENSOR);
                if(pir_has == null || pir_has.size() == 0){
                    this.addTriggerProduct(CTSL.PK_PIRSENSOR, sceneModelCode, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_PIRSENSOR, sceneModelCode, pir_has, list);
                }
                break;
            // 无人关灯处理
            case CScene.SMC_UNMANNED_OFF:
                // 获取PIR
                Map<String, EDevice.deviceEntry> pir_nohas = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_PIRSENSOR);
                if(pir_nohas == null || pir_nohas.size() == 0){
                    this.addTriggerProduct(CTSL.PK_PIRSENSOR, CScene.SMC_UNMANNED_OFF, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_PIRSENSOR, CScene.SMC_UNMANNED_OFF, pir_nohas, list);
                }
                break;
            // 报警开灯处理
            case CScene.SMC_ALARM_ON:
                // 获取烟感气传感器
                Map<String, EDevice.deviceEntry> smoke = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_SMOKESENSOR);
                if(smoke == null || smoke.size() == 0){
                    this.addTriggerProduct(CTSL.PK_SMOKESENSOR, CScene.SMC_ALARM_ON, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_SMOKESENSOR, CScene.SMC_ALARM_ON, smoke, list);
                }
                // 获取水浸气传感器
                Map<String, EDevice.deviceEntry> water = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_WATERSENSOR);
                if(water == null || water.size() == 0){
                    this.addTriggerProduct(CTSL.PK_WATERSENSOR, CScene.SMC_ALARM_ON, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_WATERSENSOR, CScene.SMC_ALARM_ON, water, list);
                }
                // 获取燃气传感器
                Map<String, EDevice.deviceEntry> gas = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GASSENSOR);
                if(gas == null || gas.size() == 0){
                    this.addTriggerProduct(CTSL.PK_GASSENSOR, CScene.SMC_ALARM_ON, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_GASSENSOR, CScene.SMC_ALARM_ON, gas, list);
                }
                break;
            // 遥控开灯、门铃播报、报警播报处理
            case CScene.SMC_REMOTE_CONTROL_ON:
            case CScene.SMC_BELL_PLAY:
            case CScene.SMC_ALARM_PLAY:
                // 获取遥控按钮
                Map<String, EDevice.deviceEntry> button = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_REMOTECONTRILBUTTON);
                if(button == null || button.size() == 0){
                    this.addTriggerProduct(CTSL.PK_REMOTECONTRILBUTTON, sceneModelCode, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_REMOTECONTRILBUTTON, sceneModelCode, button, list);
                }
                break;
            // 开门亮灯、门磁布防报警处理
            case CScene.SMC_OPEN_DOOR_ON:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                // 获取门磁
                Map<String, EDevice.deviceEntry> door = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_DOORSENSOR);
                if(door == null || door.size() == 0){
                    this.addTriggerProduct(CTSL.PK_DOORSENSOR, sceneModelCode, productList, list);
                } else {
                    this.addTriggerDevice(CTSL.PK_DOORSENSOR, sceneModelCode, door, list);
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 获取指定场景模板的时间条件
    private EScene.conditionTimeEntry getConditionTime(int sceneModelCode){
        switch (sceneModelCode){
            // 起夜开灯、无人关灯、开门亮灯、门铃播报、红外布防报警、门磁布防报警处理
            case CScene.SMC_NIGHT_RISE_ON:
            case CScene.SMC_UNMANNED_OFF:
            case CScene.SMC_OPEN_DOOR_ON:
            case CScene.SMC_BELL_PLAY:
            case CScene.SMC_PIR_DEPLOY_ALARM:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                return new EScene.conditionTimeEntry();
            default:
                break;
        }

        return null;
    }

    // 获取指定场景模板的状态条件
    private List<EScene.conditionStateEntry> getConditionState(int sceneModelCode, List<EProduct.configListEntry> productList){
        List<EScene.conditionStateEntry> list = new ArrayList<EScene.conditionStateEntry>();
        switch (sceneModelCode){
            // 红外布防报警、门磁布防报警处理
            case CScene.SMC_PIR_DEPLOY_ALARM:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                // 获取网关
                Map<String, EDevice.deviceEntry> gateway = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway == null || gateway.size() == 0){
                    this.addConditionStateProduct(CTSL.PK_GATEWAY, sceneModelCode, productList, list);
                } else {
                    this.addConditionStateDevice(CTSL.PK_GATEWAY, sceneModelCode, gateway, list);
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 获取指定场景模板的响应设备
    private List<EScene.responseEntry> getResponse(int sceneModelCode, List<EProduct.configListEntry> productList){
        List<EScene.responseEntry> list = new ArrayList<EScene.responseEntry>();
        switch (sceneModelCode){
            // 起夜开灯、报警开灯、遥控开灯、开门亮灯场景处理
            case CScene.SMC_NIGHT_RISE_ON:
            case CScene.SMC_ALARM_ON:
            case CScene.SMC_REMOTE_CONTROL_ON:
            case CScene.SMC_OPEN_DOOR_ON:
                // 1.获取一键单火开关
                Map<String, EDevice.deviceEntry> oneSwitch_on = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_ONEWAYSWITCH);
                if(oneSwitch_on == null || oneSwitch_on.size() == 0){
                    this.addResponseProduct(CTSL.PK_ONEWAYSWITCH, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_ONEWAYSWITCH, sceneModelCode, oneSwitch_on, list);
                }
                // 2.获取两键单火开关
                Map<String, EDevice.deviceEntry> twoSwitch_on = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_TWOWAYSWITCH);
                if(twoSwitch_on == null || twoSwitch_on.size() == 0){
                    this.addResponseProduct(CTSL.PK_TWOWAYSWITCH, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_TWOWAYSWITCH, sceneModelCode, twoSwitch_on, list);
                }
                break;
            // 无人关灯场景处理
            case CScene.SMC_UNMANNED_OFF:
                // 1.获取一键单火开关
                Map<String, EDevice.deviceEntry> oneSwitch_off = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_ONEWAYSWITCH);
                if(oneSwitch_off == null || oneSwitch_off.size() == 0){
                    this.addResponseProduct(CTSL.PK_ONEWAYSWITCH, CScene.SMC_UNMANNED_OFF, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_ONEWAYSWITCH, CScene.SMC_UNMANNED_OFF, oneSwitch_off, list);
                }
                // 2.获取两键单火开关
                Map<String, EDevice.deviceEntry> twoSwitch_off = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_TWOWAYSWITCH);
                if(twoSwitch_off == null || twoSwitch_off.size() == 0){
                    this.addResponseProduct(CTSL.PK_TWOWAYSWITCH, CScene.SMC_UNMANNED_OFF, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_TWOWAYSWITCH, CScene.SMC_UNMANNED_OFF, twoSwitch_off, list);
                }
                break;
            // 门铃播报场景处理
            case CScene.SMC_BELL_PLAY:
                // 1.获取网关
                Map<String, EDevice.deviceEntry> gateway = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway == null || gateway.size() == 0){
                    this.addResponseProduct(CTSL.PK_GATEWAY, CScene.SMC_BELL_PLAY, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_GATEWAY, CScene.SMC_BELL_PLAY, gateway, list);
                }
                break;
            // 报警播报、红外布防报警、门磁布防报警场景处理
            case CScene.SMC_ALARM_PLAY:
            case CScene.SMC_PIR_DEPLOY_ALARM:
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                // 1.获取网关
                Map<String, EDevice.deviceEntry> gateway_alarm = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway_alarm == null || gateway_alarm.size() == 0){
                    this.addResponseProduct(CTSL.PK_GATEWAY, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_GATEWAY, sceneModelCode, gateway_alarm, list);
                }
                break;
            // 回家模式场景处理
            case CScene.SMC_GO_HOME_PATTERN:
                // 1.获取网关
                Map<String, EDevice.deviceEntry> gateway_gohome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway_gohome == null || gateway_gohome.size() == 0){
                    this.addResponseProduct(CTSL.PK_GATEWAY, CScene.SMC_GO_HOME_PATTERN, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_GATEWAY, CScene.SMC_GO_HOME_PATTERN, gateway_gohome, list);
                }
                // 2.获取一键单火开关
                Map<String, EDevice.deviceEntry> oneSwitch_gohome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_ONEWAYSWITCH);
                if(oneSwitch_gohome == null || oneSwitch_gohome.size() == 0){
                    this.addResponseProduct(CTSL.PK_ONEWAYSWITCH, CScene.SMC_GO_HOME_PATTERN, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_ONEWAYSWITCH, CScene.SMC_GO_HOME_PATTERN, oneSwitch_gohome, list);
                }
                // 3.获取两键单火开关
                Map<String, EDevice.deviceEntry> twoSwitch_gonhome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_TWOWAYSWITCH);
                if(twoSwitch_gonhome == null || twoSwitch_gonhome.size() == 0){
                    this.addResponseProduct(CTSL.PK_TWOWAYSWITCH, CScene.SMC_GO_HOME_PATTERN, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_TWOWAYSWITCH, CScene.SMC_GO_HOME_PATTERN, twoSwitch_gonhome, list);
                }
                break;
            // 离家模式、睡觉模式场景处理
            case CScene.SMC_LEAVE_HOME_PATTERN:
            case CScene.SMC_SLEEP_PATTERN:
                // 1.获取网关
                Map<String, EDevice.deviceEntry> gateway_leavehome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway_leavehome == null || gateway_leavehome.size() == 0){
                    this.addResponseProduct(CTSL.PK_GATEWAY, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_GATEWAY, sceneModelCode, gateway_leavehome, list);
                }
                // 2.获取一键单火开关
                Map<String, EDevice.deviceEntry> oneSwitch_leavehome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_ONEWAYSWITCH);
                if(oneSwitch_leavehome == null || oneSwitch_leavehome.size() == 0){
                    this.addResponseProduct(CTSL.PK_ONEWAYSWITCH, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_ONEWAYSWITCH, sceneModelCode, oneSwitch_leavehome, list);
                }
                // 3.获取两键单火开关
                Map<String, EDevice.deviceEntry> twoSwitch_leavehome = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_TWOWAYSWITCH);
                if(twoSwitch_leavehome == null || twoSwitch_leavehome.size() == 0){
                    this.addResponseProduct(CTSL.PK_TWOWAYSWITCH, sceneModelCode, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_TWOWAYSWITCH, sceneModelCode, twoSwitch_leavehome, list);
                }
                break;
            // 起床模式场景处理
            case CScene.SMC_GETUP_PATTERN:
                // 1.获取网关
                Map<String, EDevice.deviceEntry> gateway_getup = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_GATEWAY);
                if(gateway_getup == null || gateway_getup.size() == 0){
                    this.addResponseProduct(CTSL.PK_GATEWAY, CScene.SMC_GETUP_PATTERN, productList, list);
                } else {
                    this.addResponseDevice(CTSL.PK_GATEWAY, CScene.SMC_GETUP_PATTERN, gateway_getup, list);
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 追加触发产品
    private void addTriggerProduct(String productKey, int sceneModelCode, List<EProduct.configListEntry> productList, List<EScene.triggerEntry> triggers){
        List<ETSL.stateEntry> list = CodeMapper.getPropertyTriggerState(this.mContext, productKey, sceneModelCode);
        String name = ProductHelper.getProductName(productKey, productList);
        if(list != null && list.size() > 0){
            for (ETSL.stateEntry state : list ){
                EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                triggerEntry.name = name;
                triggerEntry.productKey = productKey;
                triggerEntry.state = state;
                triggers.add(triggerEntry);
            }
        }
    }

    // 追加触发设备
    private void addTriggerDevice(String productKey, int sceneModelCode, Map<String, EDevice.deviceEntry> devices, List<EScene.triggerEntry> triggers){
        List<ETSL.stateEntry> list = CodeMapper.getPropertyTriggerState(this.mContext, productKey, sceneModelCode);
        for(EDevice.deviceEntry entry :devices.values()){
            for (ETSL.stateEntry sate : list ){
                EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                triggerEntry.productKey = productKey;
                triggerEntry.iotId = entry.iotId;
                triggerEntry.name = entry.nickName;
                triggerEntry.deviceName = entry.deviceName;
                triggerEntry.state = sate;
                triggers.add(triggerEntry);
            }
        }
    }

    // 追加状态条件产品
    private void addConditionStateProduct(String productKey, int sceneModelCode, List<EProduct.configListEntry> productList, List<EScene.conditionStateEntry> conditionStateEntries){
        List<ETSL.stateEntry> list = CodeMapper.getPropertyConditionState(this.mContext, productKey, sceneModelCode);
        String name = ProductHelper.getProductName(productKey, productList);
        if(list != null && list.size() > 0){
            for (ETSL.stateEntry state : list ){
                EScene.conditionStateEntry conditionStateEntry = new EScene.conditionStateEntry();
                conditionStateEntry.name = name;
                conditionStateEntry.productKey = productKey;
                conditionStateEntry.state = state;
                conditionStateEntries.add(conditionStateEntry);
            }
        }
    }

    // 追加状态条件设备
    private void addConditionStateDevice(String productKey, int sceneModelCode, Map<String, EDevice.deviceEntry> devices, List<EScene.conditionStateEntry> conditionStateEntries){
        List<ETSL.stateEntry> list = CodeMapper.getPropertyConditionState(this.mContext, productKey, sceneModelCode);
        for(EDevice.deviceEntry entry :devices.values()){
            for (ETSL.stateEntry sate : list ){
                EScene.conditionStateEntry conditionStateEntry = new EScene.conditionStateEntry();
                conditionStateEntry.productKey = productKey;
                conditionStateEntry.iotId = entry.iotId;
                conditionStateEntry.name = entry.nickName;
                conditionStateEntry.deviceName = entry.deviceName;
                conditionStateEntry.state = sate;
                conditionStateEntries.add(conditionStateEntry);
            }
        }
    }

    // 追加响应产品
    private void addResponseProduct(String productKey, int sceneModelCode, List<EProduct.configListEntry> productList, List<EScene.responseEntry> responses){
        List<ETSL.stateEntry> states = CodeMapper.getPropertyResponseState(this.mContext, productKey, sceneModelCode);
        List<ETSL.serviceEntry> services = CodeMapper.getServiceResponseAction(this.mContext, productKey, sceneModelCode);
        String name = ProductHelper.getProductName(productKey, productList);
        if(states != null && states.size() > 0){
            for(ETSL.stateEntry state : states){
                EScene.responseEntry responseEntry = new EScene.responseEntry();
                responseEntry.productKey = productKey;
                responseEntry.name = name;
                responseEntry.state = state;
                responses.add(responseEntry);
            }
        }
        if(services != null && services.size() > 0){
            for(ETSL.serviceEntry service : services){
                EScene.responseEntry responseEntry = new EScene.responseEntry();
                responseEntry.productKey = productKey;
                responseEntry.name = name;
                responseEntry.service = service;
                responses.add(responseEntry);
            }
        }
    }

    // 追加响应设备
    private void addResponseDevice(String productKey, int sceneModelCode, Map<String, EDevice.deviceEntry> devices, List<EScene.responseEntry> responses){
        List<ETSL.stateEntry> states = CodeMapper.getPropertyResponseState(this.mContext, productKey, sceneModelCode);
        List<ETSL.serviceEntry> services = CodeMapper.getServiceResponseAction(this.mContext, productKey, sceneModelCode);
        for(EDevice.deviceEntry entry :devices.values()){
            if(states != null && states.size() > 0){
                for(ETSL.stateEntry state : states){
                    EScene.responseEntry responseEntry = new EScene.responseEntry();
                    responseEntry.productKey = productKey;
                    responseEntry.iotId = entry.iotId;
                    responseEntry.name = entry.nickName;
                    responseEntry.deviceName = entry.deviceName;
                    responseEntry.state = state;
                    responses.add(responseEntry);
                }
            }
            if(services != null && services.size() > 0){
                for(ETSL.serviceEntry service : services){
                    EScene.responseEntry responseEntry = new EScene.responseEntry();
                    responseEntry.productKey = productKey;
                    responseEntry.iotId = entry.iotId;
                    responseEntry.name = entry.nickName;
                    responseEntry.deviceName = entry.deviceName;
                    responseEntry.service = service;
                    responses.add(responseEntry);
                }
            }
        }
    }

}
