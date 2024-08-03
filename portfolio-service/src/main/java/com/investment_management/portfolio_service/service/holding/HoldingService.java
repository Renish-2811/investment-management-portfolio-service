package com.investment_management.portfolio_service.service.holding;

import com.investment_management.portfolio_service.dto.HoldingDto;

import java.util.List;

public interface HoldingService {

    HoldingDto addHolding(HoldingDto holdingDto, String userId);

    HoldingDto deductHoldings(HoldingDto holdingDto,String userId);

    List<HoldingDto> getHolding(String portfolioId);

    List<HoldingDto> getHoldingByType(String type, String userId);

}
