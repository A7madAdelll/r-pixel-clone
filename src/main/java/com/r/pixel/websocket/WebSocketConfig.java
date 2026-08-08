package com.r.pixel.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final PixelWebSocketHandler pixelWebSocketHandler;

	public WebSocketConfig(PixelWebSocketHandler pixelWebSocketHandler) {
		this.pixelWebSocketHandler = pixelWebSocketHandler;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(pixelWebSocketHandler, "/ws/canvas").setAllowedOriginPatterns("*");
	}
}
