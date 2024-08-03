package com.investment_management.portfolio_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "holdings")
public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String userId;

    @Column
    private String type;

    @Column(unique = true)
    private String stockCode;

    @Column
    private String stockName;

    @Column
    private BigDecimal quantity;

    @Column(scale = 2)
    private BigDecimal rate;

    @Column(scale = 2)
    private BigDecimal unrealizedPL;

    @Column(scale = 2)
    private BigDecimal realizedPL;

    @Column(scale = 2)
    private BigDecimal currentMarketValue;

    @Column(scale = 2)
    private BigDecimal purchasedMarketValue;

    @ManyToOne
    @JoinColumn(name="portfolioId", nullable=false)
    private Portfolio portfolio;

    public Holdings(String type, String stockCode, String stockName, BigDecimal quantity, BigDecimal rate) {
        this.type = type;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.rate = rate;
    }


}
