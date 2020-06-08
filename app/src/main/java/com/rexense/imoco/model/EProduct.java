package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 产品实体
 */
public class EProduct {
    // 支持配网产品列表实体（注意支持序列化,以实现Intent传参）
    public static class configListEntry {
        public String productKey;
        public String name;
        public int categoryId;
        public String categoryKey;
        public String categoryName;
        public String image;
        public int nodeType;
        public int status;

        // 构造
        public configListEntry(){
            this.productKey = "";
            this.name = "";
            this.categoryKey = "";
            this.categoryName = "";
            this.image = "";
        }
    }

    // 配网引导信息实体
    public static class configGuidanceEntry {
        public int id;
        public String helpTitle;
        public String helpIcon;
        public String helpCopywriting;
        public String dnGuideIcon;
        public String dnCopywriting;
        public String buttonCopywriting;

        // 构造
        public configGuidanceEntry() {
            this.helpTitle = "";
            this.helpIcon = "";
            this.helpCopywriting = "";
            this.dnGuideIcon = "";
            this.dnCopywriting = "";
            this.buttonCopywriting = "";
        }
    }
}

