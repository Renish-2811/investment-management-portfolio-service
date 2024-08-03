package com.investment_management.portfolio_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {


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

    private UUID id;

    private String status;

    @NotNull(message = "Direction cant be null")
    @Pattern(regexp = "^(BUY|SELL)$", message = "Direction can either be BUY or SELL")
    private String direction;

    private LocalDateTime localDateTime;

}
