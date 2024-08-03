package com.investment_management.portfolio_service.dto;

import lombok.Data;

@Data
public class BankDto {

    String accountCode;

    String name;

    String ifsc;

    @Override
    public String toString() {
        return "BankDto{" +
                "accountCode='" + accountCode + '\'' +
                ", name='" + name + '\'' +
                ", ifsc='" + ifsc + '\'' +
                '}';
    }
}
