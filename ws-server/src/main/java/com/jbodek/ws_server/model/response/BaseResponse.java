package com.jbodek.ws_server.model.response;

public class BaseResponse {

    private String type;
    private String content = "No content";

    public enum ResponseType {
        ERROR, INFO
    }

    public BaseResponse(ResponseType type, String content) {
        this.type = type.toString().toLowerCase();
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
