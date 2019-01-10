package com.ivms.ivms8700.bean;

import org.json.JSONObject;

public class EventEntity {
    private int type;
    private JSONObject jsonObject;

    public EventEntity(int type, JSONObject jsonObject) {
        this.type = type;
        this.jsonObject = jsonObject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
