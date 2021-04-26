package com.rexense.wholehouse.demoTest;

import android.os.Parcel;
import android.os.Parcelable;

import com.rexense.wholehouse.contract.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaConditionEntry implements Parcelable {
    private List<Object> entries = new ArrayList<>();

    protected CaConditionEntry(Parcel in) {
    }

    public static final Creator<CaConditionEntry> CREATOR = new Creator<CaConditionEntry>() {
        @Override
        public CaConditionEntry createFromParcel(Parcel in) {
            return new CaConditionEntry(in);
        }

        @Override
        public CaConditionEntry[] newArray(int size) {
            return new CaConditionEntry[size];
        }
    };

    public CaConditionEntry() {

    }

    public List<Object> getEntries() {
        return entries;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static class Property {
        private String uri = Constant.SCENE_CONDITION_PROPERTY;
        private Map<String, Object> params = new HashMap<>();

        public void setProductKey(String productKey) {
            params.put("productKey", productKey);
        }

        public void setDeviceName(String deviceName) {
            params.put("deviceName", deviceName);
        }

        public void setPropertyName(String propertyName) {
            params.put("propertyName", propertyName);
        }

        public void setCompareType(String compareType) {
            params.put("compareType", compareType);
        }

        public void setCompareValue(Object compareValue) {
            params.put("compareValue", compareValue);
        }

        public String getProductKey() {
            return (String) params.get("productKey");
        }

        public String getDeviceName() {
            return (String) params.get("deviceName");
        }

        public String getPropertyName() {
            return (String) params.get("propertyName");
        }

        public String getCompareType() {
            return (String) params.get("compareType");
        }

        public Object getCompareValue() {
            return params.get("compareValue");
        }
    }

    public static class TimeRange {
        private String uri = Constant.SCENE_CONDITION_TIME_RANGE;
        private Map<String, String> params = new HashMap<>();

        public void setFormat(String format) {
            this.params.put("format", format);
        }

        public void setBeginDate(String beginDate) {
            this.params.put("beginDate", beginDate);
        }

        public void setEndDate(String endDate) {
            this.params.put("endDate", endDate);
        }

        public void setRepeat(String repeat) {
            this.params.put("repeat", repeat);
        }

        public void setTimezoneID(String timezoneID) {
            this.params.put("timezoneID", timezoneID);
        }

        public String getFormat() {
            return this.params.get("format");
        }

        public String getBeginDate() {
            return this.params.get("beginDate");
        }

        public String getEndDate() {
            return this.params.get("endDate");
        }

        public String getRepeat() {
            return this.params.get("repeat");
        }

        public String getTimezoneID() {
            return this.params.get("timezoneID");
        }
    }

    public static class Timer implements Parcelable {
        private String uri = Constant.SCENE_CONDITION_TIMER;
        private Map<String, Object> params = new HashMap<>();

        public Timer(Parcel in) {
            uri = in.readString();
            params = in.readHashMap(HashMap.class.getClassLoader());
        }

        public static final Creator<Timer> CREATOR = new Creator<Timer>() {
            @Override
            public Timer createFromParcel(Parcel in) {
                Timer timer = new Timer();
                timer.uri = in.readString();
                timer.params = in.readHashMap(HashMap.class.getClassLoader());
                return new Timer(in);
            }

            @Override
            public Timer[] newArray(int size) {
                return new Timer[size];
            }
        };

        public Timer() {

        }

        public void setCron(String cron) {
            this.params.put("cron", cron);
        }

        public void setCronType(String cronType) {
            this.params.put("cronType", cronType);
        }

        public void setTimezoneID(String timezoneID) {
            this.params.put("timezoneID", timezoneID);
        }

        public String getCron() {
            return (String) this.params.get("cron");
        }

        public String getCronType() {
            return (String) this.params.get("cronType");
        }

        public String getTimezoneID() {
            return (String) this.params.get("timezoneID");
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(uri);
            dest.writeMap(params);
        }
    }

    public static class Event {
        private String uri = Constant.SCENE_CONDITION_EVENT;
        private Map<String, Object> params = new HashMap<>();

        public void setProductKey(String productKey) {
            params.put("productKey", productKey);
        }

        public String getProductKey() {
            return (String) params.get("productKey");
        }

        public void setDeviceName(String deviceName) {
            params.put("deviceName", deviceName);
        }

        public String getDeviceName() {
            return (String) params.get("deviceName");
        }

        public void setEventCode(String eventCode) {
            params.put("eventCode", eventCode);
        }

        public String getEventCode() {
            return (String) params.get("eventCode");
        }

        public void setPropertyName(String propertyName) {
            params.put("propertyName", propertyName);
        }

        public String getPropertyName() {
            return (String) params.get("propertyName");
        }

        public void setCompareType(String compareType) {
            params.put("compareType", compareType);
        }

        public String getCompareType() {
            return (String) params.get("compareType");
        }

        public void setCompareValue(Object compareValue) {
            params.put("compareValue", compareValue);
        }

        public Object getCompareValue() {
            return params.get("compareValue");
        }
    }
}
