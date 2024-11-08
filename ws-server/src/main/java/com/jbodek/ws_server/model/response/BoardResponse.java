package com.jbodek.ws_server.model.response;

import java.util.Arrays;

public class BoardResponse {
    private final String type = "board";
    private int[][] board;

    public BoardResponse(int[][] board) {
        int[][] boardCp = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            boardCp[i] = Arrays.copyOf(board[i], board[i].length);
        }

        this.board = boardCp;
    }

    public String getType() {
        return type;
    }

    public int[][] getBoard() {
        return board;
    }
}
