package com.rexense.smart.demoTest;

import java.util.HashMap;
import java.util.Map;

public class ServiceInputData {
    private DataType dataType;
    private String identifier;
    private String name;
    private String selectValue;
    private String selectName;

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Map<String, Object> getDataTypeMap(){
        if (dataType == null)
            dataType = new DataType();
        return dataType.getSpecs();
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void put(String key, Object value) {
        if (dataType == null)
            dataType = new DataType();
        dataType.put(key, value);
    }

    public Object get(String key) {
        if (dataType == null)
            dataType = new DataType();
        return dataType.get(key);
    }

    public void setType(String type) {
        if (dataType == null)
            dataType = new DataType();
        dataType.setType(type);
    }

    public class DataType {
        private Map<String, Object> specs = new HashMap<>();
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void put(String key, Object value) {
            specs.put(key, value);
        }

        public Object get(String key) {
            return specs.get(key);
        }

        public Map<String, Object> getSpecs() {
            return specs;
        }
    }
}
