package com.laffey.smart.demoTest;

import android.os.Parcel;
import android.os.Parcelable;

public class IdentifierItemForCA implements Parcelable {
    private String nickName;// 设备昵称
    private String iotId;
    private String name;
    private String valueName;// 打开，关闭
    private int type;// 1:属性 2:服务 3:事件
    private Object object;
    private String desc;

    public IdentifierItemForCA() {

    }

    protected IdentifierItemForCA(Parcel in) {
        nickName = in.readString();
        iotId = in.readString();
        name = in.readString();
        valueName = in.readString();
        type = in.readInt();
        desc = in.readString();
    }

    public static final Creator<IdentifierItemForCA> CREATOR = new Creator<IdentifierItemForCA>() {
        @Override
        public IdentifierItemForCA createFromParcel(Parcel in) {
            return new IdentifierItemForCA(in);
        }

        @Override
        public IdentifierItemForCA[] newArray(int size) {
            return new IdentifierItemForCA[size];
        }
    };

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object o) {
        this.object = o;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickName);
        dest.writeString(iotId);
        dest.writeString(name);
        dest.writeString(valueName);
        dest.writeInt(type);
        dest.writeString(desc);
    }
}
