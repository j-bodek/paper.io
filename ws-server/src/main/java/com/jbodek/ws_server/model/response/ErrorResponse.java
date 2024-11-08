package com.jbodek.ws_server.model.response;

public class ErrorResponse extends BaseResponse {
    public ErrorResponse(String content) {
        super(BaseResponse.ResponseType.ERROR, content);
    }
}
