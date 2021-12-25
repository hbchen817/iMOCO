package com.rexense.smart.model;

import java.util.List;

public class SceneListResponse {
    private int code;
    private String message;
    private List<ItemScene> sceneList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ItemScene> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<ItemScene> sceneList) {
        this.sceneList = sceneList;
    }
}
