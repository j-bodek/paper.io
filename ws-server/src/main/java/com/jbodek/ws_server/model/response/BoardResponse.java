package com.jbodek.ws_server.model.response;

import java.util.Arrays;

public class BoardResponse {
    private final String type = "board";
    private String playerId;
    private int[][] board;

    public BoardResponse(String playerId, int[][] board) {
        int[][] boardCp = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            boardCp[i] = Arrays.copyOf(board[i], board[i].length);
        }

        this.playerId = playerId;
        this.board = boardCp;
    }

    public String getType() {
        return type;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int[][] getBoard() {
        return board;
    }
}
