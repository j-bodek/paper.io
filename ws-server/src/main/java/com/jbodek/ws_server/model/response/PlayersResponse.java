package com.jbodek.ws_server.model.response;

import java.util.HashMap;

import com.jbodek.ws_server.model.PlayerData;

public class PlayersResponse {
    private final String type = "players";
    private long gameplayTime;
    private HashMap<String, PlayerData> players;

    public PlayersResponse(long gameplayTime, HashMap<String, PlayerData> players) {
        this.gameplayTime = gameplayTime;
        this.players = players;
    }

    public PlayersResponse(HashMap<String, PlayerData> players) {
        this.gameplayTime = 0;
        this.players = players;
    }

    public String getType() {
        return type;
    }

    public long getGameplayTime() {
        return gameplayTime;
    }

    public HashMap<String, PlayerData> getPlayers() {
        return players;
    }
}
