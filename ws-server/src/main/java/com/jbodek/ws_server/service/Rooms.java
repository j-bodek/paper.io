package com.jbodek.ws_server.service;

import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jbodek.ws_server.model.Player;
import com.jbodek.ws_server.model.payload.PlayerPayload;
import com.jbodek.ws_server.model.response.BaseResponse;
import com.jbodek.ws_server.model.response.ErrorResponse;
import com.jbodek.ws_server.model.response.InfoResponse;

public class Rooms {
    // Class used as room manager

    static volatile private HashMap<String, Room> rooms = new HashMap<String, Room>();

    static synchronized public BaseResponse addPlayer(String roomName, PlayerPayload playerPayload,
            SimpMessagingTemplate template) {
        Room room = null;

        if (rooms.containsKey(roomName)) {
            room = rooms.get(roomName);
        } else {
            room = new Room(roomName, template);
            rooms.put(roomName, room);
        }

        if (room.size() >= 2 || room.isAlive()) {
            // todo: send message to client - throw exception
            return new ErrorResponse("Room " + roomName + " is full or active");
        }

        if (room.hasPlayer(playerPayload.getId())) {
            return new ErrorResponse("Player " + playerPayload.getId() + " already in room " + roomName);
        }

        Player player = new Player(playerPayload, room.size());
        room.addPlayer(player);

        if (room.size() == 2 && !room.isAlive()) {
            room.start();
        }

        return new InfoResponse("Player " + player.getName() + " joined room " + roomName);
    }

    static public Player getPlayer(String roomName, String playerId) {
        Room room = rooms.get(roomName);
        if (room == null) {
            return null;
        }

        return room.getPlayer(playerId);
    }

    static synchronized public void disconnected(String roomName, String playerId) {
        Room room = rooms.get(roomName);
        if (room == null) {
            return;
        }

        room.removePlayer(playerId);
    }

    // static synchronized void removePlayer(String roomName, String playerId) {
    // if (!rooms.containsKey(roomName)) {
    // return;
    // }

    // Room room = rooms.get(roomName);
    // room.removePlayer(playerId);

    // if (room.size() == 0) {
    // // if room is empty, remove it
    // removeRoom(roomName);
    // }
    // }

    static synchronized void removeRoom(String roomName) {
        if (!rooms.containsKey(roomName)) {
            return;
        }

        rooms.remove(roomName);
    }
}
