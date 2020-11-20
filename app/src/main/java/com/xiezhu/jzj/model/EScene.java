package com.xiezhu.jzj.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备实体(包括网关及子设备)
 */
public class EScene {
    // 场景基本信息实体
    public static class sceneBaseInfoEntry {
        public String sceneId;
        public String homeId;
        public String catalogId;
        public boolean enable;
        public String name;
        public String description;
        public String icon;
        public String iconColor;
        public String sceneType;

        // 构造
        public sceneBaseInfoEntry(String homeId, String catalogId, String name, String description){
            this.homeId = homeId;
            this.catalogId = catalogId;
            this.name = name;
            this.description = description;
            this.icon = CScene.DEFAULT_ICON_URL;
            this.iconColor = "#FFFFFF";
            this.sceneType = CScene.TYPE_IFTTT;
        }
    }

    // 场景列表列表条目实体
    public static class sceneListItemEntry {
        public String id;
        public Boolean enable;
        public String name;
        public String description;
        public Boolean valid;
        public String catalogId;

        // 构造
        public sceneListItemEntry(){
            this.id = "";
            this.enable = false;
            this.name = "";
            this.description = "";
            this.valid = false;
            this.catalogId = "0";
        }
    }

    // 场景列表实体
    public static class sceneListEntry {
        public int total;
        public int pageNo;
        public int pageSize;
        public List<sceneListItemEntry> scenes;

        // 构造
        public sceneListEntry() {
            this.scenes = new ArrayList<sceneListItemEntry>();
        }

        // 添加数据
        public void addData(sceneListItemEntry entry) {
            sceneListItemEntry sceneListItem = new sceneListItemEntry();
            sceneListItem.id = entry.id;
            sceneListItem.enable = entry.enable;
            sceneListItem.name = entry.name;
            sceneListItem.description = entry.description;
            sceneListItem.valid = entry.valid;
            sceneListItem.catalogId = entry.catalogId;
            this.scenes.add(sceneListItem);
        }
    }

    // 场景模板实体
    public static class sceneModelEntry {
        public int code;
        public int name;
        public int icon;

        // 构造
        public sceneModelEntry(int code, int name, int icon){
            this.code = code;
            this.name = name;
            this.icon = icon;
        }
    }

    // 触发器设备实体
    public static class triggerEntry{
        public String productKey;
        // 如果iotId为空则表示此类产品没有实际设备
        public String iotId;
        public String name;
        public String deviceName;
        public ETSL.stateEntry state;
        public boolean isSelected;

        // 构造
        public triggerEntry(){
            this.productKey = "";
            this.iotId = "";
            this.name = "";
            this.deviceName = "";
            this.state = null;
            this.isSelected = false;
        }
    }

    // 时间条件实体
    public static class conditionTimeEntry  {
        public int beginHour;
        public int beginMinute;
        public int endHour;
        public int endMinute;
        public List<Integer> repeat;
        public boolean isSelected;

