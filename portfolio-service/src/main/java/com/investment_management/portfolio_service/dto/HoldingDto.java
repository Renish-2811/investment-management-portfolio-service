package com.investment_management.portfolio_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldingDto {

    @NotNull(message = "Type can not be blank")
    @Pattern(regexp = "^(STOCKS|F&O|COMMODITY|CURRENCY|MF)$", message = "Type must be STOCKS or F&O or COMMODITY or CURRENCY or MF ")
    private String type;

    @NotNull(message = "Stock Code cant be null")
    @Pattern(regexp = "^[A-Z]+$", message = "Invalid Stock Code format")
    private String stockCode;

    @NotNull(message = "Stock name cant be null")
    private String stockName;

    @NotNull(message = "Quantity can't be null")
    private BigDecimal quantity;

    @NotNull(message = "Rate can't be null")
    private BigDecimal rate;


    private BigDecimal unrealizedPL;


    private BigDecimal realizedPL;


    private BigDecimal currentMarketValue;


    private BigDecimal purchasedMarketValue;

    public HoldingDto(String type, String stockCode, String stockName, BigDecimal quantity, BigDecimal rate) {
        this.type = type;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.rate = rate;
    }
}
