package com.laffey.smart.model;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ItemScene {
    private String mac;
    private String sceneId;
    private String name;
    private String type;// 场景类型，0:自动，1：手动
    private String enable;// 启用状态，0：禁用，1：启用
    private Timer time;// 时间规则
    private String conditionMode;// 条件模式，All：Condition节点所有条件满足时执行。Any：Condition节点任何一个节点满足就可以执行。
    private List<Condition> conditions;
    private List<Action> actions;

    public ItemScene() {
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Timer getTime() {
        return time;
    }

    public void setTime(Timer time) {
        this.time = time;
    }

    public String getConditionMode() {
        return conditionMode;
    }

    public void setConditionMode(String conditionMode) {
        this.conditionMode = conditionMode;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public ItemScene(String mac, String sceneId, String name, String type, String enable, Timer time, String conditionMode, List<Condition> conditions, List<Action> actions) {
        this.mac = mac;
        this.sceneId = sceneId;
        this.name = name;
        this.type = type;
        this.enable = enable;
        this.time = time;
        this.conditionMode = conditionMode;
        this.conditions = conditions;
        this.actions = actions;
    }

    public static class Action {
        private String type;
        private ActionParameter parameters;

        public Action() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ActionParameter getParameters() {
            return parameters;
        }

        public void setParameters(ActionParameter parameters) {
            this.parameters = parameters;
        }

        public Action(String type, ActionParameter parameters) {
            this.type = type;
            this.parameters = parameters;
        }
    }

    public static class ActionParameter {
        private String deviceId;
        private String endpointId;
        private String commandType;
        private JSONObject command;
        private String sceneId;

        public ActionParameter() {
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getEndpointId() {
            return endpointId;
        }

        public void setEndpointId(String endpointId) {
            this.endpointId = endpointId;
        }

        public String getCommandType() {
            return commandType;
        }

        public void setCommandType(String commandType) {
            this.commandType = commandType;
        }

        public JSONObject getCommand() {
            return command;
        }

        public void setCommand(JSONObject command) {
            this.command = command;
        }

        public String getSceneId() {
            return sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public ActionParameter(String deviceId, String endpointId, String commandType, JSONObject command, String sceneId) {
            this.deviceId = deviceId;
            this.endpointId = endpointId;
            this.commandType = commandType;
            this.command = command;
            this.sceneId = sceneId;
        }
    }

    public static class Condition {
        private String type;
        private ConditionParameter parameters;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ConditionParameter getParameters() {
            return parameters;
        }

        public void setParameters(ConditionParameter parameters) {
            this.parameters = parameters;
        }

        public Condition() {
        }

        public Condition(String type, ConditionParameter parameters) {
            this.type = type;
            this.parameters = parameters;
        }
    }

    public static class ConditionParameter {
        private String deviceId;
        private String endpointId;
        private String name;
        private String compareType;
        private String compareValue;
        private String eventType;
        private String parameterName;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getEndpointId() {
            return endpointId;
        }

        public void setEndpointId(String endpointId) {
            this.endpointId = endpointId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompareType() {
            return compareType;
        }

        public void setCompareType(String compareType) {
            this.compareType = compareType;
        }

        public String getCompareValue() {
            return compareValue;
        }

        public void setCompareValue(String compareValue) {
            this.compareValue = compareValue;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public ConditionParameter() {
        }

        public ConditionParameter(String deviceId, String endpointId, String name, String compareType, String compareValue, String eventType, String parameterName) {
            this.deviceId = deviceId;
            this.endpointId = endpointId;
            this.name = name;
            this.compareType = compareType;
            this.compareValue = compareValue;
            this.eventType = eventType;
            this.parameterName = parameterName;
        }
    }

    public static class Timer {
        private String type;// Timer：时间点，TimeRange：时间段
        private String cron;// 时间格式

        public Timer() {
        }

        public Timer(String type, String cron) {
            this.type = type;
            this.cron = cron;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}