        // 构造
        public conditionTimeEntry(){
            this.beginHour = 0;
            this.beginMinute = 0;
            this.endHour = 23;
            this.endMinute = 59;
            this.repeat = new ArrayList<Integer>();
            for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++){
                this.repeat.add(i);
            }
            this.isSelected = false;
        }
        public conditionTimeEntry(String cron){
            String[] items = cron.split(" ");
            String[] minutes = items[0].split("-");
            String[] hours = items[1].split("-");
            if(items[4].equals("*")){
                items[4] = "";
                for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++){
                    if(items[4].length() > 0){
                        items[4] = items[4] + ",";
                    }
                    items[4] = items[4] + i;
                }
            }
            String[] days = items[4].split(",");
            this.beginHour = Integer.parseInt(hours[0]);
            this.beginMinute = Integer.parseInt(minutes[0]);
            this.endHour = Integer.parseInt(hours[1]);
            this.endMinute = Integer.parseInt(minutes[1]);
            this.repeat = new ArrayList<Integer>();
            for(int i = 0; i < days.length; i++){
                this.repeat.add(Integer.parseInt(days[i]));
            }
            this.isSelected = false;
        }

        // 添加周循环
        public void addWeekRepeat(int dayIndex){
            if(dayIndex < CScene.WEEK_CODE_MIN || dayIndex > CScene.WEEK_CODE_MAX){
                return;
            }

            boolean isExist = false;
            for(Integer day : this.repeat){
                if(dayIndex == day){
                    return;
                }
            }
            this.repeat.add(dayIndex);
        }

        // 获取时间范围字符串
        public String getTimeRangeString(){
            return String.format("%02d:%02d - %02d:%02d", this.beginHour, this.beginMinute, this.endHour, this.endMinute);
        }

        // 获取星期循环字符串
        public String getWeekRepeatString(Context context){
            if(this.isEveryDay()){
                return context.getString(R.string.set_time_everyday);
            }
            if(this.isWorkDay()){
                return context.getString(R.string.set_time_workday);
            }
            if(this.isWeekEnd()){
                return context.getString(R.string.set_time_weekend);
            }

            String weekRepeat = "";
            boolean isFound;
            for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++)
            {
                isFound = false;
                for(Integer r : this.repeat){
                    if(r == i){
                        isFound = true;
                        break;
                    }
                }
                if(isFound){
                    if(weekRepeat.length() > 0) {
                        weekRepeat = weekRepeat + " ";
                    }
                    switch (i){
                        case CScene.WEEK_CODE_SUN:
                            weekRepeat = weekRepeat + context.getString(R.string.week_0);
                            break;
                        case CScene.WEEK_CODE_MON:
                            weekRepeat = weekRepeat + context.getString(R.string.week_1);
                            break;
                        case CScene.WEEK_CODE_TUE:
                            weekRepeat = weekRepeat + context.getString(R.string.week_2);
                            break;
                        case CScene.WEEK_CODE_WED:
                            weekRepeat = weekRepeat + context.getString(R.string.week_3);
                            break;
                        case CScene.WEEK_CODE_THU:
                            weekRepeat = weekRepeat + context.getString(R.string.week_4);
                            break;
                        case CScene.WEEK_CODE_FRI:
                            weekRepeat = weekRepeat + context.getString(R.string.week_5);
                            break;
                        case CScene.WEEK_CODE_SAT:
                            weekRepeat = weekRepeat + context.getString(R.string.week_6);
                            break;
                        default:
                            break;
                    }
                }
            }
            return weekRepeat;
        }

        // 获取取周循环选项
        public List<EChoice.itemEntry> getReportChoiceItems(Context context){
            List<EChoice.itemEntry> list = new ArrayList<EChoice.itemEntry>();
            String name = "";
            boolean isFound = false;
            for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++)
            {
                switch (i){
                    case CScene.WEEK_CODE_SUN:
                        name = context.getString(R.string.week_0);
                        break;
                    case CScene.WEEK_CODE_MON:
                        name = context.getString(R.string.week_1);
                        break;
                    case CScene.WEEK_CODE_TUE:
                        name = context.getString(R.string.week_2);
                        break;
                    case CScene.WEEK_CODE_WED:
                        name = context.getString(R.string.week_3);
                        break;
                    case CScene.WEEK_CODE_THU:
                        name = context.getString(R.string.week_4);
                        break;
                    case CScene.WEEK_CODE_FRI:
                        name = context.getString(R.string.week_5);
                        break;
                    case CScene.WEEK_CODE_SAT:
                        name = context.getString(R.string.week_6);
                        break;
                    default:
                        break;
                }

                isFound = false;
                for(Integer r : this.repeat){
                    if(r == i){
                        isFound = true;
                        break;
                    }
                }
                EChoice.itemEntry item = new EChoice.itemEntry(name, i + "", isFound);
                list.add(item);
            }

            return list;
        }

        // 生成Cron字符串
        public String genCronString(){
            String weekRepeat = "";
            for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++)
            {
                for(Integer r : this.repeat){
                    if(r == i){
                        if(weekRepeat.length() > 0){
                            weekRepeat = weekRepeat + ",";
                        }
                        weekRepeat = weekRepeat + i;
                    }
                }
            }

            if(this.isEveryDay()){
                weekRepeat = "*";
            }

            return String.format("%02d-%02d %02d-%02d * * %s", this.beginMinute, this.endMinute, this.beginHour, this.endHour, weekRepeat);
        }

        // 是否全天
        public boolean isAllDay() {
            return this.beginHour == 0 && this.beginMinute == 0 && this.endHour == 23 && this.endMinute == 59 ? true : false;
        }

        // 是否每一天
        public boolean isEveryDay() {
            boolean isFound;
            for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++)
            {
                isFound = false;
                for(Integer r : this.repeat){
                    if(r == i){
                        isFound = true;
                    }
                }
                if(!isFound){
                    return false;
                }
            }
            return true;
        }

        // 是否工作日
        public boolean isWorkDay() {
            boolean sunIsFound = false, staIsFound = false;
            int otherNumber = 0;
            for(Integer r : this.repeat){
                if(r == CScene.WEEK_CODE_SUN){
                    sunIsFound = true;
                } else if(r == CScene.WEEK_CODE_SAT){
                    staIsFound = true;
                } else {
                    otherNumber++;
                }
            }

            return !sunIsFound && !staIsFound && otherNumber == 5 ? true : false;
        }

        // 是否周末
        public boolean isWeekEnd() {
            boolean sunIsFound = false, staIsFound = false;
            int otherNumber = 0;
            for(Integer r : this.repeat){
                if(r == CScene.WEEK_CODE_SUN){
                    sunIsFound = true;
                } else if(r == CScene.WEEK_CODE_SAT){
                    staIsFound = true;
                } else {
                    otherNumber++;
                }
            }

            return sunIsFound && staIsFound && otherNumber == 0 ? true : false;
        }

        // 是否自定义
        public boolean isSelfDefine(){
            return !this.isEveryDay() && !this.isWorkDay() && !this.isWeekEnd() ? true : false;
        }

        // 快速生成周循环(type:1每一天,2工作日,3周末)
        public void quickGenRepeat(int type){
            this.repeat.clear();
            if(type == 1) {
                for(int i = CScene.WEEK_CODE_MIN; i <= CScene.WEEK_CODE_MAX; i++){
                    this.repeat.add(i);
                }
            } else if(type == 2) {
                for(int i = CScene.WEEK_CODE_MON; i <= CScene.WEEK_CODE_FRI; i++){
                    this.repeat.add(i);
                }
            } else if(3 == type) {
                this.repeat.add(CScene.WEEK_CODE_SUN);
                this.repeat.add(CScene.WEEK_CODE_SAT);
            }
        }
    }

    // 状态条件实体
    public static class conditionStateEntry extends triggerEntry  {
        // 构造
        public conditionStateEntry(){
            super();
        }
    }

    // 响应设备实体
    public static class responseEntry{
        public String productKey;
        // 如果iotId为空则表示此类产品没有实际设备
        public String iotId;
        public String name;
        public String deviceName;
        public ETSL.stateEntry state;
        public ETSL.serviceEntry service;
        public boolean isSelected;

        // 构造
        public responseEntry(){
            this.productKey = "";
            this.iotId = "";
            this.name = "";
            this.deviceName = "";
            this.state = null;
            this.service = null;
            this.isSelected = false;
        }
    }

    // 场景参数实体
    public static class parameterEntry{
        // 类型(0触发设备标题,1触发设备,2条件标题,3时间条件,4状态条件,5响应设备标题,6响应设备)
        public int type;
        public String typeName;
        public EScene.triggerEntry triggerEntry;
        public EScene.conditionTimeEntry conditionTimeEntry;
        public EScene.conditionStateEntry conditionStateEntry;
        public EScene.responseEntry responseEntry;

        // 构造
        public parameterEntry(){
            this.type = 0;
            this.typeName = "";
            this.triggerEntry = null;
            this.conditionTimeEntry = null;
            this.conditionStateEntry = null;
            this.responseEntry = null;
        }

    }

    // 原始详细信息实体
    public static class rawDetailEntry{

        /**
         * valid : true
         * sceneType : IFTTT
         * enable : true
         * name : 红外布防报警
         * icon : http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1585899444167.png
         * iconColor : #FFFFFF
         * description : 推荐场景
         * actionsJson : [{"params":{"localizedServiceName":"触发模式","iotId":"TB5iNNTwxlidEeQ7Sh8n000100","localizedProductName":"iMOCO智能网关RG4300","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559628665015.png","serviceArgs":{"InvokeVoice":1},"deviceNickName":"iMOCO智能网关RG4300","serviceName":"InvokeMode","productKey":"a1GuwJkxdQx","deviceName":"TB5iNNTwxlidEeQ7Sh8n"},"uri":"action/device/invokeService","status":1},{"params":{"compareValue":1,"identifier":"ArmMode","localizedProductName":"iMOCO智能网关RG4300","localizedPropertyName":"布撤防","deviceNickName":"iMOCO智能网关RG4300","propertyValue":1,"productKey":"a1GuwJkxdQx","propertyItems":{"ArmMode":1},"deviceName":"TB5iNNTwxlidEeQ7Sh8n","iotId":"TB5iNNTwxlidEeQ7Sh8n000100","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559628665015.png","propertyName":"ArmMode","ignoreRepeatedPropertyValue":true,"localizedCompareValueName":"布防"},"uri":"action/device/setProperty","status":1},{"params":{"compareValue":0,"identifier":"PowerSwitch_1","localizedProductName":"iMOCO一键单火开关K01Z","localizedPropertyName":"电源开关","deviceNickName":"iMOCO一键单火开关K01Z","propertyValue":0,"productKey":"a1X2BnFW2fx","propertyItems":{"PowerSwitch_1":0},"deviceName":"25FB379C48D808E9","iotId":"G5P1q4x2MtJmN373Yisf000100","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1585737871574.png","propertyName":"PowerSwitch_1","ignoreRepeatedPropertyValue":true,"localizedCompareValueName":"关闭"},"uri":"action/device/setProperty","status":1},{"params":{"compareValue":0,"identifier":"PowerSwitch_1","localizedProductName":"iMOCO两键单火开关K02Z","localizedPropertyName":"电源开关_1","deviceNickName":"iMOCO两键单火开关K02Z","propertyValue":0,"productKey":"a1C9G2We8Da","propertyItems":{"PowerSwitch_1":0},"deviceName":"A4C138470C4AB832","iotId":"65wY8NUvOvhdflY2zbRsz20000","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1585737871518.png","propertyName":"PowerSwitch_1","ignoreRepeatedPropertyValue":true,"localizedCompareValueName":"关闭"},"uri":"action/device/setProperty","status":1}]
         * id : 7799ccd3e3af46d0a725843cbad9e059
         * conditionsJson : {"uri":"logical/and","items":[{"params":{"cron":"00-59 00-23 * * *","cronType":"linux","timezoneID":"Asia/Shanghai"},"uri":"condition/timeRange","status":1},{"params":{"compareValue":1,"identifier":"ArmMode","iotId":"TB5iNNTwxlidEeQ7Sh8n000100","localizedProductName":"iMOCO智能网关RG4300","compareType":"==","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559628665015.png","propertyName":"ArmMode","localizedPropertyName":"布撤防","deviceNickName":"iMOCO智能网关RG4300","localizedCompareValueName":"布防","productKey":"a1GuwJkxdQx","deviceName":"TB5iNNTwxlidEeQ7Sh8n"},"uri":"condition/device/property","status":1}]}
         * triggersJson : {"uri":"logical/or","items":[{"params":{"compareValue":1,"identifier":"MotionAlarmState","iotId":"o0fpqxNY4t2v1MEkdkh8000100","localizedProductName":"iMOCO人体红外感应器IR01","compareType":"==","productImage":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1585736635246.png","propertyName":"MotionAlarmState","localizedPropertyName":"红外检测状态","deviceNickName":"人体红外感应器","localizedCompareValueName":"有人","productKey":"a1CzbnRNzCR","deviceName":"000D6F0011BB11FE"},"uri":"trigger/device/property","status":1}]}
         */

        private boolean valid;
        private String sceneType;
        private boolean enable;
        private String name;
        private String icon;
        private String iconColor;
        private String description;
        private String id;
        private String conditionsJson;
        private String triggersJson;
        private List<String> actionsJson;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getSceneType() {
            return sceneType;
        }

        public void setSceneType(String sceneType) {
            this.sceneType = sceneType;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIconColor() {
            return iconColor;
        }

        public void setIconColor(String iconColor) {
            this.iconColor = iconColor;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConditionsJson() {
            return conditionsJson;
        }

        public void setConditionsJson(String conditionsJson) {
            this.conditionsJson = conditionsJson;
        }

        public String getTriggersJson() {
            return triggersJson;
        }

        public void setTriggersJson(String triggersJson) {
            this.triggersJson = triggersJson;
        }

        public List<String> getActionsJson() {
            return actionsJson;
        }

        public void setActionsJson(List<String> actionsJson) {
            this.actionsJson = actionsJson;
        }
    }

    // 处理后详细信息实体
    public static class processedDetailEntry{
        public EScene.rawDetailEntry rawDetail;
        public List<JSONObject> triggerStates;
        public List<JSONObject> conditionStates;
        public List<JSONObject> conditionTimeRanges;
        public List<JSONObject> actionSetProperties;
        public List<JSONObject> actionInvokeServices;

        // 构造函数
        public processedDetailEntry(){
            this.triggerStates = new ArrayList<JSONObject>();
            this.conditionStates = new ArrayList<JSONObject>();
            this.conditionTimeRanges = new ArrayList<JSONObject>();
            this.actionSetProperties = new ArrayList<JSONObject>();
            this.actionInvokeServices = new ArrayList<JSONObject>();
        }

        // 添加触发
        public void addTrigger(String trigger) {
            JSONObject root = JSON.parseObject(trigger);
            JSONArray items = root.getJSONArray("items");
            String uri;
            if (items != null && items.size() > 0) {
                for(int i = 0; i < items.size(); i++){
                    JSONObject item = items.getJSONObject(i);
                    uri = item.getString("uri");
                    if(uri != null && uri.length() > 0){
                        if(uri.equalsIgnoreCase(CScene.URI_TRIGGER_PROPERTY)){
                            this.triggerStates.add(item);
                        }
                    }
                }
            }
        }

        // 添加条件
        public void addCondition(String condition){
            JSONObject root = JSON.parseObject(condition);
            JSONArray items = root.getJSONArray("items");
            String uri;
            if (items != null && items.size() > 0) {
                for(int i = 0; i < items.size(); i++){
                    JSONObject item = items.getJSONObject(i);
                    uri = item.getString("uri");
                    if(uri != null && uri.length() > 0){
                        if(uri.equalsIgnoreCase(CScene.URI_CONDITION_PROPERTY)){
                            this.conditionStates.add(item);
                        } else if(uri.equalsIgnoreCase(CScene.URI_CONDITION_TIME_RANGE)){
                            this.conditionTimeRanges.add(item);
                        }
                    }
                }
            }
        }

        // 添加执行动作
        public void addAction(String action){
            JSONObject obj = JSON.parseObject(action);
            String uri = obj.getString("uri");
            if(uri != null && uri.length() > 0){
                if(uri.equalsIgnoreCase(CScene.URI_ACTION_INVOKE_SERVICE)){
                    this.actionInvokeServices.add(obj);
                } else if(uri.equalsIgnoreCase(CScene.URI_ACTION_SET_PROPERTY)){
                    this.actionSetProperties.add(obj);
                }
            }
        }

        // 查找属性触发(true存在,false不存在)
        public Boolean findTriggerProperty(String iotId, String deviceName, String propertyName, String compareType, String compareValue){
            if(this.triggerStates == null || this.triggerStates.size() == 0){
                return false;
            }

            JSONObject params;
            String iotId_json, deviceName_json, propertyName_json, compareType_json, compareValue_json;
            for(JSONObject obj : this.triggerStates){
                params = obj.getJSONObject("params");
                if(params == null){
                    continue;
                }

                // 比较iotId与deviceName
                iotId_json = params.getString("iotId");
                deviceName_json = params.getString("deviceName");
                if((iotId_json == null || iotId_json.length() == 0) && (deviceName_json == null || deviceName_json.length() == 0)){
                    continue;
                }
                if(!iotId_json.equalsIgnoreCase(iotId) && !deviceName_json.equalsIgnoreCase(deviceName)){
                    continue;
                }

                // 比较propertyName
                propertyName_json = params.getString("propertyName");
                if(propertyName_json == null || propertyName_json.length() == 0 || !propertyName_json.equalsIgnoreCase(propertyName)){
                    continue;
                }

                // 比较compareType
                compareType_json = params.getString("compareType");
                if(compareType_json == null || compareType_json.length() == 0 || !compareType_json.equalsIgnoreCase(compareType)){
                    continue;
                }

                // 比较compareValue
                compareValue_json = params.getString("compareValue");
                if(compareValue_json == null || compareValue_json.length() == 0 || !compareValue_json.equalsIgnoreCase(compareValue)){
                    continue;
                }
                return true;
            }

            return false;
        }

        // 查找属性条件(true存在,false不存在)
        public Boolean findConditionProperty(String iotId, String deviceName, String propertyName, String compareType, String compareValue){
            if(this.conditionStates == null || this.conditionStates.size() == 0){
                return false;
            }

            JSONObject params;
            String iotId_json, deviceName_json, propertyName_json, compareType_json, compareValue_json;
            for(JSONObject obj : this.conditionStates){
                params = obj.getJSONObject("params");
                if(params == null){
                    continue;
                }

                // 比较iotId与deviceName
                iotId_json = params.getString("iotId");
                deviceName_json = params.getString("deviceName");
                if((iotId_json == null || iotId_json.length() == 0) && (deviceName_json == null || deviceName_json.length() == 0)){
                    continue;
                }
                if(!iotId_json.equalsIgnoreCase(iotId) && !deviceName_json.equalsIgnoreCase(deviceName)){
                    continue;
                }

                // 比较propertyName
                propertyName_json = params.getString("propertyName");
                if(propertyName_json == null || propertyName_json.length() == 0 || !propertyName_json.equalsIgnoreCase(propertyName)){
                    continue;
                }

                // 比较compareType
                compareType_json = params.getString("compareType");
                if(compareType_json == null || compareType_json.length() == 0 || !compareType_json.equalsIgnoreCase(compareType)){
                    continue;
                }

                // 比较compareValue
                compareValue_json = params.getString("compareValue");
                if(compareValue_json == null || compareValue_json.length() == 0 || !compareValue_json.equalsIgnoreCase(compareValue)){
                    continue;
                }
                return true;
            }

            return false;
        }

        // 查找时间范围条件(返回cron, 为空表示不存在)
        public String findConditionTimeRange(){
            if(this.conditionTimeRanges == null || this.conditionTimeRanges.size() == 0){
                return "";
            }

            JSONObject params;
            String cron_json, cronType_json, timezoneID_json;
            for(JSONObject obj : this.conditionTimeRanges){
                params = obj.getJSONObject("params");
                if(params == null){
                    continue;
                }

                return params.getString("cron");
            }

            return "";
        }

        // 查找设置属性执行动作(true存在,false不存在)
        public Boolean findActionSetProperty(String iotId, String deviceName, String propertyName, String propertyValue){
            if(this.actionSetProperties == null || this.actionSetProperties.size() == 0){
                return false;
            }

            JSONObject params;
            String iotId_json, deviceName_json, propertyName_json, propertyValue_json;
            for(JSONObject obj : this.actionSetProperties){
                params = obj.getJSONObject("params");
                if(params == null){
                    continue;
                }

                // 比较iotId与deviceName
                iotId_json = params.getString("iotId");
                deviceName_json = params.getString("deviceName");
                if((iotId_json == null || iotId_json.length() == 0) && (deviceName_json == null || deviceName_json.length() == 0)){
                    continue;
                }
                if(!iotId_json.equalsIgnoreCase(iotId) && !deviceName_json.equalsIgnoreCase(deviceName)){
                    continue;
                }

                // 比较propertyName
                propertyName_json = params.getString("propertyName");
                if(propertyName_json == null || propertyName_json.length() == 0 || !propertyName_json.equalsIgnoreCase(propertyName)){
                    continue;
                }

                // 比较propertyValue
                propertyValue_json = params.getString("propertyValue");
                if(propertyValue_json == null || propertyValue_json.length() == 0 || !propertyValue_json.equalsIgnoreCase(propertyValue)){
                    continue;
                }
                return true;
            }

            return false;
        }

        // 查找调用服务执行动作(true存在,false不存在)
        public Boolean findActionInvokeService(String iotId, String deviceName, String serviceName, String argName, String argValue){
            if(this.actionInvokeServices == null || this.actionInvokeServices.size() == 0){
                return false;
            }

            JSONObject params;
            String iotId_json, deviceName_json, serviceName_json, argValue_json;
            for(JSONObject obj : this.actionSetProperties){
                params = obj.getJSONObject("params");
                if(params == null){
                    continue;
                }

                // 比较iotId与deviceName
                iotId_json = params.getString("iotId");
                deviceName_json = params.getString("deviceName");
                if((iotId_json == null || iotId_json.length() == 0) && (deviceName_json == null || deviceName_json.length() == 0)){
                    continue;
                }
                if(!iotId_json.equalsIgnoreCase(iotId) && !deviceName_json.equalsIgnoreCase(deviceName)){
                    continue;
                }

                // 比较serviceName
                serviceName_json = params.getString("serviceName");
                if(serviceName_json == null || serviceName_json.length() == 0 || !serviceName_json.equalsIgnoreCase(serviceName)){
                    continue;
                }

                // 比较argName与argValue
                if(argName != null && argName.length() > 0){
                    JSONObject serviceArgs = params.getJSONObject("serviceArgs");
                    if(serviceArgs == null){
                        continue;
                    }
                    argValue_json = serviceArgs.getString(argName);
                    if(argValue_json == null || argValue_json.length() == 0 || !argValue_json.equalsIgnoreCase(argValue)){
                        continue;
                    }
                }
                return true;
            }

            return false;
        }
    }
}

