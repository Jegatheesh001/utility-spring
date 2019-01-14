package com.myweb.utility.socket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.myweb.utility.socket.handler.MessageHandler;
import com.myweb.utility.socket.handler.NameHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new NameHandler(), "/socket/name").setAllowedOrigins("*");
		registry.addHandler(new MessageHandler(), "/socket/msg").setAllowedOrigins("*");
	}
}
