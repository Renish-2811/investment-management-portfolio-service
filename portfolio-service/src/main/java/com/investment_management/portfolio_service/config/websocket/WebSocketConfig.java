package com.investment_management.portfolio_service.config.websocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.*;

@Configuration
public class WebSocketConfig{

    @Bean
    public HashMap hashMap(){
        return new HashMap();
    }

    @Bean
    public StandardWebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketHandler myWebSocketHandler() {
        return new MyWebSocketClient();
    }

    @Bean
    public MyWebSocketClient myWebSocketClient(){
        return new MyWebSocketClient();
    }

    @Bean
    public WebSocketSession webSocketSession(){
        return new WebSocketSession() {
            @Override
            public String getId() {
                return "";
            }

            @Override
            public URI getUri() {
                return null;
            }

            @Override
            public HttpHeaders getHandshakeHeaders() {
                return null;
            }

            @Override
            public Map<String, Object> getAttributes() {
                return Map.of();
            }

            @Override
            public Principal getPrincipal() {
                return null;
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                return null;
            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public String getAcceptedProtocol() {
                return "";
            }

            @Override
            public void setTextMessageSizeLimit(int messageSizeLimit) {

            }

            @Override
            public int getTextMessageSizeLimit() {
                return 0;
            }

            @Override
            public void setBinaryMessageSizeLimit(int messageSizeLimit) {

            }

            @Override
            public int getBinaryMessageSizeLimit() {
                return 0;
            }

            @Override
            public List<WebSocketExtension> getExtensions() {
                return List.of();
            }

            @Override
            public void sendMessage(WebSocketMessage<?> message) throws IOException {

            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() throws IOException {

            }

            @Override
            public void close(CloseStatus status) throws IOException {

            }
        };
    }
    @Bean
    public PriceSocketClient priceSocketClient(){
        return new PriceSocketClient();
    }

    @Bean
    public  Map<String, AbstractMessageListenerContainer> containerMap(){
        return new Map<String, AbstractMessageListenerContainer>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public AbstractMessageListenerContainer get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public AbstractMessageListenerContainer put(String key, AbstractMessageListenerContainer value) {
                return null;
            }

            @Override
            public AbstractMessageListenerContainer remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ? extends AbstractMessageListenerContainer> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<String> keySet() {
                return Set.of();
            }

            @NotNull
            @Override
            public Collection<AbstractMessageListenerContainer> values() {
                return List.of();
            }

            @NotNull
            @Override
            public Set<Entry<String, AbstractMessageListenerContainer>> entrySet() {
                return Set.of();
            }
        };
    }
}
