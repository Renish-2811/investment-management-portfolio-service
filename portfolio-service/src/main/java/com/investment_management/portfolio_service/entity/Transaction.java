package com.investment_management.portfolio_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transaction")
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String userId;

    @Column
    private String type;

    @Column
    private String stockName;

    @Column
    private String stockCode;

    @Column
    private BigDecimal quantity;

    @Column(scale = 2)
    private BigDecimal rate;

    @Column
    private String status;

    @Column
    private LocalDateTime date;

    @Column
    private String direction;

    @ManyToOne
    @JoinColumn(name = "portfolioId", nullable = false)
    private Portfolio portfolio;

    public Transaction(String type, String stockName, String stockCode, BigDecimal quantity, BigDecimal rate, String direction) {
        this.type = type;
        this.stockName = stockName;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.rate = rate;
        this.direction = direction;
    }
}
