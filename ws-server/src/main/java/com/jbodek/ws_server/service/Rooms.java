package com.jbodek.ws_server.service;

import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jbodek.ws_server.model.Player;
import com.jbodek.ws_server.model.payload.PlayerPayload;
import com.jbodek.ws_server.model.response.BaseResponse;
import com.jbodek.ws_server.model.response.ErrorResponse;
import com.jbodek.ws_server.model.response.InfoResponse;

public class Rooms {
    static private HashMap<String, Room> rooms = new HashMap<String, Room>();

    static public BaseResponse addPlayer(String roomName, PlayerPayload playerPayload, SimpMessagingTemplate template) {
        Room room = null;

        if (rooms.containsKey(roomName)) {
            room = rooms.get(roomName);
        } else {
            room = new Room(template);
            rooms.put(roomName, room);
        }

        if (room.size() >= 2) {
            // todo: send message to client - throw exception
            return new ErrorResponse("Room " + roomName + " is full");
        }

        if (room.hasPlayer(playerPayload.getId())) {
            return new ErrorResponse("Player " + playerPayload.getId() + " already in room " + roomName);
        }

        Player player = new Player(playerPayload, room.size());
        room.addPlayer(player);
        return new InfoResponse("Player " + player.getName() + " joined room " + roomName);
    }

    static public void removePlayer(String roomName, String playerId) {
        Room room = rooms.get(roomName);
        if (room == null) {
            return;
        }

        room.removePlayer(playerId);

        if (room.size() == 0) {
            rooms.remove(roomName);
        }
    }
}
