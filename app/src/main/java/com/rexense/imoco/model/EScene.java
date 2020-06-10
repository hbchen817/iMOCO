package com.rexense.imoco.model;

import android.content.Context;
import com.rexense.imoco.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备实体(包括网关及子设备)
 */
public class EScene {
    // 场景列表列表条目实体
    public static class sceneListItemEntry {
        public String id;
        public int status;
        public Boolean enable;
        public String name;
        public String description;
        public Boolean valid;
        public String groupId;

        // 构造
        public sceneListItemEntry(){
            this.id = "";
            this.status = 1;
            this.enable = false;
            this.name = "";
            this.description = "";
            this.valid = false;
            this.groupId = "0";
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
            sceneListItem.status = entry.status;
            sceneListItem.enable = entry.enable;
            sceneListItem.name = entry.name;
            sceneListItem.description = entry.description;
            sceneListItem.valid = entry.valid;
            sceneListItem.groupId = entry.groupId;
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
            for(int i = 0; i < 7; i++){
                this.repeat.add(i);
            }
            this.isSelected = false;
        }

        // 添加周循环
        public void addWeekRepeat(int dayIndex){
            this.repeat.add(dayIndex);
        }

        // 生成时间范围字符串
        public String genTimeRangeString(){
            return String.format("%d:%d - %d:%d", this.beginHour, this.beginMinute, this.endHour, this.endMinute);
        }

        // 生成星期循环字符串
        public String genWeekRepeatString(Context context){
            if(this.isEveryDay()){
                return context.getString(R.string.week_all);
            }

            String weekRepeat = "";
            boolean isFound;
            for(int i = 0; i < 7; i++)
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
                        weekRepeat = weekRepeat + ",";
                    }
                    switch (i){
                        case 0:
                            weekRepeat = weekRepeat + context.getString(R.string.week_0);
                            break;
                        case 1:
                            weekRepeat = weekRepeat + context.getString(R.string.week_1);
                            break;
                        case 2:
                            weekRepeat = weekRepeat + context.getString(R.string.week_2);
                            break;
                        case 3:
                            weekRepeat = weekRepeat + context.getString(R.string.week_3);
                            break;
                        case 4:
                            weekRepeat = weekRepeat + context.getString(R.string.week_4);
                            break;
                        case 5:
                            weekRepeat = weekRepeat + context.getString(R.string.week_5);
                            break;
                        case 6:
                            weekRepeat = weekRepeat + context.getString(R.string.week_6);
                            break;
                        default:
                            break;
                    }
                }
            }
            return weekRepeat;
        }

        // 生成Corn字符串
        public String genCornString(){
            String weekRepeat = "";
            for(int i = 0; i < 7; i++)
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
            return String.format("%d-%d %d-%d * * %s", this.beginMinute, this.endMinute, this.beginHour, this.endHour, weekRepeat);
        }

        // 是否每一天
        private boolean isEveryDay() {
            boolean isFound;
            for(int i = 0; i < 7; i++)
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
}

