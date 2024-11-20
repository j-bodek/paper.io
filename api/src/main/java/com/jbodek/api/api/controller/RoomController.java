package com.jbodek.api.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jbodek.api.api.model.CreatedRoomResponse;
import com.jbodek.api.service.RoomService;

@RestController
public class RoomController {

    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/room/create")
    public CreatedRoomResponse postMethodName() {
        return this.roomService.createRoom();
    }

    @GetMapping("/room/exists")
    public void getRoomExists(@RequestParam String roomId) {
        if (!this.roomService.roomExists(roomId)) {
            // Return 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
    }

}
