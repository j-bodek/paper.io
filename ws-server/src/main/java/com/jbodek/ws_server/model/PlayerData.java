package com.jbodek.ws_server.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerData {
    private int lineValue;
    private int areaValue;
    private int[] direction;
    private int[] curPos;
    private int[] prevPos;

    public PlayerData(Player player) {
        this.lineValue = player.getIndex() * 2 + 1;
        this.areaValue = player.getIndex() * 2 + 2;

        // player 1 moves right, player 2 moves left
        this.direction = new int[] { 1 - 2 * player.getIndex(), 0 };

        // either top left or bottom right corner
        int point = Math.max(50 * player.getIndex() - 1, 1);
        this.curPos = new int[] { point, point };
        this.prevPos = new int[] { point, point };
    }

    public PlayerData(PlayerData player) {
        this.lineValue = player.getLineValue();
        this.areaValue = player.getAreaValue();
        this.direction = Arrays.copyOf(player.direction, player.direction.length);
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
        return Arrays.copyOf(curPos, curPos.length);
    }

    public void setPrevPos(int[] prevPos) {
        this.prevPos = Arrays.copyOf(prevPos, prevPos.length);
    }

    @JsonIgnore
    public int[] getDirection() {
        return Arrays.copyOf(direction, direction.length);
    }

    public void setDirection(int[] direction) {
        this.direction = Arrays.copyOf(direction, direction.length);
    }

    @JsonIgnore
    public int[] getPrevPos() {
        return Arrays.copyOf(prevPos, prevPos.length);
    }

    public void setCurPos(int[] curPos) {
        this.curPos = Arrays.copyOf(curPos, curPos.length);
    }
}
