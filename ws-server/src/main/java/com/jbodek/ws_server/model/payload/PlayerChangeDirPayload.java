package com.jbodek.ws_server.model.payload;

import java.util.Arrays;

public class PlayerChangeDirPayload {
    // move player payload class

    private String id;
    private String roomId;
    private int[] direction;

    public PlayerChangeDirPayload(String id, String roomId, int[] direction) {
        this.id = id;
        this.roomId = roomId;
        this.direction = Arrays.copyOf(direction, direction.length);
    }

    public String getId() {
        return this.id;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public int[] getDirection() {
        return Arrays.copyOf(this.direction, this.direction.length);
    }
}
