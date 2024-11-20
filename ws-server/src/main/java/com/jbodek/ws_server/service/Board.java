package com.jbodek.ws_server.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jbodek.ws_server.model.Player;
import com.jbodek.ws_server.model.response.BoardResponse;

public class Board {

    private String winnerId = null;
    private boolean playing = false;
    private int[][] board;
    private SimpMessagingTemplate template;

    public Board(SimpMessagingTemplate template) {
        this.board = new int[50][50];
        this.template = template;
    }

    public String getWinnerId() {
        return this.winnerId;
    }

    public int[][] getBoard() {
        return this.board;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void startPlaying() {
        this.playing = true;
    }

    public void stopPlaying() {
        this.playing = false;
    }

    public void initPlayer(Player player) {
        // place player on the board

        int[] position = player.getData().getCurPos();

        if (position[0] >= 0 && position[0] < 50 && position[1] >= 0 && position[1] < 50) {
            this.board[position[1]][position[0]] = player.getData().getAreaValue();
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
                this.board[y][x] = player.getData().getAreaValue();
            }
        }

        this.broadcastUpdate(player.getId());
    }

    public void movePlayers(HashMap<String, Player> players) {
        for (String key : players.keySet()) {
            Player player = players.get(key);
            player.move();

            // detect collision
            int[] position = player.getData().getCurPos();

            if (!this.collisionDetected(position)) {

                // update player line
                if (this.board[position[1]][position[0]] != player.getData().getAreaValue()) {
                    this.board[position[1]][position[0]] = player.getData().getLineValue();
                }

                // update player area
                this.updatePlayerArea(player);
            } else {
                this.winnerId = this.getWinnerPlayer(player, players);
                this.stopPlaying();
            }
        }
    }

    private String getWinnerPlayer(Player collidatedPlayer, HashMap<String, Player> players) {
        // return winner player id

        int[] position = collidatedPlayer.getData().getCurPos();

        // Check if collidated player lost
        if (this.isOutOfBounds(position)
                || this.board[position[1]][position[0]] == collidatedPlayer.getData().getLineValue()) {

            for (String id : players.keySet()) {
                if (id != collidatedPlayer.getId()) {
                    return id;
                }
            }
        }

        // Player crossed other player line and won
        return collidatedPlayer.getId();
    }

    private boolean collisionDetected(int[] position) {

        // Check if player is out of bounds
        if (this.isOutOfBounds(position)) {
            return true;
        }

        // Check if player collidated with his own line or other player (all lines are
        // odd)
        if (this.board[position[1]][position[0]] != 0 && this.board[position[1]][position[0]] % 2 != 0) {
            return true;
        }

        return false;
    }

    private boolean isOutOfBounds(int[] position) {
        if (position[0] < 0 || position[0] >= 50 || position[1] < 0 || position[1] >= 50) {
            return true;
        }

        return false;
    }

    private void updatePlayerArea(Player player) {
        int[] curPos = player.getData().getCurPos();
        int[] prevPos = player.getData().getPrevPos();

        // if player don't attached new area, return
        if (this.board[curPos[1]][curPos[0]] != player.getData().getAreaValue()
                || this.board[prevPos[1]][prevPos[0]] != player.getData().getLineValue()) {
            return;
        }

        // Attach new area
        int[] boundingBox = this.findBoundingBox(player);
        int[][] prevBoundingBoxArea = this.copyPrevBoundingBoxArea(boundingBox);
        this.floodFillBoundingBox(boundingBox, player);
        this.setPlayerArea(boundingBox, prevBoundingBoxArea, player);
        this.broadcastUpdate(player.getId());
    }

    private int[] findBoundingBox(Player player) {
        // find bounding box around player current and newly attached area

        int[] position = player.getData().getCurPos();

        // [minX, minY, maxX, maxY]
        int[] boundingBox = new int[] { position[0], position[1], position[0], position[1] };
        Queue<int[]> queue = new LinkedList<int[]>();
        queue.add(position);

        HashSet<String> visited = new HashSet<String>();
        visited.add(position[0] + "," + position[1]);

        // move in 4 directions
        int[][] directions = {
                { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }
        };

        while (queue.size() > 0) {

            int[] cur = queue.poll();

            // update bounding box
            boundingBox[0] = Math.min(boundingBox[0], cur[0]); // min x
            boundingBox[1] = Math.min(boundingBox[1], cur[1]); // min y
            boundingBox[2] = Math.max(boundingBox[2], cur[0]); // max x
            boundingBox[3] = Math.max(boundingBox[3], cur[1]); // max y

            for (int[] dir : directions) {
                int[] next = new int[] { cur[0] + dir[0], cur[1] + dir[1] };
                if (visited.contains(next[0] + "," + next[1])) {
                    continue;
                }

                // skip if out of bounds or not player area
                if (this.isOutOfBounds(next)
                        || (this.board[next[1]][next[0]] != player.getData().getAreaValue()
                                && this.board[next[1]][next[0]] != player.getData().getLineValue())) {
                    continue;
                }

                visited.add(next[0] + "," + next[1]);
                queue.add(next);
            }
        }

        return boundingBox;
    }

