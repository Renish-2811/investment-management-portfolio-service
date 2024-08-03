package com.investment_management.portfolio_service.scheduling;

import com.investment_management.portfolio_service.config.websocket.PriceSocketClient;
import com.investment_management.portfolio_service.entity.Holdings;
import com.investment_management.portfolio_service.exception.ResourceNotFoundException;
import com.investment_management.portfolio_service.repository.HoldingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//This to run every 5 seconds and update Unrealized PL every time
@Component
public class PriceScheduler {
    @Autowired
    private HoldingRepo holdingRepo;

    @Autowired
    private PriceSocketClient webSocketClient;

    @Autowired
    private StandardWebSocketClient standardWebSocketClient;


    @Scheduled(fixedRate = 1000000000)
    public void reportCurrentTime() {
        List<Holdings> holdingsList = holdingRepo.findAll();
        if(!holdingsList.isEmpty()){
            List<String> codes = holdingsList.stream().map(Holdings::getStockCode).toList();
            webSocketClient = new PriceSocketClient(codes,SecurityContextHolder.getContext(),this);
            try {
                webSocketClient.connect(standardWebSocketClient, "ws://localhost:6789");
            }
            catch (Exception e){
                throw new ResourceNotFoundException(e.getMessage());
            }
        }
    }
    //function for updating the prices
    public void update(Map<String,Double> prices){
        List<Holdings> holdingsList = holdingRepo.findAll();
        for (Holdings holdings:holdingsList){
            Double price = prices.get(holdings.getStockCode());
            holdings.setUnrealizedPL(holdings.getQuantity().multiply(BigDecimal.valueOf(price).subtract(holdings.getRate())));
            holdings.setCurrentMarketValue(holdings.getQuantity().multiply(BigDecimal.valueOf(price)));
            holdingRepo.save(holdings);
        }
    }

}
