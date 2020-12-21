package com.xiezhu.jzj.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.contract.CTSL;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EAPIChannel;
import com.xiezhu.jzj.model.EDevice;
import com.xiezhu.jzj.model.EProduct;
import com.xiezhu.jzj.model.EScene;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.sdk.APIChannel;
import com.xiezhu.jzj.utility.Dialog;
import com.xiezhu.jzj.utility.Logger;


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

    // 检查参数(0失败,1成功)
    public Boolean checkParameter(int currentSceneNumber, int sceneModelCode, List<EScene.parameterEntry> parameters){
        if(currentSceneNumber >= CScene.RULE_MAX_NUMBER){
            Dialog.confirm(this.mContext, R.string.dialog_title, String.format(this.mContext.getString(R.string.scene_maintain_maxnumber_hint), CScene.RULE_MAX_NUMBER), R.drawable.dialog_fail, R.string.dialog_ok, false);
            return false;
        }
        if (parameters == null){
            return false;
        }

        int triggerNumber = 0, conditionNumber = 0, actionNumber = 0;
        for(EScene.parameterEntry para : parameters){
            if(para.type == CScene.SPT_TRIGGER && para.triggerEntry != null && para.triggerEntry.isSelected){
                triggerNumber++;
            }
            if(para.type == CScene.SPT_CONDITION_TIME && para.conditionTimeEntry != null && para.conditionTimeEntry.isSelected){
                conditionNumber++;
            }
            if(para.type == CScene.SPT_CONDITION_STATE && para.conditionStateEntry != null && para.conditionStateEntry.isSelected){
                conditionNumber++;
            }
            if(para.type == CScene.SPT_RESPONSE && para.responseEntry != null && para.responseEntry.isSelected){
                actionNumber++;
            }
        }

        if(sceneModelCode <= CScene.SMC_AUTOMATIC_MAX){
            if(triggerNumber == 0){
                Dialog.confirm(this.mContext, R.string.dialog_title, this.mContext.getString(R.string.scene_maintain_trigger_hint), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
            if(triggerNumber > CScene.RULE_ONE_SCENE_MAX_TRIGGER){
                Dialog.confirm(this.mContext, R.string.dialog_title, String.format(this.mContext.getString(R.string.scene_maintain_trigger_maxnumber_hint), CScene.RULE_ONE_SCENE_MAX_TRIGGER, CScene.RULE_ONE_SCENE_MAX_TRIGGER), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
            if(conditionNumber > CScene.RULE_ONE_SCENE_MAX_CONDITION){
                Dialog.confirm(this.mContext, R.string.dialog_title, String.format(this.mContext.getString(R.string.scene_maintain_condition_maxnumber_hint), CScene.RULE_ONE_SCENE_MAX_TRIGGER, CScene.RULE_ONE_SCENE_MAX_TRIGGER), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
            if(actionNumber == 0){
                Dialog.confirm(this.mContext, R.string.dialog_title, this.mContext.getString(R.string.scene_maintain_action_hint1), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
            if(actionNumber > CScene.RULE_ONE_SCENE_MAX_ACTION){
                Dialog.confirm(this.mContext, R.string.dialog_title, String.format(this.mContext.getString(R.string.scene_maintain_action_maxnumber_hint1), CScene.RULE_ONE_SCENE_MAX_ACTION, CScene.RULE_ONE_SCENE_MAX_ACTION), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
        } else {
            if(actionNumber == 0){
                Dialog.confirm(this.mContext, R.string.dialog_title, this.mContext.getString(R.string.scene_maintain_action_hint2), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
            if(actionNumber > CScene.RULE_ONE_SCENE_MAX_ACTION){
                Dialog.confirm(this.mContext, R.string.dialog_title, String.format(this.mContext.getString(R.string.scene_maintain_action_maxnumber_hint2), CScene.RULE_ONE_SCENE_MAX_ACTION, CScene.RULE_ONE_SCENE_MAX_ACTION), R.drawable.dialog_fail, R.string.dialog_ok, false);
                return false;
            }
        }

        return true;
    }

    // 生成场景模板列表
    public List<EScene.sceneModelEntry> genSceneModelList() {
        List<EScene.sceneModelEntry> list = new ArrayList<EScene.sceneModelEntry>();
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_recommend, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NIGHT_RISE_ON, R.string.scenemodel_night_rise_on, R.drawable.scene_night_rise_on));
        list.add(new EScene.sceneModelEntry(CScene.SMC_UNMANNED_OFF, R.string.scenemodel_unmanned_off, R.drawable.scene_unmanned_off));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_ON, R.string.scenemodel_alarm_on, R.drawable.scene_alarm_on));
        list.add(new EScene.sceneModelEntry(CScene.SMC_REMOTE_CONTROL_ON, R.string.scenemodel_remote_control_on, R.drawable.scene_remote_control_on));
        list.add(new EScene.sceneModelEntry(CScene.SMC_OPEN_DOOR_ON, R.string.scenemodel_open_door_on, R.drawable.scene_open_door_on));
        list.add(new EScene.sceneModelEntry(CScene.SMC_BELL_PLAY, R.string.scenemodel_bell_play, R.drawable.scene_bell_play));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_PLAY, R.string.scenemodel_alarm_play, R.drawable.scene_alarm_play));
        list.add(new EScene.sceneModelEntry(CScene.SMC_PIR_DEPLOY_ALARM, R.string.scenemodel_pir_deploy_alarm, R.drawable.scene_pir_deploy_alarm));
        list.add(new EScene.sceneModelEntry(CScene.SMC_DOOR_DEPLOY_ALARM, R.string.scenemodel_door_deploy_alarm, R.drawable.scene_door_deploy_alarm));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_one_key, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GO_HOME_PATTERN, R.string.scenemodel_go_home_pattern, R.drawable.scene_go_home_pattern));
        list.add(new EScene.sceneModelEntry(CScene.SMC_LEAVE_HOME_PATTERN, R.string.scenemodel_leave_home_pattern, R.drawable.scene_leave_home_pattern));
        list.add(new EScene.sceneModelEntry(CScene.SMC_SLEEP_PATTERN, R.string.scenemodel_sleep_pattern, R.drawable.scene_sleep_pattern));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GETUP_PATTERN, R.string.scenemodel_getup_pattern, R.drawable.scene_setup_pattern));
        return list;
    }

    // 获取场景模板名称
    public String getSceneModelName(int sceneModelCode){
        switch (sceneModelCode){
            case CScene.SMC_NIGHT_RISE_ON:
                return this.mContext.getString(R.string.scenemodel_night_rise_on);
            case CScene.SMC_UNMANNED_OFF:
                return this.mContext.getString(R.string.scenemodel_unmanned_off);
            case CScene.SMC_ALARM_ON:
                return this.mContext.getString(R.string.scenemodel_alarm_on);
            case CScene.SMC_REMOTE_CONTROL_ON:
                return this.mContext.getString(R.string.scenemodel_remote_control_on);
            case CScene.SMC_OPEN_DOOR_ON:
                return this.mContext.getString(R.string.scenemodel_open_door_on);
            case CScene.SMC_BELL_PLAY:
                return this.mContext.getString(R.string.scenemodel_bell_play);
            case CScene.SMC_ALARM_PLAY:
                return this.mContext.getString(R.string.scenemodel_alarm_play);
            case CScene.SMC_PIR_DEPLOY_ALARM:
                return this.mContext.getString(R.string.scenemodel_pir_deploy_alarm);
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                return this.mContext.getString(R.string.scenemodel_door_deploy_alarm);
            case CScene.SMC_GO_HOME_PATTERN:
                return this.mContext.getString(R.string.scenemodel_go_home_pattern);
            case CScene.SMC_LEAVE_HOME_PATTERN:
                return this.mContext.getString(R.string.scenemodel_leave_home_pattern);
            case CScene.SMC_SLEEP_PATTERN:
                return this.mContext.getString(R.string.scenemodel_sleep_pattern);
            case CScene.SMC_GETUP_PATTERN:
                return this.mContext.getString(R.string.scenemodel_getup_pattern);
            default:
                break;
        }

        return "";
    }

    // 获取场景模板描述
    public String getSceneModelDescription(int sceneModelCode){
        switch (sceneModelCode){
            case CScene.SMC_NIGHT_RISE_ON:
                return this.mContext.getString(R.string.scenemodel_night_rise_on_ds);
            case CScene.SMC_UNMANNED_OFF:
                return this.mContext.getString(R.string.scenemodel_unmanned_off_ds);
            case CScene.SMC_ALARM_ON:
                return this.mContext.getString(R.string.scenemodel_alarm_on_ds);
            case CScene.SMC_REMOTE_CONTROL_ON:
                return this.mContext.getString(R.string.scenemodel_remote_control_on_ds);
            case CScene.SMC_OPEN_DOOR_ON:
                return this.mContext.getString(R.string.scenemodel_open_door_on_ds);
            case CScene.SMC_BELL_PLAY:
                return this.mContext.getString(R.string.scenemodel_bell_play_ds);
            case CScene.SMC_ALARM_PLAY:
                return this.mContext.getString(R.string.scenemodel_alarm_play_ds);
            case CScene.SMC_PIR_DEPLOY_ALARM:
                return this.mContext.getString(R.string.scenemodel_pir_deploy_alarm_ds);
            case CScene.SMC_DOOR_DEPLOY_ALARM:
                return this.mContext.getString(R.string.scenemodel_door_deploy_alarm_ds);
            case CScene.SMC_GO_HOME_PATTERN:
                return this.mContext.getString(R.string.scenemodel_go_home_pattern_ds);
            case CScene.SMC_LEAVE_HOME_PATTERN:
                return this.mContext.getString(R.string.scenemodel_leave_home_pattern_ds);
            case CScene.SMC_SLEEP_PATTERN:
                return this.mContext.getString(R.string.scenemodel_sleep_pattern_ds);
            case CScene.SMC_GETUP_PATTERN:
                return this.mContext.getString(R.string.scenemodel_getup_pattern_ds);
            default:
                break;
        }

        return "";
    }

    // 获取场景模板代码
    public int getSceneModelCode(String sceneDescription){
        if(sceneDescription == null) {
            return -1;
        }

        if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_night_rise_on))){
            return CScene.SMC_NIGHT_RISE_ON;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_unmanned_off))) {
            return CScene.SMC_UNMANNED_OFF;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_alarm_on))) {
            return CScene.SMC_ALARM_ON;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_remote_control_on))) {
            return CScene.SMC_REMOTE_CONTROL_ON;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_open_door_on))) {
            return CScene.SMC_OPEN_DOOR_ON;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_bell_play))) {
            return CScene.SMC_BELL_PLAY;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_alarm_play))) {
            return CScene.SMC_ALARM_PLAY;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_pir_deploy_alarm))) {
            return CScene.SMC_PIR_DEPLOY_ALARM;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_door_deploy_alarm))) {
            return CScene.SMC_DOOR_DEPLOY_ALARM;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_go_home_pattern))) {
            return CScene.SMC_GO_HOME_PATTERN;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_leave_home_pattern))) {
            return CScene.SMC_LEAVE_HOME_PATTERN;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_sleep_pattern))) {
            return CScene.SMC_SLEEP_PATTERN;
        } else if(sceneDescription.equalsIgnoreCase(this.mContext.getString(R.string.scenemodel_getup_pattern))) {
            return CScene.SMC_GETUP_PATTERN;
        }

        return -1;
    }

    // 创建场景
    public void create(EScene.sceneBaseInfoEntry baseInfo, List<EScene.parameterEntry> parameters,
                       Handler commitFailureHandler,
                       Handler responseErrorHandler,
                       Handler processDataHandler) {
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CREATESCENE;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("homeId", baseInfo.homeId);
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("description", baseInfo.description);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        // 构造触发Triggers
        boolean isHasTrigger = false;
        JSONObject triggers = new JSONObject();
        triggers.put("uri", "logical/or");
        JSONArray items = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_TRIGGER || parameter.triggerEntry == null || !parameter.triggerEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "trigger/device/property");
            JSONObject params = new JSONObject();
            params.put("productKey", parameter.triggerEntry.productKey);
            params.put("deviceName", parameter.triggerEntry.deviceName);
            params.put("propertyName", parameter.triggerEntry.state.rawName);
            params.put("compareType", "==");
            params.put("compareValue", Integer.parseInt(parameter.triggerEntry.state.rawValue));
            item.put("params", params);
            items.add(item);
            isHasTrigger = true;
        }
        if(isHasTrigger){
            triggers.put("items", items);
            requestParameterEntry.addParameter("triggers", triggers);
        }
        // 构造时间条件
        boolean isHasConditionTime = false;
        JSONObject conditions = new JSONObject();
        conditions.put("uri", "logical/and");
        JSONArray conditions_items = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_CONDITION_TIME || parameter.conditionTimeEntry == null || !parameter.conditionTimeEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "condition/timeRange");
            JSONObject params = new JSONObject();
            params.put("cron", parameter.conditionTimeEntry.genCronString());
            params.put("cronType", "linux");
            params.put("timezoneID", "Asia/Shanghai");
            item.put("params", params);
            conditions_items.add(item);
            isHasConditionTime = true;
        }
        // 构造属性状态条件
        boolean isHasConditionState = false;
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_CONDITION_STATE || parameter.conditionStateEntry == null || !parameter.conditionStateEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "condition/device/property");
            JSONObject params = new JSONObject();
            params.put("productKey", parameter.conditionStateEntry.productKey);
            params.put("deviceName", parameter.conditionStateEntry.deviceName);
            params.put("propertyName", parameter.conditionStateEntry.state.rawName);
            params.put("compareType", "==");
            params.put("compareValue", Integer.parseInt(parameter.conditionStateEntry.state.rawValue));
            item.put("params", params);
            conditions_items.add(item);
            isHasConditionState = true;
        }
        if(isHasConditionTime || isHasConditionState){
            conditions.put("items", conditions_items);
            requestParameterEntry.addParameter("conditions", conditions);
        }
        // 构造响应Actions
        boolean isHasAction = false;
        JSONArray actions = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_RESPONSE || parameter.responseEntry == null || !parameter.responseEntry.isSelected ||
                    (parameter.responseEntry.state == null && parameter.responseEntry.service == null)){
                continue;
            }
            if(parameter.responseEntry.state != null) {
                // 设置属性
                JSONObject state = new JSONObject();
                state.put("uri", "action/device/setProperty");
                JSONObject params = new JSONObject();
                params.put("iotId", parameter.responseEntry.iotId);
                params.put("propertyName", parameter.responseEntry.state.rawName);
                params.put("propertyValue", Integer.parseInt(parameter.responseEntry.state.rawValue));
                state.put("params", params);
                actions.add(state);
            } else if(parameter.responseEntry.service != null) {
                // 调用服务
                JSONObject service = new JSONObject();
                service.put("uri", "action/device/invokeService");
                JSONObject params = new JSONObject();
                params.put("iotId", parameter.responseEntry.iotId);
                params.put("serviceName", parameter.responseEntry.service.rawName);
                JSONObject args = new JSONObject();
                // 构造服务参数
                for(ETSL.serviceArgEntry arg : parameter.responseEntry.service.args){
                    args.put(arg.rawName, Integer.parseInt(arg.rawValue));
                }
                params.put("serviceArgs", args);
                service.put("params", params);
                actions.add(service);
            }
            isHasAction = true;
        }
        if(isHasAction){
            requestParameterEntry.addParameter("actions", actions);
        }
        requestParameterEntry.addParameter("sceneType", baseInfo.sceneType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CREATESCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //创建CA场景
    public void createCAScene(EScene.sceneBaseInfoEntry baseInfo, List<EScene.responseEntry> parameters,
                              Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CREATESCENE;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("homeId", baseInfo.homeId);
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("description", baseInfo.description);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        requestParameterEntry.addParameter("mode", "any");
        // 构造响应Actions
        boolean isHasAction = false;
        JSONArray actions = new JSONArray();
        for (EScene.responseEntry responseEntry : parameters) {
            // 设置属性
            JSONObject state = new JSONObject();
            state.put("uri", "action/device/setProperty");
            JSONObject params = new JSONObject();
            params.put("iotId", responseEntry.iotId);
            params.put("propertyName", responseEntry.state.rawName);
            params.put("propertyValue", Integer.parseInt(responseEntry.state.rawValue));
            state.put("params", params);
            actions.add(state);
            isHasAction = true;
        }
        if (isHasAction) {
            requestParameterEntry.addParameter("actions", actions);
        }
        requestParameterEntry.addParameter("sceneType", CScene.TYPE_CA);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CREATESCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //创建用于按键自动场景触发手动场景
    public void createSwitchAutoScene(EScene.sceneBaseInfoEntry baseInfo, EScene.triggerEntry triggerEntry,
                                      String actionSceneID,
                                      Handler commitFailureHandler,
                                      Handler responseErrorHandler,
                                      Handler processDataHandler) {

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_CREATESCENE;
        requestParameterEntry.version = "1.0.1";
        requestParameterEntry.addParameter("homeId", baseInfo.homeId);
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("description", baseInfo.description);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        requestParameterEntry.addParameter("mode", "any");

        // 构造条件caConditions
        JSONArray caConditions = new JSONArray();
        // 设置属性
        JSONObject condition = new JSONObject();
        condition.put("uri", "condition/device/event");
        JSONObject conditionParams = new JSONObject();

        conditionParams.put("productKey", triggerEntry.productKey);
        conditionParams.put("deviceName", triggerEntry.deviceName);
        conditionParams.put("eventCode", "KeyValueNotification");
        conditionParams.put("propertyName", "KeyValue");
        conditionParams.put("compareType", "==");
        conditionParams.put("compareValue", Integer.parseInt(triggerEntry.state.rawValue));

        condition.put("params", conditionParams);
        caConditions.add(condition);

        // 构造响应Actions
        JSONArray actions = new JSONArray();
        // 设置属性
        JSONObject state = new JSONObject();
        state.put("uri", "action/scene/trigger");
        JSONObject params = new JSONObject();
        params.put("sceneId", actionSceneID);
        state.put("params", params);
        actions.add(state);

        requestParameterEntry.addParameter("caConditions", caConditions);
        requestParameterEntry.addParameter("actions", actions);
        requestParameterEntry.addParameter("sceneType", CScene.TYPE_CA);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_CREATE_SWITCH_AUTO_SCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //设置设备扩展信息
    public void setExtendedProperty(String iotId,
                                    String dataKey,
                                    String dataValue,
                                    Handler commitFailureHandler,
                                    Handler responseErrorHandler,
                                    Handler processDataHandler) {

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_EXTENDED_PROPERTY_SET;
        requestParameterEntry.version = "1.0.4";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("dataKey", dataKey);
        requestParameterEntry.addParameter("dataValue", dataValue);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_EXTENDED_PROPERTY_SET;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //获取设备扩展信息
    public void getExtendedProperty(String iotId,
                                    String dataKey,
                                    Handler commitFailureHandler,
                                    Handler responseErrorHandler,
                                    Handler processDataHandler) {

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_EXTENDED_PROPERTY_GET;
        requestParameterEntry.version = "1.0.4";
        requestParameterEntry.addParameter("iotId", iotId);
        requestParameterEntry.addParameter("dataKey", dataKey);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_EXTENDED_PROPERTY_GET;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 更新场景
    public void update(EScene.sceneBaseInfoEntry baseInfo, List<EScene.parameterEntry> parameters,
                       Handler commitFailureHandler,
                       Handler responseErrorHandler,
                       Handler processDataHandler) {
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATESCENE;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("sceneId", baseInfo.sceneId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        requestParameterEntry.addParameter("description", baseInfo.description);
        // 构造触发Triggers
        boolean isHasTrigger = false;
        JSONObject triggers = new JSONObject();
        triggers.put("uri", "logical/or");
        JSONArray items = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_TRIGGER || parameter.triggerEntry == null || !parameter.triggerEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "trigger/device/property");
            JSONObject params = new JSONObject();
            params.put("productKey", parameter.triggerEntry.productKey);
            params.put("deviceName", parameter.triggerEntry.deviceName);
            params.put("propertyName", parameter.triggerEntry.state.rawName);
            params.put("compareType", "==");
            params.put("compareValue", Integer.parseInt(parameter.triggerEntry.state.rawValue));
            item.put("params", params);
            items.add(item);
            isHasTrigger = true;
        }
        if(isHasTrigger){
            triggers.put("items", items);
            requestParameterEntry.addParameter("triggers", triggers);
        }
        // 构造时间条件
        boolean isHasConditionTime = false;
        JSONObject conditions = new JSONObject();
        conditions.put("uri", "logical/and");
        JSONArray conditions_items = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_CONDITION_TIME || parameter.conditionTimeEntry == null || !parameter.conditionTimeEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "condition/timeRange");
            JSONObject params = new JSONObject();
            params.put("cron", parameter.conditionTimeEntry.genCronString());
            params.put("cronType", "linux");
            params.put("timezoneID", "Asia/Shanghai");
            item.put("params", params);
            conditions_items.add(item);
            isHasConditionTime = true;
        }
        // 构造属性状态条件
        boolean isHasConditionState = false;
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_CONDITION_STATE || parameter.conditionStateEntry == null || !parameter.conditionStateEntry.isSelected){
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("uri", "condition/device/property");
            JSONObject params = new JSONObject();
            params.put("productKey", parameter.conditionStateEntry.productKey);
            params.put("deviceName", parameter.conditionStateEntry.deviceName);
            params.put("propertyName", parameter.conditionStateEntry.state.rawName);
            params.put("compareType", "==");
            params.put("compareValue", Integer.parseInt(parameter.conditionStateEntry.state.rawValue));
            item.put("params", params);
            conditions_items.add(item);
            isHasConditionState = true;
        }
        if(isHasConditionTime || isHasConditionState){
            conditions.put("items", conditions_items);
            requestParameterEntry.addParameter("conditions", conditions);
        }
        // 构造响应Actions
        boolean isHasAction = false;
        JSONArray actions = new JSONArray();
        for(EScene.parameterEntry parameter : parameters){
            if(parameter.type != CScene.SPT_RESPONSE || parameter.responseEntry == null || !parameter.responseEntry.isSelected ||
                    (parameter.responseEntry.state == null && parameter.responseEntry.service == null)){
                continue;
            }
            if(parameter.responseEntry.state != null) {
                // 设置属性
                JSONObject state = new JSONObject();
                state.put("uri", "action/device/setProperty");
                JSONObject params = new JSONObject();
                params.put("iotId", parameter.responseEntry.iotId);
                params.put("propertyName", parameter.responseEntry.state.rawName);
                params.put("propertyValue", Integer.parseInt(parameter.responseEntry.state.rawValue));
                state.put("params", params);
                actions.add(state);
            } else if(parameter.responseEntry.service != null) {
                // 调用服务
                JSONObject service = new JSONObject();
                service.put("uri", "action/device/invokeService");
                JSONObject params = new JSONObject();
                params.put("iotId", parameter.responseEntry.iotId);
                params.put("serviceName", parameter.responseEntry.service.rawName);
                JSONObject args = new JSONObject();
                // 构造服务参数
                for(ETSL.serviceArgEntry arg : parameter.responseEntry.service.args){
                    args.put(arg.rawName, Integer.parseInt(arg.rawValue));
                }
                params.put("serviceArgs", args);
                service.put("params", params);
                actions.add(service);
            }
            isHasAction = true;
        }
        if(isHasAction){
            requestParameterEntry.addParameter("actions", actions);
        }
        requestParameterEntry.addParameter("sceneType", baseInfo.sceneType);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATESCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }
    // 更新场景
    public void updateCAScene(EScene.sceneBaseInfoEntry baseInfo, List<EScene.responseEntry> parameters,
                       Handler commitFailureHandler,
                       Handler responseErrorHandler,
                       Handler processDataHandler) {
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATESCENE;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("sceneId", baseInfo.sceneId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        requestParameterEntry.addParameter("description", baseInfo.description);
        // 构造响应Actions
        boolean isHasAction = false;
        JSONArray actions = new JSONArray();
        for (EScene.responseEntry responseEntry : parameters) {
            // 设置属性
            JSONObject state = new JSONObject();
            state.put("uri", "action/device/setProperty");
            JSONObject params = new JSONObject();
            params.put("iotId", responseEntry.iotId);
            params.put("propertyName", responseEntry.state.rawName);
            params.put("propertyValue", Integer.parseInt(responseEntry.state.rawValue));
            state.put("params", params);
            actions.add(state);
            isHasAction = true;
        }
        if(isHasAction){
            requestParameterEntry.addParameter("actions", actions);
        }
        requestParameterEntry.addParameter("sceneType", CScene.TYPE_CA);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATESCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 更新用于按键自动场景触发手动场景
    public void updateSwitchAutoScene(EScene.sceneBaseInfoEntry baseInfo, EScene.triggerEntry triggerEntry,
                                      String actionSceneID,
                                      Handler commitFailureHandler,
                                      Handler responseErrorHandler,
                                      Handler processDataHandler) {
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_UPDATESCENE;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("catalogId", baseInfo.catalogId);
        requestParameterEntry.addParameter("sceneId", baseInfo.sceneId);
        requestParameterEntry.addParameter("enable", baseInfo.enable);
        requestParameterEntry.addParameter("name", baseInfo.name);
        requestParameterEntry.addParameter("icon", baseInfo.icon);
        requestParameterEntry.addParameter("iconColor", baseInfo.iconColor);
        requestParameterEntry.addParameter("description", baseInfo.description);
        requestParameterEntry.addParameter("mode", "any");

        // 构造条件caConditions
        JSONArray caConditions = new JSONArray();
        // 设置属性
        JSONObject condition = new JSONObject();
        condition.put("uri", "condition/device/event");
        JSONObject conditionParams = new JSONObject();

        conditionParams.put("productKey", triggerEntry.productKey);
        conditionParams.put("deviceName", triggerEntry.deviceName);
        conditionParams.put("eventCode", "KeyValueNotification");
        conditionParams.put("propertyName", "KeyValue");
        conditionParams.put("compareType", "==");
        conditionParams.put("compareValue", Integer.parseInt(triggerEntry.state.rawValue));

        condition.put("params", conditionParams);
        caConditions.add(condition);

        // 构造响应Actions
        JSONArray actions = new JSONArray();
        // 设置属性
        JSONObject state = new JSONObject();
        state.put("uri", "action/scene/trigger");
        JSONObject params = new JSONObject();
        params.put("sceneId", actionSceneID);
        state.put("params", params);
        actions.add(state);

        requestParameterEntry.addParameter("caConditions", caConditions);
        requestParameterEntry.addParameter("actions", actions);
        requestParameterEntry.addParameter("sceneType", CScene.TYPE_CA);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_UPDATESCENE;

        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
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

    // 查询场景详细信息
    public void querySceneDetail(String sceneId, String catalogId,
                               Handler commitFailureHandler,
                               Handler responseErrorHandler,
                               Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }
        Log.i("lzm", "param sid = "+sceneId +",catalogId"+catalogId);
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_QUERYSCENEDETAIL;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("sceneId", sceneId);
        requestParameterEntry.addParameter("catalogId", catalogId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_QUERYSCENEDETAIL;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 执行场景
    public void executeScene(String sceneId,
                               Handler commitFailureHandler,
                               Handler responseErrorHandler,
                               Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_EXECUTESCENE;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("sceneId", sceneId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_EXECUTESCENE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 删除场景
    public void deleteScene(String sceneId,
                             Handler commitFailureHandler,
                             Handler responseErrorHandler,
                             Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_DELETESCENE;
        requestParameterEntry.version = "1.0.0";
        requestParameterEntry.addParameter("sceneId", sceneId);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_DELETESCENE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
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

    // 初始化场景参数列表
    public void initSceneParameterList(List<EScene.parameterEntry> parameterEntryList, EScene.processedDetailEntry detailEntry){
        if(parameterEntryList == null || parameterEntryList.size() == 0 || detailEntry == null){
            return;
        }

        for (EScene.parameterEntry parameter : parameterEntryList) {
            // 初始化属性状态触发
            if(parameter.type == CScene.SPT_TRIGGER && parameter.triggerEntry != null && parameter.triggerEntry.state != null){
                if(detailEntry.findTriggerProperty(parameter.triggerEntry.iotId, parameter.triggerEntry.deviceName, parameter.triggerEntry.state.rawName,
                        "==", parameter.triggerEntry.state.rawValue)){
                    parameter.triggerEntry.isSelected = true;
                }
                continue;
            }

            // 初始化时间范围条件
            if(parameter.type == CScene.SPT_CONDITION_TIME && parameter.conditionTimeEntry != null && parameter.conditionTimeEntry != null){
                String cron = detailEntry.findConditionTimeRange();
                if(cron != null && cron.length() > 0){
                    parameter.conditionTimeEntry = new EScene.conditionTimeEntry(cron);
                    parameter.conditionTimeEntry.isSelected = true;
                }
                continue;
            }

            // 初始化属性状态条件
            if(parameter.type == CScene.SPT_CONDITION_STATE && parameter.conditionStateEntry != null && parameter.conditionStateEntry.state != null){
                if(detailEntry.findConditionProperty(parameter.conditionStateEntry.iotId, parameter.conditionStateEntry.deviceName, parameter.conditionStateEntry.state.rawName,
                        "==", parameter.conditionStateEntry.state.rawValue)){
                    parameter.conditionStateEntry.isSelected = true;
                }
                continue;
            }

            // 初始化设置属性状态响应
            if(parameter.type == CScene.SPT_RESPONSE && parameter.responseEntry != null && parameter.responseEntry.state != null){
                if(detailEntry.findActionSetProperty(parameter.responseEntry.iotId, parameter.responseEntry.deviceName, parameter.responseEntry.state.rawName,
                        parameter.responseEntry.state.rawValue)){
                   parameter.responseEntry.isSelected = true;
                }
                continue;
            }

            // 初始化调用服务响应
            if(parameter.type == CScene.SPT_RESPONSE && parameter.responseEntry != null && parameter.responseEntry.service != null){
                if(detailEntry.findActionInvokeService(parameter.responseEntry.iotId, parameter.responseEntry.deviceName, parameter.responseEntry.service.name,
                        parameter.responseEntry.service.args != null && parameter.responseEntry.service.args.size() > 0 ? parameter.responseEntry.service.args.get(0).rawName : "",
                        parameter.responseEntry.service.args != null && parameter.responseEntry.service.args.size() > 0 ? parameter.responseEntry.service.args.get(0).rawValue : "")){
                    parameter.responseEntry.isSelected = true;
                }
                continue;
            }
        }
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


    // 获取消息列表
    public void getSceneLogList(int pageNo,
                           Handler commitFailureHandler,
                           Handler responseErrorHandler,
                           Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETSCENELOG;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("pageNo", pageNo);
        requestParameterEntry.addParameter("pageSize", Constant.PAGE_SIZE);
        //requestParameterEntry.addParameter("nowTime", new Date().getTime()-1000*3600*24);
        requestParameterEntry.addParameter("nowTime", new Date().getTime());
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETSCENELOG;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //获取设备动作
    public void getDeviceAction(String iotID,
                                Handler commitFailureHandler,
                                Handler responseErrorHandler,
                                Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_IOTID_SCENE_ABILITY_TSL_LIST;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotID);
        requestParameterEntry.addParameter("flowType", 2);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SCENE_ABILITY_TSL;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    //获取设备动作
    public void getDeviceTrigger(String iotID,
                                 Handler commitFailureHandler,
                                 Handler responseErrorHandler,
                                 Handler processDataHandler) {
        if (processDataHandler == null) {
            Logger.e("The processDataHandler is not null!");
            return;
        }
        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_IOTID_SCENE_ABILITY_TSL_LIST;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", iotID);
        requestParameterEntry.addParameter("flowType", 0);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_SCENE_ABILITY_TSL_TRIGGER;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

}
