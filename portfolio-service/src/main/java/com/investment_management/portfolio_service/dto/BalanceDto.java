package com.investment_management.portfolio_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {


    private String type;


    private Long amount;


    private String addType;

    public BalanceDto(String type, Long amount) {
        this.type = type;
        this.amount = amount;
    }
}
