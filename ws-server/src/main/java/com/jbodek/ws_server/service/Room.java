package com.jbodek.ws_server.service;

import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jbodek.ws_server.model.Player;
import com.jbodek.ws_server.model.PlayerData;
import com.jbodek.ws_server.model.response.InfoResponse;
import com.jbodek.ws_server.model.response.PlayersResponse;

public class Room {

    private SimpMessagingTemplate template;
    private HashMap<String, Player> players;

    public Room(SimpMessagingTemplate template) {
        this.players = new HashMap<String, Player>();
        this.template = template;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getId(), new Player(player));

        this.template.convertAndSend("/room/subscribe", new PlayersResponse(this.getPlayersData()));

        if (this.size() == 2) {
            this.startGame();
        }
    }

    public void removePlayer(String playerId) {
        this.players.remove(playerId);
    }

    public boolean hasPlayer(String playerId) {
        return this.players.containsKey(playerId);
    }

    public HashMap<String, PlayerData> getPlayersData() {
        HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();
        for (String key : this.players.keySet()) {
            players.put(key, new PlayerData(this.players.get(key).getData()));
        }

        return players;
    }

    public int size() {
        return this.players.size();
    }

    public void startGame() {
        this.template.convertAndSend("/room/subscribe", new InfoResponse("Game started"));
    }
}
