package com.investment_management.portfolio_service.config.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment_management.portfolio_service.dto.MessageDto;
import com.investment_management.portfolio_service.dto.TransactionDto;
import com.investment_management.portfolio_service.service.transaction.TransactionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Data
public class MyWebSocketClient extends TextWebSocketHandler {
    private WebSocketSession session;
    private TransactionDto transactionDto;
    private TransactionService transactionService;
    private String userId;
    private UUID transactionId;
    private SecurityContext securityContext;

    @Autowired
    public MyWebSocketClient(TransactionDto transactionDto,String userId,UUID transactionId, SecurityContext securityContext, TransactionService transactionService) {
        this.transactionDto = transactionDto;
        this.userId = userId;
        this.transactionService = transactionService;
        this.transactionId=transactionId;
        this.securityContext = securityContext;
    }

    public MyWebSocketClient() {
    }


    public void connect(StandardWebSocketClient client, String uri) throws ExecutionException, InterruptedException {
        client.execute(this, uri).get();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        try {
            session.sendMessage(new TextMessage("{\"subscribe\": [\"" + transactionDto.getStockCode() + "\"]}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
            try {
                double currentPrice = parsePriceFromMessage(message.getPayload(), transactionDto.getStockCode());
                if (currentPrice == transactionDto.getRate().doubleValue()) {
                    transactionService.executeTransaction(transactionDto,transactionId,userId,securityContext);
                    session.sendMessage(new TextMessage("{\"unsubscribe\": [\"" + transactionDto.getStockCode() + "\"]}"));
                    session.close();
                }
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

    private double parsePriceFromMessage(String message, String stockCode) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);
        return  messageDto.getPrices().get(stockCode);
    }
}
