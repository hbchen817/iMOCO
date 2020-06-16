package com.rexense.imoco.model;


import com.rexense.imoco.typefactory.TypeFactory;

/**
 * @author fanyy
 * @date 2018/5/22
 */
public class ItemSceneLog implements Visitable {
    private String id;
    private String logName;
    private String logDetail;
    private String logTime;
    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogDetail() {
        return logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int type (TypeFactory typeFactory){
        return typeFactory.type(this);
    }
}
