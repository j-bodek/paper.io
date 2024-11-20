package com.jbodek.ws_server.model.payload;

public class PlayerPayload {
    // Create new player payload class

    private String id;
    private String name;

    public PlayerPayload(String id, int index, String name) {
        this.id = id;
        this.name = name;
    }

    public PlayerPayload(PlayerPayload player) {
        this.id = player.getId();
        this.name = player.getName();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
