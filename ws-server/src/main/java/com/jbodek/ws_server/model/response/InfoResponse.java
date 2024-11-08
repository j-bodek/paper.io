package com.jbodek.ws_server.model.response;

public class InfoResponse extends BaseResponse {
    public InfoResponse(String content) {
        super(BaseResponse.ResponseType.INFO, content);
    }
}
