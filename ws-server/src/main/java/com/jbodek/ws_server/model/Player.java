package com.jbodek.ws_server.model;

import com.jbodek.ws_server.model.payload.PlayerPayload;

public class Player {
    // Represents a player in the room

    private String id;
    private int index;
    private String name;

    private PlayerData data;

    public Player(String id, int index, String name) {
        this.id = id;
        // index can be either 0 or 1 (player 1 or player 2)
        this.index = index;
        this.name = name;
        this.data = new PlayerData(this);
    }

    public Player(PlayerPayload playerPayload, int index) {
        this.id = playerPayload.getId();
        this.index = index;
        this.name = playerPayload.getName();
        this.data = new PlayerData(this);
    }

    public Player(Player player) {
        this.id = player.getId();
        this.index = player.getIndex();
        this.name = player.getName();
        this.data = new PlayerData(this);
    }

    public String getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public PlayerData getData() {
        return this.data;
    }

    public void changeDirection(int[] direction) {
        this.data.setDirection(direction);
    }

    public void move() {
        int[] curPos = this.data.getCurPos();
        int[] direction = this.data.getDirection();

        int[] newPos = { curPos[0] + direction[0], curPos[1] + direction[1] };

        this.data.setPrevPos(curPos);
        this.data.setCurPos(newPos);
    }
}
