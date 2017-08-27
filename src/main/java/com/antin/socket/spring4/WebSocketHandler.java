package com.antin.socket.spring4;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2017/8/27.
 */
public class WebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> socketSessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        socketSessions.add(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        sendMessageToUser((TextMessage) message);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        socketSessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void sendMessageToUser(TextMessage message) throws IOException {
        for (WebSocketSession   socketSession : socketSessions){
            if (socketSession.isOpen()) {
                socketSession.sendMessage(message);
            }
        }
    }
}