package com.laffey.smart.demoTest;

import java.util.List;

public class ResponseIdentifierForCA {
    private String categoryType;
    private String identifier;
    private String name;
    private int type;
    private String specsType;
    private String productKey;
    private String deviceName;
    private List<PropertyItem> items;

    public String getSpecsType() {
        return specsType;
    }

    public void setSpecsType(String specsType) {
        this.specsType = specsType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<PropertyItem> getItems() {
        return items;
    }

    public void setItems(List<PropertyItem> items) {
        this.items = items;
    }

    public static class PropertyItem{
        private String title;
        private String value;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
