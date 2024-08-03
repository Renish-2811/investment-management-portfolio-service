package com.investment_management.portfolio_service.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Data
@Table(name = "portfolio")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "userId", unique = true)
    private String userId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "portfolio")
    private List<Holdings> holdingsList;

    @OneToMany(mappedBy = "portfolio")
    private List<Transaction> transactionsList;

    public Portfolio(String name) {
        this.name = name;
    }
}
