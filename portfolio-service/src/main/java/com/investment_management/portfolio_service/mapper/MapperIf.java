package com.investment_management.portfolio_service.mapper;

import com.investment_management.portfolio_service.dto.HoldingDto;
import com.investment_management.portfolio_service.dto.PortfolioDto;
import com.investment_management.portfolio_service.dto.TransactionDto;
import com.investment_management.portfolio_service.entity.Holdings;
import com.investment_management.portfolio_service.entity.Portfolio;
import com.investment_management.portfolio_service.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperIf {
    PortfolioDto PortfolioToPortFolioDto(Portfolio portfolio);

    Portfolio PortfolioDtoToPortFolio(PortfolioDto portfolioDto);

    Holdings HoldingDtoToHolding(HoldingDto holdingDto);

    HoldingDto HoldingToHoldingDto(Holdings holding);

    TransactionDto TransactionToTransactionDto(Transaction transaction);

    Transaction TransactionDtoToTransaction(TransactionDto transactionDto);
}
