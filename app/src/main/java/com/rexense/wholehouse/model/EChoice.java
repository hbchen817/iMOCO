package com.rexense.wholehouse.model;

import java.io.Serializable;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-11 15:29
 * Description: 选择实体
 */
public class EChoice {
    // 条目实体
    public static class itemEntry implements Serializable {
        public String name;
        public String value;
        public boolean isSelected;

        // 构造
        public itemEntry(String name, String value, boolean selected){
            this.name = name;
            this.value = value;
            this.isSelected = selected;
        }
    }
}

