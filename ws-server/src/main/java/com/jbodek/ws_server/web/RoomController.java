package com.jbodek.ws_server.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.jbodek.ws_server.model.payload.PlayerPayload;
import com.jbodek.ws_server.model.response.BaseResponse;
import com.jbodek.ws_server.service.Rooms;

@Controller
public class RoomController {

    private final SimpMessagingTemplate template;

    public RoomController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/room/join")
    @SendTo("/room/subscribe")
    public BaseResponse join(@Payload PlayerPayload playerPayload, SimpMessageHeaderAccessor headerAccessor)
            throws Exception {
        headerAccessor.getSessionAttributes().put("playerId", playerPayload.getId());
        headerAccessor.getSessionAttributes().put("roomId", "room");
        return Rooms.addPlayer("room", playerPayload, this.template);
    }

}