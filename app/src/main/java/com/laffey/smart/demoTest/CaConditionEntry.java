package com.laffey.smart.demoTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaConditionEntry {
    private List<Object> entries = new ArrayList<>();

    public List<Object> getEntries() {
        return entries;
    }

    public static class Property {
        private String uri = "condition/device/property";
        private Map<String,Object> params=new HashMap<>();

        public void setProductKey(String productKey){
            params.put("productKey",productKey);
        }

        public void setDeviceName(String deviceName){
            params.put("deviceName",deviceName);
        }

        public void setPropertyName(String propertyName){
            params.put("propertyName",propertyName);
        }

        public void setCompareType(String compareType){
            params.put("compareType",compareType);
        }

        public void setCompareValue(Object compareValue){
            params.put("compareValue",compareValue);
        }
    }

    public static class TimeRange {
        private String uri = "condition/timeRange";
        private Map<String, String> params = new HashMap<>();

        public void setFormat(String format) {
            this.params.put("format",format);
        }

        public void setBeginDate(String beginDate) {
            this.params.put("beginDate",beginDate);
        }

        public void setEndDate(String endDate) {
            this.params.put("endDate",endDate);
        }

        public void setRepeat(String repeat) {
            this.params.put("repeat",repeat);
        }
    }

    public static class Timer {
        private String uri = "condition/timer";
        private Map<String, Object> params = new HashMap<>();

        public void setCron(String cron) {
            this.params.put("cron", cron);
        }

        public void setCronType(String cronType) {
            this.params.put("cronType", cronType);
        }

        public void setTimezoneID(String timezoneID) {
            this.params.put("timezoneID", timezoneID);
        }
    }
}
