package com.rexense.imoco.model;

import com.rexense.imoco.contract.Constant;

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
        public ETSL.stateEntry state;
        public boolean isSelected;

        // 构造
        public triggerEntry(){
            this.productKey = "";
            this.iotId = "";
            this.name = "";
            this.state = null;
            this.isSelected = false;
        }
    }

    // 响应设备实体
    public static class responseEntry{
        public String productKey;
        // 如果iotId为空则表示此类产品没有实际设备
        public String iotId;
        public String name;
        public String action;
        public boolean isSelected;

        // 构造
        public responseEntry(){
            this.productKey = "";
            this.iotId = "";
            this.name = "";
            this.action = "";
            this.isSelected = false;
        }
    }
}

