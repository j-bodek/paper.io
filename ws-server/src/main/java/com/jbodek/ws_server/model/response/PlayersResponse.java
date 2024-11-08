package com.jbodek.ws_server.model.response;

import java.util.HashMap;

import com.jbodek.ws_server.model.PlayerData;

public class PlayersResponse {
    private final String type = "players";
    private HashMap<String, PlayerData> players;

    public PlayersResponse(HashMap<String, PlayerData> players) {
        this.players = players;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, PlayerData> getPlayers() {
        return players;
    }
}
