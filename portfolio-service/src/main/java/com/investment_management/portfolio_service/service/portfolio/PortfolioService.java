package com.investment_management.portfolio_service.service.portfolio;

import com.investment_management.portfolio_service.dto.PortfolioDto;

public interface PortfolioService {

    PortfolioDto addPortfolio(PortfolioDto portfolioDto,String userId);

    PortfolioDto getPortfolio(String userId);

    PortfolioDto updatePortfolio(PortfolioDto portfolioDto,String userId);
}
