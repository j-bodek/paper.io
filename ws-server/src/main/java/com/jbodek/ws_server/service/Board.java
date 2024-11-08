package com.jbodek.ws_server.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jbodek.ws_server.model.response.BoardResponse;

public class Board {

    private int[][] board;
    private SimpMessagingTemplate template;

    public Board(SimpMessagingTemplate template) {
        this.board = new int[50][50];
        this.template = template;
    }

    public int[][] getBoard() {
        return this.board;
    }

    public void initPlayer(int[] position, int value) {
        if (position[0] >= 0 && position[0] < 50 && position[1] >= 0 && position[1] < 50) {
            this.board[position[1]][position[0]] = value;
        }

        // 8 directions
        int[][] directions = {
                { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        for (int[] dir : directions) {
            int x = position[0] + dir[0];
            int y = position[1] + dir[1];
            if (x >= 0 && x < 50 && y >= 0 && y < 50) {
                this.board[y][x] = value;
            }
        }
        ;

        this.broadcastUpdate();
    }

    private void broadcastUpdate() {
        BoardResponse response = new BoardResponse(this.board);
        this.template.convertAndSend("/room/subscribe", response);
    }
}
