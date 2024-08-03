package com.investment_management.portfolio_service.config.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment_management.portfolio_service.dto.MessageDto;
import com.investment_management.portfolio_service.scheduling.PriceScheduler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Data
public class PriceSocketClient extends TextWebSocketHandler {
    private WebSocketSession session;
    private List<String> stockCodes;
    private SecurityContext securityContext;
    private PriceScheduler priceScheduler;

    @Autowired
    public PriceSocketClient(List<String> stockCodes, SecurityContext securityContext, PriceScheduler priceScheduler) {
        this.stockCodes=stockCodes;
        this.securityContext = securityContext;
        this.priceScheduler=priceScheduler;
    }

    public PriceSocketClient() {
    }


    public void connect(StandardWebSocketClient client, String uri) throws ExecutionException, InterruptedException {
        client.execute(this, uri).get();
        SecurityContextHolder.setContext(securityContext);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        try {
            String stockCodesJson = new ObjectMapper().writeValueAsString(stockCodes);
            session.sendMessage(new TextMessage("{\"subscribe\": " + stockCodesJson + "}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            priceScheduler.update(parsePriceFromMessage(message.getPayload()));
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }

    private Map<String,Double> parsePriceFromMessage(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);
        return messageDto.getPrices();
    }
}
