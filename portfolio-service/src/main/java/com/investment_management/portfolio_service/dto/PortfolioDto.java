package com.investment_management.portfolio_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PortfolioDto {

    @Valid
    @NotBlank(message = "Portfolio Name can't be blank")
    private String name;

    public PortfolioDto(String name) {
        this.name = name;
    }
}
