package com.r.pixel.websocket;

import com.r.pixel.dto.PixelUpdateMessage;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class PixelWebSocketHandler extends TextWebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(PixelWebSocketHandler.class);

	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
	private final ObjectMapper objectMapper;

	public PixelWebSocketHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessions.remove(session);
	}

	public void broadcast(PixelUpdateMessage message) {
		String payload;
		try {
			payload = objectMapper.writeValueAsString(message);
		} catch (Exception ex) {
			log.error("Failed to serialize pixel update", ex);
			return;
		}
		for (WebSocketSession session : sessions) {
			if (!session.isOpen()) {
				sessions.remove(session);
				continue;
			}
			try {
				session.sendMessage(new TextMessage(payload));
			} catch (Exception ex) {
				sessions.remove(session);
			}
		}
	}
}
