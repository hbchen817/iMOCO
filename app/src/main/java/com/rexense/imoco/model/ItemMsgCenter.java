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
    private String time;
    private String productKey;

    //以下属性是给设备共享类消息使用
    private boolean showBtnView;
    private int status;///-1: 初始化0：同意1：拒绝2：取消3：过期4：抢占5：删除6：发起者已解绑99：异常
    private String recordId;
    private String batchId;
    private String productImg;


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

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
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
