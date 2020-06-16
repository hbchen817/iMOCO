package com.rexense.imoco.model;


import com.rexense.imoco.typefactory.TypeFactory;

/**
 * @author fanyy
 * @date 2018/5/22
 */
public class ItemMsgCenter implements Visitable{
    private int id;
    private String content;
    private String title;
    private boolean showBtnView;
    private int status;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowBtnView() {
        return showBtnView;
    }

    public void setShowBtnView(boolean showBtnView) {
        this.showBtnView = showBtnView;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
