package com.jbodek.ws_server.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerData {
    private int lineValue;
    private int areaValue;
    private int[] curPos;
    private int[] prevPos;

    public PlayerData(Player player) {
        this.lineValue = player.getIndex() * 2 + 1;
        this.areaValue = player.getIndex() * 2 + 2;

        // either top left or bottom right corner
        int point = Math.max(50 * player.getIndex() - 1, 1);

        this.curPos = new int[] { point, point };
        this.prevPos = new int[] { point, point };
    }

    public PlayerData(PlayerData player) {
        this.lineValue = player.getLineValue();
        this.areaValue = player.getAreaValue();
        this.curPos = Arrays.copyOf(player.getCurPos(), player.getCurPos().length);
        this.prevPos = Arrays.copyOf(player.getPrevPos(), player.getPrevPos().length);
    }

    public int getLineValue() {
        return this.lineValue;
    }

    public int getAreaValue() {
        return this.areaValue;
    }

    public int[] getCurPos() {
        return this.curPos;
    }

    @JsonIgnore
    public int[] getPrevPos() {
        return this.prevPos;
    }
}
