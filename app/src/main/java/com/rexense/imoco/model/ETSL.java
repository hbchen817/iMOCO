package com.rexense.imoco.model;

import com.rexense.imoco.contract.CTSL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 设备物描述语言
 */
public class ETSL {
    // 物的基本信息实体
    public static class thingBaseInforEntry {
        public String productKey;
        public String firmwareVersion;

        // 构造
        public thingBaseInforEntry() {
            this.productKey = "";
            this.firmwareVersion = "";
        }
    }

    // 属性实体
    public static class propertyEntry {
        public String iotId;
        public String productKey;
        public Map<String, String> properties;

        // 构造
        public propertyEntry() {
            this.iotId = "";
            this.productKey = "";
            this.properties = new HashMap<String, String>();
        }

        // 添加属性
        public void addProperty(String name, String value) {
            this.properties.put(name, value);
        }

        // 获取属性值
        public String getPropertyValue(String name) {
            if(this.properties.containsKey(name)) {
                return this.properties.get(name);
            } else {
                return null;
            }
        }
    }

    // 状态实体
    public static class stateEntry {
        public String name;
        public String rawName;
        public String value;
        public String rawValue;

        // 构造
        public stateEntry(String name, String rawName, String value, String rawValue) {
            this.name = name;
            this.rawName = rawName;
            this.value = value;
            this.rawValue = rawValue;
        }
    }

    // 状态参数实体
    public static class serviceArgEntry {
        public String name;
        public String rawName;
        public String value;
        public String rawValue;

        // 构造
        public serviceArgEntry(String name, String rawName, String value, String rawValue) {
            this.name = name;
            this.rawName = rawName;
            this.value = value;
            this.rawValue = rawValue;
        }
    }

    // 服务实体
    public static class serviceEntry {
        public String name;
        public String rawName;
        public List<serviceArgEntry> args;

        // 构造
        public serviceEntry(String name, String rawName) {
            this.name = name;
            this.rawName = rawName;
            this.args = new ArrayList<serviceArgEntry>();
        }

        // 增加参数
        public void addArg(String name, String rawName, String value, String rawValue) {
            serviceArgEntry arg = new serviceArgEntry(name, rawName, value, rawValue);
            this.args.add(arg);
        }
    }

    // 事件实体
    public static class eventEntry {
        public String name;
        public String value;

        // 构造
        public eventEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    // 属性时间线实体
    public static class propertyTimelineEntry {
        public String property;
        public String data;
        public long timestamp;

        // 构造
        public propertyTimelineEntry(String property, String data, long timestamp) {
            this.property = property;
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    // 属性时间线实体列表
    public static class propertyTimelineListEntry {
        public long minTimeStamp;
        public List<ETSL.propertyTimelineEntry> items;

        // 构造
        public propertyTimelineListEntry() {
            this.minTimeStamp = 0;
            this.items = new ArrayList<ETSL.propertyTimelineEntry>();
        }

        // 追加数据
        public void add(String property, String data, long timestamp) {
            ETSL.propertyTimelineEntry entry = new ETSL.propertyTimelineEntry(property, data, timestamp);
            this.items.add(entry);
        }
    }

    // 事件时间线实体
    public static class eventTimelineEntry {
        public String event;
        public String data;
        public long timestamp;

        // 构造
        public eventTimelineEntry(String event, String data, long timestamp) {
            this.event = event;
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    // 事件时间线实体列表
    public static class eventTimelineListEntry {
        public long minTimeStamp;
        public List<ETSL.eventTimelineEntry> items;

        // 构造
        public eventTimelineListEntry() {
            this.minTimeStamp = 0;
            this.items = new ArrayList<ETSL.eventTimelineEntry>();
        }

        // 追加数据
        public void add(String event, String data, long timestamp) {
            ETSL.eventTimelineEntry entry = new ETSL.eventTimelineEntry(event, data, timestamp);
            this.items.add(entry);
        }
    }

    // 消息记录内容实体
    public static class messageRecordContentEntry {
        public String id;
        public String name;
        public int type;

        // 构造
        public messageRecordContentEntry(String id, String name, int type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }

    // 消息记录实体
    public static class messageRecordEntry {
        // 类型,1表日期提示（精确到天，此时description无效）,2表详细信息开头,3表详细信息中间,4表详细结束
        public String type;
        public String day;
        public String week;
        public  String description;

        // 构造
        public messageRecordEntry(String type, String day, String description) {
            this.type = type;
            this.day = day;
            this.description = description;
        }
    }
}