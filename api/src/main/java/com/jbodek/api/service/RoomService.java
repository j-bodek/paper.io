package com.jbodek.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jbodek.api.api.model.CreatedRoomResponse;

@Service
public class RoomService {

    public CreatedRoomResponse createRoom() {
        String roomId = UUID.randomUUID().toString();

        // Create room in database

        return new CreatedRoomResponse(roomId);
    }

    public boolean roomExists(String roomId) {
        // Check if room exists in database

        return true;
    }
}
