package com.rexense.imoco.model;

import com.rexense.imoco.typefactory.TypeFactory;

public class ItemColorLightScene implements Visitable {

    private String id;
    private String sceneName;
    private int lightness = -1;
    private int k = -1;

    public ItemColorLightScene() {
    }

    public ItemColorLightScene(String id, String sceneName) {
        this.id = id;
        this.sceneName = sceneName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getLightness() {
        return lightness;
    }

    public void setLightness(int lightness) {
        this.lightness = lightness;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
