package com.investment_management.portfolio_service.controller;

import com.investment_management.portfolio_service.dto.PortfolioDto;
import com.investment_management.portfolio_service.service.portfolio.DefaultPortfolioService;
import com.investment_management.portfolio_service.service.user.DefaultUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class PortfolioController {
    @Autowired
    private DefaultPortfolioService defaultPortfolioService;

    @Autowired
    private DefaultUserService defaultUserService;

    @PostMapping(path = "/portfolio")
    public ResponseEntity<PortfolioDto> addPortfolio(@Valid @RequestBody PortfolioDto portfolioDto){
        PortfolioDto response = defaultPortfolioService.addPortfolio(portfolioDto,defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response,HttpStatus.OK);
    }

    @PutMapping(path = "/portfolio")
    public ResponseEntity<PortfolioDto> updatePortfolio(@Valid @RequestBody PortfolioDto portfolioDto){
        PortfolioDto response = defaultPortfolioService.updatePortfolio(portfolioDto,defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioDto> getPortfolio(){
        PortfolioDto response = defaultPortfolioService.getPortfolio(defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response,HttpStatus.OK);
    }


}
