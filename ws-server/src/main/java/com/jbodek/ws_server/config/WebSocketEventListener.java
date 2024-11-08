package com.jbodek.ws_server.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.jbodek.ws_server.model.response.ErrorResponse;
import com.jbodek.ws_server.service.Rooms;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate template;

    public WebSocketEventListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String playerId = (String) headerAccessor.getSessionAttributes().get("playerId");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        Rooms.removePlayer(roomId, playerId);

        this.template.convertAndSend("/room/subscribe", new ErrorResponse("Player " + playerId + " disconnected"));
    }
}
