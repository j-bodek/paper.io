package com.jbodek.ws_server.model.response;

public class GameOverResponse {
    private final String type = "gameOver";
    private String winnerId;

    public GameOverResponse(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getType() {
        return type;
    }

    public String getWinnerId() {
        return winnerId;
    }
}
