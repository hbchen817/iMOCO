package com.laffey.smart.event;

public class ColorLightSceneEvent {

    public enum TYPE{
        TYPE_LIGHTNESS,
        TYPE_COLOR_TEMPERATURE;
    }

    private TYPE mType;
    private int mValue;

    public TYPE getmType() {
        return mType;
    }

    public void setmType(TYPE mType) {
        this.mType = mType;
    }

    public int getmValue() {
        return mValue;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
    }
}
