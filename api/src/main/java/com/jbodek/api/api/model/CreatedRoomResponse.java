package com.jbodek.api.api.model;

public class CreatedRoomResponse {
    private String uuid;

    public CreatedRoomResponse(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
