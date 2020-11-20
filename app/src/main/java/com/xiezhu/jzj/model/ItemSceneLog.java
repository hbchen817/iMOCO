package com.xiezhu.jzj.model;


import com.xiezhu.jzj.typefactory.TypeFactory;

/**
 * @author fanyy
 * @date 2018/5/22
 */
public class ItemSceneLog implements Visitable {
    private String id;
    private String logName;
    private int result;
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
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