    private int[][] copyPrevBoundingBoxArea(int[] boundingBox) {
        // copy previous bounding box area values in order to then switch
        // flood fill value to previous values

        int[][] prevBoundingBoxArea = new int[boundingBox[3] - boundingBox[1] + 1][boundingBox[2] - boundingBox[0] + 1];

        for (int row = boundingBox[1]; row <= boundingBox[3]; row++) {
            for (int col = boundingBox[0]; col <= boundingBox[2]; col++) {
                prevBoundingBoxArea[row - boundingBox[1]][col - boundingBox[0]] = this.board[row][col];
            }
        }

        return prevBoundingBoxArea;
    }

    private void floodFillBoundingBox(int[] boundingBox, Player player) {
        // flood fill bounding box (starting from edges) to create boundry around
        // player area

        // iterate over bounding box edges
        for (int row = boundingBox[1]; row <= boundingBox[3]; row++) {
            if (row == boundingBox[1] || row == boundingBox[3]) {
                // for top and bottom rows
                for (int col = boundingBox[0]; col <= boundingBox[2]; col++) {
                    this.boundryFloodFill(new int[] { col, row }, player, boundingBox);
                }
            } else {
                this.boundryFloodFill(new int[] { boundingBox[0], row }, player, boundingBox);
                this.boundryFloodFill(new int[] { boundingBox[2], row }, player, boundingBox);
            }
        }
    }

    private void boundryFloodFill(int[] position, Player player, int[] boundingBox) {
        // flood fill boundry around player area within bounding box starting from
        // position

        int fillValue = -1;
        Queue<int[]> queue = new LinkedList<int[]>();
        queue.add(position);

        HashSet<String> visited = new HashSet<String>();
        visited.add(position[0] + "," + position[1]);

        // move in 4 directions
        int[][] directions = {
                { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }
        };

        while (queue.size() > 0) {

            int[] cur = queue.poll();

            // skip if out of bounds, already set to -1 or player area/line
            if (cur[0] < boundingBox[0]
                    || cur[0] > boundingBox[2]
                    || cur[1] < boundingBox[1]
                    || cur[1] > boundingBox[3]
                    || this.board[cur[1]][cur[0]] == fillValue
                    || this.board[cur[1]][cur[0]] == player.getData().getAreaValue()
                    || this.board[cur[1]][cur[0]] == player.getData().getLineValue()) {
                continue;
            }

            this.board[cur[1]][cur[0]] = fillValue;

            for (int[] dir : directions) {
                int[] next = new int[] { cur[0] + dir[0], cur[1] + dir[1] };
                if (visited.contains(next[0] + "," + next[1])) {
                    continue;
                }

                visited.add(next[0] + "," + next[1]);
                queue.add(next);
            }
        }
    }

    private void setPlayerArea(int[] boundingBox, int[][] prevBoundingBoxArea, Player player) {
        // set unflooded area within bounding box to player area value
        // switch flood fill value to previous values

        for (int row = boundingBox[1]; row <= boundingBox[3]; row++) {
            for (int col = boundingBox[0]; col <= boundingBox[2]; col++) {
                if (this.board[row][col] == -1) {
                    int prev = prevBoundingBoxArea[row - boundingBox[1]][col - boundingBox[0]];
                    this.board[row][col] = prev;
                } else {
                    this.board[row][col] = player.getData().getAreaValue();
                }
            }
        }
    }

    public void endGame(HashMap<String, Player> players) {
        // game ended before one of the players won, calculate area
        // and set winner player

        this.playing = false;

        HashMap<String, Integer> playerScores = new HashMap<String, Integer>();
        HashMap<Integer, String> playerAreaValueMap = new HashMap<Integer, String>();

        for (String playerId : players.keySet()) {
            Player player = players.get(playerId);
            int areaValue = player.getData().getAreaValue();
            playerAreaValueMap.put(areaValue, playerId);
            playerScores.put(playerId, 0);
        }

        for (int row = 0; row < 50; row++) {
            for (int col = 0; col < 50; col++) {
                int areaValue = this.board[row][col];
                if (playerAreaValueMap.containsKey(areaValue)) {
                    playerScores.put(playerAreaValueMap.get(areaValue),
                            playerScores.get(playerAreaValueMap.get(areaValue)) + 1);
                }
            }
        }

        // check if draw
        if (playerScores.values().stream().distinct().count() == 1) {
            this.winnerId = null;
            return;
        }

        String winner = null;
        int maxScore = 0;

        for (String playerId : playerScores.keySet()) {
            if (playerScores.get(playerId) > maxScore) {
                maxScore = playerScores.get(playerId);
                winner = playerId;
            }
        }

        this.winnerId = winner;
    }

    private void broadcastUpdate(String playerId) {
        BoardResponse response = new BoardResponse(playerId, this.board);
        this.template.convertAndSend("/room/subscribe", response);
    }
}
