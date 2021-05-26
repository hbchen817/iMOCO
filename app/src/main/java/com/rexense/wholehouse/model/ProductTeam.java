package com.rexense.wholehouse.model;

import java.util.ArrayList;
import java.util.List;

public class ProductTeam {
    private String title;
    private List<EProduct.configListEntry> productList = new ArrayList<>();

    public ProductTeam(String title) {
        this.title = title;
    }

    public ProductTeam() {
    }

    public ProductTeam(String title, List<EProduct.configListEntry> productList) {
        this.title = title;
        this.productList = productList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<EProduct.configListEntry> getProductList() {
        return productList;
    }

    public void setProductList(List<EProduct.configListEntry> productList) {
        this.productList = productList;
    }
}
